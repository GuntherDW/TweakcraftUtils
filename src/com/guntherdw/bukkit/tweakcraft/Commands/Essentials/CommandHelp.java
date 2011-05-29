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

import com.guntherdw.bukkit.tweakcraft.Commands.Command;
import com.guntherdw.bukkit.tweakcraft.Commands.CommandHandler;
import com.guntherdw.bukkit.tweakcraft.Exceptions.*;
import com.guntherdw.bukkit.tweakcraft.TweakcraftUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginDescriptionFile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author GuntherDW
 */
public class CommandHelp implements Command {

    @SuppressWarnings("unchecked")
    @Override
    public boolean executeCommand(CommandSender sender, String command, String[] args, TweakcraftUtils plugin)
            throws PermissionsException, CommandSenderException, CommandUsageException, CommandException {
        List<String>  cma = new ArrayList<String>();
        CommandHandler commh = plugin.getCommandHandler();
        /* StringBuilder sb = new StringBuilder();
        Formatter formatter = new Formatter(sb); */
        boolean aliases = true;
        /* if(args.length>0) {
            if(Arrays.asList(args).contains("-a")) {
                aliases = true;
            }
        } */
        String toadd = "";
        for(String cname : commh.getCommandMap().keySet()) {
            try {
                Command c = commh.getCommand(cname);

                if(addCommandToList(sender, c, plugin)) {
                    /* toadd = String.format(ChatColor.GOLD+"%1$-10s"+ChatColor.WHITE+" : "
                            +ChatColor.YELLOW+"%2$s", cname, plugin.getCommand(cname).getDescription()); */
                    // Fuck minecraft's font :<
                    toadd = ChatColor.GOLD+cname+ChatColor.WHITE+
                            " : "+ChatColor.YELLOW+plugin.getCommand(cname).getDescription();
                    if(aliases) {
                        List<String> aliaseslist = plugin.getCommand(cname).getAliases();
                        if(aliaseslist.size()>0) {
                            toadd += ChatColor.WHITE+" (";
                            for(String alias : aliaseslist) {
                                toadd+= ChatColor.GOLD+alias+ChatColor.WHITE+",";
                            }
                            toadd = toadd.substring(0, toadd.length()-1);
                            toadd+=")";
                        }

                    }
                    cma.add(toadd);
                }
            } catch (CommandNotFoundException e) {
                throw new CommandException("Exception thrown while calling /help, contact GuntherDW!");
            }
        }
        /**
         * Extra plugins
         */
        for(String plug : plugin.getConfigHandler().extrahelpplugin) {
            if(plugin.getServer().getPluginManager().getPlugin(plug) != null) {

                PluginDescriptionFile pdesc = plugin.getServer().getPluginManager().getPlugin(plug).getDescription();
                Map<String, Map<String, Object>> cmds = (Map<String, Map<String, Object>>) pdesc.getCommands();
                for(String cmd : cmds.keySet()) {

                    if(!plugin.getConfigHandler().extrahelphide.contains(cmd)) {
                        String perm = (String) cmds.get(cmd).get("_permission");
                        if(perm==null) perm = (String) cmds.get(cmd).get("permissions"); // Added for WorldEdit support
                        if(addExtCommandToList(sender, perm, plugin)) {
                            toadd = ChatColor.GOLD+cmd+ChatColor.WHITE +
                                    " : "+ChatColor.YELLOW+((String)cmds.get(cmd).get("description"));
                            if(aliases) {
                                List<String> aliaseslist = (List<String>) cmds.get(cmd).get("aliases");
                                // toadd += ChatColor.WHITE+" ("+ChatColor.GOLD+aliaseslist+ChatColor.WHITE+")";
                                if(aliaseslist!=null && aliaseslist.size()>0) {
                                    toadd += ChatColor.WHITE+" (";
                                    for(String alias : aliaseslist) {
                                        toadd+= ChatColor.GOLD+alias+ChatColor.WHITE+",";
                                    }
                                    toadd = toadd.substring(0, toadd.length()-1);
                                    toadd+=")";
                                }
                            }
                            cma.add(toadd);
                        }
                    }
                }
            } else {
                plugin.getLogger().info("[TweakcraftUtils] EXTRAHELP error : "+plug+" is null!");
            }
        }

        Double dmaxPage = Math.ceil((double)cma.size()/plugin.getConfigHandler().helpPerPage);
        int maxPage = dmaxPage.intValue();
        int hpp     = plugin.getConfigHandler().helpPerPage;
        Integer pagereq = 0;
        if(args.length>0) {
            try {
                pagereq = Integer.parseInt(args[0]) - 1;
            } catch(NumberFormatException ex) {
                pagereq = 0;
            }
        }
        if(pagereq.intValue() < 0 || pagereq.intValue() > maxPage-1) {
            throw new CommandUsageException("Invalid page number!");
        }

        sender.sendMessage(ChatColor.AQUA+"Commands available to you : Page "+(pagereq+1)+"/"+maxPage);
        int start = pagereq.intValue()*hpp;
        int end = start + hpp;

        for(int x = start; x < end && x<cma.size(); x++) {
            sender.sendMessage(cma.get(x));
        }
        
        return true;
    }

    protected boolean addCommandToList(CommandSender sender, Command command, TweakcraftUtils plugin) {
        if(sender instanceof Player) {
            if(command.getPermissionSuffix() == null) {
                return true;
            } else {
                return plugin.check((Player)sender, command.getPermissionSuffix());
            }
        } else {
            return true;
        }
    }

    protected boolean addExtCommandToList(CommandSender sender, String perm, TweakcraftUtils plugin) {
        if(sender instanceof Player) {
            if(perm == null) {
                return true;
            } else {
                return plugin.checkfull((Player)sender, perm);
            }
        } else {
            return true;
        }
    }

    @Override
    public String getPermissionSuffix() {
        return null;
    }

}
