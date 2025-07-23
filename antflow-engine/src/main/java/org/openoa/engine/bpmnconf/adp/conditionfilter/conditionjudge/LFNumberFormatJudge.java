package org.openoa.engine.bpmnconf.adp.conditionfilter.conditionjudge;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.math.NumberUtils;
import org.openoa.base.vo.BpmnNodeConditionsConfBaseVo;
import org.openoa.base.vo.BpmnStartConditionsVo;
import org.springframework.stereotype.Service;

import javax.lang.model.util.ElementScanner6;
import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
@Slf4j
public class LFNumberFormatJudge extends AbstractLFConditionJudge{
    @Override
    public boolean judge(String nodeId, BpmnNodeConditionsConfBaseVo conditionsConf, BpmnStartConditionsVo bpmnStartConditionsVo,int index,int group) {

       return super.lfCommonJudge(conditionsConf,bpmnStartConditionsVo,(a,b,c)->{
           String[] split = a.toString().split(",");
           boolean isBooleanValue=false;
           BigDecimal valueinDbBig1 = null;
           try {
               valueinDbBig1=new BigDecimal(split[0]);
           }catch (Exception e){
               boolean parsedBoolean = Boolean.parseBoolean(split[0]);
               valueinDbBig1=parsedBoolean?new BigDecimal("1"):new BigDecimal("0");
               isBooleanValue=true;
           }
           BigDecimal valueinDbBig2=null;
           if (split.length>1){
               if(isBooleanValue){
                   boolean parsedBoolean = Boolean.parseBoolean(split[1]);
                   valueinDbBig2=parsedBoolean?new BigDecimal("1"):new BigDecimal("0");
               }else{
                   valueinDbBig2=new BigDecimal(split[1]);
               }
           }
           BigDecimal userValue =null;
           if(isBooleanValue){
               boolean parsedBoolean = Boolean.parseBoolean(b.toString());
               userValue=parsedBoolean?new BigDecimal("1"):new BigDecimal("0");
           }else{
               userValue= NumberUtils.toScaledBigDecimal(b.toString(), 2, RoundingMode.HALF_UP);;
           }
           return super.compareJudge(valueinDbBig1,valueinDbBig2,userValue,c);
       },index,group);
    }

}
