package com.ldtteam.structurize.item;

import com.ldtteam.structurize.Structurize;
import com.ldtteam.structurize.client.gui.WindowBuildTool;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.item.Rarity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

/**
 * BuildTool item class
 */
public class BuildTool extends Item
{
    /**
     * Creates default build tool item.
     *
     * @param itemGroup creative tab
     */
    public BuildTool(final ItemGroup itemGroup)
    {
        this(new Item.Properties().maxDamage(0).setNoRepair().rarity(Rarity.UNCOMMON).group(itemGroup));
    }

    /**
     * MC constructor.
     *
     * @param properties properties
     */
    public BuildTool(final Properties properties)
    {
        super(properties);
        setRegistryName("buildtool");
    }

    /**
     * <p>
     * Structurize: build tool air right click, opens {@link WindowBuildTool}.
     * <p/>
     * {@inheritDoc}
     */
    @Override
    public ActionResult<ItemStack> onItemRightClick(final World worldIn, final PlayerEntity playerIn, final Hand handIn)
    {
        final ItemStack itemstack = playerIn.getHeldItem(handIn);
        if (worldIn.isRemote())
        {
            // WindowBuildTool.open(playerIn.getPosition(), playerIn);
        }
        return new ActionResult<>(ActionResultType.SUCCESS, itemstack);
    }

    /**
     * <p>
     * Structurize: build tool block right click, opens {@link WindowBuildTool}.
     * <p/>
     * {@inheritDoc}
     */
    @Override
    public ActionResultType onItemUse(final ItemUseContext context)
    {
        if (!context.getWorld().isRemote())
        {
            // WindowBuildTool.open(context.getPos(), context.getPlayer());
        }
        return ActionResultType.SUCCESS;
    }

    /**
     * <p>
     * Structurize: return build tool when used for crafting.
     * <p/>
     * {@inheritDoc}
     */
    @Override
    public ItemStack getContainerItem(final ItemStack itemStack)
    {
        if (!hasContainerItem(itemStack))
        {
            return ItemStack.EMPTY;
        }
        return itemStack.copy();
    }

    /**
     * <p>
     * Structurize: check if build tool should be returned when used for crafting.
     * <p/>
     * {@inheritDoc}
     */
    @Override
    public boolean hasContainerItem(final ItemStack itemStack)
    {
        return !itemStack.isEmpty() && Structurize.getConfig().getServer().buildToolSurvivesCrafting.get();
    }
}
