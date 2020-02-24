package com.ldtteam.structurize;

import com.ldtteam.structurize.event.IEventManager;

/**
 * Main mod api
 */
public interface IStructurizeApi
{
    /**
     * Used to create build, scan or render events.
     *
     * @return mod instance of eventFactory
     * @see IEventManager
     */
    IEventManager getEventManager();
}
