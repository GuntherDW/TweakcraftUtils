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
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * @author GuntherDW
 */
public class CommandTele implements Command {

    public boolean executeCommand(CommandSender sender, String command, String[] args, TweakcraftUtils plugin)
            throws PermissionsException, CommandSenderException, CommandUsageException {
        if (sender instanceof Player) {
            if (!plugin.check((Player) sender, "tele")) {
                throw new PermissionsException(command);
            }
            Player player = (Player) sender;
            Integer x = null;
            Integer y = null;
            Integer z = null;
            World world = null;
            
            if(args.length == 0) {
                throw new CommandUsageException("Usage: /tele up|x z (y)");
            } else if(args.length==1 && args[0].equalsIgnoreCase("up")) {
                Location l = player.getLocation().clone();
                x = (int)l.getX();
                y = 129;
                z = (int)l.getZ();
                world = l.getWorld();
            } else {

                x = Integer.parseInt(args[0]);
                z = Integer.parseInt(args[1]);
                if (args.length == 3) {
                    y = Integer.parseInt(args[2]);
                } else {
                    y = 129;
                }
                if (args.length == 4) {
                    try {
                        if (plugin.getServer().getWorlds().contains(plugin.getServer().getWorld(args[3]))) {
                            world = plugin.getServer().getWorld(args[3]);
                        } else {
                            world = ((Player) sender).getWorld();
                        }
                    } catch (Exception e) {
                        throw new CommandUsageException("Can't find world with name " + args[3]);
                    }
                } else {
                    world = ((Player) sender).getWorld();
                }
            }
            Location loc = new Location(world, x.doubleValue(), y.doubleValue(), z.doubleValue());
            ((Player) sender).teleport(loc);


        } else {
            throw new CommandSenderException("You need to be a player to teleport!");
        }
        return true;
    }

    @Override
    public String getPermissionSuffix() {
        return "tele";
    }
}
