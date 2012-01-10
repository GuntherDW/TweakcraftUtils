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
import com.guntherdw.bukkit.tweakcraft.TweakcraftUtils;
import com.guntherdw.bukkit.tweakcraft.Util.EntityLocation;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Set;

/**
 * @author GuntherDW
 */
public class LocalChat extends ChatMode {

    private TweakcraftUtils plugin;

    public LocalChat(ChatHandler instance) {
        super(instance);
        this.plugin = instance.getTCUtilsInstance();
        this.chatModeName = "L";
    }

    @Override
    public boolean sendMessage(CommandSender sender, String message) {

        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.YELLOW + "What were you trying to do?");
            return true;
        }

        super.sendMessage(sender, message);
        if (getRecipients(sender).size() < 2)
            sender.sendMessage(ChatColor.GOLD + "No one can hear you!");

        return true;
    }

    public boolean broadcastMessage(CommandSender sender, String message) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            Set<Player> recp = getRecipients(player);
            for (Player p : recp) {
                p.sendMessage(message);
            }
            if (recp.size() < 2) {
                sender.sendMessage(ChatColor.GOLD + "No one can hear you!");
            }
            plugin.getLogger().info("L: " + message);
        } else {
            sender.sendMessage("How did you get here?!");
        }
        return true;
    }

    public Set<Player> getRecipients(CommandSender sender) {
        Set<Player> recp = new HashSet<Player>();
        if (sender instanceof Player) {
            Player player = (Player) sender;
            EntityLocation entityloc = new EntityLocation(player);
            for (Player p : player.getWorld().getPlayers()) {
                Integer distance = entityloc.getDistance(p);
                if (distance != null && distance < plugin.getConfigHandler().localchatdistance) {
                    recp.add(p);
                } /* else {
                    sender.sendMessage(distance+"");
                } */
            }
        }
        return recp;
    }

    public String getDescription() {
        return "Chat locally (" + plugin.getConfigHandler().localchatdistance + " blocks)";
    }

    @Override
    public boolean isEnabled() {
        return plugin.getConfigHandler().enableLocalChat && plugin.getConfigHandler().localchatdistance > 0;
    }

    public String getColor() {
        return ChatColor.YELLOW.toString();
    }

    public String getPrefix() {
        return this.getColor() + "L" + ChatColor.WHITE;
    }
}
