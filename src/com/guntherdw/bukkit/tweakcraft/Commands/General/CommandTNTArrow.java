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
import com.guntherdw.bukkit.tweakcraft.Packages.LocalPlayer;
import com.guntherdw.bukkit.tweakcraft.TweakcraftUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * @author GuntherDW
 */
public class CommandTNTArrow implements iCommand {
    @Override
    public boolean executeCommand(CommandSender sender, String command, String[] args, TweakcraftUtils plugin)
            throws PermissionsException, CommandSenderException, CommandUsageException, CommandException {

        if(!plugin.getConfigHandler().enableTNTArrows)
            throw new CommandException("TNT Arrows aren't enabled!");

        if(!(sender instanceof Player))
            throw new CommandSenderException("What were you trying to do?");

        LocalPlayer lp = plugin.wrapPlayer((Player)sender);
        // Seeing as we wrapped the player, this call should just work (famous last words)
        if(!plugin.check(lp.getBukkitPlayer(), "tntarrow"))
            throw new PermissionsException("You don't have permission to use TNT Arrows");

        Boolean mode = null;
        if(args.length>0) mode = Boolean.parseBoolean(args[0]);
        if(mode==null) mode = !lp.isTntArrow();
        lp.setTntArrow(mode);
        sender.sendMessage(ChatColor.YELLOW+"TNT Arrows have been "+(mode?ChatColor.RED+"ENABLED":ChatColor.GREEN+"DISABLED"));
        
        return true;
    }

    @Override
    public String getPermissionSuffix() {
        return "tntarrow";
    }
}
