# 审批操作

> AntFlow 提供了中国式办公全场景的审批操作能力:同意、不同意、退回任意节点、转办、加签、减签、变更处理人、加批、承办、转发、指定下一节点审批人等共 30+ 种按钮。本章详解按钮体系、AOP 拦截器、适配器路由与各操作的后端实现。

## 按钮体系总览

审批按钮由节点的 `BpmnNodeConfigJson.buttonSignConf.operationTypes` 字段决定,在设计器「审批人节点 → 高级设置 → 按钮配置」中开启。常量定义在 [utils/antflow/const.js](file:///d:/projects/jimuoffice/antflow-vue/src/utils/antflow/const.js):

| Code | 枚举 | 中文名 | 适用场景 |
|:---:|---|---|---|
| 1 | SUBMIT | 提交 | 发起人提交 |
| 2 | RESUBMIT | 重新提交 | 退回后重新提交 |
| 3 | AGREE | 同意 | 同意当前节点 |
| 4 | DIS_AGREE | 不同意 | 不同意,流程结束 |
| 7 | ABANDON | 作废 | 发起人作废流程 |
| 10 | UNDERTAKE | 承办 | 接手他人代办 |
| 11 | CHANGE_ASSIGNEE | 变更处理人 | 当前节点改人 |
| 12 | STOP | 终止 | 强制终止流程 |
| 15 | FORWARD | 转发/抄送 | 抄送给其他人 |
| 18 | BACK_TO_MODIFY | 退回 | 退回到指定节点 |
| 19 | JP | 加批 | 加签并继续审批 |
| 21 | ZB | 转办 | 当前任务转给他人 |
| 22 | SELF_SELECT | 自选审批人 | 发起人指定 |
| 23 | BACK_TO_ANY_NODE | 退回任意节点 | 退回任意历史节点 |
| 24 | REMOVE_ASSIGNEE | 减签 | 当前节点减人 |
| 25 | ADD_ASSIGNEE | 加签 | 当前节点加人 |
| 26 | ADD_FUTURE_ASSIGNEE | 未来节点加签 | 未到达节点加人 |
| 27 | REMOVE_FUTURE_ASSIGNEE | 未来节点减签 | 未到达节点减人 |
| 28 | CHANGE_FUTURE_ASSIGNEE | 未来节点变更 | 未到达节点改人 |
| 29 | RECOVER | 撤回 | 发起人撤回 |
| 30 | SAVE_DRAFT | 保存草稿 | 临时保存 |
| 31 | RECOVER_TO_HIS | 恢复已结束 | 已结束流程恢复 |
| 32 | REVOKE_AGREE | 撤销同意 | 撤回已审批操作 |
| 33 | PROCESS_MOVE_AHEAD | 流程推进 | 跳过后续节点 |
| 34 | REMOVE_NODE | 删除当前节点 | 删除运行中节点 |
| 35 | REMOVE_FUTURE_NODE | 删除未来节点 | 删除未到达节点 |
| 36 | INSERT_NODE_AFTER_CURRENT | 当前节点后插入 | 当前节点后加节点 |
| 37 | INSERT_NODE_AFTER_FUTURE | 未来节点后插入 | 未到达节点后加节点 |
| 38 | APPOINT_NEXT_NODE_APPROVER | 指定下一节点审批人 | 提前指定下一节点审批人 |

## 按钮渲染流程

<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 920 280" font-family="-apple-system, BlinkMacSystemFont, 'Segoe UI', sans-serif">
  <defs>
    <marker id="arr" viewBox="0 0 10 10" refX="9" refY="5" markerWidth="6" markerHeight="6" orient="auto"><path d="M0,0 L10,5 L0,10 z" fill="#475569"/></marker>
  </defs>

  <!-- 节点配置 -->
  <rect x="20" y="20" width="200" height="60" rx="8" fill="#dbeafe" stroke="#3b82f6"/>
  <text x="120" y="44" text-anchor="middle" font-size="12" font-weight="700" fill="#1e40af">t_bpmn_node.config_json</text>
  <text x="120" y="64" text-anchor="middle" font-size="10" fill="#1e3a8a">buttonSignConf.operationTypes: [3,4,18,21,25]</text>

  <!-- 后端 getButtons -->
  <rect x="260" y="20" width="240" height="60" rx="8" fill="#fef3c7" stroke="#d97706"/>
  <text x="380" y="44" text-anchor="middle" font-size="12" font-weight="700" fill="#92400e">ConfigFlowButtonContans.getButtons()</text>
  <text x="380" y="64" text-anchor="middle" font-size="10" fill="#78350f">读取 operationTypes 生成 pcButtons.audit</text>

  <!-- 节点 Labels 追加 -->
  <rect x="540" y="20" width="200" height="60" rx="8" fill="#fce7f3" stroke="#db2777"/>
  <text x="640" y="44" text-anchor="middle" font-size="12" font-weight="700" fill="#9d174d">NodeExtraInfoDTO.nodeLabelVOS</text>
  <text x="640" y="64" text-anchor="middle" font-size="10" fill="#831843">动态追加按钮(如指定下一节点)</text>

  <!-- 加签节点追加 -->
  <rect x="780" y="20" width="120" height="60" rx="8" fill="#fee2e2" stroke="#dc2626"/>
  <text x="840" y="44" text-anchor="middle" font-size="11" font-weight="700" fill="#991b1b">加签节点</text>
  <text x="840" y="64" text-anchor="middle" font-size="10" fill="#7f1d1d">追加「加批」按钮</text>

  <!-- 前端 approveForm -->
  <rect x="20" y="120" width="880" height="60" rx="8" fill="#dcfce7" stroke="#16a34a"/>
  <text x="460" y="144" text-anchor="middle" font-size="13" font-weight="700" fill="#155e2f">前端 approveForm.vue · response.data.processRecordInfo.pcButtons.audit</text>
  <text x="460" y="164" text-anchor="middle" font-size="11" fill="#14532d">去重 + 按 value 排序 → approvalButtons → 渲染按钮组</text>

  <!-- 用户点击按钮 -->
  <rect x="20" y="200" width="880" height="60" rx="8" fill="#1e293b"/>
  <text x="460" y="224" text-anchor="middle" font-size="13" font-weight="700" fill="#fff">用户点击按钮 → clickApproveSubmit(btnType) → processOperation(param)</text>
  <text x="460" y="244" text-anchor="middle" font-size="11" fill="#cbd5e1">param 含 operationType / approvalComment / verifyAttachments / lfFields / backToModifyType / userInfos</text>

  <!-- 连线 -->
  <line x1="220" y1="50" x2="260" y2="50" stroke="#475569" stroke-width="1.5" marker-end="url(#arr)"/>
  <line x1="500" y1="50" x2="540" y2="50" stroke="#475569" stroke-width="1.5" marker-end="url(#arr)"/>
  <line x1="740" y1="50" x2="780" y2="50" stroke="#475569" stroke-width="1.5" marker-end="url(#arr)"/>
  <line x1="840" y1="80" x2="460" y2="120" stroke="#475569" stroke-width="1.5" marker-end="url(#arr)" stroke-dasharray="4 3"/>
  <line x1="460" y1="180" x2="460" y2="200" stroke="#475569" stroke-width="1.5" marker-end="url(#arr)"/>
</svg>

## 审批页交互

![审批操作](/images/6-2.png)

[approveForm.vue](file:///d:/projects/jimuoffice/antflow-vue/src/views/workflow/flowTask/pendding/components/approveForm.vue) 的按钮分发逻辑:

```javascript
function clickApproveSubmit(btnType) {
  const { processNumber, taskId, formCode } = state.approveFormData;

  switch (btnType) {
    case 21:  // 转办
    case 19:  // 加批
    case 38:  // 指定下一节点审批人
      openTransferDialog(btnType);   // 单选/多选人员,经 TagUserSelect
      break;

    case 3:   // 同意
    case 4:   // 不同意
    case 2:   // 重新提交
      openApproveDialog(btnType);    // 仅备注 + 快捷回复
      break;

    case 18:  // 退回
      openRepulseDialog();           // backToModifyType 1~5 + backToNodeId
      break;

    case 10:  // 承办
      approveUndertakeSubmit();      // 直接调用,无对话框
      break;
  }
}

async function approveProcess(param) {
  await processOperation(param);     // POST /bpmnConf/process/buttonsOperation
  emit('handleRefreshList');          // 通知父组件刷新列表
}
```

### 退回操作五种模式

`repulseDialog.vue` 提供 `backToModifyType` 选择:

| backToModifyType | 退回目标 | 后续动作 |
|:---:|---|---|
| 1 | 上一节点 | 退回后重新审批 |
| 2 | 发起人(重新流转) | 发起人修改后从头流转 |
| 3 | 发起人(回到当前) | 发起人修改后直接回到当前节点 |
| 4 | 任意节点(下一) | 退回到任意历史节点,从其下一节点开始 |
| 5 | 任意节点(当前) | 退回到任意历史节点,从该节点开始 |

## 后端 AOP 拦截链

所有审批操作统一进入 `POST /bpmnConf/process/buttonsOperation`,经 AOP 拦截器解析后路由到对应适配器:

<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 920 380" font-family="-apple-system, BlinkMacSystemFont, 'Segoe UI', sans-serif">
  <defs>
    <marker id="arr2" viewBox="0 0 10 10" refX="9" refY="5" markerWidth="6" markerHeight="6" orient="auto"><path d="M0,0 L10,5 L0,10 z" fill="#475569"/></marker>
  </defs>

  <!-- 1. Controller -->
  <rect x="20" y="20" width="220" height="56" rx="8" fill="#fce7f3" stroke="#db2777"/>
  <text x="130" y="44" text-anchor="middle" font-size="12" font-weight="700" fill="#9d174d">BpmnConfController</text>
  <text x="130" y="62" text-anchor="middle" font-size="10" fill="#831843">buttonsOperation(values, formCode)</text>

  <!-- 2. ProcessApprovalServiceImpl -->
  <rect x="270" y="20" width="240" height="56" rx="8" fill="#fef3c7" stroke="#d97706"/>
  <text x="390" y="44" text-anchor="middle" font-size="12" font-weight="700" fill="#92400e">ProcessApprovalServiceImpl</text>
  <text x="390" y="62" text-anchor="middle" font-size="10" fill="#78350f">buttonsOperation → buttonsPreOperation</text>

  <!-- 3. Javassist 动态代理 -->
  <rect x="540" y="20" width="180" height="56" rx="8" fill="#e0e7ff" stroke="#4f46e5"/>
  <text x="630" y="44" text-anchor="middle" font-size="12" font-weight="700" fill="#3730a3">ButtonPreOperationService</text>
  <text x="630" y="62" text-anchor="middle" font-size="10" fill="#312e81">Javassist 动态代理空实现</text>

  <!-- 4. AOP 切面 -->
  <rect x="750" y="20" width="150" height="56" rx="8" fill="#fee2e2" stroke="#dc2626"/>
  <text x="825" y="44" text-anchor="middle" font-size="12" font-weight="700" fill="#991b1b">DoButtonOperationAspect</text>
  <text x="825" y="62" text-anchor="middle" font-size="10" fill="#7f1d1d">@Around 拦截</text>

  <!-- 5. FormFactory 反序列化 -->
  <rect x="20" y="120" width="220" height="56" rx="8" fill="#dbeafe" stroke="#3b82f6"/>
  <text x="130" y="144" text-anchor="middle" font-size="12" font-weight="700" fill="#1e40af">FormFactory.dataFormConversion</text>
  <text x="130" y="162" text-anchor="middle" font-size="10" fill="#1e3a8a">Map → BusinessDataVo</text>

  <!-- 6. 适配器查找 -->
  <rect x="270" y="120" width="240" height="56" rx="8" fill="#dcfce7" stroke="#16a34a"/>
  <text x="390" y="144" text-anchor="middle" font-size="12" font-weight="700" fill="#155e2f">AdaptorFactory.getProcessOperation</text>
  <text x="390" y="162" text-anchor="middle" font-size="10" fill="#14532d">按 vo.operationType 路由</text>

  <!-- 7. 适配器执行 -->
  <rect x="540" y="120" width="180" height="56" rx="8" fill="#fde68a" stroke="#ca8a04"/>
  <text x="630" y="144" text-anchor="middle" font-size="12" font-weight="700" fill="#713f12">ProcessOperationAdaptor</text>
  <text x="630" y="162" text-anchor="middle" font-size="10" fill="#422006">doProcessButton(vo)</text>

  <!-- 8. 后处理器 -->
  <rect x="750" y="120" width="150" height="56" rx="8" fill="#cffafe" stroke="#0891b2"/>
  <text x="825" y="144" text-anchor="middle" font-size="12" font-weight="700" fill="#155e75">ProcessorFactory</text>
  <text x="825" y="162" text-anchor="middle" font-size="10" fill="#083344">executePostProcessors</text>

  <!-- 适配器路由表 -->
  <rect x="20" y="210" width="880" height="160" rx="8" fill="#f8fafc" stroke="#94a3b8"/>
  <text x="40" y="232" font-size="13" font-weight="700" fill="#1e293b">ProcessOperationAdaptor 实现类路由表(adp/processoperation/)</text>

  <text x="40" y="256" font-size="11" font-weight="600" fill="#16a34a">ResubmitProcessImpl</text>
  <text x="200" y="256" font-size="10" fill="#475569">opType=2/3/19 重新提交/同意/加批</text>

  <text x="40" y="276" font-size="11" font-weight="600" fill="#dc2626">EndProcessImpl</text>
  <text x="200" y="276" font-size="10" fill="#475569">opType=12/4/7 终止/不同意/作废</text>

  <text x="40" y="296" font-size="11" font-weight="600" fill="#d97706">BackToModifyImpl</text>
  <text x="200" y="296" font-size="10" fill="#475569">opType=18/23 退回/退回任意节点</text>

  <text x="40" y="316" font-size="11" font-weight="600" fill="#0891b2">TransferAssigneeProcessImpl</text>
  <text x="200" y="316" font-size="10" fill="#475569">opType=21 转办</text>

  <text x="40" y="336" font-size="11" font-weight="600" fill="#7c3aed">AddAssigneeProcessImpl</text>
  <text x="200" y="336" font-size="10" fill="#475569">opType=25 当前节点加签</text>

  <text x="450" y="256" font-size="11" font-weight="600" fill="#7c3aed">RemoveAssigneeProcessImpl</text>
  <text x="610" y="256" font-size="10" fill="#475569">opType=24 当前节点减签</text>

  <text x="450" y="276" font-size="11" font-weight="600" fill="#7c3aed">ChangeAssigneeProcessImpl</text>
  <text x="610" y="276" font-size="10" fill="#475569">opType=11 变更处理人</text>

  <text x="450" y="296" font-size="11" font-weight="600" fill="#db2777">ForwardProcessImpl</text>
  <text x="610" y="296" font-size="10" fill="#475569">opType=15 转发/抄送</text>

  <text x="450" y="316" font-size="11" font-weight="600" fill="#16a34a">UndertakeProcessImpl</text>
  <text x="610" y="316" font-size="10" fill="#475569">opType=10 承办</text>

  <text x="450" y="336" font-size="11" font-weight="600" fill="#4f46e5">FastForwardProcessImpl</text>
  <text x="610" y="336" font-size="10" fill="#475569">opType=33 流程推进</text>

  <!-- 连线 -->
  <line x1="240" y1="48" x2="270" y2="48" stroke="#475569" stroke-width="1.5" marker-end="url(#arr2)"/>
  <line x1="510" y1="48" x2="540" y2="48" stroke="#475569" stroke-width="1.5" marker-end="url(#arr2)"/>
  <line x1="720" y1="48" x2="750" y2="48" stroke="#475569" stroke-width="1.5" marker-end="url(#arr2)"/>
  <line x1="825" y1="76" x2="130" y2="120" stroke="#475569" stroke-width="1.5" marker-end="url(#arr2)" stroke-dasharray="4 3"/>
  <line x1="240" y1="148" x2="270" y2="148" stroke="#475569" stroke-width="1.5" marker-end="url(#arr2)"/>
  <line x1="510" y1="148" x2="540" y2="148" stroke="#475569" stroke-width="1.5" marker-end="url(#arr2)"/>
  <line x1="720" y1="148" x2="750" y2="148" stroke="#475569" stroke-width="1.5" marker-end="url(#arr2)"/>
</svg>

## 关键操作实现详解

### 同意/重新提交/加批:ResubmitProcessImpl

[ResubmitProcessImpl.java](file:///d:/projects/jimuoffice/antflow-engine/src/main/java/org/openoa/engine/bpmnconf/adp/processoperation/ResubmitProcessImpl.java):

```java
@Override
public void doProcessButton(BusinessDataVo vo) {
    // 1. 校验当前代办归属当前用户
    Task task = taskService.createTaskQuery()
            .taskId(vo.getTaskId())
            .taskAssignee(SecurityUtils.getLogInEmpIdStr())
            .singleResult();
    Assert.notNull(task, "当前任务不存在或不属于当前用户");

    // 2. 动态条件变更时迁移流程实例
    if (isDynamicConditionChanged(vo)) {
        bpmnProcessMigrationService.migrateAndJumpToCurrent(vo);
    }

    // 3. 保存业务表单数据(更新 t_bpm_variable.processStartConditions)
    formAdapter.consentData(vo);

    // 4. 写审批历史 bpm_verify_info
    bpmVerifyInfoService.add(BpmVerifyInfo.builder()
            .processNumber(vo.getProcessNumber())
            .verifyUserId(SecurityUtils.getLogInEmpId())
            .verifyStatus(opType == 3 ? PROCESS_AGRESS_TYPE : PROCESS_SIGN_UP)
            .verifyDesc(vo.getApprovalComment())
            .attachmentsJson(JSON.toJSONString(vo.getVerifyAttachments()))
            .taskName(task.getName())
            .taskId(task.getId())
            .build());

    // 5. 完成 Activiti 任务,触发流程流转
    processNodeSubmitBizService.processComplete(task);
}
```

### 终止/不同意/作废:EndProcessImpl

[EndProcessImpl.java](file:///d:/projects/jimuoffice/antflow-engine/src/main/java/org/openoa/engine/bpmnconf/adp/processoperation/EndProcessImpl.java):

```java
@Override
public void doProcessButton(BusinessDataVo vo) {
    // 1. 更新流程实例状态
    Integer newState = vo.getOperationType() == 7 ? ABANDON_STATE : REJECT_STATE;
    bpmBusinessProcessService.updateProcessState(vo.getProcessNumber(), newState);

    // 2. 写审批历史
    bpmVerifyInfoService.add(...);   // verifyStatus = PROCESS_END_TYPE / PROCESS_REJECT_TYPE

    // 3. 删除 Activiti 运行时实例(级联删除 ACT_RU_TASK / ACT_RU_VARIABLE 等)
    businessContans.deleteProcessInstance(vo.getProcessNumber());

    // 4. 业务数据作废(DIY 模式调 FormAdaptor.cancellationData)
    formAdapter.cancellationData(vo);
}
```

### 退回任意节点:BackToModifyImpl

[BackToModifyImpl.java](file:///d:/projects/jimuoffice/antflow-engine/src/main/java/org/openoa/engine/bpmnconf/adp/processoperation/BackToModifyImpl.java) 通过 Activiti 内部 cmd 实现节点跳转:

```java
@Override
public void doProcessButton(BusinessDataVo vo) {
    // 1. 解析退回目标节点
    String targetNodeId = resolveBackToNode(vo);  // 按 backToModifyType 决定

    // 2. 通过 processNodeJump cmd 跳转(Activiti 内部命令)
    managementService.executeCommand(new ProcessNodeJumpCmd(
            vo.getProcessNumber(),
            vo.getTaskId(),
            targetNodeId,
            vo.getBackToModifyType()));

    // 3. 写审批历史,verifyStatus = PROCESS_BACK_TYPE
    bpmVerifyInfoService.add(...);
}
```

### 转办:TransferAssigneeProcessImpl

[TransferAssigneeProcessImpl.java](file:///d:/projects/jimuoffice/antflow-engine/src/main/java/org/openoa/engine/bpmnconf/adp/processoperation/TransferAssigneeProcessImpl.java):

```java
@Override
public void doProcessButton(BusinessDataVo vo) {
    // 仅修改当前任务 assignee,流程不流转
    taskService.setAssignee(vo.getTaskId(), vo.getUserInfos().get(0).getUserId());

    // 写 bpm_verify_info,verifyStatus = PROCESS_TRANSFER_TYPE
    bpmVerifyInfoService.add(...);
}
```

### 加签/减签/变更处理人

| 操作 | 实现类 | 关键动作 |
|---|---|---|
| 当前节点加签(op=25) | `AddAssigneeProcessImpl` | 在 `t_bpmn_node_additional_sign_conf` 追加配置,将新人员追加到当前任务候选用户 |
| 当前节点减签(op=24) | `RemoveAssigneeProcessImpl` | 从 `t_bpmn_node_additional_sign_conf` 移除,删除对应候选用户 |
| 变更处理人(op=11) | `ChangeAssigneeProcessImpl` | 整体替换当前任务 assignee |
| 未来节点加签(op=26) | `AddFutureAssigneeProcessImpl` | 修改 `t_bpm_variable` 中未来节点的审批人变量 |
| 未来节点减签(op=27) | `RemoveFutureAssigneeProcessImpl` | 同上,反向操作 |
| 未来节点变更(op=28) | `ChangeFutureAssigneeProcessImpl` | 同上,替换 |

## 涉及的数据库表

| 表名 | 用途 |
|---|---|
| `bpm_verify_info` | 审批历史(verifyUserId/verifyUserName/verifyStatus/verifyDesc/taskName/taskId/runInfoId/attachmentsJson) |
| `bpm_business_process` | 流程实例主表,`processState` 流转 |
| `bpm_process_forward` | 转发/抄送记录表 |
| `bpm_process_node_submit` | 节点提交记录表 |
| `t_bpmn_node_additional_sign_conf` | 加签配置表 |
| `t_bpm_variable` | 流程变量,加签/减签/变更操作修改此表 |
| `ACT_RU_TASK` | Activiti 当前用户任务,assignee 字段 |
| `ACT_HI_TASKINST` | Activiti 历史任务表 |
| `ACT_RU_IDENTITYLINK` | Activiti 候选用户/组关系 |

## 审批历史:bpm_verify_info

每次审批操作都会写入 `bpm_verify_info` 一条记录,这是审批记录 Tab 与流程预览定位当前节点的数据源:

| 字段 | 说明 |
|---|---|
| `process_number` | 流程编号 |
| `verify_user_id` | 操作人 ID |
| `verify_user_name` | 操作人姓名 |
| `verify_status` | 操作类型(同意/不同意/退回/转办/加签…) |
| `verify_desc` | 审批意见 |
| `task_name` | 节点名称 |
| `task_id` | Activiti 任务 ID |
| `run_info_id` | 运行信息 ID |
| `attachments_json` | 附件 JSON 数组 |

## 小结

- 审批按钮由节点的 `buttonSignConf.operationTypes` 决定,可在设计器中开启或关闭
- 所有审批操作统一进入 `POST /bpmnConf/process/buttonsOperation`,经 AOP 拦截后路由到对应适配器
- `DoButtonOperationAspect` 是核心解耦点,通过 `FormFactory.dataFormConversion` 把前端 Map 转为 `BusinessDataVo`,屏蔽 LF/DIY 表单差异
- 每种操作有独立的 `ProcessOperationAdaptor` 实现,遵循单一职责原则,易于扩展
- 所有操作都会写 `bpm_verify_info` 审批历史,作为审计与流程预览的数据源
- 高级操作(加签/减签/变更)同时修改 `t_bpmn_node_additional_sign_conf` 和 `t_bpm_variable`,确保流程状态一致

下一节 [流程预览](/workflow-run/flow-preview) 介绍三种预览模式与节点链路生成逻辑。
