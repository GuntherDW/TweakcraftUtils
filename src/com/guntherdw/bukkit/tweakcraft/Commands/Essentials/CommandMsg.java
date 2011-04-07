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

import com.guntherdw.bukkit.tweakcraft.Command;
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
public class CommandMsg implements Command {
    public boolean executeCommand(CommandSender sender, String command, String[] args, TweakcraftUtils plugin)
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
            String replyto = plugin.findPlayer(args[0]);
            String message = "";
            for (int x = 1; x < args.length; x++) {
                message += args[x] + " ";
            }
            if (message.length() > 1) {
                message = message.substring(0, message.length() - 1);
            }
            if (replyto == null)
                throw new CommandException("Can't find that player!");

            Player playerto = plugin.getServer().getPlayer(replyto);
            if (playerto == null)
                throw new CommandException("That player is no longer online!");

            sender.sendMessage("[Me -> " + playerto.getDisplayName() + "] " + message);
            playerto.sendMessage("[" + senderName + " -> Me] " + message);
            if (sender instanceof Player)
                plugin.setPlayerReply(playerto.getName(), ((Player) sender).getName());

            plugin.getLogger().info("[TweakcraftUtils] (MSG) " + clearName + " -> " + playerto.getName() + " : " + message);
        } else if (args.length == 1) {
            throw new CommandUsageException("I need a message!");
        } else {
            throw new CommandUsageException("I need a player!");
        }

        return true;
    }
}
