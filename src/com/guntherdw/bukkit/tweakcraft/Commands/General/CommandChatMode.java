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

import com.avaje.ebeaninternal.server.el.ElSetValue;
import com.guntherdw.bukkit.tweakcraft.Chat.ChatHandler;
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
public class CommandChatMode implements Command {
    @Override
    public boolean executeCommand(CommandSender sender, String command, String[] args, TweakcraftUtils plugin)
            throws PermissionsException, CommandSenderException, CommandUsageException, CommandException {
        if(sender instanceof Player) {
            if(args.length==0) {
                throw new CommandUsageException("/"+command+" <list|[chatmode]|...>");
            }
            Player player = (Player) sender;
            ChatHandler ch = plugin.getChathandler();
            if(ch.isMuted(player.getName())) {
                throw new CommandException("You can't use this while muted!");
            }
            String chatMode = args[0].toLowerCase();
            String msg = "";
            if(args.length == 1)
            {
                if(chatMode.equalsIgnoreCase("listplayers") && plugin.check(player, "chat.list")) {
                    sender.sendMessage(ChatColor.GOLD + "Current list of players in a chatmode");
                    // TODO: better integration
                    for(String cms : plugin.getChathandler().listChatModes()) {

                        try {
                            if(plugin.getChathandler().getChatMode(cms).isEnabled()) {
                                for(String pla : plugin.getChathandler().getChatMode(cms).getSubscribers()) {
                                    String color = "";
                                    try {
                                        color = plugin.getPlayerColor(pla, true);
                                    } catch (NullPointerException e) {
                                        color = ChatColor.WHITE.toString();
                                    }
                                    msg = color+pla+ChatColor.WHITE+" ("+plugin.getChathandler().getChatMode(cms).getColor()+cms+ChatColor.WHITE+")";
                                    sender.sendMessage(msg);
                                }
                            }
                        } catch(ChatModeException ex) {
                            
                        }
                    }
                } else if(chatMode.equalsIgnoreCase("list")) {
                    msg = ChatColor.GOLD+"Currently enabled chatmodes";
                    sender.sendMessage(msg);
                    for(String cms : plugin.getChathandler().listChatModes()) {
                        try
                        {
                            if(plugin.getChathandler().getChatMode(cms).isEnabled()) {
                                msg = "- "+ChatColor.GOLD+cms+ChatColor.WHITE+" : "+plugin.getChathandler().getChatMode(cms).getDescription();
                                sender.sendMessage(msg);
                            }
                        } catch (ChatModeException ex) {

                        }
                    }
                    msg = "- "+ChatColor.GOLD+"global"+ChatColor.WHITE+" : Chat globally, remove chatmode subscription";
                    sender.sendMessage(msg);
                } else if(chatMode.equalsIgnoreCase("global")) {
                    msg = ChatColor.YELLOW+"You will now chat globally!";
                    try {
                        ChatMode oldcm = ch.getPlayerChatMode(player);
                        if(oldcm != null)
                            oldcm.removeRecipient(player.getName());
                        ch.setPlayerchatmode(player.getName(), null);
                    } catch(ChatModeException ex) {
                        sender.sendMessage("Can't find chatmode, this shouldn't happen!");
                    }
                    sender.sendMessage(msg);
                } else { // Join specified chatmode
                    try {

                        ChatMode cm = ch.getChatMode(chatMode);
                        if((chatMode.equals("admin") && ((AdminChat)cm).isPlayerAllowed(player.getName()))
                                || plugin.check(player, "chat.mode."+chatMode)) {

                            if(cm.isEnabled()) {

                                ChatMode oldcm = ch.getPlayerChatMode(player);
                                if(oldcm != null)
                                    oldcm.removeRecipient(player.getName());

                                ch.setPlayerchatmode(player.getName(), chatMode);
                                cm.addRecipient(player.getName());
                            } else {
                                throw new CommandException(ChatColor.GOLD+"That ChatMode is not enabled");
                            }
                        }
                        else
                            throw new PermissionsException("You don't have the permission to join this chatmode!");
                        
                        sender.sendMessage(ChatColor.GOLD+"Selected ChatMode : "+chatMode);
                    } catch(ChatModeException ex) {
                        sender.sendMessage("Can't find chatmode!");
                    }
                }
            } else if(args.length>1) { //Spam the selected chatmode!
                if(chatMode.equals("global")) {
                    String spam = "";
                    int x = 0;
                    for (String m : args) {
                        if(x!=0)
                            spam += m + " ";
                        x++;
                    }
                    plugin.getServer().broadcastMessage(ChatColor.WHITE+"<"+player.getDisplayName()+ChatColor.WHITE +"> " +spam);
                } else {
                    try {
                        ChatMode cm = ch.getChatMode(chatMode);

                        if((chatMode.equals("admin") && ((AdminChat)cm).isPlayerAllowed(player.getName()))
                                    || plugin.check(player, "chat.mode."+chatMode)) {
                            if(cm.isEnabled())
                            {
                                String spam;
                                spam = "";

                                int x = 0;
                                for (String m : args) {
                                    if(x!=0)
                                        spam += m + " ";
                                    x++;
                                }
                                spam = spam.substring(0, spam.length() - 1);
                                cm.sendMessage(player, spam);
                            } else {
                                throw new CommandException(ChatColor.GOLD+"That ChatMode is not enabled");
                            }
                        } else {
                            throw new PermissionsException("You don't have the permission to join this chatmode!");
                        }


                    } catch(ChatModeException ex) {
                        sender.sendMessage("Can't find chatmode!");
                    }
                }
            }
        } else {
            throw new CommandSenderException("What are you doing here?");
        }
        return true;
    }

    @Override
    public String getPermissionSuffix() {
        return null;
    }
}
