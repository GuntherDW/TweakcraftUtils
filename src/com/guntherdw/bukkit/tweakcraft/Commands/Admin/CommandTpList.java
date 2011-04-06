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

import com.guntherdw.bukkit.tweakcraft.Command;
import com.guntherdw.bukkit.tweakcraft.Exceptions.CommandSenderException;
import com.guntherdw.bukkit.tweakcraft.Exceptions.PermissionsException;
import com.guntherdw.bukkit.tweakcraft.TweakcraftUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * @author GuntherDW
 */
public class CommandTpList implements Command {

    public boolean executeCommand(CommandSender sender, String command, String[] args, TweakcraftUtils plugin) throws PermissionsException, CommandSenderException {

        if (sender instanceof Player) {
            if (!plugin.check((Player) sender, "tplist"))
                throw new PermissionsException(command);
        }


        if (plugin.getDonottplist().size() != 0) {
            sender.sendMessage(ChatColor.YELLOW + "Current do-not-tp list : ");
            String color = "";
            String msg = "";
            for (String playername : plugin.getDonottplist()) {
                Player tpp = plugin.getServer().getPlayer(playername);
                if(tpp != null)
                {
                    sender.sendMessage(tpp.getDisplayName());
                } else {
                    sender.sendMessage(ChatColor.AQUA + "[NC] "+ChatColor.WHITE+playername);
                }
            }
        } else {
            sender.sendMessage(ChatColor.YELLOW + "Current do-not-tp list is empty!");
        }
        return true;
    }
}
