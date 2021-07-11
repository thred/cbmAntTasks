package org.cbm.ant.cc65;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import org.apache.tools.ant.BuildException;
import org.cbm.ant.util.Util;

public class Dependencies
{

    public static Dependencies load(File buildDir, File dependencyFile) throws BuildException
    {
        Dependencies result = new Dependencies(buildDir);

        try
        {
            try (BufferedReader reader = new BufferedReader(new FileReader(dependencyFile)))
            {
                String line;

                while ((line = reader.readLine()) != null)
                {
                    List<String> left = getLeft(line);
                    List<String> right = getRight(line);

                    if (right.size() > 0)
                    {
                        for (String l : left)
                        {
                            for (String r : right)
                            {
                                result.add(l, r);
                            }
                        }
                    }
                }
            }
        }
        catch (IOException e)
        {
            throw new BuildException("Error reading: " + dependencyFile.getAbsolutePath(), e);
        }

        return result;
    }

    public static List<String> getLeft(String line)
    {
        List<String> result = new ArrayList<>();
        StringTokenizer tokenizer = new StringTokenizer(line, " \t");

        while (tokenizer.hasMoreTokens())
        {
            String token = tokenizer.nextToken().trim();

            if (token.endsWith(":"))
            {
                result.add(token.substring(0, token.length() - 1));
                break;
            }

            result.add(token);
        }

        return result;
    }

    public static List<String> getRight(String line)
    {
        List<String> result = new ArrayList<>();
        boolean consume = false;
        StringTokenizer tokenizer = new StringTokenizer(line, " \t");

        while (tokenizer.hasMoreTokens())
        {
            String token = tokenizer.nextToken().trim();

            if (token.endsWith(":"))
            {
                consume = true;
                continue;
            }

            if (consume)
            {
                result.add(token);
            }
        }

        return result;
    }

    private final File buildDir;
    private final Map<File, Collection<File>> dependencies;

    private Dependencies(File buildDir)
    {
        super();

        this.buildDir = buildDir;

        dependencies = new HashMap<>();
    }

    public void add(String left, String right)
    {
        File leftFile = Util.toCanonicalForm(buildDir, new File(left));
        File rightFile = Util.toCanonicalForm(buildDir, new File(right));

        Collection<File> dependency = dependencies.get(leftFile);

        if (dependency == null)
        {
            dependency = new HashSet<>();

            dependencies.put(leftFile, dependency);
        }

        dependency.add(rightFile);
    }

    public Collection<File> get(File file)
    {
        return dependencies.get(Util.toCanonicalForm(buildDir, file));
    }

    public long getLastModified(File file)
    {
        Collection<File> dependency = get(file);

        if (dependency == null)
        {
            return Long.MIN_VALUE;
        }

        long max = Long.MIN_VALUE;

        for (File current : dependency)
        {
            max = Math.max(max, current.lastModified());
        }

        return max;
    }
}
