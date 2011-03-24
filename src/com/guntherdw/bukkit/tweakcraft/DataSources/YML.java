package com.guntherdw.bukkit.tweakcraft.DataSources;

import com.guntherdw.bukkit.tweakcraft.DataSource;

/**
 * @author GuntherDW
 */
public class YML implements DataSource {
    public DataSource getDataSource() {
        return this;
    }

    public boolean initConnection(String source) {
        return false;
    }
}
