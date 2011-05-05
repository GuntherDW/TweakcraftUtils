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

import com.guntherdw.bukkit.tweakcraft.Packages.LockdownLocation;
import com.guntherdw.bukkit.tweakcraft.TweakcraftUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.util.config.Configuration;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author GuntherDW
 */
public class ConfigurationHandler {

    private Configuration globalconfig, userconfig;
    private TweakcraftUtils plugin;
    private Configuration seenconfig;
    private Map<String, Map<Integer, Boolean>> lsbindmap;
    private Map<String, LockdownLocation> lockdowns;
    private Map<String, List<Location>> tpfromlocations;

    /**
     * Defaults
     */
    public boolean enableSeenConfig = false;
    public int localchatdistance = 200;
    public boolean enableWorldGuard = false;
    public boolean enableZones = false;
    public boolean enableIRC = false;
    public boolean enableTPBack = true;
    public Integer helpPerPage = 10;
    public List<String> extrahelpplugin = new ArrayList<String>();
    public List<String> extrahelphide = new ArrayList<String>();
    public boolean enabletamertool = true;
    public int     tamertoolid = Material.STICK.getId();
    // public Map<String, String>

    public ConfigurationHandler(TweakcraftUtils instance) {
        this.plugin = instance;
        lsbindmap = new HashMap<String, Map<Integer, Boolean>>();
        lockdowns = new HashMap<String, LockdownLocation>();
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
        enableTPBack = plugin.getConfiguration().getBoolean("enableTPBack", true);
        extrahelpplugin = new ArrayList<String>();
        for(String plist : plugin.getConfiguration().getStringList("extrahelp.plugins", null)) {
            if(plugin.getServer().getPluginManager().getPlugin(plist) != null) {
                if(!extrahelpplugin.contains(plist)) {
                    plugin.getLogger().info("[TweakcraftUtils] Adding "+plist+" to the /help addons.");
                    extrahelpplugin.add(plist);
                } else {
                    plugin.getLogger().info("[TweakcraftUtils] WARNING: "+plist+" is on the extrahelp list multiple times!");
                }
            } else {
                plugin.getLogger().info("[TweakcraftUtils] WARNING: Can't find plugin with name "+plist+"! Not adding to the help list.");
            }
        }
        extrahelphide = plugin.getConfiguration().getStringList("extrahelp.hide", null);
        if (plugin.getConfiguration().getBoolean("keepplayerhistory", false)) {
            plugin.getLogger().info("[TweakcraftUtils] Keeping player history!");
            File seenFile = new File(plugin.getDataFolder(), "players.yml");
            this.seenconfig = new Configuration(seenFile);
            this.seenconfig.load();
            this.enableSeenConfig = true;
        }
        this.localchatdistance = plugin.getConfiguration().getInt("maxrange", 200);
        this.enabletamertool =  plugin.getConfiguration().getBoolean("tamer.enabled", true);
        this.tamertoolid = plugin.getConfiguration().getInt("tamer.toolid", Material.STICK.getId());
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
    
    public Map<String, Map<Integer, Boolean>> getLsbindmap() {
        return lsbindmap;
    }

    public Map<String, LockdownLocation> getLockdowns() {
        return lockdowns;
    }
}