/*
 * Copyright (c) 2012 GuntherDW
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
import com.ensifera.animosity.craftirc.EndPoint;
import com.ensifera.animosity.craftirc.RelayedMessage;
import com.guntherdw.bukkit.tweakcraft.Chat.ChatHandler;
import com.guntherdw.bukkit.tweakcraft.Chat.ChatMode;
import com.guntherdw.bukkit.tweakcraft.TweakcraftUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Set;

/**
 * @author GuntherDW
 */
public class TCUtilsChat extends ChatMode {

    private TweakcraftUtils plugin;
    // private EndPoint tcendpoint = null;
    private CraftIRC circ = null;
    private Set<Player> emptyHashSet = new HashSet<Player>();

    public TCUtilsChat(ChatHandler instance) {
        super(instance);
        this.plugin = instance.getTCUtilsInstance();
        // this.tcendpoint = plugin.getEndPoint();
        if (plugin.getConfigHandler().enableIRC) {
            this.circ = plugin.getCraftIRC();
            circ.registerEndPoint("tcutils", plugin.getEndPoint());

            // this.tcendpoint =
        }
    }

    @Override
    public Set<Player> getRecipients(CommandSender sender) {
        return emptyHashSet;
    }

    @Override
    public boolean broadcastMessage(CommandSender sender, String message) {
        String msg = message;
        if (sender instanceof Player) {
            sender.sendMessage(msg);
        }
        for (Player p : getRecipients(sender)) {
            p.sendMessage(msg);
        }
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public boolean sendMessage(CommandSender sender, String message) {
        String sendername = "";
        String pcolor = "";
        String cleanname;

        if(plugin.getConfigHandler().enableIRC) {

            if (sender instanceof Player) {
                sendername = ((Player) sender).getDisplayName(); // ((Player)sender).getName();
                pcolor = ""; // getDisplayName handles this one!
                cleanname = ((Player) sender).getName();
            } else {
                sendername = "CONSOLE";
                cleanname = sendername;
                pcolor = ChatColor.LIGHT_PURPLE.toString();
            }
            String targetmsg = plugin.getConfigHandler().GIRCMessageFormat;
            targetmsg = targetmsg.replace("%name%", cleanname).
                replace("%message%", message).
                replace("%dispname%", ChatColor.stripColor(sendername));
            RelayedMessage rmsg = plugin.getCraftIRC().newMsgToTag(plugin.getAdminEndPoint(), plugin.getConfigHandler().GIRCtag, "generic");
            rmsg.setField("sender", pcolor + sendername);
            rmsg.setField("realSender", cleanname);
            rmsg.setField("message", targetmsg);
            if (sender instanceof Player) {
                Player p = (Player) sender;
                // World w = p.getWorld();
                rmsg.setField("world", p.getWorld().getName());
                rmsg.setField("prefix", plugin.getPermissions().getResolver().getUserPrefix(p.getWorld().getName(), p));
                rmsg.setField("suffix", plugin.getPermissions().getResolver().getUserSuffix(p.getWorld().getName(), p));
            }
            rmsg.post();
        }
        return true;
    }

    @Override
    public boolean isHidden() {
        return true;
    }

    @Override
    public String getDescription() {
        return "CraftIRC extra endpoint";
    }

    @Override
    public String getColor() {
        return ChatColor.WHITE.toString();
    }

    @Override
    public String getPrefix() {
        return "TCIRC";
    }
}
