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

import com.ensifera.animosity.craftirc.RelayedMessage;
import com.guntherdw.bukkit.tweakcraft.Commands.aCommand;
import com.guntherdw.bukkit.tweakcraft.DataSources.PersistenceClass.PlayerHistoryInfo;
import com.guntherdw.bukkit.tweakcraft.DataSources.PersistenceClass.PlayerInfo;
import com.guntherdw.bukkit.tweakcraft.Exceptions.CommandException;
import com.guntherdw.bukkit.tweakcraft.Exceptions.CommandSenderException;
import com.guntherdw.bukkit.tweakcraft.Exceptions.CommandUsageException;
import com.guntherdw.bukkit.tweakcraft.Exceptions.PermissionsException;
import com.guntherdw.bukkit.tweakcraft.Packages.LocalPlayer;
import com.guntherdw.bukkit.tweakcraft.Packages.TamerMode;
import com.guntherdw.bukkit.tweakcraft.Tools.ArgumentParser;
import com.guntherdw.bukkit.tweakcraft.Tools.TamerTool;
import com.guntherdw.bukkit.tweakcraft.TweakcraftUtils;

import com.guntherdw.bukkit.tweakcraft.Util.EntityLocation;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author GuntherDW
 */
public class GeneralCommands {

    @aCommand(
        aliases = { "addexp", "adde" },
        permissionBase = "addexp",
        description = "Adds experience to you or someone else",
        section = "general"
    )
    public boolean addExperience(CommandSender sender, String command, String[] args, TweakcraftUtils plugin)
        throws PermissionsException, CommandSenderException, CommandUsageException, CommandException {
        if(sender instanceof Player)
            if(!plugin.check((Player)sender, "addexp"))
                throw new PermissionsException(command);

        ArgumentParser ap = new ArgumentParser(args);
        String playerString =  ap.getString("p", null);
        Integer level = ap.getInteger("l", null);
        String[] ars = ap.getUnusedArgs();

        if(playerString==null) {
            if(!(sender instanceof Player))
                throw new CommandUsageException("Who do i have to give experience to?");
            else
                playerString = ((Player) sender).getName();
        }

        List<Player> p = plugin.findPlayerasPlayerList(playerString);
        if(p.size()!=1)
            throw new CommandException("Player not found!");
        if(ars.length<1 && level == null)
            throw new CommandException("No amount given");
        int amount = 0;
        try {
            if(level==null) amount = Integer.parseInt(ars[0]);
        } catch(NumberFormatException ex) {
            throw new CommandException("Number expected, garbage given");
        }

        Player player = p.get(0);

        if(level!=null) {
            int olevel = player.getLevel();
            int nlevel = olevel+level;
            sender.sendMessage(player.getDisplayName()+ChatColor.YELLOW+" was level "+olevel+", new level : "+nlevel);
            player.sendMessage(ChatColor.YELLOW+"Adding "+level+" levels to your total experience!");
            player.setLevel(nlevel);
        } else {
            sender.sendMessage(player.getDisplayName()+ChatColor.YELLOW+" had "+player.getTotalExperience()+ " experience, adding "+amount);
            player.sendMessage(ChatColor.YELLOW+"Adding "+amount+" to your total experience!");
            player.setTotalExperience(player.getTotalExperience()+amount);
        }
        // player.setTotalExperience(player.getTotalExperience()+amount);

        return true;
    }

    @aCommand(
        aliases = { "afk", "setafk" },
        permissionBase = "",
        description = "Manually toggle your AFK status",
        section = "general"
    )
    public boolean setAFK(CommandSender sender, String command, String[] args, TweakcraftUtils plugin)
        throws PermissionsException, CommandSenderException, CommandUsageException, CommandException {
        if(!(sender instanceof Player))
            throw new CommandSenderException("Now what do you think you're doing?");

        LocalPlayer lp = plugin.wrapPlayer((Player)sender);
        lp.setAfk(true);
        sender.sendMessage(ChatColor.YELLOW + "You are now AFK!");

        return true;
    }

    @aCommand(
        aliases = { "broadcast", "b", "broa" },
        permissionBase = "broadcast",
        description = "Broadcasts said message to the server",
        section = "general"
    )
    public boolean broadcast(CommandSender sender, String command, String[] realargs, TweakcraftUtils plugin)
        throws PermissionsException, CommandSenderException, CommandUsageException {
        if (sender instanceof Player)
            if (!plugin.check((Player) sender, "broadcast"))
                throw new PermissionsException(command);

        String message = "";
        ArgumentParser ap = new ArgumentParser(realargs);
        String grouparg = ap.getString("g", null);
        String[] groups = null;
        List<Player> recipients = new ArrayList<Player>();

        if(grouparg!=null) {
            groups = grouparg.split(",");
            for(String gr : groups) {
                for(Player p : plugin.getServer().getOnlinePlayers()) {
                    if(plugin.getPermissions().getResolver().inSingleGroup("world", gr, p)) {
                        if(!recipients.contains(p))
                            recipients.add(p);
                    }
                }
            }
        } else
            recipients = Arrays.asList(plugin.getServer().getOnlinePlayers());

        String[] args = ap.getUnusedArgs();

        if (args.length < 1) {
            throw new CommandUsageException("You did not specify a message!");
        } else {
            for(int x = 0; x < args.length; x++)
                message += args[x] + (x<args.length?" ":"");
        }

        if(recipients!=null)
            for (Player p : recipients) {
                p.sendMessage(ChatColor.RED + "[" + ChatColor.GREEN + "Broadcast" + ChatColor.RED + "] " + ChatColor.GREEN + message);
            }

        if(plugin.getConfigHandler().enableIRC && plugin.getCraftIRC()!=null && groups==null ) {
            if(plugin.getConfigHandler().GIRCenabled) {
                // String tag = ;

                // plugin.getCraftIRC(). ("[Broadcast] "+message, tag);
                RelayedMessage rm = plugin.getCraftIRC().newMsgToTag(plugin.getEndPoint(), plugin.getConfigHandler().GIRCtag, "generic");
                rm.setField("message", "[Broadcast] "+message);
                rm.post();
            }
        }
        // plugin.getLogger().info
        plugin.getLogger().info("[Broadcast] " + (groups==null?"":"("+grouparg+") ") + message);

        return true;
    }

    @aCommand(
        aliases = { "donotmount", "dnm", "nomount" },
        permissionBase = "nomount",
        description = "Toggle mounting access",
        section = "general"
    )
    public boolean doNotMount(CommandSender sender, String command, String[] args, TweakcraftUtils plugin)
        throws PermissionsException, CommandSenderException, CommandUsageException, CommandException {

        if(sender instanceof Player) {
            Player player = (Player) sender;
            boolean nomount = plugin.getPlayerListener().getNomount().contains(player.getName());
            if(nomount) {
                player.sendMessage(ChatColor.GOLD + "They can mount you again!");
                if(plugin.getConfigHandler().enablePersistence) {
                    plugin.getPlayerListener().removeNoMountPersistence(player.getName());
                }
                plugin.getPlayerListener().getNomount().remove(player.getName());
            } else {
                player.sendMessage(ChatColor.GOLD + "They can no longer mount you!");
                if(plugin.getConfigHandler().enablePersistence) {
                    plugin.getPlayerListener().addNoMountPersistence(player.getName());
                }
                plugin.getPlayerListener().getNomount().add(player.getName());
            }
        } else {
            throw new CommandSenderException("Now what were you trying to do?");
        }
        return true;
    }

    @aCommand(
        aliases = { "eject" },
        permissionBase = "",
        description = "Eject your freeloader!",
        section = "general"
    )
    public boolean eject(CommandSender sender, String command, String[] args, TweakcraftUtils plugin)
        throws PermissionsException, CommandSenderException, CommandUsageException, CommandException {
        if(sender instanceof Player) {
            Player player = (Player) sender;
            if(args.length>0 && args[0].equalsIgnoreCase("self")) {
                for(Entity ent : player.getWorld().getChunkAt(player.getLocation()).getEntities()) {
                    // Entity passenger;
                    if(ent.getPassenger() != null && ent.getPassenger().equals(player)) {
                        player.sendMessage(ChatColor.GOLD + "Ejecting yourself from whatever you were sitting on!");
                        ent.eject();
                    }
                }
            } else {
                // player.sendMessage(ChatColor.GOLD + "Ejecting whatever's on you!");
                if(player.eject()) {
                    player.sendMessage(ChatColor.GOLD + "Successfully booted stuff!");
                } else {
                    player.sendMessage(ChatColor.GOLD + "There was nothing on you!");
                }
            }
        } else {
            throw new CommandSenderException("Eject what, the server?");
        }
        return true;
    }

    @aCommand(
        aliases = { "enchant" },
        permissionBase = "enchant",
        description = "Adds an enchantment to your current item",
        section = "general"
    )
    public boolean enchant(CommandSender sender, String command, String[] args, TweakcraftUtils plugin)
        throws PermissionsException, CommandSenderException, CommandUsageException, CommandException {
        if(sender instanceof Player) {
            String permNode = "enchant";
            if(args.length>0 && args[0].equalsIgnoreCase("disenchant"))
                permNode+=".disenchant";

            if(!plugin.check((Player)sender, permNode))  {
                throw new PermissionsException(command);
            }
        } else {
            throw new CommandSenderException("What do you want to enchant today?");
        }

        if(args.length==0) {
            ItemStack is = ((Player)sender).getItemInHand();
            if(is!=null) {
                sender.sendMessage(ChatColor.YELLOW+"Enchantments for this item :");
                Map<Enchantment, Integer> enchantments = is.getEnchantments();
                if(enchantments==null) {
                    sender.sendMessage(ChatColor.YELLOW+"This item doesn't have any enchantments!");
                } else {
                    for(Enchantment ench : enchantments.keySet()) {
                        sender.sendMessage(ChatColor.YELLOW+ench.getName()+" at level "+enchantments.get(ench));
                    }
                }
            } else {
                sender.sendMessage(ChatColor.YELLOW+"You're not holding anything");
            }
            return true;
        } else if(args.length==1 && args[0].equalsIgnoreCase("disenchant")) {
            ItemStack is = ((Player)sender).getItemInHand();
            if(is!=null) {
                sender.sendMessage(ChatColor.YELLOW+"Clearing any enchantments this item had");
                for(Enchantment ench : is.getEnchantments().keySet()) {
                    is.removeEnchantment(ench);
                }
            } else {
                sender.sendMessage(ChatColor.YELLOW+"You're not holding anything");
            }
            return true;
        } else if(args.length!=2)
            throw new CommandUsageException("I need exactly 2 values, an ID and a level!");

        Integer enchantmentId = null;
        Integer enchantmentLevel = null;
        String encName = null;

        try{
            enchantmentId = Integer.parseInt(args[0]);
        } catch(NumberFormatException ex) {
            encName = args[0];
        }

        try{
            enchantmentLevel = Integer.parseInt(args[1]);
        } catch(NumberFormatException ex) {
            throw new CommandUsageException("I need numbers, not garbage!");
        }

        Enchantment enchantment = encName==null?Enchantment.getById(enchantmentId): Enchantment.getByName(encName.toUpperCase());

        if(enchantment!=null) {
            ItemStack is = ((Player)sender).getItemInHand();
            if(is==null) {
                sender.sendMessage(ChatColor.YELLOW+"You're not holding anything");
            } else {
                is.addUnsafeEnchantment(enchantment, enchantmentLevel);
                sender.sendMessage(ChatColor.YELLOW+"Adding "+enchantment.getName()+" level "+enchantmentLevel);
            }
        } else {
            sender.sendMessage(ChatColor.YELLOW+"Couldn't find Enchantment with id "+enchantmentId+"!");
        }

        return true;

    }


    @aCommand(
        aliases = { "ext", "extself" },
        permissionBase = "ext",
        description = "Extinguish yourself or someone else",
        section = "general"
    )
    public boolean extinguish(CommandSender sender, String command, String[] args, TweakcraftUtils plugin)
        throws PermissionsException, CommandSenderException, CommandUsageException {

        ExtMode modus = ExtMode.SELF;
        if (args.length > 0) {
            if (sender instanceof Player) {
                if (args[0].equalsIgnoreCase(((Player) sender).getName())) {
                    modus = ExtMode.SELF;
                } else if (args[0].equals("*")) {
                    modus = ExtMode.ALL;
                } else if (args[0].equalsIgnoreCase("mobs")) {
                    modus = ExtMode.MOBS;
                } else {
                    modus = ExtMode.OTHER;
                }
            } else {
                if (args[0].equals("*"))
                    modus = ExtMode.ALL;
                else
                    modus = ExtMode.OTHER;
            }
        }
        if (modus == ExtMode.SELF) {
            if (sender instanceof Player) {
                Player player = (Player) sender;
                if(plugin.check(player, "ext.self")) {
                    if (player.getFireTicks() != 0) {
                        player.setFireTicks(0);
                        player.sendMessage(ChatColor.YELLOW + "You have been extinguished!");
                    } else {
                        player.sendMessage(ChatColor.YELLOW + "You're not on fire!");
                    }
                } else {
                    throw new PermissionsException(command);
                }
            } else {
                sender.sendMessage("A console can't be on fire, right?");
            }
        } else if (modus == ExtMode.ALL) {
            if (sender instanceof Player) {
                if (!plugin.check((Player) sender, "extother"))
                    throw new PermissionsException(command);

                sender.sendMessage(ChatColor.YELLOW + "Throwing a bucket of water over every single player!");
                for (Player play : plugin.getServer().getOnlinePlayers()) {
                    play.setFireTicks(0);
                }
            }
        } else if (modus == ExtMode.MOBS) {
            if (sender instanceof Player) {
                if (!plugin.check((Player) sender, "ext.other"))
                    throw new PermissionsException(command);
            }

            sender.sendMessage(ChatColor.YELLOW + "Oh my god it's hell alright!");

            CreatureType type = null;
            Integer range = 0;
            Vector playervector = null;
            if (sender instanceof Player) {
                Location loc = ((Player) sender).getLocation();
                playervector = new Vector(loc.getX(), loc.getY(), loc.getZ());
            }
            if (args.length > 1) {

                String mobName = "";
                if (args[1].length() > 2)
                    mobName = args[1].substring(0, 1).toUpperCase() + args[1].substring(1, args[1].length());
                type = CreatureType.fromName(mobName);
                if (type == null)
                    throw new CommandUsageException("Can't find mob with name " + mobName + "!");
                if (args.length > 2) {
                    if (sender instanceof Player) {
                        try {
                            range = Integer.parseInt(args[2]);
                        } catch (NumberFormatException e) {
                            throw new CommandUsageException("Invalid range!");
                        }
                    } else {
                        throw new CommandSenderException("Where do i need to base myself off of now huh?");
                    }
                }

            }

            for (World w : plugin.getServer().getWorlds()) {
                for (LivingEntity ent : w.getLivingEntities()) {
                    if (ent instanceof Flying || ent instanceof Creature) {
                        if (type != null) {
                            if (type == CreatureType.fromName(ent.getClass().getCanonicalName().split("Craft")[1])) {
                                if (range != 0) // Range set, check range
                                {
                                    Location loc = ent.getLocation();
                                    Vector vec = new Vector(loc.getX(), loc.getY(), loc.getZ());

                                    if (playervector.distance(vec) < range) {
                                        ent.setFireTicks(0);
                                    }
                                } else { // No range set
                                    ent.setFireTicks(0);
                                }
                            }
                        } else { // every mob alive!
                            ent.setFireTicks(0);
                        }
                    }
                }
            }
        } else if (modus == ExtMode.OTHER) {
            if (sender instanceof Player) {
                if (!(plugin.check((Player) sender, "extother")))
                    throw new PermissionsException(command);
            }
            List<Player> p = plugin.getServer().matchPlayer(args[0]);
            if (p.size() != 1) {
                throw new CommandUsageException("Can't find the other player!");
            }
            Player player = p.get(0);
            if (player.getFireTicks() != 0) {
                player.setFireTicks(0);
                sender.sendMessage(player.getDisplayName() + ChatColor.YELLOW + " has been extinguished!");
            } else {
                sender.sendMessage(player.getDisplayName() + ChatColor.YELLOW + " isn't on fire!");
            }
        }
        return true;
    }

    private enum ExtMode {
        SELF,
        OTHER,
        ALL,
        MOBS
    }

    @aCommand(
        aliases = { "getspawn" },
        permissionBase = "getspawn",
        description = "Gets the spawn location for the world",
        section = "general"
    )
    public boolean getSpawn(CommandSender sender, String command, String[] realargs, TweakcraftUtils plugin)
        throws PermissionsException, CommandSenderException, CommandUsageException, CommandException {


        ArgumentParser ap = new ArgumentParser(realargs);
        String world = ap.getString("w", null);
        String player = ap.getString("p", null);
        String[] args = ap.getUnusedArgs();

        String permString = "getspawn";

        float x,y,z;
        Player pl = player!=null?plugin.findPlayerasPlayer(player):null;
        World w = null;
        boolean playerMode = pl!=null;

        if(sender instanceof Player) {

            // Default mode : self-spawn
            if(player==null && world==null) {
                playerMode = true;
                pl = (Player) sender;
            }

            if(playerMode && pl.getName().equals(((Player)sender).getName())) permString+=".self";
            if(!plugin.check((Player)sender, permString))
                throw new PermissionsException(command);
        }

        if(!playerMode) {
            if(world!=null)
                w = plugin.getServer().getWorld(world);
            else {
                if(sender instanceof Player)
                    w = ((Player)sender).getWorld();
            }
        }

        if(w==null && pl==null)
            throw new CommandUsageException("I didn't get a good world/player to fetch?");

        sender.sendMessage(ChatColor.YELLOW + "Spawn position for "+(w!=null?w.getName():pl.getDisplayName()));
        // Location spawn = w.getSpawnLocation();
        Location spawn = null;
        if(w  != null)  spawn = w.getSpawnLocation();
        if(pl != null) {
            spawn = pl.getBedSpawnLocation();
            if(spawn==null) {// NO BED
                throw new CommandException("That player doesn't have a custom bed spawn location.");
            }
        }

        x = Math.round((float) spawn.getX());
        y = Math.round((float) spawn.getY());
        z = Math.round((float) spawn.getZ());
        sender.sendMessage(ChatColor.YELLOW+ "X:"+x+" Y:"+y+" Z:"+z);
        if(playerMode)
            sender.sendMessage(ChatColor.YELLOW+"World : "+spawn.getWorld().getName());

        return true;
    }

    @aCommand(
        aliases = { "ignite", "igniteself" },
        permissionBase = "ignite",
        description = "Put someone on fire",
        section = "general"
    )
    public boolean ignite(CommandSender sender, String command, String[] args, TweakcraftUtils plugin)
        throws PermissionsException, CommandSenderException, CommandUsageException {
        IgniteMode modus = IgniteMode.SELF;
        if (args.length > 0) {
            if (sender instanceof Player) {
                if (args[0].equalsIgnoreCase(((Player) sender).getName())) {
                    modus = IgniteMode.SELF;
                } else if (args[0].equals("*")) {
                    modus = IgniteMode.ALL;
                } else if (args[0].equalsIgnoreCase("mobs")) {
                    modus = IgniteMode.MOBS;
                } else {
                    modus = IgniteMode.OTHER;
                }
            } else {
                if (args[0].equals("*"))
                    modus = IgniteMode.ALL;
                else if (args[0].equalsIgnoreCase("mobs"))
                    modus = IgniteMode.MOBS;
                else
                    modus = IgniteMode.OTHER;
            }
        }
        if (modus == IgniteMode.SELF) {
            if (sender instanceof Player) {
                Player player = (Player) sender;
                player.setFireTicks(1500);
                player.sendMessage(ChatColor.YELLOW + "You have been ignited!");
            } else {
                sender.sendMessage("A console can't be set on fire, right?");
            }
        } else if (modus == IgniteMode.ALL) {
            if (sender instanceof Player) {
                if (!plugin.check((Player) sender, "extother"))
                    throw new PermissionsException(command);

                sender.sendMessage(ChatColor.YELLOW + "Is it hot in here or is it just me?");
                for (Player play : plugin.getServer().getOnlinePlayers()) {
                    play.setFireTicks(300);
                }
            }
        } else if (modus == IgniteMode.MOBS) {
            if (sender instanceof Player) {
                if (!plugin.check((Player) sender, "extother"))
                    throw new PermissionsException(command);
            }

            sender.sendMessage(ChatColor.YELLOW + "Oh my god it's hell alright!");

            CreatureType type = null;
            Integer range = 0;
            Vector playervector = null;
            if (sender instanceof Player) {
                Location loc = ((Player) sender).getLocation();
                playervector = new Vector(loc.getX(), loc.getY(), loc.getZ());
            }
            if (args.length > 1) {

                String mobName = "";
                if (args[1].length() > 2)
                    mobName = args[1].substring(0, 1).toUpperCase() + args[1].substring(1, args[1].length());
                type = CreatureType.fromName(mobName);
                if (type == null)
                    throw new CommandUsageException("Can't find mob with name " + mobName + "!");
                if (args.length > 2) {
                    if (sender instanceof Player) {
                        try {
                            range = Integer.parseInt(args[2]);
                        } catch (NumberFormatException e) {
                            throw new CommandUsageException("Invalid range!");
                        }
                    } else {
                        throw new CommandSenderException("Where do i need to base myself off of now huh?");
                    }
                }

            }

            boolean allowed = true;
            for (World w : plugin.getServer().getWorlds()) {
                for (LivingEntity ent : w.getLivingEntities()) {
                    if (ent instanceof Flying || ent instanceof Creature) {
                        allowed = true;
                        if(ent instanceof Wolf)
                        {
                            Wolf wolf = (Wolf) ent;
                            if(wolf.isAngry() || !wolf.isTamed())
                                allowed = true;
                            else
                                allowed = false;
                        }

                        if (type != null) {
                            if (type == CreatureType.fromName(ent.getClass().getCanonicalName().split("Craft")[1])) {
                                if (range != 0) // Range set, check range
                                {
                                    Location loc = ent.getLocation();
                                    Vector vec = new Vector(loc.getX(), loc.getY(), loc.getZ());

                                    if (playervector.distance(vec) < range) {
                                        if(allowed)
                                            ent.setFireTicks(300);
                                    }
                                } else { // No range set
                                    if(allowed)
                                        ent.setFireTicks(300);
                                }
                            }
                        } else { // every mob alive!
                            if(allowed)
                                ent.setFireTicks(300);
                        }
                    }
                }
            }
        } else if (modus == IgniteMode.OTHER) {
            if (sender instanceof Player) {
                if (!(plugin.check((Player) sender, "extother")))
                    throw new PermissionsException(command);
            }
            List<Player> p = plugin.getServer().matchPlayer(args[0]);
            if (p.size() != 1) {
                throw new CommandUsageException("Can't find the other player!");
            }
            Player player = p.get(0);
            player.setFireTicks(1500);
            sender.sendMessage(player.getDisplayName() + ChatColor.YELLOW + " has been ignited!");
        }
        return true;
    }

    private enum IgniteMode {
        SELF,
        OTHER,
        ALL,
        MOBS
    }

    // chat.mode.local
    @aCommand(
        aliases = { "lwho", "localwho" },
        permissionBase = "chat.mode.local",
        description = "List of currently connected players in-range (localchat)",
        section = "general"
    )
    public boolean localWho(CommandSender sender, String command, String[] args, TweakcraftUtils plugin)
        throws PermissionsException, CommandSenderException, CommandUsageException, CommandException {

        if(!plugin.getConfigHandler().enableLocalChat) {
            throw new CommandUsageException("LocalChat not enabled!");
        }

        if(!(sender instanceof Player))
            throw new CommandSenderException("What do you need this for anyway?");

        // List<Player> lplayers = new ArrayList<Player>();
        String ps = "";
        Player self = (Player) sender;
        EntityLocation ploc = new EntityLocation(self);
        for(Player p : self.getWorld().getPlayers()) {
            Integer distance = ploc.getDistance(p);
            if (distance != null && distance < plugin.getConfigHandler().localchatdistance) {
                // lplayers.add(p);
                if(!(plugin.getPlayerListener().getInvisplayers().contains(p.getName()))) {
                    ps+= p.getDisplayName() + ChatColor.WHITE +", ";
                }
            }
        }
        if(ps.length()>0) ps=ps.substring(0, ps.length()-2);

        self.sendMessage(ChatColor.LIGHT_PURPLE+"Players in range ("+plugin.getConfigHandler().localchatdistance+" blocks)");
        if(ps.length()>0) self.sendMessage(ps);
        else              self.sendMessage(ChatColor.YELLOW+"No players in range!");
        return true;
    }

    @aCommand(
        aliases = { "nick" },
        permissionBase = "nick",
        description = "Give someone a nick",
        section = "general"
    )
    public boolean setNick(CommandSender sender, String command, String[] args, TweakcraftUtils plugin)
        throws PermissionsException, CommandSenderException, CommandUsageException, CommandException {
        if(sender instanceof Player)
            if(!plugin.check((Player)sender, "nick"))
                throw new PermissionsException(command);

        // Player player = (Player) sender;

        if(args.length==1 && !(sender instanceof Player))
            throw new CommandUsageException("If you're only giving me one var, give me a player!");

        if(args.length==1) {
            if(args[0].equalsIgnoreCase("reset")) {
                sender.sendMessage(ChatColor.GOLD + "Resetting nick to your real name.");
                plugin.getPlayerListener().removeNick(((Player)sender).getName());
            } else {
                sender.sendMessage(ChatColor.GOLD + "Setting nick to : "+args[0]);
                if(!plugin.getPlayerListener().nickTakenPersistance(((Player)sender).getName(), args[0])
                    && !plugin.getPlayerListener().nickTakenCheck(((Player)sender).getName(), args[0]))
                    plugin.getPlayerListener().setNick(((Player)sender).getName(), args[0]);
                else
                    throw new CommandException("Nick is already taken!");
            }
        } else if(args.length==2) {
            if(sender instanceof Player)
                if(!plugin.check((Player)sender, "nick.other"))
                    throw new PermissionsException(command);

            List<Player> search = plugin.getServer().matchPlayer(args[0]);
            if(search.size()!=1) {
                throw new CommandException("Can't find the other player!");
            } else {
                if(args[1].equalsIgnoreCase("reset")) {
                    sender.sendMessage("Resetting "+search.get(0).getName()+"'s nick");
                    plugin.getPlayerListener().removeNick(search.get(0).getName());
                } else {
                    Player otherplayer = search.get(0);
                    sender.sendMessage(ChatColor.GOLD + "Setting "+search.get(0).getName()+"'s nick to "+args[1]);
                    List<Player> find = plugin.getServer().matchPlayer(args[0]);
                    for(Player f : find) {
                        if(f.getName().toLowerCase().equals(args[1].toLowerCase()))
                            throw new CommandException("Nick is already taken!");
                    }
                    if(!plugin.getPlayerListener().nickTakenPersistance(otherplayer.getName(), args[1])
                        && !plugin.getPlayerListener().nickTakenCheck(otherplayer.getName(), args[1]))
                        plugin.getPlayerListener().setNick(otherplayer.getName(), args[1]);
                    else
                        throw new CommandException("Nick is already taken!");
                }
            }
        } else {
            throw new CommandUsageException("I need a nick!");
        }
        return true;
    }

    @aCommand(
        aliases = { "seen" },
        permissionBase = "seen",
        description = "Report the last logout time of the player",
        section = "general"
    )
    public boolean seen(CommandSender sender, String command, String[] args, TweakcraftUtils plugin)
        throws PermissionsException, CommandSenderException, CommandUsageException {
        if (args.length < 1) {
            throw new CommandUsageException("You did not specify a name!");
        }
        if (plugin.getConfigHandler().getSeenconfig() != null || plugin.getConfigHandler().enablePersistence) {
            Player pla;
            String playername = "";
            if ((pla = plugin.getServer().getPlayer(args[0])) != null) {
                sender.sendMessage(ChatColor.GOLD + pla.getName() + " is online right now!");
            } else {
                String seen = "";
                String extramsg = "";
                String newline = null;
                if(!plugin.getConfigHandler().enablePersistence) {
                    seen = plugin.getConfigHandler().getSeenconfig().getString(args[0].toLowerCase(), "");
                    playername = args[0];
                } else {
                    if(!plugin.getConfigHandler().useTweakBotSeen) {
                        PlayerInfo pi = plugin.getDatabase().find(PlayerInfo.class).where().ieq("name", args[0]).findUnique();
                        if(pi!=null) {
                            seen = pi.getLastseen().toString();
                            playername = pi.getName();
                        }
                    } else {
                        PlayerHistoryInfo phi = plugin.getDatabase().find(PlayerHistoryInfo.class).where().ieq("nickname", args[0]).findUnique();
                        if(phi!=null) {

                            playername = phi.getNickname();

                            if(phi.getChannel().equals("gameserver")) {
                                // extramsg = " (Gamequit!)";
                                newline = ChatColor.GOLD + "Gameserver quit!";
                            } else {
                                // extramsg = " (Non gameserver stuff!)";
                                if(phi.getAct().equals("privmsg")) {
                                    newline = ChatColor.GOLD +"(IRC) "+ phi.getChannel() + ChatColor.WHITE +": <"+phi.getNickname()+"> ";
                                    newline+= phi.getText();
                                } else if(phi.getAct().equals("action")) {
                                    newline = ChatColor.GOLD +"(IRC) "+phi.getChannel() + ChatColor.WHITE +": * "+phi.getNickname();
                                    newline+= phi.getText();
                                } else if(phi.getAct().equals("part")) {
                                    newline = ChatColor.GOLD +"(IRC) "+phi.getChannel() + ChatColor.WHITE +" :"+ChatColor.YELLOW+" Leaving channel";
                                } else if(phi.getAct().equals("join")) {
                                    newline = ChatColor.GOLD +"(IRC) "+phi.getChannel() + ChatColor.WHITE +" :"+ChatColor.YELLOW+" Joined channel";
                                } else if(phi.getAct().equals("quit")) {
                                    newline = ChatColor.GOLD +"(IRC) "+ "Quit client "+ ChatColor.WHITE +": "+ ChatColor.YELLOW +phi.getText()+"!";
                                } else if(phi.getAct().equals("nick")) {
                                    newline = ChatColor.GOLD + "(IRC) Set nick to " + ChatColor.WHITE+": "+ChatColor.YELLOW+ phi.getText();
                                } else {
                                    newline = ChatColor.GOLD + "Unknown other Act method, go nag GuntherDW!";
                                }
                            }
                            Long l = phi.getDate().getTime();
                            seen = l.toString();
                        }
                    }
                }
                // plugin.getSeenconfig().get
                if (seen.equals(""))
                    sender.sendMessage(ChatColor.DARK_AQUA+ "I haven't seen " + args[0] + " yet!");
                else {
                    SimpleDateFormat smf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                    Date datelastseen = new Date(Long.parseLong(seen));
                    String lastseen = smf.format(datelastseen);
                    sender.sendMessage(ChatColor.GOLD + playername + " was last seen on " + lastseen + "!"+extramsg);
                    if(newline!=null) {
                        sender.sendMessage(newline);
                    }
                }
            }
        } else {
            throw new CommandUsageException("Player history is disabled!");
        }
        return true;
    }

    @aCommand(
        aliases = { "tamer" },
        permissionBase = "tamer",
        description = "Tamer tool control",
        section = "general"
    )
    public boolean tamer(CommandSender sender, String command, String[] args, TweakcraftUtils plugin)
        throws PermissionsException, CommandSenderException, CommandUsageException, CommandException {

        if(sender instanceof Player) {
            Player player = (Player) sender;
            if(!plugin.check(player, "tamer"))
                throw new PermissionsException(command);

            TamerTool tamertool = plugin.getTamerTool();
            TamerMode mode = null;
            Boolean state = null;

            if(args.length==1 && args[0].equalsIgnoreCase("reset")) {
                if(tamertool.getTamers().containsKey(player)) {
                    tamertool.getTamers().remove(player);
                }
                player.sendMessage(ChatColor.GREEN+"Tamertool mode reset!");
                return true;
            }

            if(args.length>0 && args[0].equalsIgnoreCase("info")) {
                mode = new TamerMode(state, TamerMode.TamerModes.INFO);
                player.sendMessage(ChatColor.GREEN+"TamerTool INFO MODE selected!");
            } else if(args.length>0 && args[0].equalsIgnoreCase("tame")) {
                if(args.length>1) {
                    state = Boolean.parseBoolean(args[1]);
                }
                mode = new TamerMode(state, TamerMode.TamerModes.TAME);
                player.sendMessage(ChatColor.GREEN+"TamerTool TAME MODE selected!");
            } else if(args.length>0 && args[0].equalsIgnoreCase("angry")) {
                if(args.length>1) {
                    state = Boolean.parseBoolean(args[1]);
                }
                mode = new TamerMode(state, TamerMode.TamerModes.ANGRY);
                player.sendMessage(ChatColor.GREEN+"TamerTool ANGRY MODE selected!");
            } else if(args.length>0 && args[0].equalsIgnoreCase("heal")) {
                if(args.length>1) {
                    state = Boolean.parseBoolean(args[1]);
                }
                mode = new TamerMode(state, TamerMode.TamerModes.HEAL);
                player.sendMessage(ChatColor.GREEN+"TamerTool HEAL MODE selected!");
            } else if(args.length>0 && args[0].equalsIgnoreCase("sit")) {
                if(args.length>1) {
                    state = Boolean.parseBoolean(args[1]);
                }
                mode = new TamerMode(state, TamerMode.TamerModes.SIT);
                player.sendMessage(ChatColor.GREEN+"TamerTool SIT MODE selected!");
            } else if(args.length>0 && args[0].equalsIgnoreCase("setage")) {
                int age = -1;
                if(args.length>1) {
                    age = Integer.parseInt(args[1]);
                    if(age>0) age=0;
                }
                mode = new TamerMode(state, TamerMode.TamerModes.SETAGE);
                mode.setData(age);
                player.sendMessage(ChatColor.GREEN+"TamerTool SETAGE MODE selected! (age : "+age+")");
            } else if(args.length>0 && args[0].equalsIgnoreCase("none")) {
                mode = null;
                if(plugin.getTamerTool().getTamers().containsKey(player))
                    plugin.getTamerTool().getTamers().remove(player);
                player.sendMessage(ChatColor.GREEN+"Removed any set TamerTool mode");
                return true;
            }

            if(mode == null) {
                player.sendMessage(ChatColor.YELLOW+"Usage: /tame <info|tame|angry|heal|reset|setage> <mode|data> ");
            } else {
                plugin.getTamerTool().getTamers().put(player, mode);
            }
        } else {
            throw new CommandSenderException("Consoles aren't allowed in this party!");
        }
        return true;
    }

    @aCommand(
        aliases = { "tntarrow", "tnta" },
        permissionBase = "tntarrow",
        description = "Toggles the use of TNT Arrows.",
        section = "general"
    )
    public boolean setTNTArrow(CommandSender sender, String command, String[] args, TweakcraftUtils plugin)
        throws PermissionsException, CommandSenderException, CommandUsageException, CommandException {

        if(!plugin.getConfigHandler().enableTNTArrows)
            throw new CommandException("TNT Arrows aren't enabled!");

        if(!(sender instanceof Player))
            throw new CommandSenderException("What were you trying to do?");

        LocalPlayer lp = plugin.wrapPlayer((Player)sender);
        // Seeing as we wrapped the player, this call should just work (famous last words)
        if(!plugin.check(lp.getBukkitPlayer(), "tntarrow"))
            throw new PermissionsException("You don't have permission to use TNT Arrows");

        Boolean mode = null;
        if(args.length>0) mode = Boolean.parseBoolean(args[0]);
        if(mode==null) mode = !lp.isTntArrow();
        lp.setTntArrow(mode);
        sender.sendMessage(ChatColor.YELLOW+"TNT Arrows have been "+(mode?ChatColor.RED+"ENABLED":ChatColor.GREEN+"DISABLED"));

        return true;
    }

    @aCommand(
        aliases = { "whois" },
        permissionBase = "whois",
        description = "Find out who's behind that nick!",
        section = "general"
    )
    public boolean whois(CommandSender sender, String command, String[] args, TweakcraftUtils plugin)
        throws PermissionsException, CommandSenderException, CommandUsageException, CommandException {
        // First search for a nick
        Boolean getIP = true;

        if(sender instanceof Player) {
            if(!plugin.check((Player)sender, "whois"))
                throw new PermissionsException(command);
            if(!plugin.check((Player)sender, "whois.ip"))
                getIP = false;
        }



        if(args.length==1) {
            int level = 0;
            float exp = 0F;

            String nick_origplayer = plugin.getPlayerListener().findPlayerNameByNick(args[0]);
            String nickname = null;
            Player nick = plugin.getPlayerListener().findPlayerByNick(args[0]);
            // sender.sendMessage(nick.toString());
            String gname = null;
            // Group  g  = null;
            Player sp = findPlayer(args[0], plugin);
            Player who = nick==null?sp:nick;
            String groups = "";

            String playername = null;
            boolean online = (who!=null);

            if(who==null) { // Is it an offline player? Check permissions
                if(plugin.getPermissions().getResolver()!=null) {
                    // Check for nicks
                    // String pname = args[0];
                    OfflinePlayer offlineplayer = plugin.getServer().getOfflinePlayer(nick_origplayer!=null?nick_origplayer:args[0]);
                    /* level = offlineplayer.getPlayer().getLevel();
                exp  = offlineplayer.getPlayer().getExp(); */
                    level = 0;
                    exp = 0F;
                    String pname = offlineplayer.getName();
                    String wname = null;
                    World defworld = plugin.getServer().getWorlds().get(0);
                    if(defworld!=null) {
                        wname = defworld.getName();
                    }

                    groups = plugin.getPermissions().getResolver().getPrimaryUserGroup(wname, pname);
                    if(groups!=null) {
                        String n = plugin.getNickWithColors(pname);
                        if(n!=null) {
                            nickname = n;
                        }
                        // String prefix = plugin.getPermissionsResolver().getUserPrefix(wname, pname);
                        playername = pname;

                    }

                }
            } else {
                playername = who.getName();
                String wname = null;
                level = who.getLevel();
                exp = who.getPlayer().getExp();
                World defworld = plugin.getServer().getWorlds().get(0);
                if(defworld!=null) {
                    wname = defworld.getName();
                }

                groups = plugin.getPermissions().getResolver().getPrimaryUserGroup(wname, playername);
                // Check for a nick!
                String n = plugin.getNickWithColors(playername);
                if(n!=null) {
                    nickname = n;
                }
            }

            if(playername!=null) {
                // sender.sendMessage(ChatColor.YELLOW+"Playername : "+playername+" "+(nick!=null?"("+plugin.getNickWithColors(who.getName())+ChatColor.YELLOW+")":""));
                sender.sendMessage(ChatColor.YELLOW+"Playername : "+playername+" "+(nickname!=null?ChatColor.YELLOW+"("+nickname + ChatColor.YELLOW+")":""));
                // String group = plugin.getPermissionHandler.getG(who.getWorld().getName(), who.getName());
                sender.sendMessage(ChatColor.YELLOW+"Groups : "+groups);
                if(!getIP && online) {
                    if(((Player)sender).getName().equalsIgnoreCase(who.getName()))
                        getIP = true;
                }
                sender.sendMessage(ChatColor.YELLOW+"Level (exp) : "+level+" ("+exp+")");
                if(getIP && online)
                    sender.sendMessage(ChatColor.YELLOW + "IP: " + who.getAddress().getAddress().getHostName());
            } else {
                throw new CommandException("Can't find player!");
            }
        } else {
            throw new CommandUsageException("I need a player!");
        }
        return true;
    }

    private Player findPlayer(String playername, TweakcraftUtils plugin) {
        for(Player p : plugin.getServer().matchPlayer(playername)) {
            if(p.getName().toLowerCase().contains(playername.toLowerCase())) {
                return p;
            }
        }
        return null;
    }

}
