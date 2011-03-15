package com.guntherdw.bukkit.tweakcraft.Chat.Modes;

import com.guntherdw.bukkit.tweakcraft.ChatMode;
import com.guntherdw.bukkit.tweakcraft.TweakcraftUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

/**
 * @author GuntherDW
 */
public class LocalChat implements ChatMode {

    private List<Player> subscribers;
    private TweakcraftUtils plugin;

    public LocalChat(TweakcraftUtils instance)
    {
        subscribers = new ArrayList<Player>();
        plugin = instance;
    }

    public boolean sendMessage(CommandSender sender, String Message) {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public List<Player> getRecipients() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public void addRecipient(Player player) {
        if(!subscribers.contains(player))
        {
            subscribers.add(player);
        }
    }

    public void removeRecipient(Player player) {
        if(subscribers.contains(player))
        {
            subscribers.remove(player);
        }
    }

    public List<Player> getSubscribers() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
