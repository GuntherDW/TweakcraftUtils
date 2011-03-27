package com.guntherdw.bukkit.tweakcraft.Commands.Essentials;

import com.guntherdw.bukkit.tweakcraft.Ban.BanHandler;
import com.guntherdw.bukkit.tweakcraft.Command;
import com.guntherdw.bukkit.tweakcraft.Exceptions.CommandException;
import com.guntherdw.bukkit.tweakcraft.Exceptions.CommandSenderException;
import com.guntherdw.bukkit.tweakcraft.Exceptions.CommandUsageException;
import com.guntherdw.bukkit.tweakcraft.Exceptions.PermissionsException;
import com.guntherdw.bukkit.tweakcraft.Packages.Ban;
import com.guntherdw.bukkit.tweakcraft.TweakcraftUtils;
import org.bukkit.ChatColor;
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
        BanHandler handler = plugin.getBanhandler();
        if(args.length<1)
            throw new CommandUsageException(ChatColor.YELLOW + "I need at leat 1 name to ban!");
        if(handler.isBanned(args[0])) {
            sender.sendMessage(ChatColor.YELLOW + "This player is already banned!");
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
            
            handler.banPlayer(playername, reason);
            sender.sendMessage(ChatColor.YELLOW + "Banning "+playername+"!");
            
            Player player = plugin.getServer().getPlayer(plugin.findPlayer(playername));
            if(player != null)
            {
                sender.sendMessage(ChatColor.YELLOW + "Kickbanning "+player.getName());
                player.kickPlayer(reason);
            }
            plugin.getLogger().info("[TweakcraftUtils] Banning "+playername+"!");
            handler.saveBans();
        }
        return true;
    }
}
