# 流程流转控制

> AntFlow 在 Activiti 之上构建了一套完整的流程流转控制体系,涵盖任务完成、节点跳转、流程迁移、加签减签、退回任意节点等中国式办公场景。本章详解底层控制机制与 Activiti 自定义 cmd 实现。

## 流转控制总览

<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 920 380" font-family="-apple-system, BlinkMacSystemFont, 'Segoe UI', sans-serif">
  <defs>
    <marker id="arr14" viewBox="0 0 10 10" refX="9" refY="5" markerWidth="6" markerHeight="6" orient="auto"><path d="M0,0 L10,5 L0,10 z" fill="#475569"/></marker>
  </defs>

  <!-- 上层:ProcessOperationAdaptor -->
  <rect x="20" y="20" width="880" height="80" rx="8" fill="#dbeafe" stroke="#3b82f6"/>
  <text x="460" y="44" text-anchor="middle" font-size="13" font-weight="700" fill="#1e40af">ProcessOperationAdaptor(30+ 操作实现)</text>
  <text x="460" y="64" text-anchor="middle" font-size="11" fill="#1e3a8a">ResubmitProcessImpl / EndProcessImpl / BackToModifyImpl / TransferAssigneeProcessImpl / AddAssigneeProcessImpl / FastForwardProcessImpl …</text>
  <text x="460" y="84" text-anchor="middle" font-size="10" fill="#1e3a8a">doProcessButton(BusinessDataVo vo)</text>

  <!-- 中层:流转控制服务 -->
  <rect x="20" y="120" width="430" height="100" rx="8" fill="#dcfce7" stroke="#16a34a"/>
  <text x="235" y="144" text-anchor="middle" font-size="13" font-weight="700" fill="#155e2f">流程控制服务</text>

  <rect x="40" y="160" width="180" height="48" rx="4" fill="#fff" stroke="#16a34a"/>
  <text x="130" y="180" text-anchor="middle" font-size="11" font-weight="600" fill="#155e2f">ProcessNodeSubmitBizService</text>
  <text x="130" y="196" text-anchor="middle" font-size="9" fill="#14532d">processComplete(task)</text>

  <rect x="240" y="160" width="190" height="48" rx="4" fill="#fff" stroke="#16a34a"/>
  <text x="335" y="180" text-anchor="middle" font-size="11" font-weight="600" fill="#155e2f">BpmnProcessMigrationService</text>
  <text x="335" y="196" text-anchor="middle" font-size="9" fill="#14532d">migrateAndJumpToCurrent</text>

  <!-- Activiti cmd -->
  <rect x="470" y="120" width="430" height="100" rx="8" fill="#fef3c7" stroke="#d97706"/>
  <text x="685" y="144" text-anchor="middle" font-size="13" font-weight="700" fill="#92400e">Activiti 自定义 Command</text>

  <rect x="490" y="160" width="180" height="48" rx="4" fill="#fff" stroke="#d97706"/>
  <text x="580" y="180" text-anchor="middle" font-size="11" font-weight="600" fill="#92400e">ProcessNodeJumpCmd</text>
  <text x="580" y="196" text-anchor="middle" font-size="9" fill="#78350f">节点跳转(退回任意节点)</text>

  <rect x="690" y="160" width="190" height="48" rx="4" fill="#fff" stroke="#d97706"/>
  <text x="785" y="180" text-anchor="middle" font-size="11" font-weight="600" fill="#92400e">managementService.executeCommand</text>
  <text x="785" y="196" text-anchor="middle" font-size="9" fill="#78350f">执行自定义 cmd</text>

  <!-- 底层:Activiti 引擎 API -->
  <rect x="20" y="240" width="880" height="120" rx="8" fill="#f1f5f9" stroke="#475569"/>
  <text x="460" y="264" text-anchor="middle" font-size="13" font-weight="700" fill="#1e293b">Activiti 5.23 引擎 API</text>

  <rect x="40" y="280" width="160" height="60" rx="6" fill="#fff" stroke="#475569"/>
  <text x="120" y="304" text-anchor="middle" font-size="11" font-weight="700" fill="#1e293b">taskService</text>
  <text x="120" y="322" text-anchor="middle" font-size="9" fill="#475569">complete / setAssignee</text>

  <rect x="220" y="280" width="160" height="60" rx="6" fill="#fff" stroke="#475569"/>
  <text x="300" y="304" text-anchor="middle" font-size="11" font-weight="700" fill="#1e293b">runtimeService</text>
  <text x="300" y="322" text-anchor="middle" font-size="9" fill="#475569">setVariable / suspend</text>

  <rect x="400" y="280" width="160" height="60" rx="6" fill="#fff" stroke="#475569"/>
  <text x="480" y="304" text-anchor="middle" font-size="11" font-weight="700" fill="#1e293b">historyService</text>
  <text x="480" y="322" text-anchor="middle" font-size="9" fill="#475569">createHistoricTaskQuery</text>

  <rect x="580" y="280" width="160" height="60" rx="6" fill="#fff" stroke="#475569"/>
  <text x="660" y="304" text-anchor="middle" font-size="11" font-weight="700" fill="#1e293b">managementService</text>
  <text x="660" y="322" text-anchor="middle" font-size="9" fill="#475569">executeCommand</text>

  <rect x="760" y="280" width="120" height="60" rx="6" fill="#fff" stroke="#475569"/>
  <text x="820" y="304" text-anchor="middle" font-size="11" font-weight="700" fill="#1e293b">repositoryService</text>
  <text x="820" y="322" text-anchor="middle" font-size="9" fill="#475569">流程定义查询</text>

  <!-- 箭头 -->
  <line x1="235" y1="100" x2="235" y2="120" stroke="#475569" stroke-width="2" marker-end="url(#arr14)"/>
  <line x1="685" y1="100" x2="685" y2="120" stroke="#475569" stroke-width="2" marker-end="url(#arr14)"/>
  <line x1="235" y1="220" x2="235" y2="240" stroke="#475569" stroke-width="2" marker-end="url(#arr14)"/>
  <line x1="685" y1="220" x2="685" y2="240" stroke="#475569" stroke-width="2" marker-end="url(#arr14)"/>
</svg>

## 任务完成:processComplete

[ProcessNodeSubmitBizService.processComplete](file:///d:/projects/jimuoffice/antflow-engine/src/main/java/org/openoa/engine/bpmnconf/service/biz/ProcessNodeSubmitBizService.java) 是最常用的流转方法,在同意/重新提交/加批等操作后调用:

```java
public void processComplete(Task task) {
    // 1. 校验任务归属
    Assert.notNull(task, "任务不存在");

    // 2. 完成任务(Activiti 自动流转到下一节点)
    taskService.complete(task.getId());

    // 3. 检查流程是否结束
    ProcessInstance instance = runtimeService.createProcessInstanceQuery()
            .processInstanceId(task.getProcessInstanceId())
            .singleResult();
    if (instance == null) {
        // 流程已结束,更新 bpm_business_process.processState = END_STATE
        bpmBusinessProcessService.updateProcessState(
                getProcessNumber(task.getProcessInstanceId()),
                ProcessStateEnum.END_STATE.getState());
        // 触发流程完成消息
        activitiBpmMsgTemplateService.sendBpmFinishMsg(buildMsgVo(task));
    }
}
```

## 节点跳转:ProcessNodeJumpCmd

退回任意节点操作通过自定义 Activiti Command 实现。`ProcessNodeJumpCmd` 位于 engine 模块,核心逻辑:

```java
public class ProcessNodeJumpCmd implements Command<Void> {

    private String processInstanceId;
    private String currentTaskId;
    private String targetNodeId;
    private Integer backToModifyType;

    @Override
    public Void execute(CommandContext commandContext) {
        // 1. 删除当前任务及其历史
        TaskEntityManager taskEntityManager = commandContext.getTaskEntityManager();
        TaskEntity currentTask = taskEntityManager.findById(currentTaskId);
        taskEntityManager.delete(currentTask);

        // 2. 根据 backToModifyType 决定目标节点
        String actualTargetNodeId = resolveTargetNode(targetNodeId, backToModifyType);

        // 3. 在目标节点创建新任务
        ExecutionEntity execution = commandContext.getExecutionEntityManager()
                .findById(processInstanceId);
        execution.setCurrentActivityId(actualTargetNodeId);

        // 4. 触发任务创建监听器
        commandContext.getAgenda().planContinueProcessOperation(execution);

        return null;
    }
}
```

### 退回五种模式

`BackToModifyImpl` 通过 `backToModifyType` 控制退回行为:

| backToModifyType | 退回目标 | 后续动作 |
|:---:|---|---|
| 1 | 上一节点 | 退回后重新审批 |
| 2 | 发起人(重新流转) | 发起人修改后从头流转 |
| 3 | 发起人(回到当前) | 发起人修改后直接回到当前节点 |
| 4 | 任意节点(下一) | 退回到任意历史节点,从其下一节点开始 |
| 5 | 任意节点(当前) | 退回到任意历史节点,从该节点开始 |

## 流程迁移:BpmnProcessMigrationService

[BpmnProcessMigrationService.migrateAndJumpToCurrent](file:///d:/projects/jimuoffice/antflow-engine/src/main/java/org/openoa/engine/bpmnconf/service/biz/BpmnProcessMigrationService.java) 在动态条件变更时触发:

```java
public void migrateAndJumpToCurrent(BusinessDataVo vo) {
    String processNumber = vo.getProcessNumber();

    // 1. 重新评估所有条件节点
    BpmVariable variable = bpmVariableService.getByProcessNumber(processNumber);
    BpmnStartConditionsVo startConditions = JSON.parseObject(
            variable.getProcessStartConditions(),
            BpmnStartConditionsVo.class);
    // 合并最新表单数据
    mergeFormData(startConditions, vo);

    // 2. 重新计算后续节点链路
    List<BpmnNodeVo> newNodes = conditionService.filterNodesByCondition(
            getBpmnConfVo(variable.getBpmnCode()).getNodes(),
            startConditions);

    // 3. 重新解析审批人
    resolveAssignees(newNodes, startConditions);

    // 4. 删除原后续节点的 ACT_RU_TASK
    List<Task> oldFutureTasks = taskService.createTaskQuery()
            .processInstanceBusinessKey(processNumber)
            .list();
    for (Task task : oldFutureTasks) {
        if (!isCurrentNode(task)) {
            taskService.deleteTask(task.getId(), true);
        }
    }

    // 5. 按新分支创建后续节点的 ACT_RU_TASK
    for (BpmnNodeVo node : newNodes) {
        if (isAfterCurrent(node)) {
            createActivitiTask(node, processNumber);
        }
    }

    // 6. 记录迁移历史
    bpmDynamicConditionChoosenService.record(processNumber, newNodes);
}
```

## 加签减签

加签/减签通过修改 `t_bpmn_node_additional_sign_conf` 表实现,运行期影响 Activiti 任务的候选用户:

### 加签:AddAssigneeProcessImpl

```java
public void doProcessButton(BusinessDataVo vo) {
    // 1. 在 t_bpmn_node_additional_sign_conf 追加配置
    BpmnNodeAdditionalSignConf signConf = BpmnNodeAdditionalSignConf.builder()
            .bpmnNodeId(getCurrentNodeId(vo))
            .signInfos(JSON.toJSONString(vo.getUserInfos()))
            .signProperty(NodePropertyEnum.PERSONNEL.getCode())
            .signType(SignTypeEnum.COUNTERSIGN.getCode())  // 会签
            .build();
    bpmnNodeAdditionalSignConfService.save(signConf);

    // 2. 把新人员追加到当前任务的候选用户
    for (UserInfo user : vo.getUserInfos()) {
        taskService.addCandidateUser(vo.getTaskId(), user.getUserId());
    }

    // 3. 写审批历史
    bpmVerifyInfoService.add(buildVerifyInfo(vo, PROCESS_ADD_SIGN_TYPE));
}
```

### 减签:RemoveAssigneeProcessImpl

```java
public void doProcessButton(BusinessDataVo vo) {
    // 1. 从 t_bpmn_node_additional_sign_conf 移除
    bpmnNodeAdditionalSignConfService.removeSign(
            getCurrentNodeId(vo), vo.getUserInfos());

    // 2. 从当前任务的候选用户移除
    for (UserInfo user : vo.getUserInfos()) {
        taskService.deleteCandidateUser(vo.getTaskId(), user.getUserId());
    }

    // 3. 写审批历史
    bpmVerifyInfoService.add(buildVerifyInfo(vo, PROCESS_REMOVE_SIGN_TYPE));
}
```

### 未来节点加签/减签

未来节点操作通过修改 `t_bpm_variable` 中保存的审批人变量实现:

```java
public void doProcessButton(BusinessDataVo vo) {
    // 直接修改变量 JSON,等流程流转到该节点时生效
    BpmVariable variable = bpmVariableService.getByProcessNumber(vo.getProcessNumber());
    Map<String, Object> config = JSON.parseObject(
            variable.getVariableConfigJson(), Map.class);
    Map<String, List<String>> assigneesByNode = (Map) config.get("assigneesByNode");

    List<String> futureAssignees = assigneesByNode.get(vo.getNodeId());
    if (vo.getOperationType() == 26) {  // ADD_FUTURE_ASSIGNEE
        futureAssignees.addAll(vo.getUserInfos().stream()
                .map(UserInfo::getUserId).collect(Collectors.toList()));
    } else if (vo.getOperationType() == 27) {  // REMOVE_FUTURE_ASSIGNEE
        futureAssignees.removeAll(vo.getUserInfos().stream()
                .map(UserInfo::getUserId).collect(Collectors.toList()));
    }

    variable.setVariableConfigJson(JSON.toJSONString(config));
    bpmVariableService.updateById(variable);
}
```

## 节点删除与插入

### 删除当前节点:RemoveCurrentNodeProcessImpl

```java
public void doProcessButton(BusinessDataVo vo) {
    Task task = getTask(vo.getTaskId());

    // 1. 修改流程定义:把当前节点的 prev → next 直接相连
    bpmnNodeService.bypassNode(task.getTaskDefinitionKey());

    // 2. 完成当前任务,流程自动流转到下一节点
    taskService.complete(task.getId());
}
```

### 当前节点后插入:InsertNodeAfterCurrentImpl

```java
public void doProcessButton(BusinessDataVo vo) {
    // 1. 在 t_bpmn_node 表插入新节点
    BpmnNode newNode = buildNewNode(vo);
    bpmnNodeService.save(newNode);

    // 2. 修改 t_bpmn_node_to:当前节点 → 新节点 → 原下一节点
    bpmnNodeToService.insertBetween(
            currentNodeId, newNode.getNodeId(), originalNextNodeId);

    // 3. 完成当前任务,流程流转到新节点
    taskService.complete(vo.getTaskId());
}
```

## 流程推进:FastForwardProcessImpl

跳过后续节点,直接到达指定节点:

```java
public void doProcessButton(BusinessDataVo vo) {
    String processInstanceId = getProcessInstanceId(vo);

    // 1. 删除中间节点任务
    List<Task> tasks = taskService.createTaskQuery()
            .processInstanceId(processInstanceId)
            .list();
    for (Task task : tasks) {
        if (!task.getTaskDefinitionKey().equals(vo.getTargetNodeId())) {
            taskService.deleteTask(task.getId(), true);
        }
    }

    // 2. 通过 ProcessNodeJumpCmd 跳到目标节点
    managementService.executeCommand(new ProcessNodeJumpCmd(
            processInstanceId, vo.getTaskId(), vo.getTargetNodeId(), 5));
}
```

## 流程撤回与恢复

### 撤回:TaskRecoverProcessImpl

发起人撤回已发起的流程:

```java
public void doProcessButton(BusinessDataVo vo) {
    // 1. 校验当前用户是发起人
    BpmBusinessProcess bp = bpmBusinessProcessService.getByProcessNumber(vo.getProcessNumber());
    Assert.equals(bp.getCreateUser(), SecurityUtils.getLogInEmpId(), "非发起人不可撤回");

    // 2. 校验下一节点未审批(已审批则不可撤回)
    List<BpmVerifyInfo> verifyInfos = bpmVerifyInfoService.getByProcessNumber(vo.getProcessNumber());
    Assert.isTrue(verifyInfos.size() <= 1, "下一节点已审批,不可撤回");

    // 3. 删除 Activiti 流程实例
    runtimeService.deleteProcessInstance(bp.getEntryId(), "发起人撤回");

    // 4. 更新流程状态为"已撤回"
    bpmBusinessProcessService.updateProcessState(
            vo.getProcessNumber(), ProcessStateEnum.CANCEL_STATE.getState());
}
```

### 恢复已结束:RECOVER_TO_HIS

恢复已结束的流程,继续审批:

```java
public void doProcessButton(BusinessDataVo vo) {
    // 1. 校验流程已结束
    BpmBusinessProcess bp = bpmBusinessProcessService.getByProcessNumber(vo.getProcessNumber());
    Assert.isTrue(bp.getProcessState() == ProcessStateEnum.END_STATE.getState(),
            "流程未结束,不可恢复");

    // 2. 重新启动 Activiti 流程实例(从指定节点开始)
    runtimeService.startProcessInstanceById(bp.getProcInstId(),
            vo.getBusinessKey(), vo.getVariables());

    // 3. 更新流程状态为"办理中"
    bpmBusinessProcessService.updateProcessState(
            vo.getProcessNumber(), ProcessStateEnum.HANDLING_STATE.getState());
}
```

## 涉及的数据库表

| 表名 | 用途 |
|---|---|
| `ACT_RU_TASK` | 当前用户任务,完成/删除/修改 assignee |
| `ACT_RU_EXECUTION` | 执行实例,流程迁移时修改 currentActivityId |
| `ACT_RU_VARIABLE` | 流程变量,加签/减签修改变量 |
| `ACT_RU_IDENTITYLINK` | 候选用户/组,加签时追加 |
| `ACT_HI_TASKINST` | 历史任务,退回时清理 |
| `bpm_business_process` | 流程实例,processState 流转 |
| `bpm_verify_info` | 审批历史 |
| `t_bpm_variable` | 流程变量配置,未来节点加签修改 |
| `t_bpmn_node_additional_sign_conf` | 加签配置 |
| `t_bpm_dynamic_condition_choosen` | 动态条件迁移记录 |

## 小结

- AntFlow 通过 Activiti `managementService.executeCommand` 执行自定义 Command 实现节点跳转
- `ProcessNodeJumpCmd` 是退回任意节点的核心,支持 5 种退回模式
- 加签/减签通过修改 `t_bpmn_node_additional_sign_conf` + 操作 `ACT_RU_IDENTITYLINK` 实现
- 未来节点操作直接修改 `t_bpm_variable` 中的审批人变量 JSON
- 流程迁移 `BpmnProcessMigrationService` 在动态条件变更时触发,重新评估条件并创建新任务
- 节点删除/插入通过修改 `t_bpmn_node_to` 连线表实现
- 撤回/恢复通过删除/重启 Activiti 流程实例实现

下一节 [REST API 参考](/dev-guide/rest-api) 整理所有对外 REST 端点。
