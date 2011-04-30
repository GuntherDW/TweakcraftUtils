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

package com.guntherdw.bukkit.tweakcraft.Commands.Admin;

import com.guntherdw.bukkit.tweakcraft.Chat.ChatHandler;
import com.guntherdw.bukkit.tweakcraft.Chat.ChatMode;
import com.guntherdw.bukkit.tweakcraft.Commands.Command;
import com.guntherdw.bukkit.tweakcraft.Exceptions.*;
import com.guntherdw.bukkit.tweakcraft.TweakcraftUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * @author GuntherDW
 */
public class CommandAdminList implements Command {
    public boolean executeCommand(CommandSender sender, String command, String[] args, TweakcraftUtils plugin) throws PermissionsException, CommandSenderException, CommandUsageException, CommandException {
        if (sender instanceof Player) {
            if (!plugin.check((Player) sender, getPermissionSuffix()))
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

    @Override
    public String getPermissionSuffix() {
        return "adminlist";
    }
}
