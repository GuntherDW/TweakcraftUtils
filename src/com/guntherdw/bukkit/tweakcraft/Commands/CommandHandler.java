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

import com.guntherdw.bukkit.tweakcraft.Command;
import com.guntherdw.bukkit.tweakcraft.Commands.Admin.*;
import com.guntherdw.bukkit.tweakcraft.Commands.Essentials.*;
import com.guntherdw.bukkit.tweakcraft.Commands.General.*;
import com.guntherdw.bukkit.tweakcraft.Commands.Teleportation.*;
import com.guntherdw.bukkit.tweakcraft.Exceptions.CommandNotFoundException;
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
        commandMap.put("admin-add", new CommandAdminAdd());
        commandMap.put("admin-remove", new CommandAdminRemove());
        commandMap.put("admin-list", new CommandAdminList());
        commandMap.put("admon", new CommandAdmon());
        commandMap.put("admoff", new CommandAdmoff());
        commandMap.put("tplist", new CommandTpList());
        commandMap.put("tc", new CommandTC());

        /**
         * Essential commands
         */
        commandMap.put("ban", new CommandBan());
        commandMap.put("banlist", new CommandBanlist());
        commandMap.put("compass", new CommandCompass());
        commandMap.put("getpos", new CommandGetpos());
        commandMap.put("item", new CommandItem());
        commandMap.put("kick", new CommandKick());
        commandMap.put("listworlds", new CommandListWorlds());
        commandMap.put("me", new CommandMe());
        commandMap.put("msg", new CommandMsg());
        commandMap.put("mute", new CommandMute());
        commandMap.put("motd", new CommandMotd());
        commandMap.put("plugin", new CommandPlugin());
        commandMap.put("reply", new CommandReply());
        commandMap.put("spawn", new CommandSpawn());
        commandMap.put("setspawn", new CommandSetSpawn());
        commandMap.put("spawnmob", new CommandSpawnmob());
        commandMap.put("time", new CommandTime());
        commandMap.put("unban", new CommandUnban());
        commandMap.put("world", new CommandWorld());


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
        commandMap.put("tpoff", new CommandTpOff());
        commandMap.put("tpon", new CommandTpOn());
        commandMap.put("tpmob", new CommandTpMob());
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
