package org.cbm.ant.cc1541;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.apache.tools.ant.BuildException;
import org.cbm.ant.disk.AbstractDiskTask;
import org.cbm.ant.util.OS;

public class CC1541 extends AbstractDiskTask<CC1541>
{
    static final Map<OS, String> EXECUTABLES = new HashMap<>();

    static
    {
        EXECUTABLES.put(OS.LINUX, "cc1541");
        EXECUTABLES.put(OS.WINDOWS, "cc1541.exe");
    }

    public CC1541()
    {
        super();
    }

    @Override
    protected String getHomePropertyKeyPrefix()
    {
        return "cc1541";
    }

    @Override
    protected String getExecutablePropertyKey()
    {
        return "cc1541";
    }

    @Override
    protected Map<OS, String> getExecutables()
    {
        return EXECUTABLES;
    }
    
    public void addFormat(CC1541Format format)
    {
        super.addFormat(format);
    }

    public CC1541 format(CC1541Format format)
    {
        addFormat(format);

        return this;
    }

    public void addDelete(CC1541Delete delete)
    {
        super.addDelete(delete);
    }

    public CC1541 delete(CC1541Delete delete)
    {
        addDelete(delete);

        return this;
    }

    public void addWrite(CC1541Write write)
    {
        super.addWrite(write);
    }

    public CC1541 write(CC1541Write write)
    {
        addWrite(write);

        return this;
    }

    @Override
    public File getImage()
    {
        File image = super.getImage();
        String name = image.getName();

        if (!name.endsWith(".d64") && !name.endsWith(".d71") && !name.endsWith(".d81"))
        {
            throw new BuildException("Image name must end with (.d64|.d71|.d81).");
        }

        return image;
    }
}
