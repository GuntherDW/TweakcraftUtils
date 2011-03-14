package com.guntherdw.bukkit.tweakcraft;

import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;

/**
 * @author GuntherDW
 */
public class EntityLocation {

    private Location entityLocation;

    public EntityLocation(LivingEntity entity) {
        this.entityLocation = entity.getLocation();
    }

    public Integer getDistance(LivingEntity entity) {
        Location dloc;
        Double xdiff, zdiff;
        Integer xdiffi, zdiffi;
        dloc = entity.getLocation();
        xdiff = dloc.getX() - entityLocation.getX();
        xdiffi = xdiff.intValue();
        if (xdiffi < 0) {
            xdiffi = ~xdiffi + 1;
        }
        zdiff = dloc.getZ() - entityLocation.getZ();
        zdiffi = zdiff.intValue();
        if (zdiffi < 0) {
            zdiffi = ~zdiffi + 1;
        }

        return zdiffi + xdiffi;

    }

}
