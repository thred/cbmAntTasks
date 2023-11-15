package org.cbm.ant.data;

import java.io.IOException;

import org.apache.tools.ant.BuildException;

public interface DataCommand
{
    boolean isExecutionNecessary(long lastModified, boolean exists);

    void execute(DataWriter writer) throws BuildException, IOException;
}
