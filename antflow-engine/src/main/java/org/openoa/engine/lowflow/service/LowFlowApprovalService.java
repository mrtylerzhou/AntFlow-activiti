package org.openoa.engine.lowflow.service;

import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.google.common.base.Strings;
import org.apache.commons.lang3.StringUtils;
import org.openoa.base.constant.StringConstants;
import org.openoa.base.constant.enums.ButtonTypeEnum;
import org.openoa.base.constant.enums.LFFieldTypeEnum;
import org.openoa.base.exception.JiMuBizException;
import org.openoa.base.interf.ActivitiService;
import org.openoa.base.interf.ActivitiServiceAnno;
import org.openoa.base.interf.FormOperationAdaptor;
import org.openoa.base.util.DateUtil;
import org.openoa.base.util.SecurityUtils;
import org.openoa.base.util.SnowFlake;
import org.openoa.base.vo.BpmnStartConditionsVo;
import org.openoa.base.vo.BusinessDataVo;
import org.openoa.engine.bpmnconf.confentity.BpmnConfLfFormdata;
import org.openoa.engine.bpmnconf.confentity.BpmnConfLfFormdataField;
import org.openoa.engine.bpmnconf.service.BpmnConfLfFormdataFieldServiceImpl;
import org.openoa.engine.bpmnconf.service.impl.BpmnConfLfFormdataServiceImpl;
import org.openoa.engine.bpmnconf.service.impl.LFMainFieldServiceImpl;
import org.openoa.engine.bpmnconf.service.impl.LFMainServiceImpl;
import org.openoa.engine.lowflow.entity.LFMain;
import org.openoa.engine.lowflow.entity.LFMainField;
import org.openoa.engine.lowflow.service.hooks.LFProcessFinishHook;
import org.openoa.engine.lowflow.vo.UDLFApplyVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;
import java.util.stream.Collectors;
/**
 * desc = 拖拽表单低代码审批流
 * */
@ActivitiServiceAnno(svcName = StringConstants.LOWFLOW_FORM_CODE,desc = "")
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
    @Autowired(required = false)
    private List<LFProcessFinishHook> lfProcessFinishHooks;
    @Autowired
    private BpmnConfLfFormdataServiceImpl lfFormdataService;

    @Override
    public BpmnStartConditionsVo previewSetCondition(UDLFApplyVo vo) {
        String userId =  vo.getStartUserId();
        BpmnStartConditionsVo startConditionsVo = BpmnStartConditionsVo.builder()
                .isLowCodeFlow(true)
                .startUserId(userId)
                .build();
        if(!CollectionUtils.isEmpty(vo.getLfConditions())){
            startConditionsVo.setLfConditions(vo.getLfConditions());
        }else {
            startConditionsVo.setLfConditions(vo.getLfFields());
        }
        return startConditionsVo;
    }

    @Override
    public UDLFApplyVo initData(UDLFApplyVo vo) {
        return null;
    }

    @Override
    public BpmnStartConditionsVo launchParameters(UDLFApplyVo vo) {
        String userId =  vo.getStartUserId();
        BpmnStartConditionsVo startConditionsVo = BpmnStartConditionsVo.builder()
                .isLowCodeFlow(true)
                .startUserId(userId)
                .build();
        if(!CollectionUtils.isEmpty(vo.getLfConditions())){
            startConditionsVo.setLfConditions(vo.getLfConditions());
        }else {
            startConditionsVo.setLfConditions(vo.getLfFields());
        }
        return startConditionsVo;
    }

    @Override
    public UDLFApplyVo queryData(UDLFApplyVo vo) {
        LFMain lfMain = mainService.getById(vo.getBusinessId());
        if(lfMain==null){
            log.error("can not get lowcode from data by specified Id:{}",vo.getBusinessId());
            throw new JiMuBizException("can not get lowcode form data by specified id");
        }
        Long mainId = lfMain.getId();
        Long confId = lfMain.getConfId();
        String formCode = lfMain.getFormCode();

        Map<String, BpmnConfLfFormdataField> lfFormdataFieldMap = allFieldConfMap.get(confId);
        if(CollectionUtils.isEmpty(lfFormdataFieldMap)){
            Map<String, BpmnConfLfFormdataField> Id2SelfMap = lfFormdataFieldService.qryFormDataFieldMap(confId);
            allFieldConfMap.put(confId,Id2SelfMap);
        }
        lfFormdataFieldMap=allFieldConfMap.get(confId);
        List<LFMainField> lfMainFields = mainFieldService.list(Wrappers.<LFMainField>lambdaQuery().eq(LFMainField::getMainId, mainId));
        if(CollectionUtils.isEmpty(lfMainFields)){
            throw  new JiMuBizException(Strings.lenientFormat("lowcode form with formcode:%s,confid:%s has no formdata",formCode,confId));
        }
        //returned to page for presenting
        Map<String,Object> fieldVoMap=new HashMap<>(lfMainFields.size());
        Map<String, List<LFMainField>> fieldName2SelfMap = lfMainFields.stream().collect(Collectors.groupingBy(LFMainField::getFieldId));
        for (Map.Entry<String, List<LFMainField>> Id2SelfEntry : fieldName2SelfMap.entrySet()) {
            String fieldName = Id2SelfEntry.getKey();
            BpmnConfLfFormdataField currentFieldProp = lfFormdataFieldMap.get(fieldName);
            if(currentFieldProp==null){
                throw new JiMuBizException(Strings.lenientFormat("field with name:%s has no property",fieldName));
            }
            List<LFMainField> fields = Id2SelfEntry.getValue();
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
                      if(actualValue!=null){
                          String actualValueString = actualValue.toString();
                          if(actualValueString.startsWith("{")){
                              actualValue=JSON.parseObject(actualValueString);
                          }else if(actualValueString.startsWith("[")){
                              actualValue=JSON.parseArray(actualValueString);
                          }
                      }
                        break;
                    case NUMBER:
                       actualValue=Integer.parseInt(field.getFieldValue());;
                        break;
                    case DATE:
                       actualValue=DateUtil.SDF_DATETIME_PATTERN.format(field.getFieldValueDt());
                        break;
                    case DATE_TIME:
                        actualValue=DateUtil.SDF_DATE_PATTERN.format(field.getFieldValueDt());
                        break;
                    case TEXT:
                       actualValue=field.getFieldValueText();
                       break;
                    case BOOLEAN:
                        actualValue=Boolean.parseBoolean(field.getFieldValue());
                        break;
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
        vo.setLfFields(fieldVoMap);

        List<BpmnConfLfFormdata> bpmnConfLfFormdataList = lfFormdataService.list(Wrappers.<BpmnConfLfFormdata>lambdaQuery().eq(BpmnConfLfFormdata::getBpmnConfId, confId));
        if(CollectionUtils.isEmpty(bpmnConfLfFormdataList)){
            throw  new JiMuBizException(Strings.lenientFormat("can not get lowcode flow formdata by confId:%s",confId));
        }
        BpmnConfLfFormdata lfFormdata = bpmnConfLfFormdataList.get(0);
        vo.setLfFormData(lfFormdata.getFormdata());
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
        main.setId(SnowFlake.nextId());
        main.setConfId(confId);
        main.setFormCode(formCode);
        main.setCreateUser(SecurityUtils.getLogInEmpName());
        mainService.save(main);
        Long mainId = main.getId();

        Map<String, BpmnConfLfFormdataField> lfFormdataFieldMap = allFieldConfMap.get(confId);
        if(CollectionUtils.isEmpty(lfFormdataFieldMap)){
            Map<String, BpmnConfLfFormdataField> name2SelfMap = lfFormdataFieldService.qryFormDataFieldMap(confId);
            allFieldConfMap.put(confId,name2SelfMap);
        }
        Map<String, BpmnConfLfFormdataField> fieldConfMap = allFieldConfMap.get(confId);
        if(CollectionUtils.isEmpty(fieldConfMap)){
            throw  new JiMuBizException(Strings.lenientFormat("confId %s,formCode:%s does not has a field config",confId,vo.getFormCode()));
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
        if (!vo.getOperationType().equals(ButtonTypeEnum.BUTTON_TYPE_RESUBMIT.getCode())){
            return vo;
        }
        Map<String, Object> lfFields = vo.getLfFields();
        if(CollectionUtils.isEmpty(lfFields)){
            throw new JiMuBizException("form data does not contains any field");
        }
        LFMain lfMain = mainService.getById(vo.getBusinessId());
        if(lfMain==null){
            log.error("can not get lowcode from data by specified Id:{}",vo.getBusinessId());
            throw new JiMuBizException("can not get lowcode form data by specified id");
        }
        Long mainId = lfMain.getId();
        String formCode = vo.getFormCode();
        Long confId = vo.getBpmnConfVo().getId();
        List<LFMainField> lfMainFields = mainFieldService.list(Wrappers.<LFMainField>lambdaQuery().eq(LFMainField::getMainId, mainId));
        if(CollectionUtils.isEmpty(lfMainFields)){
            throw  new JiMuBizException(Strings.lenientFormat("lowcode form with formcode:%s,confid:%s has no formdata",formCode,confId));
        }
        for (LFMainField field : lfMainFields){
            String f_value = lfFields.get(field.getFieldId()).toString();
            field.setFieldValue(f_value);
            mainFieldService.updateById(field);
        }
        return vo;
    }

    @Override
    public void backToModifyData(UDLFApplyVo vo) {

    }

    @Override
    public void cancellationData(UDLFApplyVo vo) {

    }

    @Override
    public void finishData(BusinessDataVo vo) {
        if(CollectionUtils.isEmpty(lfProcessFinishHooks)){
            return;
        }
        for (LFProcessFinishHook lfProcessFinishHook : lfProcessFinishHooks) {
            lfProcessFinishHook.onFinishData(vo);
        }
    }
    private Map<String,Object> filterConditionFields(UDLFApplyVo vo){
        Long confId = vo.getBpmnConfVo().getId();
        List<String> conditionFieldNames = conditionFieldNameMap.get(confId);
        Map<String,Object>conditionFieldMap=null;
        //put values into cache
        if(CollectionUtils.isEmpty(conditionFieldNames)){
            Map<String, Object> lfConditions = vo.getLfConditions();
            if(!CollectionUtils.isEmpty(lfConditions)){
                conditionFieldMap=lfConditions;
            }
            List<BpmnConfLfFormdataField> allFields = lfFormdataFieldService.list(Wrappers.<BpmnConfLfFormdataField>lambdaQuery()
                    .eq(BpmnConfLfFormdataField::getBpmnConfId, confId));
            if(CollectionUtils.isEmpty(allFields)){
               throw new JiMuBizException("lowcode form data has no fields");
            }

            List<String> condFieldNames=new ArrayList<>();
            Map<String,BpmnConfLfFormdataField> Id2SelfMap=new HashMap<>();
            for (BpmnConfLfFormdataField field : allFields) {
                String fieldId = field.getFieldId();
                Id2SelfMap.put(fieldId,field);
                if(field.getIsConditionField()!=null&&field.getIsConditionField()==1){
                    condFieldNames.add(fieldId);
                }
            }
            conditionFieldNameMap.put(confId,condFieldNames);
           if(!allFieldConfMap.containsKey(confId)){
               allFieldConfMap.put(confId,Id2SelfMap);
           }
        }
        conditionFieldNames=conditionFieldNameMap.get(confId);


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
