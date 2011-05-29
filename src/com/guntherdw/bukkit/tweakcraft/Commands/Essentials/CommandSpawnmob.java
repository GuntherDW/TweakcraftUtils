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

import com.guntherdw.bukkit.tweakcraft.Commands.Command;
import com.guntherdw.bukkit.tweakcraft.Exceptions.CommandException;
import com.guntherdw.bukkit.tweakcraft.Exceptions.CommandSenderException;
import com.guntherdw.bukkit.tweakcraft.Exceptions.CommandUsageException;
import com.guntherdw.bukkit.tweakcraft.Exceptions.PermissionsException;
import com.guntherdw.bukkit.tweakcraft.TweakcraftUtils;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.CreatureType;
import org.bukkit.entity.Player;

/**
 * @author GuntherDW
 */
public class CommandSpawnmob implements Command {
    public boolean executeCommand(CommandSender sender, String command, String[] args, TweakcraftUtils plugin)
            throws PermissionsException, CommandSenderException, CommandUsageException, CommandException {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (!plugin.check(player, "spawnmob"))
                throw new PermissionsException(command);

            Location loc = player.getTargetBlock(null, 200).getLocation();
            loc.setY(loc.getY() + 1); // Do not spawn them into the ground, silly!
            String mobName;
            String mobRider;
            Integer amount = 1;
            String victim;
            CreatureType type = null;
            CreatureType rider = null;
            Player victimplayer = player;

            if (args.length > 0) // only a mobname!
            {
                // mobName = args[0];
                mobName = "";
                amount = 1;
                if (args[0].length() > 2)
                    mobName = args[0].substring(0, 1).toUpperCase() + args[0].substring(1, args[0].length());
                type = CreatureType.fromName(mobName);
                if (type == null) {
                    throw new CommandUsageException("Can't find that creature!");
                }
                /* if(args.length > 1) // amount or rider
                {
                    try {
                        amount = Integer.parseInt(args[1]);
                    } catch(NumberFormatException e) {
                        amount = 1;
                        mobRider = args[1];
                        rider = CreatureType.fromName(mobRider);
                        if(rider == null)
                        {
                            throw new CommandUsageException("Can't find rider creature!");
                        }
                    }
                } */
                if (args.length > 1) // amount
                {
                    try {
                        amount = Integer.parseInt(args[1]);
                        if(amount>100) {
                            amount=100; // This should be more than enough before your client starts to lag!
                        }
                    } catch (NumberFormatException e) {
                        throw new CommandUsageException("I need an amount, not a string!");
                    }
                }
                if (args.length > 2) // victim!
                {
                    victim = plugin.findPlayer(args[2]);
                    victimplayer = plugin.getServer().getPlayer(victim);
                    if (victimplayer == null) {
                        throw new CommandUsageException("Can't find that player!");
                    }
                    loc = victimplayer.getLocation();
                }

                // We're finally here
                // Creature crea = new
                if (type != null) {
                    for (int x = 0; x < amount; x++)
                        victimplayer.getWorld().spawnCreature(loc, type);
                } else {
                    sender.sendMessage(ChatColor.YELLOW + "Error trying to spawn creature!");
                }
            }
        } else {
            throw new CommandSenderException("What were you trying to do? :3");
        }
        return true;
    }

    @Override
    public String getPermissionSuffix() {
        return "spawnmob";
    }
}
