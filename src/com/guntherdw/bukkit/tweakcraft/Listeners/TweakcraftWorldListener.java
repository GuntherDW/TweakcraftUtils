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
import org.bukkit.Chunk;
import org.bukkit.block.BlockState;
import org.bukkit.block.Furnace;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.event.world.WorldListener;

/**
 * @author GuntherDW
 */
public class TweakcraftWorldListener extends WorldListener {

    private TweakcraftUtils plugin;

    public TweakcraftWorldListener(TweakcraftUtils instance) {
        this.plugin = instance;
    }

    public void onChunkUnload(ChunkUnloadEvent event) {
        if (event.isCancelled())
            return;

        if (plugin.getConfigHandler().stopChunkUnloadBurningFurnace) {
            Chunk chunk = event.getChunk();
            BlockState[] states = chunk.getTileEntities();
            for (BlockState state : states) {
                if (state instanceof Furnace) {
                    // Furnace f = (Furnace) state;
                    if (((Furnace) state).getBurnTime() > 0) {
                        event.setCancelled(true);
                        return;
                    }
                }
            }
        }
    }
}
