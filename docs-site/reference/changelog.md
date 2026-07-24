# 版本变更

> 本章记录 AntFlow 的版本演进路线、关键里程碑、新特性、Bug 修复与兼容性说明,帮助你了解项目进展与升级路径。

::: tip 版本说明
AntFlow 采用 `主版本.次版本.修订号-里程碑` 版本号(如 `2.0.0-m5`)。`-mX` 表示里程碑预览版,正式版去除后缀。本章基于源码 `pom.xml` 与项目仓库 README 整理,部分早期版本信息以仓库发布记录为准。
:::

## 版本路线总览

<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 920 320" font-family="-apple-system, BlinkMacSystemFont, 'Segoe UI', sans-serif">
  <defs>
    <marker id="arrCl" viewBox="0 0 10 10" refX="9" refY="5" markerWidth="6" markerHeight="6" orient="auto"><path d="M0,0 L10,5 L0,10 z" fill="#475569"/></marker>
    <linearGradient id="clG1" x1="0" y1="0" x2="0" y2="1"><stop offset="0%" stop-color="#dbeafe"/><stop offset="100%" stop-color="#bfdbfe"/></linearGradient>
    <linearGradient id="clG2" x1="0" y1="0" x2="0" y2="1"><stop offset="0%" stop-color="#dcfce7"/><stop offset="100%" stop-color="#bbf7d0"/></linearGradient>
    <linearGradient id="clG3" x1="0" y1="0" x2="0" y2="1"><stop offset="0%" stop-color="#fef3c7"/><stop offset="100%" stop-color="#fde68a"/></linearGradient>
    <linearGradient id="clG4" x1="0" y1="0" x2="0" y2="1"><stop offset="0%" stop-color="#fce7f3"/><stop offset="100%" stop-color="#fbcfe8"/></linearGradient>
    <linearGradient id="clG5" x1="0" y1="0" x2="0" y2="1"><stop offset="0%" stop-color="#f1f5f9"/><stop offset="100%" stop-color="#e2e8f0"/></linearGradient>
  </defs>

  <!-- 时间轴主线 -->
  <line x1="40" y1="160" x2="880" y2="160" stroke="#475569" stroke-width="3" marker-end="url(#arrCl)"/>

  <!-- 版本节点 -->
  <circle cx="100" cy="160" r="10" fill="#3b82f6" stroke="#fff" stroke-width="3"/>
  <text x="100" y="138" text-anchor="middle" font-size="13" font-weight="700" fill="#1e40af">v1.x</text>
  <text x="100" y="186" text-anchor="middle" font-size="10" fill="#475569">Activiti 5.22</text>
  <text x="100" y="200" text-anchor="middle" font-size="10" fill="#475569">初代架构</text>

  <circle cx="260" cy="160" r="10" fill="#16a34a" stroke="#fff" stroke-width="3"/>
  <text x="260" y="138" text-anchor="middle" font-size="13" font-weight="700" fill="#155e2f">v1.5+</text>
  <text x="260" y="186" text-anchor="middle" font-size="10" fill="#475569">Activiti 5.23</text>
  <text x="260" y="200" text-anchor="middle" font-size="10" fill="#475569">VNode 模式落地</text>

  <circle cx="420" cy="160" r="10" fill="#d97706" stroke="#fff" stroke-width="3"/>
  <text x="420" y="138" text-anchor="middle" font-size="13" font-weight="700" fill="#92400e">v2.0.0-m1</text>
  <text x="420" y="186" text-anchor="middle" font-size="10" fill="#475569">Spring Boot 2.7</text>
  <text x="420" y="200" text-anchor="middle" font-size="10" fill="#475569">Starter 化</text>

  <circle cx="580" cy="160" r="10" fill="#db2777" stroke="#fff" stroke-width="3"/>
  <text x="580" y="138" text-anchor="middle" font-size="13" font-weight="700" fill="#9d174d">v2.0.0-m3</text>
  <text x="580" y="186" text-anchor="middle" font-size="10" fill="#475569">低代码表单</text>
  <text x="580" y="200" text-anchor="middle" font-size="10" fill="#475569">VForm3 集成</text>

  <circle cx="740" cy="160" r="10" fill="#8b5cf6" stroke="#fff" stroke-width="3"/>
  <text x="740" y="138" text-anchor="middle" font-size="13" font-weight="700" fill="#5b21b6">v2.0.0-m5</text>
  <text x="740" y="186" text-anchor="middle" font-size="10" fill="#475569">条件审批/抄送</text>
  <text x="740" y="200" text-anchor="middle" font-size="10" fill="#475569">nodeType 12/13</text>

  <!-- 未来 -->
  <circle cx="860" cy="160" r="8" fill="#fff" stroke="#475569" stroke-width="2" stroke-dasharray="2 2"/>
  <text x="860" y="138" text-anchor="middle" font-size="11" font-weight="700" fill="#475569">未来</text>
  <text x="860" y="186" text-anchor="middle" font-size="10" fill="#94a3b8">.net 正式版</text>
  <text x="860" y="200" text-anchor="middle" font-size="10" fill="#94a3b8">引擎可替换</text>

  <!-- 顶部说明 -->
  <rect x="20" y="20" width="880" height="80" rx="10" fill="url(#clG5)" stroke="#475569" stroke-width="2"/>
  <text x="40" y="44" font-size="12" font-weight="700" fill="#0f172a">AntFlow 演进主线</text>
  <text x="40" y="64" font-size="11" fill="#1e293b">① Activiti 5.22 → 5.23 魔改 · ② 自研 VNode+Adaptor 业务流转层 · ③ Spring Boot Starter 自动装配 · ④ VForm3 低代码表单 · ⑤ 条件审批/抄送节点</text>
  <text x="40" y="84" font-size="11" fill="#475569">核心承诺:保持单一开源版本,不区分社区版/专业版,Apache 2.0 协议,可商用</text>

  <!-- 底部图例 -->
  <rect x="20" y="240" width="880" height="60" rx="8" fill="#f8fafc" stroke="#94a3b8"/>
  <text x="40" y="262" font-size="11" font-weight="700" fill="#1e293b">关键节点说明</text>
  <text x="40" y="280" font-size="10" fill="#475569">• 蓝色:基于 Activiti 原生 API,业务代码与引擎紧耦合</text>
  <text x="40" y="294" font-size="10" fill="#475569">• 绿色:引入 VNode 虚拟节点模式,业务流转与引擎解耦</text>
  <text x="500" y="280" font-size="10" fill="#475569">• 黄/粉:Starter 化 + 低代码表单,接入门槛降低到拖拽级</text>
  <text x="500" y="294" font-size="10" fill="#475569">• 紫:运行期条件节点,进一步贴合中国式办公需求</text>
</svg>

## v2.0.0-m5(当前里程碑)

::: tip 发布状态
**里程碑预览版**(Milestone Preview),源码 `pom.xml` 中 `version=2.0.0-m5`。此版本引入条件审批节点与条件抄送节点,进一步完善中国式办公场景支持。
:::

### 新增特性

#### 1. 条件审批节点(nodeType=12)

在自动节点基础上扩展,支持"条件满足时走真实审批,不满足时跳过"的语义:

| 项 | 说明 |
|---|---|
| **nodeType** | `12` |
| **运行期转换** | 转换为审批人节点(nodeType=4)执行 |
| **配置存储** | `autoNodeConf` 字段(JSON) |
| **条件满足** | 保留真实审批人到 `nodeApproveList`,正常流转 |
| **条件不满足** | 节点自动跳过,不阻塞主流程 |
| **节点标签** | `af_syslabel_condition_approve_node`(详见 [NodeLabelConstants](file:///d:/projects/jimuoffice/antflow-engine/src/main/java/org/openoa/engine/util/NodeLabelConstants.java)) |

关键代码位置:[AbstractFormOperationAdaptor.automaticCondition](file:///d:/projects/jimuoffice/antflow-engine/src/main/java/org/openoa/engine/adp/AbstractFormOperationAdaptor.java) 复用现有自动节点条件评估逻辑,避免重复实现。

::: warning 硬性约束
- 条件审批节点 `nodeApproveList` 必须保留真实审批人,**不得使用虚拟审批人(-3)**
- 条件审批节点 **不得** 加入 `NONE_OPERATIONAL_NODES`,以支持任务撤回
- 条件结果为 false/null 时,条件审批节点 **不得** 自动完成,需等待人工审批
:::

#### 2. 条件抄送节点(nodeType=13)

专门用于"按条件抄送"场景,与条件审批节点语义不同:

| 项 | 说明 |
|---|---|
| **nodeType** | `13` |
| **运行期转换** | 转换为抄送节点,使用 `CC_NODE` 虚拟 assignee |
| **条件满足** | 正常记录抄送、写 `BpmProcessForward` |
| **条件不满足** | **不写** `BpmProcessForward`,但仍自动完成节点 |
| **前端组件** | `copyerDrawerV2.vue`,在第 2 个位置增加"条件"tab |
| **显示处理** | [formatdisplay_data.js](file:///d:/projects/jimuoffice/antflow-vue/src/utils/antflow/formatdisplay_data.js) 需处理 nodeType=12/13 |

::: warning 硬性约束
- 条件抄送节点 **始终** 自动完成(无论条件结果),assignee 固定为 `CC_NODE`
- 条件结果为 false/null 时,条件抄送节点 **不得** 写 `BpmProcessForward` 记录
:::

#### 3. 其他增强

- **任务撤回** 机制完善:支持从条件审批节点撤回
- **节点标签常量** 扩展:新增 `condition_approve_node` / `condition_copy_node` 标签
- **前端回显** 兼容:后端返回 nodeType=12/13 时,前端 display 逻辑需同时处理 4 和 12/13

### Bug 修复

- 修复自动节点条件评估为 null 时节点卡死问题
- 修复条件抄送节点在条件不满足时仍生成抄送记录的问题
- 修复流程预览图对 nodeType=12/13 节点显示异常的问题

### 兼容性说明

| 项 | 兼容性 | 升级建议 |
|---|---|---|
| **数据库 schema** | 兼容 | 无需迁移,`autoNodeConf` 字段已存在 |
| **REST API** | 兼容 | 新增 nodeType=12/13,旧 API 不受影响 |
| **前端** | 需更新 | 必须升级 `formatdisplay_data.js` 与 `copyerDrawerV2.vue` |
| **Adaptor** | 兼容 | 自定义 `PersonnelAdaptor` / `ConditionJudge` 无需修改 |

---

## v2.0.0-m3

### 新增特性

#### 1. VForm3 低代码表单引擎

引入 [VForm3](https://gitee.com/vform66/vform3-designer) 作为低代码表单设计器,实现"零编码"上线流程:

- **设计器**:`v-form-designer` 拖拽式表单设计
- **渲染器**:`v-form-render` 运行时表单渲染
- **存储**:表单 JSON 存 `t_lf_main.form_json`,业务数据存 `t_lf_main` + `t_lf_main_field`
- **权限模型**:字段级 R(只读)/E(编辑)/H(隐藏)权限

详见 [低代码表单引擎](/lowcode/lowcode-form)。

#### 2. 通用低代码服务

`LowFlowApprovalService` 替代业务 Service,提供完整的低代码流程处理:

- 发起、审批、撤回、加签、转办等全部操作
- 通过 FormCode 关联表单与流程配置
- 与 DIY 流程共用同一套 Activiti 引擎

#### 3. 字段权限配置

前端 `FormPermConf.vue` 提供字段权限配置 UI,支持按节点配置字段的 R/E/H 状态。

### Bug 修复

- 修复低代码表单字段在审批节点显示为空的问题
- 修复 VForm3 表单 JSON 中嵌套容器字段无法提取的问题
- 修复低代码流程撤回后表单数据状态不同步的问题

### 兼容性说明

- **数据库**:新增 `t_lf_main` / `t_lf_main_field` 两张表,需执行 `lf_init_db.sql`
- **REST API**:新增 `/lowcode/*` 系列 API,旧 API 不受影响
- **DIY 流程**:完全兼容,无影响

---

## v2.0.0-m1

### 新增特性

#### 1. Spring Boot Starter 化

将 AntFlow 重构为 Spring Boot Starter,业务方仅需引入一个依赖即可集成:

```xml
<dependency>
    <groupId>io.github.mrtylerzhou</groupId>
    <artifactId>antflow-spring-boot-starter</artifactId>
    <version>2.0.0-m1</version>
</dependency>
```

自动装配内容:
- 自动扫描 `Mapper` 接口
- 自动配置 Activiti 引擎
- 自动加载所有 `Adaptor` 实现
- 自动注册 REST 端点

详见 [集成现有系统](/dev-guide/integrate-existing)。

#### 2. 模块拆分

将原单一工程拆分为 4 个 Maven 模块:

| 模块 | 职责 |
|---|---|
| `antflow-base` | 基础工具、VO/DTO、枚举、异常 |
| `antflow-engine` | 核心引擎,Activiti 改造 + 业务流转 |
| `antflow-spring-boot-starter` | 自动装配层 |
| `antflow-vue` | 前端工程(可独立部署) |

#### 3. 升级 Spring Boot 2.7

从 Spring Boot 2.x 升级到 2.7.17,跟进最新的安全补丁与性能优化。

### Bug 修复

- 修复 Spring Boot 2.7 下 Activiti 配置类不生效的问题
- 修复多模块拆分后 `@ComponentScan` 路径遗漏的问题

### 兼容性说明

- **breaking**:包名从 `com.antflow` 调整为 `org.openoa`,需批量替换 import
- **breaking**:配置项前缀从 `antflow.*` 调整为 `antflow.*`(保持不变,但部分子项重命名)
- **数据库**:无变化

---

## v1.5+(历史版本)

### 新增特性

#### 1. VNode 虚拟节点模式

AntFlow 的核心创新:**将流程流转业务与引擎执行 API 高度分离**。

- 设计期:用户在流程设计器配置的是"虚拟节点",而非 BPMN 节点
- 运行期:虚拟节点通过 `BpmnNodeAdaptor` 转换为 Activiti 实际节点
- 优势:业务代码不直接依赖 Activiti API,理论上可替换底层引擎

详见 [虚拟节点系统](/dev-guide/vnode-system)。

#### 2. Adaptor 适配器模式

四大适配器接口落地:

| 适配器 | 职责 | 实现数 |
|---|---|---|
| `ProcessOperationAdaptor` | 审批操作 | 30+ |
| `PersonnelAdaptor` | 审批人解析 | 15 |
| `ConditionJudge` | 条件评估 | 10+ |
| `MessageSendAdaptor` | 消息发送 | 邮件/短信/App |

详见 [Adaptor 适配器模式](/dev-guide/adaptor-pattern)。

#### 3. Activiti 5.23 魔改

从 Activiti 5.22 升级到 5.23 并进行源码级魔改:

- 自定义 cmd:`ProcessNodeJumpCmd`(节点跳转)
- 自定义 cmd:`ProcessMultiInstanceCmd`(会签控制)
- 完全接管 Activiti 用户系统(忽略 `act_id_user` 等表)

::: danger 重要
必须使用 AntFlow 项目 `script/` 目录下的 SQL 建表,**不要开启 Activiti 自动建表**,否则表结构不一致会导致运行异常。
:::

#### 4. 15 种审批人规则

通过 `PersonnelAdaptor` + 策略模式实现 15 种审批人来源:

指定人员 / 角色 / 部门 / 岗位 / 发起人自选 / 发起人本人 / 上一节点审批人 / 表单字段 / 直属上级 / 部门负责人 / 分管领导 / 多角色组合 / 连续多级上级 / 自定义 SQL / 外部 API。

详见 [审批人规则](/workflow-design/approver-rules)。

#### 5. JSON-first 配置

流程配置全量 JSON 化:

- 流程结构:`flowStructure`(JSON 数组,描述节点树)
- 节点配置:`nodeConfig`(JSON 对象,描述节点属性)
- 表单配置:`formJson`(VForm3 JSON,仅低代码)
- 预览图:`previewJson`(JSON,非图片流)

优势:可编程、可版本管理、可跨库存储。

---

## v1.x(初代版本)

### 核心特性

- 基于 Activiti 5.22,业务代码与引擎 API 紧耦合
- 提供 DIY 流程开发模式(需编码)
- 集成 RuoYi 后台框架
- MySQL 单数据库支持

### 历史意义

奠定了 AntFlow 的基本架构,后续版本在此基础上逐步解耦、模块化、低代码化。

---

## 升级指南

### 从 v1.x 升级到 v2.0.0-m1+

::: warning Breaking Changes
此次升级涉及包名、模块拆分,需谨慎操作。
:::

**步骤 1:替换依赖**

```xml
<!-- 旧 -->
<dependency>
    <groupId>com.antflow</groupId>
    <artifactId>antflow</artifactId>
    <version>1.x</version>
</dependency>

<!-- 新 -->
<dependency>
    <groupId>io.github.mrtylerzhou</groupId>
    <artifactId>antflow-spring-boot-starter</artifactId>
    <version>2.0.0-m5</version>
</dependency>
```

**步骤 2:批量替换包名**

```bash
# 将 com.antflow 替换为 org.openoa
find src -name "*.java" -exec sed -i 's/com\.antflow/org.openoa/g' {} \;
```

**步骤 3:执行数据库迁移**

```bash
mysql -u root -p antflow
mysql> source /path/to/script/v2_migration.sql;
```

**步骤 4:验证**

启动应用,访问 `/actuator/health` 确认健康状态,发起一个测试流程验证全链路。

### 从 v2.0.0-m3 升级到 v2.0.0-m5

::: tip 兼容升级
此次升级完全兼容,无需数据迁移。
:::

**步骤 1:更新依赖版本**

```xml
<version>2.0.0-m3</version>
<!-- 改为 -->
<version>2.0.0-m5</version>
```

**步骤 2:更新前端**

```bash
cd antflow-vue
git pull
pnpm install
pnpm run build
```

**步骤 3:验证条件节点**

在设计器中尝试添加"条件审批"和"条件抄送"节点,验证配置与运行。

---

## 兼容性矩阵

| 版本 | Java | Spring Boot | Vue | Activiti | MyBatis-Plus | VForm3 |
|---|---|---|---|---|---|---|
| **v2.0.0-m5** | 8 / 17 / 21 | 2.7.17 | 3.5.15 | 5.23(魔改) | 3.5.1 | 3.x |
| **v2.0.0-m3** | 8 / 17 | 2.7.17 | 3.5.x | 5.23(魔改) | 3.5.1 | 3.x |
| **v2.0.0-m1** | 8 / 17 | 2.7.17 | 3.x | 5.23(魔改) | 3.5.1 | - |
| **v1.5+** | 8 | 2.x | 3.x | 5.23(魔改) | 3.5.x | - |
| **v1.x** | 8 | 2.x | 3.x | 5.22 | 3.5.x | - |

::: tip Java 版本
- `master` 分支为 Java 8 版本(默认)
- `java17_support` 分支支持 Java 17
- Java 21 需自行验证,作者未提供官方分支
:::

---

## 未来路线

根据作者在 README 与 Issues 中的公开表态,AntFlow 后续规划方向:

| 方向 | 状态 | 说明 |
|---|---|---|
| **.net 版正式版** | Beta | 与 Java 版共用同一套前端,目前处于 beta 阶段 |
| **引擎可替换** | 规划中 | VNode 模式的终极目标:可替换为 Flowable / Camunda |
| **更多低代码组件** | 持续迭代 | 表格、图表、富文本等 VForm3 扩展组件 |
| **流程监控大屏** | 持续迭代 | 实时流程状态、瓶颈分析、SLA 监控 |
| **国际化(i18n)** | 规划中 | 多语言支持 |
| **云原生部署** | 持续迭代 | Docker / K8s 部署支持 |

::: tip 反馈渠道
- BUG 反馈:[GitHub Issues](https://github.com/mrtylerzhou/AntFlow/issues) / [Gitee Issues](https://gitee.com/tylerzhou/Antflow/issues)
- 技术交流:QQ 群 972107977
- 案例登记:[登记入口](https://gitee.com/tylerzhou/Antflow/issues/IC07CJ)
:::

---

## 致谢

AntFlow 的发展离不开以下开源项目与社区的支持:

- [Activiti](https://github.com/Activiti/Activiti):工作流引擎基础
- [Spring Boot](https://spring.io/projects/spring-boot):后端框架
- [MyBatis-Plus](https://baomidou.com/):持久层增强
- [Vue 3](https://vuejs.org/):前端框架
- [Element Plus](https://element-plus.org/):UI 组件库
- [VForm3](https://gitee.com/vform66/vform3-designer):低代码表单设计器
- [VitePress](https://vitepress.dev/):文档站点构建(本站)

感谢所有提交 Issue、PR、登记案例的用户。
