package org.openoa.engine.bpmnconf.adp.conditionfilter;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.extern.slf4j.Slf4j;
import org.openoa.base.constant.StringConstants;
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
import java.util.stream.Collectors;

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
        conditionParamTypeList=conditionParamTypeList.stream().distinct().collect(Collectors.toList());
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
        //关于默认条件,默认条件不记在表内,
        //1.如果之前是默认条件,本次不是默认,则迁移预校验会查不到,也能说明条件发生了变化,没问题.如果两次都是默认条件,则说明条件没有变化,没问题
        //2.如果之前不是默认条件,本次变成了默认条件,库里也会查不到,说明条件发生了变化,没问题
        //3.如果前后都是默认条件,则库里没记录,会跳过,也没问题
        //4.如果前后都不是默认条件,则正常逻辑,看看库里前后是否一样,不一样则说明发生了变化
        //迁移预校验
        if(Boolean.TRUE.equals(bpmnStartConditionsVo.getIsMigration())&&Boolean.TRUE.equals(bpmnStartConditionsVo.isPreview()&&isDynamicConditionGateway)){
            LambdaQueryWrapper<BpmDynamicConditionChoosen> qryWrapper = Wrappers.<BpmDynamicConditionChoosen>lambdaQuery()
                    .eq(BpmDynamicConditionChoosen::getProcessNumber, bpmnStartConditionsVo.getProcessNum())
                    .eq(BpmDynamicConditionChoosen::getNodeFrom, bpmnNodeVo.getNodeFrom());
            List<BpmDynamicConditionChoosen> conditionChoosens = dynamicConditionChoosenMapper.selectList(qryWrapper);

                List<String> nodeIdsEverUsed = conditionChoosens.stream().map(BpmDynamicConditionChoosen::getNodeId).collect(Collectors.toList());
                //如果当前节点没有使用过(曾经是false或者默认),现在变成了true,或者使用过(曾经是true),现在变成了false(或者默认),都说明说明条件发生了变化(如果使用过则一定在库里有相同记录),则抛出异常
                if((!nodeIdsEverUsed.contains(nodeId)&&result)
                        ||(nodeIdsEverUsed.contains(nodeId)&&!result)){
                    dynamicConditionChoosenMapper.delete(qryWrapper);
                    BpmDynamicConditionChoosen dynamicConditionChoosen=new BpmDynamicConditionChoosen();
                    dynamicConditionChoosen.setProcessNumber(bpmnStartConditionsVo.getProcessNum());
                    dynamicConditionChoosen.setNodeId(bpmnNodeVo.getNodeId());
                    dynamicConditionChoosenMapper.insert(dynamicConditionChoosen);
                    throw new JiMuBizException(StringConstants.CONDITION_CHANGED,"流程条件发生改变");
                }

            }

        //如果是动态条件,将条件记录下来,后面比对是否发生了变化
        if(isDynamicConditionGateway&&!Boolean.TRUE.equals(bpmnStartConditionsVo.isPreview())&&result){
            BpmDynamicConditionChoosen dynamicConditionChoosen=new BpmDynamicConditionChoosen();
            dynamicConditionChoosen.setProcessNumber(bpmnStartConditionsVo.getProcessNum());
            dynamicConditionChoosen.setNodeId(bpmnNodeVo.getNodeId());
            dynamicConditionChoosen.setNodeFrom(bpmnNodeVo.getNodeFrom());
            dynamicConditionChoosenMapper.insert(dynamicConditionChoosen);
        }
        return result;
    }
}
