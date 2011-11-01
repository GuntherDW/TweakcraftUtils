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

package com.guntherdw.bukkit.tweakcraft.Commands.Debug;


import com.guntherdw.bukkit.tweakcraft.Commands.iCommand;
import com.guntherdw.bukkit.tweakcraft.Exceptions.CommandException;
import com.guntherdw.bukkit.tweakcraft.Exceptions.CommandSenderException;
import com.guntherdw.bukkit.tweakcraft.Exceptions.CommandUsageException;
import com.guntherdw.bukkit.tweakcraft.Exceptions.PermissionsException;
import com.guntherdw.bukkit.tweakcraft.TweakcraftUtils;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * @author sk89q, GuntherDW
 */
public class CommandDebug implements iCommand {
    @Override
    public boolean executeCommand(final CommandSender sender, String command, String[] args, TweakcraftUtils plugin)
            throws PermissionsException, CommandSenderException, CommandUsageException, CommandException {
        if(sender instanceof Player)
            if(!plugin.check((Player) sender, "debug"))
                throw new PermissionsException(command);

        if(args.length>0 && args[0].equalsIgnoreCase("clock")) {
            int expected = 5;

            if (args.length == 2) {
                Integer iex;
                try{
                    iex = Integer.parseInt(args[1]);
                } catch(NumberFormatException ex) {
                    iex = 1;
                }
                expected = Math.min(30, Math.max(1, iex));
            }

            final World world = plugin.getServer().getWorlds().get(0);
            final double expectedTime = expected * 1000;
            final double expectedSecs = expected;
            final int expectedTicks = 20 * (int)expectedSecs;
            final long start = System.currentTimeMillis();
            final long startTicks = world.getFullTime();

            sender.sendMessage(ChatColor.DARK_RED
                    + "Timing clock test for " + expected + " IN-GAME seconds...");
            sender.sendMessage(ChatColor.DARK_RED
                    + "DO NOT CHANGE A WORLD'S TIME OR PERFORM A HEAVY OPERATION.");

            Runnable task = new Runnable() {
                public void run() {
                    long now = System.currentTimeMillis();
                    long nowTicks = world.getFullTime();

                    long elapsedTime = now - start;
                    double elapsedSecs = elapsedTime / 1000.0;
                    int elapsedTicks = (int) (nowTicks - startTicks);

                    double error = (expectedTime - elapsedTime) / elapsedTime * 100;
                    double grumror = (expectedTime - elapsedTime) / expectedTime * 100;
                    double clockRate = elapsedTicks / elapsedSecs;

                    if (expectedTicks != elapsedTicks) {
                        sender.sendMessage(ChatColor.DARK_RED
                                + "Warning: Bukkit scheduler inaccurate; expected "
                                + expectedTicks + ", got " + elapsedTicks);
                    }

                    if (Math.round(clockRate) == 20) {
                        sender.sendMessage(ChatColor.YELLOW + "Clock test result: "
                                + ChatColor.GREEN + "EXCELLENT");
                    } else {
                        if (elapsedSecs > expectedSecs) {
                            if (clockRate < 19) {
                                sender.sendMessage(ChatColor.YELLOW + "Clock test result: "
                                        + ChatColor.DARK_RED + "CLOCK BEHIND");
                                sender.sendMessage(ChatColor.DARK_RED
                                        + "WARNING: You have potential block respawn issues.");
                            } else {
                                sender.sendMessage(ChatColor.YELLOW + "Clock test result: "
                                        + ChatColor.DARK_RED + "CLOCK BEHIND");
                            }
                        } else {
                            sender.sendMessage(ChatColor.YELLOW + "Clock test result: "
                                    + ChatColor.DARK_RED + "CLOCK AHEAD");
                        }
                    }

                    sender.sendMessage(ChatColor.GRAY + "Expected time elapsed: " + expectedTime + "ms");
                    sender.sendMessage(ChatColor.GRAY + "Time elapsed: " + elapsedTime + "ms");
                    sender.sendMessage(ChatColor.GRAY + "Error: " + error + "%");
                    sender.sendMessage(ChatColor.GRAY + "Grumror: " + grumror + "%");
                    sender.sendMessage(ChatColor.GRAY + "Actual clock rate: " + clockRate + " ticks/sec");
                    sender.sendMessage(ChatColor.GRAY + "Expected clock rate: 20 ticks/sec");
                }
            };
            plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, task, expectedTicks);
        } else {
            sender.sendMessage(ChatColor.GOLD + "Possible modes: clock");
        }
        return true;
    }

    @Override
    public String getPermissionSuffix() {
        return "debug";
    }
}
