package org.openoa.engine.bpmnconf.adp.conditionfilter.conditionjudge;

import lombok.extern.slf4j.Slf4j;
import org.openoa.engine.bpmnconf.adp.conditionfilter.ConditionJudge;
import org.openoa.base.vo.BpmnNodeConditionsConfBaseVo;
import org.openoa.base.vo.BpmnStartConditionsVo;
import org.openoa.base.exception.JiMuBizException;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

/**
 * for demo purpose only
 * @author AntFlow
 * @since 0.5
 */
@Service
@Slf4j
public class ThirdAccountJudge implements ConditionJudge {

    @Override
    public boolean judge(String nodeId, BpmnNodeConditionsConfBaseVo conditionsConf, BpmnStartConditionsVo bpmnStartConditionsVo, int group) {
        if (ObjectUtils.isEmpty(conditionsConf.getAccountType())) {
            throw new JiMuBizException("the process has no third party account conf,please contact the administrator to add one");
        }
        if (ObjectUtils.isEmpty(bpmnStartConditionsVo.getAccountType())) {
            return false;
        }
        return conditionsConf.getAccountType().contains(bpmnStartConditionsVo.getAccountType());
    }
}
