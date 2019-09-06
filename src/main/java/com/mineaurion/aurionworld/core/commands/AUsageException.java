package com.mineaurion.aurionworld.core.commands;

public class AUsageException extends RuntimeException {
    public AUsageException() {
        super();
    }

    public AUsageException(String message) {
        super(message);
    }

    public AUsageException(String message, Throwable cause) {
        super(message, cause);
    }

    public AUsageException(Throwable cause) {
        super(cause);
    }

    protected AUsageException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
