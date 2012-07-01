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
import org.bukkit.*;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.plugin.Plugin;

/**
 * @author GuntherDW
 */
public class TweakWorld implements iWorld {

    private org.bukkit.World world;
    private org.bukkit.World nether;
    private org.bukkit.World the_end;

    private String foldername = null;
    private boolean enabled = false;
    private boolean allowmonsters = true;
    private boolean allowanimals = true;
    private boolean tooldurability = true;
    private boolean nether_enabled = false;
    private boolean the_end_enabled = false;
    private boolean keepspawnactive = false;
    private int difficulty = Difficulty.NORMAL.getValue();
    private Long seed = null;
    private Long netherseed = null;
    private Long endseed = null;
    private int portalSearchWidth = 128;
    private String worldName = "";
    private String chunkGenClass = null;
    private ChunkGenerator chunkgen = null;
    private org.bukkit.World.Environment environment;
    private org.bukkit.WorldType worldType = null;
    private WorldManager wm;
    private boolean pvp = true;
    private int viewdistance = 10;
    private GameMode gamemode = null;
    private boolean allowFlight = false;
    private String[] motd;

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
        if (viewdistance > 3 && viewdistance < 16)
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
        if (viewdistance > 3 && viewdistance < 16)
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
        if (wm.getPlugin().getConfigHandler().enablemod_InfDura)
            world.setToolDurability(state);
        else {
            wm.getPlugin().getLogger().severe("[TweakcraftUtils] Tried to enable/disable tool durability for world " + world.getName() + ",");
            wm.getPlugin().getLogger().severe("[TweakcraftUtils] But either your Bukkit is not modded, or you forgot to enable it in the config!");
        }

    }

    public Long getSeed(boolean netherseed) {
        if (netherseed && !nether_enabled) return null;
        if (netherseed) return nether.getSeed();

        return world.getSeed();
    }

    public void setSeed(long seed, boolean netherseed) {
        if (netherseed && !nether_enabled) return;
        if (netherseed) this.netherseed = seed;
        else this.seed = seed;
    }

    public Long getNetherseed() {
        return netherseed;
    }

    public void setNetherseed(Long netherseed) {
        this.netherseed = netherseed;
    }

    public Long getEndseed() {
        return endseed;
    }

    public void setEndseed(Long endseed) {
        this.endseed = endseed;
    }

    public ChunkGenerator getChunkGen() {
        return chunkgen;
    }

    public void setWorldType(WorldType type) {
        this.worldType = type;
    }

    public WorldType getWorldType() {
        return this.worldType;
    }

    public void setChunkGen(String chunkGen) {
        resolvePluginChunkGenerator(chunkGen);
    }

    public String getChunkGenClass() {
        return chunkGenClass;
    }

    public void setChunkGenClass(String chunkGenClass) {
        this.chunkGenClass = chunkGenClass;
    }

    public void resolvePluginChunkGenerator(String chunkGen) {
        String[] split = chunkGen.split(":");
        Plugin plugin = wm.getPlugin().getServer().getPluginManager().getPlugin(split[0]);
        if (plugin == null) {
            wm.getPlugin().getLogger().severe("[TweakcraftUtils] Chunkgenerator error for " + world.getName() + ",");
            wm.getPlugin().getLogger().severe("[TweakcraftUtils] Couldn't find plugin with name " + split[0] + "!");
            this.enabled = false;
            return;
        }

        chunkgen = plugin.getDefaultWorldGenerator(this.worldName, split.length > 1 ? split[1] : null);
    }

    public GameMode getGameMode() {
        if (this.gamemode == null)
            return GameMode.SURVIVAL;

        return gamemode;
    }

    public void setGameMode(GameMode mode) {
        this.gamemode = mode;
    }

    public boolean isDurabilityEnabled() {
        return world.getToolDurability();
    }

    public boolean isNetherEnabled() {
        return this.nether_enabled;
    }

    public boolean isTheEndEnabled() {
        return this.the_end_enabled;
    }

    public void loadWorld(WorldManager wm, String foldername, org.bukkit.World.Environment env, boolean enabled) {
        if (foldername.trim().equals("")) {

            this.enabled = false;
            world = null;
            worldName = null;
        } else {
            worldName = foldername.trim();
            WorldCreator worldCreator = new WorldCreator(worldName);
            if ((world = wm.getPlugin().getServer().getWorld(worldName)) == null) {
                // wm.getPlugin().getLogger().info("[TweakcraftUtils] Creating new world!");
                environment = env;
                if (chunkgen != null)
                    worldCreator.generator(chunkgen);

                else if (chunkGenClass != null) {
                    try {
                        Class clazz = Class.forName(chunkGenClass);
                        if (clazz != null) {
                            Object c = clazz.newInstance();
                            if (c instanceof ChunkGenerator) {
                                ChunkGenerator cg = (ChunkGenerator) c;
                                if (c instanceof FlatGen) {
                                    FlatGen fg = (FlatGen) cg;
                                    int mh = wm.getPlugin().getConfigHandler().getGlobalconfig().getInt("worlds.extraworlds." + worldName + ".flatGen.mapHeight", 12);
                                    byte toplayer = (byte) wm.getPlugin().getConfigHandler().getGlobalconfig().getInt("worlds.extraworlds." + worldName + ".flatGen.toplayer", Material.GRASS.getId());
                                    byte normal = (byte) wm.getPlugin().getConfigHandler().getGlobalconfig().getInt("worlds.extraworlds." + worldName + ".flatGen.normal", Material.DIRT.getId());
                                    fg.setmapHeight(mh);
                                    fg.setNormal(normal);
                                    fg.setToplayer(toplayer);
                                    fg.assignWorldManager(wm);
                                    worldCreator.generator(fg);
                                } else {
                                    worldCreator.generator(cg);
                                }
                            } else {
                                this.wm.getPlugin().getLogger().info("[TweakcraftUtils] Error in world " + worldName + "! " + chunkGenClass + " isn't a Chunk Generator class!");
                            }
                        }
                    } catch (ClassNotFoundException e) {
                        this.wm.getPlugin().getLogger().info("[TweakcraftUtils] Error in world " + worldName + "! Can't find class with name " + chunkGenClass);
                        enabled = false;
                    } catch (InstantiationException e) {
                        enabled = false;
                        e.printStackTrace();
                    } catch (IllegalAccessException e) {
                        enabled = false;
                        e.printStackTrace();
                    }
                }

            } else {
                // wm.getPlugin().getLogger().info("[TweakcraftUtils] This world already existed!");
                environment = world.getEnvironment();
            }

            if (seed != null)
                worldCreator.seed(seed);

            WorldType type = getWorldType();
            if(type!=null)
                worldCreator.type(type);

            if (enabled) {
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
        if (world != null) return;
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
        if (world != null) this.world.setKeepSpawnInMemory(state);
        this.keepspawnactive = state;
    }

    public boolean isAllowFlight() {
        return allowFlight;
    }

    public void setAllowFlight(boolean allowFlight) {
        this.allowFlight = allowFlight;
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

    public World getNetherWorld() {
        if (!isEnabled()) return null;
        if (!isNetherEnabled()) return null;
        return nether;
    }

    public boolean getAllowAnimals() {
        return world.getAllowAnimals();
    }

    public void addNether() {
        if (!isEnabled()) return;
        WorldCreator wc = new WorldCreator(this.world.getName() + "_nether");
        World nw = wm.getPlugin().getServer().getWorld(this.world.getName() + "_nether");
        if (nw == null) {
            if (netherseed != null) wc.seed(netherseed);
            nw = wc.environment(World.Environment.NETHER).createWorld();
        }
        nw.setSpawnFlags(this.allowmonsters, this.allowanimals);
        nw.setToolDurability(this.tooldurability);

        this.nether_enabled = true;
        this.nether = nw;
    }

    public void addTheEnd() {
        if(!isEnabled()) return;
        WorldCreator wc = new WorldCreator(this.world.getName() + "_the_end");
        World endw = wm.getPlugin().getServer().getWorld(this.world.getName() + "_the_end");
        if(endw == null) {
            if(endseed != null) wc.seed(endseed);
            endw = wc.environment(World.Environment.THE_END).createWorld();
        }
        endw.setSpawnFlags(this.allowmonsters, this.allowanimals);
        endw.setToolDurability(this.tooldurability);

        this.the_end_enabled = true;
        this.the_end = endw;
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

    public void setMOTD(String[] lines) {
        this.motd = lines;
    }

    public String[] getMOTD() {
        return this.motd;
    }

    public boolean hasWorldMOTD() {
        return this.motd != null && this.motd.length > 0;
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

    public YamlConfiguration getConfiguration() {
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

    public int getDifficulty() {
        return difficulty;
    }

    public Difficulty getDifficultyBukkit() {
        return Difficulty.getByValue(difficulty);
    }

    public void setDifficulty(int difficulty) {
        this.difficulty = difficulty;
        if (world != null && isEnabled())
            world.setDifficulty(getDifficultyBukkit());
        if (nether != null && isEnabled())
            nether.setDifficulty(getDifficultyBukkit());
        if (the_end != null && isEnabled())
            the_end.setDifficulty(getDifficultyBukkit());
    }

    public void setDifficulty(Difficulty difficulty) {
        this.difficulty = difficulty.getValue();
    }
}
