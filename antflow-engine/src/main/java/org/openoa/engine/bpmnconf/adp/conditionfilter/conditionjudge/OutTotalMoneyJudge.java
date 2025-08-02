package org.openoa.engine.bpmnconf.adp.conditionfilter.conditionjudge;

import com.google.common.base.Strings;
import lombok.extern.slf4j.Slf4j;
import org.openoa.base.exception.AFBizException;
import org.openoa.base.vo.BpmnNodeConditionsConfBaseVo;
import org.openoa.base.vo.BpmnStartConditionsVo;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

/**
 * total money judge, compare the total money of process\
 * it you have business logic to judge related to compare two numbers,you can refer to this class to write you own one
 */
@Service
@Slf4j
public class OutTotalMoneyJudge extends AbstractComparableJudge {
    @Override
    public boolean judge(String nodeId, BpmnNodeConditionsConfBaseVo conditionsConf, BpmnStartConditionsVo bpmnStartConditionsVo, int group) {
        if (Strings.isNullOrEmpty(conditionsConf.getOutTotalMoney()) ||Strings.isNullOrEmpty(bpmnStartConditionsVo.getOutTotalMoney())) {

            log.info("process's out total money is empty");
            throw new AFBizException("999", "process's out total money is empty");
        }
        BigDecimal outTotalMoney = new BigDecimal(conditionsConf.getOutTotalMoney());
        BigDecimal total = new BigDecimal(bpmnStartConditionsVo.getOutTotalMoney());

        //operator type
        Integer theOperatorType = conditionsConf.getNumberOperator();
        return super.compareJudge(outTotalMoney,null,total,theOperatorType);
    }
}
