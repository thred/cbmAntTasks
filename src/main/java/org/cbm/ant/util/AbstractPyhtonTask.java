package org.cbm.ant.util;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;

public abstract class AbstractPyhtonTask extends Task implements ProcessConsumer
{

    private static final Map<String, String> EXECUTABLES = new HashMap<>();

    static
    {
        EXECUTABLES.put("Linux.*", "python");
        EXECUTABLES.put("Windows.*", "python.exe");
    }

    private File pythonHome;
    private String executable;

    public File getPythonHome()
    {
        if (pythonHome != null)
        {
            return pythonHome;
        }

        String projectProperty = getProject().getProperty("pythonHome");

        if (projectProperty != null)
        {
            return new File(projectProperty);
        }

        String systemProperty = System.getProperty("pythonHome");

        if (systemProperty != null)
        {
            return new File(systemProperty);
        }

        String environmentSetting = System.getenv("PYTHON_HOME");

        if (environmentSetting != null)
        {
            return new File(environmentSetting);
        }

        return getProject().getBaseDir();
    }

    public void setPythonHome(File pythonHome)
    {
        this.pythonHome = pythonHome;
    }

    public File getExecutable()
    {
        if (executable != null)
        {
            File result = new File(executable);

            if (!result.isAbsolute())
            {
                result = new File(getPythonHome(), executable);
            }

            if (!result.exists())
            {
                throw new BuildException("Executable invalid: " + result.getAbsolutePath());
            }

            return result;
        }

        String os = System.getProperty("os.name");

        for (Map.Entry<String, String> entry : EXECUTABLES.entrySet())
        {
            if (os.matches(entry.getKey()))
            {
                File result = new File(getPythonHome(), entry.getValue());

                if (!result.exists())
                {
                    throw new BuildException("Executable invalid: " + result.getAbsolutePath());
                }

                return result;
            }
        }

        throw new BuildException("No executable defined for " + os);
    }

    public void setExecutable(String executable)
    {
        this.executable = executable;
    }
}
