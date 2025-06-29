package org.cbm.ant.oscar64;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.types.FileSet;
import org.cbm.ant.AbstractCBMProcessHandlerTask;
import org.cbm.ant.util.AntFile;
import org.cbm.ant.util.OS;
import org.cbm.ant.util.ProcessHandler;

public class Oscar64 extends AbstractCBMProcessHandlerTask
{

    private static final Map<OS, String> EXECUTABLES = new HashMap<>();

    static
    {
        EXECUTABLES.put(OS.LINUX, "oscar64");
        EXECUTABLES.put(OS.WINDOWS, "oscar64.exe");
    }

    private final Collection<FileSet> files = new ArrayList<>();

    private final List<Define> defines = new ArrayList<>();

    private File source;

    private TargetFormat targetFormat = TargetFormat.prg;

    private File outputPath;

    private boolean petscii = false; // Add petscii parameter

    private String optimize; // Add optimize parameter

    private boolean debug = false; // Add debug parameter

    private TargetMachine target = TargetMachine.c64; // Default to c64

    private boolean noLong = false; // Add noLong parameter
    private boolean noFloat = false; // Add noFloat parameter
    private boolean heapCheck = false; // Add heapCheck parameter
    private boolean noBssClear = false; // Add noBssClear parameter
    private boolean noZpClear = false; // Add noZpClear parameter

    private boolean nativeCode = true; // Add nativeCode parameter, default true

    private boolean byteCode = false; // Add byteCode parameter, default false

    @Override
    protected String getHomePropertyKeyPrefix()
    {
        return "oscar64";
    }

    @Override
    protected String getExecutablePropertyKey()
    {
        return "oscar64";
    }

    @Override
    protected Map<OS, String> getExecutables()
    {
        return EXECUTABLES;
    }

    public void addFiles(FileSet files)
    {
        this.files.add(files);
    }

    public Collection<FileSet> getFiles()
    {
        return files;
    }

    public File getSource()
    {
        return source;
    }

    public void setSource(File source)
    {
        this.source = source;
    }

    public TargetFormat getTargetFormat()
    {
        return targetFormat;
    }

    public void setTargetFormat(TargetFormat targetFormat)
    {
        this.targetFormat = targetFormat;
    }

    public File getOutputPath()
    {
        return outputPath;
    }

    public void setOutputPath(File outputPath)
    {
        this.outputPath = outputPath;
    }

    public void addDefine(Define define)
    {
        defines.add(define);
    }

    public List<Define> getDefines()
    {
        return defines;
    }

    public boolean isPetscii()
    {
        return petscii;
    }

    public void setPetscii(boolean petscii)
    {
        this.petscii = petscii;
    }

    public String getOptimize()
    {
        return optimize;
    }

    public void setOptimize(String optimize)
    {
        this.optimize = optimize;
    }

    public boolean isDebug()
    {
        return debug;
    }

    public void setDebug(boolean debug)
    {
        this.debug = debug;
    }

    public TargetMachine getTarget()
    {
        return target;
    }

    public void setTarget(TargetMachine target)
    {
        this.target = target;
    }

    public boolean isNoLong()
    {
        return noLong;
    }

    public void setNoLong(boolean noLong)
    {
        this.noLong = noLong;
    }

    public boolean isNoFloat()
    {
        return noFloat;
    }

    public void setNoFloat(boolean noFloat)
    {
        this.noFloat = noFloat;
    }

    public boolean isHeapCheck()
    {
        return heapCheck;
    }

    public void setHeapCheck(boolean heapCheck)
    {
        this.heapCheck = heapCheck;
    }

    public boolean isNoBssClear()
    {
        return noBssClear;
    }

    public void setNoBssClear(boolean noBssClear)
    {
        this.noBssClear = noBssClear;
    }

    public boolean isNoZpClear()
    {
        return noZpClear;
    }

    public void setNoZpClear(boolean noZpClear)
    {
        this.noZpClear = noZpClear;
    }

    public boolean isNativeCode()
    {
        return nativeCode;
    }

    public void setNativeCode(boolean nativeCode)
    {
        this.nativeCode = nativeCode;
    }

    public boolean isByteCode()
    {
        return byteCode;
    }

    public void setByteCode(boolean byteCode)
    {
        this.byteCode = byteCode;
    }

    /**
     * @see org.apache.tools.ant.Task#execute()
     */
    @Override
    public void execute() throws BuildException
    {
        if (source == null)
        {
            for (AntFile inputFile : collect(files))
            {
                execute(inputFile, Collections.emptyList());
            }
        }
        else
        {
            AntFile inputFile = AntFile.create(getProject().getBaseDir(), source);

            execute(inputFile, collect(files));
        }
    }

    protected void execute(AntFile inputFile, Collection<AntFile> transparentInputFiles) throws BuildException
    {
        String targetFormat = this.targetFormat.name();
        AntFile outputFile = null;

        if (outputPath != null)
        {
            outputFile = AntFile
                .create(getProject().getBaseDir(), outputPath, inputFile.getFilename())
                .withExtension(targetFormat);
        }
        else
        {
            outputFile = inputFile.withExtension(targetFormat);
        }

        Collection<File> inputFiles = new ArrayList<>(transparentInputFiles);

        inputFiles.add(inputFile);

        if (isExecutionNecessary(inputFiles, outputFile))
        {
            ProcessHandler handler = createProcessHandler();

            for (Define define : getDefines())
            {
                StringBuilder assignment = new StringBuilder().append(define.getSymbol());

                if (define.getValue() != null)
                {
                    assignment.append("=").append(define.getValue());
                }

                handler.parameter("-D").parameter(assignment.toString());
            }

            // Add boolean parameters as -d options
            if (noLong)
            {
                handler.parameter("-dNOLONG");
            }
            if (noFloat)
            {
                handler.parameter("-dNOFLOAT");
            }
            if (heapCheck)
            {
                handler.parameter("-dHEAPCHECK");
            }
            if (noBssClear)
            {
                handler.parameter("-dNOBSSCLEAR");
            }
            if (noZpClear)
            {
                handler.parameter("-dNOZPCLEAR");
            }

            handler.parameter("-tf", targetFormat);

            // Add target machine option
            handler.parameter("-tm", target.name());

            // Add optimize options
            if (optimize != null && !optimize.trim().isEmpty())
            {
                for (String opt : optimize.split(","))
                {
                    String o = opt.trim().toLowerCase();

                    switch (o)
                    {
                        case "size":
                            handler.parameter("-Os");
                            break;
                        case "autoinline":
                            handler.parameter("-Oi");
                            break;
                        case "asm":
                            handler.parameter("-Oa");
                            break;
                        case "globals":
                            handler.parameter("-Oz");
                            break;
                        case "constparams":
                            handler.parameter("-Op");
                            break;
                        case "outline":
                            handler.parameter("-Oo");
                            break;
                        case "arrays":
                            handler.parameter("-Ox");
                            break;
                        case "disable":
                        case "0":
                            handler.parameter("-O0");
                            break;
                        case "1":
                        case "2":
                        case "3":
                            handler.parameter("-O" + o);
                            break;
                        default:
                            log("Unknown optimize option: " + opt);
                            break;
                    }
                }
            }

            if (petscii) // Add -psci option if petscii is true
            {
                handler.parameter("-psci");
            }

            if (debug) // Add -gp option if debug is true
            {
                handler.parameter("-gp");
            }

            if (nativeCode) // Add -n option if nativeCode is true
            {
                handler.parameter("-n");
            }

            if (byteCode) // Add -bc option if byteCode is true
            {
                handler.parameter("-bc");
            }

            handler.parameter("-o", outputFile.ensureDirectory());

            handler.parameter(inputFile);

            log("Executing: " + handler);
            log("");

            int exitValue = handler.consume();

            log("");

            if (exitValue != 0)
            {
                outputFile.delete();

                throw new BuildException("Failed with exit value " + exitValue);
            }

            outputFile.setLastModified(inputFile.lastModified());
        }
    }

    private boolean isExecutionNecessary(Collection<File> inputFiles, File outputFile)
    {
        boolean result = !outputFile.exists();

        if (result)
        {
            return true;
        }

        for (File inputFile : inputFiles)
        {
            if (outputFile.lastModified() < inputFile.lastModified())
            {
                return true;
            }

            if (outputFile.lastModified() > inputFile.lastModified())
            {
                log("\"" + outputFile.getAbsolutePath() + "\" got modified in the future");
            }
        }

        return false;
    }
}
