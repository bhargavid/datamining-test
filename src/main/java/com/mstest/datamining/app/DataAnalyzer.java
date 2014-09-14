package com.mstest.datamining.app;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Random;

import weka.classifiers.Evaluation;
import weka.classifiers.trees.J48;
import weka.core.Instances;

public class DataAnalyzer {
    public static void main(String[] args) throws Exception {
        BufferedReader reader = new BufferedReader(
                new FileReader(
                        "C:/MyPC/GATech/MachineLearning/DataSets/bank/bank-full-training_70_pct_Noise.arff"));
        // BufferedReader reader = new BufferedReader( new
        // FileReader("C:/weather.nominal.arff"));
        Instances train = new Instances(reader);
        BufferedReader testReader = new BufferedReader(
                new FileReader(
                        "C:/MyPC/GATech/MachineLearning/DataSets/bank/bank-full-test_30_pct.arff"));
        Instances test = new Instances(testReader);
        train.setClassIndex(train.numAttributes() - 1);
        test.setClassIndex(test.numAttributes() - 1);
        reader.close();
        testReader.close();
        J48 j48Classifier = new J48();
        float v = new Float(.1);
        j48Classifier.setConfidenceFactor(v);
        j48Classifier.setMinNumObj(100);
        j48Classifier.buildClassifier(train);
        Integer percent = new Integer(0);
        for (int i = 0; i < 2; i++) {

            int splitTrainSize = (int) Math.round(train.numInstances()
                    * percent / 100);
            Instances splitTrain = new Instances(train, 0, splitTrainSize);
            // evaluate classifier and print some statistics
            Evaluation splitTrainEval = new Evaluation(splitTrain);
            Evaluation testEval = new Evaluation(test);
            if (splitTrain.numInstances() > 10) {
                System.out.println("\n Cross Validation \n");
                splitTrainEval.crossValidateModel(j48Classifier, splitTrain,
                        10, new Random(1));
            } else {
                System.out.println("\n No Cross Validation \n");
                splitTrainEval.evaluateModel(j48Classifier, splitTrain);
            }
            testEval.evaluateModel(j48Classifier, test);
            System.out.println("\n Current Iteration is " + i);
            System.out.println(splitTrainEval.toSummaryString(
                    "\n Train Results\n======\n", false));
            System.out.println(testEval.toSummaryString(
                    "\nTest Results\n======\n", false));
            Double testPctCorrect = new Double(0); // used for performance chart
            Double splitTrainPctCorrect = new Double(0); // used for performance
                                                         // chart
            testPctCorrect = testEval.pctCorrect();
            splitTrainPctCorrect = splitTrainEval.pctCorrect();
            Double testErrorRate = new Double(0); // used for RMS error chart
            Double splitTrainErrorRate = new Double(0); // used for RMS error
                                                        // chart
            testErrorRate = testEval.errorRate();
            splitTrainErrorRate = splitTrainEval.errorRate();
            percent += 5;
        }
    }
}
