package com.guntherdw.bukkit.tweakcraft;

/**
 * @author GuntherDW
 */
public interface DataSource {

    /**
     * Returns the current Datasource
     * @return the datasource you're using
     */
    public abstract DataSource getDataSource();

    /**
     * Initiate a connection
     * @return false if the conncetion didn't succeed
     */
    public abstract boolean initConnection();

    
}
