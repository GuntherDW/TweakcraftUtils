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
import com.guntherdw.bukkit.tweakcraft.Exceptions.CommandException;
import com.guntherdw.bukkit.tweakcraft.Exceptions.CommandSenderException;
import com.guntherdw.bukkit.tweakcraft.Exceptions.CommandUsageException;
import com.guntherdw.bukkit.tweakcraft.Exceptions.PermissionsException;
import com.guntherdw.bukkit.tweakcraft.Packages.TamerMode;
import com.guntherdw.bukkit.tweakcraft.Tools.TamerTool;
import com.guntherdw.bukkit.tweakcraft.TweakcraftUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * @author GuntherDW
 */
public class CommandTamer implements iCommand {

    @Override
    public boolean executeCommand(CommandSender sender, String command, String[] args, TweakcraftUtils plugin)
            throws PermissionsException, CommandSenderException, CommandUsageException, CommandException {

        if(sender instanceof Player) {
            Player player = (Player) sender;
            if(!plugin.check(player, getPermissionSuffix()))
                throw new PermissionsException(command);

            TamerTool tamertool = plugin.getTamerTool();
            TamerMode mode = null;
            Boolean state = null;

            if(args.length==1 && args[0].equalsIgnoreCase("reset")) {
                if(tamertool.getTamers().containsKey(player)) {
                    tamertool.getTamers().remove(player);
                }
                player.sendMessage(ChatColor.GREEN+"Tamertool mode reset!");
                return true;
            }

            if(args.length>0 && args[0].equalsIgnoreCase("info")) {
                mode = new TamerMode(state, TamerMode.TamerModes.INFO);
                player.sendMessage(ChatColor.GREEN+"TamerTool INFO MODE selected!");
            } else if(args.length>0 && args[0].equalsIgnoreCase("tame")) {
                if(args.length>1) {
                    state = Boolean.parseBoolean(args[1]);
                }
                mode = new TamerMode(state, TamerMode.TamerModes.TAME);
                player.sendMessage(ChatColor.GREEN+"TamerTool TAME MODE selected!");
            } else if(args.length>0 && args[0].equalsIgnoreCase("angry")) {
                if(args.length>1) {
                    state = Boolean.parseBoolean(args[1]);
                }
                mode = new TamerMode(state, TamerMode.TamerModes.ANGRY);
                player.sendMessage(ChatColor.GREEN+"TamerTool ANGRY MODE selected!");
            } else if(args.length>0 && args[0].equalsIgnoreCase("heal")) {
                if(args.length>1) {
                    state = Boolean.parseBoolean(args[1]);
                }
                mode = new TamerMode(state, TamerMode.TamerModes.HEAL);
                player.sendMessage(ChatColor.GREEN+"TamerTool HEAL MODE selected!");
            } else if(args.length>0 && args[0].equalsIgnoreCase("sit")) {
                if(args.length>1) {
                    state = Boolean.parseBoolean(args[1]);
                }
                mode = new TamerMode(state, TamerMode.TamerModes.SIT);
                player.sendMessage(ChatColor.GREEN+"TamerTool SIT MODE selected!");
            } else if(args.length>0 && args[0].equalsIgnoreCase("setage")) {
                int age = -1;
                if(args.length>1) {
                    age = Integer.parseInt(args[1]);
                    if(age>0) age=0;
                }
                mode = new TamerMode(state, TamerMode.TamerModes.SETAGE);
                mode.setData(age);
                player.sendMessage(ChatColor.GREEN+"TamerTool SETAGE MODE selected! (age : "+age+")");
            } else if(args.length>0 && args[0].equalsIgnoreCase("none")) {
                mode = null;
                if(plugin.getTamerTool().getTamers().containsKey(player))
                    plugin.getTamerTool().getTamers().remove(player);
                player.sendMessage(ChatColor.GREEN+"Removed any set TamerTool mode");
                return true;
            }
            
            if(mode == null) {
                player.sendMessage(ChatColor.YELLOW+"Usage: /tame <info|tame|angry|heal|reset|setage> <mode|data> ");
            } else {
                plugin.getTamerTool().getTamers().put(player, mode);
            }
        } else {
            throw new CommandSenderException("Consoles aren't allowed in this party!");
        }
        return true;
    }

    @Override
    public String getPermissionSuffix() {
        return "tamer";
    }
}
