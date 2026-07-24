# 条件规则

> AntFlow 的条件评估采用**策略模式**,通过 `ConditionJudge` 接口为每种条件类型实现独立的判断逻辑。支持低代码字段、业务字段、JUEL/SpEL 表达式等多种条件类型。

## 条件类型总览

AntFlow 的条件类型定义在 [ConditionTypeEnum.java](https://github.com/mrtylerzhou/AntFlow/blob/master/antflow-engine/src/main/java/org/openoa/engine/bpmnconf/constant/enus/ConditionTypeEnum.java),分为三大类:

### 业务表单字段(Demo,可扩展)

| Code | 枚举值 | 描述 | 字段 |
|---|---|---|---|
| 1 | CONDITION_THIRD_ACCOUNT_TYPE | 账户类型 | accountType |
| 2 | CONDITION_BIZ_LEAVE_TIME | 请假时长 | leaveHour |
| 3 | CONDITION_PURCHASE_FEE | 采购金额 | planProcurementTotalMoney |
| 4 | CONDITION_TYPE_OUT_TOTAL_MONEY | 外出总金额 | outTotalMoney |
| 5 | CONDITION_JOB_LEVEL_TYPE | 职级 | jobLevelVo |
| 6 | CONDITION_PURCHASE_TYPE | 采购类型 | purchaseType |
| 7 | CONDITION_TYPE_NUMBER_OPERATOR | 数字运算 | numberOperator |
| 37 | CONDITION_THIRD_PARK_AREA | 园区面积 | parkArea |
| 38 | CONDITION_TYPE_TOTAL_MONEY | 总金额 | totalMoney |
| 9999 | CONDITION_TEMPLATEMARK | 模板标记 | templateMarks |

::: warning Demo 用途
Code 1-7、37、38、9999 是 AntFlow 自带的 demo 条件类型,用于参考编写自己的 Judge。生产环境中,这些类型可能不适用,需要根据业务自行扩展。
:::

### 低代码字段(LF 系列,核心)

| Code | 枚举值 | 描述 | 适用 vform 控件 |
|---|---|---|---|
| 10000 | LF_STRING_CONDITION | 字符串条件 | input、select(单选) |
| 10001 | LF_NUMBER_CONDITION | 数字条件 | number、radio、switch |
| 10002 | LF_DATE_CONDITION | 日期条件 | date、data-range |
| 10003 | LF_DATETIME_CONDITION | 日期时间条件 | time、time-range |
| 10004 | LF_COLLECTION_CONDITION | 集合条件 | checkbox、select(多选) |

### 表达式条件

| Code | 枚举值 | 描述 | 引擎 |
|---|---|---|---|
| 20000 | JUEL_EXPRESSION | JUEL 表达式 | Activiti 内置 JUEL |
| 20001 | SPEL_EXPRESSION | SpEL 表达式 | Spring Expression Language |

::: tip 表达式上下文
- **低代码流程**:以 `lfConditions`(表单字段 Map)为上下文
  - JUEL:每个 entry 作为变量
  - SpEL:以 lfConditions 为 root Map
- **DIY 流程**:以 `BusinessDataVo` 为 `#scriptContext` 变量
:::

## 比较运算符

[JudgeOperatorEnum.java](https://github.com/mrtylerzhou/AntFlow/blob/master/antflow-base/src/main/java/org/openoa/base/constant/enums/JudgeOperatorEnum.java) 定义了 9 种运算符:

| Code | 符号 | 描述 | 二元(区间) |
|---|---|---|:---:|
| 1 | `>=` | 大于等于 | 否 |
| 2 | `>` | 大于 | 否 |
| 3 | `<=` | 小于等于 | 否 |
| 4 | `<` | 小于 | 否 |
| 5 | `=` | 等于 | 否 |
| 6 | `first<a<second` | 严格区间 | 是 |
| 7 | `first<=a<second` | 左闭右开 | 是 |
| 8 | `first<a<=second` | 左开右闭 | 是 |
| 9 | `first<=a<=second` | 闭区间 | 是 |

二元运算时,`zdy1` 与 `zdy2` 在 DB 中以逗号拼接存储(如 `"1000,5000"`)。

## 逻辑关系(AND/OR)

[ConditionRelationShipEnum.java](https://github.com/mrtylerzhou/AntFlow/blob/master/antflow-base/src/main/java/org/openoa/base/constant/enums/ConditionRelationShipEnum.java):

| Code | Value | 描述 |
|---|---|---|
| 0 | false | AND(与) |
| 1 | true | OR(或) |

条件配置采用**双层 AND/OR**结构:
- **组间关系**(`groupRelation`):条件组之间的逻辑关系
- **组内关系**(`condRelation`):组内条件项之间的逻辑关系

## 条件配置结构

### 前端格式(BpmnNodeConditionsConfVueVo)

[前端 VO](https://github.com/mrtylerzhou/AntFlow/blob/master/antflow-base/src/main/java/org/openoa/base/vo/BpmnNodeConditionsConfVueVo.java) 直接对应 UI 字段:

```java
public class BpmnNodeConditionsConfVueVo {
    private String columnId;          // ConditionTypeEnum.code
    private Integer optType;          // JudgeOperatorEnum.code (1-9)
    private String zdy1;              // 值1(单值或区间下界)
    private String opt1;              // 区间左运算符 "<" 或 "<="
    private String zdy2;              // 值2(区间上界)
    private String opt2;              // 区间右运算符 "<" 或 "<="
    private String columnDbname;      // DB 字段名(vform 字段 name)
    private String columnType;        // String/Int/Double/Date/DateTime/Boolean
    private String fieldTypeName;     // vform 控件类型
    private Boolean multiple;         // 是否多选
    private String fixedDownBoxValue; // 下拉选项 JSON
    private Boolean condRelation;     // false=AND, true=OR (组内关系)
    private Integer condGroup;        // 组号
}
```

### conditionList 结构示例

```
conditionList: [
  [  // 条件组 1 (condGroup=1)
    { columnId: "10001", columnDbname: "amount", optType: 1, zdy1: "1000", condRelation: false },
    { columnId: "10000", columnDbname: "city",   optType: 5, zdy1: "北京", condRelation: false }
  ],
  [  // 条件组 2 (condGroup=2)
    { columnId: "10002", columnDbname: "applyDate", optType: 9, zdy1: "2026-01-01", opt1: "<=", zdy2: "2026-12-31", opt2: "<=", condRelation: false }
  ]
]
groupRelation: false  // 组间 AND
```

含义:`(金额>=1000 AND 城市=北京) OR (申请日期在 2026 年内)`

## 条件评估流程

<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 920 420" font-family="-apple-system, BlinkMacSystemFont, 'Segoe UI', sans-serif">
  <text x="460" y="24" text-anchor="middle" font-size="16" font-weight="700" fill="#1e293b">条件评估流程</text>

  <!-- 入口 -->
  <rect x="320" y="50" width="280" height="44" rx="8" fill="#eef2ff" stroke="#6366f1" stroke-width="1.5"/>
  <text x="460" y="78" text-anchor="middle" font-size="12" fill="#1e293b">Activiti 网关评估 sequence flow condition</text>
  <line x1="460" y1="94" x2="460" y2="114" stroke="#475569" stroke-width="2" marker-end="url(#d)"/>

  <!-- FormAdaptor -->
  <rect x="320" y="114" width="280" height="44" rx="8" fill="#fef3c7" stroke="#d97706" stroke-width="1.5"/>
  <text x="460" y="142" text-anchor="middle" font-size="12" fill="#1e293b">FormAdaptor.automaticCondition()</text>
  <line x1="460" y1="158" x2="460" y2="178" stroke="#475569" stroke-width="2" marker-end="url(#d)"/>

  <!-- ConditionService -->
  <rect x="320" y="178" width="280" height="44" rx="8" fill="#dcfce7" stroke="#16a34a" stroke-width="1.5"/>
  <text x="460" y="206" text-anchor="middle" font-size="12" fill="#1e293b">ConditionServiceImpl.checkMatchCondition()</text>
  <line x1="460" y1="222" x2="460" y2="242" stroke="#475569" stroke-width="2" marker-end="url(#d)"/>

  <!-- 遍历组 -->
  <rect x="280" y="242" width="360" height="44" rx="8" fill="#dbeafe" stroke="#3b82f6" stroke-width="1.5"/>
  <text x="460" y="270" text-anchor="middle" font-size="12" fill="#1e293b">遍历 groupedConditionParamTypes(组间 AND/OR)</text>
  <line x1="460" y1="286" x2="460" y2="306" stroke="#475569" stroke-width="2" marker-end="url(#d)"/>

  <!-- 派发 -->
  <rect x="200" y="306" width="520" height="44" rx="8" fill="#fce7f3" stroke="#db2777" stroke-width="1.5"/>
  <text x="460" y="334" text-anchor="middle" font-size="12" fill="#1e293b">SpringBeanUtils.getBean(conditionType.getConditionJudgeCls()).judge()</text>
  <line x1="460" y1="350" x2="460" y2="370" stroke="#475569" stroke-width="2" marker-end="url(#d)"/>

  <!-- Judge 实现 -->
  <rect x="40" y="370" width="160" height="40" rx="6" fill="#fff" stroke="#16a34a"/>
  <text x="120" y="395" text-anchor="middle" font-size="10" fill="#1e293b">LFStringConditionJudge</text>
  <rect x="210" y="370" width="160" height="40" rx="6" fill="#fff" stroke="#16a34a"/>
  <text x="290" y="395" text-anchor="middle" font-size="10" fill="#1e293b">LFNumberFormatJudge</text>
  <rect x="380" y="370" width="160" height="40" rx="6" fill="#fff" stroke="#16a34a"/>
  <text x="460" y="395" text-anchor="middle" font-size="10" fill="#1e293b">JuelExpressionJudge</text>
  <rect x="550" y="370" width="160" height="40" rx="6" fill="#fff" stroke="#16a34a"/>
  <text x="630" y="395" text-anchor="middle" font-size="10" fill="#1e293b">SpelExpressionJudge</text>
  <rect x="720" y="370" width="160" height="40" rx="6" fill="#fff" stroke="#16a34a"/>
  <text x="800" y="395" text-anchor="middle" font-size="10" fill="#1e293b">自定义 Judge</text>

  <defs><marker id="d" viewBox="0 0 10 10" refX="9" refY="5" markerWidth="6" markerHeight="6" orient="auto-start-reverse"><path d="M 0 0 L 10 5 L 0 10 z" fill="#475569"/></marker></defs>
</svg>

### 派发算法(ConditionServiceImpl)

[ConditionServiceImpl.checkMatchCondition](https://github.com/mrtylerzhou/AntFlow/blob/master/antflow-engine/src/main/java/org/openoa/engine/bpmnconf/adp/conditionfilter/ConditionServiceImpl.java) 的派发逻辑:

1. 取出 `conditionsConf.getGroupedConditionParamTypes()`(Map<组号, List<条件类型code>>)
2. 外层遍历条件组,`groupRelation` 决定组间 AND/OR
3. 内层遍历组内条件类型:
   - `ConditionTypeEnum.getEnumByCode(code)` 找枚举
   - `SpringBeanUtils.getBean(conditionType.getConditionJudgeCls()).judge(...)` 调用对应 Judge
   - `condRelation`(组内关系)短路:AND 遇 false break;OR 遇 true break
4. 组间短路:AND 遇 false 返回;OR 遇 true 返回

### 动态条件迁移检测

当流程版本迁移时,如果动态条件节点的评估结果发生变化,会抛 `CONDITION_CHANGED` 异常阻止迁移:

```java
// ConditionServiceImpl.java 行 108-126
if (isMigration && isPreview && isDynamicConditionGateway) {
    // 查 BpmDynamicConditionChoosen 表对比历史选择记录
    // 若条件结果变化则抛 CONDITION_CHANGED 异常
}
```

## ConditionJudge 实现

### 接口定义

[ConditionJudge.java](https://github.com/mrtylerzhou/AntFlow/blob/master/antflow-engine/src/main/java/org/openoa/engine/bpmnconf/adp/conditionfilter/ConditionJudge.java):

```java
public interface ConditionJudge {
   boolean judge(String nodeId, BpmnNodeConditionsConfBaseVo conditionsConf,
                 BpmnStartConditionsVo bpmnStartConditionsVo, int couDGroup, int index);
}
```

### 抽象基类层级

```
ConditionJudge (接口)
  └── AbstractComparableJudge           // 基础 BigDecimal 比较,支持 9 种操作符
        └── AbstractBinaryComparableJudge  // 支持 1 < a < 2 区间比较
              ├── AskLeaveJudge           // demo: 请假时长
              ├── PurchaseTotalMoneyJudge // demo: 采购金额
              ├── OutTotalMoneyJudge      // demo: 外出金额
              ├── ParkAreaJudge           // demo: 园区面积
              └── TotalMoneyJudge         // demo: 总金额
  ├── AbstractLFConditionJudge          // 低代码基类
  │     ├── LFStringConditionJudge      // 字符串相等
  │     ├── LFNumberFormatJudge         // 数字比较
  │     └── LFCollectionConditionJudge  // 集合包含
  └── AbstractLFDateTimeConditionJudge  // 日期时间基类
        ├── LFDateConditionJudge        // 日期比较(yyyy-MM-dd)
        └── LFDateTimeConditionJudge    // 日期时间比较(yyyy-MM-dd HH:mm:ss)
```

### 各 Judge 的比较逻辑

| Judge | 比较逻辑 |
|---|---|
| LFStringConditionJudge | `a.toString().equalsIgnoreCase(b.toString())` 字符串相等 |
| LFNumberFormatJudge | `compareJudge(value1, value2, userValue, operator)` 支持布尔值转 0/1 |
| LFDateConditionJudge | 用 `yyyy-MM-dd` 解析后转时间戳比较 |
| LFDateTimeConditionJudge | 用 `yyyy-MM-dd HH:mm:ss` 解析后转时间戳比较 |
| LFCollectionConditionJudge | 遍历 DB 集合,看是否有元素与用户值相等(支持单值或集合) |
| JuelExpressionConditionJudge | `JuelEvaluator.evaluate(expression, context)` |
| SpelExpressionConditionJudge | `SpelEvaluator.evaluate(expression, context)` |

## 条件节点类型

### nodeType=3:条件节点(路由)

用于条件网关下的分支路由。配置存储在 `node_config_json.conditionsConf`,运行时通过 `ConditionServiceImpl.checkMatchCondition` 评估。

**特殊处理**:前端 select 多选时,columnId=10000 会被自动改为 10004(LF_COLLECTION_CONDITION):

```java
// BpmnConfNodePropertyConverter.java 行 67-71
if (columnId == 10000 && multiple) {
    columnId = 10004;  // 走集合判断逻辑
}
```

### nodeType=9/12/13:自动节点/条件审批/条件抄送

这三种节点的条件**直接以前端 Vue 格式存储**在 `autoNodeConf.conditionList`,不经过 `BpmnConfNodePropertyConverter.fromVue3Model` 转换。

[BpmnNodeAutoNodeConfJson](https://github.com/mrtylerzhou/AntFlow/blob/master/antflow-base/src/main/java/org/openoa/base/entity/jsonconf/BpmnNodeAutoNodeConfJson.java):

```java
public class BpmnNodeAutoNodeConfJson implements Serializable {
    private List<List<BpmnNodeConditionsConfVueVo>> conditionList;  // 前端格式
    private Boolean groupRelation;  // false=AND, true=OR
}
```

**评估入口**:[AbstractFormOperationAdaptor.automaticCondition](https://github.com/mrtylerzhou/AntFlow/blob/master/antflow-engine/src/main/java/org/openoa/engine/bpmnconf/adp/processoperation/AbstractFormOperationAdaptor.java)

```java
// 1. 先调用用户可重写的 autoCondition(vo)
Boolean userResult = autoCondition(vo);
if (userResult != null) return userResult;

// 2. 从 DB 加载 autoNodeConf
BpmnNodeAutoNodeConfJson autoNodeConf = loadAutoNodeConf(formCode, processNumber, taskDefKey);

// 3. 评估条件
return evaluateConditions(autoNodeConf, lfFields);
```

`evaluateConditions` 是 ConditionJudge 的**简化重实现**(因为 autoNodeConf 存储的是前端格式,而 ConditionJudge 操作处理后格式):

```java
// 根据 fieldTypeName 分支:
// switch: 布尔值等值比较
// select/radio: 字符串相等
// checkbox: 包含判断
// number/date/time: compareNumeric 支持 optType 1-9
```

### 三种节点的条件处理差异

| 节点 | 条件为 true | 条件为 false/null | 副作用 |
|---|---|---|---|
| 自动节点(9) | 自动 complete | 自动 complete(无条件也通过) | 调用 `automaticAction` |
| 条件审批(12) | 自动 complete | **不 complete**(人工审批) | 不调用 |
| 条件抄送(13) | complete + 写抄送记录 | complete + **不写抄送记录** | 不调用 |

详见 [节点类型详解](/workflow-design/node-types)。

## 条件字段来源

### 低代码流程:VForm 字段

前端 [selectConditionDialog.vue](https://github.com/mrtylerzhou/AntFlow/blob/master/antflow-vue/src/components/Workflow/dialog/selectConditionDialog.vue) 从 `store.lowCodeFormFields.formFields` 加载字段,按控件类型映射:

| vform 控件 | columnId | ConditionTypeEnum |
|---|---|---|
| input | 10000 | LF_STRING_CONDITION |
| number | 10001 | LF_NUMBER_CONDITION |
| select(单选) | 10000 | LF_STRING_CONDITION |
| select(多选) | 10004 | LF_COLLECTION_CONDITION |
| checkbox | 10004 | LF_COLLECTION_CONDITION |
| radio | 10001 | LF_NUMBER_CONDITION |
| switch | 10001 | LF_NUMBER_CONDITION |
| date | 10002 | LF_DATE_CONDITION |
| time | 10003 | LF_DATETIME_CONDITION |
| time-range | 10003 | LF_DATETIME_CONDITION |
| data-range | 10002 | LF_DATE_CONDITION |

::: warning 字段类型映射一致性
前端 `DynamicForm/index.vue` 的 `getFieldTypeByType()` 与后端 `LfFormWidgetParser.getFieldTypeByTypeString()` **必须保持一致**,否则会导致条件判断异常。
:::

### DIY 流程:mock/conditions.json

DIY 流程(未启用辅助表单时)从 `public/mock/conditions.json` 加载条件字段。生产环境应替换为真实接口。

### 外部表单模式(Multi-Form)

从 `store.lowCodeFormFieldsMulti`(多个表单)加载所有字段,跨表单连续编号 formId,showName 前缀加表单名(如 `【表单名】字段名`)。

## lfConditions 与 lfFields

- `lfFields`:流程实例的实际表单数据(fieldId → value)
- `lfConditions`:用于条件评估的 fieldId → value 映射,默认从 lfFields 拷贝

```java
// LFFormDataRuntimeHelper.java
public void populateLfConditions(BpmnStartConditionsVo startConditionsVo, BusinessDataVo vo) {
    if (!CollectionUtils.isEmpty(vo.getLfConditions())) {
        startConditionsVo.setLfConditions(vo.getLfConditions());
    } else {
        startConditionsVo.setLfConditions(vo.getLfFields());  // 兜底
    }
}
```

## 扩展条件类型

新增条件类型需:

1. 在 `ConditionTypeEnum` 增加枚举项(指定 `conditionJudgeCls`)
2. 实现 `ConditionJudge` 接口
3. (可选)实现 `BpmnNodeConditionsAdaptor` 用于展示字段填充
4. 在 `BpmnNodeConditionsConfBaseVo` 增加对应字段

详见 [扩展条件规则](/dev-guide/extend-condition)。

## 下一步

- [低代码表单设计](/workflow-design/form-design) — vform 表单设计器
- [版本管理与启动](/workflow-design/version-management) — 流程发布与激活
- [扩展条件规则](/dev-guide/extend-condition) — 如何新增条件类型
