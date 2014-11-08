package com.mstest.datamining.service;

import com.mstest.datamining.model.Axis;
import com.mstest.datamining.model.Plot;
import weka.classifiers.Evaluation;
import weka.classifiers.functions.MultilayerPerceptron;
import weka.core.Instances;

import java.util.Random;
import java.util.concurrent.Callable;

/**
 * Created by bdamodaran on 9/24/14.
 */
public class MLPExecutor implements Callable<Plot> {

    private String meanHiddenLayerStr;
    private Double momentum;
    private Double learningRate;
    private Integer trainingTime;
    private Instances train;
    private Instances test;

    public MLPExecutor(
            String meanHiddenLayerStr, Double momentum, Double learningRate, Integer trainingTime, Instances train,
            Instances test) {
        this.meanHiddenLayerStr = meanHiddenLayerStr;
        this.momentum = momentum;
        this.learningRate = learningRate;
        this.trainingTime = trainingTime;
        this.train = train;
        this.test = test;
    }


    public Plot call() throws Exception {

        System.out.println("Executing for hlayer: " + meanHiddenLayerStr + " momentum: " + momentum + " learningR: " +
                           learningRate + " trainingTime: " + trainingTime);

        String momentumStr = String.format("%.1f", momentum);
        momentum = Double.parseDouble(momentumStr);

        String learningRateStr = String.format("%.1f", learningRate);
        learningRate = Double.parseDouble(learningRateStr);
        MultilayerPerceptron mlpClassifier = new MultilayerPerceptron();

        mlpClassifier.setHiddenLayers(meanHiddenLayerStr);
        mlpClassifier.setLearningRate(learningRate);
        mlpClassifier.setMomentum(momentum);
        mlpClassifier.setTrainingTime(trainingTime);
        mlpClassifier.setDecay(true);
        mlpClassifier.buildClassifier(train);

        Evaluation trainEval = new Evaluation(train);
        trainEval.crossValidateModel(mlpClassifier, train, 10, new Random(1));

        Evaluation testEval = new Evaluation(test);
        testEval.evaluateModel(mlpClassifier, test);

        Double testPctCorrect = testEval.pctCorrect();
        Double trainPctCorrect = trainEval.pctCorrect();

        Double testErrorRate = testEval.errorRate();
        Double trainErrorRate = trainEval.errorRate();

        System.out.println("testPctCorrect" + testPctCorrect);
        System.out.println("trainPctCorrect"
                           + trainEval);
        System.out.println("testErrorRate" + testErrorRate);
        System.out.println("trainErrorRate" + trainErrorRate);

        // add graph points here
        Axis perf_point = getAxis(new Double(trainingTime), testPctCorrect, trainPctCorrect);
        Axis error_point = getAxis(new Double(trainingTime), testErrorRate, trainErrorRate);

        Plot plot = new Plot();
        plot.setPerfPoint(perf_point);
        plot.setErrorPoint(error_point);

        return plot;
    }


    private Axis getAxis(Double x, Double y1, Double y2) {
        Axis axis = new Axis();

        axis.setX(x);
        axis.setY1(y1);
        axis.setY2(y2);

        return axis;
    }
}
