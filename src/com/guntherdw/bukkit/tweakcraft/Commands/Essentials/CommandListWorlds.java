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

package com.guntherdw.bukkit.tweakcraft.Commands.Essentials;

import com.guntherdw.bukkit.tweakcraft.Commands.iCommand;
import com.guntherdw.bukkit.tweakcraft.Exceptions.CommandException;
import com.guntherdw.bukkit.tweakcraft.Exceptions.CommandSenderException;
import com.guntherdw.bukkit.tweakcraft.Exceptions.CommandUsageException;
import com.guntherdw.bukkit.tweakcraft.Exceptions.PermissionsException;
import com.guntherdw.bukkit.tweakcraft.TweakcraftUtils;
import com.guntherdw.bukkit.tweakcraft.Worlds.iWorld;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * @author GuntherDW
 */
public class CommandListWorlds implements iCommand {
    public boolean executeCommand(CommandSender sender, String command, String[] args, TweakcraftUtils plugin)
            throws PermissionsException, CommandSenderException, CommandUsageException, CommandException {
        if (sender instanceof Player) // Give the player a list of worlds he has access to!
        {
            Player player = (Player) sender;
            player.sendMessage(ChatColor.LIGHT_PURPLE + "Listing currently accessible worlds : ");

            // String message = "";
            String col;

            for (World w : plugin.getServer().getWorlds()) {
                if (plugin.check(player, "worlds." + w.getName())) {

                    World.Environment env = w.getEnvironment();
                    boolean customChunkGen = false;
                    if(w.getGenerator()!=null) { customChunkGen = true; env = null;}
                    iWorld tw = plugin.getworldManager().getWorld(w.getName());
                    if(tw!=null) {
                        if(tw.getChunkGen()!=null)
                            env=null;
                    }
                    if(env == null)
                        col = ChatColor.GRAY.toString();
                    else if(env == World.Environment.NORMAL)
                        col =  ChatColor.GREEN.toString();
                    else if (env == World.Environment.NETHER)
                        col = ChatColor.RED.toString();
                    else if (env == World.Environment.THE_END)
                        col = ChatColor.DARK_GRAY.toString();
                    else
                        col = ChatColor.GRAY.toString();
                    player.sendMessage(ChatColor.LIGHT_PURPLE+ "(" + w.getPlayers().size()+") " + col + w.getName() +
                            (customChunkGen?ChatColor.LIGHT_PURPLE +" (CG:"+ w.getGenerator().getClass().getSimpleName()+")":""));
                }
            }

            player.sendMessage(ChatColor.LIGHT_PURPLE + "Legend: " + ChatColor.RED + "NETHER" + ChatColor.LIGHT_PURPLE + "," +
                    ChatColor.GREEN + " NORMAL"+ ChatColor.LIGHT_PURPLE + "," + ChatColor.AQUA + " SKYLANDS"+ChatColor.LIGHT_PURPLE+","+ChatColor.GRAY+" CUSTOM/OTHER");
            player.sendMessage(ChatColor.LIGHT_PURPLE + "Warp to a world by issuing /world <worldname>");


        } else { // The console just needs a list!
            sender.sendMessage("Currently enabled worlds : ");
            String message = "";
            for (World w : plugin.getServer().getWorlds()) {
                message += w.getName() + " ("+w.getPlayers().size()+"), ";
            }
            if (message.length() > 1)
                message = message.substring(0, message.length() - 2);
            sender.sendMessage(message);
        }
        return true;
    }

    @Override
    public String getPermissionSuffix() {
        return null;
    }
}
