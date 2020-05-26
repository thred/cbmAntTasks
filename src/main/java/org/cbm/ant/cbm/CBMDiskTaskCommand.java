package org.cbm.ant.cbm;

import org.apache.tools.ant.BuildException;
import org.cbm.ant.cbm.disk.CBMDiskOperator;

public interface CBMDiskTaskCommand
{

    public boolean isFailOnError();

    public boolean isExecutionNecessary(long lastModified, boolean exists);

    public Long execute(CBMDiskTask task, CBMDiskOperator operator) throws BuildException;

}
