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

package com.guntherdw.bukkit.tweakcraft.Commands.Teleportation;

import com.guntherdw.bukkit.tweakcraft.Commands.Command;
import com.guntherdw.bukkit.tweakcraft.Exceptions.CommandSenderException;
import com.guntherdw.bukkit.tweakcraft.Exceptions.CommandUsageException;
import com.guntherdw.bukkit.tweakcraft.Exceptions.PermissionsException;
import com.guntherdw.bukkit.tweakcraft.TweakcraftUtils;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;


/**
 * @author GuntherDW
 */
public class CommandTphere implements Command {

    public boolean executeCommand(CommandSender sender, String command, String[] args, TweakcraftUtils plugin)
            throws PermissionsException, CommandSenderException, CommandUsageException {
        if (sender instanceof Player) {
            if (!plugin.check((Player) sender, "tphere"))
                throw new PermissionsException(command);

            if (args.length < 1) {
                throw new CommandUsageException("You need to give me a name!");
            }
            Player player = (Player) sender;
            // List<Player> p = plugin.getServer().matchPlayer(args[0]);
            Player p = plugin.findPlayerasPlayer(args[0]);
            if (p==null) {
                player.sendMessage(ChatColor.YELLOW + "Can't find player!");
            } else {
                // Player pto = p.get(0);
                Player pto = p;
                if (pto.getName().equals(player.getName())) {
                    player.sendMessage(ChatColor.YELLOW + "Now look at that, you've teleported yourself to yourself");
                } else {
                    Location origloc = pto.getLocation();
                    boolean success = pto.teleport(player);
                    if(success) {
                        player.sendMessage(ChatColor.YELLOW + "Teleporting " + pto.getDisplayName() + ChatColor.YELLOW + " to you!");
                        pto.sendMessage(player.getDisplayName() + ChatColor.YELLOW
                            + " teleported you to him!");
                        plugin.getTelehistory().addHistory(pto.getName(), origloc);
                    } else {
                        player.sendMessage(ChatColor.YELLOW + "Failed to teleport " + pto.getDisplayName() + ChatColor.YELLOW + " to you!");
                        pto.sendMessage(player.getDisplayName() + ChatColor.YELLOW
                            + ChatColor.RED + "tried/failed"+ ChatColor.YELLOW +  " to teleport you to him!");
                    }


                }
            }
        } else {
            throw new CommandSenderException("You need to be player to teleport someone to you!");
        }

        return true;
    }

    @Override
    public String getPermissionSuffix() {
        return "tphere";
    }

}
