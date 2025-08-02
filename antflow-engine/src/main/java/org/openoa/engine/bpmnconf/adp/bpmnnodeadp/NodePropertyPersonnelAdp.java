package org.openoa.engine.bpmnconf.adp.bpmnnodeadp;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.openoa.base.constant.enums.FieldValueTypeEnum;
import org.openoa.base.constant.enums.NodePropertyEnum;
import org.openoa.base.exception.AFBizException;
import org.openoa.base.util.SecurityUtils;
import org.openoa.base.vo.*;
import org.openoa.base.entity.BpmnNodePersonnelConf;
import org.openoa.base.entity.BpmnNodePersonnelEmplConf;
import org.openoa.base.service.empinfoprovider.BpmnEmployeeInfoProviderService;
import org.openoa.engine.bpmnconf.constant.enus.BpmnNodeAdpConfEnum;
import org.openoa.engine.bpmnconf.service.impl.BpmnNodePersonnelConfServiceImpl;
import org.openoa.engine.bpmnconf.service.impl.BpmnNodePersonnelEmplConfServiceImpl;

import org.openoa.base.util.MultiTenantUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author AntFlow
 * @since 0.5
 */
@Slf4j
@Component
public class NodePropertyPersonnelAdp extends BpmnNodeAdaptor{
    @Autowired
    private BpmnNodePersonnelConfServiceImpl bpmnNodePersonnelConfService;

    @Autowired
    private BpmnNodePersonnelEmplConfServiceImpl bpmnNodePersonnelEmplConfService;

    @Autowired
    private BpmnEmployeeInfoProviderService bpmnEmployeeInfoProviderService;

    @Override
    public BpmnNodeVo formatToBpmnNodeVo(BpmnNodeVo bpmnNodeVo) {
        BpmnNodePersonnelConf bpmnNodePersonnelConf = bpmnNodePersonnelConfService.getBaseMapper().selectOne(new QueryWrapper<BpmnNodePersonnelConf>()
                .eq("bpmn_node_id", bpmnNodeVo.getId()));

           List<String> emplIds = new ArrayList<>();
           List<String> emplNames=new ArrayList<>();
        List<BpmnNodePersonnelEmplConf> bpmnNodePersons = bpmnNodePersonnelEmplConfService.getBaseMapper().selectList(new QueryWrapper<BpmnNodePersonnelEmplConf>()
                        .eq("bpmn_node_personne_id", bpmnNodePersonnelConf.getId()))
                .stream()
                .distinct()
                .collect(Collectors.toList());
        if(CollectionUtils.isEmpty(bpmnNodePersons)){
            throw  new AFBizException("配置错误或者数据被删除,指定员人审批未获取到人员");
        }

        for (BpmnNodePersonnelEmplConf bpmnNodePerson : bpmnNodePersons) {
            String emplId = bpmnNodePerson.getEmplId();
            String emplName = bpmnNodePerson.getEmplName();
            emplIds.add(emplId);
            if(!StringUtils.isEmpty(emplName)){
                emplNames.add(emplName);
            }
        }
        bpmnNodeVo.setProperty(BpmnNodePropertysVo
                   .builder()
                   .signType(bpmnNodePersonnelConf.getSignType())
                   .emplIds(emplIds)
                   .emplList(getEmplList(emplIds,emplNames))
                   .build());

        return bpmnNodeVo;
    }

    /**
     * get emp list
     * if emplNames is not empty,it is stored in db and then loaded
     * @param emplIds
     * @return
     */
    private List<BaseIdTranStruVo> getEmplList(List<String> emplIds,List<String> emplNames) {
        List<BaseIdTranStruVo> result = new ArrayList<>();

        if(!CollectionUtils.isEmpty(emplNames)){
            if(emplIds.size()!=emplNames.size()){
                throw new AFBizException("指定人员审批存在姓名不存在的人员!");
            }
            for (int i = 0; i < emplIds.size(); i++) {
                BaseIdTranStruVo vo = new BaseIdTranStruVo();
                String emplId = emplIds.get(i);
                String emplName = emplNames.get(i);
                vo.setId(emplId);
                vo.setName(emplName);
                result.add(vo);
            }
            return result;
        }
        Map<String, String> employeeInfos = bpmnEmployeeInfoProviderService.provideEmployeeInfo(emplIds);
        for (String emplId : emplIds) {
            BaseIdTranStruVo vo = new BaseIdTranStruVo();
            vo.setId(emplId);
            String empName = employeeInfos.get(emplId);
            vo.setName(empName);
            result.add(vo);
        }
        return result;
    }

    @Override
    public void editBpmnNode(BpmnNodeVo bpmnNodeVo) {
        BpmnNodePropertysVo bpmnNodePropertysVo = Optional.ofNullable(bpmnNodeVo.getProperty())
                .orElse(new BpmnNodePropertysVo());

        BpmnNodePersonnelConf bpmnNodePersonnelConf = new BpmnNodePersonnelConf();
        bpmnNodePersonnelConf.setBpmnNodeId(bpmnNodeVo.getId().intValue());
        bpmnNodePersonnelConf.setSignType(bpmnNodePropertysVo.getSignType());
        bpmnNodePersonnelConf.setCreateTime(new Date());
        bpmnNodePersonnelConf.setCreateUser(SecurityUtils.getLogInEmpNameSafe());
        bpmnNodePersonnelConf.setUpdateTime(new Date());
        bpmnNodePersonnelConf.setUpdateUser(SecurityUtils.getLogInEmpNameSafe());
        bpmnNodePersonnelConf.setTenantId(MultiTenantUtil.getCurrentTenantId());
        bpmnNodePersonnelConfService.getBaseMapper().insert(bpmnNodePersonnelConf);

        Integer nodePersonnelId = Optional.of(bpmnNodePersonnelConf.getId()).orElse(0);

        if(ObjectUtils.isEmpty(bpmnNodePropertysVo.getEmplIds())){
            return;
        }
        List<BpmnNodePersonnelEmplConf>personnelEmplConfs=new ArrayList<>();
        List<BaseIdTranStruVo> emplList = bpmnNodePropertysVo.getEmplList();
        Map<String, String> id2nameMap=null;
        if(!CollectionUtils.isEmpty(emplList)){
            id2nameMap= emplList.stream().collect(Collectors.toMap(a->a.getId().toString(), BaseIdTranStruVo::getName, (k1, k2) -> k1));
        }
        for (String emplId : bpmnNodePropertysVo.getEmplIds()) {
             BpmnNodePersonnelEmplConf personnelEmplConf=new BpmnNodePersonnelEmplConf();
                personnelEmplConf.setBpmnNodePersonneId(nodePersonnelId);
                personnelEmplConf.setEmplId(emplId);
                personnelEmplConf.setCreateTime(new Date());
                personnelEmplConf.setCreateUser(SecurityUtils.getLogInEmpNameSafe());
                personnelEmplConf.setUpdateUser(SecurityUtils.getLogInEmpNameSafe());
                personnelEmplConf.setUpdateTime(new Date());
                personnelEmplConf.setTenantId(MultiTenantUtil.getCurrentTenantId());
                if(id2nameMap!=null&&!StringUtils.isEmpty(id2nameMap.get(emplId))){
                    personnelEmplConf.setEmplName(id2nameMap.get(emplId));
                }
                personnelEmplConfs.add(personnelEmplConf);
        }

        bpmnNodePersonnelEmplConfService.saveBatch(personnelEmplConfs);
    }

    @Override
    public PersonnelRuleVO formaFieldAttributeInfoVO() {
        Class<BpmnNodePersonnelEmplConf> entityClass=BpmnNodePersonnelEmplConf.class;
        PersonnelRuleVO personnelRuleVO = new PersonnelRuleVO();
        NodePropertyEnum nodePropertyPersonnel = NodePropertyEnum.NODE_PROPERTY_PERSONNEL;
        personnelRuleVO.setNodeProperty(nodePropertyPersonnel.getCode());
        personnelRuleVO.setNodePropertyName(nodePropertyPersonnel.getDesc());
        String fieldName = "emplIds";
        FieldAttributeInfoVO vo=new FieldAttributeInfoVO();
        vo.setFieldName(fieldName);
        vo.setFieldLabel("请选择");
        vo.setFieldType(FieldValueTypeEnum.PERSONCHOICE.getDesc());
        personnelRuleVO.setFieldInfos(Lists.newArrayList(vo));
        return personnelRuleVO;
    }

    @Override
    public void setSupportBusinessObjects() {
        addSupportBusinessObjects(
                BpmnNodeAdpConfEnum.ADP_CONF_NODE_PROPERTY_PERSONNEL,
                BpmnNodeAdpConfEnum.ADP_CONF_NODE_TYPE_COPY);
    }
}
