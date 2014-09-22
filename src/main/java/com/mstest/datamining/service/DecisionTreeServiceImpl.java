package com.mstest.datamining.service;

import com.mstest.datamining.app.AppCommandOptions;
import com.mstest.datamining.model.Axis;
import com.mstest.datamining.model.Config;
import com.mstest.datamining.model.DataFile;
import com.mstest.datamining.model.Graph;
import com.mstest.datamining.utils.FileUtil;

import static com.mstest.datamining.utils.CommonUtil.emptyIfNull;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
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

    private static final String PROPERTIES = "analyzer.properties";


    public void run(Map<String, Object> params_map) throws Exception {
        String output_dir = (String) params_map.get(AppCommandOptions.OUTPUT_DIR);

        File perf_file = null;
        File error_file = null;

        File tmp_perf_file = null;
        File tmp_error_file = null;

        InputStream testFileIn = null;
        InputStream trainingFileIn = null;

        System.out.println("Executing decision tree algorithm");

        try {
            //so the idea is run the same set for two different data sets & for two different minnumObj & confidencefactor
            List<DataFile> dataFiles = new ArrayList<DataFile>();
            List<Config> configs = new ArrayList<Config>();

            fillConfigs(dataFiles, configs);

            for (DataFile dataFile : emptyIfNull(dataFiles)) {
                String training_file_name = dataFile.getTrainingFile();
                String test_file_name = dataFile.getTestFile();

                System.out.println("Running for Training File: " + training_file_name + " Test File: " + test_file_name);

                for (Config config : emptyIfNull(configs)) {

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


                    Integer minNumObj = config.getMinNumObj();
                    Float confidenceFactor = config.getConfidenceFactor();

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

                    StringBuilder sb = new StringBuilder().append(output_dir).append("/").append(PERF_GRAPH).append(FS).append(data_file_prefix).append(FS).append(minNumObj).append(FS).append(confidenceFactor).append(FILE_FORMAT);
                    String perf_file_name = sb.toString();

                    sb = new StringBuilder().append(output_dir).append("/").append(ERR_GRAPH).append(FS).append(data_file_prefix).append(FS).append(minNumObj).append(FS).append(confidenceFactor).append(FILE_FORMAT);
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
            }
        } catch (IOException ie) {
            System.out.println("IO Exception");
            ie.printStackTrace();
        }
    }

    private void fillConfigs(List<DataFile> dataFiles, List<Config> configs) {
        //Configuration config = new PropertiesConfiguration("usergui.properties");
        try {
            Configuration config = new PropertiesConfiguration(PROPERTIES);

            //data.file
            //minnumobj.confidencefactor
            List<Object> property_configs = config.getList("minnumobj.confidencefactor");
            List<Object> property_datafiles = config.getList("data.file");

            //Format => TrainingFile:TestFile,...
            for (Object property_datafile : emptyIfNull(property_datafiles)) {
                String tmpFiles = (String) property_datafile;
                String[] arr = tmpFiles.split(":");

                if (arr != null && arr.length > 0) {
                    DataFile dataFile = new DataFile();
                    dataFile.setTrainingFile(arr[0]);
                    dataFile.setTestFile(arr[1]);

                    dataFiles.add(dataFile);
                }
            }

            //Format => minnumobj:confidencefactor,...

            for (Object property_config : emptyIfNull(property_configs)) {
                String tmpConfigs = (String) property_config;
                String[] arr = tmpConfigs.split(":");

                if (arr != null && arr.length > 0) {
                    Config tmpConfig = new Config();
                    tmpConfig.setMinNumObj(Integer.valueOf(arr[0]));
                    tmpConfig.setConfidenceFactor(Float.valueOf(arr[1]));

                    configs.add(tmpConfig);
                }
            }

        } catch (ConfigurationException ce) {
            //TODO can we handle this better?
            ce.printStackTrace();
        }

    }
}
