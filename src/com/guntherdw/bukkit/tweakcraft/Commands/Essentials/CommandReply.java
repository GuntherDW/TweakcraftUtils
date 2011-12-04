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
import com.guntherdw.bukkit.tweakcraft.Packages.LocalPlayer;
import com.guntherdw.bukkit.tweakcraft.TweakcraftUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * @author GuntherDW
 */
public class CommandReply implements iCommand {
    public boolean executeCommand(CommandSender sender, String command, String[] args, TweakcraftUtils plugin)
            throws PermissionsException, CommandSenderException, CommandUsageException, CommandException {
        if (sender instanceof Player) {
            if (args.length > 0) {
                Player player = (Player) sender;
                LocalPlayer lp = plugin.wrapPlayer(player);
                String replyTo = lp.getReplyTo();
                String message = "";

                if (replyTo == null)
                    throw new CommandException("Can't find the player to reply to!");

                for (int x = 0; x < args.length; x++) {
                    message += args[x] + (x<args.length?" ":"");
                }

                LocalPlayer lpTo = plugin.wrapPlayer(replyTo);
                Player playerto = lpTo.getBukkitPlayerSafe();
                if (playerto == null)
                    throw new CommandException("That player is no longer online!");

                sender.sendMessage("[Me -> " + playerto.getDisplayName() + "] " + message);
                playerto.sendMessage("[" + player.getDisplayName() + " -> Me] " + message);
                lpTo.setReplyTo(player.getName());
                // plugin.setPlayerReply(playerto.getName(), player.getName());
                plugin.getLogger().info("[TweakcraftUtils] (MSG) " + player.getName() + " -> " + playerto.getName() + " : " + message);
            } else if (args.length == 0) {
                throw new CommandUsageException("I need a message!");
            }
        } else {
            throw new CommandSenderException("Wait what do you want to do now?");
        }
        return true;
    }

    @Override
    public String getPermissionSuffix() {
        return null;
    }
}
