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

package com.guntherdw.bukkit.tweakcraft;

import org.bukkit.Location;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author GuntherDW
 */
public class TeleportHistory {

    private TweakcraftUtils plugin;
    private Map<String, List<Location>> historymap = new HashMap<String, List<Location>>();

    public TeleportHistory(TweakcraftUtils instance) {
        this.plugin = instance;
    }

    public Integer getRemaining(String playername) {
        if(!historymap.containsKey(playername)) {
            return null;
        } else {
            return historymap.get(playername).size();
        }
    }

    public Location getLastEntry(String playername) {
        if(plugin.getConfigHandler().enableTPBack) {
            Location loc = null;
            if(!historymap.containsKey(playername)) {
                return null;
            } else {
                List<Location> locmap = historymap.get(playername);
                loc = locmap.get(locmap.size()-1);
                locmap.remove(locmap.size()-1);
                if(locmap.size()==0) {
                    historymap.remove(playername);
                } else {
                    historymap.put(playername, locmap);
                }
            }
            return loc;
        } else {
            return null;
        }
    }

    public void addHistory(String playername, Location loc) {
        if(plugin.getConfigHandler().enableTPBack) {
            List<Location> locmap;
            if(loc.getY() > 128 || loc.getY() < 5) { // failsave
                loc.setY(130);
            }
            if(historymap.containsKey(playername)) {
                 locmap = historymap.get(playername);
                if(!locmap.get(locmap.size()-1).equals(loc)) { // Do not save identical tpback issues
                    locmap.add(loc);
                }
            } else {
                locmap = new ArrayList<Location>();
                locmap.add(loc);
            }
            this.historymap.put(playername, locmap);
        }
    }

    public void clearHistory() {
        plugin.getLogger().info("[TweakcraftUtils] Clearing complete TPBack history!");
        historymap.clear();
    }

    public void clearHistory(String playername) {
        plugin.getLogger().info("[TweakcraftUtils] Clearing TPBack history for player "+playername+"!");
        if(historymap.containsKey(playername)) {
            historymap.remove(playername);
        }
    }
}
