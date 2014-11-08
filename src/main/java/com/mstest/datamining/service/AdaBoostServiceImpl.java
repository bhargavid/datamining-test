package com.mstest.datamining.service;

import com.mstest.datamining.app.AppCommandOptions;
import com.mstest.datamining.model.*;
import com.mstest.datamining.utils.FileUtil;
import weka.core.Instances;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static com.mstest.datamining.utils.CommonUtil.fillConfigs;

/**
 * Created by bdamodaran on 9/24/14.
 */
public class AdaBoostServiceImpl implements AdaBoostService {
    private static final String PERF_GRAPH_X_AXIS = "ITERATION";
    private static final String PERF_GRAPH_Y1_AXIS = "PERFORMANCE_TEST_DATA";
    private static final String PERF_GRAPH_Y2_AXIS = "PERFORAMANCE_TRAINING_DATA";

    private static final String ERROR_GRAPH_X_AXIS = "ITERATION";
    private static final String ERROR_GRAPH_Y1_AXIS = "ERROR_TEST_DATA";
    private static final String ERROR_GRAPH_Y2_AXIS = "ERROR_TRAINING_DATA";

    private static final String PERF_GRAPH = "PERF_GRAPH";
    private static final String ERR_GRAPH = "ERR_GRAPH";

    private static final String FILE_FORMAT = ".dat";

    private static final String TMP_FILE_PATH = "/tmp/datamining-test/adaboost";

    @Override
    public void run(Map<String, Object> params_map) throws Exception {
        System.out.println("Executing adaboost algorithm");

        InputStream testFileIn = null;
        InputStream trainingFileIn = null;

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
        fillConfigs(dataConfigs, Algorithm.adaboost);

        for(DataConfig dataConfig: dataConfigs) {
            List<Axis> perf_points = new ArrayList<Axis>();
            List<Axis> error_points = new ArrayList<Axis>();

            DataFile dataFile = dataConfig.getDataFile();
            List<Config> configs = dataConfig.getConfigs();

            if (dataFile == null || configs == null || configs.isEmpty())
                continue;

            testFileIn = getClass().getResourceAsStream("/" + dataConfig.getDataFile().getTestFile());
            trainingFileIn = getClass().getResourceAsStream("/" + dataConfig.getDataFile().getTrainingFile());


            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    trainingFileIn));

            BufferedReader testReader = new BufferedReader(
                    new InputStreamReader(testFileIn));

            Instances train = new Instances(reader);
            Instances test = new Instances(testReader);
            train.setClassIndex(train.numAttributes() - 1);
            test.setClassIndex(test.numAttributes() - 1);
            reader.close();
            testReader.close();

            Integer minNumObject = null;
            Float confidenceFactor = null;

            Config config = configs.get(0);
            for(Label label: config.getLabels()) {
                if(Constant.MIN_NUM_OBJECT.equalsIgnoreCase(label.getName()))
                    minNumObject = (Integer) label.getValue();
                if(Constant.CONFIDENCE_FACTOR.equalsIgnoreCase(label.getName()))
                    confidenceFactor = (Float) label.getValue();
            }

            int iteration = 250;
            int iteration_increments = 50;

            ExecutorService pool = Executors.newFixedThreadPool(10);
            Collection<AdaBoostExecutor> tasks = new ArrayList<AdaBoostExecutor>();

            //TODO may be need to produce different instances of train & test every time?
            for(int i = 0; i < 5; i++) {
                System.out.println("\n Current Iteration is i:" + i);
                AdaBoostExecutor task = new AdaBoostExecutor(minNumObject, confidenceFactor, iteration, train, test);
                tasks.add(task);
                iteration += iteration_increments;
            }

            List<Future<Plot>> futures =  pool.invokeAll(tasks);

            for(Future<Plot> future: futures) {
                Plot plot = future.get();

                if(plot != null) {
                    perf_points.add(plot.getPerfPoint());
                    error_points.add(plot.getErrorPoint());
                }
            }

            pool.shutdown();

            Graph perfGraph = new Graph();
            Graph errorGraph = new Graph();

            perfGraph.setAxisList(perf_points);
            perfGraph.setXAxis(PERF_GRAPH_X_AXIS);
            perfGraph.setY1Axis(PERF_GRAPH_Y1_AXIS);
            perfGraph.setY2Axis(PERF_GRAPH_Y2_AXIS);

            errorGraph.setAxisList(error_points);
            errorGraph.setXAxis(ERROR_GRAPH_X_AXIS);
            errorGraph.setY1Axis(ERROR_GRAPH_Y1_AXIS);
            errorGraph.setY2Axis(ERROR_GRAPH_Y2_AXIS);

            String[] data_file_prefix_arr = dataConfig.getDataFile().getTrainingFile().split("_");
            String data_file_prefix = data_file_prefix_arr[0];
            String FS = "_";

            StringBuilder sb = new StringBuilder().append(output_dir).append("/").append(PERF_GRAPH).append(FS)
                                                  .append(data_file_prefix).append(FILE_FORMAT);
            String perf_file_name = sb.toString();

            sb = new StringBuilder().append(output_dir).append("/").append(ERR_GRAPH).append(FS)
                                    .append(data_file_prefix).append(FILE_FORMAT);
            String error_file_name = sb.toString();

            FileUtil.createPlotFile(perfGraph, perf_file_name);
            FileUtil.createPlotFile(errorGraph, error_file_name);

            if(testFileIn != null)
                testFileIn.close();

            if(trainingFileIn != null)
                trainingFileIn.close();
        }
    }
}
