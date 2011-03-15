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
public class CommandAdminAdd implements Command {
        public boolean executeCommand(CommandSender sender, String command, String[] args, TweakcraftUtils plugin)
            throws PermissionsException, CommandSenderException, CommandException, CommandUsageException {
        if(sender instanceof Player)
        {
            if(!plugin.check((Player) sender, "admon"))
                throw new PermissionsException(command);
        }
        if(args.length < 1)
        {
            throw new CommandUsageException("Give me a name to add!");
        }

        try {
            ChatMode cm = plugin.getChathandler().getChatMode("admin");
            Player player = plugin.getServer().getPlayer(args[0]);
            if(player!=null)
            {
                cm.addRecipient(player.getName());
                String addedplayer = plugin.getPlayerColor(player.getName(), false)+player.getName();
                if(!(sender instanceof Player))
                    sender.sendMessage(addedplayer +ChatColor.YELLOW + " has been added to the admin-msg list!");
                String adder = "";
                if(sender instanceof Player)
                {
                    adder = plugin.getPlayerColor(((Player) sender).getName(), false)+((Player)sender).getName();
                } else {
                    adder = ChatColor.LIGHT_PURPLE + "CONSOLE";
                }

                player.sendMessage(ChatColor.YELLOW + "You have been added to the admin-msg list by "+adder+ChatColor.YELLOW+"!");
                for(Player p : ((AdminChat)cm).getAdmins())
                {
                    p.sendMessage(adder + ChatColor.YELLOW + " added "+ addedplayer + ChatColor.YELLOW+" to the admin-msg list!");
                }
            } else {
                throw new CommandException("Can't find player!");
            }

        } catch (ChatModeException e) {
            throw new CommandException("There was an error getting the admin ChatMode!");
        }
        return true;
    }
}
