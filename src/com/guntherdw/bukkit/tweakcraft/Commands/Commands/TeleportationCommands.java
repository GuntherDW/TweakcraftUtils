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

package com.guntherdw.bukkit.tweakcraft.Commands.Commands;

import com.guntherdw.bukkit.tweakcraft.Commands.aCommand;
import com.guntherdw.bukkit.tweakcraft.Exceptions.CommandException;
import com.guntherdw.bukkit.tweakcraft.Exceptions.CommandSenderException;
import com.guntherdw.bukkit.tweakcraft.Exceptions.CommandUsageException;
import com.guntherdw.bukkit.tweakcraft.Exceptions.PermissionsException;
import com.guntherdw.bukkit.tweakcraft.Tools.ArgumentParser;
import com.guntherdw.bukkit.tweakcraft.TweakcraftUtils;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author GuntherDW
 */
public class TeleportationCommands {

    TweakcraftUtils plugin;

    public TeleportationCommands(TweakcraftUtils instance) {
        this.plugin = instance;
    }

    @aCommand(
        aliases = {"tele"},
        permissionBase = "tele",
        description = "Teleport to a specific location",
        section = "teleport"
    )
    public boolean teleportToLocation(CommandSender sender, String command, String[] realargs)
        throws PermissionsException, CommandSenderException, CommandUsageException, CommandException {
        boolean isPlayer = false;
        ArgumentParser ap = new ArgumentParser(realargs);
        Integer x = ap.getInteger("x", null);
        Integer y = ap.getInteger("y", null);
        Integer z = ap.getInteger("z", null);
        String w = ap.getString("w", null);
        String p = ap.getString("p", null);
        String[] args = ap.getUnusedArgs();
        if (sender instanceof Player) {
            isPlayer = true;
            if (!plugin.check((Player) sender, "tele"))
                throw new PermissionsException(command);
        }

        Player victim = null;


        // Player player = (Player) sender;
        /* Integer x = null;
    Integer y = null;
    Integer z = null; */
        World world = null;

        if (args.length == 0) {
            throw new CommandUsageException("Usage: /tele up|x z (y) <world> or /tele chunk x y (z) <world>");
        } else if (args.length == 1 && args[0].equalsIgnoreCase("up")) {
            Location l = ((Player) sender).getLocation().clone();
            x = (int) l.getX();
            y = 129;
            z = (int) l.getZ();
            world = l.getWorld();
        } else if (args.length > 0 && args[0].equalsIgnoreCase("chunk")) {
            if (args.length < 3) {
                throw new CommandUsageException("I need at least 2 variables!");
            }
            try {
                if (isPlayer)
                    world = ((Player) sender).getWorld();
                x = Integer.parseInt(args[1]) << 4;
                z = Integer.parseInt(args[2]) << 4;
                if (args.length > 3) {
                    y = Integer.parseInt(args[3]);
                } else {
                    y = 129;
                }
                if (args.length > 4 || w != null) {
                    if (w == null) w = args[4];
                    if (plugin.getServer().getWorlds().contains(plugin.getServer().getWorld(w))) {
                        world = plugin.getServer().getWorld(w);
                    } else {
                        if (isPlayer)
                            world = ((Player) sender).getWorld();
                        else
                            throw new CommandException("World not found!");
                    }
                }

            } catch (NumberFormatException ex) {
                throw new CommandException("I need numbers not strings!");
            } catch (NullPointerException ex) {
                throw new CommandException("I can't find that world!");
            }
        } else {
            try {
                if (x == null) x = Integer.parseInt(args[0]);
                if (z == null) z = Integer.parseInt(args[1]);
                if (args.length == 3) {
                    if (y == null) y = Integer.parseInt(args[2]);
                } else {
                    y = 129;
                }
            } catch (NumberFormatException ex) {
                throw new CommandException("I need numbers not strings!");
            }
            if (args.length > 3 || w != null) {
                if (w == null) w = args[3];
                try {
                    if (plugin.getServer().getWorlds().contains(plugin.getServer().getWorld(w))) {
                        world = plugin.getServer().getWorld(w);
                    } else {
                        if (isPlayer)
                            world = ((Player) sender).getWorld();
                        else
                            throw new CommandException("World not found!");
                    }
                } catch (Exception e) {
                    throw new CommandUsageException("Can't find world with name " + w);
                }
            } else {
                if (isPlayer)
                    world = ((Player) sender).getWorld();
            }
        }
        if (args.length == 5 || p != null) // Added victim
        {
            if (p == null) p = args[4];
            victim = plugin.findPlayerasPlayer(p);
        }
        if (world != null) {
            if (isPlayer) {
                victim = (Player) sender;
            } else {
                if (victim == null) {
                    throw new CommandException("If you're not a player, would you mind giving me a victim?");
                }
            }
            if (victim != null) {
                Location oldloc = victim.getLocation();

                Location loc = new Location(world, x.doubleValue(), y.doubleValue(), z.doubleValue());
                if (victim.teleport(loc)) {
                    plugin.getTelehistory().addHistory(victim.getName(), oldloc);
                }
            }
        } else {
            throw new CommandException("Something went wrong, check the syntax!");
        }

        // Location loc = new Location(world, x.doubleValue(), y.doubleValue(), z.doubleValue());
        // plugin.getTelehistory().addHistory(((Player)sender).getName(), ((Player)sender).getLocation());
        // ((Player) sender).teleport(loc);


        /* } else {
           throw new CommandSenderException("You need to be a player to teleport!");
       } */
        return true;
    }

    @aCommand(
        aliases = {"tp"},
        permissionBase = "tp",
        description = "Teleports you to another player",
        section = "teleport"
    )
    public boolean teleportToPlayer(CommandSender sender, String command, String[] realargs)
        throws PermissionsException, CommandSenderException, CommandUsageException {

        ArgumentParser ap = new ArgumentParser(realargs);
        String p1 = ap.getString("p", null);
        if (p1 == null) p1 = ap.getString("f", null);
        String p2 = ap.getString("t", null);

        String[] args = ap.getUnusedArgs();

        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (!plugin.check(player, "tp"))
                throw new PermissionsException(command);

            if (args.length == 1 || p1 != null) {
                if (plugin.getDonottplist().contains(player.getName()) && !plugin.check(player, "forcetp")) {
                    player.sendMessage(ChatColor.RED + "You can't tp when you don't allow others to tp to you!");
                } else {
                    // List<Player> p = plugin.getServer().matchPlayer(args[0]);
                    if (p1 == null) p1 = args[0];
                    List<Player> players = plugin.findPlayerasPlayerList(p1);
                    Player p = null;
                    if (players.size() == 1)
                        p = players.get(0);

                    if (p == null) {
                        player.sendMessage(ChatColor.YELLOW + "Can't find player!");
                    } else {
                        boolean refusetp = plugin.getDonottplist().contains(p.getName());
                        boolean tpsuccess = true;
                        if (plugin.getPlayerListener().getInvisplayers().contains(p.getName())) {
                            if (!plugin.check(player, "tpinvis")) {
                                player.sendMessage(ChatColor.YELLOW + "Can't find player!");
                                plugin.getLogger().info("[TweakcraftUtils] " + player.getName() + " tried to tp to " + p.getName() + " <invisible>!");
                                return true;
                            } else {
                                player.sendMessage(ChatColor.AQUA + "Stealth player TP!");
                            }
                        }
                        boolean override = false;
                        if (refusetp && (player.isOp() || plugin.check(player, "forcetp"))) {
                            override = true;
                        } else {
                            override = false;
                            /* if(refusetp)
                         override = true; */
                        }

                        if (!player.getWorld().getName().equals(p.getWorld().getName())) {
                            if (!plugin.check(player, "worlds." + p.getWorld().getName() + ".tp"))
                                throw new PermissionsException("You don't have permission to TP to someone in that world!");
                        }

                        if (p.getName().equals(player.getName())) {
                            player.sendMessage(ChatColor.YELLOW + "You're already there!");
                        } else {
                            if (refusetp && !override) {
                                player.sendMessage(ChatColor.RED + "You don't have the correct permission to tp to " + p.getDisplayName() + ChatColor.RED + "!");
                                p.sendMessage(player.getDisplayName() + ChatColor.YELLOW + " tried to tp to you!");
                            } else {
                                Location oldloc = player.getLocation();
                                tpsuccess = player.teleport(getTpLocation(p));
                                if (tpsuccess) {
                                    plugin.getTelehistory().addHistory(player.getName(), oldloc);
                                    p.sendMessage(player.getDisplayName() + ChatColor.LIGHT_PURPLE + " Teleported to you!");

                                    if (override)
                                        player.sendMessage(ChatColor.RED + "Forced tp!");
                                    plugin.getLogger().info("[TweakcraftUtils] " + player.getName() + " teleported to " + p.getName() + "!");
                                } else {
                                    p.sendMessage(player.getDisplayName() + ChatColor.LIGHT_PURPLE + " failed to teleport to you!");
                                    plugin.getLogger().info("[TweakcraftUtils] " + player.getName() + " failed to teleport to " + p.getName() + "!");
                                    // This doesn't happen, CraftBukkit has return true; in it's sources!
                                }
                            }
                        }
                    }
                }
            } else if (args.length == 2) {
                if (!plugin.check(player, "tpfromto"))
                    throw new PermissionsException(command);
                this.tpfromto(plugin, sender, args[0], args[1]);

            } else {
                throw new CommandUsageException("Incorrect usage!");
            }
        } else if (args.length == 2) {
            this.tpfromto(plugin, sender, args[0], args[1]);
        } else {
            throw new CommandSenderException("You're the console, where do you think you're going?");
        }

        return true;
    }

    private static int floor(double d) {
        int rt = (int) d;
        return rt > d ? rt - 1 : rt;
    }

    private Location getTpLocation(Player player) {
        Location loc = player.getLocation();
        int x = floor(loc.getX()), y = floor(loc.getY()) - 1, z = floor(loc.getZ());
        for (int dx = -1; dx < 1; dx++)
            for (int dz = -1; dz <= 1; dz++)
                if (validSpot(loc.getWorld(), x + dx, y, z + dz))
                    return new Location(loc.getWorld(), x + dx + 0.5F, y + 2, z + dz + 0.5F);
        return loc;
    }

    private boolean validSpot(World world, int x, int y, int z) {
        return world.getBlockTypeIdAt(x, y, z) != 0
            && world.getBlockTypeIdAt(x, y + 1, z) == 0
            && world.getBlockTypeIdAt(x, y + 2, z) == 0;
    }

    private void tpfromto(TweakcraftUtils plugin, CommandSender sender, String p1, String p2) {
        List<Player> pfind = plugin.findPlayerasPlayerList(p1);
        Player pfrom, pto;
        if (pfind.size() == 1) {
            pfrom = pfind.get(0);
        } else {
            sender.sendMessage(ChatColor.DARK_GREEN + "Can't find source player!");
            return;
        }

        pfind = plugin.findPlayerasPlayerList(p2);
        if (pfind.size() != 0) {
            pto = pfind.get(0);
        } else {
            sender.sendMessage(ChatColor.DARK_GREEN + "Can't find destination player!");
            return;
        }
        sender.sendMessage(ChatColor.GREEN + "Teleporting " + pfrom.getName() + " to " + pto.getName() + "!");
        String player = "";
        if (sender instanceof Player) {
            player = ((Player) sender).getName();
        } else {
            player = "CONSOLE";
        }
        plugin.getLogger().info("[TweakcraftUtils] " + player + " teleported " + pfrom.getName() + " to " + pto.getName() + "!");
        plugin.getTelehistory().addHistory(pfrom.getName(), pfrom.getLocation());
        pfrom.teleport(pto);
    }

    @aCommand(
        aliases = {"tpback", "tpb", "back"},
        permissionBase = "tpback",
        description = "Go back to where you once came from!",
        section = "teleport"
    )
    public boolean tpBack(CommandSender sender, String command, String[] args)
        throws PermissionsException, CommandSenderException, CommandUsageException, CommandException {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (!plugin.check(player, "tpback"))
                throw new PermissionsException(command);
            if (plugin.getConfigHandler().enableTPBack) {

                boolean go = true;

                if (args.length > 0) {
                    if (args[0].equalsIgnoreCase("clear")) {
                        player.sendMessage(ChatColor.GOLD + "Cleaning your TPBack history!");
                        plugin.getTelehistory().clearHistory(player.getName());
                        go = false;
                    } else if (args[0].equalsIgnoreCase("remove")) {
                        if (plugin.getTelehistory().getRemaining(player.getName()) > 0) {
                            player.sendMessage(ChatColor.YELLOW + "Removing last tpback line.");
                            plugin.getTelehistory().removeLast(player.getName());
                        } else {
                            player.sendMessage(ChatColor.YELLOW + "You don't have any tpback lines!");
                        }
                        go = false;
                    } else if (args[0].equalsIgnoreCase("debug")) {
                        int pos = 0;
                        String s = "";
                        int offs = plugin.getTelehistory().getOffset(player.getName());
                        List<Location> loclist = plugin.getTelehistory().getHistoryList(player.getName());
                        if (loclist != null) {
                            // player.sendMessage(ChatColor.GOLD + "offs : "+offs);
                            if (offs == -1) offs = loclist.size() - 1;
                            else offs = loclist.size() - offs;
                            // player.sendMessage(ChatColor.GOLD +"size : "+loclist.size());
                            for (Location l : loclist) {
                                s = "world: " + l.getWorld().getName() + " x:" + Math.floor(l.getX()) + " y:" + Math.floor(l.getY()) + " z:" + Math.floor(l.getZ());
                                player.sendMessage((pos == offs ? "--> " : "") + ChatColor.GOLD + s);
                                pos++;
                            }
                        }
                        go = false;
                    }
                }

                if (go) {
                    boolean atOrigin = plugin.getTelehistory().atOrigin(player.getName());
                    int offSet = plugin.getTelehistory().getOffset(player.getName());
                    int size = plugin.getTelehistory().getRemaining(player.getName()) - 1;
                    int pos = size - (offSet > 0 ? offSet : 0);
                    // Location back = plugin.getTelehistory().getLastEntry(player.getName(), false);
                    Location back = plugin.getTelehistory().get(player.getName(), pos, true); // getLastEntry(player.getName(), false)
                    Location oldLocation = player.getLocation().clone();
                    if (back == null) {
                        if (atOrigin) player.sendMessage(ChatColor.GOLD + "You don't have any history issues left!");
                        else player.sendMessage(ChatColor.GOLD + "You don't have any history issues yet!");

                    } else {
                        player.sendMessage(ChatColor.GOLD + "Teleporting you back to your previous position!");
                        if (back.getY() == 130) {
                            player.sendMessage(ChatColor.GOLD + "Sending you to Y:130 because you were either too high or too low!");
                        }

                        boolean success = player.teleport(back);
                        if (success) {
                            if (atOrigin) {
                                player.sendMessage(ChatColor.GOLD + "You are at your starting point!");
                            }
                            if (offSet == -1) {
                                plugin.getTelehistory().addHistory(player.getName(), oldLocation);
                                plugin.getTelehistory().setHistoryOffset(player.getName(), 2);
                            }

                        } else {
                            player.sendMessage(ChatColor.RED + "tpback failure, tpback line NOT removed!");
                            player.sendMessage(ChatColor.RED + "To remove this line, type /tpback remove");
                        }
                    }
                }
            } else {
                player.sendMessage(ChatColor.RED + "TP History isn't enabled!");
            }
        } else {
            throw new CommandSenderException("Consoles need tp history nowadays?");
        }
        return true;
    }

    @aCommand(
        aliases = {"tpforward", "tpf", "forward"},
        permissionBase = "tpback",
        description = "Go back to where you once backed from!",
        section = "teleport"
    )
    public boolean tpForward(CommandSender sender, String command, String[] args)
        throws PermissionsException, CommandSenderException, CommandUsageException, CommandException {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (!plugin.check(player, "tpback"))
                throw new PermissionsException(command);
            if (plugin.getConfigHandler().enableTPBack) {
                boolean go = true;
                if (args.length > 0) {

                    if (args[0].equalsIgnoreCase("clear")) {
                        player.sendMessage(ChatColor.GOLD + "Cleaning your TPBack future!");
                        plugin.getTelehistory().clearFuture(player.getName());
                        go = false;
                    } else if (args[0].equalsIgnoreCase("remove")) {
                        if (plugin.getTelehistory().getRemaining(player.getName()) > 0) {
                            player.sendMessage(ChatColor.YELLOW + "Removing next tpback line.");
                            plugin.getTelehistory().removeNext(player.getName());
                        } else {
                            player.sendMessage(ChatColor.YELLOW + "You don't have any tpback lines!");
                        }
                        go = false;
                    }
                }

                if (go) {
                    // boolean atOrigin = plugin.getTelehistory().atOrigin(player.getName());
                    int offSet = plugin.getTelehistory().getOffset(player.getName());
                    int size = plugin.getTelehistory().getRemaining(player.getName()) - 1;
                    int pos = (size - (offSet > 0 ? offSet : 0)) + 2;
                    Location back = plugin.getTelehistory().get(player.getName(), pos, false);
                    if (back == null) {
                        player.sendMessage(ChatColor.GOLD + "You don't have any future issues yet/left!");
                    } else {
                        player.sendMessage(ChatColor.GOLD + "Teleporting you back to your future position!");
                        if (back.getY() == 130) {
                            player.sendMessage(ChatColor.GOLD + "Sending you to Y:130 because you were either too high or too low!");
                        }

                        boolean success = player.teleport(back);
                        if (success) {
                        }
                    }
                }
            } else {
                player.sendMessage(ChatColor.RED + "TP History isn't enabled!");
            }
        } else {
            throw new CommandSenderException("Consoles need tp history nowadays?");
        }
        return true;
    }

    @aCommand(
        aliases = {"tphere", "s", "summon"},
        permissionBase = "tphere",
        description = "Teleport a player to you",
        section = "teleport"
    )
    public boolean tpHere(CommandSender sender, String command, String[] args)
        throws PermissionsException, CommandSenderException, CommandUsageException {
        if (sender instanceof Player) {
            if (!plugin.check((Player) sender, "tphere"))
                throw new PermissionsException(command);


            if (args.length < 1) {
                throw new CommandUsageException("You need to give me a name!");
            }

            List<Player> victims = null;
            List<Player> players = null;

            Player player = (Player) sender;

            if (args.length == 1 && args[0].equals("*")) {
                victims = Arrays.asList(plugin.getServer().getOnlinePlayers());
                if (victims.contains(player))
                    victims.remove(player); // remove the origin player!
            }

            if (victims == null) {
                for (String plstring : args) {
                    players = plugin.findPlayerasPlayerList(plstring);
                    if (players.size() == 1) {
                        if (victims == null) victims = new ArrayList<Player>();
                        victims.add(players.get(0));
                    }
                }

            }

            /* Player p = null;
     if(players.size()==1)
         p = players.get(0); */

            if (victims == null) {
                player.sendMessage(ChatColor.YELLOW + "Can't find player(s)!");
            } else {
                // Player pto = p.get(0);
                for (Player pto : victims) {
                    if (pto.getName().equals(player.getName())) {
                        player.sendMessage(ChatColor.YELLOW + "Now look at that, you've teleported yourself to yourself");
                    } else {
                        Location origloc = pto.getLocation();
                        boolean success = pto.teleport(player);
                        if (success) {
                            player.sendMessage(ChatColor.YELLOW + "Teleporting " + pto.getDisplayName() + ChatColor.YELLOW + " to you!");
                            pto.sendMessage(player.getDisplayName() + ChatColor.YELLOW
                                + " teleported you to him!");
                            plugin.getTelehistory().addHistory(pto.getName(), origloc);
                        } else {
                            player.sendMessage(ChatColor.YELLOW + "Failed to teleport " + pto.getDisplayName() + ChatColor.YELLOW + " to you!");
                            pto.sendMessage(player.getDisplayName() + ChatColor.YELLOW
                                + ChatColor.RED + " tried/failed" + ChatColor.YELLOW + " to teleport you to him!");
                        }

                    }
                }
            }
        } else {
            throw new CommandSenderException("You need to be player to teleport someone to you!");
        }

        return true;
    }

    @aCommand(
        aliases = {"tpmob"},
        permissionBase = "tpmob",
        description = "Teleports a mob to you",
        section = "teleport"
    )
    public boolean tpMob(CommandSender sender, String command, String[] args)
        throws PermissionsException, CommandSenderException, CommandUsageException, CommandException {
        if (sender instanceof Player) {
            Player victim = (Player) sender;
            String vname = "you";

            Integer cnum;
            CreatureType ctype = null;
            if (!plugin.check((Player) sender, "tpmob"))
                throw new PermissionsException(command);

            if (args.length < 1)
                throw new CommandUsageException("I need at least 1 number or mobname!");

            if (args.length == 2) // teleport to victim!
            {
                List<Player> find = plugin.getServer().matchPlayer(args[1]);
                if (find.size() == 1) {
                    victim = find.get(0);
                    vname = victim.getDisplayName();
                } else {
                    throw new CommandUsageException("Can't find victim!");
                }
            }

            if (args[0].equals("*")) {
                cnum = -1;
            } else {
                try {
                    cnum = Integer.parseInt(args[0]);
                } catch (NumberFormatException e) {
                    String mn = args[0];
                    if (args[0].length() > 2)
                        mn = args[0].substring(0, 1).toUpperCase() + args[0].substring(1, args[0].length());
                    cnum = 0;
                    ctype = CreatureType.fromName(mn);
                    if (ctype == null) {
                        throw new CommandUsageException("I need a number or a name, not a random string!");
                    }
                }
            }
            if (ctype != null) {
                sender.sendMessage(ChatColor.YELLOW + "Teleporting all " + ctype.getName() + " to " + vname + ChatColor.YELLOW + "!");
            } else if (cnum == -1) {
                sender.sendMessage(ChatColor.YELLOW + "Teleporting all mobs to " + vname + ChatColor.YELLOW + "!");
            } else {
                sender.sendMessage(ChatColor.YELLOW + "Teleporting mob with mobId " + cnum + " to " + vname + ChatColor.YELLOW + "!");
            }
            boolean allowed = true;
            for (LivingEntity crea : plugin.getServer().getWorld(((Player) sender).getWorld().getName()).getLivingEntities()) {
                if (crea instanceof Creature || crea instanceof Flying) {
                    allowed = true;
                    if (crea instanceof Wolf) {
                        Wolf wolf = (Wolf) crea;
                        if (wolf.isAngry() || !wolf.isTamed())
                            allowed = true;
                        else {
                            allowed = wolf.getTarget() != null && wolf.getTarget().equals(victim.getName());
                        }
                    }
                    CreatureType type = null;
                    type = CreatureType.fromName(crea.getClass().getCanonicalName().split("Craft")[1]);

                    if (cnum == -1 || ctype == type || crea.getEntityId() == cnum.intValue()) {
                        if (allowed)
                            crea.teleport(victim);
                        continue;
                    }
                }

            }
        } else {
            sender.sendMessage("Sorry, no consoles allowed in this party!");
        }
        return true;
    }

    @aCommand(
        aliases = {"tpoff"},
        permissionBase = "tpoff",
        description = "Turns off teleporting for you!",
        section = "teleport"
    )
    public boolean tpOffCommand(CommandSender sender, String command, String[] args)
        throws PermissionsException, CommandSenderException, CommandUsageException {
        if (sender instanceof Player) {
            if (!plugin.check((Player) sender, "tpoff"))
                throw new PermissionsException(command);

            if (args.length != 0 && !args[0].equalsIgnoreCase(((Player) sender).getName())) {
                if (!plugin.check((Player) sender, "tpoffother"))
                    throw new PermissionsException(command);
                this.tpoff(plugin, sender, args[0]);
            } else {
                if (!plugin.getDonottplist().contains(((Player) sender).getName())) {
                    plugin.getDonottplist().add(((Player) sender).getName());
                    sender.sendMessage(ChatColor.YELLOW + "They can no longer tp to you!");
                } else {
                    sender.sendMessage(ChatColor.YELLOW + "You already are on the do-not-tp list!");
                }
            }
        } else if (args.length == 1) {
            this.tpoff(plugin, sender, args[0]);
        } else {
            throw new CommandSenderException("Why do you need tpon for the console?");
        }

        return true;
    }

    private void tpoff(TweakcraftUtils plugin, CommandSender sender, String player) {
        String playername = plugin.findPlayer(player);
        if (!plugin.getDonottplist().contains(playername)) {
            plugin.getDonottplist().add(playername);
            sender.sendMessage(ChatColor.GREEN + "They can no longer tp to " + playername + "!");
        } else {
            sender.sendMessage(ChatColor.GREEN + playername + " already is on the do-not-tp list!");
        }
    }

    @aCommand(
        aliases = {"tpon"},
        permissionBase = "tpon",
        description = "Turns tp'ing back on!",
        section = "teleport"
    )
    public boolean tpOnCommand(CommandSender sender, String command, String[] args)
        throws PermissionsException, CommandSenderException, CommandUsageException {
        if (sender instanceof Player) {
            if (!plugin.check((Player) sender, "tpoff"))
                throw new PermissionsException(command);

            if (args.length != 0 && !args[0].equalsIgnoreCase(((Player) sender).getName())) {
                if (!plugin.check((Player) sender, "tpoffother"))
                    throw new PermissionsException(command);
                this.tpon(plugin, sender, args[0]);
            } else {
                if (plugin.getDonottplist().contains(((Player) sender).getName())) {
                    plugin.getDonottplist().remove(((Player) sender).getName());
                    sender.sendMessage(ChatColor.YELLOW + "They can now tp to you!");
                } else {
                    sender.sendMessage(ChatColor.YELLOW + "You aren't on the do-not-tp list!");
                }
            }
        } else if (args.length == 1) {
            this.tpon(plugin, sender, args[0]);
        } else {
            throw new CommandSenderException("Why do you need tpon for the console?");
        }

        return true;
    }

    private void tpon(TweakcraftUtils plugin, CommandSender sender, String player) {
        String playername = plugin.findPlayer(player);
        if (plugin.getDonottplist().contains(playername)) {
            plugin.getDonottplist().remove(playername);
            sender.sendMessage(ChatColor.GREEN + "They can now tp to " + playername + "!");
        } else {
            sender.sendMessage(ChatColor.GREEN + "I can't find " + playername + " in the do-not-tp list!");
        }
    }
}
