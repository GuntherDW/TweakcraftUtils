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
                throws PermissionsException, CommandSenderException, CommandUsageException {

        if(sender instanceof Player)
            if(!plugin.check((Player)sender, getPermissionSuffix()))
                throw new PermissionsException(command);

        ArgumentParser ap = new ArgumentParser(realargs);
        String world = ap.getString("w", null);
        String[] args = ap.getNormalArgs();
        float x,y,z;

        
        World w = null;
        
        if(world!=null)
             w = plugin.getServer().getWorld(world);
        else {
            if(sender instanceof Player)
                w = ((Player)sender).getWorld();
        }

        if(w==null)
            throw new CommandUsageException("I didn't get a good world to fetch?");
        
        sender.sendMessage(ChatColor.YELLOW + "Spawn position for "+w.getName());
        Location spawn = w.getSpawnLocation();
        x = Math.round((float) spawn.getX());
        y = Math.round((float) spawn.getY());
        z = Math.round((float) spawn.getZ());
        sender.sendMessage(ChatColor.YELLOW+ "X:"+x+" Y:"+y+" Z:"+z);
        
        return true;
    }

    @Override
    public String getPermissionSuffix() {
        return "getspawn";
    }
}
