package org.openoa.engine.bpmnconf.adp.conditionfilter.conditionjudge;

import com.google.common.base.Strings;
import lombok.extern.slf4j.Slf4j;
import org.openoa.base.exception.JiMuBizException;
import org.openoa.base.vo.BpmnNodeConditionsConfBaseVo;
import org.openoa.base.vo.BpmnStartConditionsVo;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
@Service
@Slf4j
public class AskLeaveJudge extends AbstractComparableJudge{
    @Override
    public boolean judge(String nodeId, BpmnNodeConditionsConfBaseVo conditionsConf, BpmnStartConditionsVo bpmnStartConditionsVo) {
        if (conditionsConf.getLeaveHour()==null ||bpmnStartConditionsVo.getLeaveHour()==null) {

            log.info("process's total money is empty");
            throw new JiMuBizException("999", "process's total money is empty");
        }
        BigDecimal leaveHourInDb = BigDecimal.valueOf(conditionsConf.getLeaveHour());
        BigDecimal leaveHourActual = BigDecimal.valueOf(bpmnStartConditionsVo.getLeaveHour());

        //operator type
        Integer theOperatorType = conditionsConf.getNumberOperator();
        return super.compareJudge(leaveHourInDb,leaveHourActual,theOperatorType);
    }
}
