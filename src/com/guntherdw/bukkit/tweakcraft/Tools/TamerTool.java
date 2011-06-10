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

    public void getOwner(Player player, Wolf wolf) {
        if(wolf.isTamed()) {
            AnimalTamer tamer = wolf.getOwner();
            if(tamer instanceof Player) {
                Player ptamer = (Player) tamer;
                boolean allowed = true;
                if(!ptamer.equals(player)) {
                    if(!plugin.check(player, "tamer.info.other"))
                        allowed = false;
                }
                if(allowed) {
                    player.sendMessage(ChatColor.AQUA + "Wolf owner : "+ptamer.getDisplayName());
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
            case HEAL:
                this.heal(wolf, tamermode.getState(), player);
                break;
        }
    }
}

