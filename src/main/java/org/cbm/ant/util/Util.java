package org.cbm.ant.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.types.FileSet;

public class Util
{

    public static boolean equals(final Object obj0, final Object obj1)
    {
        return obj0 == null && obj1 == null || obj0 != null && obj0.equals(obj1);
    }

    public static boolean isEmpty(String s)
    {
        return s == null || s.trim().length() <= 0;
    }

    public static int unsignedByteToInt(byte value)
    {
        return value & 0xff;
    }

    public static int parseHex(String hex) throws NumberFormatException
    {
        return ConstantStatement.evaluate(hex);
    }

    public static String toHex(byte value)
    {
        String result = Integer.toHexString(unsignedByteToInt(value));

        if (result.length() < 2)
        {
            result = "0" + result;
        }

        return "0x" + result;
    }

    public static String toHex(int value)
    {
        String result = Integer.toHexString(value);

        while (result.length() < 4)
        {
            result = "0" + result;
        }

        return "0x" + result;
    }

    public static byte[] read(File file) throws IOException
    {
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try
        {
            InputStream in = new FileInputStream(file);

            try
            {
                byte[] buffer = new byte[4096];
                int length;

                while ((length = in.read(buffer)) >= 0)
                {
                    out.write(buffer, 0, length);
                }
            }
            finally
            {
                in.close();
            }
        }
        finally
        {
            out.close();
        }

        return out.toByteArray();
    }

    public static Collection<AntFile> collect(Project project, Collection<FileSet> fileSets, File... additionalFiles)
    {
        Collection<AntFile> files = new ArrayList<>();

        if (additionalFiles != null)
        {
            for (File additionalFile : additionalFiles)
            {
                if (additionalFile != null)
                {
                    files.add(AntFile.create(project.getBaseDir(), additionalFile.getParentFile(), additionalFile));
                }
            }
        }

        for (FileSet fileSet : fileSets)
        {
            DirectoryScanner scanner = fileSet.getDirectoryScanner(project);

            for (String filename : scanner.getIncludedFiles())
            {
                files.add(AntFile.create(project.getBaseDir(), scanner.getBasedir(), filename));
            }
        }

        return files;
    }

    public static Iterator<AntFile> iterate(Project project, Collection<FileSet> fileSets, File... additionalFiles)
    {
        return collect(project, fileSets).iterator();
    }

    public static boolean anyExists(Project project, Collection<FileSet> fileSets, File... additionalFiles)
    {
        if (additionalFiles != null)
        {
            for (File file : additionalFiles)
            {
                if (file != null && file.exists())
                {
                    return true;
                }
            }
        }

        for (FileSet fileSet : fileSets)
        {
            DirectoryScanner scanner = fileSet.getDirectoryScanner(project);

            for (String filename : scanner.getIncludedFiles())
            {
                if (AntFile.create(project.getBaseDir(), scanner.getBasedir(), filename).exists())
                {
                    return true;
                }
            }
        }

        return false;
    }

    public static long lastModified(Project project, Collection<FileSet> fileSets, File... additionalFiles)
    {
        long lastModified = Long.MIN_VALUE;

        if (additionalFiles != null)
        {
            for (File file : additionalFiles)
            {
                if (file != null && file.exists())
                {
                    lastModified = Math.max(lastModified, file.lastModified());
                }
            }
        }

        for (FileSet fileSet : fileSets)
        {
            DirectoryScanner scanner = fileSet.getDirectoryScanner(project);

            for (String filename : scanner.getIncludedFiles())
            {
                File file = AntFile.create(project.getBaseDir(), scanner.getBasedir(), filename);

                if (file.exists())
                {
                    lastModified = Math.max(lastModified, file.lastModified());
                }
            }
        }

        return lastModified;
    }

    public static File withExtension(File file, String extension)
    {
        String name = file.getName();
        int index = name.lastIndexOf('.');

        if (index >= 0)
        {
            name = name.substring(0, index) + extension;
        }
        else
        {
            name = name + extension;
        }

        return new File(file.getParentFile(), name);
    }

    public static String getCanonicalPath(File file)
    {
        try
        {
            return file.getCanonicalPath().replace('\\', '/');
        }
        catch (IOException e)
        {
            return file.getAbsolutePath().replace('\\', '/');
        }
    }

    public static File toAbsoluteForm(File base, File file)
    {
        if (file.isAbsolute())
        {
            return file;
        }

        return new File(base, file.getPath());
    }

    public static File toCanonicalForm(File base, File file)
    {
        try
        {
            return toAbsoluteForm(base, file).getCanonicalFile();
        }
        catch (IOException e)
        {
            return toAbsoluteForm(base, file);
        }
    }

    public static File toRelativeForm(File base, File file)
    {
        if (base == null)
        {
            return file;
        }

        if (file.isAbsolute())
        {
            String canonicalBase = getCanonicalPath(base);
            String canonicalFile = getCanonicalPath(file);

            if (canonicalFile.contains(canonicalBase))
            {
                String result = canonicalFile.substring(canonicalBase.length());

                if (result.startsWith("/"))
                {
                    result = result.substring(1);
                }

                return new File(result);
            }

            return new File(canonicalFile);
        }

        return file;
    }

}
