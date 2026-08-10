package org.openoa.engine.bpmnconf.service.biz;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.activiti.engine.TaskService;
import org.activiti.engine.task.Task;
import org.openoa.base.constant.StringConstants;
import org.openoa.base.entity.BpmBusinessProcess;
import org.openoa.base.entity.BpmProcessAudit;
import org.openoa.base.entity.BpmnConf;
import org.openoa.base.entity.BpmnConfLfFormdataField;
import org.openoa.base.interf.BpmBusinessProcessService;
import org.openoa.base.interf.FormOperationAdaptor;
import org.openoa.base.util.SecurityUtils;
import org.openoa.base.util.SpringBeanUtils;
import org.openoa.base.vo.BusinessDataVo;
import org.openoa.engine.bpmnconf.service.interf.biz.ProcessAuditBizService;
import org.openoa.engine.bpmnconf.service.interf.repository.BpmnConfLfFormdataFieldService;
import org.openoa.engine.bpmnconf.service.interf.repository.BpmnConfService;
import org.openoa.engine.factory.FormFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

@Slf4j
@Service
public class ProcessAuditBizServiceImpl implements ProcessAuditBizService {
    @Autowired
    private FormFactory formFactory;
    @Autowired
    private TaskService taskService;
    @Autowired
    private BpmnConfLfFormdataFieldService lfFormdataFieldService;
    @Autowired
    private BpmBusinessProcessService bpmBusinessProcessService;

    /**
     * 保存流程审批过程中表单字段的审计记录.
     *
     * <p>约定: 即使字段未发生变化也记录一条记录(便于前端无差别展示当时的状态).
     * 因此不做 diff 过滤, 对每个业务字段都生成一条 BpmProcessAudit.</p>
     *
     * <ul>
     *   <li>低代码流程(isLowCodeFlow=1): 遍历 lfFields / lfFieldsMulti 所有 key;
     *       通过 lfFormdataFieldService 按 confId / formdataId 查 fieldId -> label 写入 fieldLabel.</li>
     *   <li>DIY 流程: 通过反射遍历 entityClass 自己声明的字段(declared fields),
     *       排除 BusinessDataVo 父类引擎字段. fieldLabel 留空, 前端 fallback 用 fieldName 展示.</li>
     * </ul>
     *
     * <p>调用时机: 必须在 {@code FormOperationAdaptor.consentData(vo)} 之前调用.
     * 方法内部会调用 {@code formAdapter.queryData(vo)} 把 vo 覆盖为数据库旧值以获取旧值快照,
     * 之后会把 vo 的业务字段恢复到前端提交的新值, 不影响后续 {@code consentData(vo)} 的写入.</p>
     */
    @Override
    public void saveChanges(BusinessDataVo vo, Class<?> entityClass) {
        if (vo == null || entityClass == null) {
            return;
        }
        String formCode = vo.getFormCode();
        String processNumber = vo.getProcessNumber();
        String taskId = vo.getTaskId();
        if (processNumber == null || processNumber.isEmpty()) {
            return;
        }
        List<BpmProcessAudit> audits = new ArrayList<>();
        try {
            String createUser = SecurityUtils.getLogInEmpIdStr();
            String createUserName = SecurityUtils.getLogInEmpNameSafe();
            FormOperationAdaptor formAdapter = formFactory.getFormAdaptor(vo);

            if (Objects.equals(vo.getIsLowCodeFlow(), 1)) {
                // 1) 快照前端提交值 (queryData 会把 vo 改写成数据库旧值, 这里先把新值留住)
                Map<String, Object> currentLfSnapshot = vo.getLfFields() == null
                        ? null : new HashMap<>(vo.getLfFields());

                Map<String, Map<String, Object>> currentMultiSnapshot = readLfFieldsMulti(vo);
                Map<String, Map<String, Object>> currentMultiSnapshotCopy = null;
                if (currentMultiSnapshot != null) {
                    currentMultiSnapshotCopy = new HashMap<>();
                    for (Map.Entry<String, Map<String, Object>> e : currentMultiSnapshot.entrySet()) {
                        Map<String, Object> inner = e.getValue();
                        currentMultiSnapshotCopy.put(e.getKey(), inner == null ? null : new HashMap<>(inner));
                    }
                }

                // 2) 准备 fieldId -> label 映射(低代码字段定义里查)
                //    - 内联模式: 按 confId 一次查
                //    - 外部表单模式: 按 formdataId 各查一次
                Map<String, String> inlineLabelMap = loadInlineLabelMap(vo);
                // formdataId -> fieldId -> label
                Map<String, Map<String, String>> externalLabelMap = new HashMap<>();
                Set<String> externalFormdataIds = new LinkedHashSet<>();
                if (currentMultiSnapshotCopy != null) {
                    externalFormdataIds.addAll(currentMultiSnapshotCopy.keySet());
                }
                for (String fdIdStr : externalFormdataIds) {
                    try {
                        Long fdId = Long.parseLong(fdIdStr);
                        Map<String, BpmnConfLfFormdataField> fieldMap = lfFormdataFieldService.qryFieldMapByFormdataId(fdId);
                        if (fieldMap != null && !fieldMap.isEmpty()) {
                            Map<String, String> labels = new HashMap<>();
                            for (Map.Entry<String, BpmnConfLfFormdataField> e : fieldMap.entrySet()) {
                                labels.put(e.getKey(), e.getValue() == null ? null : e.getValue().getFieldName());
                            }
                            externalLabelMap.put(fdIdStr, labels);
                        }
                    } catch (Exception ignore) {
                        // 忽略单个 formdataId 查询失败, 后续降级用 fieldName
                    }
                }

                // 3) queryData 把数据库旧值写到 vo
                formAdapter.queryData(vo);
                Map<String, Object> oldMap = vo.getLfFields();
                Map<String, Map<String, Object>> oldMultiMap = readLfFieldsMulti(vo);

                // 4) 合并所有 key (内联 lfFields + 外部 lfFieldsMulti 的所有 key)
                Set<String> allKeys = new LinkedHashSet<>();
                if (currentLfSnapshot != null) {
                    allKeys.addAll(currentLfSnapshot.keySet());
                }
                if (oldMap != null) {
                    allKeys.addAll(oldMap.keySet());
                }
                for (String key : allKeys) {
                    Object newVal = currentLfSnapshot == null ? null : currentLfSnapshot.get(key);
                    Object oldVal = oldMap == null ? null : oldMap.get(key);
                    String label = inlineLabelMap.get(key);
                    audits.add(buildAudit(formCode, processNumber, key, label, oldVal, newVal, createUser, createUserName));
                }

                // 5) 外部表单模式: 按 formdataId 维度逐个字段记
                Set<String> formdataIds = new LinkedHashSet<>();
                if (currentMultiSnapshotCopy != null) {
                    formdataIds.addAll(currentMultiSnapshotCopy.keySet());
                }
                if (oldMultiMap != null) {
                    formdataIds.addAll(oldMultiMap.keySet());
                }
                for (String fdId : formdataIds) {
                    Map<String, Object> newFields = currentMultiSnapshotCopy == null ? null : currentMultiSnapshotCopy.get(fdId);
                    Map<String, Object> oldFields = oldMultiMap == null ? null : oldMultiMap.get(fdId);
                    Set<String> subKeys = new LinkedHashSet<>();
                    if (newFields != null) {
                        subKeys.addAll(newFields.keySet());
                    }
                    if (oldFields != null) {
                        subKeys.addAll(oldFields.keySet());
                    }
                    Map<String, String> subLabels = externalLabelMap.get(fdId);
                    for (String key : subKeys) {
                        Object newVal = newFields == null ? null : newFields.get(key);
                        Object oldVal = oldFields == null ? null : oldFields.get(key);
                        String label = subLabels == null ? null : subLabels.get(key);
                        audits.add(buildAudit(formCode, processNumber, key, label, oldVal, newVal, createUser, createUserName));
                    }
                }

                // 6) 把 vo.lfFields / vo.lfFieldsMulti 恢复到前端提交的新值, 不影响后续 consentData 写入
                vo.setLfFields(currentLfSnapshot);
                writeLfFieldsMulti(vo, currentMultiSnapshotCopy);
            } else {
                // DIY 流程: 反射遍历 entityClass 自己声明的字段 (排除父类 BusinessDataVo / Object)
                List<String> fieldNames = collectDeclaredFieldNames(entityClass);
                // 1) 快照当前 (前端提交) 业务字段值
                Map<String, Object> currentSnapshot = snapshotFieldValues(vo, fieldNames);
                // 2) queryData 把数据库旧值回写到 vo
                formAdapter.queryData(vo);
                // 3) 取旧值快照
                Map<String, Object> oldSnapshot = snapshotFieldValues(vo, fieldNames);
                for (String key : fieldNames) {
                    Object newVal = currentSnapshot.get(key);
                    Object oldVal = oldSnapshot.get(key);
                    // DIY 流程无 label 概念, fieldLabel 留空, 前端 fallback 用 fieldName
                    audits.add(buildAudit(formCode, processNumber, key, null, oldVal, newVal, createUser, createUserName));
                }
                // 4) 恢复 vo 的业务字段为前端提交值
                restoreFieldValues(vo, currentSnapshot);
            }

            if (!CollectionUtils.isEmpty(audits) && taskId != null && !taskId.isEmpty()) {
                List<Task> tasks = taskService.createTaskQuery().taskId(taskId).list();
                if (!CollectionUtils.isEmpty(tasks)) {
                    Task task = tasks.get(0);
                    for (BpmProcessAudit audit : audits) {
                        audit.setTaskDefKey(task.getTaskDefinitionKey());
                        audit.setTaskName(task.getName());
                    }
                    this.getService().saveBatch(audits);
                } else {
                    String tdk = vo.getTaskDefKey();
                    for (BpmProcessAudit audit : audits) {
                        audit.setTaskDefKey(tdk);
                    }
                    this.getService().saveBatch(audits);
                }
            }
        } catch (Exception e) {
            log.error("save audit info error, processNumber:{}", processNumber, e);
        }
    }

    /**
     * 按 processNumber 查询所有审计记录, 按 taskDefKey + createTime 升序.
     */
    @Override
    public List<BpmProcessAudit> getProcessAudits(String processNumber) {
        if (processNumber == null || processNumber.isEmpty()) {
            return Collections.emptyList();
        }
        return this.getMapper().selectList(
                new QueryWrapper<BpmProcessAudit>()
                        .eq("process_number", processNumber)
                        .orderByAsc("task_def_key", "create_time")
        );
    }

    /**
     * 内联表单模式: 一次性查 confId 下所有字段的 fieldId -> label.
     * 若 confId 拿不到, 返回空 map, 后续 fallback 用 fieldName.
     */
    private Map<String, String> loadInlineLabelMap(BusinessDataVo vo) {
        Map<String, String> result = new HashMap<>();
        try {
            Long confId = vo.getBpmnConfVo() == null ? null : vo.getBpmnConfVo().getId();
            if (confId == null) {
                // 部分流程没有 bpmnConfVo(如刚发起尚未回填), 退化按流程实例定位唯一 bpmn_conf:
                // processNumber -> bpm_business_process.VERSION(bpmn_code) -> bpmn_conf(form_code + bpmn_code)
                String formCode = vo.getFormCode();
                String processNumber = vo.getProcessNumber();
                if (formCode != null && !formCode.isEmpty()
                        && processNumber != null && !processNumber.isEmpty()
                        && !StringConstants.LOWFLOW_FORM_CODE.equals(formCode)) {
                    BpmBusinessProcess bpmBusinessProcess = bpmBusinessProcessService.getBpmBusinessProcess(processNumber);
                    String bpmnCode = bpmBusinessProcess == null ? null : bpmBusinessProcess.getVersion();
                    if (bpmnCode != null && !bpmnCode.isEmpty()) {
                        BpmnConf bpmnConf = SpringBeanUtils
                                .getBean(BpmnConfService.class)
                                .getOne(new QueryWrapper<BpmnConf>()
                                        .eq("form_code", formCode)
                                        .eq("bpmn_code", bpmnCode));
                        if (bpmnConf != null) {
                            confId = bpmnConf.getId();
                        }
                    }
                }
            }
            if (confId == null) {
                return result;
            }
            Map<String, BpmnConfLfFormdataField> fieldMap = lfFormdataFieldService.qryFormDataFieldMap(confId);
            if (fieldMap != null) {
                for (Map.Entry<String, BpmnConfLfFormdataField> e : fieldMap.entrySet()) {
                    BpmnConfLfFormdataField f = e.getValue();
                    result.put(e.getKey(), f == null ? null : f.getFieldName());
                }
            }
        } catch (Exception e) {
            log.warn("loadInlineLabelMap failed, processNumber:{}", vo == null ? "" : vo.getProcessNumber(), e);
        }
        return result;
    }

    /**
     * 反射读取 vo.lfFieldsMulti (UDLFApplyVo 字段). 不存在则返回 null.
     */
    @SuppressWarnings("unchecked")
    private static Map<String, Map<String, Object>> readLfFieldsMulti(BusinessDataVo vo) {
        if (vo == null) {
            return null;
        }
        try {
            Method m = vo.getClass().getMethod("getLfFieldsMulti");
            Object val = m.invoke(vo);
            return (Map<String, Map<String, Object>>) val;
        } catch (NoSuchMethodException ignore) {
            return null;
        } catch (Exception e) {
            log.warn("readLfFieldsMulti failed", e);
            return null;
        }
    }

    /**
     * 反射写入 vo.lfFieldsMulti (UDLFApplyVo 字段). 不存在则忽略.
     */
    private static void writeLfFieldsMulti(BusinessDataVo vo, Map<String, Map<String, Object>> val) {
        if (vo == null) {
            return;
        }
        try {
            Method m = vo.getClass().getMethod("setLfFieldsMulti", Map.class);
            m.invoke(vo, val);
        } catch (NoSuchMethodException ignore) {
        } catch (Exception e) {
            log.warn("writeLfFieldsMulti failed", e);
        }
    }

    /**
     * 取 entityClass 自己声明的字段名集合 (排除父类 BusinessDataVo / Object).
     */
    private static List<String> collectDeclaredFieldNames(Class<?> entityClass) {
        List<String> names = new ArrayList<>();
        Class<?> c = entityClass;
        while (c != null && c != Object.class && !c.getName().equals("org.openoa.base.vo.BusinessDataVo")) {
            for (Field f : c.getDeclaredFields()) {
                if (java.lang.reflect.Modifier.isStatic(f.getModifiers())) {
                    continue;
                }
                if (java.lang.reflect.Modifier.isTransient(f.getModifiers())) {
                    continue;
                }
                names.add(f.getName());
            }
            c = c.getSuperclass();
        }
        return names;
    }

    /**
     * 反射取值: 优先 getter, 否则直接读字段.
     */
    private static Map<String, Object> snapshotFieldValues(Object obj, List<String> fieldNames) {
        Map<String, Object> snapshot = new HashMap<>();
        if (obj == null) {
            return snapshot;
        }
        Map<String, Method> getterMap = new HashMap<>();
        try {
            for (PropertyDescriptor pd : Introspector.getBeanInfo(obj.getClass()).getPropertyDescriptors()) {
                if (pd.getReadMethod() != null) {
                    getterMap.put(pd.getName(), pd.getReadMethod());
                }
            }
        } catch (IntrospectionException ignore) {
        }
        for (String name : fieldNames) {
            Object val = null;
            Method getter = getterMap.get(name);
            if (getter != null) {
                try {
                    val = getter.invoke(obj);
                } catch (Exception ignore) {
                }
            }
            snapshot.put(name, val);
        }
        return snapshot;
    }

    /**
     * 反射写值: 优先 setter, 否则直接写字段.
     */
    private static void restoreFieldValues(Object obj, Map<String, Object> values) {
        if (obj == null || values == null) {
            return;
        }
        Map<String, Method> setterMap = new HashMap<>();
        try {
            for (PropertyDescriptor pd : Introspector.getBeanInfo(obj.getClass()).getPropertyDescriptors()) {
                if (pd.getWriteMethod() != null) {
                    setterMap.put(pd.getName(), pd.getWriteMethod());
                }
            }
        } catch (IntrospectionException ignore) {
        }
        for (Map.Entry<String, Object> e : values.entrySet()) {
            try {
                Method setter = setterMap.get(e.getKey());
                if (setter != null) {
                    setter.invoke(obj, e.getValue());
                }
            } catch (Exception ignore) {
            }
        }
    }

    private static BpmProcessAudit buildAudit(String formCode, String processNumber,
                                              String fieldName, String fieldLabel,
                                              Object oldVal, Object newVal,
                                              String createUser, String createUserName) {
        BpmProcessAudit audit = new BpmProcessAudit();
        audit.setFormCode(formCode);
        audit.setProcessNumber(processNumber);
        audit.setFieldName(fieldName);
        audit.setFieldLabel(fieldLabel);
        audit.setOldValue(valueToString(oldVal));
        audit.setNewValue(valueToString(newVal));
        audit.setCreateUser(createUser);
        audit.setCreateUserName(createUserName);
        return audit;
    }

    /**
     * 值转字符串存储.
     * 字符串/数字/布尔等基本类型直接 toString(避免出现引号包裹);
     * 对象/集合/数组才 JSON 序列化, 保证结构信息不丢.
     */
    private static String valueToString(Object val) {
        if (val == null) {
            return null;
        }
        if (val instanceof String
                || val instanceof Number
                || val instanceof Boolean
                || val instanceof Character
                || val instanceof java.util.Date
                || val instanceof java.time.temporal.Temporal) {
            return String.valueOf(val);
        }
        try {
            return com.alibaba.fastjson2.JSON.toJSONString(val);
        } catch (Exception e) {
            return String.valueOf(val);
        }
    }
}