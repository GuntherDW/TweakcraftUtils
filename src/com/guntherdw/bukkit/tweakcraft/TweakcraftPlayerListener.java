package com.guntherdw.bukkit.tweakcraft;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.event.player.PlayerListener;

import java.util.Calendar;
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

    public void onPlayerChat(PlayerChatEvent event) {

        Player player = event.getPlayer();
        String message = event.getMessage();
        String name = player.getName();
        player.setDisplayName(plugin.getPlayerColor(name, false) + name + ChatColor.WHITE);

        ChatMode cm = plugin.getChathandler().getPlayerChatMode(player);

        if(cm != null)
        {
            if(!message.startsWith(plugin.getChathandler().getBypassChar()))
            {
                cm.sendMessage(player, message);
                event.setCancelled(true);
            } else {
                event.setMessage(message.substring(1));
            }
        }
    }

    public void onPlayerJoin(PlayerEvent event)
    {
        String name = event.getPlayer().getName();
        event.getPlayer().setDisplayName(plugin.getPlayerColor(name, false) + name + ChatColor.WHITE);
    }

    public void onPlayerQuit(PlayerEvent event)
    {
        if(plugin.isKeepplayerhistory())
        {
            Calendar cal = Calendar.getInstance();
            String time = String.valueOf(cal.getTime().getTime());
            plugin.getSeenconfig().setProperty(event.getPlayer().getName().toLowerCase(), (String) time );
            plugin.getSeenconfig().save();
            log.info("[TweakcrafUtils] Stored "+event.getPlayer().getName()+"'s logout!");
        }
        plugin.getChathandler().removePlayer(event.getPlayer());
    }

}
