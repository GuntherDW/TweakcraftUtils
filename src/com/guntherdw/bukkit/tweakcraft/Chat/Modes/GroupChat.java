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
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Set;

/**
 * @author GuntherDW
 */
public class GroupChat extends ChatMode {

    public GroupChat(ChatHandler instance) {
        super(instance);
    }

    @Override
    public boolean sendMessage(CommandSender sender, String message) {
        return false;
    }

    @Override
    public Set<Player> getRecipients(CommandSender sender) {
        return null;
    }

    @Override
    public boolean broadcastMessage(CommandSender sender, String message) {
        return false;
    }

    @Override
    public String getDescription() {
        return null;
    }


    @Override
    public String getColor() {
        return null;
    }

    @Override
    public String getPrefix() {
        return null;
    }
}
