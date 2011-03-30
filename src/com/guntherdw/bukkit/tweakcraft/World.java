package com.guntherdw.bukkit.tweakcraft;

import org.bukkit.util.config.Configuration;

/**
 * @author GuntherDW
 */
public interface World {

    public abstract World getWorld();

    public abstract org.bukkit.World getBukkitWorld();

    public abstract String getName();

    public abstract boolean isEnabled();

    public abstract boolean isLoaded();

    public abstract Configuration getConfiguration();
}
