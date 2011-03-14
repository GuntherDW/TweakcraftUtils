package com.guntherdw.bukkit.tweakcraft;

import java.util.*;
import java.util.logging.Logger;

import com.echo28.bukkit.vanish.Vanish;
import com.guntherdw.bukkit.tweakcraft.Commands.CommandHandler;
import com.guntherdw.bukkit.tweakcraft.Exceptions.CommandNotFoundException;
import com.guntherdw.bukkit.tweakcraft.Exceptions.CommandSenderException;
import com.guntherdw.bukkit.tweakcraft.Exceptions.CommandUsageException;
import com.guntherdw.bukkit.tweakcraft.Exceptions.PermissionsException;
import com.nijikokun.bukkit.Permissions.Permissions;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.Event.Priority;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * @author GuntherDW
 */

public class TweakcraftUtils extends JavaPlugin {

    private final HashMap<Player, Boolean> debugees = new HashMap<Player, Boolean>();
    public static Permissions perm = null;
    public static List<String> autolist;
    public static List<String> addlist;
    public static List<String> localchatlist;
    public final TweakcraftPlayerListener playerListener = new TweakcraftPlayerListener(this);
    // public static Server server;
    public static int maxlength = 60;
    public String col = ChatColor.YELLOW.toString();
    public String col2 = ChatColor.LIGHT_PURPLE.toString();
    private static Properties prop;
    public int playerLimit;
    public int maxRange;
    public Vanish vanish;
    public static List<String> donottp;
    private final CommandHandler commandHandler = new CommandHandler(this);

    // Packet38
    protected static final Logger log = Logger.getLogger("Minecraft");

    public String findinlist(String find, List<String> list)
    {
        for(String name : list)
        {
            if(name.toLowerCase().contains(name.toLowerCase()))
            {
                return name;
            }
        }
        return null;
    }


    private void reloadBanList()
    {
        ((CraftServer)this.getServer()).getHandle().g();
    }

    public String listToString(List<String> lijst) {
        String res = "";
        if (lijst.size() != 0) {
            for (String s : lijst) {
                res += s + ",";
            }
            res = res.substring(0, res.length() - 1);
        } else {
            res = "";
        }
        return res;
    }

    private List<String> toList(String str) {
        List<String> result = new ArrayList<String>();
        try {
            String[] names = str.split(",");
            for (String n : names)
                result.add(n.trim());

        } catch (NullPointerException e) {
            // result = new ArrayList<String>();
        }
        return result;
    }

    private String mToString(Map<String, List<String>> m) {
        String res = "";
        String pl2 = "";
        if (m.keySet().size() > 0) {
            for (String pl : m.keySet()) {
                pl2 = listToString(m.get(pl));
                res += pl + "," + pl2 + ";";
            }

            res = res.substring(0, res.length() - 1);
        } else {
            res = "";
        }
        return res;
    }

    public Map<String, List<String>> toMap(String str) {
        Map<String, List<String>> m = new HashMap<String, List<String>>();
        try {
            String[] names = str.split(";");
            for (String n : names) {
                List<String> lijst = toList(n);
                if (lijst.size() > 1) {
                    String naam = lijst.get(0);
                    lijst.remove(0);
                    m.put(naam, lijst);
                }
            }
        } catch (NullPointerException e) {

        }
        return m;
    }

    public String findPlayer(String partOfName) {
        for (Player p : this.getServer().getOnlinePlayers()) {
            if (p.getName().toUpperCase().contains(partOfName.toUpperCase())) // found, return the fullname!
                return p.getName();
        }
        // not found, just return partOfName
        return partOfName;
    }

    
    private void registerEvents() {

        getServer().getPluginManager().registerEvent(Event.Type.PLAYER_JOIN, playerListener, Priority.Normal, this);
        getServer().getPluginManager().registerEvent(Event.Type.PLAYER_CHAT, playerListener, Priority.Normal, this);
        getServer().getPluginManager().registerEvent(Event.Type.PLAYER_QUIT, playerListener, Priority.Normal, this);
        
    }

    public Logger getLogger()
    {
        return log;
    }

    public String getPlayerColor(String playername, boolean change) {
        /* String group = Permissions.getGroup(getServer().getPlayer(playername).getWorld().toString(), playername);
        String pref = Permissions.getGroupPrefix(getServer().getPlayer(playername).getWorld().toString(), group).replace("&", "§"); */
        /* String group = "";
        String pref = ""; */
        String pref = "";
        String group = "";
        Player p = this.getServer().getPlayer(playername);

        try{
            if(p!=null)
            {
                group = perm.Security.getGroup(p.getWorld().getName(), playername);
                pref = perm.Security.getGroupPrefix(p.getWorld().getName(), group).replace("&", "§");
            } else {
                pref = "§f";
            }
        } catch(NullPointerException e)
        {
            pref = "§f";
        }
        // String suf = perm.Security.getGroupSuffix(group).replace("&", "§");
        String col = ChatColor.WHITE.toString();

        if (p == null) {
            // p =
            col = ChatColor.AQUA + "[NC] " + pref;
        }

        if (p != null)
            col += pref;

        return col;

    }


    @Deprecated
    public static List<String> splitUp(String msg) {
        List<String> lijst = new ArrayList<String>();
        String toadd;
        int x = 0;
        while (x < msg.length() - maxlength) {
            toadd = msg.substring(x, x + maxlength);
            if (!toadd.trim().isEmpty())
                lijst.add(toadd.trim());
            x += maxlength;
        }
        lijst.add(msg.substring(x));

        return lijst;
    }

    public void setupPermissions() {
        Plugin plugin = this.getServer().getPluginManager().getPlugin("Permissions");

        if (perm == null) {
            if (plugin != null) {
                perm = (Permissions) plugin;
            }
        }
    }

    public void setupVanish() {
        Plugin plugin = this.getServer().getPluginManager().getPlugin("Vanish");

        if (vanish == null)
            if (plugin != null)
                vanish = (Vanish) plugin;

    }

    public boolean check(Player player, String permNode) {
        if (perm == null || player.isOp()) {
            return true;
        } else {
            return perm.Security.permission(player, permNode);
        }
    }

    /* public boolean inGroup(Player player, String Group)
    {
        if(perm == null)
            return false;
        else
            return perm.Security.inGroup()
    } */

    public void onEnable() {
        // log.info("[TweakcraftUtils] onEnable called!");
        // log.severe(Thread.currentThread().g);
        /* for(StackTraceElement stack: Thread.currentThread().getStackTrace())
        {
            log.severe(stack.toString());
        } */
        PluginDescriptionFile pdfFile = this.getDescription();

        playerLimit = this.getServer().getMaxPlayers();
        this.registerEvents();
        this.setupPermissions();
        this.setupVanish();
        autolist = toList(getConfiguration().getString("autolist"));
        addlist = toList(getConfiguration().getString("addlist"));
        maxRange = getConfiguration().getInt("maxrange", 200);
        donottp = toList(getConfiguration().getString("donottp"));
        localchatlist = toList(getConfiguration().getString("localchatlist"));

        System.out.println(pdfFile.getName() + " version " + pdfFile.getVersion() + " is enabled!");
    }

    public void onDisable() {
        // TODO: Place any custom disable code here

        // NOTE: All registered events are automatically unregistered when a plugin is disabled

        System.out.println("Goodbye world!");
    }


    public boolean onCommand(CommandSender play, Command cmd, String commandLabel, String[] args) {
        Player player = (Player) play;
        if (cmd.getName().equals("who")) {

            List<Player> list = Arrays.asList(this.getServer().getOnlinePlayers());
            String msg = ChatColor.LIGHT_PURPLE + "Player list (" + list.size() + "/" + playerLimit + "): ";
            String toadd;

            // player.

            Collections.sort(list, new Comparator<Player>() {
                public int compare(Player p1, Player p2) {
                    return p1.getName().compareToIgnoreCase(p2.getName());
                }
            });

            //for (Player p: list) msg += p.getColor() + p.getName() + Colors.White + ", ";

            player.sendMessage(msg.substring(0, msg.length()));
            msg = " ";
            for (Player p : list) {
                toadd = getPlayerColor(p.getName(), false) + p.getName() + ChatColor.WHITE + ", ";

                /* if ((msg.length() + toadd.length()) > maxlength) {
                    player.sendMessage(msg.substring(0, msg.length() - 1));
                    msg = " ";
                } */
                msg += toadd;
            }
            if (!msg.trim().isEmpty()) {
                player.sendMessage(msg.substring(0, msg.length() - 2) + " ");
            }


            return true;
        } else if (cmd.getName().equalsIgnoreCase("admin")) {
            String msg = "";
            for (String m : args)
                msg += m + " ";
            // for (String m : splitUp(msg)) {
                playerListener.sendToAdmins(player, msg, false);

            // }
            log.info("AMSG: <" + player.getName() + "> " + msg);
            return true;
        } else if (cmd.getName().equalsIgnoreCase("admin-list")
                && check(player, "tweakcraftutils.adminlist")) {

            String msg = "";

            if (TweakcraftUtils.addlist.size() != 0) {
                player.sendMessage(col2 + "Current admin-msg subscriber list : ");
                String color = "";
                // player.sendMessage(Colors.Gold + cur.substring(0, cur.length() - 2));
                for (String name : addlist) {
                    try {
                        Player p = this.getServer().getPlayer(name);
                        color = getPlayerColor(name, true);
                    } catch (NullPointerException e) {
                        color = ChatColor.WHITE.toString();
                    }
                    msg = color + name;
                    player.sendMessage(msg);
                }
            } else {
                player.sendMessage(col2 + "Current admin-msg subscriber list is empty!");
            }
            return true;
        } else if (cmd.getName().equalsIgnoreCase("admon-list")
                && check(player, "tweakcraftutils.adminlist")) {

            String msg = "";

            if (TweakcraftUtils.addlist.size() != 0) {
                player.sendMessage(col2 + "Current auto-admin-msg subscriber list : ");
                String color = "";
                // player.sendMessage(Colors.Gold + cur.substring(0, cur.length() - 2));
                for (String name : autolist) {
                    try {
                        Player p = this.getServer().getPlayer(name);
                        color = getPlayerColor(name, true);
                    } catch (NullPointerException e) {
                        color = ChatColor.WHITE.toString();
                    }
                    msg = color + name;
                    player.sendMessage(msg);
                }
            } else {
                player.sendMessage(col2 + "Current auto-admin-msg subscriber list is empty!");
            }
            return true;
        } else if (cmd.getName().equalsIgnoreCase("admin-add")
                && check(player, "tweakcraftutils.adminlist")) {
            String names = "";
            String plname;
            List<Player> pl = new ArrayList<Player>();
            for (String n : args) {
                pl = this.getServer().matchPlayer(n);
                if (pl.size() == 1) {
                    plname = pl.get(0).getName();
                    if (!addlist.contains(plname)) {
                        if (args.length < 3) {
                            names += getPlayerColor(plname, false) + n + ChatColor.YELLOW + ", ";
                        } else {
                            names += n + ", ";
                        }
                        addlist.add(plname);
                        this.getServer().getPlayer(plname).sendMessage(ChatColor.YELLOW + "You have been added to the admin-msg list!");
                        playerListener.sendToAdmins(player, getPlayerColor(player.getName(), false) + player.getName() + ChatColor.YELLOW + " has added " +
                                getPlayerColor(plname, false) + plname + ChatColor.YELLOW + " to the admin-msg list!", true);
                    } else {
                        player.sendMessage("That player already is on the admin list!");
                    }
                }
                if (!names.equals(" ")) {
                    player.sendMessage(ChatColor.YELLOW + "Added " + names.substring(0, names.length() - 2) + " to the admin-msg list!");
                } else {
                    player.sendMessage(ChatColor.YELLOW + "Couldn't find anyone to add!");
                }
            }
            return true;
        } else if (cmd.getName().equalsIgnoreCase("admin-remove") &&
                (check(player, "tweakcraftutils.adminlist")/*  ||
                addlist.contains(player.getName())*/)) {
            String names = "";
            String plname;
            List<Player> pl = new ArrayList<Player>();
            for (String n : args) {
                plname = n;
                // if(pl.size()==1)
                // {
                // if (addlist.contains(plname)) {
                String name = findinlist(plname, addlist);
                if(name != null) {
                    if (args.length < 3) {
                        names += getPlayerColor(name, false) + n + ChatColor.WHITE + ", ";
                    } else {
                        names += n + ", ";
                    }
                    addlist.remove(name);
                    this.getServer().getPlayer(name).sendMessage(ChatColor.YELLOW + "You have been removed from the admin-msg list!");
                    playerListener.sendToAdmins(player, getPlayerColor(player.getName(), false) + player.getName() + ChatColor.YELLOW + " has removed " +
                            getPlayerColor(plname, false) + name + ChatColor.YELLOW + " from the admin-msg list!", true);
                    if(autolist.contains(name))
                    {
                        playerListener.sendToAdmins(player, "Also removed him from the auto-admin-msg list!", true);
                        autolist.remove(name);
                    }
                if (!names.equals(" ")) {
                    player.sendMessage(ChatColor.YELLOW + "Removed " + names + " from the admin-msg list!");
                } else {
                    player.sendMessage(ChatColor.YELLOW + "Couldn't find anyone to remove!");
                }
                }
            }
            return true;
        } else if (cmd.getName().equalsIgnoreCase("admon")) {

            if (check(player, "tweakcraftutils.admon") ||
                    addlist.contains(player.getName())) {
                if (!autolist.contains(player.getName())) {
                    autolist.add(player.getName());
                    player.sendMessage(ChatColor.YELLOW + "You will now send every message as an admin msg!");
                    player.sendMessage(ChatColor.YELLOW + "The bypass prefix is '!'.");
                } else {
                    player.sendMessage(ChatColor.YELLOW + "You are already on the auto-admin-msg list!");
                    player.sendMessage(ChatColor.YELLOW + "To remove yourself from this list, use /admoff!");
                }
            } else {
                player.sendMessage(ChatColor.RED + "You do not have the correct permissions for this command!");
            }
            return true;
        } else if (cmd.getName().equalsIgnoreCase("admoff")) {

            if (check(player, "tweakcraftutils.admon") ||
                    addlist.contains(player.getName())) {
                if (autolist.contains(player.getName())) {
                    autolist.remove(player.getName());
                    player.sendMessage(ChatColor.YELLOW + "You will now chat like normal again!");
                } else {
                    player.sendMessage("You aren't on the auto-admin-msg yet!");
                }
            } else {
                player.sendMessage(ChatColor.RED + "You do not have the correct permissions for this command!");
            }
            return true;
        } else if (cmd.getName().equalsIgnoreCase("tp")) {
            if(check(player, "tweakcraftutils.tp")) {
                if(donottp.contains(player.getName()) && !check(player, "tweakcraftutils.tphere"))
                {
                    player.sendMessage(ChatColor.RED + "You can't teleport while you don't allow others to tp to you!");
                } else {
                    if(args.length == 1)
                    {
                        List<Player> p = this.getServer().matchPlayer(args[0]);
                        if (p == null || p.size() == 0) {
                            player.sendMessage(ChatColor.YELLOW + "Can't find player!");
                        } else {
                            Player pto = p.get(0);
                            boolean refusetp = donottp.contains(pto.getName());
                            boolean override = false;
                            if(refusetp && (player.isOp() || check(player, "tweakcraftutils.forcetp")))
                            {
                                override = true;
                            } else {
                                override = false;
                                /* if(refusetp)
                                    override = true; */
                            }

                            if (pto.getName().equals(player.getName())) {
                                player.sendMessage(ChatColor.YELLOW + "You're already there!");
                            } else {
                                if(refusetp && !override)
                                {
                                    player.sendMessage(ChatColor.RED + "You don't have the correct permission to tp to "+pto.getName()+"!");
                                } else {
                                    boolean teleportwarning = true;
                                    try{
                                        teleportwarning = !vanish.getInvisiblePlayers().contains(player.getName());
                                    } catch(NoClassDefFoundError e)
                                    {
                                        teleportwarning = true;
                                    }
                                    if(teleportwarning)
                                        pto.sendMessage(getPlayerColor(player.getName(), false) + player.getName() + ChatColor.LIGHT_PURPLE + " Teleported to you!");
                                    if(!player.getWorld().getName().equals(pto.getWorld().getName()))
                                    {
                                        player.teleportTo(pto.getWorld().getSpawnLocation());
                                    }
                                    player.teleportTo(pto);
                                    if(override)
                                        player.sendMessage(ChatColor.RED + "Forced tp!");
                                    log.info("[TweakcraftUtils] "+player.getName()+" teleported to "+pto.getName()+"!");
                                }
                            }
                        }
                    } else {
                        if(check(player, "tweakcraftutils.tpfromto"))
                        {
                            List<Player> pfind = this.getServer().matchPlayer(args[0]);
                            Player pfrom, pto;
                            if(pfind!=null || pfind.size() != 0)
                            {
                                 pfrom = pfind.get(0);
                            } else {
                                player.sendMessage(ChatColor.DARK_GREEN + "Can't find source player!");
                                return true;
                            }
                            pfind = this.getServer().matchPlayer(args[1]);
                            if(pfind!=null || pfind.size() != 0)
                            {
                                pto = pfind.get(0);
                            } else {
                                player.sendMessage(ChatColor.DARK_GREEN + "Can't find destination player!");
                                return true;
                            }
                            player.sendMessage(ChatColor.GREEN + "Teleporting " +pfrom.getName()+" to "+pto.getName()+"!");
                            log.info("[TweakcraftUtils] "+player.getName()+" teleported "+pfrom.getName() + " to "+pto.getName()+"!");
                            pfrom.teleportTo(pto);
                        } else {
                            player.sendMessage(ChatColor.RED + "You do not have access to this kind of usage!");
                            return true;
                        }
                    }
                }

            } else {
                player.sendMessage(ChatColor.RED + "You don't have the correct permission bit to tp!");
            }
            return true;
        } else if (cmd.getName().equalsIgnoreCase("ext")) {
            if (args.length != 0) {
                if (check(player, "tweakcraftutils.extother")) {
                    String name = findPlayer(args[0]);
                    Player p = this.getServer().getPlayer(name);
                    if (p != null) {
                        if (p.getFireTicks() > 0) {
                            p.setFireTicks(0);
                            p.sendMessage(getPlayerColor(name, false) + name + ChatColor.YELLOW + " has been extinguished!");
                        } else {
                            p.sendMessage(getPlayerColor(name, false) + name + ChatColor.YELLOW + " isn't on fire!");
                        }
                    } else {
                        player.sendMessage(ChatColor.YELLOW + "Can't find that player!");
                    }
                } else {
                    player.sendMessage(ChatColor.GOLD + "You don't have permission for that usage!");
                }
            } else {
                if (player.getFireTicks() > 0) {
                    player.setFireTicks(0);
                    player.sendMessage(ChatColor.YELLOW + "You have been extinguished!");
                } else {
                    player.sendMessage(ChatColor.YELLOW + "You're not on fire!");
                }
            }
            return true;
        } else if (cmd.getName().equalsIgnoreCase("ignite") &&
                check(player, "tweakcraftutils.ignite")) {
                if (args.length != 0) {
                    String name = findPlayer(args[0]);
                    Player p = this.getServer().getPlayer(name);
                    if (p != null) {
                        p.setFireTicks(1500);
                        player.sendMessage(getPlayerColor(name, false) + name + ChatColor.YELLOW + " has been ignited!");
                    } else {
                        player.sendMessage(ChatColor.YELLOW + "Can't find that player!");
                    }
                } else {
                    player.sendMessage(ChatColor.YELLOW + "You have to specify a name to ignite!");
                }
            return true;
        } else if (cmd.getName().equalsIgnoreCase("tpon")&&
                check(player, "tweakcraftutils.tpoff")) {
                if (args.length != 0) {
                    if (check(player, "tweakcraftutils.tpoffother")) {
                        String name = findPlayer(args[0]);
                        Player p = this.getServer().getPlayer(name);
                        if (p != null) {
                            if (donottp.contains(p.getName())) {
                                donottp.remove(p.getName());
                                player.sendMessage(ChatColor.YELLOW + "They can now tp to "+getPlayerColor(name, false)+p.getName()+ChatColor.YELLOW +"!");
                            } else {
                                player.sendMessage(getPlayerColor(name, false) + p.getName() + ChatColor.YELLOW + " isn't on the do-not-tp list!");
                            }
                        }
                    }

                } else if (donottp.contains(player.getName())) {
                    donottp.remove(player.getName());
                    player.sendMessage(ChatColor.YELLOW + "They can now tp to you!");
                } else {
                    player.sendMessage(ChatColor.YELLOW + "You aren't on the do-not-tp list!");
                }
                return true;
        } else if (cmd.getName().equalsIgnoreCase("tpoff")&&
                check(player, "tweakcraftutils.tpoff")) {
                if (args.length != 0) {
                    if (check(player, "tweakcraftutils.tpoffother")) {
                        String name = findPlayer(args[0]);
                        Player p = this.getServer().getPlayer(name);
                        if (p != null) {
                            if (!donottp.contains(p.getName())) {
                                donottp.add(p.getName());
                                player.sendMessage(ChatColor.YELLOW + "They can no longer tp to "+getPlayerColor(name, false)+p.getName()+ChatColor.YELLOW +"!");
                            } else {
                                player.sendMessage(getPlayerColor(name, false) + p.getName() + ChatColor.YELLOW + " already is on the do-not-tp list!");
                            }
                        }
                    }
                } else if(donottp.contains(player.getName())) {
                    player.sendMessage(ChatColor.YELLOW + "You already are on the do-not-tp list!");
                } else {
                    donottp.add(player.getName());
                    player.sendMessage(ChatColor.GREEN + "They can no longer tp to you!");
                }
                return true;
        } else if (cmd.getName().equalsIgnoreCase("tplist")&&
                check(player, "tweakcraftutils.tpoffother")) {
                           String msg = "";

            if (TweakcraftUtils.donottp.size() != 0) {
                player.sendMessage(col2 + "Current do-not-tp list : ");
                String color = "";
                // player.sendMessage(Colors.Gold + cur.substring(0, cur.length() - 2));
                for (String name : donottp) {
                    try {
                        Player p = this.getServer().getPlayer(name);
                        color = getPlayerColor(name, true);
                    } catch (NullPointerException e) {
                        color = ChatColor.WHITE.toString();
                    }
                    msg = color + name;
                    player.sendMessage(msg);
                }
            } else {
                player.sendMessage(col2 + "Current do-not-tp list is empty!");
            }
            return true;
        } else if (cmd.getName().equalsIgnoreCase("lc")) {
            if(check(player, "tweakcraftutils.localchat")) {
                if(localchatlist.contains(player.getName()))
                {
                    localchatlist.remove(player.getName());
                    player.sendMessage(ChatColor.YELLOW + "You will now chat globally!");
                } else {
                    localchatlist.add(player.getName());
                    player.sendMessage(ChatColor.YELLOW + "You will now chat locally!");
                }
                return true;
            }
        } else if(cmd.getName().equalsIgnoreCase("tele")
                && check(player, "tweakcraftutils.tele")) {
            if(args.length == 0) {
                player.sendMessage(ChatColor.GREEN + "Usage: /tele up|x z [y] [world]");
            } else if(args.length==1 && args[0].equalsIgnoreCase("up")) {
                Location loc = player.getLocation();
                loc.setY(129);
                player.teleportTo(loc);
            } else if(args.length==2 || args.length==3 || args.length==4) {
                Integer x,y,z;
                World world;
                x=Integer.parseInt(args[0]);
                z=Integer.parseInt(args[1]);
                if(args.length==3)
                {
                    y=Integer.parseInt(args[2]);
                } else {
                    y=129;
                }
                if(args.length==4)
                {
                    try
                    {
                        if(getServer().getWorlds().contains(getServer().getWorld(args[3])))
                        {
                            world = getServer().getWorld(args[3]);
                        } else {
                            world = player.getWorld();
                        }
                    } catch(Exception e) {
                        world = player.getWorld();
                    }
                } else {
                    world = player.getWorld();
                }
                Location loc = new Location(world, x.doubleValue(),y.doubleValue(),z.doubleValue());
                player.teleportTo(loc);

            }
            return true;
        } else if(cmd.getName().equalsIgnoreCase("s") || cmd.getName().equalsIgnoreCase("tphere"))
        {
            if(check(player, "tweakcraftutils.tphere"))
            {
                List<Player> p = this.getServer().matchPlayer(args[0]);
                if (p == null || p.size() == 0) {
                    player.sendMessage(ChatColor.YELLOW + "Can't find player!");
                } else {
                    Player pto = p.get(0);
                    if (pto.getName().equals(player.getName())) {
                        player.sendMessage(ChatColor.YELLOW + "Now look at that, you've teleported yourself to yourself");
                    } else {
                        // if (!vanish.getInvisiblePlayers().contains(player.getName()))
                        // pto.sendMessage(getPlayerColor(player.getName(), false) + player.getName() + ChatColor.LIGHT_PURPLE + " Teleported to you!");
                        pto.teleportTo(player);
                    }
                }
                return true;
            }
        } else if(commandHandler.getCommandMap().containsKey(cmd.getName())) {
            try
            {
                com.guntherdw.bukkit.tweakcraft.Command command = commandHandler.getCommand(cmd.getName());
                // public abstract boolean executeCommand(Server server, CommandSender sender, String command, String[] args, TweakcraftUtils plugin);
                if(!command.executeCommand(play, cmd.getName(), args, this))
                {
                    play.sendMessage("This command did not go as intended!");
                }
            } catch (CommandNotFoundException e)
            {
                play.sendMessage("TweakcraftUtils error, command not found!");
            } catch (PermissionsException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            } catch (CommandUsageException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            } catch (CommandSenderException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
        }
        return false;
    }
}