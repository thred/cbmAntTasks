package org.cbm.ant.util;

import java.util.regex.Pattern;

public enum OS
{
    LINUX("Linux.*"),

    WINDOWS("Windows.*");

    private Pattern pattern;

    OS(String regex)
    {
        pattern = Pattern.compile(regex);
    }

    public Pattern getPattern()
    {
        return pattern;
    }

    public static OS current()
    {
        String osName = System.getProperty("os.name");

        for (OS os : OS.values())
        {
            if (os.pattern.matcher(osName).matches())
            {
                return os;
            }
        }

        throw new UnsupportedOperationException("The OS \"" + osName + "\" is not supported");
    }
}
