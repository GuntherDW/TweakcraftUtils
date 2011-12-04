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

package com.guntherdw.bukkit.tweakcraft.Listeners;

import com.guntherdw.bukkit.tweakcraft.TweakcraftUtils;
import org.bukkit.ChatColor;
import org.bukkit.event.block.BlockListener;
import org.bukkit.event.block.SignChangeEvent;

/**
 * @author GuntherDW
 */
public class TweakcraftBlockListener extends BlockListener {
    
    private TweakcraftUtils plugin = null;
    
    public TweakcraftBlockListener(TweakcraftUtils instance) {
        this.plugin = instance;
    }
    
    public void onSignChange(SignChangeEvent event) {
        if(event.isCancelled()) return;
        if(plugin.getConfigHandler().enableTweakTravel&&event.getLine(0).equalsIgnoreCase("[TweakTravel]") && !plugin.check(event.getPlayer(), "tweaktravel.create")) {
            event.getPlayer().sendMessage(ChatColor.RED+"You don't have permission to create TweakTravel signs!");
            event.setCancelled(true);
        }
    }
}
