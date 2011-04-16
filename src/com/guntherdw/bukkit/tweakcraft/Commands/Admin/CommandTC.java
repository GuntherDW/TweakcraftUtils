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

import com.guntherdw.bukkit.tweakcraft.Ban.BanHandler;
import com.guntherdw.bukkit.tweakcraft.Commands.Command;
import com.guntherdw.bukkit.tweakcraft.Exceptions.CommandException;
import com.guntherdw.bukkit.tweakcraft.Exceptions.CommandSenderException;
import com.guntherdw.bukkit.tweakcraft.Exceptions.CommandUsageException;
import com.guntherdw.bukkit.tweakcraft.Exceptions.PermissionsException;
import com.guntherdw.bukkit.tweakcraft.Packages.ItemDB;
import com.guntherdw.bukkit.tweakcraft.TweakcraftUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * @author GuntherDW
 */
public class CommandTC implements Command {
    public boolean executeCommand(CommandSender sender, String command, String[] args, TweakcraftUtils plugin)
            throws PermissionsException, CommandSenderException, CommandUsageException, CommandException {
        if (args.length > 0) {
            if (args[0].equalsIgnoreCase("version")) {
                sender.sendMessage(ChatColor.WHITE + plugin.getDescription().getName() + ": version " + ChatColor.GREEN + plugin.getDescription().getVersion());
            } else if (args[0].equalsIgnoreCase("reload")) {

                if (sender instanceof Player) {
                    if (!plugin.check((Player) sender, "reload")) {
                        sender.sendMessage(ChatColor.GREEN + "Not implemented yet!");
                        return true;
                    }
                }


                sender.sendMessage(ChatColor.GREEN + "Reloading settings,dbs and setting colors.");
                plugin.reloadConfig();
                BanHandler bh = plugin.getBanhandler();
                bh.reloadBans();
                ItemDB idb = plugin.getItemDB();
                idb.loadDataBase();
                for (Player p : plugin.getServer().getOnlinePlayers()) {
                    String name = p.getName();
                    p.setDisplayName(plugin.getPlayerColor(name, false) + name + ChatColor.WHITE);
                }
                plugin.getPlayerListener().reloadInvisTable();
            }
        } else {
            throw new CommandUsageException("/tc <" + ChatColor.GREEN + "reload" + ChatColor.YELLOW + "/" + ChatColor.GREEN + "version" + ChatColor.YELLOW + ">");
        }
        return true;
    }
}
