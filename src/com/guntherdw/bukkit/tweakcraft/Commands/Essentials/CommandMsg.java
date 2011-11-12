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

import com.guntherdw.bukkit.tweakcraft.Commands.iCommand;
import com.guntherdw.bukkit.tweakcraft.Exceptions.CommandException;
import com.guntherdw.bukkit.tweakcraft.Exceptions.CommandSenderException;
import com.guntherdw.bukkit.tweakcraft.Exceptions.CommandUsageException;
import com.guntherdw.bukkit.tweakcraft.Exceptions.PermissionsException;
import com.guntherdw.bukkit.tweakcraft.Packages.LocalPlayer;
import com.guntherdw.bukkit.tweakcraft.TweakcraftUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * @author GuntherDW
 */
public class CommandMsg implements iCommand {
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
            Player playerto = plugin.findPlayerasPlayer(args[0]);

            String message = "";
            for (int x = 1; x < args.length; x++) {
                message += args[x] + (x<args.length?" ":"");
            }
            if (playerto == null)
                throw new CommandException("Can't find that player!");

            sender.sendMessage("[Me -> " + playerto.getDisplayName() + "] " + message);
            playerto.sendMessage("[" + senderName + " -> Me] " + message);
            if (sender instanceof Player) {
                LocalPlayer lp = plugin.wrapPlayer((Player)sender);
                lp.setReplyTo(playerto.getName());
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

    @Override
    public String getPermissionSuffix() {
        return null;
    }
}
