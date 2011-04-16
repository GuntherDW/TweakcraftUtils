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
public class CommandMute implements Command {
    public boolean executeCommand(CommandSender sender, String command, String[] args, TweakcraftUtils plugin)
            throws PermissionsException, CommandSenderException, CommandUsageException, CommandException {
        if (sender instanceof Player)
            if (!plugin.check((Player) sender, "mute"))
                throw new PermissionsException(command);

        ChatHandler ch = plugin.getChathandler();
        if (args.length == 1) {
            String playername = plugin.findPlayer(args[0]);
            Player player = plugin.getServer().getPlayer(playername);
            if (player != null) {

                if (!ch.getMutedPlayers().contains(player.getName().toLowerCase())) {
                    sender.sendMessage(ChatColor.YELLOW + "Muting " + player.getDisplayName());
                    ch.addMute(player.getName().toLowerCase());
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
}
