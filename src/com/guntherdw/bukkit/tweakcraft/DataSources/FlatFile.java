package com.guntherdw.bukkit.tweakcraft.DataSources;

import com.guntherdw.bukkit.tweakcraft.DataSource;

/**
 * @author GuntherDW
 */
public class FlatFile implements DataSource {
    public DataSource getDataSource() {
        return this;
    }

    public boolean initConnection(String file) {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
