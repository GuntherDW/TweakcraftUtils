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

package com.guntherdw.bukkit.tweakcraft.Commands.Chat;

import com.guntherdw.bukkit.tweakcraft.Chat.ChatMode;
import com.guntherdw.bukkit.tweakcraft.Chat.Modes.RegionChat;
import com.guntherdw.bukkit.tweakcraft.Commands.iCommand;
import com.guntherdw.bukkit.tweakcraft.Exceptions.*;
import com.guntherdw.bukkit.tweakcraft.TweakcraftUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

/**
 * @author GuntherDW
 */
public class CommandRc implements iCommand {

    public boolean executeCommand(CommandSender sender, String command, String[] args, TweakcraftUtils plugin)
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

    @Override
    public String getPermissionSuffix() {
        return "chat.mode.region";
    }
}
