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

/**
 * @author GuntherDW
 */
public final class TamerMode {

    private Boolean state;
    private TamerModes mode;
    private Integer data;

    public static enum TamerModes {
        INFO,
        TAME,
        ANGRY,
        HEAL,
        SIT,
        SETAGE,
        NONE
    }

    public TamerMode(Boolean state, TamerModes mode) {
        this.state = state;
        this.mode = mode;
    }

    public Boolean getState() {
        return state;
    }

    public TamerModes getMode() {
        return mode;
    }

    public void setMode(TamerModes mode) {
        this.mode = mode;
    }

    public void setState(Boolean state) {
        this.state = state;
    }

    public Integer getData() {
        return data;
    }

    public void setData(Integer data) {
        this.data = data;
    }
}
