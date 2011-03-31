package com.guntherdw.bukkit.tweakcraft.Commands.Admin;

import com.guntherdw.bukkit.tweakcraft.Command;
import com.guntherdw.bukkit.tweakcraft.Exceptions.CommandException;
import com.guntherdw.bukkit.tweakcraft.Exceptions.CommandSenderException;
import com.guntherdw.bukkit.tweakcraft.Exceptions.CommandUsageException;
import com.guntherdw.bukkit.tweakcraft.Exceptions.PermissionsException;
import com.guntherdw.bukkit.tweakcraft.TweakcraftUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

/**
 * @author GuntherDW
 */
public class CommandTC implements Command {
    public boolean executeCommand(CommandSender sender, String command, String[] args, TweakcraftUtils plugin)
            throws PermissionsException, CommandSenderException, CommandUsageException, CommandException {
        if(args.length>0)
        {
            if(args[0].equalsIgnoreCase("version"))
            {
                sender.sendMessage(ChatColor.WHITE+plugin.getDescription().getName() + ": version "+ChatColor.GREEN+plugin.getDescription().getVersion());
            } else if(args[0].equalsIgnoreCase("reload")) {
                sender.sendMessage(ChatColor.GREEN+"Not implemented yet!");
            }
        } else {
            throw new CommandUsageException("/tc <"+ ChatColor.GREEN+"reload"+ChatColor.YELLOW+"/"+ChatColor.GREEN+"version"+ChatColor.YELLOW+">");
        }
        return true;
    }
}
