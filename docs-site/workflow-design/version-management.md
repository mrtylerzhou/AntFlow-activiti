# 版本管理与启动

> AntFlow 的每个流程设计保存后都会生成一个新版本。同一 FormCode 下可有多个版本,同时只有一个版本处于活跃状态。新发起的流程使用活跃版本,运行中的实例继续按原版本流转。

## 版本实体:BpmnConf

每一行 `t_bpmn_conf` 记录就是一个流程版本。

**实体**:[BpmnConf.java](https://github.com/mrtylerzhou/AntFlow/blob/master/antflow-base/src/main/java/org/openoa/base/entity/BpmnConf.java)

| 字段 | 列名 | 类型 | 说明 |
|---|---|---|---|
| id | id | Long | 自增主键 |
| bpmnCode | bpmn_code | String | 版本编码(如 `QJ-00001`) |
| bpmnName | bpmn_name | String | 版本名称 |
| formCode | form_code | String | 流程类型标识(FormCode) |
| effectiveStatus | effective_status | Integer | **0=不活跃, 1=活跃** |
| isLowCodeFlow | is_lowcode_flow | Integer | 0=DIY, 1=LF |
| isOutSideProcess | is_out_side_process | Integer | 是否第三方接入流程 |
| extraFlags | extra_flags | Integer | 二进制位标志 |
| lfFormdataIds | lf_formdata_ids | String | 外部表单引用的表单版本 ID(CSV) |
| confConfigJson | conf_config_json | String | conf 级配置 JSON |
| deduplicationType | deduplication_type | Integer | 去重类型(1=不去重,2=前向,3=后向) |
| businessPartyId | business_party_id | Long | 业务方 ID |
| tenantId | tenant_id | String | 租户 ID |

### bpmnCode 生成规则

`BpmnConfBizServiceImpl.getBpmnCode`:
1. 取 `bpmnName` 首字母拼音
2. 查询同前缀最大 bpmnCode
3. +1 格式化为 5 位数字

格式常量 `formatMark = "%05d"`,`BPMN_CODE_LEN = 5`,正则 `.*-([0-9]{5})`。

示例:版本名"请假流程"→ 首字母"QJ" → `QJ-00001`、`QJ-00002`...

## 版本列表页

路径:**流程管理 → 流程设计** → 点击"版本管理"

![版本管理](/images/4-1.png)

源码:[antflow-vue/src/views/workflow/flowList/version.vue](https://github.com/mrtylerzhou/AntFlow/blob/master/antflow-vue/src/views/workflow/flowList/version.vue)

列表字段:类型标识、类型名称、版本编号(bpmnCode)、版本名称(bpmnName)、流程分类(DIY/LF)、是否去重、状态(活跃/不活跃)、描述、创建时间。

### 列表操作

| 操作 | 说明 | 条件 |
|---|---|---|
| **启动** | 激活该版本 | 仅 `effectiveStatus != 1` 时可点击 |
| **编辑** | 进入设计器修改 | 跳转 `diy-design` 或 `lf-design`(带 `id` 参数) |
| **预览** | 查看流程图和节点配置 | 跳转 `flowPreview` |
| **调试** | 模拟发起,验证流程逻辑 | 跳转 `flowDebug` |

## 版本创建(发布)流程

### 前端提交流程

在 [流程设计器](/workflow-design/flow-designer) 点击"发布"按钮:

1. 并发收集 4 步数据(基础/表单/流程/高级)
2. `FormatCommitUtils.formatSettings` 格式化流程节点
3. 调用 `setApiWorkFlowData` 提交后端
4. 跳转 `flow-version` 版本管理页面

### 后端处理流程

**入口**:[BpmnConfController.edit](https://github.com/mrtylerzhou/AntFlow/blob/master/antflow-engine/src/main/java/org/openoa/engine/bpmnconf/controller/BpmnConfController.java)

```java
@PostMapping("/edit")
public Result edit(@RequestBody BpmnConfVo bpmnConfVo) {
    bpmnConfBizService.edit(bpmnConfVo);
    return Result.newSuccessResult("ok");
}
```

**[BpmnConfBizServiceImpl.edit](https://github.com/mrtylerzhou/AntFlow/blob/master/antflow-engine/src/main/java/org/openoa/engine/bpmnconf/service/biz/BpmnConfBizServiceImpl.java)** 处理:

1. 校验 bpmnName,生成新的 bpmnCode
2. `BeanUtils.copyProperties` 拷贝属性到 `BpmnConf` 实体
3. 构建 `BpmnConfConfigJson` 并序列化为 `confConfigJson`
4. **`this.getMapper().insert(bpmnConf)`** — **总是插入新行,不更新原有版本**
5. 遍历 `bpmnConfVo.getNodes()`,对每个节点:
   - `NodeUtil.nodeSpecialProcess`:处理后端节点转换(如 9→4,设置虚拟审批人)
   - 根据节点类型设置标签(copyNodeV2、automaticNode、conditionApproveNode、conditionCopyNode)
   - 插入 `t_bpmn_node` 和 `t_bpmn_node_to`
   - 构建节点级 JSON 配置(`BpmnNodeConfigHolder`)
6. 处理抄送人、加签配置、低代码表单字段权限等

::: warning 总是新增
每次发布都生成新版本行,`effectiveStatus` 默认为 0(不活跃),需要用户手动点击"启动"激活。这是 AntFlow 的设计:**永不覆盖已有版本**,保证历史可追溯。
:::

## 版本激活机制

### 激活入口

```java
@GetMapping("/effectiveBpmn/{id}")
@Transactional(rollbackFor = Exception.class)
public Result effectiveBpmn(@PathVariable("id") Integer id) {
    bpmnConfBizService.effectiveBpmnConf(id);
    return Result.newSuccessResult(null);
}
```

### 激活逻辑

`BpmnConfBizServiceImpl.effectiveBpmnConf`:

```java
public void effectiveBpmnConf(Integer id) {
    BpmnConf bpmnConf = this.getMapper().selectById(id);
    
    // 按 formCode 查询当前活跃的旧版本
    BpmnConf confInDb = this.getMapper().selectOne(
            AFWrappers.<BpmnConf>lambdaTenantQuery()
                    .eq(BpmnConf::getFormCode, bpmnConf.getFormCode())
                    .eq(BpmnConf::getEffectiveStatus, 1));

    if (!ObjectUtils.isEmpty(confInDb)) {
        // 将旧版本 effectiveStatus 置 0
        confInDb.setEffectiveStatus(0);
        this.getService().updateById(confInDb);
    }
    // 将新版本 effectiveStatus 置 1,继承 appId/bpmnType/isAll
    this.getService().updateById(BpmnConf
            .builder()
            .id(id.longValue())
            .appId(confInDb.getAppId())
            .bpmnType(confInDb.getBpmnType())
            .isAll(getIsAll(bpmnConf, confInDb))
            .effectiveStatus(1)
            .build());
}
```

### 激活规则

1. 同一个 `formCode` 下**同时只有一个版本处于活跃状态**(`effectiveStatus=1`)
2. 激活新版本时,旧活跃版本被置为 `effectiveStatus=0`
3. 新版本继承旧版本的 `appId`、`bpmnType`,`isAll` 通过 `getIsAll` 计算
4. 整个操作在 `@Transactional` 事务中执行

<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 880 240" font-family="-apple-system, BlinkMacSystemFont, 'Segoe UI', sans-serif">
  <text x="440" y="24" text-anchor="middle" font-size="16" font-weight="700" fill="#1e293b">版本激活流程</text>

  <!-- 激活前 -->
  <text x="220" y="60" text-anchor="middle" font-size="13" font-weight="700" fill="#92400e">激活前</text>
  <rect x="40" y="75" width="360" height="40" rx="6" fill="#dcfce7" stroke="#16a34a" stroke-width="1.5"/>
  <text x="220" y="100" text-anchor="middle" font-size="11" fill="#1e293b">版本 QJ-00001 effectiveStatus=1 (活跃)</text>
  <rect x="40" y="125" width="360" height="40" rx="6" fill="#fee2e2" stroke="#ef4444" stroke-width="1.5"/>
  <text x="220" y="150" text-anchor="middle" font-size="11" fill="#1e293b">版本 QJ-00002 effectiveStatus=0 (不活跃)</text>

  <!-- 箭头 -->
  <line x1="420" y1="120" x2="460" y2="120" stroke="#475569" stroke-width="2" marker-end="url(#e)"/>
  <text x="440" y="112" text-anchor="middle" font-size="10" fill="#475569">激活</text>

  <!-- 激活后 -->
  <text x="660" y="60" text-anchor="middle" font-size="13" font-weight="700" fill="#166534">激活后</text>
  <rect x="480" y="75" width="360" height="40" rx="6" fill="#fef3c7" stroke="#d97706" stroke-width="1.5"/>
  <text x="660" y="100" text-anchor="middle" font-size="11" fill="#1e293b">版本 QJ-00001 effectiveStatus=0 (不活跃)</text>
  <rect x="480" y="125" width="360" height="40" rx="6" fill="#dcfce7" stroke="#16a34a" stroke-width="1.5"/>
  <text x="660" y="150" text-anchor="middle" font-size="11" fill="#1e293b">版本 QJ-00002 effectiveStatus=1 (活跃)</text>

  <!-- 运行中实例 -->
  <rect x="180" y="185" width="520" height="40" rx="6" fill="#eef2ff" stroke="#6366f1" stroke-width="1.5"/>
  <text x="440" y="210" text-anchor="middle" font-size="11" fill="#1e293b">运行中实例:继续按原版本(QJ-00001)流转,直到结束</text>

  <defs><marker id="e" viewBox="0 0 10 10" refX="9" refY="5" markerWidth="6" markerHeight="6" orient="auto-start-reverse"><path d="M 0 0 L 10 5 L 0 10 z" fill="#475569"/></marker></defs>
</svg>

## 运行中实例的处理

::: warning 核心规则
**版本激活不会自动迁移运行中的实例**。
- 每个运行中的流程实例(`BpmBusinessProcess`)通过 `version` 字段引用具体的 `bpmnCode`
- 实例在启动时即绑定到当时的活跃版本
- 激活新版本后,**旧版本上正在运行的实例继续按旧版本流转**,直到结束
- **新发起的实例**会使用新激活的版本
- 历史版本(`effectiveStatus=0`)不会被删除,可用于历史实例查询和回溯
:::

## 手动版本迁移

对于需要将运行中实例迁移到新版本的场景,AntFlow 提供 [BpmnProcessMigrationServiceImpl](https://github.com/mrtylerzhou/AntFlow/blob/master/antflow-engine/src/main/java/org/openoa/engine/bpmnconf/service/biz/BpmnProcessMigrationServiceImpl.java)。

### migrateAndJumpToCurrent 方法

用于在条件变更后手动迁移运行中实例:

1. 接收当前任务 `currentTask`、`BpmBusinessProcess`、`BusinessDataVo`
2. 设置 `submitVo.setIsMigration(true)`,将 `bpmnCode` 设为实例当前版本
3. 调用 `processApprovalService.buttonsOperation` 重新提交
4. 通过 `additionalInfoService.getActivitiList` 获取流程定义所有活动节点
5. 遍历活动节点,对每个已存在的任务通过 `tripleConsumer` 回调处理(保留已审批记录,补全未审批节点)

### migrationCheckConditionsChange 方法

迁移前检查条件变更:若动态条件节点的评估结果发生变化,会阻止迁移。

::: tip 迁移机制用途
该迁移机制用于特定场景(如条件配置变更后的实例修复),**并非版本激活时的自动行为**。常规使用中,版本激活后旧实例继续按原版本流转即可。
:::

## 流程调试

版本列表的"调试"按钮跳转到 `/workflow/flowDebug`,提供模拟发起功能:

- 模拟填写表单数据
- 模拟条件评估结果
- 查看流程流转路径
- 验证节点配置是否正确

调试不会创建真实的流程实例,适合在发布前验证流程逻辑。

## 去重类型(deduplicationType)

版本的基础设置中可配置去重类型:

| 值 | 名称 | 说明 |
|---|---|---|
| 1 | 不去重 | 相邻节点同一人需重复审批 |
| 2 | 前向去重 | 当前节点审批人与之前节点同一人时,自动跳过 |
| 3 | 后向去重 | 当前节点审批人与之后节点同一人时,当前节点自动通过 |

去重逻辑在运行时由引擎自动处理,对用户透明。

## 下一步

- [发起流程](/workflow-run/start-flow) — 如何发起一个流程实例
- [流程预览](/workflow-run/flow-preview) — 查看流程图和审批历史
- [架构总览](/dev-guide/architecture) — 理解 AntFlow 技术架构
