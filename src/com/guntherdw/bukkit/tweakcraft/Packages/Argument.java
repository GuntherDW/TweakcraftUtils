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
public class Argument {
    private Integer id;
    private String argname;
    private Object argvalue;

    public Argument(Integer position, String argname, Object argvalue) {
        this.id = position; this.argname = argname; this.argvalue = argvalue;
    }

    public Argument(String argname, Object argvalue) {
        this.argname = argname; this.argvalue = argvalue;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getArgname() {
        return argname;
    }

    public void setArgname(String argname) {
        this.argname = argname;
    }

    public Object getArgvalue() {
        return argvalue;
    }

    public void setArgvalue(Object argvalue) {
        this.argvalue = argvalue;
    }
}