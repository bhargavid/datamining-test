package com.mstest.datamining.utils;

import com.mstest.datamining.model.*;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;

import java.util.*;

/**
 * Created by bloganathan on 9/21/14.
 */
public class CommonUtil {
    private static final String PROPERTIES = "analyzer.properties";
    private static final String BANK = "bank";
    private static final String LETTER = "letter";


    //to avoid for each failing from null list
    public static <T> Iterable<T> emptyIfNull(Iterable<T> iterable) {
        return iterable == null ? Collections.<T>emptyList() : iterable;
    }

    public static void fillConfigs(List<DataConfig> dataConfigs, Algorithm algorithm) {
        //Configuration config = new PropertiesConfiguration("usergui.properties");
        try {
            Configuration prop = new PropertiesConfiguration(PROPERTIES);

            switch (algorithm) {
                case j48:
                case multilayerperceptron:
                case knn:
                case adaboost:
                case svm:
                    dataConfigs.add(getDataConfig(algorithm, prop, BANK));
                    dataConfigs.add(getDataConfig(algorithm, prop, LETTER));
                    break;

                default:
                    break;
            }

        } catch (ConfigurationException ce) {
            //TODO can we handle this better?
            ce.printStackTrace();
        }
    }

    private static DataConfig getDataConfig(Algorithm algorithm, Configuration prop, String dataSet) {
        DataConfig dataConfig = new DataConfig();
        DataFile dataFile = new DataFile();
        List<Label> labels = new ArrayList<Label>();
        List<Config> configs = new ArrayList<Config>();
        Config config = new Config();

        switch (algorithm) {
            case j48:

                if (BANK.equalsIgnoreCase(dataSet)) {

                    dataFile.setTrainingFile(prop.getString("dt.bank.training.file"));
                    dataFile.setTestFile(prop.getString("dt.bank.test.file"));

                    dataConfig.setDataFile(dataFile);

                    String dt_bank_config = prop.getString("dt.bank.minnumobj.confidencefactor");
                    String[] config_arr = dt_bank_config.split(":");

                    Label label = new Label();
                    label.setName(Constant.MIN_NUM_OBJECT);
                    label.setValue(Integer.valueOf(config_arr[0]));
                    labels.add(label);

                    label = new Label();
                    label.setName(Constant.CONFIDENCE_FACTOR);
                    label.setValue(Float.valueOf(config_arr[1]));
                    labels.add(label);

                    config.setLabels(labels);

                    configs.add(config);
                    dataConfig.setConfigs(configs);

                } else if (LETTER.equalsIgnoreCase(dataSet)) {
                    dataFile.setTrainingFile(prop.getString("dt.letter.training.file"));
                    dataFile.setTestFile(prop.getString("dt.letter.test.file"));

                    dataConfig.setDataFile(dataFile);

                    String dt_bank_config = prop.getString("dt.letter.minnumobj.confidencefactor");
                    String[] config_arr = dt_bank_config.split(":");

                    Label label = new Label();
                    label.setName(Constant.MIN_NUM_OBJECT);
                    label.setValue(Integer.valueOf(config_arr[0]));
                    labels.add(label);

                    label = new Label();
                    label.setName(Constant.CONFIDENCE_FACTOR);
                    label.setValue(Float.valueOf(config_arr[1]));
                    labels.add(label);

                    config.setLabels(labels);

                    configs.add(config);
                    dataConfig.setConfigs(configs);
                }

                break;

            case multilayerperceptron:
                if (BANK.equalsIgnoreCase(dataSet)) {

                    dataFile.setTrainingFile(prop.getString("mlp.bank.training.file"));
                    dataFile.setTestFile(prop.getString("mlp.bank.test.file"));
                    dataConfig.setDataFile(dataFile);

                    Label label = new Label();
                    label.setName(Constant.HIDDENLAYER);
                    label.setValue(prop.getString("mlp.bank.hiddenlayer"));
                    labels.add(label);

                    label = new Label();
                    label.setName(Constant.MOMENTUM);
                    label.setValue(prop.getDouble("mlp.bank.momentum"));
                    labels.add(label);

                    label = new Label();
                    label.setName(Constant.LEARNING_RATE);
                    label.setValue(prop.getDouble("mlp.bank.learning_rate"));
                    labels.add(label);

                } else if (LETTER.equalsIgnoreCase(dataSet)) {
                    dataFile.setTrainingFile(prop.getString("mlp.letter.training.file"));
                    dataFile.setTestFile(prop.getString("mlp.letter.test.file"));
                    dataConfig.setDataFile(dataFile);

                    Label label = new Label();
                    label.setName(Constant.HIDDENLAYER);
                    label.setValue(prop.getString("mlp.letter.hiddenlayer"));
                    labels.add(label);

                    label = new Label();
                    label.setName(Constant.MOMENTUM);
                    label.setValue(prop.getDouble("mlp.letter.momentum"));
                    labels.add(label);

                    label = new Label();
                    label.setName(Constant.LEARNING_RATE);
                    label.setValue(prop.getDouble("mlp.letter.learning_rate"));
                    labels.add(label);
                }

                config.setLabels(labels);
                configs.add(config);
                dataConfig.setConfigs(configs);

                break;

            case knn:
                if (BANK.equalsIgnoreCase(dataSet)) {
                    dataFile.setTrainingFile(prop.getString("knn.bank.training.file"));
                    dataFile.setTestFile(prop.getString("knn.bank.test.file"));
                    dataConfig.setDataFile(dataFile);


                    Label label = new Label();
                    label.setName(Constant.K);
                    Integer k = prop.getInt("knn.bank.k");
                    label.setValue(k);
                    labels.add(label);

                    label = new Label();
                    label.setName(Constant.DISTANCE);
                    label.setValue(prop.getString("knn.bank.distance"));
                    labels.add(label);
                } else if (LETTER.equalsIgnoreCase(dataSet)) {
                    dataFile.setTrainingFile(prop.getString("knn.letter.training.file"));
                    dataFile.setTestFile(prop.getString("knn.letter.test.file"));
                    dataConfig.setDataFile(dataFile);


                    Label label = new Label();
                    label.setName(Constant.K);
                    Integer k = prop.getInt("knn.letter.k");
                    label.setValue(k);
                    labels.add(label);

                    label = new Label();
                    label.setName(Constant.DISTANCE);
                    label.setValue(prop.getString("knn.letter.distance"));
                    labels.add(label);
                }

                config.setLabels(labels);
                configs.add(config);
                dataConfig.setConfigs(configs);

                break;

            case adaboost:
                if (BANK.equalsIgnoreCase(dataSet)) {

                    dataFile.setTrainingFile(prop.getString("adaboost.bank.training.file"));
                    dataFile.setTestFile(prop.getString("adaboost.bank.test.file"));

                    dataConfig.setDataFile(dataFile);

                    String dt_bank_config = prop.getString("adaboost.bank.minnumobj.confidencefactor");
                    String[] config_arr = dt_bank_config.split(":");

                    Label label = new Label();
                    label.setName(Constant.MIN_NUM_OBJECT);
                    label.setValue(Integer.valueOf(config_arr[0]));
                    labels.add(label);

                    label = new Label();
                    label.setName(Constant.CONFIDENCE_FACTOR);
                    label.setValue(Float.valueOf(config_arr[1]));
                    labels.add(label);

                    config.setLabels(labels);

                    configs.add(config);
                    dataConfig.setConfigs(configs);

                } else if (LETTER.equalsIgnoreCase(dataSet)) {
                    dataFile.setTrainingFile(prop.getString("adaboost.letter.training.file"));
                    dataFile.setTestFile(prop.getString("adaboost.letter.test.file"));

                    dataConfig.setDataFile(dataFile);

                    String dt_bank_config = prop.getString("adaboost.letter.minnumobj.confidencefactor");
                    String[] config_arr = dt_bank_config.split(":");

                    Label label = new Label();
                    label.setName(Constant.MIN_NUM_OBJECT);
                    label.setValue(Integer.valueOf(config_arr[0]));
                    labels.add(label);

                    label = new Label();
                    label.setName(Constant.CONFIDENCE_FACTOR);
                    label.setValue(Float.valueOf(config_arr[1]));
                    labels.add(label);

                    config.setLabels(labels);

                    configs.add(config);
                    dataConfig.setConfigs(configs);
                }
                break;

            case svm:
                if(BANK.equalsIgnoreCase(dataSet)) {
                    dataFile.setTrainingFile(prop.getString("svm.bank.training.file"));
                    dataFile.setTestFile(prop.getString("svm.bank.test.file"));

                    dataConfig.setDataFile(dataFile);

                    List<Object> propList = prop.getList("svm.bank.c.var.functiontype");

                    //expected format c:var:functiontype
                    for(Object property: propList) {
                        String property_str = (String) property;
                        String[] config_arr = property_str.split(":");

                        //if function type is polynomial then 2nd variable is treated as exp otherwise gamma
                        Label label = new Label();
                        label.setName(Constant.COST);
                        label.setValue(Double.valueOf(config_arr[0]));
                        labels.add(label);

                        label = new Label();

                        if(Constant.POLYNOMIAL.equalsIgnoreCase(config_arr[2]))
                            label.setName(Constant.EXP);
                        else
                            label.setName(Constant.GAMMA);
                        label.setValue(Double.valueOf(config_arr[1]));
                        labels.add(label);

                        label = new Label();
                        label.setName(Constant.FUNCTION_TYPE);
                        label.setValue(config_arr[2]);
                        labels.add(label);

                        Config tmpConfig = new Config();
                        tmpConfig.setLabels(labels);

                        configs.add(tmpConfig);
                    }

                    dataConfig.setConfigs(configs);

                } else if(LETTER.equalsIgnoreCase(dataSet)) {
                    dataFile.setTrainingFile(prop.getString("svm.letter.training.file"));
                    dataFile.setTestFile(prop.getString("svm.letter.test.file"));

                    dataConfig.setDataFile(dataFile);

                    List<Object> propList = prop.getList("svm.letter.c.var.functiontype");

                    //expected format c:var:functiontype
                    for(Object property: propList) {
                        String property_str = (String) property;
                        String[] config_arr = property_str.split(":");

                        //if function type is polynomial then 2nd variable is treated as exp otherwise gamma
                        Label label = new Label();
                        label.setName(Constant.COST);
                        label.setValue(Double.valueOf(config_arr[0]));
                        labels.add(label);

                        label = new Label();

                        if(Constant.POLYNOMIAL.equalsIgnoreCase(config_arr[2]))
                            label.setName(Constant.EXP);
                        else
                            label.setName(Constant.GAMMA);
                        label.setValue(Double.valueOf(config_arr[1]));
                        labels.add(label);

                        label = new Label();
                        label.setName(Constant.FUNCTION_TYPE);
                        label.setValue(config_arr[2]);
                        labels.add(label);

                        Config tmpConfig = new Config();
                        tmpConfig.setLabels(labels);

                        configs.add(tmpConfig);
                    }

                    dataConfig.setConfigs(configs);
                }

                break;

            default:
                break;


        }

        return dataConfig;
    }
}
