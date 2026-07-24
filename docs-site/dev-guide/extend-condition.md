# 扩展条件规则

> AntFlow 内置 17 种条件类型(LF 字符串/数字/日期/集合、JUEL/SpEL 表达式、金额、请假、采购等)。当内置条件类型不满足业务需求时,可通过自定义 `ConditionJudge` 扩展。本章详解条件评估机制与自定义示例。

## 条件评估机制

### 双层 AND/OR 结构

AntFlow 条件配置支持两层逻辑组合:外层 AND,内层 OR。即"组间 AND,组内 OR":

<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 920 240" font-family="-apple-system, BlinkMacSystemFont, 'Segoe UI', sans-serif">
  <!-- 整体 AND -->
  <rect x="20" y="20" width="880" height="200" rx="8" fill="#dbeafe" stroke="#3b82f6" stroke-width="2"/>
  <text x="460" y="44" text-anchor="middle" font-size="13" font-weight="700" fill="#1e40af">外层 AND(组间)</text>

  <!-- 组1 -->
  <rect x="40" y="60" width="260" height="140" rx="6" fill="#dcfce7" stroke="#16a34a" stroke-width="2"/>
  <text x="170" y="82" text-anchor="middle" font-size="12" font-weight="700" fill="#155e2f">条件组 1</text>
  <text x="170" y="100" text-anchor="middle" font-size="10" fill="#14532d">内层 OR(组内)</text>
  <rect x="60" y="110" width="220" height="28" rx="4" fill="#fff" stroke="#16a34a"/>
  <text x="170" y="128" text-anchor="middle" font-size="10" fill="#1e293b">条件 A:金额 &gt; 1000</text>
  <rect x="60" y="144" width="220" height="28" rx="4" fill="#fff" stroke="#16a34a"/>
  <text x="170" y="162" text-anchor="middle" font-size="10" fill="#1e293b">条件 B:类型 = 紧急</text>
  <text x="170" y="190" text-anchor="middle" font-size="9" fill="#14532d">满足任一即整组通过</text>

  <!-- 组2 -->
  <rect x="330" y="60" width="260" height="140" rx="6" fill="#fef3c7" stroke="#d97706" stroke-width="2"/>
  <text x="460" y="82" text-anchor="middle" font-size="12" font-weight="700" fill="#92400e">条件组 2</text>
  <text x="460" y="100" text-anchor="middle" font-size="10" fill="#78350f">内层 OR(组内)</text>
  <rect x="350" y="110" width="220" height="28" rx="4" fill="#fff" stroke="#d97706"/>
  <text x="460" y="128" text-anchor="middle" font-size="10" fill="#1e293b">条件 C:职级 = 经理</text>
  <rect x="350" y="144" width="220" height="28" rx="4" fill="#fff" stroke="#d97706"/>
  <text x="460" y="162" text-anchor="middle" font-size="10" fill="#1e293b">条件 D:部门 = 研发</text>
  <text x="460" y="190" text-anchor="middle" font-size="9" fill="#78350f">满足任一即整组通过</text>

  <!-- 组3 -->
  <rect x="620" y="60" width="260" height="140" rx="6" fill="#fce7f3" stroke="#db2777" stroke-width="2"/>
  <text x="750" y="82" text-anchor="middle" font-size="12" font-weight="700" fill="#9d174d">条件组 3</text>
  <text x="750" y="100" text-anchor="middle" font-size="10" fill="#831843">JUEL 表达式</text>
  <rect x="640" y="110" width="220" height="62" rx="4" fill="#fff" stroke="#db2777"/>
  <text x="750" y="130" text-anchor="middle" font-size="10" fill="#1e293b">${processNumber.contains</text>
  <text x="750" y="146" text-anchor="middle" font-size="10" fill="#1e293b">('URGENT')}</text>
  <text x="750" y="190" text-anchor="middle" font-size="9" fill="#831843">单表达式,组内单条件</text>
</svg>

### ConditionServiceImpl 分发

[ConditionServiceImpl.java](file:///d:/projects/jimuoffice/antflow-engine/src/main/java/org/openoa/engine/bpmnconf/adp/conditionfilter/ConditionServiceImpl.java):

```java
public boolean checkMatchCondition(BpmnNodeVo node,
                                   BpmnNodeConditionsConfBaseVo conditionsConf,
                                   BpmnStartConditionsVo startConditionsVo,
                                   boolean isDynamicConditionGateway) {
    // 1. 取条件类型分组(groupedConditionParamTypes)
    List<List<Integer>> groups = conditionsConf.getGroupedConditionParamTypes();

    // 2. 外层 AND:任一组不满足即返回 false
    for (List<Integer> group : groups) {
        boolean groupMatched = false;
        // 3. 内层 OR:任一条件满足即整组通过
        for (int i = 0; i < group.size(); i++) {
            Integer conditionTypeCode = group.get(i);
            ConditionTypeEnum typeEnum = ConditionTypeEnum.getEnumByCode(conditionTypeCode);

            // 4. 通过 conditionJudgeCls 查找对应 Judge
            ConditionJudge judge = SpringBeanUtils.getBean(typeEnum.getConditionJudgeCls());
            if (judge.judge(node.getNodeId(), conditionsConf, startConditionsVo,
                    groups.indexOf(group), i)) {
                groupMatched = true;
                break;  // OR 短路
            }
        }
        if (!groupMatched) {
            return false;  // AND 短路
        }
    }
    return true;
}
```

## 核心接口

[ConditionJudge.java](file:///d:/projects/jimuoffice/antflow-engine/src/main/java/org/openoa/engine/bpmnconf/adp/conditionfilter/ConditionJudge.java):

```java
public interface ConditionJudge {
    boolean judge(String nodeId,
                  BpmnNodeConditionsConfBaseVo conditionsConf,
                  BpmnStartConditionsVo bpmnStartConditionsVo,
                  int group,
                  int index);
}
```

## 注册表:ConditionTypeEnum

[ConditionTypeEnum.java](file:///d:/projects/jimuoffice/antflow-engine/src/main/java/org/openoa/engine/bpmnconf/constant/enus/ConditionTypeEnum.java) 字段含义:

- `code`:条件类型编码
- `desc`:中文描述
- `fieldName`:数据库字段名
- `fieldType`:1=列表,2=对象
- `fieldCls`:字段 Java 类型
- `cls`:`BpmnNodeConditionsAdaptor` 子类(设计期配置持久化)
- `alignmentCls`:对比对象类(通常是 `BpmnStartConditionsVo`)
- `alignmentFieldName`:对比对象字段名
- `conditionJudgeCls`:`ConditionJudge` 实现类

### 17 个内置条件类型

| 枚举 | code | desc | Judge 实现 |
|---|:---:|---|---|
| `CONDITION_THIRD_ACCOUNT_TYPE` | 1 | 三方账户 | `ThirdAccountJudge` |
| `CONDITION_BIZ_LEAVE_TIME` | 2 | 请假时长 | `AskLeaveJudge` |
| `CONDITION_PURCHASE_FEE` | 3 | 采购费用 | `PurchaseTotalMoneyJudge` |
| `CONDITION_TYPE_OUT_TOTAL_MONEY` | 4 | 支出费用 | `OutTotalMoneyJudge` |
| `CONDITION_JOB_LEVEL_TYPE` | 5 | 职级列表 | `JobLevelJudge` |
| `CONDITION_PURCHASE_TYPE` | 6 | 采购类型 | `PurchaseTypeJudge` |
| `CONDITION_TYPE_NUMBER_OPERATOR` | 7 | 数字运算符 | `MoneyOperatorJudge` |
| `CONDITION_THIRD_PARK_AREA` | 37 | 园区面积 | `ParkAreaJudge` |
| `CONDITION_TYPE_TOTAL_MONEY` | 38 | 总金额 | `TotalMoneyJudge` |
| `CONDITION_TEMPLATEMARK` | 9999 | 条件模板标识 | `BpmnTemplateMarkJudge` |
| `CONDITION_TYPE_LF_STR_CONDITION` | 10000 | 无代码字符串 | `LFStringConditionJudge` |
| `CONDITION_TYPE_LF_NUM_CONDITION` | 10001 | 无代码数字 | `LFNumberFormatJudge` |
| `CONDITION_TYPE_LF_DATE_CONDITION` | 10002 | 无代码日期 | `LFDateConditionJudge` |
| `CONDITION_TYPE_LF_DATE_TIME_CONDITION` | 10003 | 无代码日期时间 | `LFDateTimeConditionJudge` |
| `CONDITION_TYPE_LF_COLLECTION_CONDITION` | 10004 | 无代码集合 | `LFCollectionConditionJudge` |
| `CONDITION_TYPE_JUEL_EXPRESSION` | 20000 | JUEL 表达式 | `JuelExpressionConditionJudge` |
| `CONDITION_TYPE_SPEL_EXPRESSION` | 20001 | SpEL 表达式 | `SpelExpressionConditionJudge` |

## 抽象基类层次

| 基类 | 文件 | 作用 |
|---|---|---|
| `AbstractComparableJudge` | `conditionjudge/AbstractComparableJudge.java` | 提供 9 种运算符比较:`>= / > / <= / < / = / >a&<b / >=a&<b / >a&<=b / >=a&<=b` |
| `AbstractBinaryComparableJudge` | `conditionjudge/AbstractBinaryComparableJudge.java` | 用于「1<a<2」二元比较;子类只需实现 `fieldNameInDb()` 和 `fieldNameInStartConditions()` |
| `AbstractLFConditionJudge` | `conditionjudge/AbstractLFConditionJudge.java` | 低代码条件基类;通过 `TriplePredict` 委托具体判断 |
| `AbstractLFDateTimeConditionJudge` | `conditionjudge/AbstractLFDateTimeConditionJudge.java` | 日期时间基类;字符串 → Date → BigDecimal 比较 |

### 轻量级实现示例:TotalMoneyJudge

```java
@Service
public class TotalMoneyJudge extends AbstractBinaryComparableJudge {
    @Override
    protected String fieldNameInDb() {
        return "totalMoney";  // BpmnNodeConditionsConfBaseVo.totalMoney
    }

    @Override
    protected String fieldNameInStartConditions() {
        return "totalMoney";  // BpmnStartConditionsVo.totalMoney
    }
}
// 仅 22 行,所有比较逻辑由父类完成
```

## 自定义示例:职级白名单

### 步骤 1:扩展 ConditionTypeEnum

```java
// 在 ConditionTypeEnum 中新增
CONDITION_JOB_LEVEL_WHITELIST(100, "职级白名单", "jobLevelWhitelist", 1, String.class,
        BpmnNodeConditionsEmptyAdp.class, BpmnStartConditionsVo.class,
        "jobLevelWhitelist", JobLevelWhitelistJudge.class),
```

### 步骤 2:实现 ConditionJudge

```java
package com.yourcompany.workflow.condition;

import org.openoa.base.vo.BpmnNodeConditionsConfBaseVo;
import org.openoa.base.vo.BpmnStartConditionsVo;
import org.openoa.engine.bpmnconf.adp.conditionfilter.ConditionJudge;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class JobLevelWhitelistJudge implements ConditionJudge {

    @Autowired
    private YourJobLevelService jobLevelService;

    @Override
    public boolean judge(String nodeId,
                        BpmnNodeConditionsConfBaseVo conditionsConf,
                        BpmnStartConditionsVo startConditionsVo,
                        int group, int index) {
        // 1. 从 conditionsConf 取配置的职级白名单
        List<String> whitelist = parseWhitelist(conditionsConf.getExpression());

        // 2. 从 startConditionsVo 取当前用户职级
        String currentUserLevel = startConditionsVo.getJobLevelVo() != null
                ? startConditionsVo.getJobLevelVo().getId() : null;
        if (currentUserLevel == null) return false;

        // 3. 匹配
        return whitelist.contains(currentUserLevel);
    }

    private List<String> parseWhitelist(String expr) {
        return java.util.Arrays.asList(expr.split(","));
    }
}
```

## 更轻量的做法:继承 AbstractBinaryComparableJudge

如果只是数值二元比较(如 `1 < x < 100`),无需手写比较逻辑:

```java
@Service
public class CustomAmountJudge extends AbstractBinaryComparableJudge {
    @Override
    protected String fieldNameInDb() {
        return "customAmount";  // 数据库字段
    }

    @Override
    protected String fieldNameInStartConditions() {
        return "customAmount";  // 启动条件字段
    }
}
// 父类自动支持 9 种运算符
```

## 条件配置持久化:BpmnNodeConditionsAdaptor

[adp/conditionfilter/nodetypeconditions/](file:///d:/projects/jimuoffice/antflow-engine/src/main/java/org/openoa/engine/bpmnconf/adp/conditionfilter/nodetypeconditions/):

| 实现类 | 用途 |
|---|---|
| `BpmnNodeConditionsEmptyAdp` | 空实现(大部分条件用这个) |
| `BpmnNodeConditionsAccountTypeAdp` | 三方账户特殊处理 |
| `BpmnNodeConditionsPurchaseTypeAdp` | 采购类型特殊处理 |
| `BpmnNodeConditionsTotalMoneyAdp` | 总金额特殊处理 |
| `BpmnTemplateMarkAdp` | 三方接入条件模板 |

抽象方法:

```java
public abstract class BpmnNodeConditionsAdaptor {
    public abstract void setConditionsResps(BpmnNodeConditionsConfBaseVo conf);
}
```

## JUEL 与 SpEL 表达式

### JUEL 表达式

`JuelExpressionConditionJudge` 使用 `JuelEvaluator`:

```java
boolean result = JuelEvaluator.evaluate(expression, bpmnStartConditionsVo);
// 示例表达式:${processNumber.contains('URGENT')}
// 示例表达式:${totalMoney > 1000 && leaveHour > 8}
```

### SpEL 表达式

`SpelExpressionConditionJudge` 使用 Spring SpEL:

```java
boolean result = SpelEvaluator.evaluate(expression, bpmnStartConditionsVo);
// 示例表达式:#root.totalMoney > 1000 and #root.leaveHour > 8
```

两者都是把 `BpmnStartConditionsVo` 作为 root 对象,支持访问其所有字段。

## 小结

- AntFlow 条件评估采用双层 AND/OR 结构:外层 AND,内层 OR,支持短路
- 17 个内置条件类型覆盖低代码字段、业务字段、JUEL/SpEL 表达式等场景
- `ConditionTypeEnum` 是注册表,关联条件编码、字段名、Judge 实现类
- 自定义只需 2 步:扩展枚举 → 实现 ConditionJudge(或继承 AbstractBinaryComparableJudge)
- JUEL/SpEL 表达式条件提供最大灵活性,可访问 `BpmnStartConditionsVo` 全部字段

下一节 [扩展通知渠道](/dev-guide/extend-notice) 介绍如何自定义 MessageSendAdaptor。
