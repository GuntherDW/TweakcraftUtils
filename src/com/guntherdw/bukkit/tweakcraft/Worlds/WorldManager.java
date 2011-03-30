package com.guntherdw.bukkit.tweakcraft.Worlds;

import com.guntherdw.bukkit.tweakcraft.TweakcraftUtils;
import org.bukkit.World.Environment;
import org.bukkit.World;

import java.util.List;
import java.util.Map;

/**
 * @author GuntherDW
 */
public class WorldManager {

    private World nether;
    private World normal;
    private TweakcraftUtils plugin;
    private Map<String, World> extraworlds;

    public WorldManager(TweakcraftUtils instance)
    {
        this.plugin = instance;
    }

    public void setupWorlds() {
        List<World> worlds = plugin.getServer().getWorlds();
        for(World w : worlds)
        {
            if(w.getEnvironment() == org.bukkit.World.Environment.NORMAL && normal == null)
                normal = w;
            if(w.getEnvironment() == org.bukkit.World.Environment.NETHER && nether == null)
                nether = w;
        }
        if(normal == null)
            normal = plugin.getServer().createWorld("world", org.bukkit.World.Environment.NORMAL);
        if(nether == null)
            nether = plugin.getServer().createWorld("nether", org.bukkit.World.Environment.NETHER);
    }

    public com.guntherdw.bukkit.tweakcraft.World getWorld(String name)
    {
        return null;
    }

    public void createWorld(String worldname, Environment env)
    {
        if(!extraworlds.containsKey(worldname.toLowerCase()))
        {
            plugin.getServer().createWorld(worldname.toLowerCase(), env);
        }
    }

}
