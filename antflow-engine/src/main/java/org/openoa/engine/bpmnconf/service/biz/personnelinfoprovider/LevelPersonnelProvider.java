package org.openoa.engine.bpmnconf.service.biz.personnelinfoprovider;

import com.google.common.collect.Lists;
import org.openoa.base.exception.JiMuBizException;
import org.openoa.base.service.UserServiceImpl;
import org.openoa.base.vo.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

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
    private UserServiceImpl userService;
    @Override
    public List<BpmnNodeParamsAssigneeVo> getAssigneeList(BpmnNodeVo bpmnNodeVo, BpmnStartConditionsVo startConditionsVo) {
        BpmnNodePropertysVo propertysVo = bpmnNodeVo.getProperty();
        String startUserId = startConditionsVo.getStartUserId();
        if (propertysVo==null || CollectionUtils.isEmpty(propertysVo.getRoleIds())) {
            throw new JiMuBizException("指定角色找人条件不全，无法找人！");
        }
        if (propertysVo.getAssignLevelType()==null || startUserId==null) {
            throw new JiMuBizException("指定层级审批条件不全，无法找人！");
        }
        Integer assignLevelType = propertysVo.getAssignLevelType();
        Integer assignLevelGrade = propertysVo.getAssignLevelGrade();
        BaseIdTranStruVo baseIdTranStruVo = userService.queryLeaderByEmployeeIdAndLevel(startUserId, assignLevelGrade);
        if(baseIdTranStruVo==null){
            throw new JiMuBizException("未能根据发起人和指定层级找到审批人信息");
        }
        ArrayList<String> userIds = Lists.newArrayList(baseIdTranStruVo.getId().toString());
        return  super.provideAssigneeList(bpmnNodeVo,userIds);
    }
}
