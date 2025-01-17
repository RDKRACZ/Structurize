package com.ldtteam.structurize.network.messages;

import com.ldtteam.structurize.items.ItemTagTool;
import com.ldtteam.structurize.items.ModItems;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.network.NetworkEvent;
import org.jetbrains.annotations.Nullable;

/**
 * Messages for adding or removing a tag
 */
public class SetTagInTool implements IMessage
{
    /**
     * The tag to use
     */
    private String tag = "";

    /**
     * The tags blockpos
     */
    private final int slot;

    /**
     * Empty constructor used when registering the
     */
    public SetTagInTool(final PacketBuffer buf)
    {
        this.tag = buf.readUtf(32767);
        this.slot = buf.readInt();
    }

    public SetTagInTool(final String tag, final int slot)
    {
        this.slot = slot;
        this.tag = tag;
    }

    @Override
    public void toBytes(final PacketBuffer buf)
    {
        buf.writeUtf(tag);
        buf.writeInt(slot);
    }

    @Nullable
    @Override
    public LogicalSide getExecutionSide()
    {
        return LogicalSide.SERVER;
    }

    @Override
    public void onExecute(final NetworkEvent.Context ctxIn, final boolean isLogicalServer)
    {
        if (ctxIn.getSender() == null)
        {
            return;
        }

        final ItemStack stack = ctxIn.getSender().inventory.getItem(slot);
        if (stack.getItem() == ModItems.tagTool.get())
        {
            stack.getOrCreateTag().putString(ItemTagTool.TAG_CURRENT_TAG, tag);
        }
    }
}
