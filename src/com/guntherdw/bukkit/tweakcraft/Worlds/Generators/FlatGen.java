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

package com.guntherdw.bukkit.tweakcraft.Worlds.Generators;

import com.guntherdw.bukkit.tweakcraft.Worlds.WorldManager;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.generator.ChunkGenerator;

import java.util.Random;

/**
 * @author GuntherDW
 */
public class FlatGen extends ChunkGenerator {

    private WorldManager wm = null;
    private byte toplayer = (byte) Material.GRASS.getId();
    private byte normal = (byte) Material.DIRT.getId();
    private int height = 64;
    private boolean bedrockBottom = true;
    private World world = null;

    public boolean isBedrockBottom() {
        return bedrockBottom;
    }

    public void setBedrockBottom(boolean bedrockBottom) {
        this.bedrockBottom = bedrockBottom;
    }

    public void assignWorldManager(WorldManager instance) {
        this.wm = instance;
    }

    public void setToplayer(byte md) {
        toplayer = md;
    }

    public void setNormal(byte md) {
        normal = md;
    }

    public void setmapHeight(int height) {
        this.height = height;
    }

    public WorldManager getWm() {
        return wm;
    }

    public int getHeight() {
        return height;
    }

    public byte getNormal() {
        return normal;
    }

    public byte getToplayer() {
        return toplayer;
    }

    @Override
    @Deprecated
    public byte[] generate(World world, Random random, int x, int z) {
        return null;
    }

    @Override
    public short[][] generateExtBlockSections(World world, Random random, int x, int z, BiomeGrid biomes) {
        return null;
    }

    @Override
    public byte[][] generateBlockSections(World world, Random random, int cx, int cz, BiomeGrid biomes) {
        if(this.world == null) this.world = world;

        int maxHeight = world.getMaxHeight();
        byte[][] result = new byte[maxHeight / 16][];

        for(int x = 0; x < 16; x++) {
            for(int z = 0; z < 16; z++) {
                for(int y = 0; y < maxHeight; y++) {
                    if (y == 0 && bedrockBottom)
                        setBlock(result, x, y, z, (byte) 7);
                    else if (y < height)
                        setBlock(result, x, y, z, normal);
                    else if (y == height)
                        setBlock(result, x, y, z, toplayer);
                    else
                        setBlock(result, x, y, z, (byte) 0);
                }
            }
        }

        // return null; // Default - returns null, which drives call to generate()
        return result;
    }

    public void setBlock(byte[][] result, int x, int y, int z, byte blkid) {
        if (result[y >> 4] == null) {
            result[y >> 4] = new byte[4096];
        }
        result[y >> 4][((y & 0xF) << 8) | (z << 4) | x] = blkid;
    }

    public byte getBlock(byte[][] result, int x, int y, int z) {
        if (result[y >> 4] == null) {
            return (byte)0;
        }
        return result[y >> 4][((y & 0xF) << 8) | (z << 4) | x];
    }

    @Override
    public Location getFixedSpawnLocation(World world, Random random) {
        int x = random.nextInt(200) - 100;
        int z = random.nextInt(200) - 100;
        int y = world.getHighestBlockYAt(x, z);
        return new Location(world, x, y, z);
    }
}
