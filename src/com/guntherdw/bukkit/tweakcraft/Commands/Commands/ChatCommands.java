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
import com.guntherdw.bukkit.tweakcraft.Exceptions.*;
import com.guntherdw.bukkit.tweakcraft.Packages.LocalPlayer;
import com.guntherdw.bukkit.tweakcraft.TweakcraftUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author GuntherDW
 */
public class ChatCommands {

    TweakcraftUtils plugin;

    public ChatCommands(TweakcraftUtils instance) {
        this.plugin = instance;
    }

    @aCommand(
        aliases = {"chatmode", "cm"},
        permissionBase = "chat",
        description = "ChatMode control",
        section = "chat"
    )
    public boolean chatmode(CommandSender sender, String command, String[] args)
        throws PermissionsException, CommandSenderException, CommandUsageException, CommandException {
        if (args.length == 0) {
            throw new CommandUsageException("/" + command + " <list|[chatmode]|...>");
        }
        return this.chatCommand(sender, command, null, args);
    }

    @aCommand(
        aliases = {"gc"},
        permissionBase = "chat.mode.global",
        description = "Remove ChatMode subscription",
        section = "chat"
    )
    public boolean globalChat(CommandSender sender, String command, String[] args)
        throws PermissionsException, CommandSenderException, CommandUsageException, CommandException {

        this.chatCommand(sender, command, "global", args);

        return true;
    }

    @aCommand(
        aliases = {"lc"},
        permissionBase = "chat.mode.local",
        description = "Toggle local chat",
        section = "chat"
    )
    public boolean localChat(CommandSender sender, String command, String[] args)
        throws PermissionsException, CommandSenderException, CommandUsageException, CommandException {

        if (!plugin.getConfigHandler().enableLocalChat) {
            throw new CommandUsageException("LocalChat not enabled!");
        }

        this.chatCommand(sender, command, "local", args);

        return true;
    }

    @aCommand(
        aliases = {"rc"},
        permissionBase = "chat.mode.region",
        description = "Toggle region chat",
        section = "chat"
    )
    public boolean regionChat(CommandSender sender, String command, String[] args)
        throws PermissionsException, CommandSenderException, CommandUsageException, CommandException {

        if (!plugin.getConfigHandler().enableWorldGuard) {
            throw new CommandUsageException("WorldGuard not enabled!");
        }

        this.chatCommand(sender, command, "region", args);

        return true;
    }

    @aCommand(
        aliases = {"wc"},
        permissionBase = "chat.mode.world",
        description = "Toggle world chat",
        section = "chat"
    )
    public boolean worldChat(CommandSender sender, String command, String[] args)
        throws PermissionsException, CommandSenderException, CommandUsageException, CommandException {

        if (!plugin.getConfigHandler().enableWorldChat) {
            throw new CommandUsageException("WorldChat not enabled!");
        }

        this.chatCommand(sender, command, "world", args);

        return true;
    }


    @aCommand(
        aliases = {"zc"},
        permissionBase = "chat.mode.zone",
        description = "Toggle Zone chat",
        section = "chat"
    )
    public boolean zoneChat(CommandSender sender, String command, String[] args)
        throws PermissionsException, CommandSenderException, CommandUsageException, CommandException {
        if (plugin.getConfigHandler().enableZones == false) {
            throw new CommandUsageException("Zones not enabled!");
        }

        this.chatCommand(sender, command, "zones", args);

        return true;
    }


    public boolean chatCommand(CommandSender sender, String command, String chatModeName, String[] realargs)
        throws PermissionsException, CommandSenderException, CommandUsageException, CommandException {

        ChatHandler ch = plugin.getChathandler();
        if (sender instanceof Player && !ch.canTalk(((Player) sender).getName())) {
            throw new CommandException("You can't use this while muted!");
        }

        String chatMode = null;

        List<String> args = new ArrayList<String>();

        if (chatModeName != null) {
            chatMode = chatModeName;
            args.add(chatModeName);
        } else {
            chatMode = realargs[0].toLowerCase();
        }
        args.addAll(Arrays.asList(realargs));

        String msg = "";
        if (args.size() == 1) {
            if (chatMode.equalsIgnoreCase("listplayers")) {
                if (sender instanceof Player && !plugin.check((Player) sender, "chat.list"))
                    throw new PermissionsException(command);

                sender.sendMessage(ChatColor.GOLD + "Current list of players in a chatmode");
                // TODO: better integration
                for (String cms : plugin.getChathandler().listChatModes()) {

                    try {
                        if (plugin.getChathandler().getChatMode(cms).isEnabled()) {
                            for (String pla : plugin.getChathandler().getChatMode(cms).getSubscribers()) {
                                String color = "";
                                try {
                                    color = plugin.getPlayerColor(pla, true);
                                } catch (NullPointerException e) {
                                    color = ChatColor.WHITE.toString();
                                }
                                msg = color + pla + ChatColor.WHITE + " (" + plugin.getChathandler().getChatMode(cms).getColor() + cms + ChatColor.WHITE + ")";
                                sender.sendMessage(msg);
                            }
                        }
                    } catch (ChatModeException ex) {

                    }
                }
            } else if (chatMode.equalsIgnoreCase("list")) {
                msg = ChatColor.GOLD + "Currently enabled chatmodes";
                sender.sendMessage(msg);
                for (String cms : plugin.getChathandler().listChatModes()) {
                    try {
                        if (plugin.getChathandler().getChatMode(cms).isEnabled()) {
                            msg = "- " + ChatColor.GOLD + cms + ChatColor.WHITE + " : " + plugin.getChathandler().getChatMode(cms).getDescription();
                            sender.sendMessage(msg);
                        }
                    } catch (ChatModeException ex) {

                    }
                }
                msg = "- " + ChatColor.GOLD + "global" + ChatColor.WHITE + " : Chat globally, remove chatmode subscription";
                sender.sendMessage(msg);
            } else if (chatMode.equalsIgnoreCase("global") && sender instanceof Player) {

                Player player = (Player) sender;
                msg = ChatColor.YELLOW + "You will now chat globally!";
                try {
                    ChatMode oldcm = ch.getPlayerChatMode(player);
                    if (oldcm != null)
                        oldcm.removeRecipient(player.getName());
                    ch.setPlayerchatmode(player.getName(), null);
                } catch (ChatModeException ex) {
                    sender.sendMessage("Can't find chatmode, this shouldn't happen!");
                }
                sender.sendMessage(msg);


            } else { // Join specified chatmode
                if (sender instanceof Player) {
                    try {
                        Player player = (Player) sender;
                        LocalPlayer lp = plugin.wrapPlayer(player);
                        ChatMode cm = ch.getChatMode(chatMode);
                        if ((chatMode.equals("admin") && ((AdminChat) cm).isPlayerAllowed(player.getName()))
                            || plugin.check(lp.getBukkitPlayer(), "chat.mode." + chatMode)) {

                            if (cm.isEnabled()) {

                                ChatMode oldcm = ch.getPlayerChatMode(player);
                                
                                if (oldcm != null) {
                                    if(oldcm.equals(cm)) {
                                        ch.setPlayerchatmode(player.getName(), null);
                                        lp.setChatMode(null);
                                        sender.sendMessage(ChatColor.GOLD + "You will now chat globally");
                                        return true;
                                    } else
                                        oldcm.removeRecipient(player.getName());
                                }

                                ch.setPlayerchatmode(player.getName(), chatMode);
                                cm.addRecipient(player.getName());
                                lp.setChatMode(cm);

                            } else {
                                throw new CommandException(ChatColor.GOLD + "That ChatMode is not enabled");
                            }
                        } else
                            throw new PermissionsException("You don't have the permission to join this chatmode!");

                        sender.sendMessage(ChatColor.GOLD + "Selected ChatMode : " + chatMode);
                    } catch (ChatModeException ex) {
                        sender.sendMessage("Can't find chatmode!");
                    }
                } else {
                    throw new CommandSenderException("What were you trying to do?");
                }
            }
        } else if (args.size() > 1) { //Spam the selected chatmode!
            if (chatMode.equals("global")) {
                String spam = "";
                int x = 0;
                for (String m : args) {
                    if (x != 0)
                        spam += m + " ";
                    x++;
                }
                plugin.getServer().broadcastMessage(ChatColor.WHITE + "<" + (sender instanceof Player ? ((Player) sender).getDisplayName() : ChatColor.LIGHT_PURPLE + "CONSOLE") + ChatColor.WHITE + "> " + spam);
            } else {
                try {
                    ChatMode cm = ch.getChatMode(chatMode);

                    if (sender instanceof Player)
                        if ((cm instanceof AdminChat ? ((AdminChat) cm).isPlayerAllowed(((Player) sender).getName()) : !plugin.check((Player) sender, "chat.mode." + chatMode)))
                            throw new PermissionsException(command);

                    if (cm.isEnabled()) {
                        String spam;
                        spam = "";

                        int x = 0;
                        for (String m : args) {
                            if (x != 0)
                                spam += m + " ";
                            x++;
                        }
                        spam = spam.substring(0, spam.length() - 1);
                        cm.sendMessage(sender, spam);
                    } else {
                        throw new CommandException(ChatColor.GOLD + "That ChatMode is not enabled");
                    }

                } catch (ChatModeException ex) {
                    sender.sendMessage("Can't find chatmode!");
                }
            }
        }

        return true;
    }


}
