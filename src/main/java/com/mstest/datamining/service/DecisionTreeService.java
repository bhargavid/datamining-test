package com.mstest.datamining.service;

import java.util.Map;

/**
 * Created by bloganathan on 9/20/14.
 */
public interface DecisionTreeService {

    /**
     * Executes the decision tree algorithm
     *
     * @param params_map
     * @throws Exception
     */
    public void run(Map<String, Object> params_map) throws Exception;
}
