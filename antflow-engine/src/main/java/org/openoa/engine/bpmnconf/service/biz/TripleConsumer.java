package org.openoa.engine.bpmnconf.service.biz;

@FunctionalInterface
public interface TripleConsumer<T1,T2,T3> {
    void accept(T1 t1,T2 t2,T3 t3);
}
