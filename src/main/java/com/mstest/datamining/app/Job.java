package com.mstest.datamining.app;

/**
 * Created by bloganathan on 9/20/14.
 */
public enum Job {
    decisiontrees("decisiontrees");

    String job_desc;

    private Job(String job_desc) {
        this.job_desc = job_desc;
    }
}
