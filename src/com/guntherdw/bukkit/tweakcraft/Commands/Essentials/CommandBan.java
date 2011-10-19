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
import com.guntherdw.bukkit.tweakcraft.DataSources.Ban.BanHandler;
import com.guntherdw.bukkit.tweakcraft.Exceptions.CommandException;
import com.guntherdw.bukkit.tweakcraft.Exceptions.CommandSenderException;
import com.guntherdw.bukkit.tweakcraft.Exceptions.CommandUsageException;
import com.guntherdw.bukkit.tweakcraft.Exceptions.PermissionsException;
import com.guntherdw.bukkit.tweakcraft.TweakcraftUtils;
import com.guntherdw.bukkit.tweakcraft.Util.TimeTool;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * @author GuntherDW
 */
public class CommandBan implements iCommand {
    public boolean executeCommand(CommandSender sender, String command, String[] args, TweakcraftUtils plugin)
            throws PermissionsException, CommandSenderException, CommandUsageException, CommandException {
        if (sender instanceof Player)
            if (!plugin.check((Player) sender, "ban"))
                throw new PermissionsException(command);
        BanHandler handler = plugin.getBanhandler();
        if (args.length < 1)
            throw new CommandUsageException(ChatColor.YELLOW + "I need at least 1 name to ban!");
        if (handler.isBanned(args[0])) {
            sender.sendMessage(ChatColor.YELLOW + "This player is already banned!");
        } else {
            String reason = "";
            String playername = args[0];
            String duration = null;
            Long dura = null;
            String toFull = null;
            if (args.length > 1) {
                boolean skipfirst = false;
                if(args[1].startsWith("t:")) {
                    duration = args[1].substring(2);
                    dura = TimeTool.calcTime(duration);
                    toFull = TimeTool.getDurationFull(duration);
                    duration = duration.substring(0, duration.length()-1);
                }
                if(dura!=null) skipfirst=true;
                for (int x = skipfirst?2:1; x < args.length; x++) {
                    reason += args[x] + " ";
                }
                if (reason.length() > 1)
                    reason = reason.substring(0, reason.length() - 1);
            }
            if(dura!=null&&!plugin.getConfigHandler().enablePersistence) {
                throw new CommandUsageException("ERROR: For timed bans to work, persistence HAS to be enabled!");
            }

            handler.banPlayer(playername.toLowerCase(), reason, dura);
            sender.sendMessage(ChatColor.YELLOW + "Banning " + playername + ChatColor.YELLOW+ (dura!=null?" for "+duration+" "+toFull+"!":""));

            Player player = plugin.getServer().getPlayer(playername);
            if (player != null) {
                sender.sendMessage(ChatColor.YELLOW + "Kickbanning " + player.getName());
                player.kickPlayer(reason);
            }
            plugin.getLogger().info("[TweakcraftUtils] Banning " + playername + "!");
            handler.saveBans();
        }
        return true;
    }

    @Override
    public String getPermissionSuffix() {
        return "ban";
    }
}
