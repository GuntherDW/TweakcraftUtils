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
import com.guntherdw.bukkit.tweakcraft.Worlds.Generators.FlatGen;
import com.guntherdw.bukkit.tweakcraft.Worlds.Generators.PlotGen;
import org.bukkit.Difficulty;
import org.bukkit.GameMode;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.WorldType;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.generator.ChunkGenerator;

import java.io.*;
import java.util.*;

/**
 * @author GuntherDW
 */
public class WorldManager {

    private Map<String, iWorld> worlds;
    private TweakcraftUtils plugin;
    private int defaultViewDistance;
    private YamlConfiguration globalConfig = null;

    public Map<String, iWorld> getWorlds() {
        return worlds;
    }

    public World getDefaultWorld() {
        return plugin.getServer().getWorlds().get(0);
    }

    public Set<World> getDefaultWorlds() {
        boolean isNetherEnabled = plugin.getServer().getAllowNether();
        boolean isTheEndEnabled = plugin.getServer().getAllowEnd();

        int max = 0;
        if (isNetherEnabled) max++;
        if (isTheEndEnabled) max++;

        Set<World> worlds = new HashSet<World>();
        for (int i = 0; i <= max; i++)
            worlds.add(plugin.getServer().getWorlds().get(i));

        return worlds;
    }

    public int getDefaultViewDistance() {
        return plugin.getServer().getViewDistance();
    }

    public ChunkGenerator getDefaultWorldGenerator(String worldName, String id) {
        // return null;
        /* if(worlds.containsKey(worldName)) {
            iWorld iw = worlds.get(worldName); */
        // I split on _'s, so it's flatgen_height_bottomlayer_proplayer_bedrocklayer
        String[] split = id.split("_");
        if (split[0].equalsIgnoreCase("flatgen")) {
            FlatGen fg = new FlatGen();
            if (split.length > 1) fg.setmapHeight(Integer.parseInt(split[1]));
            if (split.length > 2) fg.setNormal(Byte.parseByte(split[2]));
            if (split.length > 3) fg.setToplayer(Byte.parseByte(split[3]));
            if (split.length > 4) fg.setBedrockBottom(Boolean.parseBoolean(split[4]));
            plugin.getLogger().info("Utilising FlatGen for world " + worldName);
            return fg;
        } else if (split[0].equalsIgnoreCase("plotgen")) {
            PlotGen fg = new PlotGen();
            if (split.length > 1) fg.setmapHeight(Integer.parseInt(split[1]));
            if (split.length > 2) fg.setNormal(Byte.parseByte(split[2]));
            if (split.length > 3) fg.setToplayer(Byte.parseByte(split[3]));
            if (split.length > 4) fg.setBedrockBottom(Boolean.parseBoolean(split[4]));
            if (split.length > 5) fg.setPlotSize(Integer.parseInt(split[5]));
            plugin.getLogger().info("Utilising PlotGen for world " + worldName);
            plugin.getLogger().info("PlotGen PlotSize : " + fg.getPlotSize());
            // System.out.println("[");
            return fg;
        }
        // }
        /**
         * If all else fails, just provide a standard FlatGen
         */
        return new FlatGen();
    }

    public void loadMotd(String worldname) {
        if (!plugin.getConfigHandler().enableWorldMOTD || !worlds.containsKey(worldname)) return;

        try {
            File motd = new File(plugin.getDataFolder() + plugin.getConfigHandler().getDirSeperator() + "motd-" + worldname + ".txt");
            if (!motd.exists()) return;
            BufferedReader fr = new BufferedReader(new FileReader(motd));
            String line = null;
            List<String> lines = new ArrayList<String>();
            while ((line = fr.readLine()) != null) {
                lines.add(line.replace('&', '§'));
            }

            iWorld w = worlds.get(worldname);
            w.setMOTD(lines.toArray(new String[lines.size()]));
            plugin.getLogger().info("Loaded MOTD for world " + worldname + "!");
        } catch (FileNotFoundException e) {
            plugin.getLogger().warning("Couldn't find MOTD for world " + worldname + "!");
        } catch (IOException e) {
            plugin.getLogger().warning("Error while reading MOTD for world " + worldname + "!");
        }
    }

    public WorldManager(TweakcraftUtils instance) {
        this.plugin = instance;
        worlds = new HashMap<String, iWorld>();
    }

    public void setupWorlds() {

        if (globalConfig == null) globalConfig = plugin.getConfigHandler().getGlobalconfig();

        boolean netherWorldOnline = false;
        ConfigurationSection section = globalConfig.getConfigurationSection("worlds.extraworlds");
        if (section == null)
            return;

        Set<String> extraworlds = section.getKeys(false);

        Boolean worldInfDura = globalConfig.getBoolean("worlds.durability", true);

        if (plugin.getConfigHandler().enablemod_InfDura) {
            for (World w : getDefaultWorlds())
                w.setToolDurability(worldInfDura);

        }

        int defaultviewdistance = this.getDefaultViewDistance();

        for (org.bukkit.World world : plugin.getServer().getWorlds()) {
            // catch a /reload!
            // IWorld iw = new TweakWorld(this, world.getName(), world.getEnvironment(), world.getPVP(), world.getAllowMonsters(), world.getAllowMonsters(), defaultviewdistance, worldInfDura, true);
            if (extraworlds.contains(world.getName())) {
                TweakWorld tw = new TweakWorld(this, world.getName(), world.getEnvironment(), world.getPVP(), world.getAllowMonsters(), world.getAllowMonsters(), defaultviewdistance, worldInfDura, true);
                tw.setSpawnChunksActive(world.getKeepSpawnInMemory());
                worlds.put(world.getName(), tw);
            }
            if (world.getEnvironment() == Environment.NETHER)
                netherWorldOnline = true;
        }

        if (!netherWorldOnline && globalConfig.getBoolean("worlds.enablenether", false)) {
            String netherfolder = globalConfig.getString("worlds.netherfolder", "nether");
            if (!netherfolder.equalsIgnoreCase("")) {
                plugin.getLogger().info("Loading the netherworld!");
                worlds.put(netherfolder, new TweakWorld(this, netherfolder, Environment.NETHER, true));
            } else {
                plugin.getLogger().info("The nether's folder name can't be empty!");
            }
        }

        for (String node : extraworlds) {
            if (!worlds.containsKey(node)) {

                String env = globalConfig.getString("worlds.extraworlds." + node + ".environment", "");
                boolean enabled = globalConfig.getBoolean("worlds.extraworlds." + node + ".enabled", false);
                boolean pvp = globalConfig.getBoolean("worlds.extraworlds." + node + ".pvp", false);
                boolean monsters = globalConfig.getBoolean("worlds.extraworlds." + node + ".monsters", true);
                boolean animals = globalConfig.getBoolean("worlds.extraworlds." + node + ".animals", true);
                boolean durability = globalConfig.getBoolean("worlds.extraworlds." + node + ".durability", true);
                boolean addnether = globalConfig.getBoolean("worlds.extraworlds." + node + ".addnether", false);
                boolean addtheend = globalConfig.getBoolean("worlds.extraworlds." + node + ".addtheend", false);
                boolean spawnchunksactive = globalConfig.getBoolean("worlds.extraworlds." + node + ".spawnchunksactive", false);
                boolean allowFlight = globalConfig.getBoolean("worlds.extraworlds." + node + ".allowFlight", false);
                String chunkGenClass = globalConfig.getString("worlds.extraworlds." + node + ".chunkGeneratorClass", null);
                String chunkGen = globalConfig.getString("worlds.extraworlds." + node + ".chunkGenerator", null);
                String worldType = globalConfig.getString("worlds.extraworlds." + node + ".worldType", null);
                int viewdistance = globalConfig.getInt("worlds.extraworlds." + node + ".viewdistance", defaultviewdistance);
                int portalSearchRadius = globalConfig.getInt("worlds.extraworlds." + node + ".portalSearchRadius", 128);
                int difficulty = globalConfig.getInt("worlds.extraworlds." + node + ".difficulty", Difficulty.NORMAL.getValue());
                long seed = globalConfig.getInt("worlds.extraworlds." + node + ".seed", -1);
                long nseed = globalConfig.getInt("worlds.extraworlds." + node + ".netherseed", -1);

                String gameMode = globalConfig.getString("worlds.extraworlds." + node + ".gamemode", null);
                GameMode gm = gameMode != null ? GameMode.valueOf(gameMode.toUpperCase()) : null;

                /* if(gameMode==null || gameMode.equals("")) {
                    gm=GameMode.SURVIVAL;
                } */

                Environment wenv = null;
                if (env == null || env.equals("")) {
                    plugin.getLogger().info("World " + node + " does not have a valid environment definition, using \"normal\"");
                    wenv = Environment.NORMAL;
                } else if (env.equalsIgnoreCase("nether")) {
                    wenv = Environment.NETHER;
                } else if (env.equalsIgnoreCase("normal")) {
                    wenv = Environment.NORMAL;
                } else if (env.equalsIgnoreCase("end")) {
                    wenv = Environment.THE_END;
                } else {
                    wenv = Environment.valueOf(env.toUpperCase());
                }


                if (wenv == null) {
                    plugin.getLogger().info(env + " isn't a correct environment name!");
                } else {
                    plugin.getLogger().info("Adding world with name " + node + " and environmenttype " + env + "!");
                    plugin.getLogger().info("World " + node + " has pvp " + (pvp ? "enabled" : "disabled") + "!");
                    plugin.getLogger().info("World " + node + " monsters : " + (monsters ? "enabled" : "disabled") + ", animals : " + (animals ? "enabled" : "disabled") + "!");
                    if (addnether) plugin.getLogger().info("World " + node + " added nether world!");
                    if (addtheend) plugin.getLogger().info("World " + node + " added the_end world!");
                    plugin.getLogger().info("World " + node + " Tool Durability : " + (durability ? "enabled" : "disabled"));
                    plugin.getLogger().info("World " + node + " GameMode : " + (gm != null ? gm.toString().toLowerCase() : "Survival"));
                    plugin.getLogger().info("World " + node + " Difficulity : " + Difficulty.getByValue(difficulty).name().toLowerCase());
                    TweakWorld tw = new TweakWorld(this, node, wenv, pvp, monsters, animals, viewdistance, durability, false);
                    if (difficulty != getDefaultWorld().getDifficulty().getValue())
                        tw.setDifficulty(difficulty);
                    if (gm != null) tw.setGameMode(gm);
                    if (chunkGenClass != null) {
                        plugin.getLogger().info("World " + node + " is using a custom chunkGenClass using the deprecated method!");
                        plugin.getLogger().info("Consider using the newer method!");
                        tw.setChunkGenClass(chunkGenClass);
                    } else if (chunkGen != null) {
                        plugin.getLogger().info("World " + node + " is using a custom chunkGen!");
                        tw.setChunkGen(chunkGen);
                    }

                    if (worldType != null) {
                        WorldType type = WorldType.valueOf(worldType.toUpperCase());
                        if (type != null) {
                            plugin.getLogger().info("World " + node + " has worldType "+type.getName().toLowerCase()+"!");
                            tw.setWorldType(type);
                        }
                    }

                    tw.setEnabled(enabled);
                    if (enabled) {
                        tw.loadWorld();
                        if (seed != -1) tw.setSeed(seed, false);
                        tw.setSpawnChunksActive(spawnchunksactive);
                        tw.setAllowFlight(allowFlight);
                        if (addnether) {
                            if (nseed != -1) tw.setNetherseed(seed);
                            tw.addNether();
                            tw.setPortalSearchWidth(portalSearchRadius);
                        }
                        if(addtheend) {
                            if (nseed != -1) tw.setEndseed(seed);
                            tw.addTheEnd();
                        }
                    }

                    worlds.put(node, tw);
                    this.loadMotd(tw.getName());
                }
            } else {
                plugin.getLogger().info("World with name " + node + " already exists!");
            }
        }

    }

    public iWorld getWorld(String name) {
        return this.getWorld(name, false);
    }

    public iWorld getWorld(String name, boolean filterNether) {
        if (filterNether) {
            boolean isNether = name.endsWith("_nether");
            boolean isTheEnd = name.endsWith("_the_end");
            if (isNether) name = name.substring(0, name.length() - 7); // MINUS _nether
            if (isTheEnd) name = name.substring(0, name.length() - 8); // MINUS _the_end
        }
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
