package org.openoa.engine.bpmnconf.adp.conditionfilter.conditionjudge;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.math.NumberUtils;
import org.openoa.base.vo.BpmnNodeConditionsConfBaseVo;
import org.openoa.base.vo.BpmnStartConditionsVo;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
@Slf4j
public class LFNumberFormatJudge extends AbstractLFConditionJudge{
    @Override
    public boolean judge(String nodeId, BpmnNodeConditionsConfBaseVo conditionsConf, BpmnStartConditionsVo bpmnStartConditionsVo,int index) {

       return super.lfCommonJudge(conditionsConf,bpmnStartConditionsVo,(a,b,c)->{
           String[] split = a.toString().split(",");
           BigDecimal valueinDbBig1 = new BigDecimal(split[0]);
           BigDecimal valueinDbBig2=null;
           if (split.length>1){
               valueinDbBig2 = new BigDecimal(split[1]);
           }
           BigDecimal userValue = NumberUtils.toScaledBigDecimal(b.toString(), 2, RoundingMode.HALF_UP);
           return super.compareJudge(valueinDbBig1,valueinDbBig2,userValue,c);
       },index);
    }

}
