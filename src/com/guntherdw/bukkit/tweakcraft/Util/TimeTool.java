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

package com.guntherdw.bukkit.tweakcraft.Util;

import java.security.PrivateKey;

/**
 * @author GuntherDW
 */
public class TimeTool {
    public static String calcLeft(Integer seconds) {
        if(seconds > Integer.MAX_VALUE)
            return null;

        Integer days = 0;
        Integer hours = 0;
        Integer mins = 0;
        Integer secs = 0;
        while(seconds > 60) {
            if(seconds > 60*60*24) {
                days++;
                seconds-=60*60*24;
            } else if(seconds > 60*60) {
                hours++;
                seconds-=60*60;
            } else if(seconds > 60) {
                mins++;
                seconds -= 60;
            }
        }
        secs = seconds;
        String timerem = "";

        if(days>0)
            timerem += days+" days, ";
        if(hours>0)
            timerem += hours+" hours, ";
        if(mins>0)
            timerem += mins+" minutes, ";
        if(secs>0)
            timerem += secs+" seconds, ";
        if(timerem.length()>2)
            timerem = timerem.substring(0, timerem.length()-2);
        
        return timerem;
    }
}
