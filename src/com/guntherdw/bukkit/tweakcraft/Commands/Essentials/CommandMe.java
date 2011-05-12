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

package com.guntherdw.bukkit.tweakcraft.Commands.Essentials;

import com.guntherdw.bukkit.tweakcraft.Chat.ChatHandler;
import com.guntherdw.bukkit.tweakcraft.Chat.ChatMode;
import com.guntherdw.bukkit.tweakcraft.Chat.Modes.AdminChat;
import com.guntherdw.bukkit.tweakcraft.Chat.Modes.LocalChat;
import com.guntherdw.bukkit.tweakcraft.Chat.Modes.RegionChat;
import com.guntherdw.bukkit.tweakcraft.Chat.Modes.ZoneChat;
import com.guntherdw.bukkit.tweakcraft.Commands.Command;
import com.guntherdw.bukkit.tweakcraft.Exceptions.CommandException;
import com.guntherdw.bukkit.tweakcraft.Exceptions.CommandSenderException;
import com.guntherdw.bukkit.tweakcraft.Exceptions.CommandUsageException;
import com.guntherdw.bukkit.tweakcraft.Exceptions.PermissionsException;
import com.guntherdw.bukkit.tweakcraft.TweakcraftUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * @author GuntherDW
 */
public class CommandMe implements Command {
    public boolean executeCommand(CommandSender sender, String command, String[] args, TweakcraftUtils plugin)
            throws PermissionsException, CommandSenderException, CommandUsageException, CommandException {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            ChatHandler ch = plugin.getChathandler();
            if (ch.getMutedPlayers().contains(player.getName().toLowerCase())) {
                sender.sendMessage(ChatColor.RED + "What were you trying to do?");
            } else {
                if (args.length > 0) {
                    ChatMode cm = ch.getPlayerChatMode(player);
                    String msg = "";
                    for (String m : args)
                        msg += m + " ";
                    msg = msg.substring(0, msg.length() - 1);

                    if (cm == null) {
                        if(plugin.getPlayerListener().getInvisplayers().contains(player.getName())) {
                            player.sendMessage(ChatColor.AQUA + "Are you crazy? set a chatmode first!");
                        } else {
                            plugin.getServer().broadcastMessage("* " + player.getDisplayName() + " " + msg);
                        }
                    } else if (cm instanceof LocalChat) {
                        ((LocalChat) cm).broadcastMessage(player, "[" + ChatColor.YELLOW + "L" + ChatColor.WHITE + "] * " + player.getDisplayName() + " " + msg);
                    } else if (cm instanceof AdminChat) {
                        ((AdminChat) cm).broadcastMessage(player, "[" + ChatColor.GREEN + "A" + ChatColor.WHITE + "] * " + player.getDisplayName() + " " + ChatColor.GREEN + msg);
                    } else if (cm instanceof RegionChat) {
                        ((RegionChat) cm).broadcastMessage(player, "[" + ChatColor.AQUA + "R" + ChatColor.WHITE + "] * " + player.getDisplayName() + " " + msg);
                    } else if (cm instanceof ZoneChat)  {
                        ((ZoneChat) cm).broadcastMessage(player, "[" + ChatColor.DARK_PURPLE + "Z" + ChatColor.WHITE + "] * " + player.getDisplayName() + " " + msg);
                    }
                }

            }
        } else {
            throw new CommandSenderException("Now why on earth...");
        }
        return true;
    }

    @Override
    public String getPermissionSuffix() {
        return null;
    }
}
