package org.openoa.engine.bpmnconf.service.biz.personnelinfoprovider;

import com.google.common.collect.Lists;
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
public class HrbpPersonnelProvider extends AbstractMissingAssignNodeAssigneeVoProvider{
    @Autowired
    private AfUserService userService;
    @Override
    public List<BpmnNodeParamsAssigneeVo> getAssigneeList(BpmnNodeVo bpmnNodeVo, BpmnStartConditionsVo startConditionsVo) {
        String startUserId = startConditionsVo.getStartUserId();
        BaseIdTranStruVo baseIdTranStruVo = userService.queryEmployeeHrpbByEmployeeId(startUserId);

        return  super.provideAssigneeList(bpmnNodeVo, Lists.newArrayList(baseIdTranStruVo));
    }
}
