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

package com.guntherdw.bukkit.tweakcraft.Tools.Permissions;

import com.guntherdw.bukkit.tweakcraft.Tools.PermissionsResolver;
import com.sk89q.bukkit.migration.PermissionsResolverManager;
import org.bukkit.entity.Player;

/**
 * @author GuntherDW
 */
public class WEPIFPerms extends Permissions {

    private PermissionsResolverManager manager = null;
    private PermissionsResolver permsResolver = null;

    public WEPIFPerms(PermissionsResolver instance, PermissionsResolverManager manager) {
        this.manager = manager;
        this.permsResolver = instance;
    }

    @Override
    public String getUserPrefix(String player) {
        return "§f";
    }

    @Override
    public String getUserPrefix(String world, String player) {
        return "§f";
    }

    @Override
    public String getPrimaryUserGroup(String world, String player) {
        String[] groups = manager.getGroups(player);
        return groups.length>0?groups[0]:null;
    }

    @Override
    public String getUserSuffix(String world, String player) {
        return "§f";
    }

    @Override
    public boolean inGroup(String group, Player player) {
        return manager.inGroup(player.getName(), group);
    }

    @Override
    public boolean inGroup(String world, String group, Player player) {
        return manager.inGroup(player.getName(), group);
    }

    @Override
    public boolean inSingleGroup(String world, String group, Player player) {
        return manager.inGroup(player.getName(), group);
    }

    @Override
    public boolean hasPermission(String world, Player player, String permissionbit) {
        return manager.hasPermission(world, player.getName(), permissionbit);
    }
}
