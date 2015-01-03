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
import com.guntherdw.bukkit.tweakcraft.TweakcraftUtils;
import org.bukkit.entity.Player;

import java.util.Iterator;
import java.util.List;

/**
 * @author GuntherDW
 */

/*
 * This class primary use will be to check for older profiles in the DB and update if required.
 */
public class UUIDResolver {

    private TweakcraftUtils plugin;

    public UUIDResolver(TweakcraftUtils instance) {
        this.plugin = instance;
    }

    public void checkProfile(Player p) {

        List<PlayerOptions> popts = plugin.getDatabase().find(PlayerOptions.class).where().ieq("name", p.getName()).findList();
        if(popts.size() != 0) {
            PlayerOptions uuid = plugin.getDatabase().find(PlayerOptions.class).where().ieq("name", p.getName()).ieq("optionname", "uuid").findUnique();
            if(uuid == null) {
                // Check if he changed his name
                PlayerOptions oldName = plugin.getDatabase().find(PlayerOptions.class).where().ieq("optionname", "uuid").ieq("optionvalue", p.getUniqueId().toString()).findUnique();

                if(oldName != null) {
                    plugin.getLogger().warning(oldName.getName() + " changed his name to "+p.getName() +", updating PlayerOptions database!");

                    List<PlayerOptions> oldOpts = plugin.getDatabase().find(PlayerOptions.class).where().ieq("name", oldName.getName()).findList();

                    // Update DB!
                    String newName = p.getName();
                    // plugin.getDatabase().find(PlayerOptions.class).where().ieq("name",  oldName).findList()
                    for (Iterator<PlayerOptions> iterator = oldOpts.iterator(); iterator.hasNext(); ) {
                        PlayerOptions next = iterator.next();
                        next.setName(newName);
                    }
                    plugin.getDatabase().save(oldOpts);

                } else {
                    plugin.getLogger().warning(p.getName() + " had an old profile, updating PlayerOptions database!");
                    uuid = new PlayerOptions();
                    uuid.setName(p.getName());
                    uuid.setOptionname("uuid");
                    uuid.setOptionvalue(p.getUniqueId().toString());
                    plugin.getDatabase().save(uuid);
                }
            }
        }

    }
}
