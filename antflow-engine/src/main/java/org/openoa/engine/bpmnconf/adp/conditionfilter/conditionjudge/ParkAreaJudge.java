package org.openoa.engine.bpmnconf.adp.conditionfilter.conditionjudge;

import org.openoa.base.vo.BpmnNodeConditionsConfBaseVo;
import org.openoa.base.vo.BpmnStartConditionsVo;

import java.math.BigDecimal;

/**
 * it is a demo condition judge class,you can refer to it to write your only conditions judge logic
 * @Author TylerZhou
 * @Date 2024/6/22 20:47
 * @Version 0.5
 */
public class ParkAreaJudge extends AbstractComparableJudge{
    @Override
    public boolean judge(String nodeId, BpmnNodeConditionsConfBaseVo conditionsConf, BpmnStartConditionsVo bpmnStartConditionsVo) {
        BigDecimal totalArea = BigDecimal.valueOf(conditionsConf.getParkArea());
        //mock
        BigDecimal total = new BigDecimal(bpmnStartConditionsVo.getTotalMoney());

        //运算符类型
        Integer theOperatorType = conditionsConf.getNumberOperator();
        return super.compareJudge(total,totalArea,theOperatorType);
    }
}
