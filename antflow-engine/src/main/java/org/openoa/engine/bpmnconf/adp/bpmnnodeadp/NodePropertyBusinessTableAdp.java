package org.openoa.engine.bpmnconf.adp.bpmnnodeadp;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.openoa.base.constant.enums.NodePropertyEnum;
import org.openoa.base.util.SecurityUtils;
import org.openoa.base.vo.*;
import org.openoa.base.entity.BpmnNodeBusinessTableConf;
import org.openoa.engine.bpmnconf.constant.enus.BpmnNodeAdpConfEnum;
import org.openoa.engine.bpmnconf.constant.enus.BusinessConfTableFieldEnum;
import org.openoa.engine.bpmnconf.constant.enus.ConfigurationTableEnum;
import org.openoa.engine.bpmnconf.service.impl.BpmnNodeBusinessTableConfServiceImpl;
import org.openoa.base.util.MultiTenantUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component
public class NodePropertyBusinessTableAdp extends BpmnNodeAdaptor {

    @Autowired
    private BpmnNodeBusinessTableConfServiceImpl bpmnNodeBusinessTableConfService;

    @Override
    public BpmnNodeVo formatToBpmnNodeVo(BpmnNodeVo bpmnNodeVo) {

        BpmnNodeBusinessTableConf bpmnNodeBusinessTableConf = bpmnNodeBusinessTableConfService.getOne(new QueryWrapper<BpmnNodeBusinessTableConf>()
                .eq("bpmn_node_id", bpmnNodeVo.getId()));

        if (bpmnNodeBusinessTableConf!=null) {
            bpmnNodeVo.setProperty(BpmnNodePropertysVo
                    .builder()
                    .configurationTableType(bpmnNodeBusinessTableConf.getConfigurationTableType())
                    .tableFieldType(bpmnNodeBusinessTableConf.getTableFieldType())
                    .signType(bpmnNodeBusinessTableConf.getSignType())
                    .build());
        }

        return bpmnNodeVo;
    }

    @Override
    public PersonnelRuleVO formaFieldAttributeInfoVO() {
        PersonnelRuleVO vo=new PersonnelRuleVO();
        vo.setNodePropertyName("关联业务表");
        vo.setNodeProperty(NodePropertyEnum.NODE_PROPERTY_BUSINESSTABLE.getCode());
        FieldAttributeInfoVO tableChoices = new FieldAttributeInfoVO();
        tableChoices.setFieldLabel("请选择配置表");
        tableChoices.setFieldName("configurationTableType");
        tableChoices.setSort(1);
        List<BaseIdTranStruVo> collect = Arrays.stream(
                ConfigurationTableEnum
                        .values())
                .map(a -> BaseIdTranStruVo.builder()
                        .id(a.getCode().toString()).name(a.getDesc()).build())
                .collect(Collectors.toList());
        tableChoices.setValue(collect);

        FieldAttributeInfoVO tableFieldChoice = new FieldAttributeInfoVO();
        tableFieldChoice.setFieldLabel("请选择配置表字段");
        tableFieldChoice.setFieldName("tableFieldType");
        tableFieldChoice.setSort(2);
        Map<String,List<BaseIdTranStruVo>> choices= new HashMap<>();
        for (ConfigurationTableEnum value : ConfigurationTableEnum.values()) {
            tableFieldChoice.setFieldLabel(value.getDesc());
            tableFieldChoice.setFieldValue(value.getCode().toString());
            List<BusinessConfTableFieldEnum> tableFields = BusinessConfTableFieldEnum.getByParentTable(value);
            List<BaseIdTranStruVo> baseIdTranStruVoList =
                    tableFields
                            .stream()
                            .map(a -> BaseIdTranStruVo.builder().id(a.getCode().toString()).name(a.getDesc()).build())
                            .collect(Collectors.toList());
           choices.put(value.getCode().toString(), baseIdTranStruVoList);
        }
        tableFieldChoice.setValue(choices);
        vo.setFieldInfos(Arrays.asList(tableChoices,tableFieldChoice));
        return vo;
    }

    @Override
    public void editBpmnNode(BpmnNodeVo bpmnNodeVo) {

        BpmnNodePropertysVo bpmnNodePropertysVo = Optional.ofNullable(bpmnNodeVo.getProperty())
                .orElse(new BpmnNodePropertysVo());

        BpmnNodeBusinessTableConf bpmnNodeBusinessTableConf = new BpmnNodeBusinessTableConf();
        bpmnNodeBusinessTableConf.setBpmnNodeId(bpmnNodeVo.getId());
        bpmnNodeBusinessTableConf.setConfigurationTableType(bpmnNodePropertysVo.getConfigurationTableType());
        bpmnNodeBusinessTableConf.setTableFieldType(bpmnNodePropertysVo.getTableFieldType());
        bpmnNodeBusinessTableConf.setSignType(bpmnNodePropertysVo.getSignType());
        bpmnNodeBusinessTableConf.setCreateTime(new Date());
        bpmnNodeBusinessTableConf.setCreateUser(SecurityUtils.getLogInEmpName());
        bpmnNodeBusinessTableConf.setUpdateTime(new Date());
        bpmnNodeBusinessTableConf.setUpdateUser(SecurityUtils.getLogInEmpName());
        bpmnNodeBusinessTableConf.setTenantId(MultiTenantUtil.getCurrentTenantId());
        bpmnNodeBusinessTableConfService.save(bpmnNodeBusinessTableConf);
    }

    @Override
    public void setSupportBusinessObjects() {
        addSupportBusinessObjects(BpmnNodeAdpConfEnum.ADP_CONF_NODE_PROPERTY_BUSINESSTABLE);
    }
}
