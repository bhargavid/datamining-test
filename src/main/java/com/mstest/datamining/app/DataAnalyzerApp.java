package com.mstest.datamining.app;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.PosixParser;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by bloganathan on 9/20/14.
 */
public class DataAnalyzerApp {

    private static final String CONFIG = "context.xml";
    private static final String BEAN_NAME = "jobDriver";

    public static void main(String[] argv) {

        List<Job> jobList = new ArrayList<Job>();
        Options options = new Options();

        options.addOption(AppCommandOptions.DECISION_TREE, false, "decision trees with pruning");

        try {

            CommandLineParser parser = new PosixParser();
            CommandLine cmd = parser.parse(options, argv);

            if (cmd.hasOption(AppCommandOptions.DECISION_TREE)) {
                System.out.println("Job added "+Job.decisiontrees.toString());
                jobList.add(Job.decisiontrees);
            }

            ApplicationContext context = new ClassPathXmlApplicationContext(CONFIG);
            JobDriver driver = (JobDriver) context.getBean(BEAN_NAME);

            driver.processJob(jobList);


        } catch(Exception e) {
            e.printStackTrace();
        }
    }
}
