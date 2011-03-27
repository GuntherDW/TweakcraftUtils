package com.guntherdw.bukkit.tweakcraft;

import com.guntherdw.bukkit.tweakcraft.Ban.BanHandler;
import com.guntherdw.bukkit.tweakcraft.Chat.ChatHandler;
import com.guntherdw.bukkit.tweakcraft.Exceptions.ChatModeException;
import com.guntherdw.bukkit.tweakcraft.Packages.Ban;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.player.PlayerLoginEvent;

import java.io.*;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

/**
 * @author GuntherDW
 */
public class TweakcraftPlayerListener extends PlayerListener {

    private final Logger log = Logger.getLogger("Minecraft");
    private final TweakcraftUtils plugin;


    public TweakcraftPlayerListener(TweakcraftUtils instance) {
        plugin = instance;
    }

    public void onPlayerChat(PlayerChatEvent event) {

        Player player = event.getPlayer();
        String message = event.getMessage();
        String name = player.getName();
        player.setDisplayName(plugin.getPlayerColor(name, false) + name + ChatColor.WHITE);

        ChatHandler ch = plugin.getChathandler();
        if(ch.getMutedPlayers().contains(player.getName().toLowerCase()))
        {
            player.sendMessage("You are muted! No one can hear you.");
            plugin.getLogger().info("[TweakcraftUtils] Muted player message : <"+event.getPlayer().getName()+"> "+event.getMessage());
            event.setCancelled(true);
        } else {
            ChatMode cm = ch.getPlayerChatMode(player);

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
    }


    public void onPlayerLogin(PlayerLoginEvent event)
    {
        BanHandler handler = plugin.getBanhandler();
        Ban isBanned = handler.searchBan(event.getPlayer().getName());
        if(isBanned != null)
        {
            event.disallow(PlayerLoginEvent.Result.KICK_BANNED, isBanned.getReason());
        }
    }


    public void onPlayerJoin(PlayerEvent event)
    {
        Player p = event.getPlayer();
        String name = p.getName();
        event.getPlayer().setDisplayName(plugin.getPlayerColor(name, false) + name + ChatColor.WHITE);
        // p.sendMessage("Ohai thar!");
        for(String m: plugin.getMOTD())
            p.sendMessage(m);
    }

    public void onPlayerQuit(PlayerEvent event)
    {
        if(plugin.isKeepplayerhistory())
        {
            Calendar cal = Calendar.getInstance();
            String time = String.valueOf(cal.getTime().getTime());
            plugin.getSeenconfig().setProperty(event.getPlayer().getName().toLowerCase(), time );
            plugin.getSeenconfig().save();
            log.info("[TweakcrafUtils] Stored "+event.getPlayer().getName()+"'s logout!");
        }
        plugin.getChathandler().removePlayer(event.getPlayer());
        try {
            plugin.getChathandler().setPlayerchatmode(event.getPlayer().getName(), null);
        } catch(ChatModeException e) {
            plugin.getLogger().severe("[TweakcraftUtils] Error setting ChatMode to null after the logout!");
        }
    }

}
