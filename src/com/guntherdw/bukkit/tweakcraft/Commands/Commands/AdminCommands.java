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

package com.guntherdw.bukkit.tweakcraft.Commands.Commands;

import com.guntherdw.bukkit.tweakcraft.Chat.ChatHandler;
import com.guntherdw.bukkit.tweakcraft.Chat.ChatMode;
import com.guntherdw.bukkit.tweakcraft.Chat.Modes.AdminChat;
import com.guntherdw.bukkit.tweakcraft.Commands.aCommand;
import com.guntherdw.bukkit.tweakcraft.DataSources.Ban.BanHandler;
import com.guntherdw.bukkit.tweakcraft.Events.TweakcraftUtilsEvent;
import com.guntherdw.bukkit.tweakcraft.Exceptions.*;
import com.guntherdw.bukkit.tweakcraft.Packages.ItemDB;
import com.guntherdw.bukkit.tweakcraft.Tools.ArgumentParser;
import com.guntherdw.bukkit.tweakcraft.TweakcraftUtils;
import com.guntherdw.bukkit.tweakcraft.Worlds.TweakWorld;
import com.guntherdw.bukkit.tweakcraft.Worlds.WorldManager;
import com.guntherdw.bukkit.tweakcraft.Worlds.iWorld;
import org.bukkit.ChatColor;
import org.bukkit.Difficulty;
import org.bukkit.GameMode;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author GuntherDW
 */
public class AdminCommands {

    private TweakcraftUtils plugin;

    public AdminCommands(TweakcraftUtils instance) {
        this.plugin = instance;
    }

    @aCommand(
        aliases = {"admin", "a"},
        permissionBase = "admin",
        description = "Send message to admins",
        section = "admin"
    )
    public boolean admin(CommandSender sender, String command, String[] args)
        throws PermissionsException, CommandSenderException, CommandUsageException, CommandException {
        boolean onlist = false;
        try {
            ChatMode cm = plugin.getChathandler().getChatMode("admin");
            String msg = "";
            for (String m : args)
                msg += m + " ";
            if (msg.length() > 0) {
                msg = msg.substring(0, msg.length() - 1);

                if (sender instanceof Player) {
                    onlist = (cm.getSubscribers().contains(((Player) sender).getName())
                        || ((AdminChat) cm).getAdminsString().contains(((Player) sender).getName()));
                } else {
                    onlist = true;
                }

                if (!onlist) {
                    sender.sendMessage(ChatColor.GREEN + "Message sent to admins:");
                }
                cm.sendMessage(sender, msg);
            } else {
                sender.sendMessage(ChatColor.YELLOW + "You were trying to send an empty message!");
            }
        } catch (ChatModeException e) {
            throw new CommandException("Error occurred while trying to get ChatMode!");
        }
        return true;
    }

    @aCommand(
        aliases = {"admin-add"},
        permissionBase = "admon",
        description = "Adds a player to the admin-msg list",
        section = "admin"
    )
    public boolean admin_add(CommandSender sender, String command, String[] args)
        throws PermissionsException, CommandSenderException, CommandException, CommandUsageException {
        if (sender instanceof Player) {
            if (!plugin.check((Player) sender, "admon"))
                throw new PermissionsException(command);
        }
        if (args.length < 1) {
            throw new CommandUsageException("Give me a name to add!");
        }

        try {
            ChatMode cm = plugin.getChathandler().getChatMode("admin");
            Set<String> playernames = new HashSet<String>();
            for (Player p : plugin.getServer().getOnlinePlayers()) {
                playernames.add(p.getName());
            }

            String name = plugin.findinlist(args[0], playernames);
            Player player = plugin.getServer().getPlayer(name);
            if (player != null) {
                cm.addRecipient(player.getName());
                String addedplayer = plugin.getPlayerColor(player.getName(), false) + player.getName();
                if (!(sender instanceof Player))
                    sender.sendMessage(addedplayer + ChatColor.YELLOW + " has been added to the admin-msg list!");
                String adder = "";
                if (sender instanceof Player) {
                    adder = plugin.getPlayerColor(((Player) sender).getName(), false) + ((Player) sender).getName();
                } else {
                    adder = ChatColor.LIGHT_PURPLE + "CONSOLE";
                }

                player.sendMessage(ChatColor.YELLOW + "You have been added to the admin-msg list by " + adder + ChatColor.YELLOW + "!");
                for (Player p : ((AdminChat) cm).getAdmins()) {
                    p.sendMessage(adder + ChatColor.YELLOW + " added " + addedplayer + ChatColor.YELLOW + " to the admin-msg list!");
                }
            } else {
                throw new CommandException("Can't find player!");
            }

        } catch (ChatModeException e) {
            throw new CommandException("There was an error getting the admin ChatMode!");
        }
        return true;
    }

    @aCommand(
        aliases = {"admin-remove"},
        permissionBase = "admon",
        description = "Removes a player from the admin-msg list",
        section = "admin"
    )
    public boolean admin_remove(CommandSender sender, String command, String[] args)
        throws PermissionsException, CommandSenderException, CommandUsageException, CommandException {
        if (sender instanceof Player) {
            if (!plugin.check((Player) sender, "admon"))
                throw new PermissionsException(command);
        }
        if (args.length < 1) {
            throw new CommandUsageException("Give me a name to remove!");
        }

        try {
            ChatHandler ch = plugin.getChathandler();
            ChatMode cm = ch.getChatMode("admin");
            String name = plugin.findinlist(args[0], cm.getSubscribers());
            if (cm.getSubscribers().contains(name)) {
                List<Player> p = plugin.getServer().matchPlayer(args[0]);

                Player player = null;
                String pname = name;
                String adder = "";
                if (sender instanceof Player) {
                    adder = plugin.getPlayerColor(((Player) sender).getName(), false) + ((Player) sender).getName();
                } else {
                    adder = ChatColor.LIGHT_PURPLE + "CONSOLE";
                }
                if (p.size() == 1) {
                    player = p.get(0);
                    name = plugin.getPlayerColor(player.getName(), false) + player.getName();
                    player.sendMessage(ChatColor.YELLOW + "You have been removed from the admin-msg list by " + adder + ChatColor.YELLOW + "!");
                }

                if (!(sender instanceof Player))
                    sender.sendMessage(name + ChatColor.YELLOW + " has been removed from the admin-msg list!");

                cm.removeRecipient(pname);

                boolean chatlist = false;

                if (ch.getPlayerChatModeString(pname) != null && ch.getPlayerChatModeString(pname).equals("admin")) {
                    ch.setPlayerchatmode(pname, null);
                    chatlist = true;
                }
                for (Player pl : ((AdminChat) cm).getAdmins()) {
                    pl.sendMessage(adder + ChatColor.YELLOW + " removed " + name + ChatColor.YELLOW + " from the admin-msg list!");
                    if (chatlist)
                        pl.sendMessage(ChatColor.YELLOW + "Player has also been removed from the auto-admin-msg list!");
                }
            } else {
                sender.sendMessage(ChatColor.YELLOW + "I can't find that player!");
            }

        } catch (ChatModeException e) {
            throw new CommandException("There was an error getting the admin ChatMode!");
        }
        return true;
    }

    @aCommand(
        aliases = {"admin-list"},
        permissionBase = "admon",
        description = "Show the current admin-msg list",
        section = "admin"
    )
    public boolean admin_list(CommandSender sender, String command, String[] args) throws PermissionsException, CommandSenderException, CommandUsageException, CommandException {
        if (sender instanceof Player) {
            if (!plugin.check((Player) sender, "admon"))
                throw new PermissionsException(command);
        }
        try {
            ChatHandler ch = plugin.getChathandler();
            ChatMode cm = ch.getChatMode("admin");
            if (cm.getSubscribers().size() != 0) {
                sender.sendMessage(ChatColor.YELLOW + "Current admin-msg subscriber list : ");
                String color = "";
                String msg = "";
                for (String playername : cm.getSubscribers()) {
                    try {
                        color = plugin.getPlayerColor(playername, true);
                    } catch (NullPointerException e) {
                        color = ChatColor.WHITE.toString();
                    }
                    msg = color + playername;
                    sender.sendMessage(msg);
                }

            } else {
                sender.sendMessage(ChatColor.YELLOW + "Current admin-msg subscriber list is empty!");
            }
        } catch (ChatModeException e) {
            throw new CommandException("Exception thrown while fetching ChatMode!");
        }
        return true;
    }

    @aCommand(
        aliases = {"admoff"},
        permissionBase = "",
        description = "Makes you chat like normal again",
        section = "admin"
    )
    public boolean admoff(CommandSender sender, String command, String[] args) throws PermissionsException, CommandSenderException, CommandUsageException, CommandException {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            try {
                ChatMode cm = plugin.getChathandler().getChatMode("admin");
                if (cm.getRecipients(null).contains(player)) {
                    plugin.getChathandler().setPlayerchatmode(((Player) sender).getName(), null);
                    sender.sendMessage(ChatColor.YELLOW + "You will now chat globally!");
                } else {
                    throw new PermissionsException(command);
                }

            } catch (ChatModeException e) {
                throw new CommandException("Error occured while trying to fetch ChatMode!");
            }
        } else {
            throw new CommandSenderException("Do you really need this as a console?");
        }

        return true;
    }

    @aCommand(
        aliases = {"admon"},
        permissionBase = "",
        description = "Automatically sends any msg as an admin message",
        section = "admin"
    )
    public boolean admon(CommandSender sender, String command, String[] args)
        throws PermissionsException, CommandSenderException, CommandException, CommandUsageException {
        if (sender instanceof Player) {
            try {
                ChatMode cm = plugin.getChathandler().getChatMode("admin");
                if (cm.getSubscribers().contains(((Player) sender).getName())
                    || ((AdminChat) cm).getAdminsString().contains(((Player) sender).getName())) {
                    plugin.getChathandler().setPlayerchatmode(((Player) sender).getName(), "admin");
                    sender.sendMessage(ChatColor.YELLOW + "You will now automatically send admin-msges!");
                } else {
                    throw new PermissionsException(command);
                }

            } catch (ChatModeException e) {
                throw new CommandException("Error occured while trying to fetch ChatMode!");
            }
        } else {
            throw new CommandSenderException("Do you really need this as a console?");
        }

        return true;
    }


    @aCommand(
        aliases = {"clearinventory", "cli"},
        permissionBase = "clearinventory",
        description = "Clear a player's inventory",
        section = "admin"
    )
    public boolean clearinventory(CommandSender sender, String command, String[] args)
        throws PermissionsException, CommandSenderException, CommandException, CommandUsageException {
        if (sender instanceof Player)
            if (!plugin.check((Player) sender, "clearinventory"))
                throw new PermissionsException(command);

        Player victim = null;

        if (args.length == 0 && sender instanceof Player)
            victim = (Player) sender;
        else if (args.length == 0 && !(sender instanceof Player))
            sender.sendMessage("Can't clear the console's inventory when it doesn't need one...");
        else if (args.length != 0) {
            List<Player> p = plugin.getServer().matchPlayer(args[0]);
            if (p.size() == 1) {
                victim = p.get(0);
            } else {
                sender.sendMessage(ChatColor.YELLOW + "Can't find player!");
            }
        }

        if (victim != null) {
            sender.sendMessage(ChatColor.YELLOW + "Clearning " + victim.getDisplayName() + ChatColor.YELLOW + "'s inventory!");
            victim.getInventory().clear();
        } else {
            sender.sendMessage(ChatColor.RED + "Victim is null, this isn't supposed to happen!");
        }
        return true;
    }

    @aCommand(
        aliases = {"tplist"},
        permissionBase = "tplist",
        description = "Shows who has /tpoff",
        section = "admin"
    )
    public boolean tplist(CommandSender sender, String command, String[] args) throws PermissionsException, CommandSenderException {

        if (sender instanceof Player) {
            if (!plugin.check((Player) sender, "tplist"))
                throw new PermissionsException(command);
        }

        if (plugin.getDonottplist().size() != 0) {
            sender.sendMessage(ChatColor.YELLOW + "Current do-not-tp list : ");
            String color = "";
            String msg = "";
            for (String playername : plugin.getDonottplist()) {
                Player tpp = plugin.getServer().getPlayer(playername);
                if (tpp != null) {
                    sender.sendMessage(tpp.getDisplayName());
                } else {
                    sender.sendMessage(ChatColor.AQUA + "[NC] " + ChatColor.WHITE + playername);
                }
            }
        } else {
            sender.sendMessage(ChatColor.YELLOW + "Current do-not-tp list is empty!");
        }
        return true;
    }

    @aCommand(
        aliases = {"viewdistance"},
        permissionBase = "viewdistance",
        description = "ViewDistance control!",
        section = "admin"
    )
    public boolean viewdistance(CommandSender sender, String command, String[] realargs)
        throws PermissionsException, CommandSenderException, CommandUsageException, CommandException {
        if (sender instanceof Player)
            if (!plugin.check((Player) sender, "viewdistance"))
                throw new PermissionsException(command);

        /* ArgumentParser ap = new ArgumentParser(realargs);
      String player = ap.getString("p", null);
      String[] args = ap.getUnusedArgs();

      Player victim = null;

      if(player==null && !(sender instanceof Player))
          throw new CommandUsageException("You need to give me a player if you're going to use this over the console!");

      if(player != null)
      {
          if(!player.trim().equals("")) {
              victim = plugin.findPlayerasPlayer(player);
              if(victim == null)
                  throw new CommandException("Player not found");
          }
      } else {
          victim = (Player) sender;
      }


      if(args.length==0) {
          sender.sendMessage(ChatColor.GREEN+"["+ChatColor.AQUA+"ViewDistance"+ChatColor.GREEN+"] "+ victim.getDisplayName() + ChatColor.AQUA + " : "+victim.getViewDistance());
      } else {
          if(args[0].equalsIgnoreCase("reset")) {
              sender.sendMessage(ChatColor.YELLOW+ "Resetting " + victim.getDisplayName() + ChatColor.YELLOW + "'s ViewDistance!");
              victim.resetViewDistance();
          } else {
              Integer vdist = null;
              try{
                   vdist = Integer.parseInt(args[0]);
              } catch(NumberFormatException ex) {
                  throw new CommandUsageException("I need a number, not a string!");
              }

              if(vdist < 3 || vdist > 15)
                  throw new CommandUsageException("Outside of the allowed ViewDistance limit!");

              sender.sendMessage(ChatColor.YELLOW + "Setting " +victim.getDisplayName()+ChatColor.YELLOW +"'s ViewDistance to "+vdist+"!");
              victim.setViewDistance(vdist);
          }
      }


      return true; */
        sender.sendMessage(ChatColor.GREEN + "Not working right now!");
        return true;
    }

    @aCommand(
        aliases = {"wd"},
        permissionBase = "difficulty",
        description = "World difficulty",
        section = "admin"
    )
    public boolean worlddifficulty(CommandSender sender, String command, String[] args)
        throws PermissionsException, CommandSenderException, CommandUsageException, CommandException {

        ArgumentParser ap = new ArgumentParser(args);
        String worldarg = ap.getString("w", null);
        World world = null;
        iWorld iw = null;

        if (worldarg == null)
            if (!(sender instanceof Player))
                throw new CommandSenderException("If you're going to use this from the console, at least give me a world!");
            else
                world = ((Player) sender).getWorld();
        else {
            WorldManager wm = plugin.getworldManager();
            iw = wm.getWorld(worldarg, true);
            if (iw != null) world = iw.getBukkitWorld();
            else {
                List<World> worlds = plugin.getServer().getWorlds();
                for (World w : worlds) {
                    if (w.getName().equalsIgnoreCase(worldarg))
                        world = w;
                }
            }
        }

        if (world == null) {
            sender.sendMessage(ChatColor.RED + "Can't find world!");
        } else {


            String[] _args = ap.getUnusedArgs();
            if (_args.length == 0) { /* Set difficulty! */
                if (plugin.check(sender, "difficulity.get." + world.getName())) {

                    Difficulty diff = world.getDifficulty();
                    String color = "";
                    switch (diff) {
                        case PEACEFUL:
                            color = ChatColor.GREEN.toString();
                            break;
                        case EASY:
                            color = ChatColor.DARK_GREEN.toString();
                            break;
                        case NORMAL:
                            color = ChatColor.YELLOW.toString();
                            break;
                        case HARD:
                            color = ChatColor.RED.toString();
                            break;
                        default:
                            color = ChatColor.WHITE.toString();

                    }
                    sender.sendMessage(ChatColor.YELLOW + "Difficulty level of " + world.getName() + " : " + color + diff.name().toLowerCase());
                } else {
                    sender.sendMessage(ChatColor.RED + "You don't have permission the get the difficulty of "+world.getName()+"!");
                }
            } else { /* Show current difficulty! */
                Difficulty diff = parseDifficulty(_args[0]);
                if (diff == null)
                    sender.sendMessage(ChatColor.RED + "Can't find that difficulty level!");
                else {
                    if (plugin.check(sender, "difficulity.set." + world.getName())) {
                        sender.sendMessage(ChatColor.RED + "Setting difficulty of world " + world.getName() + " to " + diff.name().toLowerCase() + "");
                        world.setDifficulty(diff);
                    } else {
                        sender.sendMessage(ChatColor.RED + "You don't have permission to set the difficulity of world " + world.getName() + "!");
                    }
                }
            }
        }


        return true;
    }

    private Difficulty parseDifficulty(String arg) {
        try { /* Try numbers first! */
            Integer d = Integer.parseInt(arg);
            return Difficulty.getByValue(d);
        } catch (NumberFormatException ex) { /* Try strings! */
            return Difficulty.valueOf(arg.toUpperCase());
        }
    }

    @aCommand(
        aliases = {"tweakcraft", "tc"},
        permissionBase = "tweakcraft",
        description = "General tweakcraftutils command",
        section = "admin"
    )
    public boolean tweakcraft(CommandSender sender, String command, String[] args)
        throws PermissionsException, CommandSenderException, CommandUsageException, CommandException {
        if (args.length > 0) {
            if (args[0].equalsIgnoreCase("version")) {
                sender.sendMessage(ChatColor.WHITE + plugin.getDescription().getName() + ": version " + ChatColor.GREEN + plugin.getDescription().getVersion());
            } else if (args[0].equalsIgnoreCase("reload")) {

                if (sender instanceof Player) {
                    if (!plugin.check((Player) sender, "reload")) {
                        sender.sendMessage(ChatColor.GREEN + "Not implemented yet!");
                        return true;
                    }
                }

                sender.sendMessage(ChatColor.GREEN + "Reloading settings,dbs and setting colors.");
                plugin.getConfigHandler().reloadConfig();
                BanHandler bh = plugin.getBanhandler();
                bh.reloadBans();
                ItemDB idb = plugin.getItemDB();
                idb.loadDataBase();
                for (Player p : plugin.getServer().getOnlinePlayers()) {
                    String name = p.getName();
                    // p.setDisplayName(plugin.getPlayerColor(name, false) + name + ChatColor.WHITE);
                    String displayName = plugin.getNickWithColors(p.getName());
                    String ldisplayname = displayName.substring(0, displayName.length() - 2);
                    p.setDisplayName(displayName);
                    if (ldisplayname.length() <= 16) {
                        try {
                            p.setPlayerListName(ldisplayname);
                        } catch (IllegalArgumentException ex) {
                            ;
                        }
                    }
                }
                WorldManager wm = plugin.getworldManager();
                for (String worldname : wm.getWorlds().keySet()) {
                    wm.loadMotd(worldname);
                }
                plugin.getPlayerListener().reloadInvisTable();
                /**
                 * This is handled by the config.reloadConfig() call.
                 */
                /* if(plugin.getConfigHandler().enablePersistence) {
                   plugin.getPlayerListener().reloadInfo();
               } */

                TweakcraftUtilsEvent reloadEvent = new TweakcraftUtilsEvent(TweakcraftUtilsEvent.Action.RELOAD);
                if (sender instanceof Player) reloadEvent.setPlayer((Player) sender);
                plugin.getServer().getPluginManager().callEvent(reloadEvent);

            } else if (args[0].equalsIgnoreCase("world")) {
                if (sender instanceof Player)
                    if (!plugin.check((Player) sender, "admin.world"))
                        throw new PermissionsException(command);
                if (args.length > 2) {
                    String modus = args[1];
                    String world = args[2];
                    String arg = args.length > 3 ? args[3] : null;
                    iWorld iw = plugin.getworldManager().getWorld(world);
                    if (modus.equalsIgnoreCase("unload")) {
                        if (iw != null) {
                            if (iw.isEnabled()) {
                                for (Player pl : iw.getBukkitWorld().getPlayers()) {
                                    pl.sendMessage(ChatColor.RED + "WORLD UNLOADING. Sending you to spawn on the first world.");
                                    pl.teleport(plugin.getServer().getWorlds().get(0).getSpawnLocation());
                                }
                                iw.setEnabled(false);
                                sender.sendMessage(ChatColor.GOLD + "Unloading world " + iw.getName());
                                plugin.getServer().unloadWorld(iw.getName(), arg == null || Boolean.parseBoolean(arg));
                            } else {
                                sender.sendMessage(ChatColor.RED + "That world wasn't enabled!");
                            }
                        } else {
                            sender.sendMessage(ChatColor.RED + "World error, is that world managed by TweakcraftUtils?");
                        }
                    } else if (modus.equalsIgnoreCase("create") || modus.equalsIgnoreCase("load")) {
                        if (arg == null) arg = "normal";
                        World.Environment env = null;
                        try {
                            env = World.Environment.valueOf(arg.toUpperCase());
                        } catch (IllegalArgumentException ex) {
                            env = World.Environment.NORMAL;
                        }
                        if (iw != null) {
                            if (iw.isEnabled()) {
                                sender.sendMessage(ChatColor.RED + "This world already is enabled!");
                            } else {
                                sender.sendMessage(ChatColor.GOLD + "Enabling world " + world + " with env " + env.name());
                                iw.loadWorld();
                                iw.setEnabled(true);
                            }
                        } else {
                            /**
                             * NEW WORLD
                             */
                            sender.sendMessage(ChatColor.GOLD + "Creating new world " + world + " with env " + env.name());
                            plugin.getworldManager().getWorlds().put(world, new TweakWorld(plugin.getworldManager(), world, env, true));
                        }
                    } else if (modus.equalsIgnoreCase("flag")) {
                        String flagset = args.length > 4 ? args[4] : null;
                        String flag = arg;
                        boolean tcworld = (iw != null);
                        World bw = null;
                        if (!tcworld) {
                            bw = plugin.getServer().getWorld(world);
                            if (bw == null)
                                throw new CommandException("World not found!");
                        } else {
                            if (!iw.isEnabled())
                                throw new CommandException("World is not enabled/active!");
                        }

                        if (flag != null) {
                            Boolean toSet = flagset != null ? Boolean.parseBoolean(flagset) : null;
                            if (flag.equalsIgnoreCase("monsters")) {
                                if (toSet != null)
                                    if (tcworld)
                                        iw.setAllowMonsters(toSet);
                                    else
                                        bw.setSpawnFlags(toSet, bw.getAllowAnimals());

                                sender.sendMessage(ChatColor.LIGHT_PURPLE + "[" + ChatColor.GOLD + (tcworld ? iw.getName() : bw.getName()) + ChatColor.LIGHT_PURPLE + "]"
                                    + " MONSTERS: " + ((tcworld ? iw.getAllowMonsters() : bw.getAllowMonsters()) ? ChatColor.GREEN + "enabled" : ChatColor.RED + "disabled"));
                            } else if (flag.equalsIgnoreCase("animals")) {
                                if (toSet != null)
                                    if (tcworld)
                                        iw.setAllowAnimals(toSet);
                                    else
                                        bw.setSpawnFlags(bw.getAllowMonsters(), toSet);

                                sender.sendMessage(ChatColor.LIGHT_PURPLE + "[" + ChatColor.GOLD + (tcworld ? iw.getName() : bw.getName()) + ChatColor.LIGHT_PURPLE + "]"
                                    + " ANIMALS: " + ((tcworld ? iw.getAllowAnimals() : bw.getAllowAnimals()) ? ChatColor.GREEN + "enabled" : ChatColor.RED + "disabled"));
                            } else if (flag.equalsIgnoreCase("pvp")) {
                                if (toSet != null)
                                    if (tcworld)
                                        iw.setPVP(toSet);
                                    else
                                        bw.setPVP(toSet);

                                sender.sendMessage(ChatColor.LIGHT_PURPLE + "[" + ChatColor.GOLD + (tcworld ? iw.getName() : bw.getName()) + ChatColor.LIGHT_PURPLE + "]"
                                    + " PVP: " + ((tcworld ? iw.getPVP() : bw.getPVP()) ? ChatColor.GREEN + "enabled" : ChatColor.RED + "disabled"));

                            } else if (flag.equalsIgnoreCase("env")) {
                                World.Environment wenv = tcworld ? iw.getBukkitWorld().getEnvironment() : bw.getEnvironment();
                                sender.sendMessage(ChatColor.LIGHT_PURPLE + "[" + ChatColor.GOLD + (tcworld ? iw.getName() : bw.getName()) + ChatColor.LIGHT_PURPLE + "]"
                                    + " ENVIRONMENT: " + wenv.name());
                            } else {
                                throw new CommandUsageException("No flag by that name found!");
                            }
                        } else {
                            throw new CommandUsageException("Set of flags to enable/disable : [monsters|animals|env]");
                        }
                    } else if (modus.equalsIgnoreCase("info")) {
                        boolean tcworld = (iw != null);
                        // sender.sendMessage("tcworld? "+tcworld);
                        World bw = null;
                        World.Environment wenv;
                        if (!tcworld) {
                            bw = plugin.getServer().getWorld(world);
                            if (bw == null)
                                throw new CommandException("World not found!");
                            else
                                wenv = bw.getEnvironment();
                        } else {
                            if (!iw.isEnabled())
                                throw new CommandException("World is not enabled/active!");

                            wenv = iw.getBukkitWorld().getEnvironment();
                        }
                        boolean monsters = tcworld ? iw.getAllowMonsters() : bw.getAllowMonsters();
                        boolean animals = tcworld ? iw.getAllowAnimals() : bw.getAllowAnimals();
                        boolean pvp = tcworld ? iw.getPVP() : bw.getPVP();
                        int amountofplayers = tcworld ? iw.getBukkitWorld().getPlayers().size() : bw.getPlayers().size();
                        boolean customChunkGen = tcworld ? iw.getChunkGen() != null : bw.getGenerator() != null;
                        boolean tooldura = tcworld ? iw.isDurabilityEnabled() : bw.getToolDurability();
                        Difficulty difficulty = tcworld ? iw.getDifficultyBukkit() : bw.getDifficulty();
                        GameMode gm = tcworld ? iw.getGameMode() : null;
                        String players = "";
                        if (amountofplayers < 5) {
                            List<Player> ps = tcworld ? iw.getBukkitWorld().getPlayers() : bw.getPlayers();
                            for (Player p : ps) {
                                players += p.getDisplayName() + ChatColor.WHITE + ",";
                            }
                            if (players.length() > 0)
                                players = players.substring(0, players.length() - 1);
                        }
                        String name = tcworld ? iw.getName() : bw.getName();
                        sender.sendMessage(ChatColor.LIGHT_PURPLE + "[" + ChatColor.GOLD + name + ChatColor.LIGHT_PURPLE + "] " + (tcworld ? "" : ChatColor.GREEN + "*"));
                        sender.sendMessage(ChatColor.RED + "ANIMALS: " + (animals ? ChatColor.GREEN + "enabled" : ChatColor.RED + "disabled"));
                        sender.sendMessage(ChatColor.RED + "MONSTERS: " + (monsters ? ChatColor.GREEN + "enabled" : ChatColor.RED + "disabled"));
                        sender.sendMessage(ChatColor.RED + "PVP: " + (pvp ? ChatColor.GREEN + "enabled" : ChatColor.RED + "disabled"));
                        sender.sendMessage(ChatColor.RED + "ENV: " + wenv);
                        sender.sendMessage(ChatColor.RED + "DIFFICULTY: " + difficulty.name().toLowerCase());
                        sender.sendMessage(ChatColor.RED + "PLAYERS: " + amountofplayers + (players.equals("") ? "" : (" (" + players + ChatColor.RED + ")")));
                        sender.sendMessage(ChatColor.RED + "TOOL DURABILITY: " + (tooldura ? ChatColor.GREEN + "enabled" : ChatColor.RED + "disabled"));
                        if (gm != null)
                            sender.sendMessage(ChatColor.RED + "GAMEMODE: " + gm.toString().toLowerCase());
                        if (customChunkGen) {
                            String ChunkGen = tcworld ? iw.getChunkGenClass() : bw.getGenerator().getClass().getName();
                            sender.sendMessage(ChatColor.RED + "CHUNKGEN: " + ChunkGen);
                        }

                    }

                } else {
                    sender.sendMessage(ChatColor.GREEN + "usage: /tc world create|unload|flag|info");
                }
            } else if (args[0].equalsIgnoreCase("improvchat")) {
                if (sender instanceof Player) {
                    Player player = (Player) sender;
                    Set<Player> lijst = plugin.getCUIPlayers();
                    if (lijst != null && !lijst.contains(player)) {
                        plugin.getLogger().info("Adding " + player.getName() + " to the CUI list!");
                        lijst.add(player);
                    }

                    plugin.sendCUIChatMode(player);
                }
            } else if (args[0].equalsIgnoreCase("tooldura")) {
                if (sender instanceof Player) {
                    Player player = (Player) sender;
                    Set<Player> lijst = plugin.getMod_InfDuraplayers();
                    if (lijst != null && !lijst.contains(player)) {
                        plugin.getLogger().info("Adding " + player.getName() + " to the mod_InfDura list!");
                        lijst.add(player);
                    }

                    plugin.sendToolDuraMode(player);
                }
            }

        } else {
            throw new CommandUsageException("/tc <" + ChatColor.GREEN + "reload" + ChatColor.YELLOW + "/" + ChatColor.GREEN + "version" + ChatColor.YELLOW + ">");
        }
        return true;
    }

}
