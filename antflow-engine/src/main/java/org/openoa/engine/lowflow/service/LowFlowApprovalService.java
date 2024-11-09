package org.openoa.engine.lowflow.service;

import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.google.common.base.Strings;
import org.openoa.base.constant.StringConstants;
import org.openoa.base.constant.enums.LFFieldTypeEnum;
import org.openoa.base.exception.JiMuBizException;
import org.openoa.base.interf.ActivitiService;
import org.openoa.base.interf.ActivitiServiceAnno;
import org.openoa.base.interf.FormOperationAdaptor;
import org.openoa.base.util.DateUtil;
import org.openoa.base.util.SecurityUtils;
import org.openoa.base.vo.BpmnStartConditionsVo;
import org.openoa.engine.bpmnconf.confentity.BpmnConfLfFormdataField;
import org.openoa.engine.bpmnconf.service.BpmnConfLfFormdataFieldServiceImpl;
import org.openoa.engine.bpmnconf.service.impl.LFMainFieldServiceImpl;
import org.openoa.engine.bpmnconf.service.impl.LFMainServiceImpl;
import org.openoa.engine.lowflow.entity.LFMain;
import org.openoa.engine.lowflow.entity.LFMainField;
import org.openoa.engine.lowflow.vo.UDLFApplyVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;
import java.util.stream.Collectors;

@ActivitiServiceAnno(svcName = StringConstants.LOWFLOW_FORM_CODE,desc = "拖拽表单低代码审批流")
public class LowFlowApprovalService implements FormOperationAdaptor<UDLFApplyVo>, ActivitiService {
    private static final Logger log = LoggerFactory.getLogger(LowFlowApprovalService.class);
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
        LFMain lfMain = mainService.getById(businessId);
        if(lfMain==null){
            log.error("can not get lowcode from data by specified Id:{}",businessId);
            throw new JiMuBizException("can not get lowcode form data by specified id");
        }
        Long mainId = lfMain.getId();
        Long confId = lfMain.getConfId();
        String formCode = lfMain.getFormCode();
        //todo check allFieldConfMap in existence
        Map<String, BpmnConfLfFormdataField> lfFormdataFieldMap = allFieldConfMap.get(mainId);
        if(CollectionUtils.isEmpty(lfFormdataFieldMap)){
            List<BpmnConfLfFormdataField> allFields = lfFormdataFieldService.list(Wrappers.<BpmnConfLfFormdataField>lambdaQuery()
                    .eq(BpmnConfLfFormdataField::getBpmnConfId, confId));
            if(CollectionUtils.isEmpty(allFields)){
                throw new JiMuBizException("lowcode form data has no fields");
            }
            Map<String,BpmnConfLfFormdataField> name2SelfMap=new HashMap<>();
            for (BpmnConfLfFormdataField field : allFields) {
                String fieldName = field.getFieldName();
                name2SelfMap.put(fieldName,field);
            }
            allFieldConfMap.put(confId,name2SelfMap);
        }
        lfFormdataFieldMap=allFieldConfMap.get(mainId);
        List<LFMainField> lfMainFields = mainFieldService.list(Wrappers.<LFMainField>lambdaQuery().eq(LFMainField::getMainId, mainId));
        if(CollectionUtils.isEmpty(lfMainFields)){
            throw  new JiMuBizException(Strings.lenientFormat("lowcode form with formcode:%s,confid:%d has no formdata",formCode,confId));
        }
        //returned to page for presenting
        Map<String,Object> fieldVoMap=new HashMap<>(lfMainFields.size());
        Map<String, List<LFMainField>> fieldName2SelfMap = lfMainFields.stream().collect(Collectors.groupingBy(LFMainField::getFieldName));
        for (Map.Entry<String, List<LFMainField>> name2SelfEntry : fieldName2SelfMap.entrySet()) {
            String fieldName = name2SelfEntry.getKey();
            BpmnConfLfFormdataField currentFieldProp = lfFormdataFieldMap.get(fieldName);
            if(currentFieldProp==null){
                throw new JiMuBizException(Strings.lenientFormat("field with name:%s has no property",fieldName));
            }
            List<LFMainField> fields = name2SelfEntry.getValue();
            int valueLen = fields.size();
            List<Object> actualMultiValue=valueLen==1?null:new ArrayList<>(valueLen);
            for (LFMainField field : fields) {
                Integer fieldType = currentFieldProp.getFieldType();
                LFFieldTypeEnum fieldTypeEnum = LFFieldTypeEnum.getByType(fieldType);
                if(fieldTypeEnum==null){
                    throw new JiMuBizException(Strings.lenientFormat("unrecognized field type,name:%s,formcode:%s,confId:%d",fieldName,formCode,confId));
                }
                Object actualValue=null;
                switch (fieldTypeEnum){
                    case STRING:
                      actualValue=field.getFieldValue();
                        break;
                    case NUMBER:
                       actualValue=field.getFieldValueNumber();
                        break;
                    case DATE:
                       actualValue=DateUtil.SDF_DATETIME_PATTERN.format(field.getFieldValueDt());
                        break;
                    case TEXT:
                       actualValue=field.getFieldValueText();
                }
                if(valueLen==1){
                    fieldVoMap.put(fieldName,actualValue);
                    break;
                }
                actualMultiValue.add(actualValue);
            }
            if(!CollectionUtils.isEmpty(actualMultiValue)){
                fieldVoMap.put(fieldName,actualMultiValue);
            }
        }
        UDLFApplyVo vo=new UDLFApplyVo();
        vo.setLfFields(fieldVoMap);
        return vo;
    }

    @Override
    public UDLFApplyVo submitData(UDLFApplyVo vo) {
        Map<String, Object> lfFields = vo.getLfFields();
        if(CollectionUtils.isEmpty(lfFields)){
            throw new JiMuBizException("form data does not contains any field");
        }
        Long confId = vo.getBpmnConfVo().getId();
        String formCode = vo.getFormCode();
        LFMain main=new LFMain();
        main.setConfId(confId);
        main.setFormCode(formCode);
        main.setCreateUser(SecurityUtils.getLogInEmpName());
        mainService.save(main);
        Long mainId = main.getId();

        Map<String, BpmnConfLfFormdataField> fieldConfMap = allFieldConfMap.get(confId);
        if(CollectionUtils.isEmpty(fieldConfMap)){
            throw  new JiMuBizException(Strings.lenientFormat("confId %d,formCode:%s does not has a field config",confId,vo.getFormCode()));
        }
        List<LFMainField> mainFields = LFMainField.parseFromMap(lfFields, fieldConfMap, mainId);
        mainFieldService.saveBatch(mainFields);
        vo.setBusinessId(mainId.toString());
        vo.setProcessDigest(vo.getRemark());
        vo.setEntityName(LowFlowApprovalService.class.getSimpleName());
        return vo;
    }

    @Override
    public UDLFApplyVo consentData(UDLFApplyVo vo) {
        return vo;
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
