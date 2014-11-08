package com.mstest.datamining.service;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import weka.clusterers.ClusterEvaluation;
import weka.clusterers.SimpleKMeans;
import weka.core.DistanceFunction;
import weka.core.EuclideanDistance;
import weka.core.Instances;

public class ClusterRunnerExecutor implements Callable<ClusterData> {

    private Instances l_trainDataClusterer;
    private Instances l_train;
    private int noOfCluster;

    private static final String output_dir = "/tmp/clustering";

    public ClusterRunnerExecutor(int iteration, Instances l_trainDataClusterer, Instances l_train) {
        this.noOfCluster = iteration;
        this.l_trainDataClusterer = l_trainDataClusterer;
        this.l_train = l_train;

    }

    public ClusterData call() throws Exception {

        SimpleKMeans simplekmeans = new SimpleKMeans();
        simplekmeans.setSeed(10);
        // simplekmeans.setMaxIterations(50);
        simplekmeans.setNumClusters(noOfCluster);
        simplekmeans.setPreserveInstancesOrder(true);
        DistanceFunction euclideanDist = new EuclideanDistance();
        simplekmeans.setDistanceFunction(euclideanDist);
        simplekmeans.buildClusterer(l_trainDataClusterer);
        ClusterEvaluation clusterEval = new ClusterEvaluation();
        System.out.println("stuck here");
        System.out.println("\n num clusters:" + simplekmeans.getNumClusters());
        clusterEval.setClusterer(simplekmeans);
        clusterEval.evaluateClusterer(l_train);
        System.out.println("stuck here1");

        int[][] counts = clusterEval.getCounts();

        int[] clustClass = new int[simplekmeans.getNumClusters()];

        if (simplekmeans.getNumClusters() <= l_train.numClasses()) {
            clustClass = clusterEval.getClassesToClusters();
        } else {
            for (int clust = 0; clust < simplekmeans.getNumClusters(); clust++) {
                Integer[] currCluster = convert(counts[clust]);
                List<Integer> currClusterList = Arrays.asList(currCluster);
                Integer maxValue = Collections.max(currClusterList);
                Map<Integer, Integer> clusterCntMap = new HashMap<Integer, Integer>();
                for (Integer classPosition = 0; classPosition < currClusterList
                        .size(); classPosition++) {
                    clusterCntMap.put(classPosition,
                            currClusterList.get(classPosition));
                }

                Integer key = null;
                for (Map.Entry entry : clusterCntMap.entrySet()) {
                    if (maxValue.equals(entry.getValue())) {
                        key = (Integer) entry.getKey();
                        break; // breaking because its one to one map
                    }
                }
                clustClass[clust] = key;
            }
        }
        System.out.println("stuck here2");

        int[] clusterAssignments = simplekmeans.getAssignments();
        int correctCnt = 0;
        int incorrectCount = 0;
        for (int instIdx = 0; instIdx < l_train.numInstances(); instIdx++) {
            int currentCluster = clusterAssignments[instIdx];
            int predictedClusterClass = clustClass[currentCluster];
            if (predictedClusterClass == l_train.get(instIdx).value(
                    l_train.numAttributes() - 1)) {
                correctCnt++;
            } else {
                incorrectCount++;
            }
        }

        System.out.println("\n\n***************** K:" + noOfCluster
                + " Starting Seed:10 Dist:EUCLIDEAN**************************");
        System.out.println("Class to cluster"
                + Arrays.toString(clusterEval.getClassesToClusters()));
        System.out.println("\n\nCounts :"
                + Arrays.deepToString(clusterEval.getCounts()));
        System.out.println("\n\nCluster assigns:"
                + Arrays.toString(clusterEval.getClusterAssignments()));
        System.out.println("Cluster Eval Results:"
                + clusterEval.clusterResultsToString());
        System.out.println("Cluster Eval Results:"
                + Arrays.toString(simplekmeans.getAssignments()));
        System.out.println("\n Correct:" + correctCnt + " Incorrect:"
                + incorrectCount);
        System.out.println(Arrays.toString(clustClass));

        String fileName = output_dir + "/" + "Letter_Cluster_kMeans_OUT.txt" + "_" + noOfCluster;
        FileWriter l_fw = new FileWriter(new File(fileName));
        BufferedWriter l_bw = new BufferedWriter(l_fw);


        l_bw.write("\n\n***************** K:" + noOfCluster
                + " Starting Seed:10 Dist:EUCLIDEAN**************************");
        l_bw.write("\nClass to cluster"
                + Arrays.toString(clusterEval.getClassesToClusters()));
        l_bw.write("\nCounts :" + Arrays.deepToString(clusterEval.getCounts()));
        l_bw.write("\nCluster assigns:"
                + Arrays.toString(clusterEval.getClusterAssignments()));
        l_bw.write("\nCluster Eval Results:"
                + clusterEval.clusterResultsToString());
        l_bw.write("\n Correct:" + correctCnt + " Incorrect:" + incorrectCount);
        l_bw.write(Arrays.toString(clustClass));
        Double pctCorrect = (((double) correctCnt / l_train.numInstances()) * 100);

        ClusterData data = new ClusterData();
        data.setNoOfCluster(noOfCluster);
        data.setAvgSilCoeff(simplekmeans.getAvgSilCoeff());
        data.setPctCorrect(pctCorrect);

        return data;
    }

    private static Integer[] convert(int[] ints) {
        Integer[] copy = new Integer[ints.length];
        for(int i = 0; i < copy.length; i++) {
            copy[i] = Integer.valueOf(ints[i]);
        }
        return copy;
    }

}