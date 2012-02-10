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

package com.guntherdw.bukkit.tweakcraft;

// import com.bergerkiller.bukkit.nolagg.NoLagg;

import com.ensifera.animosity.craftirc.CraftIRC;
import com.ensifera.animosity.craftirc.EndPoint;
import com.guntherdw.bukkit.tcutilsclientbridge.TCUtilsClientBridgePlugin;
import com.guntherdw.bukkit.tweakcraft.Chat.ChatHandler;
import com.guntherdw.bukkit.tweakcraft.Chat.ChatMode;
import com.guntherdw.bukkit.tweakcraft.Commands.CommandHandler;
import com.guntherdw.bukkit.tweakcraft.Configuration.ConfigurationHandler;
import com.guntherdw.bukkit.tweakcraft.DataSources.Ban.BanHandler;
import com.guntherdw.bukkit.tweakcraft.DataSources.PersistenceClass.PlayerHistoryInfo;
import com.guntherdw.bukkit.tweakcraft.DataSources.PersistenceClass.PlayerInfo;
import com.guntherdw.bukkit.tweakcraft.DataSources.PersistenceClass.PlayerOptions;
import com.guntherdw.bukkit.tweakcraft.Exceptions.CommandException;
import com.guntherdw.bukkit.tweakcraft.Exceptions.CommandSenderException;
import com.guntherdw.bukkit.tweakcraft.Exceptions.CommandUsageException;
import com.guntherdw.bukkit.tweakcraft.Exceptions.PermissionsException;
import com.guntherdw.bukkit.tweakcraft.Listeners.*;
import com.guntherdw.bukkit.tweakcraft.Packages.CraftIRCAdminEndPoint;
import com.guntherdw.bukkit.tweakcraft.Packages.CraftIRCEndPoint;
import com.guntherdw.bukkit.tweakcraft.Packages.ItemDB;
import com.guntherdw.bukkit.tweakcraft.Packages.LocalPlayer;
import com.guntherdw.bukkit.tweakcraft.Tools.PermissionsResolver;
import com.guntherdw.bukkit.tweakcraft.Tools.TamerTool;
import com.guntherdw.bukkit.tweakcraft.Util.TeleportHistory;
import com.guntherdw.bukkit.tweakcraft.Worlds.WorldManager;
import com.nijikokun.bukkit.Permissions.Permissions;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.zones.Zones;
import de.diddiz.LogBlock.LogBlock;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import javax.persistence.PersistenceException;
import java.io.*;
import java.util.*;
import java.util.logging.Logger;


/**
 * @author GuntherDW
 */

public class TweakcraftUtils extends JavaPlugin {

    protected Permissions perm = null;
    protected WorldGuardPlugin wg = null;
    protected CraftIRC circ = null;
    protected Zones zones = null;
    protected LogBlock lb = null;
    protected WorldEditPlugin we = null;
    protected TCUtilsClientBridgePlugin tcUtilsClientBridgePlugin = null;
    // protected NoLagg nolagg = null;
    // protected PermissionHandler ph = null;

    private final TweakcraftPlayerListener playerListener = new TweakcraftPlayerListener(this);
    private final TweakcraftEntityListener entityListener = new TweakcraftEntityListener(this);
    private final TweakcraftBlockListener blockListener = new TweakcraftBlockListener(this);
    private final TweakcraftWorldListener worldListener = new TweakcraftWorldListener(this);
    private final CommandHandler commandHandler = new CommandHandler(this);
    private final BanHandler banhandler = new BanHandler(this);
    private final ItemDB itemDB = new ItemDB(this);
    private final WorldManager worldmanager = new WorldManager(this);
    private final ConfigurationHandler configHandler = new ConfigurationHandler(this);
    private final TeleportHistory telehistory = new TeleportHistory(this);
    private final TamerTool tamertool = new TamerTool(this);
    private final ChatHandler chathandler = new ChatHandler(this);
    private final PermissionsResolver permsResolver = new PermissionsResolver(this);

    private HashMap<String, LocalPlayer> localPlayers = new HashMap<String, LocalPlayer>();

    private Object circendpoint = null;
    private Object cirdlogendpoint = null;
    private Object circadminendpoint = null;

    private Set<String> donottplist;
    private List<String> MOTDLines;

    protected final Logger log = Logger.getLogger("Minecraft");
    protected PluginDescriptionFile pdfFile = null;

    private Set<Player> cuiPlayers;
    public String CUIPattern = "§7§3§3§4";

    private Set<Player> mod_InfDuraplayers;
    public String ToolDuraPattern = "§1§1§1§1";

    public static TweakcraftUtils instance = null;

    public Map<String, String> playerReplyDB;
    public boolean databaseloaded = false;


    public Set<Player> getMod_InfDuraplayers() {
        return mod_InfDuraplayers;
    }

    public PermissionsResolver getPermissions() {
        return this.permsResolver;
    }

    public Set<Player> getCUIPlayers() {
        return cuiPlayers;
    }

    public static TweakcraftUtils getInstance() {
        /* Do not create a new instance, could be dangerous!
         * It'll get set at the onEnable() method!
         */
        /* if(instance==null)
            instance = new TweakcraftUtils(); */

        return instance;
    }

    public TCUtilsClientBridgePlugin getClientBridge() {
        return tcUtilsClientBridgePlugin;
    }

    public static void registerClientBridge(TCUtilsClientBridgePlugin clientBridgePlugin) {
        if(instance==null) return;
        instance.tcUtilsClientBridgePlugin = clientBridgePlugin;
    }

    public void sendCUIChatMode(Player player) {
        if (this.cuiPlayers != null && this.cuiPlayers.contains(player)) {
            ChatMode cm = getChathandler().getPlayerChatMode(player);

            if (cm == null)
                player.sendRawMessage(CUIPattern + "null");
            else
                player.sendRawMessage(CUIPattern + "[" + cm.getPrefix() + "]");
        }
        if(tcUtilsClientBridgePlugin != null)
            tcUtilsClientBridgePlugin.sendChatMode(player);
    }

    public LocalPlayer wrapPlayer(Player player) {
        LocalPlayer p = this.wrapPlayer(player.getName());
        if (p.getBukkitPlayer() == null)
            p.setBukkitPlayer(player);

        return p;
    }

    public LocalPlayer wrapPlayer(String player) {
        if (!this.localPlayers.containsKey(player.toLowerCase()))
            this.localPlayers.put(player.toLowerCase(), new LocalPlayer(player));

        return this.localPlayers.get(player.toLowerCase());
    }

    public void sendCUIHandShake(Player player) {
        if (getConfigHandler().enableCUI) {
            player.sendRawMessage(CUIPattern); // HANDSHAKE
        }
    }

    /**
     * Send the player a mod_InfDura string, setting the new ToolDurabilty mode
     *
     * @param player The player to send the mode to
     */
    public void sendToolDuraMode(Player player) {
        this.sendToolDuraMode(player, player.getWorld());
    }

    /**
     * Send the player a mod_InfDura string, setting the new ToolDurabilty mode
     *
     * @param player The player to send the mode to
     * @param world  The world to check the ToolDurability mode
     */
    public void sendToolDuraMode(Player player, World world) {
        if (getConfigHandler().enablemod_InfDura && mod_InfDuraplayers.contains(player)) {
            player.sendRawMessage(ToolDuraPattern + world.getToolDurability());
        }
        if(tcUtilsClientBridgePlugin != null)
            tcUtilsClientBridgePlugin.getPlayerListener().sendToolDuraMode(player, world);
    }

    /**
     * Send the specified player the mod_InfDura handshake
     *
     * @param player The player that will receive the initiation
     */
    public void sendmod_InfDuraHandshake(Player player) {
        if (getConfigHandler().enablemod_InfDura) {
            player.sendRawMessage(ToolDuraPattern);
        }
    }

    public String findinlist(String find, Set<String> list) {
        for (String name : list) {
            if (name.toLowerCase().contains(find.toLowerCase())) {
                return name;
            }
        }
        return null;
    }

    /**
     * Get the Teleport History tool
     *
     * @return TeleportHistory instance
     */
    public TeleportHistory getTelehistory() {
        return telehistory;
    }

    /**
     * Get the PlayerListener instance
     *
     * @return TweakcraftUtils's PlayerListener instance
     */
    public TweakcraftPlayerListener getPlayerListener() {
        return playerListener;
    }

    /**
     * Get the ItemDB instance
     *
     * @return ItemDB instance
     */
    public ItemDB getItemDB() {
        return itemDB;
    }

    /**
     * Get the WorldManager instance
     *
     * @return WorldManager instance
     */
    public WorldManager getworldManager() {
        return worldmanager;
    }

    /**
     * Get the TamerTool tool instance
     *
     * @return TamerTool instance
     */
    public TamerTool getTamerTool() {
        return tamertool;
    }

    public String getCompassDirection(Float rotation) {
        String dir = null;
        Float rot = (rotation - 90) % 360;
        if (rot < 0) {
            rot += 360;
        }
        Integer r = rot.intValue();

        if (r < 23)
            dir = "N";
        else if (r < 68)
            dir = "NE";
        else if (r < 113)
            dir = "E";
        else if (r < 158)
            dir = "SE";
        else if (r < 203)
            dir = "S";
        else if (r < 248)
            dir = "SW";
        else if (r < 293)
            dir = "W";
        else if (r < 338)
            dir = "NW";
        else
            dir = "N";

        return dir;
    }

    @Override
    public List<Class<?>> getDatabaseClasses() {
        List<Class<?>> list = new ArrayList<Class<?>>();
        list.add(PlayerInfo.class);
        list.add(PlayerHistoryInfo.class);
        list.add(PlayerOptions.class);
        return list;
    }

    public void setupDatabase() {
        try {
            getDatabase().find(PlayerInfo.class).findRowCount();
            getDatabase().find(PlayerOptions.class).findRowCount();
            if (configHandler.useTweakBotSeen)
                getDatabase().find(PlayerHistoryInfo.class).findRowCount();
        } catch (PersistenceException ex) {
            log.info("[TweakcraftUtils] Installing database for " + getDescription().getName() + " due to first time usage");
            if (configHandler.useTweakBotSeen)
                log.info("[TweakcraftUtils] Also creating the TweakBot !seen helpen table");
            installDDL();
        }
        databaseloaded = true;
    }

    public String listToString(List<String> lijst) {
        StringBuilder builder = new StringBuilder();
        int c = 0;
        if (lijst.size() != 0) {
            while (c < lijst.size()) {
                builder.append(lijst.get(c));
                if (c != lijst.size() - 1) builder.append(",");
                c++;
            }
        }
        return builder.toString();
    }

    private List<String> toList(String str) {
        List<String> result = new ArrayList<String>();
        try {
            String[] names = str.split(",");
            for (String n : names)
                result.add(n.trim());

        } catch (NullPointerException e) {
            // result = new ArrayList<String>();
        }
        return result;
    }

    public static List<Player> findPlayerByNick(String part) {
        /* return  getPlayerListener().findPlayerNameByNick(part); */
        if (getInstance() != null)
            return getInstance().findPlayerasPlayerList(part);
        else
            return null;
    }

    public Player findPlayerasPlayer(String partOfName) {
        // Go for the nicks first!
        Player nick = playerListener.findPlayerByNick(partOfName);
        if (nick == null) {
            for (Player p : this.getServer().getOnlinePlayers()) {
                if (p.getName().toLowerCase().contains(partOfName.toLowerCase())) // found, return the fullname!
                    return p;
            }
        }

        // not found, just return partOfName
        return nick;
    }

    public Player getPlayer(String nick, boolean findNick) {
        Player p = findNick ? getPlayerByNick(nick) : null;
        if (p == null) p = this.getServer().getPlayerExact(nick);
        return p;
    }

    public Player getPlayerByNick(String nick) {
        return playerListener.findPlayerByNick(nick, true);
    }

    public List<Player> findPlayerasPlayerList(String partOfName) {
        // Go for the nicks first!
        List<Player> players = playerListener.findPlayersByNick(partOfName);
        for (Player p : this.getServer().getOnlinePlayers()) {
            if (p.getName().toLowerCase().contains(partOfName.toLowerCase())
                && !players.contains(p)) // found, return the fullname!
                players.add(p);
        }
        return players;
    }


    public String findPlayer(String partOfName) {
        // Go for the nicks first!
        for (Player p : this.getServer().getOnlinePlayers()) {
            if (p.getName().toLowerCase().contains(partOfName.toLowerCase())) // found, return the fullname!
                return p.getName();
        }
        // not found, just return partOfName
        return partOfName;
    }


    /**
     * Register the Bukkit events to the appropriate event handlers.
     */
    private void registerEvents() {
        /* getServer().getPluginManager().registerEvent(Event.Type.PLAYER_LOGIN, playerListener, Priority.High, this);
        getServer().getPluginManager().registerEvent(Event.Type.PLAYER_JOIN, playerListener, Priority.High, this);
        getServer().getPluginManager().registerEvent(Event.Type.PLAYER_CHAT, playerListener, Priority.High, this);
        getServer().getPluginManager().registerEvent(Event.Type.PLAYER_KICK, playerListener, Priority.High, this);
        getServer().getPluginManager().registerEvent(Event.Type.PLAYER_QUIT, playerListener, Priority.Normal, this);
        getServer().getPluginManager().registerEvent(Event.Type.PLAYER_TELEPORT, playerListener, Priority.High, this);
        getServer().getPluginManager().registerEvent(Event.Type.PLAYER_INTERACT, playerListener, Priority.Normal, this);
        getServer().getPluginManager().registerEvent(Event.Type.PLAYER_COMMAND_PREPROCESS, playerListener, Priority.Normal, this);
        getServer().getPluginManager().registerEvent(Event.Type.PLAYER_INTERACT_ENTITY, playerListener, Priority.Normal, this);
        getServer().getPluginManager().registerEvent(Event.Type.PLAYER_PORTAL, playerListener, Priority.High, this);
        getServer().getPluginManager().registerEvent(Event.Type.PLAYER_RESPAWN, playerListener, Priority.High, this);
        getServer().getPluginManager().registerEvent(Event.Type.PLAYER_CHANGED_WORLD, playerListener, Priority.High, this);
        getServer().getPluginManager().registerEvent(Event.Type.SIGN_CHANGE, blockListener, Priority.Normal, this);
        getServer().getPluginManager().registerEvent(Event.Type.BLOCK_FORM, blockListener, Priority.Normal, this);
        getServer().getPluginManager().registerEvent(Event.Type.BLOCK_BREAK, blockListener, Priority.Normal, this);
        getServer().getPluginManager().registerEvent(Event.Type.ENTITY_COMBUST, entityListener, Priority.Normal, this);
        getServer().getPluginManager().registerEvent(Event.Type.EXPLOSION_PRIME, entityListener, Priority.Normal, this);
        getServer().getPluginManager().registerEvent(Event.Type.ENTITY_TARGET, entityListener, Priority.Normal, this);
        getServer().getPluginManager().registerEvent(Event.Type.PROJECTILE_HIT, entityListener, Priority.Normal, this);
        getServer().getPluginManager().registerEvent(Event.Type.ENTITY_DEATH, entityListener, Priority.Normal, this);
        // getServer().getPluginManager().registerEvent(Event.Type.CHUNK_LOAD, worldListener, Priority.Monitor, this);
        getServer().getPluginManager().registerEvent(Event.Type.CHUNK_UNLOAD, worldListener, Priority.Normal, this); */
        PluginManager manager = getServer().getPluginManager();

        manager.registerEvents(playerListener, this);
        manager.registerEvents(entityListener, this);
        manager.registerEvents(blockListener, this);
        manager.registerEvents(worldListener, this);
    }

    public TweakcraftWorldListener getWorldListener() {
        return worldListener;
    }

    public Logger getLogger() {
        return log;
    }

    public List<String> getMOTD() {
        return MOTDLines;
    }

    public CraftIRC getCraftIRC() {
        return circ;
    }

    public WorldEditPlugin getWorldEdit() {
        return we;
    }

    public WorldGuardPlugin getWorldGuard() {
        return wg;
    }

    public String getPlayerColor(String playername) {
        return this.getPlayerColor(playername, false);
    }

    public String getPlayerColor(String playername, boolean change) {
        String pref = "";
        Player p = null;
        try {
            p = this.getServer().getPlayerExact(playername);
            pref = this.getPermissions().getResolver().getUserPrefix(p.getWorld().getName(), p);
        } catch (NullPointerException e) {
            pref = "§f";
        }
        String col = ChatColor.WHITE.toString();
        if (p == null && change) col = ChatColor.AQUA + "[NC] " + pref;
        if (p != null) col += pref;
        return col;

    }

    public ChatHandler getChathandler() {
        return this.chathandler;
    }

    @Deprecated
    public static List<String> splitUp(String msg) {
        int maxlength = 55;
        List<String> lijst = new ArrayList<String>();
        String toadd;
        int x = 0;
        while (x < msg.length() - maxlength) {
            toadd = msg.substring(x, x + maxlength);
            if (!toadd.trim().equals(""))
                lijst.add(toadd.trim());
            x += maxlength;
        }
        lijst.add(msg.substring(x));

        return lijst;
    }


    public void setupCraftIRC() {
        if (this.getConfigHandler().enableIRC) {
            Plugin plugin = this.getServer().getPluginManager().getPlugin("CraftIRC");

            if (circ == null) {
                if (plugin != null) {
                    circ = (CraftIRC) plugin;
                } else {
                    this.getConfigHandler().enableIRC = false;
                    this.getLogger().warning("[TweakcraftUtils] WARNING: Couldn't find CraftIRC, but is enabled in the config.");
                    this.getLogger().warning("[TweakcraftUtils] WARNING: Disabling CraftIRC support.");
                }
            }
        }
    }

    public void setupWorldGuard() {
        if (this.getConfigHandler().enableWorldGuard) {
            Plugin plugin = this.getServer().getPluginManager().getPlugin("WorldGuard");

            if (wg == null) {
                if (plugin != null) {
                    wg = (WorldGuardPlugin) plugin;
                } else {
                    this.getConfigHandler().enableWorldGuard = false;
                    this.getLogger().warning("[TweakcraftUtils] WARNING: Couldn't find WorldGuard, but is enabled in the config.");
                    this.getLogger().warning("[TweakcraftUtils] WARNING: Disabling WorldGuard support.");
                }
            }
        }
    }

    public void setupZones() {
        if (this.getConfigHandler().enableZones) {
            Plugin plugin = this.getServer().getPluginManager().getPlugin("Zones");

            if (zones == null) {
                if (plugin != null) {
                    zones = (Zones) plugin;
                } else {
                    this.getConfigHandler().enableZones = false;
                    this.getLogger().warning("[TweakcraftUtils] WARNING: Couldn't find Zones, but is enabled in the config.");
                    this.getLogger().warning("[TweakcraftUtils] WARNING: Disabling Zones support.");
                }
            }
        }
    }

    public void setupLogBlock() {
        if (this.getConfigHandler().enableLogBlock) {
            Plugin plugin = this.getServer().getPluginManager().getPlugin("LogBlock");

            if (lb == null) {
                if (plugin != null) {
                    lb = (LogBlock) plugin;
                } else {
                    this.getConfigHandler().enableLogBlock = false;
                    this.getLogger().warning("[TweakcraftUtils] WARNING: Couldn't find LogBlock, but is enabled in the config.");
                    this.getLogger().warning("[TweakcraftUtils] WARNING: Disabling LogBlock support.");
                }
            }
        }
    }

    public void setupWorldEdit() {
        Plugin plugin = this.getServer().getPluginManager().getPlugin("WorldEdit");

        if (we == null) {
            if (plugin != null) {
                we = (WorldEditPlugin) plugin;
            } /* We don't care if it failed because it's mostly a fallback for permissions. */
        }
    }

    public Zones getZones() {
        return zones;
    }

    public LogBlock getLogBlock() {
        return lb;
    }

    public void reloadMOTD() {
        File motdfile = new File(this.getDataFolder(), "motd.txt");
        MOTDLines = new ArrayList<String>();
        try {
            BufferedReader motdfilereader = new BufferedReader(new FileReader(motdfile));
            String line = "";
            while ((line = motdfilereader.readLine()) != null) {
                MOTDLines.add(line.replace('&', '§'));
            }
            motdfilereader.close();
        } catch (FileNotFoundException e) {
            log.severe("[TweakcraftUtils] MOTD file not found!");
        } catch (IOException e) {
            log.severe("[TweakcraftUtils] IOException occured while loadign the MOTD file!");
        }

    }

    public CommandHandler getCommandHandler() {
        return commandHandler;
    }

    public String getNickWithColors(String player) {
        String nick = this.wrapPlayer(player).getNick();
        String realname = player;
        if (nick == null) nick = realname;
        /// return getPlayerColor(realname, false) + nick + ChatColor.WHITE;
        return this.getPermissions().getResolver().getUserPrefix(player) + nick + ChatColor.WHITE;
    }

    public String getNick(String player) {
        String nick = playerListener.getNick(player);
        String realname = player;
        if (nick == null) nick = realname;
        return nick;
    }

    public boolean check(Player player, String permNode) {
        return player.isOp() ||
            this.getPermissions().getResolver().hasPermission(player.getWorld().getName(), player, "tweakcraftutils." + permNode);
    }

    public boolean checkfull(Player player, String permNode) {
        return player.isOp() ||
            this.getPermissions().getResolver().hasPermission(player.getWorld().getName(), player, permNode);
    }

    public BanHandler getBanhandler() {
        return banhandler;
    }

    public boolean hasNick(String player) {
        return playerListener.getNick(player) != null;
    }


    public void onEnable() {
        pdfFile = this.getDescription();
        instance = this;

        donottplist = new HashSet<String>();
        MOTDLines = new ArrayList<String>();
        this.reloadMOTD();
        configHandler.setGlobalConfig(new File(this.getDataFolder(), "config.yml"));
        configHandler.reloadConfig();

        if(configHandler.enablePersistence) {
            if(!databaseloaded) {
                setupDatabase();
            }
        }

        this.cuiPlayers = new HashSet<Player>();
        this.mod_InfDuraplayers = new HashSet<Player>();

        this.setupWorldGuard();
        this.setupCraftIRC();
        this.setupZones();
        this.setupLogBlock();
        // this.setupNoLagg();

        playerReplyDB = new HashMap<String, String>();
        this.registerEvents();

        itemDB.loadDataBase();
        worldmanager.setupWorlds();
        banhandler.reloadBans();

        if (configHandler.enableIRC) {
            circendpoint = new CraftIRCEndPoint(this, "tcutils");
            cirdlogendpoint = new CraftIRCEndPoint(this, "tcutilslog");
            circadminendpoint = new CraftIRCAdminEndPoint(this);
        }

        if (configHandler.enableDebug)
            commandHandler.checkCommands();

        /* itemDB.writeDB(); */

        playerListener.reloadInvisTable();
        log.info("[" + pdfFile.getName() + "] " + pdfFile.getName() + " version " + pdfFile.getVersion() + " is enabled!");

    }

    public EndPoint getEndPoint() {
        return (EndPoint) circendpoint;
    }

    public EndPoint getAdminEndPoint() {
        return (EndPoint) circadminendpoint;
    }
    
    public String getVersion() {
        return pdfFile.getVersion();
    }
    
    public EndPoint getLogginEndPoint() {
        return (EndPoint) cirdlogendpoint;
    }

    public Set<String> getDonottplist() {
        return donottplist;
    }

    public void onDisable() {
        instance = null;
        log.info("[" + pdfFile.getName() + "] Shutting down!");
        // this.getDatabase().
    }

    public ConfigurationHandler getConfigHandler() {
        return configHandler;
    }

    public ChunkGenerator getDefaultWorldGenerator(String worldName, String id) {
        return worldmanager.getDefaultWorldGenerator(worldName, id);
    }

    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] unfilteredargs) {

        List<String> argsa = new ArrayList<String>();
        // String[] args = new String[];
        int argc = 0;
        for (String a : unfilteredargs) {
            if (a != null && !a.trim().equals("")) {
                argsa.add(a);
                argc++;
            }
        }
        String[] args = argsa.toArray(new String[0]);

        if (commandHandler.getCommandMap().containsKey(cmd.getName())) {
            return commandHandler.executeCommand(sender, cmd.getName(), unfilteredargs);
        }
        return false;
    }
}