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

package com.guntherdw.bukkit.tweakcraft.Chat.Modes;

import com.guntherdw.bukkit.tweakcraft.Chat.ChatHandler;
import com.guntherdw.bukkit.tweakcraft.Chat.ChatMode;
import com.guntherdw.bukkit.tweakcraft.Util.EntityLocation;
import com.guntherdw.bukkit.tweakcraft.TweakcraftUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

/**
 * @author GuntherDW
 */
public class WorldChat implements ChatMode {

    private List<String> subscribers;
    private TweakcraftUtils plugin;
    private ChatHandler chathandler;

    public WorldChat(ChatHandler instance) {
        subscribers = new ArrayList<String>();
        this.chathandler = instance;
        this.plugin = chathandler.getTCUtilsInstance();
    }

    public boolean sendMessage(CommandSender sender, String message) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            List<Player> recp = getRecipients(player);
            for (Player p : recp) {
                p.sendMessage(ChatColor.DARK_GREEN+"W"+ChatColor.WHITE+": [" + player.getDisplayName() + "]: " + message);
            }
            if (recp.size() < 2) {
                sender.sendMessage(ChatColor.GOLD + "No one can hear you!");
            }
            plugin.getLogger().info("W: ("+player.getWorld().getName()+") <" + player.getName() + "> " + message);
        } else {
            sender.sendMessage("How did you get here?!");
        }
        return true;
    }

    public boolean broadcastMessage(CommandSender sender, String message) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            List<Player> recp = getRecipients(player);
            for (Player p : recp) {
                p.sendMessage(message);
            }
            if (recp.size() < 2) {
                sender.sendMessage(ChatColor.GOLD + "No one can hear you!");
            }
            plugin.getLogger().info("W: ("+player.getWorld().getName()+") " + message);
        } else {
            sender.sendMessage("How did you get here?!");
        }
        return true;
    }

    public List<Player> getRecipients(CommandSender sender) {
        List<Player> recp = new ArrayList<Player>();
        if (sender instanceof Player) {
            Player player = (Player) sender;
            EntityLocation entityloc = new EntityLocation(player);
            for (Player p : player.getWorld().getPlayers()) {
                // if (entityloc.getDistance(p) < plugin.getConfigHandler().localchatdistance) {
                    recp.add(p);
                // }
            }
        }
        return recp;
    }

    public void addRecipient(String player) {
        if (!subscribers.contains(player)) {
            subscribers.add(player);
        }
    }

    public void removeRecipient(String player) {
        if (subscribers.contains(player)) {
            subscribers.remove(player);
        }
    }

    public List<String> getSubscribers() {
        return subscribers;
    }

    @Override
    public String getDescription() {
        return "World Chat (chat in your current world)";
    }

    public boolean isEnabled() {
        return plugin.getConfigHandler().enableWorldChat;
    }

    public String getColor() {
        return ChatColor.DARK_GREEN.toString();
    }

    public String getPrefix() {
        return this.getColor()+"W";
    }
}

