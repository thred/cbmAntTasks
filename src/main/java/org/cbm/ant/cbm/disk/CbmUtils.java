package org.cbm.ant.cbm.disk;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class CbmUtils
{

    public static final int MARK_BAM = 0xfe;
    public static final int MARK_DIR = 0xff;

    private static final char PETSCII_MAPPING[] = {
        '?', '?', '?', '?', '?', '?', '?', '?', '?', '?', '?', '?', '?', '?', '?', '?', //

        '?', '?', '?', '?', '?', '?', '?', '?', '?', '?', '?', '?', '?', '?', '?', '?', //

        ' ', '!', '\"', '#', '$', '%', '&', '\'', '(', ')', '*', '+', ',', '-', '.', '/', //

        '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', ':', ';', '<', '=', '>', '?', //

        '@', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', //

        'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', '[', '\u00a3', ']', '\u2191', '\u2190', //

        '\u2500', '\u2660', '\u2502', '\u2500', '?', '?', '?', '?', '?', '\u256e', '\u2570', '\u256f', '?', '\u2572',
        '\u2571', '?', //

        '?', '\u25cf', '?', '\u2665', '?', '\u256d', '\u2573', '\u25cb', '\u2663', '?', '\u2666', '\u253c', '?',
        '\u2502', '?', '\u25e5', //

        '?', '?', '?', '?', '?', '?', '?', '?', '?', '?', '?', '?', '?', '?', '?', '?', //

        '?', '?', '?', '?', '?', '?', '?', '?', '?', '?', '?', '?', '?', '?', '?', '?', //

        '\u00a0', '\u258c', '\u2584', '\u2594', '\u2581', '\u258e', '\u2592', '\u258a', '?', '\u25e4', '\u258a',
        '\u251c', '\u2597', '\u2514', '\u2510', '\u2582', //

        '\u250c', '\u2534', '\u252c', '\u2524', '\u258e', '\u258d', '\u258b', '\u2586', '\u2585', '\u2583', '?',
        '\u2596', '\u259d', '\u2518', '\u2598', '\u259a', //

        '_', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', //

        'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', '\u253c', '?', '\u2502', '\u03c0', '\u25e3', //

        '\u00a0', '\u258c', '\u2584', '\u2594', '\u2581', '\u258e', '\u2592', '\u258a', '?', '\u25e4', '\u258a',
        '\u251c', '\u2597', '\u2514', '\u2510', '\u2582', //

        '\u250c', '\u2534', '\u252c', '\u2524', '\u258e', '\u258d', '\u258b', '\u2586', '\u2585', '\u2583', '\u2713',
        '\u2596', '\u259d', '\u2518', '\u2598', '\u03c0', //
    };

    private static final Map<Character, Byte> ASCII_MAPPING = new HashMap<>();

    static
    {
        for (int i = 0; i < PETSCII_MAPPING.length; i += 1)
        {
            ASCII_MAPPING.put(PETSCII_MAPPING[i], (byte) (i & 0xff));
        }
    }

    public static byte ascii2petscii(char ch)
    {
        Byte result = ASCII_MAPPING.get(ch);

        if (result == null)
        {
            throw new IllegalArgumentException(String.format("Unmapable character %c (%04x)", ch, (int) ch));
        }

        return result;
    }

    public static char petscii2ascii(byte b)
    {
        return PETSCII_MAPPING[b & 0xff];
    }

    public static String apostrophes(String s)
    {
        String result = "\"";
        int index = s.indexOf('\u00a0');

        if (index < 0)
        {
            result += s + "\"";
        }
        else
        {
            result += s.substring(0, index) + "\"" + s.substring(index);
        }

        result = result.replace("\u00a0", " ");

        return result.trim();
    }

    public static char id2Key(int id)
    {
        if (id == MARK_BAM)
        {
            return '#';
        }

        if (id == MARK_DIR)
        {
            return '$';
        }

        if (id < 0)
        {
            return '?';
        }

        if (id == 0)
        {
            return '*';
        }

        id -= 1;

        if (id < 10)
        {
            return (char) ('0' + id);
        }

        id -= 10;

        if (id < 26)
        {
            return (char) ('a' + id);
        }

        id -= 26;

        if (id < 26)
        {
            return (char) ('A' + id);
        }

        return '@';
    }

    public static byte[] toCbmDosName(String s, int length)
    {
        byte[] result = new byte[length];

        Arrays.fill(result, (byte) 0xa0);

        for (int i = 0; i < s.length() && i < length; i += 1)
        {
            result[i] = ascii2petscii(s.charAt(i));
        }

        return result;
    }

    public static String fromCbmDosName(byte[] bytes)
    {
        StringBuilder builder = new StringBuilder();

        for (byte element : bytes)
        {
            builder.append(petscii2ascii(element));
        }

        return builder.toString();
    }

    public static String trimCbmDosName(String name)
    {
        int length = name.length();

        while (length > 0 && name.charAt(length - 1) == '\u00a0')
        {
            --length;
        }

        return name.substring(0, length);
    }

    public static String toHex(String prefix, int value, int length)
    {
        String result = Integer.toHexString(value);

        while (result.length() < length)
        {
            result = "0" + result;
        }

        return prefix + result;
    }
}
