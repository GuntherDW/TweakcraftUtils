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

import com.guntherdw.bukkit.tweakcraft.Commands.iCommand;
import com.guntherdw.bukkit.tweakcraft.Exceptions.CommandSenderException;
import com.guntherdw.bukkit.tweakcraft.Exceptions.CommandUsageException;
import com.guntherdw.bukkit.tweakcraft.Exceptions.PermissionsException;
import com.guntherdw.bukkit.tweakcraft.TweakcraftUtils;
import com.sk89q.worldedit.Vector;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.*;

import java.util.List;

/**
 * @author GuntherDW
 */
public class CommandIgnite implements iCommand {

    public boolean executeCommand(CommandSender sender, String command, String[] args, TweakcraftUtils plugin)
            throws PermissionsException, CommandSenderException, CommandUsageException {
        IgniteMode modus = IgniteMode.SELF;
        if (args.length > 0) {
            if (sender instanceof Player) {
                if (args[0].equalsIgnoreCase(((Player) sender).getName())) {
                    modus = IgniteMode.SELF;
                } else if (args[0].equals("*")) {
                    modus = IgniteMode.ALL;
                } else if (args[0].equalsIgnoreCase("mobs")) {
                    modus = IgniteMode.MOBS;
                } else {
                    modus = IgniteMode.OTHER;
                }
            } else {
                if (args[0].equals("*"))
                    modus = IgniteMode.ALL;
                else if (args[0].equalsIgnoreCase("mobs"))
                    modus = IgniteMode.MOBS;
                else
                    modus = IgniteMode.OTHER;
            }
        }
        if (modus == IgniteMode.SELF) {
            if (sender instanceof Player) {
                Player player = (Player) sender;
                player.setFireTicks(1500);
                player.sendMessage(ChatColor.YELLOW + "You have been ignited!");
            } else {
                sender.sendMessage("A console can't be set on fire, right?");
            }
        } else if (modus == IgniteMode.ALL) {
            if (sender instanceof Player) {
                if (!plugin.check((Player) sender, "extother"))
                    throw new PermissionsException(command);

                sender.sendMessage(ChatColor.YELLOW + "Is it hot in here or is it just me?");
                for (Player play : plugin.getServer().getOnlinePlayers()) {
                    play.setFireTicks(300);
                }
            }
        } else if (modus == IgniteMode.MOBS) {
            if (sender instanceof Player) {
                if (!plugin.check((Player) sender, "extother"))
                    throw new PermissionsException(command);
            }

            sender.sendMessage(ChatColor.YELLOW + "Oh my god it's hell alright!");

            CreatureType type = null;
            Integer range = 0;
            Vector playervector = null;
            if (sender instanceof Player) {
                Location loc = ((Player) sender).getLocation();
                playervector = new Vector(loc.getX(), loc.getY(), loc.getZ());
            }
            if (args.length > 1) {

                String mobName = "";
                if (args[1].length() > 2)
                    mobName = args[1].substring(0, 1).toUpperCase() + args[1].substring(1, args[1].length());
                type = CreatureType.fromName(mobName);
                if (type == null)
                    throw new CommandUsageException("Can't find mob with name " + mobName + "!");
                if (args.length > 2) {
                    if (sender instanceof Player) {
                        try {
                            range = Integer.parseInt(args[2]);
                        } catch (NumberFormatException e) {
                            throw new CommandUsageException("Invalid range!");
                        }
                    } else {
                        throw new CommandSenderException("Where do i need to base myself off of now huh?");
                    }
                }

            }

            boolean allowed = true;
            for (World w : plugin.getServer().getWorlds()) {
                for (LivingEntity ent : w.getLivingEntities()) {
                    if (ent instanceof Flying || ent instanceof Creature) {
                        allowed = true;
                        if(ent instanceof Wolf)
                        {
                            Wolf wolf = (Wolf) ent;
                            if(wolf.isAngry() || !wolf.isTamed())
                                allowed = true;
                            else
                                allowed = false;
                        }

                        if (type != null) {
                            if (type == CreatureType.fromName(ent.getClass().getCanonicalName().split("Craft")[1])) {
                                if (range != 0) // Range set, check range
                                {
                                    Location loc = ent.getLocation();
                                    Vector vec = new Vector(loc.getX(), loc.getY(), loc.getZ());

                                    if (playervector.distance(vec) < range) {
                                            if(allowed)
                                                ent.setFireTicks(300);
                                    }
                                } else { // No range set
                                    if(allowed)
                                        ent.setFireTicks(300);
                                }
                            }
                        } else { // every mob alive!
                            if(allowed)
                                ent.setFireTicks(300);
                        }
                    }
                }
            }
        } else if (modus == IgniteMode.OTHER) {
            if (sender instanceof Player) {
                if (!(plugin.check((Player) sender, "extother")))
                    throw new PermissionsException(command);
            }
            List<Player> p = plugin.getServer().matchPlayer(args[0]);
            if (p.size() != 1) {
                throw new CommandUsageException("Can't find the other player!");
            }
            Player player = p.get(0);
            player.setFireTicks(1500);
            sender.sendMessage(player.getDisplayName() + ChatColor.YELLOW + " has been ignited!");
        }
        return true;
    }

    private enum IgniteMode {
        SELF,
        OTHER,
        ALL,
        MOBS
    }

    @Override
    public String getPermissionSuffix() {
        return null;
    }
}
