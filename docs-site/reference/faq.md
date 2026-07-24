# FAQ

> 本章汇总 AntFlow 使用过程中最高频的问题与解答,涵盖入门选型、部署、开发、运维四大类,助你快速排雷。

## 入门选型

### Q1: AntFlow 是免费开源的吗?能否商用?

**A:** AntFlow 采用 **Apache License 2.0** 协议开源,完全免费,可商用、可修改、可闭源衍生。作者承诺"日后也不会增加社区版和专业版,就一个版本"。

- 仓库:[GitHub](https://github.com/mrtylerzhou/AntFlow) / [Gitee](https://gitee.com/tylerzhou/Antflow)
- 多数据库适配文档中部分标价(如达梦 Oracle 模式 999 元),那是**作者的付费咨询/技术支持费用**,非软件授权费用,自行接入完全免费

---

### Q2: AntFlow 与 Flowable / Camunda / Activiti 的关系?

**A:** AntFlow 基于 **Activiti 5.23** 深度改造(魔改),并在此基础上自研了业务流转引擎。关系如下:

| 项目 | 关系 | 区别 |
|---|---|---|
| Activiti 5.23 | AntFlow 的底层引擎 | AntFlow 对其源码魔改,提供 SQL 必须用项目提供的 |
| Flowable | Activiti 6+ 的 fork | AntFlow 不直接基于 Flowable,但可借鉴其特性 |
| Camunda | Activiti 早期 fork,独立发展 | AntFlow 与其无直接关系 |

AntFlow 的核心创新是 **VNode 虚拟节点模式** + **Adaptor 适配器模式**,使业务代码与引擎 API 高度解耦,理论上可以无缝替换底层引擎。

---

### Q3: AntFlow 适合什么场景?不适合什么场景?

**A:**

**适合的场景:**
- 中国式办公审批(请假、报销、用车、合同、采购等)
- 需要可视化设计流程的业务
- 需要 SaaS 多租户的流程服务
- 国产化信创场景(支持 12+ 种国产数据库)
- POC / Demo 快速验证

**不适合的场景:**
- 高频实时数据处理(每秒万级流转,建议用 Kafka + 自研)
- 复杂事件流处理(CEP,建议用 Flink)
- 纯粹的 ETL 数据管道(建议用 Airflow / DolphinScheduler)
- 微服务编排(建议用 Temporal / Camunda Zeebe)

---

### Q4: AntFlow 支持哪些数据库?

**A:** 通过 MyBatis-Plus `DbType` 机制支持 **12+ 种数据库**:

- 开源:MySQL、PostgreSQL、MongoDB(实验性)
- 商业:Oracle、SQL Server
- 国产:达梦 DM8、人大金仓 Kingbase、南大通用 GBase、OceanBase、高斯 GaussDB
- 云原生:PolarDB-PG、PolarDB-MySQL

详见 [多数据库支持](/ops/db-multi) 章节。

---

### Q5: 我应该用 LF(低代码)还是 DIY 模式?

**A:** 简单决策:

| 你的需求 | 推荐模式 |
|---|---|
| 通用审批(请假/报销/用车) | LF |
| POC / Demo | LF |
| SaaS 多租户 | LF(外部表单模式) |
| 需调用企业内部 API | DIY |
| 复杂表单字段联动 | DIY |
| 表单字段需计算 | DIY |

详见 [低代码 vs 自定义表单](/lowcode/lowcode-vs-diy) 章节的决策树。

---

## 部署相关

### Q6: 启动报错 "Table 'act_xxx' doesn't exist" 怎么办?

**A:** 必须使用 AntFlow 项目 [script/](file:///d:/projects/jimuoffice/script/) 目录下的 SQL 初始化数据库,**不要开启 Activiti 自动建表**(因为 AntFlow 对 Activiti 源码进行了魔改):

```bash
mysql -u root -p antflow
mysql> source script/act_init_db.sql;
mysql> source script/bpm_init_db.sql;
mysql> source script/bpm_init_db_data.sql;  -- 测试数据可选
```

确保 `application.properties`:

```properties
spring.activiti.check-process-definitions=false
spring.activiti.database-schema-update=none
```

详见 [常见问题排查](/ops/troubleshooting) Q1。

---

### Q7: AntFlow 默认端口是多少?如何修改?

**A:** 后端默认 **7001**,前端默认 **80**(可被占用,改用 8080)。

修改后端端口:[application-dev.properties](file:///d:/projects/jimuoffice/antflow-web/src/main/resources/application-dev.properties):

```properties
server.port=7002
```

修改前端端口:

```bash
pnpm run dev -- --port 8080
```

---

### Q8: Docker 部署有现成镜像吗?

**A:** AntFlow 仓库**未提供 Dockerfile**,需自行编写。文档中提供了完整的 Dockerfile 和 docker-compose.yml 示例,见 [生产部署](/ops/deploy#docker-化部署-可选) 章节。

---

### Q9: 如何切换数据库?需要重新建表吗?

**A:** 切换数据库步骤:

1. 修改 `application.properties` 中的数据源 URL、用户名、密码、驱动
2. 添加对应 JDBC 驱动依赖到 `pom.xml`
3. 用对应数据库的 SQL 脚本建表(MySQL 脚本需转换,详见各数据库文档)
4. **必须关闭 Activiti 自动建表**:`spring.activiti.database-schema-update=none`
5. (可选)首次启动可临时设置 `database-schema-update=true` 让 Activiti 创建引擎表,创建后立即改回 `none`

详见 [多数据库支持](/ops/db-multi) 章节。

---

### Q10: 生产环境必须装 Redis 吗?

**A:** **不是必须**,但**强烈推荐**。Redis 用于:

- 分布式锁(防止并发审批冲突)
- Session 缓存(多实例部署需要)
- 字典数据缓存(降低数据库压力)

单机部署可不用 Redis,但生产环境建议使用。

---

## 开发相关

### Q11: 我需要实现什么接口才能接入 AntFlow?

**A:** 最低接入成本只需实现 **1 个接口**:

```java
public interface AfUserService {
    List<BaseIdTranStruVo> queryByNameFuzzy(String userName);
    List<BaseIdTranStruVo> queryUserByIds(Collection<String> userIds);
    BaseIdTranStruVo getById(String id);
    List<BaseIdTranStruVo> queryCompanyByNameFuzzy(String companyName);
}
```

`BaseIdTranStruVo` 只有 `id` 和 `name` 两个字段,流程引擎只需要"谁在审批"。

详见 [集成现有系统](/dev-guide/integrate-existing) 章节。

---

### Q12: 如何自定义审批人来源(比如按岗位审批)?

**A:** 实现 `BpmnPersonnelProviderService` 接口,4 步:

1. 扩展 `NodePropertyEnum` 和 `PersonnelEnum`
2. 实现 `BpmnPersonnelProviderService`,加 `@Component("beanName")`
3. (可选)前端添加选择控件
4. 重启应用

详见 [扩展审批人来源](/dev-guide/extend-approver) 章节的"按岗位审批"示例。

---

### Q13: 如何自定义条件规则?

**A:** 实现 `ConditionJudge` 接口,3 步:

1. 扩展 `ConditionTypeEnum`
2. 实现 `ConditionJudge`,加 `@Service`
3. (可选)前端添加配置控件

详见 [扩展条件规则](/dev-guide/extend-condition) 章节的"职级白名单"示例。

---

### Q14: 如何自定义通知渠道(如企业微信)?

**A:** 继承 `AbstractMessageSendAdaptor`,3 步:

1. (如新增类型)扩展 `MessageSendTypeEnum`
2. 继承 `AbstractMessageSendAdaptor<T>`,实现 `sendMessageBatchByType` 和 `setSupportBusinessObjects`
3. 加 `@Component`

详见 [扩展通知渠道](/dev-guide/extend-notice) 章节的钉钉适配器示例。

---

### Q15: 自定义实现未生效,默认实现仍被调用?

**A:** 检查以下几点:

1. **加 `@Primary` 注解**覆盖默认实现:

```java
@Service
@Primary  // 关键
public class YourUserService implements AfUserService { ... }
```

2. **包扫描覆盖**:确认 `application.properties` 中:

```properties
antflow.common.scan-packages=org.openoa,com.yourcompany
```

3. **bean 名唯一**:自定义 `@Component("name")` 的 name 不能与默认实现冲突

4. **启动日志检查**:开启 debug 日志查看 bean 注册情况:

```properties
logging.level.org.springframework=debug
```

详见 [常见问题排查](/ops/troubleshooting) Q18。

---

### Q16: DIY 模式如何接入?formCode 怎么定?

**A:** DIY 模式接入 6 步:

1. **创建业务表** SQL(如 `t_biz_leave`)
2. **实现 Entity + Mapper**(MyBatis-Plus)
3. **实现 Service + Controller**
4. **实现 `FormOperationAdaptor`**,加 `@ActivitiServiceAnno(svcName = "LEAVE_WMA")`
5. **编写 Vue 表单组件** `.vue` 文件
6. **注册路由**:`bizFormMaps` 添加 `['LEAVE_WMA', '/forms/form2.vue']`

`formCode` 命名约定:大写 + 业务标识 + `_WMA`(如 `LEAVE_WMA`、`UCARREFUEl_WMA`),与 `t_bpmn_conf.form_code` 字段一致。

详见 [低代码 vs 自定义表单](/lowcode/lowcode-vs-diy) 章节的 DIY 示例代码。

---

### Q17: 低代码流程如何嵌入业务逻辑?

**A:** 实现 `LFFormOperationAdaptor` 接口,bean 名 = formCode:

```java
@Service("LEAVE_LF_WMA")  // bean 名必须等于低代码表单的 formCode
public class LeaveLFFormAdaptor implements LFFormOperationAdaptor<UDLFApplyVo> {
    @Override
    public void onBeforeSubmit(UDLFApplyVo vo) {
        // 提交前校验
    }
    @Override
    public void onAfterApprove(UDLFApplyVo vo) {
        // 审批后回调
    }
}
```

注册后引擎会优先调用你的实现,而非默认 `LowFlowApprovalService`。

详见 [低代码表单引擎](/lowcode/lowcode-form#spi-扩展点-lfformoperationadaptor) 章节。

---

## 运维相关

### Q18: 如何备份 AntFlow 数据?

**A:** 关键数据备份:

```bash
# 1. 数据库全量备份
mysqldump -u root -p antflow > antflow_backup_$(date +%Y%m%d).sql

# 2. 增量备份(binlog)
mysqlbinlog --start-datetime="2024-01-01 00:00:00" \
            --stop-datetime="2024-01-02 00:00:00" \
            /var/lib/mysql/mysql-bin.000001 > incremental.sql

# 3. 配置文件备份
tar -czf antflow_config_$(date +%Y%m%d).tar.gz \
    /opt/antflow/application-pro.properties \
    /opt/antflow/logback-spring.xml \
    /etc/nginx/conf.d/antflow.conf

# 4. 上传文件备份(如有)
tar -czf antflow_files_$(date +%Y%m%d).tar.gz /opt/antflow/uploads/
```

建议通过 crontab 每天凌晨执行,保留 30 天。

---

### Q19: 升级 AntFlow 需要注意什么?

**A:** 升级流程:

1. **备份**(数据库 + jar + 配置)
2. **阅读 Release Notes**:关注 breaking changes
3. **测试环境先升级**:验证 1-2 天
4. **生产环境低峰期升级**:
   - 停服务
   - 替换 jar
   - 执行增量 SQL(如有)
   - 启动服务
   - 健康检查
5. **观察 1-3 天**:监控日志、用户反馈

回滚方案:恢复 jar + 恢复数据库备份。

---

### Q20: 流程实例数据堆积,如何归档?

**A:** Activiti 历史表(`act_hi_*`)会持续增长,建议定期归档:

```sql
-- 1. 创建归档表
CREATE TABLE act_hi_taskinst_archive LIKE act_hi_taskinst;

-- 2. 归档 6 个月前的数据
INSERT INTO act_hi_taskinst_archive 
SELECT * FROM act_hi_taskinst 
WHERE end_time_ < DATE_SUB(NOW(), INTERVAL 6 MONTH);

-- 3. 删除原表数据(谨慎)
DELETE FROM act_hi_taskinst 
WHERE end_time_ < DATE_SUB(NOW(), INTERVAL 6 MONTH);

-- 4. 优化表(回收空间)
OPTIMIZE TABLE act_hi_taskinst;
```

详见 [性能优化](/ops/performance#历史数据归档) 章节。

---

### Q21: 监控应该如何接入?

**A:** 推荐三件套:**Actuator + Prometheus + Grafana**

1. 开启 Actuator:

```properties
management.endpoints.web.exposure.include=health,info,metrics,prometheus
management.metrics.export.prometheus.enabled=true
```

2. Prometheus 抓取:

```yaml
scrape_configs:
  - job_name: 'antflow'
    metrics_path: '/actuator/prometheus'
    static_configs:
      - targets: ['antflow-host:7001']
```

3. Grafana 推荐仪表盘:JVM(ID 4701)、Spring Boot(ID 11378)、MySQL(ID 7362)

详见 [性能优化](/ops/performance#监控指标) 章节。

---

### Q22: 如何查看慢 SQL?

**A:** 两种方式:

1. **AntFlow 内置慢 SQL 日志**:

```bash
tail -100 ~/log/antflow/logs/slowsql.log
```

2. **Druid 监控面板**:

```properties
spring.datasource.druid.filter.stat.slow-sql-millis=1000  # 超过 1 秒记录
spring.datasource.druid.filter.stat.log-slow-sql=true
```

访问 `http://your-domain.com/api/druid/sql.html` 查看慢 SQL 列表。

---

## 业务相关

### Q23: AntFlow 支持哪些中国式办公场景?

**A:** 完整支持:

- 串行审批(逐级)
- 并行审批(会签/或签/顺序会签)
- 加批(临时增加审批人)
- 委托(转交他人处理)
- 转办(转给其他人)
- 退回到任意历史节点
- 撤回(发起人主动撤销)
- 变更审批人(管理员干预)
- 加签/减签(动态调整会签人数)
- 跳过节点(条件满足自动跳)
- 自动完成(条件满足自动通过)
- 抄送(通知非审批人)

详见 [节点类型详解](/workflow-design/node-types) 章节。

---

### Q24: 流程版本管理是如何工作的?

**A:** 

- 一个流程可有多个版本,通过 `bpmn_code` 后缀区分(如 `LEAVE_WMA-00001`、`LEAVE_WMA-00002`)
- **同族仅一个生效版本**(互斥),其他版本 `effective_status=0`
- **运行中实例锁定版本**:即使发布新版本,已有实例仍按原版本流转
- 管理员可执行版本迁移,将运行中实例迁移到新版本

详见 [版本管理与启动](/workflow-design/version-management) 章节。

---

### Q25: 抄送节点为什么用虚拟 assignee(-3)?

**A:** AntFlow 的抄送节点使用 `CC_NODE` 虚拟 assignee(值为 `-3`),原因:

1. **统一处理**:抄送节点是 userTask,需要 assignee 才能创建任务
2. **自动完成**:CC_NODE 任务被引擎自动 complete,不阻塞流程
3. **避免误分配**:`-3` 是保留值,不会与真实用户 ID 冲突
4. **统一识别**:引擎通过 `assignee_ = '-3'` 识别抄送任务

详见 [虚拟节点系统](/dev-guide/vnode-system) 章节。

---

### Q26: 条件评估是否使用 Drools 规则引擎?

**A:** AntFlow 内置 Drools 依赖,但**条件评估不使用 Drools**,而是用自研的 `ConditionJudge` 策略模式:

- 每个条件类型对应一个 `ConditionJudge` 实现
- 通过 `ConditionTypeEnum` 枚举路由
- 评估逻辑是纯 Java,性能高、易扩展
- Drools 依赖保留是为未来扩展预留

详见 [扩展条件规则](/dev-guide/extend-condition) 章节。

---

### Q27: AntFlow 的"表单上下文人员"是什么?

**A:** 一种特殊的审批人来源(`NODE_PROPERTY_FORM_RELATED`),允许从表单字段中提取审批人。

例如:用车申请表单中有"司机"字段,可直接配置该字段的值为审批人,无需额外配置。

详见 [审批人规则](/workflow-design/approver-rules) 章节。

---

### Q28: 流程预览图是图片还是 JSON?

**A:** **JSON 数据**,前端渲染为 SVG 图。

- 后端返回 `PreviewNode` 列表(JSON)
- 前端 `flowPreview/index.vue` 根据 JSON 渲染流程图
- 优势:支持交互(点击节点查看详情)、跨端兼容、体积小

详见 [流程预览](/workflow-run/flow-preview) 章节。

---

## 集成相关

### Q29: 第三方系统如何接入 AntFlow?

**A:** 两种方式:

**方式 A:REST API(推荐)**

第三方系统调用 AntFlow 的 REST API:

```bash
# 发起流程
curl -X POST http://antflow-host:7001/bpmnConf/process/buttonsOperation \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "formCode=LEAVE_WMA&values={...}"
```

详见 [REST API 参考](/dev-guide/rest-api) 章节。

**方式 B:Spring Boot Starter(深度集成)**

```xml
<dependency>
    <groupId>org.openoa</groupId>
    <artifactId>antflow-spring-boot-starter</artifactId>
    <version>1.0</version>
</dependency>
```

引入依赖后,通过 `@Autowired` 注入 AntFlow 服务。

详见 [集成现有系统](/dev-guide/integrate-existing) 章节。

---

### Q30: 如何对接钉钉/企业微信?

**A:** 两种方式:

**方式 A:消息通知渠道**

实现 `MessageSendAdaptor`,详见 [扩展通知渠道](/dev-guide/extend-notice) 章节的钉钉适配器示例。

**方式 B:用户系统对接**

实现 `BpmnEmployeeInfoProviderService`,从钉钉/企业微信通讯录 API 获取用户信息。

---

### Q31: AntFlow 支持 SaaS 多租户吗?

**A:** 支持。开启方式:

```properties
antflow.sass.full-sass-mode=true
```

支持两种隔离级别:

- **共享数据库 + tenant_id 字段隔离**(轻量)
- **独立数据库**(完全隔离,适合高校/政府等强隔离场景)

详见 [集成现有系统](/dev-guide/integrate-existing) 章节。

---

## 章节导航

- [术语表](/reference/glossary) — 名词解释
- [版本变更](/reference/changelog) — 版本历史
- [常见问题排查](/ops/troubleshooting) — 详细排查指南
- [快速开始](/guide/quick-start) — 入门指引
- [生产部署](/ops/deploy) — 部署流程
