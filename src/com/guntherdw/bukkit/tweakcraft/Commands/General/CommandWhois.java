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
import com.guntherdw.bukkit.tweakcraft.TweakcraftUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * @author GuntherDW
 */
public class CommandWhois implements iCommand {
    @Override
    public boolean executeCommand(CommandSender sender, String command, String[] args, TweakcraftUtils plugin)
            throws PermissionsException, CommandSenderException, CommandUsageException, CommandException {
        // First search for a nick
        Boolean getIP = true;
        if(sender instanceof Player) {
            if(!plugin.check((Player)sender, "whois"))
                throw new PermissionsException(command);
            if(!plugin.check((Player)sender, "whois.ip"))
                getIP = false;
        }


        if(args.length==1) {
            Player nick = plugin.getPlayerListener().findPlayerByNick(args[0]);
            // sender.sendMessage(nick.toString());
            String gname = null;
            // Group  g  = null;
            Player sp = findPlayer(args[0], plugin);
            Player who = nick==null?sp:nick;
            String groups = ""; 

            String playername = null;
            boolean online = (who!=null);

            if(who==null) { // Is it an offline player? Check permissions
                /* if(plugin.getPermissionHandler()!=null) {
                    // Check for nicks
                    String pname = args[0];
                    String findnick = plugin.getPlayerListener().findPlayerNameByNick(pname);
                    if(findnick != null) pname = findnick;


                    PermissionHandler handler = plugin.getPermissionHandler();
                    String wname = plugin.getServer().getWorlds().get(0).getName();
                    User user = plugin.getPermissionHandler().getUserObject(wname, pname);
                    // groups = plugin.getPermissionHandler().getPrimaryGroup(wname, args[0]);
                    if(user!=null) {
                        playername = user.getName();
                        groups = plugin.getPermissionHandler().getPrimaryGroup(wname, pname);
                    }
                }
            } else {
                playername = who.getName();
                if(plugin.getPermissionHandler()!=null) {

                    String wname = plugin.getServer().getWorlds().get(0).getName();
                    groups = plugin.getPermissionHandler().getPrimaryGroup(wname, playername);
                }
            }

            if(playername!=null) {
                sender.sendMessage(ChatColor.YELLOW+"Playername : "+playername+" "+(nick!=null?"("+plugin.getNickWithColors(who.getName())+ChatColor.YELLOW+")":""));
                // String group = plugin.getPermissionHandler.getG(who.getWorld().getName(), who.getName());
                sender.sendMessage(ChatColor.YELLOW+"Groups : "+groups);
                if(!getIP && online) {
                    if(((Player)sender).getName().equalsIgnoreCase(who.getName()))
                        getIP = true;
                }
                if(getIP && online)
                    sender.sendMessage(ChatColor.YELLOW + "IP: " + who.getAddress().getAddress().getHostName());
            } else {
                throw new CommandException("Can't find player!");
            */ }
        } else {
            throw new CommandUsageException("I need a player!");
        }
        return true;
    }

    @Override
    public String getPermissionSuffix() {
        return "whois";  //To change body of implemented methods use File | Settings | File Templates.
    }

    public Player findPlayer(String playername, TweakcraftUtils plugin) {
        for(Player p : plugin.getServer().matchPlayer(playername)) {
            if(p.getName().toLowerCase().contains(playername.toLowerCase())) {
                return p;
            }
        }
        return null;
    }
}
