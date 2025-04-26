package org.openoa.engine.bpmnconf.adp.conditionfilter.conditionjudge;


import lombok.extern.slf4j.Slf4j;
import org.openoa.base.vo.BpmnNodeConditionsConfBaseVo;
import org.openoa.base.vo.BpmnStartConditionsVo;
import org.openoa.engine.bpmnconf.adp.conditionfilter.ConditionJudge;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

/**
 * @author AntFlow
 * @since 0.5
 */
@Slf4j
@Component
public class BpmnTemplateMarkJudge implements ConditionJudge {
    @Override
    public boolean judge(String nodeId, BpmnNodeConditionsConfBaseVo conditionsConf, BpmnStartConditionsVo bpmnStartConditionsVo,int index) {
        if (!CollectionUtils.isEmpty(conditionsConf.getTemplateMarks()) &&
                CollectionUtils.containsAny(conditionsConf.getTemplateMarks(),bpmnStartConditionsVo.getTemplateMarkIds())
        ) {
            return true;
        }
        return false;
    }
}
