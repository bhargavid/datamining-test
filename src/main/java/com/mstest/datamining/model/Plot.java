package com.mstest.datamining.model;

/**
 * Created by bloganathan on 9/24/14.
 */
public class Plot {
    private Axis perfPoint;
    private Axis errorPoint;

    public Axis getPerfPoint() {
        return perfPoint;
    }

    public void setPerfPoint(Axis perfPoint) {
        this.perfPoint = perfPoint;
    }

    public Axis getErrorPoint() {
        return errorPoint;
    }

    public void setErrorPoint(Axis errorPoint) {
        this.errorPoint = errorPoint;
    }
}
