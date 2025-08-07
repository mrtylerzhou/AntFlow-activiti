package org.openoa.engine.bpmnconf.service.biz.personnelinfoprovider;

import org.openoa.base.exception.AFBizException;
import org.openoa.base.interf.BpmnPersonnelProviderService;
import org.openoa.base.vo.BpmnNodeParamsAssigneeVo;
import org.openoa.base.vo.BpmnNodePropertysVo;
import org.openoa.base.vo.BpmnNodeVo;
import org.openoa.base.vo.BpmnStartConditionsVo;
import org.openoa.engine.bpmnconf.adp.personneladp.AbstractBusinessConfigurationAdaptor;
import org.openoa.engine.bpmnconf.constant.enus.BusinessConfTableFieldEnum;
import org.openoa.engine.bpmnconf.constant.enus.ConfigurationTableAdapterEnum;
import org.openoa.engine.factory.IAdaptorFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Author TylerZhou
 * @Date 2024/7/28 8:00
 * @Version 1.0
 */
@Service
public class BusinessTablePersonnelProvider implements BpmnPersonnelProviderService {
    @Autowired
    private IAdaptorFactory adaptorFactory;
    @Override
    public List<BpmnNodeParamsAssigneeVo> getAssigneeList(BpmnNodeVo bpmnNodeVo, BpmnStartConditionsVo startConditionsVo) {
        BpmnNodePropertysVo property = bpmnNodeVo.getProperty();
        if(property==null){
            throw new AFBizException("property can not be null");
        }
        Integer configurationTableType = property.getConfigurationTableType();
        Integer tableFieldType = property.getTableFieldType();
        if(tableFieldType==null){
            throw new AFBizException("table field type can not be null!");
        }
        BusinessConfTableFieldEnum tableFieldEnumByCode = BusinessConfTableFieldEnum.getTableFieldEnumByCode(tableFieldType);
        if(tableFieldEnumByCode==null){
            throw new AFBizException("can not find BusinessConfTableFieldEnum by given fieldType");
        }
        ConfigurationTableAdapterEnum byTableFieldEnum = ConfigurationTableAdapterEnum.getByTableFieldEnum(tableFieldEnumByCode);
        if(byTableFieldEnum==null){
            throw new AFBizException("can not find ConfigurationTableAdapterEnum by given fieldType");
        }
        AbstractBusinessConfigurationAdaptor businessConfigurationAdaptor = adaptorFactory.getBusinessConfigurationAdaptor(byTableFieldEnum);
        List<BpmnNodeParamsAssigneeVo> bpmnNodeParamsAssigneeVos = businessConfigurationAdaptor.doFindBusinessPerson(bpmnNodeVo, startConditionsVo);
        return bpmnNodeParamsAssigneeVos;
    }
}
