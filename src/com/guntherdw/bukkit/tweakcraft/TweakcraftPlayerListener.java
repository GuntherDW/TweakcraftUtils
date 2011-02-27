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
        String sendername = sender.getName();
        Location ploc = sender.getLocation();
        Location dloc;
        Double xdiff, zdiff;
        int xdiffi, zdiffi;

        for (Player p : plugin.getServer().getOnlinePlayers()) {
            dloc = p.getLocation();
            xdiff = dloc.getX() - ploc.getX();
            xdiffi = xdiff.intValue();
            if (xdiffi < 0) {
                xdiffi = ~xdiffi + 1;
            }
            zdiff = dloc.getZ() - ploc.getZ();
            zdiffi = zdiff.intValue();
            if (zdiffi < 0) {
                zdiffi = ~zdiffi + 1;
            }
            if (zdiffi + xdiffi < plugin.maxRange) {
                p.sendMessage("[" + plugin.getPlayerColor(sender.getName(), false) + sender.getName() + ChatColor.WHITE + "]: " + message);
            }
        }
    }

    public void onPlayerChat(PlayerChatEvent event) {
        // if(event.getPlayer().getName())
        Player player = event.getPlayer();
        String message = event.getMessage();
        String name = player.getName();
        
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
                String format = event.getFormat();
                event.setFormat("[L] "+format);
                sendLocally(player, message);
                event.setCancelled(true);
            } else {
                // event.setFormat("[g] " + event.getFormat());
                event.setMessage(event.getMessage().substring(1));
            }
        }

        // for(Player p : )
        /* if(!event.getMessage().startsWith("'"))
        {
            plugin.sendLocally(event.getPlayer(), event.getMessage());
            log.info("L: <"+name+"> "+event.getMessage());
            event.setCancelled(true);
        } else {
            event.setFormat("[g] " + event.getFormat());
            event.setMessage(event.getMessage().substring(1));
        } */
    }

    protected void sendToAdmins(Player sender, String message, boolean realadmins) {
        String sendername = sender.getName();
        List<String> msg = new ArrayList<String>();
        int x;
        String color = plugin.getPlayerColor(sendername, false);
        if (!realadmins)
            msg = plugin.splitUp(ChatColor.GREEN + "ADMMSG : <"
                    + color + sendername + ChatColor.GREEN + "> " + message);
        else
            msg = plugin.splitUp(message);

        for (Player p : plugin.getServer().getOnlinePlayers()) {
            if (plugin.check(p, "tweakcraftutils.adminmsg") || (plugin.addlist.contains(p.getName()) && !realadmins)) {
                x = 0;

                for (String m : msg) {
                    //log.log(Level.INFO, "sent stuff!");
                    if (x != 0) {
                        p.sendMessage((realadmins ? ChatColor.YELLOW : ChatColor.GREEN) + m + " ");
                    } else {
                        p.sendMessage(m + " ");
                    }
                    x++;
                }
            } else if (sendername == p.getName()) {
                p.sendMessage(ChatColor.LIGHT_PURPLE + "Message sent to admins:");
                for (String m : msg) {
                    p.sendMessage(ChatColor.GREEN + m + " ");
                }
            }
        }
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

    /* public void  onPlayerCommand(PlayerChatEvent event)
    {
        
    } */
}
