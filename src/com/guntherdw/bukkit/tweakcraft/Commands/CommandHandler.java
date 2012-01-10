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

package com.guntherdw.bukkit.tweakcraft.Commands;

import com.guntherdw.bukkit.tweakcraft.Commands.Commands.*;
import com.guntherdw.bukkit.tweakcraft.Exceptions.CommandException;
import com.guntherdw.bukkit.tweakcraft.Exceptions.CommandSenderException;
import com.guntherdw.bukkit.tweakcraft.Exceptions.CommandUsageException;
import com.guntherdw.bukkit.tweakcraft.Exceptions.PermissionsException;
import com.guntherdw.bukkit.tweakcraft.TweakcraftUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginDescriptionFile;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

/**
 * @author GuntherDW
 */
public class CommandHandler {

    private Map<String, iCommand> commandMap = new HashMap<String, iCommand>();
    private Map<String, Method> newCommandMap = new HashMap<String, Method>();
    private Map<String, Method> aliasCommandMap = new HashMap<String, Method>();
    private Map<Method, Object> instanceMap = new HashMap<Method, Object>();
    private Logger logger = Logger.getLogger("Minecraft");
    private TweakcraftUtils plugin;

    private Object getMethodInstance(Method method) {
        if (instanceMap.containsKey(method))
            return instanceMap.get(method);

        return null;
    }

    public CommandHandler(TweakcraftUtils instance) {
        this.plugin = instance;
        commandMap.clear();
        newCommandMap.clear();

        addCommandClass(new AdminCommands(instance));
        addCommandClass(new ChatCommands(instance));
        addCommandClass(new DebugCommands(instance));
        addCommandClass(new EssentialsCommands(instance));
        addCommandClass(new GeneralCommands(instance));
        addCommandClass(new TeleportationCommands(instance));
        addCommandClass(new WeatherCommands(instance));
    }

    public void addCommandClass(Object instance) {
        for (Method m : instance.getClass().getDeclaredMethods()) {
            if (m.getAnnotation(aCommand.class) != null)
                injectCommand(m, instance);
        }
    }


    public void injectCommand(Method method, Object instance) {
        aCommand annotation = method.getAnnotation(aCommand.class);
        String[] aliases = annotation.aliases();
        if (aliases.length > 0) {
            if (newCommandMap.containsKey(aliases[0])) {
                logger.warning("[TweakcraftUtils] Duplicate command found!");
                logger.warning("[TweakcraftUtils] Method : " + method.getName() + "!");
                logger.warning("[TweakcraftUtils] Command : " + aliases[0] + "!");
                return;
            }
            for (int x = 0; x < aliases.length; x++) {

                if (x == 0)
                    newCommandMap.put(aliases[x], method);
                else
                    aliasCommandMap.put(aliases[x], method);
            }

            instanceMap.put(method, instance);
        }
    }

    @SuppressWarnings("unchecked")
    public void checkCommands() {
        if (plugin.getConfigHandler().enableDebug) { /* Show commands with no command attached */
            PluginDescriptionFile pdFile = plugin.getDescription();
            Map<String, Object> pluginCommands = (Map<String, Object>) pdFile.getCommands();
            Set<String> cmds = pluginCommands.keySet();
            for (String c : cmds) {
                if (!this.newCommandMap.containsKey(c) && !this.aliasCommandMap.containsKey(c))
                    logger.warning("[TweakcraftUtils] WARNING: Unmapped command : " + c);
            }
        }
    }

    public TweakcraftUtils getPlugin() {
        return plugin;
    }

    public Map<String, Method> getCommandMap() {
        return newCommandMap;
    }

    public boolean isInjected(String commandName) {
        return plugin.getCommand(commandName) != null;
        /* for(Map.Entry<String, Method> entry : getCommandMap().entrySet()) {
            if(plugin.getCommand())
        } */
    }

    public Method getCommand(String command) {
        if (newCommandMap.containsKey(command)) {
            return newCommandMap.get(command);
        } else if (aliasCommandMap.containsKey(command)) {
            return aliasCommandMap.get(command);
        } else {
            return null;
        }
    }

    public boolean executeCommand(CommandSender sender, String name, String[] args) {

        String mess = "";
        if (args.length > 1) {
            for (String m : args)
                mess += m + " ";

            mess = mess.substring(0, mess.length() - 1);
        } else if (args.length == 1) {
            mess = args[0];
        }

        // Method

        if (newCommandMap.containsKey(name) || aliasCommandMap.containsKey(name)) {
            try {
                /* iCommand command = commandHandler.getCommand(cmd.getName()); */
                // public abstract boolean executeCommand(Server server, CommandSender sender, String command, String[] args, TweakcraftUtils plugin);

                if (!runMethod(sender, name, args)) {
                    sender.sendMessage("This command did not go as intended!");
                }
                if (sender instanceof Player)
                    plugin.getLogger().info("[TweakcraftUtils] " + sender.getName() + " issued: /" + name + " " + mess);
                else
                    plugin.getLogger().info("[TweakcraftUtils] CONSOLE issued: /" + name + " " + mess);
                return true;
            } /*catch (CommandNotFoundException e) {
                    sender.sendMessage("TweakcraftUtils error, command not found!");
                }*/ catch (PermissionsException e) {
                sender.sendMessage(ChatColor.RED + "You do not have the correct permissions for this command or usage!");
                if (sender instanceof Player) {
                    plugin.getLogger().info("[TweakcraftUtils] " + ((Player) sender).getName() + " tried: /" + name + " " + mess);
                }
            } catch (CommandUsageException e) {
                sender.sendMessage(ChatColor.YELLOW + e.getMessage());
            } catch (CommandSenderException e) {
                sender.sendMessage(ChatColor.YELLOW + e.getMessage());
            } catch (CommandException e) {
                sender.sendMessage(ChatColor.YELLOW + e.getMessage());
                plugin.getLogger().info("[TweakcraftUtils] " + (sender instanceof Player ? ((Player) sender).getName() : "CONSOLE") + " got a CommandException on : /" + name + " " + mess);
            }
        }
        return false;
    }

    public boolean runMethod(CommandSender sender, String name, String[] args)
        throws PermissionsException, CommandSenderException, CommandUsageException, CommandException {

        try {
            Method command = getCommand(name);
            return (Boolean) command.invoke(getMethodInstance(command), sender, name, args);
        } catch (InvocationTargetException e) {
            Throwable t = e.getCause();
            if (t instanceof PermissionsException)
                throw (PermissionsException) t;
            if (t instanceof CommandSenderException)
                throw (CommandSenderException) t;
            if (t instanceof CommandUsageException)
                throw (CommandUsageException) t;
            if (t instanceof CommandException)
                throw (CommandException) t;

            logger.warning("[TweakcraftUtils] Error occured while executing command!");
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            logger.warning("[TweakcraftUtils] Error occured while executing command!");
            e.printStackTrace();
        }

        return false;
    }
}
