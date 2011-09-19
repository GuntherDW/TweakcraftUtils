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

package com.guntherdw.bukkit.tweakcraft.Worlds;

import org.bukkit.util.config.Configuration;

/**
 * @author GuntherDW
 */
public interface IWorld {

    public abstract IWorld getWorld();

    public abstract org.bukkit.World getBukkitWorld();

    public abstract org.bukkit.World getNetherWorld();

    public abstract String getName();

    public abstract void loadWorld();

    public boolean isDurabilityEnabled();

    public void setDurabilityEnabled(boolean state);

    public void addNether();

    public boolean isNetherEnabled();

    public abstract boolean isEnabled();

    public abstract boolean isLoaded();

    public abstract boolean getAllowAnimals();

    public abstract String getWorldName();

    public abstract void setAllowAnimals(boolean allowanimals);

    public abstract boolean getAllowMonsters();
    
    public abstract int getViewDistance();

    public abstract void setViewDistance(int viewDistance);
    
    public abstract void setPVP(boolean pvpallowed);

    public abstract boolean getPVP();

    public abstract void setAllowMonsters(boolean allowmonsters);

    public abstract void setSpawnFlags(boolean allowMonsters, boolean allowAnimals);

    public abstract void setEnabled(boolean enabled);

    public abstract Configuration getConfiguration();
}
