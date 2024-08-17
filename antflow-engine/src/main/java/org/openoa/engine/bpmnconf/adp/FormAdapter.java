package org.openoa.engine.bpmnconf.adp;

import org.openoa.base.vo.BpmnStartConditionsVo;
import org.openoa.base.vo.BusinessDataVo;

/**
 * @Deprecated in favor of FormOperationAdaptor
 * @param <T>
 */
public interface FormAdapter<T extends BusinessDataVo> {

    /**

     * set preview Conditions
     *
     * @param vo vo
     */
    BpmnStartConditionsVo previewSetCondition(T vo);

    /**
     * init process's data
     *
     * @param vo vo
     * @return T
     */
    T initData(T vo);

    /**
     * process's launch parameters
     *
     * @param vo vo
     */
    BpmnStartConditionsVo launchParameters(T vo);

    /**
     * query business data for representation
     *
     * @param businessId businessId
     * @return business data
     */
    T queryData(Long businessId);

    /**
     * submit process data
     *
     * @param vo vo
     * @return T(the business data should be returned after submitting)
     */
    T submitData(T vo);

    /**
     * on approver's approvement
     *
     * @param vo vo
     * @return T(return business data)
     */
    T consentData(T vo);

    /**
     * back to modify data
     *
     * @param vo vo
     */
    void backToModifyData(T vo);

    /**
     * on process's abort
     *
     * @param businessId businessId
     */
    void cancellationData(Long businessId);

    /**
     * on the process is finished
     *
     * @param businessId businessId
     */
    void finishData(Long businessId);

}
