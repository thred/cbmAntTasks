package org.cbm.ant.verify;

import java.io.File;

import org.apache.tools.ant.BuildException;
import org.cbm.ant.util.PersistentProperties;

public class VerifySizeTask implements VerifyTask
{

    private File file;
    private String size;

    public VerifySizeTask()
    {
        super();
    }

    public File getFile()
    {
        return file;
    }

    public void setFile(File file)
    {
        this.file = file;
    }

    public String getSize()
    {
        return size;
    }

    public void setSize(String size)
    {
        this.size = size;
    }

    @Override
    public void execute(Verify task) throws BuildException
    {
        if (!file.exists())
        {
            throw new BuildException(String.format("\"%s\" is missing", file));
        }

        String lastLength = PersistentProperties.INSTANCE.get(file.getAbsolutePath() + "#length");

        long length = file.length();
        int size = Integer.decode(this.size);
        double percent = (double) length / size;
        String info = "";

        if (lastLength != null)
        {
            long diff = length - Long.parseLong(lastLength);

            if (diff != 0)
            {
                info = String.format(" [%+d bytes]", diff);
            }
        }

        task.log(String.format("\t%-20s %8d bytes (%4.1f %%, %8d bytes free) %s", file.getName(), length, percent * 100,
            size - length, info));

        if (percent > 1)
        {
            throw new BuildException(String.format("\"%s\" exceeds specified size by %d bytes", file, length - size));
        }

        PersistentProperties.INSTANCE.set(file.getAbsolutePath() + "#length", String.valueOf(length));
        PersistentProperties.INSTANCE.save();
    }

}
