package com.guntherdw.bukkit.tweakcraft.Commands.Admin;

import com.guntherdw.bukkit.tweakcraft.Chat.ChatHandler;
import com.guntherdw.bukkit.tweakcraft.ChatMode;
import com.guntherdw.bukkit.tweakcraft.Command;
import com.guntherdw.bukkit.tweakcraft.Exceptions.*;
import com.guntherdw.bukkit.tweakcraft.TweakcraftUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * @author GuntherDW
 */
public class CommandAdminList implements Command {
    public boolean executeCommand(CommandSender sender, String command, String[] args, TweakcraftUtils plugin) throws PermissionsException, CommandSenderException, CommandUsageException, CommandException {
        if (sender instanceof Player) {
            if (!plugin.check((Player) sender, "adminlist"))
                throw new PermissionsException(command);
        }
        try {
            ChatHandler ch = plugin.getChathandler();
            ChatMode cm = ch.getChatMode("admin");
            if (plugin.getDonottplist().size() != 0) {
                sender.sendMessage(ChatColor.YELLOW + "Current admin-msg subscriber list : ");
                String color = "";
                String msg = "";
                for (String playername : cm.getSubscribers()) {
                    try {
                        color = plugin.getPlayerColor(playername, true);
                    } catch (NullPointerException e) {
                        color = ChatColor.WHITE.toString();
                    }
                    msg = color + playername;
                    sender.sendMessage(msg);
                }

            } else {
                sender.sendMessage(ChatColor.YELLOW + "Current admin-msg subscriber list is empty!");
            }
        } catch (ChatModeException e) {
            throw new CommandException("Exception thrown while fetching ChatMode!");
        }
        return true;
    }
}
