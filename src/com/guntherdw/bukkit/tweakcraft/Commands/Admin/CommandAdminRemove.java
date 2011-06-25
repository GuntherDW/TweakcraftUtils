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
import com.guntherdw.bukkit.tweakcraft.Chat.Modes.AdminChat;
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
public class CommandAdminRemove implements iCommand {
    public boolean executeCommand(CommandSender sender, String command, String[] args, TweakcraftUtils plugin)
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

    @Override
    public String getPermissionSuffix() {
        return "admon";
    }

}
