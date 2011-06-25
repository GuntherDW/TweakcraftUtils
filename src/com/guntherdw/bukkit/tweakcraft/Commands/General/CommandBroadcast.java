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
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * @author GuntherDW
 */
public class CommandBroadcast implements iCommand {
    public boolean executeCommand(CommandSender sender, String command, String[] args, TweakcraftUtils plugin)
            throws PermissionsException, CommandSenderException, CommandUsageException {
        if (sender instanceof Player)
            if (!plugin.check((Player) sender, "broadcast"))
                throw new PermissionsException(command);

        String message = "";
        if (args.length < 1) {
            throw new CommandUsageException("You did not specify a message!");
        } else {
            for (String m : args) {
                message += m + " ";
            }
            message = message.substring(0, message.length() - 1);
        }

        for (Player p : plugin.getServer().getOnlinePlayers()) {
            p.sendMessage(ChatColor.RED + "[" + ChatColor.GREEN + "Broadcast" + ChatColor.RED + "] " + ChatColor.GREEN + message);
        }

        if(plugin.getConfigHandler().enableIRC && plugin.getCraftIRC()!=null) {
            if(plugin.getConfigHandler().GIRCenabled) {
                String tag = plugin.getConfigHandler().GIRCtag;
                
                plugin.getCraftIRC().sendMessageToTag("[Broadcast] "+message, tag);
            }
        }
        // plugin.getLogger().info
        plugin.getLogger().info("[Broadcast] " + message);

        return true;
    }

    @Override
    public String getPermissionSuffix() {
        return "broadcast";
    }
}
