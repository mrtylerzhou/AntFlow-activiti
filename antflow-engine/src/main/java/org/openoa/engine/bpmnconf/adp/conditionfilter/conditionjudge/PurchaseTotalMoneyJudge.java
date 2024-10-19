package org.openoa.engine.bpmnconf.adp.conditionfilter.conditionjudge;

import com.google.common.base.Strings;
import lombok.extern.slf4j.Slf4j;
import org.openoa.base.exception.JiMuBizException;
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
public class PurchaseTotalMoneyJudge extends AbstractComparableJudge {
    @Override
    public boolean judge(String nodeId, BpmnNodeConditionsConfBaseVo conditionsConf, BpmnStartConditionsVo bpmnStartConditionsVo) {
        if (conditionsConf.getPlanProcurementTotalMoney()==null ||bpmnStartConditionsVo.getPlanProcurementTotalMoney()==null) {

            log.info("process's Plan Procurement Total money is empty");
            throw new JiMuBizException("999", "process's Plan Procurement Total is empty");
        }
        BigDecimal purchaseInDb = BigDecimal.valueOf(conditionsConf.getPlanProcurementTotalMoney());
        BigDecimal purchaseActual = BigDecimal.valueOf(bpmnStartConditionsVo.getPlanProcurementTotalMoney());

        //operator type
        Integer theOperatorType = conditionsConf.getNumberOperator();
        return super.compareJudge(purchaseInDb,purchaseActual,theOperatorType);
    }
}
