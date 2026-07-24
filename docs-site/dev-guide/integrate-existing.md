# 集成现有系统

> AntFlow 通过 Spring Boot Starter 自动装配,业务方引入一个依赖即可集成。本章详解 Starter 配置、用户/角色/表单系统的 SPI 接入、三方 Open API 与完整集成示例。

## Spring Boot Starter

### 引入依赖

```xml
<dependency>
    <groupId>org.openoa</groupId>
    <artifactId>antflow-spring-boot-starter</artifactId>
    <version>1.7.0</version>
</dependency>
```

### 自动装配类

[AntFlowAutoConfiguration.java](file:///d:/projects/jimuoffice/antflow-spring-boot-starter/src/main/java/org/openoa/starter/config/AntFlowAutoConfiguration.java):

```java
@Configuration
@MapperScans({
    @MapperScan("org.openoa.base.mapper"),
    @MapperScan("org.openoa.common.mapper"),
    @MapperScan("org.openoa.engine.bpmnconf.mapper")
})
@ComponentScan({"org.openoa"})
public class AntFlowAutoConfiguration { }
```

### spring.factories

```
org.springframework.boot.autoconfigure.EnableAutoConfiguration=\
org.openoa.starter.config.AntFlowAutoConfiguration
```

### 设计特点

- **无 `@Conditional` 注解**:all-or-nothing 全量扫描 `org.openoa`
- **无 `@ConfigurationProperties`**:所有配置走标准 Spring Boot 属性(application.yml)
- 接入企业系统主要通过覆盖 SPI Bean

### application.yml 配置

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/antflow?useUnicode=true&characterEncoding=utf8
    username: root
    password: root
    driver-class-name: com.mysql.cj.jdbc.Driver
  mail:
    host: smtp.yourcompany.com
    port: 465
    username: noreply@yourcompany.com
    password: xxx
    properties:
      mail:
        smtp:
          ssl:
            enable: true

mybatis-plus:
  configuration:
    log-impl: org.apache.ibatis.logging.stdout.StdOutImpl
  global-config:
    db-config:
      logic-delete-field: isDel
      logic-delete-value: 1
      logic-not-delete-value: 0

server:
  port: 7001
```

## 接入企业用户/角色系统

### 核心 SPI 接口

| SPI 接口 | 关键方法 | 默认实现 |
|---|---|---|
| `BpmnEmployeeInfoProviderService` | `Map<String,String> provideEmployeeInfo(Collection<String> empIds)` 返回 userId→姓名 | `BpmnTestEmployeeInfoProvider`(测试用) |
| `BpmnRoleInfoProviderService` | `provideRoleInfo(roleIds)` / `provideRoleEmployeeInfo(roleIds)` | `BpmnRoleInfoProvider` |
| `BpmnProcessAdminProvider` | `BaseIdTranStruVo provideProcessAdminInfo()` 返回流程管理员 | 无默认,需自定义 |
| `AfUserService` | `queryByNameFuzzy`、`getEmployeeDetailById`、`getById` | `UserServiceImpl` |
| `AfRoleService` | `queryRoleByIds`、`queryUserByRoleIds` | `AfRoleServiceImpl` |

### 接入方式:覆盖默认 Bean

```java
package com.yourcompany.workflow;

import org.openoa.base.interf.BpmnEmployeeInfoProviderService;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Primary  // 覆盖默认 BpmnTestEmployeeInfoProvider
public class YourEmployeeInfoProvider implements BpmnEmployeeInfoProviderService {

    @Autowired
    private YourEmployeeService employeeService;  // 企业员工服务

    @Override
    public Map<String, String> provideEmployeeInfo(Collection<String> empIds) {
        // 调企业 HR 系统查询员工姓名
        List<YourEmployee> employees = employeeService.listByIds(empIds);
        return employees.stream()
                .collect(Collectors.toMap(
                        YourEmployee::getId,
                        YourEmployee::getName));
    }
}
```

### 自定义角色服务

```java
@Service
@Primary
public class YourRoleService implements AfRoleService {

    @Override
    public List<BaseIdTranStruVo> queryRoleByIds(List<String> roleIds) {
        // 调企业权限系统查询角色
        return yourRoleSystem.listRoles(roleIds).stream()
                .map(role -> BaseIdTranStruVo.builder()
                        .id(role.getId())
                        .name(role.getName())
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    public List<BaseIdTranStruVo> queryUserByRoleIds(List<String> roleIds) {
        // 查询角色下的用户
        List<String> userIds = yourRoleSystem.listUserIdsByRoles(roleIds);
        return userIds.stream()
                .map(id -> BaseIdTranStruVo.builder()
                        .id(id)
                        .name(employeeService.getNameById(id))
                        .build())
                .collect(Collectors.toList());
    }
}
```

## 接入企业表单系统

### 核心 SPI:FormOperationAdaptor

[FormOperationAdaptor.java](file:///d:/projects/jimuoffice/antflow-base/src/main/java/org/openoa/base/interf/FormOperationAdaptor.java):

```java
public interface FormOperationAdaptor<T extends BusinessDataVo>
        extends ProcessFinishListener, ActivitiService {

    /** 流程预览设置条件 */
    BpmnStartConditionsVo previewSetCondition(T vo);

    /** 流程初始化 */
    void initData(T vo);

    /** 发起取启动条件 */
    BpmnStartConditionsVo launchParameters(T vo);

    /** 自动条件判断 */
    Boolean automaticCondition(T vo);

    /** 自动动作执行 */
    void automaticAction(T vo, Boolean conditionResult);

    /** 审批页查询业务数据 */
    void queryData(T vo);

    /** 发起提交业务数据 */
    void submitData(T vo);

    /** 审批同意 */
    void consentData(T vo);

    /** 退回修改 */
    void backToModifyData(T vo);

    /** 流程撤销(作废业务数据) */
    void cancellationData(T vo);

    /** 流程完结/恢复 */
    void onProcessRecover(BusinessDataVo vo);
}
```

### 必须实现的方法

| 方法 | 调用时机 | 必须性 |
|---|---|:---:|
| `launchParameters` | 发起流程 | **必须** |
| `queryData` | 审批页加载 | **必须** |
| `submitData` | 发起提交 | **必须** |
| `cancellationData` | 流程作废 | **必须** |
| `previewSetCondition` | 预览 | 可选 |
| `initData` | 初始化 | 可选 |
| `automaticCondition` | 自动节点 | 可选 |
| `automaticAction` | 自动动作 | 可选 |
| `consentData` | 同意 | 可选 |
| `backToModifyData` | 退回 | 可选 |
| `onProcessRecover` | 完结/恢复 | 可选 |

### DIY 表单注册:@ActivitiServiceAnno

```java
package com.yourcompany.workflow.form;

import org.openoa.base.interf.ActivitiServiceAnno;
import org.openoa.base.interf.FormOperationAdaptor;
import org.openoa.base.vo.BusinessDataVo;
import org.openoa.base.vo.BpmnStartConditionsVo;
import org.springframework.stereotype.Service;

@Service
@ActivitiServiceAnno(svcName = "LEAVE_WMA", desc = "请假流程")
public class LeaveFormAdaptor implements FormOperationAdaptor<LeaveDataVo> {

    @Autowired
    private LeaveService leaveService;

    @Override
    public BpmnStartConditionsVo launchParameters(LeaveDataVo vo) {
        // 构造启动条件
        return BpmnStartConditionsVo.builder()
                .startUserId(vo.getStartUserId())
                .formCode("LEAVE_WMA")
                .processNumber(vo.getProcessNumber())
                .variables(buildVariables(vo))
                .build();
    }

    @Override
    public void queryData(LeaveDataVo vo) {
        // 从业务表加载请假数据
        Leave leave = leaveService.getByProcessNumber(vo.getProcessNumber());
        vo.setLeaveType(leave.getType());
        vo.setLeaveHour(leave.getHours());
        vo.setStartDate(leave.getStartDate());
        vo.setEndDate(leave.getEndDate());
        vo.setReason(leave.getReason());
    }

    @Override
    public void submitData(LeaveDataVo vo) {
        // 保存请假数据到业务表
        Leave leave = Leave.builder()
                .processNumber(vo.getProcessNumber())
                .type(vo.getLeaveType())
                .hours(vo.getLeaveHour())
                .startDate(vo.getStartDate())
                .endDate(vo.getEndDate())
                .reason(vo.getReason())
                .createUser(vo.getStartUserId())
                .build();
        leaveService.save(leave);
    }

    @Override
    public void cancellationData(LeaveDataVo vo) {
        // 作废请假数据
        leaveService.cancelByProcessNumber(vo.getProcessNumber());
    }
}
```

`svcName` 即前端使用的 `formCode`,与 `t_bpmn_conf.form_code` 对应。

### 低代码表单 SPI:LFFormOperationAdaptor

```java
public interface LFFormOperationAdaptor<T extends UDLFApplyVo> extends FormOperationAdaptor<T> {
    // 继承所有方法,用于低代码流程的自定义行为
}

// 注册方式:bean 名必须等于低代码表单的 formCode
@Service("LEAVE_LF_WMA")
public class LeaveLFFormAdaptor implements LFFormOperationAdaptor<LeaveLFDataVo> {
    // ...
}
```

## 三方接入 Open API

### 接入架构

<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 920 280" font-family="-apple-system, BlinkMacSystemFont, 'Segoe UI', sans-serif">
  <defs>
    <marker id="arr18" viewBox="0 0 10 10" refX="9" refY="5" markerWidth="6" markerHeight="6" orient="auto"><path d="M0,0 L10,5 L0,10 z" fill="#475569"/></marker>
  </defs>

  <!-- 三方系统 -->
  <rect x="20" y="20" width="180" height="240" rx="8" fill="#dbeafe" stroke="#3b82f6"/>
  <text x="110" y="44" text-anchor="middle" font-size="12" font-weight="700" fill="#1e40af">三方业务系统</text>
  <text x="40" y="68" font-size="10" fill="#1e3a8a">• ERP</text>
  <text x="40" y="86" font-size="10" fill="#1e3a8a">• CRM</text>
  <text x="40" y="104" font-size="10" fill="#1e3a8a">• OA</text>
  <text x="40" y="122" font-size="10" fill="#1e3a8a">• 财务系统</text>
  <text x="40" y="140" font-size="10" fill="#1e3a8a">• HR 系统</text>
  <text x="40" y="180" font-size="10" font-weight="700" fill="#1e40af">通过 Open API</text>
  <text x="40" y="198" font-size="10" font-weight="700" fill="#1e40af">接入 AntFlow</text>

  <!-- AntFlow -->
  <rect x="280" y="20" width="620" height="240" rx="8" fill="#dcfce7" stroke="#16a34a"/>
  <text x="590" y="44" text-anchor="middle" font-size="14" font-weight="700" fill="#155e2f">AntFlow 工作流引擎</text>

  <rect x="300" y="60" width="180" height="56" rx="6" fill="#fff" stroke="#16a34a"/>
  <text x="390" y="84" text-anchor="middle" font-size="11" font-weight="700" fill="#155e2f">OutSideBpmAccessController</text>
  <text x="390" y="102" text-anchor="middle" font-size="9" fill="#14532d">/outSide/* (5 端点)</text>

  <rect x="500" y="60" width="180" height="56" rx="6" fill="#fff" stroke="#16a34a"/>
  <text x="590" y="84" text-anchor="middle" font-size="11" font-weight="700" fill="#155e2f">OutSideBpmBusinessController</text>
  <text x="590" y="102" text-anchor="middle" font-size="9" fill="#14532d">/outSideBpm/* (14 端点)</text>

  <rect x="700" y="60" width="180" height="56" rx="6" fill="#fff" stroke="#16a34a"/>
  <text x="790" y="84" text-anchor="middle" font-size="11" font-weight="700" fill="#155e2f">OutSideBpmCallbackUrlConfController</text>
  <text x="790" y="102" text-anchor="middle" font-size="9" fill="#14532d">/outSideBpm/callbackUrlConf (4 端点)</text>

  <rect x="300" y="140" width="580" height="100" rx="6" fill="#fef9c3" stroke="#a16207"/>
  <text x="590" y="164" text-anchor="middle" font-size="12" font-weight="700" fill="#713f12">三方接入核心能力</text>
  <text x="320" y="186" font-size="10" fill="#422006">• 业务方管理(businessParty):多租户隔离</text>
  <text x="320" y="204" font-size="10" fill="#422006">• 应用管理(app):每个业务方多个应用</text>
  <text x="320" y="222" font-size="10" fill="#422006">• 条件模板 + 审批人模板:复用配置</text>

  <!-- 箭头 -->
  <line x1="200" y1="140" x2="280" y2="140" stroke="#475569" stroke-width="2" marker-end="url(#arr18)"/>
  <text x="240" y="132" text-anchor="middle" font-size="9" fill="#475569">REST API</text>
</svg>

### 核心 Open API

#### 1. 三方流程发起

```http
POST /outSide/processSubmit
Content-Type: application/json

{
  "appId": "your_app_id",
  "businessId": "your_business_id",
  "formCode": "LEAVE_WMA",
  "processNumber": "EXT-2024-001",
  "startUserId": "u001",
  "formData": {
    "leaveType": "年假",
    "leaveHour": 8,
    "reason": "回家探亲"
  },
  "assignees": {
    "node1": ["u002"],
    "node2": ["u003"]
  }
}
```

返回:

```json
{
  "code": 200,
  "data": {
    "processNumber": "EXT-2024-001",
    "processInstanceId": "xxx",
    "status": "HANDLING"
  }
}
```

#### 2. 三方流程预览

```http
POST /outSide/processPreview
```

#### 3. 三方流程中断

```http
POST /outSide/processBreak
```

#### 4. 流程记录查询

```http
GET /outSide/outSideProcessRecord?processNumber=EXT-2024-001
```

### 回调通知

AntFlow 通过 `OutSideBpmCallbackUrlConf` 配置回调 URL,在流程状态变更时主动通知三方系统:

```java
// ThirdPartyCallBackServiceImpl
public void notifyCallBack(OutSideBpmCallbackUrlConf conf, BpmBusinessProcess bp) {
    HttpUtil.post(conf.getCallbackUrl(), JSON.toJSONString(Map.of(
            "processNumber", bp.getBusinessNumber(),
            "status", bp.getProcessState(),
            "timestamp", System.currentTimeMillis()
    )));
}
```

### 三方接入实体

| 实体 | 用途 |
|---|---|
| `OutSideBpmBusinessParty` | 业务方(多租户) |
| `BpmProcessAppApplication` | 业务方下的应用 |
| `OutSideBpmApproveTemplate` | 审批人模板(复用配置) |
| `OutSideBpmConditionsTemplate` | 条件模板(复用配置) |
| `OutSideBpmCallbackUrlConf` | 回调 URL 配置 |
| `OutSideBpmAccessBusiness` | 三方流程实例 |
| `OutSideCallBackRecord` | 回调记录 |

## 完整集成示例:请假流程

### 1. 创建业务表

```sql
CREATE TABLE t_biz_leave (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  process_number VARCHAR(64),
  type VARCHAR(32) COMMENT '请假类型',
  hours DECIMAL(8,2) COMMENT '请假时长',
  start_date DATE,
  end_date DATE,
  reason VARCHAR(255),
  create_user VARCHAR(50),
  create_time DATETIME,
  is_del INT DEFAULT 0
);
```

### 2. 实现 FormOperationAdaptor

```java
@Service
@ActivitiServiceAnno(svcName = "LEAVE_WMA", desc = "请假流程")
public class LeaveFormAdaptor implements FormOperationAdaptor<LeaveDataVo> {
    // 详见上文示例
}
```

### 3. 配置前端路由

在 [utils/flow/const.js](file:///d:/projects/jimuoffice/antflow-vue/src/utils/flow/const.js) 中映射 formCode 到 Vue 组件:

```javascript
export const bizFormMaps = new Map([
  ['LEAVE_WMA', '/forms/form2.vue'],
  // ...
]);
```

### 4. 设计流程

登录 AntFlow,在「流程类型」新增 `LEAVE_WMA`,在「流程设计」中配置节点、审批人、条件。

### 5. 启动流程

通过前端发起,或通过 Open API:

```http
POST /bpmnConf/process/buttonsOperation?formCode=LEAVE_WMA
```

## 小结

- Spring Boot Starter 全量扫描 `org.openoa`,引入依赖即集成
- 接入用户/角色系统:覆盖 `BpmnEmployeeInfoProviderService`、`AfUserService`、`AfRoleService` 等默认 Bean
- 接入表单系统:实现 `FormOperationAdaptor`,通过 `@ActivitiServiceAnno(svcName=formCode)` 注册
- 三方接入提供 23 个 Open API 端点,支持 SaaS 多租户、回调通知
- 完整集成只需 4 步:建业务表 → 实现 FormAdaptor → 配置前端路由 → 设计流程

至此,开发指南板块已完成。下一节进入 [低代码专题](/lowcode/lowcode-overview)。
