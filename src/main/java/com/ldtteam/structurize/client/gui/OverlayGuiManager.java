package com.ldtteam.structurize.client.gui;

import java.util.HashMap;
import java.util.Map;
import com.ldtteam.structurize.client.render.util.RenderUtils;
import com.ldtteam.structurize.client.render.util.RenderUtils.BuiltBuffer;
import com.ldtteam.structurize.util.constant.Constants;
import com.mojang.blaze3d.systems.RenderSystem;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;
import net.minecraft.client.MainWindow;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.util.InputMappings;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.client.settings.KeyModifier;

public class OverlayGuiManager
{
    public static final KeyBinding KEY_BINDING = new KeyBinding("Interactive gui",
        KeyConflictContext.UNIVERSAL,
        KeyModifier.CONTROL,
        InputMappings.Type.KEYSYM,
        GLFW.GLFW_KEY_T,
        Constants.MOD_NAME);

    private static boolean isGuiRendered = false;
    private static Map<String, BuiltBuffer> objects = new HashMap<>();
    private static final PlacementPositionerRenderer placementPositionerRenderer = new PlacementPositionerRenderer();

    public static void setup()
    {
        try
        {
            objects.clear();
            final OBJLoader modelArrow = new OBJLoader(Constants.MOD_ID, "models/gui/arrows");
            modelArrow.loadModel();
            final OBJLoader modelLeg = new OBJLoader(Constants.MOD_ID, "models/gui/legs");
            modelLeg.loadModel();
            objects.putAll(modelArrow.loadIntoGL());
            objects.putAll(modelLeg.loadIntoGL());
        }
        catch (final Exception e)
        {
            e.printStackTrace();
        }
    }

    // call always from client main thread
    public static void tickKeyBinding()
    {
        if (KEY_BINDING.isPressed())
        {
            isGuiRendered = !isGuiRendered;
        }
    }

    public static void tickGui(final MainWindow renderInWindow)
    {
        if (true)
        {
            // legs Circle.000_Circle.004 kruhová_Torus arrows Circle.001_Circle.003 Torus.002_Torus.005 Torus.000_Torus.006
            final BuiltBuffer buffer = objects.get("Circle.001_Circle.003");
            if (buffer != null)
            {
                RenderSystem.pushMatrix();
                RenderSystem.translatef(renderInWindow.getScaledWidth() / 2.0f, renderInWindow.getScaledHeight() / 2.0f, 0);
                RenderSystem.rotatef(RenderUtils.getVanillaRenderInfo().getPitch(), -1.0F, 0.0F, 0.0F);
                RenderSystem.rotatef(RenderUtils.getVanillaRenderInfo().getYaw(), 0.0F, 1.0F, 0.0F);
                RenderSystem.scalef(-2.0F, -2.0F, -2.0F);
                RenderSystem.polygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_FILL);
                RenderUtils.drawBuiltBuffer(buffer);
                RenderSystem.popMatrix();
            }
        }
    }
}
