package org.openoa.engine.bpmnconf.adp.bpmnnodeadp;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.openoa.base.util.SecurityUtils;
import org.openoa.base.vo.*;
import org.openoa.engine.bpmnconf.adp.conditionfilter.nodetypeconditions.BpmnNodeConditionsAdaptor;
import org.openoa.engine.bpmnconf.constant.enus.BpmnNodeAdpConfEnum;
import org.openoa.engine.bpmnconf.constant.enus.ConditionTypeEnum;
import org.openoa.engine.bpmnconf.confentity.BpmnNodeConditionsConf;
import org.openoa.engine.bpmnconf.confentity.BpmnNodeConditionsParamConf;
import org.openoa.engine.bpmnconf.mapper.BpmnNodeConditionsConfMapper;
import org.openoa.engine.bpmnconf.service.impl.BpmnNodeConditionsConfServiceImpl;
import org.openoa.engine.bpmnconf.service.impl.BpmnNodeConditionsParamConfServiceImpl;
import org.openoa.base.exception.JiMuBizException;
import org.openoa.base.util.SpringBeanUtils;
import org.openoa.engine.bpmnconf.util.BpmnConfNodePropertyConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
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
    public BpmnNodeConditionsConfMapper confMapper;

    @Override
    public BpmnNodeVo formatToBpmnNodeVo(BpmnNodeVo bpmnNodeVo) {

        BpmnNodeConditionsConf bpmnNodeConditionsConf = bpmnNodeConditionsConfService.getOne(new QueryWrapper<BpmnNodeConditionsConf>()
                .eq("bpmn_node_id", bpmnNodeVo.getId()));

        if (ObjectUtils.isEmpty(bpmnNodeConditionsConf)) {
            return bpmnNodeVo;
        }
        //get conditions conf


        BpmnNodeConditionsConfBaseVo bpmnNodeConditionsConfBaseVo = new BpmnNodeConditionsConfBaseVo();
        bpmnNodeConditionsConfBaseVo.setIsDefault(bpmnNodeConditionsConf.getIsDefault());
        bpmnNodeConditionsConfBaseVo.setSort(bpmnNodeConditionsConf.getSort());


        // if the condition node is default condition then assign the condition node configuration and return
        if (Objects.equals(bpmnNodeConditionsConf.getIsDefault(), 1)) {
            setProperty(bpmnNodeVo, bpmnNodeConditionsConfBaseVo);
            bpmnNodeVo.getProperty().setIsDefault(bpmnNodeConditionsConf.getIsDefault());
            bpmnNodeVo.getProperty().setSort(bpmnNodeConditionsConf.getSort());
            return bpmnNodeVo;
        }

        List<BpmnNodeConditionsParamConf> nodeConditionsParamConfs = bpmnNodeConditionsParamConfService.list(new QueryWrapper<BpmnNodeConditionsParamConf>()
                .eq("bpmn_node_conditions_id", bpmnNodeConditionsConf.getId()));

        if (!ObjectUtils.isEmpty(nodeConditionsParamConfs)) {

            //set condition param types
            bpmnNodeConditionsConfBaseVo.setConditionParamTypes(Lists.newArrayList(nodeConditionsParamConfs
                    .stream()
                    .map(BpmnNodeConditionsParamConf::getConditionParamType)
                    .collect(Collectors.toList())));

            for (BpmnNodeConditionsParamConf nodeConditionsParamConf : nodeConditionsParamConfs) {
                ConditionTypeEnum conditionTypeEnum = ConditionTypeEnum
                        .getEnumByCode(nodeConditionsParamConf.getConditionParamType());

                String conditionParamJsom = nodeConditionsParamConf.getConditionParamJsom();
                if (!ObjectUtils.isEmpty(conditionParamJsom)) {
                    if (conditionTypeEnum.getFieldType().equals(1)) {//列表
                        List<?> objects = JSON.parseArray(conditionParamJsom, conditionTypeEnum.getFieldCls());
                        Field field = FieldUtils.getField(BpmnNodeConditionsConfBaseVo.class, conditionTypeEnum.getFieldName(),true);
                        ReflectionUtils.setField(field, bpmnNodeConditionsConfBaseVo, objects);

                    } else if (conditionTypeEnum.getFieldType().equals(2)) {//对象
                        Object object = JSON.parseObject(conditionParamJsom, conditionTypeEnum.getFieldCls());
                        Field field = FieldUtils.getField(BpmnNodeConditionsConfBaseVo.class, conditionTypeEnum.getFieldName(),true);
                        ReflectionUtils.setField(field, bpmnNodeConditionsConfBaseVo, object);
                    }
                }

                //set response
                BpmnNodeConditionsAdaptor bean = SpringBeanUtils.getBean(conditionTypeEnum.getCls());
                bean.setConditionsResps(bpmnNodeConditionsConfBaseVo);
            }
        }

        //set property
        setProperty(bpmnNodeVo, bpmnNodeConditionsConfBaseVo);
        List<BpmnNodeConditionsConfVueVo> bpmnNodeConditionsConfVueVos = BpmnConfNodePropertyConverter.toVue3Model(bpmnNodeConditionsConfBaseVo);
        Map<String, BpmnNodeConditionsConfVueVo> voMap = bpmnNodeConditionsConfVueVos.stream().collect(Collectors.toMap(BpmnNodeConditionsConfVueVo::getColumnDbname, v -> v, (k1, k2) -> k1));
        String extJson = bpmnNodeConditionsConf.getExtJson();
        List<BpmnNodeConditionsConfVueVo> extFields = JSON.parseArray(extJson, BpmnNodeConditionsConfVueVo.class);
        for (BpmnNodeConditionsConfVueVo extField : extFields) {
            String columnDbname = extField.getColumnDbname();
            BpmnNodeConditionsConfVueVo vueVo = voMap.get(columnDbname);
            if(vueVo==null){
                throw new JiMuBizException("logic error!");
            }
            String fixedDownBoxValue = vueVo.getFixedDownBoxValue();
            extField.setFixedDownBoxValue(fixedDownBoxValue);
        }
        bpmnNodeVo.getProperty().setIsDefault(bpmnNodeConditionsConf.getIsDefault());
        bpmnNodeVo.getProperty().setSort(bpmnNodeConditionsConf.getSort());
        bpmnNodeVo.getProperty().setConditionList(extFields);
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

        //get conf
        BpmnNodeConditionsConfBaseVo bpmnNodeConditionsConfBaseVo1 =
                Optional.ofNullable(bpmnNodeVo.getProperty())
                        .map(BpmnNodePropertysVo::getConditionsConf)
                        .orElse(new BpmnNodeConditionsConfBaseVo());

        BpmnNodeConditionsConfBaseVo bpmnNodeConditionsConfBaseVo =Optional.ofNullable(bpmnNodeVo.getProperty()).map(BpmnConfNodePropertyConverter::fromVue3Model)
                .orElse(new BpmnNodeConditionsConfBaseVo());

        BpmnNodeConditionsConf bpmnNodeConditionsConf = new BpmnNodeConditionsConf();
        bpmnNodeConditionsConf.setBpmnNodeId(bpmnNodeVo.getId());
        bpmnNodeConditionsConf.setIsDefault(bpmnNodeConditionsConfBaseVo.getIsDefault());
        bpmnNodeConditionsConf.setSort(bpmnNodeConditionsConfBaseVo.getSort());
        bpmnNodeConditionsConf.setExtJson(bpmnNodeConditionsConfBaseVo.getExtJson());
        bpmnNodeConditionsConf.setCreateTime(new Date());
        bpmnNodeConditionsConf.setCreateUser(SecurityUtils.getLogInEmpNameSafe());
        confMapper.insert(bpmnNodeConditionsConf);


        // if it is default condition return
        if (Objects.equals(bpmnNodeConditionsConfBaseVo.getIsDefault(), 1)) {
            return;
        }

        Long nodeConditionsId = Optional.ofNullable(Optional.ofNullable(bpmnNodeConditionsConf).orElse(new BpmnNodeConditionsConf()).getId())
                .orElse(0L);

        //if node conditions id is not null then edit it
        if (nodeConditionsId > 0) {


            Integer emptyCondition = 0;


            for (ConditionTypeEnum conditionTypeEnum : ConditionTypeEnum.values()) {
                Object conditionParam = ReflectionUtils.getField(FieldUtils.getField(BpmnNodeConditionsConfBaseVo.class, conditionTypeEnum.getFieldName(),true), bpmnNodeConditionsConfBaseVo);

                if (!ObjectUtils.isEmpty(conditionParam)) {
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
            }
        }
    }


    @Override
    public void setSupportBusinessObjects() {
        addSupportBusinessObjects(BpmnNodeAdpConfEnum.ADP_CONF_NODE_TYPE_CONDITIONS);
    }
}