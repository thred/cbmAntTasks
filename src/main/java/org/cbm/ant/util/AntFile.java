package org.cbm.ant.util;

import java.io.File;

public class AntFile extends File
{

    private static final long serialVersionUID = 1L;

    public static AntFile create(File buildDir, File file)
    {
        File base = file.getParentFile();

        if (base == null)
        {
            file = Util.toRelativeForm(base, file);
        }

        return create(buildDir, base, file);
    }

    public static AntFile create(File buildDir, File base, File file)
    {
        return create(buildDir, base, Util.getCanonicalPath(file));
    }

    public static AntFile create(File buildDir, File base, String filename)
    {
        if (buildDir == null)
        {
            throw new IllegalArgumentException("BuildDir is null");
        }

        File file = null;

        if (base != null)
        {
            base = Util.toRelativeForm(buildDir, base);
            file = Util.toRelativeForm(Util.toAbsoluteForm(buildDir, base), new File(filename));
        }
        else
        {
            file = Util.toRelativeForm(buildDir, new File(filename));
        }

        return new AntFile(buildDir, base, file.getPath());
    }

    private final File buildDir;
    private final File base;
    private final String filename;

    private AntFile(File buildDir, File base, String filename)
    {
        super(base, filename);

        this.buildDir = buildDir;
        this.base = base;
        this.filename = filename;
    }

    public File getBuildDir()
    {
        return buildDir;
    }

    public File getBase()
    {
        return base;
    }

    public String getFilename()
    {
        return filename;
    }

    public AntFile withExtension(String extension)
    {
        String filename = getFilename();
        int index = filename.lastIndexOf('.');

        if (index >= 0)
        {
            filename = filename.substring(0, index) + "." + extension;
        }
        else
        {
            filename += "." + extension;
        }

        return create(buildDir, base, filename);
    }

    public AntFile ensureDirectory()
    {
        getParentFile().mkdirs();

        return this;
    }
}
