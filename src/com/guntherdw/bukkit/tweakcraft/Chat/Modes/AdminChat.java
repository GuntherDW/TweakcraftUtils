package com.guntherdw.bukkit.tweakcraft.Chat.Modes;

import com.guntherdw.bukkit.tweakcraft.ChatMode;
import com.guntherdw.bukkit.tweakcraft.TweakcraftUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

/**
 * @author GuntherDW
 */
public class AdminChat implements ChatMode {

    private List<String> subscribers;
    private TweakcraftUtils plugin;

    public AdminChat(TweakcraftUtils instance)
    {
        subscribers = new ArrayList<String>();
        plugin = instance;
    }

    public boolean sendMessage(CommandSender sender, String message) {

        String sendername = "";
        String pcolor = "";
        if(sender instanceof Player)
        {
            sendername = ((Player)sender).getDisplayName(); // ((Player)sender).getName();
            pcolor = ""; // getDisplayName handles this one!
        } else {
            sendername = "CONSOLE";
            pcolor = ChatColor.LIGHT_PURPLE.toString();
        }
        String msg = ChatColor.GREEN + "ADMMSG : <"+ pcolor + sendername+ChatColor.GREEN+"> " + message;

        if(sender instanceof Player && !isOnList(sender))
        {
            sender.sendMessage(msg);
        }
        for(Player p : getRecipients(sender))
        {
            p.sendMessage(msg);
        }
        plugin.getLogger().info("AMSG: <" + sendername + "> " + message);
        return true;
    }

    public List<Player> getRecipients(CommandSender sender) {
        List<Player> recp = new ArrayList<Player>();
        for(String m : subscribers)
        {
            Player p = plugin.getServer().getPlayer(m);
            if(p != null)
                recp.add(p);
        }

        for(Player p : plugin.getServer().getOnlinePlayers())
        {
            if(plugin.check(p, "admon"))
            {
                recp.add(p);
            }
        }
        return recp;
    }

    public boolean isOnList(CommandSender sender)
    {
        if(sender instanceof Player)
        {
            return getRecipients(sender).contains((Player) sender);
        } else {
            return true;
        }
    }

    public void addRecipient(String player) {
        if(!subscribers.contains(player))
        {
            subscribers.add(player);
        }
    }

    public List<Player> getAdmins() {
        List<Player> recp = new ArrayList<Player>();
        for(Player p : plugin.getServer().getOnlinePlayers())
        {
            if(plugin.check(p,  "admon"))
                recp.add(p);
        }
        return recp;
    }

    public List<String> getAdminsString() {
        List<String> admins = new ArrayList<String>();
        for(Player m : getAdmins())
        {
            admins.add(m.getName());
        }
        return admins;
    }

    public void removeRecipient(String player) {
        if(subscribers.contains(player))
        {
            subscribers.remove(player);
        }
    }

    public List<String> getSubscribers() {
        return subscribers;
    }

    public boolean sendToRealAdmins(CommandSender sender, String message)
    {
        for(Player player : getAdmins())
        {
            player.sendMessage(message);
        }
        return true;
    }
}
