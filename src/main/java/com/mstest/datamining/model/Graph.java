package com.mstest.datamining.model;

import java.util.List;

public class Graph {
    private String xAxis;
    private String y1Axis;
    private String y2Axis;
    private List<Axis> axisList;
    
    public String getXAxis() {
        return xAxis;
    }
    public void setXAxis(String xAxis) {
        this.xAxis = xAxis;
    }
    public String getY1Axis() {
        return y1Axis;
    }
    public void setY1Axis(String y1Axis) {
        this.y1Axis = y1Axis;
    }
    
    public String getY2Axis() {
        return y2Axis;
    }
    public void setY2Axis(String y2Axis) {
        this.y2Axis = y2Axis;
    }
    
    public List<Axis> getAxisList() {
        return axisList;
    }
    public void setAxisList(List<Axis> axisList) {
        this.axisList = axisList;
    }            
}
