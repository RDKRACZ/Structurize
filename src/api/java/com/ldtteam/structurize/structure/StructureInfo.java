package com.ldtteam.structurize.structure;

import java.util.UUID;

public class StructureInfo
{
    private final UUID uuid;

    public StructureInfo(final UUID uuid)
    {
        this.uuid = uuid;
    }

    public UUID getUuid()
    {
        return uuid;
    }
}
