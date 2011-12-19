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
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

/**
 * @author GuntherDW
 */
public class CommandAddExperience implements iCommand {

    @Override
    public boolean executeCommand(CommandSender sender, String command, String[] args, TweakcraftUtils plugin)
            throws PermissionsException, CommandSenderException, CommandUsageException, CommandException {
        if(sender instanceof Player)
            if(!plugin.check((Player)sender, getPermissionSuffix()))
                throw new PermissionsException(command);

        ArgumentParser ap = new ArgumentParser(args);
        String playerString =  ap.getString("p", null);
        Integer level = ap.getInteger("l", null);
        String[] ars = ap.getUnusedArgs();
        
        if(playerString==null) {
            if(!(sender instanceof Player))
                throw new CommandUsageException("Who do i have to give experience to?");
            else
                playerString = ((Player) sender).getName();
        }
        
        List<Player> p = plugin.findPlayerasPlayerList(playerString);
        if(p.size()!=1)
            throw new CommandException("Player not found!");
        if(ars.length<1 && level == null)
            throw new CommandException("No amount given");
        int amount = 0;
        try {
            if(level==null) amount = Integer.parseInt(ars[0]);
        } catch(NumberFormatException ex) {
            throw new CommandException("Number expected, garbage given");
        }
        
        Player player = p.get(0);
        
        if(level!=null) {
            int olevel = player.getLevel();
            int nlevel = olevel+level;
            sender.sendMessage(player.getDisplayName()+ChatColor.YELLOW+" was level "+olevel+", new level : "+nlevel);
            player.sendMessage(ChatColor.YELLOW+"Adding "+level+" levels to your total experience!");
            player.setLevel(nlevel);
        } else {
            sender.sendMessage(player.getDisplayName()+ChatColor.YELLOW+" had "+player.getTotalExperience()+ " experience, adding "+amount);
            player.sendMessage(ChatColor.YELLOW+"Adding "+amount+" to your total experience!");
            player.setTotalExperience(player.getTotalExperience()+amount);
        }
        // player.setTotalExperience(player.getTotalExperience()+amount);

        return true;
    }

    @Override
    public String getPermissionSuffix() {
        return "addexp";
    }

}
