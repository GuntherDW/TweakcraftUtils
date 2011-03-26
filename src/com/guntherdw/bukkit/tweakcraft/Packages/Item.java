package com.guntherdw.bukkit.tweakcraft.Packages;

/**
 * @author GuntherDW
 */
public class Item {

    private int itemnumber;
    private byte damage;
    private int defaultstack;

    public Item(int itemnumber, byte damage, int defaultstack)
    {
        this.itemnumber = itemnumber;
        this.damage = damage;
        this.defaultstack = defaultstack;
    }

    public int getItemnumber() {
        return itemnumber;
    }

    public byte getDamage() {
        return damage;
    }

    public int getDefaultstack() {
        return defaultstack;
    }
}
