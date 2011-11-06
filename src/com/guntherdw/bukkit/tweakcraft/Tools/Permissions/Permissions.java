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

import org.bukkit.entity.Player;

/**
 * @author GuntherDW
 */
public abstract class Permissions {

    public abstract String getUserPrefix(String player);

    public String getUserPrefix(String world, Player player)
        {return getUserPrefix(world, player.getName());}

    public abstract String getUserPrefix(String world, String player);

    public abstract String getPrimaryUserGroup(String world, String player);

    public String getUserSuffix(String world, Player player)
        {return getUserSuffix(world, player.getName());}

    public abstract String getUserSuffix(String world, String player);

    public abstract boolean inGroup(String group, Player player);

    public abstract boolean inGroup(String world, String group, Player player);

    public abstract boolean inSingleGroup(String world, String group, Player player);

    public abstract boolean hasPermission(String world, Player player, String permissionbit);


}
