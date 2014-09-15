package com.mstest.datamining.app;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.mstest.datamining.model.Axis;
import com.mstest.datamining.model.Graph;
import com.mstest.datamining.utils.FileUtil;

import weka.classifiers.Evaluation;
import weka.classifiers.trees.J48;
import weka.core.Instances;

public class DataAnalyzer {

    private static final String PERF_GRAPH_X_AXIS = "TRAINING_SIZE";
    private static final String PERF_GRAPH_Y1_AXIS = "PERFORMANCE_TEST_DATA";
    private static final String PERF_GRAPH_Y2_AXIS = "PERFORAMANCE_TRAINING_DATA";

    private static final String ERROR_GRAPH_X_AXIS = "TRAINING_SIZE";
    private static final String ERROR_GRAPH_Y1_AXIS = "ERROR_TEST_DATA";
    private static final String ERROR_GRAPH_Y2_AXIS = "ERROR_TRAINING_DATA";

    private static final String PERF_GRAPH = "PERFORMANCE_GRAPH";
    private static final String ERROR_GRAPH = "ERROR_GRAPH";

    public static void main(String[] args) throws Exception {
        Graph perfGraph = new Graph();
        Graph errorGraph = new Graph();

        List<Axis> perf_points = new ArrayList<Axis>();
        List<Axis> error_points = new ArrayList<Axis>();

        File perf_file = null;
        File error_file = null;

        try {

            BufferedReader reader = new BufferedReader(
                    new FileReader(
                            "C:/MyPC/GATech/MachineLearning/DataSets/bank/bank-full-training_70_pct_Noise.arff"));
            // BufferedReader reader = new BufferedReader( new
            // FileReader("C:/weather.nominal.arff"));
            Instances train = new Instances(reader);
            BufferedReader testReader = new BufferedReader(
                    new FileReader(
                            "C:/MyPC/GATech/MachineLearning/DataSets/bank/bank-full-test_30_pct.arff"));
            Instances test = new Instances(testReader);
            train.setClassIndex(train.numAttributes() - 1);
            test.setClassIndex(test.numAttributes() - 1);
            reader.close();
            testReader.close();
            J48 j48Classifier = new J48();
            float v = new Float(.1);
            j48Classifier.setConfidenceFactor(v);
            j48Classifier.setMinNumObj(100);
            j48Classifier.buildClassifier(train);
            Integer percent = new Integer(0);
            for (int i = 0; i < 2; i++) {

                int splitTrainSize = (int) Math.round(train.numInstances()
                        * percent / 100);
                Instances splitTrain = new Instances(train, 0, splitTrainSize);
                // evaluate classifier and print some statistics
                Evaluation splitTrainEval = new Evaluation(splitTrain);
                Evaluation testEval = new Evaluation(test);
                if (splitTrain.numInstances() > 10) {
                    System.out.println("\n Cross Validation \n");
                    splitTrainEval.crossValidateModel(j48Classifier,
                            splitTrain, 10, new Random(1));
                } else {
                    System.out.println("\n No Cross Validation \n");
                    splitTrainEval.evaluateModel(j48Classifier, splitTrain);
                }
                testEval.evaluateModel(j48Classifier, test);
                System.out.println("\n Current Iteration is " + i);
                System.out.println(splitTrainEval.toSummaryString(
                        "\n Train Results\n======\n", false));
                System.out.println(testEval.toSummaryString(
                        "\nTest Results\n======\n", false));
                Double testPctCorrect = new Double(0); // used for performance
                                                       // chart
                Double splitTrainPctCorrect = new Double(0); // used for
                                                             // performance
                                                             // chart
                testPctCorrect = testEval.pctCorrect();
                splitTrainPctCorrect = splitTrainEval.pctCorrect();
                Double testErrorRate = new Double(0); // used for RMS error
                                                      // chart
                Double splitTrainErrorRate = new Double(0); // used for RMS
                                                            // error
                                                            // chart
                testErrorRate = testEval.errorRate();
                splitTrainErrorRate = splitTrainEval.errorRate();

                // add graph points here
                Axis perf_point = new Axis();
                Axis error_point = new Axis();

                perf_point.setX(new Double(splitTrainSize));
                perf_point.setY1(testPctCorrect);
                perf_point.setY2(splitTrainPctCorrect);

                error_point.setX(new Double(splitTrainSize));
                error_point.setY1(testErrorRate);
                error_point.setY2(splitTrainErrorRate);

                perf_points.add(perf_point);
                error_points.add(error_point);

                percent += 5;
            }

            perfGraph.setAxisList(perf_points);
            perfGraph.setXAxis(PERF_GRAPH_X_AXIS);
            perfGraph.setY1Axis(PERF_GRAPH_Y1_AXIS);
            perfGraph.setY2Axis(PERF_GRAPH_Y2_AXIS);

            errorGraph.setAxisList(error_points);
            errorGraph.setXAxis(ERROR_GRAPH_X_AXIS);
            errorGraph.setY1Axis(ERROR_GRAPH_Y1_AXIS);
            errorGraph.setY2Axis(ERROR_GRAPH_Y2_AXIS);

            perf_file = FileUtil.createDatFile(perfGraph, PERF_GRAPH);
            error_file = FileUtil.createDatFile(errorGraph, ERROR_GRAPH);
        } catch (IOException ie) {
            System.out.println("IO Exception");
            ie.printStackTrace();
        } finally {
            if (perf_file != null) {
                perf_file.delete();
            }
            if (error_file != null) {
                error_file.delete();
            }
        }
    }
}
