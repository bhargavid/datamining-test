package com.mstest.datamining.model;

import java.util.List;

/**
 * Created by bdamodaran on 9/22/14.
 */
public class DataConfig {
    private DataFile dataFile;
    private List<Config> configs;

    public DataFile getDataFile() {
        return dataFile;
    }

    public void setDataFile(DataFile dataFile) {
        this.dataFile = dataFile;
    }

    public List<Config> getConfigs() {
        return configs;
    }

    public void setConfigs(List<Config> configs) {
        this.configs = configs;
    }
}
