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
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * @author GuntherDW
 */
public class CommandItem implements Command {
    public boolean executeCommand(CommandSender sender, String command, String[] args, TweakcraftUtils plugin)
            throws PermissionsException, CommandSenderException, CommandUsageException, CommandException {
        
        if(sender instanceof Player)
        {
            Player player = (Player) sender;
            if(!plugin.check(player, "item"))
                throw new PermissionsException(command);

            ItemDB db = plugin.getItemDB();
            Item item;

            Player  receiver = null;
            Integer itemId = null;
            Byte    itemDmg = 0;
            Integer itemAmount = null;

            if(args.length>0) // just the item!
            {
                String[] split = args[0].split(":");
                if(split.length>1)
                {
                    try{
                        itemId = Integer.parseInt(split[0]);
                        itemDmg = Byte.parseByte(split[1]);
                    } catch(NumberFormatException e) {
                        item = db.getItem(split[0]);
                        itemId = item.getItemnumber();
                        itemDmg = item.getDamage();
                        itemAmount = item.getDefaultstack();
                    }
                } else {
                    try{
                        itemId = Integer.parseInt(split[0]);
                    } catch(NumberFormatException e) {
                        item = db.getItem(split[0]);
                        itemId = item.getItemnumber();
                        itemDmg = item.getDamage();
                        itemAmount = item.getDefaultstack();
                    }
                }
                if(args.length>1) { // set Amount
                    itemAmount = Integer.parseInt(args[1]);
                } else {
                    if(itemAmount == null)
                        itemAmount = 64;
                }

                if(args.length>2) { // set Receiver
                    receiver = plugin.getServer().getPlayer(plugin.findPlayer(args[2]));
                    if(receiver == null)
                    {
                        throw new CommandUsageException("Can't find the other player!");
                    }
                } else {
                    receiver = player;
                }

                if(ItemType.isValid(itemId))
                {
                    ItemStack stack = new ItemStack(itemId, itemAmount, itemDmg.shortValue());
                    receiver.getInventory().addItem(stack);
                } else {
                    throw new CommandUsageException("Specified item is not valid!");
                }
                /* sender.sendMessage(itemId.toString());
                sender.sendMessage(itemDmg.toString());
                sender.sendMessage(itemAmount.toString());
                sender.sendMessage(ItemType.toName(itemId)); */

            }

        }
        return true;
    }
}
