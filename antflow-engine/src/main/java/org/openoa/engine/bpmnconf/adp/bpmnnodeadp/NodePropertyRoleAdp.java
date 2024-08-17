package org.openoa.engine.bpmnconf.adp.bpmnnodeadp;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.openoa.base.constant.enums.FieldValueTypeEnum;
import org.openoa.base.constant.enums.NodePropertyEnum;
import org.openoa.base.service.empinfoprovider.BpmnRoleInfoProvider;
import org.openoa.base.util.AntCollectionUtil;
import org.openoa.base.util.SecurityUtils;
import org.openoa.base.vo.*;
import org.openoa.engine.bpmnconf.confentity.BpmnNodeRoleConf;
import org.openoa.engine.bpmnconf.constant.enus.BpmnNodeAdpConfEnum;
import org.openoa.engine.bpmnconf.service.impl.BpmnNodeRoleConfServiceImpl;
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
public class NodePropertyRoleAdp extends BpmnNodeAdaptor {

    @Autowired
    private BpmnNodeRoleConfServiceImpl bpmnNodeRoleConfService;
    @Autowired
    private BpmnRoleInfoProvider roleInfoProvider;


    @Override
    public BpmnNodeVo formatToBpmnNodeVo(BpmnNodeVo bpmnNodeVo) {

        List<BpmnNodeRoleConf> list = bpmnNodeRoleConfService.list(new QueryWrapper<BpmnNodeRoleConf>()
                .eq("bpmn_node_id", bpmnNodeVo.getId()));

        List<Long> roleIds = list.stream()
                .map(BpmnNodeRoleConf::getRoleId)
                .collect(Collectors.toList());

        if (!CollectionUtils.isEmpty(roleIds)) {
            bpmnNodeVo.setProperty(BpmnNodePropertysVo
                    .builder()
                    .roleIds(roleIds)
                    .signType(list.get(0).getSignType())
                    .roleList(getRoleList(roleIds))
                    .build());
        }

        return bpmnNodeVo;
    }

    /**
     * 获得指定角色列表
     *
     * @param roleIds
     * @return
     */
    private List<BaseIdTranStruVo> getRoleList(List<Long> roleIds) {
        if (CollectionUtils.isEmpty(roleIds)) {
            log.info("roIds is empty");
            return Collections.EMPTY_LIST;
        }
        Map<String, String> roleInfos = roleInfoProvider.provideRoleInfo(AntCollectionUtil.numberToStringList(roleIds));
        if(CollectionUtils.isEmpty(roleInfos)){
            log.warn("role info is empty,please check you config");
            return Collections.EMPTY_LIST;
        }
        return roleInfos
                .entrySet()
                .stream()
                .map(e -> BaseIdTranStruVo
                        .builder()
                        .id(Long.parseLong(e.getKey()))
                        .name(e.getValue())
                        .build()).
                        collect(Collectors.toList());

    }

    @Override
    public PersonnelRuleVO formaFieldAttributeInfoVO() {
        PersonnelRuleVO personnelRuleVO =new PersonnelRuleVO();
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

        BpmnNodePropertysVo bpmnNodePropertysVo = Optional.ofNullable(bpmnNodeVo.getProperty())
                .orElse(new BpmnNodePropertysVo());

        if (!CollectionUtils.isEmpty(bpmnNodePropertysVo.getRoleIds())) {

            bpmnNodeRoleConfService.saveBatch(bpmnNodePropertysVo.getRoleIds()
                    .stream()
                    .map(o ->
                            BpmnNodeRoleConf
                                    .builder()
                                    .bpmnNodeId(bpmnNodeVo.getId())
                                    .roleId(o)
                                    .signType(bpmnNodePropertysVo.getSignType())
                                    .createTime(new Date())
                                    .createUser(SecurityUtils.getLogInEmpName())
                                    .updateTime(new Date())
                                    .updateUser(SecurityUtils.getLogInEmpName())
                                    .build())
                    .collect(Collectors.toList()));
        }

    }

    @Override
    public void setSupportBusinessObjects() {
        addSupportBusinessObjects(BpmnNodeAdpConfEnum.ADP_CONF_NODE_PROPERTY_ROLE);
    }
}
