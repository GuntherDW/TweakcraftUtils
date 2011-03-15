package com.guntherdw.bukkit.tweakcraft.Commands.Teleportation;

import com.guntherdw.bukkit.tweakcraft.Command;
import com.guntherdw.bukkit.tweakcraft.Exceptions.CommandSenderException;
import com.guntherdw.bukkit.tweakcraft.Exceptions.CommandUsageException;
import com.guntherdw.bukkit.tweakcraft.Exceptions.PermissionsException;
import com.guntherdw.bukkit.tweakcraft.TweakcraftUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * @author GuntherDW
 */
public class CommandTpOff  implements Command {

    public boolean executeCommand(CommandSender sender, String command, String[] args, TweakcraftUtils plugin)
            throws PermissionsException, CommandSenderException, CommandUsageException {
        if(sender instanceof Player)
        {
            if(!plugin.check((Player)sender, "tpoff"))
                throw new PermissionsException(command);

            if (args.length != 0 && !args[0].equalsIgnoreCase(((Player)sender).getName())) {
                if(!plugin.check((Player)sender, "tpoffother"))
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
        } else if(args.length == 1) {
            this.tpoff(plugin, sender, args[0]);
        } else {
            throw new CommandSenderException("Why do you need tpon for the console?");
        }

        return true;
    }

    private void tpoff(TweakcraftUtils plugin, CommandSender sender, String player)
    {
        String playername = plugin.findPlayer(player);
        if(!plugin.getDonottplist().contains(playername))
        {
            plugin.getDonottplist().add(playername);
            sender.sendMessage(ChatColor.GREEN + "They can no longer tp to "+playername+"!");
        } else {
            sender.sendMessage(ChatColor.GREEN + playername + " already is on the do-not-tp list!");
        }
    }

}
