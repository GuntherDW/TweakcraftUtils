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

import org.bukkit.Difficulty;
import org.bukkit.GameMode;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.generator.ChunkGenerator;

/**
 * @author GuntherDW
 */
public interface iWorld {
    public iWorld getWorld();
    public org.bukkit.World getBukkitWorld();
    public org.bukkit.World getNetherWorld();
    public int getPortalSearchWidth();
    public void setPortalSearchWidth(int searchWidth);
    public String getName();
    public void loadWorld();
    public ChunkGenerator getChunkGen();
    public void setChunkGen(String chunkGen);
    public String getChunkGenClass();
    public void setChunkGenClass(String chunkGenClass);
    public GameMode getGameMode();
    public void setGameMode(GameMode mode);
    public Long getSeed(boolean netherseed);
    public void setSeed(long seed, boolean netherseed);
    public boolean isDurabilityEnabled();
    public boolean isAllowFlight();
    public void setAllowFlight(boolean state);
    public void setDurabilityEnabled(boolean state);
    public void addNether();
    public boolean isNetherEnabled();
    public boolean isEnabled();
    public boolean isLoaded();
    public boolean getAllowAnimals();
    public String getWorldName();
    public void setAllowAnimals(boolean allowanimals);
    public boolean getAllowMonsters();
    public int getViewDistance();
    public void setViewDistance(int viewDistance);
    public void setPVP(boolean pvpallowed);
    public boolean getPVP();
    public void setAllowMonsters(boolean allowmonsters);
    public void setSpawnFlags(boolean allowMonsters, boolean allowAnimals);
    public void setEnabled(boolean enabled);
    public void setMOTD(String[] lines);
    public String[] getMOTD();
    public boolean hasWorldMOTD();
    public FileConfiguration getConfiguration();
    public int getDifficulty();
    public Difficulty getDifficultyBukkit();
    public void setDifficulty(int difficulty);
    public void setDifficulty(Difficulty difficulty);

}
