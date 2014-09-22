package com.mstest.datamining.app;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.PosixParser;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by bloganathan on 9/20/14.
 */
public class DataAnalyzerApp {

    private static final String CONFIG = "context.xml";
    private static final String BEAN_NAME = "jobDriver";

    public static void main(String[] argv) {
        System.out.println("Executing the program");

        List<Job> jobList = new ArrayList<Job>();
        Map<String, Object> params_map = new HashMap<String, Object>();
        Options options = new Options();

        options.addOption(AppCommandOptions.DECISION_TREE, false, "decision trees with pruning");
        options.addOption(AppCommandOptions.OUTPUT_DIR, true, "output directory");

        try {

            CommandLineParser parser = new PosixParser();
            CommandLine cmd = parser.parse(options, argv);

            if (cmd.hasOption(AppCommandOptions.DECISION_TREE)) {
                System.out.println("Job added "+Job.decisiontrees.toString());
                jobList.add(Job.decisiontrees);
            }

            String output_dir = cmd.getOptionValue(AppCommandOptions.OUTPUT_DIR);
            if(output_dir != null) {
                params_map.put(AppCommandOptions.OUTPUT_DIR, output_dir);
            }

            String gnuplot_bin = cmd.getOptionValue(AppCommandOptions.GNUPLOT_BIN);
            if(gnuplot_bin != null) {
                params_map.put(AppCommandOptions.GNUPLOT_BIN, gnuplot_bin);
            }

            ApplicationContext context = new ClassPathXmlApplicationContext(CONFIG);
            JobDriver driver = (JobDriver) context.getBean(BEAN_NAME);

            System.out.println("Executing process job");
            driver.processJob(jobList, params_map);

        } catch(Exception e) {
            e.printStackTrace();
        }
    }
}
