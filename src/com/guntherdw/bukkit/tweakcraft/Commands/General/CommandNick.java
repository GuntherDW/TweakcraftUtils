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
import org.bukkit.entity.Player;

import java.util.List;

/**
 * @author GuntherDW
 */
public class CommandNick implements Command {
    @Override
    public boolean executeCommand(CommandSender sender, String command, String[] args, TweakcraftUtils plugin)
            throws PermissionsException, CommandSenderException, CommandUsageException, CommandException {
        if(sender instanceof Player) {
            if(!plugin.check((Player)sender, "nick"))
                throw new PermissionsException(command);

            Player player = (Player) sender;
            if(args.length==1) {
                if(args[0].equalsIgnoreCase("reset")) {
                    sender.sendMessage(ChatColor.GOLD + "Resetting nick to your real name.");
                    plugin.getPlayerListener().removeNick(player.getName());
                } else {
                    sender.sendMessage(ChatColor.GOLD + "Setting nick to : "+args[0]);
                    List<Player> find = plugin.getServer().matchPlayer(args[0]);
                    for(Player f : find) {
                        if(f.getName().toLowerCase().equals(args[0].toLowerCase()))
                            throw new CommandException("Nick is already taken!");
                    }
                    if(!plugin.getPlayerListener().nickTaken(args[0]))
                        plugin.getPlayerListener().setNick(player.getName(), args[0]);
                    else
                        throw new CommandException("Nick is already taken!");
                }
            } else if(args.length==2) {
                if(!plugin.check(player, "nick.other"))
                    throw new PermissionsException(command);

                List<Player> search = plugin.getServer().matchPlayer(args[0]);
                if(search.size()!=1) {
                    throw new CommandException("Can't find the other player!");
                } else {
                    if(args[1].equalsIgnoreCase("reset")) {
                        sender.sendMessage("Resetting "+search.get(0).getName()+"'s nick");
                        plugin.getPlayerListener().removeNick(search.get(0).getName());
                    } else {
                        sender.sendMessage(ChatColor.GOLD + "Setting "+search.get(0).getName()+"'s nick to "+args[1]);
                        List<Player> find = plugin.getServer().matchPlayer(args[0]);
                        for(Player f : find) {
                            if(f.getName().toLowerCase().equals(args[1].toLowerCase()))
                                throw new CommandException("Nick is already taken!");
                        }
                        if(!plugin.getPlayerListener().nickTaken(args[1]))
                            plugin.getPlayerListener().setNick(search.get(0).getName(), args[1]);
                        else
                            throw new CommandException("Nick is already taken!");
                    }
                }
            } else {
                throw new CommandUsageException("I need a nick!");
            }
        } else {
            throw new CommandSenderException("Not yet implemented!");
        }
        return true;
    }

    @Override
    public String getPermissionSuffix() {
        return "nick";
    }
}
