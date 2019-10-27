package com.ldtteam.structurize.event.impl;

import com.ldtteam.structurize.structure.Structure;
import com.ldtteam.structurize.structure.StructureBB;
import com.ldtteam.structurize.structure.StructureInfo;
import net.minecraft.world.World;

public class PlacementSetupEvent extends BuildEvent
{
    public PlacementSetupEvent(final StructureInfo structureInfo, final World structureWorld)
    {
        super(null, null, structureInfo, structureWorld);
    }

    public void setStructure(final Structure structure)
    {
        super.structure = structure;
    }

    public void setStructurePosition(final StructureBB structurePosition)
    {
        super.structurePosition = structurePosition;
    }

    public void setStructureInfo(final StructureInfo structureInfo)
    {
        super.structureInfo = structureInfo;
    }

    public void setStructureWorld(final World structureWorld)
    {
        super.structureWorld = structureWorld;
    }
}
