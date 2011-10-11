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

import com.guntherdw.bukkit.tweakcraft.Commands.iCommand;
import com.guntherdw.bukkit.tweakcraft.DataSources.Ban.BanHandler;
import com.guntherdw.bukkit.tweakcraft.Exceptions.CommandException;
import com.guntherdw.bukkit.tweakcraft.Exceptions.CommandSenderException;
import com.guntherdw.bukkit.tweakcraft.Exceptions.CommandUsageException;
import com.guntherdw.bukkit.tweakcraft.Exceptions.PermissionsException;
import com.guntherdw.bukkit.tweakcraft.Packages.ItemDB;
import com.guntherdw.bukkit.tweakcraft.TweakcraftUtils;
import com.guntherdw.bukkit.tweakcraft.Worlds.iWorld;
import com.guntherdw.bukkit.tweakcraft.Worlds.TweakWorld;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;


/**
 * @author GuntherDW
 */
public class CommandTC implements iCommand {
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
                    String displayName = plugin.getNickWithColors(p.getName());
                    String ldisplayname = displayName.substring(0, displayName.length()-2);
                    p.setDisplayName(displayName);
                    if(ldisplayname.length()<=16)
                        p.setPlayerListName(ldisplayname);
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
                    iWorld iw = plugin.getworldManager().getWorld(world);
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
                        boolean tcworld = (iw != null);
                        World bw = null;
                        if(!tcworld) {
                            bw = plugin.getServer().getWorld(world);
                            if(bw==null)
                                throw new CommandException("World not found!");
                        } else {
                            if(!iw.isEnabled())
                                throw new CommandException("World is not enabled/active!");
                        }

                        if(flag!=null) {
                            Boolean toSet = flagset!=null?Boolean.parseBoolean(flagset):null;
                            if(flag.equalsIgnoreCase("monsters")) {
                                if(toSet!=null)
                                    if(tcworld)
                                        iw.setAllowMonsters(toSet);
                                    else
                                        bw.setSpawnFlags(toSet, bw.getAllowAnimals());

                                sender.sendMessage(ChatColor.LIGHT_PURPLE + "["+ChatColor.GOLD+(tcworld?iw.getName():bw.getName())+ChatColor.LIGHT_PURPLE+"]"
                                        + " MONSTERS: "+((tcworld? iw.getAllowMonsters() : bw.getAllowMonsters())?ChatColor.GREEN+"enabled":ChatColor.RED+"disabled"));
                            } else if(flag.equalsIgnoreCase("animals")) {
                                if(toSet!=null)
                                    if(tcworld)
                                        iw.setAllowAnimals(toSet);
                                    else
                                        bw.setSpawnFlags(bw.getAllowMonsters(), toSet);

                                sender.sendMessage(ChatColor.LIGHT_PURPLE + "["+ChatColor.GOLD+(tcworld?iw.getName():bw.getName())+ChatColor.LIGHT_PURPLE+"]"
                                        + " ANIMALS: "+((tcworld? iw.getAllowAnimals() : bw.getAllowAnimals())?ChatColor.GREEN+"enabled":ChatColor.RED+"disabled"));
                            } else if(flag.equalsIgnoreCase("pvp")) {
                                if(toSet!=null)
                                    if(tcworld)
                                        iw.setPVP(toSet);
                                    else
                                        bw.setPVP(toSet);

                                sender.sendMessage(ChatColor.LIGHT_PURPLE + "["+ChatColor.GOLD+(tcworld?iw.getName():bw.getName())+ChatColor.LIGHT_PURPLE+"]"
                                        + " PVP: "+((tcworld? iw.getPVP() : bw.getPVP())?ChatColor.GREEN+"enabled":ChatColor.RED+"disabled"));

                            } else if(flag.equalsIgnoreCase("env")) {
                                World.Environment wenv = tcworld? iw.getBukkitWorld().getEnvironment() : bw.getEnvironment();
                                sender.sendMessage(ChatColor.LIGHT_PURPLE + "["+ChatColor.GOLD+(tcworld?iw.getName():bw.getName())+ChatColor.LIGHT_PURPLE+"]"
                                        + " ENVIRONMENT: "+wenv.name());
                            } else {
                                throw new CommandUsageException("No flag by that name found!");
                            }
                        } else {
                            throw new CommandUsageException("Set of flags to enable/disable : [monsters|animals|env]");
                        }
                    } else if(modus.equalsIgnoreCase("info")) {
                        boolean tcworld = (iw!=null);
                        World bw = null;
                        World.Environment wenv;
                        if(!tcworld) {
                            bw = plugin.getServer().getWorld(world);
                            if(bw==null)
                                throw new CommandException("World not found!");
                            else
                                wenv = bw.getEnvironment();
                        } else {
                            if(!iw.isEnabled())
                                throw new CommandException("World is not enabled/active!");

                            wenv = iw.getBukkitWorld().getEnvironment();
                        }
                        boolean monsters = tcworld?iw.getAllowMonsters():bw.getAllowMonsters();
                        boolean animals = tcworld?iw.getAllowAnimals():bw.getAllowAnimals();
                        boolean pvp     = tcworld?iw.getPVP():bw.getPVP();
                        int amountofplayers = tcworld?iw.getBukkitWorld().getPlayers().size():bw.getPlayers().size();
                        String players = "";
                        if(amountofplayers<5) {
                            List<Player> ps = tcworld?iw.getBukkitWorld().getPlayers():bw.getPlayers();
                            for(Player p : ps) {
                                players+=p.getDisplayName()+ChatColor.WHITE+",";
                            }
                            if(players.length()>0)
                                players = players.substring(0, players.length()-1);
                        }
                        String name = tcworld?iw.getName():bw.getName();
                        sender.sendMessage(ChatColor.LIGHT_PURPLE+"["+ChatColor.GOLD + name + ChatColor.LIGHT_PURPLE+"]");
                        sender.sendMessage(ChatColor.RED+"ANIMALS: "+(animals?ChatColor.GREEN+"enabled":ChatColor.RED+"disabled"));
                        sender.sendMessage(ChatColor.RED+"MONSTERS: "+(monsters?ChatColor.GREEN+"enabled":ChatColor.RED+"disabled"));
                        sender.sendMessage(ChatColor.RED+"PVP: "+(pvp?ChatColor.GREEN+"enabled":ChatColor.RED+"disabled"));
                        sender.sendMessage(ChatColor.RED+"ENV: "+wenv);
                        sender.sendMessage(ChatColor.RED+"PLAYERS: "+amountofplayers + (players.equals("")?"":(" ("+players+ChatColor.RED+")")));
                    }

                } else {
                    sender.sendMessage(ChatColor.GREEN + "usage: /tc world create|unload|flag|info");
                }
            } else if(args[0].equalsIgnoreCase("improvchat")) {
                if(sender instanceof Player) {
                    Player player = (Player) sender;
                    List<Player> lijst = plugin.getCUIPlayers();
                    if(lijst!=null && !lijst.contains(player)) {
                        plugin.getLogger().info("[TweakcraftUtils] Adding "+player.getName()+" to the CUI list!");
                        lijst.add(player);
                    }

                    plugin.sendCUIChatMode(player);
                }
            } else if(args[0].equalsIgnoreCase("tooldura")) {
                if(sender instanceof Player) {
                    Player player = (Player) sender;
                    List<Player> lijst = plugin.getMod_InfDuraplayers();
                    if(lijst!=null && !lijst.contains(player)) {
                        plugin.getLogger().info("[TweakcraftUtils] Adding "+player.getName()+" to the mod_InfDura list!");
                        lijst.add(player);
                    }

                    plugin.sendToolDuraMode(player);
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
