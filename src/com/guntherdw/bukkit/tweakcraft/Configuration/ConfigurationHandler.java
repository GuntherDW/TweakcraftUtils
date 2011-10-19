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
import com.guntherdw.bukkit.tweakcraft.Tools.PermissionsResolver;
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

    private Configuration globalconfig;
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
    public boolean cancelNickChat = true;
    public boolean GIRCenabled = true;
    public boolean AIRCenabled = false;
    public boolean enableDebug = false; /* Verbose messages */
    public boolean enableAutoTame = false;
    public boolean paySaddle = true;
    public boolean stopChunkUnloadBurningFurnace = false;
    public boolean enableCUI = false;
    public boolean enablemod_InfDura = false;
    public boolean enableExperienceOrbsHalt = false;

    public PermissionsResolver.PermissionResolvingMode permissoinsResolvingMode = null;

    public boolean enableRespawnHook = false;
    public boolean enableRespawnHeal = false;
    // public int     portalSearchRadius = 128;

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
    public boolean cancelNetherPortal = false;
    public boolean extraLogging = false;

    public ConfigurationHandler(TweakcraftUtils instance) {
        this.plugin = instance;
        lsbindmap = new HashMap<String, Map<Integer, Boolean>>();
        lockdowns = new HashMap<String, LockdownLocation>();
    }

    public void reloadConfig() {
        if (!plugin.getDataFolder().exists()) {
            plugin.getDataFolder().mkdirs();
        }

        if(this.globalconfig==null) globalconfig=plugin.getConfiguration();

        this.plugin.getLogger().info("[TweakcraftUtils] Parsing configuration file...");
        this.globalconfig.load();
        this.enableLocalChat = globalconfig.getBoolean("ChatMode.LocalChat.enabled", true);
        this.localchatdistance = globalconfig.getInt("ChatMode.LocalChat.range", 200);
        this.enableWorldChat = globalconfig.getBoolean("ChatMode.WorldChat.enabled", true);
        this.enableWorldGuard = globalconfig.getBoolean("ChatMode.RegionChat.enabled", false);
        this.enableZones = globalconfig.getBoolean("ChatMode.ZoneChat.enabled", false);
        this.enableIRC = globalconfig.getBoolean("CraftIRC.enabled", false);
        this.AIRCMessageFormat = globalconfig.getString("CraftIRC.admin.MessageFormat");
        this.AIRCenabled = globalconfig.getBoolean("CraftIRC.admin.enabled", false);
        this.AIRCtag = globalconfig.getString("CraftIRC.admin.tag", "mchatadmin");
        this.GIRCMessageFormat = globalconfig.getString("CraftIRC.regular.MessageFormat");
        this.GIRCtag = globalconfig.getString("CraftIRC.regular.tag", "mchatadmin");
        this.GIRCenabled = globalconfig.getBoolean("CraftIRC.regular.enabled", true);
        this.enableTPBack = globalconfig.getBoolean("enableTPBack", true);
        this.enableDebug = globalconfig.getBoolean("debug.enable", false);
        if(this.enableDebug) {
            plugin.getLogger().info("[TweakcraftUtils] Extra verbose messages enabled!");
        }

        this.enableRespawnHook = globalconfig.getBoolean("respawn.enableHook", false);
        this.enableRespawnHeal = globalconfig.getBoolean("respawn.healOnRespawn", false);


        this.extrahelpplugin = new ArrayList<String>();
        this.enableGroupChat = globalconfig.getBoolean("ChatMode.GroupChat", true);
        this.enablePersistence = globalconfig.getBoolean("Persistence.enabled", true);
        this.useTweakBotSeen = globalconfig.getBoolean("Persistence.useTweakBotSeen", false);
        plugin.getLogger().info("[TweakcraftUtils] Using TweakBot's seen table for /seen!");
        if(this.enablePersistence) {
            if(!plugin.databaseloaded)
                plugin.setupDatabase();
        }
        for(String plist : globalconfig.getStringList("extrahelp.plugins", null)) {
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
        this.cancelNetherPortal = globalconfig.getBoolean("worlds.cancelportal", false);
        /* this.enableBukkitPermissions = globalconfig.getBoolean("Permissions.BukkitPerms", false);
        if(this.enableBukkitPermissions)
            plugin.getLogger().warning("[TweakcraftUtils] Enabling Bukkit perms resolving, this is an experimental feature, expect bugs!"); */
        String presolver = globalconfig.getString("Permissions.resolver", null);
        if(presolver==null) presolver = "permissions";

        if(presolver.equals("permissions")) {
            permissoinsResolvingMode = PermissionsResolver.PermissionResolvingMode.NIJIPERMS;
        } else if(presolver.equals("permissionsex")) {
            permissoinsResolvingMode = PermissionsResolver.PermissionResolvingMode.PERMISSIONSEX;
        } else if(presolver.equals("bukkitperms")) {
            permissoinsResolvingMode = PermissionsResolver.PermissionResolvingMode.BUKKIT;
        }
        plugin.getPermissionsResolver().setMode(permissoinsResolvingMode);
        
        if(permissoinsResolvingMode!= PermissionsResolver.PermissionResolvingMode.NIJIPERMS) {
            plugin.getLogger().warning("[TweakcraftUtils] Other permissions resolver selected than Nijokun's Permissiosn plugin, this is experimental!");
        }


        this.extrahelphide = globalconfig.getStringList("extrahelp.hide", null);
        if (globalconfig.getBoolean("PlayerHistory.enabled", false)) {
            plugin.getLogger().info("[TweakcraftUtils] Keeping player history!");
            File seenFile = new File(plugin.getDataFolder(), "players.yml");
            this.seenconfig = new Configuration(seenFile);
            this.seenconfig.load();
            this.enableSeenConfig = true;
        }

        this.enabletamertool =  globalconfig.getBoolean("tamer.enabled", true);
        this.tamertoolid = globalconfig.getInt("tamer.toolid", Material.STICK.getId());
        this.enableAutoTame = globalconfig.getBoolean("mount.autotame", false);
        this.paySaddle = globalconfig.getBoolean("mount.paysaddle", true);
        this.stopChunkUnloadBurningFurnace = globalconfig.getBoolean("extra.stopChunkUnloadBurningFurnace", false);
        this.pigRecoverSaddle = globalconfig.getBoolean("extra.recoverPigSaddle", true);
        this.stopIgniteWorldGuard = globalconfig.getBoolean("extra.stopGodIgnite", false);
        this.enableCUI = globalconfig.getBoolean("extra.CUI", false);
        this.enablemod_InfDura = globalconfig.getBoolean("extra.mod_InfDura", false);
        this.enableExperienceOrbsHalt = globalconfig.getBoolean("extra.stopExperienceOrbs", false);
        this.cancelNickChat = globalconfig.getBoolean("extra.cancelNickChat", true);
        this.extraLogging = globalconfig.getBoolean("extra.extraLogging", false);

        this.enableSpamControl = globalconfig.getBoolean("spamcontrol.enable", false);
        if(this.enableSpamControl) {
            plugin.getLogger().info("[TweakcraftUtils] Enabling spam control!");
            plugin.getChathandler().enableAntiSpam();
        }
        this.spamCheckTime = globalconfig.getInt("spamcontrol.checkTime", 5)*100;
        this.spamMuteMinutes = globalconfig.getInt("spamcontrol.muteTime", 5);
        this.spamMaxMessages = globalconfig.getInt("spamcontrol.maxMessages", 5);
        this.spamMuteMessage = globalconfig.getString("spamcontrol.muteMessage", "{name} has been auto-muted for spamming!");

        if(this.enablePersistence) {
            plugin.getPlayerListener().reloadInfo();
        }
    }

    public Configuration getGlobalconfig() {
        return globalconfig;
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
