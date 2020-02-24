package com.ldtteam.structurize.apiimpl;

import com.ldtteam.structurize.IStructurizeApi;
import com.ldtteam.structurize.StructurizeApi;
import com.ldtteam.structurize.event.ClientEventManager;
import com.ldtteam.structurize.event.IEventManager;
import com.ldtteam.structurize.event.ServerEventManager;
import net.minecraftforge.fml.DistExecutor;

/**
 * Main mod api implementation
 */
public class ApiImpl implements IStructurizeApi
{
    private IEventManager eventManager;

    public ApiImpl()
    {
        eventManager = DistExecutor.runForDist(() -> () -> new ClientEventManager(), () -> () -> new ServerEventManager());

        StructurizeApi.getInstance().setApi(this);
    }

    @Override
    public IEventManager getEventManager()
    {
        return eventManager;
    }
}
