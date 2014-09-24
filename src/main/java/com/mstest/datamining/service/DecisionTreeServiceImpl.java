package com.mstest.datamining.service;

import com.mstest.datamining.app.AppCommandOptions;
import com.mstest.datamining.model.*;
import com.mstest.datamining.utils.FileUtil;

import static com.mstest.datamining.utils.CommonUtil.emptyIfNull;
import static com.mstest.datamining.utils.CommonUtil.fillConfigs;

import com.sun.tools.internal.jxc.apt.Const;
import weka.classifiers.Evaluation;
import weka.classifiers.trees.J48;
import weka.core.Instances;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * Created by bloganathan on 9/20/14.
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

    private static final String TMP_FILE_PATH = "/tmp/datamining-test/";
    private static final String FILE_FORMAT = ".dat";

    private static final String TEST_DATA_FILE = "/bank-full-test_30_pct.arff";
    private static final String TRAINING_DATA_FILE = "/bank-full-training_70_pct_Noise.arff";

    private static final String NEW_LINE = "\n";
    private static final String TAB = "\t";


    public void run(Map<String, Object> params_map) throws Exception {
        if(params_map.containsKey(AppCommandOptions.CONFIGURE)) {
            //configure(params_map);
        } else {
            System.out.println("Executing job decision tree");
            execute(params_map);
        }
    }

    /*private void configure(Map<String, Object> params_map) throws Exception {

        //TODO for now returning
        if(true)
            return;


        String output_dir = (String) params_map.get(AppCommandOptions.OUTPUT_DIR);
        List<DataFile> dataFiles = new ArrayList<DataFile>();
        List<Config> configs = new ArrayList<Config>();

        boolean systemConfig = false;
        //fillConfigs(dataFiles, configs, systemConfig);

        if (output_dir == null)
            output_dir = TMP_FILE_PATH;

        // check if the output directory exists
        File theDir = new File(output_dir);
        if (!theDir.exists()) {
            System.out.println("creating directory: " + output_dir);
            theDir.mkdir();
        }

        String output_file = Algorithm.decistiontree.getName()+"_configure"+FILE_FORMAT;
        output_file = output_dir+"/"+output_file;

        FileWriter fw = new FileWriter(output_file);
        BufferedWriter bw = new BufferedWriter(fw);

        for (DataFile dataFile : emptyIfNull(dataFiles)) {
            String training_file_name = dataFile.getTrainingFile();
            InputStream trainingFileIn;

            System.out
                    .println("Running for Training File: " + training_file_name);
            bw.write("Running configuration for file "+training_file_name+"======\n\n");

            for (Config config : emptyIfNull(configs)) {
                trainingFileIn = getClass().getResourceAsStream("/" + training_file_name);

                BufferedReader reader = new BufferedReader(new InputStreamReader(
                        trainingFileIn));

                Instances train = new Instances(reader);
                train.setClassIndex(train.numAttributes() - 1);

                StringBuilder sb = new StringBuilder();

                reader.close();

                float v = config.getConfidenceFactor();
                float original_v = v;

                int i_len = 9;
                int j_len = 100;

                if(v == (float) 0.1)
                    i_len = 5;

                for (int i = 1; i <= i_len; i++) {
                    for (int j = 2; j <= j_len; j += 1) {
                        J48 j48Classifier = new J48();
                        j48Classifier.setMinNumObj(j);
                        j48Classifier.setConfidenceFactor(v);
                        j48Classifier.buildClassifier(train);
                        Evaluation trainEval = new Evaluation(train);
                        trainEval.crossValidateModel(j48Classifier, train, 10, new Random(1));

                        sb.append(NEW_LINE).append("Current Iteration is ").append(i).append(TAB).append(j).append(NEW_LINE);
                        sb.append(NEW_LINE).append("Confidence Factor: ").append(v).append(TAB).append(" MinNumObj ").append(j).append(NEW_LINE);
                        sb.append(trainEval.toSummaryString("\n Train Results\n=====\n", false));

                        System.out.println("Current confidence factor" + v);
                    }
                    v += original_v;
                }

                bw.write(sb.toString());
                if(trainingFileIn != null)
                    trainingFileIn.close();
            }
        }

        if(bw != null)
            bw.close();

        if(fw != null)
            fw.close();
    }*/

    private void execute(Map<String, Object> params_map) throws Exception {
        String output_dir = (String) params_map.get(AppCommandOptions.OUTPUT_DIR);
        File perf_file = null;
        File error_file = null;

        File tmp_perf_file = null;
        File tmp_error_file = null;

        InputStream testFileIn = null;
        InputStream trainingFileIn = null;

        System.out.println("Executing decision tree algorithm");

        try {
            //so the idea is run the same set for two different data sets & for two different minnumObj &
            // confidencefactor
            List<DataConfig> dataConfigs = new ArrayList<DataConfig>();
            fillConfigs(dataConfigs, Algorithm.decistiontree);

            for (DataConfig dataConfig : emptyIfNull(dataConfigs)) {

                DataFile dataFile = dataConfig.getDataFile();
                if (dataFile == null)
                    continue;

                String training_file_name = dataFile.getTrainingFile();
                String test_file_name = dataFile.getTestFile();

                System.out
                        .println("Running for Training File: " + training_file_name + " Test File: " + test_file_name);

                Integer minNumObj = null;
                Float confidenceFactor = null;
                for (Label label : dataConfig.getConfig().getLabels()) {
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
                Integer percent = new Integer(0);
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

                tmp_perf_file = FileUtil.createDatFile(perfGraph, PERF_GRAPH);
                tmp_error_file = FileUtil.createDatFile(errorGraph,
                        ERR_GRAPH);

                if (output_dir == null)
                    output_dir = TMP_FILE_PATH;

                // check if the output directory exists
                File theDir = new File(output_dir);
                if (!theDir.exists()) {
                    System.out.println("creating directory: " + output_dir);
                    theDir.mkdir();
                }

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

                perf_file = new File(perf_file_name);
                error_file = new File(error_file_name);

                FileUtil.copyFile(tmp_perf_file, perf_file);
                FileUtil.copyFile(tmp_error_file, error_file);

                if (tmp_perf_file != null) {
                    tmp_perf_file.delete();
                }
                if (tmp_error_file != null) {
                    tmp_error_file.delete();
                }

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
