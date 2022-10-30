package org.cbm.ant;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Random;
import java.util.UUID;
import java.util.zip.CRC32;

import org.apache.tools.ant.BuildEvent;
import org.apache.tools.ant.BuildListener;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Task;

public class AntTestUtils
{
    public static File getTmpDir()
    {
        return new File(System.getProperty("java.io.tmpdir"));
    }

    public static File randomTmpFile()
    {
        File file = new File(getTmpDir(), UUID.randomUUID().toString());

        file.deleteOnExit();

        return file;
    }

    public static byte[] read(File file) throws IOException
    {
        try (InputStream in = new FileInputStream(file))
        {
            return in.readAllBytes();
        }
    }

    public static void write(File file, byte[] bytes) throws IOException
    {
        try (OutputStream out = new FileOutputStream(file))
        {
            out.write(bytes);
        }
    }

    public static String crc32(byte[] bytes)
    {
        CRC32 crc = new CRC32();

        crc.update(bytes);

        return Long.toHexString(crc.getValue());
    }

    public static Project createProject()
    {
        Project project = new Project();

        project.setBaseDir(getTmpDir());
        project.addBuildListener(new BuildListener()
        {
            @Override
            public void taskStarted(BuildEvent event)
            {
                System.out.println("Started task " + event.getTask().getTaskName());
            }

            @Override
            public void taskFinished(BuildEvent event)
            {
                System.out.println("Finished task " + event.getTask().getTaskName());
            }

            @Override
            public void targetStarted(BuildEvent event)
            {
                // TODO Auto-generated method stub

            }

            @Override
            public void targetFinished(BuildEvent event)
            {
                // TODO Auto-generated method stub

            }

            @Override
            public void messageLogged(BuildEvent event)
            {
                System.out.println("[" + event.getTask().getTaskName() + "] " + event.getMessage());

            }

            @Override
            public void buildStarted(BuildEvent event)
            {
                // TODO Auto-generated method stub

            }

            @Override
            public void buildFinished(BuildEvent event)
            {
                // TODO Auto-generated method stub

            }
        });

        return project;
    }

    public static <T extends Task> T prepare(T task)
    {
        task.setProject(createProject());

        return task;
    }

    private AntTestUtils()
    {
        super();
    }

    public static byte[] createSample(int size)
    {
        Random random = new Random(size);
        byte[] result = new byte[size];

        random.nextBytes(result);

        return result;
    }

    public static File createSampleFile(int size) throws IOException
    {
        File file = new File(getTmpDir(), UUID.randomUUID().toString());

        try (OutputStream out = new FileOutputStream(file))
        {
            out.write(createSample(size));
        }

        file.deleteOnExit();

        return file;
    }

}
