package com.mstest.datamining.model;

/**
 * Created by bdamodaran on 9/22/14.
 */
public enum Algorithm {
    j48("j48"),
    multilayerperceptron("multilayerperceptron"),
    knn("knn"),
    adaboost("adaboost"),
    svm("svm"),
    assn3("assn3"),
    seed("seed");

    private String name;

    private Algorithm(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
