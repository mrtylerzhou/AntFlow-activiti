# AntFlow 项目知识库

## 项目概述
- **项目名**: AntFlow（蚂蚁工作流）
- **性质**: 开源工作流引擎框架
- **GroupId**: `io.github.mrtylerzhou`
- **版本**: 1.7.0
- **根目录**: `/d/projects/jimuoffice`
- **核心特性**: 基于 Activiti 5.23 封装，通过 Adaptor 模式实现业务与引擎解耦，支持"零代码"配置流程

---

## 技术栈

### 后端
| 技术 | 版本 |
|------|------|
| Java | 8（兼容至 21） |
| Spring Boot | 2.7.17 |
| Activiti（流程引擎） | 5.23 |
| MyBatis Plus | 3.5.1 |
| Drools（规则引擎） | 6.5.0.Final |
| MySQL | 8.0 / 5.7 |
| Druid（连接池） | 1.1.17 |
| Fastjson2 | 2.0.53 |
| Joda-Time | 2.9.9 |
| pinyin4j | 2.5.0 |
| 构建工具 | Maven（mvnw） |

### 前端
| 技术 | 版本 |
|------|------|
| Vue | 3 |
| Vite | - |
| Element Plus | 2.10.7 |
| Pinia | 3.0.2 |
| Axios | 1.9.0 |
| ECharts | 5.6.0 |

---

## Maven 多模块结构

```
antflow/                          # 根模块（聚合 POM）
├── antflow-base/                 # 基础层：通用工具、常量、DTO、接口定义
├── antflow-engine/               # 核心引擎层：流程引擎所有业务逻辑
├── antflow-spring-boot-starter/  # 自动装配模块，对外集成入口
├── antflow-web/                  # 演示 Web 应用（业务示例）
├── antflow-vue/                  # Vue 3 前端管理界面
├── script/                       # 数据库初始化脚本
├── doc/                          # 项目文档
└── web/                          # 静态资源/构建产物目录
```

---

## 模块详解

### `antflow-base` — 基础模块
```
src/main/java/org/openoa/base/
├── adp/          # 基础适配器接口（FilterDataAdaptor、OrderedBean）
├── aspect/       # AOP 切面
├── conf/         # 基础配置类
├── constant/     # 全局常量
├── dto/          # 数据传输对象
├── entity/       # 基础实体
├── exception/    # 自定义异常体系
├── interf/       # 公共接口定义
├── listener/     # 基础监听器
├── mapper/       # 基础 Mapper
├── service/      # 基础 Service 接口
├── util/         # 工具类
└── vo/           # 基础 VO
```

### `antflow-engine` — 核心引擎模块（最重要）
```
src/main/java/org/openoa/engine/
├── bpmnconf/
│   ├── activitilistener/   # Activiti 任务/执行监听器
│   ├── actservice/         # Activiti 原生服务封装
│   ├── adp/                # 适配器层（核心）
│   │   ├── bpmnnodeadp/        # 节点审批人适配器
│   │   ├── conditionfilter/    # 条件过滤器（Drools 规则）
│   │   ├── formatter/          # 数据格式化
│   │   ├── orderedsignadp/     # 有序会签适配器
│   │   ├── personnelAdp/       # 人员信息适配器
│   │   ├── processcallback/    # 流程回调适配器
│   │   ├── processnotice/      # 通知适配器
│   │   └── processoperation/   # 操作处理器
│   ├── cmd/                # Activiti Command 扩展
│   ├── common/             # 引擎公共组件
│   ├── constant/           # 引擎常量
│   ├── controller/         # REST API 控制器（见下方 API 章节）
│   ├── mapper/             # 引擎数据访问层
│   └── service/
│       ├── biz/            # 核心业务服务（50+ 实现类）
│       ├── flowcontrol/    # 流程流转控制（加签/回退/跳转）
│       ├── impl/           # 服务实现
│       ├── interf/         # 服务接口
│       └── processor/      # 流程处理器
├── conf/                   # 引擎配置（Activiti、Drools 等）
├── factory/                # Bean 工厂
├── lowflow/                # 低代码流程支持
├── script/                 # 脚本执行相关
├── utils/                  # 引擎工具类
└── vo/                     # 引擎 VO
```

### `antflow-web` — 演示应用模块
```
src/main/java/org/openoa/
├── AntFlowApplication.java   # 应用入口（主启动类）
├── common/                   # 公共组件
├── controller/               # 业务示例 Controller
│   ├── BaseController.java
│   └── IndexController.java
├── entity/                   # 业务实体（请假、采购、报销等表单）
├── handler/                  # 业务处理器
├── mapper/                   # 业务数据访问
├── service/                  # 业务服务
└── vo/                       # 业务 VO
配置文件: src/main/resources/
├── application.properties          # 主配置（引用 Profile）
├── application-dev.properties      # 开发环境
├── application-local.properties    # 本地环境
├── application-uat.properties      # UAT 环境
├── application-pro.properties      # 生产环境
└── logback-spring.xml              # 日志配置
```

### `antflow-vue` — 前端模块
```
src/
├── api/
│   ├── system/          # 系统管理 API
│   └── workflow/        # 工作流 API（index.js、flowMsgApi.js、outsideApi.js、lowcodeApi.js）
├── components/          # 全局组件库（分页、文件上传、图表等）
├── views/
│   ├── workflow/
│   │   ├── flowDesign/      # 流程设计器
│   │   ├── flowList/        # 流程列表
│   │   ├── flowTask/        # 任务管理
│   │   ├── taskCenter/      # 任务中心
│   │   ├── startFlow/       # 发起流程
│   │   ├── flowCategory/    # 流程分类
│   │   ├── flowMsg/         # 流程消息
│   │   ├── flowPreview/     # 流程预览
│   │   ├── flowPrint/       # 流程打印
│   │   ├── flowDevOps/      # 流程运维
│   │   ├── outsideMgt/      # 外部接入管理
│   │   └── startOutside/    # 外部发起
│   └── system/          # 系统管理页面
├── store/               # Pinia 状态管理
├── router/              # 路由配置
└── layout/              # 布局组件
```

---

## REST API 接口结构

| Controller | 路径前缀 | 职责 |
|------------|---------|------|
| `BpmnConfController` | `/bpmnConf/*` | 流程定义、节点属性、审批权限 CRUD |
| `BpmnBusinessController` | `/bpmnBusiness/*` | 提交申请、保存草稿、撤回 |
| `BpmProcessControlController` | `/processControl/*` | 审核/驳回/加签/转办 |
| `BpmBusinessDraftController` | `/draft/*` | 草稿管理 |
| `LowCodeFlowController` | `/lowcode/*` | 低代码流程接入 |
| `LowFlowBusinessController` | `/lowFlowBiz/*` | 低代码业务 |
| `OutSideBpmAccessController` | `/outside/api/*` | 三方系统 Open API |
| `OutSideBpmBusinessController` | `/outside/biz/*` | 外部业务接入 |
| `InformationTemplateController` | `/infoTemplate/*` | 通知模板管理 |
| `UserController` | `/user/*` | 用户相关 |
| `SysVersionController` | `/version/*` | 版本信息 |

---

## 数据库脚本

位于 `script/` 目录：
- `act_init_db.sql` — Activiti 原生表结构（25 张表）
- `bpm_init_db.sql` — AntFlow 扩展业务表（流程配置、审批记录、模板等）
- `bpm_init_db_data.sql` — 初始化数据

---

## 关键配置项（application.properties）

```properties
# 激活环境 Profile
spring.profiles.active=@activatedProperties@

# Activiti 配置
spring.activiti.check-process-definitions=false
spring.activiti.database-schema-update=none

# SaaS 多租户模式（完全独立租户开启）
antflow.sass.full-sass-mode=false

# 员工表映射配置（适配不同系统的用户表）
antflow.common.empTable.empTblName=t_user
antflow.common.empTable.idField=id
antflow.common.empTable.nameField=user_name

# 包扫描
antflow.common.scan-packages=org.openoa,com.package

# 邮件通知
message.email.host=smtp.163.com
message.email.account=antflow@163.com
```

---

## 核心架构模式

### Adaptor 模式（最关键设计）
AntFlow 通过适配器将业务逻辑与 Activiti 引擎彻底解耦：
- **`bpmnnodeadp/`** — 节点审批人适配：不同节点类型（发起人上级、指定人员、角色等）对应不同 Adaptor 实现
- **`conditionfilter/`** — 条件过滤：使用 Drools 规则引擎处理复杂条件判断
- **`processoperation/`** — 操作处理器：封装审批、加签、转办等操作
- **`personnelAdp/`** — 人员信息适配：对接不同系统的用户/组织数据

### 流程流转控制（`flowcontrol/`）
支持高级流程操作：
- 动态跳转（`BpmnModifier`）
- 加签（`MultiInstanceSignOffService`）
- 回退（`GeneralRejectManagerImpl`）
- 任务分配链（`TaskAssignmentHandlerChain`）

### 核心启动类
- `BpmnCreateBpmnAndStartImpl` — 流程创建与启动的核心入口

---

## 开发环境说明

- **后端启动**: 运行 `AntFlowApplication.java`，需配置本地数据库并激活对应 Profile（dev/local）
- **前端启动**: `antflow-vue/` 目录下执行 `npm install && npm run dev`
- **前端配置**: `antflow-vue/vite.config.js` 和 `.env.*` 文件
- **数据库初始化**: 依次执行 `script/` 下三个 SQL 文件

---

## 项目扩展点

1. **自定义审批人来源** → 实现 `bpmnnodeadp/` 中对应 Adaptor 接口
2. **自定义条件规则** → 扩展 `conditionfilter/` 中的 Drools 规则或 Java 实现
3. **自定义通知渠道** → 实现 `processnotice/` 中的通知适配器
4. **三方系统接入** → 使用 `/outside/api/*` Open API 或实现回调接口
5. **低代码接入** → 使用 `antflow-spring-boot-starter` 自动装配快速集成

---

## BPMN 配置表精简重构（JSON-first 读取策略）

### 背景
原有 26 张 `t_bpmn_*` 配置子表，每次读取流程配置需要大量 JOIN 查询。重构方案：将子表数据合并为 JSON 字段存储在两张主表上，读取时优先从 JSON 取值，DB 作为 fallback。

### 存储结构
- **`t_bpmn_conf.conf_config_json`** — 流程级配置（视图按钮、通知模板等）
- **`t_bpmn_node.node_config_json`** — 节点级配置（审批人、条件、按钮、模板等）

JSON 结构定义在 `antflow-base/.../entity/jsonconf/` 包下：
- `BpmnConfConfigJson` — 流程级 JSON 顶层结构
- `BpmnNodeConfigJson` — 节点级 JSON 顶层结构，包含：
  - `approverConf`（`BpmnNodeApproverConfJson`）— 12 种审批人配置
  - `conditionsConf`（`BpmnNodeConditionsConfJson`）— 条件配置
  - `buttonSignConf`（`BpmnNodeButtonSignConfJson`）— 按钮/签收/标签/加签
  - `templateConf`（`BpmnNodeTemplateConfJson`）— 通知模板/催办
  - `lowCodeConf`（`BpmnNodeLowCodeConfJson`）— 低代码字段权限

### 读写策略

**写路径**（`/bpmnConf/edit` → `BpmnConfController#edit`）：
- Adaptor 的 `editBpmnNode()` 仍写入 DB 子表（保持兼容）
- `BpmnConfBizServiceImpl.buildNodeConfigJson()` 在所有 Adaptor 写完后，从 DB 读取数据构建 JSON 并写入 `node_config_json`
- 使用 `BpmnNodeConfigHolder` 静态方法构建各配置段
- 使用 `BpmnConfConfigHolder` 构建流程级配置

**读路径**（`/bpmnConf/process/buttonsOperation` → `BpmnConfBizServiceImpl.detail()`）：
- `getBpmnNodeVoList()` 检查所有节点是否都有 `node_config_json`，若是则走 JSON 路径
- `getBpmnNodeVoFromJson()` 解析 JSON，直接填充按钮/模板/催办/签收/字段权限等
- 调用 Adaptor 的 `formatToBpmnNodeVo()`，Adaptor 内部优先从 `nodeConfigJsonObj` 读取，DB 作为 fallback
- 流程级读取同理：`getBpmnConfVo()` 优先从 `conf_config_json` 读取

### 已完成 JSON-first 读取的 Adaptor（共 14 个）

| Adaptor | 配置类型 | JSON 路径 |
|---------|---------|-----------|
| `NodePropertyPersonnelAdp` | 指定人员审批 | `approverConf.personnelConf` |
| `NodePropertyRoleAdp` | 指定角色审批 | `approverConf.roleConfList[]` |
| `NodePropertyLoopAdp` | 层层审批 | `approverConf.loopConf` |
| `NodePropertyLevelAdp` | 指定层级审批 | `approverConf.assignLevelConf` |
| `NodePropertyHrbpAdp` | HRBP 审批 | `approverConf.hrbpConf` |
| `NodePropertyCustomizeAdp` | 自选审批人 | `approverConf.customizeConf` |
| `NodePropertyUDRAdp` | 自定义规则审批 | `approverConf.udrConfList[]` |
| `NodePropertyFormRelatedAdp` | 表单关联用户审批 | `approverConf.formRelatedUserConfList[]` |
| `NodePropertyOutSideAccessAdp` | 外部接入审批 | `approverConf.outSideAccessConf` |
| `NodePropertyBusinessTableAdp` | 关联业务表审批 | `approverConf.businessTableConf` |
| `AbstractAdditionSignNodeAdp` | 额外加签 | `buttonSignConf.additionalSignConfList[]` |
| `NodeTypeConditionsAdp` | 条件节点 | `conditionsConf.conditionGroups[].extJson` |
| `NodeTypeOutSideConditionsAdp` | 外部条件节点 | `conditionsConf.outSideConditionId` |

### 条件表特殊处理
`t_bpmn_node_conditions_conf.extJson` 存储了完整的 Vue3 模型（`List<List<BpmnNodeConditionsConfVueVo>>`），包含 `optType`（比较符）、`zdy1`（值）、`condGroup`（分组）等全部运行时所需字段。因此 `t_bpmn_node_conditions_param_conf` 完全冗余：
- **读路径**：`NodeTypeConditionsAdp.formatFromJson()` 直接从 Vue3 模型重建运行时字段
- **写路径**：`editBpmnNode()` 不再写入 param conf 表（`operator` 字段为历史遗留，可从 `optType` 推导）
- **运行时检查**：`OutSideBpmConditionsTemplateBizServiceImpl.templateIsUsed()` 从 `extJson` 中 `columnId=9999` 的条目读取模板标识
- `t_bpmn_node_conditions_param_conf` 可安全删除

### 关键工具类
- `JsonConfUtil` — JSON 序列化/反序列化工具
- `BpmnNodeConfigHolder` — 写路径：从 BpmnNodeVo 构建 JSON
- `BpmnConfConfigHolder` — 写路径：从 BpmnConfVo 构建 JSON
- `BpmnConfNodePropertyConverter` — Vue3 模型与 BaseVo 互转（`fromVue3Model`/`toVue3Model`）

### 待完成
- 数据迁移脚本：将现有子表数据回填到 JSON 字段
- 详细表清单见 `doc/tables_to_drop.md`
