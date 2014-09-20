package com.mstest.datamining.app;

import com.mstest.datamining.service.DecisionTreeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.List;

/**
 * Created by bloganathan on 9/20/14.
 */
public class JobDriver {
    @Autowired
    @Qualifier("dtService")
    DecisionTreeService dtService;

    public void processJob(List<Job> jobs) {
        try {
            for(Job job: jobs) {
                if (Job.decisiontrees.equals(job))
                    dtService.run();
            }
        } catch (Exception e) {
            //TODO handle exceptions properly
            e.printStackTrace();
        }
    }
}
