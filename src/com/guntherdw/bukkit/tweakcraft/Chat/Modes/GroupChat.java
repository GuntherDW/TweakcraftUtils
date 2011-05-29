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
    @Override
    public boolean sendMessage(CommandSender sender, String message) {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public List<Player> getRecipients(CommandSender sender) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void addRecipient(String player) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void removeRecipient(String player) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public List<String> getSubscribers() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public String getDescription() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean isEnabled() {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public String getColor() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public String getPrefix() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
