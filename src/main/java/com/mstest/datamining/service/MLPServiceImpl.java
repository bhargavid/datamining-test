package com.mstest.datamining.service;

import static com.mstest.datamining.utils.CommonUtil.emptyIfNull;
import static com.mstest.datamining.utils.CommonUtil.fillConfigs;

import com.mstest.datamining.app.AppCommandOptions;
import com.mstest.datamining.model.*;

import com.mstest.datamining.utils.FileUtil;
import weka.core.Instances;

import java.io.*;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Created by bdamodaran on 9/22/14.
 */
public class MLPServiceImpl implements MLPService {
    private static final String PERF_GRAPH_X_AXIS = "TRAINING_TIME";
    private static final String PERF_GRAPH_Y1_AXIS = "PERFORMANCE_TEST_DATA";
    private static final String PERF_GRAPH_Y2_AXIS = "PERFORAMANCE_TRAINING_DATA";

    private static final String ERROR_GRAPH_X_AXIS = "TRAINING_TIME";
    private static final String ERROR_GRAPH_Y1_AXIS = "ERROR_TEST_DATA";
    private static final String ERROR_GRAPH_Y2_AXIS = "ERROR_TRAINING_DATA";

    private static final String PERF_GRAPH = "PERF_GRAPH";
    private static final String ERR_GRAPH = "ERR_GRAPH";

    private static final String FILE_FORMAT = ".dat";

    private static final String TMP_FILE_PATH = "/tmp/datamining-test/multilayerperceptron";

    @Override
    public void run(Map<String, Object> params_map) throws Exception {
        System.out.println("Executing job mlp");
        execute(params_map);
    }

    private void execute(Map<String, Object> params_map) throws Exception {
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
        fillConfigs(dataConfigs, Algorithm.multilayerperceptron);

        InputStream testFileIn = null;
        InputStream trainingFileIn = null;

        for (DataConfig dataConfig : emptyIfNull(dataConfigs)) {
            DataFile dataFile = dataConfig.getDataFile();
            List<Config> configs = dataConfig.getConfigs();

            if (dataFile == null || configs == null || configs.isEmpty())
                continue;

            List<Axis> perf_points = new ArrayList<Axis>();
            List<Axis> error_points = new ArrayList<Axis>();


            testFileIn = getClass().getResourceAsStream("/" + dataFile.getTestFile());
            trainingFileIn = getClass().getResourceAsStream("/" + dataFile.getTrainingFile());


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

            int trainingtime_increments = 25;
            int trainingtime = 0;

            String meanHiddenLayerStr = null;
            Double momentum = null;
            Double learningRate = null;

            Config config = configs.get(0);

            for (Label label : config.getLabels()) {
                if (Constant.HIDDENLAYER.equalsIgnoreCase(label.getName()))
                    meanHiddenLayerStr = (String) label.getValue();
                if (Constant.MOMENTUM.equalsIgnoreCase(label.getName()))
                    momentum = (Double) label.getValue();
                if (Constant.LEARNING_RATE.equalsIgnoreCase(label.getName()))
                    learningRate = (Double) label.getValue();
            }

            ExecutorService pool = Executors.newFixedThreadPool(10);
            Collection<MLPExecutor> collection = new ArrayList<MLPExecutor>();

            for (int i = 0; i <= 20; i++) {
                MLPExecutor mlpExecutor = new MLPExecutor(meanHiddenLayerStr, momentum, learningRate, trainingtime, train, test);
                collection.add(mlpExecutor);

                trainingtime += trainingtime_increments;
            }

            List<Future<Plot>> futures = pool.invokeAll(collection);

            for (Future<Plot> future : futures) {
                Plot plot = future.get();

                if (plot != null) {
                    perf_points.add(plot.getPerfPoint());
                    error_points.add(plot.getErrorPoint());
                } else {
                    System.out.println("Failed");
                }
            }

            pool.shutdown();

            System.out.println("Completing threads");

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

            if (testFileIn != null)
                testFileIn.close();

            if (trainingFileIn != null)
                trainingFileIn.close();
        }

        return;
    }
}
