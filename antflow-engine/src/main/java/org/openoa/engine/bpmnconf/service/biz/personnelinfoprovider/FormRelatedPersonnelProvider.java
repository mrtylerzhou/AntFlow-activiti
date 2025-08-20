package org.openoa.engine.bpmnconf.service.biz.personnelinfoprovider;

import com.google.common.base.Strings;
import lombok.extern.slf4j.Slf4j;
import org.openoa.base.constant.enums.NodeFormAssigneePropertyEnum;
import org.openoa.base.exception.AFBizException;
import org.openoa.base.exception.BusinessErrorEnum;
import org.openoa.base.service.AfRoleService;
import org.openoa.base.service.AfUserService;
import org.openoa.base.vo.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class FormRelatedPersonnelProvider extends AbstractMissingAssignNodeAssigneeVoProvider{
    @Autowired
    private AfUserService afUserService;
    @Autowired
    private AfRoleService roleService;

    @Override
    public List<BpmnNodeParamsAssigneeVo> getAssigneeList(BpmnNodeVo bpmnNodeVo, BpmnStartConditionsVo startConditionsVo) {
        Long id = bpmnNodeVo.getId();
        BusinessDataVo businessDataVo = startConditionsVo.getBusinessDataVo();
        Map<String, List<String>> node2formRelatedAssignees = businessDataVo.getNode2formRelatedAssignees();
        List<BaseIdTranStruVo> assignees=new ArrayList<>();
        List<String> ids = node2formRelatedAssignees.get(String.valueOf(id));
        if(CollectionUtils.isEmpty(ids)){
            log.warn(Strings.lenientFormat("节点:%d,名称:%s,未获取到表单相关人员配置",id,bpmnNodeVo.getNodeName()));
            return new ArrayList<>();
        }
        Integer formAssigneeProperty = bpmnNodeVo.getProperty().getFormAssigneeProperty();
        if(formAssigneeProperty==null){
            throw new AFBizException(BusinessErrorEnum.PARAMS_IS_NULL.getCodeStr(),"参数:formAssigneeProperty不能为空!");
        }
        NodeFormAssigneePropertyEnum formAssigneePropertyEnum = NodeFormAssigneePropertyEnum.getByCode(formAssigneeProperty);
        if(formAssigneePropertyEnum==null){
            throw  new AFBizException(BusinessErrorEnum.PARAMS_NULL_AFTER_CONVERT);
        }
        switch (formAssigneePropertyEnum){
            case FORM_ASSIGNEE:
               assignees= afUserService.queryUserByIds(ids);
               break;
            case FORM_ROLE:
                assignees= roleService.queryUserByRoleIds(ids);
                break;
            case FORM_USER_HRBP:
                assignees=afUserService.queryEmployeeHrpbByEmployeeIds(ids);
                break;
            case FORM_USER_DIRECT_LEADER:
                assignees=afUserService.queryEmployeeDirectLeaderByIds(ids);
                break;
            case FORM_USER_DEPART_LEADER:
                assignees= afUserService.queryDepartmentLeaderByIds(ids);
                break;
            case FORM_DEPART_LEADER:
                //todo
                break;
            case FORM_USER_LEVEL_LEADER:
                //todo
                break;
            case FORM_USER_LOOP_LEADER:
                //todo
                break;
            default:
                throw new AFBizException(BusinessErrorEnum.PARAMS_NULL_AFTER_CONVERT);
        }
        return  super.provideAssigneeList(bpmnNodeVo,assignees);
    }
}
