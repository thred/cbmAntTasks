package org.cbm.ant.util;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

public class ProcessInputHandler implements Runnable
{

	private final ProcessConsumer consumer;
	private final boolean isError;
	private final InputStream input;
	private final Charset charset;
	private final byte[] buffer;
	private final Object semaphore = new Object();

	private StringBuilder builder;
	private boolean skipLF = false;
	private boolean running = true;

	public ProcessInputHandler(ProcessConsumer consumer, boolean isError, InputStream input)
	{
		this(consumer, isError, input, Charset.defaultCharset());
	}

	public ProcessInputHandler(ProcessConsumer consumer, boolean isError, InputStream input, Charset charset)
	{
		super();

		this.consumer = consumer;
		this.isError = isError;
		this.input = input;
		this.charset = charset;

		buffer = new byte[4096];
	}

	public boolean isRunning()
	{
		return running;
	}

	public void start()
	{
		Thread thread = new Thread(this, getClass().getSimpleName());

		thread.setDaemon(false);
		thread.start();
	}

	/**
	 * @see java.lang.Runnable#run()
	 */
	public void run()
	{
		int length;

		try
		{
			while ((length = input.read(buffer)) >= 0)
			{
				process(buffer, length);
			}
		}
		catch (IOException e)
		{
			e.printStackTrace(System.err);
		}
		finally
		{
			synchronized (semaphore)
			{
				running = false;

				semaphore.notifyAll();
			}
		}
	}

	public void waitFor() throws InterruptedException
	{
		synchronized (semaphore)
		{
			if (running)
			{
				semaphore.wait();
			}
		}
	}

	private void process(byte[] buffer, int length)
	{
		int start = 0;
		int i = 0;

		while (i < length)
		{
			if (buffer[i] == '\n')
			{
				if (i - start > 0)
				{
					process(new String(buffer, start, i - start, charset));
				}
				processLine();
				skipLF = true;
				start = i + 1;
			}
			else if (buffer[i] == '\r')
			{
				if (!skipLF)
				{
					if (i - start > 0)
					{
						process(new String(buffer, start, i - start, charset));
					}
					processLine();
				}
				start = i + 1;
				skipLF = false;
			}
			else
			{
				skipLF = false;
			}

			i += 1;
		}

		if (start < length)
		{
			process(new String(buffer, start, length - start, charset));
		}
	}

	private void process(String s)
	{
		if (builder == null)
		{
			builder = new StringBuilder(s);
		}
		else
		{
			builder.append(s);
		}
	}

	private void processLine()
	{
		if (builder != null)
		{
			consumer.processOutput(builder.toString(), isError);
			builder = null;
		}
	}

}
