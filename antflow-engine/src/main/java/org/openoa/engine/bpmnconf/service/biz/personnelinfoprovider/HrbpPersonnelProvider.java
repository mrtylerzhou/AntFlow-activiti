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
 * @Date 2024/7/19 19:52
 * @Version 1.0
 */
@Component
public class HrbpPersonnelProvider extends AbstractNodeAssigneeVoProvider{
    @Autowired
    private AfUserService userService;
    @Override
    public List<BpmnNodeParamsAssigneeVo> getAssigneeList(BpmnNodeVo bpmnNodeVo, BpmnStartConditionsVo startConditionsVo) {
        String startUserId = startConditionsVo.getStartUserId();
        BaseIdTranStruVo baseIdTranStruVo = userService.queryEmployeeHrpbByEmployeeId(startUserId);

        ArrayList<String> userIds = new ArrayList<>();
        String failFastInfo = "";
        if(baseIdTranStruVo!=null){
            userIds.add(baseIdTranStruVo.getId());
        }else {
            failFastInfo = String.format("未能根据发起人Id:%s查询到HRBP", startUserId);
        }
        return  super.provideAssigneeList(bpmnNodeVo,userIds,failFastInfo);
    }
}
