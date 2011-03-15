package com.guntherdw.bukkit.tweakcraft.Commands.General;

import com.guntherdw.bukkit.tweakcraft.Command;
import com.guntherdw.bukkit.tweakcraft.Exceptions.CommandSenderException;
import com.guntherdw.bukkit.tweakcraft.Exceptions.CommandUsageException;
import com.guntherdw.bukkit.tweakcraft.Exceptions.PermissionsException;
import com.guntherdw.bukkit.tweakcraft.TweakcraftUtils;
import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * @author GuntherDW
 */
public class CommandWho implements Command {

    public boolean executeCommand(CommandSender sender, String command, String[] args, TweakcraftUtils plugin)
            throws PermissionsException, CommandSenderException, CommandUsageException {

            List<Player> list = Arrays.asList(plugin.getServer().getOnlinePlayers());
            String msg = ChatColor.LIGHT_PURPLE + "Player list (" + list.size() + "/" + plugin.getServer().getMaxPlayers() + "): ";
            String toadd;

            // player.

            Collections.sort(list, new Comparator<Player>() {
                public int compare(Player p1, Player p2) {
                    return p1.getName().compareToIgnoreCase(p2.getName());
                }
            });

            sender.sendMessage(msg);
            msg = " ";
            for (Player p : list) {
                toadd = plugin.getPlayerColor(p.getName(), false) + p.getName() + ChatColor.WHITE + ", ";
                msg += toadd;
            }
            if (!msg.trim().isEmpty()) {
                sender.sendMessage(msg.substring(0, msg.length() - 2));
            }


            return true;
    }
}
