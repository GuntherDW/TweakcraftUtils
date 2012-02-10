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

package com.guntherdw.bukkit.tweakcraft.Tools;

import com.guntherdw.bukkit.tweakcraft.Tools.Permissions.*;
import com.guntherdw.bukkit.tweakcraft.TweakcraftUtils;
import org.bukkit.plugin.Plugin;

/**
 * @author GuntherDW
 */
public class PermissionsResolver {

    /* public static enum PermissionResolvingMode { BUKKIT, NIJIPERMS, PERMISSIONSEX }; */
    /* No longer needed! */

    public TweakcraftUtils plugin = null;
    private Permissions resolver = null;
    // private PermissionResolvingMode resolvmode = null;


    public PermissionsResolver(TweakcraftUtils instance) {
        this.plugin = instance;
    }

    public void init() {
        boolean foundPlugin = false;

        foundPlugin = tryPermissionsEx();
        if (!foundPlugin) foundPlugin = tryNijiPerms();
        if (!foundPlugin) tryWEPIF();
        if (!foundPlugin) {
            plugin.getLogger().info("[TweakcraftUtils] DinnerPerms found, using that for permissions resolving!");
            resolver = new BukkitPerms(this);
            plugin.getLogger().warning("[TweakcraftUtils] Use this only as a fallback please, install and/or manage a real permissions plugin!");
        }
    }

    public Permissions getResolver() {
        if (resolver == null) init();
        return resolver;
    }

    private boolean tryPermissionsEx() {
        Plugin p = plugin.getServer().getPluginManager().getPlugin("PermissionsEx");
        if (p != null) {
            resolver = new PermsEx(this);
            plugin.getLogger().info("[TweakcraftUtils] PermissionsEx found, using that for permissions resolving!");
            return true;
        }
        return false;
    }

    private boolean tryNijiPerms() {
        Plugin p = plugin.getServer().getPluginManager().getPlugin("Permissions");
        if (p != null) {
            resolver = new NijiPerms(this, ((com.nijikokun.bukkit.Permissions.Permissions) p).getHandler());
            plugin.getLogger().info("[TweakcraftUtils] NijiPermissions found, using that for permissions resolving!");
            return true;
        }
        return false;
    }

    private boolean tryWEPIF() {
        if (plugin.getWorldEdit() != null) {
            resolver = new WEPIFPerms(this, plugin.getWorldEdit().getPermissionsResolver());
            plugin.getLogger().info("[TweakcraftUtils] WorldEdit found, using that for permissions resolving!");
            plugin.getLogger().warning("[TweakcraftUtils] Use this only as a fallback please, install and/or manage a real permissions plugin.!");
            return true;
        } else {
            return false;
        }
    }

}
