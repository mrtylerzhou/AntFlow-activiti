package org.openoa.engine.lowflow.service;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.google.common.base.Strings;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.openoa.base.constant.StringConstants;
import org.openoa.base.constant.enums.BpmnConfFlagsEnum;
import org.openoa.base.constant.enums.ButtonTypeEnum;
import org.openoa.base.constant.enums.LFControlTypeEnum;
import org.openoa.base.constant.enums.LFFieldTypeEnum;
import org.openoa.base.entity.BpmnNodeFormRelatedUserConf;
import org.openoa.base.exception.AFBizException;
import org.openoa.base.exception.BusinessErrorEnum;
import org.openoa.base.interf.ActivitiService;
import org.openoa.base.interf.ActivitiServiceAnno;
import org.openoa.base.interf.FormOperationAdaptor;
import org.openoa.base.util.DateUtil;
import org.openoa.base.util.MultiTenantUtil;
import org.openoa.base.util.SecurityUtils;
import org.openoa.base.util.SnowFlake;
import org.openoa.base.vo.*;
import org.openoa.base.entity.BpmnConfLfFormdata;
import org.openoa.base.entity.BpmnConfLfFormdataField;
import org.openoa.engine.bpmnconf.service.impl.BpmnConfLfFormdataFieldServiceImpl;
import org.openoa.engine.bpmnconf.service.impl.BpmnConfLfFormdataServiceImpl;
import org.openoa.engine.bpmnconf.service.impl.LFMainFieldServiceImpl;
import org.openoa.engine.bpmnconf.service.impl.LFMainServiceImpl;
import org.openoa.engine.bpmnconf.service.interf.repository.*;
import org.openoa.engine.lowflow.entity.LFMain;
import org.openoa.engine.lowflow.entity.LFMainField;
import org.openoa.engine.lowflow.vo.UDLFApplyVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private BpmnConfLfFormdataFieldService lfFormdataFieldService;
    @Autowired
    private LFMainFieldService mainFieldService;
    @Autowired
    private LFMainService mainService;
    @Autowired
    private BpmnConfLfFormdataService lfFormdataService;
    @Autowired
    private BpmnNodeFormRelatedUserConfService bpmnNodeFormRelatedUserConfService;

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
    public void initData(UDLFApplyVo vo) {

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
    public void queryData(UDLFApplyVo vo) {
        LFMain lfMain = mainService.getById(vo.getBusinessId());
        if(lfMain==null){
            log.error("can not get lowcode from data by specified Id:{}",vo.getBusinessId());
            throw new AFBizException("can not get lowcode form data by specified id");
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
            throw  new AFBizException(Strings.lenientFormat("lowcode form with formcode:%s,confid:%s has no formdata",formCode,confId));
        }
        //returned to page for presenting
        Map<String,Object> fieldVoMap=new HashMap<>(lfMainFields.size());
        Map<String, List<LFMainField>> fieldName2SelfMap = lfMainFields.stream().collect(Collectors.groupingBy(LFMainField::getFieldId));
        for (Map.Entry<String, List<LFMainField>> Id2SelfEntry : fieldName2SelfMap.entrySet()) {
            String fieldName = Id2SelfEntry.getKey();
            BpmnConfLfFormdataField currentFieldProp = lfFormdataFieldMap.get(fieldName);
            if(currentFieldProp==null){
                throw new AFBizException(Strings.lenientFormat("field with name:%s has no property",fieldName));
            }
            List<LFMainField> fields = Id2SelfEntry.getValue();
            int valueLen = fields.size();
            List<Object> actualMultiValue=valueLen==1?null:new ArrayList<>(valueLen);
            for (LFMainField field : fields) {
                Integer fieldType = currentFieldProp.getFieldType();
                LFFieldTypeEnum fieldTypeEnum = LFFieldTypeEnum.getByType(fieldType);
                if(fieldTypeEnum==null){
                    throw new AFBizException(Strings.lenientFormat("unrecognized field type,name:%s,formcode:%s,confId:%d",fieldName,formCode,confId));
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
                        if(LFControlTypeEnum.SELECT.getName().equals(currentFieldProp.getFieldName())){
                           try {
                               Object parse = JSON.parse(field.getFieldValue());
                               if (parse==null){
                                   actualValue="";//select默认值为空字符串
                               }else if(parse instanceof JSONArray){
                                   actualValue=JSON.parseArray(field.getFieldValue());
                               }else{
                                   actualValue=parse;
                               }
                           }catch (Exception e){//如果本身是字符串类型,不能反序列化,直接取原来值
                               log.warn("field value can not be parsed to number,fieldName:{},formCode:{},confId:{}",fieldName,formCode,confId);
                               actualValue=field.getFieldValue();
                           }
                        }else{//以上对select做了特殊处理,如果不是select,直接取值
                            actualValue=field.getFieldValueNumber();
                        }
                        break;
                    case DATE_TIME:
                       actualValue=DateUtil.SDF_DATETIME_PATTERN.format(field.getFieldValueDt());
                        break;
                    case DATE:
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
            throw  new AFBizException(Strings.lenientFormat("can not get lowcode flow formdata by confId:%s",confId));
        }
        BpmnConfLfFormdata lfFormdata = bpmnConfLfFormdataList.get(0);
        vo.setLfFormData(lfFormdata.getFormdata());

    }

    @Override
    public void submitData(UDLFApplyVo vo) {
        Map<String, Object> lfFields = vo.getLfFields();
        if(CollectionUtils.isEmpty(lfFields)){
            throw new AFBizException("form data does not contains any field");
        }
        BpmnConfVo bpmnConfVo = vo.getBpmnConfVo();
        Long confId =bpmnConfVo.getId();
        String formCode = vo.getFormCode();
        String currentTenantId = MultiTenantUtil.getCurrentTenantId();
        LFMain main = new LFMain();
        main.setTenantId(currentTenantId);
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
            throw  new AFBizException(Strings.lenientFormat("confId %s,formCode:%s does not has a field config",confId,vo.getFormCode()));
        }
        List<LFMainField> mainFields = LFMainField.parseFromMap(lfFields, fieldConfMap, mainId,formCode);
        mainFieldService.saveBatch(mainFields);
        vo.setBusinessId(mainId.toString());
        vo.setProcessDigest(vo.getRemark());
        vo.setEntityName(LowFlowApprovalService.class.getSimpleName());
        Integer extraFlags = bpmnConfVo.getExtraFlags();
        if (extraFlags != null && BpmnConfFlagsEnum.hasFlag(extraFlags, BpmnConfFlagsEnum.HAS_FORM_RELATED_ASSIGNEES)) {
            List<BpmnNodeFormRelatedUserConf> bpmnNodeFormRelatedUserConfs = bpmnNodeFormRelatedUserConfService.getMapper().queryByConfId(confId);
            if (CollectionUtils.isEmpty(bpmnNodeFormRelatedUserConfs)) {
                throw new AFBizException(BusinessErrorEnum.CAN_NOT_GET_VALUE_FROM_DB);
            }
            Map<String, List<String>> node2formRelatedAssignees = new HashMap<>();
            for (BpmnNodeFormRelatedUserConf bpmnNodeFormRelatedUserConf : bpmnNodeFormRelatedUserConfs) {
                Long bpmnNodeId = bpmnNodeFormRelatedUserConf.getBpmnNodeId();
                String valueJson = bpmnNodeFormRelatedUserConf.getValueJson();
                if (StringUtils.isEmpty(valueJson)) {
                    throw new AFBizException(BusinessErrorEnum.PARAMS_IS_NULL);
                }
                List<BaseIdTranStruVo> formInfos = JSON.parseArray(valueJson, BaseIdTranStruVo.class);
                List<String> formValues = new ArrayList<>();
                for (BaseIdTranStruVo formInfo : formInfos) {
                    String formName=formInfo.getId();
                    //用于存储人员相关的表单一般是下拉框,值可能是单个,也可能是数组
                    Object formVal = lfFields.get(formName);
                    if (formVal instanceof Iterable) {
                        Iterable iterablef = (Iterable) formVal;
                        Iterator iteratorf = iterablef.iterator();
                        while (iteratorf.hasNext()) {
                            Object bValue = iteratorf.next();
                            formValues.add(bValue.toString());
                        }
                    }else{
                        formValues.add(formVal.toString());
                    }
                }
                node2formRelatedAssignees.put(bpmnNodeId.toString(),formValues);
            }
            vo.setNode2formRelatedAssignees(node2formRelatedAssignees);
        }

    }

    @Override
    public void consentData(UDLFApplyVo vo) {
        if (!vo.getOperationType().equals(ButtonTypeEnum.BUTTON_TYPE_RESUBMIT.getCode()) && !vo.getOperationType().equals(ButtonTypeEnum.BUTTON_TYPE_AGREE.getCode()) ){
            return ;
        }
        Map<String, Object> lfFields = vo.getLfFields();
        if(CollectionUtils.isEmpty(lfFields)){
            throw new AFBizException("form data does not contains any field");
        }
        LFMain lfMain = mainService.getById(vo.getBusinessId());
        if(lfMain==null){
            log.error("can not get lowcode from data by specified Id:{}",vo.getBusinessId());
            throw new AFBizException("can not get lowcode form data by specified id");
        }
        Long mainId = lfMain.getId();
        String formCode = vo.getFormCode();
        Long confId = vo.getBpmnConfVo().getId();
        List<LFMainField> lfMainFields = mainFieldService.list(Wrappers.<LFMainField>lambdaQuery().eq(LFMainField::getMainId, mainId));
	    // 如果vo.getLfFields()里面有lfMainFields没有的元素，那么就将没有的元素save到LFMainField表中
	    Map<String, Object> submitLfFields = vo.getLfFields();
	    if (ObjectUtils.isNotEmpty(submitLfFields)) {
		    Map<String, BpmnConfLfFormdataField> lfFormdataFieldMap = allFieldConfMap.get(confId);
		    if (ObjectUtils.isEmpty(lfFormdataFieldMap)) {
			    Map<String, BpmnConfLfFormdataField> name2SelfMap = lfFormdataFieldService.qryFormDataFieldMap(confId);
			    allFieldConfMap.put(confId,name2SelfMap);
		    }
		    Map<String, BpmnConfLfFormdataField> fieldConfMap = allFieldConfMap.get(confId);
		    if (ObjectUtils.isEmpty(fieldConfMap)) {
			    throw new AFBizException(Strings.lenientFormat("confId %s,formCode:%s does not has a field config",confId,vo.getFormCode()));
		    }
		    List<LFMainField> mainFields = LFMainField.parseFromMap(submitLfFields, fieldConfMap, mainId, vo.getFormCode());
		    if (CollectionUtils.isNotEmpty(mainFields)) {
			    // 根据fieldId过滤掉已存在表里的数据lfMainFields
			    mainFields.removeIf(mainField -> lfMainFields.stream().anyMatch(ori -> ori.getFieldId().equals(mainField.getFieldId())));
			    mainFieldService.saveBatch(mainFields);
		    }
	    }
		if(CollectionUtils.isEmpty(lfMainFields)){
            throw  new AFBizException(Strings.lenientFormat("lowcode form with formcode:%s,confid:%s has no formdata",formCode,confId));
        }
        for (LFMainField field : lfMainFields){
            if (lfFields.containsKey(field.getFieldId()) && lfFields.get(field.getFieldId()) != null) {
                String f_value = lfFields.get(field.getFieldId()).toString();
                if (!Objects.equals(f_value, "******")){
                    field.setFieldValue(f_value);
                }
                mainFieldService.updateById(field);
            }
        }

    }

    @Override
    public void backToModifyData(UDLFApplyVo vo) {

    }

    @Override
    public void cancellationData(UDLFApplyVo vo) {

    }

    @Override
    public void finishData(BusinessDataVo vo) {

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
               throw new AFBizException("lowcode form data has no fields");
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
            throw new AFBizException("conditionFields size can not greater than 1");
        }
        return conditionFieldMap;
    }

}
