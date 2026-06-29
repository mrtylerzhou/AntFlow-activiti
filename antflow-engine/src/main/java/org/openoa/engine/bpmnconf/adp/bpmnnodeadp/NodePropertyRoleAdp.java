package org.openoa.engine.bpmnconf.adp.bpmnnodeadp;

import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.openoa.base.constant.enums.FieldValueTypeEnum;
import org.openoa.base.constant.enums.NodePropertyEnum;
import org.openoa.base.exception.AFBizException;
import org.openoa.base.service.empinfoprovider.BpmnRoleInfoProvider;
import org.openoa.base.util.AfNodeUtils;
import org.openoa.base.vo.*;
import org.openoa.base.entity.jsonconf.BpmnNodeApproverConfJson;
import org.openoa.base.entity.jsonconf.BpmnNodeConfigJson;
import org.openoa.engine.bpmnconf.constant.enus.BpmnNodeAdpConfEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author AntFlow
 * @since 0.5
 */
@Slf4j
@Component
public class NodePropertyRoleAdp extends AbstractAdditionSignNodeAdaptor {

    @Autowired
    private BpmnRoleInfoProvider roleInfoProvider;


    @Override
    public void formatToBpmnNodeVo(BpmnNodeVo bpmnNodeVo) {

        super.formatToBpmnNodeVo(bpmnNodeVo);

        // Prefer JSON config if available
        BpmnNodeConfigJson nodeConfig = bpmnNodeVo.getNodeConfigJsonObj();
        if (nodeConfig != null && nodeConfig.getApproverConf() != null
                && !CollectionUtils.isEmpty(nodeConfig.getApproverConf().getRoleConfList())) {
            List<BpmnNodeApproverConfJson.RoleConf> roleConfs = nodeConfig.getApproverConf().getRoleConfList();
            List<BaseIdTranStruVo> roles = roleConfs.stream()
                    .map(rc -> BaseIdTranStruVo.builder().id(rc.getRoleId()).name(rc.getRoleName()).build())
                    .collect(Collectors.toList());
            AfNodeUtils.addOrEditProperty(bpmnNodeVo, a -> {
                a.setRoleIds(roles.stream().map(BaseIdTranStruVo::getId).collect(Collectors.toList()));
                a.setRoleList(roles);
                a.setSignType(roleConfs.get(0).getSignType());
            });
            // outside employees for role
            if (bpmnNodeVo.getIsOutSideProcess() != null && bpmnNodeVo.getIsOutSideProcess().equals(1)) {
                BpmnNodeApproverConfJson.RoleConf firstRole = roleConfs.get(0);
                if (!CollectionUtils.isEmpty(firstRole.getOutsideEmployees())) {
                    bpmnNodeVo.getProperty().setEmplIds(firstRole.getOutsideEmployees().stream().map(BpmnNodeApproverConfJson.EmployeeInfo::getEmplId).collect(Collectors.toList()));
                    bpmnNodeVo.getProperty().setEmplList(firstRole.getOutsideEmployees().stream()
                            .map(e -> BaseIdTranStruVo.builder().id(e.getEmplId()).name(e.getEmplName()).build()).collect(Collectors.toList()));
                }
            }
            return;
        }
        throw  new AFBizException("migration error,please contact the author");

    }


    @Override
    public PersonnelRuleVO formaFieldAttributeInfoVO() {
        PersonnelRuleVO personnelRuleVO = new PersonnelRuleVO();
        NodePropertyEnum nodePropertyRole = NodePropertyEnum.NODE_PROPERTY_ROLE;
        personnelRuleVO.setNodeProperty(nodePropertyRole.getCode());
        personnelRuleVO.setNodePropertyName(nodePropertyRole.getDesc());
        FieldAttributeInfoVO attributeInfo = new FieldAttributeInfoVO();
        attributeInfo.setFieldLabel("请选择角色");
        attributeInfo.setFieldName("roleIds");
        attributeInfo.setFieldType(FieldValueTypeEnum.ROLECHOICE.getDesc());
        personnelRuleVO.setFieldInfos(Lists.newArrayList(attributeInfo));
        return personnelRuleVO;
    }



    @Override
    public void setSupportBusinessObjects() {
        addSupportBusinessObjects(BpmnNodeAdpConfEnum.ADP_CONF_NODE_PROPERTY_ROLE);
    }
}
