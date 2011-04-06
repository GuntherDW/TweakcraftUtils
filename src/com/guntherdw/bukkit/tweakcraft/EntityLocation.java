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
        if(dloc.getWorld() == entityLocation.getWorld())
        {
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
        } else {
            return null; // Different world, different result!
        }
    }

}
