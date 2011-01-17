import java.util.List;

/**
 * @author GuntherDW
 */
public class TeleportExclude {
    public static boolean handleCommand(String[] split, Player player)
    {
        if(split.length>0)
        {
            String cmd = split[1];
            if(cmd == "add")
            {
                AddExclude(player, split);
                return true;
            } else if(cmd == "remove") {
                RemoveExclude(player, split);
            } else if(cmd == "list") {
                ListExcludes(player, split);
            }

        } else {
            player.sendMessage(Colors.Yellow + "Incorrect usage (add|remove|list)");
        }
        return false;
    }

    private static void ListExcludes(Player player, String[] split) {
        
    }
    
    private static void RemoveExclude(Player player, String[] split) {
        
    }
    
    private static void AddExclude(Player player, String[] split) {
        
    }
}
