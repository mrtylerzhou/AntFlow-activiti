# 流程监控高级运维

> 流程监控页面右上角的「更多」下拉菜单提供了 9 项高级运维操作。本页逐一解释每项操作的使用场景、前端交互和后端实现，帮助运维人员精准控制运行中的流程实例。

## 入口

登录后进入 **流程管理 → 流程监控**（`/workflow/instance`），列表每行右侧有一个「查看」按钮和「更多」下拉菜单：

![流程监控页面](/images/light-8-monitor.png)

点击「更多」展开 9 项操作：

| 序号 | 操作 | 图标 | 说明 |
|:---:|---|---|---|
| 1 | 减签 | Remove | 移除当前节点的某个审批人 |
| 2 | 加签 | CirclePlus | 在当前节点增加审批人 |
| 3 | 增加节点 | Plus | 在流程中动态插入一个新节点 |
| 4 | 删除节点 | Minus | 删除当前或未来的节点 |
| 5 | 推进流程 | DArrowRight | 跳过当前节点，直接进入下一节点 |
| 6 | 变更 | Switch | 修改当前或未来节点的处理人 |
| 7 | 撤回 | RefreshLeft | 将流程撤回到发起人 |
| 8 | 作废 | CircleClose | 终止并废除该流程实例 |
| 9 | 转发 | Promotion | 将流程抄送给其他人查看 |

前端源码位置：[`antflow-vue/src/views/workflow/flowTask/instance/index.vue`](file:///d:/projects/jimuoffice/antflow-vue/src/views/workflow/flowTask/instance/index.vue)

---

## 1. 减签（opType=24/27）

### 使用场景

会签（或签）节点中，某个审批人因调岗、离职等原因无法继续审批，管理员可将其**从当前节点移除**，避免流程卡死。

### 操作流程

点击「减签」→ 跳转专用减签页面 → 选择要移除的审批人 → 确认 → 后端执行移除。

- 前端路由：`/workflow/instance/removeSign/processNumber/:processNumber`

### 后端实现

分两类：

| 场景 | 实现类 | operationType |
|---|---|---|
| 移除**当前节点**审批人 | `RemoveAssigneeProcessImpl` | 24 (`REMOVE_ASSIGNEE`) |
| 移除**未来节点**审批人 | `RemoveFutureNodeProcessImpl` | 27 (`REMOVE_FUTURE_ASSIGNEE`) |

核心步骤：
1. 从 `t_bpmn_node_additional_sign_conf` 删除加签配置
2. 从 `ACT_RU_IDENTITYLINK` 删除候选用户关联
3. 重新计算剩余审批人，更新 `t_bpm_variable`

源码路径：`antflow-engine/.../adp/processoperation/RemoveAssigneeProcessImpl.java`

---

## 2. 加签（opType=25/26）

### 使用场景

流程运行中，管理员发现审批人不足（如某人请假、临时需要专家审核），**动态增加审批人**。

### 操作流程

点击「加签」→ 跳转加签页面 → 选择要增加的审批人（支持多选）→ 确认。

- 前端路由：`/workflow/instance/addSign/processNumber/:processNumber`

### 后端实现

| 场景 | 实现类 | operationType |
|---|---|---|
| 当前节点加签 | `AddAssigneeProcessImpl` | 25 (`ADD_ASSIGNEE`) |
| 未来节点加签 | `AddFutureAssigneeProcessImpl` | 26 (`ADD_FUTURE_ASSIGNEE`) |

核心步骤：
1. 在 `t_bpmn_node_additional_sign_conf` 新增加签配置
2. 在 `ACT_RU_IDENTITYLINK` 新增候选用户关联
3. 更新 `t_bpm_variable` 的审批人变量

源码路径：`antflow-engine/.../adp/processoperation/AddAssigneeProcessImpl.java`

---

## 3. 增加节点（opType=36/37）

### 使用场景

流程审批过程中，发现遗漏了某个审批环节（如报销金额超出预期需要 CFO 额外审批），可以**在当前位置之后插入一个新节点**。

### 操作流程

点击「增加节点」→ 跳转节点配置页面 → 指定节点类型、审批人、条件 → 提交插入。

- 前端路由：`/workflow/instance/addNode/processNumber/:processNumber`

### 后端实现

| 场景 | 实现类 | operationType |
|---|---|---|
| 当前节点后插入 | `InsertNodeAfterCurrentImpl` | 36 (`INSERT_NODE_AFTER_CURRENT`) |
| 未来节点后插入 | `InsertNodeAfterFutureImpl` | 37 (`INSERT_NODE_AFTER_FUTURE`) |

核心步骤：
1. 根据当前流程的 BPMN 定义，生成新节点的 XML 片段
2. 通过 Activiti 内部 cmd 动态修改流程实例的 BPMN 模型
3. 在 `t_bpmn_node` 和 `t_bpmn_node_to` 中记录新节点
4. 重新计算审批人分配

源码路径：`antflow-engine/.../adp/processoperation/InsertNodeAfterCurrentImpl.java`

---

## 4. 删除节点（opType=34/35）

### 使用场景

某个审批节点是重复的或不必要的，管理员可以**直接删除该节点**，流程绕过它继续流转。

### 操作流程

点击「删除节点」→ 跳转节点选择页面 → 选择要删除的节点 → 确认删除。

- 前端路由：`/workflow/instance/removeNode/processNumber/:processNumber`

### 后端实现

| 场景 | 实现类 | operationType |
|---|---|---|
| 删除当前节点 | `RemoveCurrentNodeProcessImpl` | 34 (`REMOVE_NODE`) |
| 删除未来节点 | `RemoveFutureNodeProcessImpl` | 35 (`REMOVE_FUTURE_NODE`) |

核心步骤：
1. 标记节点为删除状态
2. 修改节点连线（nodeTo），跳过���删节点
3. 触发流程迁移，将实例转移到修改后的流程定义

源码路径：`antflow-engine/.../adp/processoperation/RemoveCurrentNodeProcessImpl.java`

---

## 5. 推进流程（opType=33）

### 使用场景

当前节点卡住，管理员需要**跳过当前节点**，强制推进流程到下一审批节点或结束。

### 操作流程

点击「推进流程」→ 跳转推进页面 → 选择目标节点 → 输入推进原因 → 确认。

- 前端路由：`/workflow/instance/fastForward/processNumber/:processNumber`

### 后端实现

| 场景 | 实现类 | operationType |
|---|---|---|
| 推进流程 | `FastForwardProcessImpl` | 33 (`PROCESS_MOVE_AHEAD`) |

核心步骤：
1. 完成当前任务（`taskService.complete`）
2. 根据目标节点生成跳转路径
3. 将流程实例推进到指定节点

源码路径：`antflow-engine/.../adp/processoperation/FastForwardProcessImpl.java`

---

## 6. 变更（opType=11/28）

### 使用场景

当前审批人不合适，或未来节点的审批人需要提前修改。管理员可以**直接替换处理人**。

### 操作流程

点击「变更」→ 跳转变更页面 → 选择要变更的节点 → 选择新审批人 → 确认变更。

- 前端路由：`/workflow/instance/changeSign/processNumber/:processNumber`

### 后端实现

| 场景 | 实现类 | operationType |
|---|---|---|
| 变更当前节点处理人 | `ChangeAssigneeProcessImpl` | 11 (`CHANGE_ASSIGNEE`) |
| 变更未来节点处理人 | `ChangeFutureAssigneeProcessImpl` | 28 (`CHANGE_FUTURE_ASSIGNEE`) |

核心步骤（当前节点）：
1. 调用 `taskService.setAssignee(taskId, newAssigneeId)` 替换 assignee
2. 写 `bpm_verify_info` 变更历史

核心步骤（未来节点）：
1. 修改 `t_bpm_variable` 中对应节点的审批人变量

源码路径：`antflow-engine/.../adp/processoperation/ChangeAssigneeProcessImpl.java`

---

## 7. 撤回（opType=29）

### 使用场景

流程发起不久，发起人发现表单填错、或管理员判断流程不合规，需要**撤回到发起人**重新处理。

### 操作流程

点击「撤回」→ 弹出确认框 → 确认后自动执行。无需选择节点，直接撤回到发起人。

### 后端实现

| 场景 | 实现类 | operationType |
|---|---|---|
| 撤回流程 | `TaskRecoverProcessImpl` | 29 (`RECOVER`) |

核心步骤：
1. 读取当前流程任务和审批历史
2. 通过 `ProcessNodeJumpCmd` 跳转到发起节点
3. 将流程状态重置为「可重新提交」状态
4. 写撤回审批历史

源码路径：`antflow-engine/.../adp/processoperation/TaskRecoverProcessImpl.java`

---

## 8. 作废（opType=7）

### 使用场景

流程不再需要、或严重违规，管理员可以**直接终止并作废**该流程实例。作废后不可恢复。

### 操作流程

点击「作废」→ 弹出确认框 → 确认后执行。

### 后端实现

| 场景 | 实现类 | operationType |
|---|---|---|
| 作废流程 | `EndProcessImpl` | 7 (`ABANDON`) |

核心步骤：
1. 将 `bpm_business_process.processState` 更新为 `ABANDON_STATE`
2. 调 `FormOperationAdaptor.cancellationData()` 作废业务数据
3. 删除 Activiti 运行时实例（级联删除 `ACT_RU_TASK`、`ACT_RU_VARIABLE` 等）
4. 写入作废审批历史

源码路径：`antflow-engine/.../adp/processoperation/EndProcessImpl.java`

---

## 9. 转发（opType=15）

### 使用场景

管理员希望非审批人员（如部门秘书、审计）**查看该流程但不需要审批**，可转发给他们。

### 操作流程

点击「转发」→ 弹出选人对话框 → 单选或多选接收人 → 确认发送。

### 后端实现

| 场景 | 实现类 | operationType |
|---|---|---|
| 转发/抄送 | `ForwardProcessImpl` | 15 (`FORWARD`) |

核心步骤：
1. 在 `bpm_process_forward` 表新增转发记录
2. 接收人可在「抄送到我」列表中查看该流程
3. 转发**不影响流程流转**，只添加查看权限

源码路径：`antflow-engine/.../adp/processoperation/ForwardProcessImpl.java`

---

## 操作统一入口

所有 9 项操作最终都走同一个 AOP 入口：

```
POST /bpmnConf/process/buttonsOperation
   ↓
DoButtonOperationAspect (@Around 拦截)
   ↓ FormFactory.dataFormConversion (Map → BusinessDataVo)
   ↓ ButtonOperationServiceImpl.buttonsOperationTransactional
   ↓ AdaptorFactory.getProcessOperation(vo.operationType)
   ↓ ProcessOperationAdaptor.doProcessButton(vo)
   ↓ ProcessorFactory.executePostProcessors(vo)  // 消息通知、三方回调等
```

详细流转机制见 [审批操作](/workflow-run/approve) 章节。

---

## 注意事项

1. **权限控制**：所有「更多」操作仅**流程管理员**可用，普通用户看不到此菜单
2. **数据一致性**：加签/减签/变更操作同时修改 `t_bpmn_node_additional_sign_conf` 和 `t_bpm_variable` 两张表，确保状态同步
3. **不可逆操作**：作废（opType=7）和删除节点（opType=34/35）不可撤销，执行前务必确认
4. **流程预览**：增加/删除节点后，流程预览图会自动更新，反映新的流程结构
5. **并发安全**：多管理员同时对同一流程执行运维操作时，Activiti 的乐观锁机制（`ACT_RU_TASK.REV_`）会防止冲突

## 下一步

- [审批操作详解](/workflow-run/approve) — 理解前端的操作按钮体系
- [流程流转控制](/dev-guide/flow-control) — 深入后端 ProcessNodeJumpCmd 等核心机制
- [委托设置](/ops/monitor-advanced#委托设置) — 配置自动转交规则
