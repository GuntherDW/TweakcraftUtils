/*
 * Copyright (c) 2011 GuntherDW
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */

package com.guntherdw.bukkit.tweakcraft.Packages;

import com.guntherdw.bukkit.tweakcraft.TweakcraftUtils;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

/**
 * @author GuntherDW
 */
public class ItemDB {

    private TweakcraftUtils plugin;
    private Map<String, com.guntherdw.bukkit.tweakcraft.Packages.Item> itemmap;

    public ItemDB(TweakcraftUtils instance) {
        this.plugin = instance;
    }

    public void loadDataBase() {
        itemmap = new HashMap<String, Item>();
        File itemfile = new File(plugin.getDataFolder(), "items.csv");

        try {
            BufferedReader reader = new BufferedReader(new FileReader(itemfile));
            String itemline = "";
            Integer line = 1;
            while ((itemline = reader.readLine()) != null) {
                if (!itemline.startsWith("#")) {
                    try {
                        String[] split = itemline.split(",");
                        String itemname = split[0];
                        Integer itemnr = Integer.parseInt(split[1]);
                        Byte damage = Byte.parseByte(split[2]);
                        Integer stacks = Integer.parseInt(split[3]);
                        itemmap.put(itemname, new Item(itemnr, damage, stacks));
                    } catch (Throwable e) {
                        plugin.getLogger().info("[TweakcraftUtils] Item error at line " + line);
                    }
                }
                line++;
            }
        } catch (FileNotFoundException e) {
            plugin.getLogger().info("[TweakcraftUtils] Item DB not found!");
            plugin.getLogger().info("[TweakcraftUtils] looked in " + plugin.getDataFolder());
        } catch (IOException e) {
            plugin.getLogger().info("[TweakcraftUtils] Item DB IO error!");
        }
        plugin.getLogger().info("[TweakcraftUtils] Loaded item DB, " + itemmap.size() + " items found!");
    }

    public Item getItem(String name) {
        if (itemmap.containsKey(name)) {
            return itemmap.get(name);
        } else {
            return null;
        }
    }

}
