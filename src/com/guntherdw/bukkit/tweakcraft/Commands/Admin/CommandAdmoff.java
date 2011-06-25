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

import com.guntherdw.bukkit.tweakcraft.Chat.ChatMode;
import com.guntherdw.bukkit.tweakcraft.Commands.iCommand;
import com.guntherdw.bukkit.tweakcraft.Exceptions.*;
import com.guntherdw.bukkit.tweakcraft.TweakcraftUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * @author GuntherDW
 */
public class CommandAdmoff implements iCommand {
    public boolean executeCommand(CommandSender sender, String command, String[] args, TweakcraftUtils plugin) throws PermissionsException, CommandSenderException, CommandUsageException, CommandException {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            try {
                ChatMode cm = plugin.getChathandler().getChatMode("admin");
                if (cm.getRecipients(null).contains(player)) {
                    plugin.getChathandler().setPlayerchatmode(((Player) sender).getName(), null);
                    sender.sendMessage(ChatColor.YELLOW + "You will now chat globally!");
                } else {
                    throw new PermissionsException(command);
                }

            } catch (ChatModeException e) {
                throw new CommandException("Error occured while trying to fetch ChatMode!");
            }
        } else {
            throw new CommandSenderException("Do you really need this as a console?");
        }

        return true;
    }


    @Override
    public String getPermissionSuffix() {
        return "admin";
    }
}
