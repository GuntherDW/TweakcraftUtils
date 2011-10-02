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
    
    @Override
    public Type getType() {
        return EndPoint.Type.IRC;
    }

    @Override
    public void messageIn(RelayedMessage relayedMessage) {
        /* chat: '%grey%[IRC]%foreground% <%ircPrefix%%sender%> %message%'
              private: '%grey%[IRC]%foreground% %sender% whispers> %message%'
              action: '%grey%[IRC]%purple% * %ircPrefix%%sender% %message%'
              join: '%grey%[IRC]%darkgreen% * Joins: %sender%'
              part: '%grey%[IRC]%darkgreen% * Parts: %sender%'
              quit: '%grey%[IRC]%blue% * Quits: %sender%'
              kick: '%grey%[IRC]%darkgreen% * %sender% was kicked by %ircModPrefix%%moderator%'
              nick: '%grey%[IRC]%darkgreen% * %sender% is now known as %message%'
              generic: '%grey%%message%' */
        
        /* String event = relayedMessage.getEvent();
        if(event.equals("chat") || event.equals("action")) {

        } */
    }

    @Override
    public boolean userMessageIn(String s, RelayedMessage relayedMessage) {
        return false;
    }

    @Override
    public boolean adminMessageIn(RelayedMessage relayedMessage) {
        return false;
    }

    @Override
    public List<String> listUsers() {
        return null;
    }

    @Override
    public List<String> listDisplayUsers() {
        return null;
    }
}
