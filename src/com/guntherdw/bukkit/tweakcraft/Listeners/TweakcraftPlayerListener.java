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

package com.guntherdw.bukkit.tweakcraft.Listeners;

import com.guntherdw.bukkit.tweakcraft.Chat.ChatHandler;
import com.guntherdw.bukkit.tweakcraft.Chat.ChatMode;
import com.guntherdw.bukkit.tweakcraft.DataSources.Ban.BanHandler;
import com.guntherdw.bukkit.tweakcraft.DataSources.PersistenceClass.PlayerHistoryInfo;
import com.guntherdw.bukkit.tweakcraft.DataSources.PersistenceClass.PlayerInfo;
import com.guntherdw.bukkit.tweakcraft.DataSources.PersistenceClass.PlayerOptions;
import com.guntherdw.bukkit.tweakcraft.Events.TweakcraftUtilsEvent;
import com.guntherdw.bukkit.tweakcraft.Exceptions.ChatModeException;
import com.guntherdw.bukkit.tweakcraft.Packages.Ban;
import com.guntherdw.bukkit.tweakcraft.Packages.LocalPlayer;
import com.guntherdw.bukkit.tweakcraft.Packages.TamerMode;
import com.guntherdw.bukkit.tweakcraft.TweakcraftUtils;
import com.guntherdw.bukkit.tweakcraft.Util.UUIDResolver;
import com.guntherdw.bukkit.tweakcraft.Worlds.WorldManager;
import com.guntherdw.bukkit.tweakcraft.Worlds.iWorld;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;

import java.io.IOException;
import java.util.*;

/**
 * @author GuntherDW
 */
public class TweakcraftPlayerListener implements Listener {

    private final TweakcraftUtils plugin;
    private Set<String> invisplayers;
    private Map<String, String> nicks;
    private Map<String, String> capes;
    private Map<String, String> skins;
    private Set<PlayerInfo> playerinfo = new HashSet<PlayerInfo>();
    private Set<PlayerOptions> playeroptions = new HashSet<PlayerOptions>();
    private Set<String> nomount = new HashSet<String>();

    public TweakcraftPlayerListener(TweakcraftUtils instance) {
        plugin = instance;
        invisplayers = new HashSet<String>();
        nicks = new HashMap<String, String>();
        capes = new HashMap<String, String>();
        skins = new HashMap<String, String>();
    }

    public Set<String> getNomount() {
        return nomount;
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
        if (event.isCancelled()) return;

        final String line = event.getMessage();
        final LocalPlayer lp = plugin.wrapPlayer(event.getPlayer());

        String cmd = event.getMessage();

        List<String> args = new ArrayList<String>();
        if (line.contains(" ")) {
            String c[] = cmd.split(" ");
            cmd = c[0];
            args.addAll(Arrays.asList(c).subList(1, c.length));
        }
        if (cmd.startsWith("/") && cmd.length() > 1)
            cmd = cmd.substring(1);

        boolean go = true;
        PluginCommand pcmd = this.plugin.getCommand(cmd);

        if (pcmd != null && plugin.getCommandHandler().getCommandMap().containsKey(pcmd.getName()))
            go = false;


        /* if (pcmd == null && plugin.getConfigHandler().enableInjectedCommandsHanding) {
            // Check if it's been internally handled or injected
            Method method = plugin.getCommandHandler().getCommand(cmd.toLowerCase());
            if (method != null) {
                event.setCancelled(true);
                plugin.getCommandHandler().executeCommand(event.getPlayer(), cmd, args.toArray(new String[args.size()]));
                return;
            }
        } */

        if (go && plugin.getConfigHandler().extraLogging) {
            plugin.getLogger().info("[TweakLog] " + (lp.isInvisible()?"[INVIS] ":"") + event.getPlayer().getName() + " issued: " + line);
        }
    }

    public void removeNoMountPersistence(String playername) {
        if (plugin.getConfigHandler().enablePersistence) {
            List<PlayerOptions> po = plugin.getDatabase().find(PlayerOptions.class).where().ieq("name", playername).ieq("optionname", "nomount").findList();
            if (po == null || po.isEmpty())
                return;
            plugin.getDatabase().delete(po);
        }
    }

    public void addNoMountPersistence(String playername) {
        if (plugin.getConfigHandler().enablePersistence) {
            List<PlayerOptions> popts = plugin.getDatabase().find(PlayerOptions.class).where().ieq("name", playername).ieq("optionname", "nomount").findList();
            if (popts != null && !popts.isEmpty())
                removeNoMountPersistence(playername);

            // popts = new ArrayList<PlayerOptions>();
            PlayerOptions po = new PlayerOptions();
            po.setName(playername);
            po.setOptionname("nomount");
            plugin.getDatabase().save(po);
        }
    }

    public void setNick(String player, String nick) {
        nicks.put(player, nick);
        LocalPlayer lp = plugin.wrapPlayer(player);
        lp.setNick(nick);
        if (plugin.getConfigHandler().enablePersistence) {
            PlayerInfo pi = plugin.getDatabase().find(PlayerInfo.class).where().ieq("name", player).findUnique();
            if (pi == null) {
                pi = new PlayerInfo();
                pi.setName(player);
            }
            pi.setNick(nick);
            plugin.getDatabase().save(pi);
        }

        TweakcraftUtilsEvent event = new TweakcraftUtilsEvent(TweakcraftUtilsEvent.Action.NICK_CHANGED);
        event.setPlayer(lp);
        plugin.getServer().getPluginManager().callEvent(event);
    }

    public boolean removeNick(String player) {
        LocalPlayer lp = plugin.wrapPlayer(player);

        if (lp.hasNick()) {
            nicks.remove(player);
            if (plugin.getConfigHandler().enablePersistence) {
                PlayerInfo pi = plugin.getDatabase().find(PlayerInfo.class).where().ieq("name", player).findUnique();
                if (pi == null) {
                    pi = new PlayerInfo();
                    pi.setName(player);
                }
                pi.setNick((String) null);
                lp.setNick(null);
                plugin.getDatabase().update(pi);
            }
            /* if (plugin.getClientBridge() != null) {
                for(Player p : plugin.getServer().getOnlinePlayers()) {
                    plugin.getClientBridge().getPlayerListener().sendPlayerInfo(p, lp.getBukkitPlayerSafe().getName(), true);
                }
            } */
            TweakcraftUtilsEvent event = new TweakcraftUtilsEvent(TweakcraftUtilsEvent.Action.NICK_CHANGED);
            event.setPlayer(lp);
            plugin.getServer().getPluginManager().callEvent(event);

            return true;
        } else {
            return false;
        }
    }

    public String getNick(String player) {
        if (nicks.containsKey(player)) {
            return nicks.get(player);
        } else {
            return null;
        }
    }

    public Player findPlayerByNick(String nick) {
        return findPlayerByNick(nick, false);
    }

    public String findPlayerNameByNick(String nick) {
        return findPlayerNameByNick(nick, false);
    }

    public Player findPlayerByNick(String nick, boolean exact) {

        String p = null;
        String n = null;

        for (Map.Entry<String, String> part : nicks.entrySet()) {
            String c1 = ChatColor.stripColor(part.getValue()).toLowerCase();
            String c2 = ChatColor.stripColor(nick).toLowerCase();
            if (exact ? c1.equals(c2)
                      : c1.contains(c2)) {
                p = part.getKey();
            }
        }

        if (p != null) {
            return plugin.getServer().getPlayer(p);
        }
        return null;
    }

    public List<Player> findPlayersByNick(String nick) {

        String n = null;
        List<Player> playerlijst = new ArrayList<Player>();
        for (Map.Entry<String, String> part : nicks.entrySet()) {
            // n = nicks.get(part);
            if (ChatColor.stripColor(part.getValue()).toLowerCase().contains(nick.toLowerCase())) {
                Player player = plugin.getServer().getPlayerExact(part.getKey());
                if (player != null) playerlijst.add(player);
            }
        }
        return playerlijst;
    }

    public String findPlayerNameByNick(String nick, boolean strict) {

        String p = null;
        String n = null;

        for (Map.Entry<String, String> part : nicks.entrySet()) {
            // n = nicks.get(part);
            String c1 = ChatColor.stripColor(part.getValue()).toLowerCase();
            String c2 = ChatColor.stripColor(nick).toLowerCase();
            if (strict ? c1.equals(c2)
                       : c1.contains(c2)) {
                // p = part;
                p = part.getKey();
                if (strict) return p;
            }
        }

        return p;
    }

    public boolean nickTaken(String nick) {
        return nicks.values().contains(nick);
    }

    public boolean nickTakenCheck(String playername, String nick) {
        if (nicks.values().contains(nick)) {
            return !getNick(playername).equals(nick);
        }
        return false;
    }

    public boolean nickTakenPersistance(String playername, String nick) {
        if (plugin.getConfigHandler().enablePersistence) {
            PlayerInfo pi = plugin.getDatabase().find(PlayerInfo.class).where().ieq("nick", nick).findUnique();
            if (pi != null) {
                if (!pi.getName().equals(playername)) {
                    return true;
                }
            }
        }
        return false;
    }

    public Set<String> getInvisplayers() {
        return invisplayers;
    }

    public void reloadInfo() {
        playerinfo = plugin.getDatabase().find(PlayerInfo.class).findSet();

        nicks.clear();
        capes.clear();
        for (PlayerInfo pi : playerinfo) {
            LocalPlayer lp = plugin.wrapPlayer(pi.getName());
            if (pi.getNick() != null) {
                if (plugin.getConfigHandler().enableDebug)
                    plugin.getLogger().info("Setting " + pi.getName() + "'s nick to " + pi.getNick());
                nicks.put(pi.getName(), pi.getNick());
                pi.setNick(pi.getNick());
                lp.setNick(pi.getNick());
            }
        }
        playeroptions = plugin.getDatabase().find(PlayerOptions.class).findSet();
        nomount.clear();
        for (PlayerOptions po : playeroptions) {
            if (po.getOptionname().equals("nomount")) {
                if (plugin.getConfigHandler().enableDebug)
                    plugin.getLogger().info("Setting " + po.getName() + "'s no-ride option!");
                nomount.add(po.getName());
            }
            if (po.getOptionname().equals("mute")) {
                if (plugin.getConfigHandler().enableDebug)
                    plugin.getLogger().info("Setting " + po.getName() + "'s mute option!");
                Long toTime = null;
                try {
                    if (po.getOptionvalue() != null) {
                        toTime = Long.parseLong(po.getOptionvalue());
                    }
                } catch (NumberFormatException ex) {
                    toTime = null;
                }
                plugin.getChathandler().updateMute(po.getName(), toTime);
            }
            if(po.getOptionname().equals("capeurl")) {
                if (plugin.getConfigHandler().enableDebug)
                    plugin.getLogger().info("Setting " + po.getName() + "'s CapeURL option!");
                LocalPlayer lp = plugin.wrapPlayer(po.getName());
                lp.setCapeURL(po.getOptionvalue());
                capes.put(po.getName(), po.getOptionvalue());
            }
            if (po.getOptionname().equals("skinurl")) {
                if (plugin.getConfigHandler().enableDebug)
                    plugin.getLogger().info("Setting " + po.getName() + "'s SkinURL option!");
                LocalPlayer lp = plugin.wrapPlayer(po.getName());
                lp.setSkinURL(po.getOptionvalue());
                skins.put(po.getName(), po.getOptionvalue());
            }
        }
        /* if(plugin.getClientBridge() != null) {
            plugin.getClientBridge().getPlayerListener().reloadInfo();
        } */
        plugin.getServer().getPluginManager().callEvent(new TweakcraftUtilsEvent(TweakcraftUtilsEvent.Action.RELOAD_INFO));
    }
    
    public Map<String, String> getNicks() {
        return nicks;
    }

    public Map<String, String> getCapeURLs() {
        return capes;
    }

    public Map<String, String> getSkinURLs() {
        return skins;
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        if (event.isCancelled()) return;

        Player player = event.getPlayer();
        String message = event.getMessage();
        String name = player.getName();
        String displayName = plugin.getNickWithColors(player.getName());
        String ldisplayname = displayName.substring(0, displayName.length() - 2);
        player.setDisplayName(displayName);
        if (ldisplayname.length() <= 16) {
            try {
                player.setPlayerListName(ldisplayname);
            } catch (IllegalArgumentException ex) {
                ;
            }
        }

        ChatHandler ch = plugin.getChathandler();
        ChatMode cm = ch.getPlayerChatMode(player);

        if (!ch.canTalk(player.getName())) {
            player.sendMessage(ChatColor.GOLD + "You are muted! No one can hear you.");
            plugin.getLogger().info("Muted player message : <" + event.getPlayer().getName() + "> " + event.getMessage());
            event.setCancelled(true);
            return;
        }

        if (plugin.getConfigHandler().enableSpamControl) {
            int counter = 0;
            counter = ch.getAntiSpam().checkSpam(player, message);

            if (counter > (plugin.getConfigHandler().spamMaxMessages - 1)) {
                plugin.getLogger().info(player.getName() + " has been auto-muted for spamming!");
                long until = plugin.getConfigHandler().spamMuteMinutes * 60;
                ch.addMute(player.getName().toLowerCase(), until);
                String msg = plugin.getConfigHandler().spamMuteMessage.trim();
                if (!msg.equals("")) {

                    msg = msg.replace("{name}", name).
                        replace("{displayname}", player.getDisplayName()).
                        replace("{mins}", plugin.getConfigHandler().spamMuteMinutes + "").
                        replace("&&", "{orly}").
                        replace("&", "§").
                        replace("{orly}", "&");

                    plugin.getServer().broadcastMessage(msg);
                }
                event.setCancelled(true);
                return;
            }
        }

        if (cm != null) {
            if (!message.startsWith(plugin.getChathandler().getBypassChar())) {
                cm.sendMessage(player, message);
                event.setCancelled(true);
            } else {
                message = message.substring(1);
                event.setMessage(message);
                if (!(message.length() > 0)) {
                    event.setCancelled(true);
                    return;
                }
            }
        } else if (cm == null && getInvisplayers().contains(event.getPlayer().getName())) {
            event.getPlayer().sendMessage(ChatColor.RED + "Are you insane? You're invisible, set a chatmode!");
            event.setCancelled(true);
        }

        if (!event.isCancelled() && cm == null) {
            // Log nicks!
            if (getNick(name) != null) {
                //boolean c = plugin.getConfigHandler().cancelNickChat;
                if (plugin.getConfigHandler().cancelNickChat) {
                    event.setCancelled(true);
                    plugin.getLogger().info("(" + player.getName() + ")  <" + player.getDisplayName() + "> " + message);
                    // plugin.getServer().broadcastMessage(ChatColor.WHITE+"<"+player.getDisplayName()+ChatColor.WHITE+"> "+message);
                    for (Player p : plugin.getServer().getOnlinePlayers())
                        p.sendMessage(ChatColor.WHITE + "<" + player.getDisplayName() + ChatColor.WHITE + "> " + message);
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        // if(event.isBedSpawn())
        if (!plugin.getConfigHandler().enableRespawnHook) return;

        Player p = event.getPlayer();
        String fromworld = event.getPlayer().getWorld().getName();
        boolean isnether = fromworld.endsWith("_nether") || fromworld.endsWith("_the_end");
        if (isnether) fromworld = fromworld.substring(0, fromworld.length() - 7); // MINUS _nether
        World w = plugin.getServer().getWorld(fromworld);
        if (w != null) {
            if (event.isBedSpawn()) {
                if (!event.getRespawnLocation().getWorld().getName().equals(w.getName())) {
                    p.sendMessage(ChatColor.AQUA + "Your bed was in another world, sending you to spawn!");
                    event.setRespawnLocation(w.getSpawnLocation());
                }
            } else {
                if (!event.getRespawnLocation().getWorld().getName().equals(w.getName())) {
                    p.sendMessage(ChatColor.AQUA + "Your respawn place was in another world, sending you to spawn!");
                    event.setRespawnLocation(w.getSpawnLocation()); // Removed isNether() check
                }

            }
        }
        if (plugin.getConfigHandler().enableRespawnHeal)
            p.setHealth(20);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerTeleport(PlayerTeleportEvent event) {
        if (event.isCancelled()) return;
        if (event.getFrom().getWorld() != event.getTo().getWorld()) { // The world is different, make a check!
            Player player = event.getPlayer();
            if (plugin.getConfigHandler().enablemod_InfDura) {
                plugin.sendToolDuraMode(player, event.getTo().getWorld());
            }
            if (!plugin.check(player, "worlds." + event.getTo().getWorld().getName())) {
                event.setCancelled(true);
                player.sendMessage(ChatColor.RED + "You don't have access to this world!");
                return;
            }
            iWorld tw = plugin.getworldManager().getWorld(event.getTo().getWorld().getName(), true);
            if (tw != null && !tw.getGameMode().equals(player.getGameMode())) {
                player.setGameMode(tw.getGameMode());
            }
            if (plugin.getConfigHandler().enableFallDistanceNullify) player.setFallDistance(0);
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerLogin(PlayerLoginEvent event) {
        BanHandler handler = plugin.getBanhandler();

        Player p = event.getPlayer();
        UUID playerUUID = p.getUniqueId();

        /* Check for profile, update/link playerName if required. */
        // TODO: check when it actually is allowed to change names
        plugin.getUUIDResolver().checkProfile(p);

        Ban isBanned = handler.isBannedBan(playerUUID);
        if (isBanned != null) {
            event.disallow(PlayerLoginEvent.Result.KICK_BANNED, isBanned.getReason());
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player p = event.getPlayer();
        LocalPlayer lp = plugin.wrapPlayer(p);
        String displayName  = plugin.getNickWithColors(p.getName());
        String ldisplayname = displayName.substring(0, displayName.length() - 2);

        p.setDisplayName(displayName);
        if (ldisplayname.length() <= 16)
            try {
                p.setPlayerListName(ldisplayname);
            } catch (IllegalArgumentException ex) {
                ;
            }
        // p.sendMessage("Ohai thar!");
        for (String m : plugin.getMOTD()) {
            p.sendMessage(m);
        }

        // event.setJoinMessage(ChatColor.YELLOW + displayName + ChatColor.YELLOW +" joined the game.");
        event.setJoinMessage(ChatColor.GREEN + " + " + displayName + ChatColor.DARK_GRAY + " / joined the game.");

        if (lp.isInvisible()) { // Invisible players do not send out a "joined" message
            event.setJoinMessage(null);
            p.sendMessage(ChatColor.AQUA + "You has joined STEALTHILY!");
            for (Player play : plugin.getServer().getOnlinePlayers()) {
                if (plugin.check(play, "tpinvis"))
                    play.sendMessage(ChatColor.AQUA + "Stealth join : " + event.getPlayer().getDisplayName());
                else {
                    if(plugin.getConfigHandler().enableVanish)
                        play.hidePlayer(p);
                }
            }
        }

        if (plugin.getConfigHandler().enableCUI) {
            // String CUIPattern =
            plugin.sendCUIHandShake(p);
            // plugin.sendCUIChatMode(p);
        }

        for(String playerName : getInvisplayers()) {
            Player ptest = plugin.getServer().getPlayer(playerName);
            if (ptest != null && !p.hasPermission("tpinvis")) {
                p.hidePlayer(ptest);
            }
        }

        if (plugin.getConfigHandler().enablemod_InfDura) {
            if (!plugin.getMod_InfDuraplayers().contains(p)) {
                plugin.sendmod_InfDuraHandshake(p);
            }
            plugin.sendmod_InfDuraHandshake(p);
        }

        iWorld iw = plugin.getworldManager().getWorld(p.getWorld().getName());

        if(lp.isAllowedFlight() || (iw!=null && iw.isAllowFlight()))
            p.setAllowFlight(true);
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerQuit(PlayerQuitEvent event) {

        Player player = event.getPlayer();
        LocalPlayer lp = plugin.wrapPlayer(player);
        String name = player.getName();

        if (plugin.getConfigHandler().enableSeenConfig) {
            Calendar cal = Calendar.getInstance();
            if (!plugin.getConfigHandler().enablePersistence) {
                String time = String.valueOf(cal.getTime().getTime());
                plugin.getConfigHandler().getSeenconfig().set(name.toLowerCase(), time);
                try {
                    plugin.getConfigHandler().saveSeenConfig();
                } catch (IOException ex) {
                    plugin.getLogger().info("Couldn't save SeenConfig file!");
                }
                // plugin.getConfigHandler().getSeenconfig().save();
            } else {
                if (plugin.getConfigHandler().useTweakBotSeen) {
                    PlayerHistoryInfo phi = plugin.getDatabase().find(PlayerHistoryInfo.class).where().ieq("nickname", name).findUnique();
                    if (phi == null) {
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
                    if (pi == null) {
                        pi = new PlayerInfo();
                        pi.setName(name);
                    }
                    pi.setLastseen(cal.getTime().getTime());
                    plugin.getDatabase().save(pi);
                }
            }
            if (plugin.getConfigHandler().enableDebug)
                plugin.getLogger().info("Stored " + name + "'s logout!");
        }
        plugin.getChathandler().removePlayer(event.getPlayer());
        try {
            plugin.getChathandler().setPlayerchatmode(name, null);
        } catch (ChatModeException e) {
            plugin.getLogger().severe("Error setting ChatMode to null after the logout!");
        }


        // if (plugin.hasNick(name)) {
        // event.setQuitMessage(ChatColor.YELLOW + player.getDisplayName() + ChatColor.YELLOW + " left the game.");
        event.setQuitMessage(ChatColor.RED + " - " + player.getDisplayName() + ChatColor.DARK_GRAY + " / left the game.");
        // }

        if (lp.isInvisible()) { // Invisible players do not send out a "left" message
            event.setQuitMessage(null);
            /* if (plugin.getCraftIRC() != null) {
                plugin.getCraftIRC().sendMessageToTag("STEALTH QUIT : " +name ,"mchatadmin");
            } */
            for (Player play : plugin.getServer().getOnlinePlayers()) {
                if (plugin.check(play, "tpinvis")) {
                    play.sendMessage(ChatColor.AQUA + "Stealth quit : " + event.getPlayer().getDisplayName());
                }
            }
        }
    }

    /**
     * I still don't get why my event.getItem or i keeps on nulling out, if anyone can help, please do
     */
    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerInteract(PlayerInteractEvent event) {

        Player player = event.getPlayer();
        String playername = player.getName();
        if (plugin.getConfigHandler().getLsbindmap().containsKey(playername)) {
            Map<Integer, Boolean> bind = plugin.getConfigHandler().getLsbindmap().get(playername);
            for (Integer i : bind.keySet()) {
                if (i == null) {
                    event.getPlayer().sendMessage(ChatColor.RED + "[TweakcraftUtils] onPlayerInteract Null error!");
                    plugin.getLogger().info(playername + " triggered a i == null event!");
                } else {

                    if (event.isCancelled()) {
                        if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
                            int blockid = event.getClickedBlock().getTypeId();
                            if (blockid == Material.CHEST.getId()
                                || blockid == Material.FURNACE.getId()
                                || blockid == Material.DIODE_BLOCK_OFF.getId()
                                || blockid == Material.DIODE_BLOCK_ON.getId()
                                || blockid == Material.DISPENSER.getId())
                                return;
                        }
                    }

                    if ((event.getItem() == null && i == 0)
                        || (event.getItem() != null && event.getItem().getTypeId() == i)) {
                        Action a = event.getAction();
                        if ((!bind.get(i) && (a.equals(Action.RIGHT_CLICK_AIR) || a.equals(Action.RIGHT_CLICK_BLOCK)))
                            || (bind.get(i) && (a.equals(Action.LEFT_CLICK_AIR) || a.equals(Action.LEFT_CLICK_BLOCK)))) {
                            Location target = null;
                            if (plugin.getConfigHandler().getLockdowns().containsKey(playername)) {
                                target = plugin.getConfigHandler().getLockdowns().get(playername).getTarget();
                            } else {
                                int maxDistance = player.getServer().getViewDistance() * 16;
                                Location loc = player.getTargetBlock((HashSet<Byte>) null, maxDistance).getLocation();
                                loc.setY(loc.getY() + 1);
                                target = loc.clone();
                            }
                            if (target != null) {
                                target.getWorld().strikeLightning(target);
                            }
                        }
                    }
                }
            }
        }
        if ((event.getItem() != null && event.getItem().getType() == Material.DIAMOND_SWORD)
            && (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK)) {
            // player.sendMessage("Getting Vehicle!");
            for (LivingEntity ent : player.getWorld().getLivingEntities()) { // There is no easy way, meh
                Entity passenger = ent.getPassenger();
                if (passenger != null)
                    if (passenger.equals(player)) {
                        // player.sendMessage("Dropping you!");
                        ent.eject();
                    }
            }
        }

    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerKick(PlayerKickEvent event) {
        if (event.isCancelled()) return;

        Player p = event.getPlayer();
        // String nick = getNick(p.getName());

        if (invisplayers.contains(p.getName())) {
            event.setLeaveMessage(null);
            return;
        }

        if(!plugin.getConfigHandler().enableKickMessage)
            event.setLeaveMessage(ChatColor.YELLOW + p.getDisplayName() + ChatColor.YELLOW + " left the game.");
        else {
            event.setLeaveMessage(null);
            plugin.getServer().broadcastMessage(ChatColor.YELLOW + p.getDisplayName() + ChatColor.YELLOW + " has been kicked.");
            plugin.getServer().broadcastMessage(ChatColor.YELLOW+"Reason : ("+ChatColor.WHITE+event.getReason()+ChatColor.YELLOW+")");
            // BukkitScheduler bs = plugin.getServer().getScheduler();
            /**
             * Make it a thread to be sure that it
             */
            /** bs.scheduleAsyncDelayedTask(plugin, new Runnable() {
                public void run() {
                    plugin.getServer().broadcastMessage(ChatColor.YELLOW+"Reason : ("+ChatColor.WHITE+event.getReason()+ChatColor.YELLOW+")");
                }
            }, 5); */
        }
    }

    public void reloadInvisTable() {
        List<String> lijst = plugin.getConfigHandler().getGlobalconfig().getStringList("invisible.playerList");

        for (String s : this.invisplayers) {
            LocalPlayer lp = plugin.wrapPlayer(s);
            plugin.getLogger().info("Removing " + s + " from the invisble playerlist!");
            lp.setInvisible(false);
        }
        this.invisplayers.clear();

            /* Clear the old playerlist, there could be old players on there,
               who have been tagged as visible for now. */


        if (lijst != null) {
            this.invisplayers.addAll(lijst);
            /* Add any old and new players who have been tagged as invisible. */
            for (String s : lijst) {
                LocalPlayer lp = plugin.wrapPlayer(s);
                lp.setInvisible(true);
                // if (plugin.getConfigHandler().enableDebug)
                    plugin.getLogger().info("Adding " + s + " to the invisble playerlist!");
            }
        }
    }

    private boolean isTweakTravelSign(Block block) {
        BlockState state = block.getState();
        return state.getType().equals(Material.WALL_SIGN) && ((Sign) state).getLine(0).equals("[TweakTravel]");

    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerPickupItem(PlayerPickupItemEvent event) {
        final Player player = event.getPlayer();
        final LocalPlayer lp = plugin.wrapPlayer(player);
        if(lp.isInvisible() && !lp.isInvisiblePickup())
            event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerPortal(PlayerPortalEvent event) {
        if (event.isCancelled()) return;

        Player p = event.getPlayer();
        Location from = event.getFrom();
        World worldFrom = from.getWorld();

        boolean signMode = false;
        if (plugin.getConfigHandler().enableTweakTravel) { /* Go look for a portalSign */
            Block signBlock = null;
            Location ploc = p.getLocation();
            int searchwidth = plugin.getConfigHandler().tweakTravelSearchWidth;
            for (int i = ploc.getBlockX() - searchwidth; i < ploc.getBlockX() + searchwidth; i++) {
                for (int j = ploc.getBlockY() - searchwidth; j < ploc.getBlockY() + searchwidth; j++) {
                    for (int k = ploc.getBlockZ() - searchwidth; k < ploc.getBlockZ() + searchwidth; k++) {
                        Block b = worldFrom.getBlockAt(i, j, k);
                        if (isTweakTravelSign(b)) {
                            if (signBlock == null) {
                                signBlock = b;
                                p.sendMessage(ChatColor.YELLOW + "TweakTravel : taking you to " + (((Sign) b.getState()).getLine(1)));
                            } else {
                                plugin.getLogger().warning("Found more than one TweakTravel sign");
                                Location lold = signBlock.getLocation();
                                Location lnew = b.getLocation();
                                plugin.getLogger().warning("original location : " + lold);
                                plugin.getLogger().warning("other : " + lnew);

                                p.sendMessage(ChatColor.RED + "WARNING: Found more than one TweakTravel sign!");
                            }
                        }
                    }
                }
            }

            if (signBlock != null) {
                signMode = true;
                Sign sign = (Sign) signBlock.getState();
                String toWorld = sign.getLine(1);
                String line3 = sign.getLine(2);

                // if(line3.length()>3)
                // iWorld iw = plugin.getworldManager().getWorld(toWorld);
                World world = plugin.getServer().getWorld(toWorld);
                if (world != null && !plugin.check(p, "worlds." + world.getName())) {
                    p.sendMessage(ChatColor.RED + "You do not have permission to portal to this world!");
                    event.setCancelled(true);
                } else {
                    if (world != null) {
                        String[] coordsStrings = null;
                        Integer[] coords = new Integer[3];
                        coordsStrings = line3.split(",");
                        Location loc = world.getSpawnLocation();
                        if(line3.trim().equalsIgnoreCase("personal") && plugin.getConfigHandler().enablePersistence) {
                            List<PlayerOptions> plist = plugin.getDatabase().find(PlayerOptions.class).where().ieq("name", event.getPlayer().getName()).ieq("optionname", "worldpos").findList();
                            PlayerOptions po = null;
                            if (plist.size() > 0) {
                                for (PlayerOptions popts : plist) {
                                    Location tloc = plugin.getCommandHandler().essentialsCommands.parseLocationString(popts.getOptionvalue());
                                    if (tloc != null) {
                                        if (tloc.getWorld().getName().equals(event.getPlayer().getWorld().getName())) {
                                            po = popts;
                                        } else if (tloc.getWorld().getName().equals(world.getName())) {
                                            loc = tloc;
                                        }
                                    }
                                }
                            }
                        } else if (coordsStrings.length == 3) {
                            coords[0] = Integer.parseInt(coordsStrings[0]);
                            coords[1] = Integer.parseInt(coordsStrings[1]);
                            coords[2] = Integer.parseInt(coordsStrings[2]);
                            loc = new Location(world, coords[0], coords[1], coords[2]);
                        }


                        TravelAgent ta = event.getPortalTravelAgent();
                        event.useTravelAgent(true);


                        if (loc != null) {
                            event.setTo(loc);
                        } else {
                            event.setTo(world.getSpawnLocation());
                        }

                        ta.setSearchRadius(0);

                        ta.setCanCreatePortal(false);
                        event.setPortalTravelAgent(ta);

                    } else {
                        p.sendMessage(ChatColor.RED + "Couldn't find that world!");
                        event.setCancelled(true);
                    }
                }
            }
        }


        if (!signMode) {
            String fromworld = event.getFrom().getWorld().getName();
            boolean isnether = fromworld.endsWith("_nether") || fromworld.endsWith("_the_end");
            if (isnether) fromworld = fromworld.substring(0, fromworld.length() - 7); // MINUS _nether
            iWorld w = plugin.getworldManager().getWorld(fromworld);

            if (w != null && w.getBukkitWorld() != plugin.getworldManager().getDefaultWorld()) {

                if (!w.isNetherEnabled()) {
                    if (plugin.getConfigHandler().cancelNetherPortal) event.setCancelled(true);
                    event.getPlayer().sendMessage(ChatColor.RED + "This world doesn't have an extra nether!");

                    return;
                }

                org.bukkit.Location to = event.getFrom();
                if (isnether) {
                    to.setWorld(w.getBukkitWorld());
                    to.setX(Math.floor(to.getX() * 8));
                    to.setZ(Math.floor(to.getZ() * 8));
                } else {
                    if (w.isTheEndEnabled() && event.getCause() == PlayerTeleportEvent.TeleportCause.END_PORTAL) {
                        to.setWorld(w.getThe_endWorld());
                        to.setX(Math.floor(to.getX() * 8));
                        to.setZ(Math.floor(to.getZ() * 8));
                    } else {
                        to.setWorld(w.getNetherWorld());
                        to.setX(Math.floor(to.getX() / 8));
                        to.setZ(Math.floor(to.getZ() / 8));
                    }
                }

                TravelAgent agent = event.getPortalTravelAgent();
                event.setTo(to);
                event.useTravelAgent(true);
                int radius = w.getPortalSearchWidth();
                agent.setSearchRadius(radius);
                event.setPortalTravelAgent(agent);
            }
        }
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
        Entity entity = event.getRightClicked();
        Player player = event.getPlayer();
        if (plugin.getConfigHandler().enabletamertool) {
            if (player.getItemInHand() != null) {

                if (entity instanceof Tameable && player.getItemInHand().getTypeId() == plugin.getConfigHandler().tamertoolid) {
                    if (plugin.getTamerTool().getTamers().containsKey(player)) {
                        event.setCancelled(true);
                        plugin.getTamerTool().handleEvent(player, (Tameable) entity);
                        return;
                    }
                } else if (entity instanceof Animals) {

                    Animals animal = (Animals) entity;

                    if (player.getItemInHand().getTypeId() == plugin.getConfigHandler().tamertoolid) {

                        event.setCancelled(true);
                        TamerMode mode = plugin.getTamerTool().getTamers().get(player);
                        if (mode == null) {
                            return;
                        } else if (mode.getMode() == TamerMode.TamerModes.SETAGE) {
                            if (!plugin.check(player, "tamer.setage")) {
                                player.sendMessage(ChatColor.RED + "You don't have permission to set the age of animals!");
                            } else {
                                player.sendMessage(ChatColor.BLUE + "Setting animal age to " + mode.getData());
                                animal.setAge(mode.getData());
                            }
                        } else if(mode.getMode() == TamerMode.TamerModes.SETAGELOCK) {

                            if(plugin.check(player, "tamer.setage")) {
                                if(mode.getState() == null)
                                    mode.setState(!animal.getAgeLock());
                                player.sendMessage(ChatColor.BLUE + "Setting animal agelock to " + mode.getState());
                                animal.setAgeLock(mode.getState());
                            } else {
                                player.sendMessage(ChatColor.RED + "You don't have permission to set the age of animals!");
                            }
                        } else {

                            String clsName = animal.getClass().getCanonicalName().split("Craft")[1];
                            /* if (clsName.equals("Horse"))
                               clsName = "EntityHorse"; */

                            EntityType type = EntityType.fromName(clsName);

                            if(type == null) {
                                player.sendMessage(ChatColor.RED+"Error while getting AnimalInfo.");
                                player.sendMessage(ChatColor.RED+"ClassName was "+animal.getClass().getCanonicalName());
                                return;
                            }
                            String cname = type.getName().toLowerCase();
                            player.sendMessage(ChatColor.BLUE + "Animal info : ");
                            player.sendMessage(ChatColor.BLUE + "type : " + ChatColor.YELLOW + cname);
                            player.sendMessage(ChatColor.BLUE + "health : " + ChatColor.YELLOW + animal.getHealth());
                            player.sendMessage(ChatColor.BLUE + "entityId : "+ChatColor.YELLOW + animal.getEntityId());
                            player.sendMessage(ChatColor.BLUE + "age : " + ChatColor.YELLOW + animal.getAge() + (animal.getAgeLock()? ChatColor.RED+" L":""));
                        }

                        return;

                    } else if (player.getItemInHand().getType().equals(Material.WHEAT) && animal.getAge() < 0 && !(animal instanceof Horse)) {
                        ItemStack inHand = player.getItemInHand();
                        if (inHand.getAmount() > 1)
                            inHand.setAmount(inHand.getAmount() - 1);
                        else
                            inHand = null;

                        int newAge = animal.getAge() + 1000;
                        if (newAge > 0) newAge = 0;
                        animal.setAge(newAge);

                        player.setItemInHand(inHand);

                        player.getWorld().playEffect(animal.getLocation(), Effect.POTION_BREAK, 0);
                    } else if (player.getItemInHand().getType().equals(Material.MILK_BUCKET) && animal.getAge() < 0 && !(animal instanceof Horse)) {
                        int newAge = animal.getAge() + 2500;
                        if (newAge > 0) newAge = 0;
                        animal.setAge(newAge);

                        player.setItemInHand(new ItemStack(Material.BUCKET, 1));
                        player.getWorld().playEffect(animal.getLocation(), Effect.POTION_BREAK, 0);
                    }
                } else if (entity instanceof Monster || entity instanceof Flying) {
                    if (player.getItemInHand().getTypeId() == plugin.getConfigHandler().tamertoolid) {
                        LivingEntity lent = (LivingEntity) entity;
                        EntityType type = EntityType.fromName(lent.getClass().getCanonicalName().split("Craft")[1]);
                        String cname = type.getName().toLowerCase();
                        player.sendMessage(ChatColor.BLUE + "Creature info : ");
                        player.sendMessage(ChatColor.BLUE + "type : " + ChatColor.YELLOW + cname);
                        player.sendMessage(ChatColor.BLUE + "health : " + ChatColor.YELLOW + lent.getHealth());
                    }
                } else if(entity instanceof Ageable) { // Villagers?
                    if (player.getItemInHand().getTypeId() == plugin.getConfigHandler().tamertoolid) {
                        LivingEntity lent = (LivingEntity) entity;
                        EntityType type = EntityType.fromName(lent.getClass().getCanonicalName().split("Craft")[1]);
                        Ageable a = (Ageable) lent;
                        String cname = type.getName().toLowerCase();

                        player.sendMessage(ChatColor.BLUE + "Creature info : ");
                        player.sendMessage(ChatColor.BLUE + "type : " + ChatColor.YELLOW + cname);
                        player.sendMessage(ChatColor.BLUE + "health : " + ChatColor.YELLOW + lent.getHealth());
                        player.sendMessage(ChatColor.BLUE + "age : " + ChatColor.YELLOW + a.getAge() + (a.getAgeLock()? ChatColor.RED+" L":""));

                    }
                }
            }
        }


        if (player.getItemInHand() != null && player.getItemInHand().getType() == Material.SADDLE
            && !(entity instanceof Horse)) { // Horses have their own saddle thing.
            if (entity.isEmpty()) {
                boolean allowed = true;
                if (nomount.contains(player.getName())) {
                    player.sendMessage(ChatColor.RED + "You don't allow others to sit on you either!");
                } else {
                    if (entity instanceof Player) {
                        allowed = plugin.check(player, "mount.player");
                        if (allowed) { // TODO: Make extra checks for a "do-not-mount" option
                            allowed = !nomount.contains(((Player) entity).getName());
                        }
                    } else if (entity instanceof Pig) {
                        Pig pig = (Pig) entity;
                        if (!plugin.getConfigHandler().paySaddle && !pig.hasSaddle()) {
                            ItemStack holding = player.getItemInHand();
                            if (holding != null) {
                                if (holding.getType() == Material.SADDLE) {
                                    holding.setAmount(holding.getAmount() + 1);
                                    player.setItemInHand(holding);
                                } else {
                                    player.getInventory().addItem(new ItemStack(Material.SADDLE, 1));
                                }
                            } else {
                                holding = new ItemStack(Material.SADDLE, 1);
                                player.setItemInHand(holding);
                            }

                        }
                    } else if (entity instanceof Wolf) {
                        Wolf w = (Wolf) entity;
                        if (plugin.getConfigHandler().enableAutoTame)
                            if (!w.isTamed() && plugin.check(player, "mount.autotame")) {
                                w.setOwner(player);
                                w.setAngry(false);
                                w.setSitting(false);
                                w.setHealth(20);
                                // w.sett
                            }
                    } else {
                        allowed = plugin.check(player, "mount.other");
                    }


                    if (allowed) {
                        if (plugin.getConfigHandler().paySaddle && !(entity instanceof Pig)) {
                            ItemStack holding = player.getItemInHand();
                            holding.setAmount(holding.getAmount() - 1);
                            player.setItemInHand(holding);
                        }
                        // boolean nakedpig = entity instanceof Pig && !((Pig)entity).hasSaddle();
                        /**
                         * Minecraft handles pig saddle events just fine on its own,
                         * no need to meddle with it here.
                         */
                        if (!(entity instanceof Pig)) entity.setPassenger(player);
                    } else
                        player.sendMessage(ChatColor.RED + "You are not allowed to do that!");
                }
            } else if (!(entity instanceof Pig)
                && !entity.isEmpty()
                && entity.getPassenger().equals(player)) {

                entity.eject();
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerChangedWorld(PlayerChangedWorldEvent event) {

        Player player = event.getPlayer();
        LocalPlayer lp = plugin.wrapPlayer(player);

        WorldManager wm = plugin.getworldManager();
        iWorld world = wm.getWorld(player.getWorld().getName(), true);

        if(world == null || world.isDurabilityEnabled())
            player.setAllowFlight(false);
        else if(lp.isAllowedFlight() || world.isAllowFlight() || world.getGameMode() == GameMode.CREATIVE)
            player.setAllowFlight(true);
        else if((!world.isAllowFlight() && !lp.isAllowedFlight()))
            player.setAllowFlight(false);

        if (plugin.getConfigHandler().enableWorldMOTD && (world != null && world.hasWorldMOTD())) {
            for (String line : world.getMOTD())
                player.sendMessage(line);
        }
    }
}