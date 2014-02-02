package org.cbm.ant.verify;

import java.util.ArrayList;
import java.util.List;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;

public class Verify extends Task
{

	private final List<VerifyTask> tasks = new ArrayList<VerifyTask>();

	public Verify()
	{
		super();
	}

	public void addSize(VerifySizeTask task)
	{
		tasks.add(task);
	}

	@Override
	public void execute() throws BuildException
	{
		log("Verifying:");

		for (VerifyTask task : tasks)
		{
			task.execute(this);
		}
	}

}
