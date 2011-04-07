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

import com.guntherdw.bukkit.tweakcraft.Command;
import com.guntherdw.bukkit.tweakcraft.Exceptions.CommandException;
import com.guntherdw.bukkit.tweakcraft.Exceptions.CommandSenderException;
import com.guntherdw.bukkit.tweakcraft.Exceptions.CommandUsageException;
import com.guntherdw.bukkit.tweakcraft.Exceptions.PermissionsException;
import com.guntherdw.bukkit.tweakcraft.TweakcraftUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.io.File;

/**
 * @author GuntherDW
 */
public class CommandPlugin implements Command {
    public boolean executeCommand(CommandSender sender, String command, String[] args, TweakcraftUtils plugin)
            throws PermissionsException, CommandSenderException, CommandUsageException, CommandException {
        if (sender instanceof Player)
            if (!plugin.check((Player) sender, "plugins"))
                throw new PermissionsException(command);

        if (args.length > 0) {
            String pluginname = "";
            for (int x = 1; x < args.length; x++) {
                pluginname += args[x] + " ";
            }
            if (pluginname.length() > 1)
                pluginname = pluginname.substring(0, pluginname.length() - 1);

            if (args[0].equalsIgnoreCase("list")) {
                sender.sendMessage("Current list of plugins : ");
                String message = "";
                for (Plugin p : plugin.getServer().getPluginManager().getPlugins()) {
                    if (p.isEnabled())
                        message += ChatColor.GREEN + p.getDescription().getName() + " ";
                    else
                        message += ChatColor.GRAY + p.getDescription().getName() + " ";
                }
                if (message.length() > 0)
                    message = message.substring(0, message.length() - 1);
                sender.sendMessage(message);
            } else if (args[0].equalsIgnoreCase("reload")) {
                sender.sendMessage(ChatColor.YELLOW + "Reloading " + pluginname);
                Plugin p = plugin.getServer().getPluginManager().getPlugin(pluginname);
                plugin.getServer().getPluginManager().disablePlugin(p);
                plugin.getServer().getPluginManager().enablePlugin(p);

            } else if (args[0].equalsIgnoreCase("load")) {
                sender.sendMessage(ChatColor.YELLOW + "Loading " + pluginname);
                File plug = new File("plugins", pluginname + ".jar");
                Plugin p = null;
                try {
                    p = plugin.getServer().getPluginManager().loadPlugin(plug);
                } catch (Exception e) {
                    e.printStackTrace();
                    throw new CommandException("Exception thrown while loading a plugin!");
                }
                if (p == null) {
                    throw new CommandException("Can't load the plugin!");
                }

                if (!p.isEnabled()) {
                    plugin.getServer().getPluginManager().enablePlugin(p);
                }
            } else if (args[0].equalsIgnoreCase("enable")) {
                sender.sendMessage(ChatColor.YELLOW + "Enabling " + pluginname);
                Plugin p = plugin.getServer().getPluginManager().getPlugin(pluginname);
                if (!p.isEnabled()) {
                    plugin.getServer().getPluginManager().enablePlugin(p);
                } else {
                    sender.sendMessage("This plugin was already enabled!");
                }
            } else if (args[0].equalsIgnoreCase("disable")) {
                sender.sendMessage(ChatColor.YELLOW + "Disabling " + pluginname);
                Plugin p = plugin.getServer().getPluginManager().getPlugin(pluginname);
                if (!p.isEnabled()) {
                    sender.sendMessage("This plugin was already disabled!");
                } else {
                    plugin.getServer().getPluginManager().disablePlugin(p);
                }
            }
        } else {
            throw new CommandUsageException("I need at least 1 argument!");
        }
        return true;
    }
}
