package org.openoa.engine.bpmnconf.adp.conditionfilter.conditionjudge;

import org.openoa.engine.bpmnconf.adp.conditionfilter.ConditionJudge;
import org.openoa.base.exception.JiMuBizException;

import java.math.BigDecimal;

/**
 * @Author TylerZhou
 * @Date 2024/6/22 20:22
 * @Version 0.5
 */
public abstract class AbstractComparableJudge extends ConditionJudge {
    protected boolean compareJudge(BigDecimal confTotal,BigDecimal actual,Integer operator){
        if(confTotal==null){
            throw new JiMuBizException("operator left is null");
        }
        if(actual==null){
            throw new JiMuBizException("operator right is null");
        }

        // -1 indicates that the first argument is less than the second.0 indicates that the arguments are equal.1 indicates that the first argument is greater than the second.
        boolean falg = false;
        switch (operator) {
            case 1:
                if (actual.compareTo(confTotal) >= 0) {
                    falg = true;
                }
                break;
            case 2:
                if (actual.compareTo(confTotal) > 0) {
                    falg = true;
                }
                break;
            case 3:
                if (actual.compareTo(confTotal) <= 0) {
                    falg = true;
                }
                break;
            case 4:
                if (actual.compareTo(confTotal) < 0) {
                    falg = true;
                }
                break;
            case 5:
                if (actual.compareTo(confTotal) == 0) {
                    falg = true;
                }
                break;
        }
        return falg;
    }
}
