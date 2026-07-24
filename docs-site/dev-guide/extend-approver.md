# 扩展审批人来源

> AntFlow 内置 16 种审批人来源(指定人员、角色、HRBP、层层审批、直属领导等)。当内置规则不满足业务需求时,可通过自定义 `PersonnelAdaptor` 扩展。本章详解扩展机制、注册流程与完整示例。

## 三层 SPI 架构

AntFlow 审批人扩展采用「枚举驱动 + 双层 SPI」模式:

<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 920 280" font-family="-apple-system, BlinkMacSystemFont, 'Segoe UI', sans-serif">
  <defs>
    <marker id="arr16" viewBox="0 0 10 10" refX="9" refY="5" markerWidth="6" markerHeight="6" orient="auto"><path d="M0,0 L10,5 L0,10 z" fill="#475569"/></marker>
  </defs>

  <!-- 适配器层 -->
  <rect x="20" y="20" width="280" height="80" rx="8" fill="#dbeafe" stroke="#3b82f6"/>
  <text x="160" y="44" text-anchor="middle" font-size="13" font-weight="700" fill="#1e40af">① 适配器层</text>
  <text x="160" y="64" text-anchor="middle" font-size="10" fill="#1e3a8a">AbstractBpmnPersonnelAdaptor 子类</text>
  <text x="160" y="80" text-anchor="middle" font-size="10" fill="#1e3a8a">声明支持的 PersonnelEnum</text>

  <!-- Provider 层 -->
  <rect x="320" y="20" width="280" height="80" rx="8" fill="#dcfce7" stroke="#16a34a"/>
  <text x="460" y="44" text-anchor="middle" font-size="13" font-weight="700" fill="#155e2f">② Provider 层(真正查询)</text>
  <text x="460" y="64" text-anchor="middle" font-size="10" fill="#14532d">BpmnPersonnelProviderService 实现</text>
  <text x="460" y="80" text-anchor="middle" font-size="10" fill="#14532d">getAssigneeList(nodeVo, startConditionsVo)</text>

  <!-- 员工信息层 -->
  <rect x="620" y="20" width="280" height="80" rx="8" fill="#fef3c7" stroke="#d97706"/>
  <text x="760" y="44" text-anchor="middle" font-size="13" font-weight="700" fill="#92400e">③ 员工信息层</text>
  <text x="760" y="64" text-anchor="middle" font-size="10" fill="#78350f">BpmnEmployeeInfoProviderService</text>
  <text x="760" y="80" text-anchor="middle" font-size="10" fill="#78350f">补全员工姓名(可选)</text>

  <!-- 注入关系 -->
  <line x1="320" y1="60" x2="300" y2="60" stroke="#475569" stroke-width="2" marker-end="url(#arr16)"/>
  <text x="310" y="52" text-anchor="middle" font-size="9" fill="#475569">注入</text>
  <line x1="620" y1="60" x2="600" y2="60" stroke="#475569" stroke-width="2" marker-end="url(#arr16)"/>
  <text x="610" y="52" text-anchor="middle" font-size="9" fill="#475569">注入</text>

  <!-- 注册机制说明 -->
  <rect x="20" y="130" width="880" height="140" rx="8" fill="#f8fafc" stroke="#94a3b8"/>
  <text x="40" y="154" font-size="13" font-weight="700" fill="#1e293b">注册机制</text>
  <text x="40" y="178" font-size="11" fill="#475569">1. Spring 启动扫描所有 @Component 标注的 AbstractBpmnPersonnelAdaptor 子类</text>
  <text x="40" y="198" font-size="11" fill="#475569">2. 调 setSupportBusinessObjects() 注册到 SUPPORTED_BUSINESS ConcurrentHashMap</text>
  <text x="40" y="218" font-size="11" fill="#475569">3. 运行期 PersonnelTagParser.parseTag(PersonnelEnum) 遍历所有 Bean,通过 isSupportBusinessObject 匹配</text>
  <text x="40" y="238" font-size="11" fill="#475569">4. 找到匹配的 Adaptor,委托其内部 ProviderService 查询实际人员</text>
  <text x="40" y="258" font-size="11" font-weight="700" fill="#dc2626">无需修改工厂代码,新增 @Component Bean 即自动注册</text>
</svg>

## 核心接口

### BpmnPersonnelProviderService

[BpmnPersonnelProviderService.java](file:///d:/projects/jimuoffice/antflow-base/src/main/java/org/openoa/base/interf/BpmnPersonnelProviderService.java):

```java
public interface BpmnPersonnelProviderService {
    /** 查询节点实际审批人 */
    List<BpmnNodeParamsAssigneeVo> getAssigneeList(
            BpmnNodeVo bpmnNodeVo,
            BpmnStartConditionsVo startConditionsVo);
}
```

### AdaptorService(根接口)

[AdaptorService.java](file:///d:/projects/jimuoffice/antflow-base/src/main/java/org/openoa/base/interf/AdaptorService.java):

```java
public interface AdaptorService {
    void setSupportBusinessObjects();

    default void addSupportBusinessObjects(Enum<?>... businessObjects) {
        for (Enum<?> bo : businessObjects) {
            SUPPORTED_BUSINESS.put(this.getClass().getName() + bo.name(), bo);
        }
    }

    default boolean isSupportBusinessObject(Enum<?> businessObject) {
        return SUPPORTED_BUSINESS.containsValue(businessObject);
    }
}
```

## 内置 16 个 PersonnelAdaptor

目录:[adp/personneladp/](file:///d:/projects/jimuoffice/antflow-engine/src/main/java/org/openoa/engine/bpmnconf/adp/personneladp/)

| 实现类 | PersonnelEnum | 适用场景 |
|---|---|---|
| `UserPointedPersonnelAdp` | USERAPPOINTED_PERSONNEL | 指定具体人员 |
| `RolePersonnelAdaptor` | ROLE_PERSONNEL | 按角色查全部用户 |
| `LoopPersonnelAdaptor` | NODE_LOOP_PERSONNEL | 层层审批(逐级向上) |
| `LevelPersonnelAdaptor` | NODE_LEVEL_PERSONNEL | 指定层级领导 |
| `HrbpPersonnelAdaptor` | HRBP_PERSONNEL | 员工对应 HRBP |
| `OutSidePersonnelAdaptor` | OUT_SIDE_ACCESS_PERSONNEL | 三方系统传入人员 |
| `StartUserPersonnelAdaptor` | START_USER_PERSONNEL | 发起人自己审批 |
| `DirectLeaderPersonnelAdaptor` | DIRECT_LEADER_PERSONNEL | 直属领导 |
| `BusinessTablePersonnelAdaptor` | BUSINESS_TABLE_PERSONNEL | 关联业务表查询 |
| `DeppartmentLeaderPersonnelAdaptor` | DEPARTMENT_LEADER_PERSONNEL | 部门负责人 |
| `ApprovedUsersPersonnelAdaptor` | APPROVED_USERS_PERSONNEL | 被审批人自己 |
| `FormRelatedPersonnelAdaptor` | FORM_USERS_PERSONNEL | 表单上下文人员 |
| `UDRPersonnelAdaptor` | UDR_USERS_PERSONNEL | 用户自定义规则(UDR) |
| `PrevNodeRelatedPersonnelAdaptor` | PREV_NODE_USERS_PERSONNEL | 上一节点上下文人员 |
| `CustomizablePersonnelAdp` | CUSTOMIZABLE_PERSONNEL | 发起人自选审批人 |

Provider 实现目录:[service/biz/personnelinfoprovider/](file:///d:/projects/jimuoffice/antflow-engine/src/main/java/org/openoa/engine/bpmnconf/service/biz/personnelinfoprovider/)

## 自定义示例:按岗位审批

以"按岗位审批"为例,完整 4 步:

### 步骤 1:扩展枚举

需修改 antflow-base 源码或通过继承扩展:

```java
// 在 NodePropertyEnum 中新增
NODE_PROPERTY_POSITION(19, "指定岗位", 1, BpmnNodeParamTypeEnum.BPMN_NODE_PARAM_MULTIPLAYER);

// 在 PersonnelEnum 中新增
POSITION_PERSONNEL(NODE_PROPERTY_POSITION, "指定岗位");
```

### 步骤 2:实现 BpmnPersonnelProviderService

```java
package com.yourcompany.workflow.personnel;

import org.openoa.base.interf.BpmnPersonnelProviderService;
import org.openoa.base.vo.BpmnNodeParamsAssigneeVo;
import org.openoa.base.vo.BpmnNodeVo;
import org.openoa.base.vo.BpmnStartConditionsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.stream.Collectors;

@Component("positionPersonnelProvider")  // bean 名必须唯一
public class PositionPersonnelProvider implements BpmnPersonnelProviderService {

    @Autowired
    private YourPositionService positionService;  // 企业自有岗位服务

    @Override
    public List<BpmnNodeParamsAssigneeVo> getAssigneeList(BpmnNodeVo bpmnNodeVo,
                                                          BpmnStartConditionsVo startConditionsVo) {
        // 1. 从节点属性取岗位 id 列表
        List<String> positionIds = bpmnNodeVo.getProperty().getPositionIds();
        if (positionIds == null || positionIds.isEmpty()) {
            return Collections.emptyList();
        }
        // 2. 调企业服务查岗位下的用户
        List<String> userIds = positionService.listUserIdsByPositions(positionIds);
        // 3. 转 BpmnNodeParamsAssigneeVo
        return userIds.stream()
                .map(id -> BpmnNodeParamsAssigneeVo.builder()
                        .assignee(id)
                        .elementName(bpmnNodeVo.getNodeName())
                        .build())
                .collect(Collectors.toList());
    }
}
```

### 步骤 3:实现 AbstractBpmnPersonnelAdaptor 子类

```java
package com.yourcompany.workflow.personnel;

import org.openoa.base.constant.enums.PersonnelEnum;
import org.openoa.base.interf.BpmnPersonnelProviderService;
import org.openoa.base.service.empinfoprovider.BpmnEmployeeInfoProviderService;
import org.openoa.common.adaptor.AbstractBpmnPersonnelAdaptor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class PositionPersonnelAdaptor extends AbstractBpmnPersonnelAdaptor {

    public PositionPersonnelAdaptor(BpmnEmployeeInfoProviderService empService,
                                    @Qualifier("positionPersonnelProvider")
                                    BpmnPersonnelProviderService provider) {
        super(empService, provider);
    }

    @Override
    public void setSupportBusinessObjects() {
        addSupportBusinessObjects(PersonnelEnum.POSITION_PERSONNEL);
    }
}
```

### 步骤 4(可选):前端节点属性适配器

参考 [NodePropertyRoleAdp.java](file:///d:/projects/jimuoffice/antflow-engine/src/main/java/org/openoa/engine/bpmnconf/adp/bpmnnodeadp/NodePropertyRoleAdp.java) 实现 `formatToBpmnNodeVo` / `editBpmnNode` 持久化节点配置。

## AbstractBpmnPersonnelAdaptor 行为

`AbstractBpmnPersonnelAdaptor` 基类提供以下通用逻辑(基于测试代码推断):

| 行为 | 说明 |
|---|---|
| 调用 ProviderService | 委托 `bpmnPersonnelProviderService.getAssigneeList()` 获取候选人 |
| 设置 assignee / assigneeList | 按 `BpmnNodeParamTypeEnum.SINGLE` / `MULTIPLAYER` 区分 |
| 去重 | 对返回的 assignee 按 id 去重 |
| 补全姓名 | 若 `assigneeName` 为空,通过 `BpmnEmployeeInfoProviderService.provideEmployeeInfo()` 补全 |
| 上一节点关联 | 若 `ApprovalStandardEnum.FROM_PREV_NODE`,从上一节点 `emplList` 设置 `contextEmplList` |
| 依次会签 | 若 `orderedNodeType` 非空,委托给 `AbstractOrderedSignNodeAdp` 处理 |

## Provider 层抽象基类

| 基类 | 用途 |
|---|---|
| `AbstractNodeAssigneeVoProvider` | 普通节点 Provider 基类 |
| `AbstractMissingAssignNodeAssigneeVoProvider` | 缺失审批人节点 Provider 基类 |
| `AbstractDifferentStandardAssignNodeAssigneeVoProvider` | 不同审批标准 Provider 基类 |

## 小结

- 三层 SPI 架构:Adaptor(声明枚举)→ Provider(查询逻辑)→ EmployeeInfo(补全姓名)
- 16 个内置实现覆盖中国企业常见审批人来源
- 自定义只需 4 步:扩枚举 → 实现 Provider → 实现 Adaptor → 前端节点属性适配
- 通过 Spring 组件扫描自动注册,无需修改工厂代码
- 复用 `AbstractBpmnPersonnelAdaptor` 基类自动获得去重、姓名补全、会签等通用能力

下一节 [扩展条件规则](/dev-guide/extend-condition) 介绍如何自定义 ConditionJudge。
