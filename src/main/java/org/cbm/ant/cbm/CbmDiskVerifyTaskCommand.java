package org.cbm.ant.cbm;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;
import java.util.zip.CRC32;
import java.util.zip.Checksum;

import org.apache.tools.ant.BuildException;
import org.cbm.ant.cbm.disk.CbmDisk;
import org.cbm.ant.util.IOUtils;

public class CbmDiskVerifyTaskCommand extends AbstractCbmDiskTaskCommand
{
    private String file;
    private File reference;

    public CbmDiskVerifyTaskCommand()
    {
        super();
    }

    public String getFile() throws BuildException
    {
        if (file == null || file.trim().length() == 0)
        {
            throw new BuildException("File is missing");
        }

        return file;
    }

    public void setFile(String file)
    {
        this.file = file;
    }

    public File getReference()
    {
        if (reference.isDirectory())
        {
            return new File(reference, getFile());
        }

        return reference;
    }

    public void setReference(File reference)
    {
        this.reference = reference;
    }

    /**
     * {@inheritDoc}
     *
     * @see org.cbm.ant.cbm.CbmDiskTaskCommand#isExecutionNecessary(long, boolean)
     */
    @Override
    public boolean isExecutionNecessary(long lastModified, boolean exists)
    {
        return true;
    }

    /**
     * {@inheritDoc}
     *
     * @see org.cbm.ant.cbm.CbmDiskTaskCommand#execute(org.cbm.ant.cbm.CbmDiskTask, CbmDisk)
     */
    @Override
    public Long execute(CbmDiskTask task, CbmDisk disk) throws BuildException
    {
        task.log(String.format("Verifying \"%s\" from disk against \"%s}\" ...", file, reference));

        byte[] bytes;

        try
        {
            bytes = disk.readFileFully(getFile());
        }
        catch (IOException e)
        {
            throw new BuildException(String.format("Failed to read file \"%s\"", getFile()), e);
        }

        String checksum = computeChecksum(bytes);

        byte[] referenceBytes;

        try (InputStream in = new FileInputStream(getReference()))
        {
            referenceBytes = IOUtils.readFully(in);
        }
        catch (IOException e)
        {
            throw new BuildException(String.format("Failed to read file \"%s\": %s", getReference(), e.getMessage()),
                e);
        }

        String referenceChecksum = computeChecksum(referenceBytes);

        if (!Objects.equals(checksum, referenceChecksum))
        {
            throw new BuildException(String
                .format("The checksums of file \"%s\" and the reference \"%s}\" are not the same: %s != %s", file,
                    reference, checksum, referenceChecksum));
        }

        return null;
    }

    private static String computeChecksum(byte[] bytes)
    {
        Checksum crc32 = new CRC32();

        crc32.update(bytes, 0, bytes.length);

        return String.format("%1$016X", crc32.getValue());
    }
}
