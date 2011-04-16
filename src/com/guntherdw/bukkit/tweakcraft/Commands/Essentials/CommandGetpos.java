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

import com.guntherdw.bukkit.tweakcraft.Commands.Command;
import com.guntherdw.bukkit.tweakcraft.Exceptions.CommandException;
import com.guntherdw.bukkit.tweakcraft.Exceptions.CommandSenderException;
import com.guntherdw.bukkit.tweakcraft.Exceptions.CommandUsageException;
import com.guntherdw.bukkit.tweakcraft.Exceptions.PermissionsException;
import com.guntherdw.bukkit.tweakcraft.TweakcraftUtils;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

/**
 * @author GuntherDW
 */
public class CommandGetpos implements Command {
    public boolean executeCommand(CommandSender sender, String command, String[] args, TweakcraftUtils plugin)
            throws PermissionsException, CommandSenderException, CommandUsageException, CommandException {
        if(sender instanceof Player)
            if (!plugin.check((Player) sender , "getpos"))
                throw new PermissionsException(command);
        Location loc=null;

        /**
         * Permissions checking!
         */
        if(args.length==0 && !(sender instanceof Player))
            throw new CommandUsageException("If you're going to call this from a console i need a player to check!");
        if(args.length==0 && sender instanceof Player)
            loc = ((Player)sender).getLocation().clone();
        if(args.length>0 && sender instanceof Player)
            if(!plugin.check((Player)sender, "getposother"))
                throw new PermissionsException(command);

        if(args.length>0) {
            List<Player> plist = plugin.getServer().matchPlayer(args[0]);
            if(plist.size()==0) {
                throw new CommandUsageException(ChatColor.YELLOW + "Can't find player!");
            } else if(plist.size()>1) {
                throw new CommandUsageException(ChatColor.YELLOW + "Can't match player, got more than one result!");
            } else {
                loc = plist.get(0).getLocation().clone();
            }
        }

        if(loc != null)
        {
            Integer x, y, z, yaw, pitch;
            x = Math.round((float) loc.getX());
            y = Math.round((float) loc.getY());
            z = Math.round((float) loc.getZ());
            yaw = Math.round((float) loc.getYaw());
            pitch = Math.round((float) loc.getPitch());

            sender.sendMessage(ChatColor.YELLOW + "Pos X: " + x + " Y: " + y + " Z: " + z);
            sender.sendMessage(ChatColor.YELLOW + "Rotation: " + yaw + " Pitch: " + pitch);
            sender.sendMessage(ChatColor.YELLOW + "World: " + loc.getWorld().getName());
            String dir = plugin.getCompassDirection(loc.getYaw());
            double degreeRotation = ((loc.getYaw() - 90) % 360);
            if (degreeRotation < 0)
                degreeRotation += 360.0;

            sender.sendMessage(ChatColor.YELLOW + "Compass: " + dir + " (" + (Math.round(degreeRotation * 10) / 10.0) + ")");
        } else {
            throw new CommandException("This isn't supposed to happen!");
        }
        return true;
    }
}
