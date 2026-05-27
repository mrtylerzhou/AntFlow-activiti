package org.openoa.engine.bpmnconf.adp.bpmnnodeadp;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.openoa.base.constant.enums.FieldValueTypeEnum;
import org.openoa.base.constant.enums.NodePropertyEnum;
import org.openoa.base.exception.AFBizException;
import org.openoa.base.util.SecurityUtils;
import org.openoa.base.vo.BpmnNodePropertysVo;
import org.openoa.base.vo.BpmnNodeVo;
import org.openoa.base.entity.jsonconf.BpmnNodeConfigJson;
import org.openoa.base.vo.FieldAttributeInfoVO;
import org.openoa.base.vo.PersonnelRuleVO;
import org.openoa.base.entity.BpmnNodeAssignLevelConf;
import org.openoa.engine.bpmnconf.constant.enus.BpmnNodeAdpConfEnum;
import org.openoa.base.util.MultiTenantUtil;
import org.openoa.engine.bpmnconf.service.interf.repository.BpmnNodeAssignLevelConfService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Optional;

/**
 * @author AntFlow
 * @since 0.5
 */
@Slf4j
@Component
public class NodePropertyLevelAdp extends AbstractAdditionSignNodeAdaptor {

    @Autowired
    private BpmnNodeAssignLevelConfService bpmnNodeAssignLevelConfService;

    @Override
    public void formatToBpmnNodeVo(BpmnNodeVo bpmnNodeVo) {
        super.formatToBpmnNodeVo(bpmnNodeVo);

        // Prefer JSON config if available
        BpmnNodeConfigJson nodeConfig = bpmnNodeVo.getNodeConfigJsonObj();
        if (nodeConfig != null && nodeConfig.getApproverConf() != null
                && nodeConfig.getApproverConf().getAssignLevelConf() != null) {
            bpmnNodeVo.setProperty(BpmnNodePropertysVo.builder()
                    .assignLevelType(nodeConfig.getApproverConf().getAssignLevelConf().getAssignLevelType())
                    .assignLevelGrade(nodeConfig.getApproverConf().getAssignLevelConf().getAssignLevelGrade())
                    .build());
            return;
        }

        throw new AFBizException("migration error,please contact the author");

    }

    @Override
    public PersonnelRuleVO formaFieldAttributeInfoVO() {
        Class<BpmnNodeAssignLevelConf> entityClass=BpmnNodeAssignLevelConf.class;
        PersonnelRuleVO ruleVO = new PersonnelRuleVO();
        NodePropertyEnum nodePropertyLevel = NodePropertyEnum.NODE_PROPERTY_LEVEL;
        ruleVO.setNodeProperty(nodePropertyLevel.getCode());
        ruleVO.setNodePropertyName(nodePropertyLevel.getDesc());
        try {
            String assignLevelType = entityClass.getDeclaredField("assignLevelType").getName();
            FieldAttributeInfoVO vo=new FieldAttributeInfoVO();
            vo.setFieldName(assignLevelType);
            vo.setFieldLabel("指定层级");
            vo.setFieldType(FieldValueTypeEnum.NUMBERCHOICE.getDesc());
            ruleVO.setFieldInfos(Lists.newArrayList(vo));
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
        return ruleVO;
    }


    @Override
    public void setSupportBusinessObjects() {
        addSupportBusinessObjects(
                BpmnNodeAdpConfEnum.ADP_CONF_NODE_PROPERTY_LEVEL);
    }
}
