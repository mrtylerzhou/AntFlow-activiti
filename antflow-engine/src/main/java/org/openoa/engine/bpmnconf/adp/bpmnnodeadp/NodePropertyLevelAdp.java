package org.openoa.engine.bpmnconf.adp.bpmnnodeadp;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.openoa.base.constant.enums.FieldValueTypeEnum;
import org.openoa.base.constant.enums.NodePropertyEnum;
import org.openoa.base.util.SecurityUtils;
import org.openoa.base.vo.BpmnNodePropertysVo;
import org.openoa.base.vo.BpmnNodeVo;
import org.openoa.base.vo.FieldAttributeInfoVO;
import org.openoa.base.vo.PersonnelRuleVO;
import org.openoa.engine.bpmnconf.confentity.BpmnNodeAssignLevelConf;
import org.openoa.engine.bpmnconf.constant.enus.BpmnNodeAdpConfEnum;
import org.openoa.engine.bpmnconf.service.impl.BpmnNodeAssignLevelConfServiceImpl;
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
public class NodePropertyLevelAdp extends BpmnNodeAdaptor {

    @Autowired
    private BpmnNodeAssignLevelConfServiceImpl bpmnNodeAssignLevelConfService;

    @Override
    public BpmnNodeVo formatToBpmnNodeVo(BpmnNodeVo bpmnNodeVo) {

        BpmnNodeAssignLevelConf bpmnNodeAssignLevelConf = bpmnNodeAssignLevelConfService.getOne(new QueryWrapper<BpmnNodeAssignLevelConf>()
                .eq("bpmn_node_id", bpmnNodeVo.getId()));

        if (bpmnNodeAssignLevelConf!=null) {

            bpmnNodeVo.setProperty(BpmnNodePropertysVo
                    .builder()
                    .assignLevelType(bpmnNodeAssignLevelConf.getAssignLevelType())
                    .assignLevelGrade(bpmnNodeAssignLevelConf.getAssignLevelGrade())
                    .build());
        }

        return bpmnNodeVo;
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
    public void editBpmnNode(BpmnNodeVo bpmnNodeVo) {

        BpmnNodePropertysVo bpmnNodePropertysVo = Optional.ofNullable(bpmnNodeVo.getProperty())
                .orElse(new BpmnNodePropertysVo());


        BpmnNodeAssignLevelConf bpmnNodeAssignLevelConf = new BpmnNodeAssignLevelConf();
        bpmnNodeAssignLevelConf.setBpmnNodeId(bpmnNodeVo.getId());
        bpmnNodeAssignLevelConf.setAssignLevelType(bpmnNodePropertysVo.getAssignLevelType());
        bpmnNodeAssignLevelConf.setAssignLevelGrade(Optional.ofNullable(bpmnNodePropertysVo.getAssignLevelGrade()).orElse(0));
        bpmnNodeAssignLevelConf.setCreateTime(new Date());
        bpmnNodeAssignLevelConf.setCreateUser(SecurityUtils.getLogInEmpName());
        bpmnNodeAssignLevelConf.setUpdateTime(new Date());
        bpmnNodeAssignLevelConf.setUpdateUser(SecurityUtils.getLogInEmpName());
        bpmnNodeAssignLevelConfService.save(bpmnNodeAssignLevelConf);
    }

    @Override
    public void setSupportBusinessObjects() {
        addSupportBusinessObjects(
                BpmnNodeAdpConfEnum.ADP_CONF_NODE_PROPERTY_LEVEL);
    }
}
