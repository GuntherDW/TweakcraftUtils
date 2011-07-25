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

import com.guntherdw.bukkit.tweakcraft.Commands.iCommand;
import com.guntherdw.bukkit.tweakcraft.Exceptions.CommandException;
import com.guntherdw.bukkit.tweakcraft.Exceptions.CommandSenderException;
import com.guntherdw.bukkit.tweakcraft.Exceptions.CommandUsageException;
import com.guntherdw.bukkit.tweakcraft.Exceptions.PermissionsException;
import com.guntherdw.bukkit.tweakcraft.TweakcraftUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * @author GuntherDW
 */
public class CommandMotd implements iCommand {
    public boolean executeCommand(CommandSender sender, String command, String[] args, TweakcraftUtils plugin)
            throws PermissionsException, CommandSenderException, CommandUsageException, CommandException {

        if(args.length==1 && args[0].equalsIgnoreCase("reload")) {
            if(sender instanceof Player)
                if(!(plugin.check((Player)sender, "motdreload")))
                    throw new PermissionsException(command);

            plugin.reloadMOTD();
            sender.sendMessage(ChatColor.YELLOW + "Reloading MOTD");
        } else {
            if(sender instanceof Player)
                if(!(plugin.check((Player)sender, "motd")))
                    throw new PermissionsException(command);
            
            for(String motdline : plugin.getMOTD()) {
                sender.sendMessage(motdline);
            }
        }
        return true;
    }

    @Override
    public String getPermissionSuffix() {
        return "motd";
    }
}
