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

import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Pig;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityCombustEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityListener;
import org.bukkit.event.entity.ExplosionPrimeEvent;
import org.bukkit.inventory.ItemStack;


/**
 * @author GuntherDW
 */
public class TweakcraftEntityListener extends EntityListener {

    private TweakcraftUtils plugin;

    public TweakcraftEntityListener(TweakcraftUtils instance) {
        this.plugin = instance;
    }

    public void onEntityCombust(EntityCombustEvent event) {
        Entity ent = event.getEntity();
        if(ent instanceof Player) {
            Player player = (Player) ent;
            if(plugin.getWorldGuard()!=null) {
                if(plugin.getConfigHandler().stopIgniteWorldGuard && plugin.getWorldGuard().getGlobalConfiguration().hasGodMode(player)) {
                    event.setCancelled(true);
                }
            }
        }
    }

    public void onEntityDeath(EntityDeathEvent event) {
        Entity ent = event.getEntity();
        if(ent instanceof Pig) {
            Pig pig = (Pig) ent;
            if(pig.hasSaddle() && plugin.getConfigHandler().pigRecoverSaddle) {
                event.getDrops().add(new ItemStack(Material.SADDLE, 1));
            }
        }
    }

    public void onExplosionPrime(ExplosionPrimeEvent event) {
        if(event.isCancelled())
            return;
        
        Entity ent = event.getEntity();
        if(ent != null)
        {
            if(!ent.isEmpty()) {
                if(ent.getPassenger() instanceof Player) {
                    event.setCancelled(true);
                }
            }
        }
    }

}
