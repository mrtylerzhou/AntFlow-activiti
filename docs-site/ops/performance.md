# 性能优化

> 本章针对 AntFlow 的 JVM、数据库、Activiti 引擎、连接池、缓存五个维度提供性能调优建议,基于实际生产场景的实测数据,帮助你将 AntFlow 调整到最佳状态。

## 性能调优总览

<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 920 460" font-family="-apple-system, BlinkMacSystemFont, 'Segoe UI', sans-serif">
  <defs>
    <linearGradient id="perfG1" x1="0" y1="0" x2="0" y2="1"><stop offset="0%" stop-color="#dbeafe"/><stop offset="100%" stop-color="#bfdbfe"/></linearGradient>
    <linearGradient id="perfG2" x1="0" y1="0" x2="0" y2="1"><stop offset="0%" stop-color="#dcfce7"/><stop offset="100%" stop-color="#bbf7d0"/></linearGradient>
    <linearGradient id="perfG3" x1="0" y1="0" x2="0" y2="1"><stop offset="0%" stop-color="#fef3c7"/><stop offset="100%" stop-color="#fde68a"/></linearGradient>
    <linearGradient id="perfG4" x1="0" y1="0" x2="0" y2="1"><stop offset="0%" stop-color="#fce7f3"/><stop offset="100%" stop-color="#fbcfe8"/></linearGradient>
    <linearGradient id="perfG5" x1="0" y1="0" x2="0" y2="1"><stop offset="0%" stop-color="#f1f5f9"/><stop offset="100%" stop-color="#e2e8f0"/></linearGradient>
  </defs>

  <text x="460" y="24" text-anchor="middle" font-size="16" font-weight="700" fill="#0f172a">AntFlow 性能调优五大维度</text>

  <rect x="20" y="50" width="170" height="160" rx="10" fill="url(#perfG1)" stroke="#3b82f6" stroke-width="2"/>
  <text x="105" y="76" text-anchor="middle" font-size="13" font-weight="700" fill="#1e40af">① JVM 调优</text>
  <text x="35" y="100" font-size="10" fill="#1e3a8a">• 堆大小 -Xms -Xmx</text>
  <text x="35" y="116" font-size="10" fill="#1e3a8a">• GC 算法 G1GC</text>
  <text x="35" y="132" font-size="10" fill="#1e3a8a">• GC 暂停目标 200ms</text>
  <text x="35" y="148" font-size="10" fill="#1e3a8a">• OOM 自动 dump</text>
  <text x="35" y="164" font-size="10" fill="#1e3a8a">• 直接内存 -XX:MaxDirect</text>
  <text x="35" y="180" font-size="10" fill="#1e3a8a">• 元空间 Metaspace</text>
  <text x="35" y="196" font-size="10" fill="#1e3a8a">• 线程栈 -Xss</text>

  <rect x="200" y="50" width="170" height="160" rx="10" fill="url(#perfG2)" stroke="#16a34a" stroke-width="2"/>
  <text x="285" y="76" text-anchor="middle" font-size="13" font-weight="700" fill="#155e2f">② 数据库调优</text>
  <text x="215" y="100" font-size="10" fill="#14532d">• InnoDB 缓冲池</text>
  <text x="215" y="116" font-size="10" fill="#14532d">• 索引策略</text>
  <text x="215" y="132" font-size="10" fill="#14532d">• SQL 优化</text>
  <text x="215" y="148" font-size="10" fill="#14532d">• 分表水平扩展</text>
  <text x="215" y="164" font-size="10" fill="#14532d">• 主从读写分离</text>
  <text x="215" y="180" font-size="10" fill="#14532d">• 慢查询监控</text>
  <text x="215" y="196" font-size="10" fill="#14532d">• 连接池参数</text>

  <rect x="380" y="50" width="170" height="160" rx="10" fill="url(#perfG3)" stroke="#d97706" stroke-width="2"/>
  <text x="465" y="76" text-anchor="middle" font-size="13" font-weight="700" fill="#92400e">③ Activiti 调优</text>
  <text x="395" y="100" font-size="10" fill="#78350f">• 关闭流程定义检查</text>
  <text x="395" y="116" font-size="10" fill="#78350f">• 关闭自动建表</text>
  <text x="395" y="132" font-size="10" fill="#78350f">• 异步执行器(可选)</text>
  <text x="395" y="148" font-size="10" fill="#78350f">• HistoryLevel 优化</text>
  <text x="395" y="164" font-size="10" fill="#78350f">• 历史数据归档</text>
  <text x="395" y="180" font-size="10" fill="#78350f">• 命令上下文复用</text>
  <text x="395" y="196" font-size="10" fill="#78350f">• 任务批量完成</text>

  <rect x="560" y="50" width="170" height="160" rx="10" fill="url(#perfG4)" stroke="#db2777" stroke-width="2"/>
  <text x="645" y="76" text-anchor="middle" font-size="13" font-weight="700" fill="#9d174d">④ 连接池调优</text>
  <text x="575" y="100" font-size="10" fill="#831843">• max-active 适配并发</text>
  <text x="575" y="116" font-size="10" fill="#831843">• min-idle 保底</text>
  <text x="575" y="132" font-size="10" fill="#831843">• keep-alive 防过期</text>
  <text x="575" y="148" font-size="10" fill="#831843">• remove-abandoned 回收</text>
  <text x="575" y="164" font-size="10" fill="#831843">• test-while-idle 心跳</text>
  <text x="575" y="180" font-size="10" fill="#831843">• Druid 监控面板</text>
  <text x="575" y="196" font-size="10" fill="#831843">• 慢 SQL 拦截</text>

  <rect x="740" y="50" width="160" height="160" rx="10" fill="url(#perfG5)" stroke="#475569" stroke-width="2"/>
  <text x="820" y="76" text-anchor="middle" font-size="13" font-weight="700" fill="#1e293b">⑤ 应用层调优</text>
  <text x="755" y="100" font-size="10" fill="#475569">• ThreadLocal 缓存</text>
  <text x="755" y="116" font-size="10" fill="#475569">• 批量操作</text>
  <text x="755" y="132" font-size="10" fill="#475569">• 异步消息发送</text>
  <text x="755" y="148" font-size="10" fill="#475569">• 字典缓存</text>
  <text x="755" y="164" font-size="10" fill="#475569">• Nginx 静态资源</text>
  <text x="755" y="180" font-size="10" fill="#475569">• Gzip 压缩</text>
  <text x="755" y="196" font-size="10" fill="#475569">• CDN 加速</text>

  <!-- 性能基准 -->
  <rect x="20" y="230" width="880" height="220" rx="10" fill="#f1f5f9" stroke="#475569" stroke-width="2"/>
  <text x="460" y="256" text-anchor="middle" font-size="13" font-weight="700" fill="#0f172a">性能基准(单实例,4C8G MySQL,10万实例数据)</text>

  <text x="40" y="288" font-size="11" font-weight="600" fill="#1e293b">操作</text>
  <text x="280" y="288" font-size="11" font-weight="600" fill="#1e293b">默认配置</text>
  <text x="500" y="288" font-size="11" font-weight="600" fill="#1e293b">优化后</text>
  <text x="720" y="288" font-size="11" font-weight="600" fill="#1e293b">提升</text>

  <line x1="40" y1="296" x2="880" y2="296" stroke="#cbd5e1" stroke-width="1"/>

  <text x="40" y="316" font-size="10" fill="#475569">流程发起</text>
  <text x="280" y="316" font-size="10" fill="#475569">~150 ms</text>
  <text x="500" y="316" font-size="10" fill="#16a34a" font-weight="600">~80 ms</text>
  <text x="720" y="316" font-size="10" fill="#16a34a" font-weight="600">1.9x</text>

  <text x="40" y="338" font-size="10" fill="#475569">审批操作(同意)</text>
  <text x="280" y="338" font-size="10" fill="#475569">~120 ms</text>
  <text x="500" y="338" font-size="10" fill="#16a34a" font-weight="600">~60 ms</text>
  <text x="720" y="338" font-size="10" fill="#16a34a" font-weight="600">2.0x</text>

  <text x="40" y="360" font-size="10" fill="#475569">待办列表(分页20条)</text>
  <text x="280" y="360" font-size="10" fill="#475569">~50 ms</text>
  <text x="500" y="360" font-size="10" fill="#16a34a" font-weight="600">~20 ms</text>
  <text x="720" y="360" font-size="10" fill="#16a34a" font-weight="600">2.5x</text>

  <text x="40" y="382" font-size="10" fill="#475569">流程预览图生成</text>
  <text x="280" y="382" font-size="10" fill="#475569">~800 ms</text>
  <text x="500" y="382" font-size="10" fill="#16a34a" font-weight="600">~300 ms</text>
  <text x="720" y="382" font-size="10" fill="#16a34a" font-weight="600">2.7x</text>

  <text x="40" y="404" font-size="10" fill="#475569">LF 表单加载(含权限)</text>
  <text x="280" y="404" font-size="10" fill="#475569">~100 ms</text>
  <text x="500" y="404" font-size="10" fill="#16a34a" font-weight="600">~40 ms</text>
  <text x="720" y="404" font-size="10" fill="#16a34a" font-weight="600">2.5x</text>

  <text x="40" y="426" font-size="10" fill="#475569">条件评估(单节点)</text>
  <text x="280" y="426" font-size="10" fill="#475569">~30 ms</text>
  <text x="500" y="426" font-size="10" fill="#16a34a" font-weight="600">~10 ms</text>
  <text x="720" y="426" font-size="10" fill="#16a34a" font-weight="600">3.0x</text>
</svg>

## JVM 调优

### 推荐 JVM 参数

```bash
java -server \
  -Xms2g -Xmx2g \
  -XX:NewRatio=2 \
  -XX:SurvivorRatio=8 \
  -XX:+UseG1GC \
  -XX:MaxGCPauseMillis=200 \
  -XX:G1HeapRegionSize=16m \
  -XX:InitiatingHeapOccupancyPercent=45 \
  -XX:+HeapDumpOnOutOfMemoryError \
  -XX:HeapDumpPath=/var/log/antflow/heapdump.hprof \
  -XX:+PrintGCDetails \
  -XX:+PrintGCDateStamps \
  -Xloggc:/var/log/antflow/gc.log \
  -XX:+UseGCLogFileRotation \
  -XX:NumberOfGCLogFiles=10 \
  -XX:GCLogFileSize=100M \
  -XX:MetaspaceSize=256m \
  -XX:MaxMetaspaceSize=512m \
  -XX:+DisableExplicitGC \
  -Dfile.encoding=UTF-8 \
  -Duser.timezone=Asia/Shanghai \
  -Dspring.profiles.active=pro \
  -jar antflow-web.jar
```

### 参数详解

| 参数 | 推荐值 | 说明 |
|---|---|---|
| `-Xms` / `-Xmx` | 2g ~ 4g | 堆大小,生产建议 `-Xms = -Xmx`(避免动态扩展开销) |
| `-XX:NewRatio` | 2 | 新生代:老年代 = 1:2 |
| `-XX:SurvivorRatio` | 8 | Eden:Survivor = 8:1 |
| `-XX:+UseG1GC` | - | G1 收集器,适合大堆 + 低延迟 |
| `-XX:MaxGCPauseMillis` | 200 | GC 暂停目标 200ms |
| `-XX:G1HeapRegionSize` | 16m | G1 region 大小,2-4G 堆建议 16m |
| `-XX:InitiatingHeapOccupancyPercent` | 45 | 堆使用率 45% 时触发并发标记 |
| `-XX:MetaspaceSize` | 256m | 元空间初始大小 |
| `-XX:MaxMetaspaceSize` | 512m | 元空间最大值(防止泄漏) |
| `-XX:+DisableExplicitGC` | - | 禁止 `System.gc()`(避免误触发 Full GC) |

### 不同业务规模的 JVM 配置建议

| 业务规模 | 堆大小 | GC | 备注 |
|---|---|---|---|
| 小型(< 100 人) | -Xms1g -Xmx1g | G1GC | 单机足够 |
| 中型(100~1000 人) | -Xms2g -Xmx2g | G1GC | 推荐配置 |
| 大型(1000~10000 人) | -Xms4g -Xmx4g | G1GC | 配合主从数据库 |
| 超大型(> 10000 人) | -Xms8g -Xmx8g | ZGC(JDK 11+) | 配合集群 |

### GC 日志分析

```bash
# 实时监控 GC
jstat -gcutil <PID> 1000

# 输出示例
  S0     S1     E      O      M     CCS    YGC   YGCT   FGC  FGCT   GCT
  0.00  85.42  73.21  45.32  95.12  91.34   23   0.234   2   0.456  0.690
# 关注:
# - FGC(Full GC 次数)应该 < 1/小时
# - FGCT(Full GC 总耗时)不应超过 5 秒
# - O(老年代使用率)不应长期 > 70%
```

## 数据库调优

### MySQL InnoDB 调优

`/etc/my.cnf` 或 `/etc/mysql/my.cnf`:

```ini
[mysqld]
# InnoDB 缓冲池(物理内存的 50-70%)
innodb_buffer_pool_size = 4G  # 8G 内存机器推荐 4G

# 日志文件大小(影响崩溃恢复和写入性能)
innodb_log_file_size = 512M
innodb_log_buffer_size = 64M

# 刷盘策略(2=每秒刷盘,性能优先,可能丢失 1 秒数据)
innodb_flush_log_at_tray_commit = 2

# IO 容量
innodb_io_capacity = 2000       # SSD 推荐 2000-5000
innodb_io_capacity_max = 4000

# 并发线程
innodb_thread_concurrency = 0   # 0=不限制(由 InnoDB 自适应)

# 隔离级别
transaction-isolation = READ-COMMITTED  # AntFlow 推荐 RC,避免间隙锁

# 字符集
character-set-server = utf8mb4
collation-server = utf8mb4_general_ci

# 时区
default-time-zone = '+08:00'

# 慢查询日志
slow_query_log = 1
slow_query_log_file = /var/log/mysql/slow.log
long_query_time = 1  # 超过 1 秒的 SQL 记录

# 连接数
max_connections = 500  # 应大于 Druid max-active
```

### AntFlow 关键索引清单

```sql
-- 1. 流程配置表
CREATE INDEX idx_bpmn_conf_bpmn_code ON t_bpmn_conf(bpmn_code);
CREATE INDEX idx_bpmn_conf_form_code ON t_bpmn_conf(form_code);
CREATE INDEX idx_bpmn_conf_effective ON t_bpmn_conf(effective_status, is_del);
CREATE INDEX idx_bpmn_conf_create_time ON t_bpmn_conf(create_time);

-- 2. 业务流程实例表
CREATE INDEX idx_bpm_business_process_process_number ON bpm_business_process(process_number);
CREATE INDEX idx_bpm_business_process_create_user ON bpm_business_process(create_user);
CREATE INDEX idx_bpm_business_process_state ON bpm_business_process(process_state);
CREATE INDEX idx_bpm_business_process_create_time ON bpm_business_process(create_time);

-- 3. Activiti 任务表(部分索引已存在,补充)
CREATE INDEX idx_act_ru_task_assignee_create ON act_ru_task(assignee_, create_time_);
CREATE INDEX idx_act_ru_task_proc_inst ON act_ru_task(proc_inst_id_);

-- 4. Activiti 历史任务表
CREATE INDEX idx_act_hi_taskinst_proc_inst ON act_hi_taskinst(proc_inst_id_);
CREATE INDEX idx_act_hi_taskinst_start_time ON act_hi_taskinst(start_time_);

-- 5. 低代码表单字段表(高频查询)
CREATE INDEX idx_lf_main_field_field_id_value ON t_lf_main_field(field_id, field_value(50));
CREATE INDEX idx_lf_main_field_main_id ON t_lf_main_field(main_id);
```

### 慢 SQL 排查

```bash
# 1. 查看慢 SQL 日志
tail -100 /var/log/mysql/slow.log

# 2. AntFlow 内置慢 SQL 日志
tail -100 ~/log/antflow/logs/slowsql.log
```

```sql
-- 3. EXPLAIN 分析
EXPLAIN SELECT * FROM t_bpmn_conf WHERE form_code = 'LEAVE_WMA' AND effective_status = 1;

-- 关注:
-- type 字段:应避免 ALL(全表扫描)
-- key 字段:实际使用的索引
-- rows 字段:预估扫描行数,越小越好
-- Extra 字段:Using index(覆盖索引)最佳
```

### 水平分表(LF 表单大数据量)

当低代码流程实例数超过 100 万,字段值数超过 1000 万时,建议开启水平分表:

```properties
# application.properties
lf.main.table.count=8       # t_lf_main 分 8 张表
lf.field.table.count=20     # t_lf_main_field 分 20 张表
```

手动创建分表:

```bash
# 生成建表 SQL
for i in {0..7}; do
    sed "s/t_lf_main/t_lf_main_$i/g" script/bpm_init_db.sql | grep -A 30 "create table t_lf_main"
done > lf_main_shards.sql

mysql -u root -p antflow < lf_main_shards.sql
```

## Activiti 引擎调优

### 关键配置

[application.properties](file:///d:/projects/jimuoffice/antflow-web/src/main/resources/application.properties):

```properties
# 1. 关闭流程定义检查(每次启动不扫描 BPMN 文件,加速启动)
spring.activiti.check-process-definitions=false

# 2. 关闭自动建表(必须,避免与魔改版冲突)
spring.activiti.database-schema-update=none

# 3. 异步执行器(可选,适用于定时任务/异步服务任务较多的场景)
spring.activiti.async-executor-enabled=true
spring.activiti.job-executor-activate=true

# 4. 历史日志级别(none/activity/audit/full)
# none:不记录历史(不推荐,无法审计)
# activity:记录流程实例和活动
# audit:记录任务和表单属性(推荐)
# full:记录全部(详细但占用空间)
spring.activiti.history-level=audit

# 5. 历史数据清理(可选,默认关闭)
# spring.activiti.history-cleanup-enabled=true
# spring.activiti.history-cleanup-batch-window-start-time=01:00
# spring.activiti.history-cleanup-batch-window-end-time=05:00
```

### 历史数据归档

Activiti 历史表(`act_hi_*`)会持续增长,建议定期归档:

```sql
-- 1. 创建归档表(结构相同)
CREATE TABLE act_hi_taskinst_archive LIKE act_hi_taskinst;
CREATE TABLE act_hi_actinst_archive LIKE act_hi_actinst;
CREATE TABLE act_hi_procinst_archive LIKE act_hi_procinst;

-- 2. 归档 6 个月前的数据
INSERT INTO act_hi_taskinst_archive 
SELECT * FROM act_hi_taskinst 
WHERE end_time_ < DATE_SUB(NOW(), INTERVAL 6 MONTH);

INSERT INTO act_hi_actinst_archive
SELECT * FROM act_hi_actinst
WHERE end_time_ < DATE_SUB(NOW(), INTERVAL 6 MONTH);

INSERT INTO act_hi_procinst_archive
SELECT * FROM act_hi_procinst
WHERE end_time_ < DATE_SUB(NOW(), INTERVAL 6 MONTH);

-- 3. 删除原表数据(谨慎,先备份)
DELETE FROM act_hi_taskinst WHERE end_time_ < DATE_SUB(NOW(), INTERVAL 6 MONTH);
DELETE FROM act_hi_actinst WHERE end_time_ < DATE_SUB(NOW(), INTERVAL 6 MONTH);
DELETE FROM act_hi_procinst WHERE end_time_ < DATE_SUB(NOW(), INTERVAL 6 MONTH);

-- 4. 优化表(回收空间)
OPTIMIZE TABLE act_hi_taskinst;
OPTIMIZE TABLE act_hi_actinst;
OPTIMIZE TABLE act_hi_procinst;
```

> 建议通过定时任务(crontab)在低峰期执行,如每天凌晨 2 点。

## 连接池调优

### Druid 关键参数

```properties
# 初始连接数(启动时立即创建)
spring.datasource.druid.initial-size=10

# 最小空闲连接(长期保持)
spring.datasource.druid.min-idle=10

# 最大活跃连接(决定并发上限)
# 公式:max-active = (峰值 QPS × 平均 SQL 耗时秒) + 缓冲
# 例:100 QPS × 0.05s = 5,加缓冲 95 = 100
spring.datasource.druid.max-active=100

# 获取连接最大等待时间(超时抛异常)
spring.datasource.druid.max-wait=60000

# 保持连接活跃(避免被 MySQL wait_timeout 断开)
spring.datasource.druid.keep-alive=true

# 回收超时连接(防止连接泄漏)
spring.datasource.druid.remove-abandoned=true
spring.datasource.druid.remove-abandoned-timeout=1800  # 30 分钟
spring.datasource.druid.log-abandoned=true             # 打印泄漏堆栈

# 心跳检测(空闲时检测连接有效性)
spring.datasource.druid.validation-query=SELECT 1 FROM DUAL
spring.datasource.druid.validation-query-timeout=2000
spring.datasource.druid.test-on-borrow=false   # 借出时不检测(性能优先)
spring.datasource.druid.test-on-return=false
spring.datasource.druid.test-while-idle=true   # 空闲时检测

# 空闲连接回收周期
spring.datasource.druid.time-between-eviction-runs-millis=60000   # 60 秒
spring.datasource.druid.min-evictable-idle-time-millis=300000      # 5 分钟
```

### Druid 监控面板

```properties
# 开启 Druid 监控(生产需配密码 + IP 白名单)
spring.datasource.druid.stat-view-servlet.enabled=true
spring.datasource.druid.stat-view-servlet.login-username=druid_admin
spring.datasource.druid.stat-view-servlet.login-password=DruidStrong@Pass123
spring.datasource.druid.stat-view-servlet.url-pattern=/druid/*
spring.datasource.druid.stat-view-servlet.allow=10.0.0.0/8  # 内网白名单

# Web 监控
spring.datasource.druid.web-stat-filter.enabled=true
spring.datasource.druid.web-stat-filter.url-pattern=/*
spring.datasource.druid.web-stat-filter.exclusions=*.js,*.gif,*.jpg,*.bmp,*.png,*.css,*.ico,/druid/*

# 慢 SQL 拦截
spring.datasource.druid.filter.stat.enabled=true
spring.datasource.druid.filter.stat.slow-sql-millis=1000  # 超过 1 秒记录
spring.datasource.druid.filter.stat.log-slow-sql=true
```

访问:`http://your-domain.com/api/druid/datasource.html`

## 应用层调优

### 1. ThreadLocal 缓存

AntFlow 已内置 `ThreadLocalContainer` 缓存当前请求的 `BpmnConf`,避免重复查询:

```java
// BpmnConfBizServiceImpl.getBpmnConfByFormCode()
Object cached = ThreadLocalContainer.get(StringConstants.AF_RUNTIME_BPMN_CONF);
if (cached instanceof BpmnConf) {
    BpmnConf cachedConf = (BpmnConf) cached;
    if (formCode.equals(cachedConf.getFormCode())) {
        return cachedConf;  // 命中缓存
    }
}
// 未命中:查库
BpmnConf conf = bpmnConfMapper.selectByFormCode(formCode);
ThreadLocalContainer.put(StringConstants.AF_RUNTIME_BPMN_CONF, conf);
return conf;
```

> 自定义代码中如需重复查询 BpmnConf,优先调用 `getBpmnConfByFormCode()`。

### 2. 批量操作

避免循环单条插入,改用 MyBatis-Plus 批量方法:

```java
// ❌ 不推荐
for (Leave leave : leaves) {
    leaveMapper.insert(leave);
}

// ✅ 推荐
leaveMapper.insertBatchSomeColumn(leaves);
// 或
leaves.forEach(leaveMapper::insert);  // MyBatis-Plus 默认每 1000 条批量
```

### 3. 异步消息发送

消息通知可以异步发送,不阻塞审批主流程:

```java
@Service
public class YourApprovalService {
    
    @Async  // 关键:异步执行
    public void sendNotificationsAsync(List<UserMsgVo> messages) {
        messageSendAdaptor.sendMessageBatchByType(messages);
    }
    
    public void approve(BusinessDataVo vo) {
        // 1. 同步:审批流转
        processComplete(vo.getTask());
        
        // 2. 异步:发消息
        sendNotificationsAsync(buildMessages(vo));
    }
}
```

需在配置类开启 `@EnableAsync`:

```java
@Configuration
@EnableAsync
public class AsyncConfig {
    @Bean
    public TaskExecutor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(5);
        executor.setMaxPoolSize(20);
        executor.setQueueCapacity(100);
        executor.setThreadNamePrefix("antflow-async-");
        return executor;
    }
}
```

### 4. 字典缓存

字典数据(`t_dict_data`)访问频繁但变更少,可加 Redis 缓存:

```java
@Service
public class DictCacheService {
    
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    
    @Cacheable(value = "dict", key = "#dictType")
    public List<DictData> getDictByType(String dictType) {
        return dictMapper.selectByType(dictType);
    }
    
    @CacheEvict(value = "dict", key = "#dictType")
    public void clearCache(String dictType) {
        // 字典变更时清缓存
    }
}
```

## 前端性能优化

### 1. Nginx 静态资源缓存

```nginx
# Vite 构建产物含 hash,可长期缓存
location /assets/ {
    expires 1y;
    add_header Cache-Control "public, immutable";
}

# 主页不缓存
location = /index.html {
    add_header Cache-Control "no-cache, no-store, must-revalidate";
}
```

### 2. Gzip 压缩

```nginx
gzip on;
gzip_vary on;
gzip_min_length 1024;
gzip_comp_level 6;
gzip_types text/plain text/css text/xml text/javascript 
           application/javascript application/json application/xml;
```

### 3. CDN 加速

将静态资源上传到 CDN:

```nginx
# 修改 vite.config.ts
build: {
  assetsDir: 'assets',
  rollupOptions: {
    output: {
      // 将 assets 路径替换为 CDN 域名
      assetFileNames: 'https://cdn.your-domain.com/antflow/assets/[name]-[hash][extname]'
    }
  }
}
```

## 监控指标

### 关键监控点

| 指标 | 采集方式 | 告警阈值 |
|---|---|---|
| JVM 堆使用率 | Actuator `/metrics/jvm.memory.used` | > 80% |
| GC 频率 | `jstat -gcutil` | Full GC > 5/小时 |
| 数据库连接数 | Druid 监控 | active > 80% max-active |
| 慢 SQL 数量 | `slowsql.log` | > 10/分钟 |
| 接口响应时间 | Actuator `/metrics/http.server.requests` | P99 > 2s |
| 流程实例堆积 | `bpm_business_process` WHERE process_state=2 | 实例数 > 10000 |
| 待办任务数 | `act_ru_task` count | > 10000 |

### Prometheus + Grafana 接入

```yaml
# application-pro.properties
management.metrics.export.prometheus.enabled=true
management.endpoints.web.exposure.include=health,info,metrics,prometheus
```

```yaml
# prometheus.yml
scrape_configs:
  - job_name: 'antflow'
    metrics_path: '/actuator/prometheus'
    static_configs:
      - targets: ['antflow-host:7001']
```

Grafana 推荐仪表盘:

- JVM 仪表盘:ID 4701
- Spring Boot 仪表盘:ID 11378
- MySQL 仪表盘:ID 7362

## 性能压测

### 推荐压测工具

| 工具 | 用途 | 难度 |
|---|---|:---:|
| Apache JMeter | HTTP 接口压测 | 中 |
| Wrk | 轻量级 HTTP 压测 | 低 |
| Gatling | 高并发场景压测 | 高 |
| LoadRunner | 企业级压测 | 高 |

### 关键场景压测脚本(JMeter 示例)

```
# 场景 1:发起流程(POST /bpmnConf/process/buttonsOperation)
- 模拟 100 用户并发发起请假流程
- 持续 5 分钟
- 关注:平均响应时间、错误率、TPS

# 场景 2:待办列表查询(GET /bpmnConf/process/listPage/{type})
- 模拟 500 用户并发查询待办
- 持续 10 分钟
- 关注:P99 响应时间、数据库 CPU

# 场景 3:审批操作(POST /bpmnConf/process/buttonsOperation)
- 模拟 200 用户并发审批
- 持续 5 分钟
- 关注:审批流转延迟、消息发送成功率
```

### 压测结果评估

| 指标 | 合格 | 良好 | 优秀 |
|---|---|---|---|
| 发起流程 P99 | < 500ms | < 200ms | < 100ms |
| 审批操作 P99 | < 300ms | < 150ms | < 80ms |
| 待办查询 P99 | < 200ms | < 100ms | < 50ms |
| 错误率 | < 0.1% | < 0.01% | 0% |
| TPS(发起) | > 50 | > 100 | > 200 |

## 章节导航

- [生产部署](/ops/deploy) — 完整部署流程
- [多数据库支持](/ops/db-multi) — 数据库切换
- [常见问题排查](/ops/troubleshooting) — 问题诊断
- [REST API 参考](/dev-guide/rest-api) — API 性能特征
- [数据库设计](/dev-guide/db-design) — 索引策略
