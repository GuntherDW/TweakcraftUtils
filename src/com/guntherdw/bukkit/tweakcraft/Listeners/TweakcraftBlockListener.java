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

package com.guntherdw.bukkit.tweakcraft.Listeners;

import com.guntherdw.bukkit.tweakcraft.TweakcraftUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockFormEvent;
import org.bukkit.event.block.BlockListener;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;

import java.util.Random;

/**
 * @author GuntherDW
 */
public class TweakcraftBlockListener extends BlockListener {

    private TweakcraftUtils plugin = null;

    public TweakcraftBlockListener(TweakcraftUtils instance) {
        this.plugin = instance;
    }

    public void onSignChange(SignChangeEvent event) {
        if(event.isCancelled()) return;
        if(plugin.getConfigHandler().enableTweakTravel&&event.getLine(0).equalsIgnoreCase("[TweakTravel]") && !plugin.check(event.getPlayer(), "tweaktravel.create")) {
            event.getPlayer().sendMessage(ChatColor.RED+"You don't have permission to create TweakTravel signs!");
            event.setCancelled(true);
        }
    }

    public void onBlockForm(BlockFormEvent event) {
        if(event.isCancelled()) return;
        if(!plugin.getConfigHandler().enableSnowPile) return;

        Block b = event.getBlock();
        BlockState bnewstate = event.getNewState();
        BlockState boldstate = b.getState();
        int rate = plugin.getConfigHandler().snowPileRate;

        if(boldstate.getTypeId() == 78) {
            Random rnd = new Random();
            byte snowstate = boldstate.getData().getData();
            if(snowstate<7 && (rnd.nextInt(Integer.MAX_VALUE)%rate*25)==0)  {
                snowstate += 1;
                bnewstate.setData(Material.SNOW.getNewData(snowstate));
            } else {
                event.setCancelled(true);
            }

        }
    }


    public void onBlockBreak(BlockBreakEvent event) {
        if(event.isCancelled()) return;
        if(!plugin.getConfigHandler().enableSnowDouble) return;

        Block block = event.getBlock();

        if(block.getTypeId() == 78&&block.getData()>3)
            block.getWorld().dropItemNaturally(block.getLocation(), new ItemStack(Material.SNOW_BALL, 1));
    }

}
