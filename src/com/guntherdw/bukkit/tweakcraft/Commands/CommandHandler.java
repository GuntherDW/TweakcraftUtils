package com.guntherdw.bukkit.tweakcraft.Commands;

import com.guntherdw.bukkit.tweakcraft.Command;
import com.guntherdw.bukkit.tweakcraft.Commands.Admin.CommandAdmon;
import com.guntherdw.bukkit.tweakcraft.Commands.General.*;
import com.guntherdw.bukkit.tweakcraft.Exceptions.CommandNotFoundException;
import com.guntherdw.bukkit.tweakcraft.Commands.Admin.CommandAdmin;
import com.guntherdw.bukkit.tweakcraft.Commands.Admin.CommandAdminAdd;
import com.guntherdw.bukkit.tweakcraft.Commands.Admin.CommandAdminRemove;
import com.guntherdw.bukkit.tweakcraft.Commands.Teleportation.CommandTele;
import com.guntherdw.bukkit.tweakcraft.Commands.Teleportation.CommandTp;
import com.guntherdw.bukkit.tweakcraft.Commands.Teleportation.CommandTphere;
import com.guntherdw.bukkit.tweakcraft.TweakcraftUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * @author GuntherDW
 */
public class CommandHandler {

    public Map<String, Command> commandMap = new HashMap<String, Command>();
    private TweakcraftUtils plugin;

    public CommandHandler(TweakcraftUtils instance)
    {
        this.plugin = instance;
        commandMap.clear();

        /**
         * Admin commands
         */
        commandMap.put("admin", new CommandAdmin());
        commandMap.put("adminadd", new CommandAdminAdd());
        commandMap.put("adminremove", new CommandAdminRemove());
        commandMap.put("admon", new CommandAdmon());
        /**
         * General commands
         */
        commandMap.put("ext", new CommandExt());
        commandMap.put("ignite", new CommandIgnite());
        commandMap.put("seen", new CommandSeen());
        commandMap.put("who", new CommandWho());
        commandMap.put("broadcast", new CommandBroadcast());
        commandMap.put("lc", new CommandLc());

        /**
         * Teleportation commands
         */
        commandMap.put("tele", new CommandTele());
        commandMap.put("tp", new CommandTp());
        commandMap.put("tphere", new CommandTphere());

    }

    public TweakcraftUtils getPlugin() {
        return plugin;
    }

    public Map<String, Command> getCommandMap() {
        return commandMap;
    }

    public Command getCommand(String command) throws CommandNotFoundException
    {
        if(commandMap.containsKey(command))
        {
            return commandMap.get(command);
        } else {
            throw new CommandNotFoundException(command);
        }
    }
}
