import java.sql.Time;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

// import static ;

public class TweakcraftUtils extends Plugin {

    private static final int version = 15;
    private final static Logger log = Logger.getLogger("Minecraft");
    private static String name = "TweakcraftUtils rev" + version;
    private static List<String> addlist;
    private static String col = Colors.Yellow;
    private static String col2 = Colors.LightPurple;
    private static int maxlength;
    private static List<String> autolist;

    private static PropertiesFile properties = new PropertiesFile("tweakcraftutils.properties");

    public List<String> splitUp(String msg) {
        List<String> lijst = new ArrayList<String>();
        String toadd;
        int x = 0;
        while (x < msg.length() - maxlength) {
            toadd = msg.substring(x, maxlength);
            if (!toadd.trim().isEmpty())
                lijst.add(toadd);
            x += maxlength;
        }
        lijst.add(msg.substring(x));
        /* if(lijst.size() == 2)
        {
            if(lijst.get(0).equals(lijst.get(1)))
            {
                lijst.remove(lijst.size()-1);
            }
        } */

        return lijst;
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

    public void disable() {
        log.info(name + " disabled");
        String lijst = listToString(addlist);
        String lijst2 = listToString(autolist);
        /* for(String p : autolist)
        {
            try{
                etc.getServer().getPlayer(p).sendMessage(Colors.LightBlue + "Reloading TweakcraftUtils, remember to");
                etc.getServer().getPlayer(p).sendMessage(Colors.LightBlue + "do /admon again!");
            } catch (NullPointerException e) {

            }

        } */
        log.log(Level.INFO, "TweakcraftUtils: Writing to properties file!");
        properties.setString("admin-subscr-list", lijst);
        properties.setString("admin-auto-list", lijst2);
        properties.setInt("max-length", maxlength);
        // properties.
    }

    public void enable() {
        loadProperties();
        log.info(name + " enabled");
    }

    public void initialize() {
        PluginListener listener = new TweakcraftUtilsListener();
        etc.getLoader().addListener(PluginLoader.Hook.COMMAND, listener, this, PluginListener.Priority.MEDIUM);
        etc.getLoader().addListener(PluginLoader.Hook.CHAT, listener, this, PluginListener.Priority.MEDIUM);
//        etc.getLoader().addListener(PluginLoader.Hook.TELEPORT, listener, this, PluginListener.Priority.LOW);
        etc.getInstance().addCommand("/admin", "[msg] Message any current admin online.");

        etc.getInstance().addCommand("/admin-add", "[name] Add player to the admin msg list.");
        etc.getInstance().addCommand("/admin-remove", "[name] Remove player from the admin msg list.");
        etc.getInstance().addCommand("/admin-list", "Show the current subscriber list.");
        etc.getInstance().addCommand("/admon", "Automatically send messages as admin msg while you chat!");
        etc.getInstance().addCommand("/admoff", "Disable the auto-admin msg.");
        etc.getInstance().addCommand("/admon-list", "Show the current auto-admin list.");

        etc.getInstance().addCommand("/mlength", "Set the max length.");

    }

/*     public com.sk89q.craftbook.Vector playerVector(Player n){
        return new com.sk89q.craftbook.Vector(n.getX(), n.getY(), n.getZ());
    } */


    private static void loadProperties() {
        log.log(Level.INFO, "Loading admin subscr list!");
        log.log(Level.INFO, "Loading auto-admin list!");
        autolist = toList(properties.getString("admin-auto-list", ""));
        // log.log(Level.INFO, "auto-list: "+autolist);
        addlist = toList(properties.getString("admin-subscr-list", ""));
        maxlength = properties.getInt("max-length", 55);
        // autolist = new ArrayList<String>();
    }

    private static List<String> toList(String str) {
        if (str.trim().isEmpty()) {
            return new ArrayList<String>();
        }

        String[] names = str.split(",");
        List<String> result = new ArrayList<String>();

        for (String n : names)
            result.add(n.trim());
        return result;
    }

    public String findPlayer(String partOfName) {
        for (Player p : etc.getServer().getPlayerList()) {
            if (p.getName().toUpperCase().contains(partOfName.toUpperCase())) // found, return the fullname!
                return p.getName();
        }
        // not found, just return partOfName
        return partOfName;
    }

    public String findPlayerinList(String partOfName) {
        for (String p : addlist) {
            if (p.toUpperCase().contains(partOfName.toUpperCase()))
                return p;
        }
        return partOfName;
    }

    public String getPlayerColor(String name, boolean conn) {
        String col = Colors.White;
        Player p = etc.getDataSource().getPlayer(name);
        if (p != null)
            col = (conn ? Colors.LightBlue + "[NC] " : "") + p.getColor();

        return col;
    }

    private void sendToAdmins(String sender, String message) {
        List<String> msg = new ArrayList<String>();
        int x;
        String color = getPlayerColor(sender, false);
        msg = splitUp(Colors.LightGreen + "ADMMSG : <"
                + color + sender + Colors.LightGreen + "> " + message);

        for (Player p : etc.getServer().getPlayerList()) {
            if (p.isInGroup("admin") || addlist.contains(p.getName())) {
                x = 0;

                for (String m : msg) {
                    //log.log(Level.INFO, "sent stuff!");
                    if (x != 0) {
                        p.sendMessage(Colors.LightGreen + m + " ");
                    } else {
                        p.sendMessage(m + " ");
                    }
                    x++;
                }
            } else if (sender == p.getName()) {
                p.sendMessage(Colors.LightPurple + "Message sent to admins:");
                for (String m : msg) {
                    p.sendMessage(Colors.LightGreen + m + " ");
                }
            }
        }
    }

    private class TweakcraftUtilsListener extends PluginListener {

        public boolean onChat(Player player, String message) {
            // System.out.println(autolist.toString());
            String msg = "";
            boolean ch = false;

            if (autolist.contains(player.getName())) {
                if (message.startsWith("'")) {
                    msg = message.substring(1);
                    etc.getServer().messageAll(Colors.White + "<" + getPlayerColor(player.getName(), false) +
                            player.getName() + Colors.White + "> " + msg);
                } else {
                    ch = true;
                    msg = message;
                    sendToAdmins(player.getName(), msg);
                }
                log.log(Level.INFO, (ch ? "(ADMMSG) " : "") + "<" + player.getName() + "> " + msg);
                return true;
            } else {
                return false;
            }
        }


        public boolean onCommand(Player player, String[] split) {
            if ((split[0].equalsIgnoreCase("/playerlist") || split[0].equalsIgnoreCase("/who")) && player.canUseCommand(split[0])) {
                String msg = Colors.Rose + "Player list (" + etc.getMCServer().f.b.size() + "/" + etc.getInstance().getPlayerLimit() + "): ";


                List<Player> list = etc.getServer().getPlayerList();
                Collections.sort(list, new Comparator<Player>() {
                    public int compare(Player p1, Player p2) {
                        return p1.getName().compareToIgnoreCase(p2.getName());
                    }
                });

                //for (Player p: list) msg += p.getColor() + p.getName() + Colors.White + ", ";

                player.sendMessage(msg.substring(0, msg.length()));
                msg = " ";
                for (Player p : list) {
                    if ((msg.length() + p.getColor().length() + p.getName().length()) > maxlength) {
                        player.sendMessage(msg.substring(0, msg.length() - 1));
                        msg = " ";
                    }
                    msg += p.getColor() + p.getName() + Colors.White + ", ";
                }
                if (!msg.trim().isEmpty()) {
                    player.sendMessage(msg.substring(0, msg.length() - 2) + " ");
                }


                return true;
            } else if (split[0].equalsIgnoreCase("/admin")
                    || split[0].equalsIgnoreCase("/a")
                    || split[0].equalsIgnoreCase("/@")) {
                if (split.length > 1) {
                    if (!split[1].trim().isEmpty()) {
                        List<String> msg = new ArrayList<String>();
                        String tmp = "";
                        int y = 0;
                        int x = 0;
                        for (String m : split) {
                            if (x != 0)
                                tmp += m + " ";
                            x++;
                        }
                        sendToAdmins(player.getName(), tmp);

                    }
                } else {
                    player.sendMessage(Colors.Yellow + "You have to specify a message to send to the admins!");
                }
                return true;
            } else if (split[0].equalsIgnoreCase("/admin-add") &&
                    player.isInGroup("admin")) {

                if (split.length > 0 && !split[1].trim().isEmpty()) {
                    String name = findPlayer(split[1].trim());
                    String color;
                    try {
                        Player p = etc.getServer().getPlayer(name);
                        if (p != null) {
                            color = p.getColor();
                        } else {
                            color = getPlayerColor(name, true);
                        }
                    } catch (NullPointerException e) {
                        color = Colors.White;
                    }

                    if (addlist.contains(name)) {
                        player.sendMessage(col + name + " already is on the admin msg list!");
                    } else {
                        addlist.add(name);
                        player.sendMessage(col + "added " + name + " to the admin msg list!");

                        for (Player p : etc.getServer().getPlayerList()) {
                            if (p.isInGroup("admin")) {
                                p.sendMessage(player.getColor() + player.getName() + col2 + " added "
                                        + color + name + col2 + " to the admin subscr list!");
                            } else if (p.getName().equals(name)) {
                                p.sendMessage(col2 + "You have been added to the admin subscr list!");
                            }
                        }
                    }
                } else {
                    player.sendMessage(Colors.Yellow + "Give me a player name!");
                }
                return true;

            } else if (split[0].equalsIgnoreCase("/admin-remove") &&
                    player.isInGroup("admin")) {

                if (split.length > 0 && !split[1].trim().isEmpty()) {
                    String name = findPlayerinList(split[1].trim());
                    String color;
                    try {
                        Player p = etc.getServer().getPlayer(name);
                        if (p != null) {
                            color = p.getColor();
                        } else {
                            color = getPlayerColor(name, true);
                        }
                    } catch (NullPointerException e) {
                        color = Colors.White;
                    }


                    if (addlist.contains(name)) {
                        addlist.remove(name);
                        player.sendMessage(Colors.Yellow + name + " removed from admin msg list!");
                        for (Player p : etc.getServer().getPlayerList()) {
                            if (p.isInGroup("admin")) {
                                p.sendMessage(player.getColor() + player.getName() + col2 + " removed "
                                        + color + name + col2 + " to the admin subscr list!");
                            } else if (p.getName().equals(name)) {
                                p.sendMessage(col2 + "You have been removed from the admin subscr list!");
                            }
                        }
                    } else {
                        player.sendMessage(Colors.Yellow + split[1].trim() + " isn't on the admin list!");
                    }
                } else {
                    player.sendMessage(Colors.Yellow + "Give me a player name!");
                }
                return true;
            } else if (split[0].equalsIgnoreCase("/admin-list") &&
                    player.isInGroup("admin")) {

                String msg = "";

                if (addlist.size() != 0) {
                    player.sendMessage(col2 + "Current admin-msg subscriber list : ");
                    String color = "";
                    // player.sendMessage(Colors.Gold + cur.substring(0, cur.length() - 2));
                    for (String name : addlist) {
                        try {
                            Player p = etc.getServer().getPlayer(name);
                            if (p != null) {
                                color = p.getColor();
                            } else {
                                color = getPlayerColor(name, true);
                            }
                        } catch (NullPointerException e) {
                            color = Colors.White;
                        }
                        msg = color + name;
                        player.sendMessage(msg);
                    }
                } else {
                    player.sendMessage(col2 + "Current admin-msg subscriber list is empty!");
                }

                return true;
            } else if(split[0].equalsIgnoreCase("/admon-list") &&
                    player.isInGroup("admin"))
            {

                String msg= "";
                if(autolist.size() != 0)
                {
                    player.sendMessage(col2 + "Current admin-auto-msg list : ");
                    String color = "";
                    for (String name : autolist) {
                        try {
                             Player p = etc.getServer().getPlayer(name);
                             if (p != null) {
                                color = p.getColor();
                             } else {
                                color = getPlayerColor(name, true);
                            }
                        } catch (NullPointerException e) {
                            color = Colors.White;
                        }
                        msg = color + name;
                        player.sendMessage(msg);
                    }
                } else {
                       player.sendMessage(col2 + "Current admin-auto-msg list is empty!");
                }
                return true;
            } else if (split[0].equalsIgnoreCase("/admon")) {
                if (addlist.contains(player.getName()) || player.isInGroup("admin")) {
                    if (autolist.contains(player.getName())) {
                        player.sendMessage(Colors.LightPurple + "You already are on the list, use /admoff");
                        player.sendMessage(Colors.LightPurple + "to remove yourself from the list.");
                    } else {
                        player.sendMessage(Colors.LightGreen + "You will now automatically send as an admin msg!");
                        autolist.add(player.getName());
                    }
                } else {
                    player.sendMessage(Colors.LightPurple + "You are not on the subscriber list!");
                    player.sendMessage(Colors.LightPurple + "You need to be a subscriber use this feature.");
                }
                return true;
            } else if (split[0].equalsIgnoreCase("/admoff")) {
                if (!autolist.contains(player.getName())) {
                    player.sendMessage(Colors.LightPurple + "You aren't on the auto-admin list!");
                } else {
                    player.sendMessage(Colors.LightGreen + "You will now chat like normal!");
                    autolist.remove(player.getName());

                }
                return true;
            } else if (split[0].equalsIgnoreCase("/mlength") &&
                    player.isInGroup("admin")) {
                if (split.length > 1) {
                    try {
                        Integer mv = Integer.parseInt(split[1]);
                        maxlength = mv.intValue();
                        player.sendMessage(Colors.LightPurple + "Maxlength has been set to " + mv + ".");
                    } catch (NumberFormatException e) {
                        player.sendMessage(Colors.LightPurple + "Invalid input!");
                    }
                } else {
                    player.sendMessage(Colors.LightPurple + "Current maxlength : " + maxlength);
                }
                return true;
            }


            return false;
        }
    }
}