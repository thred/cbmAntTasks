package org.cbm.ant.viceteam;

import java.util.HashMap;
import java.util.Map;

public class XPlus4 extends AbstractViceLaunchTask
{

	private static final Map<String, String> EXECUTABLES = new HashMap<String, String>();

	static
	{
		EXECUTABLES.put("Linux.*", "xplus4");
		EXECUTABLES.put("Windows.*", "xplus4.exe");
	}

	/**
	 * @see org.cbm.ant.viceteam.AbstractViceTask#getExecutables()
	 */
	@Override
	public Map<String, String> getExecutables()
	{
		return EXECUTABLES;
	}

}
