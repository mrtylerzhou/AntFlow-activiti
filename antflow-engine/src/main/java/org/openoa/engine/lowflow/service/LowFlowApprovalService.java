package org.openoa.engine.lowflow.service;

import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.google.common.base.Strings;
import org.openoa.base.constant.StringConstants;
import org.openoa.base.exception.JiMuBizException;
import org.openoa.base.interf.ActivitiService;
import org.openoa.base.interf.ActivitiServiceAnno;
import org.openoa.base.interf.FormOperationAdaptor;
import org.openoa.base.util.SecurityUtils;
import org.openoa.base.vo.BpmnStartConditionsVo;
import org.openoa.engine.bpmnconf.confentity.BpmnConfLfFormdataField;
import org.openoa.engine.bpmnconf.service.BpmnConfLfFormdataFieldServiceImpl;
import org.openoa.engine.bpmnconf.service.impl.LFMainFieldServiceImpl;
import org.openoa.engine.bpmnconf.service.impl.LFMainServiceImpl;
import org.openoa.engine.lowflow.entity.LFMain;
import org.openoa.engine.lowflow.entity.LFMainField;
import org.openoa.engine.lowflow.vo.UDLFApplyVo;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ActivitiServiceAnno(svcName = StringConstants.LOWFLOW_FORM_CODE,desc = "拖拽表单低代码审批流")
public class LowFlowApprovalService implements FormOperationAdaptor<UDLFApplyVo>, ActivitiService {
    //key is confid,value is a list of condition fields names which belongs to this conf
    private static Map<Long,List<String>> conditionFieldNameMap=new HashMap<>();
    // key is confid,value is a map of field's name and its self
    private static Map<Long,Map<String,BpmnConfLfFormdataField>> allFieldConfMap =new HashMap<>();
    @Autowired
    private BpmnConfLfFormdataFieldServiceImpl lfFormdataFieldService;
    @Autowired
    private LFMainFieldServiceImpl mainFieldService;
    @Autowired
    private LFMainServiceImpl mainService;

    @Override
    public BpmnStartConditionsVo previewSetCondition(UDLFApplyVo vo) {
        String userId =  vo.getStartUserId();
        return BpmnStartConditionsVo.builder()
                .startUserId(userId)
                .lfConditions(filterConditionFields(vo))
                .build();
    }

    @Override
    public UDLFApplyVo initData(UDLFApplyVo vo) {
        return null;
    }

    @Override
    public BpmnStartConditionsVo launchParameters(UDLFApplyVo vo) {
        String userId =  vo.getStartUserId();
        return BpmnStartConditionsVo.builder()
                .startUserId(userId)
                .lfConditions(filterConditionFields(vo))
                .build();
    }

    @Override
    public UDLFApplyVo queryData(String businessId) {
        return null;
    }

    @Override
    public UDLFApplyVo submitData(UDLFApplyVo vo) {
        Map<String, Object> lfFields = vo.getLfFields();
        if(CollectionUtils.isEmpty(lfFields)){
            throw new JiMuBizException("form data does not contains any field");
        }
        LFMain main=new LFMain();
        main.setCreateUser(SecurityUtils.getLogInEmpName());
        mainService.save(main);
        Long confId = vo.getBpmnConfVo().getId();
        Map<String, BpmnConfLfFormdataField> fieldConfMap = allFieldConfMap.get(confId);
        if(CollectionUtils.isEmpty(fieldConfMap)){
            throw  new JiMuBizException(Strings.lenientFormat("confId %d,formCode:%s does not has a field config",confId,vo.getFormCode()));
        }
        List<LFMainField> mainFields = LFMainField.parseFromMap(lfFields, fieldConfMap, main.getId());
        mainFieldService.saveBatch(mainFields);
        return null;
    }

    @Override
    public UDLFApplyVo consentData(UDLFApplyVo vo) {
        return null;
    }

    @Override
    public void backToModifyData(UDLFApplyVo vo) {

    }

    @Override
    public void cancellationData(String businessId) {

    }

    @Override
    public void finishData(String businessId) {

    }
    private Map<String,Object> filterConditionFields(UDLFApplyVo vo){
        Long confId = vo.getBpmnConfVo().getId();
        List<String> conditionFieldNames = conditionFieldNameMap.get(confId);
        //put values into cache
        if(CollectionUtils.isEmpty(conditionFieldNames)){

            List<BpmnConfLfFormdataField> allFields = lfFormdataFieldService.list(Wrappers.<BpmnConfLfFormdataField>lambdaQuery()
                    .eq(BpmnConfLfFormdataField::getBpmnConfId, confId));
            if(CollectionUtils.isEmpty(allFields)){
               throw new JiMuBizException("lowcode form data has no fields");
            }

            List<String> condFieldNames=new ArrayList<>();
            Map<String,BpmnConfLfFormdataField> name2SelfMap=new HashMap<>();
            for (BpmnConfLfFormdataField field : allFields) {
                String fieldName = field.getFieldName();
                name2SelfMap.put(fieldName,field);
                Integer const1=1;
                if(const1.equals(field.getIsConditionField())){
                    condFieldNames.add(fieldName);
                }
            }
            conditionFieldNameMap.put(confId,condFieldNames);
            allFieldConfMap.put(confId,name2SelfMap);
        }

        Map<String,Object>conditionFieldMap=null;

        //if it is still empty here,it indicates that this approval has no condition fields
        if(!CollectionUtils.isEmpty(conditionFieldNames)){
                conditionFieldMap=new HashMap<>();
                Map<String, Object> lfFields = vo.getLfFields();
                for (Map.Entry<String, Object> stringObjectEntry : lfFields.entrySet()) {
                    String key = stringObjectEntry.getKey();
                    if (conditionFieldNames.contains(key)) {
                        conditionFieldMap.put(key,stringObjectEntry.getValue());
                    }
            }
        }
        //condition fields can not be greater than 1 at the moment
        if(!CollectionUtils.isEmpty(conditionFieldMap) &&conditionFieldMap.size()>1){
            throw new JiMuBizException("conditionFields size can not greater than 1");
        }
        return conditionFieldMap;
    }
}
