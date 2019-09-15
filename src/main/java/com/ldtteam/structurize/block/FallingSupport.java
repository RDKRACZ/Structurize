package com.ldtteam.structurize.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.block.material.PushReaction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;

/**
 * Anyblock substitution block class
 */
public class FallingSupport extends Block
{
    /**
     * Creates default anyblock substitution block.
     */
    public FallingSupport()
    {
        this(
            Properties.create(new Material(MaterialColor.WOOD, false, true, true, true, true, false, false, PushReaction.BLOCK))
                .doesNotBlockMovement()
                .noDrops()
                .hardnessAndResistance(Float.MAX_VALUE));
    }

    /**
     * MC constructor.
     *
     * @param properties properties
     */
    public FallingSupport(final Properties properties)
    {
        super(properties);
        setRegistryName("fallingsupport");
    }

    @Override
    public boolean propagatesSkylightDown(final BlockState state, final IBlockReader reader, final BlockPos pos)
    {
        return true;
    }

    @Override
    public BlockRenderType getRenderType(final BlockState state)
    {
        return BlockRenderType.INVISIBLE;
    }
}
