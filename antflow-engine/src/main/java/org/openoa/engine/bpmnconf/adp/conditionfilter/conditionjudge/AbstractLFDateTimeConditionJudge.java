package org.openoa.engine.bpmnconf.adp.conditionfilter.conditionjudge;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.FastDateFormat;
import org.openoa.base.vo.BpmnNodeConditionsConfBaseVo;
import org.openoa.base.vo.BpmnStartConditionsVo;
import org.openoa.engine.bpmnconf.service.TriplePredict;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.Date;

@Slf4j
public abstract class AbstractLFDateTimeConditionJudge extends AbstractLFConditionJudge{

    @Override
    public boolean judge(String nodeId, BpmnNodeConditionsConfBaseVo conditionsConf, BpmnStartConditionsVo bpmnStartConditionsVo, int group) {
        TriplePredict<Object,Object,Integer> predicate=(a, b,c)->{
            try {
                String[] split = a.toString().split(",");
                Date dateFromDb1 =currentDateFormatter().parse(split[0]);
                Date dateFromDb2 = null;
                if(split.length>1){
                    dateFromDb2=currentDateFormatter().parse(split[1]);
                }
                Date dateFromUser = currentDateFormatter().parse(b.toString());
                BigDecimal dateFromDbBig1=new BigDecimal(dateFromDb1.getTime());
                BigDecimal dateFromDbBig2=dateFromDb2==null?null:new BigDecimal(dateFromDb2.getTime());
                BigDecimal dateFromUserBig=new BigDecimal(dateFromUser.getTime());
                return super.compareJudge(dateFromDbBig1,dateFromDbBig2,dateFromUserBig,c);
            } catch (ParseException e) {
                log.error("date parse exception while condition judging");
                throw new RuntimeException(e);
            }
        };
        return super.lfCommonJudge(conditionsConf,bpmnStartConditionsVo,predicate, group);
    }
    abstract protected FastDateFormat currentDateFormatter();
}
