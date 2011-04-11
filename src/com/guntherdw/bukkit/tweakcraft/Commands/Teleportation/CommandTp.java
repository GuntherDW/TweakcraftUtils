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

import com.guntherdw.bukkit.tweakcraft.Command;
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
public class CommandTp implements Command {

    public boolean executeCommand(CommandSender sender, String command, String[] args, TweakcraftUtils plugin)
            throws PermissionsException, CommandSenderException, CommandUsageException {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (!plugin.check(player, "tp"))
                throw new PermissionsException(command);

            if (args.length == 1) {
                if (plugin.getDonottplist().contains(player.getName()) && !plugin.check(player, "forcetp")) {
                    player.sendMessage(ChatColor.RED + "You can't tp when you don't allow others to tp to you!");
                } else {
                    List<Player> p = plugin.getServer().matchPlayer(args[0]);
                    if (p.size() != 1) {
                        player.sendMessage(ChatColor.YELLOW + "Can't find player!");
                    } else {
                        Player pto = p.get(0);
                        boolean refusetp = plugin.getDonottplist().contains(pto.getName());
                        if(plugin.getPlayerListener().getInvisplayers().contains(pto.getName()) && !plugin.check(player, "tpinvis"))
                        {
                            player.sendMessage(ChatColor.YELLOW + "Can't find player!");
                            plugin.getLogger().info("[TweakcraftUtils] " + player.getName() + " tried to tp to " + pto.getName() + " <invisible>!");
                            return true;
                        }
                        boolean override = false;
                        if (refusetp && (player.isOp() || plugin.check(player, "forcetp"))) {
                            override = true;
                        } else {
                            override = false;
                            /* if(refusetp)
                         override = true; */
                        }

                        if (pto.getName().equals(player.getName())) {
                            player.sendMessage(ChatColor.YELLOW + "You're already there!");
                        } else {
                            if (refusetp && !override) {
                                player.sendMessage(ChatColor.RED + "You don't have the correct permission to tp to " + pto.getName() + "!");
                                pto.sendMessage(player.getDisplayName() + ChatColor.YELLOW + " tried to tp to you!");
                            } else {
                                /* boolean teleportwarning = true;
                               if(teleportwarning) */
                                pto.sendMessage(plugin.getPlayerColor(player.getName(), false) + player.getName() + ChatColor.LIGHT_PURPLE + " Teleported to you!");
                                // This is supposed to be fixed nao?
                                /* if (!player.getWorld().getName().equals(pto.getWorld().getName())) {
                                    player.teleport(pto.getWorld().getSpawnLocation());
                                } */
                                player.teleport(pto);
                                if (override)
                                    player.sendMessage(ChatColor.RED + "Forced tp!");
                                plugin.getLogger().info("[TweakcraftUtils] " + player.getName() + " teleported to " + pto.getName() + "!");
                            }
                        }
                    }
                }
            } else if (args.length == 2) {
                if (!plugin.check(player, "tpfromto"))
                    throw new PermissionsException(command);
                this.tpfromto(plugin, sender, args[0], args[1]);

            } else {
                throw new CommandUsageException("Incorrect usage!");
            }
        } else if (args.length == 2) {
            this.tpfromto(plugin, sender, args[0], args[1]);
        } else {
            throw new CommandSenderException("You're the console, where do you think you're going?");
        }

        return true;
    }

    private void tpfromto(TweakcraftUtils plugin, CommandSender sender, String p1, String p2) {
        List<Player> pfind = plugin.getServer().matchPlayer(p1);
        Player pfrom, pto;
        if (pfind.size() == 1) {
            pfrom = pfind.get(0);
        } else {
            sender.sendMessage(ChatColor.DARK_GREEN + "Can't find source player!");
            return;
        }
        pfind = plugin.getServer().matchPlayer(p2);
        if (pfind.size() != 0) {
            pto = pfind.get(0);
        } else {
            sender.sendMessage(ChatColor.DARK_GREEN + "Can't find destination player!");
            return;
        }
        sender.sendMessage(ChatColor.GREEN + "Teleporting " + pfrom.getName() + " to " + pto.getName() + "!");
        String player = "";
        if (sender instanceof Player) {
            player = ((Player) sender).getName();
        } else {
            player = "CONSOLE";
        }
        plugin.getLogger().info("[TweakcraftUtils] " + player + " teleported " + pfrom.getName() + " to " + pto.getName() + "!");
        pfrom.teleport(pto);
    }

}
