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

import com.guntherdw.bukkit.tweakcraft.Command;
import com.guntherdw.bukkit.tweakcraft.Exceptions.CommandException;
import com.guntherdw.bukkit.tweakcraft.Exceptions.CommandSenderException;
import com.guntherdw.bukkit.tweakcraft.Exceptions.CommandUsageException;
import com.guntherdw.bukkit.tweakcraft.Exceptions.PermissionsException;
import com.guntherdw.bukkit.tweakcraft.Packages.Item;
import com.guntherdw.bukkit.tweakcraft.Packages.ItemDB;
import com.guntherdw.bukkit.tweakcraft.TweakcraftUtils;
import com.sk89q.worldedit.blocks.ItemType;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * @author GuntherDW
 */
public class CommandItem implements Command {
    public boolean executeCommand(CommandSender sender, String command, String[] args, TweakcraftUtils plugin)
            throws PermissionsException, CommandSenderException, CommandUsageException, CommandException {

        if (sender instanceof Player)
            if (!plugin.check((Player) sender, "item"))
                throw new PermissionsException(command);

        ItemDB db = plugin.getItemDB();
        Item item;

        Player receiver = null;
        Integer itemId = null;
        Byte itemDmg = 0;
        Integer itemAmount = null;

        if (args.length > 0) // just the item!
        {
            String[] split = args[0].split(":");
            if (split.length > 1) {
                try {
                    itemId = Integer.parseInt(split[0]);
                    itemDmg = Byte.parseByte(split[1]);
                } catch (NumberFormatException e) {
                    item = db.getItem(split[0]);
                    itemId = item.getItemnumber();
                    itemDmg = item.getDamage();
                    itemAmount = item.getDefaultstack();
                }
            } else {
                try {
                    itemId = Integer.parseInt(split[0]);
                } catch (NumberFormatException e) {
                    item = db.getItem(split[0]);
                    if (item != null) {
                        itemId = item.getItemnumber();
                        itemDmg = item.getDamage();
                        itemAmount = item.getDefaultstack();
                    } else {
                        throw new CommandException("Can't find item!");
                    }
                }
            }
            if (args.length > 1) { // set Amount
                itemAmount = Integer.parseInt(args[1]);
            } else {
                if (itemAmount == null)
                    itemAmount = 64;
            }

            if (args.length > 2) { // set Receiver
                receiver = plugin.getServer().getPlayer(plugin.findPlayer(args[2]));
                if (receiver == null) {
                    throw new CommandUsageException("Can't find the other player!");
                }
            } else {
                if (sender instanceof Player)
                    receiver = (Player) sender;
                else
                    throw new CommandUsageException("If you're a console you have to specify the receiver!");

            }

            if (ItemType.isValid(itemId)) {
                String recvname = "";
                String giftfrom = "";
                if (sender instanceof Player) {
                    recvname = receiver.getDisplayName();
                    giftfrom = ((Player) sender).getName();
                } else {
                    recvname = receiver.getName();
                    giftfrom = "CONSOLE";
                }

                sender.sendMessage(ChatColor.YELLOW + "Giving " + recvname + ChatColor.YELLOW + " " + itemAmount + " of " + ItemType.toName(itemId) + "!");
                ItemStack stack = new ItemStack(itemId, itemAmount, itemDmg.shortValue());
                receiver.getInventory().addItem(stack);
                plugin.getLogger().info("[TweakcraftUtils] " + giftfrom + " gave " + recvname + " " + itemAmount + "x" + itemId + " (" + itemDmg.intValue() + ")");
            } else {
                throw new CommandUsageException("Specified item is not valid!");
            }
        }
        return true;
    }
}
