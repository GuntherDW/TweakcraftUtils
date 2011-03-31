package com.guntherdw.bukkit.tweakcraft.Commands.Essentials;

import com.guntherdw.bukkit.tweakcraft.Command;
import com.guntherdw.bukkit.tweakcraft.Exceptions.CommandException;
import com.guntherdw.bukkit.tweakcraft.Exceptions.CommandSenderException;
import com.guntherdw.bukkit.tweakcraft.Exceptions.CommandUsageException;
import com.guntherdw.bukkit.tweakcraft.Exceptions.PermissionsException;
import com.guntherdw.bukkit.tweakcraft.TweakcraftUtils;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

/**
 * @author GuntherDW
 */
public class CommandWorld implements Command {
    public boolean executeCommand(CommandSender sender, String command, String[] args, TweakcraftUtils plugin)
            throws PermissionsException, CommandSenderException, CommandUsageException, CommandException {
        if(sender instanceof Player)
        {
            Player player = (Player) sender;
            if(args.length>0)
            {
                List<World> worlds = plugin.getServer().getWorlds();
                String worldname = args[0];
                Integer worldnum;
                World world;
                try
                {
                    worldnum = Integer.parseInt(worldname);
                    world = worlds.get(worldnum);
                } catch(NumberFormatException e) {
                    world = plugin.getServer().getWorld(worldname);
                } catch(IndexOutOfBoundsException e) {
                    throw new CommandUsageException(ChatColor.YELLOW+"Can't find that world!");
                }
                if(world != null)
                {
                    player.teleport(world.getSpawnLocation());
                } else {
                    sender.sendMessage(ChatColor.YELLOW+"Can't find that world!");
                }
            } else {
                throw new CommandUsageException("I need a world to tp you to!");
            }
        } else {
            throw new CommandSenderException("What do you think you are doing?");
        }
        return true;
    }
}
