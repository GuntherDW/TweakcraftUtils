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

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

/**
 * @author GuntherDW
 */
public interface ChatMode {

    /**
     * Sends a message in the chosen method
     *
     * @param sender
     * @param message
     * @return
     */
    public abstract boolean sendMessage(CommandSender sender, String message);

    /**
     * Returns a list of players that will get the message
     *
     * @return
     */

    public abstract List<Player> getRecipients(CommandSender sender);

    /**
     * Tries to remove a recipient
     *
     * @param player
     */
    public abstract void addRecipient(String player);

    /**
     * Tries to remove a recipient
     *
     * @param player
     */
    public abstract void removeRecipient(String player);

    /**
     * Gets the current subscribed list
     *
     * @return
     */

    public abstract List<String> getSubscribers();

}
