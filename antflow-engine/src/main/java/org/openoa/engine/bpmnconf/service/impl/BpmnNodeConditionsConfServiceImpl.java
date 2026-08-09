package org.openoa.engine.bpmnconf.service.impl;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.TypeReference;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.openoa.base.entity.BpmBusinessProcess;
import org.openoa.base.entity.BpmnConf;
import org.openoa.base.entity.BpmnNode;
import org.openoa.base.entity.jsonconf.BpmnNodeConditionsConfJson;
import org.openoa.base.entity.jsonconf.BpmnNodeConfigJson;
import org.openoa.base.entity.jsonconf.JsonConfUtil;
import org.openoa.base.exception.AFBizException;
import org.openoa.base.interf.BpmBusinessProcessService;
import org.openoa.base.vo.BpmnNodeConditionsConfVueVo;
import org.openoa.base.vo.BusinessDataVo;
import org.openoa.engine.bpmnconf.service.interf.repository.BpmnConfService;
import org.openoa.engine.bpmnconf.service.interf.repository.BpmnNodeConditionsConfService;
import org.openoa.engine.bpmnconf.service.interf.repository.BpmnNodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

import java.util.*;

@Slf4j
@Repository
public class BpmnNodeConditionsConfServiceImpl  implements BpmnNodeConditionsConfService {

    @Autowired
    private BpmnConfService bpmnConfService;

    @Autowired
    private BpmnNodeService bpmnNodeService;

    @Autowired
    private BpmBusinessProcessService bpmBusinessProcessService;

    @Override
    public List<String> queryConditionParamNameByProcessNumber(BusinessDataVo businessDataVo) {
        String processNumber = businessDataVo.getProcessNumber();
        BpmnConf bpmnConf;
        if (!StringUtils.isEmpty(processNumber)) {
            BpmBusinessProcess process = bpmBusinessProcessService.getBpmBusinessProcess(processNumber);
            if (process == null || StringUtils.isEmpty(process.getVersion())) {
                return Collections.emptyList();
            }
            bpmnConf = bpmnConfService.getOne(new QueryWrapper<BpmnConf>()
                    .eq("bpmn_code", process.getVersion())
                    .eq("effective_status", 1));
        } else {
            bpmnConf = bpmnConfService.getOne(new QueryWrapper<BpmnConf>()
                    .eq("form_code", businessDataVo.getFormCode())
                    .eq("effective_status", 1));
        }
        if (bpmnConf == null) {
            return Collections.emptyList();
        }

        List<BpmnNode> conditionNodes = bpmnNodeService.list(new QueryWrapper<BpmnNode>()
                .eq("conf_id", bpmnConf.getId())
                .eq("node_type", 3)
                .eq("is_del", 0));
        if (CollectionUtils.isEmpty(conditionNodes)) {
            return Collections.emptyList();
        }

        Set<String> fieldNames = new LinkedHashSet<>();
        for (BpmnNode node : conditionNodes) {
            List<String> names = extractConditionParamNamesFromJson(node);
            fieldNames.addAll(names);
        }

        if (fieldNames.isEmpty()) {
           throw new AFBizException("migration error,please contact the author");
        }

        return new ArrayList<>(fieldNames);
    }

    private List<String> extractConditionParamNamesFromJson(BpmnNode node) {
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
            List<List<BpmnNodeConditionsConfVueVo>> extFieldsGroup = JSON.parseObject(extJson,
                    new TypeReference<List<List<BpmnNodeConditionsConfVueVo>>>() {});
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

}
