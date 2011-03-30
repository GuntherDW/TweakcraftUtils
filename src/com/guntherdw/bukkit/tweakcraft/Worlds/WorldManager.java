package com.guntherdw.bukkit.tweakcraft.Worlds;

import com.guntherdw.bukkit.tweakcraft.TweakcraftUtils;
import com.guntherdw.bukkit.tweakcraft.World;
import org.bukkit.World.Environment;

import java.util.HashMap;
import java.util.Map;

/**
 * @author GuntherDW
 */
public class WorldManager {

    private Map<String, com.guntherdw.bukkit.tweakcraft.World> worlds;
    private TweakcraftUtils plugin;


    public Map<String, World> getWorlds()
    {
        return worlds;
    }

    public WorldManager(TweakcraftUtils instance)
    {
        this.plugin = instance;
        worlds = new HashMap<String, World>();
    }

    public void setupWorlds() {
        /* List<World> worlds = plugin.getServer().getWorlds();
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
            nether = plugin.getServer().createWorld("nether", org.bukkit.World.Environment.NETHER); */

        /* Get the normal world-folder */
        String worldName = "";
        // plugin.get
        /* Get server config, without touching craftbukkit's sources, and get config.level-name */


    }

    public com.guntherdw.bukkit.tweakcraft.World getWorld(String name)
    {
        if(worlds.containsKey(name))
        {
            return worlds.get(name);
        } else {
            return null;
        }
    }

    public TweakcraftUtils getPlugin()
    {
        return plugin;
    }
}
