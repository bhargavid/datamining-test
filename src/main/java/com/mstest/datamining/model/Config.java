package com.mstest.datamining.model;

/**
 * Created by bloganathan on 9/21/14.
 */
public class Config {
    private Integer minNumObj;
    private Float confidenceFactor;

    public Integer getMinNumObj() {
        return minNumObj;
    }

    public void setMinNumObj(Integer minNumObj) {
        this.minNumObj = minNumObj;
    }

    public Float getConfidenceFactor() {
        return confidenceFactor;
    }

    public void setConfidenceFactor(Float confidenceFactor) {
        this.confidenceFactor = confidenceFactor;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Config)) return false;

        Config config = (Config) o;

        if (!confidenceFactor.equals(config.confidenceFactor)) return false;
        if (!minNumObj.equals(config.minNumObj)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = minNumObj.hashCode();
        result = 31 * result + confidenceFactor.hashCode();
        return result;
    }
}
