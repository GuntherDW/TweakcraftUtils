package com.guntherdw.bukkit.tweakcraft.Commands.Essentials;

import com.guntherdw.bukkit.tweakcraft.Command;
import com.guntherdw.bukkit.tweakcraft.Exceptions.CommandException;
import com.guntherdw.bukkit.tweakcraft.Exceptions.CommandSenderException;
import com.guntherdw.bukkit.tweakcraft.Exceptions.CommandUsageException;
import com.guntherdw.bukkit.tweakcraft.Exceptions.PermissionsException;
import com.guntherdw.bukkit.tweakcraft.TweakcraftUtils;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * @author GuntherDW
 */
public class CommandGetpos implements Command {
    public boolean executeCommand(CommandSender sender, String command, String[] args, TweakcraftUtils plugin)
            throws PermissionsException, CommandSenderException, CommandUsageException, CommandException {
        if(sender instanceof Player)
        {
            Player player = (Player) sender;
            if(!plugin.check(player, "getpos"))
                throw new PermissionsException(command);
            Location loc = player.getLocation();
            Integer x,y,z,yaw,pitch;
            x=Math.round((float)loc.getX());
            y=Math.round((float)loc.getY());
            z=Math.round((float)loc.getZ());
            yaw=Math.round((float)loc.getYaw());
            pitch=Math.round((float)loc.getPitch());

            sender.sendMessage(ChatColor.YELLOW + "Pos X: " + x + " Y: " + y + " Z: " + z);
            sender.sendMessage(ChatColor.YELLOW + "Rotation: " + yaw + " Pitch: " + pitch);
            String dir = plugin.getCompassDirection(player.getLocation().getYaw());
            double degreeRotation = ((player.getLocation().getYaw() - 90) % 360);
            if (degreeRotation < 0)
                 degreeRotation += 360.0;
            
            sender.sendMessage(ChatColor.YELLOW + "Compass: "+dir+" ("+(Math.round(degreeRotation * 10) / 10.0) + ")");
        } else {
            throw new CommandSenderException("Now why would a console want to know its position?");
        }
        return true;
    }
}
