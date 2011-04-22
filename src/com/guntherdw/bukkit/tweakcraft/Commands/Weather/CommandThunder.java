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

package com.guntherdw.bukkit.tweakcraft.Commands.Weather;

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
public class CommandThunder implements Command {
    @Override
    public boolean executeCommand(CommandSender sender, String command, String[] args, TweakcraftUtils plugin)
            throws PermissionsException, CommandSenderException, CommandUsageException, CommandException {

        if(!(sender instanceof Player) && args.length < 1) {
            throw new CommandUsageException("I need a world!");
        } else if(sender instanceof Player) {
            if(!plugin.check((Player) sender, "weather")) {
                throw new PermissionsException(command);
            }
        }

        if(args.length==0 && sender instanceof Player) {
            boolean dur = ((Player)sender).getWorld().isThundering();
            sender.sendMessage(ChatColor.YELLOW+"Thunder is now "+(dur?"on":"off")+"!");
        } else if(args.length == 1 && sender instanceof Player) {
            if(args[0].equalsIgnoreCase("on") || args[0].equalsIgnoreCase("off")) {
                boolean onoff = args[0].equalsIgnoreCase("on");
                sender.sendMessage(ChatColor.YELLOW + "Setting weather in world!");
                ((Player)sender).getWorld().setThundering(onoff);
            } else if(plugin.getServer().getWorld(args[0]) != null) {

                World w = plugin.getServer().getWorld(args[0]);
                boolean dur = w.isThundering();
                sender.sendMessage(ChatColor.YELLOW+"Thunder is now "+(dur?"on":"off")+"!");
            } else {
                throw new CommandUsageException("on/off or worldname please!");
            }
        } else if(args.length > 2) {
            World w = plugin.getServer().getWorld(args[0]);
            if(w==null) {
                throw new CommandUsageException("Can't find world!");
            } else {
                if(args[1].equalsIgnoreCase("on") || args[1].equalsIgnoreCase("off")) {
                    boolean onoff = args[1].equalsIgnoreCase("on");
                    sender.sendMessage(ChatColor.YELLOW + "Setting thunder in that world!");
                    w.setThundering(onoff);
                } else {
                    throw new CommandUsageException("No thunder mode specified!");
                }
            }
        }
        

        return true;
    }
}
