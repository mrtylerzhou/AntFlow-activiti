package org.openoa.engine.bpmnconf.adp.bpmnnodeadp;

import com.alibaba.fastjson2.JSON;
import com.google.common.collect.Lists;
import org.openoa.base.constant.enums.NodeFormAssigneePropertyEnum;
import org.openoa.base.constant.enums.NodePropertyEnum;
import org.openoa.base.entity.BpmnNodeFormRelatedUserConf;
import org.openoa.base.exception.AFBizException;
import org.openoa.base.exception.BusinessErrorEnum;
import org.openoa.base.util.MultiTenantUtil;
import org.openoa.base.util.SecurityUtils;
import org.openoa.base.vo.BaseIdTranStruVo;
import org.openoa.base.vo.BpmnNodePropertysVo;
import org.openoa.base.vo.BpmnNodeVo;
import org.openoa.base.vo.PersonnelRuleVO;
import org.openoa.engine.bpmnconf.constant.enus.BpmnNodeAdpConfEnum;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Service
public class NodePropertyFormRelatedAdp extends AbstractCommonBpmnNodeAdaptor<BpmnNodeFormRelatedUserConf>{


    @Override
    protected void setNodeProperty(BpmnNodeVo nodeVo,BpmnNodeFormRelatedUserConf bpmnNodeFormRelatedUserConf) {
        if(bpmnNodeFormRelatedUserConf==null){
            throw new AFBizException(BusinessErrorEnum.CAN_NOT_GET_VALUE_FROM_DB);
        }
        Integer nodeProperty = nodeVo.getNodeProperty();
        if(!Objects.equals(NodePropertyEnum.NODE_PROPERTY_FORM_RELATED.getCode(), nodeProperty)){
            throw new AFBizException(BusinessErrorEnum.PARAMS_MISMATCH.getCodeStr(),"logic error,please contact Administrator");
        }

        NodeFormAssigneePropertyEnum formAssigneePropertyEnum=NodeFormAssigneePropertyEnum.getByCode(bpmnNodeFormRelatedUserConf.getValueType());
        String valueJson = bpmnNodeFormRelatedUserConf.getValueJson();
        List<BaseIdTranStruVo> formNameAndValues = JSON.parseArray(valueJson, BaseIdTranStruVo.class);

        nodeVo.setProperty(BpmnNodePropertysVo
                .builder()
                .signType(bpmnNodeFormRelatedUserConf.getSignType())
                .formAssigneeProperty(bpmnNodeFormRelatedUserConf.getValueType())
                .formInfos(formNameAndValues)
                .build());
    }

    @Override
    protected List<BpmnNodeFormRelatedUserConf> buildEntity(BpmnNodeVo nodeVo) {
        BpmnNodeFormRelatedUserConf entity=new BpmnNodeFormRelatedUserConf();
        entity.setBpmnNodeId(nodeVo.getId());
        BpmnNodePropertysVo property = nodeVo.getProperty();
        entity.setSignType(property.getSignType());
        entity.setValueJson(JSON.toJSONString(property.getFormInfos()));
        entity.setValueType(property.getFormAssigneeProperty());
        entity.setValueTypeName(NodeFormAssigneePropertyEnum.getDescByCode(property.getFormAssigneeProperty()));
        entity.setTenantId(MultiTenantUtil.getCurrentTenantId());
        entity.setCreateInfo();
        return Lists.newArrayList(entity);
    }

    @Override
    protected void checkParam(BpmnNodeVo nodeVo) {
        BpmnNodePropertysVo property = nodeVo.getProperty();
        if(property==null){
            throw new AFBizException(BusinessErrorEnum.PARAMS_NOT_COMPLETE.getCodeStr(),"节点扩展参数:property不能为空!");
        }
        if (property.getSignType()==null) {
            property.setSignType(1);
        }
        if(property.getFormInfos()==null){
            throw new AFBizException(BusinessErrorEnum.PARAMS_NOT_COMPLETE.getCodeStr(),"节点扩展参数:formInfos不能为空!");
        }
        if (property.getFormAssigneeProperty()==null) {
            throw new AFBizException(BusinessErrorEnum.PARAMS_NOT_COMPLETE.getCodeStr(),"节点扩展参数:formAssigneeProperty不能为空!");
        }
        if (NodeFormAssigneePropertyEnum.getByCode(property.getFormAssigneeProperty())==null) {
            throw new AFBizException(BusinessErrorEnum.PARAMS_NULL_AFTER_CONVERT.getCodeStr(),"节点扩展参数:formAssigneeProperty类型未定义!");
        }
    }

    @Override
    public PersonnelRuleVO formaFieldAttributeInfoVO() {
        return null;
    }
    @Override
    public void setSupportBusinessObjects() {
        addSupportBusinessObjects(BpmnNodeAdpConfEnum.ADP_CONF_NODE_PROPERTY_FORM_RELATED_USERS);
    }
}
