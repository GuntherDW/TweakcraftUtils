package com.guntherdw.bukkit.tweakcraft.Commands.Essentials;

import com.guntherdw.bukkit.tweakcraft.Command;
import com.guntherdw.bukkit.tweakcraft.Exceptions.CommandException;
import com.guntherdw.bukkit.tweakcraft.Exceptions.CommandSenderException;
import com.guntherdw.bukkit.tweakcraft.Exceptions.CommandUsageException;
import com.guntherdw.bukkit.tweakcraft.Exceptions.PermissionsException;
import com.guntherdw.bukkit.tweakcraft.TweakcraftUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * @author GuntherDW
 */
public class CommandKick implements Command {
    public boolean executeCommand(CommandSender sender, String command, String[] args, TweakcraftUtils plugin)
            throws PermissionsException, CommandSenderException, CommandUsageException, CommandException {
        if(sender instanceof Player)
            if(!plugin.check((Player) sender, "kick"))
                throw new PermissionsException(command);

        String reason = "";
        Player player;
        if(args.length > 0) // No reason set!
        {
            String p = plugin.findPlayer(args[0]);
            player = plugin.getServer().getPlayer(p);
            if(player == null)
                throw new CommandUsageException("Can't find player!");
            if(args.length>1) // Reason given!
            {
                for(int x=1; x<args.length; x++)
                {
                    reason += args[x]+" ";
                }
                if(reason.length()>1)
                    reason = reason.substring(0, reason.length()-1);

            }
            player.kickPlayer(reason);
        }

        return true;
    }
}
