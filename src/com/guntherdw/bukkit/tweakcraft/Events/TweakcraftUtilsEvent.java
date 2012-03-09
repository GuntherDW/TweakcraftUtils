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

package com.guntherdw.bukkit.tweakcraft.Events;

import com.guntherdw.bukkit.tweakcraft.Packages.LocalPlayer;
import com.guntherdw.bukkit.tweakcraft.TweakcraftUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;


public class TweakcraftUtilsEvent extends Event {

	protected Action action;
    private LocalPlayer player;
	private static final HandlerList handlers = new HandlerList();

	public TweakcraftUtilsEvent(Action action) {
		this.action = action;
	}

    public LocalPlayer getPlayer() {
        return player;
    }

    public void setPlayer(LocalPlayer player) {
        this.player = player;
    }

    public void setPlayer(Player player) {
        TweakcraftUtils.getInstance().wrapPlayer(player);
    }

    public Action getAction() {
		return this.action;
	}

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}

	public enum Action {
        CHATMODE_CHANGED,
        NICK_CHANGED,
        RELOAD_INFO
	}
}