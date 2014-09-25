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
 * Created by bloganathan on 9/20/14.
 */
public class DataAnalyzerApp {

    private static final String CONFIG = "context.xml";
    private static final String BEAN_NAME = "jobDriver";

    public static void main(String[] argv) {
        System.out.println("Executing the program");

        List<Algorithm> jobList = new ArrayList<Algorithm>();
        Map<String, Object> params_map = new HashMap<String, Object>();
        Options options = new Options();

        options.addOption(AppCommandOptions.DECISION_TREE, false, "decision trees with pruning");
        options.addOption(AppCommandOptions.OUTPUT_DIR, true, "output directory");
        options.addOption(AppCommandOptions.CONFIGURE, true, "configure algorithms");
        options.addOption(AppCommandOptions.MULTILAYER_PERCEPTRON, false, "multi layer perceptron");
        options.addOption(AppCommandOptions.KNN, false, Algorithm.knn.getName());
        options.addOption(AppCommandOptions.ADABOOST, false, Algorithm.adaboost.getName());

        try {

            CommandLineParser parser = new PosixParser();
            CommandLine cmd = parser.parse(options, argv);

            //For now we are supporting only one job
            if (cmd.hasOption(AppCommandOptions.DECISION_TREE)) {
                System.out.println("Job added "+Algorithm.decistiontree.getName());
                jobList.add(Algorithm.decistiontree);
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

            String output_dir = cmd.getOptionValue(AppCommandOptions.OUTPUT_DIR);
            if(output_dir != null) {
                params_map.put(AppCommandOptions.OUTPUT_DIR, output_dir);
            }

/*            if(cmd.hasOption(AppCommandOptions.CONFIGURE)) {
                String commands_str = cmd.getOptionValue(AppCommandOptions.CONFIGURE);

                if(commands_str != null) {
                    //expecting a comma separated value
                    String[] commands = commands_str.split(",");
                    for(String command: commands) {
                        for(Algorithm algorithm: Algorithm.values()) {
                            if(command.equals(algorithm.getName())) {
                                jobList.add(algorithm);
                            }
                        }
                    }
                    params_map.put(AppCommandOptions.CONFIGURE, null);
                } else {
                    for(Algorithm algorithm: Algorithm.values()) {
                        jobList.add(algorithm);
                    }
                }
            }*/

            ApplicationContext context = new ClassPathXmlApplicationContext(CONFIG);
            JobDriver driver = (JobDriver) context.getBean(BEAN_NAME);

            System.out.println("Executing process job");
            driver.processJob(jobList, params_map);

        } catch(Exception e) {
            e.printStackTrace();
        }
    }
}
