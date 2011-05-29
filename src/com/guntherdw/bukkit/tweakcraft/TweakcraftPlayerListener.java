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

package com.guntherdw.bukkit.tweakcraft;

import com.guntherdw.bukkit.tweakcraft.Ban.BanHandler;
import com.guntherdw.bukkit.tweakcraft.Chat.ChatHandler;
import com.guntherdw.bukkit.tweakcraft.Chat.ChatMode;
import com.guntherdw.bukkit.tweakcraft.DataSources.PersistenceClass.PlayerHistoryInfo;
import com.guntherdw.bukkit.tweakcraft.DataSources.PersistenceClass.PlayerInfo;
import com.guntherdw.bukkit.tweakcraft.Exceptions.ChatModeException;
import com.guntherdw.bukkit.tweakcraft.Packages.Ban;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Wolf;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;

import javax.lang.model.util.ElementScanner6;
import java.util.*;

/**
 * @author GuntherDW
 */
public class TweakcraftPlayerListener extends PlayerListener {

    //private final Logger log = Logger.getLogger("Minecraft");
    private final TweakcraftUtils plugin;
    private List<String> invisplayers;
    private Map<String, String> nicks;
    private List<PlayerInfo> playerinfo = new ArrayList<PlayerInfo>();


    public TweakcraftPlayerListener(TweakcraftUtils instance) {
        plugin = instance;
        invisplayers = new ArrayList<String>();
        nicks = new HashMap<String, String>();
    }

    public void setNick(String player, String nick) {
        nicks.put(player, nick);
        if(plugin.getConfigHandler().enablePersistence) {
            PlayerInfo pi = plugin.getDatabase().find(PlayerInfo.class).where().ieq("name", player).findUnique();
            if(pi==null) {
                pi = new PlayerInfo();
                pi.setName(player);
            }
            pi.setNick(nick);
            plugin.getDatabase().save(pi);
        }
    }

    public boolean removeNick(String player) {
        if(nicks.containsKey(player)) {
            nicks.remove(player);
            if(plugin.getConfigHandler().enablePersistence) {
                PlayerInfo pi = plugin.getDatabase().find(PlayerInfo.class).where().ieq("name", player).findUnique();
                if(pi==null) {
                    pi = new PlayerInfo();
                    pi.setName(player);
                }
                pi.setNick((String)null);
                plugin.getDatabase().save(pi);
            }
            return true;
        } else {
            return false;
        }
    }

    public String getNick(String player) {
        if(nicks.containsKey(player)) {
            return nicks.get(player);
        } else {
            return null;
        }
    }

    public Player findPlayerByNick(String nick) {

        String p = null;
        String n = null;

        for(String part : nicks.keySet()) {
            n = nicks.get(part);
            if(n.toLowerCase().contains(nick.toLowerCase())) {
                p = part;
            }
        }

        if(p!=null) {
            return plugin.getServer().getPlayer(p);
        }
        return null;
    }

    public boolean nickTaken(String nick) {
        return nicks.values().contains(nick);
    }

    public boolean nickTakenPersistance(String playername, String nick) {
        if(plugin.getConfigHandler().enablePersistence) {
            PlayerInfo pi = plugin.getDatabase().find(PlayerInfo.class).where().ieq("name", nick).findUnique();
            if(pi!=null)
            {
                if(!pi.getName().equals(playername)) {
                    return true;
                }
            }
        }
        return false;
    }

    public List<String> getInvisplayers() {
        return invisplayers;
    }

    public void reloadInfo() {
        playerinfo = plugin.getDatabase().find(PlayerInfo.class).findList();
        nicks.clear();
        for(PlayerInfo pi : playerinfo) {
            if(pi.getNick()!=null) {
                plugin.getLogger().info("[TweakcraftUtils] Setting "+pi.getName()+"'s nick to "+pi.getNick());
                nicks.put(pi.getName(), pi.getNick());
            }
        }

    }

    public void onPlayerChat(PlayerChatEvent event) {

        Player player = event.getPlayer();
        String message = event.getMessage();
        String name = player.getName();
        player.setDisplayName(plugin.getNickWithColors(player.getName()));

        ChatHandler ch = plugin.getChathandler();
        ChatMode cm = ch.getPlayerChatMode(player);

        if (ch.getMutedPlayers().contains(player.getName().toLowerCase())) {
            player.sendMessage(ChatColor.GOLD + "You are muted! No one can hear you.");
            plugin.getLogger().info("[TweakcraftUtils] Muted player message : <" + event.getPlayer().getName() + "> " + event.getMessage());
            event.setCancelled(true);
        } else {


            if (cm != null) {
                if (!message.startsWith(plugin.getChathandler().getBypassChar())) {
                    cm.sendMessage(player, message);
                    event.setCancelled(true);
                } else {
                    if(!plugin.hasNick(player.getName())) {

                        event.setMessage(message.substring(1));
                        message = event.getMessage();
                    } else {
                        message = message.substring(1);
                    }
                }
            } else if(cm == null && getInvisplayers().contains(event.getPlayer().getName())) {
                event.getPlayer().sendMessage(ChatColor.RED + "Are you insane? You're invisible, set a chatmode!");
                event.setCancelled(true);
            }
        }

        if(!event.isCancelled() && cm!=null) {
            // Log nicks!
            if(getNick(name)!=null) {
                // plugin.getLogger().info("[TweakcraftUtils] "+getNick(name)+" is "+name);
                event.setCancelled(true);
                plugin.getLogger().info("("+player.getName()+")  <"+player.getDisplayName()+"> "+message);
                /* if(plugin.getConfigHandler().enableIRC && plugin.getCraftIRC()!=null) {
                    plugin.getCraftIRC().sendMessageToTag();
                } */
                plugin.getServer().broadcastMessage(ChatColor.WHITE+"<"+player.getDisplayName()+ChatColor.WHITE+"> "+message);
            }
        }
    }

    public void onPlayerTeleport(PlayerTeleportEvent event) {
        Location floc = event.getFrom();
        Location tloc = event.getTo();
        if (floc.getWorld() != tloc.getWorld()) { // The world is different, make a check!
            Player player = event.getPlayer();
            if (!plugin.check(player, "worlds." + tloc.getWorld().getName())) {
                event.setCancelled(true);
                player.sendMessage(ChatColor.RED + "You don't have access to this world!");
            }
        }
    }


    public void onPlayerLogin(PlayerLoginEvent event) {
        BanHandler handler = plugin.getBanhandler();
        Ban isBanned = handler.searchBan(event.getPlayer().getName());
        if (isBanned != null) {
            event.disallow(PlayerLoginEvent.Result.KICK_BANNED, isBanned.getReason());
        }
    }


    public void onPlayerJoin(PlayerJoinEvent event) {
        Player p = event.getPlayer();
        String name = p.getName();
        event.getPlayer().setDisplayName(plugin.getNickWithColors(name));
        // p.sendMessage("Ohai thar!");
        for (String m : plugin.getMOTD()) {
            p.sendMessage(m);
        }


        if(plugin.hasNick(name)) {
            event.setJoinMessage(ChatColor.YELLOW + getNick(name) + " joined the game.");
        }

        if(getInvisplayers().contains(event.getPlayer().getName())) { // Invisible players do not send out a "joined" message
            event.setJoinMessage(null);
            p.sendMessage(ChatColor.AQUA + "You has joined STEALTHILY!");
            /* if (plugin.getCraftIRC() != null) {
                plugin.getCraftIRC().sendMessageToTag("STEALTH JOIN : " +event.getPlayer().getName() ,"mchatadmin");
            } */
            /* try {
                ChatHandler ch = plugin.getChathandler();
                ChatMode    cm = ch.getChatMode("admin");
                AdminChat   am = (AdminChat) cm; */
                for(Player play : plugin.getServer().getOnlinePlayers())
                {
                    if(plugin.check(play, "tpinvis"))
                    {
                        play.sendMessage(ChatColor.AQUA+"Stealth join : "+event.getPlayer().getDisplayName());
                    }
                }
                // am.broadcastMessageRealAdmins(ChatColor.AQUA+"Stealth join : "+event.getPlayer().getDisplayName());
            /* } catch (ChatModeException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            } */
        }
    }

    public void onPlayerQuit(PlayerQuitEvent event) {

        String name = event.getPlayer().getName();

        if (plugin.getConfigHandler().enableSeenConfig) {
            Calendar cal = Calendar.getInstance();
            if(!plugin.getConfigHandler().enablePersistence) {
                String time = String.valueOf(cal.getTime().getTime());
                plugin.getConfigHandler().getSeenconfig().setProperty(name.toLowerCase(), time);
                plugin.getConfigHandler().getSeenconfig().save();
            } else {
                if(plugin.getConfigHandler().useTweakBotSeen) {
                    PlayerHistoryInfo phi = plugin.getDatabase().find(PlayerHistoryInfo.class).where().ieq("nickname", name).findUnique();
                    if(phi==null) {
                        phi = new PlayerHistoryInfo();
                        phi.setNickname(name);
                    }
                    phi.setDate(cal.getTime());
                    phi.setText("");
                    phi.setAct("QUIT");
                    phi.setChannel("gameserver");
                    plugin.getDatabase().save(phi);
                } else {
                    PlayerInfo pi = plugin.getDatabase().find(PlayerInfo.class).where().ieq("name", name).findUnique();
                    if(pi==null) {
                        pi = new PlayerInfo();
                        pi.setName(name);
                    }
                    pi.setLastseen(cal.getTime().getTime());
                    plugin.getDatabase().save(pi);
                }
            }
            plugin.getLogger().info("[TweakcraftUtils] Stored " + name + "'s logout!");
        }
        plugin.getChathandler().removePlayer(event.getPlayer());
        try {
            plugin.getChathandler().setPlayerchatmode(name, null);
        } catch (ChatModeException e) {
            plugin.getLogger().severe("[TweakcraftUtils] Error setting ChatMode to null after the logout!");
        }



        if(plugin.hasNick(name)) {
            event.setQuitMessage(ChatColor.YELLOW + getNick(name) + " has left the game.");
        }

        if(getInvisplayers().contains(name)) { // Invisible players do not send out a "left" message
            event.setQuitMessage(null);
            /* if (plugin.getCraftIRC() != null) {
                plugin.getCraftIRC().sendMessageToTag("STEALTH QUIT : " +name ,"mchatadmin");
            } */
            for(Player play : plugin.getServer().getOnlinePlayers())
            {
                if(plugin.check(play, "tpinvis"))
                {
                    play.sendMessage(ChatColor.AQUA+"Stealth quit : "+event.getPlayer().getDisplayName());
                }
            }
        }
    }

    /**
     *  I still don't get why my event.getItem or i keeps on nulling out, if anyone can help, please do
     */
    public void onPlayerInteract(PlayerInteractEvent event) {
        String playername = event.getPlayer().getName();
        if(plugin.getConfigHandler().getLsbindmap().containsKey(playername)) {
            Map<Integer, Boolean> bind = plugin.getConfigHandler().getLsbindmap().get(playername);
            for(Integer i : bind.keySet()) {
                if(i == null) {
                    event.getPlayer().sendMessage(ChatColor.RED+"[TweakcraftUtils] onPlayerInteract Null error!");
                    plugin.getLogger().info("[TweakcraftUtils] "+event.getPlayer().getName()+" triggered a i == null event!");
                } else {
                    if((event.getItem()==null && i.intValue()==0)
                            || (event.getItem() != null && event.getItem().getTypeId() == i.intValue())) {
                        Action a = event.getAction();
                        if((!bind.get(i) && (a.equals(Action.RIGHT_CLICK_AIR) || a.equals(Action.RIGHT_CLICK_BLOCK)))
                                || ( bind.get(i) && (a.equals(Action. LEFT_CLICK_AIR) || a.equals(Action. LEFT_CLICK_BLOCK)))) {
                            Location target = null;
                            if(plugin.getConfigHandler().getLockdowns().containsKey(playername)) {
                                target = plugin.getConfigHandler().getLockdowns().get(playername).getTarget();
                            } else {
                                Location loc = event.getPlayer().getTargetBlock(null, 200).getLocation();
                                loc.setY(loc.getY()+1);
                                target = loc.clone();
                            }
                            if(target!=null) {
                                target.getWorld().strikeLightning(target);
                            }
                        }
                    }
                }

            }
        }
    }

    public void reloadInvisTable() {
        List<String> lijst = plugin.getConfiguration().getStringList("invisible-playerlist", null);
        this.invisplayers.clear();
        if(lijst != null)
        {
            this.invisplayers.addAll(lijst);
        }
        for(String s : lijst) {
            plugin.getLogger().info("[TweakcraftUtils] Adding "+s+" to the invisble playerlist!");
        }
    }

    public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
        Entity entity = event.getRightClicked();
        Player player = event.getPlayer();
        if(plugin.getConfigHandler().enabletamertool) {
            if(entity instanceof Wolf) {
                if(player.getItemInHand() != null &&
                   player.getItemInHand().getTypeId() == plugin.getConfigHandler().tamertoolid) {

                    if(plugin.getTamerTool().getTamers().containsKey(player)) {
                        event.setCancelled(true);
                        plugin.getTamerTool().handleEvent(player, (Wolf) entity);
                    }
                }
            }
        }
    }

}
