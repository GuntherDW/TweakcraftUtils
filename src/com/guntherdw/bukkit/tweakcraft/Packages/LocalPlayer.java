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

package com.guntherdw.bukkit.tweakcraft.Packages;

import com.guntherdw.bukkit.tweakcraft.Chat.ChatHandler;
import com.guntherdw.bukkit.tweakcraft.Chat.ChatMode;
import com.guntherdw.bukkit.tweakcraft.Exceptions.ChatModeException;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

/**
 * @author GuntherDW
 */
public class LocalPlayer {

    private String name;
    private UUID uuid;
    private Player bukkitPlayer;
    private ChatMode chatMode;
    private boolean afk;
    private int spamcounter;
    private long lastmessagetime;
    private String nick = null;
    private boolean tpOff = false;
    private boolean invisible = false;
    private boolean invisiblePickup = false;
    private String replyTo = null;
    private boolean tntArrow = false;
    private boolean allowedFlight = false;
    private String capeURL = null;

    public boolean isInvisible() {
        return this.invisible;
    }

    public boolean isAllowedFlight() {
        return allowedFlight;
    }

    public void setAllowedFlight(boolean hasFlight) {
        this.allowedFlight = hasFlight;
        if(getBukkitPlayer()!=null)
            getBukkitPlayer().setAllowFlight(hasFlight);
    }

    public void setInvisible(boolean state) {
        this.invisible = state;
    }

    public boolean isTpOff() {
        return tpOff;
    }

    public void setTpOff(boolean state) {
        this.tpOff = state;
    }

    public void setInvisiblePickup(boolean state) {
        this.invisiblePickup = state;
    }

    public boolean isTntArrow() {
        return tntArrow;
    }

    public void setTntArrow(boolean tntArrow) {
        this.tntArrow = tntArrow;
    }

    public boolean hasNick() {
        return nick != null;
    }

    public String getNick() {
        return this.nick;
    }

    public String getReplyTo() {
        return replyTo;
    }

    public void setReplyTo(String replyTo) {
        this.replyTo = replyTo;
    }

    public void setNick(String nick) {
        this.nick = nick;
    }

    public Player getBukkitPlayerSafe() {
        if (this.bukkitPlayer == null) {
            Player p = Bukkit.getPlayerExact(name);
            if (p != null) bukkitPlayer = p;
        }
        return bukkitPlayer;
    }

    public Player getBukkitPlayer() {
        return bukkitPlayer;
    }

    public void setBukkitPlayer(Player bukkitPlayer) {
        this.bukkitPlayer = bukkitPlayer;
    }

    public boolean isAfk() {
        return afk;
    }

    public void setAfk(boolean afk) {
        this.afk = afk;
        this.getBukkitPlayerSafe().setSleepingIgnored(afk);
    }

    public long getLastmessagetime() {
        return lastmessagetime;
    }

    public void setLastmessagetime(long lastmessagetime) {
        this.lastmessagetime = lastmessagetime;
    }

    public int getSpamcounter() {
        return spamcounter;
    }

    public void setSpamcounter(int spamcounter) {
        this.spamcounter = spamcounter;
    }

    public String getName() {
        return name;
    }

    public LocalPlayer(Player player) {
        this.bukkitPlayer = player;
        this.name = player.getName();
        this.uuid = player.getUniqueId();
    }

    public LocalPlayer(String name) {
        this.name = name;
    }

    public LocalPlayer(UUID uuid) {
        this.uuid = uuid;
    }

    public LocalPlayer(String name, UUID uuid) {
        this.name = name;
        this.uuid = uuid;
    }

    public ChatMode getChatMode() {
        return chatMode;
    }

    public void setChatMode(ChatMode chatMode) {
        this.chatMode = chatMode;
    }
    
    public void setCapeURL(String URL) {
        this.capeURL = URL;
    }

    public boolean isInvisiblePickup() {
        return invisiblePickup;
    }

    public String getCapeURL() {
        return this.capeURL;
    }

    /**
     * Try to resolve a ChatMode and set it
     *
     * @param chathandler The ChatHandler instance
     * @param chatmode    String defining the ChatMode
     * @throws ChatModeException Thrown when either the ChatMode isn't found, or isn't enabled
     */
    public boolean toggleChatMode(ChatHandler chathandler, String chatmode) throws ChatModeException {
        ChatMode cm = null;
        cm = chathandler.getChatMode(chatmode);

        if (cm != null && chathandler.getTCUtilsInstance().check(this.bukkitPlayer, "chat.mode." + chatmode)) {
            this.setChatMode(cm);
            return true;
        }

        return false;
    }
}
