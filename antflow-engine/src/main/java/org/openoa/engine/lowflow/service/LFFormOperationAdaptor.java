package org.openoa.engine.lowflow.service;

import org.openoa.engine.lowflow.vo.UDLFApplyVo;

/**
 * 此类主要用于用特定低代码表流程的一些自定义行为
 * 如果你熟悉LFFormOperationAdaptor
 * @param <T>
 */
public interface LFFormOperationAdaptor<T extends UDLFApplyVo> {

    void previewSetCondition(T vo);


    void initData(T vo);


   void launchParameters(T vo);


    void queryData(T vo);


    void submitData(T vo);


    void consentData(T vo);


    void backToModifyData(T vo);


    void cancellationData(T vo);

    void finishData(T vo);

}
