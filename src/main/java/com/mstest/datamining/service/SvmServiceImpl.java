/*
package com.mstest.datamining.service;

import com.mstest.datamining.app.AppCommandOptions;
import com.mstest.datamining.model.*;
import com.mstest.datamining.utils.FileUtil;
import weka.classifiers.Evaluation;
import weka.classifiers.functions.LibSVM;
import weka.classifiers.functions.SMO;
import weka.classifiers.functions.supportVector.PolyKernel;
import weka.classifiers.functions.supportVector.RBFKernel;
import weka.core.Instances;
import weka.core.SelectedTag;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static com.mstest.datamining.utils.CommonUtil.emptyIfNull;
import static com.mstest.datamining.utils.CommonUtil.fillConfigs;

*/
/**
 * Created by bdamodaran on 9/25/14.
 *//*
public class SvmServiceImpl implements SvmService{
    private static final String PERF_GRAPH_X_AXIS = "TRAINING_TIME";
    private static final String PERF_GRAPH_Y1_AXIS = "PERFORMANCE_TEST_DATA";
    private static final String PERF_GRAPH_Y2_AXIS = "PERFORAMANCE_TRAINING_DATA";

    private static final String ERROR_GRAPH_X_AXIS = "TRAINING_TIME";
    private static final String ERROR_GRAPH_Y1_AXIS = "ERROR_TEST_DATA";
    private static final String ERROR_GRAPH_Y2_AXIS = "ERROR_TRAINING_DATA";

    private static final String PERF_GRAPH = "PERF_GRAPH";
    private static final String ERR_GRAPH = "ERR_GRAPH";

    private static final String FILE_FORMAT = ".dat";

    private static final String TMP_FILE_PATH = "/tmp/datamining-test/"+Algorithm.svm.getName();

    @Override
    public void run(Map<String, Object> params_map) throws Exception {
        System.out.println("Executing SVM");

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
        fillConfigs(dataConfigs, Algorithm.svm);

        InputStream testFileIn = null;
        InputStream trainingFileIn = null;

        for(DataConfig dataConfig: dataConfigs) {
            DataFile dataFile = dataConfig.getDataFile();
            List<Config> configs = dataConfig.getConfigs();

            if (dataFile == null || configs == null || configs.isEmpty())
                continue;

            String training_file_name = dataFile.getTrainingFile();
            String test_file_name = dataFile.getTestFile();

            System.out
                    .println("Running for Training File: " + training_file_name + " Test File: " + test_file_name);

            for(Config config: emptyIfNull(configs)) {

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

                Double gamma = null;
                Double cost = null;
                Double exp = null;
                String library = null;
                String function_type = null;

                for (Label label : emptyIfNull(config.getLabels())) {
                    if (Constant.GAMMA.equalsIgnoreCase(label.getName()))
                        gamma = (Double) label.getValue();
                    if (Constant.COST.equalsIgnoreCase(label.getName()))
                        cost = (Double) label.getValue();
                    if (Constant.SVM_LIBRARY.equalsIgnoreCase(label.getName()))
                        library = String.valueOf(label.getValue());
                    if(Constant.EXP.equalsIgnoreCase(label.getName()))
                        exp = (Double) label.getValue();
                    if(Constant.FUNCTION_TYPE.equalsIgnoreCase(label.getName()))
                        function_type = (String) label.getValue();
                }

                System.out.println("Inovking threads for gamma: "+gamma+" cost: "+cost+" exp: "+exp+" library: "+library+" function_type: "+function_type);

                List<Axis> perf_points = new ArrayList<Axis>();
                List<Axis> error_points = new ArrayList<Axis>();

                Integer percent = 5;
                ExecutorService pool = Executors.newFixedThreadPool(10);
                Collection<SvmExecutor> collection = new ArrayList<SvmExecutor>();

                for(int indx = 0; indx < 20; indx++) {
                    SvmExecutor task = new SvmExecutor(gamma, cost, exp, library, function_type, percent, train, test);
                    collection.add(task);
                    percent += 5;
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

                String[] data_file_prefix_arr = training_file_name.split("_");
                String data_file_prefix = data_file_prefix_arr[0];
                String FS = "_";

                StringBuilder sb = new StringBuilder().append(output_dir).append("/").append(PERF_GRAPH).append(FS)
                                                      .append(data_file_prefix).append(FS).append(cost)
                                                      .append(FS).append(function_type).append(
                                FS).append(library).append(FILE_FORMAT);
                String perf_file_name = sb.toString();

                sb = new StringBuilder().append(output_dir).append("/").append(ERR_GRAPH).append(FS)
                                                      .append(data_file_prefix).append(FS).append(cost)
                                                      .append(FS).append(function_type).append(FS).append(library).append(FILE_FORMAT);
                String error_file_name = sb.toString();

                FileUtil.createPlotFile(perfGraph, perf_file_name);
                FileUtil.createPlotFile(errorGraph, error_file_name);

                if(trainingFileIn != null)
                    trainingFileIn.close();

                if(testFileIn != null)
                    testFileIn.close();
            }
        }

        return;
    }
}
*/
