package org.openoa.engine.bpmnconf.adp.bpmnnodeadp;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.openoa.base.constant.enums.NodePropertyEnum;
import org.openoa.base.exception.AFBizException;
import org.openoa.base.util.AfNodeUtils;
import org.openoa.base.util.SecurityUtils;
import org.openoa.base.vo.*;
import org.openoa.base.entity.BpmnNodeBusinessTableConf;
import org.openoa.base.entity.jsonconf.BpmnNodeApproverConfJson;
import org.openoa.base.entity.jsonconf.BpmnNodeConfigJson;
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
public class NodePropertyBusinessTableAdp extends AbstractAdditionSignNodeAdaptor {

    @Autowired
    private BpmnNodeBusinessTableConfServiceImpl bpmnNodeBusinessTableConfService;

    @Override
    public void formatToBpmnNodeVo(BpmnNodeVo bpmnNodeVo) {
        super.formatToBpmnNodeVo(bpmnNodeVo);

        // Prefer JSON config if available
        BpmnNodeConfigJson nodeConfig = bpmnNodeVo.getNodeConfigJsonObj();
        if (nodeConfig != null && nodeConfig.getApproverConf() != null
                && nodeConfig.getApproverConf().getBusinessTableConf() != null) {
            BpmnNodeApproverConfJson.BusinessTableConf btc = nodeConfig.getApproverConf().getBusinessTableConf();
            AfNodeUtils.addOrEditProperty(bpmnNodeVo, p -> {
                p.setConfigurationTableType(btc.getConfigurationTableType());
                p.setTableFieldType(btc.getTableFieldType());
                p.setSignType(btc.getSignType());
            });
        }
    throw new AFBizException("migration error,please contact the author");
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
    public void setSupportBusinessObjects() {
        addSupportBusinessObjects(BpmnNodeAdpConfEnum.ADP_CONF_NODE_PROPERTY_BUSINESSTABLE);
    }
}
