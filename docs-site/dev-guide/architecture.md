# 架构总览

> AntFlow 是基于 Activiti 5.23 深度改造的企业级低代码工作流引擎。本章从模块划分、分层架构、核心抽象、关键设计决策四个维度全面剖析 AntFlow 的技术架构,为后续章节奠定基础。

## 模块划分

AntFlow 采用 Maven 多模块结构,各模块职责清晰、依赖单向:

<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 920 420" font-family="-apple-system, BlinkMacSystemFont, 'Segoe UI', sans-serif">
  <defs>
    <marker id="arr5" viewBox="0 0 10 10" refX="9" refY="5" markerWidth="6" markerHeight="6" orient="auto"><path d="M0,0 L10,5 L0,10 z" fill="#475569"/></marker>
    <linearGradient id="g1" x1="0" y1="0" x2="0" y2="1"><stop offset="0%" stop-color="#dbeafe"/><stop offset="100%" stop-color="#bfdbfe"/></linearGradient>
    <linearGradient id="g2" x1="0" y1="0" x2="0" y2="1"><stop offset="0%" stop-color="#dcfce7"/><stop offset="100%" stop-color="#bbf7d0"/></linearGradient>
    <linearGradient id="g3" x1="0" y1="0" x2="0" y2="1"><stop offset="0%" stop-color="#fef3c7"/><stop offset="100%" stop-color="#fde68a"/></linearGradient>
    <linearGradient id="g4" x1="0" y1="0" x2="0" y2="1"><stop offset="0%" stop-color="#fce7f3"/><stop offset="100%" stop-color="#fbcfe8"/></linearGradient>
  </defs>

  <!-- 前端层 -->
  <rect x="20" y="20" width="880" height="80" rx="8" fill="url(#g1)" stroke="#3b82f6" stroke-width="2"/>
  <text x="460" y="44" text-anchor="middle" font-size="14" font-weight="700" fill="#1e40af">antflow-vue · 前端层</text>
  <text x="460" y="64" text-anchor="middle" font-size="11" fill="#1e3a8a">Vue 3 + Vite + Element Plus + Pinia + VForm3</text>
  <text x="460" y="84" text-anchor="middle" font-size="10" fill="#1e3a8a">流程设计器 / 任务中心 / 表单设计器 / 监控大屏</text>

  <!-- Starter -->
  <rect x="20" y="120" width="880" height="60" rx="8" fill="url(#g4)" stroke="#db2777" stroke-width="2"/>
  <text x="460" y="144" text-anchor="middle" font-size="14" font-weight="700" fill="#9d174d">antflow-starter · Spring Boot Starter 自动装配层</text>
  <text x="460" y="164" text-anchor="middle" font-size="11" fill="#831843">引入一个依赖即可集成全套 AntFlow · 自动扫描 Mapper / 配置引擎 / 加载 Adaptor</text>

  <!-- Engine -->
  <rect x="20" y="200" width="880" height="120" rx="8" fill="url(#g2)" stroke="#16a34a" stroke-width="2"/>
  <text x="460" y="224" text-anchor="middle" font-size="14" font-weight="700" fill="#155e2f">antflow-engine · 引擎层(核心)</text>
  <text x="460" y="244" text-anchor="middle" font-size="11" fill="#14532d">包含 Activiti 5.23 改造代码 + AntFlow 自研业务流转引擎</text>

  <rect x="40" y="258" width="200" height="48" rx="6" fill="#fff" stroke="#16a34a"/>
  <text x="140" y="278" text-anchor="middle" font-size="11" font-weight="600" fill="#155e2f">adp/ · 适配器层</text>
  <text x="140" y="294" text-anchor="middle" font-size="10" fill="#14532d">processoperation/personneladp/conditionfilter/processnotice</text>

  <rect x="252" y="258" width="200" height="48" rx="6" fill="#fff" stroke="#16a34a"/>
  <text x="352" y="278" text-anchor="middle" font-size="11" font-weight="600" fill="#155e2f">factory/ · 工厂层</text>
  <text x="352" y="294" text-anchor="middle" font-size="10" fill="#14532d">FormFactory / AdaptorFactory / Javassist 动态代理</text>

  <rect x="464" y="258" width="200" height="48" rx="6" fill="#fff" stroke="#16a34a"/>
  <text x="564" y="278" text-anchor="middle" font-size="11" font-weight="600" fill="#155e2f">service/biz/ · 业务服务</text>
  <text x="564" y="294" text-anchor="middle" font-size="10" fill="#14532d">BpmnConfBizService / ProcessApprovalService</text>

  <rect x="676" y="258" width="200" height="48" rx="6" fill="#fff" stroke="#16a34a"/>
  <text x="776" y="278" text-anchor="middle" font-size="11" font-weight="600" fill="#155e2f">controller/ · REST API</text>
  <text x="776" y="294" text-anchor="middle" font-size="10" fill="#14532d">BpmnConfController / InformationTemplateController</text>

  <!-- Base -->
  <rect x="20" y="340" width="430" height="60" rx="8" fill="url(#g3)" stroke="#d97706" stroke-width="2"/>
  <text x="235" y="364" text-anchor="middle" font-size="14" font-weight="700" fill="#92400e">antflow-base · 基础层</text>
  <text x="235" y="384" text-anchor="middle" font-size="11" fill="#78350f">通用工具、异常、枚举、VO、DTO、MyBatis 包装</text>

  <!-- DB -->
  <rect x="470" y="340" width="430" height="60" rx="8" fill="#f1f5f9" stroke="#475569" stroke-width="2"/>
  <text x="685" y="364" text-anchor="middle" font-size="14" font-weight="700" fill="#1e293b">数据库 · MySQL / PostgreSQL / Oracle / 达梦 …</text>
  <text x="685" y="384" text-anchor="middle" font-size="11" fill="#475569">AntFlow 业务表 + Activiti 引擎表(34+12 表)</text>

  <!-- 依赖箭头(从上到下) -->
  <line x1="460" y1="100" x2="460" y2="120" stroke="#475569" stroke-width="2" marker-end="url(#arr5)"/>
  <line x1="460" y1="180" x2="460" y2="200" stroke="#475569" stroke-width="2" marker-end="url(#arr5)"/>
  <line x1="300" y1="320" x2="235" y2="340" stroke="#475569" stroke-width="2" marker-end="url(#arr5)"/>
  <line x1="620" y1="320" x2="685" y2="340" stroke="#475569" stroke-width="2" marker-end="url(#arr5)"/>
</svg>

### 各模块职责

| 模块 | 路径 | 职责 |
|---|---|---|
| **antflow-vue** | `antflow-vue/` | 前端 Vue 3 工程,含流程设计器、表单设计器、任务中心 |
| **antflow-starter** | `antflow-starter/` | Spring Boot Starter,自动装配,业务方仅需引入一个依赖 |
| **antflow-engine** | `antflow-engine/` | 核心引擎层,基于 Activiti 5.23 改造,包含全部业务流转逻辑 |
| **antflow-base** | `antflow-base/` | 基础工具层,通用 VO/DTO/枚举/异常/MyBatis 增强 |

## 分层架构

AntFlow 采用经典的 **适配器+工厂+ AOP** 三层架构,在 Activiti 之上构建了完全独立的业务流转层:

<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 920 460" font-family="-apple-system, BlinkMacSystemFont, 'Segoe UI', sans-serif">
  <defs>
    <marker id="arr6" viewBox="0 0 10 10" refX="9" refY="5" markerWidth="6" markerHeight="6" orient="auto"><path d="M0,0 L10,5 L0,10 z" fill="#475569"/></marker>
  </defs>

  <!-- 表现层 -->
  <rect x="20" y="20" width="880" height="56" rx="8" fill="#dbeafe" stroke="#3b82f6"/>
  <text x="460" y="44" text-anchor="middle" font-size="13" font-weight="700" fill="#1e40af">表现层 · Controller</text>
  <text x="460" y="62" text-anchor="middle" font-size="10" fill="#1e3a8a">BpmnConfController / InformationTemplateController / OutSideAccessController / LowCodeController</text>

  <!-- AOP 切面层 -->
  <rect x="20" y="92" width="880" height="56" rx="8" fill="#fee2e2" stroke="#dc2626"/>
  <text x="460" y="116" text-anchor="middle" font-size="13" font-weight="700" fill="#991b1b">AOP 切面层 · DoButtonOperationAspect</text>
  <text x="460" y="134" text-anchor="middle" font-size="10" fill="#7f1d1d">@Around 拦截 ButtonPreOperationService.buttonsPreOperation · FormFactory.dataFormConversion 反序列化 BusinessDataVo</text>

  <!-- 业务服务层 -->
  <rect x="20" y="164" width="880" height="56" rx="8" fill="#fef3c7" stroke="#d97706"/>
  <text x="460" y="188" text-anchor="middle" font-size="13" font-weight="700" fill="#92400e">业务服务层 · ButtonOperationServiceImpl</text>
  <text x="460" y="206" text-anchor="middle" font-size="10" fill="#78350f">buttonsOperationTransactional(vo) · AdaptorFactory.getProcessOperation(vo) · ProcessorFactory.executePostProcessors(vo)</text>

  <!-- 适配器层 -->
  <rect x="20" y="236" width="880" height="100" rx="8" fill="#dcfce7" stroke="#16a34a"/>
  <text x="460" y="260" text-anchor="middle" font-size="13" font-weight="700" fill="#155e2f">适配器层 · adp/(核心解耦点)</text>

  <rect x="40" y="276" width="200" height="48" rx="6" fill="#fff" stroke="#16a34a"/>
  <text x="140" y="296" text-anchor="middle" font-size="11" font-weight="600" fill="#155e2f">ProcessOperationAdaptor</text>
  <text x="140" y="312" text-anchor="middle" font-size="9" fill="#14532d">30+ 种审批操作实现</text>

  <rect x="252" y="276" width="200" height="48" rx="6" fill="#fff" stroke="#16a34a"/>
  <text x="352" y="296" text-anchor="middle" font-size="11" font-weight="600" fill="#155e2f">PersonnelAdaptor</text>
  <text x="352" y="312" text-anchor="middle" font-size="9" fill="#14532d">15 种审批人来源解析</text>

  <rect x="464" y="276" width="200" height="48" rx="6" fill="#fff" stroke="#16a34a"/>
  <text x="564" y="296" text-anchor="middle" font-size="11" font-weight="600" fill="#155e2f">ConditionJudge</text>
  <text x="564" y="312" text-anchor="middle" font-size="9" fill="#14532d">10+ 种条件评估策略</text>

  <rect x="676" y="276" width="200" height="48" rx="6" fill="#fff" stroke="#16a34a"/>
  <text x="776" y="296" text-anchor="middle" font-size="11" font-weight="600" fill="#155e2f">MessageSendAdaptor</text>
  <text x="776" y="312" text-anchor="middle" font-size="9" fill="#14532d">邮件/短信/App 推送</text>

  <!-- Activiti 引擎层 -->
  <rect x="20" y="356" width="430" height="80" rx="8" fill="#f1f5f9" stroke="#475569"/>
  <text x="235" y="380" text-anchor="middle" font-size="13" font-weight="700" fill="#1e293b">Activiti 5.23 引擎层(改造)</text>
  <text x="235" y="400" text-anchor="middle" font-size="10" fill="#475569">runtimeService / taskService / historyService</text>
  <text x="235" y="416" text-anchor="middle" font-size="10" fill="#475569">+ 自定义 cmd:ProcessNodeJumpCmd 等</text>

  <!-- 持久层 -->
  <rect x="470" y="356" width="430" height="80" rx="8" fill="#fef9c3" stroke="#a16207"/>
  <text x="685" y="380" text-anchor="middle" font-size="13" font-weight="700" fill="#713f12">持久层 · MyBatis-Plus</text>
  <text x="685" y="400" text-anchor="middle" font-size="10" fill="#422006">t_bpmn_conf / t_bpmn_node / bpm_business_process</text>
  <text x="685" y="416" text-anchor="middle" font-size="10" fill="#422006">t_bpm_variable / bpm_verify_info / Activiti 表</text>

  <!-- 箭头 -->
  <line x1="460" y1="76" x2="460" y2="92" stroke="#475569" stroke-width="1.5" marker-end="url(#arr6)"/>
  <line x1="460" y1="148" x2="460" y2="164" stroke="#475569" stroke-width="1.5" marker-end="url(#arr6)"/>
  <line x1="460" y1="220" x2="460" y2="236" stroke="#475569" stroke-width="1.5" marker-end="url(#arr6)"/>
  <line x1="280" y1="336" x2="235" y2="356" stroke="#475569" stroke-width="1.5" marker-end="url(#arr6)"/>
  <line x1="640" y1="336" x2="685" y2="356" stroke="#475569" stroke-width="1.5" marker-end="url(#arr6)"/>
</svg>

## 核心抽象:四大适配器

AntFlow 通过 4 个核心适配器接口,把业务变化点与引擎执行完全解耦:

### 1. ProcessOperationAdaptor(操作适配器)

每个 `operationType` 对应一个实现,处理一种审批操作。位于 [adp/processoperation/](file:///d:/projects/jimuoffice/antflow-engine/src/main/java/org/openoa/engine/bpmnconf/adp/processoperation/):

```java
public interface ProcessOperationAdaptor {
    /** 执行具体操作 */
    void doProcessButton(BusinessDataVo vo);

    /** 返回对应的操作类型枚举 */
    Enum<?> getOperationType();
}
```

已实现 30+ 种操作,详见 [审批操作](/workflow-run/approve) 章节。

### 2. PersonnelAdaptor(审批人适配器)

每个 `nodeProperty` 对应一个实现,把设计期配置的审批人规则解析为运行期实际 assignee。位于 [adp/personneladp/](file:///d:/projects/jimuoffice/antflow-engine/src/main/java/org/openoa/engine/bpmnconf/adp/personneladp/):

```java
public interface PersonnelAdaptor {
    /** 解析节点配置的实际审批人 */
    List<String> getAssigneeList(Map<String, Object> variables, BpmnNode node);

    /** 返回对应的 nodeProperty 枚举 */
    Enum<?> getEnum();
}
```

### 3. ConditionJudge(条件评估器)

每个条件类型对应一个实现,评估节点条件是否满足。位于 [adp/conditionfilter/](file:///d:/projects/jimuoffice/antflow-engine/src/main/java/org/openoa/engine/bpmnconf/adp/conditionfilter/):

```java
public interface ConditionJudge {
    /** 评估条件是否满足 */
    boolean judge(String conditionValue, Object actualValue, Map<String, Object> variables);

    /** 返回对应的条件类型枚举 */
    Enum<?> getEnum();
}
```

### 4. MessageSendAdaptor(消息发送适配器)

每个通知通道对应一个实现,负责实际的消息发送。位于 [adp/processnotice/](file:///d:/projects/jimuoffice/antflow-engine/src/main/java/org/openoa/engine/bpmnconf/adp/processnotice/):

```java
public abstract class AbstractMessageSendAdaptor {
    /** 发送消息 */
    public abstract void sendMessage(List<String> receivers, String content, MessageSendTypeEnum sendType);

    /** 返回对应的通知通道 */
    public abstract MessageSendTypeEnum getSupportType();
}
```

## 工厂层:动态路由

[FormFactory](file:///d:/projects/jimuoffice/antflow-engine/src/main/java/org/openoa/engine/factory/FormFactory.java)、`AdaptorFactory`、`ProcessorFactory` 三个工厂负责运行期动态查找适配器:

<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 920 240" font-family="-apple-system, BlinkMacSystemFont, 'Segoe UI', sans-serif">
  <defs>
    <marker id="arr7" viewBox="0 0 10 10" refX="9" refY="5" markerWidth="6" markerHeight="6" orient="auto"><path d="M0,0 L10,5 L0,10 z" fill="#475569"/></marker>
  </defs>

  <!-- 三个工厂 -->
  <rect x="20" y="20" width="280" height="80" rx="8" fill="#dbeafe" stroke="#3b82f6"/>
  <text x="160" y="44" text-anchor="middle" font-size="13" font-weight="700" fill="#1e40af">FormFactory</text>
  <text x="160" y="64" text-anchor="middle" font-size="10" fill="#1e3a8a">按 formCode 查找 FormAdaptor</text>
  <text x="160" y="80" text-anchor="middle" font-size="10" fill="#1e3a8a">dataFormConversion: Map → BusinessDataVo</text>
  <text x="160" y="94" text-anchor="middle" font-size="10" fill="#1e3a8a">屏蔽 LF / DIY / 外部接入表单差异</text>

  <rect x="320" y="20" width="280" height="80" rx="8" fill="#dcfce7" stroke="#16a34a"/>
  <text x="460" y="44" text-anchor="middle" font-size="13" font-weight="700" fill="#155e2f">AdaptorFactory</text>
  <text x="460" y="64" text-anchor="middle" font-size="10" fill="#14532d">按 operationType 查找 ProcessOperationAdaptor</text>
  <text x="460" y="80" text-anchor="middle" font-size="10" fill="#14532d">按 nodeProperty 查找 PersonnelAdaptor</text>
  <text x="460" y="94" text-anchor="middle" font-size="10" fill="#14532d">按 conditionType 查找 ConditionJudge</text>

  <rect x="620" y="20" width="280" height="80" rx="8" fill="#fef3c7" stroke="#d97706"/>
  <text x="760" y="44" text-anchor="middle" font-size="13" font-weight="700" fill="#92400e">ProcessorFactory</text>
  <text x="760" y="64" text-anchor="middle" font-size="10" fill="#78350f">执行 PostProcessor 链</text>
  <text x="760" y="80" text-anchor="middle" font-size="10" fill="#78350f">消息发送、流程监控、审计日志</text>
  <text x="760" y="94" text-anchor="middle" font-size="10" fill="#78350f">按 Order 排序执行</text>

  <!-- 实现机制 -->
  <rect x="20" y="130" width="880" height="100" rx="8" fill="#f8fafc" stroke="#94a3b8"/>
  <text x="40" y="152" font-size="12" font-weight="700" fill="#1e293b">实现机制</text>
  <text x="40" y="174" font-size="11" fill="#475569">① Spring 启动时扫描所有 @Component 注解的 Adaptor 实现,缓存到 Map&lt;Enum, Adaptor&gt;</text>
  <text x="40" y="194" font-size="11" fill="#475569">② 运行期通过 Enum key O(1) 查找对应实现</text>
  <text x="40" y="214" font-size="11" fill="#475569">③ Javassist 动态代理生成 ButtonPreOperationService 空实现,作为 AOP 切点(无需硬编码接口)</text>

  <!-- 箭头 -->
  <line x1="160" y1="100" x2="160" y2="130" stroke="#475569" stroke-width="1" marker-end="url(#arr7)"/>
  <line x1="460" y1="100" x2="460" y2="130" stroke="#475569" stroke-width="1" marker-end="url(#arr7)"/>
  <line x1="760" y1="100" x2="760" y2="130" stroke="#475569" stroke-width="1" marker-end="url(#arr7)"/>
</svg>

## 关键设计决策

### 决策 1:虚拟节点(VNode)模式

AntFlow 全网首创的 **虚拟节点模式**,把 AntFlow 自研节点类型(条件、抄送、自动节点、条件审批、条件抄送等)在运行期转换为 Activiti 原生支持的审批人节点(nodeType=4)。这样:

- 流程定义阶段:支持丰富的节点类型
- 引擎执行阶段:只看到统一的审批人节点,无需改造 Activiti 内核

详见 [虚拟节点系统](/dev-guide/vnode-system)。

### 决策 2:JSON-first 配置

AntFlow 把所有流程配置都以 JSON 形式存储,而非 Activiti 原生的 BPMN XML:

| 配置 | 存储位置 | 字段 |
|---|---|---|
| 流程级配置 | `t_bpmn_conf.conf_config_json` | `BpmnConfConfigJson` |
| 节点级配置 | `t_bpmn_node.config_json` | `BpmnNodeConfigJson` |
| 节点连线 | `t_bpmn_node_to` 表 | 独立表存储 |
| 启动表单数据 | `t_bpm_variable.process_start_conditions` | JSON |

JSON-first 的好处:

- 前端易解析、易可视化
- 字段级扩展灵活(直接加 JSON 属性,无需 DDL)
- 跨数据库兼容

### 决策 3:不走 Drools,自研条件引擎

AntFlow 没有引入 Drools 等重量级规则引擎,而是基于策略模式自研轻量级条件评估:

- 双层 AND/OR 结构:外层 AND,内层 OR
- 每个条件由对应 `ConditionJudge` 实现,扩展只需新增实现类
- 支持 LF 字段、业务字段、JUEL/SpEL 表达式等多种条件类型

详见 [扩展条件规则](/dev-guide/extend-condition)。

### 决策 4:不接管用户系统,适配器接入

AntFlow 不维护 Activiti 的用户表、群组表、成员关系表,而是通过 `PersonnelAdaptor` 适配器接入企业现有系统的用户、角色:

```java
// 业务方只需实现一个接口
public interface PersonnelAdaptor {
    List<String> getAssigneeList(Map<String, Object> variables, BpmnNode node);
    Enum<?> getEnum();
}
```

详见 [扩展审批人来源](/dev-guide/extend-approver)。

### 决策 5:Spring Boot Starter 自动装配

通过 `antflow-starter` 模块,业务方引入一个依赖即可集成全套 AntFlow:

```xml
<dependency>
    <groupId>org.openoa</groupId>
    <artifactId>antflow-spring-boot-starter</artifactId>
    <version>1.0.0</version>
</dependency>
```

Starter 自动完成:

- Mapper 扫描
- Activiti 引擎配置
- 适配器组件扫描
- 多数据库方言适配

详见 [集成现有系统](/dev-guide/integrate-existing)。

## 性能与扩展性

| 维度 | 设计 | 实测 |
|---|---|---|
| 单流程节点数 | 无硬限制(受 JSON 字段长度限制) | 100+ 节点流畅运行 |
| 并发审批 | Activiti 原生 + 业务表乐观锁 | 千级 TPS |
| 消息发送 | @Async 异步,不阻塞主流程 | 不影响审批响应时间 |
| 适配器查找 | Map O(1) | 微秒级 |
| 流程预览 | 服务端递归计算节点链路 | 100 节点 <200ms |

## 小结

- AntFlow 采用 **Vue 3 + Spring Boot Starter + Engine + Base** 四模块结构,职责清晰、依赖单向
- 核心解耦点是 **适配器层**,通过 4 个核心适配器接口隔离业务变化与引擎执行
- 关键设计决策:VNode 模式、JSON-first、自研条件引擎、不接管用户系统、Spring Boot Starter
- 工厂层通过 Spring 组件扫描 + Map 缓存实现 O(1) 适配器查找
- AOP 拦截器 `DoButtonOperationAspect` 是所有审批操作的统一入口,屏蔽表单差异

下一节 [Adaptor 适配器模式](/dev-guide/adaptor-pattern) 深入剖析适配器的设计与实现。
