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

import com.ensifera.animosity.ircplugin.IRCPlugin;

import com.guntherdw.bukkit.tweakcraft.Ban.BanHandler;
import com.guntherdw.bukkit.tweakcraft.Chat.ChatHandler;
import com.guntherdw.bukkit.tweakcraft.Commands.CommandHandler;
import com.guntherdw.bukkit.tweakcraft.Configuration.ConfigurationHandler;
import com.guntherdw.bukkit.tweakcraft.Exceptions.*;
import com.guntherdw.bukkit.tweakcraft.Packages.ItemDB;
import com.guntherdw.bukkit.tweakcraft.Tools.TamerTool;
import com.guntherdw.bukkit.tweakcraft.Worlds.WorldManager;
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

import java.io.*;
import java.util.*;
import java.util.logging.Logger;

/**
 * @author GuntherDW
 */

public class TweakcraftUtils extends JavaPlugin {

    protected Permissions perm = null;
    protected WorldGuardPlugin wg = null;
    protected IRCPlugin circ = null;
    protected Zones zones = null;

    private final TweakcraftPlayerListener playerListener = new TweakcraftPlayerListener(this);
    private final CommandHandler commandHandler = new CommandHandler(this);
    private final BanHandler banhandler = new BanHandler(this);
    private final ItemDB itemDB = new ItemDB(this);
    private final WorldManager worldmanager = new WorldManager(this);
    private final ConfigurationHandler configHandler = new ConfigurationHandler(this);
    private final TeleportHistory telehistory = new TeleportHistory(this);
    private final TamerTool tamertool = new TamerTool(this);

    private List<String> MOTDLines;
    public Map<String, String> playerReplyDB;
    private final ChatHandler chathandler = new ChatHandler(this);
    private List<String> donottplist;
    public static File datafolder;

    protected static final Logger log = Logger.getLogger("Minecraft");

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
        getServer().getPluginManager().registerEvent(Event.Type.PLAYER_LOGIN, playerListener, Priority.High, this);
        getServer().getPluginManager().registerEvent(Event.Type.PLAYER_JOIN, playerListener, Priority.Monitor, this);
        getServer().getPluginManager().registerEvent(Event.Type.PLAYER_CHAT, playerListener, Priority.Normal, this);
        getServer().getPluginManager().registerEvent(Event.Type.PLAYER_QUIT, playerListener, Priority.Normal, this);
        getServer().getPluginManager().registerEvent(Event.Type.PLAYER_TELEPORT, playerListener, Priority.Normal, this);
        getServer().getPluginManager().registerEvent(Event.Type.PLAYER_INTERACT, playerListener, Priority.Normal, this);
        getServer().getPluginManager().registerEvent(Event.Type.PLAYER_INTERACT_ENTITY, playerListener, Priority.Normal, this);
    }

    public Logger getLogger() {
        return log;
    }

    public List<String> getMOTD() {
        return MOTDLines;
    }

    public IRCPlugin getCraftIRC() {
        return circ;
    }

    public WorldGuardPlugin getWorldGuard() {
        return wg;
    }

    public Permissions getPerm() {
        return perm;
    }
    
    public String getPlayerColor(String playername, boolean change) {

        String pref = "";
        String group = "";
        Player p = this.getServer().getPlayer(playername);

        try {
            if (p != null) {
                group = perm.Security.getGroup(p.getWorld().getName(), playername);
                pref = perm.Security.getGroupPrefix(p.getWorld().getName(), group).replace("&", "§");
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
            }
        }
    }

    public void setupCraftIRC() {
        if(this.getConfigHandler().enableIRC) {
            Plugin plugin = this.getServer().getPluginManager().getPlugin("IRCPlugin");

            if (circ == null) {
                if (plugin != null) {
                    circ = (IRCPlugin) plugin;
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
                }
            }
        }
    }

    public void setupZones() {
        if(this.getConfigHandler().enableZones) {
            Plugin plugin = this.getServer().getPluginManager().getPlugin("Zones");

            if(zones == null) {
                if(plugin != null)
                    zones = (Zones) plugin;
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
            return perm.Security.permission(player, "tweakcraftutils." + permNode);
        }
    }

    public boolean checkfull(Player player, String permNode) {
        if (perm == null || player.isOp()) {
            return true;
        } else {
            return perm.Security.permission(player, permNode);
        }
    }

    public BanHandler getBanhandler() {
        return banhandler;
    }

    public boolean hasNick(String player) {
        return playerListener.getNick(player)!=null;
    }

    public void onEnable() {

        PluginDescriptionFile pdfFile = this.getDescription();
        
        donottplist = new ArrayList<String>();
        MOTDLines = new ArrayList<String>();
        this.reloadMOTD();
        configHandler.reloadConfig();
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
        log.info("[TweakcraftUtils] Goodbye world!");
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
                com.guntherdw.bukkit.tweakcraft.Commands.Command command = commandHandler.getCommand(cmd.getName());
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