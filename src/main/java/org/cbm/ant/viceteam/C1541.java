package org.cbm.ant.viceteam;

import java.util.HashMap;
import java.util.Map;

import org.cbm.ant.disk.AbstractDiskTask;
import org.cbm.ant.util.OS;

public class C1541 extends AbstractDiskTask<C1541>
{
    static final Map<OS, String> EXECUTABLES = new HashMap<>();

    static
    {
        EXECUTABLES.put(OS.LINUX, "c1541");
        EXECUTABLES.put(OS.WINDOWS, "c1541.exe");
    }

    public C1541()
    {
        super();
    }

    @Override
    protected String getHomePropertyKeyPrefix()
    {
        return "vice";
    }

    @Override
    protected String getExecutablePropertyKey()
    {
        return "c1541";
    }

    @Override
    protected Map<OS, String> getExecutables()
    {
        return EXECUTABLES;
    }

    public void addFormat(C1541Format format)
    {
        super.addFormat(format);
    }

    public C1541 format(C1541Format format)
    {
        addFormat(format);

        return this;
    }

    public void addRead(C1541Read read)
    {
        super.addRead(read);
    }

    public C1541 read(C1541Read read)
    {
        addRead(read);

        return this;
    }

    public void addDelete(C1541Delete delete)
    {
        super.addDelete(delete);
    }

    public C1541 delete(C1541Delete delete)
    {
        addDelete(delete);

        return this;
    }

    public void addWrite(C1541Write write)
    {
        super.addWrite(write);
    }

    public C1541 write(C1541Write write)
    {
        addWrite(write);

        return this;
    }

    public void addList(C1541List list)
    {
        super.addList(list);
    }

    public C1541 list(C1541List list)
    {
        addList(list);

        return this;
    }
}
