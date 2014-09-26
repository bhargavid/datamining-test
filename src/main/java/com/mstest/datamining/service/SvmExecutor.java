package com.mstest.datamining.service;

import com.mstest.datamining.model.Axis;
import com.mstest.datamining.model.Constant;
import com.mstest.datamining.model.Plot;
import weka.classifiers.Evaluation;
import weka.classifiers.functions.LibSVM;
import weka.classifiers.functions.SMO;
import weka.classifiers.functions.supportVector.PolyKernel;
import weka.classifiers.functions.supportVector.RBFKernel;
import weka.core.Instances;
import weka.core.SelectedTag;

import java.util.Random;
import java.util.concurrent.Callable;

/**
 * Created by bdamodaran on 9/26/14.
 */
public class SvmExecutor implements Callable<Plot> {
    private Double gamma;
    private Double cost;
    private Double exp;
    private String library;
    private String function_type;
    private Integer percent;
    private Instances train;
    private Instances test;

    public SvmExecutor(
            Double gamma, Double cost, Double exp, String library, String function_type, Integer percent,
            Instances train, Instances test) {
        this.gamma = gamma;
        this.cost = cost;
        this.exp = exp;
        this.library = library;
        this.function_type = function_type;
        this.percent = percent;
        this.train = train;
        this.test = test;
    }

    @Override
    public Plot call() throws Exception {
        Plot plot = new Plot();

        int splitTrainSize = (int) Math.round(train.numInstances()
                                              * percent / 100);

        Instances splitTrain = new Instances(train, 0, splitTrainSize);
        splitTrain.setClassIndex(splitTrain.numAttributes() - 1);

        System.out.println("Running for percent: "+percent);

        if (library != null && Constant.LIBSVM.equalsIgnoreCase(library)) {

            LibSVM svm = new LibSVM();

            if (Constant.RBF.equalsIgnoreCase(function_type))
                svm.setKernelType(new SelectedTag(LibSVM.KERNELTYPE_RBF, LibSVM.TAGS_KERNELTYPE));
            else
                svm.setKernelType(new SelectedTag(LibSVM.KERNELTYPE_POLYNOMIAL, LibSVM.TAGS_KERNELTYPE));

            svm.setCost(cost);
            svm.setGamma(gamma);
            svm.buildClassifier(splitTrain);

            Evaluation splitTrainEval = new Evaluation(splitTrain);
            Evaluation testEval = new Evaluation(test);

            if (splitTrain.numInstances() > 10) {
                splitTrainEval.crossValidateModel(svm,
                        splitTrain, 10, new Random(1));
            } else {
                splitTrainEval.evaluateModel(svm, splitTrain);
            }
            testEval.evaluateModel(svm, test);

            Double testPctCorrect = testEval.pctCorrect();
            Double splitTrainPctCorrect = splitTrainEval.pctCorrect();

            Double testErrorRate = testEval.errorRate();
            Double splitTrainErrorRate = splitTrainEval.errorRate();

            // add graph points here
            Axis perf_point = new Axis();
            Axis error_point = new Axis();

            perf_point.setX(new Double(splitTrainSize));
            perf_point.setY1(testPctCorrect);
            perf_point.setY2(splitTrainPctCorrect);

            error_point.setX(new Double(splitTrainSize));
            error_point.setY1(testErrorRate);
            error_point.setY2(splitTrainErrorRate);

            plot.setPerfPoint(perf_point);
            plot.setErrorPoint(error_point);

            System.out.println(splitTrainEval.toSummaryString(
                    "\n Train Results\n======\n", false));
            System.out.println(testEval.toSummaryString(
                    "\nTest Results\n======\n", false));


        } else if (library != null && Constant.SMO.equalsIgnoreCase(library)) {
            //smo library

            SMO smoSVM = new SMO();

            if (Constant.RBF.equalsIgnoreCase(function_type)) {
                RBFKernel rbfKernel = new RBFKernel();
                rbfKernel.setGamma(gamma);
                smoSVM.setKernel(rbfKernel);
            } else if (Constant.POLY_KERNEL.equalsIgnoreCase(function_type)) {
                PolyKernel polyKernel = new PolyKernel();
                polyKernel.setExponent(exp);
                polyKernel.setUseLowerOrder(true);
                smoSVM.setKernel(polyKernel);
            }
            smoSVM.setC(cost);
            smoSVM.buildClassifier(splitTrain);

            Evaluation splitTrainEval = new Evaluation(splitTrain);
            Evaluation testEval = new Evaluation(test);

            if (splitTrain.numInstances() > 10) {
                splitTrainEval.crossValidateModel(smoSVM,
                        splitTrain, 10, new Random(1));
            } else {
                splitTrainEval.evaluateModel(smoSVM, splitTrain);
            }
            testEval.evaluateModel(smoSVM, test);

            Double testPctCorrect = testEval.pctCorrect();
            Double splitTrainPctCorrect = splitTrainEval.pctCorrect();

            Double testErrorRate = testEval.errorRate();
            Double splitTrainErrorRate = splitTrainEval.errorRate();

            // add graph points here
            Axis perf_point = new Axis();
            Axis error_point = new Axis();

            perf_point.setX(new Double(splitTrainSize));
            perf_point.setY1(testPctCorrect);
            perf_point.setY2(splitTrainPctCorrect);

            error_point.setX(new Double(splitTrainSize));
            error_point.setY1(testErrorRate);
            error_point.setY2(splitTrainErrorRate);

            plot.setPerfPoint(perf_point);
            plot.setErrorPoint(error_point);

            System.out.println(splitTrainEval.toSummaryString(
                    "\n Train Results\n======\n", false));
            System.out.println(testEval.toSummaryString(
                    "\nTest Results\n======\n", false));

        } else {
            System.out.println("Some configs are skipping");
        }

        System.out.println("Thread is wrapping up");
        return plot;
    }
}
