package com.ldtteam.structurize;

import com.ldtteam.structurize.client.render.EventRenderer;
import com.ldtteam.structurize.config.Configuration;
import com.ldtteam.structurize.event.ClientEventSubscriber;
import com.ldtteam.structurize.event.EventSubscriber;
import com.ldtteam.structurize.event.LifecycleSubscriber;
import com.ldtteam.structurize.network.NetworkChannel;
import com.ldtteam.structurize.pipeline.ComponentRegistries;
import com.ldtteam.structurize.util.constant.DataVersion;
import com.ldtteam.structurize.util.constant.GeneralConstants;
import com.ldtteam.structurize.world.schematic.SchematicWorldType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;

/**
 * Mod main class.
 * The value in annotation should match an entry in the META-INF/mods.toml file.
 */
@Mod(GeneralConstants.MOD_ID)
public class Structurize
{
    private static Structurize instance;
    private final Logger logger;
    private final NetworkChannel networkChannel;
    private final Configuration configuration;
    private final EventRenderer eventRenderer;
    private final ComponentRegistries componentRegistries;
    private final SchematicWorldType schematicWorldType;

    /**
     * Mod init, registers events to their respective busses
     */
    public Structurize()
    {
        instance = this;
        logger = LogManager.getLogger(GeneralConstants.MOD_ID);
        logger.warn("Structurize");
        networkChannel = new NetworkChannel("net-channel");
        configuration = new Configuration(ModLoadingContext.get().getActiveContainer());
        eventRenderer = DistExecutor.callWhenOn(Dist.CLIENT, () -> () -> new EventRenderer());
        componentRegistries = new ComponentRegistries();
        schematicWorldType = new SchematicWorldType();
        Mod.EventBusSubscriber.Bus.MOD.bus().get().register(LifecycleSubscriber.class);
        Mod.EventBusSubscriber.Bus.FORGE.bus().get().register(EventSubscriber.class);
        DistExecutor.runWhenOn(Dist.CLIENT, () -> () -> Mod.EventBusSubscriber.Bus.FORGE.bus().get().register(ClientEventSubscriber.class));

        if (DataVersion.CURRENT == DataVersion.UPCOMING)
        {
            throw new RuntimeException("Missing newest data versions. Please update api/util/constant/DataVersion");
        }
    }

    /**
     * @return mod network channel
     */
    public static NetworkChannel getNetwork()
    {
        return instance.networkChannel;
    }

    /**
     * @return mod logger
     */
    public static Logger getLogger()
    {
        return instance.logger;
    }

    /**
     * @return mod configurations
     */
    public static Configuration getConfig()
    {
        return instance.configuration;
    }

    /**
     * @return renderer for previews
     */
    public static EventRenderer getEventRenderer()
    {
        return instance.eventRenderer;
    }

    /**
     * @return structure component scanner/placer registries
     */
    public static ComponentRegistries getComponentRegistries()
    {
        return instance.componentRegistries;
    }

    /**
     * @return registered world type instance
     */
    public static SchematicWorldType getSchematicWorldType()
    {
        return instance.schematicWorldType;
    }
}
