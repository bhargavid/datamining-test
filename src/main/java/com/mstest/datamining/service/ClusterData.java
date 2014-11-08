package com.mstest.datamining.service;

public class ClusterData {
    private int noOfCluster;
    private double avgSilCoeff;
    private double pctCorrect;


    public int getNoOfCluster() {
        return noOfCluster;
    }
    public void setNoOfCluster(int noOfCluster) {
        this.noOfCluster = noOfCluster;
    }
    public double getAvgSilCoeff() {
        return avgSilCoeff;
    }
    public void setAvgSilCoeff(double avgSilCoeff) {
        this.avgSilCoeff = avgSilCoeff;
    }
    public double getPctCorrect() {
        return pctCorrect;
    }
    public void setPctCorrect(double pctCorrect) {
        this.pctCorrect = pctCorrect;
    }
}
