package org.openoa.engine.bpmnconf.service.biz;

import org.openoa.base.vo.BpmnConfVo;
import org.openoa.base.vo.BpmnStartConditionsVo;
import org.openoa.engine.bpmnconf.adp.formatter.BpmnStartFormat;
import org.openoa.common.service.ConditionFilterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class BpmnStartFormatImpl implements BpmnStartFormat {

    @Autowired
    ConditionFilterService conditionFilterService;

    @Override
    public void formatBpmnConf(BpmnConfVo bpmnConfVo, BpmnStartConditionsVo bpmnStartConditions) {
         conditionFilterService.conditionfilterNode(bpmnConfVo, bpmnStartConditions);
    }
}
