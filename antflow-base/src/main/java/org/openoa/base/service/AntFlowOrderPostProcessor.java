package org.openoa.base.service;

import org.openoa.base.adp.OrderedBean;

public interface AntFlowOrderPostProcessor<T> extends OrderedBean {
    void postProcess(T t);
}
