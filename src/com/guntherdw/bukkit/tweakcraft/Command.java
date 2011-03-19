package com.guntherdw.bukkit.tweakcraft;

import com.guntherdw.bukkit.tweakcraft.Exceptions.CommandException;
import com.guntherdw.bukkit.tweakcraft.Exceptions.CommandSenderException;
import com.guntherdw.bukkit.tweakcraft.Exceptions.CommandUsageException;
import com.guntherdw.bukkit.tweakcraft.Exceptions.PermissionsException;
import org.bukkit.command.CommandSender;

/**
 * @author GuntherDW
 */
public interface Command {

    /**
     * Execute a command
     *
     * @param server
     * @param sender
     * @param command
     * @param args
     * @return Wether or not the command went as planned
     */
    public abstract boolean executeCommand(CommandSender sender, String command, String[] args, TweakcraftUtils plugin)
            throws PermissionsException, CommandSenderException, CommandUsageException, CommandException;
}
