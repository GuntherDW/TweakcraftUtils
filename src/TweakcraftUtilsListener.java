import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.logging.Level;

/**
 * @author GuntherDW
 */
public class TweakcraftUtilsListener extends PluginListener {



    public String findPlayer(String partOfName) {
        for (Player p : etc.getServer().getPlayerList()) {
            if (p.getName().toUpperCase().contains(partOfName.toUpperCase())) // found, return the fullname!
                return p.getName();
        }
        // not found, just return partOfName
        return partOfName;
    }

    public String findPlayerinList(String partOfName) {
        for (String p : TweakcraftUtils.addlist) {
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
        msg = TweakcraftUtils.splitUp(Colors.LightGreen + "ADMMSG : <"
                + color + sender + Colors.LightGreen + "> " + message);

        for (Player p : etc.getServer().getPlayerList()) {
            if (p.isInGroup("admin") || TweakcraftUtils.addlist.contains(p.getName())) {
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

    public boolean onChat(Player player, String message) {
        // System.out.println(autolist.toString());
        String msg = "";
        boolean ch = false;

        if (TweakcraftUtils.autolist.contains(player.getName())) {
            if (message.startsWith("'")) {
                msg = message.substring(1);
                etc.getServer().messageAll(Colors.White + "<" + getPlayerColor(player.getName(), false) +
                        player.getName() + Colors.White + "> " + msg);
            } else {
                ch = true;
                msg = message;
                sendToAdmins(player.getName(), msg);
            }
            TweakcraftUtils.log.log(Level.INFO, (ch ? "(ADMMSG) " : "") + "<" + player.getName() + "> " + msg);
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
                if ((msg.length() + p.getColor().length() + p.getName().length()) > TweakcraftUtils.maxlength) {
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

                if (TweakcraftUtils.addlist.contains(name)) {
                    player.sendMessage(TweakcraftUtils.col + name + " already is on the admin msg list!");
                } else {
                    TweakcraftUtils.addlist.add(name);
                    player.sendMessage(TweakcraftUtils.col + "added " + name + " to the admin msg list!");

                    for (Player p : etc.getServer().getPlayerList()) {
                        if (p.isInGroup("admin")) {
                            p.sendMessage(player.getColor() + player.getName() + TweakcraftUtils.col2 + " added "
                                    + color + name + TweakcraftUtils.col2 + " to the admin subscr list!");
                        } else if (p.getName().equals(name)) {
                            p.sendMessage(TweakcraftUtils.col2 + "You have been added to the admin subscr list!");
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


                if (TweakcraftUtils.addlist.contains(name)) {
                    TweakcraftUtils.addlist.remove(name);
                    player.sendMessage(Colors.Yellow + name + " removed from admin msg list!");
                    for (Player p : etc.getServer().getPlayerList()) {
                        if (p.isInGroup("admin")) {
                            p.sendMessage(player.getColor() + player.getName() + TweakcraftUtils.col2 + " removed "
                                    + color + name + TweakcraftUtils.col2 + " to the admin subscr list!");
                        } else if (p.getName().equals(name)) {
                            p.sendMessage(TweakcraftUtils.col2 + "You have been removed from the admin subscr list!");
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

            if (TweakcraftUtils.addlist.size() != 0) {
                player.sendMessage(TweakcraftUtils.col2 + "Current admin-msg subscriber list : ");
                String color = "";
                // player.sendMessage(Colors.Gold + cur.substring(0, cur.length() - 2));
                for (String name : TweakcraftUtils.addlist) {
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
                player.sendMessage(TweakcraftUtils.col2 + "Current admin-msg subscriber list is empty!");
            }

            return true;
        } else if (split[0].equalsIgnoreCase("/admon-list") &&
                player.isInGroup("admin")) {

            String msg = "";
            if (TweakcraftUtils.autolist.size() != 0) {
                player.sendMessage(TweakcraftUtils.col2 + "Current admin-auto-msg list : ");
                String color = "";
                for (String name : TweakcraftUtils.autolist) {
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
                player.sendMessage(TweakcraftUtils.col2 + "Current admin-auto-msg list is empty!");
            }
            return true;
        } else if (split[0].equalsIgnoreCase("/admon")
                    && (TweakcraftUtils.addlist.contains(player.getName()) || player.isInGroup("admin"))) {
            if (TweakcraftUtils.addlist.contains(player.getName()) || player.isInGroup("admin")) {
                if (TweakcraftUtils.autolist.contains(player.getName())) {
                    player.sendMessage(Colors.LightPurple + "You already are on the list, use /admoff");
                    player.sendMessage(Colors.LightPurple + "to remove yourself from the list.");
                } else {
                    player.sendMessage(Colors.LightGreen + "You will now automatically send as an admin msg!");
                    TweakcraftUtils.autolist.add(player.getName());
                }
            } else {
                player.sendMessage(Colors.LightPurple + "You are not on the subscriber list!");
                player.sendMessage(Colors.LightPurple + "You need to be a subscriber use this feature.");
            }
            return true;
        } else if (split[0].equalsIgnoreCase("/admoff")
                    && TweakcraftUtils.addlist.contains(player.getName())) {
            if (!TweakcraftUtils.autolist.contains(player.getName())) {
                player.sendMessage(Colors.LightPurple + "You aren't on the auto-admin list!");
            } else {
                player.sendMessage(Colors.LightGreen + "You will now chat like normal!");
                TweakcraftUtils.autolist.remove(player.getName());

            }
            return true;
        } else if (split[0].equalsIgnoreCase("/mlength") &&
                player.isInGroup("admin")) {
            if (split.length > 1) {
                try {
                    Integer mv = Integer.parseInt(split[1]);
                    TweakcraftUtils.maxlength = mv.intValue();
                    player.sendMessage(Colors.LightPurple + "Maxlength has been set to " + mv + ".");
                } catch (NumberFormatException e) {
                    player.sendMessage(Colors.LightPurple + "Invalid input!");
                }
            } else {
                player.sendMessage(Colors.LightPurple + "Current maxlength : " + TweakcraftUtils.maxlength);
            }
            return true;
        } else if (split[0].equalsIgnoreCase("/tp") && player.canUseCommand("/tp")) {
            if (split.length < 2) {
                player.sendMessage(Colors.Rose + "Correct usage is: /tp [player]!");
            } else {

                Player toplayer = etc.getServer().matchPlayer(split[1]);


                if (toplayer != null) {
                    boolean refusetp = TweakcraftUtils.donottp.contains(toplayer.getName());
                    boolean override = false;
                    
                    if(refusetp && player.isInGroup("admin"))
                        if(toplayer.isAdmin())
                            override = false;
                        else
                            override = true;

                    if (toplayer.getName().equalsIgnoreCase(player.getName())) {
                        player.sendMessage(Colors.Rose + "You're already here!");
                    } else {
                        if(refusetp && !override)
                        {
                            TweakcraftUtils.log.info("I refused to tp " + player.getName() + " to " + toplayer.getName());
                            player.sendMessage(Colors.Red + "You cannot teleport to " + toplayer.getName());
                            toplayer.sendMessage(Colors.Red + "TP : " + getPlayerColor(player.getName(), false) + player.getName() + Colors.Red + " tried to teleport to you!");
                        } else {
                            TweakcraftUtils.log.info(player.getName() + " teleported to " + toplayer.getName());
                            toplayer.sendMessage(getPlayerColor(player.getName(), false) + player.getName() + Colors.LightPurple + " teleported to you!");
                            if(refusetp)
                                player.sendMessage(Colors.Red + "TP: Forced tp!");
                            player.teleportTo(toplayer);
                        }
                    }
                } else {
                    player.sendMessage(Colors.Rose + "Can't find user " + split[1] + ".");
                }
            }
            return true;
        } else if(split[0].equalsIgnoreCase("/tpoff") && player.isInGroup("vip")) {
            if(player.isInGroup("admin") && split.length > 1)
            {
                Player p = etc.getServer().matchPlayer(split[1]);
                if(p!=null)
                {
                    if(!TweakcraftUtils.donottp.contains(p.getName()))
                    {
                        TweakcraftUtils.donottp.add(p.getName());
                        player.sendMessage(Colors.Yellow + "TP: They can no longer teleport to " + getPlayerColor(p.getName(), false) + p.getName());
                    } else {
                        player.sendMessage(Colors.Yellow + "TP: " + getPlayerColor(p.getName(), false) + p.getName() + Colors.Yellow + " isn't "
                                            + "on the do-no-tp list!");
                    }
                } else {
                    player.sendMessage(Colors.Yellow + "TP: Cannot find player");
                }
            } else {
                player.sendMessage(Colors.Yellow + "They can no longer tp to you!");
                if(!TweakcraftUtils.donottp.contains(player.getName()))
                    TweakcraftUtils.donottp.add(player.getName());
            }
            return true;
        } else if(split[0].equalsIgnoreCase("/tpon") && player.isInGroup("vip")) {
            if(player.isInGroup("admin") && split.length > 1)
            {
                Player p = etc.getServer().matchPlayer(split[1]);
                if(p!=null)
                {
                    if(TweakcraftUtils.donottp.contains(p.getName()) && !p.isAdmin())
                    {
                        TweakcraftUtils.donottp.remove(p.getName());
                        player.sendMessage(Colors.Yellow + "TP: They can now teleport to " + getPlayerColor(p.getName(), false) + p.getName());
                    } else {
                        player.sendMessage(Colors.Yellow + "TP: " + getPlayerColor(p.getName(), false) + p.getName() + Colors.Yellow +
                                                " isn't on the do-not-tp list!");
                    }
                } else {
                    player.sendMessage(Colors.Yellow + "TP: Cannot find player");
                }
            } else {
                player.sendMessage(Colors.Yellow + "They can now tp to you!");
                if(TweakcraftUtils.donottp.contains(player.getName()))
                    TweakcraftUtils.donottp.remove(player.getName());

            }
            return true;

        } else if(split[0].equalsIgnoreCase("/tplist") && player.isInGroup("admin")) {
            if(TweakcraftUtils.donottp.size()>0)
            {
                String msg;
                player.sendMessage(TweakcraftUtils.col2 + "Current tp-off list : ");
                String color = "";
                for (String name : TweakcraftUtils.donottp) {
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
                player.sendMessage(TweakcraftUtils.col2 + "Current tp-off list is empty!");
            }
            return true;
        } else if(split[0].equalsIgnoreCase("/tpe") && player.canUseCommand("/tp")) {
            return TeleportExclude.handleCommand(split, player);
        }
        return false;
    }
}