package org.openoa.engine.bpmnconf.adp.conditionfilter.conditionjudge;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * total money judge, compare the total money of process\
 * it you have business logic to judge related to compare two numbers,you can refer to this class to write you own one
 */
@Service
@Slf4j
public class PurchaseTotalMoneyJudge extends AbstractBinaryComparableJudge {

    @Override
    protected String fieldNameInDb() {
        return "planProcurementTotalMoney";
    }

    @Override
    protected String fieldNameInStartConditions() {
        return "planProcurementTotalMoney";
    }
}
