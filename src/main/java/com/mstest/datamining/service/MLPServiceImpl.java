package com.mstest.datamining.service;

import static com.mstest.datamining.utils.CommonUtil.emptyIfNull;
import static com.mstest.datamining.utils.CommonUtil.fillConfigs;

import com.mstest.datamining.app.AppCommandOptions;
import com.mstest.datamining.model.Algorithm;
import com.mstest.datamining.model.Config;
import com.mstest.datamining.model.DataConfig;
import com.mstest.datamining.model.DataFile;
import weka.classifiers.Evaluation;
import weka.classifiers.functions.MultilayerPerceptron;
import weka.core.Instances;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * Created by bloganathan on 9/22/14.
 */
public class MLPServiceImpl implements MLPService {

    private static final String TMP_FILE_PATH = "/tmp/datamining-test/";

    @Override
    public void run(Map<String, Object> params_map) throws Exception {
        if(params_map.containsKey(AppCommandOptions.CONFIGURE)) {
            configure(params_map);
        } else {
            System.out.println("Executing job mlp");
            execute(params_map);
        }
    }

    private void execute(Map<String, Object> params_map) throws Exception {
        String output_dir = (String) params_map.get(AppCommandOptions.OUTPUT_DIR);
        if (output_dir == null)
            output_dir = TMP_FILE_PATH;

        FileWriter fw = new FileWriter(output_dir+"/"+"multilayer_perceptron.dat");
        BufferedWriter bw = new BufferedWriter(fw);

        List<DataConfig> dataConfigs = new ArrayList<DataConfig>();
        fillConfigs(dataConfigs, Algorithm.multilayerperceptron);

        InputStream testFileIn = null;
        InputStream trainingFileIn = null;

        for(DataConfig dataConfig: emptyIfNull(dataConfigs)) {


            testFileIn = getClass().getResourceAsStream("/" + dataConfig.getDataFile().getTestFile());
            trainingFileIn = getClass().getResourceAsStream("/" + dataConfig.getDataFile().getTrainingFile());


            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    trainingFileIn));

            BufferedReader testReader = new BufferedReader(
                    new InputStreamReader(testFileIn));

            Instances train = new Instances(reader);
            Instances test = new Instances(testReader);
            train.setClassIndex(train.numAttributes() - 1);
            test.setClassIndex(test.numAttributes() - 1);
            reader.close();
            testReader.close();

            int trainingtime_increments = 50;
            int trainingtime = 0;
/*
            Bank:
            HiddenLayer:4 Momentum:0.7 Learning rate:0.3

            Letters:
            HiddenLayer:19 Momentum:0.7 Learning rate:0.3
*/


            String meanHiddenLayerStr = new String("19");
            Double momentum = new Double(0.7);
            Double learningRate = new Double(0.3);


            if(dataConfig.getDataFile().getTestFile().contains("Bank")) {
                meanHiddenLayerStr = new String("4");
                momentum = new Double(0.7);
                learningRate = new Double(0.3);
            }

            for(int i = 0; i <= 2; i++) {

                Evaluation testEval = new Evaluation(test);

                String momentumStr = String.format("%.1f", momentum);
                momentum = Double.parseDouble(momentumStr);

                String learningRateStr = String.format("%.1f", learningRate);
                learningRate = Double.parseDouble(learningRateStr);
                MultilayerPerceptron mlpClassifier = new MultilayerPerceptron();

                mlpClassifier.setHiddenLayers(meanHiddenLayerStr);
                mlpClassifier.setLearningRate(learningRate);
                mlpClassifier.setMomentum(momentum);
                mlpClassifier.setTrainingTime(trainingtime);
                mlpClassifier.setDecay(true);
                mlpClassifier.buildClassifier(train);
                Evaluation trainEval = new Evaluation(train);
                trainEval.crossValidateModel(mlpClassifier, train, 10, new Random(1));
                testEval.evaluateModel(mlpClassifier, test);


                bw.write(trainEval.toSummaryString("\n Train Results\n======\n", false));
                bw.write(testEval.toSummaryString("\n Test Results\n=====\n", false));
                bw.write(
                        "HiddenLayer:" + meanHiddenLayerStr + "Momentum:" + momentum + "Learning rate:" + learningRate +
                        "training time: "+trainingtime+ " iteration i" +i +"\n");
                System.out.println(trainEval.toSummaryString("\n Train Results\n======\n", false));
                System.out.println(
                        "HiddenLayer:" + meanHiddenLayerStr + "Momentum:" + momentum + "Learning rate:" + learningRate + "training time: "+trainingtime+ " iteration i" +i );

                trainingtime += trainingtime_increments;
            }

            if(testFileIn != null)
                testFileIn.close();

            if(trainingFileIn != null)
                trainingFileIn.close();
        }
    }

    private void configure(Map<String, Object> params_map) {
        //TODO implement this
    }
}
