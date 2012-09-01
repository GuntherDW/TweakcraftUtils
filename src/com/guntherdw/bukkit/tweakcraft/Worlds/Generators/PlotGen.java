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
public class PlotGen extends FlatGen {

    private WorldManager wm = null;
    private byte toplayer = (byte) Material.GRASS.getId();
    private byte normal = (byte) Material.DIRT.getId();
    private int height = 64;
    private boolean bedrockBottom = true;
    private int plotSize = 32;
    private int plotSize_parsed = plotSize + 7;

    @Override
    public boolean isBedrockBottom() {
        return bedrockBottom;
    }

    public void setPlotSize(int plotSize) {
        this.plotSize = plotSize;
        this.plotSize_parsed = plotSize + 7;
    }

    @Override
    public void setBedrockBottom(boolean bedrockBottom) {
        this.bedrockBottom = bedrockBottom;
    }

    @Override
    public void assignWorldManager(WorldManager instance) {
        this.wm = instance;
    }

    @Override
    public void setToplayer(byte md) {
        toplayer = md;
    }

    @Override
    public void setNormal(byte md) {
        normal = md;
    }

    @Override
    public void setmapHeight(int height) {
        this.height = height;
    }

    @Override
    public WorldManager getWm() {
        return wm;
    }

    @Override
    public int getHeight() {
        return height;
    }

    @Override
    public byte getNormal() {
        return normal;
    }

    @Override
    public byte getToplayer() {
        return toplayer;
    }

    @Deprecated
    @Override
    public byte[] generate(World world, Random random, int x, int z) {
        return null;
    }

    @Override
    public short[][] generateExtBlockSections(World world, Random random, int x, int z, BiomeGrid biomes) {
        return null;
    }

    boolean isBorder(int x, int z) {
        return ( x      % plotSize_parsed == 0) || ( z      % plotSize_parsed == 0)
            || ((x + 1) % plotSize_parsed == 0) || ((z + 1) % plotSize_parsed == 0)
            || ((x + 2) % plotSize_parsed == 0) || ((z + 2) % plotSize_parsed == 0)
            || ((x - 1) % plotSize_parsed == 0) || ((z - 1) % plotSize_parsed == 0)
            || ((x - 2) % plotSize_parsed == 0) || ((z - 2) % plotSize_parsed == 0);
    }

    boolean isDefiniteBorder(int x, int z) {
        return ((x + 3) % plotSize_parsed == 0) || ((z + 3) % plotSize_parsed == 0)
            || ((x - 3) % plotSize_parsed == 0) || ((z - 3) % plotSize_parsed == 0);
    }

    @Override
    public byte[][] generateBlockSections(World world, Random random, int cx, int cz, BiomeGrid biomes) {
        int maxHeight = world.getMaxHeight();
        byte[][] result = new byte[maxHeight / 16][];
        int realChunkX = cx << 4;
        int realChunkZ = cz << 4;

        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                for (int y = 0; y < maxHeight; y++) {
                    if (y == 0 && bedrockBottom)
                        setBlock(result, x, y, z, (byte) 7);
                    else if (y < height)
                        setBlock(result, x, y, z, normal);
                    else if (y == height)
                        setBlock(result, x, y, z, toplayer);
                    else
                        setBlock(result, x, y, z, (byte) 0);

                    if(isBorder(realChunkX + x, realChunkZ + z) && y == height) {
                        setBlock(result, x, y, z, (byte) Material.WOOD.getId());
                    } else if(isDefiniteBorder(realChunkX + x, realChunkZ + z) && y == height) {
                        setBlock(result, x, y, z, (byte) Material.DOUBLE_STEP.getId());
                    } /* else if(isDefiniteBorder(realChunkZ + x, realChunkZ + z) && y == height + 1) {
                        if(!isBorder(realChunkX + x, realChunkZ + z))
                            setBlock(result, x, y, z, (byte) Material.STEP.getId());
                    } */
                }
            }
        }

        // return null; // Default - returns null, which drives call to generate()
        return result;
    }

    @Override
    public void setBlock(byte[][] result, int x, int y, int z, byte blkid) {
        if (result[y >> 4] == null) {
            result[y >> 4] = new byte[4096];
        }
        result[y >> 4][((y & 0xF) << 8) | (z << 4) | x] = blkid;
    }

    @Override
    public byte getBlock(byte[][] result, int x, int y, int z) {
        if (result[y >> 4] == null) {
            return (byte) 0;
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

    public int getPlotSize() {
        return plotSize;
    }
}
