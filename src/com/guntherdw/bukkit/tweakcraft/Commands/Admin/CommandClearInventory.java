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

package com.guntherdw.bukkit.tweakcraft.Commands.Admin;

import com.guntherdw.bukkit.tweakcraft.Commands.Command;
import com.guntherdw.bukkit.tweakcraft.Exceptions.CommandException;
import com.guntherdw.bukkit.tweakcraft.Exceptions.CommandSenderException;
import com.guntherdw.bukkit.tweakcraft.Exceptions.CommandUsageException;
import com.guntherdw.bukkit.tweakcraft.Exceptions.PermissionsException;
import com.guntherdw.bukkit.tweakcraft.TweakcraftUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

/**
 * @author GuntherDW
 */
public class CommandClearInventory implements Command {
    public boolean executeCommand(CommandSender sender, String command, String[] args, TweakcraftUtils plugin)
            throws PermissionsException, CommandSenderException, CommandException, CommandUsageException {
        if(sender instanceof Player)
            if(!plugin.check((Player)sender, "clearinventory"))
                throw new PermissionsException(command);

        Player victim = null;

        if(args.length == 0 && sender instanceof Player)
            victim = (Player) sender;
        else if(args.length == 0 && !(sender instanceof Player))
            sender.sendMessage("Can't clear the console's inventory when it doesn't need one...");
        else if(args.length!=0) {
            List<Player> p = plugin.getServer().matchPlayer(args[0]);
            if(p.size()==1) { victim = p.get(0); } else { sender.sendMessage(ChatColor.YELLOW + "Can't find player!"); }
        }

        if(victim != null) {
            sender.sendMessage(ChatColor.YELLOW+"Clearning "+victim.getDisplayName()+ChatColor.YELLOW+"'s inventory!");
            victim.getInventory().clear();
        } else {
            sender.sendMessage(ChatColor.RED+"Victim is null, this isn't supposed to happen!");
        }
        return true;
    }
}
