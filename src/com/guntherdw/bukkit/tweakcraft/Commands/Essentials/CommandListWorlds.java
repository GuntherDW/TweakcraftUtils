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
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * @author GuntherDW
 */
public class CommandListWorlds implements Command {
    public boolean executeCommand(CommandSender sender, String command, String[] args, TweakcraftUtils plugin)
            throws PermissionsException, CommandSenderException, CommandUsageException, CommandException {
        if (sender instanceof Player) // Give the player a list of worlds he has access to!
        {
            Player player = (Player) sender;
            player.sendMessage(ChatColor.LIGHT_PURPLE + "Listing currently accessible worlds : ");

            // String message = "";

            for (World w : plugin.getServer().getWorlds()) {
                if (plugin.check(player, "worlds." + w.getName())) {
                    player.sendMessage((w.getEnvironment() == World.Environment.NORMAL ? ChatColor.GREEN : ChatColor.RED) + w.getName());
                }
            }
            player.sendMessage(ChatColor.LIGHT_PURPLE + "Legend: " + ChatColor.RED + "NETHER" + ChatColor.LIGHT_PURPLE + "," +
                    ChatColor.GREEN + " NORMAL");
            player.sendMessage(ChatColor.LIGHT_PURPLE + "Warp to a world by issuing /world <worldname>");


        } else { // The console just needs a list!
            sender.sendMessage("Currently enabled worlds : ");
            String message = "";
            for (World w : plugin.getServer().getWorlds()) {
                message += w.getName() + ", ";
            }
            if (message.length() > 1)
                message = message.substring(0, message.length() - 2);
            sender.sendMessage(message);
        }
        return true;
    }

    @Override
    public String getPermissionSuffix() {
        return null;
    }
}
