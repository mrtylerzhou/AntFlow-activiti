# 流程设计器

> 流程设计器是 AntFlow 的核心可视化工具,通过拖拽配置审批节点、条件分支、抄送节点,无需编写 BPMN XML 即可完成工作流定义。

## 入口

从 [流程类型列表](/workflow-design/flow-category) 点击"流程设计"进入,根据流程类型跳转到不同设计器:

| 流程类型 | 设计器路由 | 组件 |
|---|---|---|
| LF(低代码) | `/workflow/lf-design` | [lf.vue](https://github.com/mrtylerzhou/AntFlow/blob/master/antflow-vue/src/views/workflow/flowDesign/lf.vue) |
| DIY(自定义表单) | `/workflow/diy-design` | [diy.vue](https://github.com/mrtylerzhou/AntFlow/blob/master/antflow-vue/src/views/workflow/flowDesign/diy.vue) |

## 四步设计向导

两个设计器结构高度相似,都采用 4 步骤向导:

<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 900 140" font-family="-apple-system, BlinkMacSystemFont, 'Segoe UI', sans-serif">
  <defs>
    <linearGradient id="step1" x1="0" y1="0" x2="0" y2="1">
      <stop offset="0%" stop-color="#dbeafe"/><stop offset="100%" stop-color="#bfdbfe"/>
    </linearGradient>
    <linearGradient id="step2" x1="0" y1="0" x2="0" y2="1">
      <stop offset="0%" stop-color="#dcfce7"/><stop offset="100%" stop-color="#bbf7d0"/>
    </linearGradient>
    <linearGradient id="step3" x1="0" y1="0" x2="0" y2="1">
      <stop offset="0%" stop-color="#fef3c7"/><stop offset="100%" stop-color="#fde68a"/>
    </linearGradient>
    <linearGradient id="step4" x1="0" y1="0" x2="0" y2="1">
      <stop offset="0%" stop-color="#fce7f3"/><stop offset="100%" stop-color="#fbcfe8"/>
    </linearGradient>
  </defs>
  <rect x="20" y="20" width="200" height="100" rx="10" fill="url(#step1)" stroke="#3b82f6" stroke-width="1.5"/>
  <text x="120" y="50" text-anchor="middle" font-size="14" font-weight="700" fill="#1e40af">① 基础设置</text>
  <text x="120" y="75" text-anchor="middle" font-size="11" fill="#1e293b">版本名称、去重规则</text>
  <text x="120" y="95" text-anchor="middle" font-size="11" fill="#1e293b">辅助表单/外部表单开关</text>

  <rect x="240" y="20" width="200" height="100" rx="10" fill="url(#step2)" stroke="#16a34a" stroke-width="1.5"/>
  <text x="340" y="50" text-anchor="middle" font-size="14" font-weight="700" fill="#166534">② 表单设计</text>
  <text x="340" y="75" text-anchor="middle" font-size="11" fill="#1e293b">LF: vform 拖拽设计</text>
  <text x="340" y="95" text-anchor="middle" font-size="11" fill="#1e293b">DIY: 关联前端组件</text>

  <rect x="460" y="20" width="200" height="100" rx="10" fill="url(#step3)" stroke="#d97706" stroke-width="1.5"/>
  <text x="560" y="50" text-anchor="middle" font-size="14" font-weight="700" fill="#92400e">③ 流程设计</text>
  <text x="560" y="75" text-anchor="middle" font-size="11" fill="#1e293b">审批人/条件/抄送节点</text>
  <text x="560" y="95" text-anchor="middle" font-size="11" fill="#1e293b">可视化拖拽配置</text>

  <rect x="680" y="20" width="200" height="100" rx="10" fill="url(#step4)" stroke="#db2777" stroke-width="1.5"/>
  <text x="780" y="50" text-anchor="middle" font-size="14" font-weight="700" fill="#9d174d">④ 高级设置</text>
  <text x="780" y="75" text-anchor="middle" font-size="11" fill="#1e293b">通知模板、操作按钮</text>
  <text x="780" y="95" text-anchor="middle" font-size="11" fill="#1e293b">字段权限配置</text>

  <line x1="220" y1="70" x2="240" y2="70" stroke="#475569" stroke-width="2" marker-end="url(#a)"/>
  <line x1="440" y1="70" x2="460" y2="70" stroke="#475569" stroke-width="2" marker-end="url(#a)"/>
  <line x1="660" y1="70" x2="680" y2="70" stroke="#475569" stroke-width="2" marker-end="url(#a)"/>
  <defs><marker id="a" viewBox="0 0 10 10" refX="9" refY="5" markerWidth="6" markerHeight="6" orient="auto-start-reverse"><path d="M 0 0 L 10 5 L 0 10 z" fill="#475569"/></marker></defs>
</svg>

### 步骤一:基础设置

![基础表单设置](/images/3-2.png)

配置项:
- **版本名称**:本次设计的版本名(校验:非空、无空格、无特殊字符)
- **去重类型**:1=不去重,2=前向去重,3=后向去重(审批人去重逻辑)
- **辅助表单开关**(DIY 专属):勾选后启用"表单设计"步骤,允许 DIY 流程附加一个低代码辅助表单
- **外部表单开关**(LF 专属):勾选后禁用"表单设计"步骤,改为引用独立表单管理中的表单

### 步骤二:表单设计

**LF 流程**:集成 vform 设计器,拖拽控件设计表单(详见 [低代码表单设计](/workflow-design/form-design))

![表单设计](/images/3-3.png)

**DIY 流程**:不在此步骤设计表单,表单由前端代码实现。若启用了"辅助表单",则可在此步骤设计一个低代码辅助表单(用于条件判断等)。

### 步骤三:流程设计

核心步骤,可视化拖拽配置流程节点:

![流程设计](/images/3-4.png)

详见下方 [节点树与交互](#节点树与交互)。

### 步骤四:高级设置

配置节点级的高级选项:
- **操作按钮**:每个审批节点可配置可见的按钮(同意、退回、转办、委托、加签等)
- **通知模板**:为不同节点配置通知消息模板
- **字段权限**:低代码表单的字段级权限控制(R 只读 / E 可编辑 / H 隐藏)

## 节点树与交互

流程设计步骤的主组件是 [nodeWrap.vue](https://github.com/mrtylerzhou/AntFlow/blob/master/antflow-vue/src/components/Workflow/nodeWrap.vue),它通过递归渲染构建节点树。

### 节点树结构

<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 880 460" font-family="-apple-system, BlinkMacSystemFont, 'Segoe UI', sans-serif">
  <!-- 发起人节点 -->
  <rect x="340" y="10" width="160" height="40" rx="6" fill="#e0e7ff" stroke="#6366f1" stroke-width="1.5"/>
  <text x="420" y="35" text-anchor="middle" font-size="12" fill="#1e293b">发起人(nodeType=1)</text>
  <line x1="420" y1="50" x2="420" y2="70" stroke="#475569" stroke-width="2" marker-end="url(#b)"/>

  <!-- 审批人节点 -->
  <rect x="340" y="70" width="160" height="40" rx="6" fill="#fef3c7" stroke="#d97706" stroke-width="1.5"/>
  <text x="420" y="95" text-anchor="middle" font-size="12" fill="#1e293b">审批人(nodeType=4)</text>
  <line x1="420" y1="110" x2="420" y2="130" stroke="#475569" stroke-width="2" marker-end="url(#b)"/>

  <!-- 条件网关 -->
  <rect x="280" y="130" width="280" height="40" rx="6" fill="#dcfce7" stroke="#16a34a" stroke-width="1.5"/>
  <text x="420" y="155" text-anchor="middle" font-size="12" fill="#1e293b">条件网关(nodeType=2)</text>

  <!-- 分支1 -->
  <line x1="340" y1="170" x2="200" y2="200" stroke="#475569" stroke-width="2" marker-end="url(#b)"/>
  <line x1="500" y1="170" x2="640" y2="200" stroke="#475569" stroke-width="2" marker-end="url(#b)"/>

  <rect x="120" y="200" width="160" height="40" rx="6" fill="#fef3c7" stroke="#d97706" stroke-width="1.5"/>
  <text x="200" y="225" text-anchor="middle" font-size="11" fill="#1e293b">条件1: 金额&lt;1000</text>

  <rect x="560" y="200" width="160" height="40" rx="6" fill="#fef3c7" stroke="#d97706" stroke-width="1.5"/>
  <text x="640" y="225" text-anchor="middle" font-size="11" fill="#1e293b">条件2: 金额≥1000</text>

  <line x1="200" y1="240" x2="200" y2="260" stroke="#475569" stroke-width="2" marker-end="url(#b)"/>
  <line x1="640" y1="240" x2="640" y2="260" stroke="#475569" stroke-width="2" marker-end="url(#b)"/>

  <rect x="120" y="260" width="160" height="40" rx="6" fill="#fef3c7" stroke="#d97706" stroke-width="1.5"/>
  <text x="200" y="285" text-anchor="middle" font-size="11" fill="#1e293b">组长审批</text>

  <rect x="560" y="260" width="160" height="40" rx="6" fill="#fef3c7" stroke="#d97706" stroke-width="1.5"/>
  <text x="640" y="285" text-anchor="middle" font-size="11" fill="#1e293b">经理审批</text>

  <!-- 汇合 -->
  <line x1="200" y1="300" x2="420" y2="330" stroke="#475569" stroke-width="2" marker-end="url(#b)"/>
  <line x1="640" y1="300" x2="420" y2="330" stroke="#475569" stroke-width="2" marker-end="url(#b)"/>

  <!-- 抄送节点 -->
  <rect x="340" y="330" width="160" height="40" rx="6" fill="#dbeafe" stroke="#3b82f6" stroke-width="1.5"/>
  <text x="420" y="355" text-anchor="middle" font-size="12" fill="#1e293b">抄送(nodeType=6/8)</text>
  <line x1="420" y1="370" x2="420" y2="390" stroke="#475569" stroke-width="2" marker-end="url(#b)"/>

  <!-- 结束 -->
  <circle cx="420" cy="410" r="20" fill="#fee2e2" stroke="#ef4444" stroke-width="1.5"/>
  <text x="420" y="415" text-anchor="middle" font-size="11" fill="#1e293b">结束</text>

  <defs><marker id="b" viewBox="0 0 10 10" refX="9" refY="5" markerWidth="6" markerHeight="6" orient="auto-start-reverse"><path d="M 0 0 L 10 5 L 0 10 z" fill="#475569"/></marker></defs>
</svg>

### 添加节点

每个节点下方有"+"按钮,点击弹出 [addNode.vue](https://github.com/mrtylerzhou/AntFlow/blob/master/antflow-vue/src/components/Workflow/addNode.vue) 的 popover,提供 12 种节点类型:

| 弹层按钮 | 生成的 nodeType | 说明 |
|---|---|---|
| 审批人 | 4 | 普通审批人节点 |
| 并行审批 | 7 | 并行网关,多分支同时执行 |
| 抄送人 | 6 | 抄送节点 V1 |
| 抄送人V2 | 8 | 抄送节点 V2(支持运行时动态抄送) |
| 条件分支 | 2 | 条件网关,根据条件路由 |
| 动态条件 | 2(isDynamicCondition=true) | 动态条件,运行时由发起人选择路径 |
| 条件并行 | 2(isParallel=true) | 条件并行网关 |
| 自动节点 | 9 | 自动审批节点,根据条件自动通过 |
| 办理节点 | (组合) | 一次性生成两个审批人节点 |
| 自动办理 | (组合) | 自动节点 + 发起人确认审批人节点 |
| 条件审批 | 12 | 带条件的审批节点(条件满足自动通过) |
| 条件抄送 | 13 | 带条件的抄送节点 |
| 克隆器 | (复制) | 深拷贝已存在的 4/8 节点 |

::: tip 节点类型编号
注意 AntFlow 的 nodeType 编号并不连续:1=发起人, 2=网关, 3=条件, 4=审批人, 6=抄送, 7=并行, 8=抄送V2, 9=自动, 12=条件审批, 13=条件抄送。详见 [节点类型详解](/workflow-design/node-types)。
:::

### 节点配置抽屉

点击节点内容区,根据 nodeType 弹出不同的配置抽屉:

| nodeType | 抽屉组件 | 配置内容 |
|---|---|---|
| 1(发起人) | promoterDrawer.vue | 发起人权限、字段权限 |
| 4/12(审批人/条件审批) | approverDrawer.vue | 审批人规则、会签类型、字段权限 |
| 6(抄送V1) | copyerDrawer.vue | 抄送人列表 |
| 8/13(抄送V2/条件抄送) | copyerDrawerV2.vue | 抄送人列表、条件配置 |
| 3(条件) | conditionDrawer.vue | 条件组、AND/OR 关系 |
| 9(自动节点) | autoNodeDrawer.vue | 自动通过条件 |

源码目录:[antflow-vue/src/components/Workflow/drawer/](https://github.com/mrtylerzhou/AntFlow/tree/master/antflow-vue/src/components/Workflow/drawer)

## 数据提交与回显

### 提交格式化(formatcommit_data.js)

[FormatCommitUtils](https://github.com/mrtylerzhou/AntFlow/blob/master/antflow-vue/src/utils/antflow/formatcommit_data.js) 负责将前端树形结构转为后端期望的扁平节点列表:

```
树形 nodeConfig
   ↓ flattenMapTreeToList(树展平,建立 nodeFrom/nodeTo 关系)
   ↓ getEndpointNodeId(处理网关下属子节点的 nodeTo)
   ↓ cleanNodeList(去重并过滤无效 nodeTo)
   ↓ adapterActivitiNodeList(转换为 Activiti 格式)
扁平 nodeList
```

各 nodeType 的处理:
- **2(网关)**:保留 `nodeTo` 为条件分支 ID 数组,删除 `conditionNodes`
- **3(条件)**:把 `conditionList`/`sort`/`isDefault`/`groupRelation` 合并到 `property` 对象
- **4/6/8/12/13**:构建 `approveObj`(包含 `emplIds`/`roleIds`/`signType` 等)
- **9(自动节点)**:把 `conditionList` 塞进 `autoNodeConf`
- **12/13(条件审批/抄送)**:提交前转换显示格式为存储格式,塞进 `autoNodeConf`

### 回显格式化(formatdisplay_data.js)

[FormatDisplayUtils](https://github.com/mrtylerzhou/AntFlow/blob/master/antflow-vue/src/utils/antflow/formatdisplay_data.js) 负责将后端返回的扁平节点列表转为前端树形结构。

**关键**:后端存储时 9/12/13 都被转为 4(审批人),通过节点标签反显:
- `nodeType=4` + 标签 `auto_node` → 反显为 `nodeType=9`
- `nodeType=4/12` + 标签 `condition_approve_node` → 反显为 `nodeType=12`
- `nodeType=4/8/13` + 标签 `condition_copy_node` → 反显为 `nodeType=13`

## 辅助标志位(BpmnConfFlagsEnum)

设计器的部分配置通过二进制位叠加存储于 `t_bpmn_conf.extra_flags` 字段:

| 标志位 | 值 | 含义 |
|---|---|---|
| `HAS_NODE_LABELS` | `0b1` | 包含节点标签 |
| `HAS_STARTUSER_CHOOSE_MODULES` | `0b10` | 发起人自选模块 |
| `HAS_DYNAMIC_CONDITIONS` | `0b100` | 动态条件 |
| `HAS_COPY` | `0b1000` | 包含抄送 |
| `HAS_LAST_NODE_COPY` | `0b10000` | 末节点抄送 |
| `HAS_FORM_RELATED_ASSIGNEES` | `0b100000` | 表单中选取人员 |
| `USE_EXTERNAL_FORM` | `0b1000000` | 外部表单模式(LF) |
| `USE_AUXILIARY_FORM` | `0b10000000` | 辅助表单模式(DIY) |

源码:[BpmnConfFlagsEnum.java](https://github.com/mrtylerzhou/AntFlow/blob/master/antflow-base/src/main/java/org/openoa/base/constant/enums/BpmnConfFlagsEnum.java)

## 发布流程

完成 4 步配置后,点击"发布"按钮:

1. 并发收集 4 步数据
2. `FormatCommitUtils.formatSettings` 格式化流程节点
3. 调用 `setApiWorkFlowData` 提交后端(`POST /bpmnConf/edit`)
4. 跳转 `flow-version` 版本管理页面

后端 `BpmnConfBizServiceImpl.edit()` 处理:
- 校验 bpmnName,生成新的 bpmnCode(格式如 `QJ-00001`)
- 插入新的 `t_bpmn_conf` 记录(`effectiveStatus=0`,需手动激活)
- 遍历节点,`NodeUtil.nodeSpecialProcess` 处理节点类型转换(如 9→4)
- 插入 `t_bpmn_node` 和 `t_bpmn_node_to`
- 构建节点级 JSON 配置(`BpmnNodeConfigHolder`)

详见 [版本管理与启动](/workflow-design/version-management)。

## 下一步

- [节点类型详解](/workflow-design/node-types) — 深入理解每种节点类型
- [审批人规则](/workflow-design/approver-rules) — 15 种审批人来源配置
- [条件规则](/workflow-design/condition-rules) — 条件评估机制
