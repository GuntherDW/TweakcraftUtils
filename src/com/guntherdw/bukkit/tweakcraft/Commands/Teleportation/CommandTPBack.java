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
import com.guntherdw.bukkit.tweakcraft.Exceptions.CommandException;
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
public class CommandTPBack implements Command {
    @Override
    public boolean executeCommand(CommandSender sender, String command, String[] args, TweakcraftUtils plugin)
            throws PermissionsException, CommandSenderException, CommandUsageException, CommandException {
        if(sender instanceof Player) {
            Player player = (Player) sender;
            if(!plugin.check(player, "tpback"))
                throw new PermissionsException(command);
            if(plugin.getConfigHandler().enableTPBack) {
                Location back = plugin.getTelehistory().getLastEntry(player.getName());
                if(back == null) {
                    player.sendMessage(ChatColor.GOLD+"You don't have any history issues yet!");
                } else {
                    player.sendMessage(ChatColor.GOLD+"Teleporting you back to your previous position!");
                    player.teleport(back);
                }
            } else {
                player.sendMessage(ChatColor.RED+"TP History isn't enabled!");
            }
        } else {
            throw new CommandSenderException("Consoles need tp history nowadays?");
        }
        return true;
    }

    @Override
    public String getPermissionSuffix() {
        return "tpback";
    }
}