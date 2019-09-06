package com.mineaurion.aurionworld.world;

public class AWorldException extends RuntimeException
{
    public final static String NO_PROVIDER = "%s is no provider by that name";
    public final static String NO_WORLDTYPE = "%s is no worlds type by that name";

    public AWorldException() {
        super();
    }

    public AWorldException(String message) {
        super(message);
    }

    public AWorldException(String message, Throwable cause) {
        super(message, cause);
    }

    public AWorldException(Throwable cause) {
        super(cause);
    }

    protected AWorldException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}

