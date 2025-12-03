package org.openoa.engine.bpmnconf.adp.processoperation;

import org.openoa.base.interf.ActivitiService;
import org.openoa.base.interf.FormOperationAdaptor;
import org.openoa.base.vo.BusinessDataVo;

/**
 * 此抽象类主要是为了维护AntFlow内部的数个demo流程,用户创建DIY流程建议按照文档[1.antflow-activiti快速上手指南一]里面的快速开始一个DIY流程里介绍的方法来,即实现FormOperationAdaptor
 * 如果直接继承本类,确定你已经明白了(或者大概明白了)每个方法做什么的
 * @param <T>
 */
public abstract class AbstractFormOperationAdaptor<T extends BusinessDataVo>  implements FormOperationAdaptor<T>, ActivitiService {



    @Override
    public void initData(T vo) {

    }



    @Override
    public void queryData(T vo) {

    }

    @Override
    public void submitData(T vo) {

    }

    @Override
    public void consentData(T vo) {

    }

    @Override
    public void backToModifyData(T vo) {

    }

    @Override
    public void cancellationData(T businessDataVo) {

    }

    @Override
    public void onProcessRecover(BusinessDataVo businessData) {

    }
}
