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

import com.guntherdw.bukkit.tweakcraft.Packages.LocalPlayer;
import com.guntherdw.bukkit.tweakcraft.Tools.PermissionsResolver;
import com.zones.Zones;
import com.zones.util.ZoneUtil;
import org.bukkit.entity.Player;

import java.util.List;

/**
 * @author GuntherDW
 */
public class ZonesPerms extends Permissions {

    private Zones zones;
    private ZoneUtil zonesAPI;
    private PermissionsResolver resolver;
    private com.zones.permissions.Permissions permsResolver;

    public ZonesPerms(PermissionsResolver permissionsResolver, Zones zonesInstance) {
        this.zones = zonesInstance;
        this.resolver = permissionsResolver;
        this.zonesAPI = zonesInstance.getApi();
        this.permsResolver = zonesInstance.getPermissions();
    }

    @Override
    public String getUserPrefix(String player) {
        LocalPlayer lp = resolver.plugin.wrapPlayer(player);
        Player p = lp.getBukkitPlayer();

        return p != null ? permsResolver.getPrefix(p) : "§f";
    }

    @Override
    public String getUserPrefix(String world, String player) {
        LocalPlayer lp = resolver.plugin.wrapPlayer(player);
        Player p = lp.getBukkitPlayer();

        return p != null ? permsResolver.getPrefix(p, world) : "§f";
    }

    @Override
    public String getUserSuffix(String world, String player) {
        LocalPlayer lp = resolver.plugin.wrapPlayer(player);
        Player p = lp.getBukkitPlayer();

        return p != null ? permsResolver.getSuffix(p, world) : "§f";
    }

    @Override
    public String getPrimaryUserGroup(String world, String player) {
        List<String> groups = permsResolver.getGroups(world, player);
        return groups.size() > 0 ? groups.get(0) : null;
    }

    @Override
    public boolean inGroup(String group, Player player) {
        return permsResolver.inGroup(player, group);
    }

    @Override
    public boolean inGroup(String world, String group, Player player) {
        return permsResolver.inGroup(player, world, group);
    }

    /**
     * No longer used
     */
    @Override
    public boolean inSingleGroup(String world, String group, Player player) {
        return false;
    }

    @Override
    public boolean hasPermission(String world, Player player, String permissionbit) {
        return permsResolver.canUse(player, world, permissionbit);
    }
}
