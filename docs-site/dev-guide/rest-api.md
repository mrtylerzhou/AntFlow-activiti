# REST API 参考

> AntFlow 引擎层提供 12 个 Controller,共 80+ 个 REST 端点,覆盖流程定义、流程实例、审批操作、消息模板、低代码表单、Saas 接入等全部功能。本章按功能分组整理所有端点,便于二次开发与集成。

## Controller 总览

所有 Controller 位于 [antflow-engine/.../controller/](file:///d:/projects/jimuoffice/antflow-engine/src/main/java/org/openoa/engine/bpmnconf/controller/):

| Controller | 基础路径 | 端点数 | 功能分组 |
|---|---|:---:|---|
| `BpmnConfController` | `/bpmnConf` | 12 | 流程定义/实例/审批 |
| `BpmProcessControlController` | `/taskMgmt` | 4 | 任务管理/字典 |
| `BpmnBusinessController` | `/bpmnBusiness` | 6 | 业务/DIY 表单/委托 |
| `UserController` | `/user` | 7 | 用户/角色查询 |
| `InformationTemplateController` | `/informationTemplates` | 13 | 消息模板 |
| `LowCodeFlowController` | `/lowcode` | 14 | 低代码表单管理 |
| `LowFlowBusinessController` | `/lowFlowBusiness` | 1 | 低代码业务数据 |
| `BpmBusinessDraftController` | `/processDraft` | 1 | 草稿 |
| `SysVersionController` | `/appVersion` | 5 | 系统版本 |
| `OutSideBpmAccessController` | `/outSide` | 5 | 三方接入核心 |
| `OutSideBpmBusinessController` | `/outSideBpm` | 14 | 三方业务方/模板 |
| `OutSideBpmCallbackUrlConfController` | `/outSideBpm` | 4 | 三方回调配置 |

## 流程定义/实例/审批:BpmnConfController

[BpmnConfController.java](file:///d:/projects/jimuoffice/antflow-engine/src/main/java/org/openoa/engine/bpmnconf/controller/BpmnConfController.java):

| 方法 | 路径 | 入参 | 出参 | 说明 |
|---|---|---|---|---|
| GET | `/bpmnConf/todoList` | — | `Result<TaskMgmtVO>` | 首页代办统计 |
| POST | `/bpmnConf/edit` | `BpmnConfVo` | `Result<String>` | 流程设计发布/复制 |
| POST | `/bpmnConf/listPage` | `ConfDetailRequestDto` | `Result<ResultAndPage<BpmnConfVo>>` | 流程设计列表 |
| POST | `/bpmnConf/preview` | `String params`(JSON) | `Result` | 设计期预览 |
| POST | `/bpmnConf/startPagePreviewNode` | `String params` | `Result<PreviewNode>` | 发起/审批页预览 |
| POST | `/bpmnConf/loadNodeOperationUser` | `String params` | `Result<List<BaseIdTranStruVo>>` | 节点实际操作人 |
| GET | `/bpmnConf/getBpmVerifyInfoVos` | `processNumber` | `Result<List<BpmVerifyInfoVo>>` | 审批进度 |
| POST | `/bpmnConf/process/viewBusinessProcess` | `values, formCode` | `Result<BusinessDataVo>` | 审批页业务数据 |
| POST | `/bpmnConf/process/buttonsOperation` | `values, formCode` | `Result` | **审批操作**(1 发起/2 重新提交/3 审批…) |
| GET | `/bpmnConf/effectiveBpmn/{id}` | `id` | `Result` | 启用流程版本 |
| @RequestMapping | `/bpmnConf/detail/{id}` | `id` | `Result<BpmnConfVo>` | 流程详情 |
| @RequestMapping | `/bpmnConf/process/listPage/{type}` | `DetailRequestDto, type` | `ResultAndPage<TaskMgmtVO>` | 任务列表分页 |

### listPage type 枚举

| type | 含义 |
|:---:|---|
| 3 | 我的发起 |
| 4 | 我的已办 |
| 5 | 我的代办 |
| 6 | 全部流程(监控) |
| 7 | 退回给我 |
| 8 | 全部流程(监控 V2) |
| 9 | 抄送给我 |

## 任务管理/字典:BpmProcessControlController

| 方法 | 路径 | 入参 | 出参 |
|---|---|---|---|
| POST | `/taskMgmt/taskMgmt` | `ProcessConfVo` | `Result` |
| GET | `/taskMgmt/getFormRelatedOptions` | — | `Result<List<BaseNumIdStruVo>>` |
| GET | `/taskMgmt/getUDROptions` | — | `Result<List<BaseIdTranStruVo>>` |
| GET | `/taskMgmt/getDictDataByType` | `dictType` | `Result<List<BaseIdTranStruVo>>` |

## 业务/DIY 表单/委托:BpmnBusinessController

| 方法 | 路径 | 入参 | 出参 |
|---|---|---|---|
| GET | `/bpmnBusiness/getDIYFormCodeList` | `desc` | `Result<List<DIYProcessInfoDTO>>` |
| POST | `/bpmnBusiness/getAllFormCodeList` | `desc` | `Result` (DIY+LF+SaaS) |
| POST | `/bpmnBusiness/entrustlist/{type}` | `DetailRequestDto, type` | `ResultAndPage<Entrust>` |
| GET | `/bpmnBusiness/entrustDetail/{id}` | `id` | `Result<UserEntrust>` |
| POST | `/bpmnBusiness/editEntrust` | `DataVo` | `Result<String>` |
| GET | `/bpmnBusiness/getStartUserChooseModules` | `formCode` | `Result<List<BpmnNodeVo>>` |

## 用户/角色查询:UserController

| 方法 | 路径 | 入参 | 出参 |
|---|---|---|---|
| @RequestMapping | `/user/queryUserByNameFuzzy` | `userName` | `Result<List<BaseIdTranStruVo>>` |
| @RequestMapping | `/user/queryCompanyByNameFuzzy` | `companyName` | `Result` |
| GET | `/user/getUser` 或 `/user/getUser/{roleId}` | `roleId`(可选) | `Result<List<BaseIdTranStruVo>>` |
| POST | `/user/getUserPageList` | `DetailRequestDto` | `ResultAndPage<BaseIdTranStruVo>` |
| GET | `/user/getRoleInfo` | — | `Result<List<BaseIdTranStruVo>>` |
| GET | `/user/queryNodeAssigneesByNodeId` | `processNumber, nodeId` | `Result<List<BaseIdTranStruVo>>` |
| GET | `/user/queryNodeAssigneesByElementId` | `processNumber, elementId` | `Result<List<BaseIdTranStruVo>>` |

## 消息模板:InformationTemplateController

[InformationTemplateController.java](file:///d:/projects/jimuoffice/antflow-engine/src/main/java/org/openoa/engine/bpmnconf/controller/InformationTemplateController.java):

| 方法 | 路径 | 入参 | 出参 |
|---|---|---|---|
| POST | `/informationTemplates/listPage` | `InformationPgeRequestDto` | `ResultAndPage` |
| GET | `/informationTemplates/getInformationTemplateById` | `templateId` | `Result<InformationTemplateVo>` |
| POST | `/informationTemplates/updateById` | `InformationTemplateVo` | `Result` |
| POST | `/informationTemplates/save` | `InformationTemplateVo` | `Result<Long>` |
| POST | `/informationTemplates/deleteById` | `id` | `Result` |
| GET | `/informationTemplates/listByName` | `name`(可选) | `Result<List<InformationTemplate>>` |
| GET | `/informationTemplates/defaultTemplates` | — | `Result` |
| POST | `/informationTemplates/setDefaultTemplates` | `DefaultTemplateVo[]` | `Result` |
| GET | `/informationTemplates/getWildcardCharacter` | `name`(可选) | `Result<List<EnumerateVo>>` |
| GET | `/informationTemplates/getProcessEvents` | — | `Result<List<BaseNumIdStruVo>>` |
| GET | `/informationTemplates/getAllNoticeTypes` | — | `Result<List<BaseNumIdStruVo>>` |
| GET | `/informationTemplates/getNoticeTypeByFormCode` | `formCode` | `Result<List<BaseNumIdStruVo>>` |
| GET | `/informationTemplates/testDoTimeoutReminder` | — | `void` |

## 低代码表单:LowCodeFlowController

| 方法 | 路径 | 入参 | 出参 |
|---|---|---|---|
| GET | `/lowcode/getLowCodeFlowFormCodes` | — | `Result<List<BaseKeyValueStruVo>>` |
| POST | `/lowcode/getLFFormCodePageList` | `DetailRequestDto` | `ResultAndPage<BaseKeyValueStruVo>` |
| POST | `/lowcode/getLFActiveFormCodePageList` | `DetailRequestDto` | `ResultAndPage<BaseKeyValueStruVo>` |
| GET | `/lowcode/getformDataByFormCode` | `formCode` | `Result<String>` |
| GET | `/lowcode/getStartFormData` | `formCode` | `Result<LfStartFormVo>` |
| POST | `/lowcode/createLowCodeFormCode` | `BaseKeyValueStruVo` | `Result` |
| POST | `/lowcode/form/listPage` | `DetailRequestDto` | `ResultAndPage<LfFormManageVo>` |
| GET | `/lowcode/form/{id}` | `id` | `Result<LfFormManageVo>` |
| POST | `/lowcode/form/save` | `LfFormManageVo` | `Result<Long>` |
| DELETE | `/lowcode/form/{id}` | `id` | `Result<Void>` |
| GET | `/lowcode/form/history` | `formCode` | `Result<List<LfFormManageVo>>` |
| GET | `/lowcode/form/listEffectiveForSelect` | — | `Result<List<LfFormManageVo>>` |
| PUT | `/lowcode/form/effective/{id}` | `id` | `Result` |
| GET | `/lowcode/form/references/{formdataId}` | `formdataId` | `Result<List<BpmnConfVo>>` |

## 草稿:BpmBusinessDraftController

| 方法 | 路径 | 入参 | 出参 |
|---|---|---|---|
| GET | `/processDraft/loadDraft` | `formCode` | `Result<BusinessDataVo>` |

## 系统版本:SysVersionController

| 方法 | 路径 | 入参 | 出参 |
|---|---|---|---|
| GET | `/appVersion/appVersion` | — | `Result` |
| GET | `/appVersion/getQrCode` | — | `Result` |
| GET | `/appVersion/versionList` | — | `Result` |
| POST | `/appVersion/{id}` | `id` | `Result` |
| POST | `/appVersion/save` | body | `Result` |

## 三方接入:OutSideBpmAccessController

[OutSideBpmAccessController.java](file:///d:/projects/jimuoffice/antflow-engine/src/main/java/org/openoa/engine/bpmnconf/controller/OutSideBpmAccessController.java):

| 方法 | 路径 | 入参 | 出参 | 说明 |
|---|---|---|---|---|
| POST | `/outSide/processSubmit` | `OutSideBpmAccessBusinessVo` | `Result<OutSideBpmAccessRespVo>` | 三方流程发起 |
| POST | `/outSide/getOutSideFormCodePageList` | `ConfDetailRequestDto` | `ResultAndPage<BpmnConfVo>` | 三方 FormCode 列表 |
| POST | `/outSide/processPreview` | `OutSideBpmAccessBusinessVo` | `Result` | 三方流程预览 |
| POST | `/outSide/processBreak` | `OutSideBpmAccessBusinessVo` | `Result` | 三方流程中断 |
| GET | `/outSide/outSideProcessRecord` | `processNumber` | `Result` | 三方流程记录查询 |

## 三方业务方/模板:OutSideBpmBusinessController

| 方法 | 路径 | 入参 | 出参 |
|---|---|---|---|
| POST | `/outSideBpm/businessParty/listPage` | `DetailRequestDto` | `ResultAndPage<OutSideBpmBusinessParty>` |
| GET | `/outSideBpm/businessParty/detail/{id}` | `id` | `Result<OutSideBpmBusinessParty>` |
| POST | `/outSideBpm/businessParty/edit` | `OutSideBpmBusinessParty` | `Result` |
| GET | `/outSideBpm/businessParty/appList/{id}` | `id` | `Result<List<BpmProcessAppApplication>>` |
| POST | `/outSideBpm/businessParty/addApp` | `BpmProcessAppApplication` | `Result` |
| GET | `/outSideBpm/businessParty/appDetail/{id}` | `id` | `Result<BpmProcessAppApplication>` |
| POST | `/outSideBpm/conditionTemplate/listPage` | `DetailRequestDto` | `ResultAndPage<OutSideBpmConditionsTemplate>` |
| GET | `/outSideBpm/conditionTemplate/listByAppId/{appId}` | `appId` | `Result<List<OutSideBpmConditionsTemplate>>` |
| POST | `/outSideBpm/conditionTemplate/edit` | `OutSideBpmConditionsTemplate` | `Result` |
| POST | `/outSideBpm/conditionTemplate/delete/{id}` | `id` | `Result` |
| POST | `/outSideBpm/approveTemplate/listPage` | `DetailRequestDto` | `ResultAndPage<OutSideBpmApproveTemplate>` |
| GET | `/outSideBpm/approveTemplate/listByAppId/{appId}` | `appId` | `Result<List<OutSideBpmApproveTemplate>>` |
| POST | `/outSideBpm/approveTemplate/edit` | `OutSideBpmApproveTemplate` | `Result` |
| GET | `/outSideBpm/approveTemplate/detail/{id}` | `id` | `Result<OutSideBpmApproveTemplate>` |

## 三方回调配置:OutSideBpmCallbackUrlConfController

| 方法 | 路径 | 入参 | 出参 |
|---|---|---|---|
| GET | `/outSideBpm/callbackUrlConf/list/{formCode}` | `formCode` | `Result<List<OutSideBpmCallbackUrlConf>>` |
| POST | `/outSideBpm/callbackUrlConf/listPage` | `DetailRequestDto` | `ResultAndPage<OutSideBpmCallbackUrlConf>` |
| GET | `/outSideBpm/callbackUrlConf/detail/{id}` | `id` | `Result<OutSideBpmCallbackUrlConf>` |
| POST | `/outSideBpm/callbackUrlConf/edit` | `OutSideBpmCallbackUrlConf` | `Result` |

## 通用响应格式

所有接口统一返回 `Result<T>`:

```typescript
interface Result<T> {
  code: number;       // 200=成功,其他=失败
  msg: string;        // 提示信息
  data: T;            // 业务数据
}

interface ResultAndPage<T> {
  list: T[];
  total: number;
  pageNum: number;
  pageSize: number;
  pages: number;
}
```

## 鉴权

AntFlow 使用 Spring Security + JWT 鉴权,所有接口需要在 Header 携带:

```
Authorization: Bearer {token}
```

登录接口:`POST /login`(由 antflow-web 模块提供)

## 接口文档

AntFlow 集成 Knife4j(Swagger 增强),启动后端后访问:

```
http://localhost:7001/doc.html
```

可直接在线测试所有接口。

## 小结

- AntFlow 共 12 个 Controller,80+ REST 端点,覆盖全部功能
- 核心审批操作统一入口 `POST /bpmnConf/process/buttonsOperation`,通过 `operationType` 区分 30+ 种操作
- 任务列表统一入口 `POST /bpmnConf/process/listPage/{type}`,通过 type 区分 5 种视图
- 三方接入提供 23 个 `/outSide` + `/outSideBpm` 端点,支持完整 SaaS 场景
- 所有接口返回统一的 `Result<T>` 格式,易于前端处理

下一节 [数据库设计](/dev-guide/db-design) 详解核心表结构与字段含义。
