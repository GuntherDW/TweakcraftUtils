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
public final class ArgumentParser {

    private Map<String, Argument> _namedargs;
    private List<Argument> _args;
    private List<String> _flagsUsed;

    public ArgumentParser(String[] args) {
        _namedargs = new HashMap<String, Argument>();
        _args = new ArrayList<Argument>();
        _flagsUsed = new ArrayList<String>();
        int x=0;
        for(String arg: args) {
            if(arg.startsWith("-")) { /* Flag type */
                Argument argument = new Argument(x, arg.substring(1), null);
                argument.setArgType(true);
                if(args.length > x+1) { /* Add the value as well! */
                    argument.setArgvalue(args[x+1]);
                }
                _namedargs.put(argument.getArgname(), argument);
                _args.add(argument);
            } else { /* Possible argname:argvalue type */
                String[] argslist = arg.split("\\:", 2);
                // _args.put(argslist)
                if(argslist.length>1) {
                    argslist[0] = argslist[0].toLowerCase();
                    Argument argument = new Argument(x, argslist[0], argslist[1]);
                    argument.setArgType(false);
                    _namedargs.put(argslist[0], argument);
                    _args.add(argument);
                } else {
                    Argument argument = new Argument(x, null, argslist[0]);
                    _args.add(argument);
                }
            }
            // arglist
            x++;
        }
    }

    public ArgumentParser() { }

    public void setUsed(String argName, boolean state) {
        Argument arg = null;
        // First the named argument map, this is the easiest one.
        arg = _namedargs.get(argName);
        if(arg!=null)  {
            arg.set_used(state);
            if(arg.isArgType()) {
                int pos = arg.getId();
                if(_args.size()>pos+1) _args.get(pos+1).set_used(true);
            }
        }
        // Second the List<Argument> one, this'll require a for loop
        for(int x=0; x<_args.size(); x++) {
            arg = _args.get(x);
            if(arg.getArgname() != null && arg.getArgname().equals(argName)) arg.set_used(state);
            if(arg.isArgType()) {
                if(_args.size()>x+1) _args.get(x+1).set_used(true);
            }
        }
    }

    public int getSize() {
        return _args.size();
    }

    public Object getValue(int pos) {
        if(pos<0)
            return null;
        else if(pos>this.getSize())
            return null;
        else {
            this.setUsed(_args.get(pos).getArgname(), true);
            return _args.get(pos).getArgvalue();
        }
    }

    public Boolean getBoolean(String argname, Boolean defaultval) {
        if(_namedargs.containsKey(argname)) {
            this.setUsed(argname, true);
            return Boolean.parseBoolean((String) _namedargs.get(argname).getArgvalue());
        } else
            return defaultval;
    }

    public Integer getInteger(String argname, Integer defaultval) {
        if(_namedargs.containsKey(argname)) {
            this.setUsed(argname, true);
            return Integer.parseInt((String) _namedargs.get(argname).getArgvalue());
        } else
            return defaultval;
    }

    public Boolean getBoolean(int pos, Boolean defaultval) {
        if(pos >=0 && this.getSize()>pos) {
            this.setUsed(_args.get(pos).getArgname(), true);
            return Boolean.parseBoolean((String) _args.get(pos).getArgvalue());
        } else
            return defaultval;
    }

    public Integer getInteger(int pos, Integer defaultval) {
        if(pos >=0 && this.getSize()>pos) {
            this.setUsed(_args.get(pos).getArgname(), true);
            return Integer.parseInt((String)_args.get(pos).getArgvalue());
        } else
            return defaultval;
    }

    public String getString(int pos, String defaultval) {
        if(pos >=0 && this.getSize()>pos) {
            this.setUsed(_args.get(pos).getArgname(), true);
            return (String)_args.get(pos).getArgvalue();
        } else
            return defaultval;
    }

    public String getString(String argname, String defaultval) {
        if(_namedargs.containsKey(argname)) {
            this.setUsed(argname, true);
            return (String) _namedargs.get(argname).getArgvalue();
        }
        else
            return defaultval;
    }

    public boolean is_Flag(Argument arg) {
        boolean flag = false;
        int pos = arg.getId();
        if(pos < _args.size()) {
            if(arg.isArgType()) flag = true;
        }
        if(!flag && pos > 0) {
            if(_args.get(pos-1).isArgType()) flag = true;
        }
        return flag;
    }

    public List<Argument> getDefaultArguments() {
        List<Argument> vals = new ArrayList<Argument>();
        for(Argument arg: _args) {
            if(!is_Flag(arg) && arg.getArgname() == null)
                vals.add(arg);
        }
        return vals;
    }

    public List<Argument> getUnusedArgsList() {
        List<Argument> vals = new ArrayList<Argument>();
        for(Argument arg : _args) {
            if(is_Flag(arg) && !arg.is_used()) {
                vals.add(arg);
            } else if(arg.getArgname()==null || !arg.is_used())
                vals.add(arg);
        }
        return vals;
    }

    public boolean isflagUsed(String needle) { /* Reserved for simple flag checking */
        for(Argument arg : _args) {
            String lookFor = null;
            if(arg.isArgType())
                if(arg.getArgname().equals(needle)) {
                    _flagsUsed.add(needle);
                    return true;
                }
        }
        return false;
    }

    public String[] getUnusedArgs() {
        // return (String[]) this.getUnusedArgsList().toArray();
        List<Argument> als = this.getUnusedArgsList();
        // String[] args = new String[als.size()];
        List<String> args = new ArrayList<String>();
        int x=0;
        while(x<als.size()) {
            String tmp = "";
            Argument arg = als.get(x);
            if(is_Flag(arg)) {
                boolean flagUsed = false;
                if(arg.getArgname()!=null) {
                    flagUsed =  _flagsUsed.contains(arg.getArgname());
                    if(!flagUsed) { args.add("-"+arg.getArgname()); }
                }
                if(arg.getArgvalue()!=null) { x++; tmp+=(String)arg.getArgvalue(); args.add(tmp); }
            } else {
                if(arg.getArgname()!=null) { tmp+=arg.getArgname()+":"; }
                if(arg.getArgvalue()!=null) tmp+=(String)arg.getArgvalue();
                args.add(tmp);
            }
            x++;
        }
        return args.toArray(new String[args.size()]);
    }

    public String[] getNormalArgs() {
        List<Argument> al = this.getDefaultArguments();
        String[] lijst = new String[al.size()];
        for(int x=0;x<al.size();x++) {
            lijst[x]=(String)al.get(x).getArgvalue();
        }
        return lijst;
    }

}