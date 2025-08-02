package org.openoa.engine.bpmnconf.adp.conditionfilter.conditionjudge;

import com.google.common.base.Strings;
import lombok.extern.slf4j.Slf4j;
import org.openoa.base.exception.AFBizException;
import org.openoa.base.vo.BpmnNodeConditionsConfBaseVo;
import org.openoa.base.vo.BpmnStartConditionsVo;
import org.openoa.engine.bpmnconf.service.TriplePredict;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import java.util.List;
import java.util.Map;

@Slf4j
public abstract class AbstractLFConditionJudge extends AbstractComparableJudge{

    protected boolean lfCommonJudge(BpmnNodeConditionsConfBaseVo conditionsConf, BpmnStartConditionsVo bpmnStartConditionsVo, TriplePredict<Object,Object,Integer> predicate, int currentGroup) {
        Map<Integer, Map<String, Object>> groupedLfConditionsMap = conditionsConf.getGroupedLfConditionsMap();
        Map<Integer, List<Integer>> groupedNumberOperatorListMap = conditionsConf.getGroupedNumberOperatorListMap();
        if(groupedLfConditionsMap==null){
            return false;
        }
        Map<String, Object> lfConditionsFromDb = groupedLfConditionsMap.get(currentGroup);
        Map<String, Object> lfConditionsFromUser = bpmnStartConditionsVo.getLfConditions();
        if (CollectionUtils.isEmpty(lfConditionsFromDb)) {

            throw new AFBizException("the process has no no code conditions conf,please contact the administrator to add one");
        }
        if (ObjectUtils.isEmpty(lfConditionsFromUser)) {
            return false;
        }
        boolean isMatch=true;
        int iterIndex=0;
        List<Integer> numberOperatorList =groupedNumberOperatorListMap.get(currentGroup);
        //operator type
        for (Map.Entry<String, Object> stringObjectEntry : lfConditionsFromDb.entrySet()) {

            String key = stringObjectEntry.getKey();
            Integer numberOperator = numberOperatorList.get(iterIndex);
            Object valueFromUser = lfConditionsFromUser.get(key);
            if(valueFromUser==null){
                log.error(Strings.lenientFormat("condition field from user %s can not be null",key));
                return false;
            }
            Object valueFromDb = stringObjectEntry.getValue();
            if(valueFromDb==null){
                throw new AFBizException(Strings.lenientFormat("condition field from db %s can not be null",key));
            }
            isMatch = predicate.test(valueFromDb, valueFromUser,numberOperator);
            iterIndex++;
            if(!isMatch){
                return false;
            }
        }
        return  isMatch;
    }
}
