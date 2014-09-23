package com.mstest.datamining.utils;

import com.mstest.datamining.model.Algorithm;
import com.mstest.datamining.model.Config;
import com.mstest.datamining.model.DataConfig;
import com.mstest.datamining.model.DataFile;
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


                default:
                    break;
            }

        } catch (ConfigurationException ce) {
            //TODO can we handle this better?
            ce.printStackTrace();
        }
    }

    private static DataConfig getDataConfig(Algorithm algorithm, Configuration prop, String dataSet) {
        DataConfig dataConfig = null;
        List<Config> configs = new ArrayList<Config>();
        DataFile dataFile = new DataFile();
        List<Object> configList = null;

        switch (algorithm) {
            case decistiontree:
                dataConfig = new DataConfig();

                if (BANK.equals(dataSet)) {
                    dataFile.setTestFile(prop.getString("dt.bank.test.file"));
                    dataFile.setTrainingFile(prop.getString("dt.bank.training.file"));

                    configList = prop.getList("dt.bank.minnumobj.confidencefactor");
                } else if (LETTER.equals(dataSet)) {
                    dataFile.setTestFile(prop.getString("dt.letter.test.file"));
                    dataFile.setTrainingFile(prop.getString("dt.letter.training.file"));

                    configList = prop.getList("dt.letter.minnumobj.confidencefactor");
                }
                for (Object bankConfig : configList) {
                    String[] tmpArr = ((String) bankConfig).split(":");

                    Config config = new Config();
                    config.setMinNumObj(Integer.valueOf(tmpArr[0]));
                    config.setConfidenceFactor(Float.valueOf(tmpArr[1]));

                    configs.add(config);
                }
                dataConfig.setDataFile(dataFile);
                dataConfig.setConfigList(configs);

                break;

            case multilayerperceptron:
                dataConfig = new DataConfig();
                if (BANK.equals(dataSet)) {
                    dataFile.setTestFile(prop.getString("mlp.bank.test.file"));
                    dataFile.setTrainingFile(prop.getString("mlp.bank.training.file"));
                } else if (LETTER.equals(dataSet)) {
                    dataFile.setTestFile(prop.getString("mlp.letter.test.file"));
                    dataFile.setTrainingFile(prop.getString("mlp.letter.training.file"));
                }

                dataConfig.setDataFile(dataFile);
                dataConfig.setConfigList(configs);

                break;
            default:
                break;
        }

        return dataConfig;
    }
}
