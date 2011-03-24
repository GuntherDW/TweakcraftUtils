package com.guntherdw.bukkit.tweakcraft.Commands.Teleportation;

import com.guntherdw.bukkit.tweakcraft.Command;
import com.guntherdw.bukkit.tweakcraft.Exceptions.CommandSenderException;
import com.guntherdw.bukkit.tweakcraft.Exceptions.CommandUsageException;
import com.guntherdw.bukkit.tweakcraft.Exceptions.PermissionsException;
import com.guntherdw.bukkit.tweakcraft.TweakcraftUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

/**
 * @author GuntherDW
 */
public class CommandTphere implements Command {

    public boolean executeCommand(CommandSender sender, String command, String[] args, TweakcraftUtils plugin)
            throws PermissionsException, CommandSenderException, CommandUsageException {
        if (sender instanceof Player) {
            if (!plugin.check((Player) sender, "tphere"))
                throw new PermissionsException(command);

            if (args.length < 1) {
                throw new CommandUsageException("You need to give me a name!");
            }
            Player player = (Player) sender;
            List<Player> p = plugin.getServer().matchPlayer(args[0]);
            if(p.size()<1)
            {
                player.sendMessage(ChatColor.YELLOW + "Can't find player!");
            } else {
                Player pto = p.get(0);
                if (pto.getName().equals(player.getName())) {
                    player.sendMessage(ChatColor.YELLOW + "Now look at that, you've teleported yourself to yourself");
                } else {
                    player.sendMessage(ChatColor.YELLOW + "Teleporting "+plugin.getPlayerColor(pto.getName(), false)
                            +pto.getName()+ChatColor.YELLOW + " to you!");
                    pto.sendMessage(plugin.getPlayerColor(player.getName(), false)+player.getName() + ChatColor.YELLOW
                            +" teleported you to him!");
                    pto.teleport(player);
                }
            }
        } else {
            throw new CommandSenderException("You need to be player to teleport someone to you!");
        }

        return true;

    }

}
