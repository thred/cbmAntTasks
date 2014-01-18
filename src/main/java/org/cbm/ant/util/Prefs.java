package org.cbm.ant.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.prefs.Preferences;

public class Prefs
{

    private static Prefs defaultPrefs = null;

    public static Prefs getDefault()
    {
        return defaultPrefs;
    }

    public static void setDefault(Class<?> type)
    {
        defaultPrefs = new Prefs(Preferences.userNodeForPackage(type));
    }

    public static Prefs get(Class<?> type)
    {
        return new Prefs(Preferences.userNodeForPackage(type));
    }

    private final Preferences prefs;

    private Prefs(Preferences prefs)
    {
        super();

        this.prefs = prefs;
    }

    public boolean exists(String key)
    {
        return prefs.get(key, null) != null;
    }

    public int get(String key, int defaultValue)
    {
        return prefs.getInt(key, defaultValue);
    }

    public boolean set(String key, int value)
    {
        if ((exists(key)) && (Util.equals(prefs.getInt(key, 0), value)))
        {
            return false;
        }

        prefs.putInt(key, value);

        return true;
    }

    public long get(String key, long defaultValue)
    {
        return prefs.getLong(key, defaultValue);
    }

    public boolean set(String key, long value)
    {
        if ((exists(key)) && (Util.equals(prefs.getLong(key, 0), value)))
        {
            return false;
        }

        prefs.putLong(key, value);

        return true;
    }

    public float get(String key, float defaultValue)
    {
        return prefs.getFloat(key, defaultValue);
    }

    public boolean set(String key, float value)
    {
        if ((exists(key)) && (Util.equals(prefs.getFloat(key, 0), value)))
        {
            return false;
        }

        prefs.putFloat(key, value);

        return true;
    }

    public double get(String key, double defaultValue)
    {
        return prefs.getDouble(key, defaultValue);
    }

    public boolean set(String key, double value)
    {
        if ((exists(key)) && (Util.equals(prefs.getDouble(key, 0), value)))
        {
            return false;
        }

        prefs.putDouble(key, value);

        return true;
    }

    public String get(String key, String defaultValue)
    {
        return prefs.get(key, defaultValue);
    }

    public boolean set(String key, String value)
    {
        if ((exists(key)) && (Util.equals(prefs.get(key, null), value)))
        {
            return false;
        }

        prefs.put(key, value);

        return true;
    }

    public boolean get(String key, boolean defaultValue)
    {
        return prefs.getBoolean(key, defaultValue);
    }

    public boolean set(String key, boolean value)
    {
        if ((exists(key)) && (Util.equals(prefs.getBoolean(key, !value), value)))
        {
            return false;
        }

        prefs.putBoolean(key, value);

        return true;
    }

    public byte[] get(String key, byte[] defaultValue)
    {
        return prefs.getByteArray(key, defaultValue);
    }

    public boolean set(String key, byte[] value)
    {
        if ((exists(key)) && (Util.equals(prefs.get(key, null), value)))
        {
            return false;
        }

        prefs.putByteArray(key, value);

        return true;
    }

    @SuppressWarnings("unchecked")
    public <TYPE extends Enum<?>> TYPE get(String key, TYPE defaultValue)
    {
        String value = prefs.get(key, null);

        if (value == null)
        {
            return defaultValue;
        }

        try
        {
            return (TYPE) Enum.valueOf(defaultValue.getClass(), value);
        }
        catch (IllegalArgumentException e)
        {
            return defaultValue;
        }
    }

    public <TYPE extends Enum<?>> boolean set(String key, TYPE value)
    {
        if ((exists(key)) && (Util.equals(prefs.get(key, null), value.name())))
        {
            return false;
        }

        prefs.put(key, value.name());

        return true;
    }

    @SuppressWarnings("unchecked")
    public <TYPE extends Serializable> TYPE get(Class<TYPE> type, String key, TYPE defaultValue)
    {
        byte[] bytes = prefs.getByteArray(key, null);

        if (bytes == null)
        {
            return defaultValue;
        }

        try
        {
            ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(bytes));

            try
            {
                return (TYPE) in.readObject();
            }
            catch (ClassNotFoundException e)
            {
                return defaultValue;
            }
            finally
            {
                in.close();
            }
        }
        catch (IOException e)
        {
            return defaultValue;
        }
    }

    public <TYPE extends Serializable> boolean set(Class<TYPE> type, String key, TYPE value)
    {
        if (value == null)
        {
            if (exists(key))
            {
                prefs.remove(key);

                return true;
            }

            return false;
        }

        try
        {
            ByteArrayOutputStream byteStream = new ByteArrayOutputStream();

            try
            {
                ObjectOutputStream out = new ObjectOutputStream(byteStream);

                out.writeObject(value);
            }
            finally
            {
                byteStream.close();
            }

            prefs.putByteArray(key, byteStream.toByteArray());

            return true;
        }
        catch (IOException e)
        {
            throw new IllegalArgumentException("Failed to store value of type " + type + " with key " + key);
        }
    }

}
