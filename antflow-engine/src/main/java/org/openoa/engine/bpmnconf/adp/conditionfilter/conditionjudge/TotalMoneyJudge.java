package org.openoa.engine.bpmnconf.adp.conditionfilter.conditionjudge;

import com.google.common.base.Strings;
import lombok.extern.slf4j.Slf4j;
import org.openoa.base.vo.BpmnNodeConditionsConfBaseVo;
import org.openoa.base.vo.BpmnStartConditionsVo;
import org.openoa.base.exception.JiMuBizException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

/**
 * total money judge, compare the total money of process\
 * it you have business logic to judge related to compare two numbers,you can refer to this class to write you own one
 */
@Service
@Slf4j
public class TotalMoneyJudge extends AbstractComparableJudge {
    @Override
    public boolean judge(String nodeId, BpmnNodeConditionsConfBaseVo conditionsConf, BpmnStartConditionsVo bpmnStartConditionsVo) {
        if (Strings.isNullOrEmpty(conditionsConf.getTotalMoney()) ||Strings.isNullOrEmpty(bpmnStartConditionsVo.getTotalMoney())) {

            log.info("process's total money is empty");
            throw new JiMuBizException("999", "process's total money is empty");
        }
        BigDecimal totalMoney = new BigDecimal(conditionsConf.getTotalMoney());
        BigDecimal total = new BigDecimal(bpmnStartConditionsVo.getTotalMoney());

        //operator type
        Integer theOperatorType = conditionsConf.getNumberOperator();
        return super.compareJudge(totalMoney,total,theOperatorType);
    }
}
