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

        return null;
    }

    public void setPythonHome(File pythonHome)
    {
        this.pythonHome = pythonHome;
    }

    /**
     * Creates a process handler.
     *
     * @return the executable
     * @throws BuildException on occasion
     */
    public ProcessHandler createProcessHandler() throws BuildException
    {
        ProcessHandler handler = new ProcessHandler(this).directory(getProject().getBaseDir());

        if (executable != null)
        {
            return handler.executable(executable);
        }

        return handler.executable(getPythonHome(), EXECUTABLES);
    }

    public String getExecutable()
    {
        return executable;
    }

    public void setExecutable(String executable)
    {
        this.executable = executable;
    }

    /**
     * @see org.cbm.ant.util.ProcessConsumer#processOutput(java.lang.String, boolean)
     */
    @Override
    public void processOutput(String output, boolean isError)
    {
        log(output);
    }
}
