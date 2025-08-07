package org.openoa.engine.bpmnconf.service;

@FunctionalInterface
public interface TriplePredict<T, U,V> {
    boolean test(T t, U u,V v);
}
