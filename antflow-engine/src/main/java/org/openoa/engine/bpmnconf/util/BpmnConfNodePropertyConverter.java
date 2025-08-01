package org.openoa.engine.bpmnconf.util;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.alibaba.fastjson2.TypeReference;
import com.google.common.base.Joiner;
import jodd.util.StringUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.openoa.base.constant.StringConstants;
import org.openoa.base.constant.enums.ConditionRelationShipEnum;
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
import java.util.stream.Collectors;

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


        Integer isDefault = propertysVo.getIsDefault();
        Map<Integer, Map<String,Object>> groupedLfConditionsMap=new HashMap<>();
        BpmnNodeConditionsConfBaseVo result=new BpmnNodeConditionsConfBaseVo();
        //outer common property
        result.setIsDefault(propertysVo.getIsDefault());
        result.setSort(propertysVo.getSort());
        result.setGroupRelation(ConditionRelationShipEnum.getCodeByValue(propertysVo.getGroupRelation()));
        List<Integer> conditionTypes=new ArrayList<>();
        Map<Integer,List<Integer>> groupedConditionTypes=new HashMap<>();
        Integer strEnumCode = ConditionTypeEnum.CONDITION_TYPE_LF_STR_CONDITION.getCode();

        boolean isLowCodeFlow = false;
        List<List<BpmnNodeConditionsConfVueVo>> groupedNewModels = propertysVo.getConditionList();
        if(ObjectUtils.isEmpty(groupedNewModels)&&Objects.equals(isDefault,0)){
            throw new JiMuBizException("input nodes is empty");
        }

        int index=0;
        for (List<BpmnNodeConditionsConfVueVo> newModels : groupedNewModels) {
            Map<String,Object> wrapperResult=new HashMap<>();
            index++;
            List<Integer> currentGroupConditionTypes=new ArrayList<>();
            for (BpmnNodeConditionsConfVueVo newModel : newModels) {
                newModel.setCondGroup(index);
                String columnId = newModel.getColumnId();
                if(columnId==null){
                    throw new JiMuBizException("each and every node must have a columnId value");
                }

                int columnIdInt = Integer.parseInt(columnId);
                //select控件多选特殊处理 columnId =10000 改为 10004
                if (strEnumCode == columnIdInt){
                    if (newModel.getMultiple() != null && newModel.getMultiple()){
                        columnIdInt =  ConditionTypeEnum.CONDITION_TYPE_LF_COLLECTION_CONDITION.getCode();
                    }
                }
                ConditionTypeEnum enumByCode = ConditionTypeEnum.getEnumByCode(columnIdInt);
                if(enumByCode==null){
                    throw new JiMuBizException(String.format("columnId of value:%s is not a valid value",columnId));
                }
                conditionTypes.add(columnIdInt);
                currentGroupConditionTypes.add(columnIdInt);
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
                    if (zdy1.startsWith("[")&& zdy1.endsWith("]")){
                        zdy1 = zdy1.substring(1,zdy1.length() -1);
                    }
                    String[] keys = zdy1.split(",");
                    List<String> keysList = Arrays.asList(keys);
                    List<Object> values = new ArrayList<>(keys.length);
                    keysList.forEach(key->{
                        BaseKeyValueStruVo baseKeyValueStruVo = valueStruVoList.stream().filter(x->x.getKey().equals(key)).findFirst().get();
                        if(fieldCls.equals(String.class)){
                            values.add(baseKeyValueStruVo.getKey());
                        }else{
                            Object parsedObject = JSON.parseObject(baseKeyValueStruVo.getKey(), fieldCls);
                            values.add(parsedObject);
                        }
                    });
                    Object valueOrWrapper=null;
                    if(ConditionTypeEnum.isLowCodeFlow(enumByCode)){
                        isLowCodeFlow=true;
                        wrapperResult.put(columnDbname,values);
                        valueOrWrapper=wrapperResult;
                        groupedLfConditionsMap.put(index,wrapperResult);
                    }else{
                        Field field = FieldUtils.getField(BpmnNodeConditionsConfBaseVo.class, enumByCode.getFieldName(),true);
                        ReflectionUtils.setField(field, result, valueOrWrapper!=null?valueOrWrapper:values);
                    }

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
                            isLowCodeFlow=true;
                            wrapperResult.put(columnDbname,zdy1);
                            valueOrWrapper=wrapperResult;
                            groupedLfConditionsMap.put(index,wrapperResult);
                        }else{
                            ReflectionUtils.setField(field, result, valueOrWrapper!=null?valueOrWrapper:zdy1);
                        }
                    }else{
                        Object valueOrWrapper=null;
                        Object actualValue=null;
                        Object zdy2Value=null;
                        actualValue= JSON.parseObject(zdy1,fieldCls);
                        if (!StringUtils.isEmpty(zdy2)){
                            zdy2Value=JSON.parseObject(zdy2,fieldCls);
                        }
                        if(JudgeOperatorEnum.binaryOperator().contains(optType)){
                            zdy1=zdy1+","+zdy2;//antflow目前只有一个自定义值,介于之间的提前定义好JudgeOperatorEnum,值用字符串拼接,使用时再分割
                        }
                        if(ConditionTypeEnum.isLowCodeFlow(enumByCode)){
                            isLowCodeFlow=true;
                            wrapperResult.put(columnDbname,actualValue);
                            valueOrWrapper=wrapperResult;
                            groupedLfConditionsMap.put(index,wrapperResult);
                        }else {
                            ReflectionUtils.setField(field, result, valueOrWrapper!=null?valueOrWrapper:actualValue);
                        }
                    }

                }

            }
            groupedConditionTypes.put(index,currentGroupConditionTypes);
        }
        if(isLowCodeFlow){
            result.setGroupedLfConditionsMap(groupedLfConditionsMap);
        }
        String extJson = JSON.toJSONString(groupedNewModels);
        result.setExtJson(extJson);
        result.setConditionParamTypes(conditionTypes);
        result.setGroupedConditionParamTypes(groupedConditionTypes);
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

        Map<Integer, List<Integer>> groupedConditionParamTypes = baseVo.getGroupedConditionParamTypes()!=null?baseVo.getGroupedConditionParamTypes():new HashMap<>();
        String extJson = baseVo.getExtJson();
        Map<Integer, List<BpmnNodeConditionsConfVueVo>> groupedConditionsConf=new HashMap<>();
        if(!StringUtils.isEmpty(extJson)){
            List<List<BpmnNodeConditionsConfVueVo>> extFieldsArray = JSON.parseObject(extJson, new TypeReference<List<List<BpmnNodeConditionsConfVueVo>>>() {
            });
            groupedConditionsConf = extFieldsArray.stream().flatMap(Collection::stream).collect(Collectors.groupingBy(BpmnNodeConditionsConfVueVo::getCondGroup));
        }

        for (Map.Entry<Integer, List<Integer>> integerListEntry : groupedConditionParamTypes.entrySet()) {
            Integer group = integerListEntry.getKey();
            List<BpmnNodeConditionsConfVueVo> bpmnNodeConditionsConfVueVos = groupedConditionsConf.get(group);
            List<Integer> conditionParamTypes = integerListEntry.getValue();
            int index=0;
            for (Integer conditionParamType : conditionParamTypes) {
                index++;
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

                        if(!CollectionUtils.isEmpty(bpmnNodeConditionsConfVueVos)){
                            String fixedDownBoxValue = bpmnNodeConditionsConfVueVos.get(index-1).getFixedDownBoxValue();
                            if(!StringUtils.isEmpty(fixedDownBoxValue)){
                                vueVo.setFixedDownBoxValue(fixedDownBoxValue);
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
                    String extJsonx = JSON.toJSONString(keyValuePairVos);
                    vueVo.setFixedDownBoxValue(extJsonx);

                }else{
                    //todo
                }
                results.add(vueVo);
            }
        }

        return results;
    }
}
