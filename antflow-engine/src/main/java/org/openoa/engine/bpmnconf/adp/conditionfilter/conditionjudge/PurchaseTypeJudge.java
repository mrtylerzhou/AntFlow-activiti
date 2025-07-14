package org.openoa.engine.bpmnconf.adp.conditionfilter.conditionjudge;

import lombok.extern.slf4j.Slf4j;
import org.openoa.base.exception.JiMuBizException;
import org.openoa.base.vo.BpmnNodeConditionsConfBaseVo;
import org.openoa.base.vo.BpmnStartConditionsVo;
import org.openoa.engine.bpmnconf.adp.conditionfilter.ConditionJudge;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

/**
 * for demo purpose only
 * @author AntFlow
 * @since 0.5
 */
@Service
@Slf4j
public class PurchaseTypeJudge implements ConditionJudge {

    @Override
    public boolean judge(String nodeId, BpmnNodeConditionsConfBaseVo conditionsConf, BpmnStartConditionsVo bpmnStartConditionsVo,int index,int group) {
        if (ObjectUtils.isEmpty(conditionsConf.getPurchaseType())) {
            throw new JiMuBizException("the process has no Purchase Type conf,please contact the administrator to add one");
        }
        if (ObjectUtils.isEmpty(bpmnStartConditionsVo.getPurchaseType())) {
            throw new JiMuBizException("the process has no Purchase Type  when start up,but it is a must,please contact the administrator");
        }
        return conditionsConf.getPurchaseType().equals(bpmnStartConditionsVo.getPurchaseType());
    }
}
