package org.openoa.engine.bpmnconf.adp.bpmnnodeadp;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;
import org.openoa.base.constant.enums.AFSpecialDictCategoryEnum;
import org.openoa.base.constant.enums.NodePropertyEnum;
import org.openoa.base.entity.BpmnNodeUDRConf;
import org.openoa.base.entity.DictData;
import org.openoa.base.exception.AFBizException;
import org.openoa.base.exception.BusinessErrorEnum;
import org.openoa.base.vo.BpmnNodePropertysVo;
import org.openoa.base.vo.BpmnNodeVo;
import org.openoa.base.vo.PersonnelRuleVO;
import org.openoa.engine.bpmnconf.constant.enus.BpmnNodeAdpConfEnum;
import org.openoa.engine.bpmnconf.service.interf.DicDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class NodePropertyUDRAdp extends AbstractCommonBpmnNodeAdaptor<BpmnNodeUDRConf>{

    @Autowired
    private DicDataService dicDataService;

    @Override
    protected void setNodeProperty(BpmnNodeVo bpmnNodeVo, BpmnNodeUDRConf bpmnNodeUDRConf) {
        if(bpmnNodeUDRConf==null){
            throw new AFBizException(BusinessErrorEnum.CAN_NOT_GET_VALUE_FROM_DB);
        }
        Integer nodeProperty = bpmnNodeVo.getNodeProperty();
        if(!Objects.equals(NodePropertyEnum.NODE_PROPERTY_ZDY_RULES.getCode(), nodeProperty)){
            throw new AFBizException(BusinessErrorEnum.PARAMS_MISMATCH.getCodeStr(),"logic error,please contact Administrator");
        }
        String udrProperty = bpmnNodeUDRConf.getUdrProperty();
        if(StringUtils.isEmpty(udrProperty)){
            throw new AFBizException(BusinessErrorEnum.PARAMS_NOT_COMPLETE.getCodeStr(),"param is incomplete,udrProperty is not in present");
        }
        List<DictData> udrRules = dicDataService.queryDicDataByCategory(AFSpecialDictCategoryEnum.USER_DEFINED_RULE_FOR_ASSIGNEE.getDesc());
        if(udrRules.stream().noneMatch(a -> udrProperty.equalsIgnoreCase(a.getValue()))){
            throw new AFBizException(BusinessErrorEnum.PARAMS_NULL_AFTER_CONVERT.getCodeStr(), Strings.lenientFormat("undefined property %s for udrProperty"),udrProperty);
        }
        String ext1 = bpmnNodeUDRConf.getExt1();
        String ext2 = bpmnNodeUDRConf.getExt2();
        String ext3 = bpmnNodeUDRConf.getExt3();
        String ext4 = bpmnNodeUDRConf.getExt4();
        Integer signType = bpmnNodeUDRConf.getSignType();
        bpmnNodeVo.setProperty(BpmnNodePropertysVo
                .builder()
                .signType(signType)
                .udrAssigneeProperty(udrProperty)
                .ext1(ext1)
                .ext2(ext2)
                .ext3(ext3)
                .ext4(ext4)
                .build());
    }

    @Override
    protected List<BpmnNodeUDRConf> buildEntity(BpmnNodeVo nodeVo) {
        BpmnNodeUDRConf udrConf=new BpmnNodeUDRConf();
        BpmnNodePropertysVo property = nodeVo.getProperty();
        Integer signType = property.getSignType();
        String udrAssigneeProperty = property.getUdrAssigneeProperty();
        String ext1 = property.getExt1();
        String ext2 = property.getExt2();
        String ext3 = property.getExt3();
        String ext4 = property.getExt4();
        udrConf.setSignType(signType);
        udrConf.setUdrProperty(udrAssigneeProperty);
        List<DictData> udrRules = dicDataService.queryDicDataByCategory(AFSpecialDictCategoryEnum.USER_DEFINED_RULE_FOR_ASSIGNEE.getDesc());
        List<DictData> dictData = udrRules.stream().filter(a -> udrAssigneeProperty.equalsIgnoreCase(a.getValue())).collect(Collectors.toList());
        if(CollectionUtils.isEmpty(dictData)){
            throw new AFBizException(BusinessErrorEnum.PARAMS_NULL_AFTER_CONVERT.getCodeStr(), Strings.lenientFormat("undefined property %s for udrAssigneeProperty"),udrAssigneeProperty);
        }
        if(dictData.size()>1){
            throw new AFBizException(BusinessErrorEnum.STATUS_ERROR.getCodeStr(),"there is more than one record for udrAssigneeProperty,please check you dict data!");
        }
        DictData dictData1 = dictData.get(0);
        udrConf.setUdrPropertyName(dictData1.getLabel());
        udrConf.setExt1(ext1);
        udrConf.setExt2(ext2);
        udrConf.setExt3(ext3);
        udrConf.setExt4(ext4);
        udrConf.setCreateInfo();
        return Lists.newArrayList(udrConf);
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
        String udrAssigneeProperty = property.getUdrAssigneeProperty();
        if(StringUtils.isEmpty(udrAssigneeProperty)){
            throw new AFBizException(BusinessErrorEnum.PARAMS_NOT_COMPLETE.getCodeStr(),"节点扩展参数:udrAssigneeProperty不能为空!");
        }
        //在字典中是否存在不再校验了,后面入库时要使用,直接在那里校验了,减少不必要查库
    }

    @Override
    public PersonnelRuleVO formaFieldAttributeInfoVO() {
        return null;
    }

    @Override
    public void setSupportBusinessObjects() {
        addSupportBusinessObjects(BpmnNodeAdpConfEnum.ADP_CONF_NODE_PROPERTY_UDR_USERS);
    }
}
