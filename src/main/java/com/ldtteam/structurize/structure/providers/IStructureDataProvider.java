package com.ldtteam.structurize.structure.providers;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;

/**
 * Interface for providing structure for {@link PlaceEventInfoHolder}
 */
public interface IStructureDataProvider
{
    /**
     * Getter for zero based anchor used when rotating/mirroring structure.
     * This method can be time expensive, result must be cached by implementation.
     *
     * @return anchor for rotating/mirroring structure
     */
    BlockPos getZeroBasedMirrorRotationAnchor();

    /**
     * Rotates structure clockwise.
     */
    void rotateClockwise();

    /**
     * Rotates structure counterclockwise.
     */
    void rotateCounterClockwise();

    /**
     * Mirrors structure through YZ plane according to current rotation.
     * Applied before rotation.
     */
    void mirrorX();

    /**
     * Mirrors structure through XY plane according to current rotation.
     * Applied before rotation.
     */
    void mirrorZ();

    /**
     * Takes given rotation and mirror and apply them on structure data.
     *
     * @param rotation rotation from render wrapper
     * @param mirror   is mirrored from render wrapper
     */
    void applyMirrorRotationOnStructure(Rotation rotation, Mirror mirror);

    /**
     * Getter for X block size.
     *
     * @return X block size
     */
    int getXsize();

    /**
     * Getter for Y block size.
     *
     * @return Y block size
     */
    int getYsize();

    /**
     * Getter for Z block size.
     *
     * @return Z block size
     */
    int getZsize();

    /**
     * List of mod IDs that are needed for structure to be built.
     *
     * @return list of mod IDs
     */
    List<String> getRequiredMods();

    /**
     * Structure block palette, indexes are used in blocks array as reference to specific block.
     *
     * @return structure block palette
     */
    List<BlockState> getBlockPalette();

    /**
     * Structure blocks 3d array, indexes: y|z|x.
     *
     * @return structure blocks array
     */
    short[][][] getBlocks();

    /**
     * List of structure entities.
     *
     * @return structure entities list
     */
    List<CompoundNBT> getEntities();

    /**
     * Structure tile entities 3d array, indexes: y|z|x.
     *
     * @return structure tile entities array
     */
    Map<BlockPos, CompoundNBT> getTileEntities();

    /**
     * Converts actual structure pallete to list of required mod IDs.
     *
     * @return list of required mods IDs
     */
    default List<String> getRequiredModsFromBlockPalette()
    {
        final List<String> reqMods = new ArrayList<>();
        for (final BlockState bs : getBlockPalette())
        {
            final String newMod = bs.getBlock().getRegistryName().getNamespace();
            if (!reqMods.contains(newMod))
            {
                reqMods.add(newMod);
            }
        }
        return reqMods;
    }
}
