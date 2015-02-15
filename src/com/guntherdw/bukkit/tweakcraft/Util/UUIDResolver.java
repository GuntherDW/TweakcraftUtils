/*
 * Copyright (c) 2014 GuntherDW
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

import com.guntherdw.bukkit.tweakcraft.DataSources.PersistenceClass.PlayerOptions;
import com.guntherdw.bukkit.tweakcraft.Packages.LocalPlayer;
import com.guntherdw.bukkit.tweakcraft.TweakcraftUtils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.logging.Level;

/**
 * @author GuntherDW
 */

/*
 * This class primary use will be to check for older profiles in the DB and update if required.
 */
public class UUIDResolver {

    private TweakcraftUtils plugin;
    private Map<String, UUID> uuids = new HashMap<String, UUID>();

    public UUIDResolver(TweakcraftUtils instance) {
        this.plugin = instance;
    }

    public UUID getOrCreateUUID(LocalPlayer lp) {

        if(uuids.containsKey(lp.getName()))
           return uuids.get(lp.getName());

        // List<PlayerOptions> popts = plugin.getDatabase().find(PlayerOptions.class).where().ieq("name", lp.getName()).findList();
        UUID retUUID = null;

        // if (popts.size() != 0) {
            PlayerOptions uuid = plugin.getDatabase().find(PlayerOptions.class).where().ieq("name", lp.getName()).ieq("optionname", "uuid").findUnique();
            if (uuid == null) {
                // Create one
                retUUID = lp.getBukkitOfflinePlayer().getUniqueId();
                if(retUUID == null) {
                    plugin.getLogger().warning("Didn't find any UUID for " + lp.getName() + ", is mojang's API down, or is this a fake player?!");
                    return null;
                }
                plugin.getLogger().warning("Didn't find a cached UUID for " + lp.getName() + ", updating PlayerOptions database!");
                uuid = new PlayerOptions();
                uuid.setName(lp.getName());
                uuid.setOptionname("uuid");
                uuid.setOptionvalue(retUUID.toString());
                plugin.getDatabase().save(uuid);
            } else {
                plugin.getLogger().log(Level.FINE, "We did find a cached UUID value for "+ lp.getName() + ", using that!");
                retUUID = UUID.fromString(uuid.getOptionvalue());
            }
        // }

        lp.setUuid(retUUID);
        uuids.put(lp.getName(), retUUID);
        return retUUID;
    }

    public UUID getOrCreateUUID(String player) {
        return getOrCreateUUID(plugin.wrapPlayer(player));
    }

    public void checkProfile(Player p) {

        if(uuids.containsKey(p.getName()))
            return;

        LocalPlayer lp = plugin.wrapPlayer(p);

        String player = p.getName();

        // Check if the person changed his name
        PlayerOptions oldName = plugin.getDatabase().find(PlayerOptions.class).where().ieq("optionname", "uuid").ieq("optionvalue", p.getUniqueId().toString()).findUnique();

        if (oldName != null && !oldName.getName().equalsIgnoreCase(player)) {
            plugin.getLogger().warning(oldName.getName() + " changed his name to " + player + ", updating PlayerOptions database!");

            List<PlayerOptions> oldOpts = plugin.getDatabase().find(PlayerOptions.class).where().ieq("name", oldName.getName()).findList();

            // Update DB!
            // plugin.getDatabase().find(PlayerOptions.class).where().ieq("name",  oldName).findList()
            for (Iterator<PlayerOptions> iterator = oldOpts.iterator(); iterator.hasNext(); ) {
                PlayerOptions next = iterator.next();
                next.setName(player);
            }
            plugin.getDatabase().save(oldOpts);

        }

        // List<PlayerOptions> popts = plugin.getDatabase().find(PlayerOptions.class).where().ieq("name", player).findList();
        // if(popts.size() != 0) {
            PlayerOptions uuid = plugin.getDatabase().find(PlayerOptions.class).where().ieq("name", player).ieq("optionname", "uuid").findUnique();
            if(uuid == null) {
                plugin.getLogger().warning(player + " had an old profile, updating PlayerOptions database!");
                uuid = new PlayerOptions();
                uuid.setName(player);
                uuid.setOptionname("uuid");
                uuid.setOptionvalue(p.getUniqueId().toString());
                plugin.getDatabase().save(uuid);
            }
        // }
    }
}
