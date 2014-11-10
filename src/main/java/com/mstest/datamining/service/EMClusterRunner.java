package com.mstest.datamining.service;

import com.mstest.datamining.utils.FileUtil;
import weka.clusterers.ClusterEvaluation;
import weka.clusterers.EM;
import weka.core.Instances;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.*;
import java.util.concurrent.Callable;

/**
 * Created by bhargavi on 11/9/14.
 */
public class EMClusterRunner implements Callable<ClusterData>{
    private Instances l_EMTrainDataClusterer;
    private Instances l_train;
    private int noOfCluster;
    private String output_dir;
    private String file_name;

    public EMClusterRunner(int noOfCluster, Instances l_EMTrainDataClusterer, Instances l_train, String output_dir, String file_name) {
        this.noOfCluster = noOfCluster;
        this.l_EMTrainDataClusterer = l_EMTrainDataClusterer;
        this.l_train = l_train;
        this.output_dir = output_dir;
        this.file_name = file_name;
    }

    public ClusterData call() throws Exception {

        File theDir = new File(output_dir);
        if (!FileUtil.createDirs(theDir)) {
            System.out.println("ERROR:: Failed to create output directory. " + output_dir);
            return null;
        }

        EM emCluster = new EM();
        emCluster.setNumClusters(noOfCluster);
        emCluster.setSeed(10);
        emCluster.buildClusterer(l_EMTrainDataClusterer);
        ClusterEvaluation emClusterEval = new ClusterEvaluation();
        emClusterEval.setClusterer(emCluster);
        emClusterEval.evaluateClusterer(l_train);

        int[][] counts = emClusterEval.getCounts();
        int[] clustClass = new int[emCluster.getNumClusters()];
        if(emCluster.getNumClusters() <= l_train.numClasses()) {
            clustClass = emClusterEval.getClassesToClusters();
        } else {
            for(int clust = 0 ; clust < emCluster.getNumClusters(); clust++) {
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

        double[] clusterAssignments = emClusterEval.getClusterAssignments();
        int correctCnt = 0;
        int incorrectCount = 0;
        for(int instIdx=0 ;instIdx < l_train.numInstances(); instIdx++ ) {
            int currentCluster = (int) clusterAssignments[instIdx];
            int predictedClusterClass = clustClass[currentCluster];
            if(predictedClusterClass == l_train.get(instIdx).value(l_train.numAttributes() - 1)) {
                correctCnt++;
            } else {
                incorrectCount++;
            }
        }
        Random RandomO = new Random(emCluster.getSeed());
        int instanceIndex = RandomO.nextInt(l_train.numInstances() + 1);

        System.out.println("\n\n***************** K:2 Starting Seed:"+noOfCluster+" instanceIndex:"+instanceIndex+"Dist:EUCLIDEAN**************************");
        System.out.println("\n\nCounts :"+Arrays.deepToString(emClusterEval.getCounts()));
        System.out.println("\n\nCluster assigns:"+Arrays.toString(emClusterEval.getClusterAssignments()));
        System.out.println("\n\n loglikelihood"+emClusterEval.getLogLikelihood());
        System.out.println("Cluster Eval Results:"+emClusterEval.clusterResultsToString());
        System.out.println("\n Correct:"+correctCnt+" Incorrect:"+incorrectCount);

        String fileName = output_dir + "/" + file_name + "_" + "Letter_Cluster_EM_OUT.txt" + "_" + noOfCluster;
        FileWriter l_fw = new FileWriter(new File(fileName));
        BufferedWriter l_bw = new BufferedWriter(l_fw);

        l_bw.write("\n\n***************** K:2 Starting Seed:"+noOfCluster+" instanceIndex:"+instanceIndex+"Dist:EUCLIDEAN**************************");
        l_bw.write("\n\nCounts :"+Arrays.deepToString(emClusterEval.getCounts()));
        l_bw.write("\n\nCluster assigns:"+Arrays.toString(emClusterEval.getClusterAssignments()));
        l_bw.write("\n\n loglikelihood"+emClusterEval.getLogLikelihood());
        l_bw.write("Cluster Eval Results:"+emClusterEval.clusterResultsToString());
        l_bw.write("\n Correct:"+correctCnt+" Incorrect:"+incorrectCount);

        l_bw.close();
        l_fw.close();

        Double pctCorrect =  (((double)correctCnt / l_train.numInstances()) * 100);

        ClusterData data = new ClusterData();
        data.setNoOfCluster(noOfCluster);
        data.setX(emClusterEval.getLogLikelihood());
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
