package org.cbm.ant.viceteam;

public class ViceUtil
{

    /**
     * Escapes all chars other than [a-zA-Z0-9]
     * 
     * @param s the string
     * @return the escaped string
     */
    public static String escape(String s)
    {
        //		StringBuilder result = new StringBuilder();
        //
        //		for (int i = 0; i < s.length(); i += 1)
        //		{
        //			char c = s.charAt(i);
        //
        //			if (!Character.isLetterOrDigit(c))
        //			{
        //				result.append("\\");
        //			}
        //
        //			result.append(c);
        //		}
        //
        //		return result.toString();
        return "\"" + s + "\"";
    }
}
