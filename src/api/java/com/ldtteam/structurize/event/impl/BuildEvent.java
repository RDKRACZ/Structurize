package com.ldtteam.structurize.event.impl;

import com.ldtteam.structurize.event.Event;
import com.ldtteam.structurize.structure.Structure;
import com.ldtteam.structurize.structure.StructureBB;
import com.ldtteam.structurize.structure.StructureInfo;
import net.minecraft.world.World;

public class BuildEvent extends Event
{
    protected Structure structure;
    protected StructureBB structurePosition;
    protected StructureInfo structureInfo;
    protected World structureWorld;

    public BuildEvent(final Structure structure, final StructureBB structurePosition, final StructureInfo structureInfo, final World structureWorld)
    {
        this.structure = structure;
        this.structurePosition = structurePosition;
        this.structureInfo = structureInfo;
        this.structureWorld = structureWorld;
    }

    public Structure getStructure()
    {
        return structure;
    }

    public StructureBB getStructurePosition()
    {
        return structurePosition;
    }

    public StructureInfo getStructureInfo()
    {
        return structureInfo;
    }

    public World getStructureWorld()
    {
        return structureWorld;
    }
}
