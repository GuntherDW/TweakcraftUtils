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
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

/**
 * @author GuntherDW
 */
public class CommandLightning implements Command {
    @Override
    public boolean executeCommand(CommandSender sender, String command, String[] args, TweakcraftUtils plugin)
            throws PermissionsException, CommandSenderException, CommandUsageException, CommandException {
        if(sender instanceof Player) {
            if(!plugin.check((Player)sender, "weather"))
                throw new PermissionsException(command);
        }
        Location loc = null;
        if(args.length==0 && sender instanceof Player) {
            loc = ((Player)sender).getTargetBlock(null, 200).getLocation();
            loc.setY(loc.getY()+1);
            // sender.sendMessage(ChatColor.RED+"*CCCRRREEAAAAAAAACK*");
        } else if(args.length == 1) {
            List<Player> players = plugin.getServer().matchPlayer(args[0]);
            if(players.size()!=1) { throw new CommandUsageException("Can't find player!"); }
            loc = players.get(0).getLocation();
            sender.sendMessage(ChatColor.RED+"I hope "+players.get(0).getName()+ChatColor.RED+" wasn't standing under a tree!");
        }

        if(loc != null)
        {
            loc.getWorld().strikeLightning(loc);
        }
        return true;
    }
}
