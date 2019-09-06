package com.mineaurion.aurionworld.core.commands;

import org.omg.CORBA.PUBLIC_MEMBER;

public class ACommandException extends RuntimeException {
    public static String PLAYER_NOT_EXIST = "Player %s doesn't exist";
    public static String PLAYER_IS_ALREADY_MEMBER = "%s is already trust as member";
    public static String PLAYER_IS_ALREADY_OWNER = "%s is already trust as member owner";
    public static String PLAYER_NOT_MEMBER = "This user isn't attached to %s world";
    public static String PLAYER_IS_CREATE = "%s is the creator, you can't manage him";
    public static String PLAYER_CANT_MANAGE_HIMSELF = "You can't manage yourself";
    public static String NOT_ALLOWED = "You are not allowed to do that!";

    public static String WORLD_NOT_EXIST = "This world doesn't exist";
    public static String WORLD_IS_LOADED = "This world is already loaded!";
    public static String WORLD_NOT_LOADED = "This world isn't loaded!";
    public static String WORLD_CANT_BE_LOADED = "World %s can't be loaded";
    public static String RULE_NOT_EXIST = "Rule %s doesn't exist!";

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
