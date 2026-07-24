# 发起流程

> 流程经过设计、激活后,业务用户即可在「流程中心」入口发起流程实例。本章详细介绍发起流程的入口、表单适配、提交链路与后端启动逻辑。

## 入口与可用流程

登录系统后,点击左侧菜单 **流程中心**,页面默认展示「可用流程」看板:

- **可用流程(DIY)**:自定义业务表单的流程(如请假、加油上报、第三方整合)
- **可用流程(LF)**:低代码表单流程,完全在 AntFlow 内设计

两种类型按 `t_bpmn_conf.is_lowcode_flow` 字段区分(0=DIY,1=LF),仅 `effectiveStatus=1` 的活跃版本才会出现。

![发起请求菜单](/images/5-1.png)

点击任一流程卡片即跳转至发起页 `/startFlow/index`,URL 形如 `#/startFlow/index?flowCode=LEAVE_WMA`。

## 发起页结构

发起页 [startFlow/index.vue](file:///d:/projects/jimuoffice/antflow-vue/src/views/workflow/startFlow/index.vue) 采用 **Tab 双视图**:

<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 880 260" font-family="-apple-system, BlinkMacSystemFont, 'Segoe UI', sans-serif">
  <defs>
    <linearGradient id="tab1" x1="0" y1="0" x2="0" y2="1"><stop offset="0%" stop-color="#dbeafe"/><stop offset="100%" stop-color="#bfdbfe"/></linearGradient>
    <linearGradient id="tab2" x1="0" y1="0" x2="0" y2="1"><stop offset="0%" stop-color="#dcfce7"/><stop offset="100%" stop-color="#bbf7d0"/></linearGradient>
    <linearGradient id="form" x1="0" y1="0" x2="1" y2="1"><stop offset="0%" stop-color="#f8fafc"/><stop offset="100%" stop-color="#e2e8f0"/></linearGradient>
  </defs>

  <rect x="20" y="20" width="840" height="40" rx="6" fill="#1e293b"/>
  <text x="40" y="46" font-size="14" font-weight="700" fill="#fff">发起流程申请 · LEAVE_WMA · 张三</text>

  <rect x="20" y="80" width="120" height="32" rx="4" fill="url(#tab1)" stroke="#3b82f6"/>
  <text x="80" y="100" text-anchor="middle" font-size="13" font-weight="600" fill="#1e40af">① 填写表单</text>
  <rect x="140" y="80" width="120" height="32" rx="4" fill="url(#tab2)" stroke="#16a34a"/>
  <text x="200" y="100" text-anchor="middle" font-size="13" font-weight="600" fill="#155e2f">② 流程预览</text>

  <rect x="20" y="124" width="840" height="120" rx="6" fill="url(#form)" stroke="#94a3b8" stroke-dasharray="4 3"/>
  <text x="40" y="148" font-size="13" font-weight="600" fill="#334155">业务表单区(动态加载)</text>
  <text x="40" y="172" font-size="11" fill="#475569">DIY 模式:从 bizFormMaps 映射加载 /components/forms/formN.vue</text>
  <text x="40" y="190" font-size="11" fill="#475569">LF 模式:调 getStartFormData 返回 vform JSON,由 VFormRender 渲染</text>
  <text x="40" y="208" font-size="11" fill="#475569">外部多 Tab 模式:extraFlags &amp; 64,加载多个 LF 表单</text>
  <text x="40" y="228" font-size="11" fill="#dc2626">表单字段权限由 lfFieldControlVOs(HIDDEN/READ/Edit)控制</text>
</svg>

### 关键前端逻辑

[startFlow/index.vue](file:///d:/projects/jimuoffice/antflow-vue/src/views/workflow/startFlow/index.vue) 关键代码片段:

```javascript
// 1. 进入页面时按 formCode 加载表单数据
async function loadFormData(flowCode) {
  const isLowCode = await checkIsLowCodeFlow(flowCode);
  if (isLowCode) {
    // LF 模式:后端返回 vform JSON + 字段权限
    const { data } = await getStartFormData(flowCode);
    state.lfFormData = data.lfFormData;             // vform schema
    state.lfFieldControlVOs = data.lfFieldControlVOs; // 字段权限 R/E/H
    state.useExternalForm = data.useExternalForm;   // 是否外部表单
    state.formHidden = data.formHidden;
  } else {
    // DIY 模式:从 bizFormMaps 动态加载 Vue 组件
    loadDIYComponent(flowCode);
  }
}

// 2. 提交发起流程
async function startTest() {
  const param = {
    operationType: 1,                  // 1=发起 SUBMIT
    isLowCodeFlow: state.isLowCode,
    formCode: state.flowCode,
    lfFields: collectLFFields(),       // LF 表单值
    lfFieldsMulti: state.multiFormData,// 外部多表单值
    approvalComment: '',               // 发起人备注
  };
  await processOperation(param);
  ElMessage.success('流程已发起');
  router.push('/flowtask/mytask');
}
```

[api/workflow/index.js](file:///d:/projects/jimuoffice/antflow-vue/src/api/workflow/index.js) 中三个核心 API:

| 方法 | HTTP | 用途 |
|---|---|---|
| `processOperation(data)` | POST `/bpmnConf/process/buttonsOperation?formCode=xxx` | 发起/审批统一入口,`operationType=1` 表示发起 |
| `getFlowPreview(data)` | POST `/bpmnConf/startPagePreviewNode` | 切到「流程预览」Tab 时拉取节点链路 |
| `getStartFormData(formCode)` | GET `/lowcode/getStartFormData?formCode=xxx` | LF 表单 schema 加载 |

## 后端启动链路

发起请求进入后端后,经过 **Controller → AOP 拦截 → 适配器路由 → Activiti 启动** 的多层调用链:

<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 920 360" font-family="-apple-system, BlinkMacSystemFont, 'Segoe UI', sans-serif">
  <defs>
    <marker id="arr" viewBox="0 0 10 10" refX="9" refY="5" markerWidth="6" markerHeight="6" orient="auto"><path d="M0,0 L10,5 L0,10 z" fill="#475569"/></marker>
  </defs>

  <!-- 前端 -->
  <rect x="20" y="20" width="180" height="60" rx="8" fill="#dbeafe" stroke="#3b82f6"/>
  <text x="110" y="44" text-anchor="middle" font-size="13" font-weight="700" fill="#1e40af">前端 startFlow</text>
  <text x="110" y="64" text-anchor="middle" font-size="11" fill="#1e3a8a">processOperation({opType:1})</text>

  <!-- Controller -->
  <rect x="240" y="20" width="200" height="60" rx="8" fill="#fce7f3" stroke="#db2777"/>
  <text x="340" y="44" text-anchor="middle" font-size="13" font-weight="700" fill="#9d174d">BpmnConfController</text>
  <text x="340" y="64" text-anchor="middle" font-size="11" fill="#831843">buttonsOperation(values, formCode)</text>

  <!-- Service -->
  <rect x="480" y="20" width="200" height="60" rx="8" fill="#fef3c7" stroke="#d97706"/>
  <text x="580" y="44" text-anchor="middle" font-size="13" font-weight="700" fill="#92400e">ProcessApprovalServiceImpl</text>
  <text x="580" y="64" text-anchor="middle" font-size="11" fill="#78350f">buttonsOperation → buttonsPreOperation</text>

  <!-- AOP -->
  <rect x="720" y="20" width="180" height="60" rx="8" fill="#fee2e2" stroke="#dc2626"/>
  <text x="810" y="44" text-anchor="middle" font-size="13" font-weight="700" fill="#991b1b">DoButtonOperationAspect</text>
  <text x="810" y="64" text-anchor="middle" font-size="11" fill="#7f1d1d">@Around AOP 拦截</text>

  <!-- 事务层 -->
  <rect x="20" y="130" width="200" height="60" rx="8" fill="#e0e7ff" stroke="#4f46e5"/>
  <text x="120" y="154" text-anchor="middle" font-size="13" font-weight="700" fill="#3730a3">ButtonOperationServiceImpl</text>
  <text x="120" y="174" text-anchor="middle" font-size="11" fill="#312e81">buttonsOperationTransactional</text>

  <!-- 适配器工厂 -->
  <rect x="260" y="130" width="200" height="60" rx="8" fill="#dcfce7" stroke="#16a34a"/>
  <text x="360" y="154" text-anchor="middle" font-size="13" font-weight="700" fill="#14532d">AdaptorFactory</text>
  <text x="360" y="174" text-anchor="middle" font-size="11" fill="#052e16">getProcessOperation(vo)</text>

  <!-- SubmitProcessImpl -->
  <rect x="500" y="130" width="220" height="60" rx="8" fill="#fde68a" stroke="#ca8a04"/>
  <text x="610" y="154" text-anchor="middle" font-size="13" font-weight="700" fill="#713f12">SubmitProcessImpl</text>
  <text x="610" y="174" text-anchor="middle" font-size="11" fill="#422006">doProcessButton(opType=1)</text>

  <!-- 后处理器 -->
  <rect x="760" y="130" width="140" height="60" rx="8" fill="#cffafe" stroke="#0891b2"/>
  <text x="830" y="154" text-anchor="middle" font-size="13" font-weight="700" fill="#155e75">ProcessorFactory</text>
  <text x="830" y="174" text-anchor="middle" font-size="11" fill="#083344">executePostProcessors</text>

  <!-- Activiti -->
  <rect x="100" y="240" width="320" height="100" rx="8" fill="#f1f5f9" stroke="#475569"/>
  <text x="260" y="264" text-anchor="middle" font-size="13" font-weight="700" fill="#1e293b">BpmnConfBizServiceImpl.startProcess</text>
  <text x="120" y="288" font-size="11" fill="#334155">① 格式化 BpmnConfVo  ② 设置视图按钮</text>
  <text x="120" y="306" font-size="11" fill="#334155">③ BpmnNodeFormatImpl 生成元素列表</text>
  <text x="120" y="324" font-size="11" fill="#334155">④ BpmnInsertVariablesImpl 写 t_bpm_variable</text>

  <rect x="480" y="240" width="320" height="100" rx="8" fill="#fef9c3" stroke="#a16207"/>
  <text x="640" y="264" text-anchor="middle" font-size="13" font-weight="700" fill="#713f12">BpmnCreateBpmnAndStartImpl</text>
  <text x="500" y="288" font-size="11" fill="#422006">① createBpmnAndStart 启动 Activiti 实例</text>
  <text x="500" y="306" font-size="11" fill="#422006">② 写入 ACT_RU_EXECUTION / ACT_RU_TASK</text>
  <text x="500" y="324" font-size="11" fill="#422006">③ 触发首个用户任务,流程流转到下一节点</text>

  <!-- 连线 -->
  <line x1="200" y1="50" x2="240" y2="50" stroke="#475569" stroke-width="1.5" marker-end="url(#arr)"/>
  <line x1="440" y1="50" x2="480" y2="50" stroke="#475569" stroke-width="1.5" marker-end="url(#arr)"/>
  <line x1="680" y1="50" x2="720" y2="50" stroke="#475569" stroke-width="1.5" marker-end="url(#arr)"/>
  <line x1="810" y1="80" x2="120" y2="130" stroke="#475569" stroke-width="1.5" marker-end="url(#arr)" stroke-dasharray="4 3"/>
  <line x1="220" y1="160" x2="260" y2="160" stroke="#475569" stroke-width="1.5" marker-end="url(#arr)"/>
  <line x1="460" y1="160" x2="500" y2="160" stroke="#475569" stroke-width="1.5" marker-end="url(#arr)"/>
  <line x1="720" y1="160" x2="760" y2="160" stroke="#475569" stroke-width="1.5" marker-end="url(#arr)"/>
  <line x1="610" y1="190" x2="260" y2="240" stroke="#475569" stroke-width="1.5" marker-end="url(#arr)" stroke-dasharray="4 3"/>
  <line x1="420" y1="290" x2="480" y2="290" stroke="#475569" stroke-width="1.5" marker-end="url(#arr)"/>
</svg>

### Controller 入口

[BpmnConfController.java](file:///d:/projects/jimuoffice/antflow-engine/src/main/java/org/openoa/engine/bpmnconf/controller/BpmnConfController.java):

```java
@RequestMapping("/bpmnConf")
public class BpmnConfController {

    @PostMapping("/process/buttonsOperation")
    public Result<String> buttonsOperation(@RequestBody Map<String, Object> values,
                                           @RequestParam("formCode") String formCode) {
        return Result.success(processApprovalService.buttonsOperation(values, formCode));
    }
}
```

### AOP 拦截层

[DoButtonOperationAspect.java](file:///d:/projects/jimuoffice/antflow-engine/src/main/java/org/openoa/engine/conf/aspect/DoButtonOperationAspect.java) 是核心解耦点:

```java
@Around("execution(* org.openoa.engine.factory.ButtonPreOperationService.buttonsPreOperation(..))")
public Object around(ProceedingJoinPoint pjp) throws Throwable {
    Map<String, Object> params = (Map<String, Object>) pjp.getArgs()[0];
    String formCode = (String) pjp.getArgs()[1];

    // 1. 通过 FormFactory 找到流程对应的 FormAdaptor,反序列化为 BusinessDataVo
    BusinessDataVo vo = formFactory.dataFormConversion(params, formCode);

    // 2. 根据 operationType 找到对应枚举,设置终止/作废标志
    ProcessOperationEnum opEnum = ProcessOperationEnum.getEnumByCode(vo.getOperationType());
    vo.setFlag(opEnum.isTerminate());   // 终止/不同意=false,作废=true

    // 3. 委派给事务层执行
    return buttonOperationService.buttonsOperationTransactional(vo);
}
```

### SubmitProcessImpl 适配器

[SubmitProcessImpl.java](file:///d:/projects/jimuoffice/antflow-engine/src/main/java/org/openoa/engine/bpmnconf/adp/processoperation/SubmitProcessImpl.java) 是发起操作的适配器:

```java
@Component("submitProcess")
public class SubmitProcessImpl implements ProcessOperationAdaptor {

    @Override
    public void doProcessButton(BusinessDataVo vo) {
        // 1. 业务表单数据持久化(调 FormAdaptor.submitData)
        formAdapter.submitData(vo);

        // 2. 构造启动条件 VO
        BpmnStartConditionsVo bpmnStartConditionsVo = formAdapter.launchParameters(vo);

        // 3. 创建业务流程实例记录,processState = HANDLING_STATE
        BpmBusinessProcess bp = bpmBusinessProcessService.addBusinessProcess(
                vo.getFormCode(),
                vo.getProcessNumber(),
                SecurityUtils.getLogInEmpId(),
                bpmnStartConditionsVo);

        // 4. 启动 Activiti 流程实例(详见 BpmnConfBizServiceImpl.startProcess)
        bpmnConfCommonService.startProcess(bpmnCode, bpmnStartConditionsVo);
    }

    @Override
    public Enum<?> getOperationType() { return ProcessOperationEnum.SUBMIT; }
}
```

### Activiti 启动

[BpmnConfBizServiceImpl.startProcess](file:///d:/projects/jimuoffice/antflow-engine/src/main/java/org/openoa/engine/bpmnconf/service/biz/BpmnConfBizServiceImpl.java) 完成实际的 Activiti 实例创建:

1. 读取 `t_bpmn_conf` 与 `t_bpmn_node` 还原流程定义
2. 通过 `personneladp/*PersonnelAdaptor` 解析每个节点的实际审批人
3. `BpmnDeduplicationFormatImpl` 执行审批人去重
4. `ConditionServiceImpl` 评估条件节点,决定后续分支
5. 写入 `t_bpm_variable`(processStartConditions JSON,保存启动时的表单数据)
6. `BpmnCreateBpmnAndStartImpl.createBpmnAndStart` 调用 Activiti `runtimeService.startProcessInstanceById`
7. 流程流转到首个审批节点,Activiti 在 `ACT_RU_TASK` 写入任务记录

## 表单填写与提交

填写表单并提交:

![填写表单并提交](/images/5-2.png)

提交后:

1. 后端校验表单必填项(由 `lfFieldControlVOs` 中 `required` 标记)
2. 业务表单数据持久化(LF 模式存 `t_bpm_variable.processStartConditions` JSON;DIY 模式存到对应业务表,如 `t_biz_leave`)
3. 创建 `bpm_business_process` 记录,`processState=2`(办理中)
4. 启动 Activiti 流程实例
5. 前端跳转到「我的发起」页

## 涉及的数据库表

| 表名 | 用途 |
|---|---|
| `bpm_business_process` | 流程实例主记录(processNumber、processState、createUser 等) |
| `t_bpm_variable` | 流程变量,`processStartConditions` 字段以 JSON 形式保存表单数据 |
| `t_bpmn_conf` | 流程定义(effectiveStatus=1 的活跃版本) |
| `t_bpmn_node` | 节点定义,运行期读取以解析审批人 |
| `bpm_business_draft` | 草稿表,`operationType=30` 时存入 |
| `ACT_RE_PROCDEF` | Activiti 流程定义表 |
| `ACT_RU_EXECUTION` | Activiti 运行中执行实例 |
| `ACT_RU_TASK` | Activiti 当前用户任务 |
| `ACT_HI_PROCINST` | Activiti 历史流程实例 |

## 与流程设计阶段的衔接

| 设计阶段产物 | 运行期使用点 |
|---|---|
| `BpmnNodeConfigJson.approver.nodeProperty` | `personneladp/*PersonnelAdaptor` 解析实际审批人 |
| `BpmnNodeConfigJson.buttonSignConf.operationTypes` | 决定每个节点允许的按钮(同意/退回/转办等) |
| `BpmnNodeConditionsConfJson` | `ConditionServiceImpl` 评估条件分支 |
| `BpmnConfConfigJson.noticeChannelTypes` | 决定消息发送通道 |
| `BpmnConfConfigJson.deduplicationType` | `BpmnDeduplicationFormatImpl` 全局去重策略 |
| `NodeExtraInfoDTO.nodeLabelVOS` | 节点 Labels 触发特殊运行期行为(动态条件迁移等) |

## 小结

- 发起流程的统一入口是 `POST /bpmnConf/process/buttonsOperation?formCode=xxx`,通过 `operationType=1` 区分发起动作
- AOP 拦截层 `DoButtonOperationAspect` 把前端 Map 反序列化为 `BusinessDataVo`,屏蔽了 LF/DIY 表单的差异
- `SubmitProcessImpl` 适配器是发起操作的处理器,完成业务数据保存 + Activiti 实例启动
- LF 表单数据存 `t_bpm_variable.processStartConditions` JSON,DIY 表单数据存对应业务表
- 提交成功后,流程实例进入「办理中」状态,Activiti 在 `ACT_RU_TASK` 写入首个审批任务

下一节 [我的待办](/workflow-run/my-tasks) 介绍如何查看与处理待办任务。
