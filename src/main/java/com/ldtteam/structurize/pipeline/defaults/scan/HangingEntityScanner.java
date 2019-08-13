package com.ldtteam.structurize.pipeline.defaults.scan;

import com.ldtteam.structurize.pipeline.scan.ComponentScanner;
import net.minecraft.item.Items;

public class HangingEntityScanner
{
    public static void register()
    {
        ComponentScanner.EntityComponentScanner.newBuilder().setScanner(null).setRegistryName(Items.WHITE_BANNER.getRegistryName()).buildAndRegister();
    }
}