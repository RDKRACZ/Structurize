package com.ldtteam.structurize.structure.providers;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.ldtteam.structurize.structure.blueprint.Blueprint;
import com.ldtteam.structurize.structure.blueprint.BlueprintUtils;
import com.ldtteam.structurize.pipeline.build.EventInfoHolder;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;

/**
 * Blueprint structure wrapper for {@link EventInfoHolder}
 */
public class BlueprintStructureDataProvider implements IStructureDataProvider
{
    private EventInfoHolder<BlueprintStructureDataProvider> event;

    private Blueprint blueprint;
    private Path blueprintPath;

    private BlockPos mirrorRotationAnchor = null;
    private Map<BlockPos, CompoundNBT> transformedTEs = null;

    private BlueprintStructureDataProvider()
    {
    }

    /**
     * Creates new structure provider.
     *
     * @return new instance
     */
    public static BlueprintStructureDataProvider create()
    {
        return new BlueprintStructureDataProvider();
    }

    /**
     * Sets event reference.
     *
     * @param eventIn actual event
     */
    public void setEvent(final EventInfoHolder<BlueprintStructureDataProvider> eventIn)
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
        // event.getPosition().resize(getXsize(), getYsize(), getZsize());
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
            /*
             * short index = 0;
             * for (final BlockState bs : getBlockPalette())
             * {
             * if (bs.getBlock() instanceof IAnchorBlock)
             * {
             * break;
             * }
             * index++;
             * }
             * for (final BlockPos pos : event.getPosition().getZeroBasedPosIterator())
             * {
             * if (getBlocks()[pos.getY()][pos.getZ()][pos.getX()] == index)
             * {
             * mirrorRotationAnchor = pos;
             * break;
             * }
             * }
             */
            if (mirrorRotationAnchor == null)
            {
                mirrorRotationAnchor = new BlockPos(getXsize() / 2, getYsize() / 2, getZsize() / 2);
            }
        }
        return mirrorRotationAnchor;
    }

    @Override
    public void rotateClockwise()
    {
        // event.getPosition().rotateCW(getZeroBasedMirrorRotationAnchor().add(event.getPosition().getAnchor()));
        applyMirrorRotationOnStructure(Rotation.CLOCKWISE_90, Mirror.NONE);
        // event.getRenderer().recompile();
    }

    @Override
    public void rotateCounterClockwise()
    {
        // event.getPosition().rotateCCW(getZeroBasedMirrorRotationAnchor().add(event.getPosition().getAnchor()));
        applyMirrorRotationOnStructure(Rotation.COUNTERCLOCKWISE_90, Mirror.NONE);
        // event.getRenderer().recompile();
    }

    @Override
    public void mirrorX()
    {
        // event.getPosition().mirrorX(getZeroBasedMirrorRotationAnchor().add(event.getPosition().getAnchor()));
        applyMirrorRotationOnStructure(Rotation.NONE, Mirror.FRONT_BACK);
        // event.getRenderer().recompile();
    }

    @Override
    public void mirrorZ()
    {
        // event.getPosition().mirrorZ(getZeroBasedMirrorRotationAnchor().add(event.getPosition().getAnchor()));
        applyMirrorRotationOnStructure(Rotation.NONE, Mirror.LEFT_RIGHT);
        // event.getRenderer().recompile();
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
