# 流程消息

> AntFlow 内置完整的消息通知体系,支持邮件、短信、App 推送、钉钉等工作流事件通知。本章详解消息模板管理、9 种通知类型、通配符替换机制与多通道发送适配器。

## 消息体系总览

<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 920 360" font-family="-apple-system, BlinkMacSystemFont, 'Segoe UI', sans-serif">
  <defs>
    <marker id="arr4" viewBox="0 0 10 10" refX="9" refY="5" markerWidth="6" markerHeight="6" orient="auto"><path d="M0,0 L10,5 L0,10 z" fill="#475569"/></marker>
  </defs>

  <!-- 触发源:各 ProcessOperationAdaptor -->
  <rect x="20" y="20" width="200" height="80" rx="8" fill="#dbeafe" stroke="#3b82f6"/>
  <text x="120" y="44" text-anchor="middle" font-size="13" font-weight="700" fill="#1e40af">ProcessOperationAdaptor</text>
  <text x="120" y="64" text-anchor="middle" font-size="10" fill="#1e3a8a">同意/退回/转办/加签/作废…</text>
  <text x="120" y="82" text-anchor="middle" font-size="10" fill="#1e3a8a">doProcessButton(vo)</text>

  <!-- 后处理器 -->
  <rect x="260" y="20" width="180" height="80" rx="8" fill="#dcfce7" stroke="#16a34a"/>
  <text x="350" y="44" text-anchor="middle" font-size="13" font-weight="700" fill="#155e2f">ProcessorFactory</text>
  <text x="350" y="64" text-anchor="middle" font-size="10" fill="#14532d">executePostProcessors</text>
  <text x="350" y="82" text-anchor="middle" font-size="10" fill="#14532d">触发消息发送</text>

  <!-- 消息 Service -->
  <rect x="480" y="20" width="240" height="80" rx="8" fill="#fef3c7" stroke="#d97706"/>
  <text x="600" y="44" text-anchor="middle" font-size="13" font-weight="700" fill="#92400e">ActivitiBpmMsgTemplateServiceImpl</text>
  <text x="600" y="64" text-anchor="middle" font-size="10" fill="#78350f">@Async 异步发送</text>
  <text x="600" y="82" text-anchor="middle" font-size="10" fill="#78350f">按 MsgNoticeTypeEnum 分发</text>

  <!-- 通道适配器 -->
  <rect x="760" y="20" width="140" height="80" rx="8" fill="#fee2e2" stroke="#dc2626"/>
  <text x="830" y="44" text-anchor="middle" font-size="13" font-weight="700" fill="#991b1b">MessageSendAdaptor</text>
  <text x="830" y="64" text-anchor="middle" font-size="10" fill="#7f1d1d">EmailSendAdaptor</text>
  <text x="830" y="82" text-anchor="middle" font-size="10" fill="#7f1d1d">SMSSendAdaptor / AppPushAdaptor</text>

  <!-- 9 种通知类型 -->
  <rect x="20" y="130" width="880" height="100" rx="8" fill="#f8fafc" stroke="#94a3b8"/>
  <text x="40" y="152" font-size="13" font-weight="700" fill="#1e293b">9 种 MsgNoticeTypeEnum 通知类型</text>

  <rect x="40" y="166" width="160" height="28" rx="4" fill="#dcfce7" stroke="#16a34a"/>
  <text x="120" y="184" text-anchor="middle" font-size="10" fill="#155e2f">PROCESS_FLOW 流转</text>

  <rect x="208" y="166" width="160" height="28" rx="4" fill="#dcfce7" stroke="#16a34a"/>
  <text x="288" y="184" text-anchor="middle" font-size="10" fill="#155e2f">RECEIVE_FLOW_PROCESS 转发</text>

  <rect x="376" y="166" width="160" height="28" rx="4" fill="#dcfce7" stroke="#16a34a"/>
  <text x="456" y="184" text-anchor="middle" font-size="10" fill="#155e2f">PROCESS_FINISH 完成</text>

  <rect x="544" y="166" width="160" height="28" rx="4" fill="#fef3c7" stroke="#d97706"/>
  <text x="624" y="184" text-anchor="middle" font-size="10" fill="#92400e">PROCESS_REJECT 不通过</text>

  <rect x="712" y="166" width="168" height="28" rx="4" fill="#fee2e2" stroke="#dc2626"/>
  <text x="796" y="184" text-anchor="middle" font-size="10" fill="#991b1b">PROCESS_TIME_OUT 超时</text>

  <rect x="40" y="200" width="160" height="28" rx="4" fill="#fee2e2" stroke="#dc2626"/>
  <text x="120" y="218" text-anchor="middle" font-size="10" fill="#991b1b">PROCESS_STOP 终止</text>

  <rect x="208" y="200" width="160" height="28" rx="4" fill="#e0e7ff" stroke="#4f46e5"/>
  <text x="288" y="218" text-anchor="middle" font-size="10" fill="#3730a3">PROCESS_WAIR_VERIFY 代审</text>

  <rect x="376" y="200" width="160" height="28" rx="4" fill="#fce7f3" stroke="#db2777"/>
  <text x="456" y="218" text-anchor="middle" font-size="10" fill="#9d174d">PROCESS_CHANGE_OPERATOR</text>

  <rect x="544" y="200" width="160" height="28" rx="4" fill="#fce7f3" stroke="#db2777"/>
  <text x="624" y="218" text-anchor="middle" font-size="10" fill="#9d174d">PROCESS_CHANGE_NOW_OPERATOR</text>

  <!-- 模板与通配符 -->
  <rect x="20" y="260" width="430" height="80" rx="8" fill="#f1f5f9" stroke="#475569"/>
  <text x="235" y="284" text-anchor="middle" font-size="13" font-weight="700" fill="#1e293b">t_information_template / t_bpmn_conf_notice_template_detail</text>
  <text x="40" y="306" font-size="11" fill="#475569">① 模板按 bpmnCode + msgNoticeType 绑定到流程</text>
  <text x="40" y="324" font-size="11" fill="#475569">② 未绑定则用 MsgNoticeTypeEnum.getDefaultValueByCode</text>

  <rect x="470" y="260" width="430" height="80" rx="8" fill="#fef9c3" stroke="#a16207"/>
  <text x="685" y="284" text-anchor="middle" font-size="13" font-weight="700" fill="#713f12">NoticeReplaceEnum 通配符替换</text>
  <text x="490" y="306" font-size="11" fill="#422006">① {发起人} {流程编号} {当前节点} 等</text>
  <text x="490" y="324" font-size="11" fill="#422006">② 反射替换 NoticeReplaceEnum.replaceContent</text>

  <!-- 连线 -->
  <line x1="220" y1="60" x2="260" y2="60" stroke="#475569" stroke-width="1.5" marker-end="url(#arr4)"/>
  <line x1="440" y1="60" x2="480" y2="60" stroke="#475569" stroke-width="1.5" marker-end="url(#arr4)"/>
  <line x1="720" y1="60" x2="760" y2="60" stroke="#475569" stroke-width="1.5" marker-end="url(#arr4)"/>
  <line x1="600" y1="100" x2="600" y2="130" stroke="#475569" stroke-width="1.5" marker-end="url(#arr4)" stroke-dasharray="4 3"/>
</svg>

## 消息模板管理

前端入口:**流程运维 → 消息模板**,组件 [flowMsg/msgTemplete/index.vue](file:///d:/projects/jimuoffice/antflow-vue/src/views/workflow/flowMsg/msgTemplete/index.vue)。

### 模板列表

左侧 Tab 切换两种类型:

- **通用模板**:站内信/邮件通知
- **短信模板**:短信通道专用

通用模板列表 [comTemplateList.vue](file:///d:/projects/jimuoffice/antflow-vue/src/views/workflow/flowMsg/msgTemplete/templates/comTemplateList.vue) 调用:

- API:POST `/informationTemplates/listPage`
- 列:模板编号、名称、跳转页面、主题、状态、更新时间

### 模板新增/编辑

[comForm.vue](file:///d:/projects/jimuoffice/antflow-vue/src/views/workflow/flowMsg/msgTemplete/templates/comForm.vue) 表单字段:

| 字段 | 说明 |
|---|---|
| 模板名称 | 模板标识 |
| 事件类型 | 从 `getProcessEvents` 拉取 EventTypeEnum 列表 |
| 主题 | 消息标题 |
| 通知内容 | 支持通配符按钮插入 |
| 跳转页面 | 1=流程审批页,2=流程查看页,3=流程待办页 |

通配符按钮点击后调用 `getWildcardCharacter(name)` 拉取 `WildcardCharacterEnum` 列表,用户选择后插入 `{通配符}` 占位。

## 后端实现

### Controller 入口

[InformationTemplateController.java](file:///d:/projects/jimuoffice/antflow-engine/src/main/java/org/openoa/engine/bpmnconf/controller/InformationTemplateController.java):

| 端点 | 用途 |
|---|---|
| POST `/informationTemplates/listPage` | 分页查询模板 |
| GET `/informationTemplates/getInformationTemplateById?templateId=xxx` | 模板详情 |
| POST `/informationTemplates/save` | 保存模板 |
| POST `/informationTemplates/updateById` | 更新模板 |
| POST `/informationTemplates/deleteById?id=xxx` | 软删(isDel=1) |
| GET `/informationTemplates/getWildcardCharacter?name=xxx` | 通配符列表 |
| GET `/informationTemplates/getProcessEvents` | 事件类型列表 |
| GET `/informationTemplates/getAllNoticeTypes` | 通知类型列表 |
| GET `/informationTemplates/getNoticeTypeByFormCode?formCode=xxx` | 流程启用的通知通道 |
| GET `/informationTemplates/testDoTimeoutReminder` | 测试超时提醒 |

### 消息发送 Service

[ActivitiBpmMsgTemplateServiceImpl.java](file:///d:/projects/jimuoffice/antflow-engine/src/main/java/org/openoa/engine/bpmnconf/service/biz/ActivitiBpmMsgTemplateServiceImpl.java) 全部 `@Async` 异步执行,按 `MsgNoticeTypeEnum` 分发:

```java
@Async
public void sendBpmApprovalMsg(BpmnMsgVo vo) {
    // 1. 取模板内容(优先取 t_bpmn_conf_notice_template_detail 中按 bpmnCode+type 绑定的,
    //    无则用 MsgNoticeTypeEnum.getDefaultValueByCode)
    String content = getContent(vo, MsgNoticeTypeEnum.PROCESS_FLOW);

    // 2. 通配符替换
    content = replaceTemplateDetail(vo, content);

    // 3. 取该流程启用的通知通道(从 BpmnConf.confConfigJson.noticeChannelTypes)
    MessageSendTypeEnum[] sendTypes = getMessageSendTypeEnums(
            vo.getProcessId(), vo.getFormCode(), vo.getSelectMack());

    // 4. 按通道分发到底层 Adaptor
    UserMsgUtils.sendMessages(vo.getReceivers(), content, sendTypes);
}
```

### 9 种通知类型方法

| 方法 | MsgNoticeTypeEnum | 触发场景 |
|---|---|---|
| `sendBpmApprovalMsg` | PROCESS_FLOW | 流程流转到下一节点 |
| `sendBpmForwardedlMsg` | RECEIVE_FLOW_PROCESS | 抄送/转发 |
| `sendBpmFinishMsg` | PROCESS_FINISH | 流程正常完成 |
| `sendBpmRejectMsg` | PROCESS_REJECT | 不同意 |
| `sendBpmOverTimeMsg` | PROCESS_TIME_OUT | 节点超时(从节点 `templateConf.overtimeConf.noticeTypes` 取通道) |
| `sendBpmTerminationMsg` | PROCESS_STOP | 终止 |
| `sendBpmGenerationApprovalMsg` | PROCESS_WAIR_VERIFY | 代审批 |
| `sendBpmChangePersonOrgiMsg` | PROCESS_CHANGE_OPERATOR | 变更原处理人 |
| `sendBpmChangePersonNewMsg` | PROCESS_CHANGE_NOW_OPERATOR | 变更新处理人 |

每个方法都有对应的 `sendBpmXxxMsgBatch` 批量版本,优化大量并发场景。

### 通配符替换:NoticeReplaceEnum

`replaceTemplateDetail` 通过反射调用 `NoticeReplaceEnum.replaceContent`:

```java
private String replaceTemplateDetail(BpmnMsgVo vo, String content) {
    for (NoticeReplaceEnum replace : NoticeReplaceEnum.values()) {
        // 占位符形如 {发起人} {流程编号} {当前节点}
        String placeholder = "{" + replace.getName() + "}";
        if (content.contains(placeholder)) {
            String value = replace.replaceContent(vo);   // 反射调用对应方法
            content = content.replace(placeholder, value);
        }
    }
    return content;
}
```

### 多通道发送适配器

底层 `UserMsgUtils.sendMessages` 按 `MessageSendTypeEnum` 分发:

| 适配器 | 通道 | 实现位置 |
|---|---|---|
| `EmailSendAdaptor` | 邮件 | [adp/processnotice/EmailSendAdaptor.java](file:///d:/projects/jimuoffice/antflow-engine/src/main/java/org/openoa/engine/bpmnconf/adp/processnotice/EmailSendAdaptor.java) |
| `SMSSendAdaptor` | 短信 | [adp/processnotice/SMSSendAdaptor.java](file:///d:/projects/jimuoffice/antflow-engine/src/main/java/org/openoa/engine/bpmnconf/adp/processnotice/SMSSendAdaptor.java) |
| `AppPushAdaptor` | App 推送 | [adp/processnotice/AppPushAdaptor.java](file:///d:/projects/jimuoffice/antflow-engine/src/main/java/org/openoa/engine/bpmnconf/adp/processnotice/AppPushAdaptor.java) |

均继承 `AbstractMessageSendAdaptor`,实现 `sendMessage(List<String> receivers, String content)` 方法。

### 超时提醒

`BpmVariableApproveRemindBizService.doTimeoutReminder()` 由定时任务驱动,扫描 `t_bpmn_approve_remind` 表中配置的超时规则,触发 `sendBpmOverTimeMsg`。

测试入口:`GET /informationTemplates/testDoTimeoutReminder`

## 涉及的数据库表

| 表名 | 用途 |
|---|---|
| `t_information_template` | 消息模板主表 |
| `t_bpmn_conf_notice_template` | 流程-模板关联表 |
| `t_bpmn_conf_notice_template_detail` | 按 bpmnCode + msgNoticeType 维度的模板明细 |
| `t_user_message` | 已发送消息记录 |
| `t_user_message_status` | 消息读状态 |
| `t_bpmn_approve_remind` | 审批提醒/超时配置 |
| `bpm_flowrun_entrust` | 委托记录(影响消息接收人) |

## 与流程设计阶段的衔接

| 设计阶段配置 | 运行期使用 |
|---|---|
| `BpmnConfConfigJson.noticeChannelTypes` | 流程级通道,决定该流程所有消息走哪些通道 |
| `BpmnNodeConfigJson.templateConf.overtimeConf.noticeTypes` | 节点级超时通道 |
| `t_bpmn_conf_notice_template_detail` | 按 bpmnCode + msgNoticeType 绑定模板 |
| `EventTypeEnum` / `MsgNoticeTypeEnum` | 设计期模板选择与运行期触发一一对应 |
| `WildcardCharacterEnum` | 设计期插入占位符,运行期反射替换 |

## 小结

- 消息发送全部 `@Async` 异步执行,不阻塞主流程
- 9 种通知类型覆盖流转、转发、完成、不通过、超时、终止、代审、变更等全部场景
- 模板按 `bpmnCode + msgNoticeType` 维度绑定,无绑定则用默认模板
- 通配符替换基于 `NoticeReplaceEnum` 反射调用,支持 `{发起人}` `{流程编号}` 等占位
- 多通道通过 `MessageSendAdaptor` 适配器模式实现,扩展新通道只需新增 Adaptor
- 超时提醒由定时任务扫描 `t_bpmn_approve_remind` 触发

至此,流程运行板块已完成。下一节进入 [开发指南](/dev-guide/architecture),深入剖析 AntFlow 的架构设计与扩展机制。
