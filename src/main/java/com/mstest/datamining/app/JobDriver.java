package com.mstest.datamining.app;

import com.mstest.datamining.model.Algorithm;
import com.mstest.datamining.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.List;
import java.util.Map;

/**
 * Created by bdamodaran on 9/20/14.
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

    /*@Autowired
    @Qualifier("svmService")
    SvmService svmService;
*/
    @Autowired
    @Qualifier("assn3Service")
    Assignment3 assn3Service;

    public void processJob(List<Algorithm> algorithms, Map<String, Object> params_map) {
        try {
            for(Algorithm algorithm: algorithms) {
                if (Algorithm.j48.equals(algorithm))
                    dtService.run(params_map);

                if(Algorithm.multilayerperceptron.equals(algorithm))
                    mlpService.run(params_map);

                if(Algorithm.knn.equals(algorithm))
                    knnService.run(params_map);

                if(Algorithm.adaboost.equals(algorithm))
                    adaBoostService.run(params_map);

//                if(Algorithm.svm.equals(algorithm))
//                    svmService.run(params_map);

                if(Algorithm.assn3.equals(algorithm))
                    assn3Service.execute(1, "/tmp/clustering/k");

                if(Algorithm.seed.equals(algorithm))
                    assn3Service.execute(2, "/tmp/clustering/seed");
            }
        } catch (Exception e) {
            //TODO handle exceptions properly
            e.printStackTrace();
        }
    }
}
