package com.ldtteam.structurize.structure;

import java.nio.file.Path;
import com.ldtteam.structurize.structure.blueprint.Blueprint;
import com.ldtteam.structurize.structure.blueprint.BlueprintUtils;
import com.ldtteam.structurize.structure.providers.BlueprintStructureProvider;
import com.ldtteam.structurize.structure.providers.IStructureDataProvider;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * Holds necessary informations of structrure placement event.
 * Created when any building tool is opened.
 * Passed into BuildProvider after placement data is confirmed by player.
 * Destroyed when BuildProvider finished its job.
 *
 * @param <T> DataStructureProvider type
 */
public class PlaceEventInfoHolder<T extends IStructureDataProvider>
{
    private StructureBB position;
    private T structure;
    private World world;

    /**
     * Creates a new placement event info holder.
     * TODO: add facing
     *
     * @param structure structure provider
     * @param anchorPos position where should structure begin
     * @param world     world of player
     */
    private PlaceEventInfoHolder(final T structure, final BlockPos anchorPos, final World world)
    {
        this.structure = structure;
        this.world = world;
        position = new StructureBB(anchorPos, anchorPos.add(structure.getXsize(), structure.getYsize(), structure.getZsize()));
    }

    /**
     * Creates blueprint event.
     *
     * @param anchorPos position where should structure begin
     * @param world     world of player
     * @return new blueprint event
     */
    public static PlaceEventInfoHolder<BlueprintStructureProvider> createBlueprintEvent(final BlockPos anchorPos, final World world)
    {
        final PlaceEventInfoHolder<BlueprintStructureProvider> result = new PlaceEventInfoHolder<>(BlueprintStructureProvider.create(world), anchorPos, world);
        result.getStructure().setStructurePath(Minecraft.getInstance().gameDir.toPath().resolve("structurize").resolve("tempschem.blueprint").toAbsolutePath());
        return result;
    }

    /**
     * Getter for position wrapper.
     *
     * @return position wrapper
     */
    public StructureBB getPosition()
    {
        return position;
    }

    /**
     * Getter for structure wrapper.
     *
     * @return structure wrapper
     */
    public T getStructure()
    {
        return structure;
    }
}