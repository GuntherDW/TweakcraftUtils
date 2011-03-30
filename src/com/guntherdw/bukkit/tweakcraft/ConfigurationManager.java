package com.guntherdw.bukkit.tweakcraft;

import org.bukkit.util.config.Configuration;

import java.util.List;
import java.util.Map;

/**
 *
 * @author: GuntherDW
 */
public class ConfigurationManager {

    private Configuration globalconfig, userconfig;
    private TweakcraftUtils plugin;

    /**
     * Defaults
     */
    public boolean enableSeenConfig   = false;
    public int     localchatdistance  = 200;
    public Map<String, String>

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
