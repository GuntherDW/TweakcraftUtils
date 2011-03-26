package com.guntherdw.bukkit.tweakcraft.Commands.Essentials;

import com.guntherdw.bukkit.tweakcraft.Command;
import com.guntherdw.bukkit.tweakcraft.Exceptions.CommandException;
import com.guntherdw.bukkit.tweakcraft.Exceptions.CommandSenderException;
import com.guntherdw.bukkit.tweakcraft.Exceptions.CommandUsageException;
import com.guntherdw.bukkit.tweakcraft.Exceptions.PermissionsException;
import com.guntherdw.bukkit.tweakcraft.Packages.Ban;
import com.guntherdw.bukkit.tweakcraft.TweakcraftUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * @author GuntherDW
 */
public class CommandBan implements Command {
    public boolean executeCommand(CommandSender sender, String command, String[] args, TweakcraftUtils plugin)
            throws PermissionsException, CommandSenderException, CommandUsageException, CommandException {
        if(sender instanceof Player)
            if(!plugin.check((Player) sender, "ban"))
                throw new PermissionsException(command);
        if(args.length<1)
            throw new CommandUsageException("I need at leat 1 name to ban!");
        if(plugin.getBanhandler().getBans().containsKey(args[0])) {
            sender.sendMessage("This player is already banned!");
        } else {
            String reason = "";
            String playername = args[0];
            if(args.length > 1)
            {
                for(int x=1; x<args.length; x++)
                {
                    reason += args[x]+" ";
                }
                if(reason.length()>1)
                    reason = reason.substring(0, reason.length()-1);
            }
            
            plugin.getBanhandler().banPlayer(playername, reason);
            sender.sendMessage("Banning "+playername+"!");
            
            Player player = plugin.getServer().getPlayer(plugin.findPlayer(playername));
            if(player != null)
            {
                sender.sendMessage("Kickbanning "+player.getName());
                player.kickPlayer(reason);
            }
        }
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
