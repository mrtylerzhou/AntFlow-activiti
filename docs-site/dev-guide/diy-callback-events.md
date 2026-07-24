# DIY 流程回调与事件机制

> AntFlow 在流程生命周期的各个关键节点提供了**三类回调/事件机制**：DIY 表单生命周期回调、MQ 事件总线、三方系统 HTTP 回调。本章基于源码完整解析这三条链路。

## 一、回调机制全景图

<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 920 540" font-family="-apple-system, BlinkMacSystemFont, 'Segoe UI', sans-serif">
  <defs>
    <marker id="arr" viewBox="0 0 10 10" refX="9" refY="5" markerWidth="6" markerHeight="6" orient="auto"><path d="M 0 0 L 10 5 L 0 10 z" fill="#475569"/></marker>
    <linearGradient id="g1" x1="0" y1="0" x2="0" y2="1"><stop offset="0%" stop-color="#dbeafe"/><stop offset="100%" stop-color="#bfdbfe"/></linearGradient>
    <linearGradient id="g2" x1="0" y1="0" x2="0" y2="1"><stop offset="0%" stop-color="#dcfce7"/><stop offset="100%" stop-color="#bbf7d0"/></linearGradient>
    <linearGradient id="g3" x1="0" y1="0" x2="0" y2="1"><stop offset="0%" stop-color="#fef3c7"/><stop offset="100%" stop-color="#fde68a"/></linearGradient>
  </defs>

  <!-- 标题 -->
  <text x="460" y="28" text-anchor="middle" font-size="16" font-weight="700" fill="#1e293b">AntFlow 三路回调机制</text>

  <!-- 通道1: FormOperationAdaptor -->
  <rect x="20" y="50" width="280" height="440" rx="10" fill="url(#g1)" stroke="#3b82f6" stroke-width="1.5"/>
  <text x="160" y="75" text-anchor="middle" font-size="13" font-weight="700" fill="#1e40af">通道 1: DIY 表单回调</text>
  <text x="160" y="94" text-anchor="middle" font-size="10" fill="#3b82f6">FormOperationAdaptor 接口</text>

  <rect x="35" y="108" width="250" height="36" rx="5" fill="#fff" stroke="#93c5fd"/>
  <text x="160" y="130" text-anchor="middle" font-size="10" fill="#1e293b">submitData()       — 提交流程</text>
  <rect x="35" y="150" width="250" height="36" rx="5" fill="#fff" stroke="#93c5fd"/>
  <text x="160" y="172" text-anchor="middle" font-size="10" fill="#1e293b">consentData()      — 审批同意</text>
  <rect x="35" y="192" width="250" height="36" rx="5" fill="#fff" stroke="#93c5fd"/>
  <text x="160" y="214" text-anchor="middle" font-size="10" fill="#1e293b">backToModifyData() — 退回修改</text>
  <rect x="35" y="234" width="250" height="36" rx="5" fill="#fff" stroke="#93c5fd"/>
  <text x="160" y="256" text-anchor="middle" font-size="10" fill="#1e293b">cancellationData() — 作废处理</text>
  <rect x="35" y="276" width="250" height="36" rx="5" fill="#fff" stroke="#93c5fd"/>
  <text x="160" y="298" text-anchor="middle" font-size="10" fill="#1e293b">queryData()        — 查业务数据</text>
  <rect x="35" y="318" width="250" height="36" rx="5" fill="#fff" stroke="#93c5fd"/>
  <text x="160" y="340" text-anchor="middle" font-size="10" fill="#1e293b">onProcessRecover() — 恢复回调</text>
  <rect x="35" y="360" width="250" height="36" rx="5" fill="#fff" stroke="#93c5fd"/>
  <text x="160" y="382" text-anchor="middle" font-size="10" fill="#1e293b">finishData()       — 流程结束</text>
  <rect x="35" y="402" width="250" height="36" rx="5" fill="#e0e7ff" stroke="#6366f1"/>
  <text x="160" y="424" text-anchor="middle" font-size="10" font-weight="600" fill="#1e40af">+ 3 个 methodSignature 方法</text>

  <text x="160" y="460" text-anchor="middle" font-size="10" fill="#64748b">同步执行 · 顺序回调</text>
  <text x="160" y="478" text-anchor="middle" font-size="10" fill="#64748b">业务 → antflow-engine</text>

  <!-- 通道2: MQ事件总线 -->
  <rect x="320" y="50" width="280" height="440" rx="10" fill="url(#g2)" stroke="#16a34a" stroke-width="1.5"/>
  <text x="460" y="75" text-anchor="middle" font-size="13" font-weight="700" fill="#14532d">通道 2: MQ 事件总线</text>
  <text x="460" y="94" text-anchor="middle" font-size="10" fill="#16a34a">BusinessCallBackFactory</text>

  <rect x="335" y="108" width="250" height="36" rx="5" fill="#fff" stroke="#86efac"/>
  <text x="460" y="130" text-anchor="middle" font-size="10" fill="#1e293b">流程提交 → PROCESS_SUBMIT</text>
  <rect x="335" y="150" width="250" height="36" rx="5" fill="#fff" stroke="#86efac"/>
  <text x="460" y="172" text-anchor="middle" font-size="10" fill="#1e293b">审批同意 → PROCESS_APPROVE</text>
  <rect x="335" y="192" width="250" height="36" rx="5" fill="#fff" stroke="#86efac"/>
  <text x="460" y="214" text-anchor="middle" font-size="10" fill="#1e293b">审批拒绝 → PROCESS_NOT_APPROVE</text>
  <rect x="335" y="234" width="250" height="36" rx="5" fill="#fff" stroke="#86efac"/>
  <text x="460" y="256" text-anchor="middle" font-size="10" fill="#1e293b">流程作废 → PROCESS_ABANDON</text>
  <rect x="335" y="276" width="250" height="36" rx="5" fill="#fff" stroke="#86efac"/>
  <text x="460" y="298" text-anchor="middle" font-size="10" fill="#1e293b">流程结束 → PROCESS_FINISH</text>
  <rect x="335" y="318" width="250" height="36" rx="5" fill="#fff" stroke="#86efac"/>
  <text x="460" y="340" text-anchor="middle" font-size="10" fill="#1e293b">转办 → PROCESS_UNDERTAKE</text>
  <rect x="335" y="360" width="250" height="36" rx="5" fill="#fff" stroke="#86efac"/>
  <text x="460" y="382" text-anchor="middle" font-size="10" fill="#1e293b">回溯同步 → HISTORY_SYNC</text>

  <rect x="335" y="408" width="250" height="60" rx="5" fill="#dcfce7" stroke="#16a34a"/>
  <text x="460" y="428" text-anchor="middle" font-size="10" fill="#14532d">MqProcessEventVo →</text>
  <text x="460" y="446" text-anchor="middle" font-size="10" fill="#14532d">MsgTopics.WORKFLOW_EVENT_PUSH</text>
  <text x="460" y="462" text-anchor="middle" font-size="9" fill="#64748b">(oa_workflow_event_push)</text>

  <text x="460" y="485" text-anchor="middle" font-size="10" fill="#64748b">异步发布 · 重试机制</text>

  <!-- 通道3: HTTP回调 -->
  <rect x="620" y="50" width="280" height="440" rx="10" fill="url(#g3)" stroke="#d97706" stroke-width="1.5"/>
  <text x="760" y="75" text-anchor="middle" font-size="13" font-weight="700" fill="#92400e">通道 3: 三方 HTTP 回调</text>
  <text x="760" y="94" text-anchor="middle" font-size="10" fill="#d97706">ThirdPartyCallbackFactory</text>

  <rect x="635" y="108" width="250" height="36" rx="5" fill="#fff" stroke="#fbbf24"/>
  <text x="760" y="130" text-anchor="middle" font-size="10" fill="#1e293b">PROC_STARTED_CALL_BACK 发起完成</text>
  <rect x="635" y="150" width="250" height="36" rx="5" fill="#fff" stroke="#fbbf24"/>
  <text x="760" y="172" text-anchor="middle" font-size="10" fill="#1e293b">PROC_COMMIT_CALL_BACK 流转完成</text>
  <rect x="635" y="192" width="250" height="36" rx="5" fill="#fff" stroke="#fbbf24"/>
  <text x="760" y="214" text-anchor="middle" font-size="10" fill="#1e293b">PROC_END_CALL_BACK     终止操作</text>
  <rect x="635" y="234" width="250" height="36" rx="5" fill="#fff" stroke="#fbbf24"/>
  <text x="760" y="256" text-anchor="middle" font-size="10" fill="#1e293b">PROC_FINISH_CALL_BACK  完成操作</text>
  <rect x="635" y="276" width="250" height="36" rx="5" fill="#fff" stroke="#fbbf24"/>
  <text x="760" y="298" text-anchor="middle" font-size="10" fill="#1e293b">PROC_CONDITION_CALL_BACK 条件判断</text>
  <rect x="635" y="318" width="250" height="36" rx="5" fill="#fff" stroke="#fbbf24"/>
  <text x="760" y="340" text-anchor="middle" font-size="10" fill="#1e293b">CONF_CONDITION_CALL_BACK 配置校验</text>

  <rect x="635" y="370" width="250" height="50" rx="5" fill="#fef3c7" stroke="#d97706"/>
  <text x="760" y="390" text-anchor="middle" font-size="10" fill="#92400e">HTTP POST + MD5签名</text>
  <text x="760" y="408" text-anchor="middle" font-size="10" fill="#92400e">api-client-id / api-workflow-sign</text>

  <rect x="635" y="435" width="250" height="45" rx="5" fill="#fff" stroke="#fbbf24"/>
  <text x="760" y="455" text-anchor="middle" font-size="9" fill="#475569">CallbackReqVo</text>
  <text x="760" y="470" text-anchor="middle" font-size="9" fill="#475569">含 processNum / eventType / processRecord</text>

  <text x="760" y="495" text-anchor="middle" font-size="10" fill="#64748b">outside.callback.switch 控制开关</text>

  <!-- 连线下方的触发时序说明 -->
  <text x="460" y="530" text-anchor="middle" font-size="10" fill="#94a3b8">通道1 在引擎侧同步执行 · 通道2/3 在 ProcessorFactory 中异步分发</text>
</svg>

---

## 二、通道 1: DIY 表单生命周期回调

### 2.1 FormOperationAdaptor 接口

DIY 流程的**唯一集成接口**。实现此接口后，引擎在流程生命周期的各个阶段调用对应方法。

```java
// antflow-base/.../interf/FormOperationAdaptor.java
public interface FormOperationAdaptor<T extends BusinessDataVo>
    extends ProcessFinishListener, ActivitiService {

    // ===== 必须实现 =====
    BpmnStartConditionsVo launchParameters(T vo);
    void submitData(T vo);
    void queryData(T vo);
    void cancellationData(T businessDataVo);

    // ===== 可选实现 =====
    default BpmnStartConditionsVo previewSetCondition(T vo) { return null; }
    default void initData(T vo) {}
    default Boolean automaticCondition(T businessDataVo) { return null; }
    default void automaticAction(T businessDataVo, Boolean conditionResult) {}
    default void consentData(T vo) {}
    default void backToModifyData(T vo) {}
    default void onProcessRecover(BusinessDataVo businessData) {}

    // ===== 继承自 ProcessFinishListener =====
    void finishData(BusinessDataVo vo);

    // ===== 继承自 ActivitiService =====
    // (marker interface, 标注这是一个业务逻辑服务)
}
```

源码路径：`antflow-base/src/main/java/org/openoa/base/interf/FormOperationAdaptor.java`

### 2.2 各回调触发时机

| 方法 | 触发时机 | 典型用途 | 必须 |
|---|---|---|---|
| `launchParameters` | 发起流程前 | 设置流程标题、业务ID、启动参数 | ✅ |
| `previewSetCondition` | 流程预览时 | 预填条件所需的上下文数据 | ❌ |
| `initData` | 表单页面打开时 | 初始化表单默认值 | ❌ |
| `submitData` | 发起人提交流程时 | 持久化业务数据到业务表 | ✅ |
| `queryData` | 审批人查看表单时 | 查询业务数据供审批人查看 | ✅ |
| `consentData` | 审批人同意通过时 | 更新业务状态（如"审批中→已通过"） | ❌ |
| `backToModifyData` | 审批人退回修改时 | 标记业务数据为"待修改" | ❌ |
| `automaticCondition` | 自动节点判断时 | 自定义自动通过条件 | ❌ |
| `automaticAction` | 自动节点条件满足后 | 自动执行操作（如发通知） | ❌ |
| `cancellationData` | 流程作废时 | 作废业务数据 | ✅ |
| `onProcessRecover` | 流程恢复时 | 恢复已结束的流程数据 | ❌ |
| `finishData` | 流程结束时 | 最终处理（如归档、统计） | ❌ |

### 2.3 回调调用链

以发起流程为例，展示完整调用链：

```
前端 POST /bpmnConf/process/buttonsOperation
  ↓ (opType=1, SUBMIT)
BpmnConfController.buttonsOperation
  ↓
ProcessApprovalServiceImpl.buttonsOperation
  ↓
FormFactory.dataFormConversion(map, formCode)
  → 反序列化 Map 为 BusinessDataVo 子类
  ↓
SubmitProcessImpl.doProcessButton(vo)
  ↓
formAdapter.launchParameters(vo)     // ① 获取启动参数
  ↓
bpmBusinessProcessService.add(...)   // 创建流程实例记录
  ↓
formAdapter.submitData(vo)           // ② 持久化业务数据
  ↓
processNodeSubmitBizService.processComplete(task)
  ↓ (Activiti 引擎执行...)
ProcessorFactory.executePostProcessors(vo)
  → ③ 触发 MQ 事件 + 三方 HTTP 回调
```

### 2.4 注册机制

DIY 流程通过 `@ActivitiServiceAnno` 注解注册：

```java
// antflow-base/.../interf/ActivitiServiceAnno.java
@ActivitiServiceAnno(svcName = "LEAVE_WMA", desc = "请假流程")
@Autowired
private FormOperationAdaptor<LeaveApplyVo> leaveFormAdaptor;
```

`svcName` 的值对应流程分类中的 `类型标识`（formCode），引擎通过它查找对应的适配器。

### 2.5 ProcessFinishListener

`FormOperationAdaptor` 继承自 `ProcessFinishListener`，后者定义了一个额外回调：

```java
// antflow-base/.../interf/ProcessFinishListener.java
public interface ProcessFinishListener {
    void finishData(BusinessDataVo vo);
}
```

当流程最终到达结束节点时，引擎依次调用：
1. `formAdapter.finishData(vo)` — 同步，在引擎事务内
2. `ProcessorFactory.executePostProcessors(vo)` — 异步，消息通知等
3. `ThirdPartyCallbackFactory.doCallback(PROC_FINISH_CALL_BACK, ...)` — 异步，三方回调

---

## 三、通道 2: MQ 事件总线

### 3.1 架构

AntFlow 将所有流程操作事件通过 **MQ 消息** 发布到外部系统。核心组件：

```
流程操作完成
  ↓
ProcessorFactory.executePostProcessors(vo)
  ↓
BusinessCallBackFactory.build().doCallBacks(vo, PROCESS_EVENT_CALLBACK)
  ↓ (遍历所有注册的 ProcessEventCallback Adaptor)
ProcessEventSendMessageAdaptor.doCallBack(vo)
  ↓
format BusinessDataVo → MqProcessEventVo
  ↓
publish to MsgTopics.WORKFLOW_EVENT_PUSH ("oa_workflow_event_push")
```

源码路径：
- `antflow-engine/.../callback/BusinessCallBackFactory.java`
- `antflow-base/.../service/ProcessEventSendMessageAdaptor.java`
- `antflow-base/.../constant/enums/MsgProcessEventEnum.java`

### 3.2 事件类型枚举

`MsgProcessEventEnum` 定义了全部事件类型：

| Code | 枚举值 | 触发场景 |
|:---:|---|---|
| 1 | `PROCESS_SUBMIT` | 发起人提交流程 |
| 2 | `PROCESS_RESUBMIT` | 退回后重新提交 |
| 3 | `PROCESS_APPROVE` | 审批人同意 |
| 4 | `PROCESS_NOT_APPROVE` | 审批人不同意 |
| 7 | `PROCESS_ABANDON` | 发起人/管理员作废 |
| 10 | `PROCESS_UNDERTAKE` | 承办 |
| 11 | `PROCESS_CHANGE_DEALER` | 变更处理人 |
| 12 | `PROCESS_ABORT` | 终止流程 |
| 15 | `PROCESS_FORWARD` | 转发 |
| 18 | `BUTTON_BACK_TO_MODIFY` | 退回到修改 |
| 19 | `PROCESS_JP` | 加批 |
| 20 | `PROCESS_FINISH` | 流程完成 |
| 100 | `HISTORY_SYNC` | 历史数据同步 |
| 101 | `PROCESS_DATA_SYNC` | 流程数据同步 |

### 3.3 MqProcessEventVo 结构

```java
// antflow-base/.../vo/MqProcessEventVo.java
public class MqProcessEventVo {
    private String processCode;       // 流程编号
    private String businessId;        // 业务编号
    private String procInstId;        // Activiti 实例 ID
    private Integer buttonOperationType; // 按钮操作类型 (对应 MsgProcessEventEnum code)
    private String taskId;            // 任务 ID
    private Date opTime;              // 操作时间
    private String operationUserId;   // 操作人 ID
}
```

### 3.4 重试机制

`BusinessCallBackFactory` 内置了重试逻辑：

```java
// antflow-engine/.../callback/BusinessCallBackFactory.java (L53-62)
Retryer retryer = new Retryer.Default();  // 默认指数退避
while (true) {
    try {
        businessCallBackAdaptor.doCallBack(params);
        break;                            // 成功则退出
    } catch (RuntimeException e) {
        retryer.continueOrPropagate(e);   // 失败则重试
    }
}
```

### 3.5 扩展自定义事件

实现 `BusinessCallBackAdaptor` 并注册：

```java
@Component
public class CustomEventAdaptor extends BaseSendMqMsgAdaptor<CustomEventVo, BusinessDataVo> {
    @Override
    protected String getTopicName() {
        return "my_custom_topic";
    }

    @Override
    public CustomEventVo formattedValue(BusinessDataVo vo) {
        // 构造自定义事件对象
        return new CustomEventVo(...);
    }
}
```

引擎通过 `BusinessCallbackEnum.getAllAdaptorsByType()` 自动扫描所有 `BusinessCallBackFace` 实现。

---

## 四、通道 3: 三方系统 HTTP 回调

### 4.1 适用场景

第三方系统通过 Open API 接入 AntFlow 后，需要**实时接收流程状态变更通知**。例如：
- OA 系统发起请假流程后，需要知道流程是否被批准
- ERP 系统发起采购流程后，需要同步审批结果

### 4.2 回调类型

`CallbackTypeEnum` 定义了 6 种回调：

| 枚举值 | mark | desc | beanId |
|---|---|---|---|
| `CONF_CONDITION_CALL_BACK` | `CONF_CONDITION_CALL_BACK` | 条件分支回调 | `CONF_CONDITION_CALL_BACK` |
| `PROC_CONDITION_CALL_BACK` | `PROC_CONDITION_CALL_BACK` | 条件判断回调 | `PROC_CONDITION_CALL_BACK` |
| `PROC_SUBMIT_CALL_BACK` | `PROC_SUBMIT_CALL_BACK` | 提交操作回调 | `PROC_SUBMIT_CALL_BACK` |
| `PROC_STARTED_CALL_BACK` | `PROC_STARTED_CALL_BACK` | 流程发起完成 | `PROC_BASE_CALL_BACK` |
| `PROC_COMMIT_CALL_BACK` | `PROC_COMMIT_CALL_BACK` | 流转操作回调 | `PROC_BASE_CALL_BACK` |
| `PROC_END_CALL_BACK` | `PROC_END_CALL_BACK` | 结束操作回调 | `PROC_BASE_CALL_BACK` |
| `PROC_FINISH_CALL_BACK` | `PROC_FINISH_CALL_BACK` | 完成操作回调 | `PROC_BASE_CALL_BACK` |

`beanId` 字段决定了由哪个 `CallbackAdaptor` 实现来处理这个回调。

### 4.3 ThirdPartyCallbackFactory 执行流程

```java
// antflow-engine/.../factory/ThirdPartyCallbackFactory.java
public <T> T doCallback(CallbackTypeEnum callbackTypeEnum, BpmnConfVo bpmnConfVo,
                        String processNum, String businessId) {

    // 1. 检查回调开关 (outside.callback.switch)
    boolean callBackSwitch = Boolean.parseBoolean(
        environment.getProperty("outside.callback.switch", "true"));

    // 2. 根据 beanId 查找回调适配器
    CallbackAdaptor callbackAdaptor = getCallbackAdaptor(callbackTypeEnum.getBeanId());
    if (callbackAdaptor == null) return null;

    // 3. 关闭回调时返回空响应
    if (!callBackSwitch) {
        return (T) callbackAdaptor.getNewRespObj();
    }

    // 4. 构造回调请求
    CallbackReqVo callbackReqVo = callbackAdaptor.formatRequest(bpmnConfVo);
    callbackReqVo.setEventType(callbackTypeEnum.getMark());
    callbackReqVo.setFormCode(formCode);
    callbackReqVo.setProcessNum(processNum);
    callbackReqVo.setBusinessId(businessId);

    // 5. 查询审批历史记录（如果流程已运行）
    if (!StringUtils.isEmpty(processNum)) {
        List<BpmVerifyInfoVo> records = bpmVerifyInfoNewService.getBpmVerifyInfoVos(processNum, finishFlag);
        callbackReqVo.setProcessRecord(convertToRecordVos(records));
    }

    // 6. API 签名：MD5(body + secret) → Base64
    String jsonString = JSON.toJSONString(callbackReqVo);
    String md5Hex = DigestUtils.md5Hex(jsonString + apiClientSecret);
    String sign = Base64.encodeBase64String(md5Hex.getBytes());

    // 7. HTTP POST 回调
    headers.put("api-client-id", apiClientId);
    headers.put("api-workflow-sign", sign);
    headers.put("sso-uid", currentUserId);
    headers.put("sso-name", URLEncoder.encode(currentUserName, "UTF-8"));
    resultJson = doPost(url, headers, callbackReqVo);

    // 8. 解析响应
    if (success) {
        CallbackRespVo resp = callbackAdaptor.formatResponce(resultJson);
        resp.setBusinessPartyMark(businessPartyMark);
        return (T) resp;
    }
}
```

### 4.4 回调请求结构

```java
// CallbackReqVo (由 CallbackAdaptor.formatRequest 构造)
{
    "eventType": "PROC_STARTED_CALL_BACK",  // 回调类型
    "formCode": "biz_LEAVE_WMA",             // 表单编号（含业务方前缀）
    "businessPartyMark": "COMPANY_A",        // 业务方标识
    "processNum": "LC-00001",                // 流程编号
    "businessId": "BIZ123",                  // 对接方业务编号
    "processRecord": [                       // 审批历史（仅流转后回调有）
        {
            "verifyUserName": "张三",
            "verifyStatus": 1,               // 1=同意 4=不同意
            "verifyDesc": "同意",
            "verifyTime": "2025-07-25 10:00:00"
        }
    ]
}
```

### 4.5 触发时机

`ThirdPartyCallBackServiceImpl` 在以下节点调用 `doCallback`：

```java
// antflow-engine/.../service/biz/ThirdPartyCallBackServiceImpl.java
@MethodReplay
public void doCallback(CallbackTypeEnum callbackTypeEnum, BpmnConfVo bpmnConfVo,
                       String processNum, String businessId, String verifyUserName) {

    ThirdPartyCallbackFactory.build().doCallback(
        callbackTypeEnum, bpmnConfVo, processNum, businessId);
}
```

| 回调类型 | 触发位置 | 时机 |
|---|---|---|
| `PROC_STARTED_CALL_BACK` | 发起流程完成后 | 流程实例创建、第一个任务生成后 |
| `PROC_COMMIT_CALL_BACK` | 每次审批操作后 | 同意/退回/转办/加签等操作完成后 |
| `PROC_END_CALL_BACK` | 流程被终止时 | 作废/管理员终止 |
| `PROC_FINISH_CALL_BACK` | 流程正常结束时 | 到达结束节点后 |
| `PROC_CONDITION_CALL_BACK` | 条件分支评估时 | 网关条件判断前后 |
| `CONF_CONDITION_CALL_BACK` | 流程配置校验时 | 配置发布/激活时 |

---

## 五、三路回调对比

| 维度 | 通道 1: 表单回调 | 通道 2: MQ 事件 | 通道 3: HTTP 回调 |
|---|---|---|---|
| 调用方式 | 同步，顺序调用 | 异步，MQ 发布 | 异步，HTTP POST |
| 适用场景 | DIY 流程业务逻辑 | 企业内部系统集成 | 外部三方系统对接 |
| 注册方式 | `@ActivitiServiceAnno` | 实现 `BusinessCallBackAdaptor` | 配置回调 URL |
| 重试机制 | 无（抛异常即失败） | Retryer 指数退避 | 无（失败只打日志） |
| 数据载体 | `BusinessDataVo` 子类 | `MqProcessEventVo` | `CallbackReqVo` |
| 事务边界 | 与引擎同一事务 | 独立，after-commit | 独立，after-commit |
| 开关控制 | 无 | 无 | `outside.callback.switch` |

---

## 六、完整生命周期事件序列

以一个请假流程完整生命周期为例，展示所有回调的触发顺序：

```
1. 发起人填写表单，点击"提交"
   → launchParameters()        // 通道1: 准备参数
   → submitData()              // 通道1: 持久化数据
   → PROCESS_SUBMIT MQ         // 通道2: 事件总线
   → PROC_STARTED_CALL_BACK    // 通道3: 三方回调

2. 审批人 A 点击"同意"
   → consentData()             // 通道1: 更新状态
   → PROCESS_APPROVE MQ        // 通道2: 事件总线
   → PROC_COMMIT_CALL_BACK     // 通道3: 三方回调

3. 审批人 B 点击"退回"
   → backToModifyData()        // 通道1: 标记待修改
   → BUTTON_BACK_TO_MODIFY MQ  // 通道2: 事件总线
   → PROC_COMMIT_CALL_BACK     // 通道3: 三方回调

4. 发起人修改后重新提交
   → consentData()             // 通道1: 再次持久化
   → PROCESS_RESUBMIT MQ       // 通道2: 事件总线
   → PROC_COMMIT_CALL_BACK     // 通道3: 三方回调

5. 最后一个审批人同意
   → consentData()             // 通道1: 更新状态
   → PROCESS_APPROVE MQ        // 通道2: 事件总线
   → finishData()              // 通道1: 结束处理
   → PROCESS_FINISH MQ         // 通道2: 事件总线
   → PROC_FINISH_CALL_BACK     // 通道3: 三方回调
```

---

## 下一步

- [Adaptor 适配器模式](/dev-guide/adaptor-pattern) — 理解适配器注册与调度机制
- [扩展通知渠道](/dev-guide/extend-notice) — 自定义消息通知
- [集成现有系统](/dev-guide/integrate-existing) — 三方系统接入完整指南
