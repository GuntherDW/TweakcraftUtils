package com.guntherdw.bukkit.tweakcraft.Commands.Essentials;

import com.guntherdw.bukkit.tweakcraft.Command;
import com.guntherdw.bukkit.tweakcraft.Exceptions.CommandException;
import com.guntherdw.bukkit.tweakcraft.Exceptions.CommandSenderException;
import com.guntherdw.bukkit.tweakcraft.Exceptions.CommandUsageException;
import com.guntherdw.bukkit.tweakcraft.Exceptions.PermissionsException;
import com.guntherdw.bukkit.tweakcraft.TweakcraftUtils;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.*;
import org.bukkit.util.Vector;

import java.util.HashSet;
import java.util.List;


/**
 * @author GuntherDW
 */
public class CommandSpawnmob implements Command {
    public boolean executeCommand(CommandSender sender, String command, String[] args, TweakcraftUtils plugin)
            throws PermissionsException, CommandSenderException, CommandUsageException, CommandException {
        if(sender instanceof Player)
        {
            Player player = (Player) sender;
            if(!plugin.check(player, "spawnmob"))
                throw new PermissionsException(command);

            Location loc = player.getTargetBlock(null, 200).getLocation();
            loc.setY(loc.getY()+1); // Do not spawn them into the ground, silly!
            String mobName;
            String mobRider;
            Integer amount = 1;
            String victim;
            CreatureType type = null;
            CreatureType rider = null;
            Player victimplayer = player;

            if(args.length > 0) // only a mobname!
            {
                // mobName = args[0];
                mobName = "";
                amount = 1;
                if(args[0].length()>2)
                        mobName = args[0].substring(0, 1).toUpperCase() + args[0].substring(1, args[0].length());
                type = CreatureType.fromName(mobName);
                if(type == null)
                {
                    throw new CommandUsageException("Can't find that creature!");
                }
                /* if(args.length > 1) // amount or rider
                {
                    try {
                        amount = Integer.parseInt(args[1]);
                    } catch(NumberFormatException e) {
                        amount = 1;
                        mobRider = args[1];
                        rider = CreatureType.fromName(mobRider);
                        if(rider == null)
                        {
                            throw new CommandUsageException("Can't find rider creature!");
                        }
                    }
                } */
                if(args.length > 1) // amount
                {
                    try {
                        amount = Integer.parseInt(args[1]);
                    } catch(NumberFormatException e) {
                        throw new CommandUsageException("I need an amount, not a string!");
                    }
                }
                if(args.length > 2) // victim!
                {
                    victim = plugin.findPlayer(args[2]);
                    victimplayer = plugin.getServer().getPlayer(victim);
                    if(victimplayer == null)
                    {
                        throw new CommandUsageException("Can't find that player!");
                    }
                    loc = victimplayer.getLocation();
                }

                // We're finally here
                // Creature crea = new
                if(type != null)
                {
                    for(int x=0;x<amount;x++)
                        victimplayer.getWorld().spawnCreature(loc, type);
                }
                else
                {
                    sender.sendMessage(ChatColor.YELLOW + "Error trying to spawn creature!");
                }
            }
        } else {
            throw new CommandSenderException("What were you trying to do? :3");
        }
        return true;
    }
}
