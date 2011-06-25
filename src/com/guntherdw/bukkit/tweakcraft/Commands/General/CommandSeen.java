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
import com.guntherdw.bukkit.tweakcraft.DataSources.PersistenceClass.PlayerHistoryInfo;
import com.guntherdw.bukkit.tweakcraft.DataSources.PersistenceClass.PlayerInfo;
import com.guntherdw.bukkit.tweakcraft.Exceptions.CommandSenderException;
import com.guntherdw.bukkit.tweakcraft.Exceptions.CommandUsageException;
import com.guntherdw.bukkit.tweakcraft.Exceptions.PermissionsException;
import com.guntherdw.bukkit.tweakcraft.TweakcraftUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author GuntherDW
 */
public class CommandSeen implements iCommand {

    public boolean executeCommand(CommandSender sender, String command, String[] args, TweakcraftUtils plugin)
            throws PermissionsException, CommandSenderException, CommandUsageException {
        if (args.length < 1) {
            throw new CommandUsageException("You did not specify a name!");
        }
        if (plugin.getConfigHandler().getSeenconfig() != null || plugin.getConfigHandler().enablePersistence) {
            Player pla;
            if ((pla = plugin.getServer().getPlayer(args[0])) != null) {
                sender.sendMessage(ChatColor.GOLD + pla.getName() + " is online right now!");
            } else {
                String seen = "";
                String extramsg = "";
                String newline = null;
                if(!plugin.getConfigHandler().enablePersistence)
                    seen = plugin.getConfigHandler().getSeenconfig().getString(args[0].toLowerCase(), "");
                else {
                    if(!plugin.getConfigHandler().useTweakBotSeen) {
                        PlayerInfo pi = plugin.getDatabase().find(PlayerInfo.class).where().ieq("name", args[0]).findUnique();
                        if(pi!=null)
                            seen = pi.getLastseen().toString();
                    } else {
                        PlayerHistoryInfo phi = plugin.getDatabase().find(PlayerHistoryInfo.class).where().ieq("nickname", args[0]).findUnique();
                        if(phi!=null) {
                            if(phi.getChannel().equals("gameserver")) {
                                // extramsg = " (Gamequit!)";
                                newline = ChatColor.GOLD + "Gameserver quit!";
                            } else {
                                // extramsg = " (Non gameserver stuff!)";
                                if(phi.getAct().equals("privmsg")) {
                                    newline = ChatColor.GOLD +"(IRC) "+ phi.getChannel() + ChatColor.WHITE +": <"+phi.getNickname()+"> ";
                                    newline+= phi.getText();
                                } else if(phi.getAct().equals("action")) {
                                    newline = ChatColor.GOLD +"(IRC) "+phi.getChannel() + ChatColor.WHITE +": * "+phi.getNickname();
                                    newline+= phi.getText();
                                } else if(phi.getAct().equals("part")) {
                                    newline = ChatColor.GOLD +"(IRC) "+phi.getChannel() + ChatColor.WHITE +" :"+ChatColor.YELLOW+" Leaving channel";
                                } else if(phi.getAct().equals("join")) {
                                    newline = ChatColor.GOLD +"(IRC) "+phi.getChannel() + ChatColor.WHITE +" :"+ChatColor.YELLOW+" Joined channel";
                                } else if(phi.getAct().equals("quit")) {
                                    newline = ChatColor.GOLD +"(IRC) "+ "Quit client "+ ChatColor.WHITE +": "+ ChatColor.YELLOW +phi.getText()+"!";
                                } else if(phi.getAct().equals("nick")) {
                                    newline = ChatColor.GOLD + "(IRC) Set nick to " + ChatColor.WHITE+": "+ChatColor.YELLOW+ phi.getText();
                                } else {
                                    newline = ChatColor.GOLD + "Unknown other Act method, go nag GuntherDW!";
                                }
                            }
                            Long l = phi.getDate().getTime();
                            seen = l.toString();
                        }
                    }
                }
                // plugin.getSeenconfig().get
                if (seen.equals(""))
                    sender.sendMessage(ChatColor.DARK_AQUA+ "I haven't seen " + args[0] + " yet!");
                else {
                    SimpleDateFormat smf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                    Date datelastseen = new Date(Long.parseLong(seen));
                    String lastseen = smf.format(datelastseen);
                    sender.sendMessage(ChatColor.GOLD + args[0] + " was last seen on " + lastseen + "!"+extramsg);
                    if(newline!=null) {
                        sender.sendMessage(newline);
                    }
                }
            }
        } else {
            throw new CommandUsageException("Player history is disabled!");
        }
        return true;
    }

    @Override
    public String getPermissionSuffix() {
        return null;
    }
}
