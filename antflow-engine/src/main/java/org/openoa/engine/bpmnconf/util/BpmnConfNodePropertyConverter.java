package org.openoa.engine.bpmnconf.util;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.TypeReference;
import com.google.common.base.Joiner;
import jodd.util.StringUtil;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.openoa.engine.bpmnconf.constant.JimContants;
import org.openoa.engine.bpmnconf.constant.enus.ConditionTypeEnum;
import org.openoa.base.constant.enums.JudgeOperatorEnum;
import org.openoa.base.vo.*;
import org.openoa.base.exception.JiMuBizException;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;

import java.lang.reflect.Field;
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
                throw new JiMuBizException(String.format("columnDbname:%s is not a valid name",columnDbname));
            }
            Integer fieldType = enumByCode.getFieldType();
            Class<?> fieldCls = enumByCode.getFieldCls();
            if(fieldType==1){//list
                String fixedDownBoxValue = newModel.getFixedDownBoxValue();
                Map<String, BaseKeyValueStruVo> valueStruVoMap = JSON.parseObject(fixedDownBoxValue, new TypeReference<Map<String, BaseKeyValueStruVo>>() {
                });
                String zdy1 = newModel.getZdy1();
                String[] keys = zdy1.split(",");
                List<Object> values=new ArrayList<>(keys.length);
                for (String key : keys) {
                    BaseKeyValueStruVo baseKeyValueStruVo = valueStruVoMap.get(key);
                    if(fieldCls.equals(String.class)){
                        values.add(baseKeyValueStruVo.getKey());
                    }else{
                        Object parsedObject = JSON.parseObject(baseKeyValueStruVo.getKey(), fieldCls);
                        values.add(parsedObject);
                    }
                }
                Field field = FieldUtils.getField(BpmnNodeConditionsConfBaseVo.class, enumByCode.getFieldName(),true);
                ReflectionUtils.setField(field, result, values);
            }else{
                String zdy1 = newModel.getZdy1();
                Field field = FieldUtils.getField(BpmnNodeConditionsConfBaseVo.class, enumByCode.getFieldName(),true);
                String opt1 = newModel.getOpt1();
                if(!StringUtils.isEmpty(opt1)){
                    JudgeOperatorEnum symbol = JudgeOperatorEnum.getBySymbol(opt1);
                    if(symbol==null){
                        throw new JiMuBizException(String.format("condition symbol of %s is undefined!",opt1));
                    }
                    Field opField = FieldUtils.getField(BpmnNodeConditionsConfBaseVo.class, JimContants.NUM_OPERATOR, true);
                    ReflectionUtils.setField(opField,result,symbol.getCode());
                }
                if(String.class.isAssignableFrom(fieldCls)){
                    ReflectionUtils.setField(field, result, zdy1);
                }else{
                    ReflectionUtils.setField(field, result, JSON.parseObject(zdy1,fieldCls));
                }

            }

        }
        newModels.forEach(a->a.setFixedDownBoxValue(null));
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
                List<?> objects = (List<?>) ReflectionUtils.getField(field, baseVo);
                String join = Joiner.on(",").join(objects);
                vueVo.setZdy1(join);
                Field extField = FieldUtils.getField(BpmnNodeConditionsConfBaseVo.class, enumByCode.getFieldName()+"List",true);
                List<BaseIdTranStruVo> extFields = (List<BaseIdTranStruVo>) ReflectionUtils.getField(extField, baseVo);
                Map<String,BaseKeyValueStruVo> map=new HashMap<>();
                if(CollectionUtils.isEmpty(extFields)){
                    continue;
                }
                for (BaseIdTranStruVo baseIdTranStruVo : extFields) {
                    String id = baseIdTranStruVo.getId();
                    String name = baseIdTranStruVo.getName();
                    BaseKeyValueStruVo keyValueStruVo=new BaseKeyValueStruVo();
                    String key= id;
                    keyValueStruVo.setKey(key);
                    keyValueStruVo.setValue(name);
                    map.put(key,keyValueStruVo);
                }
                String extJson = JSON.toJSONString(map);
                vueVo.setFixedDownBoxValue(extJson);

            }else{
                //todo
            }
            results.add(vueVo);
        }
        return results;
    }
}
