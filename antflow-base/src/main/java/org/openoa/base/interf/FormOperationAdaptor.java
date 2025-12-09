package org.openoa.base.interf;

import org.openoa.base.util.SpringBeanUtils;
import org.openoa.base.vo.BpmnStartConditionsVo;
import org.openoa.base.vo.BusinessDataVo;

import java.util.HashMap;
import java.util.Map;

/**
 * FormOperationAdaptor is the core interface to adapt different business data to a process
 * @param <T>
 */
public interface FormOperationAdaptor<T extends BusinessDataVo> extends ProcessFinishListener, ActivitiService {

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
    void initData(T vo);

    /**
     * process's launch parameters,most of the time,it is a must method,even  rough you do not have any start conditions,you should always define a new empty one
     *
     * @param vo vo
     */
    BpmnStartConditionsVo launchParameters(T vo);


    /**
     * 自动条件,用户可以在页面设置条件,也可以完全通过此方法自定义条件
     * @param businessDataVo
     * @return
     */
    Boolean automaticCondition(T businessDataVo);

    /**
     * 执行的动作,可以执行预定制的动作,也可以自己定义一个或者多个动作来执行
     * @param businessDataVo
     * @param conditionResult 结果来自于 automaticCondition,如果需要无条件执行一个动作,automaticCondition返回null即可
     */
    void  automaticAction(T businessDataVo,Boolean conditionResult);
    /**
     * query business data,most of the times,it is a must method,it is used to query business data for a process for approvers reference
     *
     * @param vo businessId the business id for a specified process,it will passed by antflow engine,and you should use it to query business data
     * @return business data
     */
    void queryData(T vo);

    /**
     * a method to submit data for a process,most of the times,it is a must method,it is used to submit business data for a process
     * ⚠️⚠️⚠️ 入参和出参类型一致,返回对象时一定要返回原对象(参考第三方账号申请,将新属性赋值给原对象)
     * @param vo vo
     * @return T(business data)
     */
    void submitData(T vo);

    /**
     * when an approver submit his/her approve,it will call this method to do some business logic,it is not a must method most of the times
     *
     * @param vo vo
     * @return T(business data)
     */
    void consentData(T vo);

    /**
     * this method is called when a process is back to modify,it is not a must method most of the times
     *
     * @param vo vo
     */
    void backToModifyData(T vo);

    /**
     * when a process is cancelled,it will call this method to do some business logic,it is a must method,usually,it is used to invalid the business data
     *
     * @param businessDataVo businessDataVo
     */
    void cancellationData(T businessDataVo);

    //流程完结以后(正常完成,被发起人拒绝,终止以后重新恢复
    void onProcessRecover(BusinessDataVo businessData);

   default Map<String,FormOperationAdaptor> getNoneLFFormOperationAdaptors(){
       Map<String, FormOperationAdaptor> beansOfType = SpringBeanUtils.getBeansOfType(FormOperationAdaptor.class);
       Map<String, FormOperationAdaptor> resultMap=new HashMap<>();
       for (String beanName : beansOfType.keySet()) {
           FormOperationAdaptor formOperationAdaptor = beansOfType.get(beanName);
           if(formOperationAdaptor instanceof LFFormOperationAdaptor){
               continue;
           }
           resultMap.put(beanName,formOperationAdaptor);
       }
       return resultMap;
   }
}
