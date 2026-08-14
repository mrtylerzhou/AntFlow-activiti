package org.openoa.engine.bpmnconf.service.biz;

import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.openoa.base.constant.enums.SortTypeEnum;
import org.openoa.base.dto.PageDto;
import org.openoa.base.entity.BpmUserAutoApprove;
import org.openoa.base.entity.BpmnConf;
import org.openoa.base.entity.BpmnNode;
import org.openoa.base.entity.jsonconf.BpmnNodeAutoNodeConfJson;
import org.openoa.base.entity.jsonconf.BpmnNodeConfigJson;
import org.openoa.base.entity.jsonconf.JsonConfUtil;
import org.openoa.base.exception.AFBizException;
import org.openoa.base.util.AFWrappers;
import org.openoa.base.util.MultiTenantUtil;
import org.openoa.base.util.PageUtils;
import org.openoa.base.util.SecurityUtils;
import org.openoa.base.vo.ResultAndPage;
import org.openoa.base.vo.UserAutoApproveVo;
import org.openoa.engine.bpmnconf.mapper.BpmUserAutoApproveMapper;
import org.openoa.engine.bpmnconf.service.impl.BpmUserAutoApproveServiceImpl;
import org.openoa.engine.bpmnconf.service.interf.repository.BpmnConfService;
import org.openoa.engine.bpmnconf.service.interf.repository.BpmnNodeService;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 用户自动审批设置 业务服务
 */
@Service
@Slf4j
public class UserAutoApproveBizServiceImpl {

    @Resource
    private BpmUserAutoApproveServiceImpl autoApproveService;
    @Resource
    private BpmUserAutoApproveMapper autoApproveMapper;
    @Resource
    private BpmnConfService bpmnConfService;
    @Resource
    private BpmnNodeService bpmnNodeService;

    // ==================== 列表 ====================

    public ResultAndPage<UserAutoApproveVo> listPage(PageDto pageDto, String ownerUserName, String formCode) {
        Page<BpmUserAutoApprove> page = PageUtils.getPageByPageDto(pageDto);
        LambdaQueryWrapper<BpmUserAutoApprove> qw = AFWrappers.<BpmUserAutoApprove>lambdaTenantQuery()
                .eq(BpmUserAutoApprove::getIsDel, 0)
                .like(StringUtils.hasText(ownerUserName), BpmUserAutoApprove::getOwnerUserName, ownerUserName)
                .like(StringUtils.hasText(formCode), BpmUserAutoApprove::getFormCode, formCode);
        Page<BpmUserAutoApprove> result = autoApproveMapper.selectPage(page, qw);

        List<BpmUserAutoApprove> records = result.getRecords();
        Map<String, String> formCode2ActiveBpmnCode = new HashMap<>();
        Map<String, BpmnConf> bpmnCode2Conf = new HashMap<>();
        if (!CollectionUtils.isEmpty(records)) {
            Set<String> formCodes = records.stream().map(BpmUserAutoApprove::getFormCode).collect(Collectors.toSet());
            List<BpmnConf> activeConfs = bpmnConfService.list(new QueryWrapper<BpmnConf>()
                    .in("form_code", formCodes).eq("effective_status", 1));
            for (BpmnConf conf : activeConfs) {
                formCode2ActiveBpmnCode.putIfAbsent(conf.getFormCode(), conf.getBpmnCode());
            }
            Set<String> bpmnCodes = records.stream().map(BpmUserAutoApprove::getBpmnCode).collect(Collectors.toSet());
            bpmnCodes.addAll(formCode2ActiveBpmnCode.values());
            List<BpmnConf> confs = bpmnConfService.list(new QueryWrapper<BpmnConf>().in("bpmn_code", bpmnCodes));
            for (BpmnConf conf : confs) {
                bpmnCode2Conf.putIfAbsent(conf.getBpmnCode(), conf);
            }
        }
        List<UserAutoApproveVo> vos = records.stream().map(e -> toVo(e, formCode2ActiveBpmnCode, bpmnCode2Conf)).collect(Collectors.toList());
        return PageUtils.getResultAndPage(vos, PageUtils.getPageDto(result));
    }

    private UserAutoApproveVo toVo(BpmUserAutoApprove e, Map<String, String> formCode2ActiveBpmnCode, Map<String, BpmnConf> bpmnCode2Conf) {
        UserAutoApproveVo vo = UserAutoApproveVo.builder()
                .id(e.getId())
                .ownerUserId(e.getOwnerUserId())
                .ownerUserName(e.getOwnerUserName())
                .formCode(e.getFormCode())
                .bpmnCode(e.getBpmnCode())
                .defaultComment(e.getDefaultComment())
                .enabled(e.getEnabled())
                .createTime(e.getCreateTime())
                .build();
        if (StringUtils.hasText(e.getNodeScopeJson())) {
            vo.setNodeScope(JSON.parseArray(e.getNodeScopeJson(), UserAutoApproveVo.NodeScopeItem.class));
        }
        if (StringUtils.hasText(e.getConditionJson())) {
            BpmnNodeAutoNodeConfJson conf = JSON.parseObject(e.getConditionJson(), BpmnNodeAutoNodeConfJson.class);
            if (conf != null) {
                vo.setConditionList(conf.getConditionList());
                vo.setGroupRelation(conf.getGroupRelation());
            }
        }
        String activeBpmnCode = formCode2ActiveBpmnCode.get(e.getFormCode());
        vo.setActive(activeBpmnCode != null && activeBpmnCode.equals(e.getBpmnCode()));
        BpmnConf conf = bpmnCode2Conf.get(e.getBpmnCode());
        if (conf != null) {
            vo.setConfId(conf.getId());
            vo.setBpmnName(conf.getBpmnName());
            vo.setFlowType(resolveFlowType(conf));
        }
        return vo;
    }

    private Integer resolveFlowType(BpmnConf conf) {
        if (Objects.equals(conf.getIsLowCodeFlow(), 1)) {
            return 2;
        }
        if (Objects.equals(conf.getIsOutSideProcess(), 1)) {
            return 3;
        }
        return 1;
    }

    // ==================== 活跃流程下拉 ====================

    public List<UserAutoApproveVo> activeConfList() {
        List<BpmnConf> confs = bpmnConfService.list(new QueryWrapper<BpmnConf>().eq("effective_status", 1));
        return confs.stream().map(c -> UserAutoApproveVo.builder()
                .id(c.getId())
                .formCode(c.getFormCode())
                .bpmnCode(c.getBpmnCode())
                .bpmnName(c.getBpmnName())
                .flowType(resolveFlowType(c))
                .build()).collect(Collectors.toList());
    }

    // ==================== 新增/编辑 ====================

    public void save(UserAutoApproveVo vo) {
        if (!StringUtils.hasText(vo.getFormCode())) {
            throw new AFBizException("请选择要自动审批的流程");
        }
        BpmnConf activeConf = getActiveConf(vo.getFormCode());
        if (!StringUtils.hasText(vo.getOwnerUserId())) {
            vo.setOwnerUserId(SecurityUtils.getLogInEmpIdStr());
            vo.setOwnerUserName(SecurityUtils.getLogInEmpNameSafe());
        }
        validateAndRefreshNodeScope(vo.getNodeScope(), activeConf.getId());

        BpmUserAutoApprove entity = BpmUserAutoApprove.builder()
                .ownerUserId(vo.getOwnerUserId())
                .ownerUserName(vo.getOwnerUserName())
                .formCode(vo.getFormCode())
                .bpmnCode(activeConf.getBpmnCode())
                .nodeScopeJson(serializeNodeScope(vo.getNodeScope()))
                .conditionJson(buildConditionJson(vo, activeConf))
                .defaultComment(vo.getDefaultComment())
                .enabled(vo.getEnabled() != null ? vo.getEnabled() : 1)
                .isDel(0)
                .tenantId(MultiTenantUtil.getCurrentTenantId())
                .createUser(SecurityUtils.getLogInEmpNameSafe())
                .createTime(new Date())
                .build();
        autoApproveMapper.insert(entity);
    }

    public void update(UserAutoApproveVo vo) {
        if (vo.getId() == null) {
            throw new AFBizException("id不能为空");
        }
        BpmUserAutoApprove entity = autoApproveMapper.selectById(vo.getId());
        if (entity == null || Objects.equals(entity.getIsDel(), 1)) {
            throw new AFBizException("配置不存在");
        }
        BpmnConf pointedConf = bpmnConfService.getOne(new QueryWrapper<BpmnConf>()
                .eq("bpmn_code", entity.getBpmnCode()).last("limit 1"));
        if (pointedConf == null) {
            throw new AFBizException("配置指向的流程版本不存在:" + entity.getBpmnCode());
        }
        validateAndRefreshNodeScope(vo.getNodeScope(), pointedConf.getId());
        entity.setNodeScopeJson(serializeNodeScope(vo.getNodeScope()));
        entity.setConditionJson(buildConditionJson(vo, pointedConf));
        entity.setDefaultComment(vo.getDefaultComment());
        if (vo.getEnabled() != null) {
            entity.setEnabled(vo.getEnabled());
        }
        entity.setUpdateUser(SecurityUtils.getLogInEmpNameSafe());
        entity.setUpdateTime(new Date());
        autoApproveMapper.updateById(entity);
    }

    public void toggle(Long id, Integer enabled) {
        BpmUserAutoApprove entity = autoApproveMapper.selectById(id);
        if (entity == null || Objects.equals(entity.getIsDel(), 1)) {
            throw new AFBizException("配置不存在");
        }
        entity.setEnabled(enabled);
        entity.setUpdateUser(SecurityUtils.getLogInEmpNameSafe());
        entity.setUpdateTime(new Date());
        autoApproveMapper.updateById(entity);
    }

    public void delete(Long id) {
        BpmUserAutoApprove entity = autoApproveMapper.selectById(id);
        if (entity == null || Objects.equals(entity.getIsDel(), 1)) {
            throw new AFBizException("配置不存在");
        }
        entity.setIsDel(1);
        entity.setUpdateUser(SecurityUtils.getLogInEmpNameSafe());
        entity.setUpdateTime(new Date());
        autoApproveMapper.updateById(entity);
    }

    // ==================== 复制 ====================

    public void copy(Long id) {
        BpmUserAutoApprove config = autoApproveMapper.selectById(id);
        if (config == null || Objects.equals(config.getIsDel(), 1)) {
            throw new AFBizException("配置不存在");
        }
        BpmnConf activeConf = getActiveConf(config.getFormCode());
        //同归属人已存在指向活跃版本的配置 → 禁止复制
        Long activeCnt = autoApproveMapper.selectCount(AFWrappers.<BpmUserAutoApprove>lambdaTenantQuery()
                .eq(BpmUserAutoApprove::getOwnerUserId, config.getOwnerUserId())
                .eq(BpmUserAutoApprove::getFormCode, config.getFormCode())
                .eq(BpmUserAutoApprove::getBpmnCode, activeConf.getBpmnCode())
                .eq(BpmUserAutoApprove::getIsDel, 0));
        if (activeCnt != null && activeCnt > 0) {
            throw new AFBizException("该流程已存在活跃版本的自动审批配置,不允许复制");
        }
        BpmnConf oldConf = bpmnConfService.getOne(new QueryWrapper<BpmnConf>()
                .eq("bpmn_code", config.getBpmnCode()));
        if (oldConf == null) {
            throw new AFBizException("配置指向的旧版本不存在:" + config.getBpmnCode());
        }
        //节点对比: 人审节点按elementId对应, 数量+名称一致
        Map<String, String> oldNodes = approverNodeMap(oldConf.getId());
        Map<String, String> newNodes = approverNodeMap(activeConf.getId());
        if (oldNodes.size() != newNodes.size()) {
            throw new AFBizException("节点数量发生变化(旧:" + oldNodes.size() + ",新:" + newNodes.size() + "),不允许复制");
        }
        for (Map.Entry<String, String> en : oldNodes.entrySet()) {
            String newName = newNodes.get(en.getKey());
            if (newName == null || !newName.equals(en.getValue())) {
                throw new AFBizException("节点[" + en.getValue() + "]在最新版本中不存在或名称已变化,不允许复制");
            }
        }
        //表单字段名并集对比
        Set<String> oldFields = formFieldUnion(oldConf.getId());
        Set<String> newFields = formFieldUnion(activeConf.getId());
        if (!oldFields.isEmpty() || !newFields.isEmpty()) {
            if (!oldFields.equals(newFields)) {
                throw new AFBizException("表单字段发生变化,不允许复制");
            }
        }
        BpmUserAutoApprove copied = BpmUserAutoApprove.builder()
                .ownerUserId(config.getOwnerUserId())
                .ownerUserName(config.getOwnerUserName())
                .formCode(config.getFormCode())
                .bpmnCode(activeConf.getBpmnCode())
                .nodeScopeJson(config.getNodeScopeJson())
                .conditionJson(config.getConditionJson())
                .defaultComment(config.getDefaultComment())
                .enabled(1)
                .isDel(0)
                .tenantId(config.getTenantId())
                .createUser(SecurityUtils.getLogInEmpNameSafe())
                .createTime(new Date())
                .build();
        autoApproveMapper.insert(copied);
    }

    // ==================== 运行时查询 ====================

    public List<BpmUserAutoApprove> listForRuntime(String ownerUserId, String formCode, String bpmnCode) {
        return autoApproveService.list(AFWrappers.<BpmUserAutoApprove>lambdaTenantQuery()
                .eq(BpmUserAutoApprove::getOwnerUserId, ownerUserId)
                .eq(BpmUserAutoApprove::getFormCode, formCode)
                .eq(BpmUserAutoApprove::getBpmnCode, bpmnCode)
                .eq(BpmUserAutoApprove::getEnabled, 1)
                .eq(BpmUserAutoApprove::getIsDel, 0));
    }

    // ==================== 内部工具 ====================

    private BpmnConf getActiveConf(String formCode) {
        BpmnConf conf = bpmnConfService.getOne(new QueryWrapper<BpmnConf>()
                .eq("form_code", formCode).eq("effective_status", 1));
        if (conf == null) {
            throw new AFBizException("formCode[" + formCode + "]无活跃的流程版本");
        }
        return conf;
    }

    /**
     * 校验节点范围: elementId 必须存在于指定conf的人审节点中, 并以库中名称刷新快照
     */
    private void validateAndRefreshNodeScope(List<UserAutoApproveVo.NodeScopeItem> nodeScope, Long confId) {
        if (CollectionUtils.isEmpty(nodeScope)) {
            return;
        }
        Map<String, String> nodeMap = approverNodeMap(confId);
        for (UserAutoApproveVo.NodeScopeItem item : nodeScope) {
            String name = nodeMap.get(item.getElementId());
            if (name == null) {
                throw new AFBizException("节点[" + item.getNodeName() + "(" + item.getElementId() + ")]不存在于该流程版本的人审节点中");
            }
            item.setNodeName(name);
        }
    }

    private Map<String, String> approverNodeMap(Long confId) {
        List<BpmnNode> nodes = bpmnNodeService.list(new QueryWrapper<BpmnNode>()
                .eq("conf_id", confId).eq("node_type", 4).eq("is_del", 0));
        Map<String, String> map = new HashMap<>();
        for (BpmnNode node : nodes) {
            map.put(node.getNodeId(), node.getNodeName());
        }
        return map;
    }

    /**
     * 取conf内各节点表单权限字段名并集
     */
    private Set<String> formFieldUnion(Long confId) {
        List<BpmnNode> nodes = bpmnNodeService.list(new QueryWrapper<BpmnNode>()
                .eq("conf_id", confId).eq("is_del", 0));
        Set<String> fields = new HashSet<>();
        for (BpmnNode node : nodes) {
            if (!StringUtils.hasText(node.getNodeConfigJson())) {
                continue;
            }
            BpmnNodeConfigJson configJson = JsonConfUtil.parseNodeConfig(node.getNodeConfigJson());
            if (configJson == null || configJson.getLowCodeConf() == null
                    || CollectionUtils.isEmpty(configJson.getLowCodeConf().getFieldControls())) {
                continue;
            }
            configJson.getLowCodeConf().getFieldControls().stream()
                    .filter(fc -> StringUtils.hasText(fc.getFieldName()))
                    .forEach(fc -> fields.add(fc.getFieldName()));
        }
        return fields;
    }

    private String serializeNodeScope(List<UserAutoApproveVo.NodeScopeItem> nodeScope) {
        if (CollectionUtils.isEmpty(nodeScope)) {
            return null;
        }
        return JSON.toJSONString(nodeScope);
    }

    /**
     * 条件JSON仅LF流程存储
     */
    private String buildConditionJson(UserAutoApproveVo vo, BpmnConf conf) {
        if (!Objects.equals(conf.getIsLowCodeFlow(), 1) || CollectionUtils.isEmpty(vo.getConditionList())) {
            return null;
        }
        BpmnNodeAutoNodeConfJson confJson = BpmnNodeAutoNodeConfJson.builder()
                .conditionList(vo.getConditionList())
                .groupRelation(vo.getGroupRelation())
                .build();
        return JSON.toJSONString(confJson);
    }
}
