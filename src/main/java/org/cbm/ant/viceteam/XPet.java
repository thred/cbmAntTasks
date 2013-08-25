package org.cbm.ant.viceteam;

import java.util.HashMap;
import java.util.Map;

public class XPet extends AbstractViceLaunchTask
{

	private static final Map<String, String> EXECUTABLES = new HashMap<String, String>();

	static
	{
		EXECUTABLES.put("Linux.*", "xpet");
		EXECUTABLES.put("Windows.*", "xpet.exe");
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
