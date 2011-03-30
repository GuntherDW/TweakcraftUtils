package com.guntherdw.bukkit.tweakcraft.Ban;

import com.guntherdw.bukkit.tweakcraft.Packages.Ban;
import com.guntherdw.bukkit.tweakcraft.TweakcraftUtils;

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

    public Ban searchBan(String playername)
    {
        Ban ban = null;
        for(String search : bans.keySet())
        {
            if(search.equalsIgnoreCase(playername))
            {
                ban = bans.get(search);
                return ban;
            }
        }
        return ban;
    }

    private void loadBans()
    {
        bans = new HashMap<String, Ban>();
        try{
            File banfile = new File(plugin.getDataFolder(), "banned-players.txt");
            BufferedReader banfilereader = new BufferedReader(new FileReader(banfile));
            String line = "";
            while((line = banfilereader.readLine()) != null)
            {
                if(!line.trim().equals(""))
                {
                    String[] lin = line.split(",");
                    if(lin.length>1)
                        bans.put(line, new Ban(line, lin[1]));
                    else
                        bans.put(line, new Ban(line, ""));
                }
            }
        } catch (FileNotFoundException e) {
            plugin.getLogger().info("[TweakcraftUtils] Ban file not found!");
        } catch (IOException e) {

        }
        // return banlist;
    }

    public boolean isBanned(String playername)
    {
        return searchBan(playername)!=null;
    }

    public boolean isBannedFullname(String playername)
    {
        return bans.containsKey(playername);
    }

    public boolean banPlayer(String playername, String reason)
    {
        if(playername.trim().equals(""))
        {
            plugin.getLogger().info("[TweakcraftUtils] Can't ban an empty player!");
        } else {
            bans.put(playername, new Ban(playername, reason));
            return true;
        }
        return false;
    }

    public Map<String, Ban> getBans()
    {
        return bans;
    }

    public void unBan(String player)
    {
        if(bans.containsKey(player))
        {
            bans.remove(player);
        }
    }

    public void saveBans()
    {
        File banfile = new File(plugin.getDataFolder(), "banned-players.txt");
        plugin.getLogger().info("[TweakcraftUtils] Trying to save banlist!");
        if(!banfile.exists())
        {
            try{
                banfile.createNewFile();
            } catch (IOException e) {
                plugin.getLogger().severe("[TweakcraftUtils] Failed trying to create banlist!");
            }
        }


        try{
            BufferedWriter banfilewriter = new BufferedWriter(new FileWriter(banfile));
            for(String bannedplayer : bans.keySet())
            {
                String line = bannedplayer+","+bans.get(bannedplayer).getReason()+"\n";
                banfilewriter.write(line);
            }
            banfilewriter.close();
            plugin.getLogger().info("[TweakcraftUtils] Save complete!");
        } catch (FileNotFoundException e) {
            plugin.getLogger().info("[TweakcraftUtils] Ban file not found!");
        } catch (IOException e) {
            plugin.getLogger().severe("[TweakcraftUtils] Failed trying to save banlist!");
        }
    }

    public void reloadBans()
    {
        this.loadBans();
        plugin.getLogger().info("[TweakcraftUtils] Loaded banlist, "+bans.size()+" bans and counting!");
    }
}
