package com.mstest.datamining.app;

import com.mstest.datamining.model.Algorithm;

/**
 * Created by bdamodaran on 9/20/14.
 */
public class AppCommandOptions {
    public static final String J48 = Algorithm.j48.getName();
    public static final String OUTPUT_DIR = "output_dir";
    public static final String MULTILAYER_PERCEPTRON = Algorithm.multilayerperceptron.getName();
    public static final String KNN = Algorithm.knn.getName();
    public static final String CONFIGURE = "configure";
    public static final String ADABOOST = Algorithm.adaboost.getName();
    public static final String SVM = Algorithm.svm.getName();
    public static final String ASSN3 = Algorithm.assn3.getName();
    public static final String SEED = Algorithm.seed.getName();
    public static final String EM = Algorithm.em.getName();
}
