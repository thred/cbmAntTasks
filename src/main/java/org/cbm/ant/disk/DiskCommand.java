package org.cbm.ant.disk;

import java.io.File;

import org.apache.tools.ant.BuildException;
import org.cbm.ant.util.ProcessHandler;

public interface DiskCommand
{
    boolean isFailOnError();

    boolean isExecutionNecessary(long lastModified, boolean exists);

    int execute(AbstractDiskTask<?> task, ProcessHandler handler, File image) throws BuildException;
}
