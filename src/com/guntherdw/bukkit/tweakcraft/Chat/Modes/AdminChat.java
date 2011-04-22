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

import com.guntherdw.bukkit.tweakcraft.Chat.ChatMode;
import com.guntherdw.bukkit.tweakcraft.TweakcraftUtils;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

/**
 * @author GuntherDW
 */
public class AdminChat implements ChatMode {

    private List<String> subscribers;
    private TweakcraftUtils plugin;

    public AdminChat(TweakcraftUtils instance) {
        subscribers = new ArrayList<String>();
        plugin = instance;
    }

    public boolean sendMessage(CommandSender sender, String message) {

        String sendername = "";
        String pcolor = "";
        String cleanname;
        if (sender instanceof Player) {
            sendername = ((Player) sender).getDisplayName(); // ((Player)sender).getName();
            pcolor = ""; // getDisplayName handles this one!
            cleanname = ((Player) sender).getName();
        } else {
            sendername = "CONSOLE";
            cleanname = sendername;
            pcolor = ChatColor.LIGHT_PURPLE.toString();
        }
        String msg = ChatColor.GREEN + "A: [" + pcolor + sendername + ChatColor.GREEN + "] " + message;
        if (plugin.getCraftIRC() != null) {
            String w = null;
            String prex = "";
            String sufx = "";
            if(sender instanceof Player) {
                w = ((Player)sender).getWorld().getName();
                prex = plugin.getCraftIRC().getPermPrefix(w, cleanname);
                sufx = plugin.getCraftIRC().getPermSuffix(w, cleanname);
            } else {
                prex = Character.toString((char) 3)+String.format("%02d", plugin.getCraftIRC().cColorIrcFromName("magenta"));
                sufx = "";
            }
            if(sufx.equals("")) {
                // System.out.println("bla "+this.plugin.cColorIrcFromName("foreground"));
                sufx = Character.toString((char) 3)+String.format("%02d", plugin.getCraftIRC().cColorIrcFromName("foreground")); }
            plugin.getCraftIRC().sendMessageToTag("[A] <" + prex + cleanname + sufx + "> " + message, "mchatadmin");
        }

        if (sender instanceof Player && !isOnList(sender)) {
            sender.sendMessage(msg);
        }
        for (Player p : getRecipients(sender)) {
            p.sendMessage(msg);
        }
        plugin.getLogger().info("AMSG: <" + sendername + "> " + message);
        return true;
    }

    public boolean broadcastMessage(CommandSender sender, String message) {

        String msg = message;
        if (sender instanceof Player && !isOnList(sender)) {
            sender.sendMessage(msg);
        }
        for (Player p : getRecipients(sender)) {
            p.sendMessage(msg);
        }
        plugin.getLogger().info("AMSG: " + message);
        return true;
    }

    public boolean broadcastMessage(String message) {
        // String msg = message;
        for (Player p : getRecipients(null)) {
            p.sendMessage(message);
        }
        plugin.getLogger().info("AMSG: " + message);
        return true;
    }

    public boolean broadcastMessageRealAdmins(String message) {
        for (Player player : getAdmins()) {
            player.sendMessage(message);
        }
        plugin.getLogger().info("AMSG: " + message);
        return true;
    }

    public List<Player> getRecipients(CommandSender sender) {
        List<Player> recp = new ArrayList<Player>();
        for (String m : subscribers) {
            Player p = plugin.getServer().getPlayer(m);
            if (p != null)
                recp.add(p);
        }

        for (Player p : plugin.getServer().getOnlinePlayers()) {
            if (plugin.check(p, "admon")) {
                recp.add(p);
            }
        }
        return recp;
    }

    public boolean isOnList(CommandSender sender) {
        if (sender instanceof Player) {
            return getRecipients(sender).contains((Player) sender);
        } else {
            return true;
        }
    }

    public void addRecipient(String player) {
        if (!subscribers.contains(player)) {
            subscribers.add(player);
        }
    }

    public List<Player> getAdmins() {
        List<Player> recp = new ArrayList<Player>();
        for (Player p : plugin.getServer().getOnlinePlayers()) {
            if (plugin.check(p, "admon"))
                recp.add(p);
        }
        return recp;
    }

    public List<String> getAdminsString() {
        List<String> admins = new ArrayList<String>();
        for (Player m : getAdmins()) {
            admins.add(m.getName());
        }
        return admins;
    }

    public void removeRecipient(String player) {
        if (subscribers.contains(player)) {
            subscribers.remove(player);
        }
    }

    public List<String> getSubscribers() {
        return subscribers;
    }

    public boolean sendToRealAdmins(CommandSender sender, String message) {
        for (Player player : getAdmins()) {
            player.sendMessage(message);
        }
        return true;
    }
}
