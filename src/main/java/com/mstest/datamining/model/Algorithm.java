package com.mstest.datamining.model;

/**
 * Created by bloganathan on 9/22/14.
 */
public enum Algorithm {
    decistiontree("decisiontree"),
    multilayerperceptron("multilayerperceptron"),
    knn("knn"),
    adaboost("adaboost");

    private String name;

    private Algorithm(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
