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
import com.sk89q.worldedit.Vector;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.bukkit.BukkitPlayer;
import com.sk89q.worldguard.protection.GlobalRegionManager;
import com.sk89q.worldguard.protection.managers.RegionManager;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author GuntherDW
 */
public class RegionChat implements ChatMode {

    private List<String> subscribers;
    private TweakcraftUtils plugin;
    private ChatHandler chathandler;

    public RegionChat(ChatHandler instance) {
        this.chathandler = instance;
        this.plugin = chathandler.getTCUtilsInstance();
        this.subscribers = new ArrayList<String>();
    }

    public boolean sendMessage(CommandSender sender, String message) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            List<Player> recp = getRecipients(player);
            for (Player p : recp) {
                p.sendMessage(ChatColor.AQUA + "R" + ChatColor.WHITE + ": [" + player.getDisplayName() + "]: " + message);
            }
            if (getRegionName(player.getName(), false).equals("")) {
                sender.sendMessage(ChatColor.GOLD + "You're currently not inside of a region!");
            } else if (recp.size() < 2) {
                sender.sendMessage(ChatColor.GOLD + "No one can hear you!");
            }
            plugin.getLogger().info("R: (" + getRegionName(player.getName(), false) + ") <" + player.getName() + "> " + message);
        } else {
            sender.sendMessage("How did you get here?!");
        }
        return true;
    }

    public List<Player> getRecipients(CommandSender sender) {
        List<Player> recp = new ArrayList<Player>();
        List<String> regionIds = null;
        if (sender instanceof Player) {
            GlobalRegionManager gm = plugin.getWorldGuard().getGlobalRegionManager();
            Player player = (Player) sender;
            if (plugin.getWorldGuard() == null) {
                sender.sendMessage(ChatColor.GOLD + "WorldGuard error!");
            } else {

                // RegionManager rm = gm.getRegionManager(block.getWorld().getName());
                RegionManager rm = gm.get(player.getWorld());
                Location loc = player.getLocation().clone();
                Vector vec = new Vector(loc.getX(), loc.getY(), loc.getZ());
                LocalPlayer localplayer = new BukkitPlayer(plugin.getWorldGuard(), player);
                // ApplicableRegionSet regionset = rm.getApplicableRegions(vec);
                regionIds = rm.getApplicableRegionsIDs(vec);
                Vector pvec = null;
                List<String> preg = null;
                for (Player p : player.getWorld().getPlayers()) {
                    pvec = new Vector(p.getLocation().getX(), p.getLocation().getY(), p.getLocation().getZ());
                    preg = rm.getApplicableRegionsIDs(pvec);
                    for (String s : preg) {
                        if (recp.contains(p)) // List already contains player, skip checks
                            continue;
                        if (!s.equalsIgnoreCase("__global__") && regionIds.contains(s)) {
                            recp.add(p);
                        }
                    }
                }
            }
            if (!recp.contains(player)) {
                recp.add(player);
            }
        }
        return recp;
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
            plugin.getLogger().info("R: (" + getRegionName(player.getName(), false) + ") : " + message);
        } else {
            sender.sendMessage("How did you get here?!");
        }
        return true;
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

    public String getDescription() {
        return "WorldGuard regions chat!";
    }

    public String getRegionName(String sender, boolean color) {
        Player p = plugin.getServer().getPlayer(sender);
        if (p != null) {
            GlobalRegionManager gm = plugin.getWorldGuard().getGlobalRegionManager();
            Location loc = p.getLocation().clone();
            Vector vec = new Vector(loc.getX(), loc.getY(), loc.getZ());
            RegionManager rm = gm.get(p.getWorld());
            List<String> regionIds = rm.getApplicableRegionsIDs(vec);
            String msg = "";
            for (Iterator<String> iterator = regionIds.iterator(); iterator.hasNext(); ) {
                String next = iterator.next();
                if (!next.equalsIgnoreCase("__global__")) {
                    msg += (color ? ChatColor.GOLD : "") + next;
                    if (iterator.hasNext()) {
                        msg += (color ? ChatColor.WHITE : "") + ",";
                    }
                }
            }
            return msg + (color ? ChatColor.WHITE : "");
        } else {
            return null;
        }
    }

    public boolean isEnabled() {
        return plugin.getConfigHandler().enableWorldGuard;
    }

    public String getColor() {
        return ChatColor.AQUA.toString();
    }

    public String getPrefix() {
        return this.getColor() + "R";
    }
}
