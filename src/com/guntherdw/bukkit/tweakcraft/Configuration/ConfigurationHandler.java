/*
 * Copyright (c) 2011 GuntherDW
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */

package com.guntherdw.bukkit.tweakcraft.Configuration;

import com.guntherdw.bukkit.tweakcraft.TweakcraftUtils;
import org.bukkit.util.config.Configuration;

import java.io.File;

/**
 * @author GuntherDW
 */
public class ConfigurationHandler {

    private Configuration globalconfig, userconfig;
    private TweakcraftUtils plugin;
    private Configuration seenconfig;

    /**
     * Defaults
     */
    public boolean enableSeenConfig = false;
    public int localchatdistance = 200;
    public boolean enableWorldGuard = false;
    public boolean enableZones = false;
    public boolean enableIRC = false;
    // public Map<String, String>

    public ConfigurationHandler(TweakcraftUtils instance) {
        this.plugin = instance;
    }

    public void reloadConfig() {
        if (!plugin.getDataFolder().exists()) {
            plugin.getDataFolder().mkdirs();
        }
        plugin.getLogger().info("[TweakcraftUtils] Parsing configuration file...");
        plugin.getConfiguration().load();
        enableWorldGuard = plugin.getConfiguration().getBoolean("enableWorldGuard", false);
        enableZones = plugin.getConfiguration().getBoolean("enableZones", false);
        enableIRC = plugin.getConfiguration().getBoolean("enableIRC", false);
        if (plugin.getConfiguration().getBoolean("keepplayerhistory", false)) {
            plugin.getLogger().info("[TweakcraftUtils] Keeping player history!");
            File seenFile = new File(plugin.getDataFolder(), "players.yml");
            this.seenconfig = new Configuration(seenFile);
            this.seenconfig.load();
            this.enableSeenConfig = true;
        }
        this.localchatdistance = plugin.getConfiguration().getInt("maxrange", 200);
    }

    public Configuration getGlobalconfig() {
        return globalconfig;
    }

    public Configuration getUserconfig() {
        return userconfig;
    }

    public boolean isEnableZones() {
        return enableZones;
    }

    public boolean isEnableWorldGuard() {
        return enableWorldGuard;
    }

    public int getLocalchatdistance() {
        return localchatdistance;
    }

    public boolean isEnableSeenConfig() {
        return enableSeenConfig;
    }

    public Configuration getSeenconfig() {
        return seenconfig;
    }
}