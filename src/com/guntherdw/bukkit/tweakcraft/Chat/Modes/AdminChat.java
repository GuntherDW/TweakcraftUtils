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

import com.ensifera.animosity.craftirc.CraftIRC;
import com.ensifera.animosity.craftirc.RelayedMessage;
import com.guntherdw.bukkit.tweakcraft.Chat.ChatHandler;
import com.guntherdw.bukkit.tweakcraft.Chat.ChatMode;
import com.guntherdw.bukkit.tweakcraft.TweakcraftUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

/**
 * @author GuntherDW
 */
public class AdminChat implements ChatMode {

    /* TweakcraftUtils stuff */

    private List<String> subscribers;
    private TweakcraftUtils plugin;
    private ChatHandler chathandler;
    private CraftIRC circ;

    public AdminChat(ChatHandler instance) {
        subscribers = new ArrayList<String>();
        this.chathandler = instance;
        this.plugin = chathandler.getTCUtilsInstance();
        if(plugin.getConfigHandler().enableIRC) {
            this.circ = plugin.getCraftIRC();
            circ.registerEndPoint("tcutilsadmin", plugin.getAdminEndPoint());
        }
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
        if (plugin.getConfigHandler().enableIRC && plugin.getCraftIRC() != null) {
            if(plugin.getConfigHandler().AIRCenabled) {

                String targetmsg = plugin.getConfigHandler().AIRCMessageFormat;
                       targetmsg = targetmsg.replace("%name%", cleanname);
                       targetmsg = targetmsg.replace("%message%", message);
                       targetmsg = targetmsg.replace("%dispname%", ChatColor.stripColor(sendername));

                // plugin.getCraftIRC().sendMessageToTag(targetmsg, plugin.getConfigHandler().AIRCtag);
                /// plugin.getCraftIRC().newMsgToTag(this, plugin.getConfigHandler().AIRCtag, targetmsg);

                RelayedMessage rmsg = plugin.getCraftIRC().newMsgToTag(plugin.getAdminEndPoint(), plugin.getConfigHandler().AIRCtag, "generic");
                rmsg.setField("sender", pcolor+sendername);
                rmsg.setField("realSender", cleanname);
                rmsg.setField("message", targetmsg);
                if(sender instanceof Player) {
                    Player p = (Player) sender;
                    // World w = p.getWorld();
                    rmsg.setField("world", p.getWorld().getName());
                    rmsg.setField("prefix", plugin.getPermissionsResolver().getUserPrefix(p.getWorld().getName(), p));
                    rmsg.setField("suffix", plugin.getPermissionsResolver().getUserSuffix(p.getWorld().getName(), p));
                }
                rmsg.post();

            }
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
                if(!recp.contains(p))
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

    @Override
    public String getDescription() {
        return "Admin chat (needs permissions!)";
    }

    public boolean sendToRealAdmins(CommandSender sender, String message) {
        for (Player player : getAdmins()) {
            player.sendMessage(message);
        }
        return true;
    }

    public boolean isEnabled() {
        return true;
    }

    public boolean isPlayerAllowed(String playername) {
        return subscribers.contains(playername);
    }

    public String getColor() {
        return ChatColor.GREEN.toString();
    }

    public String getPrefix() {
        return this.getColor()+"A";
    }
}
