package com.guntherdw.bukkit.tweakcraft.Commands.General;

import com.guntherdw.bukkit.tweakcraft.Command;
import com.guntherdw.bukkit.tweakcraft.Exceptions.CommandSenderException;
import com.guntherdw.bukkit.tweakcraft.Exceptions.CommandUsageException;
import com.guntherdw.bukkit.tweakcraft.Exceptions.PermissionsException;
import com.guntherdw.bukkit.tweakcraft.TweakcraftUtils;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * @author GuntherDW
 */
public class CommandBroadcast implements Command {
    public boolean executeCommand(CommandSender sender, String command, String[] args, TweakcraftUtils plugin)
            throws PermissionsException, CommandSenderException, CommandUsageException {
        if(sender instanceof Player)
            if(!plugin.check((Player) sender, "broadcast"))
                throw new PermissionsException(command);
        
        String message = "";
        if(args.length < 1)
        {
            throw new CommandUsageException("You did not specify a message!");
        } else {
            for(String m : args)
            {
                message += m+" ";
            }
            message = message.substring(0, message.length()-1);
        }
        
        for(Player p : plugin.getServer().getOnlinePlayers())
        {
            p.sendMessage("[Broadcast] §4"+message);
        }

        // plugin.getLogger().info
        plugin.getLogger().info("[Broadcast] "+message);

        return true;
    }
}
