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

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

/**
 * @author GuntherDW
 */
public abstract class ChatMode {

    protected Set<String> subscribers;
    protected ChatHandler chathandler;
    protected String chatModeName = null;
    protected Logger logger = Logger.getLogger("Minecraft");

    public ChatMode(ChatHandler instance) {
        subscribers = new HashSet<String>();
        this.chathandler = instance;
    }

    /**
     * Sends a message in the chosen method
     *
     * @param sender
     * @param message
     * @return
     */
    public boolean sendMessage(CommandSender sender, String message) {

        String playerMessage = String.format(getChatFormatString(), sender instanceof Player ? ((Player) sender).getDisplayName() : ChatColor.LIGHT_PURPLE + "CONSOLE" + ChatColor.WHITE, message);
        for (Player player : getRecipients(sender)) {
            player.sendMessage(playerMessage);
        }

        logChat(sender, message);

        return true;
    }

    /**
     * Broadcasts a message in the ChatMode without a playertag
     *
     * @param message
     * @return
     */
    public boolean broadcastMessage(String message) {
        // String msg = message;
        for (Player p : getRecipients(null)) {
            p.sendMessage(message);
        }

        logChat(message);
        return true;
    }

    /**
     * Returns a list of players that will get the message
     *
     * @return
     */
    public abstract Set<Player> getRecipients(CommandSender sender);

    /**
     * Tries to remove a recipient
     *
     * @param player
     */
    public void addRecipient(String player) {
        if (!subscribers.contains(player)) {
            subscribers.add(player);
        }
    }

    /**
     * Broadcasts a message in the current ChatMode
     *
     * @param sender
     * @param message
     * @return if the broadcast was successful
     */
    public abstract boolean broadcastMessage(CommandSender sender, String message);

    /**
     * Tries to remove a recipient
     *
     * @param player
     */
    public void removeRecipient(String player) {
        if (subscribers.contains(player)) {
            subscribers.remove(player);
        }
    }

    /**
     * Gets the current subscribed list
     *
     * @return
     */
    public Set<String> getSubscribers() {
        return this.subscribers;
    }

    /**
     * Shows a little description about the ChatMode
     *
     * @return The description
     */
    public abstract String getDescription();

    /**
     * Check if it's enabled
     *
     * @return enabled
     */
    public boolean isEnabled() {
        return false;
    }


    /**
     * Gets the ChatMode's color
     *
     * @return the color
     */
    public abstract String getColor();

    /**
     * Returns the ChatMode's prefix
     *
     * @return the prefix
     */
    public abstract String getPrefix();

    /**
     * Should this ChatMode show up in /chatmode list?
     *
     * @return boolean true if it should show up in /chatmode list
     */
    public boolean isHidden() {
        return false;
    }

    /**
     *
     */
    public String getLoggingFormatString() {
        String name = this.chatModeName == null ? getClass().getSimpleName() : chatModeName;
        return name + ": <%1$s> %2$s";
    }

    public String getLoggingFormatStringNoPlayerTag() {
            String name = this.chatModeName == null ? getClass().getSimpleName() : chatModeName;
            return name + ": %1$s";
        }

    public String getChatFormatString() {
        String prefix = getPrefix();
        return (prefix != null ? prefix + ": " : "") + "[%1$s] %2$s";
    }

    public void logChat(CommandSender sender, String message) {
        String logFormat = getLoggingFormatString();
        if (logFormat != null) {
            String logMessage = String.format(logFormat, sender instanceof Player ? ((Player) sender).getName() : "CONSOLE", message);
            logger.info(logMessage);
        }
    }

    public void logChat(String message) {

        String logFormat = getLoggingFormatString();
        if (logFormat != null) {
            String logMessage = String.format(getLoggingFormatStringNoPlayerTag(), message);
            logger.info(logMessage);
        }
    }
}
