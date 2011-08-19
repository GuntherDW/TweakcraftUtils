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

import com.guntherdw.bukkit.tweakcraft.TweakcraftUtils;
import org.bukkit.World.Environment;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author GuntherDW
 */
public class WorldManager {

    private Map<String, IWorld> worlds;
    private TweakcraftUtils plugin;
    private int defaultViewDistance;

    public Map<String, IWorld> getWorlds() {
        return worlds;
    }
    
    public int getDefaultViewDistance() {
        return plugin.getServer().getViewDistance();
    }

    public WorldManager(TweakcraftUtils instance) {
        this.plugin = instance;
        worlds = new HashMap<String, IWorld>();
    }

    public void setupWorlds() {
        boolean netherWorldOnline = false;

        for (org.bukkit.World world : plugin.getServer().getWorlds()) {
            worlds.put(world.getName(), new TweakWorld(this, world.getName(), world.getEnvironment(), world.getPVP(), world.getAllowMonsters(), world.getAllowMonsters(), true));
            if (world.getEnvironment() == Environment.NETHER)
                netherWorldOnline = true;
        }
        if (netherWorldOnline == false && plugin.getConfiguration().getBoolean("worlds.enablenether", false)) {
            String netherfolder = plugin.getConfiguration().getString("worlds.netherfolder", "nether");
            if (!netherfolder.equalsIgnoreCase("")) {
                plugin.getLogger().info("[TweakcraftUtils] Loading the netherworld!");
                worlds.put(netherfolder, new TweakWorld(this, netherfolder, Environment.NETHER, true));
            } else {
                plugin.getLogger().info("[TweakcraftUtils] The nether's folder name can't be empty!");
            }
        }
        // List<String> extraworlds = plugin.getConfiguration().getKeys("worlds.extraworlds");
        List<String> extraworlds = plugin.getConfiguration().getKeys("worlds.extraworlds");
        int defaultviewdistance = this.getDefaultViewDistance();
        for (String node : extraworlds) {
            if (!worlds.containsKey(node)) {

                String env = plugin.getConfiguration().getString("worlds.extraworlds." + node + ".environment", "");
                boolean enabled = plugin.getConfiguration().getBoolean("worlds.extraworlds." + node + ".enabled", false);
                boolean pvp = plugin.getConfiguration().getBoolean("worlds.extraworlds." + node + ".pvp", false);
                boolean monsters = plugin.getConfiguration().getBoolean("worlds.extraworlds." + node + ".monsters", true);
                boolean animals = plugin.getConfiguration().getBoolean("worlds.extraworlds." + node + ".animals", true);
                int viewdistance = plugin.getConfiguration().getInt("worlds.extraworlds." + node + ".viewdistance", defaultviewdistance);

                Environment wenv = null;
                if(env==null || env=="") {
                    plugin.getLogger().info("[TweakcraftUtils] World "+node+" does not have a valid environment definition, using \"normal\"");
                    wenv = Environment.NORMAL;
                } else if (env.equalsIgnoreCase("nether")) {
                    wenv = Environment.NETHER;
                } else if (env.equalsIgnoreCase("normal")) {
                    wenv = Environment.NORMAL;
                } else if (env.equalsIgnoreCase("skylands")) {
                    wenv = Environment.SKYLANDS;
                } else {
                    wenv = Environment.valueOf(env);
                }


                if (!(wenv == null)) {
                    plugin.getLogger().info("[TweakcraftUtils] Adding world with name " + node + " and environmenttype " + env + "!");
                    plugin.getLogger().info("[TweakcraftUtils] World "+node+" has pvp "+(pvp?"enabled":"disabled")+"!");
                    plugin.getLogger().info("[TweakcraftUtils] World "+node+" monsters : "+(monsters?"enabled":"disabled")+", animals : "+(animals?"enabled":"disabled")+"!");
                    worlds.put(node, new TweakWorld(this, node, wenv, pvp, monsters, animals, viewdistance, enabled));
                } else {
                    plugin.getLogger().info("[TweakcraftUtils] " + env + " isn't a correct environment name!");
                }
            } else {
                plugin.getLogger().info("[TweakcraftUtils] World with name " + node + " already exists!");
            }
        }

    }

    public IWorld getWorld(String name) {
        if (worlds.containsKey(name)) {
            return worlds.get(name);
        } else {
            return null;
        }
    }

    public TweakcraftUtils getPlugin() {
        return plugin;
    }
}
