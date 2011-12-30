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
import com.guntherdw.bukkit.tweakcraft.Commands.CommandHandler;
import com.guntherdw.bukkit.tweakcraft.Commands.aCommand;
import com.guntherdw.bukkit.tweakcraft.DataSources.Ban.BanHandler;
import com.guntherdw.bukkit.tweakcraft.DataSources.PersistenceClass.PlayerOptions;
import com.guntherdw.bukkit.tweakcraft.Exceptions.CommandException;
import com.guntherdw.bukkit.tweakcraft.Exceptions.CommandSenderException;
import com.guntherdw.bukkit.tweakcraft.Exceptions.CommandUsageException;
import com.guntherdw.bukkit.tweakcraft.Exceptions.PermissionsException;
import com.guntherdw.bukkit.tweakcraft.Packages.Ban;
import com.guntherdw.bukkit.tweakcraft.Packages.Item;
import com.guntherdw.bukkit.tweakcraft.Packages.ItemDB;
import com.guntherdw.bukkit.tweakcraft.Packages.LocalPlayer;
import com.guntherdw.bukkit.tweakcraft.Tools.ArgumentParser;
import com.guntherdw.bukkit.tweakcraft.TweakcraftUtils;
import com.guntherdw.bukkit.tweakcraft.Util.TimeTool;
import com.guntherdw.bukkit.tweakcraft.Worlds.iWorld;
import org.bukkit.*;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;

import java.io.File;
import java.lang.reflect.Method;
import java.util.*;

/**
 * @author GuntherDW
 */
public class EssentialsCommands {

    @aCommand(
        aliases = {"ban"},
        permissionBase = "ban",
        description = "Bans a player with a reason",
        section = "essentials"
    )
    public boolean ban(CommandSender sender, String command, String[] realargs, TweakcraftUtils plugin)
        throws PermissionsException, CommandSenderException, CommandUsageException, CommandException {
        if (sender instanceof Player)
            if (!plugin.check((Player) sender, "ban"))
                throw new PermissionsException(command);
        List<Player> playerlist = null;
        BanHandler handler = plugin.getBanhandler();

        ArgumentParser ap = new ArgumentParser(realargs);
        boolean exact = ap.isflagUsed("e");
        String durationarg = ap.getString("t", null);
        String[] args = ap.getUnusedArgs();

        if (args.length < 1)
            throw new CommandUsageException(ChatColor.YELLOW + "I need at least 1 name to ban!");

        String playername = args[0];

        if (!exact) {
            sender.sendMessage(ChatColor.LIGHT_PURPLE + "PlayerFinder enabled.");
            playerlist = plugin.getServer().matchPlayer(playername);
            if (playerlist.size() == 1) {
                playername = playerlist.get(0).getName();
                sender.sendMessage(ChatColor.YELLOW + "Found " + playername);
            } else {
                throw new CommandException("Didn't find the player, cancelling!");
            }
        }

        if (handler.isBanned(playername)) {
            sender.sendMessage(ChatColor.YELLOW + "This player is already banned!");
        } else {
            String reason = "";
            String duration = null;
            Long dura = null;

            String toFull = null;
            if (args.length > 1) {
                if (durationarg != null) {
                    duration = args[1].substring(2);
                    dura = TimeTool.calcTime(duration);
                    toFull = TimeTool.getDurationFull(duration);
                    duration = duration.substring(0, duration.length() - 1);
                }
                for (int x = 1; x < args.length; x++) {
                    reason += args[x] + " ";
                }
                if (reason.length() > 1)
                    reason = reason.substring(0, reason.length() - 1);
            }
            if (dura != null && !plugin.getConfigHandler().enablePersistence) {
                throw new CommandUsageException("ERROR: For timed bans to work, persistence HAS to be enabled!");
            }

            handler.banPlayer(playername.toLowerCase(), reason, dura);
            sender.sendMessage(ChatColor.YELLOW + "Banning " + playername + ChatColor.YELLOW + (dura != null ? " for " + duration + " " + toFull + "!" : ""));

            Player player = plugin.getServer().getPlayerExact(playername);
            if (player != null) {
                sender.sendMessage(ChatColor.YELLOW + "Kickbanning " + player.getName());
                player.kickPlayer(reason);
            }
            plugin.getLogger().info("[TweakcraftUtils] Banning " + playername + "!");
            handler.saveBans();
        }
        return true;
    }

    @aCommand(
        aliases = {"banlist"},
        permissionBase = "ban",
        description = "Lists the current bans",
        section = "essentials"
    )
    public boolean banlist(CommandSender sender, String command, String[] args, TweakcraftUtils plugin)
        throws PermissionsException, CommandSenderException, CommandUsageException, CommandException {
        if (sender instanceof Player)
            if (!plugin.check((Player) sender, "ban"))
                throw new PermissionsException(command);

        String banned = "";
        if (args.length > 0) {
            String tofind = args[0];
            Ban ban = plugin.getBanhandler().isBannedBan(tofind);
            if (ban != null) {
                banned = ChatColor.YELLOW + tofind + " is still banned for " + plugin.getBanhandler().getRemainingTime(tofind);
            } else {
                banned = ChatColor.YELLOW + tofind + " isn't banned!";
            }
        } else {

            String banmsg = ChatColor.YELLOW + "Currently banned players : ";
            sender.sendMessage(banmsg);
            banned = "";

            for (String banName : plugin.getBanhandler().getBans().keySet()) {
                banned += banName + " ";
            }
            if (banned.length() > 1)
                banned = banned.substring(0, banned.length() - 1);

        }
        sender.sendMessage(banned);

        return true;
    }

    @aCommand(
        aliases = {"compass"},
        permissionBase = "",
        description = "Shows your current orientation",
        section = "essentials"
    )
    public boolean compass(CommandSender sender, String command, String[] args, TweakcraftUtils plugin)
        throws PermissionsException, CommandSenderException, CommandUsageException, CommandException {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            String dir = plugin.getCompassDirection(player.getLocation().getYaw());

            sender.sendMessage(ChatColor.YELLOW + "Current looking direction : " + dir);
        } else {
            throw new CommandSenderException("Now why would a console want to know its position?");
        }
        return true;
    }

    @aCommand(
        aliases = {"getpos"},
        permissionBase = "getpos",
        description = "Shows your current position",
        section = "essentials"
    )
    public boolean getpos(CommandSender sender, String command, String[] args, TweakcraftUtils plugin)
        throws PermissionsException, CommandSenderException, CommandUsageException, CommandException {
        if (sender instanceof Player)
            if (!plugin.check((Player) sender, "getpos"))
                throw new PermissionsException(command);
        Location loc = null;

        /**
         * Permissions checking!
         */
        if (args.length == 0 && !(sender instanceof Player))
            throw new CommandUsageException("If you're going to call this from a console i need a player to check!");
        if (args.length == 0 && sender instanceof Player)
            loc = ((Player) sender).getLocation().clone();
        if (args.length > 0 && sender instanceof Player)
            if (!plugin.check((Player) sender, "getpos.other"))
                throw new PermissionsException(command);

        if (args.length > 0) {
            List<Player> plist = plugin.getServer().matchPlayer(args[0]);
            if (plist.size() == 0) {
                throw new CommandUsageException(ChatColor.YELLOW + "Can't find player!");
            } else if (plist.size() > 1) {
                throw new CommandUsageException(ChatColor.YELLOW + "Can't match player, got more than one result!");
            } else {
                loc = plist.get(0).getLocation().clone();
            }
        }

        if (loc != null) {
            Integer x, y, z, yaw, pitch;
            x = Math.round((float) loc.getX());
            y = Math.round((float) loc.getY());
            z = Math.round((float) loc.getZ());
            yaw = Math.round((float) loc.getYaw());
            pitch = Math.round((float) loc.getPitch());

            sender.sendMessage(ChatColor.YELLOW + "Pos X: " + x + " Y: " + y + " Z: " + z);
            sender.sendMessage(ChatColor.YELLOW + "Rotation: " + yaw + " Pitch: " + pitch);
            sender.sendMessage(ChatColor.YELLOW + "World: " + loc.getWorld().getName());
            String dir = plugin.getCompassDirection(loc.getYaw());
            double degreeRotation = ((loc.getYaw() - 90) % 360);
            if (degreeRotation < 0)
                degreeRotation += 360.0;

            sender.sendMessage(ChatColor.YELLOW + "Compass: " + dir + " (" + (Math.round(degreeRotation * 10) / 10.0) + ")");
        } else {
            throw new CommandException("This isn't supposed to happen!");
        }
        return true;
    }

    @SuppressWarnings("unchecked")
    @aCommand(
        aliases = {"help"},
        permissionBase = "help",
        description = "What you're reading right now",
        section = "essentials"
    )
    public boolean help(CommandSender sender, String command, String[] args, TweakcraftUtils plugin)
        throws PermissionsException, CommandSenderException, CommandUsageException, CommandException {

        List<String> cma = new ArrayList<String>();
        CommandHandler commh = plugin.getCommandHandler();

        boolean aliases = true;
        String toadd = "";
        for (String cname : commh.getCommandMap().keySet()) {
            Method commandMethod = commh.getCommandMap().get(cname);
            // aCommand annotation = commandMethod.getAnnotation(aCommand.class);

            if (addCommandToList(sender, commandMethod, plugin)) {
                // Fuck minecraft's font :<
                toadd = ChatColor.GOLD + cname + ChatColor.WHITE +
                    " : " + ChatColor.YELLOW + plugin.getCommand(cname).getDescription();
                if (aliases) {
                    List<String> aliaseslist = plugin.getCommand(cname).getAliases();
                    if (aliaseslist.size() > 0) {
                        toadd += ChatColor.WHITE + " (";
                        for (String alias : aliaseslist) {
                            toadd += ChatColor.GOLD + alias + ChatColor.WHITE + ",";
                        }
                        toadd = toadd.substring(0, toadd.length() - 1);
                        toadd += ")";
                    }

                }
                cma.add(toadd);
            }
        }
        /**
         * Extra plugins
         */
        for (String plug : plugin.getConfigHandler().extrahelpplugin) {
            if (plugin.getServer().getPluginManager().getPlugin(plug) != null) {

                PluginDescriptionFile pdesc = plugin.getServer().getPluginManager().getPlugin(plug).getDescription();
                Map<String, Map<String, Object>> cmds = (Map<String, Map<String, Object>>) pdesc.getCommands();
                for (String cmd : cmds.keySet()) {

                    if (!plugin.getConfigHandler().extrahelphide.contains(cmd)) {
                        String perm = (String) cmds.get(cmd).get("_permission");
                        if (perm == null)
                            perm = (String) cmds.get(cmd).get("permissions"); // Added for WorldEdit support
                        if (addExtCommandToList(sender, perm, plugin)) {
                            toadd = ChatColor.GOLD + cmd + ChatColor.WHITE +
                                " : " + ChatColor.YELLOW + ((String) cmds.get(cmd).get("description"));
                            if (aliases) {
                                List<String> aliaseslist = (List<String>) cmds.get(cmd).get("aliases");
                                // toadd += ChatColor.WHITE+" ("+ChatColor.GOLD+aliaseslist+ChatColor.WHITE+")";
                                if (aliaseslist != null && aliaseslist.size() > 0) {
                                    toadd += ChatColor.WHITE + " (";
                                    for (String alias : aliaseslist) {
                                        toadd += ChatColor.GOLD + alias + ChatColor.WHITE + ",";
                                    }
                                    toadd = toadd.substring(0, toadd.length() - 1);
                                    toadd += ")";
                                }
                            }
                            cma.add(toadd);
                        }
                    }
                }
            } else {
                plugin.getLogger().info("[TweakcraftUtils] EXTRAHELP error : " + plug + " is null!");
            }
        }

        Double dmaxPage = Math.ceil((double) cma.size() / plugin.getConfigHandler().helpPerPage);
        int maxPage = dmaxPage.intValue();
        int hpp = plugin.getConfigHandler().helpPerPage;
        Integer pagereq = 0;
        if (args.length > 0) {
            try {
                pagereq = Integer.parseInt(args[0]) - 1;
            } catch (NumberFormatException ex) {
                pagereq = 0;
            }
        }
        if (pagereq.intValue() < 0 || pagereq.intValue() > maxPage - 1) {
            throw new CommandUsageException("Invalid page number!");
        }

        sender.sendMessage(ChatColor.AQUA + "Commands available to you : Page " + (pagereq + 1) + "/" + maxPage);
        int start = pagereq.intValue() * hpp;
        int end = start + hpp;

        for (int x = start; x < end && x < cma.size(); x++) {
            sender.sendMessage(cma.get(x));
        }

        return true;
    }

    protected boolean addCommandToList(CommandSender sender, Method command, TweakcraftUtils plugin) {
        if (sender instanceof Player) {
            aCommand annotation = command.getAnnotation(aCommand.class);
            if (annotation == null) {
                return true;
            } else {
                return plugin.check((Player) sender, annotation.permissionBase());
            }
        } else {
            return true;
        }
    }

    protected boolean addExtCommandToList(CommandSender sender, String perm, TweakcraftUtils plugin) {
        if (sender instanceof Player) {
            if (perm == null) {
                return true;
            } else {
                return plugin.checkfull((Player) sender, perm);
            }
        } else {
            return true;
        }
    }

    @aCommand(
        aliases = {"item", "i", "give"},
        permissionBase = "item",
        description = "Give yourself an item of choice",
        section = "essentials"
    )
    public boolean item(CommandSender sender, String command, String[] realargs, TweakcraftUtils plugin)
        throws PermissionsException, CommandSenderException, CommandUsageException, CommandException {

        if (sender instanceof Player)
            if (!plugin.check((Player) sender, "item"))
                throw new PermissionsException(command);

        ItemDB db = plugin.getItemDB();
        Item item;

        Player receiver = null;
        Integer itemId = null;
        Byte itemDmg = 0;
        Integer itemAmount = null;

        ArgumentParser ap = new ArgumentParser(realargs);
        String recv = ap.getString("p", null);
        int dmgval = ap.getInteger("d", -1);
        int dataval = ap.getInteger("dat", -1);
        String[] args = ap.getUnusedArgs();

        if (args.length > 0) // just the item!
        {
            String[] split = args[0].split(":");
            if (split.length > 1) {
                try {
                    itemId = Integer.parseInt(split[0]);
                    itemDmg = Byte.parseByte(split[1]);
                } catch (NumberFormatException e) {
                    item = db.getItem(split[0]);
                    itemId = item.getItemnumber();
                    itemDmg = item.getDamage();
                    itemAmount = item.getDefaultstack();
                }
            } else {
                try {
                    itemId = Integer.parseInt(split[0]);
                } catch (NumberFormatException e) {
                    item = db.getItem(split[0]);
                    if (item != null) {
                        itemId = item.getItemnumber();
                        itemDmg = item.getDamage();
                        itemAmount = item.getDefaultstack();
                    } else {
                        throw new CommandException("Can't find item!");
                    }
                }
            }
            if (args.length > 1) { // set Amount
                itemAmount = Integer.parseInt(args[1]);
            } else {
                if (itemAmount == null)
                    itemAmount = 64;
            }

            /* if (args.length > 2) { // set Receiver
               receiver = plugin.getServer().getPlayer(plugin.findPlayer(args[2]));
               if (receiver == null) {
                   throw new CommandUsageException("Can't find the other player!");
               }
           } else {
               if (sender instanceof Player)
                   receiver = (Player) sender;
               else
                   throw new CommandUsageException("If you're a console you have to specify the receiver!");

           } */

            if (dmgval > -1 && dmgval < 256) {
                itemDmg = (byte) dmgval;
            }

            if (recv != null) {
                receiver = plugin.getServer().getPlayer(plugin.findPlayer(recv));
                if (receiver == null) {
                    throw new CommandUsageException("Can't find the other player!");
                }
            } else {
                if (sender instanceof Player)
                    receiver = (Player) sender;
                else
                    throw new CommandUsageException("If you're a console you have to specify the receiver!");
            }

            boolean isValid = false;
            Material mat = Material.getMaterial(itemId);
            // if(ItemType.class == null) {
            isValid = mat != null;
            /* } else {
               isValid = ItemType.isValid(itemId);
           } */

            if (isValid) {
                String recvname = "";
                String giftfrom = "";
                if (sender instanceof Player) {
                    recvname = receiver.getDisplayName();
                    giftfrom = ((Player) sender).getName();
                } else {
                    recvname = receiver.getName();
                    giftfrom = "CONSOLE";
                }
                String itemName = mat.toString().toLowerCase().replace('_', ' ');

                sender.sendMessage(ChatColor.YELLOW + "Giving " + recvname + ChatColor.YELLOW + " " + itemAmount + " of " + itemName + "!");
                if (!(receiver.getName().equals(giftfrom)))
                    receiver.sendMessage(ChatColor.AQUA + "Enjoy your gift! :3");
                ItemStack stack = new ItemStack(itemId, itemAmount, itemDmg.shortValue());
                if (dataval != -1)
                    stack.setData(Material.getMaterial(itemId).getNewData((byte) dataval));

                receiver.getInventory().addItem(stack);
                plugin.getLogger().info("[TweakcraftUtils] " + giftfrom + " gave " + recvname + " " + itemAmount + "x" + itemId + " (" + itemDmg.intValue() + ")");
            } else {
                throw new CommandUsageException("Specified item is not valid!");
            }
        }
        return true;
    }


    @aCommand(
        aliases = {"kick"},
        permissionBase = "kick",
        description = "Kicks a player with reason",
        section = "essentials"
    )
    public boolean kick(CommandSender sender, String command, String[] args, TweakcraftUtils plugin)
        throws PermissionsException, CommandSenderException, CommandUsageException, CommandException {
        if (sender instanceof Player)
            if (!plugin.check((Player) sender, "kick"))
                throw new PermissionsException(command);

        String reason = "";
        Player player;
        String kicker = "";
        if (sender instanceof Player) {
            kicker = plugin.getNick(((Player) sender).getName());
        } else {
            kicker = "CONSOLE";
        }
        if (args.length > 0) // No reason set!
        {
            String p = plugin.findPlayer(args[0]);
            player = plugin.getServer().getPlayer(p);
            if (player == null)
                throw new CommandUsageException("Can't find player!");
            if (args.length > 1) // Reason given!
            {
                for (int x = 1; x < args.length; x++) {
                    reason += args[x] + " ";
                }
                reason = reason.trim();
                if (!(reason.length() > 0))
                    reason = "No reason given!";

            }
            player.kickPlayer(kicker + ": " + reason);
        }

        return true;
    }

    @aCommand(
        aliases = {"listworlds", "lw"},
        permissionBase = "worlds",
        description = "Lists the currently active worlds",
        section = "essentials"
    )
    public boolean listworlds(CommandSender sender, String command, String[] args, TweakcraftUtils plugin)
        throws PermissionsException, CommandSenderException, CommandUsageException, CommandException {
        if (sender instanceof Player) // Give the player a list of worlds he has access to!
        {
            Player player = (Player) sender;
            player.sendMessage(ChatColor.LIGHT_PURPLE + "Listing currently accessible worlds : ");

            // String message = "";
            String col;

            for (World w : plugin.getServer().getWorlds()) {
                if (plugin.check(player, "worlds." + w.getName())) {

                    World.Environment env = w.getEnvironment();
                    boolean customChunkGen = false;
                    if (w.getGenerator() != null) {
                        customChunkGen = true;
                        env = null;
                    }
                    iWorld tw = plugin.getworldManager().getWorld(w.getName());
                    if (tw != null) {
                        if (tw.getChunkGen() != null)
                            env = null;
                    }
                    if (env == null)
                        col = ChatColor.GRAY.toString();
                    else if (env == World.Environment.NORMAL)
                        col = ChatColor.GREEN.toString();
                    else if (env == World.Environment.NETHER)
                        col = ChatColor.RED.toString();
                    else if (env == World.Environment.THE_END)
                        col = ChatColor.DARK_GRAY.toString();
                    else
                        col = ChatColor.GRAY.toString();
                    player.sendMessage(ChatColor.LIGHT_PURPLE + "(" + w.getPlayers().size() + ") " + col + w.getName() +
                        (customChunkGen ? ChatColor.LIGHT_PURPLE + " (CG:" + w.getGenerator().getClass().getSimpleName() + ")" : ""));
                }
            }

            player.sendMessage(ChatColor.LIGHT_PURPLE + "Legend: " + ChatColor.RED + "NETHER" + ChatColor.LIGHT_PURPLE + "," +
                ChatColor.GREEN + " NORMAL" + ChatColor.LIGHT_PURPLE + "," + ChatColor.AQUA + " SKYLANDS" + ChatColor.LIGHT_PURPLE + "," + ChatColor.GRAY + " CUSTOM/OTHER");
            player.sendMessage(ChatColor.LIGHT_PURPLE + "Warp to a world by issuing /world <worldname>");


        } else { // The console just needs a list!
            sender.sendMessage("Currently enabled worlds : ");
            String message = "";
            for (World w : plugin.getServer().getWorlds()) {
                message += w.getName() + " (" + w.getPlayers().size() + "), ";
            }
            if (message.length() > 1)
                message = message.substring(0, message.length() - 2);
            sender.sendMessage(message);
        }
        return true;
    }

    @aCommand(
        aliases = {"me"},
        permissionBase = "",
        description = "Broadcasts a message in third person",
        section = "essentials"
    )
    public boolean me(CommandSender sender, String command, String[] args, TweakcraftUtils plugin)
        throws PermissionsException, CommandSenderException, CommandUsageException, CommandException {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            ChatHandler ch = plugin.getChathandler();
            if (!ch.canTalk(player.getName())) {
                sender.sendMessage(ChatColor.RED + "What were you trying to do?");
            } else {
                if (args.length > 0) {
                    ChatMode cm = ch.getPlayerChatMode(player);
                    String msg = "";
                    for (String m : args)
                        msg += m + " ";
                    msg = msg.substring(0, msg.length() - 1);

                    if (cm == null) {
                        if (plugin.getPlayerListener().getInvisplayers().contains(player.getName())) {
                            player.sendMessage(ChatColor.AQUA + "Are you crazy? set a chatmode first!");
                        } else {
                            plugin.getServer().broadcastMessage("* " + player.getDisplayName() + " " + msg);
                        }
                    } else if (cm instanceof AdminChat) {
                        cm.broadcastMessage(player, "[" + ChatColor.GREEN + "A" + ChatColor.WHITE + "] * " + player.getDisplayName() + " " + ChatColor.GREEN + msg);
                    } else {
                        cm.broadcastMessage(player, "[" + cm.getPrefix() + ChatColor.WHITE + "] * " + player.getDisplayName() + " " + msg);
                    }
                }

            }
        } else {
            throw new CommandSenderException("Now why on earth...");
        }
        return true;
    }

    @aCommand(
        aliases = {"motd"},
        permissionBase = "motd",
        description = "Shows the message of the day!",
        section = "essentials"
    )
    public boolean motd(CommandSender sender, String command, String[] args, TweakcraftUtils plugin)
        throws PermissionsException, CommandSenderException, CommandUsageException, CommandException {

        if (args.length == 1 && args[0].equalsIgnoreCase("reload")) {
            if (sender instanceof Player)
                if (!(plugin.check((Player) sender, "motdreload")))
                    throw new PermissionsException(command);

            plugin.reloadMOTD();
            sender.sendMessage(ChatColor.YELLOW + "Reloading MOTD");
        } else {
            if (sender instanceof Player)
                if (!(plugin.check((Player) sender, "motd")))
                    throw new PermissionsException(command);

            for (String motdline : plugin.getMOTD()) {
                sender.sendMessage(motdline);
            }
        }
        return true;
    }

    @aCommand(
        aliases = {"msg", "tell", "whisper"},
        permissionBase = "msg",
        description = "Send a message to a player",
        section = "essentials"
    )
    public boolean msg(CommandSender sender, String command, String[] args, TweakcraftUtils plugin)
        throws PermissionsException, CommandSenderException, CommandUsageException, CommandException {

        String senderName = "";
        String clearName = "";
        if (sender instanceof Player) {
            clearName = ((Player) sender).getName();
            senderName = ((Player) sender).getDisplayName();
        } else {
            clearName = "CONSOLE";
            senderName = ChatColor.LIGHT_PURPLE + "CONSOLE" + ChatColor.WHITE;
        }

        if (args.length > 1) {
            Player playerto = plugin.findPlayerasPlayer(args[0]);

            String message = "";
            for (int x = 1; x < args.length; x++) {
                message += args[x] + (x < args.length ? " " : "");
            }
            if (playerto == null)
                throw new CommandException("Can't find that player!");

            sender.sendMessage("[Me -> " + playerto.getDisplayName() + "] " + message);
            playerto.sendMessage("[" + senderName + " -> Me] " + message);
            if (sender instanceof Player) {
                Player player = (Player) sender;
                LocalPlayer lp = plugin.wrapPlayer(playerto);
                lp.setReplyTo(player.getName());
                // plugin.setPlayerReply(playerto.getName(), ((Player) sender).getName());
            }

            plugin.getLogger().info("[TweakcraftUtils] (MSG) " + clearName + " -> " + playerto.getName() + " : " + message);
        } else if (args.length == 1) {
            throw new CommandUsageException("I need a message!");
        } else {
            throw new CommandUsageException("I need a player!");
        }

        return true;
    }

    @aCommand(
        aliases = {"mute"},
        permissionBase = "mute",
        description = "Mute a player",
        section = "essentials"
    )
    public boolean mute(CommandSender sender, String command, String[] args, TweakcraftUtils plugin)
        throws PermissionsException, CommandSenderException, CommandUsageException, CommandException {
        if (sender instanceof Player)
            if (!plugin.check((Player) sender, "mute"))
                throw new PermissionsException(command);
        Long dura = null;
        String toFull = null;
        ChatHandler ch = plugin.getChathandler();
        if (args.length == 1 && args[0].equalsIgnoreCase("list")) {
            sender.sendMessage(ChatColor.LIGHT_PURPLE + "Current list of muted players : ");
            if (ch.getMutedPlayers() == null || ch.getMutedPlayers().size() == 0) {
                sender.sendMessage(ChatColor.LIGHT_PURPLE + "List is empty!");
            } else {
                for (String s : ch.getMutedPlayers().keySet()) {
                    if (!ch.canTalk(s)) {
                        Long secsremain = ch.getRemaining(s);
                        String rem = (secsremain == null ? "forever" : (TimeTool.calcLeft(secsremain)));
                        sender.sendMessage(ChatColor.GOLD + s + ChatColor.WHITE + " - " + ChatColor.GOLD + rem);
                    }
                }
            }
        } else if (args.length > 0) {
            String playername = plugin.findPlayer(args[0]);
            String duration = null;
            if (args.length > 1 && args[1].startsWith("t:")) {
                duration = args[1].substring(2);
                dura = TimeTool.calcTime(duration);
                toFull = TimeTool.getDurationFull(duration);
                duration = duration.substring(0, duration.length() - 1);
            }
            Player player = plugin.getServer().getPlayer(playername);
            if (player != null) {

                if (ch.canTalk(playername) || dura != null) {
                    sender.sendMessage(ChatColor.YELLOW + "Muting " + player.getDisplayName() + ChatColor.YELLOW + (dura != null ? " for " + duration + " " + toFull + "!" : ""));
                    ch.addMute(player.getName().toLowerCase(), dura);
                } else {
                    sender.sendMessage(ChatColor.YELLOW + "Unmuting " + player.getDisplayName());
                    ch.removeMute(player.getName().toLowerCase());
                }
            } else {
                sender.sendMessage(ChatColor.YELLOW + "Can't find player!");
            }
        } else {
            sender.sendMessage(ChatColor.YELLOW + "Now who on earth do i have to mute?");
        }

        return true;
    }

    @aCommand(
        aliases = {"plugin"},
        permissionBase = "plugin",
        description = "General plugin command",
        section = "essentials"
    )
    public boolean plugin(CommandSender sender, String command, String[] args, TweakcraftUtils plugin)
        throws PermissionsException, CommandSenderException, CommandUsageException, CommandException {
        if (sender instanceof Player)
            if (!plugin.check((Player) sender, "plugins"))
                throw new PermissionsException(command);

        if (args.length > 0) {
            String pluginname = "";
            for (int x = 1; x < args.length; x++) {
                pluginname += args[x] + " ";
            }
            if (pluginname.length() > 1)
                pluginname = pluginname.substring(0, pluginname.length() - 1);

            if (args[0].equalsIgnoreCase("list")) {
                sender.sendMessage("Current list of plugins : ");
                String message = "";
                for (Plugin p : plugin.getServer().getPluginManager().getPlugins()) {
                    if (p.isEnabled())
                        message += ChatColor.GREEN + p.getDescription().getName() + " ";
                    else
                        message += ChatColor.GRAY + p.getDescription().getName() + " ";
                }
                if (message.length() > 0)
                    message = message.substring(0, message.length() - 1);
                sender.sendMessage(message);
            } else if (args[0].equalsIgnoreCase("reload")) {
                sender.sendMessage(ChatColor.YELLOW + "Reloading " + pluginname);
                Plugin p = plugin.getServer().getPluginManager().getPlugin(pluginname);
                plugin.getServer().getPluginManager().disablePlugin(p);
                plugin.getServer().getPluginManager().enablePlugin(p);

            } else if (args[0].equalsIgnoreCase("load")) {
                sender.sendMessage(ChatColor.YELLOW + "Loading " + pluginname);
                File plug = new File("plugins", pluginname + ".jar");
                Plugin p = null;
                try {
                    p = plugin.getServer().getPluginManager().loadPlugin(plug);
                } catch (Exception e) {
                    e.printStackTrace();
                    throw new CommandException("Exception thrown while loading a plugin!");
                }
                if (p == null) {
                    throw new CommandException("Can't load the plugin!");
                }

                if (!p.isEnabled()) {
                    plugin.getServer().getPluginManager().enablePlugin(p);
                }
            } else if (args[0].equalsIgnoreCase("enable")) {
                sender.sendMessage(ChatColor.YELLOW + "Enabling " + pluginname);
                Plugin p = plugin.getServer().getPluginManager().getPlugin(pluginname);
                if (!p.isEnabled()) {
                    plugin.getServer().getPluginManager().enablePlugin(p);
                } else {
                    sender.sendMessage("This plugin was already enabled!");
                }
            } else if (args[0].equalsIgnoreCase("disable")) {
                sender.sendMessage(ChatColor.YELLOW + "Disabling " + pluginname);
                Plugin p = plugin.getServer().getPluginManager().getPlugin(pluginname);
                if (!p.isEnabled()) {
                    sender.sendMessage("This plugin was already disabled!");
                } else {
                    plugin.getServer().getPluginManager().disablePlugin(p);
                }
            }
        } else {
            throw new CommandUsageException("I need at least 1 argument!");
        }
        return true;
    }

    @aCommand(
        aliases = {"reply", "r", "re"},
        permissionBase = "msg",
        description = "Reply to the last msg",
        section = "essentials"
    )
    public boolean reply(CommandSender sender, String command, String[] args, TweakcraftUtils plugin)
        throws PermissionsException, CommandSenderException, CommandUsageException, CommandException {
        if (sender instanceof Player) {
            if (args.length > 0) {
                Player player = (Player) sender;
                LocalPlayer lp = plugin.wrapPlayer(player);
                String replyTo = lp.getReplyTo();
                String message = "";

                if (replyTo == null)
                    throw new CommandException("Can't find the player to reply to!");

                for (int x = 0; x < args.length; x++) {
                    message += args[x] + (x < args.length ? " " : "");
                }

                LocalPlayer lpTo = plugin.wrapPlayer(replyTo);
                Player playerto = lpTo.getBukkitPlayerSafe();
                if (playerto == null)
                    throw new CommandException("That player is no longer online!");

                sender.sendMessage("[Me -> " + playerto.getDisplayName() + "] " + message);
                playerto.sendMessage("[" + player.getDisplayName() + " -> Me] " + message);
                lpTo.setReplyTo(player.getName());
                // plugin.setPlayerReply(playerto.getName(), player.getName());
                plugin.getLogger().info("[TweakcraftUtils] (MSG) " + player.getName() + " -> " + playerto.getName() + " : " + message);
            } else if (args.length == 0) {
                throw new CommandUsageException("I need a message!");
            }
        } else {
            throw new CommandSenderException("Wait what do you want to do now?");
        }
        return true;
    }

    @aCommand(
        aliases = {"setspawn"},
        permissionBase = "setspawn",
        description = "Sets the spawn of the current world",
        section = "essentials"
    )
    public boolean setSpawn(CommandSender sender, String command, String[] args, TweakcraftUtils plugin)
        throws PermissionsException, CommandSenderException, CommandUsageException, CommandException {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (!plugin.check(player, "setspawn"))
                throw new PermissionsException(command);

            World world = player.getWorld();
            Location loc = player.getLocation().clone();
            if (!world.setSpawnLocation((int) loc.getX(), (int) loc.getY(), (int) loc.getZ())) {
                throw new CommandException("Something went wrong setting the spawn location for this world!");
            } else {
                sender.sendMessage(ChatColor.YELLOW + "Successfully set the spawn location for this world!");
            }

        } else {
            throw new CommandSenderException("What do you think you are doing?");
        }
        return true;
    }

    @aCommand(
        aliases = {"spawn"},
        permissionBase = "spawn",
        description = "Sends you to the spawn of the world you're in",
        section = "essentials"
    )
    public boolean spawn(CommandSender sender, String command, String[] args, TweakcraftUtils plugin)
        throws PermissionsException, CommandSenderException, CommandUsageException, CommandException {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (!plugin.check(player, "spawn"))
                throw new PermissionsException(command);

            Location loc = player.getLocation();
            boolean success = player.teleport(player.getWorld().getSpawnLocation());
            if (success) {
                plugin.getTelehistory().addHistory(player.getName(), loc);
                sender.sendMessage(ChatColor.YELLOW + "Teleporting you to spawn!");
            } else {
                sender.sendMessage(ChatColor.RED + "Failed to teleport you to spawn!");
            }
        } else {
            throw new CommandSenderException("What do you think you are doing?");
        }
        return true;
    }

    @aCommand(
        aliases = {"spawnmob"},
        permissionBase = "spawnmob",
        description = "Spawns mobs at your crosshair",
        section = "essentials"
    )
    public boolean spawnMob(CommandSender sender, String command, String[] realargs, TweakcraftUtils plugin)
        throws PermissionsException, CommandSenderException, CommandUsageException, CommandException {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (!plugin.check(player, "spawnmob"))
                throw new PermissionsException(command);

            Location loc = player.getTargetBlock((HashSet<Byte>) null, 200).getLocation();
            ArgumentParser ap = new ArgumentParser(realargs);

            Random rnd = new Random();

            int slimesize = ap.getInteger("s", -1);
            int health = ap.getInteger("h", -1);
            boolean powered = ap.getBoolean("p", false);
            boolean shoven = ap.getBoolean("sh", false);
            String sheepcolor = ap.getString("sc", null);
            int age = ap.getInteger("a", -1);
            String[] args = ap.getUnusedArgs();

            loc.setY(loc.getY() + 1); // Do not spawn them into the ground, silly!
            String mobName;
            String mobRider;
            Integer amount = 1;
            String victim = null;
            CreatureType type = null;
            CreatureType rider = null;
            Player victimplayer = player;
            LivingEntity lent = null;
            List<CreatureType> riders = null;

            if (args.length > 0) // only a mobname!
            {
                mobName = "";
                amount = 1;
                if (args[0].length() > 2)
                    mobName = args[0].substring(0, 1).toUpperCase() + args[0].substring(1, args[0].length());
                type = CreatureType.fromName(mobName);
                if (type == null) {
                    sender.sendMessage("Tried : " + mobName);
                    throw new CommandUsageException("Can't find that creature!");
                }
                if (args.length > 1) // amount
                {
                    try {
                        amount = Integer.parseInt(args[1]);
                        if (amount > 100) {
                            amount = 100; // This should be more than enough before your client starts to lag!
                        }
                    } catch (NumberFormatException e) {
                        throw new CommandUsageException("I need an amount, not a string!");
                    }
                }
                if (args.length > 2) // Riders and/or player!
                {
                    riders = new ArrayList<CreatureType>();
                    CreatureType tmpRider;
                    String tmpRiderString;
                    for (int x = 2; x < args.length; x++) {

                        if (x == args.length - 1) {
                            // Check to see if the last argument contains a playername
                            victim = plugin.findPlayer(args[args.length - 1]);
                            victimplayer = plugin.getServer().getPlayer(victim);
                            /* if (victimplayer == null) {
                              throw new CommandUsageException("Can't find that player!");
                           } */
                            if (victimplayer != null) {

                                loc = victimplayer.getLocation();
                            }
                        }
                        if (x < args.length - 1 ||
                            (x == args.length - 1 && victimplayer == null)) {
                            tmpRiderString = args[x];
                            if (args[x].length() > 2)
                                tmpRiderString = args[x].substring(0, 1).toUpperCase() + args[x].substring(1, args[x].length());
                            rider = CreatureType.fromName(tmpRiderString);
                            if (rider == null) {
                                sender.sendMessage(ChatColor.YELLOW + "Didn't find one of the specified extra riders!");
                                // throw new CommandUsageException("Can't find rider creature!");
                            } else {
                                riders.add(rider);
                            }
                        }
                    }

                }

                // if (args.length > 3) // victim!
                // {

                // }

                // We're finally here
                // Creature crea = new
                if (victimplayer == null)
                    victimplayer = player;

                if (type != null) {
                    LivingEntity rid = null;
                    for (int x = 0; x < amount; x++) {
                        lent = victimplayer.getWorld().spawnCreature(loc, type);
                        if (health > 0)
                            lent.setHealth(health);

                        if (lent instanceof Animals && age != -1)
                            ((Animals) lent).setAge(age);

                        if (lent instanceof Slime && slimesize > 0)
                            ((Slime) lent).setSize(slimesize);

                        if (lent instanceof Creeper && powered)
                            ((Creeper) lent).setPowered(powered);

                        if (lent instanceof Sheep && shoven || sheepcolor != null) {
                            ((Sheep) lent).setSheared(shoven);

                            if (sheepcolor != null) {
                                DyeColor dc = null;
                                if (sheepcolor.equalsIgnoreCase("random")) {
                                    dc = DyeColor.getByData((byte) rnd.nextInt(16));
                                } else {
                                    dc = DyeColor.valueOf(sheepcolor.toUpperCase());
                                }
                                if (dc != null) ((Sheep) lent).setColor(dc);
                            }
                        }

                        if (riders != null && riders.size() != 0) {
                            for (CreatureType t : riders) {
                                rid = victimplayer.getWorld().spawnCreature(loc, t);
                                if (lent != null) lent.setPassenger(rid);
                                lent = rid; // This makes the currently added mob the new "to-ride-along" mob
                            }
                        }
                    }
                } else {
                    sender.sendMessage(ChatColor.YELLOW + "Error trying to spawn creature!");
                }
            }
        } else {
            throw new CommandSenderException("What were you trying to do? :3");
        }
        return true;
    }

    @aCommand(
        aliases = {"time", "settime"},
        permissionBase = "time",
        description = "Sets the time",
        section = "essentials"
    )
    public boolean time(CommandSender sender, String command, String[] args, TweakcraftUtils plugin)
        throws PermissionsException, CommandSenderException, CommandUsageException, CommandException {
        if (sender instanceof Player)
            if (!plugin.check((Player) sender, "time"))
                throw new PermissionsException(command);

        if (args.length == 0) // Check the current time!
        {
            if (sender instanceof Player)
                sender.sendMessage("Current time in this world : " + (((Player) sender).getWorld().getTime() / 1000));
        } else {
            String settime = args[0];
            long timeset = 0;
            if (settime.equalsIgnoreCase("day")) {
                timeset = 0L;
            } else if (settime.equalsIgnoreCase("night")) {
                timeset = 13000L;
            } else {
                if (settime.length() < 3) {
                    try {
                        timeset = Integer.parseInt(settime) * 1000;
                    } catch (NumberFormatException e) {
                        sender.sendMessage(ChatColor.YELLOW + "You handed me something different than day or night, but it is not a number!");
                    }
                } else {
                    try {
                        timeset = Long.parseLong(settime);
                    } catch (NumberFormatException e) {
                        sender.sendMessage(ChatColor.YELLOW + "You handed me something different than day or night, but it is not a number!");
                    }
                }
            }
            if (args.length > 1) { // World?
                World world = plugin.getServer().getWorld(args[1].toLowerCase());
                if (world != null) {
                    sender.sendMessage(ChatColor.YELLOW + "Setting time in world " + world.getName());
                    world.setTime(timeset);
                } else {
                    sender.sendMessage(ChatColor.YELLOW + "Can't find that world!");
                }
            } else {
                if (sender instanceof Player) {
                    Player p = (Player) sender;
                    sender.sendMessage(ChatColor.YELLOW + "Setting time in world " + p.getWorld().getName());
                    p.getWorld().setTime(timeset);
                }
            }
        }

        return true;
    }

    @aCommand(
        aliases = {"unban"},
        permissionBase = "ban",
        description = "Unban a player",
        section = "essentials"
    )
    public boolean unban(CommandSender sender, String command, String[] args, TweakcraftUtils plugin)
        throws PermissionsException, CommandSenderException, CommandUsageException, CommandException {
        if (sender instanceof Player)
            if (!plugin.check((Player) sender, "ban"))
                throw new PermissionsException(command);
        BanHandler handler = plugin.getBanhandler();
        if (args.length > 0) {
            String target = args[0].toLowerCase();
            if (handler.isBanned(target)) {
                sender.sendMessage(ChatColor.YELLOW + "Unbanning player!");
                handler.unBan(target);
                handler.saveBans();
            } else {
                sender.sendMessage(ChatColor.YELLOW + "That player isn't banned!");
            }

        }
        return true;
    }

    @aCommand(
        aliases = {"who", "playerlist"},
        permissionBase = "who",
        description = "List of currently connected players",
        section = "essentials"
    )
    public boolean who(CommandSender sender, String command, String[] realargs, TweakcraftUtils plugin)
        throws PermissionsException, CommandSenderException, CommandUsageException {


        ArgumentParser ap = new ArgumentParser(realargs);
        String world = ap.getString("w", null);
        String[] args = ap.getUnusedArgs();
        World w = null;

        if (world != null) {
            w = plugin.getServer().getWorld(world);
            if (w == null) throw new CommandUsageException("World not found!");
        }

        List<Player> list = null;

        if (w == null) list = Arrays.asList(plugin.getServer().getOnlinePlayers());
        else list = w.getPlayers();
        Integer amountofinvis = 0;
        for (Player p : list) {
            if (plugin.getPlayerListener().getInvisplayers().contains(p.getName()))
                amountofinvis++;
        }
        boolean hasperm;
        if (sender instanceof Player)
            hasperm = plugin.check((Player) sender, "tpinvis");
        else
            hasperm = true;

        String msg = ChatColor.LIGHT_PURPLE + "Player list (" + (w == null ? (list.size() - amountofinvis) + "/" + plugin.getServer().getMaxPlayers() : ChatColor.GREEN + w.getName() + ChatColor.LIGHT_PURPLE) + "): ";
        if (amountofinvis > 0) {
            if (hasperm)
                msg += ChatColor.AQUA + " [" + list.size() + "/" + plugin.getServer().getMaxPlayers() + "]";
        }
        String toadd;
        Collections.sort(list, new Comparator<Player>() {
            public int compare(Player p1, Player p2) {
                return p1.getName().compareToIgnoreCase(p2.getName());
            }
        });

        sender.sendMessage(msg);
        msg = " ";
        boolean check;

        for (Player p : list) {
            LocalPlayer lp = plugin.wrapPlayer(p);
            toadd = "";
            check = plugin.getPlayerListener().getInvisplayers().contains(p.getName());

            /* if (!(sender instanceof Player)) { // console won't show gold colors? shame! // THIS HAS BEEN FIXED LONG AGO!
             if(check)
                 toadd = ChatColor.AQUA+"[";

             toadd += p.getDisplayName(); // .replace(ChatColor.GOLD.toString(), ChatColor.YELLOW.toString());

             if(check)
                 toadd += ChatColor.AQUA+"]";

             toadd+=ChatColor.WHITE+", ";
         } else { */
            if (check && hasperm) {
                toadd = ChatColor.AQUA + "[" + p.getDisplayName() + ChatColor.AQUA + "]" + (lp.isAfk() ? ChatColor.RED + " [AFK]" : "") + ChatColor.WHITE + ", ";
            } else if (!check) {
                toadd = p.getDisplayName() + (lp.isAfk() ? ChatColor.RED + " [AFK]" : "") + ChatColor.WHITE + ", ";
            }
            // }
            msg += toadd;
        }
        if (!msg.trim().equals("")) {
            sender.sendMessage(msg.substring(0, msg.length() - 2));
        }

        return true;
    }

    @aCommand(
        aliases = {"world"},
        permissionBase = "world",
        description = "Teleports you to a world of choice",
        section = "essentials"
    )
    public boolean world(CommandSender sender, String command, String[] args, TweakcraftUtils plugin)
        throws PermissionsException, CommandSenderException, CommandUsageException, CommandException {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (args.length > 0) {
                List<World> worlds = plugin.getServer().getWorlds();
                String worldname = args[0];
                Integer worldnum;
                World world;
                try {
                    worldnum = Integer.parseInt(worldname);
                    world = worlds.get(worldnum);
                } catch (NumberFormatException e) {
                    world = plugin.getServer().getWorld(worldname);
                } catch (IndexOutOfBoundsException e) {
                    throw new CommandUsageException(ChatColor.YELLOW + "Can't find that world!");
                }
                if (world != null) {
                    if (world.getName().equals(player.getWorld().getName())) {
                        throw new CommandUsageException("You already are on that world!");
                    }
                    if (!plugin.check(player, "worlds." + world.getName() + ".world")) {
                        throw new PermissionsException("You don't have permission to /world to that world!");
                    } else {
                        Location oldlocation = player.getLocation().clone();
                        Location toLocation = world.getSpawnLocation();
                        // String locString = "";

                        if (plugin.getConfigHandler().enablePersistence) {
                            List<PlayerOptions> plist = plugin.getDatabase().find(PlayerOptions.class).where().ieq("name", player.getName()).ieq("optionname", "worldpos").findList();
                            PlayerOptions po = null;
                            if (plist.size() > 0) {
                                for (PlayerOptions popts : plist) {
                                    Location tloc = this.parseLocationString(popts.getOptionvalue());
                                    if (tloc != null) {
                                        if (tloc.getWorld().getName().equals(player.getWorld().getName())) {
                                            po = popts;
                                        } else if (tloc.getWorld().getName().equals(world.getName())) {
                                            toLocation = tloc;
                                        }
                                    }
                                }
                            }
                            if (po == null) {
                                po = new PlayerOptions();
                                po.setOptionname("worldpos");
                                po.setName(player.getName());
                            }
                            po.setOptionvalue(this.exportLocationString(player.getLocation()));
                            plugin.getDatabase().save(po);
                        }
                        if (player.teleport(toLocation))
                            plugin.getTelehistory().addHistory(player.getName(), oldlocation);
                    }
                } else {
                    sender.sendMessage(ChatColor.YELLOW + "Can't find that world!");
                }
            } else {
                throw new CommandUsageException("I need a world to tp you to!");
            }
        } else {
            throw new CommandSenderException("What do you think you are doing?");
        }
        return true;
    }


    // x=0.25,y=30,z=30,w=survival,yaw=40,pit=40
    public Location parseLocationString(String locstring) {
        try {
            String[] stuff = locstring.split(",");
            Double x = Double.parseDouble(stuff[0].substring(2));
            Double y = Double.parseDouble(stuff[1].substring(2));
            Double z = Double.parseDouble(stuff[2].substring(2));
            World world = Bukkit.getServer().getWorld(stuff[3].substring(2));
            Float yaw = Float.parseFloat(stuff[4].substring(4));
            Float pitch = Float.parseFloat(stuff[5].substring(4));
            if (world == null) return null;

            return new Location(world, x, y, z, yaw, pitch);


        } catch (NumberFormatException ex) {
            return null;
        }
    }

    public String exportLocationString(Location loc) {
        return "x=" + loc.getX() + ",y=" + loc.getY() + ",z=" + loc.getZ() + ",w=" + loc.getWorld().getName() + ",yaw=" + loc.getYaw() + ",pit=" + loc.getPitch();
        // return null;
    }
}
