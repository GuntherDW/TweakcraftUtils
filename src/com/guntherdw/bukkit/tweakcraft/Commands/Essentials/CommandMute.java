package com.guntherdw.bukkit.tweakcraft.Commands.Essentials;

import com.guntherdw.bukkit.tweakcraft.Chat.ChatHandler;
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
public class CommandMute implements Command {
    public boolean executeCommand(CommandSender sender, String command, String[] args, TweakcraftUtils plugin)
            throws PermissionsException, CommandSenderException, CommandUsageException, CommandException {
        if(sender instanceof Player)
            if(!plugin.check((Player)sender, "mute"))
                throw new PermissionsException(command);

        ChatHandler ch = plugin.getChathandler();
        if(args.length==1)
        {
            String playername = plugin.findPlayer(args[0]);
            Player player = plugin.getServer().getPlayer(playername);
            if(player!= null)
            {

                if(!ch.getMutedPlayers().contains(player.getName()))
                {
                    sender.sendMessage(ChatColor.YELLOW + "Muting "+player.getDisplayName());
                    ch.addMute(player.getName());
                } else {
                    sender.sendMessage(ChatColor.YELLOW + "Unmuting "+player.getDisplayName());
                    ch.removeMute(player.getDisplayName());
                }
            } else {
                sender.sendMessage(ChatColor.YELLOW + "Can't find player!");
            }
        } else {
            sender.sendMessage(ChatColor.YELLOW+"Now who on earth do i have to mute?");
        }

        return true;
    }
}
