package org.openoa.engine.bpmnconf.adp.personneladp;

import org.openoa.base.exception.AFBizException;
import org.openoa.base.interf.AdaptorService;
import org.openoa.base.vo.BpmnNodeParamsAssigneeVo;
import org.openoa.base.vo.BpmnNodeVo;
import org.openoa.base.vo.BpmnStartConditionsVo;

import java.util.List;

public abstract class AbstractBusinessConfigurationAdaptor implements AdaptorService {

    /**
     * 业务表和字段对应找人方法
     *
     * @param bpmnNodeVo          当前节点
     * @param bpmnStartConditions 条件
     */
    public abstract List<BpmnNodeParamsAssigneeVo> doFindBusinessPerson(BpmnNodeVo bpmnNodeVo, BpmnStartConditionsVo bpmnStartConditions);
    protected void paramValidated(BpmnStartConditionsVo conditionsVo) {
        if(conditionsVo==null){
            throw new AFBizException("process has no start conditions");
        }

    }
}
