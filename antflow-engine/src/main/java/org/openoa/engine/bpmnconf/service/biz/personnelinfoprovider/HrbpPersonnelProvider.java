package org.openoa.engine.bpmnconf.service.biz.personnelinfoprovider;

import com.google.common.collect.Lists;
import org.openoa.base.exception.JiMuBizException;
import org.openoa.base.service.UserServiceImpl;
import org.openoa.base.vo.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author TylerZhou
 * @Date 2024/7/19 19:52
 * @Version 1.0
 */
@Component
public class HrbpPersonnelProvider extends AbstractNodeAssigneeVoProvider{
    @Autowired
    private UserServiceImpl userService;
    @Override
    public List<BpmnNodeParamsAssigneeVo> getAssigneeList(BpmnNodeVo bpmnNodeVo, BpmnStartConditionsVo startConditionsVo) {
        String startUserId = startConditionsVo.getStartUserId();
        BaseIdTranStruVo baseIdTranStruVo = userService.queryEmployeeHrpbByEmployeeId(startUserId);
        if(baseIdTranStruVo==null){
            throw new JiMuBizException("发起人HRBP不存在");
        }

        ArrayList<String> userIds = Lists.newArrayList(baseIdTranStruVo.getId().toString());
        return  super.provideAssigneeList(bpmnNodeVo,userIds);
    }
}
