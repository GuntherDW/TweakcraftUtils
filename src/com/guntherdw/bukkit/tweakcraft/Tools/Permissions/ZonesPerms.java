/*
 * Copyright (c) 2012 GuntherDW
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

package com.guntherdw.bukkit.tweakcraft.Tools.Permissions;

import com.guntherdw.bukkit.tweakcraft.Tools.PermissionsResolver;
import com.zones.Zones;
import org.bukkit.entity.Player;

/**
 * @author GuntherDW
 */
public class ZonesPerms extends Permissions {

    private Zones zones;
    private PermissionsResolver resolver;

    public ZonesPerms(PermissionsResolver permissionsResolver, Zones zonesInstance) {
        this.zones = zonesInstance;
        this.resolver = permissionsResolver;
    }

    @Override
    public String getUserPrefix(String player) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public String getUserPrefix(String world, String player) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public String getPrimaryUserGroup(String world, String player) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public String getUserSuffix(String world, String player) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean inGroup(String group, Player player) {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean inGroup(String world, String group, Player player) {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean inSingleGroup(String world, String group, Player player) {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean hasPermission(String world, Player player, String permissionbit) {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
