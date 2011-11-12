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
import com.guntherdw.bukkit.tweakcraft.Exceptions.CommandSenderException;
import com.guntherdw.bukkit.tweakcraft.Exceptions.CommandUsageException;
import com.guntherdw.bukkit.tweakcraft.Exceptions.PermissionsException;
import com.guntherdw.bukkit.tweakcraft.Tools.ArgumentParser;
import com.guntherdw.bukkit.tweakcraft.TweakcraftUtils;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

/**
 * @author GuntherDW
 */
public class CommandTp implements iCommand {

    public boolean executeCommand(CommandSender sender, String command, String[] realargs, TweakcraftUtils plugin)
            throws PermissionsException, CommandSenderException, CommandUsageException {

        ArgumentParser ap = new ArgumentParser(realargs);
        String p1 = ap.getString("p", null);
        if(p1==null) p1 = ap.getString("f", null);
        String p2 = ap.getString("t", null);

        String[] args = ap.getUnusedArgs();

        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (!plugin.check(player, "tp"))
                throw new PermissionsException(command);

            if (args.length == 1 || p1 != null) {
                if (plugin.getDonottplist().contains(player.getName()) && !plugin.check(player, "forcetp")) {
                    player.sendMessage(ChatColor.RED + "You can't tp when you don't allow others to tp to you!");
                } else {
                    // List<Player> p = plugin.getServer().matchPlayer(args[0]);
                    if(p1==null)p1=args[0];
                    List<Player> players = plugin.findPlayerasPlayerList(p1);
                    Player p = null;
                    if(players.size()==1)
                        p = players.get(0);

                    if (p == null) {
                        player.sendMessage(ChatColor.YELLOW + "Can't find player!");
                    } else {
                        boolean refusetp = plugin.getDonottplist().contains(p.getName());
                        boolean tpsuccess = true;
                        if(plugin.getPlayerListener().getInvisplayers().contains(p.getName()))
                        {
                            if(!plugin.check(player, "tpinvis")) {
                                player.sendMessage(ChatColor.YELLOW + "Can't find player!");
                                plugin.getLogger().info("[TweakcraftUtils] " + player.getName() + " tried to tp to " + p.getName() + " <invisible>!");
                                return true;
                            } else {
                                player.sendMessage(ChatColor.AQUA + "Stealth player TP!");
                            }
                        }
                        boolean override = false;
                        if (refusetp && (player.isOp() || plugin.check(player, "forcetp"))) {
                            override = true;
                        } else {
                            override = false;
                            /* if(refusetp)
                         override = true; */
                        }

                        if(!player.getWorld().getName().equals(p.getWorld().getName())) {
                            if(!plugin.check(player, "worlds."+p.getWorld().getName()+".tp"))
                                throw new PermissionsException("You don't have permission to TP to someone in that world!");
                        }

                        if (p.getName().equals(player.getName())) {
                            player.sendMessage(ChatColor.YELLOW + "You're already there!");
                        } else {
                            if (refusetp && !override) {
                                player.sendMessage(ChatColor.RED + "You don't have the correct permission to tp to " + p.getDisplayName() + ChatColor.RED + "!");
                                p.sendMessage(player.getDisplayName() + ChatColor.YELLOW + " tried to tp to you!");
                            } else {
                                Location oldloc = player.getLocation();
                                tpsuccess = player.teleport(getTpLocation(p));
                                if(tpsuccess) {
                                    plugin.getTelehistory().addHistory(player.getName(), oldloc);
                                    p.sendMessage(player.getDisplayName() + ChatColor.LIGHT_PURPLE + " Teleported to you!");

                                    if (override)
                                        player.sendMessage(ChatColor.RED + "Forced tp!");
                                    plugin.getLogger().info("[TweakcraftUtils] " + player.getName() + " teleported to " + p.getName() + "!");
                                } else {
                                    p.sendMessage(player.getDisplayName() + ChatColor.LIGHT_PURPLE + " failed to teleport to you!");
                                    plugin.getLogger().info("[TweakcraftUtils] " + player.getName() + " failed to teleport to " + p.getName() + "!");
                                    // This doesn't happen, CraftBukkit has return true; in it's sources!
                                }
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
    private static int floor(double d) { int rt = (int) d; return rt > d ? rt-1 : rt; }
    private Location getTpLocation(Player player) {
    	Location loc = player.getLocation();
    	int x = floor(loc.getX()), y = floor(loc.getY())-1,  z = floor(loc.getZ());
    	for(int dx = -1; dx < 1;dx++)
    		for(int dz = -1; dz <= 1;dz++)
    			if(validSpot(loc.getWorld(),x+dx,y,z+dz))
    				return new Location(loc.getWorld(),x+dx+0.5F,y+2,z+dz+0.5F);
    	return loc;
    }
    private boolean validSpot(World world,int x, int y, int z) {
    	return world.getBlockTypeIdAt(x, y, z) != 0
            && world.getBlockTypeIdAt(x, y+1, z) == 0
            && world.getBlockTypeIdAt(x, y+2, z) == 0;
    }

    private void tpfromto(TweakcraftUtils plugin, CommandSender sender, String p1, String p2) {
        List<Player> pfind = plugin.findPlayerasPlayerList(p1);
        Player pfrom, pto;
        if (pfind.size() == 1) {
            pfrom = pfind.get(0);
        } else {
            sender.sendMessage(ChatColor.DARK_GREEN + "Can't find source player!");
            return;
        }
        
        pfind = plugin.findPlayerasPlayerList(p2);
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
        plugin.getTelehistory().addHistory(pfrom.getName(), pfrom.getLocation());
        pfrom.teleport(pto);
    }

    @Override
    public String getPermissionSuffix() {
        return "tp";
    }

}
