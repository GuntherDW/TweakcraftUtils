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
    public boolean enableWorldGuard = false;
    public boolean enableZones = false;
    public boolean enableIRC = false;
    public boolean enableTPBack = true;
    public boolean enableGroupChat = true;
    public boolean enableLocalChat = true;
    public boolean enableWorldChat = true;
    public int localchatdistance = 200;
    public Integer helpPerPage = 10;
    public List<String> extrahelpplugin = new ArrayList<String>();
    public List<String> extrahelphide = new ArrayList<String>();
    public boolean enabletamertool = true;
    public int     tamertoolid = Material.STICK.getId();
    public boolean enablePersistence = true;
    public boolean useTweakBotSeen = false;
    public String  AIRCtag = "mchatadmin";
    public String  GIRCtag = "mchat";
    public String  GIRCMessageFormat = "[A] <%name%> %message%";
    public String  AIRCMessageFormat = "<%name%> %message%";
    public boolean GIRCenabled = true;
    public boolean AIRCenabled = false;
    public boolean enableDebug = false; /* Verbose messages */
    public boolean enableAutoTame = false;
    public boolean paySaddle = true;
    public boolean stopChunkUnloadBurningFurnace = false;
    public boolean enableCUI = false;


    public boolean enableSpamControl = false;
    public long spamCheckTime = 500L;
    public int spamMuteMinutes = 5;
    public int spamMaxMessages = 5;
    public String spamMuteMessage = "{name} has been auto-muted for spamming!";


    /**
     * EXTRA/TEMPORARY STUFF
     */
    public boolean pigRecoverSaddle = true;
    public boolean stopIgniteWorldGuard = true;
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
        this.plugin.getLogger().info("[TweakcraftUtils] Parsing configuration file...");
        this.plugin.getConfiguration().load();
        this.enableLocalChat = plugin.getConfiguration().getBoolean("ChatMode.LocalChat.enabled", true);
        this.localchatdistance = plugin.getConfiguration().getInt("ChatMode.LocalChat.range", 200);
        this.enableWorldChat = plugin.getConfiguration().getBoolean("ChatMode.WorldChat.enabled", true);
        this.enableWorldGuard = plugin.getConfiguration().getBoolean("ChatMode.RegionChat.enabled", false);
        this.enableZones = plugin.getConfiguration().getBoolean("ChatMode.ZoneChat.enabled", false);
        this.enableIRC = plugin.getConfiguration().getBoolean("CraftIRC.enabled", false);
        this.AIRCMessageFormat = plugin.getConfiguration().getString("CraftIRC.admin.MessageFormat");
        this.AIRCenabled = plugin.getConfiguration().getBoolean("CraftIRC.admin.enabled", false);
        this.AIRCtag = plugin.getConfiguration().getString("CraftIRC.admin.tag", "mchatadmin");
        this.GIRCMessageFormat = plugin.getConfiguration().getString("CraftIRC.regular.MessageFormat");
        this.GIRCtag = plugin.getConfiguration().getString("CraftIRC.regular.tag", "mchatadmin");
        this.GIRCenabled = plugin.getConfiguration().getBoolean("CraftIRC.regular.enabled", true);
        this.enableTPBack = plugin.getConfiguration().getBoolean("enableTPBack", true);
        this.enableDebug = plugin.getConfiguration().getBoolean("debug.enable", false);
        if(this.enableDebug) {
            plugin.getLogger().info("[TweakcraftUtils] Extra verbose messages enabled!");
        }
        this.extrahelpplugin = new ArrayList<String>();
        this.enableGroupChat = plugin.getConfiguration().getBoolean("ChatMode.GroupChat", true);
        this.enablePersistence = plugin.getConfiguration().getBoolean("Persistence.enabled", true);
        this.useTweakBotSeen = plugin.getConfiguration().getBoolean("Persistence.useTweakBotSeen", false);
        plugin.getLogger().info("[TweakcraftUtils] Using TweakBot's seen table for /seen!");
        if(this.enablePersistence) {
            if(!plugin.databaseloaded)
                plugin.setupDatabase();
        }
        for(String plist : plugin.getConfiguration().getStringList("extrahelp.plugins", null)) {
            if(plugin.getServer().getPluginManager().getPlugin(plist) != null) {
                if(!extrahelpplugin.contains(plist)) {
                    if(this.enableDebug)
                        plugin.getLogger().info("[TweakcraftUtils] Adding "+plist+" to the /help addons.");
                    extrahelpplugin.add(plist);
                } else {
                    plugin.getLogger().info("[TweakcraftUtils] WARNING: "+plist+" is on the extrahelp list multiple times!");
                }
            } else {
                plugin.getLogger().info("[TweakcraftUtils] WARNING: Can't find plugin with name "+plist+"! Not adding to the help list.");
            }
        }
        this.extrahelphide = plugin.getConfiguration().getStringList("extrahelp.hide", null);
        if (plugin.getConfiguration().getBoolean("PlayerHistory.enabled", false)) {
            plugin.getLogger().info("[TweakcraftUtils] Keeping player history!");
            File seenFile = new File(plugin.getDataFolder(), "players.yml");
            this.seenconfig = new Configuration(seenFile);
            this.seenconfig.load();
            this.enableSeenConfig = true;
        }

        this.enabletamertool =  plugin.getConfiguration().getBoolean("tamer.enabled", true);
        this.tamertoolid = plugin.getConfiguration().getInt("tamer.toolid", Material.STICK.getId());
        this.enableAutoTame = plugin.getConfiguration().getBoolean("mount.autotame", false);
        this.paySaddle = plugin.getConfiguration().getBoolean("mount.paysaddle", true);
        this.stopChunkUnloadBurningFurnace = plugin.getConfiguration().getBoolean("extra.stopChunkUnloadBurningFurnace", false);
        this.pigRecoverSaddle = plugin.getConfiguration().getBoolean("extra.recoverPigSaddle", true);
        this.stopIgniteWorldGuard = plugin.getConfiguration().getBoolean("extra.StopGodIgnite", true);
        this.enableCUI = plugin.getConfiguration().getBoolean("extra.CUI", false);

        this.enableSpamControl = plugin.getConfiguration().getBoolean("spamcontrol.enable", false);
        if(this.enableSpamControl) {
            plugin.getLogger().info("[TweakcraftUtils] Enabling spam control!");
            plugin.getChathandler().enableAntiSpam();
        }
        this.spamCheckTime = plugin.getConfiguration().getInt("spamcontrol.checkTime", 5)*100;
        this.spamMuteMinutes = plugin.getConfiguration().getInt("spamcontrol.muteTime", 5);
        this.spamMaxMessages = plugin.getConfiguration().getInt("spamcontrol.maxMessages", 5);
        this.spamMuteMessage = plugin.getConfiguration().getString("spamcontrol.muteMessage", "{name} has been auto-muted for spamming!");

        if(this.enablePersistence) {
            plugin.getPlayerListener().reloadInfo();
        }
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
