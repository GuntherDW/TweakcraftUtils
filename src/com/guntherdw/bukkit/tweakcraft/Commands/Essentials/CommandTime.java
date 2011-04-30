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
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * @author GuntherDW
 */
public class CommandTime implements Command {
    public boolean executeCommand(CommandSender sender, String command, String[] args, TweakcraftUtils plugin)
            throws PermissionsException, CommandSenderException, CommandUsageException, CommandException {
        if (sender instanceof Player)
            if (!plugin.check((Player) sender, "time"))
                throw new PermissionsException(command);

        if (args.length == 0) // Check the current time!
        {
            if (sender instanceof Player)
                sender.sendMessage("Current time in this world : " + (((Player) sender).getWorld().getTime() / 1000));
        } else {
            String settime = args[0];
            long timeset = 0;
            if (settime.equalsIgnoreCase("day")) {
                timeset = 0L;
            } else if (settime.equalsIgnoreCase("night")) {
                timeset = 13000L;
            }
            if (args.length > 1) { // World?
                World world = plugin.getServer().getWorld(args[1].toLowerCase());
                if (world != null) {
                    sender.sendMessage(ChatColor.YELLOW + "Setting time in world " + world.getName());
                    world.setTime(timeset);
                } else {
                    sender.sendMessage(ChatColor.YELLOW + "Can't find that world!");
                }
            } else {
                if (sender instanceof Player) {
                    Player p = (Player) sender;
                    sender.sendMessage(ChatColor.YELLOW + "Setting time in world " + p.getWorld().getName());
                    p.getWorld().setTime(timeset);
                }
            }
        }

        return true;
    }

    @Override
    public String getPermissionSuffix() {
        return "time";
    }
}
