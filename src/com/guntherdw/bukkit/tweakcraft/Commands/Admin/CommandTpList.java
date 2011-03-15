package com.guntherdw.bukkit.tweakcraft.Commands.Admin;

import com.guntherdw.bukkit.tweakcraft.Command;
import com.guntherdw.bukkit.tweakcraft.Exceptions.CommandSenderException;
import com.guntherdw.bukkit.tweakcraft.Exceptions.PermissionsException;
import com.guntherdw.bukkit.tweakcraft.TweakcraftUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * @author GuntherDW
 */
public class CommandTpList implements Command {

    public boolean executeCommand(CommandSender sender, String command, String[] args, TweakcraftUtils plugin) throws PermissionsException, CommandSenderException {

        if (sender instanceof Player) {
            if (!plugin.check((Player) sender, "tplist"))
                throw new PermissionsException(command);
        }


        if (plugin.getDonottplist().size() != 0) {
            sender.sendMessage(ChatColor.YELLOW + "Current do-not-tp list : ");
            String color = "";
            String msg = "";
            for (String playername : plugin.getDonottplist()) {
                try {
                    color = plugin.getPlayerColor(playername, true);
                } catch (NullPointerException e) {
                    color = ChatColor.WHITE.toString();
                }
                msg = color + playername;
                sender.sendMessage(msg);
            }
        } else {
            sender.sendMessage(ChatColor.YELLOW + "Current do-not-tp list is empty!");
        }
        return true;
    }
}
