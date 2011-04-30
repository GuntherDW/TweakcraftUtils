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

import com.guntherdw.bukkit.tweakcraft.Commands.Command;
import com.guntherdw.bukkit.tweakcraft.Exceptions.CommandSenderException;
import com.guntherdw.bukkit.tweakcraft.Exceptions.CommandUsageException;
import com.guntherdw.bukkit.tweakcraft.Exceptions.PermissionsException;
import com.guntherdw.bukkit.tweakcraft.TweakcraftUtils;
import org.bukkit.ChatColor;
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
        Integer amountofinvis = 0;
        for(Player p : list)
        {
            if(plugin.getPlayerListener().getInvisplayers().contains(p.getName()))
                amountofinvis++;
        }
        boolean hasperm;
        if(sender instanceof Player)
                hasperm = plugin.check((Player)sender, "tpinvis");
            else
                hasperm = true;

        String msg = ChatColor.LIGHT_PURPLE + "Player list (" + (list.size()-amountofinvis) + "/" + plugin.getServer().getMaxPlayers() + "): ";
        if(amountofinvis>0) {
            if(hasperm)
                msg += ChatColor.AQUA+" ["+list.size()+"/"+plugin.getServer().getMaxPlayers()+"]";
        }
        String toadd;
        Collections.sort(list, new Comparator<Player>() {
            public int compare(Player p1, Player p2) {
                return p1.getName().compareToIgnoreCase(p2.getName());
            }
        });

        sender.sendMessage(msg);
        msg = " ";
        boolean check;
        
        for (Player p : list) {
            toadd = "";
            check = plugin.getPlayerListener().getInvisplayers().contains(p.getName());

            if (!(sender instanceof Player)) { // console won't show gold colors? shame!
                if(check)
                    toadd = ChatColor.AQUA+"[";

                toadd += p.getDisplayName().replace(ChatColor.GOLD.toString(), ChatColor.YELLOW.toString());
                
                if(check)
                    toadd += ChatColor.AQUA+"]";

                toadd+=ChatColor.WHITE+", ";
            } else {
                if(check && hasperm)
                {
                    toadd = ChatColor.AQUA+"["+p.getDisplayName() + ChatColor.AQUA + "]"+ChatColor.WHITE+", ";
                } else if(!check) {
                    toadd = p.getDisplayName() + ChatColor.WHITE + ", ";
                }
            }
            msg += toadd;
        }
        if (!msg.trim().isEmpty()) {
            sender.sendMessage(msg.substring(0, msg.length() - 2));
        }


        return true;
    }

    @Override
    public String getPermissionSuffix() {
        return null;
    }
}
