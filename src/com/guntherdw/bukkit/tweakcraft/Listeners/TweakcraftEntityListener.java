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

package com.guntherdw.bukkit.tweakcraft.Listeners;

import com.guntherdw.bukkit.tweakcraft.Packages.LocalPlayer;
import com.guntherdw.bukkit.tweakcraft.TweakcraftUtils;
import de.diddiz.LogBlock.Consumer;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Pig;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.*;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;


/**
 * @author GuntherDW
 */
public class TweakcraftEntityListener extends EntityListener {

    private TweakcraftUtils plugin;

    public TweakcraftEntityListener(TweakcraftUtils instance) {
        this.plugin = instance;
    }

    public void onEntityTarget(EntityTargetEvent event) {
        if(!plugin.getConfigHandler().enableTargetIgnoreAFKPlayers
                || event.isCancelled() || !(event.getTarget() instanceof Player)) return;

        LocalPlayer lp = plugin.wrapPlayer((Player) event.getTarget());
        if(event.getReason().equals(EntityTargetEvent.TargetReason.CLOSEST_PLAYER)
                || event.getReason().equals(EntityTargetEvent.TargetReason.RANDOM_TARGET)) {
            if(lp.isAfk()) event.setCancelled(true);
        }
    }

    public void onProjectileHit(ProjectileHitEvent event) {
        if(!plugin.getConfigHandler().enableTNTArrows) return;
        if(event.getEntity()==null || !(event.getEntity() instanceof Arrow)) return;
        Arrow a = (Arrow) event.getEntity();
        if(a.getShooter() instanceof Player) {
            LocalPlayer lp = plugin.wrapPlayer((Player)a.getShooter());

            // Check for TNT in inventory!
            if(lp.isTntArrow() && plugin.check(lp.getBukkitPlayer(), "tntarrow")) {
                boolean hasTNT = false;
                Inventory inv = lp.getBukkitPlayer().getInventory();
                hasTNT = inv!=null && inv.contains(Material.TNT);
                if(!hasTNT) return;

                Location loc = a.getLocation().clone();
                float TNTPower = plugin.getConfigHandler().tntArrowForce;

                ExplosionPrimeEvent explosionPrimeEvent = new ExplosionPrimeEvent(a, TNTPower, false);
                plugin.getServer().getPluginManager().callEvent(explosionPrimeEvent);
                if(explosionPrimeEvent.isCancelled()) return;

                if(a.getWorld().createExplosion(loc, TNTPower)) {
                    lp.getBukkitPlayer().getInventory().removeItem(new ItemStack(Material.TNT, 1));
                    a.remove();
                    if(plugin.getConfigHandler().enableLogBlock) {
                        Consumer consumer = plugin.getLogBlock().getConsumer();
                        consumer.queueBlockBreak(lp.getName(), loc, Material.TNT.getId(), (byte) 0);
                    }
                }
            }
        }

    }

    public void onEntityCombust(EntityCombustEvent event) {
        if(!plugin.getConfigHandler().stopIgniteWorldGuard) return;
        Entity ent = event.getEntity();

        if(ent instanceof Player) {
            Player player = (Player) ent;
            if(plugin.getWorldGuard()!=null&&plugin.getWorldGuard().getGlobalConfiguration().hasGodMode(player)) {
                event.setCancelled(true);
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

        if(plugin.getConfigHandler().enableExperienceOrbsHalt) {
            event.setDroppedExp(0);
        }
    }

    public void onExplosionPrime(ExplosionPrimeEvent event) {
        if(event.isCancelled()) return;

        Entity ent = event.getEntity();
        if(ent != null && !ent.isEmpty() && ent.getPassenger() instanceof Player) {
            event.setCancelled(true);
        }
    }

}
