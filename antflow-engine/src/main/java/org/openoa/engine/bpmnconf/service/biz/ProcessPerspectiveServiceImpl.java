package org.openoa.engine.bpmnconf.service.biz;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.extern.slf4j.Slf4j;
import org.openoa.base.constant.enums.BpmnConfFlagsEnum;
import org.openoa.base.constant.enums.NodeTypeEnum;
import org.openoa.base.entity.BpmnConf;
import org.openoa.base.entity.jsonconf.BpmnConfConfigJson;
import org.openoa.base.entity.jsonconf.BpmnNodeButtonSignConfJson;
import org.openoa.base.entity.jsonconf.BpmnNodeConfigJson;
import org.openoa.base.entity.jsonconf.JsonConfUtil;
import org.openoa.base.vo.*;
import org.openoa.engine.bpmnconf.service.interf.biz.BpmnConfBizService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 流程透视搜索服务
 */
@Slf4j
@Service
public class ProcessPerspectiveServiceImpl {

    @Autowired
    private BpmnConfBizService bpmnConfBizService;

    private static final int DEFAULT_BATCH_SIZE = 5;
    private static final int USE_EXTERNAL_FORM_FLAG = 64;
    private static final int USE_AUXILIARY_FORM_FLAG = 128;

    /**
     * 分批搜索流程配置
     */
    public ProcessPerspectiveResultVo search(ProcessPerspectiveVo vo) {
        List<String> formCodes = vo.getFormCodes();
        if (CollectionUtils.isEmpty(formCodes)) {
            return ProcessPerspectiveResultVo.builder()
                    .results(Collections.emptyList())
                    .hasMore(false)
                    .processedCount(0)
                    .totalCount(0)
                    .build();
        }

        int batchSize = vo.getBatchSize() != null ? vo.getBatchSize() : DEFAULT_BATCH_SIZE;
        int offset = vo.getOffset() != null ? vo.getOffset() : 0;
        int totalCount = formCodes.size();

        // 截取本批处理的formCodes
        int end = Math.min(offset + batchSize, totalCount);
        List<String> batchFormCodes = formCodes.subList(offset, end);
        boolean hasMore = end < totalCount;

        ProcessPerspectiveVo.Filters filters = vo.getFilters();
        String versionMode = vo.getVersionMode() != null ? vo.getVersionMode() : "RECENT";
        int recentN = vo.getRecentN() != null ? vo.getRecentN() : 1;

        List<ProcessPerspectiveResultVo.FormCodeResult> results = new ArrayList<>();

        for (String formCode : batchFormCodes) {
            // 1. SQL粗筛: 获取候选BpmnConf列表
            List<BpmnConf> candidates = getCandidates(formCode, versionMode, recentN, filters);
            if (CollectionUtils.isEmpty(candidates)) {
                continue;
            }

            // 2. 内存精筛: 逐条检查节点级条件
            List<ProcessPerspectiveResultVo.VersionMatch> matches = new ArrayList<>();
            for (BpmnConf conf : candidates) {
                if (matchesNodeLevelFilters(conf, filters)) {
                    matches.add(ProcessPerspectiveResultVo.VersionMatch.builder()
                            .confId(conf.getId())
                            .bpmnCode(conf.getBpmnCode())
                            .bpmnName(conf.getBpmnName())
                            .effectiveStatus(conf.getEffectiveStatus())
                            .createTime(conf.getCreateTime())
                            .build());
                }
            }

            if (!matches.isEmpty()) {
                // 确定flowType
                BpmnConf first = candidates.get(0);
                String flowType = determineFlowType(first);
                String displayName = determineDisplayName(first);

                results.add(ProcessPerspectiveResultVo.FormCodeResult.builder()
                        .formCode(formCode)
                        .displayName(displayName)
                        .flowType(flowType)
                        .latestMatch(matches.get(0))
                        .allMatches(matches)
                        .build());
            }
        }

        return ProcessPerspectiveResultVo.builder()
                .results(results)
                .hasMore(hasMore)
                .processedCount(batchFormCodes.size())
                .totalCount(totalCount)
                .build();
    }

    /**
     * SQL层粗筛: 根据简单条件获取候选BpmnConf
     */
    private List<BpmnConf> getCandidates(String formCode, String versionMode, int recentN,
                                         ProcessPerspectiveVo.Filters filters) {
        LambdaQueryWrapper<BpmnConf> wrapper = Wrappers.<BpmnConf>lambdaQuery()
                .eq(BpmnConf::getFormCode, formCode)
                .eq(BpmnConf::getIsDel, 0);

        // 版本模式
        if ("EFFECTIVE".equals(versionMode)) {
            wrapper.eq(BpmnConf::getEffectiveStatus, 1);
        }

        // SQL可下推条件
        if (filters != null) {
            if (StringUtils.hasText(filters.getBpmnNameLike())) {
                wrapper.like(BpmnConf::getBpmnName, filters.getBpmnNameLike());
            }
            if (Boolean.TRUE.equals(filters.getUseExternalForm())) {
                wrapper.apply("extra_flags & {0} = {0}", USE_EXTERNAL_FORM_FLAG);
            }
            if (Boolean.TRUE.equals(filters.getDeduplication())) {
                wrapper.gt(BpmnConf::getDeduplicationType, 1);
            }
        }

        wrapper.orderByDesc(BpmnConf::getCreateTime);

        List<BpmnConf> list = bpmnConfBizService.getService().list(wrapper);
        if (CollectionUtils.isEmpty(list)) {
            return Collections.emptyList();
        }

        // RECENT模式截取前N条
        if ("RECENT".equals(versionMode) && list.size() > recentN) {
            return list.subList(0, recentN);
        }
        return list;
    }

    /**
     * 节点级条件内存匹配
     */
    private boolean matchesNodeLevelFilters(BpmnConf conf, ProcessPerspectiveVo.Filters filters) {
        if (filters == null) {
            return true;
        }

        // 判断是否需要加载节点(有节点级条件时才加载)
        boolean needNodes = needLoadNodes(filters);

        // conf级条件: 允许撤回/作废/转发 (viewPageButtons)
        if (!matchesViewPageButtons(conf, filters)) {
            return false;
        }

        // conf级条件: 通知(conf级)
        BpmnConfConfigJson confConfig = parseConfConfig(conf.getConfConfigJson());

        if (!needNodes) {
            // 仅需conf级判断
            if (Boolean.TRUE.equals(filters.getHasNotice())) {
                return hasConfLevelNotice(confConfig);
            }
            return true;
        }

        // 加载完整detail(含节点)
        BpmnConfVo detail;
        try {
            detail = bpmnConfBizService.detail(conf.getId());
        } catch (Exception e) {
            log.warn("加载流程配置详情失败, confId={}", conf.getId(), e);
            return false;
        }
        if (detail == null || CollectionUtils.isEmpty(detail.getNodes())) {
            // 无节点时,仅conf级通知可满足
            if (Boolean.TRUE.equals(filters.getHasNotice())) {
                return hasConfLevelNotice(confConfig);
            }
            return false;
        }

        List<BpmnNodeVo> nodes = detail.getNodes();

        // 表单字段匹配
        if (StringUtils.hasText(filters.getFormFieldKeyword())) {
            if (!matchesFormField(conf, confConfig, filters.getFormFieldKeyword())) {
                return false;
            }
        }

        // 字段权限: 非发起人节点含可编辑字段
        if (Boolean.TRUE.equals(filters.getHasEditableFieldPerm())) {
            if (!matchesEditableFieldPerm(nodes)) {
                return false;
            }
        }

        // 审批人规则
        if (!CollectionUtils.isEmpty(filters.getApproverRules())) {
            if (!matchesApproverRules(nodes, filters.getApproverRules())) {
                return false;
            }
        }

        // 额外增加/排除审批
        if (Boolean.TRUE.equals(filters.getHasAdditionalSign()) || Boolean.TRUE.equals(filters.getHasExcludeSign())) {
            if (!matchesAdditionalSign(nodes, filters)) {
                return false;
            }
        }

        // 审批人为空规则
        if (!CollectionUtils.isEmpty(filters.getNoHeaderActions())) {
            if (!matchesNoHeaderAction(nodes, filters.getNoHeaderActions())) {
                return false;
            }
        }

        // 按钮权限
        if (!CollectionUtils.isEmpty(filters.getButtonTypes())) {
            if (!matchesButtonTypes(nodes, filters.getButtonTypes())) {
                return false;
            }
        }

        // 通知(conf级 + node级)
        if (Boolean.TRUE.equals(filters.getHasNotice())) {
            if (!hasConfLevelNotice(confConfig) && !hasNodeLevelNotice(nodes)) {
                return false;
            }
        }

        // 节点类型
        if (!CollectionUtils.isEmpty(filters.getNodeTypes())) {
            if (!matchesNodeTypes(nodes, filters.getNodeTypes())) {
                return false;
            }
        }

        return true;
    }

    private boolean needLoadNodes(ProcessPerspectiveVo.Filters filters) {
        return Boolean.TRUE.equals(filters.getHasEditableFieldPerm())
                || !CollectionUtils.isEmpty(filters.getApproverRules())
                || Boolean.TRUE.equals(filters.getHasAdditionalSign())
                || Boolean.TRUE.equals(filters.getHasExcludeSign())
                || !CollectionUtils.isEmpty(filters.getNoHeaderActions())
                || !CollectionUtils.isEmpty(filters.getButtonTypes())
                || !CollectionUtils.isEmpty(filters.getNodeTypes())
                || Boolean.TRUE.equals(filters.getHasNotice())
                || StringUtils.hasText(filters.getFormFieldKeyword());
    }

    /**
     * 检查viewPageButtons中的高级设置(撤回/作废/转发)
     */
    private boolean matchesViewPageButtons(BpmnConf conf, ProcessPerspectiveVo.Filters filters) {
        boolean needCheck = Boolean.TRUE.equals(filters.getAllowRevoke())
                || Boolean.TRUE.equals(filters.getAllowCancel())
                || Boolean.TRUE.equals(filters.getAllowForward());
        if (!needCheck) {
            return true;
        }

        BpmnConfConfigJson confConfig = parseConfConfig(conf.getConfConfigJson());
        if (confConfig == null || CollectionUtils.isEmpty(confConfig.getViewPageButtons())) {
            return false;
        }

        Set<Integer> startBtnTypes = confConfig.getViewPageButtons().stream()
                .filter(b -> b.getViewType() != null && b.getViewType() == 1)
                .map(BpmnConfConfigJson.ViewPageButton::getButtonType)
                .collect(Collectors.toSet());

        if (Boolean.TRUE.equals(filters.getAllowRevoke()) && !startBtnTypes.contains(29)) {
            return false;
        }
        if (Boolean.TRUE.equals(filters.getAllowCancel()) && !startBtnTypes.contains(7)) {
            return false;
        }
        if (Boolean.TRUE.equals(filters.getAllowForward()) && !startBtnTypes.contains(15)) {
            return false;
        }
        return true;
    }

    private boolean hasConfLevelNotice(BpmnConfConfigJson confConfig) {
        if (confConfig == null) {
            return false;
        }
        return !CollectionUtils.isEmpty(confConfig.getNoticeChannelTypes())
                || !CollectionUtils.isEmpty(confConfig.getConfTemplates());
    }

    private boolean hasNodeLevelNotice(List<BpmnNodeVo> nodes) {
        for (BpmnNodeVo node : nodes) {
            BpmnNodeConfigJson nodeConfig = node.getNodeConfigJsonObj();
            if (nodeConfig != null && nodeConfig.getTemplateConf() != null) {
                return true;
            }
            // fallback: templateVos
            if (!CollectionUtils.isEmpty(node.getTemplateVos())) {
                return true;
            }
        }
        return false;
    }

    /**
     * 表单字段匹配: 检查lowCodeFormConfig.fields中是否包含关键字
     */
    private boolean matchesFormField(BpmnConf conf, BpmnConfConfigJson confConfig, String keyword) {
        // 仅LF流程、DIY辅助表单、第三方低代码有表单字段
        boolean hasFormCapability = (conf.getIsLowCodeFlow() != null && conf.getIsLowCodeFlow() == 1)
                || (conf.getExtraFlags() != null && (conf.getExtraFlags() & USE_AUXILIARY_FORM_FLAG) == USE_AUXILIARY_FORM_FLAG);
        if (!hasFormCapability) {
            return false;
        }
        if (confConfig == null || confConfig.getLowCodeFormConfig() == null
                || CollectionUtils.isEmpty(confConfig.getLowCodeFormConfig().getFields())) {
            return false;
        }
        String lowerKeyword = keyword.toLowerCase();
        return confConfig.getLowCodeFormConfig().getFields().stream().anyMatch(f ->
                (f.getFieldName() != null && f.getFieldName().toLowerCase().contains(lowerKeyword))
                        || (f.getFieldId() != null && f.getFieldId().toLowerCase().contains(lowerKeyword)));
    }

    /**
     * 非发起人节点含可编辑字段权限(E或W)
     */
    private boolean matchesEditableFieldPerm(List<BpmnNodeVo> nodes) {
        for (BpmnNodeVo node : nodes) {
            if (NodeTypeEnum.NODE_TYPE_START.getCode().equals(node.getNodeType())) {
                continue;
            }
            List<LFFieldControlVO> controls = node.getLfFieldControlVOs();
            if (!CollectionUtils.isEmpty(controls)) {
                for (LFFieldControlVO ctrl : controls) {
                    if ("E".equals(ctrl.getPerm()) || "W".equals(ctrl.getPerm())) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * 审批人规则: 流程中存在任一选中类型(OR)
     */
    private boolean matchesApproverRules(List<BpmnNodeVo> nodes, List<Integer> rules) {
        Set<Integer> ruleSet = new HashSet<>(rules);
        for (BpmnNodeVo node : nodes) {
            if (node.getNodeProperty() != null && ruleSet.contains(node.getNodeProperty())) {
                return true;
            }
        }
        return false;
    }

    /**
     * 额外增加/排除审批
     */
    private boolean matchesAdditionalSign(List<BpmnNodeVo> nodes, ProcessPerspectiveVo.Filters filters) {
        for (BpmnNodeVo node : nodes) {
            BpmnNodePropertysVo prop = node.getProperty();
            if (prop == null || CollectionUtils.isEmpty(prop.getAdditionalSignInfoList())) {
                continue;
            }
            for (ExtraSignInfoVo signInfo : prop.getAdditionalSignInfoList()) {
                if (Boolean.TRUE.equals(filters.getHasAdditionalSign())
                        && signInfo.getPropertyType() != null && signInfo.getPropertyType() == 1) {
                    return true;
                }
                if (Boolean.TRUE.equals(filters.getHasExcludeSign())
                        && signInfo.getPropertyType() != null && signInfo.getPropertyType() == 2) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 审批人为空规则(OR)
     */
    private boolean matchesNoHeaderAction(List<BpmnNodeVo> nodes, List<Integer> actions) {
        Set<Integer> actionSet = new HashSet<>(actions);
        for (BpmnNodeVo node : nodes) {
            if (node.getNoHeaderAction() != null && actionSet.contains(node.getNoHeaderAction())) {
                return true;
            }
        }
        return false;
    }

    /**
     * 按钮权限: 任意节点包含选中按钮类型(OR)
     */
    private boolean matchesButtonTypes(List<BpmnNodeVo> nodes, List<Integer> buttonTypes) {
        Set<Integer> btnSet = new HashSet<>(buttonTypes);
        for (BpmnNodeVo node : nodes) {
            BpmnNodeConfigJson nodeConfig = node.getNodeConfigJsonObj();
            if (nodeConfig != null && nodeConfig.getButtonSignConf() != null
                    && !CollectionUtils.isEmpty(nodeConfig.getButtonSignConf().getButtonConfList())) {
                for (BpmnNodeButtonSignConfJson.ButtonConf bc : nodeConfig.getButtonSignConf().getButtonConfList()) {
                    if (bc.getButtonType() != null && btnSet.contains(bc.getButtonType())) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * 节点类型(OR)
     */
    private boolean matchesNodeTypes(List<BpmnNodeVo> nodes, List<Integer> nodeTypes) {
        Set<Integer> typeSet = new HashSet<>(nodeTypes);
        for (BpmnNodeVo node : nodes) {
            if (node.getNodeType() != null && typeSet.contains(node.getNodeType())) {
                return true;
            }
        }
        return false;
    }

    private BpmnConfConfigJson parseConfConfig(String confConfigJson) {
        if (!StringUtils.hasText(confConfigJson)) {
            return null;
        }
        return JsonConfUtil.parseConfConfig(confConfigJson);
    }

    private String determineFlowType(BpmnConf conf) {
        if (conf.getIsOutSideProcess() != null && conf.getIsOutSideProcess() == 1) {
            return "OUTSIDE";
        }
        if (conf.getIsLowCodeFlow() != null && conf.getIsLowCodeFlow() == 1) {
            return "LF";
        }
        return "DIY";
    }

    private String determineDisplayName(BpmnConf conf) {
        // 使用bpmnName作为显示名,去除版本后缀
        return conf.getBpmnName();
    }
}
