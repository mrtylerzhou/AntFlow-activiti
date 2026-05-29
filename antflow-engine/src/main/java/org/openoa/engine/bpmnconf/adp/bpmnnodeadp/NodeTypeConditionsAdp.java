package org.openoa.engine.bpmnconf.adp.bpmnnodeadp;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.TypeReference;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.openoa.base.constant.enums.ConditionRelationShipEnum;
import org.openoa.base.entity.jsonconf.BpmnNodeConditionsConfJson;
import org.openoa.base.entity.jsonconf.BpmnNodeConfigJson;
import org.openoa.base.vo.*;
import org.openoa.engine.bpmnconf.adp.conditionfilter.nodetypeconditions.BpmnNodeConditionsAdaptor;
import org.openoa.engine.bpmnconf.constant.enus.BpmnNodeAdpConfEnum;
import org.openoa.engine.bpmnconf.constant.enus.ConditionTypeEnum;
import org.openoa.engine.bpmnconf.service.impl.BpmnConfLfFormdataFieldServiceImpl;
import org.openoa.base.exception.AFBizException;
import org.openoa.base.util.SpringBeanUtils;
import org.openoa.engine.bpmnconf.service.interf.repository.BpmnNodeConditionsConfService;
import org.openoa.engine.utils.BpmnConfNodePropertyConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.*;

/**
 * @author AntFlow
 * @since 0.5
 */
@Slf4j
@Component("nodeTypeConditionsAdp")
public class NodeTypeConditionsAdp implements BpmnNodeAdaptor {

    @Autowired
    private BpmnNodeConditionsConfService bpmnNodeConditionsConfService;

    @Autowired
    private BpmnConfLfFormdataFieldServiceImpl lfFormdataFieldService;

    @Override
    public void formatToBpmnNodeVo(BpmnNodeVo bpmnNodeVo) {
        // Prefer JSON config if available
        BpmnNodeConfigJson nodeConfig = bpmnNodeVo.getNodeConfigJsonObj();
        if (nodeConfig != null && nodeConfig.getConditionsConf() != null
                && !CollectionUtils.isEmpty(nodeConfig.getConditionsConf().getConditionGroups())) {
            formatFromJson(bpmnNodeVo, nodeConfig.getConditionsConf());
        }else{
            throw new AFBizException("migration error,please contact the author");
        }
    }

    /**
     * Read conditions from node_config_json (JSON-first path).
     * Uses BpmnConfNodePropertyConverter.fromVue3Model to reconstruct all runtime value fields
     * (accountType, leaveHour, etc.) from the Vue3 model stored in extJson,
     * then supplements with metadata fields that fromVue3Model does not cover
     * (numberOperatorList, groupedCondRelations, setConditionsResps, etc.).
     */
    private void formatFromJson(BpmnNodeVo bpmnNodeVo, BpmnNodeConditionsConfJson conditionsConf) {
        List<BpmnNodeConditionsConfJson.ConditionGroup> groups = conditionsConf.getConditionGroups();
        BpmnNodeConditionsConfJson.ConditionGroup firstGroup = groups.get(0);

        if (Objects.equals(firstGroup.getIsDefault(), 1)) {
            BpmnNodeConditionsConfBaseVo baseVo = new BpmnNodeConditionsConfBaseVo();
            baseVo.setIsDefault(firstGroup.getIsDefault());
            baseVo.setSort(firstGroup.getSort());
            setProperty(bpmnNodeVo, baseVo);
            bpmnNodeVo.getProperty().setIsDefault(firstGroup.getIsDefault());
            bpmnNodeVo.getProperty().setSort(firstGroup.getSort());
            return;
        }

        String extJson = firstGroup.getExtJson();
        if (StringUtils.isEmpty(extJson)) {
            BpmnNodeConditionsConfBaseVo baseVo = new BpmnNodeConditionsConfBaseVo();
            baseVo.setIsDefault(firstGroup.getIsDefault());
            baseVo.setSort(firstGroup.getSort());
            baseVo.setGroupRelation(firstGroup.getGroupRelation());
            setProperty(bpmnNodeVo, baseVo);
            return;
        }

        List<List<BpmnNodeConditionsConfVueVo>> extFieldsGroup = JSON.parseObject(extJson,
                new TypeReference<List<List<BpmnNodeConditionsConfVueVo>>>() {});

        // Use fromVue3Model to reconstruct ALL runtime value fields (accountType, leaveHour, etc.)
        // plus conditionParamTypes, groupedConditionParamTypes, groupedLfConditionsMap, etc.
        BpmnNodePropertysVo propertysVo = BpmnNodePropertysVo.builder()
                .isDefault(firstGroup.getIsDefault())
                .sort(firstGroup.getSort())
                .groupRelation(ConditionRelationShipEnum.getValueByCode(firstGroup.getGroupRelation()))
                .conditionList(extFieldsGroup)
                .build();

        BpmnNodeConditionsConfBaseVo baseVo = BpmnConfNodePropertyConverter.fromVue3Model(propertysVo);

        // fromVue3Model does not populate these metadata fields — set them here
        Map<Integer, Integer> groupedCondRelations = new HashMap<>();
        Map<Integer, List<Integer>> groupedNumberOperatorListMap = new HashMap<>();
        Set<Integer> processedTypes = new HashSet<>();

        for (List<BpmnNodeConditionsConfVueVo> groupConds : extFieldsGroup) {
            for (BpmnNodeConditionsConfVueVo cond : groupConds) {
                Integer condGroup = cond.getCondGroup();
                Integer columnId = Integer.parseInt(cond.getColumnId());

                groupedCondRelations.put(condGroup, ConditionRelationShipEnum.getCodeByValue(cond.getCondRelation()));

                if (cond.getOptType() != null) {
                    baseVo.getNumberOperatorList().add(cond.getOptType());
                    groupedNumberOperatorListMap.computeIfAbsent(condGroup, k -> new ArrayList<>()).add(cond.getOptType());
                }

                // Call setConditionsResps for each distinct condition type
                // to populate display list fields (e.g. accountTypeList, purchaseTypeList)
                if (!processedTypes.contains(columnId)) {
                    ConditionTypeEnum conditionTypeEnum = ConditionTypeEnum.getEnumByCode(columnId);
                    if (conditionTypeEnum != null) {
                        BpmnNodeConditionsAdaptor bean = SpringBeanUtils.getBean(conditionTypeEnum.getCls());
                        bean.setConditionsResps(baseVo);
                        processedTypes.add(columnId);
                    }
                }
            }
        }
        baseVo.setGroupedCondRelations(groupedCondRelations);
        baseVo.setGroupedNumberOperatorListMap(groupedNumberOperatorListMap);

        setProperty(bpmnNodeVo, baseVo);

        bpmnNodeVo.getProperty().setGroupRelation(ConditionRelationShipEnum.getValueByCode(firstGroup.getGroupRelation()));
        bpmnNodeVo.getProperty().setConditionList(extFieldsGroup);
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
    public void setSupportBusinessObjects() {
        addSupportBusinessObjects(BpmnNodeAdpConfEnum.ADP_CONF_NODE_TYPE_CONDITIONS);
    }
}