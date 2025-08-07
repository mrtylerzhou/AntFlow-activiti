package org.openoa.engine.bpmnconf.service.biz.personnelinfoprovider;

import com.google.common.base.Strings;
import org.openoa.base.exception.AFBizException;
import org.openoa.base.interf.BpmnPersonnelProviderService;
import org.openoa.base.vo.*;
import org.openoa.common.util.AssigneeVoBuildUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import java.util.List;

/**
 *@Author JimuOffice
 * @Description //TODO $
 * @Date 2022-05-01 10:27
 * @Param
 * @return
 * @Version 1.0
 */
@Component
public class UserPointedPersonnelProvider implements BpmnPersonnelProviderService {
    @Autowired
    private AssigneeVoBuildUtils assigneeVoBuildUtils;

    @Override
    public List<BpmnNodeParamsAssigneeVo> getAssigneeList( BpmnNodeVo bpmnNodeVo, BpmnStartConditionsVo startConditionsVo) {
        if(bpmnNodeVo==null){
            throw new AFBizException("node can not be null!");
        }
        BpmnNodePropertysVo propertysVo = bpmnNodeVo.getProperty();
        if (ObjectUtils.isEmpty(propertysVo) || ObjectUtils.isEmpty(propertysVo.getEmplIds())) {
            throw new AFBizException("appointed assignee doest not meet basic condition,can not go on");
        }
        String elementName=bpmnNodeVo.getNodeName();
        if(Strings.isNullOrEmpty(elementName)){
            elementName="指定人员";
        }
        if(bpmnNodeVo.getIsOutSideProcess()!=null&&bpmnNodeVo.getIsOutSideProcess().equals(1)){
            List<BaseIdTranStruVo> emplList = bpmnNodeVo.getProperty().getEmplList();
            if(CollectionUtils.isEmpty(emplList)){
                throw new AFBizException("thirdy party process role node has no employee info");
            }
          return assigneeVoBuildUtils.buildVOs(emplList,elementName,false);
        }
        List<String> emplIds = propertysVo.getEmplIds();

        List<BpmnNodeParamsAssigneeVo> bpmnNodeParamsAssigneeVos = assigneeVoBuildUtils.buildVos(emplIds, elementName,false);
        return bpmnNodeParamsAssigneeVos;
    }
}
