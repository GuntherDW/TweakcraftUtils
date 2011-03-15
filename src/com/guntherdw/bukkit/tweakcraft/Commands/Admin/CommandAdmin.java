package com.guntherdw.bukkit.tweakcraft.Commands.Admin;

import com.guntherdw.bukkit.tweakcraft.Chat.Modes.AdminChat;
import com.guntherdw.bukkit.tweakcraft.ChatMode;
import com.guntherdw.bukkit.tweakcraft.Command;
import com.guntherdw.bukkit.tweakcraft.Exceptions.*;
import com.guntherdw.bukkit.tweakcraft.TweakcraftUtils;
import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * @author GuntherDW
 */
public class CommandAdmin implements Command {
    public boolean executeCommand(CommandSender sender, String command, String[] args, TweakcraftUtils plugin)
            throws PermissionsException, CommandSenderException, CommandUsageException, CommandException {
        boolean onlist = false;
        try {
            ChatMode cm = plugin.getChathandler().getChatMode("admin");
            String msg = "";
            for (String m : args)
                msg += m + " ";
            msg = msg.substring(0, msg.length()-1);

            if(sender instanceof Player)
            {
                onlist = (cm.getSubscribers().contains(((Player) sender).getName())
                        || ((AdminChat)cm).getAdminsString().contains(((Player) sender).getName()));
            } else {
                onlist=true;
            }

            if(!onlist)
            {
                sender.sendMessage(ChatColor.GREEN + "Message sent to admins:");
            }
            cm.sendMessage(sender, msg);
        } catch(ChatModeException e)
        {
            throw new CommandException("Error occurred while trying to get ChatMode!");
        }
        return true;
    }
}
