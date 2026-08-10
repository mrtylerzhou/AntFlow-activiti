package org.openoa.engine.bpmnconf.adp.orderedsignadp;

import org.openoa.base.exception.AFBizException;
import org.openoa.base.service.AfUserService;
import org.openoa.base.vo.*;
import org.openoa.common.adaptor.bpmnelementadp.AbstractOrderedSignNodeAdp;
import org.openoa.base.constant.enums.OrderNodeTypeEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 层层审批找人适配器。
 *
 * <p>当前为 demo 实现:底层 {@link AfUserService#queryLeadersByEmployeeIdAndTier} /
 * {@link AfUserService#queryLeadersByEmployeeIdAndGrade} 仍返回扁平 {@code List<BaseIdTranStruVo>},
 * 每层只有 1 个领导。这里把它包装成 {@code [[a],[b],[c]]}(每层 1 人),
 * 行为与改造前一致。</p>
 *
 * <p>真正业务侧需要"每层多人"时,重写本类,返回 {@code List<List<BaseIdTranStruVo>>},
 * 外层=层,内层=层内多人。框架层已支持多层多人。</p>
 *
 * @Author TylerZhou
 * @Date 2024/7/18 22:30
 * @Version 0.5
 */
@Service
public class BpmnLoopSignNodeAdp extends AbstractOrderedSignNodeAdp {
    @Autowired
    private AfUserService userService;
    @Override
    public List<List<BaseIdTranStruVo>> getAssigneeIds(BpmnNodeVo nodeVo, BpmnStartConditionsVo bpmnStartConditions) {
        BpmnNodePropertysVo propertysVo = nodeVo.getProperty();
        if(propertysVo==null){
            throw new AFBizException("loop sign failure,node has no property!");
        }
        String startUserId = bpmnStartConditions.getStartUserId();

        //type organization line,reporting line,you can also give it other meaning
        //it is just a property,it is only meaningful when you use it in your business
        Integer loopEndType = propertysVo.getLoopEndType();

        //two parameters,can not be both empty
        //how many levels
        Integer loopNumberPlies = propertysVo.getLoopNumberPlies();
        //end levels
        Integer loopEndGrade = propertysVo.getLoopEndGrade();
        //end person
        HashSet<String> loopEndPersonList = new HashSet<>();
        if (!CollectionUtils.isEmpty(propertysVo.getLoopEndPersonList())) {
            for (Serializable s : propertysVo.getLoopEndPersonList()) {
                loopEndPersonList.add(s.toString());
            }
        }
        if (loopNumberPlies==null && loopEndGrade==null) {
            throw new AFBizException("组织线层层审批找人时，两个入参都为空！");
        }
        List<BaseIdTranStruVo> baseIdTranStruVos=null;
        if(loopNumberPlies!=null){
            baseIdTranStruVos= userService.queryLeadersByEmployeeIdAndTier(startUserId, loopNumberPlies);
            if(CollectionUtils.isEmpty(baseIdTranStruVos)){
                throw new AFBizException("未能根据发起人找到层层审批人信息");
            }
        }
        if(loopEndGrade!=null){
            baseIdTranStruVos=userService.queryLeadersByEmployeeIdAndGrade(startUserId, loopEndGrade);
            if(CollectionUtils.isEmpty(baseIdTranStruVos)){
                throw new AFBizException("未能根据发起人找到汇报线审批人信息");
            }
        }
        if(CollectionUtils.isEmpty(baseIdTranStruVos)){
            throw new AFBizException("未能根据发起人找到审批人信息");
        }

        //loopEndPersonList 跳过"人",不跳过"层":遇到 endPerson 就跳过他本人,层里其他人继续
        List<BaseIdTranStruVo> finalApprovers = new ArrayList<>();
        for (BaseIdTranStruVo approver : baseIdTranStruVos) {
            if(!loopEndPersonList.contains(approver.getId())){
                finalApprovers.add(approver);
            }
        }

        //扁平 list 包装成 [[a],[b],[c]]:每层 1 人,行为与改造前一致
        List<List<BaseIdTranStruVo>> result = new ArrayList<>();
        for (BaseIdTranStruVo approver : finalApprovers) {
            List<BaseIdTranStruVo> layer = new ArrayList<>();
            layer.add(approver);
            result.add(layer);
        }

        return  result;
    }

    @Override
    public void setSupportBusinessObjects() {
        addSupportBusinessObjects(OrderNodeTypeEnum.LOOP_NODE);
    }
}
