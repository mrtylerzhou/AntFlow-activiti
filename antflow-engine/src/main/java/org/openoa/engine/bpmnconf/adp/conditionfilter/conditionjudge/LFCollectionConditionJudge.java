package org.openoa.engine.bpmnconf.adp.conditionfilter.conditionjudge;

import lombok.extern.slf4j.Slf4j;
import org.openoa.base.exception.JiMuBizException;
import org.openoa.base.vo.BpmnNodeConditionsConfBaseVo;
import org.openoa.base.vo.BpmnStartConditionsVo;
import org.openoa.engine.lowflow.entity.LFMainField;
import org.springframework.stereotype.Service;

import java.util.Iterator;
import java.util.function.BiPredicate;

@Service
@Slf4j
public class LFCollectionConditionJudge extends AbstractLFConditionJudge{
    @Override
    public boolean judge(String nodeId, BpmnNodeConditionsConfBaseVo conditionsConf, BpmnStartConditionsVo bpmnStartConditionsVo) {
        BiPredicate<Object,Object> predicate=(a,b)->{
          if(!(a instanceof Iterable)){
              throw new JiMuBizException("value from db is not iterable");
          }
            Iterable iterableValue = (Iterable)a;
            Iterator iterator = iterableValue.iterator();
            int sort=0;
            while (iterator.hasNext()){
                Object actualValue=iterator.next();
                if(actualValue.toString().equals(b.toString())){
                    return true ;
                }
            }
            return false;
        };
        return super.lfCommonJudge(conditionsConf,bpmnStartConditionsVo,predicate);
    }
}
