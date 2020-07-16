package com.ldtteam.structurize.structure;

public enum CenterMode
{
    SINGLE(0),
    DOUBLE(1);

    private final int id;

    private CenterMode(final int id)
    {
        this.id = id;
    }

    public int getId()
    {
        return id;
    }

    public static CenterMode fromId(final int id)
    {
        if (id == 0)
        {
            return SINGLE;
        }
        else
        {
            return DOUBLE;
        }
    }
}
