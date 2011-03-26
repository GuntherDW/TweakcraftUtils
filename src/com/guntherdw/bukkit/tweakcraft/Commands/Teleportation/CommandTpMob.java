package com.guntherdw.bukkit.tweakcraft.Commands.Teleportation;

import com.guntherdw.bukkit.tweakcraft.Command;
import com.guntherdw.bukkit.tweakcraft.Exceptions.CommandException;
import com.guntherdw.bukkit.tweakcraft.Exceptions.CommandSenderException;
import com.guntherdw.bukkit.tweakcraft.Exceptions.CommandUsageException;
import com.guntherdw.bukkit.tweakcraft.Exceptions.PermissionsException;
import com.guntherdw.bukkit.tweakcraft.TweakcraftUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.*;

import java.util.List;

/**
 * @author GuntherDW
 */
public class CommandTpMob implements Command {
    public boolean executeCommand(CommandSender sender, String command, String[] args, TweakcraftUtils plugin)
            throws PermissionsException, CommandSenderException, CommandUsageException, CommandException {
        if(sender instanceof Player)
        {
            Player victim = (Player)sender;
            String vname = "you";

            Integer cnum;
            CreatureType ctype = null;
            if(!plugin.check((Player) sender, "tpmob"))
                throw new PermissionsException(command);

            if(args.length<1)
                throw new CommandUsageException("I need at least 1 number or mobname!");

            if(args.length==2) // teleport to victim!
            {
                List<Player> find = plugin.getServer().matchPlayer(args[1]);
                if(find.size()==1)
                {
                    victim = find.get(0);
                    vname = victim.getDisplayName();
                } else {
                    throw new CommandUsageException("Can't find victim!");
                }
            }

            if(args[0].equals("*")) {
                cnum = -1;
            } else {
                try{
                    cnum = Integer.parseInt(args[0]);
                } catch (NumberFormatException e) {
                    String mn = args[0];
                    if(args[0].length()>2)
                        mn = args[0].substring(0, 1).toUpperCase() + args[0].substring(1, args[0].length());
                    cnum = 0;
                    ctype = CreatureType.fromName(mn);
                    if(ctype == null)
                    {
                        throw new CommandUsageException("I need a number or a name, not a random string!");
                    }
                }
            }
            if(ctype != null)
            {
                sender.sendMessage(ChatColor.YELLOW + "Teleporting all "+ctype.getName() +" to "+vname+ChatColor.YELLOW+"!");
            } else if(cnum == -1) {
                sender.sendMessage(ChatColor.YELLOW + "Teleporting all mobs to "+vname+ChatColor.YELLOW+"!");
            } else {
                sender.sendMessage(ChatColor.YELLOW + "Teleporting mob with mobId "+cnum+" to "+vname+ChatColor.YELLOW+"!");
            }

            for(LivingEntity crea : plugin.getServer().getWorld(((Player)sender).getWorld().getName()).getLivingEntities())
            {
                if(crea instanceof Creature || crea instanceof Flying)
                {
                    // Creature crea = (Creature) c;
                    CreatureType type = null;
                    if (crea instanceof Chicken) {
                        type = CreatureType.CHICKEN;
                    } else if (crea instanceof Cow) {
                        type = CreatureType.COW;
                    } else if (crea instanceof Creeper) {
                        type = CreatureType.CREEPER;
                    } else if (crea instanceof Ghast) {
                        type = CreatureType.GHAST;
                    } else if (crea instanceof Pig) {
                        type = CreatureType.PIG;
                    } else if (crea instanceof PigZombie) {
                        type = CreatureType.PIG_ZOMBIE;
                    } else if (crea instanceof Sheep) {
                        type = CreatureType.SHEEP;
                    } else if (crea instanceof Skeleton) {
                        type = CreatureType.SKELETON;
                    } else if (crea instanceof Spider) {
                        type = CreatureType.SPIDER;
                    } else if (crea instanceof Zombie) {
                        type = CreatureType.ZOMBIE;
                    } else if (crea instanceof Slime) {
                        type = CreatureType.SLIME;
                    } else if (crea instanceof Squid) {
                        type = CreatureType.SQUID;
                    }

                    if(cnum == -1 || ctype == type || crea.getEntityId() == cnum.intValue())
                    {
                        crea.teleport(victim);
                        continue;
                    }
                }

            }
        } else {
            sender.sendMessage("Sorry, no consoles allowed in this party!");
        }
        return true;
    }
}
