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
import org.bukkit.GameMode;
import org.bukkit.World.Environment;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.util.config.Configuration;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author GuntherDW
 */
public class WorldManager {

    private Map<String, iWorld> worlds;
    private TweakcraftUtils plugin;
    private int defaultViewDistance;
    private Configuration globalConfig = null;

    public Map<String, iWorld> getWorlds() {
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
        if(split[0].equalsIgnoreCase("flatgen")) {
            FlatGen fg = new FlatGen();
            if(split.length>1) fg.setmapHeight(Integer.parseInt(split[1]));
            if(split.length>2) fg.setNormal(Byte.parseByte(split[2]));
            if(split.length>3) fg.setToplayer(Byte.parseByte(split[3]));
            if(split.length>4) fg.setBedrockBottom(Boolean.parseBoolean(split[4]));
            plugin.getLogger().info("[TweakcraftUtils] Utilising FlatGen for world "+worldName);
            return fg;
        }
        // }
        /**
         * If all else fails, just provide a standard FlatGen
         */
        return new FlatGen();
    }
    
    public void loadMotd(String worldname) {
        if(!plugin.getConfigHandler().enableWorldMOTD || !worlds.containsKey(worldname)) return;
        
        try{
            File motd = new File(plugin.getDataFolder()+plugin.getConfigHandler().getDirSeperator()+"motd-"+worldname+".txt");
            if(!motd.exists()) return;
            BufferedReader fr = new BufferedReader(new FileReader(motd));
            String line = null;
            List<String> lines = new ArrayList<String>();
            while((line = fr.readLine()) != null) {
                lines.add(line.replace('&', '§'));
            }

            iWorld w = worlds.get(worldname);
            w.setMOTD(lines.toArray(new String[0]));
            plugin.getLogger().info("[TweakcraftUtils] Loaded MOTD for world "+worldname+"!");
        } catch (FileNotFoundException e) {
            plugin.getLogger().warning("[TweakcraftUtils] Couldn't find MOTD for world "+worldname+"!");
        } catch (IOException e) {
            plugin.getLogger().warning("[TweakcraftUtils] Error while reading MOTD for world "+worldname+"!");
        }
    }

    public WorldManager(TweakcraftUtils instance) {
        this.plugin = instance;
        worlds = new HashMap<String, iWorld>();
    }

    public void setupWorlds() {

        if(globalConfig==null) globalConfig = plugin.getConfigHandler().getGlobalconfig();

        boolean netherWorldOnline = false;
        List<String> extraworlds = globalConfig.getKeys("worlds.extraworlds");
        
        Boolean worldInfDura = globalConfig.getBoolean("worlds.durability", true);
        int defaultviewdistance = this.getDefaultViewDistance();
        
        for (org.bukkit.World world : plugin.getServer().getWorlds()) {
            // catch a /reload!
            // IWorld iw = new TweakWorld(this, world.getName(), world.getEnvironment(), world.getPVP(), world.getAllowMonsters(), world.getAllowMonsters(), defaultviewdistance, worldInfDura, true);
            if(!extraworlds.contains(world.getName())) {
                TweakWorld tw = new TweakWorld(this, world.getName(), world.getEnvironment(), world.getPVP(), world.getAllowMonsters(), world.getAllowMonsters(), defaultviewdistance, worldInfDura, true);
                tw.setSpawnChunksActive(world.getKeepSpawnInMemory());
                worlds.put(world.getName(), tw);
            }
            if (world.getEnvironment() == Environment.NETHER)
                netherWorldOnline = true;
        }
        
        if (netherWorldOnline == false && globalConfig.getBoolean("worlds.enablenether", false)) {
            String netherfolder = globalConfig.getString("worlds.netherfolder", "nether");
            if (!netherfolder.equalsIgnoreCase("")) {
                plugin.getLogger().info("[TweakcraftUtils] Loading the netherworld!");
                worlds.put(netherfolder, new TweakWorld(this, netherfolder, Environment.NETHER, true));
            } else {
                plugin.getLogger().info("[TweakcraftUtils] The nether's folder name can't be empty!");
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
                boolean spawnchunksactive = globalConfig.getBoolean("worlds.extraworlds." + node + ".spawnchunksactive", false);
                String chunkGen = globalConfig.getString("worlds.extraworlds." + node + ".chunkGenerator", null);
                int viewdistance = globalConfig.getInt("worlds.extraworlds." + node + ".viewdistance", defaultviewdistance);
                int portalSearchRadius = globalConfig.getInt("worlds.extraworlds." + node + ".portalSearchRadius", 128);
                long seed = globalConfig.getInt("worlds.extraworlds." + node + ".seed", -1);
                long nseed = globalConfig.getInt("worlds.extraworlds." + node + ".netherseed", -1);
                
                String gameMode = globalConfig.getString("worlds.extraworlds." + node + ".gamemode", null);
                GameMode gm = gameMode!=null?GameMode.valueOf(gameMode.toUpperCase()):null;

                /* if(gameMode==null || gameMode.equals("")) {
                    gm=GameMode.SURVIVAL;
                } */

                Environment wenv = null;
                if(env==null || env=="") {
                    plugin.getLogger().info("[TweakcraftUtils] World "+node+" does not have a valid environment definition, using \"normal\"");
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


                if (!(wenv == null)) {
                    plugin.getLogger().info("[TweakcraftUtils] Adding world with name " + node + " and environmenttype " + env + "!");
                    plugin.getLogger().info("[TweakcraftUtils] World "+node+" has pvp "+(pvp?"enabled":"disabled")+"!");
                    plugin.getLogger().info("[TweakcraftUtils] World "+node+" monsters : "+(monsters?"enabled":"disabled")+", animals : "+(animals?"enabled":"disabled")+"!");
                    if(addnether) plugin.getLogger().info("[TweakcraftUtils] World "+node+" added nether world!");
                    plugin.getLogger().info("[TweakcraftUtils] World "+node+" Tool Durability : "+(durability?"enabled":"disabled"));
                    plugin.getLogger().info("[TweakcraftUtils] World "+node+" GameMode : "+(gm!=null?gm.toString().toLowerCase():"Survival"));
                    TweakWorld tw = new TweakWorld(this, node, wenv, pvp, monsters, animals, viewdistance, durability, false);
                    if(gm!=null) tw.setGameMode(gm);
                    if(chunkGen!=null) {
                        plugin.getLogger().info("[TweakcraftUtils] World "+node+" is using a custom chunkGen!");
                        tw.setChunkGen(chunkGen);
                    }
                    tw.setEnabled(enabled);
                    if(enabled) {

                        tw.loadWorld();
                        if(seed != -1) tw.setSeed(seed, false);
                        if(addnether) {
                            if(nseed != -1) tw.setSeed(seed, true);
                            tw.addNether();
                            tw.setSpawnChunksActive(spawnchunksactive);
                            tw.setPortalSearchWidth(portalSearchRadius);
                        }
                    }

                    worlds.put(node, tw);
                    this.loadMotd(tw.getName());
                } else {
                    plugin.getLogger().info("[TweakcraftUtils] " + env + " isn't a correct environment name!");
                }
            } else {
                plugin.getLogger().info("[TweakcraftUtils] World with name " + node + " already exists!");
            }
        }

    }

    public iWorld getWorld(String name) {
        return this.getWorld(name, false);
    }

    public iWorld getWorld(String name, boolean filterNether) {
        if(filterNether) {
            boolean isnether = name.endsWith("_nether");
            if(isnether) name = name.substring(0, name.length()-7); // MINUS _nether
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
