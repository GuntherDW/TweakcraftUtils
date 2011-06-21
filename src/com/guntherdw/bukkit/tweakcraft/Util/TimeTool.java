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

/**
 * @author GuntherDW
 */
public class TimeTool {
    public static String calcLeft(Long seconds) {
        if(seconds > Long.MAX_VALUE)
            return null;

        Integer days = 0;
        Integer hours = 0;
        Integer mins = 0;
        Long secs = 0L;
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

        if(timerem.trim().equals(""))
            timerem = "UNDEFINED";
        
        return timerem;
    }

    public static Long calcTime(String tstring) {
        Long l = null;
        Long multi = 0L;
        if(tstring.length()>0) {
            char last = tstring.charAt(tstring.length()-1);
            switch(last) {
                case 's':
                    multi = 1L;
                    break;
                case 'm':
                    multi = 60L;
                    break;
                case 'h':
                    multi = 60L*60L;
                    break;
                case 'd':
                    multi = 60L*60L*24L;
                    break;
                case 'w':
                    multi = 60L*60L*24L*7L;
                    break;
                default:
                    break;
            }
            if(multi != 0) {
                try {
                    Integer i = Integer.parseInt(tstring.substring(0, tstring.length()-1));
                    l = i*multi;
                } catch(NumberFormatException ex) {
                    l = null;
                }
            }
        }
        return l;
    }

    public static String getDurationFull(String tstring) {
        String res = null;
        if(tstring.length()>0) {
            char last = tstring.charAt(tstring.length()-1);
            switch(last) {
                case 's':
                    res = "second";
                    break;
                case 'm':
                    res = "minute";
                    break;
                case 'h':
                    res = "hour";
                    break;
                case 'd':
                    res = "day";
                    break;
                case 'w':
                    res = "week";
                    break;
                default:
                    break;
            }
            try {
                Integer i = Integer.parseInt(tstring.substring(0, tstring.length()-1));
                if(i>1) res=res+"s";
            } catch(NumberFormatException ex) {
                res = null;
            }
        }
        return res;
    }
}
