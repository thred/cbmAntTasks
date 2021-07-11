package org.cbm.ant.cbm;

import org.apache.tools.ant.BuildException;
import org.cbm.ant.cbm.disk.CBMDiskOperator;

public interface CBMDiskTaskCommand
{

    boolean isFailOnError();

    boolean isExecutionNecessary(long lastModified, boolean exists);

    Long execute(CBMDiskTask task, CBMDiskOperator operator) throws BuildException;

}
