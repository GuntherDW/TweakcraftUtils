package com.guntherdw.bukkit.tweakcraft.Chat;

import com.guntherdw.bukkit.tweakcraft.Chat.Modes.AdminChat;
import com.guntherdw.bukkit.tweakcraft.Chat.Modes.LocalChat;
import com.guntherdw.bukkit.tweakcraft.ChatMode;
import com.guntherdw.bukkit.tweakcraft.Exceptions.ChatModeException;
import com.guntherdw.bukkit.tweakcraft.TweakcraftUtils;
import org.bukkit.entity.Player;

import java.util.Map;

/**
 * @author GuntherDW
 */
public class ChatHandler {

    public Map<String, ChatMode> chatmodes;
    private TweakcraftUtils plugin;

    public ChatHandler(TweakcraftUtils instance)
    {
        chatmodes.clear();
        chatmodes.put("local", new LocalChat(plugin));
        chatmodes.put("admin", new AdminChat(plugin));
        plugin = instance;
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
}
