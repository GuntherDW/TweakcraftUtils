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

package com.guntherdw.bukkit.tweakcraft.Commands.Admin;

import com.guntherdw.bukkit.tweakcraft.Ban.BanHandler;
import com.guntherdw.bukkit.tweakcraft.Commands.Command;
import com.guntherdw.bukkit.tweakcraft.Exceptions.CommandException;
import com.guntherdw.bukkit.tweakcraft.Exceptions.CommandSenderException;
import com.guntherdw.bukkit.tweakcraft.Exceptions.CommandUsageException;
import com.guntherdw.bukkit.tweakcraft.Exceptions.PermissionsException;
import com.guntherdw.bukkit.tweakcraft.Packages.ItemDB;
import com.guntherdw.bukkit.tweakcraft.TweakcraftUtils;
import com.guntherdw.bukkit.tweakcraft.Worlds.IWorld;
import com.guntherdw.bukkit.tweakcraft.Worlds.TweakWorld;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;


/**
 * @author GuntherDW
 */
public class CommandTC implements Command {
    public boolean executeCommand(CommandSender sender, String command, String[] args, TweakcraftUtils plugin)
            throws PermissionsException, CommandSenderException, CommandUsageException, CommandException {
        if (args.length > 0) {
            if (args[0].equalsIgnoreCase("version")) {
                sender.sendMessage(ChatColor.WHITE + plugin.getDescription().getName() + ": version " + ChatColor.GREEN + plugin.getDescription().getVersion());
            } else if (args[0].equalsIgnoreCase("reload")) {

                if (sender instanceof Player) {
                    if (!plugin.check((Player) sender, "reload")) {
                        sender.sendMessage(ChatColor.GREEN + "Not implemented yet!");
                        return true;
                    }
                }


                sender.sendMessage(ChatColor.GREEN + "Reloading settings,dbs and setting colors.");
                plugin.getConfigHandler().reloadConfig();
                BanHandler bh = plugin.getBanhandler();
                bh.reloadBans();
                ItemDB idb = plugin.getItemDB();
                idb.loadDataBase();
                for (Player p : plugin.getServer().getOnlinePlayers()) {
                    String name = p.getName();
                    // p.setDisplayName(plugin.getPlayerColor(name, false) + name + ChatColor.WHITE);
                    p.setDisplayName(plugin.getNickWithColors(p.getName()));
                }
                plugin.getPlayerListener().reloadInvisTable();
                /**
                 * This is handled by the config.reloadConfig() call.
                 */
                /* if(plugin.getConfigHandler().enablePersistence) {
                    plugin.getPlayerListener().reloadInfo();
                } */

            } else if(args[0].equalsIgnoreCase("world")) {
                if(sender instanceof Player)
                    if(!plugin.check((Player) sender, "admin.world"))
                        throw new PermissionsException(command);
                if(args.length>2) {
                    String modus = args[1];
                    String world = args[2];
                    String arg   = args.length>3? args[3] : null;
                    IWorld iw = plugin.getworldManager().getWorld(world);
                    if(modus.equalsIgnoreCase("unload")) {
                        if(iw!=null) {
                            if(iw.isEnabled()) {
                                for(Player pl : iw.getBukkitWorld().getPlayers()) {
                                    pl.sendMessage(ChatColor.RED + "WORLD UNLOADING. Sending you to spawn on the first world.");
                                    pl.teleport(plugin.getServer().getWorlds().get(0).getSpawnLocation());
                                }
                                iw.setEnabled(false);
                                sender.sendMessage(ChatColor.GOLD + "Unloading world "+iw.getName());
                                plugin.getServer().unloadWorld(iw.getName(), arg==null?true:Boolean.parseBoolean(arg));
                            } else {
                                sender.sendMessage(ChatColor.RED+"That world wasn't enabled!");
                            }
                        } else {
                            sender.sendMessage(ChatColor.RED+"World error, is that world managed by TweakcraftUtils?");
                        }
                    } else if(modus.equalsIgnoreCase("create") || modus.equalsIgnoreCase("load")) {
                        if(arg == null) arg = "normal";
                        World.Environment env = null;
                        try { env = World.Environment.valueOf(arg.toUpperCase()); }
                        catch(IllegalArgumentException ex) { env = World.Environment.NORMAL; } 
                        if(iw!=null) {
                            if(iw.isEnabled()) {
                                sender.sendMessage(ChatColor.RED + "This world already is enabled!");
                            } else {
                                sender.sendMessage(ChatColor.GOLD + "Enabling world "+world+" with env "+env.name());
                                iw.loadWorld();
                                iw.setEnabled(true);
                            }
                        } else {
                            /**
                             * NEW WORLD
                             */
                            sender.sendMessage(ChatColor.GOLD + "Creating new world "+world+" with env "+env.name());
                            plugin.getworldManager().getWorlds().put(world, new TweakWorld(plugin.getworldManager(), world, env, true));
                        }
                    } else if(modus.equalsIgnoreCase("flag")) {
                        String flagset = args.length>4?args[4]:null;
                        String flag = arg;
                        if(flag!=null) {
                            if(flag.equalsIgnoreCase("monsters") || flag.equalsIgnoreCase("animals")) {
                                Boolean toSet = flagset!=null?Boolean.parseBoolean(flagset):null;
                                if(flag.equalsIgnoreCase("monsters")) {
                                    if(toSet!=null)
                                        iw.setAllowMonsters(toSet);
                                    sender.sendMessage(ChatColor.LIGHT_PURPLE + "["+ChatColor.GOLD+iw.getName()+ChatColor.LIGHT_PURPLE+"]"
                                                       + " MONSTERS: "+(iw.getAllowMonsters()?ChatColor.GREEN+"enabled":ChatColor.RED+"disabled"));
                                } else if(flag.equalsIgnoreCase("animals")) {
                                    if(toSet!=null)
                                        iw.setAllowAnimals(toSet);
                                    sender.sendMessage(ChatColor.LIGHT_PURPLE + "["+ChatColor.GOLD+iw.getName()+ChatColor.LIGHT_PURPLE+"]"
                                                       + " ANIMALS: "+(iw.getAllowAnimals()?ChatColor.GREEN+"enabled":ChatColor.RED+"disabled"));
                                } else if(flag.equalsIgnoreCase("env")) {
                                    World.Environment wenv = iw.getBukkitWorld().getEnvironment();
                                    sender.sendMessage(ChatColor.LIGHT_PURPLE + "["+ChatColor.GOLD+iw.getName()+ChatColor.LIGHT_PURPLE+"]"
                                                       + " ENVIRONMENT: "+wenv.name());
                                }
                            }
                        } else {
                            sender.sendMessage("Set of flags to enable/disable : [monsters|animals|env]");
                        }
                    }

                } else {
                    sender.sendMessage(ChatColor.GREEN + "usage: /tc world create|unload|flag");
                }
            }
        } else {
            throw new CommandUsageException("/tc <" + ChatColor.GREEN + "reload" + ChatColor.YELLOW + "/" + ChatColor.GREEN + "version" + ChatColor.YELLOW + ">");
        }
        return true;
    }

    @Override
    public String getPermissionSuffix() {
        return "reload";
    }
}
