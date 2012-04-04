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
import com.zones.model.ZoneBase;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author GuntherDW
 */
public class ZoneChat extends ChatMode {

    private TweakcraftUtils plugin;

    public ZoneChat(ChatHandler instance) {
        super(instance);
        this.plugin = chathandler.getTCUtilsInstance();
    }


    @Override
    public boolean sendMessage(CommandSender sender, String message) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            Set<Player> recp = getRecipients(player);
            for (Player p : recp) {
                p.sendMessage(ChatColor.DARK_PURPLE + "Z" + ChatColor.WHITE + ": [" + player.getDisplayName() + "]: " + message);
            }
            if (getZoneName(player, false).equals("")) {
                sender.sendMessage(ChatColor.GOLD + "You're currently not inside of a zone!");
            } else if (recp.size() < 2) {
                sender.sendMessage(ChatColor.GOLD + "No one can hear you!");
            }

        } else {
            sender.sendMessage(ChatColor.YELLOW + "What were you trying to do?");
        }
        return true;
    }

    @Override
    public Set<Player> getRecipients(CommandSender sender) {
        Set<Player> recp = new HashSet<Player>();
        if (sender instanceof Player) {

            Player player = (Player) sender;
            com.zones.WorldManager wm = plugin.getZones().getWorldManager(player.getWorld());
            List<ZoneBase> zbs = wm.getActiveZones(player.getLocation());
            for (ZoneBase zb : zbs) {
                for (Player p : zb.getPlayersInside())
                    if (!recp.contains(p))
                        recp.add(p);
            }
            /* if(zb != null) {
                recp.addAll(zb.getCharactersInside().values());
            } */
            if (!recp.contains(player)) {
                recp.add(player);
            }

        }
        return recp;
    }

    @Override
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
            plugin.getLogger().info("Z: (" + getZoneName(player, false) + ") : " + message);
        } else {
            sender.sendMessage("How did you get here?!");
        }
        return true;
    }

    @Override
    public String getDescription() {
        return "Zones zone chat!";
    }

    public String getZoneName(CommandSender sender, boolean color) {
        Player p = (Player) sender;
        if (p != null) {
            com.zones.WorldManager wm = plugin.getZones().getWorldManager(p);
            String msg = "";
            ZoneBase zb = wm.getActiveZone(p);
            if (zb != null) {
                msg = (color ? ChatColor.GOLD : "") + zb.getName();
            }
            return msg + (color ? ChatColor.WHITE : "");
        } else {
            return null;
        }
    }

    @Override
    public boolean isEnabled() {
        return plugin.getConfigHandler().enableZones;
    }

    @Override
    public String getColor() {
        return ChatColor.DARK_PURPLE.toString();
    }

    @Override
    public String getPrefix() {
        return this.getColor() + "Z" + ChatColor.WHITE;
    }

    @Override
    public void logChat(CommandSender sender, String message) {
        /* Nothing but players here, so safe to cast */
        Player player = (Player) sender;
        plugin.getLogger().info("Z: (" + getZoneName(player, false) + ") <" + player.getName() + "> " + message);
    }
}
