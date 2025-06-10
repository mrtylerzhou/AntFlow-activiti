package org.openoa.engine.bpmnconf.service.biz.personnelinfoprovider;

import com.google.common.collect.Lists;
import org.openoa.base.exception.JiMuBizException;
import org.openoa.base.service.AfUserService;
import org.openoa.base.vo.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author TylerZhou
 * @Date 2024/7/19 7:21
 * @Version 1.0
 */
@Component
public class LevelPersonnelProvider extends AbstractNodeAssigneeVoProvider{
    @Autowired
    private AfUserService userService;
    @Override
    public List<BpmnNodeParamsAssigneeVo> getAssigneeList(BpmnNodeVo bpmnNodeVo, BpmnStartConditionsVo startConditionsVo) {
        BpmnNodePropertysVo propertysVo = bpmnNodeVo.getProperty();
        String startUserId = startConditionsVo.getStartUserId();

        if (propertysVo==null||propertysVo.getAssignLevelGrade()==null || startUserId==null) {
            throw new JiMuBizException("指定层级审批条件不全，无法找人！");
        }
        Integer assignLevelType = propertysVo.getAssignLevelType();
        Integer assignLevelGrade = propertysVo.getAssignLevelGrade();
        BaseIdTranStruVo baseIdTranStruVo = userService.queryLeaderByEmployeeIdAndLevel(startUserId, assignLevelGrade);

        ArrayList<String> userIds = new ArrayList<>();
        String failFastInfo = "";
        if(baseIdTranStruVo!=null){
            userIds.add(baseIdTranStruVo.getId());
        }else {
            failFastInfo = String.format("未能根据发起人Id:%s查询到层级为%s的领导", startUserId, assignLevelGrade);
        }
        return  super.provideAssigneeList(bpmnNodeVo,userIds,failFastInfo);
    }
}
