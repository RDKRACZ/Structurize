package com.ldtteam.structurize;

import com.ldtteam.structurize.apiimpl.ApiImpl;
import com.ldtteam.structurize.client.gui.GuiKeybindManager;
import com.ldtteam.structurize.config.Configuration;
import com.ldtteam.structurize.event.forge.ClientEventSubscriber;
import com.ldtteam.structurize.event.forge.EventSubscriber;
import com.ldtteam.structurize.event.forge.LifecycleSubscriber;
import com.ldtteam.structurize.network.NetworkChannel;
import com.ldtteam.structurize.pipeline.ComponentRegistries;
import com.ldtteam.structurize.util.constant.DataVersion;
import com.ldtteam.structurize.util.constant.Constants;
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
@Mod(Constants.MOD_ID)
public class Structurize
{
    private static Structurize instance;
    private final Logger logger;
    private final NetworkChannel networkChannel;
    private final Configuration configuration;
    private final ComponentRegistries componentRegistries;
    private final SchematicWorldType schematicWorldType;
    private final IStructurizeApi api;

    /**
     * Mod init. Registers events to their respective buses. Initialize essential mod structures.
     */
    public Structurize()
    {
        instance = this;
        logger = LogManager.getLogger(Constants.MOD_ID);
        logger.warn("Structurize");
        networkChannel = new NetworkChannel("net-channel");
        configuration = new Configuration(ModLoadingContext.get().getActiveContainer());
        componentRegistries = new ComponentRegistries();
        schematicWorldType = new SchematicWorldType();
        Mod.EventBusSubscriber.Bus.MOD.bus().get().register(LifecycleSubscriber.class);
        Mod.EventBusSubscriber.Bus.FORGE.bus().get().register(EventSubscriber.class);
        DistExecutor.runWhenOn(Dist.CLIENT, () -> () -> Mod.EventBusSubscriber.Bus.FORGE.bus().get().register(ClientEventSubscriber.class));
        api = new ApiImpl();
        GuiKeybindManager.KEY_BINDING.setToDefault();

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

    /**
     * @return main mod api
     */
    public static IStructurizeApi getApi()
    {
        return instance.api;
    }
}
