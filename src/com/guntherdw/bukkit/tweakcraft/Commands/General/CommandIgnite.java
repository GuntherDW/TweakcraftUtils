package com.guntherdw.bukkit.tweakcraft.Commands.General;

import com.guntherdw.bukkit.tweakcraft.Command;
import com.guntherdw.bukkit.tweakcraft.Exceptions.CommandSenderException;
import com.guntherdw.bukkit.tweakcraft.Exceptions.CommandUsageException;
import com.guntherdw.bukkit.tweakcraft.Exceptions.PermissionsException;
import com.guntherdw.bukkit.tweakcraft.TweakcraftUtils;
import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

/**
 * @author GuntherDW
 */
public class CommandIgnite implements Command {

    public boolean executeCommand(CommandSender sender, String command, String[] args, TweakcraftUtils plugin)
            throws PermissionsException, CommandSenderException, CommandUsageException {
        IngiteMode modus = IngiteMode.SELF;
        if(args.length > 0)
        {
            if(sender instanceof Player)
            {
                if(args[0].equalsIgnoreCase(((Player)sender).getName()))
                {
                    modus = IngiteMode.SELF;
                }
            } else {
                    modus = IngiteMode.OTHER;
            }
        }
        if(modus == IngiteMode.SELF)
        {
            if(sender instanceof Player)
            {
                Player player = (Player) sender;
                player.setFireTicks(1500);
                player.sendMessage(ChatColor.YELLOW+"You have been ignited!");
            } else {
                sender.sendMessage("A console can't be on fire, right?");
            }
        } else {
            if(sender instanceof Player)
            {
                if(!(plugin.check((Player)sender, "extother")))
                    throw new PermissionsException(command);
            }
            List<Player> p = plugin.getServer().matchPlayer(args[0]);
            if(p.size()!=1)
            {
                throw new CommandUsageException("Can't find the other player!");
            }
            Player player = p.get(0);
            player.setFireTicks(1500);
            sender.sendMessage(plugin.getPlayerColor(player.getName(), false)+player.getName()
                                    +ChatColor.YELLOW+" has been ignited!");
        }
        return true;
    }

    private enum IngiteMode
    {
        SELF,
        OTHER;
    }
}
