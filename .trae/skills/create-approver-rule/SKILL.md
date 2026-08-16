---
name: "create-approver-rule"
description: "Creates a new approver rule in AntFlow (backend Provider+Adaptor, frontend radio+config). Invoke when user asks to add/create a new approver rule type to the workflow designer."
---

# 创建新的审批人规则

本 skill 指导在 AntFlow 中新增一种"审批人规则"（审批人设置 tab 里的 radio 选项，如"指定人员""根据标签选择"等）的完整流程。涵盖后端 Provider + Adaptor + 枚举注册，前端 radio + 配置区 + 提交格式化 + 回显。

## 何时使用

当用户要求新增一种审批人规则类型时（例如"加一个根据XXX选择审批人的规则""新增XXX审批方式"），立即按本 skill 步骤执行。

## 前置：确认关键设计点

开始编码前，先用 AskUserQuestion 澄清以下几点（参考 label-based-approver-rule 的澄清过程）：

1. **评估时机**：规则在流程发起时评估（统一模式，推荐）还是运行时动态评估？
   - AntFlow 所有规则统一在发起时评估，评估结果写入按实例部署的 BPMN XML。优先采用发起时评估。
2. **paramType**：单人（SINGLE）还是多人（MULTIPLAYER）？
3. **规则配置存储**：单值字段还是列表？存在 `BpmnNodePropertysVo` 还是 `BpmnNodeVo`？
4. **signType 兼容性**：是否复用节点 signType（会签/或签/顺序会签/仲裁签）？是否禁用某些 signType？
5. **找人机制**：是否需要调用 AfUserService 的自定义方法？默认实现返回什么？
6. **前端 radio value**：是否直接对应后端 nodeProperty（推荐），还是像"上一节点指定"那样需要转换？
7. **节点类型互斥**：是否仅普通审批人节点（nodeType=4）可配置？与抄送/自动/条件节点是否互斥？
8. **校验规则**：规则配置的字段校验（必填、重复、数量上限等）。

## 后端改动步骤

### 步骤 1：新增 NodePropertyEnum 枚举值

文件：`antflow-base/src/main/java/org/openoa/base/constant/enums/NodePropertyEnum.java`

```java
NODE_PROPERTY_XXX(20, "规则名称", 1, BPMN_NODE_PARAM_MULTIPLAYER),
```

- code：避开已用值（查现有枚举，20 是"根据标签选择"占用的）
- paramType：`BPMN_NODE_PARAM_SINGLE`（1）或 `BPMN_NODE_PARAM_MULTIPLAYER`（2）

### 步骤 2：新增 PersonnelEnum 枚举值

文件：`antflow-base/src/main/java/org/openoa/base/constant/enums/PersonnelEnum.java`

```java
XXX_PERSONNEL(NODE_PROPERTY_XXX, "规则名称"),
```

### 步骤 3：新增规则配置 VO（如有配置字段）

文件：`antflow-base/src/main/java/org/openoa/base/vo/`

- 简单规则：直接复用 `BpmnNodePropertysVo` 现有字段
- 复杂规则：新建 VO（如 `LabelBasedApproverRuleVo`），含规则特有字段

### 步骤 4：在 BpmnNodePropertysVo 新增配置字段

文件：`antflow-base/src/main/java/org/openoa/base/vo/BpmnNodePropertysVo.java`

```java
private XxxRuleVo xxxRuleConfig;
```

该字段会随 `node_config_json` 序列化持久化。

### 步骤 5：新增 PersonnelProvider

文件：`antflow-engine/src/main/java/org/openoa/engine/bpmnconf/service/biz/personnelinfoprovider/`

```java
@Slf4j
@Component
public class XxxPersonnelProvider extends AbstractMissingAssignNodeAssigneeVoProvider {
    @Autowired
    private AfUserService afUserService;

    @Override
    public List<BpmnNodeParamsAssigneeVo> getAssigneeList(BpmnNodeVo bpmnNodeVo, BpmnStartConditionsVo startConditionsVo) {
        // 1. 读取规则配置
        BpmnNodePropertysVo property = bpmnNodeVo.getProperty();
        // ... 校验配置非空
        // 2. 调用找人服务（如有）
        BusinessDataVo businessDataVo = startConditionsVo.getBusinessDataVo();
        List<BaseIdTranStruVo> approvers = afUserService.queryXxx(businessDataVo, ruleConfig);
        if (CollectionUtils.isEmpty(approvers)) {
            approvers = new ArrayList<>();
        }
        // 3. super.provideAssigneeList 处理找不到人策略 + 额外加签/减签
        return super.provideAssigneeList(bpmnNodeVo, approvers);
    }

    // 校验方法（可选）
    private void validate(...) { ... }
}
```

- extends `AbstractMissingAssignNodeAssigneeVoProvider`：复用找不到人策略（跳过/转管理员/报错）
- `super.provideAssigneeList(bpmnNodeVo, approvers)` 会处理找不到人策略和额外加签/减签

### 步骤 6：新增 PersonnelAdaptor

文件：`antflow-engine/src/main/java/org/openoa/engine/bpmnconf/adp/personneladp/`

```java
@Component
public class XxxPersonnelAdaptor extends AbstractBpmnPersonnelAdaptor {
    public XxxPersonnelAdaptor(BpmnEmployeeInfoProviderService bpmnEmployeeInfoProviderService,
                               @Qualifier("xxxPersonnelProvider") BpmnPersonnelProviderService bpmnPersonnelProviderService) {
        super(bpmnEmployeeInfoProviderService, bpmnPersonnelProviderService);
    }

    @Override
    public void setSupportBusinessObjects() {
        addSupportBusinessObjects(PersonnelEnum.XXX_PERSONNEL);
    }
}
```

- 注册 PersonnelEnum，使 `BpmnPersonnelFormatImpl` 能匹配到此规则
- **不需要新建 BpmnElementAdaptor 子类**，由 `BpmnGeneralPurposeElementAdaptor` 回退处理，按 signType 生成多实例 BPMN 元素

### 步骤 7：扩展 AfUserService（如需自定义找人）

文件：`antflow-base/src/main/java/org/openoa/base/service/AfUserService.java`

```java
List<BaseIdTranStruVo> queryXxx(BusinessDataVo businessDataVo, XxxRuleVo ruleConfig);
```

文件：`antflow-base/src/main/java/org/openoa/base/service/UserServiceImpl.java`

```java
@Override
public List<BaseIdTranStruVo> queryXxx(BusinessDataVo businessDataVo, XxxRuleVo ruleConfig) {
    return Collections.emptyList(); // 默认实现,用户在此改写
}
```

## 前端改动步骤

### 步骤 8：const.js 新增 radio 选项

文件：`antflow-vue/src/utils/antflow/const.js`

```js
// setTypes 数组新增
{ value: 20, label: "规则名称" },

// 相关常量
export const XXX_SET_TYPE = 20;
```

### 步骤 9：approverDrawer.vue 新增配置区

文件：`antflow-vue/src/components/Workflow/drawer/approverDrawer.vue`

**模板**：在 setType==17 配置区后新增 setType==20 配置区

```vue
<div class="approver_text" v-if="approverConfig.setType == 20">
    <!-- 规则特有配置控件 -->
</div>
```

**脚本**：

```js
import { XXX_SET_TYPE } from '@/utils/antflow/const';

// 状态：优先用 computed 直接读 approverConfig 嵌套字段,避免双向同步 watch 递归
let xxxConfig = computed(() => approverConfig.value?.property?.xxxRuleConfig || null);

// changeType: 切到本规则时初始化,切离时清空
if (val == XXX_SET_TYPE) {
    approverConfig.value.signType = 2; // 默认会签
    if (!approverConfig.value.property) approverConfig.value.property = {};
    if (!approverConfig.value.property.xxxRuleConfig) {
        approverConfig.value.property.xxxRuleConfig = { /* 初始结构 */ };
    }
} else {
    if (approverConfig.value.property) approverConfig.value.property.xxxRuleConfig = null;
}

// approverConfig watch: 回显
if (val.setType == XXX_SET_TYPE) {
    // 回填状态
}

// saveApprover: 校验
if (approverConfig.value.setType == XXX_SET_TYPE) {
    // 校验配置字段
}
```

**signType 禁用**（如需）：在仲裁签 radio 加 `:disabled="approverConfig.setType == 20"`

### 步骤 10：formatcommit_data.js 新增提交分支

文件：`antflow-vue/src/utils/antflow/formatcommit_data.js`

```js
} else if (node.setType == 20) {
    approveObj.xxxRuleConfig = node.property?.xxxRuleConfig ?? null;
}
```

### 步骤 11：index.js 新增节点显示文本

文件：`antflow-vue/src/utils/antflow/index.js` 的 `_buildApproverBaseStr`

```js
} else if (nodeConfig.setType == 20) {
    return "规则名称:" + (nodeConfig.property?.xxxRuleConfig?.labelName || "");
}
```

## 关键陷阱与规范

### 1. Vue watch 递归（必看）

**禁止**：在已有 `watch(approverConfig, deep)` 的组件中，为 approverConfig 的子字段建立独立 ref + 双向同步 watch。会导致 `Maximum recursive updates exceeded`。

**正确**：用 `computed` 直接读 approverConfig 嵌套字段，让 v-model 写穿到原对象。

### 2. 不新建 BpmnElementAdaptor 子类

`BpmnGeneralPurposeElementAdaptor` 是通用元素适配器，无专用适配器匹配时回退处理。新规则只需新建 PersonnelAdaptor（注册 PersonnelEnum），元素生成由通用适配器完成。

### 3. 评估时机统一在发起时

所有审批人规则在流程发起时（submit）评估，评估结果写入按实例部署的 BPMN XML。不要在部署时或运行时评估（除非有特殊需求且已充分澄清）。

### 4. 找不到人策略

Provider extends `AbstractMissingAssignNodeAssigneeVoProvider`，调用 `super.provideAssigneeList(bpmnNodeVo, approvers)` 自动处理找不到人策略（跳过/转管理员/报错）和额外加签/减签。

### 5. 前端 radio value 直接对应后端 nodeProperty

优先采用直接对应（value=20 → nodeProperty=20），避免像"上一节点指定"那样的转换模式（value=19 → 转换为 setType=5 + 虚拟人 + 标识）。

### 6. 校验双重化

前端 `saveApprover` + 后端 Provider 双重校验规则配置字段（必填、重复、数量上限等）。

## 参考实现

完整参考：`.scratch/label-based-approver-rule-design.md`（"根据标签选择"规则的完整设计文档）

参考现有规则实现：
- `UserPointedPersonnelProvider` — 指定人员（最简单）
- `FormRelatedPersonnelProvider` — 表单关联人员（发起时用 businessDataVo）
- `UDRPersonnelProvider` — 自定义规则（发起时用 businessDataVo + 自定义配置）
