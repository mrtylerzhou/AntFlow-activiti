package org.openoa.base.service;

import org.openoa.base.adp.OrderedBean;

public interface AntFlowOrderPreProcessor<T> extends OrderedBean {
    void preWriteProcess(T t);
    void preReadProcess(T t);
}
