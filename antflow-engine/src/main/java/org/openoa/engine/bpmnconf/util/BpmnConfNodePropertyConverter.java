package org.openoa.engine.bpmnconf.util;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.google.common.base.Joiner;
import jodd.util.StringUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.openoa.base.constant.StringConstants;
import org.openoa.base.util.DateUtil;
import org.openoa.engine.bpmnconf.constant.AntFlowConstants;
import org.openoa.engine.bpmnconf.constant.enus.ConditionTypeEnum;
import org.openoa.base.constant.enums.JudgeOperatorEnum;
import org.openoa.base.vo.*;
import org.openoa.base.exception.JiMuBizException;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.text.ParseException;
import java.util.*;

/**
 * @Author TylerZhou
 * @Date 2024/6/20 21:40
 * @Version 1.0
 */
public class BpmnConfNodePropertyConverter {
    public static BpmnNodeConditionsConfBaseVo fromVue3Model(BpmnNodePropertysVo propertysVo){

        if(propertysVo==null){
            throw new JiMuBizException("node has no property!");
        }
        List<BpmnNodeConditionsConfVueVo> newModels = propertysVo.getConditionList();

        Integer isDefault = propertysVo.getIsDefault();
        if(ObjectUtils.isEmpty(newModels)&&Objects.equals(isDefault,0)){
            throw new JiMuBizException("input nodes is empty");
        }
        BpmnNodeConditionsConfBaseVo result=new BpmnNodeConditionsConfBaseVo();
        //outer common property
        result.setIsDefault(propertysVo.getIsDefault());
        result.setSort(propertysVo.getSort());
        List<Integer> conditionTypes=new ArrayList<>(newModels.size());
        for (BpmnNodeConditionsConfVueVo newModel : newModels) {
            String columnId = newModel.getColumnId();
            if(columnId==null){
                throw new JiMuBizException("each and every node must have a columnId value");
            }
            int columnIdInt = Integer.parseInt(columnId);
            ConditionTypeEnum enumByCode = ConditionTypeEnum.getEnumByCode(columnIdInt);
            if(enumByCode==null){
                throw new JiMuBizException(String.format("columnId of value:%s is not a valid value",columnId));
            }
            conditionTypes.add(columnIdInt);
            String fieldName = enumByCode.getFieldName();
            String columnDbname = newModel.getColumnDbname();
            if(!fieldName.equals(columnDbname) && !StringUtil.isEmpty(columnDbname)){
                //if it is a lowcode flow condition,its name defined in ConditionTypeEnum is a constant,it is lfConditions,it is always not equals to the name specified
               if(!StringConstants.LOWFLOW_CONDITION_CONTAINER_FIELD_NAME.equals(fieldName)){
                   throw new JiMuBizException(String.format("columnDbname:%s is not a valid name",columnDbname));
               }
            }
            Integer fieldType = enumByCode.getFieldType();
            Class<?> fieldCls = enumByCode.getFieldCls();
            if(fieldType==1){//list
                String fixedDownBoxValue = newModel.getFixedDownBoxValue();
                List<BaseKeyValueStruVo> valueStruVoList = JSON.parseArray(fixedDownBoxValue,BaseKeyValueStruVo.class);
                String zdy1 = newModel.getZdy1();
                String[] keys = zdy1.split(",");
                List<Object> values=new ArrayList<>(keys.length);

                for (int i = 0; i < keys.length; i++) {
                    BaseKeyValueStruVo baseKeyValueStruVo = valueStruVoList.get(i);
                    if(fieldCls.equals(String.class)){
                        values.add(baseKeyValueStruVo.getKey());
                    }else{
                        Object parsedObject = JSON.parseObject(baseKeyValueStruVo.getKey(), fieldCls);
                        values.add(parsedObject);
                    }
                }
                Object valueOrWrapper=null;
                if(ConditionTypeEnum.isLowCodeFlow(enumByCode)){
                    Map<String,Object> wrapperResult=new HashMap<>();
                    wrapperResult.put(fieldName,values);
                    valueOrWrapper=wrapperResult;
                }
                Field field = FieldUtils.getField(BpmnNodeConditionsConfBaseVo.class, enumByCode.getFieldName(),true);
                ReflectionUtils.setField(field, result, valueOrWrapper!=null?valueOrWrapper:values);
            }else{
                String zdy1 = newModel.getZdy1();
                String zdy2=newModel.getZdy2();

                Field field = FieldUtils.getField(BpmnNodeConditionsConfBaseVo.class, enumByCode.getFieldName(),true);
                String opt1 = newModel.getOpt1();
                String opt2=newModel.getOpt2();
                Integer optType = newModel.getOptType();
                if(optType!=null){
                    JudgeOperatorEnum symbol = JudgeOperatorEnum.getByOpType(optType);
                    if(symbol==null){
                        throw new JiMuBizException(String.format("condition optype of %d is undefined!",optType));
                    }
                    Field opField = FieldUtils.getField(BpmnNodeConditionsConfBaseVo.class, AntFlowConstants.NUM_OPERATOR, true);
                    ReflectionUtils.setField(opField,result,symbol.getCode());
                }
                if(String.class.isAssignableFrom(fieldCls)){
                    Object valueOrWrapper=null;
                    //处理多值first<b<second这种类型
                    if(JudgeOperatorEnum.binaryOperator().contains(optType)){
                       zdy1=zdy1+","+zdy2;//antflow目前只有一个自定义值,介于之间的提前定义好JudgeOperatorEnum,值用字符串拼接,使用时再分割
                    }
                    if(ConditionTypeEnum.isLowCodeFlow(enumByCode)){
                        Map<String,Object> wrapperResult=new HashMap<>();
                        wrapperResult.put(fieldName,zdy1);
                        valueOrWrapper=wrapperResult;
                    }
                    ReflectionUtils.setField(field, result, valueOrWrapper!=null?valueOrWrapper:zdy1);
                }else{
                    Object valueOrWrapper=null;
                    Object actualValue=null;
                    if(enumByCode==ConditionTypeEnum.CONDITION_TYPE_LF_DATE_CONDITION){
                        try {
                            actualValue= DateUtil.SDF_DATE_PATTERN.parse(zdy1);
                        } catch (ParseException e) {
                            throw new RuntimeException(e);
                        }
                    }else if(enumByCode==ConditionTypeEnum.CONDITION_TYPE_LF_DATE_TIME_CONDITION){
                        try {
                            actualValue=DateUtil.SDF_DATETIME_PATTERN.parse(zdy1);
                        } catch (ParseException e) {
                            throw new RuntimeException(e);
                        }
                    }else{
                       actualValue= JSON.parseObject(zdy1,fieldCls);
                    }
                    if(ConditionTypeEnum.isLowCodeFlow(enumByCode)){
                        Map<String,Object> wrapperResult=new HashMap<>();
                        wrapperResult.put(fieldName,actualValue);
                        valueOrWrapper=wrapperResult;
                    }
                    ReflectionUtils.setField(field, result, valueOrWrapper!=null?valueOrWrapper:actualValue);
                }

            }

        }
        String extJson = JSON.toJSONString(newModels);
        result.setExtJson(extJson);
        result.setConditionParamTypes(conditionTypes);
        return result;
    }
    public static List<BpmnNodeConditionsConfVueVo> toVue3Model(BpmnNodeConditionsConfBaseVo baseVo){
        if(baseVo==null){
            throw new JiMuBizException("baseVo to convert is null");
        }
        if(Objects.equals(baseVo.getIsDefault(),1)){
            return  new ArrayList<>();
        }
        List<BpmnNodeConditionsConfVueVo> results=new ArrayList<>();
        List<Integer> conditionParamTypes = baseVo.getConditionParamTypes();
        for (Integer conditionParamType : conditionParamTypes) {
            BpmnNodeConditionsConfVueVo vueVo=new BpmnNodeConditionsConfVueVo();
            ConditionTypeEnum enumByCode = ConditionTypeEnum.getEnumByCode(conditionParamType);
            vueVo.setColumnDbname(enumByCode.getFieldName());
            Integer fieldType = enumByCode.getFieldType();
            vueVo.setShowName(enumByCode.getDesc());
            if(fieldType==1){

                Field field = FieldUtils.getField(BpmnNodeConditionsConfBaseVo.class, enumByCode.getFieldName(),true);
                Map<String,Collection<?>> wrappedValues=null;
                List objects =new ArrayList();
                if (ConditionTypeEnum.isLowCodeFlow(enumByCode)) {
                    //低代码多值条件是固定的,{"key":["a","b"]
                  wrappedValues =(Map<String,Collection<?>>)ReflectionUtils.getField(field, baseVo);
                    Collection<Collection<?>> values = wrappedValues.values();
                    for (Collection<?> value : values) {
                       objects.addAll(value);
                    }
                }else{
                    objects = (List<?>) ReflectionUtils.getField(field, baseVo);
                }
                String join = Joiner.on(",").join(objects);
                vueVo.setZdy1(join);
                Field extField = null;
                if (ConditionTypeEnum.isLowCodeFlow(enumByCode)){
                    extField=field;
                }else{
                    extField= FieldUtils.getField(BpmnNodeConditionsConfBaseVo.class, enumByCode.getFieldName()+"List",true);
                }


                List<BaseIdTranStruVo> extFields = null;
                if (ConditionTypeEnum.isLowCodeFlow(enumByCode)){
                    String extJson = baseVo.getExtJson();
                   if(!StringUtils.isEmpty(extJson)){
                       JSONArray jsonArray = JSON.parseArray(extJson);
                       JSONObject jsonObject = jsonArray.getJSONObject(0);
                       Object fixedDownBoxValue = jsonObject.get("fixedDownBoxValue");
                       if(fixedDownBoxValue!=null){
                            vueVo.setFixedDownBoxValue(fixedDownBoxValue.toString());
                       }
                   }
                }else{
                    extFields= (List<BaseIdTranStruVo>) ReflectionUtils.getField(extField, baseVo);
                }
                if(CollectionUtils.isEmpty(extFields)){
                    continue;
                }
                List<BaseKeyValueStruVo> keyValuePairVos=new ArrayList<>();
                for (BaseIdTranStruVo baseIdTranStruVo : extFields) {
                    BaseKeyValueStruVo keyValuePairVo=new BaseKeyValueStruVo();
                    keyValuePairVo.setKey(baseIdTranStruVo.getId());
                    keyValuePairVo.setValue(baseIdTranStruVo.getName());
                    keyValuePairVos.add(keyValuePairVo);
                }
                String extJson = JSON.toJSONString(keyValuePairVos);
                vueVo.setFixedDownBoxValue(extJson);

            }else{
                //todo
            }
            results.add(vueVo);
        }
        return results;
    }
}
