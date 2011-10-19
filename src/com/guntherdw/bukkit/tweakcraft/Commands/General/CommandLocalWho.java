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

import com.guntherdw.bukkit.tweakcraft.Commands.iCommand;
import com.guntherdw.bukkit.tweakcraft.Exceptions.CommandException;
import com.guntherdw.bukkit.tweakcraft.Exceptions.CommandSenderException;
import com.guntherdw.bukkit.tweakcraft.Exceptions.CommandUsageException;
import com.guntherdw.bukkit.tweakcraft.Exceptions.PermissionsException;
import com.guntherdw.bukkit.tweakcraft.TweakcraftUtils;
import com.guntherdw.bukkit.tweakcraft.Util.EntityLocation;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * @author GuntherDW
 */
public class CommandLocalWho implements iCommand {
    @Override
    public boolean executeCommand(CommandSender sender, String command, String[] args, TweakcraftUtils plugin)
            throws PermissionsException, CommandSenderException, CommandUsageException, CommandException {

        if(!plugin.getConfigHandler().enableLocalChat) {
            throw new CommandUsageException("LocalChat not enabled!");
        }

        if(!(sender instanceof Player))
            throw new CommandSenderException("What do you need this for anyway?");

        // List<Player> lplayers = new ArrayList<Player>();
        String ps = "";
        Player self = (Player) sender;
        EntityLocation ploc = new EntityLocation(self);
        for(Player p : self.getWorld().getPlayers()) {
            Integer distance = ploc.getDistance(p);
            if (distance != null && distance < plugin.getConfigHandler().localchatdistance) {
                // lplayers.add(p);
                if(!(plugin.getPlayerListener().getInvisplayers().contains(p.getName()))) {
                    ps+= p.getDisplayName() + ChatColor.WHITE +", ";
                }
            }
        }
        if(ps.length()>0) ps=ps.substring(0, ps.length()-2);

        self.sendMessage(ChatColor.LIGHT_PURPLE+"Players in range ("+plugin.getConfigHandler().localchatdistance+" blocks)");
        if(ps.length()>0) self.sendMessage(ps);
        else              self.sendMessage(ChatColor.YELLOW+"No players in range!");
        return true;

    }

    @Override
    public String getPermissionSuffix() {
        return "chat.mode.local";
    }
}
