package com.guntherdw.bukkit.tweakcraft.Commands.Essentials;

import com.guntherdw.bukkit.tweakcraft.Command;
import com.guntherdw.bukkit.tweakcraft.Exceptions.CommandException;
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
public class CommandMotd implements Command {
    public boolean executeCommand(CommandSender sender, String command, String[] args, TweakcraftUtils plugin)
            throws PermissionsException, CommandSenderException, CommandUsageException, CommandException {
        if(sender instanceof Player)
        {
             if(args.length == 1 && args[0].equalsIgnoreCase("reload"))
             {
                 if(plugin.check((Player) sender, "motdreload"))
                 {
                     plugin.reloadMOTD();
                     sender.sendMessage(ChatColor.YELLOW + "Reloading MOTD");
                 } else {
                     throw new PermissionsException(command);
                 }
             } else {
                 for(String motdline : plugin.getMOTD())
                 {
                     sender.sendMessage(motdline);
                 }
             }
        } else {
            // consoles?
            throw new CommandSenderException("Why do consoles need motds?");
        }
        return true;
    }
}
