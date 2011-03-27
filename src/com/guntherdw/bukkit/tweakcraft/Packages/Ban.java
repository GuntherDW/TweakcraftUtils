package com.guntherdw.bukkit.tweakcraft.Packages;

/**
 * @author GuntherDW
 */
public class Ban {

    String player;
    String reason;

    public Ban(String Player, String Reason)
    {
        this.player = Player;
        this.reason = Reason;
    }

    public String getPlayer() {
        return player;
    }

    public String getReason() {
        if(reason == null || reason.isEmpty())
        {
            return "You are banned!";
        } else {
            return reason;
        }
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
