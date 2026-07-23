package org.openoa.engine.lowflow.service;

import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.apache.commons.lang3.StringUtils;
import org.openoa.base.constant.enums.BpmnConfFlagsEnum;
import org.openoa.base.constant.enums.NodePropertyEnum;
import org.openoa.base.entity.BpmnConf;
import org.openoa.base.entity.BpmBusinessProcess;
import org.openoa.base.entity.BpmnNode;
import org.openoa.base.entity.jsonconf.BpmnNodeApproverConfJson;
import org.openoa.base.entity.jsonconf.BpmnNodeConfigJson;
import org.openoa.base.entity.jsonconf.BpmnNodeConditionsConfJson;
import org.openoa.base.entity.jsonconf.JsonConfUtil;
import org.openoa.base.exception.AFBizException;
import org.openoa.base.exception.BusinessErrorEnum;
import org.openoa.base.interf.BpmBusinessProcessService;
import org.openoa.base.vo.BaseIdTranStruVo;
import org.openoa.base.vo.BpmnNodeConditionsConfVueVo;
import org.openoa.base.vo.BpmnStartConditionsVo;
import org.openoa.base.vo.BusinessDataVo;
import org.openoa.engine.bpmnconf.service.interf.repository.BpmnConfService;
import org.openoa.engine.bpmnconf.service.interf.repository.BpmnNodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * 低代码表单运行期共享助手: lfFields→lfConditions 拷贝、表单上下文人员解析、
 * DIY辅助表单字段抽取等。供 LF (LowFlowApprovalService) 与 DIY 辅助表单流程共用,
 * 使启用辅助表单的 DIY 流程在运行期可像 LF 一样基于辅助表单字段做条件判断与表单取人。
 * 注意: DIY 辅助表单仅作为字段名契约(辅助表单字段名须与业务表单VO字段名完全一致),
 * 不参与审批渲染; 运行期由本助手从 businessDataVo 按字段名反射取值组装 lfFields。
 */
@Service
public class LFFormDataRuntimeHelper {

    @Autowired
    private BpmnNodeService bpmnNodeService;
    @Autowired
    private BpmnConfService bpmnConfService;
    @Autowired
    private BpmBusinessProcessService bpmBusinessProcessService;

    /**
     * 将 vo.lfFields 拷贝到 startConditionsVo.lfConditions
     * (若 vo 已显式提供 lfConditions 则优先使用),供 LF 条件 judge 读取。
     */
    public void populateLfConditions(BpmnStartConditionsVo startConditionsVo, BusinessDataVo vo) {
        if (!CollectionUtils.isEmpty(vo.getLfConditions())) {
            startConditionsVo.setLfConditions(vo.getLfConditions());
        } else {
            startConditionsVo.setLfConditions(vo.getLfFields());
        }
    }

    /**
     * 解析"表单上下文人员"节点: 按 HAS_FORM_RELATED_ASSIGNEES 标志查找 NODE_PROPERTY_FORM_RELATED 节点,
     * 从 vo.lfFields 中按字段名取出人员标识,写入 vo.node2formRelatedAssignees。
     */
    public void processFormRelatedUserConf(Long confId, Integer extraFlags, BusinessDataVo vo) {
        if (extraFlags == null || !BpmnConfFlagsEnum.HAS_FORM_RELATED_ASSIGNEES.flagsContainsCurrent(extraFlags)) {
            return;
        }
        Map<String, Object> lfFields = vo.getLfFields();
        List<BpmnNode> formRelatedNodes = bpmnNodeService.list(Wrappers.<BpmnNode>lambdaQuery()
                .eq(BpmnNode::getConfId, confId)
                .eq(BpmnNode::getNodeProperty, NodePropertyEnum.NODE_PROPERTY_FORM_RELATED.getCode()));
        Map<String, List<String>> node2formRelatedAssignees = new HashMap<>();
        if (!CollectionUtils.isEmpty(formRelatedNodes)) {
            for (BpmnNode node : formRelatedNodes) {
                List<BpmnNodeApproverConfJson.FormRelatedUserConf> formRelatedConfs = getFormRelatedConfsFromNode(node);
                for (BpmnNodeApproverConfJson.FormRelatedUserConf formRelatedConf : formRelatedConfs) {
                    String valueJson = formRelatedConf.getValueJson();
                    if (StringUtils.isEmpty(valueJson)) {
                        throw new AFBizException(BusinessErrorEnum.PARAMS_IS_NULL);
                    }
                    List<BaseIdTranStruVo> formInfos = JSON.parseArray(valueJson, BaseIdTranStruVo.class);
                    List<String> formValues = new ArrayList<>();
                    for (BaseIdTranStruVo formInfo : formInfos) {
                        String formName = formInfo.getId();
                        Object formVal = lfFields.get(formName);
                        if (formVal instanceof Iterable) {
                            Iterator iteratorf = ((Iterable) formVal).iterator();
                            while (iteratorf.hasNext()) {
                                formValues.add(iteratorf.next().toString());
                            }
                        } else {
                            formValues.add(formVal.toString());
                        }
                    }
                    node2formRelatedAssignees.put(node.getId().toString(), formValues);
                }
            }
        }
        if (node2formRelatedAssignees.isEmpty()) {
            throw new AFBizException("migration error,please contact the author");
        }
        vo.setNode2formRelatedAssignees(node2formRelatedAssignees);
    }

    /**
     * 解析得到指定流程已引用的辅助表单字段名集合 = 条件字段名 ∪ 表单取人字段名。
     * 条件字段名: 条件节点(node_type=3) conditionsConf.conditionGroups[].extJson → columnDbname。
     * 表单取人字段名: NODE_PROPERTY_FORM_RELATED 节点 approverConf.formRelatedUserConfList[].valueJson → id。
     * 空集合表示未配置任何条件/取人引用,不抛异常。
     */
    public Set<String> collectReferencedFieldNames(Long confId) {
        Set<String> names = new LinkedHashSet<>();
        if (confId == null) {
            return names;
        }
        // 条件字段名
        List<BpmnNode> conditionNodes = bpmnNodeService.list(Wrappers.<BpmnNode>lambdaQuery()
                .eq(BpmnNode::getConfId, confId)
                .eq(BpmnNode::getNodeType, 3)
                .eq(BpmnNode::getIsDel, 0));
        if (!CollectionUtils.isEmpty(conditionNodes)) {
            for (BpmnNode node : conditionNodes) {
                names.addAll(extractConditionParamNames(node));
            }
        }
        // 表单取人字段名
        List<BpmnNode> formRelatedNodes = bpmnNodeService.list(Wrappers.<BpmnNode>lambdaQuery()
                .eq(BpmnNode::getConfId, confId)
                .eq(BpmnNode::getNodeProperty, NodePropertyEnum.NODE_PROPERTY_FORM_RELATED.getCode())
                .eq(BpmnNode::getIsDel, 0));
        if (!CollectionUtils.isEmpty(formRelatedNodes)) {
            for (BpmnNode node : formRelatedNodes) {
                for (BpmnNodeApproverConfJson.FormRelatedUserConf conf : getFormRelatedConfsFromNode(node)) {
                    String valueJson = conf.getValueJson();
                    if (StringUtils.isEmpty(valueJson)) {
                        continue;
                    }
                    List<BaseIdTranStruVo> formInfos = JSON.parseArray(valueJson, BaseIdTranStruVo.class);
                    if (CollectionUtils.isEmpty(formInfos)) {
                        continue;
                    }
                    for (BaseIdTranStruVo formInfo : formInfos) {
                        if (formInfo.getId() != null) {
                            names.add(formInfo.getId());
                        }
                    }
                }
            }
        }
        return names;
    }

    /**
     * 按 processNumber/formCode 解析当前生效的 BpmnConf;解析不到返回 null。
     */
    public BpmnConf resolveBpmnConf(BusinessDataVo businessDataVo) {
        String processNumber = businessDataVo.getProcessNumber();
        if (!StringUtils.isEmpty(processNumber)) {
            BpmBusinessProcess process = bpmBusinessProcessService.getBpmBusinessProcess(processNumber);
            if (process == null || StringUtils.isEmpty(process.getVersion())) {
                return null;
            }
            return bpmnConfService.getOne(new QueryWrapper<BpmnConf>()
                    .eq("bpmn_code", process.getVersion())
                    .eq("effective_status", 1));
        }
        if (!StringUtils.isEmpty(businessDataVo.getFormCode())) {
            return bpmnConfService.getOne(new QueryWrapper<BpmnConf>()
                    .eq("form_code", businessDataVo.getFormCode())
                    .eq("effective_status", 1));
        }
        return null;
    }

    /**
     * 用 MethodHandles.lookup 按字段名从 businessDataVo 取值(遍历父类链)。
     * 契约: 辅助表单字段名须与业务表单VO字段名完全一致;取不到抛异常,强制用户显式解决。
     */
    public Map<String, Object> extractFieldsByLookup(BusinessDataVo businessDataVo, Collection<String> fieldNames) throws Throwable {
        Map<String, Object> result = new HashMap<>();
        if (fieldNames == null || fieldNames.isEmpty()) {
            return result;
        }
        Class<?> clazz = businessDataVo.getClass();
        MethodHandles.Lookup lookup = MethodHandles.lookup();
        for (String fieldName : fieldNames) {
            Field field = findFieldInHierarchy(clazz, fieldName);
            if (field == null) {
                throw new AFBizException("辅助表单字段 '" + fieldName + "' 在业务数据对象中找不到对应属性,请确认辅助表单字段名与业务表单VO字段名完全一致");
            }
            field.setAccessible(true);
            MethodHandle getter = lookup.unreflectGetter(field);
            Object value = getter.invoke(businessDataVo);
            if (value != null) {
                result.put(fieldName, value);
            }
        }
        return result;
    }

    private List<String> extractConditionParamNames(BpmnNode node) {
        List<String> result = new ArrayList<>();
        String nodeConfigJson = node.getNodeConfigJson();
        if (StringUtils.isEmpty(nodeConfigJson)) {
            return result;
        }
        BpmnNodeConfigJson nodeConfig = JsonConfUtil.parseNodeConfig(nodeConfigJson);
        if (nodeConfig == null || nodeConfig.getConditionsConf() == null) {
            return result;
        }
        BpmnNodeConditionsConfJson conditionsConf = nodeConfig.getConditionsConf();
        List<BpmnNodeConditionsConfJson.ConditionGroup> groups = conditionsConf.getConditionGroups();
        if (CollectionUtils.isEmpty(groups)) {
            return result;
        }
        for (BpmnNodeConditionsConfJson.ConditionGroup group : groups) {
            if (Objects.equals(group.getIsDefault(), 1)) {
                continue;
            }
            String extJson = group.getExtJson();
            if (StringUtils.isEmpty(extJson)) {
                continue;
            }
            List<List<BpmnNodeConditionsConfVueVo>> extFieldsGroup =
                    JSON.parseObject(extJson, new com.alibaba.fastjson2.TypeReference<List<List<BpmnNodeConditionsConfVueVo>>>() {});
            if (CollectionUtils.isEmpty(extFieldsGroup)) {
                continue;
            }
            for (List<BpmnNodeConditionsConfVueVo> groupConds : extFieldsGroup) {
                for (BpmnNodeConditionsConfVueVo cond : groupConds) {
                    String columnDbname = cond.getColumnDbname();
                    if (!StringUtils.isEmpty(columnDbname)) {
                        result.add(columnDbname);
                    }
                }
            }
        }
        return result;
    }

    private List<BpmnNodeApproverConfJson.FormRelatedUserConf> getFormRelatedConfsFromNode(BpmnNode node) {
        String nodeConfigJson = node.getNodeConfigJson();
        if (StringUtils.isEmpty(nodeConfigJson)) {
            return Collections.emptyList();
        }
        BpmnNodeConfigJson nodeConfig = JsonConfUtil.parseNodeConfig(nodeConfigJson);
        if (nodeConfig == null || nodeConfig.getApproverConf() == null
                || CollectionUtils.isEmpty(nodeConfig.getApproverConf().getFormRelatedUserConfList())) {
            return Collections.emptyList();
        }
        return nodeConfig.getApproverConf().getFormRelatedUserConfList();
    }

    private Field findFieldInHierarchy(Class<?> clazz, String fieldName) {
        while (clazz != null && clazz != Object.class) {
            try {
                return clazz.getDeclaredField(fieldName);
            } catch (NoSuchFieldException ignored) {
                clazz = clazz.getSuperclass();
            }
        }
        return null;
    }
}
