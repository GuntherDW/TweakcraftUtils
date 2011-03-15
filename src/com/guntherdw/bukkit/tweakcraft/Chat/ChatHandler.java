package com.guntherdw.bukkit.tweakcraft.Chat;

import com.guntherdw.bukkit.tweakcraft.Chat.Modes.AdminChat;
import com.guntherdw.bukkit.tweakcraft.Chat.Modes.LocalChat;
import com.guntherdw.bukkit.tweakcraft.ChatMode;
import com.guntherdw.bukkit.tweakcraft.Exceptions.ChatModeException;
import com.guntherdw.bukkit.tweakcraft.TweakcraftUtils;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

/**
 * @author GuntherDW
 */
public class ChatHandler {

    public Map<String, ChatMode> chatmodes = new HashMap<String, ChatMode>();
    private TweakcraftUtils plugin;
    public Map<String, String> playerchatmode = new HashMap<String, String>();

    public ChatHandler(TweakcraftUtils instance)
    {
        plugin = instance;
        chatmodes.clear();
        chatmodes.put("admin", new AdminChat(plugin)); /* This one has a higher priority! */
        chatmodes.put("local", new LocalChat(plugin));
    }

    public ChatMode getChatMode(String mode) throws ChatModeException
    {
        if(chatmodes.containsKey(mode))
        {
            return chatmodes.get(mode);
        } else {
            throw new ChatModeException("No chat mode found by that name!");
        }
    }

    public ChatMode getPlayerChatMode(Player player)
    {
        if(playerchatmode.containsKey(player.getName()))
        {
            return chatmodes.get(playerchatmode.get(player.getName()));
        }
        return null;
    }


    public String getPlayerChatModeString(String player)
    {
        if(playerchatmode.containsKey(player))
        {
            return playerchatmode.get(player);
        }
        return null;
    }


    public String getBypassChar()
    {
        return "!";
    }

    public void removePlayer(Player player)
    {
        for(ChatMode cm : chatmodes.values())
        {
            cm.removeRecipient(player.getName());
        }
    }



    public void setPlayerchatmode(String player, String mode) throws ChatModeException
    {
        if(playerchatmode.containsKey(player))
        {
            try{
                ChatMode cm = getChatMode(playerchatmode.get(player));
                if(!(cm instanceof AdminChat))
                {
                    cm.removeRecipient(player);
                }
            } catch(ChatModeException e) {
                
            }
        }
        if(mode == null)
        {
            playerchatmode.remove(player);
        } else {
            ChatMode cm = getChatMode(mode);
            if(cm instanceof AdminChat)
            {
                Player p = plugin.getServer().getPlayer(player);
                if(p != null)
                    if(!plugin.check(p, "admon"))
                        cm.addRecipient(player);
            }
            playerchatmode.put(player,  mode);
        }

    }


}
