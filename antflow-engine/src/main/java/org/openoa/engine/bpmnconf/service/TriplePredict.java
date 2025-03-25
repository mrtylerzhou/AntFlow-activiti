package org.openoa.engine.bpmnconf.service;

import java.util.function.BiPredicate;

@FunctionalInterface
public interface TriplePredict<T, U,V> {
    boolean test(T t, U u,V v);
}
