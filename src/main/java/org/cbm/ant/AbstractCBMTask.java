package org.cbm.ant;

import java.io.File;
import java.util.Collection;
import java.util.Iterator;

import org.apache.tools.ant.Task;
import org.apache.tools.ant.types.FileSet;
import org.cbm.ant.util.AntFile;
import org.cbm.ant.util.Util;

public abstract class AbstractCBMTask extends Task
{

    public Collection<AntFile> collect(Collection<FileSet> fileSets, File... additionalFiles)
    {
        return Util.collect(getProject(), fileSets, additionalFiles);
    }

    public Iterator<AntFile> iterate(Collection<FileSet> fileSets, File... additionalFiles)
    {
        return Util.iterate(getProject(), fileSets, additionalFiles);
    }

    public boolean anyExsits(Collection<FileSet> fileSets, File... additionalFiles)
    {
        return Util.anyExists(getProject(), fileSets, additionalFiles);
    }

    public long lastModified(Collection<FileSet> fileSets, File... additionalFiles)
    {
        return Util.lastModified(getProject(), fileSets, additionalFiles);
    }

}
