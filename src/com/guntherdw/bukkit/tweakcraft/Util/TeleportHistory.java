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

package com.guntherdw.bukkit.tweakcraft.Util;

import com.guntherdw.bukkit.tweakcraft.TweakcraftUtils;
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
    private Map<String, Integer> historyoffset = new HashMap<String, Integer>();

    public TeleportHistory(TweakcraftUtils instance) {
        this.plugin = instance;
        this.historymap.clear();
        this.historyoffset.clear();
    }

    public int getRemaining(String playername) {
        if(!historymap.containsKey(playername)) {
            return 0;
        } else {
            return historymap.get(playername).size();
        }
    }

    public boolean atOrigin(String player) {
        if(!historymap.containsKey(player)) return true;
        int size = historymap.get(player).size()-1;
        int position = 0;
        if(historyoffset.containsKey(player)) { Integer i = historyoffset.get(player);position+=(i!=null?i:0); }
        return position==size;
    }

    public List<Location> getHistoryList(String player) {
        if(!historymap.containsKey(player)) return null;
        return historymap.get(player);
    }

    // public int get

    public void setHistoryOffset(String player, Integer pos) {
        if((pos==null || pos < 0)
                && historyoffset.containsKey(player)) {
            historyoffset.remove(player);
        } else {
            historyoffset.put(player, pos);
        }
    }

    public int getOffset(String player) {
        Integer offs = -1;
        if(historyoffset.containsKey(player)) {
            offs = historyoffset.get(player);
        }
        return offs!=null?offs:0;
    }

    public Location get(String player, int position, Boolean back) {
        if(!plugin.getConfigHandler().enableTPBack) return null;
        if(!historymap.containsKey(player)) return null;
        List<Location> locList = historymap.get(player);

        if(position<0 || position>=locList.size()) return null;

        if(back!=null) {
            int offset = getOffset(player);
            offset = offset + (back? 1 : -1);
            setHistoryOffset(player, offset);
        }

        return locList.get(position);
    }


    public void removeLast(String playername) {
        if(historymap.containsKey(playername)) {
            List<Location> locmap = historymap.get(playername);
            locmap.remove(locmap.size()-1);
            if(locmap.size()==0) {
                historymap.remove(playername);
            } else {
                historymap.put(playername, locmap);
            }
        }
    }

    public void removeNext(String playername) {
        if(historymap.containsKey(playername) && historyoffset.containsKey(playername)) {
            List<Location> locmap = historymap.get(playername);
            int pos = locmap.size() - getOffset(playername);
            locmap.remove(pos);
            if(locmap.size()==0) {
                historymap.remove(playername);
            } else {
                historymap.put(playername, locmap);
            }
        }
    }

    public void addHistory(String playername, Location loc) {
        if(plugin.getConfigHandler().enableTPBack) {
            List<Location> locmap;
            if(loc.getY() > 128 || loc.getY() < 1) { // failsafe
                loc.setY(130);
            }


            if(historymap.containsKey(playername)) {
                locmap = historymap.get(playername);

                if(historyoffset.containsKey(playername) && getOffset(playername) > 0) {

                    int siz = locmap.size();
                    int pos = siz-getOffset(playername)+1;

                    locmap.removeAll(locmap.subList(pos, siz));
                    setHistoryOffset(playername, null);
                }
                if(!locmap.get(locmap.size()-1).equals(loc)) locmap.add(loc);
            } else {
                locmap = new ArrayList<Location>();
                locmap.add(loc);
            }
            this.historymap.put(playername, locmap);
        }
    }

    public void clearHistory() {
        plugin.getLogger().info("[TweakcraftUtils] Clearing complete TPBack history!");
        this.historymap.clear();
        this.historyoffset.clear();
    }

    public void clearFuture(String playername) {
        plugin.getLogger().info("[TweakcraftUtils] Clearing TPBack future for player "+playername+"!");
        if(historymap.containsKey(playername) && historyoffset.containsKey(playername)) {
            List<Location> loclist = historymap.get(playername);
            loclist.removeAll(loclist.subList(loclist.size()-getOffset(playername), loclist.size()));
            historymap.put(playername, loclist);
            setHistoryOffset(playername, null);
        }
    }

    public void clearHistory(String playername) {
        plugin.getLogger().info("[TweakcraftUtils] Clearing TPBack history for player "+playername+"!");
        if(historymap.containsKey(playername)) {
            historymap.remove(playername);
        }
        if(historyoffset.containsKey(playername)) {
            historyoffset.remove(playername);
        }
    }
}
