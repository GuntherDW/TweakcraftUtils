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
import com.nijiko.permissions.PermissionHandler;
import com.nijiko.permissions.User;
import org.bukkit.World;
import org.bukkit.entity.Player;

/**
 * @author GuntherDW
 */
public class NijiPerms extends Permissions {

    private PermissionHandler permshandler = null;
    private PermissionsResolver permsResolver;

    public NijiPerms(PermissionsResolver instance, PermissionHandler handler) {
        this.permshandler = handler;
        this.permsResolver = instance;
    }

    @Override
    public String getUserPrefix(String player) {
        World world = permsResolver.plugin.getServer().getWorlds().get(0);
        return getUserPrefix(world.getName(), player);
    }

    @Override
    public String getUserPrefix(String world, String player) {
        User user = permshandler.getUserObject(world, player);
        if(user!=null) return user.getPrefix().replace("&", "§");
        return "§f";
    }

    @Override
    public String getPrimaryUserGroup(String world, String player) {
        User user = permshandler.getUserObject(world, player);
        if(user!=null) {
            return permshandler.getPrimaryGroupObject(world, user.getName()).getName();
        }
        return null;
    }

    @Override
    public String getUserSuffix(String world, String player) {
        User user = permshandler.getUserObject(world, player);
        if(user!=null) return user.getPrefix().replace("&", "§");
        return "§f";
    }

    @Override
    public boolean inGroup(String group, Player player) {
        /* return permshandler.inGroup(player.getWorld().getName(), player.getName(), group); */
        return inGroup(player.getWorld().getName(), group, player);
    }

    @Override
    public boolean inGroup(String world, String group, Player player) {
        return permshandler.inGroup(world, player.getName(), group);
    }

    @Override
    public boolean inSingleGroup(String world, String group, Player player) {
        return permshandler.inSingleGroup(player.getWorld().getName(), player.getName(), group);
    }

    @Override
    public boolean hasPermission(String world, Player player, String permissionbit) {
        return permshandler.has(world, player.getName(), permissionbit);
    }
}
