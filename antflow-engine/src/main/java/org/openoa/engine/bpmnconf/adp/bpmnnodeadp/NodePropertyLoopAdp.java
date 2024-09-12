package org.openoa.engine.bpmnconf.adp.bpmnnodeadp;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.google.common.collect.Lists;
import jodd.util.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.openoa.base.constant.enums.FieldValueTypeEnum;
import org.openoa.base.constant.enums.NodePropertyEnum;
import org.openoa.base.constant.enums.OrderNodeTypeEnum;
import org.openoa.base.service.empinfoprovider.BpmnEmployeeInfoProviderService;
import org.openoa.base.util.AntCollectionUtil;
import org.openoa.base.util.SecurityUtils;
import org.openoa.base.vo.*;
import org.openoa.engine.bpmnconf.confentity.BpmnNodeLoopConf;
import org.openoa.engine.bpmnconf.constant.enus.BpmnNodeAdpConfEnum;
import org.openoa.engine.bpmnconf.service.impl.BpmnNodeLoopConfServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author: AntFlow
 * @since 0.5
 */
@Slf4j
@Component
public class NodePropertyLoopAdp extends BpmnNodeAdaptor {

    @Autowired
    private BpmnNodeLoopConfServiceImpl bpmnNodeLoopConfService;
    @Autowired
    private BpmnEmployeeInfoProviderService bpmnEmployeeInfoProviderService;

    @Override
    public BpmnNodeVo formatToBpmnNodeVo(BpmnNodeVo bpmnNodeVo) {

        BpmnNodeLoopConf bpmnNodeLoopConf = bpmnNodeLoopConfService.getOne(new QueryWrapper<BpmnNodeLoopConf>()
                .eq("bpmn_node_id", bpmnNodeVo.getId()));

        if (bpmnNodeLoopConf!=null) {
            List<Serializable> list =!StringUtil.isEmpty(bpmnNodeLoopConf.getLoopEndPerson())
                    ? Arrays.asList(bpmnNodeLoopConf.getLoopEndPerson().split(","))
                    .stream()
                    .map(Long::new)
                    .collect(Collectors.toList())
                    : new ArrayList<>();
            List<Serializable> noList = !StringUtil.isEmpty(bpmnNodeLoopConf.getNoparticipatingStaffIds())
                    ? Arrays.asList(bpmnNodeLoopConf.getNoparticipatingStaffIds().split(","))
                    .stream()
                    .map(Long::new)
                    .collect(Collectors.toList())
                    : new ArrayList<>();

            List<BaseIdTranStruVo> loopEndPersonList = bpmnEmployeeInfoProviderService.provideEmployeeInfo(AntCollectionUtil.serializeToStringCollection(list))
                    .entrySet()
                    .stream()
                    .map(a -> BaseIdTranStruVo
                            .builder()
                            .id(a.getKey())
                            .name(a.getValue()).build())
                    .collect(Collectors.toList());

            List<BaseIdTranStruVo> noparticipatingStaffs = bpmnEmployeeInfoProviderService.provideEmployeeInfo(AntCollectionUtil.serializeToStringCollection(noList))
                    .entrySet()
                    .stream()
                    .map(a -> BaseIdTranStruVo
                            .builder()
                            .id(a.getKey())
                            .name(a.getValue()).build())
                    .collect(Collectors.toList());
            bpmnNodeVo.setProperty(BpmnNodePropertysVo
                    .builder()
                    .loopEndType(bpmnNodeLoopConf.getLoopEndType())
                    .loopNumberPlies(bpmnNodeLoopConf.getLoopNumberPlies())
                    .loopEndGrade(bpmnNodeLoopConf.getLoopEndGrade())
                    .loopEndPersonList(list)
                    .loopEndPersonObjList(loopEndPersonList)
                    .noparticipatingStaffIds(noList)
                    .noparticipatingStaffs(noparticipatingStaffs)
                    .build());
            bpmnNodeVo.setOrderedNodeType(OrderNodeTypeEnum.OUT_SIDE_NODE.getCode());
        }

        return bpmnNodeVo;
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
    public void editBpmnNode(BpmnNodeVo bpmnNodeVo) {


        BpmnNodePropertysVo bpmnNodePropertysVo = Optional.ofNullable(bpmnNodeVo.getProperty())
                .orElse(new BpmnNodePropertysVo());


        BpmnNodeLoopConf bpmnNodeLoopConf = new BpmnNodeLoopConf();
        bpmnNodeLoopConf.setBpmnNodeId(bpmnNodeVo.getId());
        bpmnNodeLoopConf.setLoopEndType(bpmnNodePropertysVo.getLoopEndType());
        bpmnNodeLoopConf.setLoopNumberPlies(bpmnNodePropertysVo.getLoopNumberPlies());
        bpmnNodeLoopConf.setLoopEndGrade(bpmnNodePropertysVo.getLoopEndGrade());
        if (!CollectionUtils.isEmpty(bpmnNodePropertysVo.getLoopEndPersonList())) {
            bpmnNodeLoopConf.setLoopEndPerson(StringUtils.join(bpmnNodePropertysVo.getLoopEndPersonList(), ","));
        }
        if (!CollectionUtils.isEmpty(bpmnNodePropertysVo.getNoparticipatingStaffIds())) {
            bpmnNodeLoopConf.setNoparticipatingStaffIds(StringUtils.join(bpmnNodePropertysVo.getNoparticipatingStaffIds(), ","));
        }
        bpmnNodeLoopConf.setCreateTime(new Date());
        bpmnNodeLoopConf.setCreateUser(SecurityUtils.getLogInEmpName());
        bpmnNodeLoopConf.setUpdateTime(new Date());
        bpmnNodeLoopConf.setUpdateUser(SecurityUtils.getLogInEmpName());
        bpmnNodeLoopConfService.save(bpmnNodeLoopConf);
    }

    @Override
    public void setSupportBusinessObjects() {
        addSupportBusinessObjects(
                BpmnNodeAdpConfEnum.ADP_CONF_NODE_PROPERTY_LOOP);
    }
}
