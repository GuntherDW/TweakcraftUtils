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
import org.bukkit.entity.AnimalTamer;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Wolf;

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

    public void getOwner(Player player, Wolf wolf) {
        if(wolf.isTamed()) {
            AnimalTamer tamer = wolf.getOwner();
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
                    player.sendMessage(ChatColor.AQUA + "Wolf owner : "+(online?ptamer.getDisplayName():offlinetamer.getName()));
                } else {
                    player.sendMessage(ChatColor.RED + "You do not have the permission to check the ownership of other wolves!");
                }
            } else {
                player.sendMessage(ChatColor.AQUA + "This wolf is owned by an entity/offline player!");
            }
        } else {
            player.sendMessage(ChatColor.AQUA + "This wolf isn't tamed!");
        }
        player.sendMessage(ChatColor.AQUA + "Wolf health : "+wolf.getHealth());
        player.sendMessage(ChatColor.AQUA + "Wolf age : "+wolf.getAge() + (wolf.getAgeLock() ? ChatColor.RED+" L":""));
        if(wolf.isAngry()) {
            player.sendMessage(ChatColor.AQUA + "This wolf is "+ChatColor.RED+"angry"+ChatColor.AQUA+"!");
            LivingEntity le = wolf.getTarget();
            String target = "";
            if(le instanceof Player)
                target = ((Player)le).getDisplayName();
            else
                target = "Animal";
            player.sendMessage(ChatColor.AQUA + "Target : "+target);
        }
    }

    public void setTame(Wolf wolf, Boolean tame, Player player) {
        if(plugin.check(player, "tamer.tame")) {
            Boolean allowed = true;
            if(tame==null) {
                tame = !wolf.isTamed();
            }
            if(wolf.isTamed() && !tame) {
                AnimalTamer tamer = wolf.getOwner();

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
                    wolf.setOwner(null);
                    wolf.setTamed(false);
                } else {
                    player.sendMessage(ChatColor.RED + "You do not have permission to untame this wolf!");
                }
            } else if(!wolf.isTamed() && tame) {
                if(!plugin.check(player, "tamer.tame.tame"))
                    allowed = false;
                
                if(allowed) {
                    wolf.setTamed(true);
                    wolf.setAngry(false);
                    wolf.setTarget(null);
                    wolf.setOwner(player);
                } else {
                    player.sendMessage(ChatColor.RED + "You do not have permission to tame this wolf!");
                }
            }
        } else {
            player.sendMessage(ChatColor.RED + "You do not have permission to tame wolves!");
        }
    }

    public void setAngry(Wolf wolf, Boolean angry, Player player) {
        if(plugin.check(player, "tamer.angry"))
        {
            Boolean allowed = true;
            if(wolf.isTamed()) {
                if(wolf.getOwner().equals(player)) {
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
                player.sendMessage(ChatColor.RED + "You do not have permission to anger this wolf!");
            }
        } else {
            player.sendMessage(ChatColor.RED + "You do not have permission to anger wolves!");
        }
    }
    
    public void sit(Wolf wolf, Boolean sit, Player player) {
        if(plugin.check(player, "tamer.sit")) {
            Boolean allowed = true;
            if(wolf.isSitting()) {
                if(wolf.getOwner()!=null && wolf.getOwner().equals(player)) {
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
                sit = !wolf.isSitting();

            if(allowed)
                wolf.setSitting(sit);

            else
                player.sendMessage(ChatColor.RED + "You do not have permission to command this wolf!");
        } else {
            player.sendMessage(ChatColor.RED + "You do not have permission to command wolves!");
        }
    }

    public void heal(Wolf wolf, Boolean kill, Player player) {
        if(plugin.check(player, "tamer.heal")) {
            Boolean allowed = true;
            if(wolf.isTamed()) {
                if(wolf.getOwner()!=null && wolf.getOwner().equals(player)) {
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
                wolf.setHealth(20);
            else
                player.sendMessage(ChatColor.RED + "You do not have permission to heal this wolf!");
        } else {
            player.sendMessage(ChatColor.RED + "You do not have permission to heal wolves!");
        }
    }
    
    public void setAge(Wolf wolf, Integer age, Player player) {
        if(plugin.check(player, "tamer.setage")) {
            wolf.setAge(age);
        }
    }

    public void setAgeLock(Wolf wolf, Boolean lock, Player player) {
            if(plugin.check(player, "tamer.setage")) {
                player.sendMessage(ChatColor.BLUE + "Setting animal agelock to " + lock);
                wolf.setAgeLock(lock);
            }
        }
    
    public void handleEvent(Player player, Wolf wolf) {
        if(!tamers.containsKey(player))
            return;

        TamerMode tamermode = tamers.get(player);
        switch(tamermode.getMode()) {
            case INFO:
                this.getOwner(player, wolf);
                break;
            case TAME:
                this.setTame(wolf, tamermode.getState(), player);
                break;
            case ANGRY:
                this.setAngry(wolf, tamermode.getState(), player);
                break;
            case SIT:
                this.sit(wolf, tamermode.getState(), player);
                break;
            case HEAL:
                this.heal(wolf, tamermode.getState(), player);
                break;
            case SETAGELOCK:
                this.setAgeLock(wolf, tamermode.getState(), player);
                break;
            case SETAGE:
                this.setAge(wolf, tamermode.getData(), player);
                break;
        }
    }
}

