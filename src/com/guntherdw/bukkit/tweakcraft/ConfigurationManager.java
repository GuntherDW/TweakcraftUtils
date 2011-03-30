package com.guntherdw.bukkit.tweakcraft;

import com.guntherdw.bukkit.tweakcraft.TweakcraftUtils;
import org.bukkit.util.config.Configuration;

/**
 *
 * @author: GuntherDW
 */
public class ConfigurationManager {

    private Configuration globalconfig, userconfig;
    private TweakcraftUtils plugin;

    public ConfigurationManager(TweakcraftUtils instance) {
        this.plugin = instance;
        if(!plugin.getDataFolder().exists())
        {
            plugin.getDataFolder().mkdirs();
        }
    }

    public Configuration getGlobalconfig() {
        return globalconfig;
    }

    public Configuration getUserconfig() {
        return userconfig;
    }
}
