package com.ldtteam.structurize.structure.providers;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.ldtteam.structurize.structure.blueprint.Blueprint;
import com.ldtteam.structurize.structure.blueprint.BlueprintUtils;
import com.ldtteam.structurize.block.IAnchorBlock;
import com.ldtteam.structurize.pipeline.PlaceEventInfoHolder;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;

/**
 * Blueprint structure wrapper for {@link PlaceEventInfoHolder}
 */
public class BlueprintStructureProvider implements IStructureDataProvider
{
    private Blueprint blueprint;
    private Path blueprintPath;
    private PlaceEventInfoHolder<BlueprintStructureProvider> event;
    private BlockPos mirrorRotationAnchor = null;
    private Map<BlockPos, CompoundNBT> transformedTEs = null;

    private BlueprintStructureProvider()
    {
    }

    /**
     * Creates new structure provider.
     *
     * @return new instance
     */
    public static BlueprintStructureProvider create()
    {
        return new BlueprintStructureProvider();
    }

    /**
     * Sets event reference.
     *
     * @param eventIn actual event
     */
    public void setEvent(final PlaceEventInfoHolder<BlueprintStructureProvider> eventIn)
    {
        event = eventIn;
    }

    /**
     * Sets blueprint file system path.
     *
     * @param path valid path for blueprint
     */
    public void setStructurePath(final Path path)
    {
        blueprint = BlueprintUtils.readFromStream(path);
        blueprintPath = path;
        mirrorRotationAnchor = null;
        event.getPosition().resize(getXsize(), getYsize(), getZsize());
    }

    /**
     * Getter for blueprint file system path.
     *
     * @return path of current blueprint
     */
    public Path getStructurePath()
    {
        return blueprintPath;
    }

    @Override
    public BlockPos getZeroBasedMirrorRotationAnchor()
    {
        if (mirrorRotationAnchor == null)
        {
            short index = 0;
            for (final BlockState bs : getBlockPalette())
            {
                if (bs.getBlock() instanceof IAnchorBlock)
                {
                    break;
                }
                index++;
            }
            for (final BlockPos pos : event.getPosition().getZeroBasedPosIterator())
            {
                if (getBlocks()[pos.getY()][pos.getZ()][pos.getX()] == index)
                {
                    mirrorRotationAnchor = pos;
                    break;
                }
            }
            if (mirrorRotationAnchor == null)
            {
                mirrorRotationAnchor = new BlockPos(getXsize() / 2, getYsize() / 2, getZsize() / 2);
            }
        }
        return mirrorRotationAnchor;
    }

    @Override
    public void applyMirrorRotationOnStructure(final Rotation rotation, final Mirror mirror)
    {
        mirrorRotationAnchor = null;
        transformedTEs = null;
        blueprint.rotateWithMirror(rotation, mirror, event.getWorld());
    }

    @Override
    public int getXsize()
    {
        return blueprint.getSizeX();
    }

    @Override
    public int getYsize()
    {
        return blueprint.getSizeY();
    }

    @Override
    public int getZsize()
    {
        return blueprint.getSizeZ();
    }

    @Override
    public List<String> getRequiredMods()
    {
        return blueprint.getRequiredMods();
    }

    @Override
    public List<BlockState> getBlockPalette()
    {
        return blueprint.getPalette();
    }

    @Override
    public short[][][] getBlocks()
    {
        return blueprint.getStructure();
    }

    @Override
    public List<CompoundNBT> getEntities()
    {
        return blueprint.getEntitiesAsList();
    }

    @Override
    public Map<BlockPos, CompoundNBT> getTileEntities()
    {
        if (transformedTEs == null)
        {
            transformedTEs = new HashMap<>();
            for (final CompoundNBT te : blueprint.getTileEntities())
            {
                transformedTEs.put(new BlockPos(te.getInt("x"), te.getInt("y"), te.getInt("z")), te);
            }
        }
        return transformedTEs;
    }
}
