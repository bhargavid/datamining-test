package com.mstest.datamining.app;

import com.mstest.datamining.model.Algorithm;

/**
 * Created by bloganathan on 9/20/14.
 */
public class AppCommandOptions {
    public static final String DECISION_TREE = Algorithm.decistiontree.getName();
    public static final String OUTPUT_DIR = "output_dir";
    public static final String GNUPLOT_BIN = "gnuplot_bin";
    public static final String MULTILAYER_PERCEPTRON = Algorithm.multilayerperceptron.getName();
    public static final String KNN = Algorithm.knn.getName();
    public static final String CONFIGURE = "configure";
}
