package com.mstest.datamining.service;

import com.mstest.datamining.app.AppCommandOptions;
import com.mstest.datamining.model.*;
import com.mstest.datamining.utils.FileUtil;
import weka.classifiers.Evaluation;
import weka.classifiers.lazy.IBk;
import weka.core.EuclideanDistance;
import weka.core.Instances;
import weka.core.ManhattanDistance;
import weka.core.NormalizableDistance;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import static com.mstest.datamining.utils.CommonUtil.emptyIfNull;
import static com.mstest.datamining.utils.CommonUtil.fillConfigs;

/**
 * Created by bdamodaran on 9/23/14.
 */
public class KnnServiceImpl implements KnnService {
    private static final String PERF_GRAPH_X_AXIS_1 = "TRAINING_SIZE";
    private static final String PERF_GRAPH_X_AXIS_2 = "K";
    private static final String PERF_GRAPH_Y1_AXIS = "PERFORMANCE_TEST_DATA";
    private static final String PERF_GRAPH_Y2_AXIS = "PERFORAMANCE_TRAINING_DATA";

    private static final String ERROR_GRAPH_X_AXIS_1 = "TRAINING_SIZE";
    private static final String ERROR_GRAPH_X_AXIS_2 = "TRAINING_SIZE";
    private static final String ERROR_GRAPH_Y1_AXIS = "ERROR_TEST_DATA";
    private static final String ERROR_GRAPH_Y2_AXIS = "ERROR_TRAINING_DATA";

    private static final String PERF_GRAPH = "PERF_GRAPH";
    private static final String ERR_GRAPH = "ERR_GRAPH";

    private static final String TMP_FILE_PATH = "/tmp/datamining-test/knn";
    private static final String FILE_FORMAT = ".dat";

    @Override
    public void run(Map<String, Object> params_map) throws Exception {
        execute(params_map);
    }

    private void execute(Map<String, Object> params_map) throws Exception {
        System.out.println("Executing decision tree algorithm");

        InputStream testFileIn = null;
        InputStream trainingFileIn = null;
        File tmp_perf_file = null;
        File tmp_error_file = null;
        File perf_file = null;
        File error_file = null;

        String output_dir = (String) params_map.get(AppCommandOptions.OUTPUT_DIR);
        if (output_dir == null)
            output_dir = TMP_FILE_PATH;

        // check if the output directory exists
        File theDir = new File(output_dir);

        if (!FileUtil.createDirs(theDir)) {
            System.out.println("ERROR:: Failed to create output directory. " + output_dir);
            return;
        }

        List<DataConfig> dataConfigs = new ArrayList<DataConfig>();
        fillConfigs(dataConfigs, Algorithm.knn);

        for(DataConfig dataConfig: emptyIfNull(dataConfigs)) {
            DataFile dataFile = dataConfig.getDataFile();
            List<Config> configs = dataConfig.getConfigs();

            if (dataFile == null || configs == null || configs.isEmpty())
                continue;

            String training_file_name = dataFile.getTrainingFile();
            String test_file_name = dataFile.getTestFile();

            System.out
                    .println("Running for Training File: " + training_file_name + " Test File: " + test_file_name);

            for(int indx = 1; indx <= 2; indx++) {

                List<Axis> perf_points = new ArrayList<Axis>();
                List<Axis> error_points = new ArrayList<Axis>();

                Graph perfGraph = new Graph();
                Graph errorGraph = new Graph();

                testFileIn = getClass().getResourceAsStream("/" + test_file_name);
                trainingFileIn = getClass().getResourceAsStream("/" + training_file_name);

                BufferedReader trainingReader = new BufferedReader(new InputStreamReader(
                        trainingFileIn));


                BufferedReader testReader = new BufferedReader(
                        new InputStreamReader(testFileIn));

                Instances train = new Instances(trainingReader);
                Instances test = new Instances(testReader);
                train.setClassIndex(train.numAttributes() - 1);
                test.setClassIndex(test.numAttributes() - 1);
                trainingReader.close();
                testReader.close();
                Integer percent = new Integer(0);
                System.out.println("Training size length: " + train.numInstances());

                //get the labels here
                //TODO check if config exists
                Integer k = null;
                String distance_str = null;

                Config config = configs.get(0);
                for(Label label: config.getLabels()) {
                    if(Constant.K.equals(label.getName()))
                        k = (Integer) label.getValue();
                    if(Constant.DISTANCE.equals(label.getName()))
                        distance_str = (String) label.getValue();
                }

                if(indx == 1) {
                    //run with same k, varying the split train
                    for (int i = 0; i < 20; i++) {
                        int splitTrainSize = (int) Math.round(train.numInstances()
                                                              * percent / 100);
                        Instances splitTrain = new Instances(train, 0, splitTrainSize);
                        splitTrain.setClassIndex(splitTrain.numAttributes() - 1);

                        IBk IBkClassifier = new IBk();
                        IBkClassifier.setCrossValidate(false);

                        NormalizableDistance distance = null;

                        if(Constant.MANHATTAN_DISTANCE.equalsIgnoreCase(distance_str))
                            distance = new ManhattanDistance();
                        else if(Constant.EUCLIDEAN_DISTANCE.equalsIgnoreCase(distance_str))
                            distance = new EuclideanDistance();

                        IBkClassifier.getNearestNeighbourSearchAlgorithm().setDistanceFunction(distance);

                        IBkClassifier.setKNN(k);
                        IBkClassifier.buildClassifier(splitTrain);

                        // evaluate classifier and print some statistics
                        Evaluation splitTrainEval = new Evaluation(splitTrain);
                        Evaluation testEval = new Evaluation(test);
                        if (splitTrain.numInstances() > 10) {
                            System.out.println("\n Cross Validation \n");
                            splitTrainEval.crossValidateModel(IBkClassifier,
                                    splitTrain, 10, new Random(1));
                        } else {
                            System.out.println("\n No Cross Validation \n");
                            splitTrainEval.evaluateModel(IBkClassifier, splitTrain);
                        }
                        testEval.evaluateModel(IBkClassifier, test);

                        System.out.println("\n Current Iteration is " + i);
                        System.out.println(splitTrainEval.toSummaryString(
                                "\n Train Results\n======\n", false));
                        System.out.println(testEval.toSummaryString(
                                "\nTest Results\n======\n", false));
                        Double testPctCorrect = testEval.pctCorrect();
                        Double splitTrainPctCorrect =splitTrainEval.pctCorrect();

                        Double testErrorRate = testEval.errorRate();
                        Double splitTrainErrorRate = splitTrainEval.errorRate();

                        System.out.println("testPctCorrect" + testPctCorrect);
                        System.out.println("splitTrainPctCorrect"
                                           + splitTrainPctCorrect);
                        System.out.println("testErrorRate" + testErrorRate);
                        System.out.println("splitTrainErrorRate" + splitTrainErrorRate);

                        // add graph points here
                        Axis perf_point = getAxis(new Double(splitTrainSize), testPctCorrect, splitTrainPctCorrect);
                        Axis error_point = getAxis(new Double(splitTrainSize), testErrorRate, splitTrainErrorRate);

                        perf_points.add(perf_point);
                        error_points.add(error_point);

                        percent += 5;
                    }
                } else if(indx == 2) {
                    //run with varying k & constant split train
                    for (int i = 1; i <= 10; i++) {

                        IBk IBkClassifier = new IBk();
                        IBkClassifier.setCrossValidate(false);

                        NormalizableDistance distance = null;

                        if(Constant.MANHATTAN_DISTANCE.equalsIgnoreCase(distance_str))
                            distance = new ManhattanDistance();
                        else if(Constant.EUCLIDEAN_DISTANCE.equalsIgnoreCase(distance_str))
                            distance = new EuclideanDistance();

                        IBkClassifier.getNearestNeighbourSearchAlgorithm().setDistanceFunction(distance);
                        IBkClassifier.setKNN(i);
                        IBkClassifier.buildClassifier(train);

                        // evaluate classifier and print some statistics
                        Evaluation trainEval = new Evaluation(train);
                        Evaluation testEval = new Evaluation(test);

                        trainEval.crossValidateModel(IBkClassifier,
                                train, 10, new Random(1));
                        testEval.evaluateModel(IBkClassifier, test);

                        System.out.println("\n Current Iteration is " + i);
                        System.out.println(trainEval.toSummaryString(
                                "\n Train Results\n======\n", false));
                        System.out.println(testEval.toSummaryString(
                                "\nTest Results\n======\n", false));
                        Double testPctCorrect = testEval.pctCorrect();
                        Double trainPctCorrect = trainEval.pctCorrect();

                        Double testErrorRate = testEval.errorRate();
                        Double trainErrorRate = trainEval.errorRate();

                        System.out.println("testPctCorrect" + testPctCorrect);
                        System.out.println("trainPctCorrect"
                                           + trainEval);
                        System.out.println("testErrorRate" + testErrorRate);
                        System.out.println("trainErrorRate" + trainErrorRate);

                        // add graph points here
                        Axis perf_point = getAxis(new Double(i), testPctCorrect, trainPctCorrect);
                        Axis error_point = getAxis(new Double(i), testErrorRate, trainErrorRate);

                        perf_points.add(perf_point);
                        error_points.add(error_point);

                    }
                }

                String variation = null;
                String x_axis_perf = null;
                String x_axis_error = null;
                if(indx == 1) {
                    x_axis_perf = PERF_GRAPH_X_AXIS_1;
                    x_axis_error = ERROR_GRAPH_X_AXIS_1;
                    variation = "splittrain";
                }
                else if(indx == 2) {
                    x_axis_error = PERF_GRAPH_X_AXIS_2;
                    x_axis_error = ERROR_GRAPH_X_AXIS_2;
                    variation = "k";
                }

                perfGraph.setAxisList(perf_points);
                perfGraph.setXAxis(x_axis_perf);
                perfGraph.setY1Axis(PERF_GRAPH_Y1_AXIS);
                perfGraph.setY2Axis(PERF_GRAPH_Y2_AXIS);

                errorGraph.setAxisList(error_points);
                errorGraph.setXAxis(x_axis_error);
                errorGraph.setY1Axis(ERROR_GRAPH_Y1_AXIS);
                errorGraph.setY2Axis(ERROR_GRAPH_Y2_AXIS);

                tmp_perf_file = FileUtil.createDatFile(perfGraph, PERF_GRAPH);
                tmp_error_file = FileUtil.createDatFile(errorGraph,
                        ERR_GRAPH);

                String[] data_file_prefix_arr = training_file_name.split("_");
                String data_file_prefix = data_file_prefix_arr[0];
                String FS = "_";

                StringBuilder sb = new StringBuilder().append(output_dir).append("/").append(PERF_GRAPH).append(FS)
                                                      .append(data_file_prefix).append(FS).append(variation)
                                                      .append(FILE_FORMAT);
                String perf_file_name = sb.toString();

                sb = new StringBuilder().append(output_dir).append("/").append(ERR_GRAPH).append(FS)
                                        .append(data_file_prefix).append(FS).append(variation).append(FILE_FORMAT);

                String error_file_name = sb.toString();

                perf_file = new File(perf_file_name);
                error_file = new File(error_file_name);

                FileUtil.copyFile(tmp_perf_file, perf_file);
                FileUtil.copyFile(tmp_error_file, error_file);

                //close all the handles here
                if(testFileIn != null)
                    testFileIn.close();

                if(trainingFileIn != null)
                    trainingFileIn.close();

                if (tmp_perf_file != null) {
                    tmp_perf_file.delete();
                }

                if (tmp_error_file != null) {
                    tmp_error_file.delete();
                }
            }
        }

    }

    private Axis getAxis(Double x, Double y1, Double y2) {
        Axis axis = new Axis();

        axis.setX(x);
        axis.setY1(y1);
        axis.setY2(y2);

        return axis;
    }
}
