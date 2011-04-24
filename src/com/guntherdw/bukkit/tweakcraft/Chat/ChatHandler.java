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

package com.guntherdw.bukkit.tweakcraft.Chat;

import com.guntherdw.bukkit.tweakcraft.Chat.Modes.AdminChat;
import com.guntherdw.bukkit.tweakcraft.Chat.Modes.LocalChat;
import com.guntherdw.bukkit.tweakcraft.Chat.Modes.RegionChat;
import com.guntherdw.bukkit.tweakcraft.Chat.Modes.ZoneChat;
import com.guntherdw.bukkit.tweakcraft.Exceptions.ChatModeException;
import com.guntherdw.bukkit.tweakcraft.TweakcraftUtils;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author GuntherDW
 */
public class ChatHandler {

    private TweakcraftUtils plugin;
    public Map<String, ChatMode> chatmodes = new HashMap<String, ChatMode>();
    public Map<String, String> playerchatmode = new HashMap<String, String>();
    public List<String> mutedPlayers = new ArrayList<String>();

    public ChatHandler(TweakcraftUtils instance) {
        plugin = instance;
        chatmodes.clear();
        chatmodes.put("admin",  new AdminChat(plugin)); /* This one has a higher priority! */
        chatmodes.put("local",  new LocalChat(plugin));
        chatmodes.put("region", new RegionChat(plugin));
        chatmodes.put("zones",  new ZoneChat(plugin));
    }

    public ChatMode getChatMode(String mode) throws ChatModeException {
        if (chatmodes.containsKey(mode)) {
            return chatmodes.get(mode);
        } else {
            throw new ChatModeException("No chat mode found by that name!");
        }
    }

    public ChatMode getPlayerChatMode(Player player) {
        if (playerchatmode.containsKey(player.getName())) {
            return chatmodes.get(playerchatmode.get(player.getName()));
        }
        return null;
    }

    public List<String> listChatModes() {
        List<String> l = new ArrayList<String>();
        for(String s : chatmodes.keySet()) {
            l.add(s);
        }
        return l;
    }

    public String getPlayerChatModeString(String player) {
        if (playerchatmode.containsKey(player)) {
            return playerchatmode.get(player);
        }
        return null;
    }


    public String getBypassChar() {
        return "!";
    }

    public void removePlayer(Player player) {
        for (ChatMode cm : chatmodes.values()) {
            cm.removeRecipient(player.getName());
        }
    }


    public void setPlayerchatmode(String player, String mode) throws ChatModeException {
        if (playerchatmode.containsKey(player)) {
            try {
                ChatMode cm = getChatMode(playerchatmode.get(player));
                if (!(cm instanceof AdminChat)) {
                    cm.removeRecipient(player);
                }
            } catch (ChatModeException e) {

            }
        }
        if (mode == null) {
            playerchatmode.remove(player);
        } else {
            ChatMode cm = getChatMode(mode);
            if (cm instanceof AdminChat) {
                Player p = plugin.getServer().getPlayer(player);
                if (p != null)
                    if (!plugin.check(p, "admon"))
                        cm.addRecipient(player);
            }
            playerchatmode.put(player, mode);
        }

    }

    public void addMute(String player) {
        if (!mutedPlayers.contains(player)) {
            mutedPlayers.add(player);
        }
    }

    public void removeMute(String player) {
        if (mutedPlayers.contains(player)) {
            mutedPlayers.remove(player);
        }
    }

    public List<String> getMutedPlayers() {
        return mutedPlayers;
    }

    public boolean isMuted(String playername) {
        return mutedPlayers.contains(playername.toLowerCase());
    }
}
