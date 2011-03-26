package com.guntherdw.bukkit.tweakcraft;

import com.guntherdw.bukkit.tweakcraft.Chat.ChatHandler;
import com.guntherdw.bukkit.tweakcraft.Commands.CommandHandler;
import com.guntherdw.bukkit.tweakcraft.Exceptions.*;
import com.nijikokun.bukkit.Permissions.Permissions;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.Event.Priority;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.config.Configuration;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;

/**
 * @author GuntherDW
 */

public class TweakcraftUtils extends JavaPlugin {

    private final HashMap<Player, Boolean> debugees = new HashMap<Player, Boolean>();
    public Permissions perm = null;
    public final TweakcraftPlayerListener playerListener = new TweakcraftPlayerListener(this);
    public int playerLimit;
    public int maxRange;
    private final CommandHandler commandHandler = new CommandHandler(this);
    public static int maxlength = 55;
    private static File seenFile;
    private Configuration seenconfig;
    protected boolean keepplayerhistory = false;

    private final ChatHandler chathandler = new ChatHandler(this);
    private List<String> donottplist;

    protected static final Logger log = Logger.getLogger("Minecraft");

    public String findinlist(String find, List<String> list)
    {
        for(String name : list)
        {
            if(name.toLowerCase().contains(find.toLowerCase()))
            {
                return name;
            }
        }
        return null;
    }


    /* private void reloadBanList()
    {
        ((CraftServer)this.getServer()).getHandle().g();
    } */

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

    public String findPlayer(String partOfName) {
        for (Player p : this.getServer().getOnlinePlayers()) {
            if (p.getName().toUpperCase().contains(partOfName.toUpperCase())) // found, return the fullname!
                return p.getName();
        }
        // not found, just return partOfName
        return partOfName;
    }

    
    private void registerEvents() {
        getServer().getPluginManager().registerEvent(Event.Type.PLAYER_LOGIN, playerListener, Priority.Normal, this);
        getServer().getPluginManager().registerEvent(Event.Type.PLAYER_JOIN,  playerListener, Priority.Normal, this);
        getServer().getPluginManager().registerEvent(Event.Type.PLAYER_CHAT,  playerListener, Priority.Normal, this);
        getServer().getPluginManager().registerEvent(Event.Type.PLAYER_QUIT,  playerListener, Priority.Normal, this);
    }

    public Logger getLogger()
    {
        return log;
    }

    public String getPlayerColor(String playername, boolean change) {

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

        } catch(NullPointerException e) {
            pref = "§f";
        }
        String col = ChatColor.WHITE.toString();
        if (p == null) col = ChatColor.AQUA + "[NC] " + pref;
        if (p != null) col += pref;
        return col;

    }

    public ChatHandler getChathandler() {
        return this.chathandler;
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

    public boolean check(Player player, String permNode) {
        if (perm == null || player.isOp()) {
            return true;
        } else {
            return perm.Security.permission(player, "tweakcraftutils."+permNode);
        }
    }


    public void onEnable() {

        PluginDescriptionFile pdfFile = this.getDescription();

        playerLimit = this.getServer().getMaxPlayers();
        donottplist = new ArrayList<String>();
        this.registerEvents();
        this.setupPermissions();
        if(getConfiguration().getBoolean("keepplayerhistory", false))
        {
            log.info("[TweakcraftUtils] Keeping player history!");
            seenFile = new File(getDataFolder(), "players.yml");
            seenconfig = new Configuration(seenFile);
            seenconfig.load();
            keepplayerhistory = true;
        }
        maxRange = getConfiguration().getInt("maxrange", 200);
        log.info("["+pdfFile.getName() + "] "+pdfFile.getName()+" version " + pdfFile.getVersion() + " is enabled!");
    }

    public List<String> getDonottplist() {
        return donottplist;
    }
    
    public boolean isKeepplayerhistory() {
        return keepplayerhistory;
    }

    public Configuration getSeenconfig() {
        return seenconfig;
    }

    public void onDisable() {
        log.info("[TweakcraftUtils] Goodbye world!");
    }

    public void onLoad() {
        //To change body of implemented methods use File | Settings | File Templates.
    }


    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {

        if(commandHandler.getCommandMap().containsKey(cmd.getName())) {
            try
            {
                com.guntherdw.bukkit.tweakcraft.Command command = commandHandler.getCommand(cmd.getName());
                // public abstract boolean executeCommand(Server server, CommandSender sender, String command, String[] args, TweakcraftUtils plugin);
                if(!command.executeCommand(sender, cmd.getName(), args, this))
                {
                    sender.sendMessage("This command did not go as intended!");
                }
                String mess = "";
                if(args.length>1)
                {
                    for (String m : args)
                        mess+=m+" ";

                    mess=mess.substring(0, mess.length()-1);
                } else if(args.length==1) {
                    mess=args[0];
                }

                if(sender instanceof Player)
                    log.info("[TweakcraftUtils] "+((Player)sender).getName()+" issued: /" + cmd.getName() + " " + mess);
                return true;
            } catch (CommandNotFoundException e)
            {
                sender.sendMessage("TweakcraftUtils error, command not found!");
            } catch (PermissionsException e) {
                sender.sendMessage("You do not have the correct permissions for this command!");
                if(sender instanceof Player) {
                    String mess = "";
                    if(args.length>1)
                    {
                        for (String m : args)
                            mess+=m+" ";

                        mess=mess.substring(0, mess.length()-1);
                    } else if(args.length==1) {
                        mess=args[0];
                    }
                    log.info("[TweakcraftUtils] "+((Player)sender).getName()+" tried: /" + cmd.getName() + " " + mess);

                }
            } catch (CommandUsageException e) {
                sender.sendMessage(e.getMessage());
            } catch (CommandSenderException e) {
                sender.sendMessage(e.getMessage());
            } catch (CommandException e) {
                sender.sendMessage(e.getMessage());
            }
        }
        return false;
    }
}