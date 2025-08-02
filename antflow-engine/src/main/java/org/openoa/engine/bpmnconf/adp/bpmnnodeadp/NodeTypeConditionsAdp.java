package org.openoa.engine.bpmnconf.adp.bpmnnodeadp;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.TypeReference;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.openoa.base.constant.StringConstants;
import org.openoa.base.constant.enums.ConditionRelationShipEnum;
import org.openoa.base.util.SecurityUtils;
import org.openoa.base.vo.*;
import org.openoa.engine.bpmnconf.adp.conditionfilter.nodetypeconditions.BpmnNodeConditionsAdaptor;
import org.openoa.engine.bpmnconf.constant.enus.BpmnNodeAdpConfEnum;
import org.openoa.engine.bpmnconf.constant.enus.ConditionTypeEnum;
import org.openoa.engine.bpmnconf.confentity.BpmnNodeConditionsConf;
import org.openoa.engine.bpmnconf.confentity.BpmnNodeConditionsParamConf;
import org.openoa.engine.bpmnconf.mapper.BpmnNodeConditionsConfMapper;
import org.openoa.engine.bpmnconf.service.BpmnConfLfFormdataFieldServiceImpl;
import org.openoa.engine.bpmnconf.service.impl.BpmnNodeConditionsConfServiceImpl;
import org.openoa.engine.bpmnconf.service.impl.BpmnNodeConditionsParamConfServiceImpl;
import org.openoa.base.exception.JiMuBizException;
import org.openoa.base.util.SpringBeanUtils;
import org.openoa.engine.utils.BpmnConfNodePropertyConverter;
import org.openoa.engine.utils.MultiTenantUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author AntFlow
 * @since 0.5
 */
@Slf4j
@Component("nodeTypeConditionsAdp")
public class NodeTypeConditionsAdp extends BpmnNodeAdaptor {

    @Autowired
    private BpmnNodeConditionsConfServiceImpl bpmnNodeConditionsConfService;

    @Autowired
    private BpmnNodeConditionsParamConfServiceImpl bpmnNodeConditionsParamConfService;
    @Autowired
    private BpmnNodeConditionsConfMapper confMapper;
    @Autowired
    private BpmnConfLfFormdataFieldServiceImpl lfFormdataFieldService;

    @Override
    public BpmnNodeVo formatToBpmnNodeVo(BpmnNodeVo bpmnNodeVo) {

        BpmnNodeConditionsConf bpmnNodeConditionsConf = bpmnNodeConditionsConfService.getOne(new QueryWrapper<BpmnNodeConditionsConf>()
                .eq("bpmn_node_id", bpmnNodeVo.getId()));

        if (ObjectUtils.isEmpty(bpmnNodeConditionsConf)) {
            return bpmnNodeVo;
        }
        //get conditions conf

        String extJson = bpmnNodeConditionsConf.getExtJson();
        //List<BpmnNodeConditionsConfVueVo> extFields = JSON.parseArray(extJson, BpmnNodeConditionsConfVueVo.class);
        List<List<BpmnNodeConditionsConfVueVo>> extFieldsGroup = JSON.parseObject(extJson, new TypeReference<List<List<BpmnNodeConditionsConfVueVo>>>() {
        });

        Map<String,BpmnNodeConditionsConfVueVo> name2confVueMap=extFieldsGroup.stream()
                .flatMap(List::stream)
                .collect(Collectors.toMap(
                        a->a.getColumnDbname()+"_"+a.getCondGroup(),
                        b -> b,
                        (k1, k2) -> k1
                ));
        BpmnNodeConditionsConfBaseVo bpmnNodeConditionsConfBaseVo = new BpmnNodeConditionsConfBaseVo();
        bpmnNodeConditionsConfBaseVo.setIsDefault(bpmnNodeConditionsConf.getIsDefault());
        bpmnNodeConditionsConfBaseVo.setSort(bpmnNodeConditionsConf.getSort());
        bpmnNodeConditionsConfBaseVo.setExtJson(extJson);
        bpmnNodeConditionsConfBaseVo.setGroupRelation(bpmnNodeConditionsConf.getGroupRelation());

        // if the condition node is default condition then assign the condition node configuration and return
        if (Objects.equals(bpmnNodeConditionsConf.getIsDefault(), 1)) {
            setProperty(bpmnNodeVo, bpmnNodeConditionsConfBaseVo);
            bpmnNodeVo.getProperty().setIsDefault(bpmnNodeConditionsConf.getIsDefault());
            bpmnNodeVo.getProperty().setSort(bpmnNodeConditionsConf.getSort());
            return bpmnNodeVo;
        }


        List<BpmnNodeConditionsParamConf> nodeConditionsParamConfs = bpmnNodeConditionsParamConfService.list(new LambdaQueryWrapper<BpmnNodeConditionsParamConf>()
                .eq(BpmnNodeConditionsParamConf::getBpmnNodeConditionsId, bpmnNodeConditionsConf.getId())
                .orderByAsc(BpmnNodeConditionsParamConf::getCondGroup));

        if (!ObjectUtils.isEmpty(nodeConditionsParamConfs)) {

            //set condition param types
            bpmnNodeConditionsConfBaseVo.setConditionParamTypes(nodeConditionsParamConfs
                    .stream()
                    .map(BpmnNodeConditionsParamConf::getConditionParamType)
                    .collect(Collectors.toList()));
            bpmnNodeConditionsConfBaseVo.setGroupedConditionParamTypes(nodeConditionsParamConfs
                    .stream().peek(a->{
                        Integer condGroup = a.getCondGroup();
                        Integer condRelation = a.getCondRelation();
                       /* if(condGroup==null||condRelation==null){
                            throw new JiMuBizException("logic error,please contact the Administrator");
                        }*/
                        Map<Integer, Integer> groupedCondRelations = bpmnNodeConditionsConfBaseVo.getGroupedCondRelations();
                        groupedCondRelations.put(condGroup,condRelation);
                    })
                    .collect(Collectors.groupingBy(BpmnNodeConditionsParamConf::getCondGroup,
                                    Collectors.mapping(BpmnNodeConditionsParamConf::getConditionParamType, Collectors.toList()))));


            Map<Integer, Map<String,Object>> groupedWrappedValue=new HashMap<>();
            boolean isLowCodeFlow=false;
            for (BpmnNodeConditionsParamConf nodeConditionsParamConf : nodeConditionsParamConfs) {
                Map<String,Object> wrappedValue=null;
                ConditionTypeEnum conditionTypeEnum = ConditionTypeEnum
                        .getEnumByCode(nodeConditionsParamConf.getConditionParamType());
                if(conditionTypeEnum==null){
                    throw  new JiMuBizException("can not get ConditionTypeEnum by code:"+nodeConditionsParamConf.getConditionParamType());
                }
                Integer condGroup = nodeConditionsParamConf.getCondGroup();
                String conditionParamJsom = nodeConditionsParamConf.getConditionParamJsom();
                Integer operator = nodeConditionsParamConf.getOperator();
                String paramKey = nodeConditionsParamConf.getConditionParamName() + "_" + nodeConditionsParamConf.getCondGroup();
                if (!ObjectUtils.isEmpty(conditionParamJsom)) {
                    if (conditionTypeEnum.getFieldType().equals(1)) {//列表
                        List<?> objects = JSON.parseArray(conditionParamJsom, conditionTypeEnum.getFieldCls());

                        if(ConditionTypeEnum.isLowCodeFlow(conditionTypeEnum)){
                            String columnDbname = name2confVueMap.get(paramKey).getColumnDbname();
                            if(wrappedValue==null){
                                wrappedValue=new LinkedHashMap<>();
                            }
                            wrappedValue.put(columnDbname,objects);
                            if(groupedWrappedValue.containsKey(condGroup)){
                                groupedWrappedValue.get(condGroup).put(columnDbname,objects);
                            }else{
                                groupedWrappedValue.put(condGroup,wrappedValue);
                            }
                        }
                        Field field = FieldUtils.getField(BpmnNodeConditionsConfBaseVo.class, conditionTypeEnum.getFieldName(),true);
                        ReflectionUtils.setField(field, bpmnNodeConditionsConfBaseVo, wrappedValue!=null?wrappedValue:objects);

                    } else if (conditionTypeEnum.getFieldType().equals(2)) {//对象
                        Object object=null;
                        if(String.class.isAssignableFrom(conditionTypeEnum.getFieldCls())){
                            object=conditionParamJsom;
                        }else{
                            object = JSON.parseObject(conditionParamJsom, conditionTypeEnum.getFieldCls());
                        }

                        if(ConditionTypeEnum.isLowCodeFlow(conditionTypeEnum)){
                            isLowCodeFlow=true;
                            String columnDbname = name2confVueMap.get(paramKey).getColumnDbname();
                            if(wrappedValue==null){
                                wrappedValue=new LinkedHashMap<>();
                            }
                            wrappedValue.put(columnDbname,object);
                            if(groupedWrappedValue.containsKey(condGroup)){
                                groupedWrappedValue.get(condGroup).put(columnDbname,object);
                            }else{
                                groupedWrappedValue.put(condGroup,wrappedValue);
                            }
                        }else{
                            Field field = FieldUtils.getField(BpmnNodeConditionsConfBaseVo.class, conditionTypeEnum.getFieldName(),true);
                            ReflectionUtils.setField(field, bpmnNodeConditionsConfBaseVo,  wrappedValue!=null?wrappedValue:object);
                        }
                    }
                }

                //set response
                BpmnNodeConditionsAdaptor bean = SpringBeanUtils.getBean(conditionTypeEnum.getCls());
                bean.setConditionsResps(bpmnNodeConditionsConfBaseVo);
                bpmnNodeConditionsConfBaseVo.getNumberOperatorList().add(operator);
                Map<Integer, List<Integer>> groupedNumberOperatorListMap = bpmnNodeConditionsConfBaseVo.getGroupedNumberOperatorListMap();
                if(groupedNumberOperatorListMap.containsKey(nodeConditionsParamConf.getCondGroup())){
                    groupedNumberOperatorListMap.get(nodeConditionsParamConf.getCondGroup()).add(operator);
                }else{
                    List<Integer> numberOperatorList = Lists.newArrayList();
                    numberOperatorList.add(operator);
                    groupedNumberOperatorListMap.put(nodeConditionsParamConf.getCondGroup(),numberOperatorList);
                }
            }
            if(isLowCodeFlow){
                bpmnNodeConditionsConfBaseVo.setGroupedLfConditionsMap(groupedWrappedValue);
            }

        }

        //set property
        setProperty(bpmnNodeVo, bpmnNodeConditionsConfBaseVo);
        List<BpmnNodeConditionsConfVueVo> bpmnNodeConditionsConfVueVos = BpmnConfNodePropertyConverter.toVue3Model(bpmnNodeConditionsConfBaseVo);
        Map<String, BpmnNodeConditionsConfVueVo> voMap = bpmnNodeConditionsConfVueVos.stream().collect(Collectors.toMap(BpmnNodeConditionsConfVueVo::getColumnDbname, v -> v, (k1, k2) -> k1));

        List<BpmnNodeConditionsConfVueVo> extFields = extFieldsGroup.stream().flatMap(Collection::stream).collect(Collectors.toList());
        for (BpmnNodeConditionsConfVueVo extField : extFields) {
            String columnDbname = extField.getColumnDbname();
            boolean lowCodeFlow = ConditionTypeEnum.isLowCodeFlow(ConditionTypeEnum.getEnumByCode(Integer.parseInt(extField.getColumnId())));
            if(lowCodeFlow){
                columnDbname=StringConstants.LOWFLOW_CONDITION_CONTAINER_FIELD_NAME;
            }
            if(!CollectionUtils.isEmpty(voMap)){
                BpmnNodeConditionsConfVueVo vueVo = voMap.get(columnDbname);
                if(vueVo==null){
                    throw new JiMuBizException("logic error!");
                }
                //String fixedDownBoxValue = vueVo.getFixedDownBoxValue();
            }
            extField.setFixedDownBoxValue(extField.getFixedDownBoxValue());
        }
        bpmnNodeVo.getProperty().setIsDefault(bpmnNodeConditionsConf.getIsDefault());
        bpmnNodeVo.getProperty().setSort(bpmnNodeConditionsConf.getSort());
        bpmnNodeVo.getProperty().setGroupRelation(ConditionRelationShipEnum.getValueByCode(bpmnNodeConditionsConf.getGroupRelation()));
        bpmnNodeVo.getProperty().setConditionList(extFieldsGroup);
        return bpmnNodeVo;
    }

    @Override
    public PersonnelRuleVO formaFieldAttributeInfoVO() {
        return null;
    }

    /**
     * set property config
     *
     * @param bpmnNodeVo
     * @param bpmnNodeConditionsConfBaseVo
     */
    private void setProperty(BpmnNodeVo bpmnNodeVo, BpmnNodeConditionsConfBaseVo bpmnNodeConditionsConfBaseVo) {
        bpmnNodeVo.setProperty(BpmnNodePropertysVo
                .builder()
                .conditionsConf(bpmnNodeConditionsConfBaseVo)
                .build());
    }

    @Override
    public void editBpmnNode(BpmnNodeVo bpmnNodeVo) {

        BpmnNodeConditionsConfBaseVo bpmnNodeConditionsConfBaseVo =Optional.ofNullable(bpmnNodeVo.getProperty())
                .map(BpmnConfNodePropertyConverter::fromVue3Model)
                .orElse(new BpmnNodeConditionsConfBaseVo());

        BpmnNodeConditionsConf bpmnNodeConditionsConf = new BpmnNodeConditionsConf();
        bpmnNodeConditionsConf.setBpmnNodeId(bpmnNodeVo.getId());
        bpmnNodeConditionsConf.setIsDefault(bpmnNodeConditionsConfBaseVo.getIsDefault());
        bpmnNodeConditionsConf.setSort(bpmnNodeConditionsConfBaseVo.getSort());
        bpmnNodeConditionsConf.setGroupRelation(bpmnNodeConditionsConfBaseVo.getGroupRelation());
        bpmnNodeConditionsConf.setExtJson(bpmnNodeConditionsConfBaseVo.getExtJson());
        bpmnNodeConditionsConf.setCreateTime(new Date());
        bpmnNodeConditionsConf.setCreateUser(SecurityUtils.getLogInEmpNameSafe());
        bpmnNodeConditionsConf.setTenantId(MultiTenantUtil.getCurrentTenantId());
        confMapper.insert(bpmnNodeConditionsConf);


        // if it is default condition return
        if (Objects.equals(bpmnNodeConditionsConfBaseVo.getIsDefault(), 1)) {
            return;
        }

        Long nodeConditionsId = Optional.ofNullable(Optional.of(bpmnNodeConditionsConf).orElse(new BpmnNodeConditionsConf()).getId())
                .orElse(0L);

        //if node conditions id is not null then edit it
        if (nodeConditionsId > 0) {


            int emptyCondition = 0;

            String extJson = bpmnNodeConditionsConfBaseVo.getExtJson();

            List<List<BpmnNodeConditionsConfVueVo>> extFieldsArray = JSON.parseObject(extJson, new TypeReference<List<List<BpmnNodeConditionsConfVueVo>>>() {
            });
            int index=0;
            for (List<BpmnNodeConditionsConfVueVo> extFields : extFieldsArray) {
                index++;
                for (BpmnNodeConditionsConfVueVo extField : extFields) {
                    String columnId = extField.getColumnId();
                    String columnDbname = extField.getColumnDbname();
                    ConditionTypeEnum conditionTypeEnum=ConditionTypeEnum.getEnumByCode(Integer.parseInt(columnId));
                    if(conditionTypeEnum==null){
                        throw new JiMuBizException(Strings.lenientFormat("can not get node ConditionTypeEnum by code:%s",columnId));
                    }
                    Object conditionParam = null;
                    if(ConditionTypeEnum.isLowCodeFlow(conditionTypeEnum)){
                        Map<Integer, Map<String, Object>> groupedLfConditionsMap = bpmnNodeConditionsConfBaseVo.getGroupedLfConditionsMap();
                        conditionParam = groupedLfConditionsMap.get(index);
                    }else{
                        conditionParam= ReflectionUtils.getField(FieldUtils.getField(BpmnNodeConditionsConfBaseVo.class, conditionTypeEnum.getFieldName(),true), bpmnNodeConditionsConfBaseVo);
                    }
                    if (!ObjectUtils.isEmpty(conditionParam)) {
                        if(ConditionTypeEnum.isLowCodeFlow(conditionTypeEnum)){
                            Map<String, Object> containerWrapper = (Map<String, Object>) conditionParam;
                            conditionParam= containerWrapper.get(columnDbname);
                        }
                        String conditionParamJson;
                        if(conditionParam instanceof String){
                            conditionParamJson=conditionParam.toString();
                        }else{
                            conditionParamJson=JSON.toJSONString(conditionParam);
                        }


                        if (conditionTypeEnum.getFieldType() == 1 &&
                                ObjectUtils.isEmpty(JSON.parseArray(conditionParamJson, conditionTypeEnum.getFieldCls()))) {
                            continue;
                        }
                        Integer numberOperator= extField.getOptType();

                        bpmnNodeConditionsParamConfService.getBaseMapper().insert(BpmnNodeConditionsParamConf
                                .builder()
                                .bpmnNodeConditionsId(nodeConditionsId)
                                .conditionParamType(conditionTypeEnum.getCode())
                                .conditionParamName(extField.getColumnDbname())
                                .conditionParamJsom(conditionParamJson)
                                .operator(numberOperator)
                                .condGroup(extField.getCondGroup())
                                .condRelation(ConditionRelationShipEnum.getCodeByValue(extField.getCondRelation()))
                                .createUser(SecurityUtils.getLogInEmpNameSafe())
                                .createTime(new Date())
                                .build());
                        //if condition value doest not a collection and doest not a string type,it must have an operator
                        if(conditionTypeEnum.getFieldType()==2&&!String.class.isAssignableFrom(conditionTypeEnum.getFieldCls())){
                            bpmnNodeConditionsParamConfService.getBaseMapper().insert(BpmnNodeConditionsParamConf
                                    .builder()
                                    .bpmnNodeConditionsId(nodeConditionsId)
                                    .conditionParamType(ConditionTypeEnum.CONDITION_TYPE_NUMBER_OPERATOR.getCode())
                                    .conditionParamName(ConditionTypeEnum.CONDITION_TYPE_NUMBER_OPERATOR.getFieldName())
                                    .conditionParamJsom(numberOperator.toString())
                                    .condGroup(extField.getCondGroup())
                                    .condRelation(ConditionRelationShipEnum.getCodeByValue(extField.getCondRelation()))
                                    .createUser(SecurityUtils.getLogInEmpNameSafe())
                                    .createTime(new Date())
                                    .build());
                        }
                        Long confId = bpmnNodeVo.getConfId();
                        lfFormdataFieldService.getBaseMapper().updateByConfIdAndFieldName(confId,columnDbname);
                    }

                }
            }

            /*for (ConditionTypeEnum conditionTypeEnum : ConditionTypeEnum.values()) {
                Object conditionParam = ReflectionUtils.getField(FieldUtils.getField(BpmnNodeConditionsConfBaseVo.class, conditionTypeEnum.getFieldName(),true), bpmnNodeConditionsConfBaseVo);

                if (!ObjectUtils.isEmpty(conditionParam)) {
                    if(ConditionTypeEnum.isLowCodeFlow(conditionTypeEnum)){
                        Map<String, Object> containerWrapper = (Map<String, Object>) conditionParam;
                        Set<String> keys = containerWrapper.keySet();
                        if(keys.size()>1){
                            throw new JiMuBizException("conditions field can not have more than 1 field at the moment");
                        }
                        //when codes run to here,keys only have one element,through iterate it by a for statement(set value can not be  reached via index)
                        for (String key : keys) {
                            conditionParam= containerWrapper.get(key);
                        }
                    }
                    String conditionParamJson;
                    if(conditionParam instanceof String){
                        conditionParamJson=conditionParam.toString();
                    }else{
                        conditionParamJson=JSON.toJSONString(conditionParam);
                    }


                    if (conditionTypeEnum.getFieldType() == 1 &&
                            ObjectUtils.isEmpty(JSON.parseArray(conditionParamJson, conditionTypeEnum.getFieldCls()))) {
                        emptyCondition++;
                        continue;
                    }

                    bpmnNodeConditionsParamConfService.getBaseMapper().insert(BpmnNodeConditionsParamConf
                            .builder()
                            .bpmnNodeConditionsId(nodeConditionsId)
                            .conditionParamType(conditionTypeEnum.getCode())
                            .conditionParamJsom(conditionParamJson)
                            .createUser(SecurityUtils.getLogInEmpNameSafe())
                            .createTime(new Date())
                            .build());
                } else {
                    emptyCondition++;
                }
            }
            if (emptyCondition == ConditionTypeEnum.values().length) {
                throw new JiMuBizException("condition node has no condition config,can not save the config！");
            }*/
        }
    }


    @Override
    public void setSupportBusinessObjects() {
        addSupportBusinessObjects(BpmnNodeAdpConfEnum.ADP_CONF_NODE_TYPE_CONDITIONS);
    }
}