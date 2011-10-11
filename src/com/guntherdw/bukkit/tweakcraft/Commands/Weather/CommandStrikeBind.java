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

import com.guntherdw.bukkit.tweakcraft.Commands.iCommand;
import com.guntherdw.bukkit.tweakcraft.Exceptions.CommandException;
import com.guntherdw.bukkit.tweakcraft.Exceptions.CommandSenderException;
import com.guntherdw.bukkit.tweakcraft.Exceptions.CommandUsageException;
import com.guntherdw.bukkit.tweakcraft.Exceptions.PermissionsException;
import com.guntherdw.bukkit.tweakcraft.Packages.LockdownLocation;
import com.guntherdw.bukkit.tweakcraft.TweakcraftUtils;
import com.sk89q.worldedit.blocks.ItemType;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

/**
 * @author GuntherDW
 */
public class CommandStrikeBind implements iCommand {
    @Override
    public boolean executeCommand(CommandSender sender, String command, String[] args, TweakcraftUtils plugin)
            throws PermissionsException, CommandSenderException, CommandUsageException, CommandException {
        if(sender instanceof Player) {
            if(!plugin.check((Player)sender, "weather"))
                throw new PermissionsException(command);

            Player player = (Player) sender;
            boolean left = false;
            if(args.length==0) {
                left = false;
            } else if(args[0].equalsIgnoreCase("true")) {
                left = true;
            } else if(args[0].equalsIgnoreCase("reset")) {
                if(plugin.getConfigHandler().getLsbindmap().containsKey(player.getName())) {
                    plugin.getConfigHandler().getLsbindmap().remove(player.getName());
                }
                sender.sendMessage(ChatColor.GOLD + "Strike tool unbound!");
                return true;
            } else if(args[0].equalsIgnoreCase("lockdown")) {
                
                Location lockdownloc;
                LockdownLocation lockdownlocation = null;
                if(args.length>1) {
                    if(args[1].equalsIgnoreCase("reset")) {
                        if(plugin.getConfigHandler().getLockdowns().containsKey(player.getName())) {
                            plugin.getConfigHandler().getLockdowns().remove(player.getName());
                        }
                        sender.sendMessage(ChatColor.YELLOW+"Lockdown target reset!");
                    } else {
                        List<Player> targeta = plugin.getServer().matchPlayer(args[1]);

                        if(targeta.size()!=1) {
                            throw new CommandException("Can't find player!");
                        } else {
                            lockdownlocation = new LockdownLocation(null, targeta.get(0), true);
                            sender.sendMessage(ChatColor.YELLOW+"Lockdown set to "+targeta.get(0).getDisplayName()+ChatColor.YELLOW+"!");
                        }
                    }
                } else {
                    lockdownloc = player.getTargetBlock((HashSet<Byte>)null, 200).getLocation();
                    lockdownloc.setY(lockdownloc.getY()+1);
                    lockdownlocation = new LockdownLocation(lockdownloc, null, false);
                    sender.sendMessage(ChatColor.YELLOW+"Lockdown to current cursor position!");
                }
                if(lockdownlocation!=null) {
                    plugin.getConfigHandler().getLockdowns().put(player.getName(), lockdownlocation);
                }
                return true;
                
            }

            int item = player.getItemInHand().getTypeId();
            if (item > 0 && item < 255) {
                player.sendMessage(ChatColor.GOLD + "Can't bind to "+ ItemType.toName(item)+". Can't use blocks!");
            } else if (item == 263 || item == 348 || item == 0) {
                player.sendMessage(ChatColor.GOLD + "Can't bind to "+ItemType.toName(item)+". Item is nog usable!");
            } else {
                player.sendMessage(ChatColor.GOLD + "Strike tool bound to "+ItemType.toName(item)+"."+(left?" (left click)":""));
                Map<Integer, Boolean> itemmap = new HashMap<Integer, Boolean>();
                itemmap.put(item, left);
                plugin.getConfigHandler().getLsbindmap().put(player.getName(), itemmap);
            }
        } else {
            throw new CommandSenderException("What's there to bind to?");
        }
        return true;
    }

    @Override
    public String getPermissionSuffix() {
        return "weather";
    }
}
