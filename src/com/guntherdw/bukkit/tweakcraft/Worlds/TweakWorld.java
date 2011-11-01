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

import com.guntherdw.bukkit.tweakcraft.Worlds.Generators.FlatGen;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.generator.ChunkGenerator;

/**
 * @author GuntherDW
 */
public class TweakWorld implements iWorld {

    private org.bukkit.World world;
    private org.bukkit.World nether;
    private String foldername = null;
    private boolean enabled = false;
    private boolean allowmonsters = true;
    private boolean allowanimals = true;
    private boolean tooldurability = true;
    private boolean netherenabled = false;
    private boolean keepspawnactive = false;
    private Long seed=null;
    private Long netherseed=null;
    private int    portalSearchWidth = 128;
    private String worldName = "";
    private String chunkGen = null;
    private org.bukkit.World.Environment environment;
    private WorldManager wm;
    private boolean pvp = true;
    private int viewdistance = 10;
    private GameMode gamemode = null;

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

    public Long getSeed(boolean netherseed) {
        if(netherseed && !netherenabled) return null;
        if(netherseed) return nether.getSeed();

        return world.getSeed();
    }

    public void setSeed(long seed, boolean netherseed) {
        if(netherseed&&!netherenabled) return;
        if(netherseed) this.netherseed = seed;
        else this.seed = seed;
    }

    public String getChunkGen() {
        return chunkGen;
    }

    public void setChunkGen(String chunkGen) {
        this.chunkGen = chunkGen;
    }

    @Override
    public GameMode getGameMode() {
        if(this.gamemode==null)
            return GameMode.SURVIVAL;

        return gamemode;
    }

    @Override
    public void setGameMode(GameMode mode) {
        this.gamemode = mode;
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
            WorldCreator worldCreator = new WorldCreator(worldName);
            if ((world = wm.getPlugin().getServer().getWorld(worldName)) == null) {
                // wm.getPlugin().getLogger().info("[TweakcraftUtils] Creating new world!");
                environment = env;

                if(chunkGen!=null) {
                    try {
                        Class clazz = Class.forName(chunkGen);
                        if(clazz!=null) {
                            Object c = clazz.newInstance();
                            if(c instanceof ChunkGenerator) {
                                ChunkGenerator cg = (ChunkGenerator) c;
                                if(c instanceof FlatGen) {
                                    FlatGen fg = (FlatGen) cg;
                                    int mh = wm.getPlugin().getConfiguration().getInt("worlds.extraworlds." + worldName + ".flatGen.mapHeight", 12);
                                    byte toplayer = (byte)wm.getPlugin().getConfiguration().getInt("worlds.extraworlds." + worldName + ".flatGen.toplayer", Material.GRASS.getId());
                                    byte normal = (byte)wm.getPlugin().getConfiguration().getInt("worlds.extraworlds." + worldName + ".flatGen.normal", Material.DIRT.getId());
                                    fg.setmapHeight(mh);
                                    fg.setNormal(normal);
                                    fg.setToplayer(toplayer);
                                    fg.assignWorldManager(wm);
                                    /* if(seed!=null)
                                        world = wm.getPlugin().getServer().createWorld(worldName, environment, seed, fg);
                                    else
                                        world = wm.getPlugin().getServer().createWorld(worldName, environment, fg); */
                                    // world = wm.getPlugin().getServer().createWorld()
                                    worldCreator.generator(fg);

                                } else {
                                    /* if(seed!=null)
                                         world = wm.getPlugin().getServer().createWorld(worldName, environment, seed, cg); */
                                    // else
                                    worldCreator.generator(cg);
                                }
                            } else {
                                this.wm.getPlugin().getLogger().info("[TweakcraftUtils] Error in world"+worldName+"! "+chunkGen+" isn't a Chunk Generator class!");
                            }
                        }
                    } catch (ClassNotFoundException e) {
                        this.wm.getPlugin().getLogger().info("[TweakcraftUtils] Error in world"+worldName+"! Can't find class with name "+chunkGen);
                        enabled=false;
                    } catch (InstantiationException e) {
                        enabled=false;
                        e.printStackTrace();
                    } catch (IllegalAccessException e) {
                        enabled=false;
                        e.printStackTrace();
                    }
                }

            } else {
                // wm.getPlugin().getLogger().info("[TweakcraftUtils] This world already existed!");
                environment = world.getEnvironment();
            }
            
            if(seed!=null)
                worldCreator.seed(seed.longValue());
            
            if(enabled) {
                worldCreator.environment(env);
                world = worldCreator.createWorld();
                world.setKeepSpawnInMemory(this.getSpawnChunksActive());
                world.setPVP(pvp);
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

    public iWorld getWorld() {
        return this;
    }

    public org.bukkit.World.Environment getEnvironment() {
        return environment;
    }
    
    public void setSpawnChunksActive(boolean state) {
        // this.world.setKeepSpawnInMemory(state);
        if(world!=null) this.world.setKeepSpawnInMemory(state);
        this.keepspawnactive = state;
    }

    public boolean getSpawnChunksActive() {
        return this.keepspawnactive;
    }

    public org.bukkit.World getBukkitWorld() {
        return world;
    }
    
    public int getPortalSearchWidth() {
        return this.portalSearchWidth;
    }

    public void setPortalSearchWidth(int searchWidth) {
        this.portalSearchWidth = searchWidth;
    }

    @Override
    public World getNetherWorld() {
        if(!isEnabled()) return null;
        if(!isNetherEnabled()) return null;
        return nether;
    }

    public boolean getAllowAnimals() {
        return world.getAllowAnimals();
    }

    public void addNether() {
        if(!isEnabled()) return;
        WorldCreator wc = new WorldCreator(this.world.getName()+"_nether");
        World nw = wm.getPlugin().getServer().getWorld(this.world.getName()+"_nether");
        if(nw == null)
        {
            /* if(netherseed != null) {
                nw = wm.getPlugin().getServer().createWorld(this.world.getName()+"_nether", World.Environment.NETHER, netherseed);
            } else {
                nw = wm.getPlugin().getServer().createWorld(this.world.getName()+"_nether", World.Environment.NETHER);
            } */
            if(netherseed!=null) wc.seed(netherseed);
            nw = wc.environment(World.Environment.NETHER).createWorld();
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

    public FileConfiguration getConfiguration() {
        /* File f = new File(wm.getPlugin().datafolder, "worlds/" + worldName + ".yml");
        if (!f.exists()) {
            try {
                f.mkdirs();
                f.createNewFile();
            } catch (IOException e) {
                wm.getPlugin().getLogger().info("[TweakcraftUtils] Can't create world's config file!");
                return null;
            }
        }
        FileConfiguration config = new FileConfiguration(f);

        return config; */
        return null;
    }
}
