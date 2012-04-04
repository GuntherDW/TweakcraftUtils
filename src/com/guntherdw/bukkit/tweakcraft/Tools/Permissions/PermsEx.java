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
import org.bukkit.World;
import org.bukkit.entity.Player;
import ru.tehkode.permissions.PermissionGroup;
import ru.tehkode.permissions.PermissionManager;
import ru.tehkode.permissions.PermissionUser;

/**
 * @author GuntherDW
 */
public class PermsEx extends Permissions {

    private PermissionsResolver permsResolver;
    private final PermissionManager permsManager =
            ru.tehkode.permissions.bukkit.PermissionsEx.getPermissionManager();

    public PermsEx(PermissionsResolver instance) {
        this.permsResolver = instance;
    }

    @Override
    public String getUserPrefix(String player) {
        World world = permsResolver.plugin.getServer().getWorlds().get(0);
        return getUserPrefix(world.getName(), player);
    }

    @Override
    public String getUserPrefix(String world, String player) {
        PermissionUser puser = permsManager.getUser(player);
        return puser!=null?puser.getPrefix(world).replace("&", "§"):"§f";
    }

    @Override
    public String getPrimaryUserGroup(String world, String player) {
        PermissionUser puser = permsManager.getUser(player);
        if(puser!=null) {
            PermissionGroup[] groups = puser.getGroups();
            if(groups.length>0) return groups[0].getName();
        }
        return null;
    }

    @Override
    public String getUserSuffix(String world, String player) {
        PermissionUser puser = permsManager.getUser(player);
        return puser!=null?puser.getSuffix(world).replace("&", "§"):"§f";
    }

    @Override
    public boolean inGroup(String group, Player player) {
        return inGroup(player.getWorld().getName(), group, player);
    }

    @Override
    public boolean inGroup(String world, String group, Player player) {
        PermissionUser puser = permsManager.getUser(player);
        return puser != null && puser.inGroup(world, group);
    }

    @Override
    public boolean inSingleGroup(String world, String group, Player player) {
        PermissionUser puser = permsManager.getUser(player);
        return puser != null && puser.inGroup(group, world, false);
    }

    @Override
    public boolean hasPermission(String world, Player player, String permissionbit) {
        PermissionUser puser = permsManager.getUser(player);
        return puser != null && puser.has(permissionbit, world);
    }
}
