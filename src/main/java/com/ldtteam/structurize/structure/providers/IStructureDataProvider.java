package com.ldtteam.structurize.structure.providers;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;

/**
 * Interface for providing structure for {@link PlaceEventInfoHolder}
 */
public interface IStructureDataProvider
{
    /**
     * Rotates structure clockwise.
     */
    void rotateClockwise();

    /**
     * Rotates structure counterclockwise.
     */
    void rotateCounterClockwise();

    /**
     * Mirrors structure along X axis.
     */
    void mirrorX();

    /**
     * Mirrors structure along Z axis.
     */
    void mirrorZ();

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
    List<BlockState> getStructureBlockPalette();

    /**
     * Structure blocks 3d array, indexes: y|z|x.
     *
     * @return structure blocks array
     */
    short[][][] getStructureBlocks();

    /**
     * List of structure entities.
     *
     * @return structure entities list
     */
    List<CompoundNBT> getStructureEntities();

    /**
     * Structure tile entities 3d array, indexes: y|z|x.
     *
     * @return structure tile entities array
     */
    CompoundNBT[][][] getStructureTileEntities();

    /**
     * Converts actual structure pallete to list of required mod IDs.
     *
     * @return list of required mods IDs
     */
    default List<String> getRequiredModsFromStructurePalette()
    {
        final List<String> reqMods = new ArrayList<>();
        for (final BlockState bs : getStructureBlockPalette())
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
