package org.cbm.ant.data;

import java.io.IOException;
import java.io.OutputStream;

import org.apache.tools.ant.BuildException;

public class DataText implements DataCommand
{

	private final StringBuilder builder = new StringBuilder();

	private int length = -1;
	private boolean convert = true;

	public DataText()
	{
		super();
	}

	public int getLength()
	{
		return length;
	}

	public void setLength(int length)
	{
		this.length = length;
	}

	public boolean isConvert()
	{
		return convert;
	}

	public void setConvert(boolean convert)
	{
		this.convert = convert;
	}

	public void addText(String text)
	{
		builder.append(text.trim()).append(" ");
	}

	/**
	 * @see org.cbm.ant.data.DataCommand#isExecutionNecessary(long, boolean)
	 */
	@Override
	public boolean isExecutionNecessary(long lastModified, boolean exists)
	{
		return !exists;
	}

	/**
	 * @see org.cbm.ant.data.DataCommand#execute(Data, java.io.OutputStream)
	 */
	@Override
	public void execute(Data task, OutputStream out) throws BuildException, IOException
	{
		task.log("Adding text data");

		char[] data = builder.toString().trim().toCharArray();
		int index = 0;
		int count = 0;

		while (index < data.length)
		{
			if (count >= length)
			{
				throw new BuildException("Text exceeds length");
			}

			char ch = data[index++];

			if (ch == '\\')
			{
				if (index >= data.length)
				{
					throw new BuildException("Invalid escape sequence at position " + index);
				}

				ch = data[index++];

				switch (ch)
				{
					case '0':
						out.write(0);
						count += 1;
						continue;

					case 'n':
						out.write('\n');
						count += 1;
						continue;

					case 'r':
						out.write('\r');
						count += 1;
						continue;

					case 't':
						out.write('\t');
						count += 1;
						continue;

					case 'u':
						if (index >= data.length)
						{
							throw new BuildException("Invalid escape sequence at position " + index);
						}

						ch = Character.toLowerCase(data[index++]);

						if ((!((ch >= '0') && (ch <= '9'))) && (!((ch >= 'a') && (ch <= 'f'))))
						{
							throw new BuildException("Invalid escape sequence at position " + index);
						}

						int value = ((ch >= '0') && (ch <= '9')) ? (ch - '0') : ((ch - 'a') + 10);

						if (index >= data.length)
						{
							throw new BuildException("Invalid escape sequence at position " + index);
						}

						ch = data[index++];

						if (!Character.isDigit(ch))
						{
							throw new BuildException("Invalid escape sequence at position " + index);
						}

						value <<= 4;
						value += ((ch >= '0') && (ch <= '9')) ? (ch - '0') : ((ch - 'a') + 10);
						out.write(value);
						count += 1;
						continue;
				}

				throw new BuildException("Invalid escape sequence at position " + index);
			}

			switch (ch) {
				case 'Ä':
					ch = 0x5b;
					break;
					
				case 'Ö':
					ch = 0x5c;
					break;

				case 'Ü':
					ch = 0x5d;
					break;
					
				case 'ä':
					ch = 0x7b;
					break;
					
				case 'ö':
					ch = 0x7c;
					break;

				case 'ü':
					ch = 0x7d;
					break;
					
				case 'ß':
					ch = 0x7e;
					break;
			}
			
			if (convert)
			{
				if ((ch >= 0x40) && (ch < 0x60))
				{
					ch += 0x80;
				}
				else if ((ch >= 0x60) && (ch < 0x80))
				{
					ch -= 0x20;
				}
			}

			out.write(ch);
			count += 1;
		}

		while (count < length)
		{
			out.write(0);
			count += 1;
		}
	}
}
