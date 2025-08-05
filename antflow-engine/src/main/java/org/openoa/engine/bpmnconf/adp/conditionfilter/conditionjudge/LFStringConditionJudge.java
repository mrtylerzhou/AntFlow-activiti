package org.openoa.engine.bpmnconf.adp.conditionfilter.conditionjudge;

import lombok.extern.slf4j.Slf4j;
import org.openoa.base.vo.BpmnNodeConditionsConfBaseVo;
import org.openoa.base.vo.BpmnStartConditionsVo;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class LFStringConditionJudge extends AbstractLFConditionJudge {
    @Override
    public boolean judge(String nodeId, BpmnNodeConditionsConfBaseVo conditionsConf, BpmnStartConditionsVo bpmnStartConditionsVo, int group) {
        return super.lfCommonJudge(conditionsConf,bpmnStartConditionsVo,(a,b,c)->a.toString().equalsIgnoreCase(b.toString()), group);
    }
}
