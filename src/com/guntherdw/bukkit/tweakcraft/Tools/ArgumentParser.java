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

package com.guntherdw.bukkit.tweakcraft.Tools;

import com.guntherdw.bukkit.tweakcraft.Packages.Argument;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author GuntherDW
 */
public class ArgumentParser {

    private Map<String, Argument> _namedargs;
    private List<Argument> _args;

    public ArgumentParser(String[] args) {
        _namedargs = new HashMap<String, Argument>();
        _args = new ArrayList<Argument>();
        int x=0;
        for(String arg: args) {

            String[] argslist = arg.split("\\:", 2);
            // _args.put(argslist)
            if(argslist.length>1) {
                argslist[0] = argslist[0].toLowerCase();
                Argument argument = new Argument(x, argslist[0], argslist[1]);
                _namedargs.put(argslist[0], argument);
                _args.add(argument);
            } else {
                Argument argument = new Argument(x, null, argslist[0]);
                _args.add(argument);
            }
            // arglist
            x++;
        }
    }

    public ArgumentParser() { }

    public int getSize() {
        return _args.size();
    }

    public Object getValue(int pos) {
        if(pos<0)
            return null;
        else if(pos>this.getSize())
            return null;
        else
            return _args.get(pos);
    }

    public Boolean getBoolean(String argname, Boolean defaultval) {
        if(_namedargs.containsKey(argname))
            return Boolean.parseBoolean((String) _namedargs.get(argname).getArgvalue());
        else
            return defaultval;
    }

    public Integer getInteger(String argname, Integer defaultval) {
        if(_namedargs.containsKey(argname))
            return Integer.parseInt((String) _namedargs.get(argname).getArgvalue());
        else
            return defaultval;
    }

    public Boolean getBoolean(int pos, Boolean defaultval) {
        if(pos >=0 && this.getSize()>pos)
            return Boolean.parseBoolean((String) _args.get(pos).getArgvalue());
        else
            return defaultval;
    }

    public Integer getInteger(int pos, Integer defaultval) {
        if(pos >=0 && this.getSize()>pos)
            return Integer.parseInt((String)_args.get(pos).getArgvalue());
        else
            return defaultval;
    }

    public String getString(int pos, String defaultval) {
        if(pos >=0 && this.getSize()>pos)
            return (String)_args.get(pos).getArgvalue();
        else
            return defaultval;
    }

    public String getString(String argname, String defaultval) {
        if(_namedargs.containsKey(argname))
            return (String) _namedargs.get(argname).getArgvalue();
        else
            return defaultval;
    }

    public List<Argument> getDefaultArguments() {
        List<Argument> vals = new ArrayList<Argument>();
        for(Argument arg: _args) {
            if(arg.getArgname()==null)
                vals.add(arg);
        }
        return vals;
    }

    public String[] getNormalArgs() {
        List<Argument> al = this.getDefaultArguments();
        String[] lijst = new String[al.size()];
        for(int x=0;x<al.size();x++)
            lijst[x]=(String)al.get(x).getArgvalue();
        return lijst;
    }

}
