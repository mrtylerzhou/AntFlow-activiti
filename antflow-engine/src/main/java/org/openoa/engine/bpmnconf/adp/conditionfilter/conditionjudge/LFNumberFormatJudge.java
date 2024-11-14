package org.openoa.engine.bpmnconf.adp.conditionfilter.conditionjudge;

import com.google.common.base.Strings;
import lombok.extern.slf4j.Slf4j;
import org.openoa.base.exception.JiMuBizException;
import org.openoa.base.vo.BpmnNodeConditionsConfBaseVo;
import org.openoa.base.vo.BpmnStartConditionsVo;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import java.math.BigDecimal;

@Service
@Slf4j
public class LFNumberFormatJudge extends AbstractLFConditionJudge{
    @Override
    public boolean judge(String nodeId, BpmnNodeConditionsConfBaseVo conditionsConf, BpmnStartConditionsVo bpmnStartConditionsVo) {
       return super.lfCommonJudge(conditionsConf,bpmnStartConditionsVo,(a,b)->super.compareJudge(new BigDecimal(a.toString()),new BigDecimal(b.toString()),conditionsConf.getNumberOperator()));
    }

}
