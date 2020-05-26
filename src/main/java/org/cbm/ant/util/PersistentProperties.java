package org.cbm.ant.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

public class PersistentProperties
{

    public static final PersistentProperties INSTANCE = new PersistentProperties();

    private final Properties properties = new Properties();

    private PersistentProperties()
    {
        super();

        load();
    }

    public String get(String key)
    {
        return properties.getProperty(key);
    }

    public void set(String key, String value)
    {
        properties.setProperty(key, value);
    }

    protected void load()
    {
        File file = file();

        if (file.exists())
        {
            try
            {
                FileInputStream in = new FileInputStream(file);

                try
                {
                    properties.load(in);
                }
                finally
                {
                    in.close();
                }
            }
            catch (IOException e)
            {
                System.err.println("Failed to load properties. File may be damaged: " + file);
            }
        }
    }

    public void save()
    {
        File file = file();

        try
        {
            FileOutputStream out = new FileOutputStream(file);

            try
            {
                properties.store(out, "CBMAntTasks - Persistent Properties");
            }
            finally
            {
                out.close();
            }
        }
        catch (IOException e)
        {
            e.printStackTrace(System.err);
        }
    }

    private File file()
    {
        return new File(System.getProperty("java.io.tmpdir"), "CBMAntTasksPersistent.properties");
    }

}
