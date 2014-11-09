package com.mstest.datamining.app;

import com.mstest.datamining.model.Algorithm;
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
 * Created by bdamodaran on 9/20/14.
 */
public class DataAnalyzerApp {

    private static final String CONFIG = "context.xml";
    private static final String BEAN_NAME = "jobDriver";

    public static void main(String[] argv) {
        System.out.println("Executing the program");

        List<Algorithm> jobList = new ArrayList<Algorithm>();
        Map<String, Object> params_map = new HashMap<String, Object>();
        Options options = new Options();

        options.addOption(AppCommandOptions.J48, false, Algorithm.j48.getName());
        options.addOption(AppCommandOptions.OUTPUT_DIR, true, "output directory");
        options.addOption(AppCommandOptions.CONFIGURE, true, "configure algorithms");
        options.addOption(AppCommandOptions.MULTILAYER_PERCEPTRON, false, Algorithm.multilayerperceptron.getName());
        options.addOption(AppCommandOptions.KNN, false, Algorithm.knn.getName());
        options.addOption(AppCommandOptions.ADABOOST, false, Algorithm.adaboost.getName());
        options.addOption(AppCommandOptions.SVM, false, Algorithm.svm.getName());
        options.addOption(AppCommandOptions.ASSN3, false, Algorithm.assn3.getName());
        options.addOption(AppCommandOptions.SEED, false, Algorithm.seed.getName());

        try {

            CommandLineParser parser = new PosixParser();
            CommandLine cmd = parser.parse(options, argv);

            //For now we are supporting only one job
            if (cmd.hasOption(AppCommandOptions.J48)) {
                System.out.println("Job added "+Algorithm.j48.getName());
                jobList.add(Algorithm.j48);
            }

            if (cmd.hasOption(AppCommandOptions.MULTILAYER_PERCEPTRON)) {
                System.out.println("Job added "+Algorithm.multilayerperceptron.getName());
                jobList.add(Algorithm.multilayerperceptron);
            }

            if (cmd.hasOption(AppCommandOptions.KNN)) {
                System.out.println("Job added "+Algorithm.knn.getName());
                jobList.add(Algorithm.knn);
            }

            if (cmd.hasOption(AppCommandOptions.ADABOOST)) {
                System.out.println("Job added "+Algorithm.adaboost.getName());
                jobList.add(Algorithm.adaboost);
            }

            if (cmd.hasOption(AppCommandOptions.SVM)) {
                System.out.println("Job added "+Algorithm.svm.getName());
                jobList.add(Algorithm.svm);
            }

            if (cmd.hasOption(AppCommandOptions.ASSN3)) {
                System.out.println("Job added "+Algorithm.assn3.getName());
                jobList.add(Algorithm.assn3);
            }

            if (cmd.hasOption(AppCommandOptions.SEED)) {
                System.out.println("Job added "+Algorithm.seed.getName());
                jobList.add(Algorithm.seed);
            }

            String output_dir = cmd.getOptionValue(AppCommandOptions.OUTPUT_DIR);
            if(output_dir != null) {
                params_map.put(AppCommandOptions.OUTPUT_DIR, output_dir);
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
