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
import com.guntherdw.bukkit.tweakcraft.Exceptions.*;
import com.guntherdw.bukkit.tweakcraft.TweakcraftUtils;
import org.bukkit.command.CommandSender;
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
    private Map<Method, Object> instanceMap = new HashMap<Method, Object>();
    /*private AdminCommands adminCommands;
    private ChatCommands chatcommands;
    private DebugCommands debugCommands; 
    private EssentialsCommands essentialsCommands;
    private GeneralCommands generalCommands;
    private TeleportationCommands teleportationCommands;
    private WeatherCommands weatherCommands;*/
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

        addCommandClass(new AdminCommands());
        addCommandClass(new ChatCommands());
        addCommandClass(new DebugCommands());
        addCommandClass(new EssentialsCommands());
        addCommandClass(new GeneralCommands());
        addCommandClass(new TeleportationCommands());
        addCommandClass(new WeatherCommands());
    }

    private void addCommandClass(Object instance) {
        for (Method m : instance.getClass().getDeclaredMethods()) {
            if (m.getAnnotation(aCommand.class) != null)
                injectCommand(m, instance);
        }
    }


    private void injectCommand(Method method, Object instance) {
        aCommand annotation = method.getAnnotation(aCommand.class);
        String[] aliases = annotation.aliases();
        if (aliases.length > 0) {
            if (newCommandMap.containsKey(aliases[0])) {
                logger.warning("[TweakcraftUtils] Duplicate command found!");
                logger.warning("[TweakcraftUtils] Method : " + method.getName() + "!");
                logger.warning("[TweakcraftUtils] Command : " + aliases[0] + "!");
                return;
            }
            newCommandMap.put(aliases[0], method);
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
                if (!this.newCommandMap.containsKey(c))
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

    public iCommand getCommand(String command) throws CommandNotFoundException {
        if (commandMap.containsKey(command)) {
            return commandMap.get(command);
        } else {
            throw new CommandNotFoundException(command);
        }
    }

    public boolean executeCommand(CommandSender sender, String name, String[] args, TweakcraftUtils tweakcraftUtils)
        throws PermissionsException, CommandSenderException, CommandUsageException, CommandException {

        Method command = newCommandMap.get(name);
        try {
            return (Boolean) command.invoke(getMethodInstance(command), sender, name, args, tweakcraftUtils);
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
