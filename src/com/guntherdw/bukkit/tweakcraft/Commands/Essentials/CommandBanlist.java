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
public class CommandBanlist implements Command {
    public boolean executeCommand(CommandSender sender, String command, String[] args, TweakcraftUtils plugin)
            throws PermissionsException, CommandSenderException, CommandUsageException, CommandException {
        if(sender instanceof Player)
            if(!plugin.check((Player) sender, "ban"))
                throw new PermissionsException(command);

        String banmsg = ChatColor.YELLOW + "Currently banned players : ";
        sender.sendMessage(banmsg);
        String banned = "";

        for(String banname : plugin.getBanhandler().getBans().keySet())
        {
            banned += banname + " ";
        }
        if(banned.length()>1)
            banned = banned.substring(0, banned.length()-1);

        sender.sendMessage(banned);
        
        return true;
    }
}
