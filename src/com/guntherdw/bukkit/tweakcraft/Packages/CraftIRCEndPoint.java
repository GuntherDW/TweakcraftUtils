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

import com.ensifera.animosity.craftirc.CraftIRC;
import com.ensifera.animosity.craftirc.EndPoint;
import com.ensifera.animosity.craftirc.RelayedMessage;
import com.guntherdw.bukkit.tweakcraft.Chat.ChatHandler;
import com.guntherdw.bukkit.tweakcraft.TweakcraftUtils;

import java.util.List;

/**
 * @author GuntherDW
 */
public class CraftIRCEndPoint implements EndPoint {

    private ChatHandler ch = null;
    private CraftIRC circ = null;

    public CraftIRCEndPoint(TweakcraftUtils instance) {
        this.ch = instance.getChathandler();
        this.circ = instance.getCraftIRC();
        circ.registerEndPoint("tcutils", this);
    }

    public Type getType() {
        return EndPoint.Type.MINECRAFT;
    }

    public void messageIn(RelayedMessage relayedMessage) {
        /* String event = relayedMessage.getEvent();
        if(event.equals("chat") || event.equals("action")) {
            String pname = relayedMessage.getField("sender");
            if(pname!=null) {
                String name = relayedMessage.getField("sender");
                String nick = plugin.getPlayerListener().findPlayerNameByNick(name, true);
                relayedMessage.setField("sender", plugin.getNickWithColors(nick==null?name:nick));
            }
            ac.broadcastMessage(relayedMessage.getMessage(this));
        } */
    }

    public boolean userMessageIn(String s, RelayedMessage relayedMessage) {
        return false;
    }

    public boolean adminMessageIn(RelayedMessage relayedMessage) {
        return false;
    }

    public List<String> listUsers() {
        return null;
    }

    public List<String> listDisplayUsers() {
        return null;
    }
}
