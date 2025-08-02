package org.openoa.engine.bpmnconf.adp.conditionfilter.conditionjudge;

import lombok.extern.slf4j.Slf4j;
import org.openoa.engine.bpmnconf.adp.conditionfilter.ConditionJudge;
import org.openoa.base.exception.AFBizException;

import java.math.BigDecimal;

/**
 * @Author TylerZhou
 * @Date 2024/6/22 20:22
 * @Version 0.5
 */
@Slf4j
public abstract class AbstractComparableJudge implements ConditionJudge {
    protected boolean compareJudge(BigDecimal confTotal, BigDecimal confTotal2, BigDecimal actual, Integer operator) {
        if (confTotal == null) {
            log.error("confTotal is null");
            return false;
        }
        if (actual == null) {
            log.error("operator right is null");
            return false;
        }
        if (operator == null) {
            throw new AFBizException("operator is null");
        }

        // -1 indicates that the first argument is less than the second.0 indicates that the arguments are equal.1 indicates that the first argument is greater than the second.
        boolean flag = false;
        switch (operator) {
            case 1:
                if (actual.compareTo(confTotal) >= 0) {
                    flag = true;
                }
                break;
            case 2:
                if (actual.compareTo(confTotal) > 0) {
                    flag = true;
                }
                break;
            case 3:
                if (actual.compareTo(confTotal) <= 0) {
                    flag = true;
                }
                break;
            case 4:
                if (actual.compareTo(confTotal) < 0) {
                    flag = true;
                }
                break;
            case 5:
                if (actual.compareTo(confTotal) == 0) {
                    flag = true;
                }
                break;
            case 6:
                if (actual.compareTo(confTotal) > 0 && actual.compareTo(confTotal2) < 0) {
                    flag = true;
                }
                break;
            case 7:
                if (actual.compareTo(confTotal) >= 0 && actual.compareTo(confTotal2) < 0) {
                    flag = true;
                }
                break;
            case 8:
                if (actual.compareTo(confTotal) > 0 && actual.compareTo(confTotal2) <= 0) {
                    flag = true;
                }
                break;
            case 9:
                if (actual.compareTo(confTotal) >= 0 && actual.compareTo(confTotal2) <= 0) {
                    flag = true;
                }
                break;
            default:
                throw new AFBizException("operator is not support at the moment yet");
        }
        return flag;
    }
}
