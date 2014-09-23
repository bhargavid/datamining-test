package com.mstest.datamining.model;

import java.util.List;

/**
 * Created by bloganathan on 9/22/14.
 */
public class DataConfig {
    private DataFile dataFile;
    private List<Config> configList;

    public DataFile getDataFile() {
        return dataFile;
    }

    public void setDataFile(DataFile dataFile) {
        this.dataFile = dataFile;
    }

    public List<Config> getConfigList() {
        return configList;
    }

    public void setConfigList(List<Config> configList) {
        this.configList = configList;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DataConfig)) return false;

        DataConfig that = (DataConfig) o;

        if (!configList.equals(that.configList)) return false;
        if (!dataFile.equals(that.dataFile)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = dataFile.hashCode();
        result = 31 * result + configList.hashCode();
        return result;
    }
}
