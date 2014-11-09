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


public class Assignment3ReducedImpl implements Assignment3Reduced {

    public void execute(String output_dir) throws Exception {
        try {

            File theDir = new File(output_dir);
            if (!FileUtil.createDirs(theDir)) {
                System.out.println("ERROR:: Failed to create output directory. " + output_dir);
                return;
            }

            for(int option  = 1; option <= 3; option++) {
                switch (option) {
                    case 1: {
                        XYSeriesCollection xySeriesCollection_letter_k = new XYSeriesCollection();
                        XYSeriesCollection xySeriesCollection_letter_k_pct = new XYSeriesCollection();
                        XYSeries xySeries_letter_k = new XYSeries("K means Clustering - Letter dataset - K variation vs Avg Silhouette Coeff");
                        XYSeries xySeries_letter_k_pct = new XYSeries("K means Clustering - Letter dataset - K variation vs Cluster to Class Percent Correct");

                        System.out.println("Option 1");
                        ExecutorService pool = Executors.newFixedThreadPool(10);
                        Collection<ClusterRunnerExecutor> collection = new ArrayList<ClusterRunnerExecutor>();

                        for (int i = 2; i < 13; i++) {
                            String training_file_name = "/letter_training_70pct_1.arff";

                            InputStream trainingFileStream = getClass().getResourceAsStream(training_file_name);

                            BufferedReader l_training_buffer = new BufferedReader(new InputStreamReader(
                                    trainingFileStream));

                            Instances l_train = new Instances(l_training_buffer);
                            l_train.setClassIndex(l_train.numAttributes() - 1);

                            weka.filters.unsupervised.attribute.Remove l_filter = new weka.filters.unsupervised.attribute.Remove();
                            l_filter.setAttributeIndices("" + (l_train.classIndex() + 1));
                            l_filter.setInputFormat(l_train);
                            Instances l_trainDataClusterer = Filter.useFilter(l_train, l_filter);


                            l_training_buffer.close();
                            trainingFileStream.close();

                            ClusterRunnerExecutor executor = new ClusterRunnerExecutor(i, l_trainDataClusterer, l_train, output_dir);
                            collection.add(executor);
                        }

                        List<Future<ClusterData>> futures = pool.invokeAll(collection);
                        for (Future<ClusterData> future : futures) {
                            ClusterData data = future.get();
                            if (data != null) {
                                System.out.println("Got data for clusters: " + data.getNoOfCluster());
                                xySeries_letter_k.add((double) data.getNoOfCluster(), data.getX());
                                xySeries_letter_k_pct.add((double) data.getNoOfCluster(), data.getPctCorrect());
                            } else {
                                System.out.println("Failed");
                            }
                        }
                        pool.shutdown();

                        System.out.println("Threads completed");

                        xySeriesCollection_letter_k.addSeries(xySeries_letter_k);
                        xySeriesCollection_letter_k_pct.addSeries(xySeries_letter_k_pct);

                        chart(xySeriesCollection_letter_k, "K_means_Clustering_Letter_dataset_Avg_Silhouette_Coeff", "K variation", "Average Silhouette Coefficient", output_dir);
                        chart(xySeriesCollection_letter_k_pct, "K_means_Clustering_Letter_dataset_Cluster_to_Class_Prediction_Correct_Pct", "K variation", "Cluster to Class Prediction Correct Percent", output_dir);
                    }
                    break;

                    case 2: {

                        XYSeriesCollection xySeriesCollection_letter_seed = new XYSeriesCollection();
                        XYSeriesCollection xySeriesCollection_letter_seed_pct = new XYSeriesCollection();
                        XYSeries xySeries_letter_seed = new XYSeries("K means Clustering - Letter dataset - Starting instance variation vs Avg Silhouette Coeff");
                        XYSeries xySeries_letter_seed_pct = new XYSeries("K means Clustering - Letter dataset - Starting instance variation vs Cluster to Class Percent Correct");


                        System.out.println("Option 2");
                        ExecutorService pool = Executors.newFixedThreadPool(10);
                        Collection<SimpleClusterRunner> collection = new ArrayList<SimpleClusterRunner>();

                        for (int i = 1; i < 10; i++) {
                            String training_file_name = "/letter_training_70pct_1.arff";

                            InputStream trainingFileStream = getClass().getResourceAsStream(training_file_name);

                            BufferedReader l_training_buffer = new BufferedReader(new InputStreamReader(
                                    trainingFileStream));

                            Instances l_train = new Instances(l_training_buffer);
                            l_train.setClassIndex(l_train.numAttributes() - 1);

                            weka.filters.unsupervised.attribute.Remove l_filter = new weka.filters.unsupervised.attribute.Remove();
                            l_filter.setAttributeIndices("" + (l_train.classIndex() + 1));
                            l_filter.setInputFormat(l_train);
                            Instances l_trainDataClusterer = Filter.useFilter(l_train, l_filter);

                            l_training_buffer.close();
                            trainingFileStream.close();

                            SimpleClusterRunner executor = new SimpleClusterRunner(i, l_trainDataClusterer, l_train, output_dir);
                            collection.add(executor);
                        }

                        List<Future<ClusterData>> futures = pool.invokeAll(collection);
                        for (Future<ClusterData> future : futures) {
                            ClusterData data = future.get();
                            if (data != null) {
                                System.out.println("Got data for clusters: " + data.getNoOfCluster());
                                xySeries_letter_seed.add((double) data.getNoOfCluster(), data.getX());
                                xySeries_letter_seed_pct.add((double) data.getNoOfCluster(), data.getPctCorrect());


                            } else {
                                System.out.println("Failed");
                            }
                        }
                        pool.shutdown();

                        System.out.println("Threads completed for seeding");

                        xySeriesCollection_letter_seed.addSeries(xySeries_letter_seed);
                        xySeriesCollection_letter_seed_pct.addSeries(xySeries_letter_seed_pct);

                        chart(xySeriesCollection_letter_seed, "K_means_Clustering_Letter_dataset_Seed_Avg_Silhouette_Coeff", "Starting instance index", "Average Silhouette Coefficient", output_dir);
                        chart(xySeriesCollection_letter_seed_pct, "K_means_Clustering_Letter_dataset_Seed_Cluster_to_Class_Prediction_Correct_Pct", "Starting instance index", "Cluster to Class Prediction Correct Percent", output_dir);
                    }
                    break;

                    case 3: {

                        XYSeriesCollection xySeriesCollection_letter_em_k = new XYSeriesCollection();
                        XYSeriesCollection xySeriesCollection_letter_em_k_pct = new XYSeriesCollection();
                        XYSeries xySeries_letter_em_k = new XYSeries("EM Clustering - Letter dataset - No. of Clusters variation vs Loglikelihood");
                        XYSeries xySeries_letter_em_k_pct = new XYSeries("EM Clustering - Letter dataset - No. of Clusters variation vs Cluster to Class Percent Correct");


                        System.out.println("Option 3");
                        ExecutorService pool = Executors.newFixedThreadPool(10);
                        Collection<EMClusterRunner> collection = new ArrayList<EMClusterRunner>();

                        for (int i = 2; i < 13; i++) {
                            String training_file_name = "/letter_training_70pct_1.arff";

                            InputStream trainingFileStream = getClass().getResourceAsStream(training_file_name);

                            BufferedReader l_training_buffer = new BufferedReader(new InputStreamReader(
                                    trainingFileStream));

                            Instances l_train = new Instances(l_training_buffer);
                            l_train.setClassIndex(l_train.numAttributes() - 1);

                            weka.filters.unsupervised.attribute.Remove EMTrainfilter_l = new weka.filters.unsupervised.attribute.Remove();

                            EMTrainfilter_l.setAttributeIndices("" + (l_train.classIndex() + 1));
                            EMTrainfilter_l.setInputFormat(l_train);
                            Instances l_EMTrainDataClusterer = Filter.useFilter(l_train, EMTrainfilter_l);


                            l_training_buffer.close();
                            trainingFileStream.close();

                            EMClusterRunner executor = new EMClusterRunner(i, l_EMTrainDataClusterer, l_train, output_dir);
                            collection.add(executor);
                        }

                        List<Future<ClusterData>> futures = pool.invokeAll(collection);
                        for (Future<ClusterData> future : futures) {
                            ClusterData data = future.get();
                            if (data != null) {
                                System.out.println("Got data for clusters: " + data.getNoOfCluster());
                                xySeries_letter_em_k.add((double) data.getNoOfCluster(), data.getX());
                                xySeries_letter_em_k_pct.add((double) data.getNoOfCluster(), data.getPctCorrect());
                            } else {
                                System.out.println("Failed");
                            }
                        }
                        pool.shutdown();

                        System.out.println("Threads completed for em");

                        xySeriesCollection_letter_em_k.addSeries(xySeries_letter_em_k);
                        xySeriesCollection_letter_em_k_pct.addSeries(xySeries_letter_em_k_pct);

                        chart(xySeriesCollection_letter_em_k, "EM_Clustering_Letter_dataset_Clusters_variation_vs_Loglikelihood", "No. of Clusters variation", "Loglikelihood", output_dir);
                        chart(xySeriesCollection_letter_em_k_pct, "EM Clustering_Letter_dataset_Clusters_variation_vs_Cluster_to_Class_Prediction_Correct_Pct", "No. of Clusters variation", "Cluster to Class Prediction Correct Percent", output_dir);
                    }
                    break;

                    default:
                        break;
                }
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }


    private static void chart(XYSeriesCollection xySeriesCollection, String algo, String xLabel, String yLabel, String output_dir) throws Exception {

        JFreeChart xyLineChart = ChartFactory.createXYLineChart(algo, xLabel, yLabel, xySeriesCollection, PlotOrientation.VERTICAL, true, true, false);

		/* Step -3 : Write line chart to a file */
        int width = 640; /* Width of the image */
        int height = 480; /* Height of the image */
        String fileNamePrefix = output_dir + "/" + algo;
        File XYlineChart = new File(fileNamePrefix + ".png");
        ChartUtilities.saveChartAsPNG(XYlineChart, xyLineChart, width, height);
    }


}