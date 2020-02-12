package com.ldtteam.structurize.util;

import com.ldtteam.structurize.Structurize;
import com.ldtteam.structurize.util.constant.DataVersion;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.util.datafix.DefaultTypeReferences;

/**
 * Utils for data fixer mechanism
 */
public class DataFixerUtils
{
    private static final String NEWER_VERSION = "Trying to read structure which was saved in newer version then version which is current Minecraft instance running. Structure {} > Minecraft {}";

    /**
     * Private constructor to hide implicit one.
     */
    private DataFixerUtils()
    {
        // Intentionally left empty.
    }

    /**
     * Updates chunk to data version which is current Minecraft instance running.
     *
     * @param chunkData        chunk in nbt format
     * @param chunkDataVersion chunk data version
     * @return updated chunk data if update was necessary, otherwise null
     */
    public CompoundNBT updateChunkDataToCurrentVersion(final CompoundNBT chunkData, final DataVersion chunkDataVersion)
    {
        return updateChunkDataToCurrentVersion(chunkData, chunkDataVersion.getDataVersion());
    }

    /**
     * Updates chunk to data version which is current Minecraft instance running.
     *
     * @param chunkData        chunk in nbt format
     * @param chunkDataVersion chunk data version
     * @return updated chunk data if update was necessary, otherwise null
     */
    public CompoundNBT updateChunkDataToCurrentVersion(final CompoundNBT chunkData, final int chunkDataVersion)
    {
        if (chunkDataVersion >= DataVersion.CURRENT.getDataVersion())
        {
            if (chunkDataVersion > DataVersion.CURRENT.getDataVersion())
            {
                Structurize.getLogger().warn(NEWER_VERSION, chunkDataVersion, DataVersion.CURRENT.getDataVersion());
                // TODO: create GUI notification, kinda serious problem so player should be notified
            }
            return null;
        }
        return NBTUtil.update(Minecraft.getInstance().getDataFixer(), DefaultTypeReferences.CHUNK, chunkData, chunkDataVersion);
    }
}
