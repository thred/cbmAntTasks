package org.cbm.ant.cbm.disk;

import java.io.IOException;

public class CbmIOException extends IOException
{
    public enum Type
    {
        INVALID_DATA_SIZE("Invalid data size: %s bytes"),
        INCOMPATIBLE_DISK_TYPE("Incompatible disk type: %s"),
        DISK_FULL("Disk is full."),
        DIRECTORY_FULL("Directory is full."),
        FILE_NOT_EMPTY("File is not empty."),
        FILE_LOCKED("File is locked."),
        FILE_ALREADY_OPEN("File already open."),
        LOOP_DETECTED("File loop detected at %s."),
        FILE_NOT_FOUND("File not found: %s"),
        DISK_NOT_FORMATTED("Disk not formatted: %s"),
        FILE_CLOSED("File is closed."),
        FILE_EXISTS("File exists already: %s"),
        NOT_SUPPORTED("Feature not supported.");

        private final String message;

        Type(String message)
        {
            this.message = message;
        }

        public String getMessage()
        {
            return message;
        }
    }

    private static final long serialVersionUID = 5674502158975407754L;

    private final Type type;

    public CbmIOException(Type type, Throwable cause, Object... parameters)
    {
        super(String.format(type.getMessage(), parameters), cause);

        this.type = type;
    }

    public CbmIOException(Type type, Object... parameters)
    {
        super(String.format(type.getMessage(), parameters));

        this.type = type;
    }

    public Type getType()
    {
        return type;
    }
}
