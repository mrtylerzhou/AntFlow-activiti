# 我的待办

> AntFlow 任务中心提供 5 种任务视图,覆盖用户参与流程的全部场景:我发起的、待办、已办、抄送给我、退回给我。监控人员还可在「流程监控」查看全量实例。本章详解任务列表的查询、字段、统一预览抽屉与后端实现。

## 任务视图总览

「任务中心」菜单下包含 5 个子页面,均位于 [antflow-vue/src/views/workflow/flowTask/](file:///d:/projects/jimuoffice/antflow-vue/src/views/workflow/flowTask/):

| 子页面 | 路由 | 后端 type | 含义 |
|---|---|:---:|---|
| 我的发起 | `/flowtask/mytask` | 3 | 当前用户发起的所有流程实例 |
| 我的待办 | `/flowtask/pendding` | 5 | 当前用户待审批的任务 |
| 我的已办 | `/flowtask/approved` | 4 | 当前用户已处理过的任务 |
| 抄送到我 | `/flowtask/CopyToMe` | 9 | 当前用户作为抄送对象的流程 |
| 撤销/退回 | `/flowtask/resubmit` | 7 | 被退回到当前用户的任务 |
| 流程监控 | `/workflow/instance` | 6/8 | 全量流程实例(管理员) |

不同 type 走同一个 REST 端点 `POST /bpmnConf/process/listPage/{type}`,后端通过 switch 分发到不同 Mapper 方法。

<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 880 280" font-family="-apple-system, BlinkMacSystemFont, 'Segoe UI', sans-serif">
  <defs>
    <marker id="arr" viewBox="0 0 10 10" refX="9" refY="5" markerWidth="6" markerHeight="6" orient="auto"><path d="M0,0 L10,5 L0,10 z" fill="#475569"/></marker>
  </defs>

  <!-- 前端 5 个页面 -->
  <rect x="20" y="20" width="160" height="48" rx="6" fill="#dbeafe" stroke="#3b82f6"/>
  <text x="100" y="42" text-anchor="middle" font-size="12" font-weight="700" fill="#1e40af">我的发起</text>
  <text x="100" y="58" text-anchor="middle" font-size="10" fill="#1e3a8a">type=3</text>

  <rect x="20" y="78" width="160" height="48" rx="6" fill="#dcfce7" stroke="#16a34a"/>
  <text x="100" y="100" text-anchor="middle" font-size="12" font-weight="700" fill="#155e2f">我的待办</text>
  <text x="100" y="116" text-anchor="middle" font-size="10" fill="#14532d">type=5</text>

  <rect x="20" y="136" width="160" height="48" rx="6" fill="#fef3c7" stroke="#d97706"/>
  <text x="100" y="158" text-anchor="middle" font-size="12" font-weight="700" fill="#92400e">我的已办</text>
  <text x="100" y="174" text-anchor="middle" font-size="10" fill="#78350f">type=4</text>

  <rect x="20" y="194" width="160" height="48" rx="6" fill="#fce7f3" stroke="#db2777"/>
  <text x="100" y="216" text-anchor="middle" font-size="12" font-weight="700" fill="#9d174d">抄送到我</text>
  <text x="100" y="232" text-anchor="middle" font-size="10" fill="#831843">type=9</text>

  <rect x="20" y="252" width="160" height="20" rx="6" fill="#fee2e2" stroke="#dc2626"/>
  <text x="100" y="266" text-anchor="middle" font-size="12" font-weight="700" fill="#991b1b">退回给我 type=7</text>

  <!-- 统一 API -->
  <rect x="240" y="100" width="220" height="80" rx="8" fill="#1e293b"/>
  <text x="350" y="126" text-anchor="middle" font-size="13" font-weight="700" fill="#fff">POST /bpmnConf/process/listPage/{type}</text>
  <text x="350" y="146" text-anchor="middle" font-size="11" fill="#cbd5e1">BpmnConfController.viewPcProcessList</text>
  <text x="350" y="164" text-anchor="middle" font-size="11" fill="#cbd5e1">→ ProcessApprovalServiceImpl.findPcProcessList</text>

  <!-- 5 个 Mapper 方法 -->
  <rect x="520" y="20" width="180" height="36" rx="6" fill="#f1f5f9" stroke="#475569"/>
  <text x="610" y="42" text-anchor="middle" font-size="11" fill="#1e293b">viewPcpNewlyBuildList(3)</text>

  <rect x="520" y="62" width="180" height="36" rx="6" fill="#f1f5f9" stroke="#475569"/>
  <text x="610" y="84" text-anchor="middle" font-size="11" fill="#1e293b">viewPcToDoList(5)</text>

  <rect x="520" y="104" width="180" height="36" rx="6" fill="#f1f5f9" stroke="#475569"/>
  <text x="610" y="126" text-anchor="middle" font-size="11" fill="#1e293b">viewPcAlreadyDoneList(4)</text>

  <rect x="520" y="146" width="180" height="36" rx="6" fill="#f1f5f9" stroke="#475569"/>
  <text x="610" y="168" text-anchor="middle" font-size="11" fill="#1e293b">viewPcForwardList(9)</text>

  <rect x="520" y="188" width="180" height="36" rx="6" fill="#f1f5f9" stroke="#475569"/>
  <text x="610" y="210" text-anchor="middle" font-size="11" fill="#1e293b">backToModifyList(7)</text>

  <rect x="520" y="230" width="180" height="36" rx="6" fill="#fee2e2" stroke="#dc2626"/>
  <text x="610" y="252" text-anchor="middle" font-size="11" fill="#991b1b">allProcessList(6,8 监控)</text>

  <!-- 连线 -->
  <line x1="180" y1="44" x2="240" y2="120" stroke="#475569" stroke-width="1" marker-end="url(#arr)"/>
  <line x1="180" y1="102" x2="240" y2="140" stroke="#475569" stroke-width="1" marker-end="url(#arr)"/>
  <line x1="180" y1="160" x2="240" y2="160" stroke="#475569" stroke-width="1" marker-end="url(#arr)"/>
  <line x1="180" y1="218" x2="240" y2="180" stroke="#475569" stroke-width="1" marker-end="url(#arr)"/>
  <line x1="180" y1="262" x2="240" y2="180" stroke="#475569" stroke-width="1" marker-end="url(#arr)"/>
  <line x1="460" y1="140" x2="520" y2="40" stroke="#475569" stroke-width="1" marker-end="url(#arr)"/>
  <line x1="460" y1="140" x2="520" y2="80" stroke="#475569" stroke-width="1" marker-end="url(#arr)"/>
  <line x1="460" y1="140" x2="520" y2="122" stroke="#475569" stroke-width="1" marker-end="url(#arr)"/>
  <line x1="460" y1="140" x2="520" y2="164" stroke="#475569" stroke-width="1" marker-end="url(#arr)"/>
  <line x1="460" y1="140" x2="520" y2="206" stroke="#475569" stroke-width="1" marker-end="url(#arr)"/>
  <line x1="460" y1="140" x2="520" y2="248" stroke="#475569" stroke-width="1" marker-end="url(#arr)"/>
</svg>

## 我的待办页面

「我的待办」是用户最常用的页面,默认以列表形式展示当前用户待审批任务:

![我的待办列表](/images/6-1.png)

### 列表字段

| 字段 | 来源 | 说明 |
|---|---|---|
| 流程编号 | `bpm_business_process.process_number` | 唯一标识,如 `LEAVE_WMA-2024-001` |
| 流程类型 | 关联 `t_bpmn_conf` | 流程名称,如"请假流程" |
| 版本 | `bpm_business_process.version` | 流程定义版本号 |
| 节点名称 | `ACT_RU_TASK.name` | 当前待办节点名称 |
| 申请人 | `bpm_business_process.create_user` | 发起人姓名 |
| 创建时间 | `bpm_business_process.create_time` | 流程发起时间 |
| 运行时长 | 计算 | 流程从发起到现在的耗时 |
| 状态 | `processState` 枚举 | 2=办理中,3=已结束,6=已驳回 |

### 操作按钮

- **审批**:跳转 `/flowtask/pendding` 老版单条审批页
- **查看**:打开 `previewDrawer` 抽屉,四 Tab 查看(详见下文)

## 审批 V2(卡片+三 Tab)

新版审批页 [pendding/approveV2.vue](file:///d:/projects/jimuoffice/antflow-vue/src/views/workflow/flowTask/pendding/approveV2.vue) 采用 **左列表 + 右侧三 Tab** 布局:

<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 880 320" font-family="-apple-system, BlinkMacSystemFont, 'Segoe UI', sans-serif">
  <!-- 左侧代办卡片列表 -->
  <rect x="20" y="20" width="280" height="280" rx="8" fill="#f8fafc" stroke="#94a3b8"/>
  <text x="160" y="44" text-anchor="middle" font-size="13" font-weight="700" fill="#1e293b">待办卡片列表</text>
  <text x="160" y="60" text-anchor="middle" font-size="10" fill="#64748b">getPenddinglistPage(type=5)</text>

  <!-- 卡片1 选中 -->
  <rect x="40" y="80" width="240" height="60" rx="6" fill="#dbeafe" stroke="#3b82f6" stroke-width="2"/>
  <text x="50" y="100" font-size="11" font-weight="700" fill="#1e40af">LEAVE_WMA-2024-001</text>
  <text x="50" y="116" font-size="10" fill="#1e3a8a">张三 · 请假流程</text>
  <text x="50" y="132" font-size="10" fill="#1e3a8a">当前节点:部门经理审批</text>

  <!-- 卡片2 -->
  <rect x="40" y="150" width="240" height="60" rx="6" fill="#fff" stroke="#cbd5e1"/>
  <text x="50" y="170" font-size="11" font-weight="600" fill="#334155">UCARREFUEl_WMA-2024-012</text>
  <text x="50" y="186" font-size="10" fill="#64748b">李四 · 加油上报</text>
  <text x="50" y="202" font-size="10" fill="#64748b">当前节点:财务审批</text>

  <!-- 卡片3 -->
  <rect x="40" y="220" width="240" height="60" rx="6" fill="#fff" stroke="#cbd5e1"/>
  <text x="50" y="240" font-size="11" font-weight="600" fill="#334155">DSFZH_WMA-2024-008</text>
  <text x="50" y="256" font-size="10" fill="#64748b">王五 · 第三方整合</text>
  <text x="50" y="272" font-size="10" fill="#64748b">当前节点:主管审批</text>

  <!-- 右侧三个 Tab -->
  <rect x="320" y="20" width="540" height="280" rx="8" fill="#fff" stroke="#94a3b8"/>
  <rect x="332" y="32" width="100" height="28" rx="4" fill="#3b82f6"/>
  <text x="382" y="50" text-anchor="middle" font-size="11" font-weight="700" fill="#fff">表单信息</text>
  <rect x="436" y="32" width="100" height="28" rx="4" fill="#f1f5f9" stroke="#cbd5e1"/>
  <text x="486" y="50" text-anchor="middle" font-size="11" fill="#334155">审批记录</text>
  <rect x="540" y="32" width="100" height="28" rx="4" fill="#f1f5f9" stroke="#cbd5e1"/>
  <text x="590" y="50" text-anchor="middle" font-size="11" fill="#334155">流程预览</text>

  <!-- 表单信息内容 -->
  <text x="340" y="80" font-size="11" font-weight="600" fill="#1e293b">业务表单(ApproveForm 组件)</text>
  <rect x="340" y="90" width="500" height="160" rx="4" fill="#f8fafc" stroke="#cbd5e1" stroke-dasharray="3 3"/>
  <text x="350" y="110" font-size="10" fill="#475569">- 表单字段(LF vform 渲染 / DIY 业务组件)</text>
  <text x="350" y="128" font-size="10" fill="#475569">- 字段权限按节点配置 R/E/H 渲染</text>
  <text x="350" y="146" font-size="10" fill="#475569">- 审批意见区(支持快捷回复)</text>
  <text x="350" y="164" font-size="10" fill="#475569">- 附件上传</text>

  <!-- 按钮区 -->
  <rect x="340" y="260" width="500" height="32" rx="4" fill="#fef3c7" stroke="#d97706"/>
  <text x="350" y="280" font-size="11" font-weight="700" fill="#92400e">操作按钮区(由节点 buttonSignConf 决定)</text>
  <circle cx="500" cy="276" r="3" fill="#16a34a"/>
  <circle cx="540" cy="276" r="3" fill="#dc2626"/>
  <circle cx="580" cy="276" r="3" fill="#d97706"/>
  <circle cx="620" cy="276" r="3" fill="#0891b2"/>
  <circle cx="660" cy="276" r="3" fill="#7c3aed"/>
  <text x="700" y="280" font-size="10" fill="#64748b">同意/不同意/退回/转办/加签…</text>
</svg>

卡片点击切换时,通过 `setPreviewDrawerConfig` 设置全局 store,触发右侧三 Tab 联动重新加载。

### 关键代码

[approveV2.vue](file:///d:/projects/jimuoffice/antflow-vue/src/views/workflow/flowTask/pendding/approveV2.vue) 卡片切换:

```javascript
function toggleFlowActive(item) {
  state.approveFormDataConfig = {
    formCode: item.formCode,
    processNumber: item.processNumber,
    taskId: item.taskId,
    isOutSideAccess: item.isOutSideProcess,
    isLowCodeFlow: item.isLowCodeFlow,
  };
  // 触发 store,驱动 previewDrawer、ReviewWarp 联动
  store.setPreviewDrawerConfig(state.approveFormDataConfig);
}
```

## 统一预览抽屉

所有任务列表都通过 `previewDrawer` 抽屉查看详情,组件位于 [components/previewDrawer.vue](file:///d:/projects/jimuoffice/antflow-vue/src/views/workflow/components/previewDrawer.vue):

<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 880 280" font-family="-apple-system, BlinkMacSystemFont, 'Segoe UI', sans-serif">
  <!-- 4 个 Tab -->
  <rect x="20" y="20" width="100" height="32" rx="4" fill="#3b82f6"/>
  <text x="70" y="40" text-anchor="middle" font-size="11" font-weight="700" fill="#fff">表单信息</text>
  <rect x="124" y="20" width="100" height="32" rx="4" fill="#f1f5f9" stroke="#cbd5e1"/>
  <text x="174" y="40" text-anchor="middle" font-size="11" fill="#334155">审批记录</text>
  <rect x="228" y="20" width="100" height="32" rx="4" fill="#f1f5f9" stroke="#cbd5e1"/>
  <text x="278" y="40" text-anchor="middle" font-size="11" fill="#334155">流程预览</text>
  <rect x="332" y="20" width="100" height="32" rx="4" fill="#f1f5f9" stroke="#cbd5e1"/>
  <text x="382" y="40" text-anchor="middle" font-size="11" fill="#334155">流程模板</text>

  <!-- 内容区 -->
  <rect x="20" y="60" width="840" height="200" rx="6" fill="#f8fafc" stroke="#94a3b8"/>

  <!-- 表单信息:previewComponent -->
  <text x="40" y="84" font-size="12" font-weight="700" fill="#1e293b">previewComponent.vue</text>
  <text x="40" y="104" font-size="10" fill="#64748b">展示业务表单(LF vform 或 DIY 业务组件)</text>
  <text x="40" y="120" font-size="10" fill="#64748b">含发起人按钮:转发 / 打印预览</text>

  <!-- 审批记录:FlowStepTable -->
  <text x="320" y="84" font-size="12" font-weight="700" fill="#1e293b">FlowStepTable</text>
  <text x="320" y="104" font-size="10" fill="#64748b">从 bpm_verify_info 拉取审批历史</text>
  <text x="320" y="120" font-size="10" fill="#64748b">每条记录含:操作人/时间/动作/意见/附件</text>
  <text x="320" y="136" font-size="10" fill="#64748b">ProcessStateImg 显示当前状态</text>

  <!-- 流程预览:ReviewWarp -->
  <text x="560" y="84" font-size="12" font-weight="700" fill="#1e293b">ReviewWarp</text>
  <text x="560" y="104" font-size="10" fill="#64748b">调用 taskPagePreviewNode</text>
  <text x="560" y="120" font-size="10" fill="#64748b">从 t_bpm_variable.processStartConditions</text>
  <text x="560" y="136" font-size="10" fill="#64748b">还原表单数据后预览不同分支</text>

  <!-- 流程模板:Process -->
  <text x="730" y="84" font-size="12" font-weight="700" fill="#1e293b">Process(模板)</text>
  <text x="730" y="104" font-size="10" fill="#64748b">仅监控模式加载</text>
  <text x="730" y="120" font-size="10" fill="#64748b">getApiWorkFlowData({id: confId})</text>
  <text x="730" y="136" font-size="10" fill="#64748b">显示流程定义模板结构</text>

  <!-- 底部状态条 -->
  <rect x="40" y="170" width="800" height="32" rx="4" fill="#fef3c7" stroke="#d97706"/>
  <text x="60" y="190" font-size="11" font-weight="600" fill="#92400e">ProcessStateImg · 当前流程状态:办理中(节点:部门经理审批)</text>
</svg>

### 流程状态枚举

`bpm_business_process.processState` 字段值含义:

| Code | 枚举 | 名称 | 说明 |
|:---:|---|---|---|
| 2 | `HANDLING_STATE` | 办理中 | 流程尚未结束 |
| 3 | `END_STATE` | 已结束 | 正常完成 |
| 6 | `REJECT_STATE` | 已驳回 | 任意节点拒绝 |
| 7 | `ABANDON_STATE` | 已作废 | 发起人主动作废 |

## 后端查询实现

### Controller 入口

[BpmnConfController.viewPcProcessList](file:///d:/projects/jimuoffice/antflow-engine/src/main/java/org/openoa/engine/bpmnconf/controller/BpmnConfController.java):

```java
@RequestMapping("/process/listPage/{type}")
public Result<PageInfo<TaskMgmtVO>> viewPcProcessList(
        @PathVariable Integer type,
        @RequestBody DetailRequestDto<TaskMgmtVO> request) {
    return Result.success(processApprovalService.findPcProcessList(
            request.getPageDto(), request.getTaskMgmtVO()));
}
```

### Service 查询逻辑

[ProcessApprovalServiceImpl.findPcProcessList](file:///d:/projects/jimuoffice/antflow-engine/src/main/java/org/openoa/engine/bpmnconf/service/biz/ProcessApprovalServiceImpl.java):

```java
public PageInfo<TaskMgmtVO> findPcProcessList(PageDto pageDto, TaskMgmtVO vo) {
    // 强制设置当前登录人为查询条件
    vo.setApplyUser(SecurityUtils.getLogInEmpIdStr());

    PageInfo<TaskMgmtVO> page;
    switch (vo.getType()) {
        case 3: page = viewPcProcessList(pageDto, vo, ProcessApprovalMapper::viewPcpNewlyBuildList); break;
        case 4: page = viewPcProcessList(pageDto, vo, ProcessApprovalMapper::viewPcAlreadyDoneList); break;
        case 5: page = viewPcProcessList(pageDto, vo, ProcessApprovalMapper::viewPcToDoList); break;
        case 6:
        case 8: page = viewPcProcessList(pageDto, vo, ProcessApprovalMapper::allProcessList); break;
        case 7: page = viewPcProcessList(pageDto, vo, ProcessApprovalMapper::backToModifyList); break;
        case 9: page = viewPcProcessList(pageDto, vo, ProcessApprovalMapper::viewPcForwardList); break;
    }
    // 补全流程类型名、是否 LF、是否外部等附加字段
    return getPcProcessData(page, vo.getType());
}
```

### 后处理:getPcProcessData

查到列表后,还要做以下补全:

1. **批量查 `t_bpmn_conf`**:补全 `isOutSideProcess / isLowCodeFlow / confId`
2. **状态描述**:`ProcessStateEnum.getDescByCode(processState)` 设置 `taskState`
3. **流程类型名**:`bpmnConfMapper.getBpmProcessVoByFormCode(formCode)` 拉取流程类型显示名

### 首页代办统计

首页右上角的"代办数/今日已办/今日发起"统计:

- API:GET `/bpmnConf/todoList`
- Service:`ProcessApprovalServiceImpl.processStatistics()`
- 返回字段:`todoCount / doneTodayCount / doneCreateCount`

## 涉及的数据库表

| 表名 | 用途 |
|---|---|
| `bpm_business_process` | 流程实例主表,所有列表的核心数据来源 |
| `bpm_process_forward` | 抄送/转发记录表,type=9 时 LEFT JOIN |
| `AF_HI_TASKINST` | Activiti 历史任务表,已办(type=4)、退回(type=7)查询来源 |
| `ACT_RU_TASK` | Activiti 运行中任务表,待办(type=5)查询来源 |
| `t_bpmn_conf` | 流程定义表,补全流程类型信息 |
| `bpm_verify_info` | 审批历史表,审批记录 Tab 数据来源 |

## 小结

- 5 种任务视图统一走 `POST /bpmnConf/process/listPage/{type}`,通过 type 切换 Mapper 方法
- 待办 V2 采用左卡片+右三 Tab 布局,通过 store 联动刷新
- 所有任务列表共用 `previewDrawer.vue` 抽屉,含 4 个 Tab(表单/记录/预览/模板)
- 后端通过 `SecurityUtils.getLogInEmpIdStr()` 强制按当前登录人过滤,保证数据隔离
- 流程状态由 `processState` 字段表示:2=办理中、3=已结束、6=已驳回、7=已作废

下一节 [审批操作](/workflow-run/approve) 详解审批按钮体系与各操作的内部实现。
