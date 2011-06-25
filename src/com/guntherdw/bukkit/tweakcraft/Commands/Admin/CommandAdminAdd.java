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

import com.guntherdw.bukkit.tweakcraft.Chat.ChatMode;
import com.guntherdw.bukkit.tweakcraft.Chat.Modes.AdminChat;
import com.guntherdw.bukkit.tweakcraft.Commands.iCommand;
import com.guntherdw.bukkit.tweakcraft.Exceptions.*;
import com.guntherdw.bukkit.tweakcraft.TweakcraftUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

/**
 * @author GuntherDW
 */
public class CommandAdminAdd implements iCommand {
    public boolean executeCommand(CommandSender sender, String command, String[] args, TweakcraftUtils plugin)
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
            List<String> playernames = new ArrayList<String>();
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

    @Override
    public String getPermissionSuffix() {
        return "admon";
    }
}
