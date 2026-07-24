# 流程分类管理

> 流程分类是 AntFlow 工作流的入口。每个流程(请假、报销、采购…)在系统中以一个 **FormCode**(类型标识)为唯一标识,归属于 LF(低代码)或 DIY(自定义表单)类型。

## 三层概念辨析

AntFlow 中"流程分类"在不同语境下指代不同的东西,需先理清:

| 概念 | 存储位置 | 作用 |
|---|---|---|
| **FormCode(类型标识)** | `t_bpmn_conf.form_code` 字段 | 流程家族唯一标识,如 `LEAVE_WMA`。一个 FormCode 可有多个版本 |
| **BpmProcessCategory** | `bpm_process_category` 表 | PC/APP 端发起页面的分类目录(如"行政类"、"人事类"),与流程定义无强绑定 |
| **DIY / LF 类型** | `t_bpmn_conf.is_lowcode_flow` 字段 | 0=DIY 自定义表单流程,1=LF 低代码表单流程 |

同一个 FormCode 在创建时即决定是 LF 还是 DIY 类型,通过不同的设计器入口(`lf-design` vs `diy-design`)进行设计。

## 入口

登录系统后:**流程管理 → 流程类型** (`/workflow/flowCategory`)

![流程类型列表](/images/2-1.png)

页面采用 `el-tabs` 双 Tab 结构:

- **流程分类(LF)**:调用 `getLFFormCodePageList` 分页查询 LF 类型 FormCode,支持新增
- **流程分类(DIY)**:调用 `getDIYFromCodeData` 查询 DIY 类型 FormCode,**不支持前端直接新增**(需后端先实现 `FormOperationAdaptor` 接口后才能在列表中显示)

源码位置:[antflow-vue/src/views/workflow/flowCategory/index.vue](https://github.com/mrtylerzhou/AntFlow/blob/master/antflow-vue/src/views/workflow/flowCategory/index.vue)

## 新增 LF 流程分类

点击"新增"按钮,弹出对话框:

![新增流程分类](/images/2-2.png)

| 字段 | 校验规则 | 说明 |
|---|---|---|
| 类型标识 | `^[A-Z]{4,10}$`(只能大写字母,4-10位) | FormCode,流程类型唯一标识,如 `LEAVE_WMA` |
| 类型名称 | `^[\u4e00-\u9fa5]{4,10}$`(必须汉字,4-10位) | 流程显示名称,如"请假流程" |
| 备注 | 无 | 文本域,可选 |

::: warning FormCode 命名规范
- 必须**全大写字母**,4-10 位
- 一旦创建不可修改(后续版本都绑定此标识)
- 推荐使用有业务含义的命名,如 `LEAVE_WMA`(请假)、`EXPENSE_WMA`(报销)
- 后端代码通过 FormCode 关联业务表单实现(`@ActivitiServiceAnno("LEAVE_WMA")`)
:::

## 列表操作

每个流程类型行支持以下操作:

| 操作 | 说明 | 跳转 |
|---|---|---|
| **流程设计** | 进入流程设计器 | LF → `/workflow/lf-design`<br>DIY → `/workflow/diy-design` |
| **版本管理** | 查看该 FormCode 下所有版本 | `/workflow/flow-version` |
| **查看表单** | 预览流程表单(LF 专属) | 弹窗预览 |
| **通知设置** | 配置该流程的通知规则 | 弹窗配置 |

跳转时携带参数:`fc`(FormCode)和 `fcname`(类型名称,URL 编码)。

```javascript
// antflow-vue/src/views/workflow/flowCategory/index.vue
async function handleLFDesign(row) {
    const param = { fcname: encodeURIComponent(row.value), fc: row.key };
    proxy.$tab.openPage({ path: "/workflow/lf-design", query: param });
}
```

## 后端实现

### LF FormCode 管理接口

**Controller**:[LowCodeFlowController.java](https://github.com/mrtylerzhou/AntFlow/blob/master/antflow-engine/src/main/java/org/openoa/engine/bpmnconf/controller/LowCodeFlowController.java)

路由前缀 `/lowcode`,核心接口:

| 接口 | 方法 | 作用 |
|---|---|---|
| `/getLFFormCodePageList` | POST | 分页查询 LF FormCode(列表页) |
| `/getLFActiveFormCodePageList` | POST | 已设计且启用的 LF FormCode(发起页) |
| `/createLowCodeFormCode` | POST | 新建 LF FormCode |
| `/getStartFormData` | GET | 发起页表单数据(兼容内联/外部表单) |

### FormCode 数据结构

**VO**:[BaseKeyValueStruVo.java](https://github.com/mrtylerzhou/AntFlow/blob/master/antflow-base/src/main/java/org/openoa/base/vo/BaseKeyValueStruVo.java)

```java
public class BaseKeyValueStruVo {
    private String key;          // FormCode
    private String value;        // 类型名称
    private String type;         // LF / DIY
    private String remark;       // 备注
    private Date createTime;
    private Boolean hasStarUserChooseModule;  // 是否包含发起人自选模块
    private List<BaseNumIdStruVo> processNotices;  // 通知配置
    private List<BpmnTemplateVo> templateVos;      // 模板配置
}
```

### 流程分类目录(BpmProcessCategory)

独立于 FormCode 的另一套分类体系,用于**发起页面的目录展示**(如 PC 端按"行政"、"人事"分组展示流程)。

**实体**:[BpmProcessCategory.java](https://github.com/mrtylerzhou/AntFlow/blob/master/antflow-base/src/main/java/org/openoa/base/entity/BpmProcessCategory.java)

表 `bpm_process_category`,字段:`id`、`process_type_name`、`is_app`(0=PC,1=APP)、`entrance`、`sort`、`is_del`、`tenant_id`、`state`。

::: tip 与 FormCode 的关系
BpmProcessCategory 与 FormCode 是**独立的**两套分类体系:
- FormCode 是流程的**业务标识**,后端代码通过它关联业务逻辑
- BpmProcessCategory 是流程的**展示分组**,仅用于前端 UI 分组,与流程定义无强绑定
:::

## DIY 流程的特殊性

DIY(自定义表单)流程**不能在前端直接新增**,因为:

1. 后端必须先实现 `FormOperationAdaptor` 接口并标注 `@ActivitiServiceAnno("FORM_CODE")`
2. 前端必须在 `views/workflow/components/forms/` 下创建对应的表单组件
3. 在 `bizFormMaps`(const.js)中注册 FormCode → 组件路径映射

完成后端和前端代码后,系统会自动在 DIY 列表中显示出该 FormCode。

```javascript
// antflow-vue/src/utils/flow/const.js
export const bizFormMaps = new Map([
  ['DSFZH_WMA', '/forms/form1.vue'],
  ['LEAVE_WMA', '/forms/form2.vue'],
  ['UCARREFUEl_WMA', '/forms/form3.vue']
]);
```

详见 [集成现有系统](/dev-guide/integrate-existing)。

## 下一步

- [流程设计器](/workflow-design/flow-designer) — 了解如何设计流程节点
- [低代码表单设计](/workflow-design/form-design) — 学习 vform 表单设计器
- [节点类型详解](/workflow-design/node-types) — 理解各种节点类型
