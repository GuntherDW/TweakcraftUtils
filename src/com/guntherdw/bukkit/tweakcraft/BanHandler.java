package com.guntherdw.bukkit.tweakcraft;

import com.guntherdw.bukkit.tweakcraft.Packages.Ban;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

/**
 * @author GuntherDW
 */
public class BanHandler {

    private Map<String, Ban> bans;
    private TweakcraftUtils plugin;

    public BanHandler(TweakcraftUtils instance)
    {
        this.plugin = instance;
        loadBans();
    }

    private void loadBans()
    {
        Map<String, Ban> banlist = new HashMap<String, Ban>();
        try{
            File banfile = new File(plugin.getDataFolder(), "banned-players.txt");
            BufferedReader banfilereader = new BufferedReader(new FileReader(banfile));
            String line = banfilereader.readLine();
            while(line != null)
            {
                banlist.put(line, new Ban(line, ""));
                line = banfilereader.readLine();
            }
        } catch (FileNotFoundException e) {

        } catch (IOException e) {

        }
        // return banlist;
    }

    private boolean isBanned(String playername)
    {
        return bans.containsKey(playername);
    }

    public Map<String, Ban> getBans()
    {
        return bans;
    }
    
}
