package org.cbm.ant.verify;

import org.apache.tools.ant.BuildException;

public interface VerifyTask
{

    void execute(Verify task) throws BuildException;
}
