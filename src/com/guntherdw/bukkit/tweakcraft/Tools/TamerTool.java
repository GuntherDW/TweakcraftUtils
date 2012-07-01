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

package com.guntherdw.bukkit.tweakcraft.Tools;

import com.guntherdw.bukkit.tweakcraft.Packages.TamerMode;
import com.guntherdw.bukkit.tweakcraft.TweakcraftUtils;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.*;

import java.util.HashMap;
import java.util.Map;

/**
 * @author GuntherDW
 */
public class TamerTool {

    private TweakcraftUtils plugin;
    private Map<Player, TamerMode> tamers = new HashMap<Player, TamerMode>();

    public TamerTool(TweakcraftUtils instance) {
        this.plugin = instance;
    }

    public Map<Player, TamerMode> getTamers() {
        return tamers;
    }

    public void getOwner(Player player, Tameable tameable) {
        String className = tameable.getClass().getCanonicalName().split("Craft")[1];
        if(className.equals("Ocelot")) className = "Ozelot";
        EntityType entityType = EntityType.fromName(className);

        String cname = entityType.getName().toLowerCase();

        player.sendMessage(ChatColor.AQUA + "Tameable type : "+cname);

        if(tameable.isTamed()) {
            AnimalTamer tamer = tameable.getOwner();
            if(tamer instanceof Player || tamer instanceof OfflinePlayer) {
                // Player ptamer = (Player) tamer;
                boolean online = false;
                Player ptamer=null; OfflinePlayer offlinetamer=null;
                if(tamer instanceof Player) { ptamer = (Player) tamer; online = true; }
                else                        { offlinetamer = (OfflinePlayer) tamer; online = false; }

                boolean allowed = true;
                if(online && !ptamer.equals(player)) {
                    if(!plugin.check(player, "tamer.info.other"))
                        allowed = false;
                }
                if(allowed) {
                    player.sendMessage(ChatColor.AQUA + "Tameable owner : "+(online?ptamer.getDisplayName():offlinetamer.getName()));
                } else {
                    player.sendMessage(ChatColor.RED + "You do not have the permission to check the ownership of other wolves!");
                }
            } else {
                player.sendMessage(ChatColor.AQUA + "This tameable is owned by an entity/offline player!");
            }
        } else {
            player.sendMessage(ChatColor.AQUA + "This tameable isn't tamed!");
        }
        player.sendMessage(ChatColor.AQUA + "Tameable health : "+((LivingEntity)tameable).getHealth());
        if(tameable instanceof Ageable) {
            player.sendMessage(ChatColor.AQUA + "Tameable age : "+((Ageable)tameable).getAge() + (((Ageable)tameable).getAgeLock() ? ChatColor.RED+" L":""));
        }
        if(tameable instanceof Ocelot) {
            player.sendMessage(ChatColor.AQUA + "Tameable cat-type : "+((Ocelot)tameable).getCatType().toString().toLowerCase());
        }
        if(tameable instanceof Wolf) {
            Wolf wolf = (Wolf) tameable;
            if(wolf.isAngry()) {
                player.sendMessage(ChatColor.AQUA + "This tameable is "+ChatColor.RED+"angry"+ChatColor.AQUA+"!");
                LivingEntity le = wolf.getTarget();
                String target = "";
                if(le instanceof OfflinePlayer)
                    if(((OfflinePlayer) le).isOnline())
                        target = ((Player)le).getDisplayName();
                    else
                        target = ((OfflinePlayer) le).getName();
                else {
                    String extraInfo = "";
                    if ( le != null ) {
                        EntityType et = le.getType();
                        extraInfo = et.getName();
                    }
                    target = "Animal "+extraInfo;// +le!=null?" ("+(et.getName())+")":"";
                }
                player.sendMessage(ChatColor.AQUA + "Target : "+target);
            }
        }
    }

    public void setTame(Tameable tameable, Boolean tame, Player player) {
        if(plugin.check(player, "tamer.tame")) {
            Boolean allowed = true;
            if(tame==null) {
                tame = !tameable.isTamed();
            }
            if(tameable.isTamed() && !tame) {
                AnimalTamer tamer = tameable.getOwner();

                if(tamer instanceof Player) {
                    if(!((Player)tamer).equals(player)) {
                        if(!plugin.check(player, "tamer.tame.untame.other"))
                            allowed = false;
                    } else {
                        if(!plugin.check(player, "tamer.tame.untame.own"))
                            allowed = false;
                    }
                }
                if(allowed) {
                    tameable.setOwner(null);
                    tameable.setTamed(false);
                } else {
                    player.sendMessage(ChatColor.RED + "You do not have permission to untame this tameables!");
                }
            } else if(!tameable.isTamed() && tame) {
                if(!plugin.check(player, "tamer.tame.tame"))
                    allowed = false;

                if(allowed) {
                    tameable.setTamed(true);
                    if(tameable instanceof Wolf) {
                        ((Wolf)tameable).setAngry(false);
                    }
                    ((Creature)tameable).setTarget(null);

                    tameable.setOwner(player);
                } else {
                    player.sendMessage(ChatColor.RED + "You do not have permission to tame this tameables!");
                }
            }
        } else {
            player.sendMessage(ChatColor.RED + "You do not have permission to tame tameables!");
        }
    }

    public void setAngry(Tameable tameable, Boolean angry, Player player) {
        if(plugin.check(player, "tamer.angry")) {
            if(tameable instanceof Wolf) {
                Wolf wolf = (Wolf) tameable;
                Boolean allowed = true;
                if(tameable.isTamed()) {
                    if(tameable.getOwner().equals(player)) {
                        if(!plugin.check(player, "tamer.angry.own"))
                            allowed = false;
                    } else {
                        if(!plugin.check(player, "tamer.angry.other"))
                            allowed = false;
                    }

                }
                if(angry==null) {
                    angry = !wolf.isAngry();
                }
                if(allowed) {
                    wolf.setAngry(angry);

                    if(!angry) {
                        wolf.setTarget(null);
                    }
                } else {
                    player.sendMessage(ChatColor.RED + "You do not have permission to anger this tameables!");
                }
            } else {
                player.sendMessage(ChatColor.RED + "You can't anger anything else than wolves!");
            }
        } else {
            player.sendMessage(ChatColor.RED + "You do not have permission to anger tameables!");
        }
    }

    public void sit(Tameable tameable, Boolean sit, Player player) {
        if(plugin.check(player, "tamer.sit")) {
            Boolean allowed = true;

            Boolean sitting = tameable instanceof Ocelot ? ((Ocelot)tameable).isSitting() :
                tameable instanceof Wolf && ((Wolf) tameable).isSitting();


            if(sitting) {
                if(tameable.getOwner()!=null && tameable.getOwner().equals(player)) {
                    if(!plugin.check(player, "tamer.sit.own"))
                        allowed = false;
                } else {
                    if(!plugin.check(player, "tamer.sit.other"))
                        allowed = false;
                }
            } else {
                if(!plugin.check(player, "tamer.sit.wild"))
                    allowed = false;
            }
            if(sit==null)
                sit = !sitting;

            if(allowed) {
                if(tameable instanceof Wolf)
                    ((Wolf)tameable).setSitting(sit);
                if(tameable instanceof Ocelot)
                    ((Ocelot)tameable).setSitting(sit);
            }


            else
                player.sendMessage(ChatColor.RED + "You do not have permission to command this tameables!");
        } else {
            player.sendMessage(ChatColor.RED + "You do not have permission to command tameables!");
        }
    }

    public void heal(Tameable tameable, Boolean kill, Player player) {
        if(plugin.check(player, "tamer.heal")) {
            Boolean allowed = true;
            if(tameable.isTamed()) {
                if(tameable.getOwner()!=null && tameable.getOwner().equals(player)) {
                    if(!plugin.check(player, "tamer.heal.own"))
                        allowed = false;
                } else {
                    if(!plugin.check(player, "tamer.heal.other"))
                        allowed = false;
                }
            } else {
                if(!plugin.check(player, "tamer.heal.wild"))
                    allowed = false;
            }

            if(allowed)
                ((LivingEntity)tameable).setHealth(((LivingEntity)tameable).getMaxHealth());
            else
                player.sendMessage(ChatColor.RED + "You do not have permission to heal this tameables!");
        } else {
            player.sendMessage(ChatColor.RED + "You do not have permission to heal tameables!");
        }
    }

    public void setAge(Tameable tameable, Integer age, Player player) {
        if(plugin.check(player, "tamer.setage")) {
            if(tameable instanceof Ageable) ((Ageable)tameable).setAge(age);
        }
    }

    public void setAgeLock(Tameable tameable, Boolean lock, Player player) {
        if(plugin.check(player, "tamer.setage")) {
            if(tameable instanceof Ageable) {
                player.sendMessage(ChatColor.BLUE + "Setting animal agelock to " + lock);
                ((Ageable)tameable).setAgeLock(lock);
            }
        }
    }

    public void handleEvent(Player player, Tameable tameable) {
        if(!tamers.containsKey(player))
            return;

        TamerMode tamermode = tamers.get(player);
        switch(tamermode.getMode()) {
            case INFO:
                this.getOwner(player, tameable);
                break;
            case TAME:
                this.setTame(tameable, tamermode.getState(), player);
                break;
            case ANGRY:
                this.setAngry(tameable, tamermode.getState(), player);
                break;
            case SIT:
                this.sit(tameable, tamermode.getState(), player);
                break;
            case HEAL:
                this.heal(tameable, tamermode.getState(), player);
                break;
            case SETAGELOCK:
                this.setAgeLock(tameable, tamermode.getState(), player);
                break;
            case SETAGE:
                this.setAge(tameable, tamermode.getData(), player);
                break;
        }
    }
}

