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

package com.guntherdw.bukkit.tweakcraft.DataSources.Ban;

import com.guntherdw.bukkit.tweakcraft.DataSources.PersistenceClass.PlayerOptions;
import com.guntherdw.bukkit.tweakcraft.Packages.Ban;
import com.guntherdw.bukkit.tweakcraft.TweakcraftUtils;
import com.guntherdw.bukkit.tweakcraft.Util.TimeTool;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author GuntherDW
 */
public class BanHandler {

    private Map<UUID, Ban> bans;
    private TweakcraftUtils plugin;

    private final String BANNED_SECTION = "banned-players";

    public BanHandler(TweakcraftUtils instance) {
        this.plugin = instance;
        /**
         * This is handled later on with reloadBans();
         */
        // loadBans();
    }

    public Ban searchBan(String playername) {
        //Ban ban = null;
        for (Map.Entry<UUID, Ban> entryMap : bans.entrySet()) {
            if (entryMap.getValue().getPlayer().equalsIgnoreCase(playername)) {
                return entryMap.getValue();
            }
        }
        return null; // I Has found nothing!
    }

    private String findOldBan(Map<String, Ban> oldBanMap, String playerName) {
        for(Map.Entry<String, Ban> banEntry : oldBanMap.entrySet()) {
            if(playerName.equalsIgnoreCase(banEntry.getKey()))
                return banEntry.getKey();
        }
        return null;
    }

    private void convertOldBanFile(File oldBanFile, YamlConfiguration newBanConfig) {

        Map<String, Ban> oldBans = new HashMap<String, Ban>();

        try {
            // File banfile = new File(plugin.getDataFolder(), "banned-players.txt");

            Ban.skipUUIDCheck = true;
            BufferedReader banfilereader = new BufferedReader(new FileReader(oldBanFile));
            String line = "";
            while ((line = banfilereader.readLine()) != null) {
                if (!line.trim().equals("")) {
                    String[] lin = line.split(",");
                    if (lin.length > 1)
                        oldBans.put(lin[0], new Ban(lin[0], lin[1]));
                    else
                        oldBans.put(lin[0], new Ban(lin[0], ""));
                }
            }
            banfilereader.close();

            Map<String, UUID> uuids = new HashMap<String, UUID>();

            try {
                if(Class.forName("com.evilmidget38.UUIDFetcher") != null) {
                    List<String> bannedNames = new ArrayList<String>(oldBans.keySet());
                    com.evilmidget38.UUIDFetcher fetcher = new com.evilmidget38.UUIDFetcher(bannedNames, true);
                    uuids = fetcher.call();
                }

            } catch( Exception e ) {
                /* Do it the slow way */

                plugin.getLogger().warning("Didn't find evilmidgets' UUIDFetcher, doing the lookup the slow way.");
                for (Map.Entry<String, Ban> banEntry : oldBans.entrySet()) {
                /* Check for UUID, just remove the play if we didn't find any (non-existant profile) */
                    OfflinePlayer op = Bukkit.getOfflinePlayer(banEntry.getKey());
                    UUID uuid = op.getUniqueId();
                    uuids.put(op.getName(), op.getUniqueId());
                }
            }

            Ban.skipUUIDCheck = false;

            // Go over the list again because names might not be in the correct case.
            for(Map.Entry<String, UUID> uuidEntry : uuids.entrySet()) {
                String findBan = findOldBan(oldBans, uuidEntry.getKey());
                if(!(findBan.equals(uuidEntry.getKey()))) {
                    Ban ban = oldBans.get(findBan);
                    ban.setPlayer(uuidEntry.getKey());
                    oldBans.remove(findBan);
                    oldBans.put(uuidEntry.getKey(), ban);
                }
            }

            for (Map.Entry<String, Ban> banEntry : oldBans.entrySet()) {
                UUID uuid = uuids.get(banEntry.getKey());
                if (uuid == null) {
                    plugin.getLogger().warning("No Mojang profile found for " + banEntry.getValue().getPlayer() + ", ignoring line!");
                } else {
                    Ban ban = banEntry.getValue();
                    String playerName = ban.getPlayer();
                    Long toTime = ban.getToTime();
                    String reason = ban.getReason();

                    String section = BANNED_SECTION + "." + uuid;
                    newBanConfig.createSection(section);
                    newBanConfig.set(section + ".playername", playerName);
                    newBanConfig.set(section + ".reason", reason);
                    if (toTime != null) {
                        newBanConfig.set(section + ".toTime", toTime);
                    }
                }
            }


            boolean deleted = oldBanFile.delete();
            if(!deleted) plugin.getLogger().warning("Could not remove old banfile!");

            plugin.getLogger().info("Converted banned players file to newer format.");

        } catch (FileNotFoundException e) {
            plugin.getLogger().warning("Ban file not found!");
        } catch (IOException e) {
            plugin.getLogger().info("Ban file I/O error!");
        }
    }

    private void loadBans() {

        bans = new HashMap<UUID, Ban>();

        File banFile = new File(plugin.getDataFolder(), "banned-players.yml");
        if(!banFile.exists()) {
            try {
                banFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        YamlConfiguration banConfig = new YamlConfiguration();
        try {
            banConfig.load(banFile);
        } catch(Exception e) {
            plugin.getLogger().warning("Error while loading banned-players.yml, does this file exist?");
        }

        // check for old BanFile
        File oldBanFile = new File(plugin.getDataFolder(), "banned-players.txt");
        if (oldBanFile.exists()) {
            plugin.getLogger().warning("Old banned-players file found, converting to new format!");
            this.convertOldBanFile(oldBanFile, banConfig);
            try {
                banConfig.save(banFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        ConfigurationSection section = banConfig.getConfigurationSection(BANNED_SECTION);
        if (section != null) {
            Set<String> bannedUUIDSet = section.getKeys(false);
            for (String b : bannedUUIDSet) {
                UUID uuid = UUID.fromString(b);
                String playerName = banConfig.getString(BANNED_SECTION + "." + b + ".playername");
                String reason = banConfig.getString(BANNED_SECTION + "." + b + ".reason");

                Long time = banConfig.getLong(BANNED_SECTION + "." + b + ".toTime", 0L);
                if (time == 0L) time = null;

                Ban ban = new Ban(playerName, uuid, reason, time);
                bans.put(uuid, ban);
            }
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

                boolean fakeplayer = false;

                PlayerOptions poUUID = plugin.getDatabase().find(PlayerOptions.class).where().ieq("name", po.getName()).ieq("optionname", "uuid").findUnique();
                if(poUUID == null) {

                    OfflinePlayer op = Bukkit.getServer().getOfflinePlayer(po.getName());
                    UUID uuid = op.getUniqueId();
                    if (uuid == null) {
                        plugin.getLogger().warning("Found a banned player in the database without a GameProfile, does this user exist? "+po.getName());
                        fakeplayer = true;
                    } else {
                        poUUID = new PlayerOptions();
                        poUUID.setName(po.getName());
                        poUUID.setOptionname("uuid");
                        poUUID.setOptionvalue(uuid.toString());
                        plugin.getDatabase().save(poUUID);
                    }
                    if(!fakeplayer) bans.put(UUID.fromString(poUUID.getOptionvalue()), new Ban(po.getName(), reason, time));
                }

            }
        }

        // return banlist;
    }

    public boolean isBanned(UUID uuid) {
        boolean banned = false;

        Ban b = bans.get(uuid);
        // Ban b = searchBan(playername);

        if (b != null) {
            if (b.getToTime() == null) {
                banned = true;
            } else {
                Long curTime = Calendar.getInstance().getTime().getTime();
                if (curTime < b.getToTime()) {
                    if (plugin.getConfigHandler().enableDebug) {
                        Long toTime = b.getToTime();
                        Double timerem = Math.floor((toTime - curTime) / 1000);
                        String toGo = TimeTool.calcLeft(timerem.longValue());
                        plugin.getLogger().info("Bans: " + b.getPlayer() + " still has " + toGo + " to go!");
                    }
                    banned = true;
                } else {
                    plugin.getLogger().info("Bans: auto-unbanning " + b.getPlayer() + ", his bantime was over!");
                    if (plugin.getConfigHandler().enableDebug) {
                        Long toTime = b.getToTime();
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

                        plugin.getLogger().info("Bans: it expired at " + sdf.format(new Date(toTime)) + "!");
                    }
                    banned = false;
                    unBan(b.getPlayer());
                }
            }
        }
        return banned;
    }

    public boolean isBanned(String playername) {
        Ban b = searchBan(playername);
        return b != null && isBanned(b.getUUID());
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

    public Ban isBannedBan(UUID uuid) {
        return bans.get(uuid);
        // return null;
    }

    public boolean isBannedFullname(String playername) {
        return bans.containsKey(playername);
    }

    public boolean banPlayer(UUID uuid, String playername, String reason) {
        if (playername.trim().equals("")) {
            plugin.getLogger().info("Can't ban an empty player!");
        } else {
            bans.put(uuid, new Ban(playername, uuid, reason, null));
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

    public boolean banPlayer(UUID uuid, String player, String reason, Long duration) {
        if(duration == null) { return banPlayer(uuid, player.toLowerCase(), reason); } else {
            String playername = player.toLowerCase();
            Long toTime = null;
            toTime  = Calendar.getInstance().getTime().getTime();
            toTime += duration*1000;
            if (playername.trim().equals("")) {
                plugin.getLogger().info("Can't ban an empty player!");
                return false;
            } else {
                bans.put(uuid, new Ban(playername, uuid, reason, toTime));
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
                PlayerOptions banmsg = plugin.getDatabase().find(PlayerOptions.class).where().ieq("name", playername).ieq("optionname", "banmsg").findUnique();
                if(banmsg==null) {
                    banmsg = new PlayerOptions();
                    banmsg.setName(playername);
                    banmsg.setOptionname("banmsg");
                }
                
                banmsg.setOptionvalue(reason);
                plugin.getDatabase().save(banmsg);
                return true;
            }
        }
    }

    public Map<UUID, Ban> getBans() {
        return bans;
    }

    public void unBan(String player) {
        OfflinePlayer p = Bukkit.getOfflinePlayer(player);
        UUID uuid = p.getUniqueId();
        if(bans.containsKey(uuid))
            bans.remove(uuid);

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

        File banFile = new File(plugin.getDataFolder(), "banned-players.yml");
        if (!banFile.exists()) {
            try {
                banFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        YamlConfiguration banConfig = new YamlConfiguration();
        banConfig.createSection(BANNED_SECTION);
        for(Map.Entry<UUID, Ban> banEntry : bans.entrySet()) {
            Ban ban = banEntry.getValue();
            String playerName = ban.getPlayer();
            Long toTime = ban.getToTime();
            String reason = ban.getReason();

            String section = BANNED_SECTION+"."+banEntry.getKey().toString();
            banConfig.createSection(section);
            banConfig.set(section + ".playername", playerName);
            banConfig.set(section+".reason", reason);
            if(toTime != null) {
                banConfig.set(section+".toTime", toTime);
            }
        }
        try {
            banConfig.save(banFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void reloadBans() {
        this.loadBans();
        plugin.getLogger().info("Loaded banlist, " + bans.size() + " bans and counting!");
    }
}
