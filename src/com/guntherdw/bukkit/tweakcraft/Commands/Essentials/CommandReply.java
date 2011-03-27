package com.guntherdw.bukkit.tweakcraft.Commands.Essentials;

import com.guntherdw.bukkit.tweakcraft.Command;
import com.guntherdw.bukkit.tweakcraft.Exceptions.CommandException;
import com.guntherdw.bukkit.tweakcraft.Exceptions.CommandSenderException;
import com.guntherdw.bukkit.tweakcraft.Exceptions.CommandUsageException;
import com.guntherdw.bukkit.tweakcraft.Exceptions.PermissionsException;
import com.guntherdw.bukkit.tweakcraft.TweakcraftUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * @author GuntherDW
 */
public class CommandReply implements Command {
    public boolean executeCommand(CommandSender sender, String command, String[] args, TweakcraftUtils plugin)
            throws PermissionsException, CommandSenderException, CommandUsageException, CommandException {
        if (sender instanceof Player) {
            if (args.length > 0) {
                Player player = (Player) sender;
                String replyto = plugin.getPlayerReply(((Player) sender).getName());
                // String replyto = player.getName();

                String message = "";
                for (int x = 0; x < args.length; x++) {
                    message += args[x]+ " ";
                }
                if (message.length() > 1)
                {
                    message = message.substring(0, message.length() - 1);
                }
                if (replyto == null)
                    throw new CommandException("Can't find the player to reply to!");

                Player playerto = plugin.getServer().getPlayer(replyto);
                if (playerto == null)
                    throw new CommandException("That player is no longer online!");

                sender.sendMessage("[Me -> " + playerto.getDisplayName() + "] " + message);
                playerto.sendMessage("[" + player.getDisplayName() + " -> Me] " + message);
                plugin.setPlayerReply(playerto.getName(), player.getName());
                plugin.getLogger().info("[TweakcraftUtils] (MSG) " + player.getName() + " -> " + playerto.getName() + " : " + message);
            } else if (args.length == 0) {
                throw new CommandUsageException("I need a message!");
            }
        } else {
            throw new CommandSenderException("Wait what do you want to do now?");
        }
        return true;
    }
}
