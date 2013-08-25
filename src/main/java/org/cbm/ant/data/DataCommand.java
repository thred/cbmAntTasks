package org.cbm.ant.data;

import java.io.IOException;
import java.io.OutputStream;

import org.apache.tools.ant.BuildException;

public interface DataCommand {

	boolean isExecutionNecessary(long lastModified, boolean exists);
	
	void execute(Data task, OutputStream out) throws BuildException, IOException;
	
}
