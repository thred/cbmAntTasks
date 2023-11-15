package org.cbm.ant.data;

import java.io.File;
import java.io.IOException;
import java.util.StringTokenizer;

import org.apache.tools.ant.BuildException;
import org.cbm.ant.util.Util;
import org.cbm.ant.util.XMLParser;

public class DataFont implements DataCommand
{
    private File fontFile;
    private String name;

    public DataFont()
    {
        super();
    }

    public File getFont()
    {
        return fontFile;
    }

    public void setFont(File font)
    {
        fontFile = font;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    /**
     * @see org.cbm.ant.data.DataCommand#isExecutionNecessary(long, boolean)
     */
    @Override
    public boolean isExecutionNecessary(long lastModified, boolean exists)
    {
        File file = getFont();

        if (exists && file.exists())
        {
            return file.lastModified() > lastModified;
        }

        return true;
    }

    /**
     * @see org.cbm.ant.data.DataCommand#execute(DataWriter)
     */
    @Override
    public void execute(DataWriter writer) throws BuildException, IOException
    {
        XMLParser parser = new XMLParser(fontFile);

        for (XMLParser current : parser.into("cbm-font-editor-project").into("fonts").iterator("font"))
        {
            if (getName().equals(current.attribute("name", null)))
            {
                StringTokenizer tokenizer = new StringTokenizer(current.textOf("memory"), " ,\r\n\t\f");

                while (tokenizer.hasMoreTokens())
                {
                    writer.writeByte(Util.parseHex(tokenizer.nextToken()));
                }

                return;
            }
        }

        throw new BuildException(String.format("Font %s not found", getName()));
    }
}
