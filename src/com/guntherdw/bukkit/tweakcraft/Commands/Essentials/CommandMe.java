package com.guntherdw.bukkit.tweakcraft.Commands.Essentials;

import com.guntherdw.bukkit.tweakcraft.Chat.ChatHandler;
import com.guntherdw.bukkit.tweakcraft.Chat.Modes.AdminChat;
import com.guntherdw.bukkit.tweakcraft.Chat.Modes.LocalChat;
import com.guntherdw.bukkit.tweakcraft.ChatMode;
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
public class CommandMe implements Command {
    public boolean executeCommand(CommandSender sender, String command, String[] args, TweakcraftUtils plugin)
            throws PermissionsException, CommandSenderException, CommandUsageException, CommandException {
        if(sender instanceof Player)
        {
            Player player = (Player) sender;
            ChatHandler ch = plugin.getChathandler();
            if(ch.getMutedPlayers().contains(player.getName()))
            {
                sender.sendMessage(ChatColor.RED + "What were you trying to do?");
            } else {
                if(args.length>0)
                {
                    ChatMode cm = ch.getPlayerChatMode(player);
                    String msg = "";
                        for (String m : args)
                            msg += m + " ";
                        msg = msg.substring(0, msg.length()-1);
                    if(cm == null)
                    {
                        plugin.getServer().broadcastMessage("* "+player.getDisplayName()+" "+msg);
                    } else if(cm instanceof LocalChat) {
                        ((LocalChat) cm).broadcastMessage(player, "["+ChatColor.YELLOW+"L"+ChatColor.WHITE+"] * "+player.getDisplayName()+" "+msg);
                    } else if(cm instanceof AdminChat) {
                        ((AdminChat) cm).broadcastMessage(player, ChatColor.GREEN+"ADMIN"+ChatColor.WHITE+" * "+player.getDisplayName()+" "+ChatColor.GREEN+msg);
                    }
                }

            }
        } else {
            throw new CommandSenderException("Now why on earth...");
        }
        return true;
    }
}
