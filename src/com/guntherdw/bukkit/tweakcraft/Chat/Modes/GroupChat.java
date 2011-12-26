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
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

/**
 * @author GuntherDW
 */
public class GroupChat implements ChatMode {
    public boolean sendMessage(CommandSender sender, String message) {
        return false;
    }

    public List<Player> getRecipients(CommandSender sender) {
        return null;
    }

    public void addRecipient(String player) {

    }

    public boolean broadcastMessage(CommandSender sender, String message) {
        return false;
    }

    public void removeRecipient(String player) {

    }

    public List<String> getSubscribers() {
        return null;
    }

    public String getDescription() {
        return null;
    }

    public boolean isEnabled() {
        return false;
    }

    public String getColor() {
        return null;
    }

    public String getPrefix() {
        return null;
    }
}
