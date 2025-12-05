package org.openoa.base.dto;

import org.activiti.engine.impl.pvm.process.ActivityImpl;

public class ParallelPair {
    public ActivityImpl fork;
    public ActivityImpl join;

    public ParallelPair(ActivityImpl fork, ActivityImpl join) {
        this.fork = fork;
        this.join = join;
    }
}
