package org.openoa.base.interf;

import org.openoa.base.vo.BpmnStartConditionsVo;
import org.openoa.base.vo.BusinessDataVo;

/**
 * FormOperationAdaptor is the core interface to adapt different business data to a process
 * @param <T>
 */
public interface FormOperationAdaptor<T extends BusinessDataVo> extends ActivitiService {

    /**
     * set for preview condition,it is not a must method,but most of the time it is needed for preview a process
     *
     * @param vo vo
     */
    BpmnStartConditionsVo previewSetCondition(T vo);

    /**
     * some init data for a process,most of the time it is not a must method
     *
     * @param vo vo
     * @return T
     */
    T initData(T vo);

    /**
     * process's launch parameters,most of the time,it is a must method,even  rough you do not have any start conditions,you should always define a new empty one
     *
     * @param vo vo
     */
    BpmnStartConditionsVo launchParameters(T vo);

    /**
     * query business data,most of the times,it is a must method,it is used to query business data for a process for approvers reference
     *
     * @param businessId businessId the business id for a specified process,it will passed by antflow engine,and you should use it to query business data
     * @return business data
     */
    T queryData(String businessId);

    /**
     * a method to submit data for a process,most of the times,it is a must method,it is used to submit business data for a process
     *
     * @param vo vo
     * @return T(business data)
     */
    T submitData(T vo);

    /**
     * when an approver submit his/her approve,it will call this method to do some business logic,it is not a must method most of the times
     *
     * @param vo vo
     * @return T(business data)
     */
    T consentData(T vo);

    /**
     * this method is called when a process is back to modify,it is not a must method most of the times
     *
     * @param vo vo
     */
    void backToModifyData(T vo);

    /**
     * when a process is cancelled,it will call this method to do some business logic,it is a must method,usually,it is used to invalid the business data
     *
     * @param businessId businessId
     */
    void cancellationData(String businessId);

    /**
     * this method is called when a process is finished,it is a must method usually
     *
     * @param businessId businessId
     */
    void finishData(String businessId);

}
