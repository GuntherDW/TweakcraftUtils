package com.guntherdw.bukkit.tweakcraft.Worlds;

import com.guntherdw.bukkit.tweakcraft.World;
import org.bukkit.util.config.Configuration;

import java.io.File;
import java.io.IOException;

/**
 * @author GuntherDW
 */
public class TweakWorld implements com.guntherdw.bukkit.tweakcraft.World {

    private org.bukkit.World world;
    private boolean enabled = false;
    private String worldName = "";
    private org.bukkit.World.Environment environment;
    private WorldManager wm;

    public TweakWorld(WorldManager wm, String foldername, org.bukkit.World.Environment env, boolean enabled)
    {
        if(enabled)
        {
            if(foldername.trim().isEmpty())
            {
                this.wm = wm;
                this.enabled = false;
                world = null;
                worldName = null;
            } else {
                worldName = foldername.trim();
                if((world = wm.getPlugin().getServer().getWorld(worldName)) == null)
                {
                    // wm.getPlugin().getLogger().info("[TweakcraftUtils] Creating new world!");
                    environment = env;
                    world = wm.getPlugin().getServer().createWorld(worldName, environment);
                } else {
                    // wm.getPlugin().getLogger().info("[TweakcraftUtils] This world already existed!");
                    environment = world.getEnvironment();
                }
                this.enabled = true;
            }
        }
    }

    public World getWorld() {
        return this;
    }

    public org.bukkit.World.Environment getEnvironment()
    {
        return environment;
    }

    public org.bukkit.World getBukkitWorld() {
        return world;
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

    public Configuration getConfiguration() {
        File f = new File(wm.getPlugin().datafolder, "worlds/"+worldName+".yml");
        if(!f.exists())
        {
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
