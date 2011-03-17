package com.guntherdw.bukkit.tweakcraft.Commands.General;

import com.guntherdw.bukkit.tweakcraft.Command;
import com.guntherdw.bukkit.tweakcraft.Exceptions.CommandSenderException;
import com.guntherdw.bukkit.tweakcraft.Exceptions.CommandUsageException;
import com.guntherdw.bukkit.tweakcraft.Exceptions.PermissionsException;
import com.guntherdw.bukkit.tweakcraft.TweakcraftUtils;
import com.sun.org.apache.xpath.internal.axes.SelfIteratorNoPredicate;
import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;


/**
 * @author GuntherDW
 */
public class CommandExt implements Command {

    public boolean executeCommand(CommandSender sender, String command, String[] args, TweakcraftUtils plugin)
            throws PermissionsException, CommandSenderException, CommandUsageException {

        ExtMode modus = ExtMode.SELF;
        if(args.length > 0)
        {
            if(sender instanceof Player)
            {
                if(args[0].equalsIgnoreCase(((Player)sender).getName()))
                {
                    modus = ExtMode.SELF;
                } else if(args[0].equals("*")) {
                    modus = ExtMode.ALL;
                } else {
                    modus = ExtMode.OTHER;
                }
            } else {
                if(args[0].equals("*"))
                    modus = ExtMode.ALL;
                else
                    modus = ExtMode.OTHER;
            }
        }
        if(modus == ExtMode.SELF)
        {
            if(sender instanceof Player)
            {
                Player player = (Player) sender;
                if(player.getFireTicks() != 0)
                {
                    player.setFireTicks(0);
                    player.sendMessage(ChatColor.YELLOW + "You have been extinguished!");
                } else {
                    player.sendMessage(ChatColor.YELLOW + "You're not on fire!");
                }
            } else {
                sender.sendMessage("A console can't be on fire, right?");
            }
        } else if(modus == ExtMode.ALL) {
            if(sender instanceof Player)
            {
                if(!plugin.check((Player)sender, "extother"))
                    throw new PermissionsException(command);

                sender.sendMessage(ChatColor.YELLOW + "Throwing a bucket of water over every single player!");
                for(Player play : plugin.getServer().getOnlinePlayers())
                {
                    play.setFireTicks(0);
                }
            }
        } else if(modus == ExtMode.OTHER) {
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
            if(player.getFireTicks()!=0)
            {
                player.setFireTicks(0);
                sender.sendMessage(plugin.getPlayerColor(player.getName(), false)+player.getName()
                                    +ChatColor.YELLOW+" has been extinguished!");
            } else {
                sender.sendMessage(plugin.getPlayerColor(player.getName(), false)+player.getName()
                                    +ChatColor.YELLOW+" isn't on fire!");
            }
        }
        return true;
    }

    private enum ExtMode
    {
        SELF,
        OTHER,
        ALL
    }

}