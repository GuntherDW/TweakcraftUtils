package com.guntherdw.bukkit.tweakcraft.Commands.Admin;

import com.guntherdw.bukkit.tweakcraft.Chat.ChatHandler;
import com.guntherdw.bukkit.tweakcraft.Chat.Modes.AdminChat;
import com.guntherdw.bukkit.tweakcraft.ChatMode;
import com.guntherdw.bukkit.tweakcraft.Command;
import com.guntherdw.bukkit.tweakcraft.Exceptions.*;
import com.guntherdw.bukkit.tweakcraft.TweakcraftUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

/**
 * @author GuntherDW
 */
public class CommandAdminRemove implements Command {
    public boolean executeCommand(CommandSender sender, String command, String[] args, TweakcraftUtils plugin)
            throws PermissionsException, CommandSenderException, CommandUsageException, CommandException {
        if(sender instanceof Player)
        {
            if(!plugin.check((Player) sender, "admon"))
                throw new PermissionsException(command);
        }
        if(args.length < 1)
        {
            throw new CommandUsageException("Give me a name to remove!");
        }

        try {
            ChatHandler ch = plugin.getChathandler();
            ChatMode cm = ch.getChatMode("admin");
            String name = plugin.findinlist(args[0], cm.getSubscribers());
            if(cm.getSubscribers().contains(name))
            {
                List<Player> p = plugin.getServer().matchPlayer(args[0]);

                Player player = null;
                String pname = name;
                String adder = "";
                if(sender instanceof Player)
                {
                    adder = plugin.getPlayerColor(((Player) sender).getName(), false)+((Player)sender).getName();
                } else {
                    adder = ChatColor.LIGHT_PURPLE + "CONSOLE";
                }
                if(p.size()==1)
                {
                    player = p.get(0);
                    name = plugin.getPlayerColor(player.getName(), false) + player.getName();
                    player.sendMessage(ChatColor.YELLOW + "You have been removed from the admin-msg list by "+adder+ChatColor.YELLOW+"!");
                }

                if(!(sender instanceof Player))
                    sender.sendMessage(name + ChatColor.YELLOW + " has been removed from the admin-msg list!");

                cm.removeRecipient(pname);

                boolean chatlist = false;
                
                if(ch.getPlayerChatModeString(pname).equals("admin"))
                {
                    ch.setPlayerchatmode(pname, null);
                    chatlist=true;
                }
                for(Player pl : ((AdminChat)cm).getAdmins())
                {
                    pl.sendMessage(adder + ChatColor.YELLOW +" removed "+ name + ChatColor.YELLOW+" from the admin-msg list!");
                    if(chatlist)
                        pl.sendMessage(ChatColor.YELLOW + "Player has also been removed from the auto-admin-msg list!");
                }
            } else {
                sender.sendMessage(ChatColor.YELLOW + "I can't find that player!");
            }



        } catch (ChatModeException e) {
            throw new CommandException("There was an error getting the admin ChatMode!");
        }
        return true;
    }
}
