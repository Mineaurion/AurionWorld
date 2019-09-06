package com.mineaurion.aurionworld.core.commands;

public class ACommandException extends RuntimeException {
    public ACommandException() {
        super();
    }

    public ACommandException(String message) {
        super(message);
    }

    public ACommandException(String message, Throwable cause) {
        super(message, cause);
    }

    public ACommandException(Throwable cause) {
        super(cause);
    }

    protected ACommandException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
