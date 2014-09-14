package com.mstest.datamining.model;

import java.util.List;

public class Graph {
    private String xAxis;
    private String yAxis;
    private List<Axis> axisList;
    
    public String getXAxis() {
        return xAxis;
    }
    public void setXAxis(String xAxis) {
        this.xAxis = xAxis;
    }
    public String getYAxis() {
        return yAxis;
    }
    public void setYAxis(String yAxis) {
        this.yAxis = yAxis;
    }
    public List<Axis> getAxisList() {
        return axisList;
    }
    public void setAxisList(List<Axis> axisList) {
        this.axisList = axisList;
    }            
}
