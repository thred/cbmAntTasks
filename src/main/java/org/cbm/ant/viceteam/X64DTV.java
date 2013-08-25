package org.cbm.ant.viceteam;

import java.util.HashMap;
import java.util.Map;

public class X64DTV extends AbstractViceLaunchTask
{

	private static final Map<String, String> EXECUTABLES = new HashMap<String, String>();

	static
	{
		EXECUTABLES.put("Linux.*", "x64dtv");
		EXECUTABLES.put("Windows.*", "x64dtv.exe");
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
