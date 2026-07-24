# 常见问题排查

> 本章汇总 AntFlow 在开发、部署、运行过程中可能遇到的问题及排查思路,涵盖启动失败、数据库问题、流程引擎异常、前端问题、集成问题五大类,提供 L3 级诊断指引。

## 启动问题

### Q1: 启动报错 "Table 'act_xxx' doesn't exist"

**现象:**

```
Caused by: java.sql.SQLSyntaxErrorException: Table 'antflow.act_ru_task' doesn't exist
	at com.mysql.cj.jdbc.exceptions.SQLError.createSQLException(SQLError.java:120)
```

**原因:** Activiti 引擎表未初始化,或开启了自动建表与魔改版冲突。

**解决:**

1. 必须使用 AntFlow 项目 [script/](file:///d:/projects/jimuoffice/script/) 目录下的 SQL,**不要用 Activiti 自动建表**

```bash
mysql -u root -p antflow
mysql> source /path/to/script/act_init_db.sql;
mysql> source /path/to/script/bpm_init_db.sql;
mysql> source /path/to/script/bpm_init_db_data.sql;  -- 测试数据可选
```

2. 确保 `application.properties` 关闭自动建表:

```properties
spring.activiti.check-process-definitions=false
spring.activiti.database-schema-update=none
```

3. 重启应用

参考:[1.新手快速上手.md](file:///d:/projects/jimuoffice/doc/AI文档(经过作者review,适合新手)/1.新手快速上手.md)

---

### Q2: 启动报错 "ClassNotFoundException: com.mysql.cj.jdbc.Driver"

**现象:**

```
Caused by: java.lang.ClassNotFoundException: com.mysql.cj.jdbc.Driver
	at java.net.URLClassLoader.findClass(URLClassLoader.java:382)
```

**原因:** MySQL JDBC 驱动未打包进 jar。

**解决:**

检查 [antflow-base/pom.xml](file:///d:/projects/jimuoffice/antflow-base/pom.xml) 是否包含:

```xml
<dependency>
    <groupId>mysql</groupId>
    <artifactId>mysql-connector-java</artifactId>
    <scope>runtime</scope>  <!-- 注意:不要用 provided -->
</dependency>
```

重新打包:`mvn clean install -DskipTests`

---

### Q3: 启动报错 "Failed to configure a DataSource"

**现象:**

```
Description:
Failed to configure a DataSource: 'url' attribute is not specified and no embedded datasource could be configured.
Reason: Failed to determine a suitable driver class
Action:
Consider the following:
	If you want an embedded in-memory database, please put it on the classpath.
	If you have database settings to be loaded from a particular profile, you may need to active it.
```

**原因:** Maven Profile 未正确替换 `application.properties` 中的占位符。

**解决:**

1. 检查 `application.properties` 中的 `spring.profiles.active=@activatedProperties@`,确认是 `@...@` 格式(Maven 资源过滤占位符),而非 `${...}`(Spring 占位符)

2. 确认 Maven Profile 已激活:

```bash
mvn clean install -DskipTests -Pdev  # 指定 dev profile
```

3. 启动时显式指定 profile:

```bash
java -jar antflow-web.jar --spring.profiles.active=dev
```

4. 检查打包后的 jar 中 `application-dev.properties` 是否存在:

```bash
jar tf antflow-web.jar | grep application
# BOOT-INF/classes/application.properties
# BOOT-INF/classes/application-dev.properties
```

---

### Q4: 启动报错 "Activiti 字体乱码"

**现象:** 启动正常,但生成流程图时中文显示为方块或问号。

**原因:** Activiti 默认使用 sans-serif 字体,Linux 服务器无中文字体。

**解决:**

AntFlow 已在 [ActivitiConfig.java](file:///d:/projects/jimuoffice/antflow-engine/src/main/java/org/openoa/engine/conf/engineconfig/ActivitiConfig.java) 中配置宋体:

```java
@Configuration
public class ActivitiConfig implements ProcessEngineConfigurationConfigurer {
    @Override
    public void configure(SpringProcessEngineConfiguration processEngineConfiguration) {
        processEngineConfiguration.setActivityFontName("宋体");
        processEngineConfiguration.setAnnotationFontName("宋体");
        processEngineConfiguration.setLabelFontName("宋体");
    }
}
```

需在 Linux 服务器上安装中文字体:

```bash
# CentOS / RHEL
sudo yum install -y fontconfig wqy-zenhei-fonts wqy-microhei-fonts

# Ubuntu / Debian
sudo apt install -y fonts-wqy-zenhei fonts-wqy-microhei

# 或手动安装宋体
sudo mkdir -p /usr/share/fonts/chinese
sudo cp SimSun.ttf /usr/share/fonts/chinese/
sudo fc-cache -fv

# 验证
fc-list | grep -i song
```

---

### Q5: 启动后端口被占用

**现象:**

```
Description:
Web server failed to start. Port 7001 was already in use.
Action:
Identify and stop the process that's listening on port 7001 or configure this application to use another port.
```

**解决:**

```bash
# Linux 查找占用进程
sudo lsof -i :7001
sudo netstat -tlnp | grep 7001
sudo kill -9 <PID>

# Windows 查找占用进程
netstat -ano | findstr :7001
taskkill /PID <PID> /F

# 或修改端口
# application.properties
server.port=7002
```

---

## 数据库问题

### Q6: 数据库连接超时

**现象:**

```
com.alibaba.druid.pool.GetConnectionTimeoutException: wait millis 60000, active 100, maxActive 100
```

**原因:** 连接池被耗尽,可能是 SQL 慢查询或连接泄漏。

**解决:**

1. **查看慢 SQL**:`tail -f ~/log/antflow/logs/slowsql.log`

2. **临时调大连接池**:

```properties
spring.datasource.druid.max-active=200
spring.datasource.druid.max-wait=120000
```

3. **开启 Druid 监控面板**,定位泄漏点:

```properties
spring.datasource.druid.stat-view-servlet.enabled=true
spring.datasource.druid.stat-view-servlet.login-username=admin
spring.datasource.druid.stat-view-servlet.login-password=admin
spring.datasource.druid.remove-abandoned=true
spring.datasource.druid.remove-abandoned-timeout=300  # 缩短到 5 分钟
spring.datasource.druid.log-abandoned=true  # 打印泄漏堆栈
```

访问 `http://localhost:7001/druid/datasource.html` 查看未关闭的连接。

4. **常见泄漏点排查**:
   - 自定义 Mapper 未加 `@Transactional` 却执行多 SQL
   - Activiti `taskService.complete()` 在异常时未释放连接
   - 第三方 API 调用超时阻塞连接

---

### Q7: MySQL 字符集问题导致中文乱码

**现象:** 数据库中中文显示为 `???` 或 `?`。

**解决:**

1. **数据库字符集**:

```sql
CREATE DATABASE antflow DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;

-- 检查
SHOW VARIABLES LIKE 'character%';
-- character_set_database 应为 utf8mb4
```

2. **JDBC URL 显式指定**:

```properties
spring.datasource.url=jdbc:mysql://host:3306/antflow?useUnicode=true&characterEncoding=utf-8&serverTimezone=Asia/Shanghai
```

3. **现有数据库修复**:

```sql
ALTER DATABASE antflow CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
USE antflow;
-- 对每张表
ALTER TABLE t_bpmn_conf CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
```

---

### Q8: Oracle/达梦 表名大小写问题

**现象:** 启动报错 "Table T_BPMN_CONF not found" 或 "table or view does not exist"。

**原因:** Oracle 默认表名大写存储,PostgreSQL 默认小写,与 MySQL 不同。

**解决:**

1. **Oracle / 达梦 Oracle 模式**:所有表名、字段名使用大写,或建表时加引号强制小写

```sql
-- 推荐:大写
CREATE TABLE T_BPMN_CONF (...);

-- 或强制小写(需在查询时也加引号)
CREATE TABLE "t_bpmn_conf" (...);
```

2. **PostgreSQL**:全部使用小写(默认)

3. **MyBatis Mapper XML** 中的 SQL 注意大小写匹配

---

### Q9: 切换数据库后 MyBatis-Plus 分页失败

**现象:** 切换到 Oracle/达梦后,分页查询返回全部数据或报错。

**原因:** MyBatis-Plus 未识别到新数据库类型,使用了错误的方言。

**解决:**

1. 确认 JDBC URL 正确,包含 `compatibleMode` 等参数:

```properties
# 达梦 Oracle 模式
spring.datasource.url=jdbc:dm://host:5236?compatibleMode=oracle&schema=ANTFLOW
```

2. 检查 [DefaultDataBaseTypeDetector](file:///d:/projects/jimuoffice/antflow-engine/src/main/java/org/openoa/engine/conf/engineconfig/DefaultDataBaseTypeDetector.java) 是否能识别数据库类型

3. 必要时手动配置 MyBatis-Plus:

```java
@Configuration
public class MybatisPlusConfig {
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        // 显式指定 DbType
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.MYSQL));
        return interceptor;
    }
}
```

---

## 流程引擎问题

### Q10: 流程提交后无任务生成

**现象:** 用户提交流程,`bpm_business_process` 表有记录,但 `act_ru_task` 表无任务。

**原因:** 流程定义未发布到 Activiti 引擎,或 BPMN XML 格式错误。

**排查:**

```sql
-- 1. 检查流程定义是否部署
SELECT * FROM act_re_procdef WHERE key_ = '你的流程编码';

-- 2. 检查流程实例是否创建
SELECT * FROM act_ru_execution WHERE proc_inst_id_ = '流程实例ID';

-- 3. 检查业务流程记录
SELECT process_state FROM bpm_business_process WHERE process_number = '流程编号';
-- process_state=2(运行中)正常,0(未启动)说明启动失败

-- 4. 检查错误日志
grep "ERROR" ~/log/antflow/logs/error.log | tail -50
```

**解决:**

1. 在流程设计器中重新发布流程
2. 检查 `t_bpmn_conf_form_code` 表的 `form_view_json` 是否完整
3. 联系开发排查 `BpmnConfBizServiceImpl.edit` 方法日志

---

### Q11: 审批通过后流程未流转

**现象:** 用户点击审批通过,前端提示成功,但流程未流转到下一节点。

**原因:**

- `ProcessOperationAdaptor` 实现类未正确调用 `processComplete`
- Activiti 引擎内部异常被吞掉
- 节点跳转条件不满足

**排查:**

```bash
# 1. 查看审批操作日志
grep "buttonsOperation" ~/log/antflow/logs/sql.log | tail -20

# 2. 查看 Activiti 内部日志
# 临时开启 Activiti debug 日志
# application.properties
logging.level.org.activiti=debug
```

```sql
-- 3. 检查当前任务状态
SELECT * FROM act_ru_task WHERE proc_inst_id_ = '流程实例ID';

-- 4. 检查历史任务
SELECT * FROM act_hi_taskinst WHERE proc_inst_id_ = '流程实例ID' ORDER BY start_time_ DESC;

-- 5. 检查节点配置
SELECT * FROM t_bpmn_node WHERE conf_id = (SELECT id FROM t_bpmn_conf WHERE bpmn_code = '流程编码');
```

**解决:**

参考 [流程流转控制](/dev-guide/flow-control) 章节,确认 `ProcessNodeSubmitBizService.processComplete` 是否被正确调用。

---

### Q12: 条件节点不生效

**现象:** 配置了条件节点,但流程始终走默认分支或全部走第一个分支。

**原因:**

- 条件配置 JSON 格式错误
- `ConditionJudge` 实现类未正确注册
- 条件字段值类型不匹配

**排查:**

```sql
-- 1. 检查条件配置
SELECT auto_node_conf FROM t_bpmn_node WHERE node_id = '条件节点ID';
-- 确认 JSON 完整,包含 conditions / groups / relations

-- 2. 检查条件字段元数据
SELECT * FROM t_bpmn_conf_lf_formdata_field 
WHERE formdata_id = (SELECT id FROM t_bpmn_conf_lf_formdata WHERE bpmn_conf_id = ...);
-- 确认 is_condition=1 的字段正确

-- 3. 检查运行期条件字段值
SELECT * FROM t_lf_main_field WHERE main_id = ... AND field_id = '条件字段ID';
```

**解决:**

1. 在流程设计器中重新配置条件
2. 检查自定义 `ConditionJudge` 实现类是否加 `@Service` 注解
3. 参考 [扩展条件规则](/dev-guide/extend-condition)

---

### Q13: 抄送节点任务一直挂着不消失

**现象:** 抄送节点生成的任务在 `act_ru_task` 表中长期存在,占满列表。

**原因:** 抄送节点未正确使用 `CC_NODE` 虚拟 assignee(-3),或自动完成逻辑未触发。

**解决:**

1. 检查 `t_bpmn_node` 中抄送节点的 `node_type` 是否为 6(NODE_TYPE_COPY)或 8(NODE_TYPE_COPY_V2)
2. 检查 `act_ru_task.assignee_` 是否为 `-3`(CC_NODE 虚拟 assignee)
3. 检查 `ProcessNodeSubmitBizService.completeCopyTask` 是否被调用
4. 临时修复 SQL(谨慎):

```sql
-- 查找挂起的抄送任务
SELECT * FROM act_ru_task WHERE assignee_ = '-3' AND create_time_ < DATE_SUB(NOW(), INTERVAL 1 HOUR);

-- 手动完成(谨慎,先备份)
-- DELETE FROM act_ru_task WHERE assignee_ = '-3' AND id_ IN ('xxx');
-- 注意:还需清理 act_ru_variable / act_ru_identitylink 等关联表
```

---

### Q14: 加签/减签操作失败

**现象:** 管理员对运行中流程执行加签,提示"操作失败"。

**原因:**

- 流程已结束或被撤销
- 当前节点不是会签/并行节点
- Activiti 内部跳转命令异常

**排查:**

```sql
-- 1. 检查流程状态
SELECT process_state FROM bpm_business_process WHERE process_number = '流程编号';
-- 2=运行中,3=已结束,4=已撤销,5=已退回

-- 2. 检查当前节点类型
SELECT node_type FROM t_bpmn_node n
JOIN bpm_business_process p ON p.process_node_id = n.node_id
WHERE p.process_number = '流程编号';
-- 仅 node_type=7(并行网关)或会签节点支持加签

-- 3. 查看错误日志
grep "addSign\|ProcessNodeJumpCmd" ~/log/antflow/logs/error.log | tail -20
```

**解决:**

参考 [流程流转控制](/dev-guide/flow-control) 中"加签/减签"章节,确认操作前置条件。

---

## 前端问题

### Q15: 前端启动报错 "Cannot find module 'vform'"

**现象:**

```
Failed to resolve import "vform" from "src/views/system/lfForm/design.vue".
```

**原因:** VForm3 是 UMD 本地包,不是 npm 安装。

**解决:**

检查 [antflow-vue/src/main.js](file:///d:/projects/jimuoffice/antflow-vue/src/main.js):

```javascript
// 必须引入本地 UMD 包
import VForm3 from "@/./lib/vform/designer.umd.js";
import "./lib/vform/designer.style.css";
app.use(VForm3);
```

确认文件存在:

```bash
ls antflow-vue/src/lib/vform/
# designer.umd.js
# designer.style.css
```

---

### Q16: 前端构建后页面空白

**现象:** `npm run build:prod` 后,访问页面空白,控制台报 404。

**原因:**

- Nginx 静态资源路径配置错误
- `vite.config.ts` 中 `base` 配置错误
- 前端路由模式与 Nginx 配置不匹配

**解决:**

1. 检查 `index.html` 中的资源路径,如果是相对路径(`/assets/xxx.js`),Nginx 配置 root 即可

2. 检查 [vite.config.ts](file:///d:/projects/jimuoffice/antflow-vue/vite.config.ts) 的 `base` 配置:

```javascript
export default defineConfig({
  base: '/',  // 默认根路径,部署到子路径需修改如 '/antflow/'
})
```

3. Nginx 配置 SPA 回退:

```nginx
location / {
    root /var/www/antflow;
    try_files $uri $uri/ /index.html;  # SPA 路由回退
}
```

---

### Q17: 前端跨域问题

**现象:** 浏览器控制台报 CORS 错误:

```
Access to XMLHttpRequest at 'http://backend:7001/api/...' from origin 'http://frontend' 
has been blocked by CORS policy
```

**解决:**

**方案 1:Nginx 同源代理(推荐生产)**

```nginx
location /api/ {
    proxy_pass http://127.0.0.1:7001/;
}
```

前端 API 路径改为相对路径:

```properties
# .env.production
VITE_APP_BASE_API=/api
```

**方案 2:后端配置 CORS(开发环境)**

```java
@Configuration
public class CorsConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("http://localhost:8080")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true);
    }
}
```

---

## 集成问题

### Q18: 自定义用户服务不生效

**现象:** 实现了 `AfUserService` 接口并加 `@Service`,但 AntFlow 仍使用默认 `UserServiceImpl`。

**原因:** 默认实现未通过 `@Primary` 标注优先级,或包扫描未覆盖到自定义实现。

**解决:**

1. 在自定义实现上加 `@Primary`:

```java
@Service
@Primary  // 关键:覆盖默认实现
public class YourUserService implements AfUserService {
    // ...
}
```

2. 确认包扫描覆盖:

```properties
# application.properties
antflow.common.scan-packages=org.openoa,com.yourcompany
```

3. 检查 AntFlow Starter 的 `@ComponentScan` 配置

参考:[集成现有系统](/dev-guide/integrate-existing) 章节。

---

### Q19: 自定义审批人规则不生效

**现象:** 实现了 `BpmnPersonnelProviderService` 接口,但流程设计器中看不到新的审批人类型。

**原因:** 枚举未扩展,或实现类未正确注册。

**解决:**

1. 扩展枚举:

```java
// NodePropertyEnum
NODE_PROPERTY_POSITION(19, "指定岗位", 1, BpmnNodeParamTypeEnum.BPMN_NODE_PARAM_MULTIPLAYER);

// PersonnelEnum
POSITION_PERSONNEL(NODE_PROPERTY_POSITION, "指定岗位");
```

2. 实现类加 `@Component` 并确保 bean 名唯一:

```java
@Component("positionPersonnelProvider")
public class PositionPersonnelProvider implements BpmnPersonnelProviderService {
    // ...
}
```

参考:[扩展审批人来源](/dev-guide/extend-approver) 章节。

---

### Q20: 消息通知发送失败

**现象:** 流程审批后,审批人未收到邮件/短信通知。

**排查:**

```bash
# 1. 查看消息发送日志
grep "sendMessageBatchByType\|MessageSendAdaptor" ~/log/antflow/logs/error.log | tail -20

# 2. 检查 SMTP 配置
grep "mail" /opt/antflow/application-pro.properties
```

```sql
-- 3. 检查消息模板
SELECT * FROM t_bpm_msg_template WHERE msg_type = 1;  -- 1=待办通知

-- 4. 检查消息发送记录
SELECT * FROM t_bpm_process_forward WHERE process_number = '流程编号' ORDER BY create_time DESC;
```

**解决:**

1. **邮件问题**:
   - 测试 SMTP 连通性:`telnet smtp.163.com 465`
   - 检查邮箱密码是否使用授权码(163/QQ 邮箱需使用授权码而非登录密码)
   - 检查 SMTP 端口是否被防火墙拦截

2. **自定义通知渠道**:参考 [扩展通知渠道](/dev-guide/extend-notice) 章节

---

## 性能问题

### Q21: 列表查询缓慢

**现象:** 流程列表、待办列表查询时间超过 3 秒。

**排查:**

```bash
# 1. 查看慢 SQL
tail -100 ~/log/antflow/logs/slowsql.log

# 2. EXPLAIN 慢 SQL
mysql> EXPLAIN SELECT * FROM t_bpmn_conf WHERE ...;
```

**优化建议:**

1. **添加索引**:

```sql
-- 流程编码索引(应已存在)
CREATE INDEX idx_bpmn_conf_bpmn_code ON t_bpmn_conf(bpmn_code);

-- 创建时间索引(列表查询常用)
CREATE INDEX idx_bpmn_conf_create_time ON t_bpmn_conf(create_time);

-- 待办任务索引
CREATE INDEX idx_act_ru_task_assignee ON act_ru_task(assignee_);
CREATE INDEX idx_act_ru_task_create_time ON act_ru_task(create_time_);
```

2. **分页优化**:避免深分页(`OFFSET 100000`),改用游标查询

3. **参考** [性能优化](/ops/performance) 章节

---

### Q22: JVM 频繁 Full GC

**现象:** 应用响应变慢,jstat 显示频繁 Full GC。

**排查:**

```bash
# 1. 查看 JVM 状态
jps -lvm
jstat -gcutil <PID> 1000 10

# 2. 查看堆内存
jmap -heap <PID>

# 3. dump 堆内存(谨慎,会暂停应用)
jmap -dump:format=b,file=/tmp/heap.hprof <PID>
```

**优化:**

1. **调整 JVM 参数**:

```bash
-Xms2g -Xmx4g  # 增大堆
-XX:+UseG1GC    # 使用 G1
-XX:MaxGCPauseMillis=200
-XX:+HeapDumpOnOutOfMemoryError
-XX:HeapDumpPath=/var/log/antflow/
```

2. **排查内存泄漏**:用 MAT / VisualVM 分析 heap dump

3. **常见内存泄漏点**:
   - ThreadLocal 未清理(`ThreadLocalContainer`)
   - Activiti 命令上下文未关闭
   - 自定义缓存无淘汰策略

---

## 日志排查速查表

| 问题类型 | 关键日志文件 | 关键 grep 关键字 |
|---|---|---|
| 启动失败 | `stdout.log` / `stderr.log` | `ERROR` / `Caused by` / `Failed to` |
| 数据库异常 | `error.log` / `sql.log` | `SQLException` / `Table` / `Connection` |
| 流程引擎异常 | `error.log` | `Activiti` / `ProcessNodeJumpCmd` / `task` |
| 慢查询 | `slowsql.log` | (整个文件) |
| 业务异常 | `error.log` | `BpmnConf` / `buttonsOperation` / `ProcessApproval` |
| 前端请求异常 | `sql.log` | (查对应 SQL) / Nginx `access.log` |

```bash
# 综合 grep 示例
grep -E "ERROR|Exception" ~/log/antflow/logs/error.log | tail -50
grep "buttonsOperation" ~/log/antflow/logs/sql.log | tail -20
grep -B 2 -A 10 "Caused by:" ~/log/antflow/logs/error.log
```

## 章节导航

- [生产部署](/ops/deploy) — 部署流程
- [多数据库支持](/ops/db-multi) — 数据库切换
- [性能优化](/ops/performance) — 调优指南
- [FAQ](/reference/faq) — 常见问答
- [术语表](/reference/glossary) — 名词解释
