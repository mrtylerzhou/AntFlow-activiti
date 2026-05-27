package org.openoa.engine.bpmnconf.adp.bpmnnodeadp;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.openoa.base.constant.enums.FieldValueTypeEnum;
import org.openoa.base.constant.enums.NodePropertyEnum;
import org.openoa.base.constant.enums.OrderNodeTypeEnum;
import org.openoa.base.exception.AFBizException;
import org.openoa.base.exception.BusinessErrorEnum;
import org.openoa.base.service.empinfoprovider.BpmnEmployeeInfoProviderService;
import org.openoa.base.util.AntCollectionUtil;
import org.openoa.base.util.SecurityUtils;
import org.openoa.base.vo.*;
import org.openoa.base.entity.BpmnNodeLoopConf;
import org.openoa.base.entity.jsonconf.BpmnNodeApproverConfJson;
import org.openoa.base.entity.jsonconf.BpmnNodeConfigJson;
import org.openoa.engine.bpmnconf.constant.enus.BpmnNodeAdpConfEnum;
import org.openoa.base.util.MultiTenantUtil;
import org.openoa.engine.bpmnconf.service.interf.repository.BpmnNodeLoopConfService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author: AntFlow
 * @since 0.5
 */
@Slf4j
@Component
public class NodePropertyLoopAdp implements BpmnNodeAdaptor {

    @Autowired
    private BpmnNodeLoopConfService bpmnNodeLoopConfService;
    @Autowired
    private BpmnEmployeeInfoProviderService bpmnEmployeeInfoProviderService;

    @Override
    public void formatToBpmnNodeVo(BpmnNodeVo bpmnNodeVo) {

        // Prefer JSON config if available
        BpmnNodeConfigJson nodeConfig = bpmnNodeVo.getNodeConfigJsonObj();
        if (nodeConfig != null && nodeConfig.getApproverConf() != null
                && nodeConfig.getApproverConf().getLoopConf() != null) {
            BpmnNodeApproverConfJson.LoopConf lc = nodeConfig.getApproverConf().getLoopConf();
            List<Serializable> list = !ObjectUtils.isEmpty(lc.getLoopEndPerson())
                    ? Arrays.asList(lc.getLoopEndPerson().split(",")).stream().map(Long::new).collect(Collectors.toList())
                    : new ArrayList<>();
            List<Serializable> noList = !ObjectUtils.isEmpty(lc.getNoparticipatingStaffIds())
                    ? Arrays.asList(lc.getNoparticipatingStaffIds().split(",")).stream().map(Long::new).collect(Collectors.toList())
                    : new ArrayList<>();

            List<BaseIdTranStruVo> loopEndPersonList = bpmnEmployeeInfoProviderService.provideEmployeeInfo(AntCollectionUtil.serializeToStringCollection(list))
                    .entrySet().stream().map(a -> BaseIdTranStruVo.builder().id(a.getKey()).name(a.getValue()).build()).collect(Collectors.toList());
            List<BaseIdTranStruVo> noparticipatingStaffs = bpmnEmployeeInfoProviderService.provideEmployeeInfo(AntCollectionUtil.serializeToStringCollection(noList))
                    .entrySet().stream().map(a -> BaseIdTranStruVo.builder().id(a.getKey()).name(a.getValue()).build()).collect(Collectors.toList());

            bpmnNodeVo.setProperty(BpmnNodePropertysVo.builder()
                    .loopEndType(lc.getLoopEndType())
                    .loopNumberPlies(lc.getLoopNumberPlies())
                    .loopEndGrade(lc.getLoopEndGrade())
                    .loopEndPersonList(list)
                    .loopEndPersonObjList(loopEndPersonList)
                    .noparticipatingStaffIds(noList)
                    .noparticipatingStaffs(noparticipatingStaffs)
                    .build());
            bpmnNodeVo.setOrderedNodeType(OrderNodeTypeEnum.LOOP_NODE.getCode());
        }

        throw new AFBizException("migration error,please contact the author");
    }

    @Override
    public PersonnelRuleVO formaFieldAttributeInfoVO() {
        Class<BpmnNodeLoopConf> entityClass=BpmnNodeLoopConf.class;
        PersonnelRuleVO rule=new PersonnelRuleVO();
        NodePropertyEnum nodePropertyLoop = NodePropertyEnum.NODE_PROPERTY_LOOP;
        rule.setNodeProperty(nodePropertyLoop.getCode());
        rule.setNodePropertyName(nodePropertyLoop.getDesc());
        try {
            String loopNumberPlies = entityClass.getDeclaredField("loopNumberPlies").getName();
            FieldAttributeInfoVO loopNumberPliesVO=new FieldAttributeInfoVO();
            loopNumberPliesVO.setFieldName(loopNumberPlies);
            loopNumberPliesVO.setFieldLabel("层数");
            loopNumberPliesVO.setFieldType(FieldValueTypeEnum.NUMBERCHOICE.getDesc());


            String loopEndPerson="loopEndPersonList";
            FieldAttributeInfoVO loopEndPersonVO = new FieldAttributeInfoVO();
            loopEndPersonVO.setFieldName(loopEndPerson);
            loopEndPersonVO.setFieldLabel("层层审批结束人员");
            loopEndPersonVO.setFieldType(FieldValueTypeEnum.PERSONCHOICE.getDesc());


            String loopEndGrade= entityClass.getDeclaredField("loopEndGrade").getName();
            FieldAttributeInfoVO loopEndGradeVO = new FieldAttributeInfoVO();
            loopEndGradeVO.setFieldName(loopEndGrade);
            loopEndGradeVO.setFieldLabel("层层审批结束等级");
            loopEndGradeVO.setFieldType(FieldValueTypeEnum.NUMBERCHOICE.getDesc());


            rule.setFieldInfos(Lists.newArrayList(loopNumberPliesVO,loopEndPersonVO,loopEndGradeVO));
        }catch (Exception e){
            e.printStackTrace();
        }
        return rule;
    }



    @Override
    public void setSupportBusinessObjects() {
        addSupportBusinessObjects(
                BpmnNodeAdpConfEnum.ADP_CONF_NODE_PROPERTY_LOOP);
    }
}
