package com.mineaurion.aurionworld.core.commands;

import com.mineaurion.aurionworld.world.AWorldException;

public class AUsageException extends RuntimeException {
    public static String UNKNOW = "";
    public static String NOT_ENOUGH = "Not enough arguments";
    public static String TOO_MANY = "Too many arguments";


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
