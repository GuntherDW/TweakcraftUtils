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

import java.util.List;

/**
 * @author GuntherDW
 */
public class CommandWorld implements Command {
    public boolean executeCommand(CommandSender sender, String command, String[] args, TweakcraftUtils plugin)
            throws PermissionsException, CommandSenderException, CommandUsageException, CommandException {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (args.length > 0) {
                List<World> worlds = plugin.getServer().getWorlds();
                String worldname = args[0];
                Integer worldnum;
                World world;
                try {
                    worldnum = Integer.parseInt(worldname);
                    world = worlds.get(worldnum);
                } catch (NumberFormatException e) {
                    world = plugin.getServer().getWorld(worldname);
                } catch (IndexOutOfBoundsException e) {
                    throw new CommandUsageException(ChatColor.YELLOW + "Can't find that world!");
                }
                if (world != null) {
                    if (!plugin.check(player, "worlds." + world.getName()))
                        throw new PermissionsException(command);
                    else
                        player.teleport(world.getSpawnLocation());
                } else {
                    sender.sendMessage(ChatColor.YELLOW + "Can't find that world!");
                }
            } else {
                throw new CommandUsageException("I need a world to tp you to!");
            }
        } else {
            throw new CommandSenderException("What do you think you are doing?");
        }
        return true;
    }
}
