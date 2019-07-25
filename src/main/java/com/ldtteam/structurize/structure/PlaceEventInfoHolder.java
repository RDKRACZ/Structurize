package com.ldtteam.structurize.structure;

import java.util.UUID;
import com.ldtteam.structurize.structure.providers.BlueprintStructureProvider;
import com.ldtteam.structurize.structure.providers.IStructureDataProvider;
import net.minecraft.client.Minecraft;
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
    private final UUID eventId = UUID.randomUUID();
    private StructureBB position;
    private T structure;
    private World world;
    private boolean isCanceled = false;

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
     * Sends event to server where is accepted by selected buildProvider.
     * When accepted on server every player is notified to cancel this event clientside.
     *
     * @param playerUUID player id to match event originator later
     */
    public void passToBuildProvider(final UUID playerUUID)
    {

    }

    /**
     * Sets cancel state. Event will be removed from every active list asap.
     */
    public void cancel()
    {
        isCanceled = true;
    }

    /**
     * Getter for isCanceled state.
     *
     * @return isCanceled state
     */
    public boolean isCanceled()
    {
        return isCanceled;
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

    /**
     * Getter for world reference.
     *
     * @return world reference
     */
    public World getWorld()
    {
        return world;
    }

    /**
     * Getter for unique event id.
     *
     * @return event id
     */
    public UUID getEventId()
    {
        return eventId;
    }
}
