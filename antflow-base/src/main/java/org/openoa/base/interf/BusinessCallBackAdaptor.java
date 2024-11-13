package org.openoa.base.interf;

public interface BusinessCallBackAdaptor<R,P> {
    void doCallBack(P param);
    R formattedValue(P value);
}
