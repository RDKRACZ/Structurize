package com.ldtteam.structurize.proxy;

import java.io.File;
import org.jetbrains.annotations.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;

/**
 * Basic proxy interface.
 */
public interface IProxy
{
    /**
     * Opens a build tool window.
     *
     * @param pos coordinates.
     */
    default void openBuildToolWindow(final BlockPos pos)
    {
    }

    /**
     * Opens a shape tool window.
     *
     * @param pos coordinates.
     */
    default void openShapeToolWindow(final BlockPos pos)
    {
    }

    /**
     * Get the file representation of the additional schematics' folder.
     *
     * @return the folder for the schematic
     */
    @Nullable
    default File getSchematicsFolder()
    {
        return null;
    }

    @Nullable
    default BlockState getBlockStateFromWorld(final BlockPos pos)
    {
        return null;
    }

    /**
     * Sends given message to client chat (if client sided) or to all server OPs (if server sided).
     * Is ensured to run on main thread.
     *
     * @param message to send
     */
    default void notifyClientOrServerOps(ITextComponent message)
    {
    }
}
