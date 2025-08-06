package org.openoa.engine.bpmnconf.adp.conditionfilter.conditionjudge;

import lombok.extern.slf4j.Slf4j;
import org.openoa.engine.bpmnconf.adp.conditionfilter.ConditionJudge;
import org.openoa.base.vo.BaseIdTranStruVo;
import org.openoa.base.vo.BpmnNodeConditionsConfBaseVo;
import org.openoa.base.vo.BpmnStartConditionsVo;
import org.openoa.base.exception.AFBizException;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.Objects;

/**
 *@Author JimuOffice
 * @Description job level judge
 * @Date 2022-04-30
 * @Param
 * @return
 * @Version 0.5
 */
@Service
@Slf4j
public class JobLevelJudge implements ConditionJudge {
    @Override
    public boolean judge(String nodeId, BpmnNodeConditionsConfBaseVo conditionsConf, BpmnStartConditionsVo bpmnStartConditionsVo, int group) {
        BaseIdTranStruVo voFromDb = conditionsConf.getJobLevelVo();
        BaseIdTranStruVo voFromUser = bpmnStartConditionsVo.getJobLevelVo();
        if (ObjectUtils.isEmpty(voFromDb)) {

            throw new AFBizException("the process has no third party account conf,please contact the administrator to add one");
        }
        if (ObjectUtils.isEmpty(voFromUser)) {
            throw new AFBizException("process has no third party account form,please contact the administrator");
        }
        return Objects.equals(voFromDb.getId(),voFromUser.getId())&&Objects.equals(voFromDb.getName(),voFromUser.getName());
    }
}
