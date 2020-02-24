package com.ldtteam.structurize.client.gui;

import com.ldtteam.blockout.element.simple.Button.ButtonConstructionDataBuilder;
import com.ldtteam.blockout.gui.IBlockOutGui;
import com.ldtteam.blockout.proxy.ProxyHolder;
import com.ldtteam.structurize.Structurize;
import com.ldtteam.structurize.util.Utils;
import com.ldtteam.structurize.util.constant.Constants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.util.InputMappings;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.client.settings.KeyModifier;

public class GuiKeybindManager
{
    public static final KeyBinding KEY_BINDING = new KeyBinding("Interactive gui",
        KeyConflictContext.UNIVERSAL,
        KeyModifier.CONTROL,
        InputMappings.Type.KEYSYM,
        69,
        Constants.MOD_NAME);

    // call always from client main thread
    public static void tick()
    {
        if (KEY_BINDING.isKeyDown() && !(Minecraft.getInstance().currentScreen instanceof IBlockOutGui))
        {
            ProxyHolder.getInstance()
                .getClientSideOnlyGuiController()
                .openUI(Minecraft.getInstance().player,
                    iGuiKeyBuilder -> iGuiKeyBuilder.ofFile(Utils.createLocationFor("gui/test_event_positioner.json"))
                        .usingData(iBlockOutGuiConstructionDataBuilder -> iBlockOutGuiConstructionDataBuilder
                            .withControl("test_click", ButtonConstructionDataBuilder.class, buttonConstructionDataBuilder -> {
                                buttonConstructionDataBuilder.withClickedEventHandler((button, eventArgs) -> Structurize.getLogger()
                                    .error("Button pressed! State: " + eventArgs.getButton() + " Initial Press: " + eventArgs.isStart()));
                            }))
                        .withDefaultItemHandlerManager()
                        .forClientSideOnly());
        }
        else if (!KEY_BINDING.isKeyDown() && Minecraft.getInstance().currentScreen instanceof IBlockOutGui)
        {
        }
    }
}
