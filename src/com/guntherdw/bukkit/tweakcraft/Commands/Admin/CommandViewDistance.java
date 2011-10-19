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

package com.guntherdw.bukkit.tweakcraft.Commands.Admin;

import com.guntherdw.bukkit.tweakcraft.Commands.iCommand;
import com.guntherdw.bukkit.tweakcraft.Exceptions.CommandException;
import com.guntherdw.bukkit.tweakcraft.Exceptions.CommandSenderException;
import com.guntherdw.bukkit.tweakcraft.Exceptions.CommandUsageException;
import com.guntherdw.bukkit.tweakcraft.Exceptions.PermissionsException;
import com.guntherdw.bukkit.tweakcraft.TweakcraftUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * @author GuntherDW
 */
public class CommandViewDistance implements iCommand {

    @Override
    public boolean executeCommand(CommandSender sender, String command, String[] realargs, TweakcraftUtils plugin)
            throws PermissionsException, CommandSenderException, CommandUsageException, CommandException {
        if(sender instanceof Player)
            if(!plugin.check((Player)sender, "viewdistance"))
                throw new PermissionsException(command);

        /* ArgumentParser ap = new ArgumentParser(realargs);
        String player = ap.getString("p", null);
        String[] args = ap.getNormalArgs();

        Player victim = null;
        
        if(player==null && !(sender instanceof Player))
            throw new CommandUsageException("You need to give me a player if you're going to use this over the console!");

        if(player != null)
        {
            if(!player.trim().equals("")) {
                victim = plugin.findPlayerasPlayer(player);
                if(victim == null)
                    throw new CommandException("Player not found");
            }
        } else {
            victim = (Player) sender;
        }

        
        if(args.length==0) {
            sender.sendMessage(ChatColor.GREEN+"["+ChatColor.AQUA+"ViewDistance"+ChatColor.GREEN+"] "+ victim.getDisplayName() + ChatColor.AQUA + " : "+victim.getViewDistance());
        } else {
            if(args[0].equalsIgnoreCase("reset")) {
                sender.sendMessage(ChatColor.YELLOW+ "Resetting " + victim.getDisplayName() + ChatColor.YELLOW + "'s ViewDistance!");
                victim.resetViewDistance();
            } else {
                Integer vdist = null;
                try{
                     vdist = Integer.parseInt(args[0]);
                } catch(NumberFormatException ex) {
                    throw new CommandUsageException("I need a number, not a string!");
                }

                if(vdist<3 || vdist > 15)
                    throw new CommandUsageException("Outside of the allowed ViewDistance limit!");

                sender.sendMessage(ChatColor.YELLOW + "Setting " +victim.getDisplayName()+ChatColor.YELLOW +"'s ViewDistance to "+vdist+"!");
                victim.setViewDistance(vdist);
            }
        }
        

        return true; */
        sender.sendMessage(ChatColor.GREEN+"Not working right now!");
        return true;
    }

    @Override
    public String getPermissionSuffix() {
        return "viewdistance";
    }
}
