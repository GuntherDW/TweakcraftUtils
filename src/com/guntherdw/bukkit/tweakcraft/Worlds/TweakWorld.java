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

import org.bukkit.World;
import org.bukkit.util.config.Configuration;

import java.io.File;
import java.io.IOException;

/**
 * @author GuntherDW
 */
public class TweakWorld implements IWorld {

    private org.bukkit.World world;
    private org.bukkit.World nether;
    private String foldername = null;
    private boolean enabled = false;
    private boolean allowmonsters = true;
    private boolean allowanimals = true;
    private boolean tooldurability = true;
    private boolean netherenabled = false;
    private boolean keepspawnactive = false;
    private String worldName = "";
    private org.bukkit.World.Environment environment;
    private WorldManager wm;
    private boolean pvp = true;
    private int viewdistance = 10;

    public TweakWorld(WorldManager wm, String foldername, org.bukkit.World.Environment env, boolean enabled) {
        this.wm = wm;
        this.foldername = foldername;
        this.environment = env;
        this.allowanimals = true;
        this.allowmonsters = true;
        this.tooldurability = true;
        this.enabled = enabled;
        if (enabled) {
            loadWorld(wm, foldername, env, enabled);
        }
    }

    public TweakWorld(WorldManager wm, String foldername, org.bukkit.World.Environment env, boolean pvp, boolean allowMonsters, boolean allowAnimals, boolean enabled) {
        this.wm = wm;
        this.foldername = foldername;
        this.environment = env;
        this.pvp = pvp;
        this.allowanimals = allowAnimals;
        this.allowmonsters = allowMonsters;
        this.tooldurability = true;
        this.enabled = enabled;
        if (enabled) {
            loadWorld(wm, foldername, env, enabled);
        }
    }

    public TweakWorld(WorldManager wm, String foldername, org.bukkit.World.Environment env, boolean pvp, boolean allowMonsters, boolean allowAnimals, int viewdistance, boolean enabled) {
            this.wm = wm;
            this.foldername = foldername;
            this.environment = env;
            this.pvp = pvp;
            this.allowanimals = allowAnimals;
            this.allowmonsters = allowMonsters;
            this.enabled = enabled;
            this.tooldurability = true;
            if(viewdistance>3 && viewdistance<16)
                this.viewdistance = viewdistance;
            else
                this.viewdistance = wm.getDefaultViewDistance();
            if (enabled) {
                loadWorld(wm, foldername, env, enabled);
            }
        }

    public TweakWorld(WorldManager wm, String foldername, org.bukkit.World.Environment env, boolean pvp, boolean allowMonsters, boolean allowAnimals, int viewdistance, boolean tooldurability, boolean enabled) {
        this.wm = wm;
        this.foldername = foldername;
        this.environment = env;
        this.pvp = pvp;
        this.allowanimals = allowAnimals;
        this.allowmonsters = allowMonsters;
        this.enabled = enabled;
        this.tooldurability = tooldurability;
        if(viewdistance>3 && viewdistance<16)
            this.viewdistance = viewdistance;
        else
            this.viewdistance = wm.getDefaultViewDistance();
        if (enabled) {
            loadWorld(wm, foldername, env, enabled);
        }
    }

    public TweakWorld(WorldManager wm, String foldername, org.bukkit.World.Environment env, boolean allowMonsters, boolean allowAnimals, boolean enabled) {
        this.wm = wm;
        this.foldername = foldername;
        this.environment = env;
        this.allowanimals = allowAnimals;
        this.allowmonsters = allowMonsters;
        this.tooldurability = true;
        this.enabled = enabled;
        if (enabled) {
            loadWorld(wm, foldername, env, enabled);
        }
    }
    
    public void setDurabilityEnabled(boolean state) {
        if(wm.getPlugin().getConfigHandler().enablemod_InfDura)
            world.setToolDurability(state);
        else {
            wm.getPlugin().getLogger().severe("[TweakcraftUtils] Tried to enable/disable tool durability for world "+world.getName()+",");
            wm.getPlugin().getLogger().severe("[TweakcraftUtils] But either your Bukkit is not modded, or you forgot to enable it in the config!");
        }
        
    }

    public boolean isDurabilityEnabled() {
        return world.getToolDurability();
    }

    public boolean isNetherEnabled() {
        return this.netherenabled;
    }

    public void loadWorld(WorldManager wm, String foldername, org.bukkit.World.Environment env, boolean enabled) {
        if (foldername.trim().isEmpty()) {

            this.enabled = false;
            world = null;
            worldName = null;
        } else {
            worldName = foldername.trim();
            if ((world = wm.getPlugin().getServer().getWorld(worldName)) == null) {
                // wm.getPlugin().getLogger().info("[TweakcraftUtils] Creating new world!");
                environment = env;
                world = wm.getPlugin().getServer().createWorld(worldName, environment);
            } else {
                // wm.getPlugin().getLogger().info("[TweakcraftUtils] This world already existed!");
                environment = world.getEnvironment();
            }
            // TODO: Re-enable after it's been added again!
            // world.setViewDistance(this.viewdistance);
            this.setSpawnFlags(this.allowmonsters, this.allowmonsters);
            this.setDurabilityEnabled(tooldurability);
            this.enabled = true;
        }
    }

    public void loadWorld() {
        if(world != null) return;
        loadWorld(wm, foldername, environment, enabled);
    }

    public IWorld getWorld() {
        return this;
    }

    public org.bukkit.World.Environment getEnvironment() {
        return environment;
    }
    
    public void setSpawnChunksActive(boolean state) {
        this.world.setKeepSpawnInMemory(state);
        this.keepspawnactive = state;
    }

    public boolean getSpawnChunksActive() {
        return this.keepspawnactive;
    }

    public org.bukkit.World getBukkitWorld() {
        return world;
    }

    @Override
    public World getNetherWorld() {
        if(!isNetherEnabled()) return null;
        return nether;
    }

    public boolean getAllowAnimals() {
        return world.getAllowAnimals();
    }

    public void addNether() {
        World nw = wm.getPlugin().getServer().getWorld(this.world.getName()+"_nether");
        if(nw == null)
        {
            nw = wm.getPlugin().getServer().createWorld(this.world.getName()+"_nether", World.Environment.NETHER);
        }
        nw.setSpawnFlags(this.allowmonsters, this.allowanimals);
        nw.setToolDurability(this.tooldurability);
        this.netherenabled = true;
        this.nether = nw;
    }

    public String getWorldName() {
        return worldName;
    }

    public void setAllowAnimals(boolean allowanimals) {
        this.world.setSpawnFlags(getAllowMonsters(), allowanimals);
    }

    public boolean getAllowMonsters() {
        return world.getAllowMonsters();
    }

    public void setAllowMonsters(boolean allowmonsters) {
        this.setSpawnFlags(allowmonsters, getAllowAnimals());
    }

    public void setSpawnFlags(boolean allowMonsters, boolean allowAnimals) {
        this.allowanimals = allowAnimals;
        this.allowmonsters = allowMonsters;
        this.world.setSpawnFlags(allowMonsters, allowAnimals);
    }

    public String getName() {
        return worldName;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public boolean isLoaded() {
        return world != null;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public void setPVP(boolean pvpallowed) {
        this.pvp = pvpallowed;
        this.world.setPVP(pvpallowed);
    }

    public boolean getPVP() {
        return this.pvp;
    }

    public int getViewDistance() {
        return viewdistance;
    }

    public void setViewDistance(int viewdistance) {
        this.viewdistance = viewdistance;
    }

    public Configuration getConfiguration() {
        File f = new File(wm.getPlugin().datafolder, "worlds/" + worldName + ".yml");
        if (!f.exists()) {
            try {
                f.mkdirs();
                f.createNewFile();
            } catch (IOException e) {
                wm.getPlugin().getLogger().info("[TweakcraftUtils] Can't create world's config file!");
                return null;
            }
        }
        Configuration config = new Configuration(f);

        return config;
    }
}
