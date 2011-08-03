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

import com.ensifera.animosity.craftirc.CraftIRC;

import com.guntherdw.bukkit.tweakcraft.Chat.ChatMode;
import com.guntherdw.bukkit.tweakcraft.DataSources.Ban.BanHandler;
import com.guntherdw.bukkit.tweakcraft.Chat.ChatHandler;
import com.guntherdw.bukkit.tweakcraft.Commands.CommandHandler;
import com.guntherdw.bukkit.tweakcraft.Commands.iCommand;
import com.guntherdw.bukkit.tweakcraft.Configuration.ConfigurationHandler;
import com.guntherdw.bukkit.tweakcraft.DataSources.PersistenceClass.PlayerHistoryInfo;
import com.guntherdw.bukkit.tweakcraft.DataSources.PersistenceClass.PlayerInfo;
import com.guntherdw.bukkit.tweakcraft.DataSources.PersistenceClass.PlayerOptions;
import com.guntherdw.bukkit.tweakcraft.Exceptions.*;
import com.guntherdw.bukkit.tweakcraft.Listeners.TweakcraftEntityListener;
import com.guntherdw.bukkit.tweakcraft.Listeners.TweakcraftPlayerListener;
import com.guntherdw.bukkit.tweakcraft.Listeners.TweakcraftWorldListener;
import com.guntherdw.bukkit.tweakcraft.Packages.ItemDB;
import com.guntherdw.bukkit.tweakcraft.Tools.TamerTool;
import com.guntherdw.bukkit.tweakcraft.Util.TeleportHistory;
import com.guntherdw.bukkit.tweakcraft.Worlds.WorldManager;
import com.nijiko.permissions.Group;
import com.nijiko.permissions.PermissionHandler;
import com.nijikokun.bukkit.Permissions.Permissions;

import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.zones.Zones;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.Event.Priority;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
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
    protected PermissionHandler ph = null;

    private final TweakcraftPlayerListener playerListener = new TweakcraftPlayerListener(this);
    private final TweakcraftEntityListener entityListener = new TweakcraftEntityListener(this);
    private final TweakcraftWorldListener worldListener = new TweakcraftWorldListener(this);
    private final CommandHandler commandHandler = new CommandHandler(this);
    private final BanHandler banhandler = new BanHandler(this);
    private final ItemDB itemDB = new ItemDB(this);
    private final WorldManager worldmanager = new WorldManager(this);
    private final ConfigurationHandler configHandler = new ConfigurationHandler(this);
    private final TeleportHistory telehistory = new TeleportHistory(this);
    private final TamerTool tamertool = new TamerTool(this);
    private final ChatHandler chathandler = new ChatHandler(this);
    
    private List<String> donottplist;
    private List<String> MOTDLines;

    protected static final Logger log = Logger.getLogger("Minecraft");
    protected PluginDescriptionFile pdfFile = null;

    private List<Player> cuiPlayers;
    public String CUIPattern = "§7§3§3§4";


    public static File datafolder;
    public Map<String, String> playerReplyDB;
    public boolean databaseloaded = false;


    public List<Player> getCUIPlayers() {
        return cuiPlayers;
    }

    public void sendCUIChatMode(Player player) {
        if(this.cuiPlayers != null && this.cuiPlayers.contains(player))
        {
            ChatMode cm = getChathandler().getPlayerChatMode(player);

            if(cm==null)
                player.sendRawMessage(CUIPattern+"null");
            else
                player.sendRawMessage(CUIPattern+"["+cm.getPrefix()+ChatColor.WHITE+"]");
        }
    }

    public void sendCUIHandShake(Player player) {
        if(getConfigHandler().enableCUI)
            player.sendRawMessage(CUIPattern); // HANDSHAKE
    }

    public String findinlist(String find, List<String> list) {
        for (String name : list) {
            if (name.toLowerCase().contains(find.toLowerCase())) {
                return name;
            }
        }
        return null;
    }

    public String getPlayerReply(String player) {
        if (playerReplyDB.containsKey(player)) {
            return playerReplyDB.get(player);
        } else {
            return null;
        }
    }

    public TeleportHistory getTelehistory() {
        return telehistory;
    }

    public TweakcraftPlayerListener getPlayerListener() {
        return playerListener;
    }

    public void setPlayerReply(String player, String toPlayer) {
        playerReplyDB.put(player, toPlayer);
    }

    public ItemDB getItemDB() {
        return itemDB;
    }

    public WorldManager getworldManager() {
        return worldmanager;
    }

    public TamerTool getTamerTool() {
        return tamertool;
    }

    public String getCompassDirection(Float rotation) {
        String dir;
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
             if(configHandler.useTweakBotSeen)
                 getDatabase().find(PlayerHistoryInfo.class).findRowCount();
         } catch (PersistenceException ex) {
             log.info("[TweakcraftUtils] Installing database for " + getDescription().getName() + " due to first time usage");
             if(configHandler.useTweakBotSeen)
                 log.info("[TweakcraftUtils] Also creating the TweakBot !seen helpen table");
             installDDL();
         }
         databaseloaded = true;
    }

    public String listToString(List<String> lijst) {
        String res = "";
        if (lijst.size() != 0) {
            for (String s : lijst) {
                res += s + ",";
            }
            res = res.substring(0, res.length() - 1);
        } else {
            res = "";
        }
        return res;
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

    public Player findPlayerasPlayer(String partOfName) {
        // Go for the nicks first!
        Player nick = playerListener.findPlayerByNick(partOfName);
        if(nick==null) {
            for (Player p : this.getServer().getOnlinePlayers()) {
                if (p.getName().toLowerCase().contains(partOfName.toLowerCase())) // found, return the fullname!
                    return p;
            }
        }

        // not found, just return partOfName
        return nick;
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


    private void registerEvents() {
        getServer().getPluginManager().registerEvent(Event.Type.PLAYER_LOGIN,           playerListener, Priority.High, this);
        getServer().getPluginManager().registerEvent(Event.Type.PLAYER_JOIN,            playerListener, Priority.Monitor, this);
        getServer().getPluginManager().registerEvent(Event.Type.PLAYER_CHAT,            playerListener, Priority.Normal, this);
        getServer().getPluginManager().registerEvent(Event.Type.PLAYER_KICK,            playerListener, Priority.Normal, this);
        getServer().getPluginManager().registerEvent(Event.Type.PLAYER_QUIT,            playerListener, Priority.Normal, this);
        getServer().getPluginManager().registerEvent(Event.Type.PLAYER_TELEPORT,        playerListener, Priority.Normal, this);
        getServer().getPluginManager().registerEvent(Event.Type.PLAYER_INTERACT,        playerListener, Priority.Normal, this);
        getServer().getPluginManager().registerEvent(Event.Type.PLAYER_INTERACT_ENTITY, playerListener, Priority.Normal, this);
        getServer().getPluginManager().registerEvent(Event.Type.ENTITY_COMBUST,         entityListener, Priority.Normal, this);
        getServer().getPluginManager().registerEvent(Event.Type.EXPLOSION_PRIME,        entityListener, Priority.Normal, this);
        getServer().getPluginManager().registerEvent(Event.Type.ENTITY_DEATH,           entityListener, Priority.Normal, this);
        getServer().getPluginManager().registerEvent(Event.Type.CHUNK_UNLOAD,           worldListener , Priority.Normal, this);
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

    public WorldGuardPlugin getWorldGuard() {
        return wg;
    }

    public Permissions getPerm() {
        return perm;
    }

    public String getPlayerColor(String playername, boolean change) {

        Group g = null;
        String pref = "";
        String group = "";
        Player p = this.getServer().getPlayer(playername);


        try {
            if (p != null) {

                // group = g.getName(); // Not used right now.
                pref = ph.getUserPrefix(p.getWorld().getName(), p.getName()).replace("&", "§");
            } else {
                pref = "§f";
            }

        } catch (NullPointerException e) {
            pref = "§f";
        }
        String col = ChatColor.WHITE.toString();
        if (p == null) col = ChatColor.AQUA + "[NC] " + pref;
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
            if (!toadd.trim().isEmpty())
                lijst.add(toadd.trim());
            x += maxlength;
        }
        lijst.add(msg.substring(x));

        return lijst;
    }

    public void setupPermissions() {
        Plugin plugin = this.getServer().getPluginManager().getPlugin("Permissions");

        if (perm == null) {
            if (plugin != null) {
                perm = (Permissions) plugin;
                ph = perm.getHandler();
            }
        }
    }

    public void setupCraftIRC() {
        if(this.getConfigHandler().enableIRC) {
            Plugin plugin = this.getServer().getPluginManager().getPlugin("CraftIRC");

            if (circ == null) {
                if (plugin != null) {
                    circ = (CraftIRC) plugin;
                }   else {
                    this.getConfigHandler().enableIRC = false;
                    this.getLogger().warning("[TweakcraftUtils] WARNING: Couldn't find CraftIRC, but is enabled in the config.");
                    this.getLogger().warning("[TweakcraftUtils] WARNING: Disabling CraftIRC support.");
                }
            }
        }
    }

    public void setupWorldGuard() {
        if(this.getConfigHandler().enableWorldGuard) {
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
        if(this.getConfigHandler().enableZones) {
            Plugin plugin = this.getServer().getPluginManager().getPlugin("Zones");

            if(zones == null) {
                if(plugin != null) {
                    zones = (Zones) plugin;
                }  else {
                    this.getConfigHandler().enableZones = false;
                    this.getLogger().warning("[TweakcraftUtils] WARNING: Couldn't find Zones, but is enabled in the config.");
                    this.getLogger().warning("[TweakcraftUtils] WARNING: Disabling Zones support.");
                }
            }
        }
    }

    public Zones getZones() {
        return zones;
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
        String nick = playerListener.getNick(player);
        String realname = player;
        if(nick == null) nick = realname;
        return getPlayerColor(realname, false) + nick + ChatColor.WHITE;
    }
    
    public String getNick(String player) {
        String nick = playerListener.getNick(player);
        String realname = player;
        if(nick == null) nick = realname;
        return  nick;
    }

    public boolean check(Player player, String permNode) {
        if (perm == null || player.isOp()) {
            return true;
        } else {
            return ph.permission(player, "tweakcraftutils." + permNode);
        }
    }

    public boolean checkfull(Player player, String permNode) {
        if (perm == null || player.isOp()) {
            return true;
        } else {
            return ph.permission(player, permNode);
        }
    }

    public PermissionHandler getPermissionHandler() {
        return ph;
    }

    public BanHandler getBanhandler() {
        return banhandler;
    }

    public boolean hasNick(String player) {
        return playerListener.getNick(player)!=null;
    }

    public void onEnable() {
        pdfFile = this.getDescription();

        donottplist = new ArrayList<String>();
        MOTDLines = new ArrayList<String>();
        this.reloadMOTD();
        configHandler.reloadConfig();
        this.cuiPlayers = new ArrayList<Player>();
        
        this.setupWorldGuard();
        this.setupCraftIRC();
        this.setupZones();

        playerReplyDB = new HashMap<String, String>();
        this.registerEvents();
        this.setupPermissions();

        itemDB.loadDataBase();
        worldmanager.setupWorlds();
        banhandler.reloadBans();
        /* itemDB.writeDB(); */

        playerListener.reloadInvisTable();
        log.info("[" + pdfFile.getName() + "] " + pdfFile.getName() + " version " + pdfFile.getVersion() + " is enabled!");
    }

    public List<String> getDonottplist() {
        return donottplist;
    }

    public void onDisable() {
        log.info("["+pdfFile.getName()+"] Shutting down!");
        // this.getDatabase().
    }

    public ConfigurationHandler getConfigHandler() {
        return configHandler;
    }

    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] unfilteredargs) {

        List<String> argsa = new ArrayList<String>();
        // String[] args = new String[];
        int argc = 0;
        for(String a : unfilteredargs) {
            if(a!=null&& !a.isEmpty() && !a.trim().equals("")) {
                // args[argc] = a;
                argsa.add(a);
                argc++;
            }
        }
        String[] args = argsa.toArray(new String[0]);

        if (commandHandler.getCommandMap().containsKey(cmd.getName())) {
            try {
                iCommand command = commandHandler.getCommand(cmd.getName());
                // public abstract boolean executeCommand(Server server, CommandSender sender, String command, String[] args, TweakcraftUtils plugin);
                if (!command.executeCommand(sender, cmd.getName(), args, this)) {
                    sender.sendMessage("This command did not go as intended!");
                }
                String mess = "";
                if (args.length > 1) {
                    for (String m : args)
                        mess += m + " ";

                    mess = mess.substring(0, mess.length() - 1);
                } else if (args.length == 1) {
                    mess = args[0];
                }

                if (sender instanceof Player)
                    log.info("[TweakcraftUtils] " + ((Player) sender).getName() + " issued: /" + cmd.getName() + " " + mess);
                else
                    log.info("[TweakcraftUtils] CONSOLE issued: /" + cmd.getName() + " " + mess);
                return true;
            } catch (CommandNotFoundException e) {
                sender.sendMessage("TweakcraftUtils error, command not found!");
            } catch (PermissionsException e) {
                sender.sendMessage(ChatColor.RED + "You do not have the correct permissions for this command or usage!");
                if (sender instanceof Player) {
                    String mess = "";
                    if (args.length > 1) {
                        for (String m : args)
                            mess += m + " ";

                        mess = mess.substring(0, mess.length() - 1);
                    } else if (args.length == 1) {
                        mess = args[0];
                    }
                    log.info("[TweakcraftUtils] " + ((Player) sender).getName() + " tried: /" + cmd.getName() + " " + mess);

                }
            } catch (CommandUsageException e) {
                sender.sendMessage(ChatColor.YELLOW + e.getMessage());
            } catch (CommandSenderException e) {
                sender.sendMessage(ChatColor.YELLOW + e.getMessage());
            } catch (CommandException e) {
                sender.sendMessage(ChatColor.YELLOW + e.getMessage());
            }
        }
        return false;
    }
}
