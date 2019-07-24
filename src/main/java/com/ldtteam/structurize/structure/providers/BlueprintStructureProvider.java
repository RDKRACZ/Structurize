package com.ldtteam.structurize.structure.providers;

import java.nio.file.Path;
import com.ldtteam.structurize.structure.blueprint.Blueprint;
import com.ldtteam.structurize.structure.blueprint.BlueprintUtils;
import com.ldtteam.structurize.structure.PlaceEventInfoHolder;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * Blueprint structure wrapper for {@link PlaceEventInfoHolder}
 */
public class BlueprintStructureProvider implements IStructureDataProvider
{
    private Blueprint blueprint;
    private Path blueprintPath;
    private World world;

    private BlueprintStructureProvider()
    {
    }

    /**
     * Creates new structure provider.
     *
     * @param world build and render world
     * @return new instance
     */
    public static BlueprintStructureProvider create(final World world)
    {
        final BlueprintStructureProvider result = new BlueprintStructureProvider();
        result.world = world;
        return result;
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
    public void rotateClockwise()
    {
        final BlockPos offset = blueprint.rotateWithMirror(Rotation.CLOCKWISE_90, Mirror.NONE, world);
    }

    @Override
    public void rotateCounterClockwise()
    {
        final BlockPos offset = blueprint.rotateWithMirror(Rotation.COUNTERCLOCKWISE_90, Mirror.NONE, world);
    }

    @Override
    public void mirrorX()
    {
        final BlockPos offset = blueprint.rotateWithMirror(Rotation.NONE, Mirror.FRONT_BACK, world);
    }

    @Override
    public void mirrorZ()
    {
        final BlockPos offset = blueprint.rotateWithMirror(Rotation.NONE, Mirror.LEFT_RIGHT, world);
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
}
