import java.sql.Time;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

// import static ;

public class TweakcraftUtils extends Plugin {

    private static final int version = 20;
    public final static Logger log = Logger.getLogger("Minecraft");
    private static String name = "TweakcraftUtils rev" + version;
    public static List<String> addlist;
    public static String col = Colors.Yellow;
    public static String col2 = Colors.LightPurple;
    public static int maxlength;
    public static List<String> autolist;
    public static List<String> donottp;
    public static Map<String, List<String>> donottpexclude;

    private static PropertiesFile properties = new PropertiesFile("tweakcraftutils.properties");

    public static List<String> splitUp(String msg) {
        List<String> lijst = new ArrayList<String>();
        String toadd;
        int x = 0;
        while (x < msg.length() - maxlength) {
            toadd = msg.substring(x, x+maxlength);
            if (!toadd.trim().isEmpty())
                lijst.add(toadd.trim());
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

    public static String listToString(List<String> lijst) {
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

    private static String mToString(Map<String, List<String>> m)
    {
        String res = "";
        String pl2 = "";
        if(m.keySet().size() > 0)
        {
            for(String pl : m.keySet())
            {
                pl2 = listToString(m.get(pl));
                res += pl + ","+pl2 + ";";
            }

            res = res.substring(0, res.length()-1);
        } else {
            res = "";
        }
        return res;
    }

    public static Map<String, List<String>> toMap(String str) {
        Map<String, List<String>> m = new HashMap<String, List<String>>();
        if(str.trim().isEmpty())
        {
            return m;
        }
        String[] names = str.split(";");
        for(String n : names)
        {
            List<String> lijst = toList(n);
            if(lijst.size() > 1)
            {
                String naam = lijst.get(0);
                lijst.remove(0);
                m.put(naam, lijst);
            }
        }
        return m;
    }

    public void disable() {
        log.info(name + " disabled");
        String lijst = listToString(addlist);
        String lijst2 = listToString(autolist);
        String lijst3 = listToString(donottp);
        String lijst4 = mToString(donottpexclude);

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
        properties.setString("do-not-tp", lijst3);
        properties.setString("do-not-tp-exclude", lijst4);
        properties.setInt("max-length", maxlength);
        etc.getInstance().removeCommand("/admin");
        etc.getInstance().removeCommand("/admin-add");
        etc.getInstance().removeCommand("/admin-remove");
        etc.getInstance().removeCommand("/admin-list");
        etc.getInstance().removeCommand("/admon");
        etc.getInstance().removeCommand("/admoff");
        etc.getInstance().removeCommand("/admon-list");
        etc.getInstance().removeCommand("/mlength");
        etc.getInstance().removeCommand("/tpon");
        etc.getInstance().removeCommand("/tpoff");
        etc.getInstance().removeCommand("/tplist");
        etc.getInstance().removeCommand("/tpe");
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
        etc.getInstance().addCommand("/tpon", "Players can teleport to you.");
        etc.getInstance().addCommand("/tpoff", "Players can't teleport to you.");
        etc.getInstance().addCommand("/tplist", "Players that don't want anyone tp'ing to them.");
        etc.getInstance().addCommand("/tpe", "Tp exclude configuration");
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
        donottp = toList(properties.getString("do-not-tp", ""));
        // autolist = new ArrayList<String>();

        donottpexclude = toMap(properties.getString("do-not-tp-exclude", ""));
    }
}