package com.guntherdw.bukkit.tweakcraft;

import com.nijikokun.bukkit.Permissions.Permissions;
import org.bukkit.event.server.PluginEvent;
import org.bukkit.event.server.ServerListener;

/**
 * @author GuntherDW
 */
public class TweakcraftListener extends ServerListener {

    public TweakcraftListener() {
    }

    /* @Override
    public void onPluginEnabled(PluginEvent event) {
        if (event.getPlugin().getDescription().getName().equals("Permissions")) {
            TweakcraftUtils.Permissions = ((Permissions) event.getPlugin()).Security;
            TweakcraftUtils.log.info("[TweakcraftUtils] Attached plugin to Permissions. Enjoy~");
        }
    } */
}
