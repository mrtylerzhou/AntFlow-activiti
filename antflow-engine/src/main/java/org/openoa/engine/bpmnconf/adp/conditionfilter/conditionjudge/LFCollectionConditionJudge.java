package org.openoa.engine.bpmnconf.adp.conditionfilter.conditionjudge;

import lombok.extern.slf4j.Slf4j;
import org.openoa.base.exception.JiMuBizException;
import org.openoa.base.vo.BpmnNodeConditionsConfBaseVo;
import org.openoa.base.vo.BpmnStartConditionsVo;
import org.openoa.engine.bpmnconf.service.TriplePredict;
import org.springframework.stereotype.Service;

import java.util.Iterator;

@Service
@Slf4j
public class LFCollectionConditionJudge extends AbstractLFConditionJudge{
    @Override
    public boolean judge(String nodeId, BpmnNodeConditionsConfBaseVo conditionsConf, BpmnStartConditionsVo bpmnStartConditionsVo, int group) {
        //a是数据库里存的集合,b是用户传过来的集合(或者单个值),遍历a,b,如果b在a里,则返回true
        TriplePredict<Object,Object,Integer> predicate=(a, b,c)->{
          if(!(a instanceof Iterable)){
              throw new JiMuBizException("value from db is not iterable");
          }
            Iterable iterableValue = (Iterable)a;
            Iterator iterator = iterableValue.iterator();
            int sort=0;
            while (iterator.hasNext()){
                Object actualValue=iterator.next();
                if (b instanceof Iterable) {
                    Iterable iterableB = (Iterable) b;
                    Iterator iteratorB = iterableB.iterator();
                    while (iteratorB.hasNext()) {
                        Object bValue = iteratorB.next();
                        if (actualValue.toString().equals(bValue.toString())) {
                            return true;
                        }
                    }
                } else {
                    if (actualValue.toString().equals(b.toString())) {
                        return true;
                    }
                }
            }
            return false;
        };
        return super.lfCommonJudge(conditionsConf,bpmnStartConditionsVo,predicate, group);
    }
}
