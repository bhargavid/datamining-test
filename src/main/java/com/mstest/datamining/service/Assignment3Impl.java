package com.mstest.datamining.service;

import com.mstest.datamining.utils.FileUtil;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import weka.core.Instances;
import weka.filters.Filter;

import java.io.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;


public class Assignment3Impl implements Assignment3 {
    private static final String output_dir = "/tmp/clustering";
    public void execute() throws Exception {
        try {

            File theDir = new File(output_dir);
            if (!FileUtil.createDirs(theDir)) {
                System.out.println("ERROR:: Failed to create output directory. " + output_dir);
                return;
            }

            StringBuilder sb = new StringBuilder();

            XYSeriesCollection xySeriesCollection_letter_k = new XYSeriesCollection();
            XYSeriesCollection xySeriesCollection_letter_seed = new XYSeriesCollection();
            XYSeriesCollection xySeriesCollection_letter_k_pct = new XYSeriesCollection();
            XYSeriesCollection xySeriesCollection_letter_seed_pct = new XYSeriesCollection();
            XYSeries xySeries_letter_k = new XYSeries("K means Clustering - Letter dataset - K variation vs Avg Silhouette Coeff");
            XYSeries xySeries_letter_seed = new XYSeries("K means Clustering - Letter dataset - Starting instance variation vs Avg Silhouette Coeff");
            XYSeries xySeries_letter_k_pct = new XYSeries("K means Clustering - Letter dataset - K variation vs Cluster to Class Percent Correct");
            XYSeries xySeries_letter_seed_pct = new XYSeries("K means Clustering - Letter dataset - Starting instance variation vs Cluster to Class Percent Correct");

            ExecutorService pool = Executors.newFixedThreadPool(30);
            Collection<ClusterRunnerExecutor> collection = new ArrayList<ClusterRunnerExecutor>();

            for (int i = 2; i < 27; i++) {
                String test_file_name = "/letter_training_70pct_1.arff";

                InputStream testFileIn = getClass().getResourceAsStream(test_file_name);

                BufferedReader l_training_reader = new BufferedReader(new InputStreamReader(
                        testFileIn));

                Instances l_train = new Instances(l_training_reader);
                l_train.setClassIndex(l_train.numAttributes() - 1);

                weka.filters.unsupervised.attribute.Remove l_filter = new weka.filters.unsupervised.attribute.Remove();
                l_filter.setAttributeIndices("" + (l_train.classIndex() + 1));
                l_filter.setInputFormat(l_train);
                Instances l_trainDataClusterer = Filter.useFilter(l_train, l_filter);


                l_training_reader.close();
                testFileIn.close();

                ClusterRunnerExecutor executor = new ClusterRunnerExecutor(i, l_trainDataClusterer, l_train);
                collection.add(executor);
            }

            List<Future<ClusterData>> futures = pool.invokeAll(collection);
            for (Future<ClusterData> future : futures) {
                ClusterData data = future.get();
                if (data != null) {
                    System.out.println("Got data for clusters: " + data.getNoOfCluster());
                    xySeries_letter_k.add((double) data.getNoOfCluster(), data.getAvgSilCoeff());
                    xySeries_letter_k_pct.add((double) data.getNoOfCluster(), data.getPctCorrect());

                    xySeriesCollection_letter_k.addSeries(xySeries_letter_k);
                    xySeriesCollection_letter_k_pct.addSeries(xySeries_letter_k_pct);
                } else {
                    System.out.println("Failed");
                }
            }
            pool.shutdown();

            System.out.println("Threads completed");

            chart(xySeriesCollection_letter_k, "K_means_Clustering_Letter_dataset_Avg_Silhouette_Coeff", "K variation", "Average Silhouette Coefficient");
            chart(xySeriesCollection_letter_k_pct, "K_means_Clustering_Letter_dataset_Cluster_to_Class_Prediction_Correct_Pct", "K variation", "Cluster to Class Prediction Correct Percent");
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private static void chart(XYSeriesCollection xySeriesCollection, String algo, String xLabel, String yLabel) throws Exception {

        JFreeChart xyLineChart = ChartFactory.createXYLineChart(algo, xLabel, yLabel, xySeriesCollection, PlotOrientation.VERTICAL, true, true, false);

		/* Step -3 : Write line chart to a file */
        int width = 640; /* Width of the image */
        int height = 480; /* Height of the image */
        String fileNamePrefix = output_dir + "/" + algo;
        File XYlineChart = new File(fileNamePrefix + ".png");
        ChartUtilities.saveChartAsPNG(XYlineChart, xyLineChart, width, height);
    }


}