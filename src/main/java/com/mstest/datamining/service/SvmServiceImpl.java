package com.mstest.datamining.service;

import com.mstest.datamining.app.AppCommandOptions;
import com.mstest.datamining.model.Algorithm;
import com.mstest.datamining.model.DataConfig;
import com.mstest.datamining.utils.FileUtil;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.mstest.datamining.utils.CommonUtil.fillConfigs;

/**
 * Created by bdamodaran on 9/25/14.
 */
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

    private static final String TMP_FILE_PATH = "/tmp/datamining-test/svm";

    @Override
    public void run(Map<String, Object> params_map) throws Exception {
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

        return;
    }
}
