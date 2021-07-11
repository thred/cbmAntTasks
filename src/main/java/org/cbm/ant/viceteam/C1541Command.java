package org.cbm.ant.viceteam;

import java.io.File;

import org.apache.tools.ant.BuildException;
import org.cbm.ant.util.ProcessHandler;

public interface C1541Command
{

    boolean isFailOnError();

    boolean isExecutionNecessary(long lastModified, boolean exists);

    int execute(C1541 task, ProcessHandler handler, File image) throws BuildException;

}
