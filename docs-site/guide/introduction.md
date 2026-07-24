# 什么是 AntFlow

> AntFlow 是一款基于 Activiti 的**企业级低代码工作流引擎平台**。可独立部署,也可以作为模块嵌入到现有系统中。使用简单,易于维护,前端只需要简单点击和选择即可完成流程配置,后端只需要实现一个接口即可新开发出一条业务流程。

## 核心特性

### 🥇 虚拟节点(VNode)模式

AntFlow 的**最核心设计**。将流程流转业务和引擎(Flowable / camunda / activiti)执行 API 高度分离,**零流程引擎知识也可上手开发工作流系统**。

- 所有节点配置(审批人、条件、抄送等)都以"虚拟节点"形式存在,在运行时再转换为 Activiti 能识别的 BPMN 元素
- 你不需要懂 BPMN 2.0 规范、不需要懂 Activiti API,只需要理解"审批人 → 条件 → 抄送"的业务语义
- 引擎底层的魔改 Activiti 5.23 完全被 Adaptor 层封装,升级引擎对业务代码零影响

详见 [虚拟节点系统](/dev-guide/vnode-system)。

### 😄 超级简单的开发模式

使用**适配器模式**将流程引擎流转业务和用户表单处理业务完全分离。

- **DIY 流程**:后端只需实现 `FormOperationAdaptor` 一个接口,即可快速开发上线一条业务流程
- **低代码流程**:拖拽设计表单和流程,**零代码**即可发起业务流程
- **接入方流程**:第三方系统通过 Open API 接入,无需嵌入前端

详见 [Adaptor 适配器模式](/dev-guide/adaptor-pattern)。

### 🚩 中国式办公全支持

AntFlow 从设计之初就面向中国式办公场景,支持以下高级流程操作:

| 能力 | 说明 |
|---|---|
| 串行审批 | 节点依次执行 |
| 并行审批 | 多分支同时执行,聚合后继续 |
| 会签 | 多人全部同意才通过(不限顺序) |
| 或签 | 一人同意即通过 |
| 顺序会签 | 按指定顺序依次审批 |
| 审批人去重 | 相邻节点同一人自动跳过 |
| 加批 | 动态增加审批人(会签/或签/顺序会签) |
| 减签 | 动态移除审批人 |
| 委托 | 将任务转交他人处理 |
| 转办 | 将任务转移给他人,自己不再参与 |
| 退回任意节点 | 支持退回发起人、退回上一节点、退回任意历史节点 |
| 动态跳过节点 | 运行时根据条件跳过指定节点 |
| 变更处理人 | 运行时修改当前或未来节点的处理人 |
| 版本迁移 | 流程定义升级后,存量实例可迁移到新版本 |

详见 [流程流转控制](/dev-guide/flow-control)。

### 👨‍👨‍👦‍👦 完全接管用户系统

Activiti 自身的用户系统太弱,而且企业都有自己的用户系统。AntFlow **完全接管** Activiti 用户系统:

- 忘掉 Activiti 中的用户表、群组表、成员关系表
- 通过 `antflow.common.empTable.*` 配置项指定你自己的用户表名和字段名
- 实现 `AfUserService` 接口即可对接任意用户系统(SSO、企业微信、钉钉、飞书等)

详见 [扩展审批人来源](/dev-guide/extend-approver)。

### 💻 流程数据全 JSON

流程预览图、流程审批路径都是 **JSON 数据**,而非图片流。

- 前端可自定义视觉渲染风格,以适应不同风格的系统
- 流程配置采用 **JSON-first 读取策略**:节点配置统一存入 `node_config_json` 字段,减少 DB JOIN,提升读取性能
- 配置读写通过强类型 Java 对象(`BpmnNodeConfigJson`),避免手写 JSON 解析

## 技术栈

### 后端

| 技术 | 版本 | 说明 |
|---|---|---|
| Java | 8(兼容至 21) | master 分支为 Java 8,新版本请切换 `java17_support` 分支 |
| Spring Boot | 2.7.17 | Web 框架 |
| Activiti | 5.23(fork 魔改) | 流程引擎,**必须使用 AntFlow 源码提供的 SQL 建表** |
| MyBatis Plus | 3.5.1 | ORM |
| Drools | — | README 提及,但**实际未使用**(条件评估走自研 `ConditionJudge` 策略模式) |
| MySQL | 5.7+ | 主数据库,同时支持 12+ 其他数据库 |
| Druid | 1.1.17 | 连接池 |
| Fastjson2 | 2.0.53 | JSON 序列化(配置 JSON-first 策略的基础) |

::: warning 关于 Drools
README 和部分文档提及使用 Drools 规则引擎,但通过源码检索,`antflow-engine` 中**没有任何 Drools 依赖和调用**。实际条件评估走的是自研 `ConditionJudge` 接口 + 每条件类型一个 Judge 实现的策略模式。本文档以源码实际行为为准。
:::

### 前端

| 技术 | 版本 |
|---|---|
| Vue | 3.5.15 |
| Vite | — |
| Element Plus | 2.10.7 |
| Pinia | 3.0.2 |
| Axios | 1.9.0 |
| ECharts | 5.6.0 |
| vform | —(低代码表单设计器) |

## 项目结构

AntFlow 采用 Maven 多模块结构:

```
antflow/                          # 根模块(聚合 POM)
├── antflow-base/                 # 基础层:通用工具、常量、DTO、接口定义
├── antflow-engine/               # 核心引擎层:流程引擎所有业务逻辑
├── antflow-spring-boot-starter/  # 自动装配模块,对外集成入口
├── antflow-web/                  # 演示 Web 应用(业务示例)
├── antflow-vue/                  # Vue 3 前端管理界面
├── script/                       # 数据库初始化脚本
└── doc/                          # 项目文档
```

各模块职责详见 [架构总览](/dev-guide/architecture)。

## 适用场景

- **企业内部审批系统**:请假、报销、采购、合同、用印等
- **SaaS 工作流平台**:多租户隔离,支持各租户独立用户系统
- **嵌入式工作流引擎**:通过 Starter 嵌入现有 Spring Boot 项目
- **三方系统接入**:通过 Open API 让外部系统调用 AntFlow 流程能力

## 许可证

Apache 2.0 协议,可用于个人或公司项目,**禁止二次开源**(联系作者获得授权的除外)。

## 学习路径

| 角色 | 推荐路径 |
|---|---|
| 新手 | [快速开始](/guide/quick-start) → [系统总览](/guide/overview) → [流程设计](/workflow-design/flow-category) |
| 实施人员 | [流程设计](/workflow-design/flow-designer) → [节点类型](/workflow-design/node-types) → [审批人规则](/workflow-design/approver-rules) |
| 后端开发者 | [架构总览](/dev-guide/architecture) → [Adaptor 模式](/dev-guide/adaptor-pattern) → [集成现有系统](/dev-guide/integrate-existing) |
| 运维人员 | [生产部署](/ops/deploy) → [多数据库支持](/ops/db-multi) → [常见问题](/ops/troubleshooting) |
