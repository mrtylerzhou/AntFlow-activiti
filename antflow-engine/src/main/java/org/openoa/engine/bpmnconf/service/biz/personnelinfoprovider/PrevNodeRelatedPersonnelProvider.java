package org.openoa.engine.bpmnconf.service.biz.personnelinfoprovider;

import lombok.extern.slf4j.Slf4j;
import org.openoa.base.constant.enums.NodePrevNodeAssigneePropertyEnum;
import org.openoa.base.exception.AFBizException;
import org.openoa.base.exception.BusinessErrorEnum;
import org.openoa.base.service.AfRoleService;
import org.openoa.base.service.AfUserService;
import org.openoa.base.vo.BaseIdTranStruVo;
import org.openoa.base.vo.BpmnNodeParamsAssigneeVo;
import org.openoa.base.vo.BpmnNodePropertysVo;
import org.openoa.base.vo.BpmnNodeVo;
import org.openoa.base.vo.BpmnStartConditionsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Component
public class PrevNodeRelatedPersonnelProvider extends AbstractMissingAssignNodeAssigneeVoProvider{
    @Autowired
    private AfUserService afUserService;
    @Autowired
    private AfRoleService roleService;

    @Override
    public List<BpmnNodeParamsAssigneeVo> getAssigneeList(BpmnNodeVo bpmnNodeVo, BpmnStartConditionsVo startConditionsVo) {
        List<BaseIdTranStruVo> assignees = new ArrayList<>();
        List<BaseIdTranStruVo> contextEmplList = Optional.ofNullable(bpmnNodeVo.getProperty())
                .map(BpmnNodePropertysVo::getContextEmplList)
                .orElse(null);
        if (CollectionUtils.isEmpty(contextEmplList)) {
            log.warn("节点:{},名称:{},未获取到上一节点审批人配置", bpmnNodeVo.getId(), bpmnNodeVo.getNodeName());
            return new ArrayList<>();
        }
        List<String> ids = contextEmplList.stream().map(BaseIdTranStruVo::getId).collect(Collectors.toList());
        Integer prevNodeAssigneeProperty = bpmnNodeVo.getProperty().getFormAssigneeProperty();
        if (prevNodeAssigneeProperty == null) {
            throw new AFBizException(BusinessErrorEnum.PARAMS_IS_NULL.getCodeStr(), "参数:formAssigneeProperty不能为空!");
        }
        NodePrevNodeAssigneePropertyEnum prevNodeEnum = NodePrevNodeAssigneePropertyEnum.getByCode(prevNodeAssigneeProperty);
        if (prevNodeEnum == null) {
            throw new AFBizException(BusinessErrorEnum.PARAMS_NULL_AFTER_CONVERT);
        }
        switch (prevNodeEnum) {
            case PREV_NODE_ASSIGNEE:
                assignees = afUserService.queryUserByIds(ids);
                break;
            case PREV_NODE_USER_HRBP:
                assignees = afUserService.queryEmployeeHrpbByEmployeeIds(ids);
                break;
            case PREV_NODE_USER_DIRECT_LEADER:
                assignees = afUserService.queryEmployeeDirectLeaderByIds(ids);
                break;
            case PREV_NODE_USER_DEPART_LEADER:
                assignees = afUserService.queryDepartmentLeaderByIds(ids);
                break;
            case PREV_NODE_DEPART_LEADER:
                //todo
                break;
            case PREV_NODE_USER_LEVEL_LEADER:
                //todo
                break;
            case PREV_NODE_USER_LOOP_LEADER:
                //todo
                break;
            default:
                throw new AFBizException(BusinessErrorEnum.PARAMS_NULL_AFTER_CONVERT);
        }
        return super.provideAssigneeList(bpmnNodeVo, assignees);
    }
}
