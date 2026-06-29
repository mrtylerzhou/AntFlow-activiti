package org.openoa.engine.bpmnconf.adp.bpmnnodeadp;

import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.openoa.base.constant.enums.FieldValueTypeEnum;
import org.openoa.base.constant.enums.NodePropertyEnum;
import org.openoa.base.exception.AFBizException;
import org.openoa.base.util.AfNodeUtils;
import org.openoa.base.vo.*;
import org.openoa.base.entity.jsonconf.BpmnNodeApproverConfJson;
import org.openoa.base.entity.jsonconf.BpmnNodeConfigJson;
import org.openoa.base.service.empinfoprovider.BpmnEmployeeInfoProviderService;
import org.openoa.engine.bpmnconf.constant.enus.BpmnNodeAdpConfEnum;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.*;

/**
 * @author AntFlow
 * @since 0.5
 */
@Slf4j
@Component
public class NodePropertyPersonnelAdp extends AbstractAdditionSignNodeAdaptor{


    @Autowired
    private BpmnEmployeeInfoProviderService bpmnEmployeeInfoProviderService;

    @Override
    public void formatToBpmnNodeVo(BpmnNodeVo bpmnNodeVo) {
        super.formatToBpmnNodeVo(bpmnNodeVo);

        // Prefer JSON config if available
        BpmnNodeConfigJson nodeConfig = bpmnNodeVo.getNodeConfigJsonObj();
        if (nodeConfig != null && nodeConfig.getApproverConf() != null
                && nodeConfig.getApproverConf().getPersonnelConf() != null) {
            BpmnNodeApproverConfJson.PersonnelConf pc = nodeConfig.getApproverConf().getPersonnelConf();
            List<String> emplIds = new ArrayList<>();
            List<String> emplNames = new ArrayList<>();
            if (!CollectionUtils.isEmpty(pc.getEmployees())) {
                for (BpmnNodeApproverConfJson.EmployeeInfo e : pc.getEmployees()) {
                    emplIds.add(e.getEmplId());
                    if (!StringUtils.isEmpty(e.getEmplName())) {
                        emplNames.add(e.getEmplName());
                    }
                }
            }
            List<String> finalEmplIds = emplIds;
            List<String> finalEmplNames = emplNames;
            AfNodeUtils.addOrEditProperty(bpmnNodeVo, a -> {
                a.setSignType(pc.getSignType());
                a.setEmplIds(finalEmplIds);
                a.setEmplList(getEmplList(finalEmplIds, finalEmplNames));
            });
            return;
        }
        throw  new AFBizException("migration error,please contact the author");
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
    public PersonnelRuleVO formaFieldAttributeInfoVO() {
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
