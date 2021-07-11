package org.cbm.ant.cbm.disk;

public class CBMDiskException extends Exception
{

    public enum Type
    {
        DISK_FULL("Disk is full"),
        NO_FREE_SECTOR("No free sector found");

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

    public CBMDiskException(Type type, Throwable cause, Object... parameters)
    {
        super(String.format(type.getMessage(), parameters), cause);
    }

    public CBMDiskException(Type type, Object... parameters)
    {
        super(String.format(type.getMessage(), parameters));
    }

}
