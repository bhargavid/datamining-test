package com.mstest.datamining.service;

import com.mstest.datamining.model.Axis;
import com.mstest.datamining.model.Plot;
import weka.classifiers.Evaluation;
import weka.classifiers.meta.AdaBoostM1;
import weka.classifiers.trees.J48;
import weka.core.Instances;

import java.util.Random;
import java.util.concurrent.Callable;

/**
 * Created by bloganathan on 9/25/14.
 */
public class AdaBoostExecutor implements Callable<Plot> {
    private Integer minNumObj;
    private Float confidenceFactor;
    private Integer iterations;
    private Instances train;
    private Instances test;

    public AdaBoostExecutor(
            Integer minNumObj, Float confidenceFactor, Integer iterations, Instances train, Instances test) {
        this.minNumObj = minNumObj;
        this.confidenceFactor = confidenceFactor;
        this.iterations = iterations;
        this.train = train;
        this.test = test;
    }


    @Override
    public Plot call() throws Exception {
        AdaBoostM1 adaBoostM1Classifier = new AdaBoostM1();
        adaBoostM1Classifier.setNumIterations(iterations);
        J48 j48Classifier = new J48();
        j48Classifier.setMinNumObj(minNumObj);
        j48Classifier.setConfidenceFactor(confidenceFactor);
        adaBoostM1Classifier.setClassifier(j48Classifier);
        adaBoostM1Classifier.buildClassifier(train);

        Evaluation trainEval = new Evaluation(train);
        trainEval.crossValidateModel(adaBoostM1Classifier, train, 10, new Random(1));

        Evaluation testEval = new Evaluation(test);
        testEval.evaluateModel(adaBoostM1Classifier, test);

        System.out.println(trainEval.toSummaryString("\n Train Results\n======\n", false));
        System.out.println("Current confidence factor:" + confidenceFactor + " MinNumObj:" + minNumObj + " numOfIterations: "+iterations);

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
        Axis perf_point = getAxis(new Double(iterations), testPctCorrect, trainPctCorrect);
        Axis error_point = getAxis(new Double(iterations), testErrorRate, trainErrorRate);

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
