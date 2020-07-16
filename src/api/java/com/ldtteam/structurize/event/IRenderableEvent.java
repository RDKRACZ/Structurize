package com.ldtteam.structurize.event;

import net.minecraftforge.client.event.RenderWorldLastEvent;

public interface IRenderableEvent
{
    void render(RenderWorldLastEvent context);
}
