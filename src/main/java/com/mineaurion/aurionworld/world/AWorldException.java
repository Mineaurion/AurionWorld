package com.mineaurion.aurionworld.world;

public class AWorldException extends Exception
{
    private static final long serialVersionUID = 1L;

    public static enum Type
    {
        ALREADY_EXISTS("A worlds with that name already exists"),
        NO_PROVIDER("There is no provider by that name"),
        NO_WORLDTYPE("There is no worlds type by that name");

        public String error;

        private Type(String error)
        {
            this.error = error;
        }
    }

    public Type type;

    protected AWorldException(Type type)
    {
        this.type = type;

    }
}

