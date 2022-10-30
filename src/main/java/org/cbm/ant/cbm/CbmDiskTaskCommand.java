package org.cbm.ant.cbm;

import org.apache.tools.ant.BuildException;
import org.cbm.ant.cbm.disk.CbmDisk;

public interface CbmDiskTaskCommand
{
    boolean isFailOnError();

    boolean isExecutionNecessary(long lastModified, boolean exists);

    Long execute(CbmDiskTask task, CbmDisk disk) throws BuildException;
}
