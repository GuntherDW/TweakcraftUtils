package com.guntherdw.bukkit.tweakcraft.Worlds;

import com.guntherdw.bukkit.tweakcraft.World;
import org.bukkit.util.config.Configuration;

/**
 * @author GuntherDW
 */
public class TweakWorld implements com.guntherdw.bukkit.tweakcraft.World {

    private org.bukkit.World world;
    private boolean enabled = false;
    private String worldName = "";
    private org.bukkit.World.Environment environment;

    public TweakWorld(WorldManager wm, String foldername, org.bukkit.World.Environment env, boolean enabled)
    {
        if(enabled)
        {
            if(foldername.trim().isEmpty())
            {
                this.enabled =false;
                world = null;
                worldName = null;
            } else {
                worldName = foldername.trim();
                if((world = wm.getPlugin().getServer().getWorld(worldName)) == null)
                {
                    environment = env;
                    world = wm.getPlugin().getServer().createWorld(worldName, environment);
                } else {
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
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
