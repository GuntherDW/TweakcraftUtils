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
import com.guntherdw.bukkit.tweakcraft.Exceptions.*;
import com.guntherdw.bukkit.tweakcraft.Packages.ItemDB;
import com.guntherdw.bukkit.tweakcraft.Worlds.WorldManager;
import com.nijikokun.bukkit.Permissions.Permissions;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.Event.Priority;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.config.Configuration;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

/**
 * @author GuntherDW
 */

public class TweakcraftUtils extends JavaPlugin {

    private Permissions perm = null;
    private WorldGuardPlugin wg = null;
    private IRCPlugin circ = null;

    private final TweakcraftPlayerListener playerListener = new TweakcraftPlayerListener(this);
    private final CommandHandler commandHandler = new CommandHandler(this);
    private final BanHandler banhandler = new BanHandler(this);
    private final ItemDB itemDB = new ItemDB(this);
    private final WorldManager worldmanager = new WorldManager(this);
    // private final
    public int playerLimit;
    public int maxRange;
    private Configuration seenconfig;
    protected boolean keepplayerhistory = false;
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

    public String findPlayer(String partOfName) {
        for (Player p : this.getServer().getOnlinePlayers()) {
            if (p.getName().toUpperCase().contains(partOfName.toUpperCase())) // found, return the fullname!
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

    public void reloadConfig() {
        log.info("[TweakcraftUtils] Parsing configuration file...");
        getConfiguration().load();
        if (getConfiguration().getBoolean("keepplayerhistory", false)) {
            log.info("[TweakcraftUtils] Keeping player history!");
            File seenFile = new File(getDataFolder(), "players.yml");
            seenconfig = new Configuration(seenFile);
            seenconfig.load();
            keepplayerhistory = true;
        }
        maxRange = getConfiguration().getInt("maxrange", 200);
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
        Plugin plugin = this.getServer().getPluginManager().getPlugin("IRCPlugin");

        if (circ == null) {
            if (plugin != null) {
                circ = (IRCPlugin) plugin;
            }
        }
    }

    public void setupWorldGuard() {
        Plugin plugin = this.getServer().getPluginManager().getPlugin("WorldGuard");

        if (wg == null) {
            if (plugin != null) {
                wg = (WorldGuardPlugin) plugin;
            }
        }
    }

    public void reloadMOTD() {
        File motdfile = new File(this.getDataFolder(), "motd.txt");
        MOTDLines = new ArrayList<String>();
        try {
            BufferedReader motdfilereader = new BufferedReader(new FileReader(motdfile));
            String line = motdfilereader.readLine();
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

    public boolean check(Player player, String permNode) {
        if (perm == null || player.isOp()) {
            return true;
        } else {
            return perm.Security.permission(player, "tweakcraftutils." + permNode);
        }
    }

    public BanHandler getBanhandler() {
        return banhandler;
    }

    public void onEnable() {

        PluginDescriptionFile pdfFile = this.getDescription();
        
        playerLimit = this.getServer().getMaxPlayers();
        donottplist = new ArrayList<String>();
        MOTDLines = new ArrayList<String>();
        this.reloadMOTD();
        this.setupWorldGuard();
        this.setupCraftIRC();

        playerReplyDB = new HashMap<String, String>();

        this.registerEvents();
        this.reloadConfig();
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

    public boolean isKeepplayerhistory() {
        return keepplayerhistory;
    }

    public Configuration getSeenconfig() {
        return seenconfig;
    }

    public void onDisable() {
        log.info("[TweakcraftUtils] Goodbye world!");
    }


    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {

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