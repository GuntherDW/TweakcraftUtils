package com.guntherdw.bukkit.tweakcraft.Commands.Essentials;

import com.guntherdw.bukkit.tweakcraft.Ban.BanHandler;
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
public class CommandUnban implements Command {
    public boolean executeCommand(CommandSender sender, String command, String[] args, TweakcraftUtils plugin)
            throws PermissionsException, CommandSenderException, CommandUsageException, CommandException {
        if(sender instanceof Player)
            if(!plugin.check((Player)sender, "ban"))
                throw new PermissionsException(command);
        BanHandler handler = plugin.getBanhandler();
        if(args.length>0)
        {
            String target = args[0];
            if(handler.isBanned(target))
            {
                sender.sendMessage(ChatColor.YELLOW + "Unbanning player!");
                handler.unBanPlayer(target);
                handler.saveBans();
            } else {
                sender.sendMessage(ChatColor.YELLOW + "That player isn't banned!");
            }

        }
        return true;
    }
}
