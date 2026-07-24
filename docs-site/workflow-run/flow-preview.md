# 流程预览

> AntFlow 提供三种流程预览模式:设计期预览(设计器中)、发起页预览(填写表单后预览)、审批页预览(审批时查看后续节点)。本章详解三种模式的差异、调用链路与节点链路生成算法。

## 三种预览模式

<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 920 320" font-family="-apple-system, BlinkMacSystemFont, 'Segoe UI', sans-serif">
  <defs>
    <marker id="arr3" viewBox="0 0 10 10" refX="9" refY="5" markerWidth="6" markerHeight="6" orient="auto"><path d="M0,0 L10,5 L0,10 z" fill="#475569"/></marker>
  </defs>

  <!-- 三个场景 -->
  <rect x="20" y="20" width="280" height="120" rx="8" fill="#dbeafe" stroke="#3b82f6"/>
  <text x="160" y="44" text-anchor="middle" font-size="14" font-weight="700" fill="#1e40af">① 设计期预览</text>
  <text x="40" y="68" font-size="11" fill="#1e3a8a">入口:流程设计器 → 右侧「预览」按钮</text>
  <text x="40" y="86" font-size="11" fill="#1e3a8a">API:POST /bpmnConf/preview</text>
  <text x="40" y="104" font-size="11" fill="#1e3a8a">输入:bpmnCode + 节点配置 JSON</text>
  <text x="40" y="122" font-size="11" fill="#1e3a8a">特点:无表单数据,展示所有可能分支</text>

  <rect x="320" y="20" width="280" height="120" rx="8" fill="#dcfce7" stroke="#16a34a"/>
  <text x="460" y="44" text-anchor="middle" font-size="14" font-weight="700" fill="#155e2f">② 发起页预览</text>
  <text x="340" y="68" font-size="11" fill="#14532d">入口:startFlow → Tab「流程预览」</text>
  <text x="340" y="86" font-size="11" fill="#14532d">API:POST /bpmnConf/startPagePreviewNode</text>
  <text x="340" y="104" font-size="11" fill="#14532d">输入:bpmnCode + 当前表单数据</text>
  <text x="340" y="122" font-size="11" fill="#14532d">特点:按表单数据评估条件,展示真实分支</text>

  <rect x="620" y="20" width="280" height="120" rx="8" fill="#fef3c7" stroke="#d97706"/>
  <text x="760" y="44" text-anchor="middle" font-size="14" font-weight="700" fill="#92400e">③ 审批页预览</text>
  <text x="640" y="68" font-size="11" fill="#78350f">入口:previewDrawer → Tab「流程预览」</text>
  <text x="640" y="86" font-size="11" fill="#78350f">API:POST /bpmnConf/startPagePreviewNode</text>
  <text x="640" y="104" font-size="11" fill="#78350f">输入:bpmnCode + processNumber</text>
  <text x="640" y="122" font-size="11" fill="#78350f">特点:从 t_bpm_variable 还原表单数据</text>

  <!-- BpmnConfBizServiceImpl -->
  <rect x="180" y="180" width="560" height="56" rx="8" fill="#1e293b"/>
  <text x="460" y="204" text-anchor="middle" font-size="13" font-weight="700" fill="#fff">BpmnConfBizServiceImpl</text>
  <text x="460" y="224" text-anchor="middle" font-size="11" fill="#cbd5e1">previewNode / startPagePreviewNode / taskPagePreviewNode</text>

  <!-- getPreviewNode 核心 -->
  <rect x="180" y="260" width="560" height="56" rx="8" fill="#fef9c3" stroke="#a16207"/>
  <text x="460" y="284" text-anchor="middle" font-size="13" font-weight="700" fill="#713f12">getPreviewNode(params, isStart) · 节点链路生成算法</text>
  <text x="460" y="304" text-anchor="middle" font-size="11" fill="#422006">PreviewNode(bpmnName/formCode/bpmnNodeList/currentNodeId/afterNodeIds/beforeNodeIds)</text>

  <!-- 连线 -->
  <line x1="160" y1="140" x2="380" y2="180" stroke="#475569" stroke-width="1.5" marker-end="url(#arr3)"/>
  <line x1="460" y1="140" x2="460" y2="180" stroke="#475569" stroke-width="1.5" marker-end="url(#arr3)"/>
  <line x1="760" y1="140" x2="540" y2="180" stroke="#475569" stroke-width="1.5" marker-end="url(#arr3)"/>
  <line x1="460" y1="236" x2="460" y2="260" stroke="#475569" stroke-width="1.5" marker-end="url(#arr3)"/>
</svg>

## 预览效果

![流程预览](/images/7-1.png)

预览组件 [ReviewWarp.vue](file:///d:/projects/jimuoffice/antflow-vue/src/components/Workflow/Preview/reviewWarp.vue) 通过 `LineWarp` 递归渲染节点链路,支持缩放:

- `zoomInit`:初始化缩放
- `wheelZoomFunc`:鼠标滚轮缩放
- `resetImage`:重置缩放

### FormatPreviewUtils 格式化

[utils/antflow/formatFlowPreview.js](file:///d:/projects/jimuoffice/antflow-vue/src/utils/antflow/formatFlowPreview.js):

```javascript
export function formatSettings(data) {
  // 1. 把后端返回的 PreviewNode 转为前端树形结构
  const nodeConfig = buildTree(data.bpmnNodeList);

  // 2. 标记当前节点 currentNodeId
  markCurrentNode(nodeConfig, data.currentNodeId);

  // 3. 标记后续节点 afterNodeIds(高亮显示)
  markAfterNodes(nodeConfig, data.afterNodeIds);

  // 4. 标记前置节点 beforeNodeIds(灰色显示)
  markBeforeNodes(nodeConfig, data.beforeNodeIds);

  return nodeConfig;
}
```

## 后端节点链路生成算法

[BpmnConfBizServiceImpl.getPreviewNode](file:///d:/projects/jimuoffice/antflow-engine/src/main/java/org/openoa/engine/bpmnconf/service/biz/BpmnConfBizServiceImpl.java) 是核心算法:

```java
private PreviewNode getPreviewNode(Map<String, Object> params, boolean isStart) {
    PreviewNode node = new PreviewNode();
    node.setBpmnName((String) params.get("bpmnName"));
    node.setFormCode((String) params.get("formCode"));

    // 1. 读取流程定义 + 节点列表
    BpmnConfVo confVo = getBpmnConfVo(bpmnCode);
    List<BpmnNode> bpmnNodeList = confVo.getNodes();

    // 2. 通过 personneladp 解析每个节点的实际审批人
    reTreatNodeAssignee(bpmnNodeList, params);

    // 3. 定位当前节点(仅审批页预览有)
    if (!isStart) {
        List<String> currentNodeIds = bpmVerifyInfoBizService.findCurrentNodeIds(processNumber);
        node.setCurrentNodeId(currentNodeIds.get(0));
    }

    // 4. 递归计算后续节点
    List<String> afterNodeIds = processNodeToRecursively(
            bpmnNodeList,
            node.getCurrentNodeId(),
            Direction.AFTER);
    node.setAfterNodeIds(afterNodeIds);

    // 5. 递归计算前置节点
    List<String> beforeNodeIds = processNodeToRecursively(
            bpmnNodeList,
            node.getCurrentNodeId(),
            Direction.BEFORE);
    node.setBeforeNodeIds(beforeNodeIds);

    // 6. 设置节点列表
    node.setBpmnNodeList(bpmnNodeList);
    return node;
}
```

### 三种预览方法差异

[BpmnConfBizServiceImpl.java](file:///d:/projects/jimuoffice/antflow-engine/src/main/java/org/openoa/engine/bpmnconf/service/biz/BpmnConfBizServiceImpl.java):

| 方法 | 入参 | 触发场景 | 表单数据来源 |
|---|---|---|---|
| `previewNode` | bpmnCode + 节点配置 JSON | 设计器内预览 | 无,展示所有可能分支 |
| `startPagePreviewNode` | bpmnCode + 表单数据 | 发起页 Tab 切换 | 前端传入当前填写的表单 |
| `taskPagePreviewNode` | bpmnCode + processNumber | 审批页 Tab | 从 `t_bpm_variable.processStartConditions` 还原 |

`taskPagePreviewNode` 还支持监控模式(`ignoreReadonly=true`):直接用前端提交的表单数据,不查存储表单,方便管理员预演不同分支。

## loadNodeOperationUser:节点实际操作人

预览时点击节点,可查看该节点的实际操作人,API:POST `/bpmnConf/loadNodeOperationUser`。

[BpmnConfBizServiceImpl.loadNodeOperationUser](file:///d:/projects/jimuoffice/antflow-engine/src/main/java/org/openoa/engine/bpmnconf/service/biz/BpmnConfBizServiceImpl.java):

```java
public List<String> loadNodeOperationUser(Map<String, Object> params) {
    String nodeId = (String) params.get("nodeId");
    String isCurrent = (String) params.get("isCurrent");

    if ("1".equals(isCurrent)) {
        // 当前节点:查 ACT_RU_TASK.assignee
        return taskService.createTaskQuery()
                .processInstanceBusinessKey(processNumber)
                .taskDefinitionKey(nodeId)
                .list()
                .stream()
                .map(Task::getAssignee)
                .collect(Collectors.toList());
    } else {
        // 未来节点:查 t_bpm_variable 中该节点的审批人变量
        BpmVariable var = bpmVariableService.getByProcessNumberAndNodeId(processNumber, nodeId);
        return JSON.parseArray(var.getAssigneesJson(), String.class);
    }
}
```

## 流程模板预览

监控模式的「流程模板」Tab 调用独立 API:

- API:GET `/bpmnConf/detail/{id}`
- Service:`bpmnConfBizService.detail(id)` 返回 `BpmnConfVo`
- 前端经 `FormatDisplayUtils.getToTree(data)` 转树形结构
- 由 `<Process>` 组件渲染完整流程模板

[utils/antflow/formatdisplay_data.js](file:///d:/projects/jimuoffice/antflow-vue/src/utils/antflow/formatdisplay_data.js) 同时处理 nodeType=12(条件审批)和 nodeType=13(条件抄送)的特殊回显逻辑。

## 涉及的数据库表

| 表名 | 用途 |
|---|---|
| `t_bpmn_conf` | 流程定义 |
| `t_bpmn_node` | 节点定义 |
| `t_bpmn_node_to` | 节点连线(BpmnNodeTo) |
| `t_bpm_variable` | `processStartConditions` 还原表单数据 |
| `bpm_verify_info` | `currentNodeId` 来源 |
| `ACT_RU_TASK` | 当前任务 assignee,loadNodeOperationUser 数据源 |

## 与流程设计阶段的衔接

- 预览复用设计期 `BpmnConfVo.nodes`,通过相同的 `personneladp/*PersonnelAdaptor` 解析审批人,**确保预览 = 真实流转结果**
- `FormatPreviewUtils` / `FormatDisplayUtils` 负责前后端节点结构互转
- 条件节点评估复用 `ConditionServiceImpl`,与运行期逻辑一致
- 节点 Labels(存在 task.formKey 的 `NodeExtraInfoDTO`)影响预览展示(如动态条件节点标识)

## 小结

- 三种预览模式覆盖设计、发起、审批全流程,共用 `getPreviewNode` 核心算法
- 预览通过相同的 `personneladp` 适配器和 `ConditionServiceImpl` 解析,确保预览结果 = 真实流转
- `taskPagePreviewNode` 支持监控模式,管理员可基于不同表单数据预演流程分支
- `loadNodeOperationUser` 区分当前节点(查 `ACT_RU_TASK`)与未来节点(查 `t_bpm_variable`)
- 前端 `FormatPreviewUtils` 与 `FormatDisplayUtils` 负责节点结构前后端互转

下一节 [流程消息](/workflow-run/flow-msg) 介绍消息模板管理与多通道通知机制。
