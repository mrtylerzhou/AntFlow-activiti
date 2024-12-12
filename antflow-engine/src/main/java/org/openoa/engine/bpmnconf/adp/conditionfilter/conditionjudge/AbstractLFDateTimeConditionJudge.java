package org.openoa.engine.bpmnconf.adp.conditionfilter.conditionjudge;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.FastDateFormat;
import org.openoa.base.vo.BpmnNodeConditionsConfBaseVo;
import org.openoa.base.vo.BpmnStartConditionsVo;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.Date;
import java.util.function.BiPredicate;

@Slf4j
public abstract class AbstractLFDateTimeConditionJudge extends AbstractLFConditionJudge{

    @Override
    public boolean judge(String nodeId, BpmnNodeConditionsConfBaseVo conditionsConf, BpmnStartConditionsVo bpmnStartConditionsVo) {
        BiPredicate<Object,Object> predicate=(a,b)->{
            try {
                Date dateFromDb =(Date) a;
                Date dateFromUser = currentDateFormatter().parse(b.toString());
                BigDecimal dateFromDbBig=new BigDecimal(dateFromDb.getTime());
                BigDecimal dateFromUserBig=new BigDecimal(dateFromUser.getTime());
                Integer numberOperator = conditionsConf.getNumberOperator();
                return super.compareJudge(dateFromDbBig,dateFromUserBig,numberOperator);
            } catch (ParseException e) {
                log.error("date parse exception while condition judging");
                throw new RuntimeException(e);
            }
        };
        return super.lfCommonJudge(conditionsConf,bpmnStartConditionsVo,predicate);
    }
    abstract protected FastDateFormat currentDateFormatter();
}
