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
import org.bukkit.ChatColor;
import org.bukkit.Enchantment;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Map;

/**
 * @author GuntherDW
 */
public class CommandEnchant implements iCommand {
    @Override
    public boolean executeCommand(CommandSender sender, String command, String[] args, TweakcraftUtils plugin)
            throws PermissionsException, CommandSenderException, CommandUsageException, CommandException {
        if(sender instanceof Player) {
            if(!plugin.check((Player)sender, getPermissionSuffix()))
                throw new PermissionsException(command);
        } else {
            throw new CommandSenderException("What do you want to enchant today?");
        }

        if(args.length==0) {
            ItemStack is = ((Player)sender).getItemInHand();
            if(is!=null) {
                sender.sendMessage(ChatColor.YELLOW+"Enchantments for this item :");
                Map<Enchantment, Integer> enchantments = is.getEnchantments();
                if(enchantments==null) {
                    sender.sendMessage(ChatColor.YELLOW+"This item doesn't have any enchantments!");
                } else {
                    for(Enchantment ench : enchantments.keySet()) {
                        sender.sendMessage(ChatColor.YELLOW+ench.name()+" at level "+enchantments.get(ench));
                    }
                }
            } else {
                sender.sendMessage(ChatColor.YELLOW+"You're not holding anything");
            }
            return true;
        } else if(args.length==1 && args[0].equalsIgnoreCase("disenchant")) {
            ItemStack is = ((Player)sender).getItemInHand();
            if(is!=null) {
                sender.sendMessage(ChatColor.YELLOW+"Clearing any enchantments this item had");
                is.clearEnchantments();
            } else {
                sender.sendMessage(ChatColor.YELLOW+"You're not holding anything");
            }
        } else if(args.length!=2)
            throw new CommandUsageException("I need exactly 2 values, an ID and a level!");

        Integer enchantmentId = null;
        Integer enchantmentLevel = null;

        try{
            enchantmentId = Integer.parseInt(args[0]);
            enchantmentLevel = Integer.parseInt(args[1]);
        } catch(NumberFormatException ex) {
            throw new CommandUsageException("I need numbers, not garbage!");
        }

        Enchantment enchantment =  Enchantment.byId(enchantmentId);

        if(enchantment!=null) {
            ItemStack is = ((Player)sender).getItemInHand();
            is.clearEnchantments();
            is.addEnchantment(enchantment, enchantmentLevel);
            sender.sendMessage(ChatColor.YELLOW+"Adding "+enchantment.name()+" level "+enchantmentLevel);
        } else {
            sender.sendMessage(ChatColor.YELLOW+"Couldn't find Enchantment with id "+enchantmentId+"!");
        }

        return true;

    }

    @Override
    public String getPermissionSuffix() {
        return "enchant";
    }
}
