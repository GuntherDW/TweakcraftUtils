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

import com.guntherdw.bukkit.tweakcraft.Command;
import com.guntherdw.bukkit.tweakcraft.Exceptions.CommandSenderException;
import com.guntherdw.bukkit.tweakcraft.Exceptions.CommandUsageException;
import com.guntherdw.bukkit.tweakcraft.Exceptions.PermissionsException;
import com.guntherdw.bukkit.tweakcraft.TweakcraftUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author GuntherDW
 */
public class CommandSeen implements Command {

    public boolean executeCommand(CommandSender sender, String command, String[] args, TweakcraftUtils plugin)
            throws PermissionsException, CommandSenderException, CommandUsageException {
        if(args.length<1)
        {
            throw new CommandUsageException("You did not specify a name!");
        }
        if(plugin.getSeenconfig() != null)
        {
            if(plugin.getServer().getPlayer(args[0]) != null)
            {
                sender.sendMessage(ChatColor.GOLD + args[0] + " is online right now!");
            } else {
                String seen = plugin.getSeenconfig().getString(args[0].toLowerCase(), "");
                // plugin.getSeenconfig().get
                if(seen.equals(""))
                    sender.sendMessage("I haven't seen "+args[0]+" yet!");
                else
                {
                        SimpleDateFormat smf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                        Date datelastseen = new Date(Long.parseLong(seen));
                        String lastseen = smf.format(datelastseen);
                        sender.sendMessage(ChatColor.GOLD + args[0] + " was last seen on "+lastseen+"!");
                }
            }
        } else {
            throw new CommandUsageException("Player history is disabled!");
        }
        return true;
    }
}
