package com.ldtteam.structurize.world.schematic;

import java.util.Random;
import com.ldtteam.structurize.util.constants.GeneralConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.CreateFlatWorldScreen;
import net.minecraft.client.gui.screen.CreateWorldScreen;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.WorldType;

/**
 * Schematic world worldtype
 */
public class SchematicWorldType extends WorldType
{
    /**
     * Registers our world type to global registry
     */
    public SchematicWorldType()
    {
        super(GeneralConstants.MOD_ID);
        setCustomOptions(true);
        enableInfoNotice();
    }

    @Override
    public float getCloudHeight()
    {
        return 0.0f;
    }

    @Override
    public double getHorizon(final World world)
    {
        return super.getHorizon(world);
    }

    @Override
    public boolean handleSlimeSpawnReduction(final Random random, final IWorld world)
    {
        return true;
    }

    @Override
    public void onCustomizeButton(final Minecraft mc, final CreateWorldScreen gui)
    {
        // open customize gui
        // flat mechanism layer builder without structures etc.
        mc.displayGuiScreen(new CreateFlatWorldScreen(gui, gui.chunkProviderSettingsJson));
    }

    @Override
    public void onGUICreateWorldPress()
    {
        // runnable before integrated server starts
    }

    /**
     * @see net.minecraft.world.dimension.Dimension#getVoidFogYFactor()
     */
    @Override
    public double voidFadeMagnitude()
    {
        return 2.0d;
    }
}
