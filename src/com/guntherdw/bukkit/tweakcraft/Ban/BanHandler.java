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

package com.guntherdw.bukkit.tweakcraft.Ban;

import com.guntherdw.bukkit.tweakcraft.DataSources.PersistenceClass.PlayerOptions;
import com.guntherdw.bukkit.tweakcraft.Packages.Ban;
import com.guntherdw.bukkit.tweakcraft.TweakcraftUtils;
import com.guntherdw.bukkit.tweakcraft.Util.TimeTool;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author GuntherDW
 */
public class BanHandler {

    private Map<String, Ban> bans;
    private TweakcraftUtils plugin;

    public BanHandler(TweakcraftUtils instance) {
        this.plugin = instance;
        /**
         * This is handled later on with reloadBans();
         */
        // loadBans();
    }

    public Ban searchBan(String playername) {
        //Ban ban = null;
        for (String search : bans.keySet()) {
            if (search.equalsIgnoreCase(playername)) {
                return bans.get(search);
            }
        }
        return null; // I Has found nothing!
    }

    private void loadBans() {
        bans = new HashMap<String, Ban>();
        try {
            File banfile = new File(plugin.getDataFolder(), "banned-players.txt");
            BufferedReader banfilereader = new BufferedReader(new FileReader(banfile));
            String line = "";
            while ((line = banfilereader.readLine()) != null) {
                if (!line.trim().equals("")) {
                    String[] lin = line.split(",");
                    if (lin.length > 1)
                        bans.put(lin[0], new Ban(lin[0], lin[1]));
                    else
                        bans.put(lin[0], new Ban(lin[0], ""));
                }
            }
            banfilereader.close();
        } catch (FileNotFoundException e) {
            plugin.getLogger().info("[TweakcraftUtils] Ban file not found!");
        } catch (IOException e) {

        }
        if(plugin.getConfigHandler().enablePersistence) {
            List<PlayerOptions> popts = plugin.getDatabase(). find(PlayerOptions.class).where().ieq("optionname", "ban").findList();
            for(PlayerOptions po : popts) {
                PlayerOptions tmppo = plugin.getDatabase().find(PlayerOptions.class).where().ieq("name", po.getName()).ieq("optionname", "banmsg").findUnique();
                String reason = "";
                if(tmppo!=null)
                    reason = tmppo.getOptionvalue();
                Long time = null;
                try{
                    time = Long.parseLong(po.getOptionvalue());
                } catch(NumberFormatException ex) { }
                bans.put(po.getName(), new Ban(po.getName(), reason, time));
            }
        }

        // return banlist;
    }

    public boolean isBanned(String playername) {
        boolean banned = false;
        Ban b = searchBan(playername);
        if(b!=null) {
            if(b.getToTime()==null) {
                banned = true;
            } else {
                Long curTime = Calendar.getInstance().getTime().getTime();
                if(curTime<b.getToTime()) {
                    if(plugin.getConfigHandler().enableDebug) {
                        Long toTime = b.getToTime();
                        Double timerem = Math.floor((toTime-curTime)/1000);
                        String toGo = TimeTool.calcLeft(timerem.longValue());
                        plugin.getLogger().info("[TweakcraftUtils] Bans: "+b.getPlayer()+" still has "+ toGo + " to go!");
                    }
                    banned = true;
                }
                else {
                    plugin.getLogger().info("[TweakcraftUtils] Bans: auto-unbanning "+b.getPlayer()+", his bantime was over!");
                    if(plugin.getConfigHandler().enableDebug) {
                        Long toTime = b.getToTime();
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                        
                        plugin.getLogger().info("[TweakcraftUtils] Bans: it expired at "+sdf.format(new Date(toTime))+"!");
                    }
                    banned = false;
                    unBan(b.getPlayer());
                }
            }
        }
        return banned;
    }

    public String getRemainingTime(String playername) {
        if(this.isBanned(playername)) {
            Ban b = isBannedBan(playername);
            if(b.getToTime()==null) {
                return "forever";
            } else {
                Long curTime = Calendar.getInstance().getTime().getTime();
                Long toTime = b.getToTime();
                Double timerem = Math.floor((toTime-curTime)/1000);
                return TimeTool.calcLeft(timerem.longValue());
            }
        } else {
            return null;
        }
    }

    public String getReason(String playername) {
        Ban b = this.isBannedBan(playername);
        if(b!=null) {
            return b.getReason();
        } else {
            return null;
        }
    }

    public Ban isBannedBan(String playername) {
        if(isBanned(playername))
            return searchBan(playername);
        else
            return null;
    }

    public boolean isBannedFullname(String playername) {
        return bans.containsKey(playername);
    }

    public boolean banPlayer(String playername, String reason) {
        if (playername.trim().equals("")) {
            plugin.getLogger().info("[TweakcraftUtils] Can't ban an empty player!");
        } else {
            bans.put(playername, new Ban(playername, reason));
            if(plugin.getConfigHandler().enablePersistence) {
                PlayerOptions po = plugin.getDatabase().find(PlayerOptions.class).where().ieq("name", playername).ieq("optionname", "ban").findUnique();
                if(po==null) {
                    po = new PlayerOptions();
                    po.setName(playername);
                    po.setOptionname("ban");
                }
                plugin.getDatabase().save(po);
            }
            return true;
        }
        return false;
    }

    public boolean banPlayer(String playername, String reason, Long duration) {
        if(duration == null) { return banPlayer(playername, reason); } else {
            Long toTime = null;
            toTime  = Calendar.getInstance().getTime().getTime();
            toTime += duration*1000;
            if (playername.trim().equals("")) {
                plugin.getLogger().info("[TweakcraftUtils] Can't ban an empty player!");
                return false;
            } else {
                bans.put(playername, new Ban(playername, reason, toTime));
                // if(plugin.getConfigHandler().enablePersistence) /* Don't need this, CommandBan already checks this! */
                PlayerOptions po = plugin.getDatabase().find(PlayerOptions.class).where().ieq("name", playername).ieq("optionname", "ban").findUnique();
                if(po==null) {
                    po = new PlayerOptions();
                    po.setName(playername);
                    po.setOptionname("ban");
                }
                po.setOptionvalue(toTime.toString());
                plugin.getDatabase().save(po);
                /* Now we have to save the reason! */
                po = plugin.getDatabase().find(PlayerOptions.class).where().ieq("name", playername).ieq("optionname", "banmsg").findUnique();
                if(po==null) {
                    po = new PlayerOptions();
                    po.setName(playername);
                    po.setOptionname("banmsg");
                }
                po.setOptionvalue(reason);
                plugin.getDatabase().save(po);
                return true;
            }
        }
    }

    public Map<String, Ban> getBans() {
        return bans;
    }

    public void unBan(String player) {
        if (bans.containsKey(player)) {
            bans.remove(player);
        }
        if(plugin.getConfigHandler().enablePersistence) {
            List<PlayerOptions> popts = plugin.getDatabase().find(PlayerOptions.class).where().ieq("name", player).in("optionname", "ban", "banmsg").findList();
            for(PlayerOptions po : popts)
                plugin.getDatabase().delete(po);
        }

    }

    /**
     * This recreates banned-player.txt with the list that is loaded in memory
     *
     * This excludes the persistent (non-permament bans) ones. They are saved automatically
     * as well so we won't need something like this.
     */
    public void saveBans() {
        File banfile = new File(plugin.getDataFolder(), "banned-players.txt");
        plugin.getLogger().info("[TweakcraftUtils] Trying to save banlist!");
        if (!banfile.exists()) {
            try {
                banfile.createNewFile();
            } catch (IOException e) {
                plugin.getLogger().severe("[TweakcraftUtils] Failed trying to create banlist!");
            }
        }

        try {
            BufferedWriter banfilewriter = new BufferedWriter(new FileWriter(banfile));
            for (String bannedplayer : bans.keySet()) {
                if(bans.get(bannedplayer).getToTime()!=null)
                {
                    String line = bannedplayer + "," + bans.get(bannedplayer).getReason() + "\n";
                    banfilewriter.write(line);
                }
            }
            banfilewriter.close();
            plugin.getLogger().info("[TweakcraftUtils] Save complete!");
        } catch (FileNotFoundException e) {
            plugin.getLogger().info("[TweakcraftUtils] Ban file not found!");
        } catch (IOException e) {
            plugin.getLogger().severe("[TweakcraftUtils] Failed trying to save banlist!");
        }
    }

    public void reloadBans() {
        this.loadBans();
        plugin.getLogger().info("[TweakcraftUtils] Loaded banlist, " + bans.size() + " bans and counting!");
    }
}
