package org.openoa.engine.bpmnconf.adp.conditionfilter.conditionjudge;

import com.google.common.base.Strings;
import org.openoa.base.exception.JiMuBizException;
import org.openoa.base.vo.BpmnNodeConditionsConfBaseVo;
import org.openoa.base.vo.BpmnStartConditionsVo;
import org.openoa.engine.bpmnconf.service.biz.TripleConsumer;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import java.util.Map;
import java.util.function.BiPredicate;

public abstract class AbstractLFConditionJudge extends AbstractComparableJudge{

    protected boolean lfCommonJudge(BpmnNodeConditionsConfBaseVo conditionsConf, BpmnStartConditionsVo bpmnStartConditionsVo, BiPredicate<Object,Object> predicate) {
        Map<String, Object> lfConditionsFromDb = conditionsConf.getLfConditions();
        Map<String, Object> lfConditionsFromUser = bpmnStartConditionsVo.getLfConditions();
        if (CollectionUtils.isEmpty(lfConditionsFromDb)) {

            throw new JiMuBizException("the process has no no code conditions conf,please contact the administrator to add one");
        }
        if (ObjectUtils.isEmpty(lfConditionsFromUser)) {
            throw new JiMuBizException("the process has no no code form,please contact the administrator");
        }
        boolean isMatch=false;
        //operator type
        for (Map.Entry<String, Object> stringObjectEntry : lfConditionsFromDb.entrySet()) {
            String key = stringObjectEntry.getKey();
            Object valueFromUser = lfConditionsFromUser.get(key);
            if(valueFromUser==null){
                throw new JiMuBizException(Strings.lenientFormat("condition field from user %s can not be null",key));
            }
            Object valueFromDb = stringObjectEntry.getValue();
            if(valueFromDb==null){
                throw new JiMuBizException(Strings.lenientFormat("condition field from db %s can not be null",key));
            }
            isMatch= predicate.test(valueFromDb,valueFromUser);
        }
        return  isMatch;
    }
}
