# 系统总览

> 登录 AntFlow 后,你将看到一个典型的后台管理系统布局。本页带你快速了解每个菜单模块的作用,以及它们如何串联成完整的工作流闭环。

## 整体布局

AntFlow 前端采用 **Vue 3 + Element Plus** 实现的经典后台管理布局,从上到下、从左到右依次为:

| 区域 | 位置 | 说明 |
|---|---|---|
| 顶部导航栏 | 顶部 | 系统标题、面包屑、用户头像、主题切换 |
| 左侧菜单栏 | 左侧 | 根据登录用户角色动态渲染,支持折叠 |
| 主内容区 | 中右 | 路由出口,承载各功能页面 |
| 标签页栏 | 主内容区上方 | 多标签页切换,支持关闭/关闭其他/刷新 |

登录后默认进入"流程中心"首页,展示当前用户的待办、已办、抄送统计卡片。

![首页布局](/images/1-1.png)

首页主要元素:
- **统计卡片**:待办任务数、已办任务数、抄送到我数、我的发起数
- **快捷入口**:发起流程、查看待办、流程监控等
- **最近流程**:展示最近处理的流程实例

## 菜单结构

AntFlow 的左侧菜单通过 `GET /mock/menu.json` 接口动态加载,根据登录用户的角色权限渲染。完整菜单结构如下:

```
AntFlow 后台
├── 流程中心          # 首页仪表盘(/taskCenter)
├── 任务中心          # 个人任务处理
│   ├── 我的发起      # 我发起的流程实例
│   ├── 撤销/退回     # 被撤销或退回的流程
│   ├── 待办任务      # 等待我审批的任务
│   ├── 已办任务      # 我已经处理过的任务
│   └── 抄送到我      # 抄送给我的流程
├── 流程管理          # 流程配置与监控
│   ├── 流程监控      # 全部流程实例运维视图
│   ├── 流程类型      # 流程分类与表单配置
│   └── 流程设计      # 流程版本与设计入口
├── 流程运维          # 运维配置
│   ├── 委托设置      # 配置我的委托规则
│   ├── 委托列表      # 查看生效中的委托
│   ├── 消息模板      # 通知消息模板配置
│   └── 表单管理      # 低代码表单库管理
└── Saas流程          # 三方系统接入
    ├── Saas租户      # 接入方租户管理
    ├── 应用管理      # 接入方应用配置(AppId/AppKey)
    └── 流程设计      # 接入方流程模板配置
```

::: tip 菜单可见性
菜单数据来自后端接口,生产环境通过角色权限控制可见性。演示环境 `mock/menu.json` 返回全部菜单,所有登录用户都能看到。
:::

## 五大模块详解

### 1. 流程中心(首页)

路径:`/taskCenter`,组件:`workflow/taskCenter/index`

登录后的默认落地页,是一个聚合型仪表盘:

- **数据卡片**:实时统计当前用户的待办、已办、抄送、发起数量
- **快捷操作**:一键跳转发起流程、查看待办、流程监控
- **图表展示**:基于 ECharts 5.6.0 渲染的流程趋势图

源码位置:[antflow-vue/src/views/workflow/taskCenter/index.vue](https://github.com/mrtylerzhou/AntFlow/blob/master/antflow-vue/src/views/workflow/taskCenter/index.vue)

### 2. 任务中心

路径前缀:`/flowtask`,这是**普通用户最常使用**的模块,承载日常的流程发起与审批。

#### 我的发起(`/flowtask/mytask`)

展示当前登录用户作为发起人提交的所有流程实例,支持:
- 按流程类型、状态、时间筛选
- 查看流程详情、流程预览图
- **撤销**:流程还在流转中时,发起人可撤销
- **催办**:对未处理的审批人发送催办通知

#### 撤销/退回(`/flowtask/resubmit`)

展示被发起人撤销、或被审批人退回的流程,支持:
- 查看撤销/退回原因
- **重新提交**:修改表单后再次提交,沿用原流程定义

#### 待办任务(`/flowtask/pendding`)

展示当前登录用户作为审批人**需要处理**的任务:

![待办任务](/images/6-1.png)

支持的操作:
- **同意 / 退回 / 转办 / 委托 / 加签 / 减签**
- **查看流程图**:可视化展示当前流程进度
- **查看表单**:展示发起人填写的业务表单
- **查看审批历史**:展示所有已审批节点的处理人、时间、意见

源码位置:[antflow-vue/src/views/workflow/flowTask/pendding/approveV2](https://github.com/mrtylerzhou/AntFlow/blob/master/antflow-vue/src/views/workflow/flowTask/pendding/approveV2)

#### 已办任务(`/flowtask/approved`)

展示当前登录用户**已经处理过**的任务,支持查看处理结果和流程当前状态。

#### 抄送到我(`/flowtask/CopyToMe`)

展示抄送给当前登录用户的流程实例,抄送人**只需查看无需审批**,支持标记已读。

### 3. 流程管理

路径前缀:`/workflow`,这是**流程设计人员**和**管理员**使用的模块。

#### 流程监控(`/workflow/instance`)

管理员视角的全部流程实例列表,支持:
- 查看所有流程实例的当前状态、当前节点、处理人
- **强制终止**:异常流程管理员可强制结束
- **变更处理人**:运行时修改当前或未来节点的处理人
- **加节点 / 删节点**:运行时动态调整流程结构
- **流程推进**:跳过当前节点,直接推进到指定节点
- **加签 / 减签**:动态调整会签节点的审批人

源码位置:[antflow-vue/src/views/workflow/flowTask/instance/index](https://github.com/mrtylerzhou/AntFlow/blob/master/antflow-vue/src/views/workflow/flowTask/instance/index)

#### 流程类型(`/workflow/flowCategory`)

流程分类管理,每个流程必须归属一个分类:

![流程类型列表](/images/2-1.png)

新增分类时需配置:
- **流程名称**:如"请假流程"、"报销流程"
- **类型标识**:唯一值,如 `LEAVE_WMA`,后端代码通过此标识关联业务表单
- **表单类型**:`DIY`(自定义表单)或 `LF`(低代码表单)
- **关联表单**:低代码流程关联表单设计器中的表单;DIY 流程关联前端 `bizFormMaps` 注册的组件
- **图标、排序、描述**:展示用元数据

源码位置:[antflow-vue/src/views/workflow/flowCategory/index](https://github.com/mrtylerzhou/AntFlow/blob/master/antflow-vue/src/views/workflow/flowCategory/index)

#### 流程设计(`/workflow/flowList`)

流程版本管理入口,展示每个流程类型下的所有版本:

![流程设计列表](/images/3-1.png)

每个版本支持:
- **设计**:进入流程设计器(DIY 走 `diy-design`,低代码走 `lf-design`)
- **版本管理**:查看历史版本、切换激活版本
- **启动 / 停用**:控制流程是否可被发起
- **预览**:查看流程图和节点配置
- **调试**:模拟发起,验证流程逻辑

详见 [流程设计器](/workflow-design/flow-designer)。

### 4. 流程运维

路径前缀:`/flowDevOps`,这是**系统运维人员**使用的模块。

#### 委托设置(`/flowDevops/setting`)

用户可配置**自动委托规则**:
- 当我请假/出差时,将待办任务自动转交给指定人
- 支持按流程类型、时间区间配置
- 委托生效期间,新产生的待办会自动流转给受托人

#### 委托列表(`/flowDevops/list`)

查看当前生效中和历史委托记录,支持撤销。

#### 消息模板(`/flowDevops/flowMsgTemp`)

配置各类通知消息的模板:
- 待办提醒、审批通过、审批退回、抄送通知、催办通知
- 支持 `${变量名}` 占位符(如 `${userName}`、`${processName}`)
- 支持多渠道:站内信、邮件、企业微信、钉钉(需后端实现通知渠道)

源码位置:[antflow-vue/src/views/workflow/flowMsg/msgTemplete/index](https://github.com/mrtylerzhou/AntFlow/blob/master/antflow-vue/src/views/workflow/flowMsg/msgTemplete/index)

#### 表单管理(`/flowDevops/lfForm`)

低代码表单库,管理所有通过 vform 设计器创建的表单:
- 新建表单、编辑表单、复制表单
- 表单设计器入口(进入 `lfForm-design` 路由)
- 表单被流程类型引用后不可删除

详见 [低代码表单引擎](/lowcode/lowcode-form)。

### 5. Saas流程

路径前缀:`/outsideMgt`,这是**面向三方接入系统**的模块,详见 [三方系统接入](/dev-guide/integrate-existing)。

#### Saas租户(`/outsideMgt/outsideJoin`)

管理接入 AntFlow 的三方租户,每个租户拥有独立的:
- 流程模板空间
- 审批人模板
- 条件模板
- 用户体系(通过 AppId/AppKey 鉴权)

#### 应用管理(`/outsideMgt/outsideApp`)

每个租户下可创建多个应用,每个应用对应一组 AppId/AppKey,用于:
- 调用 AntFlow Open API 时的身份标识
- 隔离不同业务线的流程数据

#### 流程设计(`/outsideMgt/outsideTemp`)

为三方租户配置流程模板,功能与内部流程设计器类似,但:
- 审批人来源通过 Open API 由接入方动态提供
- 条件字段通过 Open API 由接入方动态提供
- 表单数据由接入方自行管理,AntFlow 只负责流转

## 工作流闭环

下图展示一个典型流程从设计到运行的全生命周期:

<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 920 540" font-family="-apple-system, BlinkMacSystemFont, 'Segoe UI', sans-serif">
  <defs>
    <marker id="arrow" viewBox="0 0 10 10" refX="9" refY="5" markerWidth="6" markerHeight="6" orient="auto-start-reverse">
      <path d="M 0 0 L 10 5 L 0 10 z" fill="#6366f1"/>
    </marker>
    <linearGradient id="g1" x1="0" y1="0" x2="0" y2="1">
      <stop offset="0%" stop-color="#eef2ff"/>
      <stop offset="100%" stop-color="#e0e7ff"/>
    </linearGradient>
    <linearGradient id="g2" x1="0" y1="0" x2="0" y2="1">
      <stop offset="0%" stop-color="#fef3c7"/>
      <stop offset="100%" stop-color="#fde68a"/>
    </linearGradient>
    <linearGradient id="g3" x1="0" y1="0" x2="0" y2="1">
      <stop offset="0%" stop-color="#dcfce7"/>
      <stop offset="100%" stop-color="#bbf7d0"/>
    </linearGradient>
    <linearGradient id="g4" x1="0" y1="0" x2="0" y2="1">
      <stop offset="0%" stop-color="#fce7f3"/>
      <stop offset="100%" stop-color="#fbcfe8"/>
    </linearGradient>
  </defs>

  <!-- 标题 -->
  <text x="460" y="30" text-anchor="middle" font-size="18" font-weight="700" fill="#1e293b">AntFlow 工作流全生命周期</text>

  <!-- 阶段分组背景 -->
  <rect x="20" y="60" width="200" height="420" rx="12" fill="url(#g1)" stroke="#c7d2fe" stroke-width="1.5"/>
  <rect x="240" y="60" width="200" height="420" rx="12" fill="url(#g2)" stroke="#fde68a" stroke-width="1.5"/>
  <rect x="460" y="60" width="200" height="420" rx="12" fill="url(#g3)" stroke="#bbf7d0" stroke-width="1.5"/>
  <rect x="680" y="60" width="220" height="420" rx="12" fill="url(#g4)" stroke="#fbcfe8" stroke-width="1.5"/>

  <!-- 阶段标题 -->
  <text x="120" y="88" text-anchor="middle" font-size="14" font-weight="700" fill="#4338ca">① 设计阶段</text>
  <text x="340" y="88" text-anchor="middle" font-size="14" font-weight="700" fill="#92400e">② 启动阶段</text>
  <text x="560" y="88" text-anchor="middle" font-size="14" font-weight="700" fill="#166534">③ 运行阶段</text>
  <text x="790" y="88" text-anchor="middle" font-size="14" font-weight="700" fill="#9d174d">④ 运维阶段</text>

  <!-- ① 设计阶段节点 -->
  <g>
    <rect x="40" y="110" width="160" height="44" rx="6" fill="#fff" stroke="#6366f1" stroke-width="1.5"/>
    <text x="120" y="137" text-anchor="middle" font-size="12" fill="#1e293b">创建流程分类</text>
    <text x="120" y="170" text-anchor="middle" font-size="10" fill="#64748b">/workflow/flowCategory</text>
  </g>
  <g>
    <rect x="40" y="195" width="160" height="44" rx="6" fill="#fff" stroke="#6366f1" stroke-width="1.5"/>
    <text x="120" y="222" text-anchor="middle" font-size="12" fill="#1e293b">设计表单</text>
    <text x="120" y="255" text-anchor="middle" font-size="10" fill="#64748b">vform 拖拽 / DIY 代码</text>
  </g>
  <g>
    <rect x="40" y="280" width="160" height="44" rx="6" fill="#fff" stroke="#6366f1" stroke-width="1.5"/>
    <text x="120" y="307" text-anchor="middle" font-size="12" fill="#1e293b">设计流程节点</text>
    <text x="120" y="340" text-anchor="middle" font-size="10" fill="#64748b">审批人/条件/抄送</text>
  </g>
  <g>
    <rect x="40" y="365" width="160" height="44" rx="6" fill="#fff" stroke="#6366f1" stroke-width="1.5"/>
    <text x="120" y="392" text-anchor="middle" font-size="12" fill="#1e293b">发布版本</text>
    <text x="120" y="425" text-anchor="middle" font-size="10" fill="#64748b">版本号 + 激活状态</text>
  </g>

  <!-- ② 启动阶段节点 -->
  <g>
    <rect x="260" y="110" width="160" height="44" rx="6" fill="#fff" stroke="#d97706" stroke-width="1.5"/>
    <text x="340" y="137" text-anchor="middle" font-size="12" fill="#1e293b">发起流程</text>
    <text x="340" y="170" text-anchor="middle" font-size="10" fill="#64748b">/startFlow/index</text>
  </g>
  <g>
    <rect x="260" y="195" width="160" height="44" rx="6" fill="#fff" stroke="#d97706" stroke-width="1.5"/>
    <text x="340" y="222" text-anchor="middle" font-size="12" fill="#1e293b">填写表单</text>
    <text x="340" y="255" text-anchor="middle" font-size="10" fill="#64748b">业务数据落库</text>
  </g>
  <g>
    <rect x="260" y="280" width="160" height="44" rx="6" fill="#fff" stroke="#d97706" stroke-width="1.5"/>
    <text x="340" y="307" text-anchor="middle" font-size="12" fill="#1e293b">提交流程</text>
    <text x="340" y="340" text-anchor="middle" font-size="10" fill="#64748b">创建流程实例</text>
  </g>
  <g>
    <rect x="260" y="365" width="160" height="44" rx="6" fill="#fff" stroke="#d97706" stroke-width="1.5"/>
    <text x="340" y="392" text-anchor="middle" font-size="12" fill="#1e293b">流转到首个审批节点</text>
    <text x="340" y="425" text-anchor="middle" font-size="10" fill="#64748b">Adaptor.draftSubmit()</text>
  </g>

  <!-- ③ 运行阶段节点 -->
  <g>
    <rect x="480" y="110" width="160" height="44" rx="6" fill="#fff" stroke="#16a34a" stroke-width="1.5"/>
    <text x="560" y="137" text-anchor="middle" font-size="12" fill="#1e293b">接收待办</text>
    <text x="560" y="170" text-anchor="middle" font-size="10" fill="#64748b">/flowtask/pendding</text>
  </g>
  <g>
    <rect x="480" y="195" width="160" height="44" rx="6" fill="#fff" stroke="#16a34a" stroke-width="1.5"/>
    <text x="560" y="222" text-anchor="middle" font-size="12" fill="#1e293b">审批操作</text>
    <text x="560" y="255" text-anchor="middle" font-size="10" fill="#64748b">同意/退回/转办/加签…</text>
  </g>
  <g>
    <rect x="480" y="280" width="160" height="44" rx="6" fill="#fff" stroke="#16a34a" stroke-width="1.5"/>
    <text x="560" y="307" text-anchor="middle" font-size="12" fill="#1e293b">流转下一节点</text>
    <text x="560" y="340" text-anchor="middle" font-size="10" fill="#64748b">条件评估 + 虚拟节点转换</text>
  </g>
  <g>
    <rect x="480" y="365" width="160" height="44" rx="6" fill="#fff" stroke="#16a34a" stroke-width="1.5"/>
    <text x="560" y="392" text-anchor="middle" font-size="12" fill="#1e293b">流程结束 / 抄送</text>
    <text x="560" y="425" text-anchor="middle" font-size="10" fill="#64748b">归档 + 通知发起人</text>
  </g>

  <!-- ④ 运维阶段节点 -->
  <g>
    <rect x="700" y="110" width="180" height="44" rx="6" fill="#fff" stroke="#db2777" stroke-width="1.5"/>
    <text x="790" y="137" text-anchor="middle" font-size="12" fill="#1e293b">流程监控</text>
    <text x="790" y="170" text-anchor="middle" font-size="10" fill="#64748b">/workflow/instance</text>
  </g>
  <g>
    <rect x="700" y="195" width="180" height="44" rx="6" fill="#fff" stroke="#db2777" stroke-width="1.5"/>
    <text x="790" y="222" text-anchor="middle" font-size="12" fill="#1e293b">运行时干预</text>
    <text x="790" y="255" text-anchor="middle" font-size="10" fill="#64748b">变更处理人/加节点/跳过</text>
  </g>
  <g>
    <rect x="700" y="280" width="180" height="44" rx="6" fill="#fff" stroke="#db2777" stroke-width="1.5"/>
    <text x="790" y="307" text-anchor="middle" font-size="12" fill="#1e293b">委托设置</text>
    <text x="790" y="340" text-anchor="middle" font-size="10" fill="#64748b">/flowDevops/setting</text>
  </g>
  <g>
    <rect x="700" y="365" width="180" height="44" rx="6" fill="#fff" stroke="#db2777" stroke-width="1.5"/>
    <text x="790" y="392" text-anchor="middle" font-size="12" fill="#1e293b">版本迁移</text>
    <text x="790" y="425" text-anchor="middle" font-size="10" fill="#64748b">存量实例迁到新版本</text>
  </g>

  <!-- 阶段间箭头 -->
  <line x1="200" y1="132" x2="260" y2="132" stroke="#6366f1" stroke-width="2" marker-end="url(#arrow)"/>
  <line x1="200" y1="217" x2="260" y2="217" stroke="#6366f1" stroke-width="2" marker-end="url(#arrow)"/>
  <line x1="200" y1="302" x2="260" y2="302" stroke="#6366f1" stroke-width="2" marker-end="url(#arrow)"/>
  <line x1="200" y1="387" x2="260" y2="387" stroke="#6366f1" stroke-width="2" marker-end="url(#arrow)"/>

  <line x1="420" y1="132" x2="480" y2="132" stroke="#d97706" stroke-width="2" marker-end="url(#arrow)"/>
  <line x1="420" y1="217" x2="480" y2="217" stroke="#d97706" stroke-width="2" marker-end="url(#arrow)"/>
  <line x1="420" y1="302" x2="480" y2="302" stroke="#d97706" stroke-width="2" marker-end="url(#arrow)"/>
  <line x1="420" y1="387" x2="480" y2="387" stroke="#d97706" stroke-width="2" marker-end="url(#arrow)"/>

  <!-- 运行阶段回环箭头 -->
  <path d="M 560 324 Q 560 360 560 195" fill="none" stroke="#16a34a" stroke-width="2" stroke-dasharray="4,3" marker-end="url(#arrow)"/>
  <text x="575" y="265" font-size="10" fill="#166534">循环流转</text>

  <!-- 运行 → 运维 -->
  <line x1="640" y1="132" x2="700" y2="132" stroke="#db2777" stroke-width="2" stroke-dasharray="4,3" marker-end="url(#arrow)"/>
  <line x1="640" y1="217" x2="700" y2="217" stroke="#db2777" stroke-width="2" stroke-dasharray="4,3" marker-end="url(#arrow)"/>
  <line x1="640" y1="302" x2="700" y2="302" stroke="#db2777" stroke-width="2" stroke-dasharray="4,3" marker-end="url(#arrow)"/>
  <line x1="640" y1="387" x2="700" y2="387" stroke="#db2777" stroke-width="2" stroke-dasharray="4,3" marker-end="url(#arrow)"/>

  <!-- 底部图例 -->
  <text x="40" y="510" font-size="11" fill="#64748b">— 顺序流转</text>
  <line x1="120" y1="506" x2="160" y2="506" stroke="#6366f1" stroke-width="2" marker-end="url(#arrow)"/>
  <text x="200" y="510" font-size="11" fill="#64748b">— 运行时干预</text>
  <line x1="290" y1="506" x2="330" y2="506" stroke="#db2777" stroke-width="2" stroke-dasharray="4,3" marker-end="url(#arrow)"/>
</svg>

## 系统架构概览

从技术视角看,AntFlow 是一个**前后端分离 + 引擎封装**的三层架构:

<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 920 480" font-family="-apple-system, BlinkMacSystemFont, 'Segoe UI', sans-serif">
  <defs>
    <linearGradient id="layer-fe" x1="0" y1="0" x2="0" y2="1">
      <stop offset="0%" stop-color="#dbeafe"/>
      <stop offset="100%" stop-color="#bfdbfe"/>
    </linearGradient>
    <linearGradient id="layer-api" x1="0" y1="0" x2="0" y2="1">
      <stop offset="0%" stop-color="#fef3c7"/>
      <stop offset="100%" stop-color="#fde68a"/>
    </linearGradient>
    <linearGradient id="layer-engine" x1="0" y1="0" x2="0" y2="1">
      <stop offset="0%" stop-color="#dcfce7"/>
      <stop offset="100%" stop-color="#bbf7d0"/>
    </linearGradient>
    <linearGradient id="layer-activiti" x1="0" y1="0" x2="0" y2="1">
      <stop offset="0%" stop-color="#fce7f3"/>
      <stop offset="100%" stop-color="#fbcfe8"/>
    </linearGradient>
  </defs>

  <text x="460" y="28" text-anchor="middle" font-size="18" font-weight="700" fill="#1e293b">AntFlow 分层架构</text>

  <!-- 前端层 -->
  <rect x="20" y="50" width="880" height="80" rx="10" fill="url(#layer-fe)" stroke="#93c5fd" stroke-width="1.5"/>
  <text x="40" y="75" font-size="13" font-weight="700" fill="#1e40af">前端层 antflow-vue · Vue 3 + Vite + Element Plus</text>
  <rect x="40" y="88" width="120" height="32" rx="5" fill="#fff" stroke="#3b82f6"/>
  <text x="100" y="108" text-anchor="middle" font-size="11" fill="#1e293b">流程设计器</text>
  <rect x="170" y="88" width="120" height="32" rx="5" fill="#fff" stroke="#3b82f6"/>
  <text x="230" y="108" text-anchor="middle" font-size="11" fill="#1e293b">vform 表单设计</text>
  <rect x="300" y="88" width="120" height="32" rx="5" fill="#fff" stroke="#3b82f6"/>
  <text x="360" y="108" text-anchor="middle" font-size="11" fill="#1e293b">任务中心</text>
  <rect x="430" y="88" width="120" height="32" rx="5" fill="#fff" stroke="#3b82f6"/>
  <text x="490" y="108" text-anchor="middle" font-size="11" fill="#1e293b">流程监控</text>
  <rect x="560" y="88" width="120" height="32" rx="5" fill="#fff" stroke="#3b82f6"/>
  <text x="620" y="108" text-anchor="middle" font-size="11" fill="#1e293b">Saas 接入</text>
  <rect x="690" y="88" width="200" height="32" rx="5" fill="#fff" stroke="#3b82f6"/>
  <text x="790" y="108" text-anchor="middle" font-size="11" fill="#1e293b">流程预览(JSON 渲染)</text>

  <!-- API 层 -->
  <rect x="20" y="150" width="880" height="80" rx="10" fill="url(#layer-api)" stroke="#fbbf24" stroke-width="1.5"/>
  <text x="40" y="175" font-size="13" font-weight="700" fill="#92400e">REST API 层 antflow-web · Spring Boot 2.7 · Open API</text>
  <rect x="40" y="188" width="120" height="32" rx="5" fill="#fff" stroke="#d97706"/>
  <text x="100" y="208" text-anchor="middle" font-size="11" fill="#1e293b">流程定义接口</text>
  <rect x="170" y="188" width="120" height="32" rx="5" fill="#fff" stroke="#d97706"/>
  <text x="230" y="208" text-anchor="middle" font-size="11" fill="#1e293b">任务操作接口</text>
  <rect x="300" y="188" width="120" height="32" rx="5" fill="#fff" stroke="#d97706"/>
  <text x="360" y="208" text-anchor="middle" font-size="11" fill="#1e293b">用户/角色接口</text>
  <rect x="430" y="188" width="120" height="32" rx="5" fill="#fff" stroke="#d97706"/>
  <text x="490" y="208" text-anchor="middle" font-size="11" fill="#1e293b">表单数据接口</text>
  <rect x="560" y="188" width="120" height="32" rx="5" fill="#fff" stroke="#d97706"/>
  <text x="620" y="208" text-anchor="middle" font-size="11" fill="#1e293b">消息通知接口</text>
  <rect x="690" y="188" width="200" height="32" rx="5" fill="#fff" stroke="#d97706"/>
  <text x="790" y="208" text-anchor="middle" font-size="11" fill="#1e293b">Open API(三方接入)</text>

  <!-- 引擎业务层 -->
  <rect x="20" y="250" width="880" height="100" rx="10" fill="url(#layer-engine)" stroke="#86efac" stroke-width="1.5"/>
  <text x="40" y="275" font-size="13" font-weight="700" fill="#166534">引擎业务层 antflow-engine · Adaptor 适配器 + 虚拟节点系统</text>
  <rect x="40" y="288" width="130" height="50" rx="5" fill="#fff" stroke="#16a34a"/>
  <text x="105" y="308" text-anchor="middle" font-size="11" fill="#1e293b">FormOperationAdaptor</text>
  <text x="105" y="326" text-anchor="middle" font-size="10" fill="#64748b">业务表单适配</text>
  <rect x="180" y="288" width="130" height="50" rx="5" fill="#fff" stroke="#16a34a"/>
  <text x="245" y="308" text-anchor="middle" font-size="11" fill="#1e293b">VNode 转换器</text>
  <text x="245" y="326" text-anchor="middle" font-size="10" fill="#64748b">JSON → BPMN</text>
  <rect x="320" y="288" width="130" height="50" rx="5" fill="#fff" stroke="#16a34a"/>
  <text x="385" y="308" text-anchor="middle" font-size="11" fill="#1e293b">ConditionJudge</text>
  <text x="385" y="326" text-anchor="middle" font-size="10" fill="#64748b">条件评估策略</text>
  <rect x="460" y="288" width="130" height="50" rx="5" fill="#fff" stroke="#16a34a"/>
  <text x="525" y="308" text-anchor="middle" font-size="11" fill="#1e293b">ApproverNode</text>
  <text x="525" y="326" text-anchor="middle" font-size="10" fill="#64748b">12 种审批人来源</text>
  <rect x="600" y="288" width="130" height="50" rx="5" fill="#fff" stroke="#16a34a"/>
  <text x="665" y="308" text-anchor="middle" font-size="11" fill="#1e293b">FlowControl</text>
  <text x="665" y="326" text-anchor="middle" font-size="10" fill="#64748b">退回/加签/转办</text>
  <rect x="740" y="288" width="140" height="50" rx="5" fill="#fff" stroke="#16a34a"/>
  <text x="810" y="308" text-anchor="middle" font-size="11" fill="#1e293b">NoticeService</text>
  <text x="810" y="326" text-anchor="middle" font-size="10" fill="#64748b">消息通知渠道</text>

  <!-- 引擎底层 -->
  <rect x="20" y="370" width="880" height="80" rx="10" fill="url(#layer-activiti)" stroke="#f9a8d4" stroke-width="1.5"/>
  <text x="40" y="395" font-size="13" font-weight="700" fill="#9d174d">引擎底层 · Activiti 5.23(fork 魔改) · AF_ 表前缀</text>
  <rect x="40" y="408" width="160" height="32" rx="5" fill="#fff" stroke="#db2777"/>
  <text x="120" y="428" text-anchor="middle" font-size="11" fill="#1e293b">BPMN 2.0 流程定义</text>
  <rect x="210" y="408" width="160" height="32" rx="5" fill="#fff" stroke="#db2777"/>
  <text x="290" y="428" text-anchor="middle" font-size="11" fill="#1e293b">任务实例执行</text>
  <rect x="380" y="408" width="160" height="32" rx="5" fill="#fff" stroke="#db2777"/>
  <text x="460" y="428" text-anchor="middle" font-size="11" fill="#1e293b">历史记录归档</text>
  <rect x="550" y="408" width="160" height="32" rx="5" fill="#fff" stroke="#db2777"/>
  <text x="630" y="428" text-anchor="middle" font-size="11" fill="#1e293b">事件监听</text>
  <rect x="720" y="408" width="160" height="32" rx="5" fill="#fff" stroke="#db2777"/>
  <text x="800" y="428" text-anchor="middle" font-size="11" fill="#1e293b">作业调度</text>

  <!-- 层间箭头 -->
  <line x1="460" y1="130" x2="460" y2="148" stroke="#475569" stroke-width="2" marker-end="url(#arrow)"/>
  <line x1="460" y1="230" x2="460" y2="248" stroke="#475569" stroke-width="2" marker-end="url(#arrow)"/>
  <line x1="460" y1="350" x2="460" y2="368" stroke="#475569" stroke-width="2" marker-end="url(#arrow)"/>

  <defs>
    <marker id="arrow" viewBox="0 0 10 10" refX="9" refY="5" markerWidth="6" markerHeight="6" orient="auto-start-reverse">
      <path d="M 0 0 L 10 5 L 0 10 z" fill="#475569"/>
    </marker>
  </defs>
</svg>

分层说明:

| 层级 | 模块 | 职责 |
|---|---|---|
| 前端层 | antflow-vue | 流程设计器、表单设计器、任务中心、流程预览 |
| API 层 | antflow-web | REST 接口、Open API、鉴权、参数校验 |
| 引擎业务层 | antflow-engine | Adaptor 适配器、虚拟节点转换、条件评估、流程控制 |
| 引擎底层 | Activiti 5.23 fork | BPMN 执行、任务调度、历史归档 |

**核心设计**:引擎业务层(antflow-engine)通过 Adaptor 模式和虚拟节点系统,**完全封装**了 Activiti API。业务代码只与 Adaptor 交互,不直接调用 Activiti。这使得:
- 升级 Activiti 版本对业务零影响
- 替换为 Flowable / Camunda 也只需改底层适配
- 业务开发者无需学习 BPMN 2.0 规范

详见 [架构总览](/dev-guide/architecture)。

## 关键文件索引

| 文件 | 作用 |
|---|---|
| [antflow-vue/src/router/index.js](https://github.com/mrtylerzhou/AntFlow/blob/master/antflow-vue/src/router/index.js) | 前端路由表,定义所有页面路径 |
| [antflow-vue/public/mock/menu.json](https://github.com/mrtylerzhou/AntFlow/blob/master/antflow-vue/public/mock/menu.json) | 演示环境菜单数据 |
| [antflow-vue/src/store/modules/user.js](https://github.com/mrtylerzhou/AntFlow/blob/master/antflow-vue/src/store/modules/user.js) | 用户状态管理,登录/登出逻辑 |
| [antflow-vue/src/utils/auth.js](https://github.com/mrtylerzhou/AntFlow/blob/master/antflow-vue/src/utils/auth.js) | Token Cookie 工具 |
| [antflow-vue/vite.config.js](https://github.com/mrtylerzhou/AntFlow/blob/master/antflow-vue/vite.config.js) | Vite 构建配置,含后端代理 |

## 下一步

- [流程设计](/workflow-design/flow-category) — 开始设计你的第一个工作流
- [架构总览](/dev-guide/architecture) — 深入理解 AntFlow 技术架构
- [Adaptor 模式](/dev-guide/adaptor-pattern) — 了解 AntFlow 的核心开发模式
