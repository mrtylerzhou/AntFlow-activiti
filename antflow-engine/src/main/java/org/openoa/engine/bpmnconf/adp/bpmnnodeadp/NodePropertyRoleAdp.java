package org.openoa.engine.bpmnconf.adp.bpmnnodeadp;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.openoa.base.constant.enums.FieldValueTypeEnum;
import org.openoa.base.constant.enums.NodePropertyEnum;
import org.openoa.base.service.empinfoprovider.BpmnRoleInfoProvider;
import org.openoa.base.util.AfNodeUtils;
import org.openoa.base.util.SecurityUtils;
import org.openoa.base.vo.*;
import org.openoa.base.entity.BpmnNodeRoleConf;
import org.openoa.base.entity.jsonconf.BpmnNodeApproverConfJson;
import org.openoa.base.entity.jsonconf.BpmnNodeConfigJson;
import org.openoa.base.entity.BpmnNodeRoleOutsideEmpConf;
import org.openoa.engine.bpmnconf.constant.enus.BpmnNodeAdpConfEnum;
import org.openoa.engine.bpmnconf.service.impl.BpmnNodeRoleOutsideEmpConfServiceImpl;
import org.openoa.base.util.MultiTenantUtil;
import org.openoa.engine.bpmnconf.service.interf.repository.BpmnNodeRoleConfService;
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
    private BpmnNodeRoleConfService bpmnNodeRoleConfService;
    @Autowired
    private BpmnRoleInfoProvider roleInfoProvider;

    @Autowired
    private BpmnNodeRoleOutsideEmpConfServiceImpl bpmnNodeRoleOutsideEmpConfService;

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

        // Fallback to DB
        List<BpmnNodeRoleConf> list = bpmnNodeRoleConfService.list(new QueryWrapper<BpmnNodeRoleConf>()
                .eq("bpmn_node_id", bpmnNodeVo.getId()));

        if (CollectionUtils.isEmpty(list)) {
            return ;
        }
        List<BaseIdTranStruVo> roles = list.stream().map(conf -> BaseIdTranStruVo.builder().id(conf.getRoleId()).name(conf.getRoleName()).build())
                .collect(Collectors.toList());

        AfNodeUtils.addOrEditProperty(bpmnNodeVo,a->{
            a.setRoleIds(roles.stream().map(BaseIdTranStruVo::getId).collect(Collectors.toList()));
            a.setRoleList(roles);
            a.setSignType(list.get(0).getSignType());
        });


        if (bpmnNodeVo.getIsOutSideProcess() != null && bpmnNodeVo.getIsOutSideProcess().equals(1)) {
            List<BpmnNodeRoleOutsideEmpConf> bpmnNodeRoleOutsideEmpConfs = bpmnNodeRoleOutsideEmpConfService.list(new QueryWrapper<BpmnNodeRoleOutsideEmpConf>()
                    .eq("node_id", bpmnNodeVo.getId()));
            if (!CollectionUtils.isEmpty(bpmnNodeRoleOutsideEmpConfs)) {
                bpmnNodeVo.getProperty().setEmplIds(bpmnNodeRoleOutsideEmpConfs.stream().map(BpmnNodeRoleOutsideEmpConf::getEmplId).collect(Collectors.toList()));
                List<BaseIdTranStruVo> emplList = bpmnNodeRoleOutsideEmpConfs
                        .stream()
                        .map(a -> BaseIdTranStruVo.builder().id(a.getEmplId()).name(a.getEmplName()).build()).collect(Collectors.toList());
                bpmnNodeVo.getProperty().setEmplList(emplList);
            }
        }

    }

    /**
     * 获得指定角色列表
     *
     * @param roleIds
     * @return
     */
    private List<BaseIdTranStruVo> getRoleList(List<String> roleIds) {
        if (CollectionUtils.isEmpty(roleIds)) {
            log.info("roIds is empty");
            return Collections.EMPTY_LIST;
        }


        Map<String, String> roleInfos = roleInfoProvider.provideRoleInfo(roleIds);
        if (CollectionUtils.isEmpty(roleInfos)) {
            log.warn("role info is empty,please check you config");
            return Collections.EMPTY_LIST;
        }
        return roleInfos
                .entrySet()
                .stream()
                .map(e -> BaseIdTranStruVo
                        .builder()
                        .id(e.getKey())
                        .name(e.getValue())
                        .build()).
                collect(Collectors.toList());

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
    public void editBpmnNode(BpmnNodeVo bpmnNodeVo) {
        super.editBpmnNode(bpmnNodeVo);
        BpmnNodePropertysVo bpmnNodePropertysVo = Optional.ofNullable(bpmnNodeVo.getProperty())
                .orElse(new BpmnNodePropertysVo());

        if (!CollectionUtils.isEmpty(bpmnNodePropertysVo.getRoleList())) {
            List<BaseIdTranStruVo> roleList = bpmnNodePropertysVo.getRoleList();
            if (!CollectionUtils.isEmpty(roleList)) {
                bpmnNodeRoleConfService.saveBatch(roleList
                        .stream()
                        .map(o ->
                                BpmnNodeRoleConf
                                        .builder()
                                        .bpmnNodeId(bpmnNodeVo.getId())
                                        .roleId(o.getId())
                                        .roleName(o.getName())
                                        .signType(bpmnNodePropertysVo.getSignType())
                                        .createTime(new Date())
                                        .createUser(SecurityUtils.getLogInEmpName())
                                        .updateTime(new Date())
                                        .updateUser(SecurityUtils.getLogInEmpName())
                                        .tenantId(MultiTenantUtil.getCurrentTenantId())
                                        .build())
                        .collect(Collectors.toList()));
            }


            //if it is an outside process,then
            if (bpmnNodeVo.getIsOutSideProcess() != null && bpmnNodeVo.getIsOutSideProcess().equals(1)) {
                //if it is an outside process,then emplList should be assigned values
                List<BaseIdTranStruVo> emplList = bpmnNodeVo.getProperty().getEmplList();
                if (!CollectionUtils.isEmpty(emplList)) {
                    List<BpmnNodeRoleOutsideEmpConf> bpmnNodeRoleOutsideEmpConfs = new ArrayList<>();
                    for (BaseIdTranStruVo baseIdTranStruVo : emplList) {
                        BpmnNodeRoleOutsideEmpConf bpmnNodeRoleOutsideEmpConf = new BpmnNodeRoleOutsideEmpConf();
                        bpmnNodeRoleOutsideEmpConf.setNodeId(bpmnNodeVo.getId());
                        bpmnNodeRoleOutsideEmpConf.setEmplId(baseIdTranStruVo.getId());
                        bpmnNodeRoleOutsideEmpConf.setEmplName(baseIdTranStruVo.getName());
                        bpmnNodeRoleOutsideEmpConf.setCreateUser(SecurityUtils.getLogInEmpName());
                        bpmnNodeRoleOutsideEmpConf.setUpdateUser(SecurityUtils.getLogInEmpName());
                        bpmnNodeRoleOutsideEmpConf.setTenantId(MultiTenantUtil.getCurrentTenantId());
                        bpmnNodeRoleOutsideEmpConfs.add(bpmnNodeRoleOutsideEmpConf);
                    }
                    bpmnNodeRoleOutsideEmpConfService.saveBatch(bpmnNodeRoleOutsideEmpConfs);
                }
            }
        }

    }

    @Override
    public void setSupportBusinessObjects() {
        addSupportBusinessObjects(BpmnNodeAdpConfEnum.ADP_CONF_NODE_PROPERTY_ROLE);
    }
}
