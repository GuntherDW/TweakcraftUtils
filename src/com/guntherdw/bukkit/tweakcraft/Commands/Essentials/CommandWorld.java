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
import com.guntherdw.bukkit.tweakcraft.DataSources.PersistenceClass.PlayerOptions;
import com.guntherdw.bukkit.tweakcraft.Exceptions.CommandException;
import com.guntherdw.bukkit.tweakcraft.Exceptions.CommandSenderException;
import com.guntherdw.bukkit.tweakcraft.Exceptions.CommandUsageException;
import com.guntherdw.bukkit.tweakcraft.Exceptions.PermissionsException;
import com.guntherdw.bukkit.tweakcraft.TweakcraftUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

/**
 * @author GuntherDW
 */
public class CommandWorld implements iCommand {
    public boolean executeCommand(CommandSender sender, String command, String[] args, TweakcraftUtils plugin)
            throws PermissionsException, CommandSenderException, CommandUsageException, CommandException {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (args.length > 0) {
                List<World> worlds = plugin.getServer().getWorlds();
                String worldname = args[0];
                Integer worldnum;
                World world;
                try {
                    worldnum = Integer.parseInt(worldname);
                    world = worlds.get(worldnum);
                } catch (NumberFormatException e) {
                    world = plugin.getServer().getWorld(worldname);
                } catch (IndexOutOfBoundsException e) {
                    throw new CommandUsageException(ChatColor.YELLOW + "Can't find that world!");
                }
                if (world != null) {
                    if(world.getName().equals(player.getWorld().getName())) {
                        throw new CommandUsageException("You already are on that world!");
                    }
                    if (!plugin.check(player, "worlds." + world.getName())) {
                        throw new PermissionsException(command);
                    }
                    else {
                        Location oldlocation = player.getLocation();
                        Location toLocation = world.getSpawnLocation();
                        String locString = "";
                        plugin.getTelehistory().addHistory(player.getName(), oldlocation);
                        if(plugin.getConfigHandler().enablePersistence) {
                            List<PlayerOptions> plist = plugin.getDatabase().find(PlayerOptions.class).where().ieq("name", player.getName()).ieq("optionname", "worldpos").findList();
                            PlayerOptions po = null;
                            if(plist.size()>0) {
                                for(PlayerOptions popts : plist) {
                                    Location tloc = this.parseLocationString(popts.getOptionvalue());
                                    if(tloc!=null) {
                                        if(tloc.getWorld().getName().equals(player.getWorld().getName())) {
                                            po = popts;
                                        } else if(tloc.getWorld().getName().equals(world.getName())) {
                                            toLocation = tloc;
                                        }
                                    }
                                }
                            }
                            if(po==null) {
                                po = new PlayerOptions();
                                po.setOptionname("worldpos");
                                po.setName(player.getName());
                            }
                            po.setOptionvalue(this.exportLocationString(player.getLocation()));
                            plugin.getDatabase().save(po);
                        }
                        player.teleport(toLocation);
                    }
                } else {
                    sender.sendMessage(ChatColor.YELLOW + "Can't find that world!");
                }
            } else {
                throw new CommandUsageException("I need a world to tp you to!");
            }
        } else {
            throw new CommandSenderException("What do you think you are doing?");
        }
        return true;
    }

    @Override
    public String getPermissionSuffix() {
        return null;
    }

    // x=0.25,y=30,z=30,w=survival,yaw=40,pit=40
    public Location parseLocationString(String locstring) {
        try{
            String[] stuff = locstring.split(",");
            Double x = Double.parseDouble(stuff[0].substring(2));
            Double y = Double.parseDouble(stuff[1].substring(2));
            Double z = Double.parseDouble(stuff[2].substring(2));
            World world = Bukkit.getServer().getWorld(stuff[3].substring(2));
            Float yaw = Float.parseFloat(stuff[4].substring(4));
            Float pitch = Float.parseFloat(stuff[5].substring(4));
            if(world==null) return null;

            return new Location(world, x, y, z, yaw, pitch);


        } catch(NumberFormatException ex) {
            return null;
        }
    }
    
    public String exportLocationString(Location loc) {
        return "x="+loc.getX()+",y="+loc.getY()+",z="+loc.getZ()+",w="+loc.getWorld().getName()+",yaw="+loc.getYaw()+",pit="+loc.getPitch();
        // return null;
    }
}
