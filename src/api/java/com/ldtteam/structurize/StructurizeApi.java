package com.ldtteam.structurize;

import com.ldtteam.structurize.event.IEventManager;

/**
 * Main mod api holder
 */
public final class StructurizeApi implements IStructurizeApi
{
    private static final StructurizeApi INSTANCE = new StructurizeApi();
    private IStructurizeApi api;

    public static StructurizeApi getInstance()
    {
        return INSTANCE;
    }

    public void setApi(final IStructurizeApi apiIn)
    {
        if (api != null)
        {
            throw new IllegalStateException("Can't replace api instance!");
        }
        api = apiIn;
    }

    public IEventManager getEventManager()
    {
        return api.getEventManager();
    }
}
