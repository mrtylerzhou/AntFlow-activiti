package org.openoa.engine.bpmnconf.adp.conditionfilter;

import lombok.extern.slf4j.Slf4j;
import org.openoa.base.interf.ConditionService;
import org.openoa.base.vo.BpmnNodeVo;
import org.openoa.engine.bpmnconf.confentity.BpmDynamicConditionChoosen;
import org.openoa.engine.bpmnconf.constant.enus.ConditionTypeEnum;
import org.openoa.base.vo.BpmnNodeConditionsConfBaseVo;
import org.openoa.base.vo.BpmnStartConditionsVo;
import org.openoa.base.exception.JiMuBizException;
import org.openoa.base.util.SpringBeanUtils;
import org.openoa.engine.bpmnconf.mapper.BpmDynamicConditionChoosenMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * @author AntFlow
 * @since 0.5
 */
@Service
@Slf4j
public class ConditionServiceImpl implements ConditionService {
    @Autowired
    private BpmDynamicConditionChoosenMapper dynamicConditionChoosenMapper;

    @Override
    public boolean checkMatchCondition(BpmnNodeVo bpmnNodeVo, BpmnNodeConditionsConfBaseVo conditionsConf
            , BpmnStartConditionsVo bpmnStartConditionsVo,boolean isDynamicConditionGateway) {
        String nodeId=bpmnNodeVo.getNodeId();
        List<Integer> conditionParamTypeList = conditionsConf.getConditionParamTypes();
        if (CollectionUtils.isEmpty(conditionParamTypeList)) {
            return false;
        }
        boolean result = true;
        for (Integer integer : conditionParamTypeList) {
            ConditionTypeEnum conditionTypeEnum = ConditionTypeEnum.getEnumByCode(integer);
            if (conditionTypeEnum == null) {
                log.info("condition type is null,type:{}", integer);
                result = false;
                break;
            }
            try {
                if (!SpringBeanUtils.getBean(conditionTypeEnum.getConditionJudgeCls()).judge(nodeId, conditionsConf, bpmnStartConditionsVo)) {
                    result = false;
                    break;
                }
            } catch (JiMuBizException e) {
                log.info("condiiton judge business exception:" + e.getMessage());
                result = false;
                break;
            } catch (Exception e) {
                log.error("conditionJudgeClass instantiate failure", e);
                result = false;
                break;
            }

        }
        if(Boolean.TRUE.equals(bpmnStartConditionsVo.getIsMigration())){

        }
        if(isDynamicConditionGateway){
            BpmDynamicConditionChoosen dynamicConditionChoosen=new BpmDynamicConditionChoosen();
            dynamicConditionChoosen.setProcessNumber(bpmnStartConditionsVo.getProcessNum());
            dynamicConditionChoosen.setNodeId(bpmnNodeVo.getNodeId());
            dynamicConditionChoosenMapper.insert(dynamicConditionChoosen);
        }
        return result;
    }
}
