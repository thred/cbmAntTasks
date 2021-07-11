package org.cbm.ant.cbm.disk;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class CBMDiskUtil
{

    public static final int MARK_BAM = 0xfe;
    public static final int MARK_DIR = 0xff;

    private static final char PETSCII_MAPPING[] = {
        '?',
        '?',
        '?',
        '?',
        '?',
        '?',
        '?',
        '?',
        '?',
        '?',
        '?',
        '?',
        '?',
        '?',
        '?',
        '?', //
        '?',
        '?',
        '?',
        '?',
        '?',
        '?',
        '?',
        '?',
        '?',
        '?',
        '?',
        '?',
        '?',
        '?',
        '?',
        '?', //
        ' ',
        '!',
        '\"',
        '#',
        '$',
        '%',
        '&',
        '\'',
        '(',
        ')',
        '*',
        '+',
        ',',
        '-',
        '.',
        '/', //
        '0',
        '1',
        '2',
        '3',
        '4',
        '5',
        '6',
        '7',
        '8',
        '9',
        ':',
        ';',
        '<',
        '=',
        '>',
        '?', //
        '@',
        'a',
        'b',
        'c',
        'd',
        'e',
        'f',
        'g',
        'h',
        'i',
        'j',
        'k',
        'l',
        'm',
        'n',
        'o', //
        'p',
        'q',
        'r',
        's',
        't',
        'u',
        'v',
        'w',
        'x',
        'y',
        'z',
        '[',
        '\u00a3',
        ']',
        '\u2191',
        '\u2190', //
        '_',
        'A',
        'B',
        'C',
        'D',
        'E',
        'F',
        'G',
        'H',
        'I',
        'J',
        'K',
        'L',
        'M',
        'N',
        'O', //
        'P',
        'Q',
        'R',
        'S',
        'T',
        'U',
        'V',
        'W',
        'X',
        'Y',
        'Z',
        '?',
        '?',
        '?',
        '?',
        '?', //
        '?',
        '?',
        '?',
        '?',
        '?',
        '?',
        '?',
        '?',
        '?',
        '?',
        '?',
        '?',
        '?',
        '?',
        '?',
        '?', //
        '?',
        '?',
        '?',
        '?',
        '?',
        '?',
        '?',
        '?',
        '?',
        '?',
        '?',
        '?',
        '?',
        '?',
        '?',
        '?', //
        '\u00a0',
        '?',
        '?',
        '?',
        '?',
        '?',
        '?',
        '?',
        '?',
        '?',
        '?',
        '?',
        '?',
        '?',
        '?',
        '?', //
        '?',
        '?',
        '?',
        '?',
        '?',
        '?',
        '?',
        '?',
        '?',
        '?',
        '?',
        '?',
        '?',
        '?',
        '?',
        '?', //
        '?',
        '?',
        '?',
        '?',
        '?',
        '?',
        '?',
        '?',
        '?',
        '?',
        '?',
        '?',
        '?',
        '?',
        '?',
        '?', //
        '?',
        '?',
        '?',
        '?',
        '?',
        '?',
        '?',
        '?',
        '?',
        '?',
        '?',
        '?',
        '?',
        '?',
        '?',
        '?', //
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

        return result.byteValue();
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

    public static byte[] toCBMDOSName(String s, int length)
    {
        byte[] result = new byte[length];

        Arrays.fill(result, (byte) 0xa0);

        for (int i = 0; i < s.length() && i < length; i += 1)
        {
            result[i] = ascii2petscii(s.charAt(i));
        }

        return result;
    }

    public static String fromCBMDOSName(byte[] bytes)
    {
        StringBuilder builder = new StringBuilder();

        for (byte b : bytes)
        {
            builder.append(petscii2ascii(b));
        }

        return builder.toString();
    }

}
