package org.openoa.engine.lowflow.service;

import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.google.common.collect.Maps;
import org.openoa.base.constant.StringConstants;
import org.openoa.base.interf.ActivitiService;
import org.openoa.base.interf.ActivitiServiceAnno;
import org.openoa.base.interf.FormOperationAdaptor;
import org.openoa.base.vo.BpmnStartConditionsVo;
import org.openoa.engine.bpmnconf.confentity.BpmnConfLfFormdataField;
import org.openoa.engine.bpmnconf.service.BpmnConfLfFormdataFieldServiceImpl;
import org.openoa.engine.lowflow.vo.UDLFApplyVo;
import org.springframework.beans.factory.annotation.Autowired;

import java.sql.Wrapper;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@ActivitiServiceAnno(svcName = StringConstants.LOWFLOW_FORM_CODE,desc = "拖拽表单低代码审批流")
public class LowFlowApprovalService implements FormOperationAdaptor<UDLFApplyVo>, ActivitiService {
    private static List<String> conditionFieldNames;
    @Autowired
    private BpmnConfLfFormdataFieldServiceImpl lfFormdataFieldService;

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
        if(CollectionUtils.isEmpty(conditionFieldNames)){
            Long confId = vo.getBpmnConfVo().getId();
            List<BpmnConfLfFormdataField> conditionFields = lfFormdataFieldService.list(Wrappers.<BpmnConfLfFormdataField>lambdaQuery()
                    .eq(BpmnConfLfFormdataField::getBpmnConfId, confId)
                    .eq(BpmnConfLfFormdataField::getIsConditionField, 1));
            if(!CollectionUtils.isEmpty(conditionFields)){
                conditionFieldNames=conditionFields.stream().map(BpmnConfLfFormdataField::getFieldName).collect(Collectors.toList());
            }
        }
        Map<String,Object>conditionFields=null;
        if(!CollectionUtils.isEmpty(conditionFieldNames)){
            conditionFields=new HashMap<>();
            Map<String, Object> lfFields = vo.getLfFields();
            for (Map.Entry<String, Object> stringObjectEntry : lfFields.entrySet()) {
                String key = stringObjectEntry.getKey();
                if (conditionFieldNames.contains(key)) {
                    conditionFields.put(key,stringObjectEntry.getValue());
                }
            }
        }
        return conditionFields;
    }
}
