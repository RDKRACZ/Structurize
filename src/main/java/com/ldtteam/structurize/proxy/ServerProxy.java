package com.ldtteam.structurize.proxy;

import java.io.File;
import com.ldtteam.structurize.api.util.constant.Constants;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.fml.server.ServerLifecycleHooks;

/**
 * Proxy to the server.
 */
public class ServerProxy implements IProxy
{
    @Override
    public File getSchematicsFolder()
    {
        return new File(ServerLifecycleHooks.getCurrentServer().getDataDirectory() + "/" + Constants.MOD_ID);
    }

    @Override
    public void notifyClientOrServerOps(final ITextComponent message)
    {
        ServerLifecycleHooks.getCurrentServer().execute(() -> {
            ServerLifecycleHooks.getCurrentServer().getPlayerList().getOppedPlayers().getEntries().forEach((opNick) -> {
                ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayerByUUID(opNick.getValue().getId()).sendMessage(message, opNick.getValue().getId());
            });
        });
    }
}
