package com.ldtteam.structurize.client.render;

import net.minecraftforge.client.event.RenderWorldLastEvent;

public interface IWorldRenderer
{
    void render(RenderWorldLastEvent contextEvent);

    void rebuild();
}
