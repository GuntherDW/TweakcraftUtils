import com.sun.xml.internal.ws.message.saaj.SAAJHeader;

import java.text.Collator;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author GuntherDW
 */
public class TeleportExclude {
    
    // private Map<String, List<String>> tpoffexclude = TweakcraftUtils.donottpexclude;
    
    public static boolean handleCommand(String[] split, Player player)
    {
        if(split.length>1)
        {
            String cmd = split[1];
            if(cmd.equalsIgnoreCase("add"))
            {
                AddExclude(player, split);
                return true;
            } else if(cmd.equalsIgnoreCase("remove")) {
                RemoveExclude(player, split);
                return true;
            } else if(cmd.equalsIgnoreCase("list")) {
                ListExcludes(player, split);
                return true;
            } else {
                player.sendMessage(Colors.Yellow + "Incorrect usage (add|remove|list)");
                return true;
            }

        } else {
            player.sendMessage(Colors.Yellow + "Incorrect usage (add|remove|list)");
            return true;
        }
    }

    private static void ListExcludes(Player player, String[] split) {
        if(split.length > 2 && player.isInGroup("admin"))
        {
            String naam = TweakcraftUtilsListener.findPlayer(split[2]);
            String col = TweakcraftUtilsListener.getPlayerColor(naam, true);
            if(TweakcraftUtils.donottpexclude.containsKey(naam))
            {
                List<String> excl = TweakcraftUtils.donottpexclude.get(naam);
                player.sendMessage(Colors.LightPurple + "TP: List of excludes for " + col + naam + Colors.LightPurple + ":");
                if(excl != null && excl.size()>0)
                    for(String n : excl)
                    {
                        col = TweakcraftUtilsListener.getPlayerColor(n, true);
                        player.sendMessage(col+n);
                    }
                else
                    player.sendMessage(Colors.Yellow + "Empty!");
            } else {
                player.sendMessage(Colors.Yellow + "Can't find "+ col + naam + Colors.Yellow + "'s"+  " exclude list!");
                player.sendMessage(Colors.Yellow + "Does he have one?");
            }
        } else if(split.length > 1) {
            String col;
            String naam = player.getName();
            List<String> excl = TweakcraftUtils.donottpexclude.get(naam);
                player.sendMessage(Colors.LightPurple + "TP: Your current excludes list : ");
                if(excl != null && excl.size()>0)
                    for(String name : excl)
                    {
                        col = TweakcraftUtilsListener.getPlayerColor(name, true);
                        player.sendMessage(col+name);
                    }
                else
                    player.sendMessage(Colors.Yellow + "Empty!");
        } else {
            player.sendMessage(Colors.Yellow + "Invalid syntax, how did you get here?");
        }
    }
    
    private static void RemoveExclude(Player player, String[] split) {
        String naam = player.getName();
        List<String> l = TweakcraftUtils.donottpexclude.get(naam);
        if(l==null)
        {
            player.sendMessage(Colors.Yellow + "You don't have anyone in your exclude list!");
            // return true;
            
        } else {
            String l2 = "";
            
            if(split.length > 2)
            {
                for(int x=2;x<split.length;x++)
                {
                    String n = TweakcraftUtilsListener.findPlayer(split[x]);
                    if(l.contains(n))
                    {
                        l.remove(n);
                        l2 += n+", ";
                    }
                }
                // TweakcraftUtils.donottpexclude.Entry
                if(!l2.equals(", "))
                {
                    TweakcraftUtils.donottpexclude.put(naam, l);
                    player.sendMessage(Colors.Yellow + "Successfully removed " + l2.substring(0, l2.length()-2) +
                            " from your exclude list!");
                } else {
                    player.sendMessage("No new players to remove found!");
                }
                if(l.size() == 0)
                    TweakcraftUtils.donottpexclude.remove(player.getName()); // no stale players in the list!
            } else {
                player.sendMessage(Colors.Yellow + "You have to give me some names to remove!");
            }
        }
    }
    
    private static void AddExclude(Player player, String[] split) {
        String naam = player.getName();
        List<String> l = TweakcraftUtils.donottpexclude.get(naam);
        if(l==null)
        {
            l = new ArrayList<String>();
        }
        String l2 = "";
        if(split.length > 2)
        {
            for(int x=2;x<split.length;x++)
            {
                String n = TweakcraftUtilsListener.findPlayer(split[x]);
                if(!l.contains(n))
                {
                    l.add(n);
                    l2 += n+", ";
                }
            }
            // TweakcraftUtils.donottpexclude.Entry
            if(l2.length()>2)
            {
                TweakcraftUtils.donottpexclude.put(naam, l);
                player.sendMessage(Colors.Yellow + "Successfully added " + l2.substring(0, l2.length()-2) +
                        " to your exclude list!");
            } else {
                    player.sendMessage("No new players to exclude found!");
            }
        } else {
            player.sendMessage(Colors.Yellow + "You have to give me some names to add!");
        }
    }
}
