package org.openoa.engine.bpmnconf.adp.bpmnnodeadp;

import org.openoa.base.constant.enums.NodePropertyEnum;
import org.openoa.base.exception.AFBizException;
import org.openoa.base.exception.BusinessErrorEnum;
import org.openoa.base.vo.BpmnNodePropertysVo;
import org.openoa.base.vo.BpmnNodeVo;
import org.openoa.base.vo.PersonnelRuleVO;
import org.openoa.engine.bpmnconf.constant.enus.BpmnNodeAdpConfEnum;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.Optional;

/**
 * @Author tylerzhou
 * @since 0.5
 */
@Component
public class NodePropertyStartUserAdp implements BpmnNodeAdaptor {
    @Override
    public void formatToBpmnNodeVo(BpmnNodeVo bpmnNodeVo) {
        bpmnNodeVo.setDeduplicationExclude(true);

    }

    @Override
    public void editBpmnNode(BpmnNodeVo bpmnNodeVo) {
        BpmnNodePropertysVo bpmnNodePropertysVo = Optional.ofNullable(bpmnNodeVo.getProperty())
                .orElse(new BpmnNodePropertysVo());
        if(!CollectionUtils.isEmpty(bpmnNodePropertysVo.getAdditionalSignInfoList())){
            throw new AFBizException(BusinessErrorEnum.STATUS_ERROR.getCodeStr(),"发起人自己审批不允许全局增加/减少审批人!");
        }
    }

    @Override
    public PersonnelRuleVO formaFieldAttributeInfoVO() {
        PersonnelRuleVO vo=new PersonnelRuleVO();
        NodePropertyEnum nodePropertyStartUser = NodePropertyEnum.NODE_PROPERTY_START_USER;
        vo.setNodeProperty(nodePropertyStartUser.getCode());
        vo.setNodePropertyName(nodePropertyStartUser.getDesc());
        return vo;
    }


    @Override
    public void setSupportBusinessObjects() {
        addSupportBusinessObjects(BpmnNodeAdpConfEnum.ADP_CONF_NODE_PROPERTY_START_USER);
    }
}
