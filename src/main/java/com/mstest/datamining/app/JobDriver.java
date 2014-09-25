package com.mstest.datamining.app;

import com.mstest.datamining.model.Algorithm;
import com.mstest.datamining.service.AdaBoostService;
import com.mstest.datamining.service.DecisionTreeService;
import com.mstest.datamining.service.KnnService;
import com.mstest.datamining.service.MLPService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import sun.security.x509.AlgIdDSA;

import java.util.List;
import java.util.Map;

/**
 * Created by bloganathan on 9/20/14.
 */
public class JobDriver {
    @Autowired
    @Qualifier("dtService")
    DecisionTreeService dtService;

    @Autowired
    @Qualifier("mlpService")
    MLPService mlpService;

    @Autowired
    @Qualifier("knnService")
    KnnService knnService;

    @Autowired
    @Qualifier("adaBoostService")
    AdaBoostService adaBoostService;

    public void processJob(List<Algorithm> algorithms, Map<String, Object> params_map) {
        try {
            for(Algorithm algorithm: algorithms) {
                if (Algorithm.decistiontree.equals(algorithm))
                    dtService.run(params_map);

                if(Algorithm.multilayerperceptron.equals(algorithm))
                    mlpService.run(params_map);

                if(Algorithm.knn.equals(algorithm))
                    knnService.run(params_map);

                if(Algorithm.adaboost.equals(algorithm))
                    adaBoostService.run(params_map);
            }
        } catch (Exception e) {
            //TODO handle exceptions properly
            e.printStackTrace();
        }
    }
}
