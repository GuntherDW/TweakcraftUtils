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

package com.guntherdw.bukkit.tweakcraft.Commands.General;

import com.guntherdw.bukkit.tweakcraft.Commands.Command;
import com.guntherdw.bukkit.tweakcraft.Exceptions.CommandException;
import com.guntherdw.bukkit.tweakcraft.Exceptions.CommandSenderException;
import com.guntherdw.bukkit.tweakcraft.Exceptions.CommandUsageException;
import com.guntherdw.bukkit.tweakcraft.Exceptions.PermissionsException;
import com.guntherdw.bukkit.tweakcraft.TweakcraftUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;


/**
 * @author GuntherDW
 */
public class CommandEject implements Command {
    @Override
    public boolean executeCommand(CommandSender sender, String command, String[] args, TweakcraftUtils plugin)
            throws PermissionsException, CommandSenderException, CommandUsageException, CommandException {
        if(sender instanceof Player) {
            Player player = (Player) sender;
            if(args.length>0 && args[0].equalsIgnoreCase("self")) {
                for(Entity ent : player.getWorld().getChunkAt(player.getLocation()).getEntities()) {
                    // Entity passenger;
                    if(ent.getPassenger() != null && ent.getPassenger().equals(player)) {
                        player.sendMessage(ChatColor.GOLD + "Ejecting yourself from whatever you were sitting on!");
                        ent.eject();
                    }
                }
            } else {
                // player.sendMessage(ChatColor.GOLD + "Ejecting whatever's on you!");
                if(player.eject()) {
                    player.sendMessage(ChatColor.GOLD + "Successfully booted stuff!");
                } else {
                    player.sendMessage(ChatColor.GOLD + "There was nothing on you!");
                }
            }
        } else {
            throw new CommandSenderException("Eject what, the server?");
        }
        return true;
    }

    @Override
    public String getPermissionSuffix() {
        return null; // Now why would there need to be a permission to eject someone from you?
    }
}
