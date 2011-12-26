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
import com.guntherdw.bukkit.tweakcraft.Chat.Modes.RegionChat;
import com.guntherdw.bukkit.tweakcraft.Chat.Modes.ZoneChat;
import com.guntherdw.bukkit.tweakcraft.Commands.aCommand;
import com.guntherdw.bukkit.tweakcraft.Exceptions.*;
import com.guntherdw.bukkit.tweakcraft.TweakcraftUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

/**
 * @author GuntherDW
 */
public class ChatCommands {

    @aCommand(
        aliases = { "chatmode", "cm" },
        permissionBase = "chat",
        description = "ChatMode control",
        section = "chat"
    )
    public boolean chatmode(CommandSender sender, String command, String[] args, TweakcraftUtils plugin)
        throws PermissionsException, CommandSenderException, CommandUsageException, CommandException {
        if(sender instanceof Player) {
            if(args.length==0) {
                throw new CommandUsageException("/"+command+" <list|[chatmode]|...>");
            }
            Player player = (Player) sender;
            ChatHandler ch = plugin.getChathandler();
            if(!ch.canTalk(player.getName())) {
                throw new CommandException("You can't use this while muted!");
            }
            String chatMode = args[0].toLowerCase();
            String msg = "";
            if(args.length == 1)
            {
                if(chatMode.equalsIgnoreCase("listplayers") && plugin.check(player, "chat.list")) {
                    sender.sendMessage(ChatColor.GOLD + "Current list of players in a chatmode");
                    // TODO: better integration
                    for(String cms : plugin.getChathandler().listChatModes()) {

                        try {
                            if(plugin.getChathandler().getChatMode(cms).isEnabled()) {
                                for(String pla : plugin.getChathandler().getChatMode(cms).getSubscribers()) {
                                    String color = "";
                                    try {
                                        color = plugin.getPlayerColor(pla, true);
                                    } catch (NullPointerException e) {
                                        color = ChatColor.WHITE.toString();
                                    }
                                    msg = color+pla+ChatColor.WHITE+" ("+plugin.getChathandler().getChatMode(cms).getColor()+cms+ChatColor.WHITE+")";
                                    sender.sendMessage(msg);
                                }
                            }
                        } catch(ChatModeException ex) {

                        }
                    }
                } else if(chatMode.equalsIgnoreCase("list")) {
                    msg = ChatColor.GOLD+"Currently enabled chatmodes";
                    sender.sendMessage(msg);
                    for(String cms : plugin.getChathandler().listChatModes()) {
                        try
                        {
                            if(plugin.getChathandler().getChatMode(cms).isEnabled()) {
                                msg = "- "+ChatColor.GOLD+cms+ChatColor.WHITE+" : "+plugin.getChathandler().getChatMode(cms).getDescription();
                                sender.sendMessage(msg);
                            }
                        } catch (ChatModeException ex) {

                        }
                    }
                    msg = "- "+ChatColor.GOLD+"global"+ChatColor.WHITE+" : Chat globally, remove chatmode subscription";
                    sender.sendMessage(msg);
                } else if(chatMode.equalsIgnoreCase("global")) {
                    msg = ChatColor.YELLOW+"You will now chat globally!";
                    try {
                        ChatMode oldcm = ch.getPlayerChatMode(player);
                        if(oldcm != null)
                            oldcm.removeRecipient(player.getName());
                        ch.setPlayerchatmode(player.getName(), null);
                    } catch(ChatModeException ex) {
                        sender.sendMessage("Can't find chatmode, this shouldn't happen!");
                    }
                    sender.sendMessage(msg);
                } else { // Join specified chatmode
                    try {

                        ChatMode cm = ch.getChatMode(chatMode);
                        if((chatMode.equals("admin") && ((AdminChat)cm).isPlayerAllowed(player.getName()))
                            || plugin.check(player, "chat.mode."+chatMode)) {

                            if(cm.isEnabled()) {

                                ChatMode oldcm = ch.getPlayerChatMode(player);
                                if(oldcm != null)
                                    oldcm.removeRecipient(player.getName());

                                ch.setPlayerchatmode(player.getName(), chatMode);
                                cm.addRecipient(player.getName());
                            } else {
                                throw new CommandException(ChatColor.GOLD+"That ChatMode is not enabled");
                            }
                        }
                        else
                            throw new PermissionsException("You don't have the permission to join this chatmode!");

                        sender.sendMessage(ChatColor.GOLD+"Selected ChatMode : "+chatMode);
                    } catch(ChatModeException ex) {
                        sender.sendMessage("Can't find chatmode!");
                    }
                }
            } else if(args.length>1) { //Spam the selected chatmode!
                if(chatMode.equals("global")) {
                    String spam = "";
                    int x = 0;
                    for (String m : args) {
                        if(x!=0)
                            spam += m + " ";
                        x++;
                    }
                    plugin.getServer().broadcastMessage(ChatColor.WHITE+"<"+player.getDisplayName()+ChatColor.WHITE +"> " +spam);
                } else {
                    try {
                        ChatMode cm = ch.getChatMode(chatMode);

                        if((chatMode.equals("admin") && ((AdminChat)cm).isPlayerAllowed(player.getName()))
                            || plugin.check(player, "chat.mode."+chatMode)) {
                            if(cm.isEnabled())
                            {
                                String spam;
                                spam = "";

                                int x = 0;
                                for (String m : args) {
                                    if(x!=0)
                                        spam += m + " ";
                                    x++;
                                }
                                spam = spam.substring(0, spam.length() - 1);
                                cm.sendMessage(player, spam);
                            } else {
                                throw new CommandException(ChatColor.GOLD+"That ChatMode is not enabled");
                            }
                        } else {
                            throw new PermissionsException("You don't have the permission to join this chatmode!");
                        }

                    } catch(ChatModeException ex) {
                        sender.sendMessage("Can't find chatmode!");
                    }
                }
            }
        } else {
            throw new CommandSenderException("What are you doing here?");
        }
        return true;
    }

    @aCommand(
        aliases = { "lc" },
        permissionBase = "chat.mode.local",
        description = "Toggle local chat",
        section = "chat"
    )
    public boolean localchat(CommandSender sender, String command, String[] args, TweakcraftUtils plugin)
        throws PermissionsException, CommandSenderException, CommandUsageException, CommandException {

        if(!plugin.getConfigHandler().enableLocalChat) {
            throw new CommandUsageException("LocalChat not enabled!");
        }

        if (args.length != 0 && args[0].equalsIgnoreCase("list")) {

            if (sender instanceof Player)
                if (!plugin.check((Player) sender, "chat.list.local"))
                    throw new PermissionsException(command);

            try {
                ChatMode cm = plugin.getChathandler().getChatMode("local");
                List<String> sublist = cm.getSubscribers();
                if (sublist.size() != 0) {
                    sender.sendMessage(ChatColor.LIGHT_PURPLE + "Current localchat playerlist:");
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
                    sender.sendMessage(ChatColor.LIGHT_PURPLE + "Current localchat playerlist is empty!");
                }
                // sender.sendMessage(ChatColor.LIGHT_PURPLE + "Current localchat chatters : ");

            } catch (ChatModeException e) {
                throw new CommandException("Exception thrown when setting chatmode!");
            }
        } else if(args.length != 0) {
            if (sender instanceof Player)
                if (!plugin.check((Player) sender, "chat.mode.local"))
                    throw new PermissionsException(command);
            try {
                ChatMode cm = plugin.getChathandler().getChatMode("local");
                String msg = "";
                for (String m : args)
                    msg += m + " ";
                msg = msg.substring(0, msg.length() - 1);
                cm.sendMessage(sender, msg);

            } catch(ChatModeException ex) {
                throw new CommandException("Exception thrown when setting chatmode!");
            }
        } else {
            if (sender instanceof Player) {
                if (!plugin.check((Player) sender, "chat.mode.local"))
                    throw new PermissionsException(command);

                else {
                    try {
                        ChatMode cm = plugin.getChathandler().getChatMode("local");
                        List<String> sublist = cm.getSubscribers();
                        if (!sublist.contains(((Player) sender).getName())) {
                            cm.addRecipient(((Player) sender).getName());
                            plugin.getChathandler().setPlayerchatmode(((Player) sender).getName(), "local");
                            sender.sendMessage(ChatColor.YELLOW + "You will now chat locally!");
                        } else {
                            cm.removeRecipient(((Player) sender).getName());
                            plugin.getChathandler().setPlayerchatmode(((Player) sender).getName(), null);
                            sender.sendMessage(ChatColor.YELLOW + "You will now chat globally!");
                        }

                    } catch (ChatModeException e) {
                        throw new CommandException("Exception thrown when setting chatmode!");
                    }
                }
            } else {
                // It's the console!
                throw new CommandSenderException("You need to be a player to use LocalChat!");
            }
        }

        return true;
    }

    @aCommand(
        aliases = { "rc" },
        permissionBase = "chat.mode.region",
        description = "Toggle region chat",
        section = "chat"
    )
    public boolean regionchat(CommandSender sender, String command, String[] args, TweakcraftUtils plugin)
        throws PermissionsException, CommandSenderException, CommandUsageException, CommandException {

        if(!plugin.getConfigHandler().enableWorldGuard) {
            throw new CommandUsageException("WorldGuard not enabled!");
        }

        if (args.length != 0 && args[0].equalsIgnoreCase("list")) {

            if (sender instanceof Player)
                if (!plugin.check((Player) sender, "chat.list.region"))
                    throw new PermissionsException(command);

            try {
                ChatMode cm = plugin.getChathandler().getChatMode("region");
                List<String> sublist = cm.getSubscribers();
                if (sublist.size() != 0) {
                    RegionChat rc = (RegionChat) cm;
                    sender.sendMessage(ChatColor.LIGHT_PURPLE + "Current regionchat playerlist:");
                    String color = "";
                    String msg = "";
                    for (String playername : rc.getSubscribers()) {
                        try {
                            color = plugin.getPlayerColor(playername, true);
                        } catch (NullPointerException e) {
                            color = ChatColor.WHITE.toString();
                        }
                        msg = color + playername + ChatColor.WHITE+" ("+rc.getRegionName(playername, true)+")";
                        sender.sendMessage(msg);
                    }
                } else {
                    sender.sendMessage(ChatColor.LIGHT_PURPLE + "Current regionchat playerlist is empty!");
                }
                // sender.sendMessage(ChatColor.LIGHT_PURPLE + "Current localchat chatters : ");

            } catch (ChatModeException e) {
                throw new CommandException("Exception thrown when setting chatmode!");
            }
        } else if(args.length != 0) {
            if (sender instanceof Player)
                if (!plugin.check((Player) sender, "chat.mode.region"))
                    throw new PermissionsException(command);
            try {
                ChatMode cm = plugin.getChathandler().getChatMode("region");
                String msg = "";
                for (String m : args)
                    msg += m + " ";
                msg = msg.substring(0, msg.length() - 1);
                cm.sendMessage(sender, msg);

            } catch(ChatModeException ex) {
                throw new CommandException("Exception thrown when setting chatmode!");
            }
        } else {
            if (sender instanceof Player) {
                if (!plugin.check((Player) sender, "chat.mode.region"))
                    throw new PermissionsException(command);

                else {
                    try {
                        ChatMode cm = plugin.getChathandler().getChatMode("region");
                        List<String> sublist = cm.getSubscribers();
                        if (!sublist.contains(((Player) sender).getName())) {
                            RegionChat rc = (RegionChat) cm;
                            cm.addRecipient(((Player) sender).getName());
                            plugin.getChathandler().setPlayerchatmode(((Player) sender).getName(), "region");
                            String rgnames = rc.getRegionName(((Player)sender).getName(), true);
                            if(rgnames == null || rc.getRegionName(((Player)sender).getName(), false).equals(""))
                            {
                                sender.sendMessage(ChatColor.YELLOW + "Regionchat enabled but haven't found any active region!");
                            } else {
                                sender.sendMessage(ChatColor.YELLOW + "Regionchat in regions "+ChatColor.WHITE+"["+rgnames+"]!");
                            }
                        } else {
                            cm.removeRecipient(((Player) sender).getName());
                            plugin.getChathandler().setPlayerchatmode(((Player) sender).getName(), null);
                            sender.sendMessage(ChatColor.YELLOW + "You will now chat globally!");
                        }

                    } catch (ChatModeException e) {
                        throw new CommandException("Exception thrown when setting chatmode!");
                    }
                }
            } else {
                // It's the console!
                throw new CommandSenderException("You need to be a player to use RegionChat!");
            }
        }

        return true;
    }

    @aCommand(
        aliases = { "wc" },
        permissionBase = "chat.mode.world",
        description = "Toggle world chat",
        section = "chat"
    )
    public boolean worldchat(CommandSender sender, String command, String[] args, TweakcraftUtils plugin)
        throws PermissionsException, CommandSenderException, CommandUsageException, CommandException {

        if(!plugin.getConfigHandler().enableWorldChat) {
            throw new CommandUsageException("WorldChat not enabled!");
        }

        if (args.length != 0 && args[0].equalsIgnoreCase("list")) {

            if (sender instanceof Player)
                if (!plugin.check((Player) sender, "chat.list.world"))
                    throw new PermissionsException(command);

            try {
                ChatMode cm = plugin.getChathandler().getChatMode("world");
                List<String> sublist = cm.getSubscribers();
                if (sublist.size() != 0) {
                    sender.sendMessage(ChatColor.LIGHT_PURPLE + "Current WorldChat playerlist:");
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
                    sender.sendMessage(ChatColor.LIGHT_PURPLE + "Current WorldChat playerlist is empty!");
                }
                // sender.sendMessage(ChatColor.LIGHT_PURPLE + "Current localchat chatters : ");

            } catch (ChatModeException e) {
                throw new CommandException("Exception thrown when setting chatmode!");
            }
        } else if(args.length != 0) {
            if (sender instanceof Player)
                if (!plugin.check((Player) sender, "chat.mode.world"))
                    throw new PermissionsException(command);
            try {
                ChatMode cm = plugin.getChathandler().getChatMode("world");
                String msg = "";
                for (String m : args)
                    msg += m + " ";
                msg = msg.substring(0, msg.length() - 1);
                cm.sendMessage(sender, msg);

            } catch(ChatModeException ex) {
                throw new CommandException("Exception thrown when setting chatmode!");
            }
        } else {
            if (sender instanceof Player) {
                if (!plugin.check((Player) sender, "chat.mode.world"))
                    throw new PermissionsException(command);

                else {
                    try {
                        ChatMode cm = plugin.getChathandler().getChatMode("world");
                        List<String> sublist = cm.getSubscribers();
                        if (!sublist.contains(((Player) sender).getName())) {
                            cm.addRecipient(((Player) sender).getName());
                            plugin.getChathandler().setPlayerchatmode(((Player) sender).getName(), "world");
                            sender.sendMessage(ChatColor.YELLOW + "You will now chat in the current world!");
                        } else {
                            cm.removeRecipient(((Player) sender).getName());
                            plugin.getChathandler().setPlayerchatmode(((Player) sender).getName(), null);
                            sender.sendMessage(ChatColor.YELLOW + "You will now chat globally!");
                        }

                    } catch (ChatModeException e) {
                        throw new CommandException("Exception thrown when setting chatmode!");
                    }
                }
            } else {
                // It's the console!
                throw new CommandSenderException("You need to be a player to use WorldChat!");
            }
        }

        return true;
    }


    @aCommand(
        aliases = { "zc" },
        permissionBase = "chat.mode.zone",
        description = "Toggle Zone chat",
        section = "chat"
    )
    public boolean executeCommand(CommandSender sender, String command, String[] args, TweakcraftUtils plugin)
        throws PermissionsException, CommandSenderException, CommandUsageException, CommandException {
        if(plugin.getConfigHandler().enableZones==false) {
            throw new CommandUsageException("Zones not enabled!");
        }

        if (args.length != 0 && args[0].equalsIgnoreCase("list")) {

            if (sender instanceof Player)
                if (!plugin.check((Player) sender, "chat.list.zones"))
                    throw new PermissionsException(command);

            try {
                ChatMode cm = plugin.getChathandler().getChatMode("zones");
                List<String> sublist = cm.getSubscribers();
                if (sublist.size() != 0) {
                    ZoneChat zc = (ZoneChat) cm;
                    sender.sendMessage(ChatColor.LIGHT_PURPLE + "Current ZoneChat playerlist:");
                    String color = "";
                    String msg = "";
                    for (String playername : zc.getSubscribers()) {
                        try {
                            Player p = plugin.getServer().getPlayer(playername);
                            color = plugin.getPlayerColor(playername, true);
                            msg = color + playername + ChatColor.WHITE+" ("+zc.getZoneName(p, true)+")";
                            sender.sendMessage(msg);
                        } catch (NullPointerException e) {
                            // color = ChatColor.WHITE.toString();
                        }

                    }
                } else {
                    sender.sendMessage(ChatColor.LIGHT_PURPLE + "Current ZoneChat playerlist is empty!");
                }
                // sender.sendMessage(ChatColor.LIGHT_PURPLE + "Current localchat chatters : ");

            } catch (ChatModeException e) {
                throw new CommandException("Exception thrown when setting chatmode!");
            }
        } else if(args.length != 0) {
            if (sender instanceof Player)
                if (!plugin.check((Player) sender, "chat.mode.zones"))
                    throw new PermissionsException(command);
            try {
                ChatMode cm = plugin.getChathandler().getChatMode("zones");
                String msg = "";
                for (String m : args)
                    msg += m + " ";
                msg = msg.substring(0, msg.length() - 1);
                cm.sendMessage(sender, msg);

            } catch(ChatModeException ex) {
                throw new CommandException("Exception thrown when setting chatmode!");
            }
        } else {
            if (sender instanceof Player) {
                if (!plugin.check((Player) sender, "chat.mode.zones"))
                    throw new PermissionsException(command);

                else {
                    try {
                        ChatMode cm = plugin.getChathandler().getChatMode("zones");
                        List<String> sublist = cm.getSubscribers();
                        if (!sublist.contains(((Player) sender).getName())) {
                            ZoneChat zc = (ZoneChat) cm;
                            cm.addRecipient(((Player) sender).getName());
                            plugin.getChathandler().setPlayerchatmode(((Player) sender).getName(), "zones");
                            String rgnames = zc.getZoneName(sender, true);
                            if(rgnames == null || zc.getZoneName(sender, false).equals(""))
                            {
                                sender.sendMessage(ChatColor.YELLOW + "ZoneChat enabled but haven't found any active zone!");
                            } else {
                                sender.sendMessage(ChatColor.YELLOW + "ZoneChat in zones "+ChatColor.WHITE+"["+rgnames+"]!");
                            }
                        } else {
                            cm.removeRecipient(((Player) sender).getName());
                            plugin.getChathandler().setPlayerchatmode(((Player) sender).getName(), null);
                            sender.sendMessage(ChatColor.YELLOW + "You will now chat globally!");
                        }

                    } catch (ChatModeException e) {
                        throw new CommandException("Exception thrown when setting chatmode!");
                    }
                }
            } else {
                // It's the console!
                throw new CommandSenderException("You need to be a player to use ZoneChat!");
            }
        }
        return true;
    }
}
