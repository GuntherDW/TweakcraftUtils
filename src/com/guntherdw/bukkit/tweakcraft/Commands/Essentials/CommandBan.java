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

import com.guntherdw.bukkit.tweakcraft.Ban.BanHandler;
import com.guntherdw.bukkit.tweakcraft.Command;
import com.guntherdw.bukkit.tweakcraft.Exceptions.CommandException;
import com.guntherdw.bukkit.tweakcraft.Exceptions.CommandSenderException;
import com.guntherdw.bukkit.tweakcraft.Exceptions.CommandUsageException;
import com.guntherdw.bukkit.tweakcraft.Exceptions.PermissionsException;
import com.guntherdw.bukkit.tweakcraft.Packages.Ban;
import com.guntherdw.bukkit.tweakcraft.TweakcraftUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * @author GuntherDW
 */
public class CommandBan implements Command {
    public boolean executeCommand(CommandSender sender, String command, String[] args, TweakcraftUtils plugin)
            throws PermissionsException, CommandSenderException, CommandUsageException, CommandException {
        if(sender instanceof Player)
            if(!plugin.check((Player) sender, "ban"))
                throw new PermissionsException(command);
        BanHandler handler = plugin.getBanhandler();
        if(args.length<1)
            throw new CommandUsageException(ChatColor.YELLOW + "I need at leat 1 name to ban!");
        if(handler.isBanned(args[0])) {
            sender.sendMessage(ChatColor.YELLOW + "This player is already banned!");
        } else {
            String reason = "";
            String playername = args[0];
            if(args.length > 1)
            {
                for(int x=1; x<args.length; x++)
                {
                    reason += args[x]+" ";
                }
                if(reason.length()>1)
                    reason = reason.substring(0, reason.length()-1);
            }
            
            handler.banPlayer(playername, reason);
            sender.sendMessage(ChatColor.YELLOW + "Banning "+playername+"!");
            
            Player player = plugin.getServer().getPlayer(plugin.findPlayer(playername));
            if(player != null)
            {
                sender.sendMessage(ChatColor.YELLOW + "Kickbanning "+player.getName());
                player.kickPlayer(reason);
            }
            plugin.getLogger().info("[TweakcraftUtils] Banning "+playername+"!");
            handler.saveBans();
        }
        return true;
    }
}
