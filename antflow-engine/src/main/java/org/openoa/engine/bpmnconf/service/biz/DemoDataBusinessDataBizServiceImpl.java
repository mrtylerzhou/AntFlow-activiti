package org.openoa.engine.bpmnconf.service.biz;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.commons.lang3.StringUtils;
import org.openoa.base.constant.StringConstants;
import org.openoa.base.constant.enums.LFFieldTypeEnum;
import org.openoa.base.constant.enums.ProcessStateEnum;
import org.openoa.base.dto.BusinessDataListPageReq;
import org.openoa.base.dto.PageDto;
import org.openoa.base.entity.*;
import org.openoa.base.entity.jsonconf.BpmnNodeConfigJson;
import org.openoa.base.entity.jsonconf.JsonConfUtil;
import org.openoa.base.entity.jsonconf.VariableConfigJson;
import org.openoa.base.exception.AFBizException;
import org.openoa.base.interf.BpmnProcessAdminProvider;
import org.openoa.base.service.AfRoleService;
import org.openoa.base.util.AFWrappers;
import org.openoa.base.util.PageUtils;
import org.openoa.base.util.SecurityUtils;
import org.openoa.base.vo.BaseIdTranStruVo;
import org.openoa.base.vo.BusinessDataListVo;
import org.openoa.common.entity.BpmVariableMultiplayer;
import org.openoa.common.entity.BpmVariableMultiplayerPersonnel;
import org.openoa.common.mapper.BpmVariableMultiplayerMapper;
import org.openoa.common.mapper.BpmVariableMultiplayerPersonnelMapper;
import org.openoa.engine.bpmnconf.mapper.BpmProcessPermissionsMapper;
import org.openoa.engine.bpmnconf.mapper.DemoDataBusinessDataMapper;
import org.openoa.engine.bpmnconf.service.impl.BpmFlowrunEntrustServiceImpl;
import org.openoa.engine.bpmnconf.service.impl.BpmVariableServiceImpl;
import org.openoa.engine.bpmnconf.service.impl.BpmnConfServiceImpl;
import org.openoa.engine.bpmnconf.service.impl.BpmnNodeServiceImpl;
import org.openoa.engine.bpmnconf.service.interf.repository.BpmnConfLfFormdataFieldService;
import org.openoa.engine.lowflow.entity.LFMainField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 演示数据-业务数据 动态列表
 * <p>分表兼容:主查询只访问 bpm_business_process(非路由表);
 * 字段值查询走 XML 原生 SQL 且参数名必须为 formCode(LF 分表路由拦截器依赖)</p>
 */
@Service
public class DemoDataBusinessDataBizServiceImpl {

    private static final Logger log = LoggerFactory.getLogger(DemoDataBusinessDataBizServiceImpl.class);

    /** 脱敏符号 */
    private static final String MASK_VALUE = "***";

    @Autowired
    private BpmBusinessProcessServiceImpl bpmBusinessProcessService;
    @Autowired
    private BpmnConfServiceImpl bpmnConfService;
    @Autowired
    private BpmnConfLfFormdataFieldService lfFormdataFieldService;
    @Autowired
    private BpmnNodeServiceImpl bpmnNodeService;
    @Autowired
    private DemoDataBusinessDataMapper demoDataBusinessDataMapper;
    @Autowired
    private BpmVariableServiceImpl bpmVariableService;
    @Autowired
    private BpmVariableMultiplayerMapper bpmVariableMultiplayerMapper;
    @Autowired
    private BpmVariableMultiplayerPersonnelMapper bpmVariableMultiplayerPersonnelMapper;
    @Autowired
    private BpmFlowrunEntrustServiceImpl bpmFlowrunEntrustService;
    @Autowired
    private BpmProcessPermissionsMapper bpmProcessPermissionsMapper;
    @Autowired
    private AfRoleService afRoleService;
    @Autowired
    private BpmnProcessAdminProvider bpmnProcessAdminProvider;

    // ==================== 列表 ====================

    /**
     * 分页查询低代码流程业务数据,动态拼接横向表
     * <p>流程编号优先:传入流程编号时忽略 formCode,只按流程编号查询;
     * 未传流程编号时才要求 formCode</p>
     *
     * @param req formCode(可选) + 流程编号关键字(可选) + 分页,两者至少传一个
     * @return columns + rows + total
     */
    public BusinessDataListVo listPage(BusinessDataListPageReq req) {
        if (req == null) {
            throw new AFBizException("请选择低代码流程或输入流程编号");
        }
        String formCode = StringUtils.isBlank(req.getFormCode()) ? null : req.getFormCode().trim();
        String processNumber = StringUtils.isBlank(req.getProcessNumber()) ? null : req.getProcessNumber().trim();
        if (formCode == null && processNumber == null) {
            throw new AFBizException("请选择低代码流程或输入流程编号");
        }

        // 1. 主查询分页(bpm_business_process 非路由表,安全)
        //    流程编号优先: 有流程编号时忽略 formCode 过滤
        Page<BpmBusinessProcess> page = PageUtils.getPageByPageDto(req.getPageDto() == null ? PageDto.first() : req.getPageDto());
        LambdaQueryWrapper<BpmBusinessProcess> qw = AFWrappers.<BpmBusinessProcess>lambdaTenantQuery()
                .eq(BpmBusinessProcess::getIsLowCodeFlow, 1)
                .eq(BpmBusinessProcess::getIsDel, 0)
                .orderByDesc(BpmBusinessProcess::getCreateTime);
        if (processNumber != null) {
            qw.like(BpmBusinessProcess::getBusinessNumber, processNumber);
        } else {
            qw.eq(BpmBusinessProcess::getProcessinessKey, formCode);
        }
        Page<BpmBusinessProcess> bpmPage = bpmBusinessProcessService.page(page, qw);
        List<BpmBusinessProcess> records = bpmPage.getRecords();

        // 2. 列定义来源 formCode: 未传时取第一条记录(最新)的 processiness_key
        if (formCode == null && !records.isEmpty()) {
            formCode = records.get(0).getProcessinessKey();
        }
        if (formCode == null) {
            // 无记录且未传 formCode: 无列定义,直接返回空
            return BusinessDataListVo.builder()
                    .columns(Collections.emptyList())
                    .rows(Collections.emptyList())
                    .total(bpmPage.getTotal())
                    .build();
        }

        // 3. 有效流程配置 -> confId(字段配置链路,不经路由表)
        BpmnConf bpmnConf = bpmnConfService.getOne(
                AFWrappers.<BpmnConf>lambdaTenantQuery()
                        .eq(BpmnConf::getFormCode, formCode)
                        .eq(BpmnConf::getEffectiveStatus, 1)
                        .last("LIMIT 1"));
        if (bpmnConf == null) {
            throw new AFBizException("未找到低代码流程 " + formCode + " 的有效配置");
        }
        Long confId = bpmnConf.getId();

        // 4. 字段配置(按id升序,列顺序即创建顺序)
        List<BpmnConfLfFormdataField> fieldConfigs = lfFormdataFieldService.list(
                new LambdaQueryWrapper<BpmnConfLfFormdataField>()
                        .eq(BpmnConfLfFormdataField::getBpmnConfId, confId)
                        .eq(BpmnConfLfFormdataField::getIsDel, 0)
                        .orderByAsc(BpmnConfLfFormdataField::getId));

        // 5. 隐藏字段集合(任意节点 perm=H -> 后端脱敏)
        Set<String> hiddenFieldIds = collectHiddenFieldIds(confId);

        // 6. 批量查竖表字段值(main_id in + formCode,参数名必须为formCode)
        List<Long> mainIds = records.stream()
                .map(BpmBusinessProcess::getBusinessId)
                .filter(Objects::nonNull)
                .map(id -> parseLongQuietly(id))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        Map<String, List<LFMainField>> mainId2Fields = new HashMap<>();
        if (!mainIds.isEmpty()) {
            List<LFMainField> lfMainFields = demoDataBusinessDataMapper.selectLfMainFieldsByMainIds(mainIds, formCode);
            mainId2Fields = lfMainFields.stream()
                    .filter(f -> f.getParentFieldId() == null || StringUtils.isBlank(f.getParentFieldId()))
                    .collect(Collectors.groupingBy(f -> String.valueOf(f.getMainId())));
        }

        // 7. 拼接 rows
        List<Map<String, Object>> rows = new ArrayList<>(records.size());
        for (BpmBusinessProcess bp : records) {
            Map<String, Object> row = new LinkedHashMap<>();
            row.put("description", bp.getDescription());
            row.put("version", bp.getVersion());
            // 动态列(业务数据)
            List<LFMainField> mainFields = mainId2Fields.getOrDefault(bp.getBusinessId(), Collections.emptyList());
            Map<String, List<LFMainField>> fieldId2Fields = mainFields.stream()
                    .collect(Collectors.groupingBy(LFMainField::getFieldId));
            for (BpmnConfLfFormdataField fieldConfig : fieldConfigs) {
                String fieldId = fieldConfig.getFieldId();
                if (hiddenFieldIds.contains(fieldId)) {
                    row.put(fieldKey(fieldId), MASK_VALUE);
                    continue;
                }
                List<LFMainField> fields = fieldId2Fields.get(fieldId);
                row.put(fieldKey(fieldId), buildFieldValue(fields, fieldConfig));
            }
            // 流程编号在业务数据之后、发起人之前
            row.put("processNumber", bp.getBusinessNumber());
            row.put("processKey", bp.getProcessinessKey());
            row.put("createUser", StringUtils.isNotBlank(bp.getUserName()) ? bp.getUserName() : bp.getCreateUser());
            row.put("processState", bp.getProcessState());
            row.put("processStateName", formatProcessState(bp.getProcessState()));
            row.put("createTime", bp.getCreateTime() == null ? null
                    : new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(bp.getCreateTime()));
            rows.add(row);
        }

        // 8. columns
        List<BusinessDataListVo.BusinessDataColumnVo> columns = buildColumns(fieldConfigs);

        return BusinessDataListVo.builder()
                .columns(columns)
                .rows(rows)
                .total(bpmPage.getTotal())
                .build();
    }

    /**
     * 构建列定义:流程名称(第1列) + 流程版本(第2列) + 动态业务数据列 + 流程编号 + 发起人/流程状态/发起时间
     */
    private List<BusinessDataListVo.BusinessDataColumnVo> buildColumns(List<BpmnConfLfFormdataField> fieldConfigs) {
        List<BusinessDataListVo.BusinessDataColumnVo> columns = new ArrayList<>();
        columns.add(BusinessDataListVo.BusinessDataColumnVo.builder().key("description").label("流程名称").fixed(true).build());
        columns.add(BusinessDataListVo.BusinessDataColumnVo.builder().key("version").label("流程版本").fixed(false).build());
        for (BpmnConfLfFormdataField fieldConfig : fieldConfigs) {
            columns.add(BusinessDataListVo.BusinessDataColumnVo.builder()
                    .key(fieldKey(fieldConfig.getFieldId()))
                    .label(fieldConfig.getFieldName())
                    .fixed(false)
                    .build());
        }
        // 流程编号在业务数据之后、发起人之前
        columns.add(BusinessDataListVo.BusinessDataColumnVo.builder().key("processNumber").label("流程编号").fixed(false).build());
        columns.add(BusinessDataListVo.BusinessDataColumnVo.builder().key("processStateName").label("流程状态").fixed(false).build());
        columns.add(BusinessDataListVo.BusinessDataColumnVo.builder().key("createUser").label("发起人").fixed(false).build());
        columns.add(BusinessDataListVo.BusinessDataColumnVo.builder().key("createTime").label("发起时间").fixed(false).build());
        return columns;
    }

    /**
     * 构建字段展示值:单值标量,多值逗号拼接;JSON 提取 name/label
     */
    private Object buildFieldValue(List<LFMainField> fields, BpmnConfLfFormdataField fieldConfig) {
        if (CollectionUtils.isEmpty(fields)) {
            return null;
        }
        LFFieldTypeEnum fieldTypeEnum = LFFieldTypeEnum.getByType(fieldConfig.getFieldType());
        List<String> values = new ArrayList<>(fields.size());
        for (LFMainField field : fields) {
            Object v = parseSingleFieldValue(field, fieldTypeEnum);
            if (v == null) {
                continue;
            }
            values.add(formatDisplayValue(v));
        }
        return String.join(",", values);
    }

    /**
     * 按字段类型取对应值列(参考 LowFlowBusinessController.getBusinessData)
     */
    private Object parseSingleFieldValue(LFMainField field, LFFieldTypeEnum fieldTypeEnum) {
        if (fieldTypeEnum == null) {
            return field.getFieldValue();
        }
        switch (fieldTypeEnum) {
            case NUMBER:
                return field.getFieldValueNumber();
            case DATE_TIME:
                return field.getFieldValueDt() == null ? null
                        : new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(field.getFieldValueDt());
            case DATE:
                return field.getFieldValueDt() == null ? null
                        : new SimpleDateFormat("yyyy-MM-dd").format(field.getFieldValueDt());
            case TEXT:
                return field.getFieldValueText();
            case BOOLEAN:
                return field.getFieldValue();
            default:
                return field.getFieldValue();
        }
    }

    /**
     * JSON 值提取 name/label 拼接;非 JSON 原样展示
     */
    private String formatDisplayValue(Object value) {
        if (value == null) {
            return "";
        }
        String str = value.toString().trim();
        if (StringUtils.isBlank(str)) {
            return "";
        }
        char first = str.charAt(0);
        if (first == '{' || first == '[') {
            try {
                if (first == '{') {
                    return extractNameFromJsonObject(str);
                }
                JSONArray array = JSON.parseArray(str);
                if (array == null || array.isEmpty()) {
                    return str;
                }
                List<String> names = new ArrayList<>(array.size());
                for (int i = 0; i < array.size(); i++) {
                    String name = extractNameFromJsonObject(array.get(i).toString());
                    if (StringUtils.isNotBlank(name)) {
                        names.add(name);
                    }
                }
                return String.join(",", names);
            } catch (Exception e) {
                return str;
            }
        }
        return str;
    }

    private String extractNameFromJsonObject(String json) {
        try {
            JSONObject obj = JSON.parseObject(json);
            if (obj == null) {
                return "";
            }
            String name = obj.getString("name");
            if (StringUtils.isBlank(name)) {
                name = obj.getString("label");
            }
            if (StringUtils.isBlank(name)) {
                name = obj.getString("text");
            }
            if (StringUtils.isBlank(name)) {
                name = obj.getString("value");
            }
            return name == null ? "" : name;
        } catch (Exception e) {
            return "";
        }
    }

    // ==================== 字段脱敏 ====================

    /**
     * 汇总 form_code 下所有节点配置中 perm=H 的 fieldId 集合
     * 来源: t_bpmn_node.node_config_json.lowCodeConf.fieldControls
     * (t_bpmn_node_lf_formdata_field_control 表已删除,迁移至 node_config_json,见 doc/tables_to_drop.md)
     */
    private Set<String> collectHiddenFieldIds(Long confId) {
        Set<String> hidden = new HashSet<>();
        List<BpmnNode> nodes = bpmnNodeService.list(
                new LambdaQueryWrapper<BpmnNode>()
                        .eq(BpmnNode::getConfId, confId)
                        .eq(BpmnNode::getIsDel, 0));
        if (CollectionUtils.isEmpty(nodes)) {
            return hidden;
        }
        for (BpmnNode node : nodes) {
            if (StringUtils.isBlank(node.getNodeConfigJson())) {
                continue;
            }
            try {
                BpmnNodeConfigJson configJson = JsonConfUtil.parseNodeConfig(node.getNodeConfigJson());
                if (configJson == null || configJson.getLowCodeConf() == null
                        || CollectionUtils.isEmpty(configJson.getLowCodeConf().getFieldControls())) {
                    continue;
                }
                configJson.getLowCodeConf().getFieldControls().stream()
                        .filter(fc -> StringConstants.HIDDEN_FIELD_PERMISSION.equals(fc.getPerm()))
                        .filter(fc -> StringUtils.isNotBlank(fc.getFieldId()))
                        .forEach(fc -> hidden.add(fc.getFieldId()));
            } catch (Exception e) {
                log.warn("解析节点配置失败,nodeId={},err={}", node.getId(), e.getMessage());
            }
        }
        return hidden;
    }

    // ==================== 权限校验 ====================

    /**
     * 校验当前登录用户是否有权查看流程详情
     * 权限集合:发起人 / 参与人 / 加签人员 / 被委托人 / 流程管理员 / 权限表(用户+角色,查看/监控)
     */
    public boolean checkPermission(String processNumber) {
        if (StringUtils.isBlank(processNumber)) {
            return false;
        }
        BpmBusinessProcess bp = bpmBusinessProcessService.getBpmBusinessProcess(processNumber);
        if (bp == null) {
            return false;
        }
        String loginUserId = SecurityUtils.getLogInEmpIdSafe();

        // 1. 发起人
        if (loginUserId.equals(bp.getCreateUser())) {
            return true;
        }
        // 2. 流程管理员
        try {
            BaseIdTranStruVo admin = bpmnProcessAdminProvider.provideProcessAdminInfo();
            if (admin != null && loginUserId.equals(admin.getId())) {
                return true;
            }
        } catch (Exception e) {
            log.warn("获取流程管理员失败:{}", e.getMessage());
        }
        // 3. 参与人(三表关联)
        BpmVariable bpmVariable = bpmVariableService.getOne(
                AFWrappers.<BpmVariable>lambdaTenantQuery()
                        .eq(BpmVariable::getProcessNum, processNumber)
                        .last("LIMIT 1"));
        if (bpmVariable != null) {
            // 3.1 multiplayer personnel
            List<BpmVariableMultiplayer> multiplayers = bpmVariableMultiplayerMapper.selectList(
                    new LambdaQueryWrapper<BpmVariableMultiplayer>()
                            .eq(BpmVariableMultiplayer::getVariableId, bpmVariable.getId())
                            .eq(BpmVariableMultiplayer::getIsDel, 0));
            if (!CollectionUtils.isEmpty(multiplayers)) {
                List<Long> multiplayerIds = multiplayers.stream().map(BpmVariableMultiplayer::getId).collect(Collectors.toList());
                List<BpmVariableMultiplayerPersonnel> personnelList = bpmVariableMultiplayerPersonnelMapper.selectList(
                        new LambdaQueryWrapper<BpmVariableMultiplayerPersonnel>()
                                .in(BpmVariableMultiplayerPersonnel::getVariableMultiplayerId, multiplayerIds)
                                .eq(BpmVariableMultiplayerPersonnel::getIsDel, 0));
                if (!CollectionUtils.isEmpty(personnelList)) {
                    boolean hit = personnelList.stream()
                            .map(BpmVariableMultiplayerPersonnel::getAssignee)
                            .anyMatch(loginUserId::equals);
                    if (hit) {
                        return true;
                    }
                }
            }
            // 3.2 加签人员(variable_config_json.signUps)
            if (StringUtils.isNotBlank(bpmVariable.getVariableConfigJson())) {
                try {
                    VariableConfigJson config = JsonConfUtil.parseVariableConfig(bpmVariable.getVariableConfigJson());
                    if (config != null && !CollectionUtils.isEmpty(config.getSignUps())) {
                        for (VariableConfigJson.SignUpItem signUp : config.getSignUps()) {
                            if (signUp.getPersonnelByElement() == null) {
                                continue;
                            }
                            for (List<VariableConfigJson.PersonnelItem> personnel : signUp.getPersonnelByElement().values()) {
                                if (CollectionUtils.isEmpty(personnel)) {
                                    continue;
                                }
                                boolean hit = personnel.stream()
                                        .map(VariableConfigJson.PersonnelItem::getAssignee)
                                        .filter(StringUtils::isNotBlank)
                                        .anyMatch(loginUserId::equals);
                                if (hit) {
                                    return true;
                                }
                            }
                        }
                    }
                } catch (Exception e) {
                    log.warn("解析加签配置失败:{}", e.getMessage());
                }
            }
        }
        // 4. 被委托人(bpm_flowrun_entrust, 过滤减签 action_type=3 及 actual 为0/空)
        if (StringUtils.isNotBlank(bp.getProcInstId())) {
            List<BpmFlowrunEntrust> entrusts = bpmFlowrunEntrustService.findEntrustByProcInstId(bp.getProcInstId());
            if (!CollectionUtils.isEmpty(entrusts)) {
                boolean hit = entrusts.stream()
                        .filter(e -> e.getActionType() == null || !Objects.equals(e.getActionType(), 3))
                        .map(BpmFlowrunEntrust::getActual)
                        .filter(StringUtils::isNotBlank)
                        .filter(a -> !"0".equals(a.trim()))
                        .anyMatch(loginUserId::equals);
                if (hit) {
                    return true;
                }
            }
        }
        // 5. 权限表(process_key=form_code, permissions_type in (1,3))
        String formCode = bp.getProcessinessKey();
        if (StringUtils.isNotBlank(formCode)) {
            // 5.1 object_type=1 指定用户
            List<BpmProcessPermissions> userPerms = bpmProcessPermissionsMapper.selectList(
                    new LambdaQueryWrapper<BpmProcessPermissions>()
                            .eq(BpmProcessPermissions::getProcessKey, formCode)
                            .eq(BpmProcessPermissions::getObjectType, 1)
                            .eq(BpmProcessPermissions::getObjectId, loginUserId)
                            .in(BpmProcessPermissions::getPermissionsType, 1, 3)
                            .eq(BpmProcessPermissions::getIsDel, 0));
            if (!CollectionUtils.isEmpty(userPerms)) {
                return true;
            }
            // 5.2 object_type=3 角色 -> 角色下用户
            List<BpmProcessPermissions> rolePerms = bpmProcessPermissionsMapper.selectList(
                    new LambdaQueryWrapper<BpmProcessPermissions>()
                            .eq(BpmProcessPermissions::getProcessKey, formCode)
                            .eq(BpmProcessPermissions::getObjectType, 3)
                            .in(BpmProcessPermissions::getPermissionsType, 1, 3)
                            .eq(BpmProcessPermissions::getIsDel, 0));
            if (!CollectionUtils.isEmpty(rolePerms)) {
                List<String> roleIds = rolePerms.stream()
                        .map(BpmProcessPermissions::getObjectId)
                        .filter(StringUtils::isNotBlank)
                        .distinct()
                        .collect(Collectors.toList());
                if (!roleIds.isEmpty()) {
                    List<BaseIdTranStruVo> roleUsers = afRoleService.queryUserByRoleIds(roleIds);
                    if (!CollectionUtils.isEmpty(roleUsers)) {
                        boolean hit = roleUsers.stream().map(BaseIdTranStruVo::getId).anyMatch(loginUserId::equals);
                        if (hit) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    // ==================== 工具 ====================

    private String fieldKey(String fieldId) {
        return "field_" + fieldId;
    }

    private Long parseLongQuietly(String value) {
        try {
            return Long.parseLong(value);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private String formatProcessState(Integer processState) {
        if (processState == null) {
            return "";
        }
        for (ProcessStateEnum state : ProcessStateEnum.values()) {
            if (Objects.equals(state.getCode(), processState)) {
                return state.getDesc();
            }
        }
        return String.valueOf(processState);
    }
}
