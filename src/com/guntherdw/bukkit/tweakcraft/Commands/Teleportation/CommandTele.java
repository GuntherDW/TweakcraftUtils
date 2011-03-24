package com.guntherdw.bukkit.tweakcraft.Commands.Teleportation;

import com.guntherdw.bukkit.tweakcraft.Command;
import com.guntherdw.bukkit.tweakcraft.Exceptions.CommandSenderException;
import com.guntherdw.bukkit.tweakcraft.Exceptions.CommandUsageException;
import com.guntherdw.bukkit.tweakcraft.Exceptions.PermissionsException;
import com.guntherdw.bukkit.tweakcraft.TweakcraftUtils;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * @author GuntherDW
 */
public class CommandTele implements Command {

    public boolean executeCommand(CommandSender sender, String command, String[] args, TweakcraftUtils plugin)
            throws PermissionsException, CommandSenderException, CommandUsageException {
        if (sender instanceof Player) {
            if (!plugin.check((Player) sender, "tele")) {
                throw new PermissionsException(command);
            }
            if (args.length < 2) {
                throw new CommandUsageException("I need two or more coordinates!");
            }

            Integer x, y, z;
            World world;

            x = Integer.parseInt(args[0]);
            z = Integer.parseInt(args[1]);
            if (args.length == 3) {
                y = Integer.parseInt(args[2]);
            } else {
                y = 129;
            }
            if (args.length == 4) {
                try {
                    if (plugin.getServer().getWorlds().contains(plugin.getServer().getWorld(args[3]))) {
                        world = plugin.getServer().getWorld(args[3]);
                    } else {
                        world = ((Player) sender).getWorld();
                    }
                } catch (Exception e) {
                    world = ((Player) sender).getWorld();
                }
            } else {
                world = ((Player) sender).getWorld();
            }
            Location loc = new Location(world, x.doubleValue(), y.doubleValue(), z.doubleValue());
            ((Player) sender).teleport(loc);

        } else {
            throw new CommandSenderException("You need to be a player to teleport!");
        }
        return true;
    }
}
