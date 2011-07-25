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

package com.guntherdw.bukkit.tweakcraft.DataSources.PersistenceClass;

import com.avaje.ebean.validation.Length;
import com.sun.istack.internal.NotNull;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.List;

/**
 * @author GuntherDW
 */
@Entity()
@Table(name="tcutils_chatgroup")
public class ChatGroup {

    @Id
    @NotNull
    private int Id;

    @NotNull
    @Length(max=25)
    private String chanName;

    @Length(max=100)
    private String joinmsg;

    @Length(max=100)
    private String leavemsg;

    @Length(max=12)
    private String password;

    private List<String> subscribers;

    public int getId() {
        return Id;
    }

    public void setId(int id) {
        Id = id;
    }

    public String getChanName() {
        return chanName;
    }

    public void setChanName(String chanName) {
        this.chanName = chanName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public List<String> getSubscribers() {
        return subscribers;
    }

    public void setSubscribers(List<String> subscribers) {
        this.subscribers = subscribers;
    }

    public String getJoinmsg() {
        return joinmsg;
    }

    public void setJoinmsg(String joinmsg) {
        this.joinmsg = joinmsg;
    }

    public String getLeavemsg() {
        return leavemsg;
    }

    public void setLeavemsg(String leavemsg) {
        this.leavemsg = leavemsg;
    }
}
