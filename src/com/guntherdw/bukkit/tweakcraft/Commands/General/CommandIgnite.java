package com.guntherdw.bukkit.tweakcraft.Commands.General;

import com.guntherdw.bukkit.tweakcraft.Command;
import com.guntherdw.bukkit.tweakcraft.Exceptions.CommandSenderException;
import com.guntherdw.bukkit.tweakcraft.Exceptions.CommandUsageException;
import com.guntherdw.bukkit.tweakcraft.Exceptions.PermissionsException;
import com.guntherdw.bukkit.tweakcraft.TweakcraftUtils;
import com.sk89q.worldedit.Vector;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.*;

import java.util.List;

/**
 * @author GuntherDW
 */
public class CommandIgnite implements Command {

    public boolean executeCommand(CommandSender sender, String command, String[] args, TweakcraftUtils plugin)
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
            if(sender instanceof Player)
            {
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
                    if(sender instanceof Player)
                    {
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
                        if(type != null)
                        {
                            if(type == CreatureType.fromName(ent.getClass().getCanonicalName().split("Craft")[1]))
                            {
                                if(range != 0) // Range set, check range
                                {
                                    Location loc = ent.getLocation();
                                    Vector   vec = new Vector(loc.getX(), loc.getY(), loc.getZ());

                                    if(playervector.distance(vec) < range)
                                    {
                                        ent.setFireTicks(300);
                                    }
                                } else { // No range set
                                    ent.setFireTicks(300);
                                }
                            }
                        } else { // every mob alive!
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
}
