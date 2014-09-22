package com.mstest.datamining.model;

/**
 * Created by bloganathan on 9/21/14.
 */
public class DataFile {
    private String trainingFile;
    private String testFile;

    public String getTrainingFile() {
        return trainingFile;
    }

    public void setTrainingFile(String trainingFile) {
        this.trainingFile = trainingFile;
    }

    public String getTestFile() {
        return testFile;
    }

    public void setTestFile(String testFile) {
        this.testFile = testFile;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DataFile)) return false;

        DataFile dataFile = (DataFile) o;

        if (!testFile.equals(dataFile.testFile)) return false;
        if (!trainingFile.equals(dataFile.trainingFile)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = trainingFile.hashCode();
        result = 31 * result + testFile.hashCode();
        return result;
    }
}
