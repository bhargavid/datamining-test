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
                case decistiontree:
                    dataConfigs.add(getDataConfig(algorithm, prop, BANK));
                    dataConfigs.add(getDataConfig(algorithm, prop, LETTER));
                    break;

                case multilayerperceptron:
                    dataConfigs.add(getDataConfig(algorithm, prop, BANK));
                    dataConfigs.add(getDataConfig(algorithm, prop, LETTER));
                    break;

                case knn:
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
        Config config = new Config();

        switch (algorithm) {
            case decistiontree:

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

                    dataConfig.setConfig(config);

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

                    dataConfig.setConfig(config);
                }

                break;

            case multilayerperceptron:
                if (BANK.equalsIgnoreCase(dataSet)) {

                    dataFile.setTrainingFile(prop.getString("mlp.bank.training.file"));
                    dataFile.setTestFile(prop.getString("mlp.bank.test.file"));

                    dataConfig.setDataFile(dataFile);

                } else if (LETTER.equalsIgnoreCase(dataSet)) {
                    dataFile.setTrainingFile(prop.getString("mlp.letter.training.file"));
                    dataFile.setTestFile(prop.getString("mlp.letter.test.file"));

                    dataConfig.setDataFile(dataFile);
                }

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
                dataConfig.setConfig(config);

                break;

            default:
                break;


        }

        return dataConfig;
    }
}
