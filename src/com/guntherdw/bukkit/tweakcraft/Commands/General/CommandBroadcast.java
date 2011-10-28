/*
 * Copyright (c) 2011 GuntherDW
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */

package com.guntherdw.bukkit.tweakcraft.Commands.General;

import com.ensifera.animosity.craftirc.RelayedMessage;
import com.guntherdw.bukkit.tweakcraft.Commands.iCommand;
import com.guntherdw.bukkit.tweakcraft.Exceptions.CommandSenderException;
import com.guntherdw.bukkit.tweakcraft.Exceptions.CommandUsageException;
import com.guntherdw.bukkit.tweakcraft.Exceptions.PermissionsException;
import com.guntherdw.bukkit.tweakcraft.Tools.ArgumentParser;
import com.guntherdw.bukkit.tweakcraft.TweakcraftUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author GuntherDW
 */
public class CommandBroadcast implements iCommand {
    public boolean executeCommand(CommandSender sender, String command, String[] realargs, TweakcraftUtils plugin)
            throws PermissionsException, CommandSenderException, CommandUsageException {
        if (sender instanceof Player)
            if (!plugin.check((Player) sender, "broadcast"))
                throw new PermissionsException(command);

        String message = "";
        ArgumentParser ap = new ArgumentParser(realargs);
        String grouparg = ap.getString("g", null);
        String[] groups = null;
        List<Player> recipients = new ArrayList<Player>();

        if(grouparg!=null) {
            groups = grouparg.split(",");
            for(String gr : groups) {
                for(Player p : plugin.getServer().getOnlinePlayers()) {
                    if(plugin.getPermissions().getResolver().inSingleGroup("world", gr, p)) {
                        if(!recipients.contains(p))
                            recipients.add(p);
                    }
                }
            }
        } else
            recipients = Arrays.asList(plugin.getServer().getOnlinePlayers());

        String[] args = ap.getUnusedArgs();
        
        if (args.length < 1) {
            throw new CommandUsageException("You did not specify a message!");
        } else {
            for (String m : args) {
                message += m + " ";
            }
            message = message.substring(0, message.length() - 1);
        }


        if(recipients!=null)
            for (Player p : recipients) {
                p.sendMessage(ChatColor.RED + "[" + ChatColor.GREEN + "Broadcast" + ChatColor.RED + "] " + ChatColor.GREEN + message);
            }

        if(plugin.getConfigHandler().enableIRC && plugin.getCraftIRC()!=null && groups==null ) {
            if(plugin.getConfigHandler().GIRCenabled) {
                // String tag = ;
                
                // plugin.getCraftIRC(). ("[Broadcast] "+message, tag);
                RelayedMessage rm = plugin.getCraftIRC().newMsgToTag(plugin.getEndPoint(), plugin.getConfigHandler().GIRCtag, "generic");
                rm.setField("message", "[Broadcast] "+message);
                rm.post();
            }
        }
        // plugin.getLogger().info
        plugin.getLogger().info("[Broadcast] " + (groups==null?"":"("+grouparg+") ") + message);

        return true;
    }

    @Override
    public String getPermissionSuffix() {
        return "broadcast";
    }
}
