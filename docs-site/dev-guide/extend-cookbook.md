# 扩展实战手册

> 4 个完整 Step-by-Step 代码示例，覆盖 AntFlow 的四个核心扩展方向：审批人来源、条件类型、通知渠道、DIY 表单。

## 场景一：扩展审批人来源——按「项目负责人」审批

### 需求

新增一种审批人类型：根据表单中的项目编号字段，自动查找该项目负责人作为审批人。

### Step 1: 扩展枚举

```java
// NodePropertyEnum 中新增（如果不需要新的 nodeType 可不改）
// PersonnelEnum 中新增映射
PROJECT_LEADER(19, "项目负责人");
```

### Step 2: 实现 BpmnPersonnelProviderService

```java
// 新建 provider: NodePropertyProjectLeaderProvider.java
@Component
public class NodePropertyProjectLeaderProvider implements BpmnPersonnelProviderService {

    @Override
    public List<BpmnNodeParamsAssigneeVo> getAssigneeList(
            BpmnNodeVo nodeVo, BpmnStartConditionsVo startConditionsVo) {

        // 1. 从表单条件中取出项目编号
        String projectCode = startConditionsVo.getAccountType();  // 或自定义字段
        if (StringUtils.isEmpty(projectCode)) {
            return Collections.emptyList();
        }

        // 2. 根据项目编号查询项目负责人
        String projectLeadId = projectService.getProjectLeadByCode(projectCode);
        if (projectLeadId == null) return Collections.emptyList();

        // 3. 构造返回
        return Collections.singletonList(
            AssigneeVoBuildUtils.buildVo(projectLeadId, BpmnEmployeeInfoProviderService)
        );
    }
}
```

### Step 3: 实现 AbstractBpmnPersonnelAdaptor（可选）

如果是全新的审批人逻辑，可以继承 `AbstractBpmnPersonnelAdaptor`：

```java
// antflow-engine/.../adp/personneladp/NodePropertyProjectLeaderAdp.java
@Component
public class NodePropertyProjectLeaderAdp extends AbstractBpmnPersonnelAdaptor {

    @PostConstruct
    public void init() {
        addSupportBusinessObjects(NodePropertyEnum.PROJECT_LEADER);
    }

    // 继承基类的 setNodeParams / buildAssignees / 去重 / 委派 / 名称补全 等逻辑
}
```

### Step 4: 前端配置（如需设计器支持）

在设计器中注册新的审批人选项（`approverDrawer.vue`），绑定枚举值 19。

---

## 场景二：扩展条件类型——按「请假天数」判断

### 需求

新增条件类型 `LEAVE_DAYS`，根据请假天数判断走哪个审批分支。

### Step 1: 实现 ConditionJudge

```java
// antflow-engine/.../adp/conditionfilter/LeaveDaysJudge.java
@Component
public class LeaveDaysJudge extends AbstractJudge<BpmnNodeConditionsConfBaseVo> {

    @PostConstruct
    public void init() {
        addSupportBusinessObjects(ConditionTypeEnum.LEAVE_DAYS);
    }

    @Override
    public boolean judge(String nodeId,
                         BpmnNodeConditionsConfBaseVo conditionsConf,
                         BpmnStartConditionsVo startConditionsVo,
                         int groupIndex, int index) {

        // 1. 从 startConditionsVo 获取实际请假天数
        Double leaveDays = startConditionsVo.getLeaveHour();

        // 2. 从 conditionsConf 获取配置的比较值
        ConditionItem item = conditionsConf.getConditionGroups()
            .get(groupIndex).getConditionList().get(index);
        Double threshold = Double.parseDouble(item.getConditionValue());

        // 3. 根据运算符判断
        JudgeOperatorEnum operator = JudgeOperatorEnum.getByCode(
            Integer.parseInt(item.getOperator()));
        return compare(leaveDays, threshold, operator);
    }
}
```

### Step 2: 前端注册

在 `selectConditionDialog.vue` 的条件类型选择器中注册：

```javascript
// antflow-vue/src/components/Workflow/drawer/selectConditionDialog.vue
const conditionTypeOptions = [
  // ... 已有类型
  { code: 'LEAVE_DAYS', name: '请假天数' }
];
```

---

## 场景三：扩展通知渠道——对接企业微信

### 需求

除了内置的邮件/短信/App 推送，新增企业微信通知渠道。

### Step 1: 扩展枚举（`MessageSendTypeEnum` 已预定义）

```java
// antflow-base/.../constant/enums/MessageSendTypeEnum.java (已存在)
WECHAT_TYPE(5, "企业微信"),
```

### Step 2: 实现 AbstractMessageSendAdaptor

```java
// antflow-engine/.../adp/processnotice/WechatSendAdaptor.java
@Component
public class WechatSendAdaptor extends AbstractMessageSendAdaptor {

    @PostConstruct
    public void init() {
        addSupportBusinessObjects(MessageSendTypeEnum.WECHAT_TYPE);
    }

    @Override
    public void sendMessage(List<String> receivers, String content,
                            MessageSendTypeEnum sendType) {
        for (String receiver : receivers) {
            try {
                // 使用企业微信 SDK 发送消息
                // 注意：不要调用 MessageServiceImpl，直接使用 SDK
                wechatWorkApi.sendTextMessage(receiver, content);
            } catch (Exception e) {
                log.error("企业微信消息发送失败: receiver={}", receiver, e);
                // 可选：写入失败队列或降级
            }
        }
    }

    @Override
    public MessageSendTypeEnum getSupportType() {
        return MessageSendTypeEnum.WECHAT_TYPE;
    }
}
```

### Step 3: 在流程设置中启用

进入 **流程运维 → 消息模板**，编辑对应流程的通知模板，勾选「企业微信」渠道：

![消息模板](/images/light-11-msgtemplate.png)

消息模板支持通配符替换：`${发起人}`、`${流程编号}`、`${审批结果}` 等，系统会自动替换。

---

## 场景四：开发一个新的 DIY 流程——「合同审批」

### 需求

开发一个完整的「合同审批」DIY 流程，需要后端写代码处理业务逻辑。

### Step 1: 定义 VO

```java
// antflow-web/.../vo/ContractApplyVo.java
@Data
public class ContractApplyVo extends BusinessDataVo {
    private String contractName;     // 合同名称
    private String partyName;        // 对方单位
    private Double contractAmount;   // 合同金额
    private String contractType;     // 合同类型
    private Date effectiveDate;      // 生效日期
}
```

### Step 2: 实现 FormOperationAdaptor

```java
// antflow-web/.../adaptor/ContractFormAdaptor.java
@ActivitiServiceAnno(svcName = "CONTRACT_WMA", desc = "合同审批流程")
@Component
public class ContractFormAdaptor implements FormOperationAdaptor<ContractApplyVo> {

    @Autowired
    private ContractService contractService;

    @Override
    public BpmnStartConditionsVo launchParameters(ContractApplyVo vo) {
        // 必须实现：设置启动参数
        BpmnStartConditionsVo conditions = new BpmnStartConditionsVo();
        conditions.setContractAmount(vo.getContractAmount());  // 用于条件判断
        conditions.setStartUserId(SecurityUtils.getLogInEmpIdStr());
        return conditions;
    }

    @Override
    public void submitData(ContractApplyVo vo) {
        // 必须实现：持久化合同数据到业务表
        ContractEntity entity = new ContractEntity();
        BeanUtils.copyProperties(vo, entity);
        entity.setStatus("审批中");
        contractService.save(entity);
    }

    @Override
    public void queryData(ContractApplyVo vo) {
        // 必须实现：查询合同数据供审批人查看
        ContractEntity entity = contractService.getByProcessNum(vo.getProcessNumber());
        BeanUtils.copyProperties(entity, vo);
    }

    @Override
    public void consentData(ContractApplyVo vo) {
        // 可选：审批通过后的状态更新
        contractService.updateStatus(vo.getProcessNumber(), "审批中");
    }

    @Override
    public void cancellationData(ContractApplyVo vo) {
        // 必须实现：流程作废时作废合同数据
        contractService.updateStatus(vo.getProcessNumber(), "已作废");
    }

    @Override
    public void finishData(BusinessDataVo vo) {
        // 流程结束时自动调用（ProcessFinishListener）
        contractService.updateStatus(vo.getProcessNumber(), "已完成");
        log.info("合同审批完成: processNum={}", vo.getProcessNumber());
    }
}
```

### Step 3: 配置数据库

在 SQL 中新增业务表：

```sql
CREATE TABLE contract_apply (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    process_number VARCHAR(50) NOT NULL COMMENT '流程编号',
    contract_name VARCHAR(200) COMMENT '合同名称',
    party_name VARCHAR(200) COMMENT '对方单位',
    contract_amount DECIMAL(18,2) COMMENT '合同金额',
    contract_type VARCHAR(50) COMMENT '合同类型',
    effective_date DATE COMMENT '生效日期',
    status VARCHAR(20) DEFAULT '审批中' COMMENT '状态',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP
);
```

### Step 4: 前端注册表单组件

```javascript
// antflow-vue/src/utils/flow/const.js
export const bizFormMaps = {
  // ... 已有注册
  CONTRACT_WMA: () => import('@/views/biz/contract/ContractForm.vue')
};
```

### Step 5: 创建流程分类

进入 **流程管理 → 流程类型**，新增：

| 字段 | 值 |
|---|---|
| 流程名称 | 合同审批 |
| 类型标识 | `CONTRACT_WMA` |
| 表单类型 | DIY |

### Step 6: 设计流程

进入 **流程管理 → 流程设计**，选择「合同审批」，设计审批节点和条件分支。

### Step 7: 发起与审批

用户即可在「我的发起」中选择「合同审批」发起流程。

---

## 四方向对比总结

| 维度 | 审批人来源 | 条件类型 | 通知渠道 | DIY 表单 |
|---|---|---|---|---|
| **扩展接口** | `BpmnPersonnelProviderService` | `ConditionJudge` | `AbstractMessageSendAdaptor` | `FormOperationAdaptor` |
| **枚举注册** | `NodePropertyEnum` + `PersonnelEnum` | `ConditionTypeEnum` | `MessageSendTypeEnum` | `@ActivitiServiceAnno(svcName)` |
| **注册模式** | `@Component` + `@PostConstruct` + `addSupportBusinessObjects` | 同左 | 同左 | `@Component` + `@ActivitiServiceAnno` |
| **前端改造** | `approverDrawer.vue` 添加选项 | `selectConditionDialog.vue` 添加类型 | 模板中勾选渠道 | `bizFormMaps` 注册组件 |
| **实现难度** | 低（单方法实现） | 低（单方法实现） | 中（需集成渠道 SDK） | 高（全生命周期 11 个回调） |
| **必须实现** | `getAssigneeList()` | `judge()` | `sendMessage()` | `launchParameters()` + `submitData()` + `queryData()` + `cancellationData()` |

---

## 下一步

- [扩展审批人来源（详细）](/dev-guide/extend-approver)
- [扩展条件规则（详细）](/dev-guide/extend-condition)
- [扩展通知渠道（详细）](/dev-guide/extend-notice)
- [DIY 回调与事件机制](/dev-guide/diy-callback-events) — 完整回调生命周期
