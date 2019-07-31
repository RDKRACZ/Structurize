package com.ldtteam.structurize.client.render;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import org.jetbrains.annotations.NotNull;
import javax.annotation.Nullable;

/**
 * Structure simulated chunk.
 */
public class StructureChunk extends Chunk
{
    /**
     * Structure world.
     */
    private final StructureWorld world;

    /**
     * Construct the element.
     *
     * @param worldIn the blockAccess
     * @param x       the chunk x
     * @param z       the chunk z
     */
    public StructureChunk(final World worldIn, final int x, final int z)
    {
        super(worldIn, new ChunkPos(x, z), new Biome[0]);
        this.world = (StructureWorld) worldIn;
    }

    @Nullable
    @Override
    public TileEntity getTileEntity(@NotNull final BlockPos pos, final CreateEntityType creationMode)
    {
        return world.getTileEntity(pos);
    }
}
