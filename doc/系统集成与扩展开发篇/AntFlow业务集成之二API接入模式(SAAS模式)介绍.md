# AntFlow集成模式之外部接入模式

### 一、API接入模式说明

API接入模式是指  **AntFlow 以独立服务方式部署** ，业务系统通过 **REST API 调用流程引擎能力** 来完成流程发起、审批、查询等操作。

在这种模式下：

* AntFlow 与业务系统 **运行在不同进程甚至不同服务器**
* 业务系统通过 **HTTP / REST API** 调用流程能力
* 流程引擎成为一个 **独立的流程服务中心**

典型调用流程如下：

```
业务系统
   │
   │ REST API
   ▼
AntFlow流程服务
   │
   ▼
流程实例运行
```

# 二、API接入的主要优点

### 1️⃣ 对业务系统侵入最小

API模式  **几乎不需要修改原有系统架构** 。

企业已有系统只需要：

* 调用流程 **发起接口**
* 查询 **流程状态**
* 获取 **待办任务**

即可完成流程集成。

优势：

* 不需要引入流程引擎jar包
* 不影响现有系统架构
* 改造成本低

适合场景：

* 已上线的大型系统
* 不方便修改架构的系统
* 异构系统集成

---

### 2️⃣ 支持多语言、多技术栈

因为使用  **HTTP API** ，所以任何技术栈都可以接入，例如：

* Java
* .NET
* Python
* Go
* Node.js
* PHP

例如公司内部系统：

<pre class="overflow-visible! px-0!" data-start="678" data-end="725"><div class="relative w-full mt-4 mb-1"><div class=""><div class="relative"><div class="h-full min-h-0 min-w-0"><div class="h-full min-h-0 min-w-0"><div class="border border-token-border-light border-radius-3xl corner-superellipse/1.1 rounded-3xl"><div class="h-full w-full border-radius-3xl bg-token-bg-elevated-secondary corner-superellipse/1.1 overflow-clip rounded-3xl lxnfua_clipPathFallback"><div class="pointer-events-none absolute end-1.5 top-1 z-2 md:end-2 md:top-1"></div><div class="pt-3"><div class="relative z-0 flex max-w-full"><div id="code-block-viewer" dir="ltr" class="q9tKkq_viewer cm-editor z-10 light:cm-light dark:cm-light flex h-full w-full flex-col items-stretch ͼ5 ͼj"><div class="cm-scroller"><div class="cm-content q9tKkq_readonly"><span>CRM (Java)</span><br/><span>ERP (.NET)</span><br/><span>MES (C#)</span><br/><span>WMS (Go)</span></div></div></div></div></div></div></div></div></div><div class=""><div class=""></div></div></div></div></div></pre>

都可以统一调用 AntFlow。

### 3️⃣ 流程能力统一平台化

API模式可以把  **流程能力沉淀为企业级基础服务** 。

企业架构变为：

```
          ┌─────────────┐
          │  AntFlow    │
          │ 流程服务中心 │
          └──────┬──────┘
                 │API
     ┌───────────┼───────────┐
     ▼           ▼           ▼
   CRM         ERP         MES
```

好处：

流程统一管理

流程规则统一

运维集中化

避免各系统重复开发流程

### 5️⃣ 非常适合跨系统流程

很多企业流程会跨系统，例如：

```
OA发起流程
   ↓
ERP审批
   ↓
MES执行
   ↓
CRM同步
```

PI模式天然支持这种  **跨系统流程编排** 。

# 三、API模式需要注意的问题

虽然优点很多，但API模式也有典型挑战：

### 1 数据一致性问题

因为是  **跨服务调用** ：

<pre class="overflow-visible! px-0!" data-start="1382" data-end="1411"><div class="relative w-full mt-4 mb-1"><div class=""><div class="relative"><div class="h-full min-h-0 min-w-0"><div class="h-full min-h-0 min-w-0"><div class="border border-token-border-light border-radius-3xl corner-superellipse/1.1 rounded-3xl"><div class="h-full w-full border-radius-3xl bg-token-bg-elevated-secondary corner-superellipse/1.1 overflow-clip rounded-3xl lxnfua_clipPathFallback"><div class="pointer-events-none absolute end-1.5 top-1 z-2 md:end-2 md:top-1"></div><div class="pt-3"><div class="relative z-0 flex max-w-full"><div id="code-block-viewer" dir="ltr" class="q9tKkq_viewer cm-editor z-10 light:cm-light dark:cm-light flex h-full w-full flex-col items-stretch ͼ5 ͼj"><div class="cm-scroller"><div class="cm-content q9tKkq_readonly"><span>业务系统事务</span><br/><span>      +</span><br/><span>流程服务事务</span></div></div></div></div></div></div></div></div></div><div class=""><div class=""></div></div></div></div></div></pre>

可能产生一致性问题。

常见解决方案：

* 分布式事务（Seata）
* 事务消息
* 补偿机制（Saga）

---

### 2 网络调用开销

API调用相比本地调用：

* 多一次网络通信
* 需要处理超时与重试

# 四、典型企业使用方式

很多企业会采用如下架构：

```
                AntFlow流程中心
                      │
                 REST API
                      │
     ┌───────┬────────────┬────────────┐
     ▼       ▼            ▼            ▼
    OA      CRM          ERP          MES
```

各系统只需要：

调用流程发起API

审批、流程流转全部在流程系统中完成。

这样：

每个系统无需嵌入流程引擎

运维成本大幅降低

开发复杂度降低

✅ 总结

API接入模式的核心价值是：

低侵入 + 高复用 + 平台化

适合：

多系统企业

异构技术栈

希望构建统一流程平台
