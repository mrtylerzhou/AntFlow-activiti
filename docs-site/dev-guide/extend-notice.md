# 扩展通知渠道

> AntFlow 内置 3 种通知渠道(邮件、短信、App 推送),并预定义了 3 种未实现渠道(企业微信、钉钉、飞书)。本章详解消息发送机制、适配器实现与自定义渠道示例。

## 消息发送链路

<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 920 320" font-family="-apple-system, BlinkMacSystemFont, 'Segoe UI', sans-serif">
  <defs>
    <marker id="arr17" viewBox="0 0 10 10" refX="9" refY="5" markerWidth="6" markerHeight="6" orient="auto"><path d="M0,0 L10,5 L0,10 z" fill="#475569"/></marker>
  </defs>

  <!-- 业务触发 -->
  <rect x="20" y="20" width="180" height="56" rx="8" fill="#dbeafe" stroke="#3b82f6"/>
  <text x="110" y="44" text-anchor="middle" font-size="11" font-weight="700" fill="#1e40af">业务触发</text>
  <text x="110" y="62" text-anchor="middle" font-size="9" fill="#1e3a8a">ProcessOperationAdaptor</text>

  <!-- 后处理器 -->
  <rect x="220" y="20" width="180" height="56" rx="8" fill="#dcfce7" stroke="#16a34a"/>
  <text x="310" y="44" text-anchor="middle" font-size="11" font-weight="700" fill="#155e2f">ProcessorFactory</text>
  <text x="310" y="62" text-anchor="middle" font-size="9" fill="#14532d">executePostProcessors</text>

  <!-- 消息 Service -->
  <rect x="420" y="20" width="220" height="56" rx="8" fill="#fef3c7" stroke="#d97706"/>
  <text x="530" y="44" text-anchor="middle" font-size="11" font-weight="700" fill="#92400e">ActivitiBpmMsgTemplateServiceImpl</text>
  <text x="530" y="62" text-anchor="middle" font-size="9" fill="#78350f">@Async 异步</text>

  <!-- UserMsgUtils -->
  <rect x="660" y="20" width="240" height="56" rx="8" fill="#fee2e2" stroke="#dc2626"/>
  <text x="780" y="44" text-anchor="middle" font-size="11" font-weight="700" fill="#991b1b">UserMsgUtils.sendMessages</text>
  <text x="780" y="62" text-anchor="middle" font-size="9" fill="#7f1d1d">遍历 MessageSendTypeEnum[]</text>

  <!-- AdaptorFactory -->
  <rect x="20" y="110" width="220" height="56" rx="8" fill="#e0e7ff" stroke="#4f46e5"/>
  <text x="130" y="134" text-anchor="middle" font-size="11" font-weight="700" fill="#3730a3">AdaptorFactory</text>
  <text x="130" y="152" text-anchor="middle" font-size="9" fill="#312e81">getProcessNoticeAdaptor(type)</text>

  <!-- 具体适配器 -->
  <rect x="260" y="110" width="160" height="56" rx="8" fill="#fff" stroke="#4f46e5"/>
  <text x="340" y="134" text-anchor="middle" font-size="10" font-weight="700" fill="#3730a3">EmailSendAdaptor</text>
  <text x="340" y="152" text-anchor="middle" font-size="9" fill="#312e81">MAIL</text>

  <rect x="430" y="110" width="160" height="56" rx="8" fill="#fff" stroke="#4f46e5"/>
  <text x="510" y="134" text-anchor="middle" font-size="10" font-weight="700" fill="#3730a3">SMSSendAdaptor</text>
  <text x="510" y="152" text-anchor="middle" font-size="9" fill="#312e81">MESSAGE</text>

  <rect x="600" y="110" width="160" height="56" rx="8" fill="#fff" stroke="#4f46e5"/>
  <text x="680" y="134" text-anchor="middle" font-size="10" font-weight="700" fill="#3730a3">AppPushAdaptor</text>
  <text x="680" y="152" text-anchor="middle" font-size="9" fill="#312e81">PUSH</text>

  <rect x="770" y="110" width="130" height="56" rx="8" fill="#fef9c3" stroke="#a16207" stroke-dasharray="3 2"/>
  <text x="835" y="134" text-anchor="middle" font-size="10" font-weight="700" fill="#713f12">自定义</text>
  <text x="835" y="152" text-anchor="middle" font-size="9" fill="#422006">DING_TALK等</text>

  <!-- MessageServiceImpl -->
  <rect x="20" y="200" width="880" height="100" rx="8" fill="#f1f5f9" stroke="#475569"/>
  <text x="460" y="224" text-anchor="middle" font-size="13" font-weight="700" fill="#1e293b">MessageServiceImpl(@Async)</text>
  <text x="460" y="248" text-anchor="middle" font-size="11" fill="#475569">sendMailBatch / sendSmsBatch / sendAppPushBatch</text>
  <text x="460" y="270" text-anchor="middle" font-size="11" fill="#475569">发送前查 t_user_message_status 判断用户是否开启对应渠道</text>
  <text x="460" y="290" text-anchor="middle" font-size="11" fill="#dc2626">自定义适配器不走 MessageService,直接调企业 SDK</text>

  <!-- 箭头 -->
  <line x1="200" y1="48" x2="220" y2="48" stroke="#475569" stroke-width="1.5" marker-end="url(#arr17)"/>
  <line x1="400" y1="48" x2="420" y2="48" stroke="#475569" stroke-width="1.5" marker-end="url(#arr17)"/>
  <line x1="640" y1="48" x2="660" y2="48" stroke="#475569" stroke-width="1.5" marker-end="url(#arr17)"/>
  <line x1="780" y1="76" x2="130" y2="110" stroke="#475569" stroke-width="1.5" marker-end="url(#arr17)" stroke-dasharray="3 2"/>
  <line x1="240" y1="138" x2="260" y2="138" stroke="#475569" stroke-width="1.5" marker-end="url(#arr17)"/>
  <line x1="420" y1="138" x2="430" y2="138" stroke="#475569" stroke-width="1.5" marker-end="url(#arr17)"/>
  <line x1="590" y1="138" x2="600" y2="138" stroke="#475569" stroke-width="1.5" marker-end="url(#arr17)"/>
  <line x1="760" y1="138" x2="770" y2="138" stroke="#475569" stroke-width="1.5" marker-end="url(#arr17)"/>
  <line x1="340" y1="166" x2="250" y2="200" stroke="#475569" stroke-width="1" marker-end="url(#arr17)" stroke-dasharray="3 2"/>
  <line x1="510" y1="166" x2="400" y2="200" stroke="#475569" stroke-width="1" marker-end="url(#arr17)" stroke-dasharray="3 2"/>
  <line x1="680" y1="166" x2="600" y2="200" stroke="#475569" stroke-width="1" marker-end="url(#arr17)" stroke-dasharray="3 2"/>
</svg>

## 核心接口

### ProcessNoticeAdaptor

[ProcessNoticeAdaptor.java](file:///d:/projects/jimuoffice/antflow-base/src/main/java/org/openoa/base/interf/ProcessNoticeAdaptor.java):

```java
public interface ProcessNoticeAdaptor extends AdaptorService {
    /** 批量发送消息 */
    void sendMessageBatchByType(List<UserMsgVo> userMsgVos);
}
```

### AbstractMessageSendAdaptor

[AbstractMessageSendAdaptor.java](file:///d:/projects/jimuoffice/antflow-engine/src/main/java/org/openoa/engine/bpmnconf/adp/processnotice/AbstractMessageSendAdaptor.java):

```java
public abstract class AbstractMessageSendAdaptor<T> implements ProcessNoticeAdaptor {

    @Autowired
    protected MessageServiceImpl messageService;

    /** 工具方法:把 UserMsgVo 列表转为发送对象 */
    protected Map<String, T> messageProcessing(List<UserMsgVo> userMsgVos,
                                               Function<UserMsgVo, T> mapper) {
        return userMsgVos.stream()
                .collect(Collectors.toMap(
                        UserMsgVo::getUserId,
                        mapper,
                        (a, b) -> a));
    }
}
```

## MessageSendTypeEnum

[MessageSendTypeEnum.java](file:///d:/projects/jimuoffice/antflow-base/src/main/java/org/openoa/base/constant/enums/MessageSendTypeEnum.java):

| 枚举 | code | desc | 是否已实现 |
|---|:---:|---|:---:|
| `ALL` | 50 | 所有类型 | — (聚合标识) |
| `MAIL` | 1 | 邮件 | ✅ |
| `MESSAGE` | 2 | 短信 | ✅ |
| `PUSH` | 3 | APP-PUSH | ✅ |
| `WECHAT_PUSH` | 5 | 企微消息 | ❌ |
| `DING_TALK_TYPE` | 6 | 钉钉 | ❌ |
| `FEISHU_TYPE` | 7 | 飞书 | ❌ |

> 后 3 种已预定义但适配器未实现,是低成本的扩展切入点。

## 内置 3 个适配器

### EmailSendAdaptor

[EmailSendAdaptor.java](file:///d:/projects/jimuoffice/antflow-engine/src/main/java/org/openoa/engine/bpmnconf/adp/processnotice/EmailSendAdaptor.java):

```java
@Component
public class EmailSendAdaptor extends AbstractMessageSendAdaptor<MailInfo> {

    @Override
    public void sendMessageBatchByType(List<UserMsgVo> userMsgVos) {
        Map<String, MailInfo> map = messageProcessing(userMsgVos, UserMsgUtils::buildMailInfo);
        messageService.sendMailBatch(map);   // @Async
    }

    @Override
    public void setSupportBusinessObjects() {
        addSupportBusinessObjects(MessageSendTypeEnum.MAIL);
    }
}
```

### SMSSendAdaptor

```java
@Component
public class SMSSendAdaptor extends AbstractMessageSendAdaptor<MessageInfo> {
    @Override
    public void sendMessageBatchByType(List<UserMsgVo> userMsgVos) {
        Map<String, MessageInfo> map = messageProcessing(userMsgVos, UserMsgUtils::buildMessageInfo);
        messageService.sendSmsBatch(map);
    }

    @Override
    public void setSupportBusinessObjects() {
        addSupportBusinessObjects(MessageSendTypeEnum.MESSAGE);
    }
}
```

### AppPushAdaptor

```java
@Component
public class AppPushAdaptor extends AbstractMessageSendAdaptor<BaseMsgInfo> {
    @Override
    public void sendMessageBatchByType(List<UserMsgVo> userMsgVos) {
        Map<String, BaseMsgInfo> map = messageProcessing(userMsgVos, UserMsgUtils::buildBaseMsgInfo);
        messageService.sendAppPushBatch(map);
    }

    @Override
    public void setSupportBusinessObjects() {
        addSupportBusinessObjects(MessageSendTypeEnum.PUSH);
    }
}
```

## 注册机制

[IAdaptorFactory](file:///d:/projects/jimuoffice/antflow-engine/src/main/java/org/openoa/engine/factory/IAdaptorFactory.java) 通过 `@AutoParse` 注解声明:

```java
@AutoParse
ProcessNoticeAdaptor getProcessNoticeAdaptor(MessageSendTypeEnum messageSendTypeEnum);
```

[UserMsgUtils.sendMessages](file:///d:/projects/jimuoffice/antflow-engine/src/main/java/org/openoa/engine/utils/UserMsgUtils.java):

```java
public static void sendMessages(List<UserMsgVo> userMsgVos, MessageSendTypeEnum[] sendTypes) {
    for (MessageSendTypeEnum sendType : sendTypes) {
        ProcessNoticeAdaptor adaptor = adaptorFactory.getProcessNoticeAdaptor(sendType);
        if (adaptor != null) {
            adaptor.sendMessageBatchByType(userMsgVos);
        } else {
            log.warn("未实现的消息发送策略!{}", sendType);
        }
    }
}
```

## 自定义示例:钉钉适配器

利用已定义的 `MessageSendTypeEnum.DING_TALK_TYPE`,无需改枚举:

```java
package com.yourcompany.workflow.notice;

import lombok.extern.slf4j.Slf4j;
import org.openoa.base.constant.enums.MessageSendTypeEnum;
import org.openoa.base.vo.UserMsgVo;
import org.openoa.engine.bpmnconf.adp.processnotice.AbstractMessageSendAdaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class DingTalkSendAdaptor extends AbstractMessageSendAdaptor<String> {

    @Autowired
    private DingTalkClient dingTalkClient;  // 企业自有钉钉 SDK 封装

    @Override
    public void sendMessageBatchByType(List<UserMsgVo> userMsgVos) {
        // 1. 复用父类工具方法,把 UserMsgVo 转为钉钉 markdown 内容
        Map<String, String> userIdToMarkdown = super.messageProcessing(
                userMsgVos, this::buildDingTalkMarkdown);
        if (userIdToMarkdown == null) return;

        // 2. 调钉钉工作通知接口
        for (Map.Entry<String, String> entry : userIdToMarkdown.entrySet()) {
            try {
                dingTalkClient.sendWorkNotice(entry.getKey(), entry.getValue());
            } catch (Exception e) {
                log.error("钉钉消息发送失败 userId={}", entry.getKey(), e);
            }
        }
    }

    @Override
    public void setSupportBusinessObjects() {
        addSupportBusinessObjects(MessageSendTypeEnum.DING_TALK_TYPE);
    }

    private String buildDingTalkMarkdown(UserMsgVo msg) {
        return String.format("### %s\n\n%s\n\n[点击查看](%s)",
                msg.getTitle(), msg.getContent(), msg.getAppUrl());
    }
}
```

## 自定义示例:企业微信适配器

完全同构,只是换枚举与 SDK:

```java
@Component
public class WeChatWorkSendAdaptor extends AbstractMessageSendAdaptor<String> {

    @Autowired
    private WeChatWorkClient weChatWorkClient;

    @Override
    public void sendMessageBatchByType(List<UserMsgVo> userMsgVos) {
        Map<String, String> map = super.messageProcessing(userMsgVos, this::buildWeChatMarkdown);
        for (Map.Entry<String, String> entry : map.entrySet()) {
            weChatWorkClient.sendWorkNotice(entry.getKey(), entry.getValue());
        }
    }

    @Override
    public void setSupportBusinessObjects() {
        addSupportBusinessObjects(MessageSendTypeEnum.WECHAT_PUSH);
    }

    private String buildWeChatMarkdown(UserMsgVo msg) {
        return msg.getTitle() + "\n" + msg.getContent() + "\n" + msg.getAppUrl();
    }
}
```

## 完全自定义新通道

如果 `MessageSendTypeEnum` 没有预定义你需要的通道,需要先扩枚举:

### 步骤 1:扩展 MessageSendTypeEnum

```java
// 在 MessageSendTypeEnum 中新增
FEISHU_TYPE(7, "飞书");  // 已预定义
// 或完全自定义
SLACK_TYPE(10, "Slack");
```

### 步骤 2:实现适配器(同上)

### 步骤 3:在流程配置中开启通道

在 `BpmnConfConfigJson.noticeChannelTypes` 数组追加通道 code:

```json
{
  "noticeChannelTypes": [1, 2, 3, 6]  // 邮件+短信+App+钉钉
}
```

前端在流程设计器「基础设置 → 通知渠道」勾选即可。

## 重要说明

> **`MessageServiceImpl` 中没有 `sendDingTalkBatch` 等方法**,自定义适配器不要走 `messageService`,直接调企业自己的 SDK 即可。
>
> 内置 3 个适配器使用 `messageService` 是因为它们走 AntFlow 自身的邮件/短信/App 推送基础设施。自定义通道属于企业自有基础设施,应直接对接。

## 涉及的数据库表

| 表名 | 用途 |
|---|---|
| `t_user_message` | 已发送消息记录 |
| `t_user_message_status` | 用户消息读状态 + 渠道开启状态 |
| `t_information_template` | 消息模板主表 |
| `t_bpmn_conf_notice_template` | 流程-模板关联 |
| `t_bpmn_conf_notice_template_detail` | 按 bpmnCode + msgNoticeType 维度的模板明细 |

## 小结

- AntFlow 内置 3 种渠道(邮件/短信/App),预定义 3 种未实现(企微/钉钉/飞书)
- 适配器统一继承 `AbstractMessageSendAdaptor`,实现 `sendMessageBatchByType` 与 `setSupportBusinessObjects`
- 注册机制:Spring 启动扫描 `@Component`,通过 `isSupportBusinessObject(MessageSendTypeEnum)` 匹配
- 自定义钉钉/企微适配器只需 1 步:实现适配器类(枚举已预定义)
- 自定义完全新通道需 3 步:扩枚举 → 实现适配器 → 流程配置开启通道
- 自定义适配器**不走** `MessageServiceImpl`,直接调企业自有 SDK

下一节 [集成现有系统](/dev-guide/integrate-existing) 介绍如何通过 Spring Boot Starter 接入企业用户/角色/表单系统。
