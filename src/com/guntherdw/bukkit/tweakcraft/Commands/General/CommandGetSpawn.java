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
import com.guntherdw.bukkit.tweakcraft.Exceptions.CommandException;
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

/**
 * @author GuntherDW
 */
public class CommandGetSpawn implements iCommand {
    @Override
    public boolean executeCommand(CommandSender sender, String command, String[] realargs, TweakcraftUtils plugin)
            throws PermissionsException, CommandSenderException, CommandUsageException, CommandException {


        ArgumentParser ap = new ArgumentParser(realargs);
        String world = ap.getString("w", null);
        String player = ap.getString("p", null);
        String[] args = ap.getNormalArgs();

        String permString = this.getPermissionSuffix();

        float x,y,z;
        Player pl = player!=null?plugin.findPlayerasPlayer(player):null;
        World w = null;
        boolean playerMode = pl!=null;

        if(sender instanceof Player) {

            // Default mode : self-spawn
            if(player==null && world==null) {
                playerMode = true;
                pl = (Player) sender;
            }

            if(playerMode && pl.getName().equals(((Player)sender).getName())) permString+=".self";
            if(!plugin.check((Player)sender, permString))
                throw new PermissionsException(command);
        }


        if(!playerMode) {
            if(world!=null)
                w = plugin.getServer().getWorld(world);
            else {
                if(sender instanceof Player)
                    w = ((Player)sender).getWorld();
            }
        }

        if(w==null && pl==null)
            throw new CommandUsageException("I didn't get a good world/player to fetch?");

        sender.sendMessage(ChatColor.YELLOW + "Spawn position for "+(w!=null?w.getName():pl.getDisplayName()));
        // Location spawn = w.getSpawnLocation();
        Location spawn = null;
        if(w  != null)  spawn = w.getSpawnLocation();
        if(pl != null) {
            spawn = pl.getBedSpawnLocation();
            if(spawn==null) {// NO BED
                throw new CommandException("That player doesn't have a custom bed spawn location.");
            }
        }

        x = Math.round((float) spawn.getX());
        y = Math.round((float) spawn.getY());
        z = Math.round((float) spawn.getZ());
        sender.sendMessage(ChatColor.YELLOW+ "X:"+x+" Y:"+y+" Z:"+z);
        if(playerMode)
            sender.sendMessage(ChatColor.YELLOW+"World : "+spawn.getWorld().getName());

        return true;
    }

    @Override
    public String getPermissionSuffix() {
        return "getspawn";
    }
}
