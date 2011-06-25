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
import com.guntherdw.bukkit.tweakcraft.Exceptions.CommandException;
import com.guntherdw.bukkit.tweakcraft.Exceptions.CommandSenderException;
import com.guntherdw.bukkit.tweakcraft.Exceptions.CommandUsageException;
import com.guntherdw.bukkit.tweakcraft.Exceptions.PermissionsException;
import com.guntherdw.bukkit.tweakcraft.TweakcraftUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * @author GuntherDW
 */
public class CommandKick implements iCommand {
    public boolean executeCommand(CommandSender sender, String command, String[] args, TweakcraftUtils plugin)
            throws PermissionsException, CommandSenderException, CommandUsageException, CommandException {
        if (sender instanceof Player)
            if (!plugin.check((Player) sender, "kick"))
                throw new PermissionsException(command);

        String reason = "";
        Player player;
        String kicker = "";
        if(sender instanceof Player) {
            kicker = plugin.getNick(((Player)sender).getName());
        } else {
            kicker = "CONSOLE";
        }
        if (args.length > 0) // No reason set!
        {
            String p = plugin.findPlayer(args[0]);
            player = plugin.getServer().getPlayer(p);
            if (player == null)
                throw new CommandUsageException("Can't find player!");
            if (args.length > 1) // Reason given!
            {
                for (int x = 1; x < args.length; x++) {
                    reason += args[x] + " ";
                }
                if (reason.length() > 1)
                    reason = reason.substring(0, reason.length() - 1);
                else
                    reason = "No reason given!";

            }
            player.kickPlayer(kicker+": "+reason);
        }

        return true;
    }

    @Override
    public String getPermissionSuffix() {
        return "kick";
    }
}
