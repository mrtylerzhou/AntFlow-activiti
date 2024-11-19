package org.openoa.engine.bpmnconf.adp.conditionfilter.conditionjudge;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.math.NumberUtils;
import org.openoa.base.vo.BpmnNodeConditionsConfBaseVo;
import org.openoa.base.vo.BpmnStartConditionsVo;
import org.springframework.stereotype.Service;

import java.math.RoundingMode;

@Service
@Slf4j
public class LFNumberFormatJudge extends AbstractLFConditionJudge{
    @Override
    public boolean judge(String nodeId, BpmnNodeConditionsConfBaseVo conditionsConf, BpmnStartConditionsVo bpmnStartConditionsVo) {
       return super.lfCommonJudge(conditionsConf,bpmnStartConditionsVo,(a,b)->super.compareJudge(NumberUtils.toScaledBigDecimal(a.toString(),2, RoundingMode.HALF_UP)
               ,NumberUtils.toScaledBigDecimal(a.toString(),2, RoundingMode.HALF_UP),conditionsConf.getNumberOperator()));
    }

}
