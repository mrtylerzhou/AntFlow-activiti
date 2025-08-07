package org.openoa.engine.bpmnconf.service.biz.personnelinfoprovider;

import com.google.common.collect.Lists;
import org.openoa.base.service.AfUserService;
import org.openoa.base.vo.BaseIdTranStruVo;
import org.openoa.base.vo.BpmnNodeParamsAssigneeVo;
import org.openoa.base.vo.BpmnNodeVo;
import org.openoa.base.vo.BpmnStartConditionsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @Author TylerZhou
 * @Date 2024/7/19 21:01
 * @Version 1.0
 */
@Component
public class DirectLeaderPersonnelProvider extends AbstractMissingAssignNodeAssigneeVoProvider{
    @Autowired
    private AfUserService userService;
    @Override
    public List<BpmnNodeParamsAssigneeVo> getAssigneeList(BpmnNodeVo bpmnNodeVo, BpmnStartConditionsVo startConditionsVo) {
        String startUserId = startConditionsVo.getStartUserId();
        BaseIdTranStruVo baseIdTranStruVo = userService.queryEmployeeDirectLeaderById(startUserId);

        return  super.provideAssigneeList(bpmnNodeVo,Lists.newArrayList(baseIdTranStruVo));
    }
}
