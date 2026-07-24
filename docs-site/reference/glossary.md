# 术语表

> 本章汇总 AntFlow 文档中涉及的核心术语、缩写、概念,按字母顺序排列,帮助你快速理解 AntFlow 的领域语言。

## A

### Activiti
AntFlow 基于其 5.23 版本深度改造的开源 BPMN 2.0 工作流引擎。AntFlow 对其源码进行了魔改,因此**必须使用项目提供的 SQL 建表,不能开启 Activiti 自动建表**。

### Actuator
Spring Boot 提供的生产级监控端点,AntFlow 通过 `/actuator/health`、`/actuator/metrics` 等端点暴露应用状态。

### Adaptor(适配器)
AntFlow 的核心设计模式,将流程引擎流转业务和具体业务处理分离。包含 4 大类:
- `ProcessOperationAdaptor`:操作适配器(审批操作)
- `PersonnelAdaptor`:审批人适配器(人员解析)
- `ConditionJudge`:条件评估器(条件判断)
- `MessageSendAdaptor`:消息发送适配器(通知渠道)

### AdaptorFactory
适配器工厂,根据 `operationType`、`personnelType` 等路由到对应的适配器实现。

### AfRoleService
AntFlow 角色服务 SPI 接口,实现此接口可接入企业角色系统。

### AfUserService
AntFlow 用户服务 SPI 接口,实现此接口可接入企业用户系统。需实现 4 个核心方法:`queryByNameFuzzy`、`queryUserByIds`、`getById`、`queryCompanyByNameFuzzy`。

### AntFlow
开源企业级低代码工作流引擎,基于 Activiti 5.23 改造,采用 Apache 2.0 协议开源。

### AntFlowAutoConfiguration
Spring Boot Starter 的自动配置类,通过 `@ComponentScan({"org.openoa"})` 自动注册所有组件。

### API(REST API)
AntFlow 后端通过 REST 接口提供服务,主要入口:`/bpmnConf/*`、`/lowcode/*`、`/informationTemplate/*` 等。

### AppId(应用 ID)
第三方接入时分配的应用标识,用于隔离不同业务方。

### ApproveNode(审批节点)
流程中由人工审批的节点,对应 `nodeType=4`(NODE_TYPE_APPROVER),运行期映射为 Activiti 原生 `userTask`。

### Assignee(处理人)
Activiti 任务的实际处理人,通过 `taskService.setAssignee()` 设置。

### AutoNode(自动节点)
条件满足时自动完成的节点,对应 `nodeType=9`(NODE_TYPE_AUTO_NODE)。

## B

### BaseIdTranStruVo
AntFlow 通用的 ID-Name 传输对象,只有 `id` 和 `label` 两个字段,广泛用于人员/角色选择。

### BPMN(Business Process Model and Notation)
业务流程模型与标记法,OMG 标准的流程建模语言。AntFlow 兼容 BPMN 2.0 子集。

### BpmnConf
流程配置主实体,对应 `t_bpmn_conf` 表,记录每个流程的基本信息、表单编码、生效状态等。

### BpmnConfBizService
流程配置核心服务,提供 `edit`(保存)、`detail`(详情)、`effectiveBpmn`(生效)、`preview`(预览)等方法。

### BpmnNodeAdaptor
节点适配器,负责将 AntFlow 节点类型转换为 Activiti 引擎能理解的元素。

### BpmnNodeVo
节点 VO,包含节点 ID、名称、类型、属性(审批人/条件配置)等。

### BpmnPersonnelProviderService
审批人提供者 SPI 接口,实现此接口可扩展审批人来源(15 种内置规则)。

### BpmnProcessForward
流程流转记录表(`t_bpm_process_forward`),记录每个任务的审批历史。

### BpmnStartConditionsVo
流程启动条件 VO,包含发起人、表单数据、流程变量等启动所需信息。

## C

### CC_NODE(抄送节点)
抄送任务使用的虚拟 assignee(值为 `-3`),自动完成,不阻塞流程。

### ConditionJudge
条件评估器接口,实现此接口可扩展条件规则。AntFlow 内置 10+ 种条件类型。

### ConditionTypeEnum
条件类型枚举,定义所有内置条件类型(数值比较、字符串匹配、日期范围等)。

### ConfDetailRequestDto
流程配置详情查询 DTO,包含分页参数和过滤条件。

### Consent(同意)
审批操作的一种,`operationType=3`,同意并流转到下一节点。

### CopyNode(抄送节点)
将流程信息通知给指定人员的节点,对应 `nodeType=6`(V1)或 `nodeType=8`(V2)。

### CustomProcess(自定义流程)
即 DIY 模式,业务方需自己实现表单、Service、Controller。

## D

### DataPermissionAdaptor
数据权限适配器,控制流程实例、任务的可访问范围。

### DbType
MyBatis-Plus 内置的数据库类型枚举,AntFlow 通过 `DefaultDataBaseTypeDetector` 自动侦测,支持 12+ 种数据库。

### DIY(Do It Yourself)
自定义流程模式,业务方需编写代码实现表单与业务逻辑。详见 [低代码 vs 自定义表单](/lowcode/lowcode-vs-diy)。

### Druid
阿里开源的 JDBC 连接池,AntFlow 默认使用,提供监控面板和慢 SQL 拦截。

## E

### EffectiveStatus(生效状态)
流程/表单版本是否生效的字段,`0=未生效,1=已生效`。同族仅一个生效版本(互斥)。

### Engine(引擎层)
AntFlow 的核心模块 `antflow-engine`,包含 Activiti 改造代码 + AntFlow 自研业务流转引擎。

### ExclusiveGateway(排他网关)
BPMN 元素,用于条件分支。AntFlow 的条件节点(nodeType=3)转换为 `exclusiveGateway`。

## F

### FormCode(表单编码)
流程与表单的关联标识,与 `t_bpmn_conf.form_code` 对应。每个 FormOperationAdaptor 实现类的 `@ActivitiServiceAnno(svcName=...)` 必须与 formCode 一致。

### FormFactory
表单工厂,根据 formCode 路由到对应的 `FormOperationAdaptor` 实现。

### FormOperationAdaptor
表单操作适配器,核心 SPI 接口,DIY 模式必须实现。包含 `submitData`、`queryData`、`consentData` 等方法。

### FormPermission(表单权限)
字段级权限控制,值为 `R`(只读)、`E`(可编辑)、`H`(隐藏)。

### FormTemplate(表单模板)
预置的表单模板,可通过模板快速创建流程。

### ForwardNode(转办节点)
将任务转给其他人处理的操作,`operationType=10`。

## G

### Gateway(网关)
BPMN 元素,用于流程分支控制。AntFlow 支持:
- `exclusiveGateway`(排他网关,条件分支)
- `parallelGateway`(并行网关,会签/或签)

### GaussDB
华为开源的数据库,AntFlow 支持其开源版 openGauss。

## H

### HistoryLevel(历史级别)
Activiti 历史日志记录级别:`none`、`activity`、`audit`(推荐)、`full`。

### Hook(钩子)
扩展点,在特定时机执行自定义逻辑。AntFlow 通过 SPI 接口提供 Hook 机制。

## I

### InlineForm(内联表单)
低代码表单的默认模式,一个流程对应一个表单,通过 `bpmn_conf_id` 关联。与外部表单模式相对。

### IsLowCodeFlow(是否低代码流程)
`t_bpmn_conf.is_lowcode_flow` 字段,`1=低代码流程,0=DIY 流程`。

## J

### JobExecutor(任务执行器)
Activiti 的异步任务执行组件,通过 `spring.activiti.job-executor-activate=true` 开启。

### Javassist
字节码操作库,AntFlow 在 `AdaptorFactory` 中使用 Javassist 动态生成代理类,加速 SPI 路由。

## L

### LF(Low-Code Form)
低代码表单模式,业务方零编码,通过 VForm3 拖拽设计表单。

### LFFieldTypeEnum
低代码表单字段类型枚举,定义 7 种存储类型(STRING/NUMBER/DATE/DATE_TIME/TEXT/BOOLEAN/BLOB)。

### LFFormDataPreProcessor
低代码表单数据预处理器,在保存/读取流程配置时拆分/合并字段元数据。

### LFFormOperationAdaptor
低代码表单操作适配器 SPI 接口,实现此接口可为特定 formCode 的低代码流程定制行为。bean 名必须等于 formCode。

### LfFormManageBizService
低代码表单管理服务,提供独立表单的 CRUD、版本管理、生效切换等功能。

### LfFormWidgetParser
VForm3 表单 JSON 解析器,递归提取字段元数据并写入 `t_bpmn_conf_lf_formdata_field`。

### LFMain
低代码表单运行期主表实体,对应 `t_lf_main` 表。

### LFMainField
低代码表单运行期字段值表实体,对应 `t_lf_main_field` 表。

### LowFlowApprovalService
低代码流程通用服务,通过 `@ActivitiServiceAnno(svcName = LOWFLOW_FORM_CODE)` 注册,作为所有低代码流程的默认实现。

## M

### MainTask(主任务)
当前活跃的审批任务,存储在 `act_ru_task` 表。

### Mapper(MyBatis Mapper)
数据访问层接口,AntFlow 使用 MyBatis-Plus 简化 Mapper 开发。

### MessageSendAdaptor
消息发送适配器抽象类,内置 3 个实现(邮件/短信/App 推送),可通过继承扩展。

### MessageSendTypeEnum
消息发送类型枚举:`EMAIL_TYPE`、`SMS_TYPE`、`APP_MESSAGE_TYPE`、`DING_TALK_TYPE` 等。

### MongoDB
AntFlow 实验性支持的 NoSQL 数据库,需使用副本集模式以支持事务。

### MultiTenant(多租户)
AntFlow 支持的 SaaS 模式,每个租户数据完全隔离,可通过 `antflow.sass.full-sass-mode=true` 开启。

## N

### NodeLabelConstants
节点标签常量,格式 `af_syslabel_*` 或短标识如 `condition_approve_node`。

### NodePropertyEnum
节点属性枚举,定义 15 种审批人来源规则(指定人员/角色/部门/岗位/发起人领导等)。

### NodeTypeEnum
节点类型枚举,定义 12 种节点类型(发起人/审批人/条件/抄送/并行/自动等)。

## O

### OpenAPI(开放 API)
AntFlow 提供给第三方系统接入的 REST API,详见 [REST API 参考](/dev-guide/rest-api)。

### OperationType(操作类型)
审批操作的类型枚举:`1=发起,2=重新提交,3=审批,4=拒绝,5=退回,6=撤销,7=加批,...`。

### OutsideProcess(三方流程)
外部业务方接入的流程,通过 `t_bpmn_conf.is_out_side_process=1` 标识。

## P

### ParallelGateway(并行网关)
BPMN 元素,用于会签/或签。AntFlow 的 `nodeType=7` 转换为 `parallelGateway`。

### PersonnelAdaptor(审批人适配器)
将设计期配置的审批人规则解析为运行期实际 assignee。

### PersonnelEnum
审批人类型枚举,与 `NodePropertyEnum` 一一对应。

### PersonnelType(审批人类型)
审批人来源类型,如指定人员、角色、部门、岗位、发起人领导、表单字段等。

### PreNode(前置节点)
流程中位于当前节点之前的节点,可通过"退回"操作回到前置节点。

### PreviewNode(预览节点)
流程预览时的节点信息,包含节点名称、处理人、条件等。

### ProcessComplete(流程完成)
完成任务并流转到下一节点的方法,核心:`taskService.complete(taskId)`。

### ProcessInstance(流程实例)
一次具体的流程运行,对应 `bpm_business_process` 表的一行记录。

### ProcessNodeJumpCmd
Activiti 自定义命令,用于节点跳转(加签/减签/退回/跳过等)。

### ProcessNodeSubmitBizService
流程节点提交业务服务,提供 `processComplete` 等流转方法。

### ProcessNumber(流程编号)
流程实例的唯一编号,格式如 `LEAVE_WMA-20240724001`。

### ProcessOperationAdaptor
操作适配器接口,每个 `operationType` 对应一个实现,处理一种审批操作。

### ProcessStateEnum
流程状态枚举:`0=未启动,2=运行中,3=已结束,4=已撤销,5=已退回`。

### ProcessVersion(流程版本)
同一流程的多个版本,通过 `bpmn_code` 后缀区分(如 `LEAVE_WMA-00001`、`LEAVE_WMA-00002`)。

## R

### Recall(撤回)
发起人主动撤销已发起的流程,`operationType=6`。

### Redis
推荐使用的缓存组件,用于分布式锁、Session 缓存、字典数据缓存等。

### Refuse(拒绝)
审批操作的一种,`operationType=4`,拒绝并结束流程。

### Resubmit(重新提交)
审批操作的一种,`operationType=2`,审批人退回后,发起人重新提交表单。

### Result(返回结果)
AntFlow 统一的 API 返回格式,包含 `code`、`msg`、`data` 三部分。

### ReturnNode(退回)
审批操作的一种,`operationType=5`,退回到指定的历史节点。

### RoleInfo(角色信息)
角色 VO,包含角色 ID、名称、成员等。

## S

### SaaS(Software as a Service)
软件即服务模式,AntFlow 支持多租户 SaaS 部署,各租户数据完全隔离。

### SchemaUpdate(数据库结构更新)
Activiti 的自动建表/更新表结构功能,AntFlow **必须关闭**(`spring.activiti.database-schema-update=none`)。

### SequenceFlow(顺序流)
BPMN 元素,连接两个节点的箭头,可附加条件表达式。

### SPI(Service Provider Interface)
服务提供者接口,AntFlow 的核心扩展机制,通过实现接口 + Spring `@Service`/`@Primary` 注解覆盖默认实现。

### Starter(Spring Boot Starter)
AntFlow 的 `antflow-spring-boot-starter` 模块,引入一个依赖即可集成全套 AntFlow。

### Submit(提交)
发起人提交表单启动流程的操作,`operationType=1`。

### StartConditionsVo(启动条件 VO)
流程启动时携带的上下文,包含表单数据、发起人、流程变量等。

### StartNode(发起人节点)
流程的起点,`nodeType=1`(NODE_TYPE_START),assignee 固定为发起人。

### StringConstants
字符串常量类,包含权限值 `R/E/H`、`LOWFLOW_FORM_CODE`、`CC_NODE` 等。

## T

### Task(任务)
Activiti 中的用户任务,存储在 `act_ru_task` 表,每个待审批条目对应一个 task。

### TaskMgmtVO
任务管理 VO,包含任务列表、统计信息等。

### Tenant(租户)
SaaS 模式下的独立数据空间,通过 `tenant_id` 字段隔离。

### ThreadLocalContainer
AntFlow 内置的 ThreadLocal 容器,缓存当前请求的 BpmnConf 等上下文,避免重复查询。

### TidbIndex
联合索引,通过 `lf.main.table.count` 配置分表数量。

## U

### UDLFApplyVo
低代码表单通用申请 VO,作为 `LowFlowApprovalService` 的入参,包含 `lfFields`(字段值列表)、`startUserMo`(发起人)等。

### UseExternalForm(使用外部表单)
`BpmnConfFlagsEnum.USE_EXTERNAL_FORM` 标志位(位掩码 64),开启后流程关联多个独立表单而非单一内联表单。

### UserMsgVo
用户消息 VO,包含接收人、消息标题、内容、跳转链接等。

## V

### VForm3
AntFlow 集成的低代码表单设计器,以 UMD 包形式集成,提供 20+ 控件类型。

### VNode(虚拟节点)
AntFlow 创新的节点模式,将 12 种丰富节点类型在运行期统一转换为 Activiti 原生 `userTask`,实现"引擎无关"。

## W

### WorkerThread(工作线程)
Activiti 异步执行器的工作线程,处理定时任务和异步服务任务。

### Workflow(工作流)
业务流程的自动化执行,AntFlow 的核心能力。

### WorkflowEngineAdaptor
工作流引擎适配器,封装 Activiti API,提供引擎无关的接口。

## 章节导航

- [FAQ](/reference/faq) — 常见问答
- [版本变更](/reference/changelog) — 版本历史
- [架构总览](/dev-guide/architecture) — 技术架构
- [Adaptor 适配器模式](/dev-guide/adaptor-pattern) — 核心设计
- [虚拟节点系统](/dev-guide/vnode-system) — VNode 详解
