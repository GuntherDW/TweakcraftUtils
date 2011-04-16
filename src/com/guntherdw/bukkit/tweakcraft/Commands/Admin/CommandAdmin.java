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
import com.guntherdw.bukkit.tweakcraft.Chat.Modes.AdminChat;
import com.guntherdw.bukkit.tweakcraft.Commands.Command;
import com.guntherdw.bukkit.tweakcraft.Exceptions.*;
import com.guntherdw.bukkit.tweakcraft.TweakcraftUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * @author GuntherDW
 */
public class CommandAdmin implements Command {
    public boolean executeCommand(CommandSender sender, String command, String[] args, TweakcraftUtils plugin)
            throws PermissionsException, CommandSenderException, CommandUsageException, CommandException {
        boolean onlist = false;
        try {
            ChatMode cm = plugin.getChathandler().getChatMode("admin");
            String msg = "";
            for (String m : args)
                msg += m + " ";
            msg = msg.substring(0, msg.length() - 1);

            if (sender instanceof Player) {
                onlist = (cm.getSubscribers().contains(((Player) sender).getName())
                        || ((AdminChat) cm).getAdminsString().contains(((Player) sender).getName()));
            } else {
                onlist = true;
            }

            if (!onlist) {
                sender.sendMessage(ChatColor.GREEN + "Message sent to admins:");
            }
            cm.sendMessage(sender, msg);
        } catch (ChatModeException e) {
            throw new CommandException("Error occurred while trying to get ChatMode!");
        }
        return true;
    }
}
