package com.guntherdw.bukkit.tweakcraft;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.event.player.PlayerListener;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * @author GuntherDW
 */
public class TweakcraftPlayerListener extends PlayerListener {

    private final Logger log = Logger.getLogger("Minecraft");
    private final TweakcraftUtils plugin;

    public TweakcraftPlayerListener(TweakcraftUtils instance) {
        TweakcraftUtils.log.info("[TweakcraftUtils] PlayerListener called!");
        plugin = instance;
    }

    protected void sendLocally(Player sender, String message) {
        EntityLocation ploc = new EntityLocation(sender);
        int playersfound = 0;
        for (Player p : plugin.getServer().getOnlinePlayers()) {
           if(ploc.getDistance(p) < plugin.maxRange) {
                p.sendMessage("L: [" + sender.getDisplayName() + "]: " + message);
                playersfound++;
            }
        }
        if(playersfound < 2)
        {
            sender.sendMessage(ChatColor.GOLD + "Noone can hear you!");
        }
    }

    public void onPlayerChat(PlayerChatEvent event) {

        Player player = event.getPlayer();
        String message = event.getMessage();
        String name = player.getName();
        // if(!event.isCancelled())
        // {
            player.setDisplayName(plugin.getPlayerColor(name, false) + name + ChatColor.WHITE);
        // }

        if (TweakcraftUtils.autolist.contains(name)) {
            if (!message.startsWith("!")) {
                sendToAdmins(player, event.getMessage(), false);
                log.info("AMSG: <" + name + "> " + message);
                event.setCancelled(true);
            } else {
                event.setMessage(message.substring(1));
            }
        } else if(TweakcraftUtils.localchatlist.contains(name)) {
            if(!message.startsWith("!"))
            {
                log.info("L: <"+name+"> "+message);
                sendLocally(player, message);
                event.setCancelled(true);
            } else {
                // event.setFormat("[g] " + event.getFormat());
                event.setMessage(message.substring(1));
            }
        }

    }

    protected void sendToAdmins(Player sender, String message, boolean realadmins) {
        String sendername = sender.getName();
        // List<String> msg = new ArrayList<String>();
        String msg = "";
        int x;
        // String color = plugin.getPlayerColor(sendername, false);
        if (!realadmins)
            msg = ChatColor.GREEN + "ADMMSG : <"
                    + plugin.getPlayerColor(sendername, false) + sendername + ChatColor.GREEN + "> " + message;
        else
            msg = message;

        for (Player p : plugin.getServer().getOnlinePlayers()) {
            if (plugin.check(p, "tweakcraftutils.adminmsg")
                    || (plugin.addlist.contains(p.getName()) && !realadmins)) {

                p.sendMessage((realadmins ? ChatColor.YELLOW : ChatColor.GREEN) + msg + " ");

            } else if (sendername == p.getName()) {
                p.sendMessage(ChatColor.LIGHT_PURPLE + "Message sent to admins:");
                p.sendMessage(ChatColor.GREEN + msg + " ");
            }
        }
    }

    public void onPlayerJoin(PlayerEvent event)
    {
     //    if(!event.isCancelled()) {
            // message = message.replace("%", "%%");
            String name = event.getPlayer().getName();
            event.getPlayer().setDisplayName(plugin.getPlayerColor(name, false) + name + ChatColor.WHITE);
            // event.setFormat("<" +  + "> " + message);
            // event.setMessage(message);
     //    }
    }

    public void onPlayerQuit(PlayerEvent event)
    {
        if(TweakcraftUtils.donottp.contains(event.getPlayer().getName()))
        {
            TweakcraftUtils.donottp.remove(event.getPlayer().getName());
        }
        if(TweakcraftUtils.autolist.contains(event.getPlayer().getName()))
        {
            TweakcraftUtils.autolist.remove(event.getPlayer().getName());
        }
        if(TweakcraftUtils.localchatlist.contains(event.getPlayer().getName()))
        {
            TweakcraftUtils.localchatlist.remove(event.getPlayer().getName());
        }
    }

}
