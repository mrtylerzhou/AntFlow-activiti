package org.openoa.engine.bpmnconf.service.biz.personnelinfoprovider;

import com.google.common.base.Strings;
import lombok.extern.slf4j.Slf4j;
import org.openoa.base.exception.AFBizException;
import org.openoa.base.exception.BusinessErrorEnum;
import org.openoa.base.service.AfUserService;
import org.openoa.base.vo.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * "根据标签选择"审批人规则 Provider(nodeProperty=20)
 *
 * 评估时机: 流程发起时(submit),由 BpmnPersonnelFormatImpl 调用
 * 找人机制: 调用 AfUserService.queryApproversByLabel,把标签和自定义变量透传给用户实现
 * 找不到人时: 复用 AbstractMissingAssignNodeAssigneeVoProvider 的策略(跳过/转管理员/报错)
 *
 * 用户应在 AfUserServiceImpl 中改写 queryApproversByLabel 方法实现真实找人逻辑
 *
 * @Author JimuOffice
 * @since 1.0
 */
@Slf4j
@Component
public class LabelBasedPersonnelProvider extends AbstractMissingAssignNodeAssigneeVoProvider {
    @Autowired
    private AfUserService afUserService;

    @Override
    public List<BpmnNodeParamsAssigneeVo> getAssigneeList(BpmnNodeVo bpmnNodeVo, BpmnStartConditionsVo startConditionsVo) {
        if (bpmnNodeVo == null) {
            throw new AFBizException("node can not be null!");
        }
        BpmnNodePropertysVo propertysVo = bpmnNodeVo.getProperty();
        if (ObjectUtils.isEmpty(propertysVo) || ObjectUtils.isEmpty(propertysVo.getLabelBasedApproverRule())) {
            throw new AFBizException(BusinessErrorEnum.PARAMS_IS_NULL.getCodeStr(), "根据标签选择规则配置不能为空!");
        }
        LabelBasedApproverRuleVo ruleConfig = propertysVo.getLabelBasedApproverRule();
        if (Strings.isNullOrEmpty(ruleConfig.getLabelKey())) {
            throw new AFBizException(BusinessErrorEnum.PARAMS_IS_NULL.getCodeStr(), "根据标签选择的标签不能为空!");
        }
        //校验自定义变量组: 0-5组, varName 必填不重复, varValue 必填非空
        validateCustomVars(ruleConfig.getCustomVars());

        BusinessDataVo businessDataVo = startConditionsVo.getBusinessDataVo();
        List<BaseIdTranStruVo> approvers = afUserService.queryApproversByLabel(businessDataVo, ruleConfig);
        if (CollectionUtils.isEmpty(approvers)) {
            log.warn("根据标签选择审批人:节点:{}({}),标签:{},未获取到审批人",
                    bpmnNodeVo.getId(), bpmnNodeVo.getNodeName(), ruleConfig.getLabelKey());
            approvers = new ArrayList<>();
        }
        //super.provideAssigneeList 会处理找不到人策略(跳过/转管理员/报错)以及额外加签/减签
        return super.provideAssigneeList(bpmnNodeVo, approvers);
    }

    /**
     * 校验自定义变量组
     * - 组数: 0-5(空或null允许,表示无自定义变量)
     * - varName: 必填,同一规则内不可重复(作为 Map key)
     * - varValue: 必填非空字符串
     */
    private void validateCustomVars(List<CustomVarGroup> customVars) {
        if (CollectionUtils.isEmpty(customVars)) {
            return;
        }
        if (customVars.size() > 5) {
            throw new AFBizException("根据标签选择的自定义变量组不能超过5组,当前:" + customVars.size());
        }
        java.util.Set<String> varNameSet = new java.util.HashSet<>();
        for (int i = 0; i < customVars.size(); i++) {
            CustomVarGroup group = customVars.get(i);
            if (group == null) {
                throw new AFBizException("第" + (i + 1) + "组自定义变量不能为空");
            }
            if (Strings.isNullOrEmpty(group.getVarName())) {
                throw new AFBizException("第" + (i + 1) + "组自定义变量的变量名不能为空");
            }
            if (!varNameSet.add(group.getVarName())) {
                throw new AFBizException("第" + (i + 1) + "组自定义变量的变量名重复:" + group.getVarName());
            }
            if (Strings.isNullOrEmpty(group.getVarValue())) {
                throw new AFBizException("第" + (i + 1) + "组自定义变量的变量值不能为空");
            }
        }
    }
}
