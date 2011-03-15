package com.guntherdw.bukkit.tweakcraft;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

/**
 * @author GuntherDW
 */
public interface ChatMode {

    /**
     * Sends a message in the chosen method
     * @param sender
     * @param Message
     * @return
     */
    public abstract boolean sendMessage(CommandSender sender, String Message);

    /**
     * Returns a list of players that will get the message
     * @return
     */

    public abstract List<Player> getRecipients();

    /**
     * Tries to remove a recipient
     * @param player
     */
    public abstract void addRecipient(Player player);

    /**
     * Tries to remove a recipient
     * @param player
     */
    public abstract void removeRecipient(Player player);

    /**
     * Gets the current subscribed list
     * @return
     */

    public abstract List<Player> getSubscribers();

}
