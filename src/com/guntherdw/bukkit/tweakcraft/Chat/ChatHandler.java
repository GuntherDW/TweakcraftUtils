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

import com.guntherdw.bukkit.tweakcraft.Chat.Modes.*;
import com.guntherdw.bukkit.tweakcraft.DataSources.PersistenceClass.PlayerOptions;
import com.guntherdw.bukkit.tweakcraft.Exceptions.ChatModeException;
import com.guntherdw.bukkit.tweakcraft.TweakcraftUtils;
import org.bukkit.entity.Player;

import java.util.*;

/**
 * @author GuntherDW
 */
public class ChatHandler {

    private TweakcraftUtils plugin;
    private AntiSpam antispam = null;
    private Map<String, ChatMode> chatmodes = new HashMap<String, ChatMode>();
    private Map<String, String> playerchatmode = new HashMap<String, String>();
    private Map<String, Long> mutedPlayers = new HashMap<String, Long>();

    public ChatHandler(TweakcraftUtils instance) {
        plugin = instance;
        chatmodes.clear();
        this.registerChatMode("admin", new AdminChat(this)); /* This one has a higher priority! */
        this.registerChatMode("local", new LocalChat(this));
        this.registerChatMode("region", new RegionChat(this));
        this.registerChatMode("zones", new ZoneChat(this));
        this.registerChatMode("world", new WorldChat(this));

        this.registerChatMode("tcutils", new TCUtilsChat(this)); /* IRC extra stuff */
    }
    
    public boolean registerChatMode(String chatModeLabel, ChatMode chatMode) {
        if(chatmodes.containsValue(chatMode)) return false;
        
        chatmodes.put(chatModeLabel, chatMode);
        return true;
    }

    public void enableAntiSpam() {
        if (plugin.getConfigHandler().enableSpamControl && antispam == null)
            this.antispam = new AntiSpam(this, plugin.getConfigHandler());
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

    public TweakcraftUtils getTCUtilsInstance() {
        return plugin;
    }

    public List<String> listChatModes() {
        List<String> l = new ArrayList<String>();
        for (String s : chatmodes.keySet()) {
            if ((chatmodes.get(s)).isEnabled() && !((chatmodes.get(s)).isHidden()))
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
        if (plugin.getConfigHandler().enableCUI) {
            Player p = plugin.getServer().getPlayer(player);
            if (p != null)
                plugin.sendCUIChatMode(p);
        }
    }

    public void addMute(String player) {
        addMute(player, null);
    }

    public void updateMute(String player, Long toTime) {
        mutedPlayers.put(player, toTime);
    }

    public AntiSpam getAntiSpam() {
        return this.antispam;
    }

    public void addMute(String player, Long duration) {
        Long toTime = null;

        if (player != null) player = player.toLowerCase(); /* Sanity checks */
        else return;

        if (duration != null) {
            toTime = Calendar.getInstance().getTime().getTime();
            toTime += duration * 1000;
        }
        if (plugin.getConfigHandler().enablePersistence) {
            PlayerOptions po = plugin.getDatabase().find(PlayerOptions.class).where().ieq("name", player).ieq("optionname", "mute").findUnique();
            if (po == null) {
                po = new PlayerOptions();
                po.setName(player);
                po.setOptionname("mute");
            }

            po.setOptionvalue(toTime == null ? null : toTime.toString());
            plugin.getDatabase().save(po);
        }
        mutedPlayers.put(player, toTime);
    }

    public boolean canTalk(String player) {
        if (mutedPlayers.containsKey(player.toLowerCase())) {
            Long checktime = Calendar.getInstance().getTime().getTime();
            Long muteTime = mutedPlayers.get(player.toLowerCase());
            if (muteTime == null || checktime < muteTime) {
                return false;
            }
            if (checktime > muteTime) {
                if (plugin.getConfigHandler().enableDebug)
                    plugin.getLogger().info("[TweakcraftUtils] Mutes: auto-unmuting " + player + ", his mutetime was over!");
                removeMute(player);
            }
        }
        return true;
    }

    public void removeMute(String player) {
        if (mutedPlayers.containsKey(player)) {
            mutedPlayers.remove(player);
        }
        if (plugin.getConfigHandler().enablePersistence) {
            PlayerOptions po = plugin.getDatabase().find(PlayerOptions.class).where().ieq("name", player).ieq("optionname", "mute").findUnique();
            if (po != null)
                plugin.getDatabase().delete(po);
        }
    }

    public Map<String, Long> getMutedPlayers() {
        return mutedPlayers;
    }

    public Long getRemaining(String player) {
        if (canTalk(player)) return null;
        Long remain = mutedPlayers.get(player);
        if (remain == null) return null;
        Long checktime = Calendar.getInstance().getTime().getTime();
        Double dres = Math.floor((remain - checktime) / 1000);
        return dres.longValue();
    }

    public boolean isMuted(String playername) {
        return mutedPlayers.containsKey(playername.toLowerCase());
    }
}
