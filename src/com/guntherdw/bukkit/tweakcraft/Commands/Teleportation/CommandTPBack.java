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

import com.guntherdw.bukkit.tweakcraft.Commands.iCommand;
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
public class CommandTPBack implements iCommand {
    @Override
    public boolean executeCommand(CommandSender sender, String command, String[] args, TweakcraftUtils plugin)
            throws PermissionsException, CommandSenderException, CommandUsageException, CommandException {
        if(sender instanceof Player) {
            Player player = (Player) sender;
            if(!plugin.check(player, "tpback"))
                throw new PermissionsException(command);
            if(plugin.getConfigHandler().enableTPBack) {

                boolean go = true;

                if(args.length>0) {
                    if(args[0].equalsIgnoreCase("clear")) {
                        player.sendMessage(ChatColor.GOLD+"Cleaning your TPBack history!");
                        plugin.getTelehistory().clearHistory(player.getName());
                        go=false;
                    } else if(args[0].equalsIgnoreCase("remove")) {
                        if(plugin.getTelehistory().getRemaining(player.getName())>0) {
                            player.sendMessage(ChatColor.YELLOW + "Removing last tpback line.");
                            plugin.getTelehistory().removeLast(player.getName());
                        } else {
                            player.sendMessage(ChatColor.YELLOW + "You don't have any tpback lines!");
                        }
                        go=false;
                    }
                }


                if(go){
                    boolean atOrigin = plugin.getTelehistory().atOrigin(player.getName());
                    int offSet = plugin.getTelehistory().getOffset(player.getName());
                    int size = plugin.getTelehistory().getRemaining(player.getName()) -1;
                    int pos = size - (offSet>0?offSet:0);
                    // Location back = plugin.getTelehistory().getLastEntry(player.getName(), false);
                    Location back = plugin.getTelehistory().get(player.getName(), pos, true); // getLastEntry(player.getName(), false)
                    Location oldLocation = player.getLocation().clone();
                    if(back == null) {
                        if(atOrigin) player.sendMessage(ChatColor.GOLD+"You don't have any history issues left!");
                        else player.sendMessage(ChatColor.GOLD+"You don't have any history issues yet!");

                    } else {
                        player.sendMessage(ChatColor.GOLD+"Teleporting you back to your previous position!");
                        if(back.getY()==130) {
                            player.sendMessage(ChatColor.GOLD+"Sending you to Y:130 because you were either too high or too low!");
                        }

                        boolean success = player.teleport(back);
                        if(success) {
                            if(atOrigin) {
                                player.sendMessage(ChatColor.GOLD+"You are at your starting point!");
                            }
                            // System.out.println("offset : "+offSet);
                            if(offSet==-1) {
                                System.out.println("Adding "+oldLocation);
                                plugin.getTelehistory().addHistory(player.getName(), oldLocation);
                                plugin.getTelehistory().setHistoryOffset(player.getName(), 2);
                            }

                        } else {
                            player.sendMessage(ChatColor.RED+"tpback failure, tpback line NOT removed!");
                            player.sendMessage(ChatColor.RED+"To remove this line, type /tpback remove");
                        }
                    }
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
