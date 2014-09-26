package com.mstest.datamining.service;

import com.mstest.datamining.app.AppCommandOptions;
import com.mstest.datamining.model.*;
import com.mstest.datamining.utils.FileUtil;

import static com.mstest.datamining.utils.CommonUtil.emptyIfNull;
import static com.mstest.datamining.utils.CommonUtil.fillConfigs;

import weka.classifiers.Evaluation;
import weka.classifiers.trees.J48;
import weka.core.Instances;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * Created by bdamodaran on 9/20/14.
 */
public class DecisionTreeServiceImpl implements DecisionTreeService {
    private static final String PERF_GRAPH_X_AXIS = "TRAINING_SIZE";
    private static final String PERF_GRAPH_Y1_AXIS = "PERFORMANCE_TEST_DATA";
    private static final String PERF_GRAPH_Y2_AXIS = "PERFORAMANCE_TRAINING_DATA";

    private static final String ERROR_GRAPH_X_AXIS = "TRAINING_SIZE";
    private static final String ERROR_GRAPH_Y1_AXIS = "ERROR_TEST_DATA";
    private static final String ERROR_GRAPH_Y2_AXIS = "ERROR_TRAINING_DATA";

    private static final String PERF_GRAPH = "PERF_GRAPH";
    private static final String ERR_GRAPH = "ERR_GRAPH";

    private static final String TMP_FILE_PATH = "/tmp/datamining-test/decisiontree";
    private static final String FILE_FORMAT = ".dat";

    public void run(Map<String, Object> params_map) throws Exception {
        System.out.println("Executing job decision tree");
        execute(params_map);
    }


    private void execute(Map<String, Object> params_map) throws Exception {
        String output_dir = (String) params_map.get(AppCommandOptions.OUTPUT_DIR);

        InputStream testFileIn = null;
        InputStream trainingFileIn = null;

        System.out.println("Executing decision tree algorithm");

        try {
            if (output_dir == null)
                output_dir = TMP_FILE_PATH;

            // check if the output directory exists
            File theDir = new File(output_dir);

            if (!FileUtil.createDirs(theDir)) {
                System.out.println("ERROR:: Failed to create output directory. " + output_dir);
                return;
            }

            List<DataConfig> dataConfigs = new ArrayList<DataConfig>();
            fillConfigs(dataConfigs, Algorithm.j48);

            for (DataConfig dataConfig : emptyIfNull(dataConfigs)) {

                DataFile dataFile = dataConfig.getDataFile();
                if (dataFile == null || dataConfig.getConfigs() == null || dataConfig.getConfigs().isEmpty())
                    continue;

                String training_file_name = dataFile.getTrainingFile();
                String test_file_name = dataFile.getTestFile();

                System.out
                        .println("Running for Training File: " + training_file_name + " Test File: " + test_file_name);

                Integer minNumObj = null;
                Float confidenceFactor = null;

                Config config = dataConfig.getConfigs().get(0);
                for (Label label : config.getLabels()) {
                    if (Constant.MIN_NUM_OBJECT.equals(label.getName()))
                        minNumObj = (Integer) label.getValue();
                    if (Constant.CONFIDENCE_FACTOR.equals(label.getName()))
                        confidenceFactor = (Float) label.getValue();
                }

                testFileIn = getClass().getResourceAsStream("/" + test_file_name);
                trainingFileIn = getClass().getResourceAsStream("/" + training_file_name);

                BufferedReader reader = new BufferedReader(new InputStreamReader(
                        trainingFileIn));

                Instances train = new Instances(reader);

                BufferedReader testReader = new BufferedReader(
                        new InputStreamReader(testFileIn));

                Graph perfGraph = new Graph();
                Graph errorGraph = new Graph();

                List<Axis> perf_points = new ArrayList<Axis>();
                List<Axis> error_points = new ArrayList<Axis>();

                Instances test = new Instances(testReader);
                train.setClassIndex(train.numAttributes() - 1);
                test.setClassIndex(test.numAttributes() - 1);
                reader.close();
                testReader.close();
                Integer percent = 5;
                System.out.println("Training size length: " + train.numInstances());
                for (int i = 0; i < 20; i++) {
                    int splitTrainSize = (int) Math.round(train.numInstances()
                            * percent / 100);
                    Instances splitTrain = new Instances(train, 0, splitTrainSize);
                    splitTrain.setClassIndex(splitTrain.numAttributes() - 1);
                    J48 j48Classifier = new J48();
                    j48Classifier.setConfidenceFactor(confidenceFactor);
                    j48Classifier.setMinNumObj(minNumObj);
                    j48Classifier.buildClassifier(splitTrain);

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
                    // error chart
                    testErrorRate = testEval.errorRate();
                    splitTrainErrorRate = splitTrainEval.errorRate();
                    System.out.println("testPctCorrect" + testPctCorrect);
                    System.out.println("splitTrainPctCorrect"
                            + splitTrainPctCorrect);
                    System.out.println("testErrorRate" + testErrorRate);
                    System.out.println("splitTrainErrorRate" + splitTrainErrorRate);

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

                String[] data_file_prefix_arr = training_file_name.split("_");
                String data_file_prefix = data_file_prefix_arr[0];
                String FS = "_";

                StringBuilder sb = new StringBuilder().append(output_dir).append("/").append(PERF_GRAPH).append(FS)
                        .append(data_file_prefix).append(FS).append(minNumObj)
                        .append(FS).append(confidenceFactor).append(FILE_FORMAT);
                String perf_file_name = sb.toString();

                sb = new StringBuilder().append(output_dir).append("/").append(ERR_GRAPH).append(FS)
                        .append(data_file_prefix).append(FS).append(minNumObj).append(FS)
                        .append(confidenceFactor).append(FILE_FORMAT);
                String error_file_name = sb.toString();

                FileUtil.createPlotFile(perfGraph, perf_file_name);
                FileUtil.createPlotFile(errorGraph, error_file_name);

                if (trainingFileIn != null) {
                    trainingFileIn.close();
                }
                if (testFileIn != null) {
                    testFileIn.close();
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
