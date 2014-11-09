package com.mstest.datamining.service;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.*;
import java.util.concurrent.Callable;

import weka.clusterers.ClusterEvaluation;
import weka.clusterers.SimpleKMeans;
import weka.core.DistanceFunction;
import weka.core.EuclideanDistance;
import weka.core.Instances;

/**
 * Created by bhargavi on 11/8/14.
 */
public class SimpleClusterRunner implements Callable<ClusterData> {


    private Instances l_trainDataClusterer;
    private Instances l_train;
    private int seed;
    private String output_dir;

    public SimpleClusterRunner(int seed, Instances l_trainDataClusterer, Instances l_train, String output_dir) {
        this.seed = seed;
        this.l_trainDataClusterer = l_trainDataClusterer;
        this.l_train = l_train;
        this.output_dir = output_dir;
    }

    public ClusterData call() throws Exception {
        SimpleKMeans simplekmeans = new SimpleKMeans();
        simplekmeans.setSeed(seed);
        //simplekmeans.setMaxIterations(500);
        simplekmeans.setNumClusters(12);
        simplekmeans.setPreserveInstancesOrder(true);
        DistanceFunction euclideanDist = new EuclideanDistance();
        simplekmeans.setDistanceFunction(euclideanDist);
        simplekmeans.buildClusterer(l_trainDataClusterer);
        ClusterEvaluation clusterEval = new ClusterEvaluation();
        clusterEval.setClusterer(simplekmeans);
        System.out.println("start");
        clusterEval.evaluateClusterer(l_train);
        System.out.println("start1");
        int[][] counts = clusterEval.getCounts();
        int[] clustClass = new int[simplekmeans.getNumClusters()];
        System.out.println("start2");
        if(simplekmeans.getNumClusters() <= l_train.numClasses()) {
            System.out.println("start3");
            clustClass = clusterEval.getClassesToClusters();
            System.out.println("start4");
        } else {
            for(int clust = 0 ; clust < simplekmeans.getNumClusters(); clust++) {
                Integer[] currCluster = convert(counts[clust]);
                List<Integer> currClusterList = Arrays.asList(currCluster);
                Integer maxValue = Collections.max(currClusterList);
                Map<Integer, Integer> clusterCntMap = new HashMap<Integer, Integer>();
                for( Integer classPosition=0; classPosition<currClusterList.size(); classPosition++ ) {
                    clusterCntMap.put(classPosition,currClusterList.get(classPosition));
                }

                Integer key = null;
                for(Map.Entry entry: clusterCntMap.entrySet()){
                    if(maxValue.equals(entry.getValue())){
                        key = (Integer) entry.getKey();
                        break; //breaking because its one to one map
                    }
                }
                clustClass[clust] = key;
            }
        }

        int[] clusterAssignments = simplekmeans.getAssignments();
        int correctCnt = 0;
        int incorrectCount = 0;
        for(int instIdx=0 ;instIdx < l_train.numInstances(); instIdx++ ) {
            int currentCluster = clusterAssignments[instIdx];
            int predictedClusterClass = clustClass[currentCluster];
            if(predictedClusterClass == l_train.get(instIdx).value(l_train.numAttributes() - 1)) {
                correctCnt++;
            } else {
                incorrectCount++;
            }
        }
        Random RandomO = new Random(simplekmeans.getSeed());
        int instanceIndex = RandomO.nextInt(l_train.numInstances() + 1);

        System.out.println("\n\n***************** K:2 Starting Seed:"+ seed +" instanceIndex:"+instanceIndex+"Dist:EUCLIDEAN**************************");
        System.out.println("Class to cluster"+Arrays.toString(clusterEval.getClassesToClusters()));
        System.out.println("\n\nCounts :"+Arrays.deepToString(clusterEval.getCounts()));
        System.out.println("\n\nCluster assigns:"+Arrays.toString(clusterEval.getClusterAssignments()));
        System.out.println("Cluster Eval Results:"+clusterEval.clusterResultsToString());
        System.out.println("Cluster Eval Results:"+Arrays.toString(simplekmeans.getAssignments()));
        System.out.println("\n Correct:"+correctCnt+" Incorrect:"+incorrectCount);
        System.out.println(Arrays.toString(clustClass));

        String fileName = output_dir + "/" + "Letter_Cluster_Seed_kMeans_OUT.txt" + "_" + seed;
        FileWriter l_fw = new FileWriter(new File(fileName));
        BufferedWriter l_bw = new BufferedWriter(l_fw);

        l_bw.write("\n\n***************** K:2 Starting Seed:"+ seed +" instanceIndex:"+instanceIndex+" Dist:EUCLIDEAN**************************");
        l_bw.write("\nClass to cluster"+Arrays.toString(clusterEval.getClassesToClusters()));
        l_bw.write("\nCounts :"+Arrays.deepToString(clusterEval.getCounts()));
        l_bw.write("\nCluster assigns:"+Arrays.toString(clusterEval.getClusterAssignments()));
        l_bw.write("\nCluster Eval Results:"+clusterEval.clusterResultsToString());
        l_bw.write("\n Correct:"+correctCnt+" Incorrect:"+incorrectCount);
        l_bw.write(Arrays.toString(clustClass));

        l_bw.close();
        l_fw.close();

        Double pctCorrect =  (((double)correctCnt / l_train.numInstances()) * 100);

        ClusterData data = new ClusterData();
        data.setNoOfCluster(seed);
        data.setX(simplekmeans.getAvgSilCoeff());
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
