# 多数据库支持

> AntFlow 通过 MyBatis-Plus 的 `DbType` 机制支持 12+ 种数据库,业务表 SQL 跨库兼容。本章列出全部支持的数据库、配置切换方式、国产数据库兼容模式对照表,以及切换注意事项。

## 支持的数据库列表

<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 920 580" font-family="-apple-system, BlinkMacSystemFont, 'Segoe UI', sans-serif">
  <defs>
    <linearGradient id="dbG1" x1="0" y1="0" x2="0" y2="1"><stop offset="0%" stop-color="#dbeafe"/><stop offset="100%" stop-color="#bfdbfe"/></linearGradient>
    <linearGradient id="dbG2" x1="0" y1="0" x2="0" y2="1"><stop offset="0%" stop-color="#dcfce7"/><stop offset="100%" stop-color="#bbf7d0"/></linearGradient>
    <linearGradient id="dbG3" x1="0" y1="0" x2="0" y2="1"><stop offset="0%" stop-color="#fef3c7"/><stop offset="100%" stop-color="#fde68a"/></linearGradient>
    <linearGradient id="dbG4" x1="0" y1="0" x2="0" y2="1"><stop offset="0%" stop-color="#fce7f3"/><stop offset="100%" stop-color="#fbcfe8"/></linearGradient>
  </defs>

  <text x="460" y="24" text-anchor="middle" font-size="16" font-weight="700" fill="#0f172a">AntFlow 支持的数据库矩阵(12+ 种)</text>

  <!-- 开源数据库 -->
  <rect x="20" y="50" width="220" height="200" rx="10" fill="url(#dbG1)" stroke="#3b82f6" stroke-width="2"/>
  <text x="130" y="76" text-anchor="middle" font-size="13" font-weight="700" fill="#1e40af">开源数据库</text>
  <rect x="40" y="90" width="180" height="36" rx="6" fill="#fff" stroke="#3b82f6"/>
  <text x="130" y="113" text-anchor="middle" font-size="11" font-weight="600" fill="#1e3a8a">MySQL 5.7+ / 8.0+</text>
  <rect x="40" y="132" width="180" height="36" rx="6" fill="#fff" stroke="#3b82f6"/>
  <text x="130" y="155" text-anchor="middle" font-size="11" font-weight="600" fill="#1e3a8a">PostgreSQL 12+</text>
  <rect x="40" y="174" width="180" height="36" rx="6" fill="#fff" stroke="#3b82f6"/>
  <text x="130" y="197" text-anchor="middle" font-size="11" font-weight="600" fill="#1e3a8a">MongoDB 4.0+(实验性)</text>
  <text x="130" y="230" text-anchor="middle" font-size="10" fill="#1e3a8a">默认配置</text>
  <text x="130" y="244" text-anchor="middle" font-size="10" fill="#1e3a8a">社区活跃,生态完整</text>

  <!-- 商业数据库 -->
  <rect x="260" y="50" width="220" height="200" rx="10" fill="url(#dbG2)" stroke="#16a34a" stroke-width="2"/>
  <text x="370" y="76" text-anchor="middle" font-size="13" font-weight="700" fill="#155e2f">商业数据库</text>
  <rect x="280" y="90" width="180" height="36" rx="6" fill="#fff" stroke="#16a34a"/>
  <text x="370" y="113" text-anchor="middle" font-size="11" font-weight="600" fill="#14532d">Oracle 11g+</text>
  <rect x="280" y="132" width="180" height="36" rx="6" fill="#fff" stroke="#16a34a"/>
  <text x="370" y="155" text-anchor="middle" font-size="11" font-weight="600" fill="#14532d">SQL Server 2016+</text>
  <rect x="280" y="174" width="180" height="36" rx="6" fill="#fff" stroke="#16a34a"/>
  <text x="370" y="197" text-anchor="middle" font-size="11" font-weight="600" fill="#14532d">DB2(实验性)</text>
  <text x="370" y="230" text-anchor="middle" font-size="10" fill="#14532d">企业级场景</text>
  <text x="370" y="244" text-anchor="middle" font-size="10" fill="#14532d">付费授权</text>

  <!-- 国产数据库 -->
  <rect x="500" y="50" width="220" height="200" rx="10" fill="url(#dbG3)" stroke="#d97706" stroke-width="2"/>
  <text x="610" y="76" text-anchor="middle" font-size="13" font-weight="700" fill="#92400e">国产数据库</text>
  <rect x="520" y="90" width="180" height="26" rx="4" fill="#fff" stroke="#d97706"/>
  <text x="610" y="108" text-anchor="middle" font-size="10" font-weight="600" fill="#78350f">达梦 DM8(4 种兼容模式)</text>
  <rect x="520" y="120" width="180" height="26" rx="4" fill="#fff" stroke="#d97706"/>
  <text x="610" y="138" text-anchor="middle" font-size="10" font-weight="600" fill="#78350f">人大金仓 Kingbase(3 种模式)</text>
  <rect x="520" y="150" width="180" height="26" rx="4" fill="#fff" stroke="#d97706"/>
  <text x="610" y="168" text-anchor="middle" font-size="10" font-weight="600" fill="#78350f">南大通用 GBase</text>
  <rect x="520" y="180" width="180" height="26" rx="4" fill="#fff" stroke="#d97706"/>
  <text x="610" y="198" text-anchor="middle" font-size="10" font-weight="600" fill="#78350f">OceanBase(Oracle 模式)</text>
  <rect x="520" y="210" width="180" height="26" rx="4" fill="#fff" stroke="#d97706"/>
  <text x="610" y="228" text-anchor="middle" font-size="10" font-weight="600" fill="#78350f">高斯 GaussDB / openGauss</text>
  <text x="610" y="246" text-anchor="middle" font-size="9" fill="#78350f">★ 信创场景主流选择</text>

  <!-- 云数据库 -->
  <rect x="740" y="50" width="160" height="200" rx="10" fill="url(#dbG4)" stroke="#db2777" stroke-width="2"/>
  <text x="820" y="76" text-anchor="middle" font-size="13" font-weight="700" fill="#9d174d">云数据库</text>
  <rect x="760" y="90" width="120" height="36" rx="6" fill="#fff" stroke="#db2777"/>
  <text x="820" y="113" text-anchor="middle" font-size="10" font-weight="600" fill="#831843">PolarDB-PG</text>
  <rect x="760" y="132" width="120" height="36" rx="6" fill="#fff" stroke="#db2777"/>
  <text x="820" y="155" text-anchor="middle" font-size="10" font-weight="600" fill="#831843">PolarDB-MySQL</text>
  <rect x="760" y="174" width="120" height="36" rx="6" fill="#fff" stroke="#db2777"/>
  <text x="820" y="197" text-anchor="middle" font-size="10" font-weight="600" fill="#831843">AWS RDS</text>
  <text x="820" y="230" text-anchor="middle" font-size="10" fill="#831843">阿里云/腾讯云</text>
  <text x="820" y="244" text-anchor="middle" font-size="10" fill="#831843">AWS/Azure</text>

  <!-- 适配机制 -->
  <text x="460" y="290" text-anchor="middle" font-size="14" font-weight="700" fill="#0f172a">适配机制</text>

  <rect x="20" y="310" width="880" height="260" rx="10" fill="#f1f5f9" stroke="#475569" stroke-width="2"/>
  <text x="40" y="338" font-size="12" font-weight="700" fill="#1e293b">基于 MyBatis-Plus DbType + 多数据库方言自动侦测</text>

  <rect x="40" y="358" width="270" height="76" rx="6" fill="#dbeafe" stroke="#3b82f6"/>
  <text x="60" y="380" font-size="11" font-weight="600" fill="#1e3a8a">① 启动时侦测数据库类型</text>
  <text x="60" y="398" font-size="10" fill="#1e3a8a">DefaultDataBaseTypeDetector</text>
  <text x="60" y="414" font-size="10" fill="#1e3a8a">从 DataSource 元数据取 dbType</text>
  <text x="60" y="430" font-size="10" fill="#1e3a8a">注册到 MyBatis-Plus 配置</text>

  <rect x="325" y="358" width="270" height="76" rx="6" fill="#dcfce7" stroke="#16a34a"/>
  <text x="345" y="380" font-size="11" font-weight="600" fill="#14532d">② SQL 自动方言适配</text>
  <text x="345" y="398" font-size="10" fill="#14532d">MyBatis-Plus 内置 12+ 种方言</text>
  <text x="345" y="414" font-size="10" fill="#14532d">分页/批量/主键策略自动切换</text>
  <text x="345" y="430" font-size="10" fill="#14532d">业务 SQL 跨库通用</text>

  <rect x="610" y="358" width="270" height="76" rx="6" fill="#fef3c7" stroke="#d97706"/>
  <text x="630" y="380" font-size="11" font-weight="600" fill="#78350f">③ 多租户/多数据源(可选)</text>
  <text x="630" y="398" font-size="10" fill="#78350f">MBPDynamicDataSourceDetector</text>
  <text x="630" y="414" font-size="10" fill="#78350f">支持每个租户独立数据源</text>
  <text x="630" y="430" font-size="10" fill="#78350f">支持跨数据库的租户隔离</text>

  <text x="40" y="460" font-size="12" font-weight="700" fill="#1e293b">关键代码位置</text>
  <text x="40" y="480" font-size="10" fill="#475569">• DefaultDataBaseTypeDetector.java — 数据库类型侦测</text>
  <text x="40" y="498" font-size="10" fill="#475569">• MBPDynamicDataSourceDetector.java — 多数据源侦测(支持 mybatis-plus 多数据源)</text>
  <text x="40" y="516" font-size="10" fill="#475569">• GenericDruidDataSourceConfig.java — Druid 多租户数据源配置(注释参考)</text>
  <text x="40" y="534" font-size="10" fill="#475569">• ActivitiConfig.java — Activiti 中文字体修复 + Schema 配置</text>
  <text x="40" y="552" font-size="10" fill="#475569">• ActivitiProperties.java — 引擎可调优参数(jobExecutorActivate 等)</text>
</svg>

## 详细支持矩阵

完整文档位于 [doc/多数据库支持/](file:///d:/projects/jimuoffice/doc/多数据库支持/) 目录,共 16 篇:

| 序号 | 数据库 | 兼容模式 | 驱动 | 推荐场景 | 文档 |
|:---:|---|---|---|---|---|
| 1 | MySQL 8.0 | 原生 | `com.mysql.cj.jdbc.Driver` | 默认数据库,互联网场景 | - |
| 2 | PostgreSQL 12+ | 原生 | `org.postgresql.Driver` | 开源首选,功能强 | [文档](file:///d:/projects/jimuoffice/doc/多数据库支持/2.antflow%20postgresql支持.md) |
| 3 | Oracle 11g+ | 原生 | `oracle.jdbc.OracleDriver` | 传统企业,金融行业 | [文档](file:///d:/projects/jimuoffice/doc/多数据库支持/1.antflow%20oracle支持.md) |
| 4 | SQL Server 2016+ | 原生 | `com.microsoft.sqlserver.jdbc.SQLServerDriver` | 微软生态企业 | [文档](file:///d:/projects/jimuoffice/doc/多数据库支持/3.antflow%20sql%20server支持.md) |
| 5 | 达梦 DM8 | Oracle 兼容 | `dm.jdbc.driver.DmDriver` | 信创首选,政府/央企 | [文档](file:///d:/projects/jimuoffice/doc/多数据库支持/4.antflow%20达梦dm8%20oracle支持.md) |
| 6 | 达梦 DM8 | MySQL 兼容 | `dm.jdbc.driver.DmDriver` | 信创轻量级 | [文档](file:///d:/projects/jimuoffice/doc/多数据库支持/5.antflow%20达梦mysql模式支持.md) |
| 7 | 达梦 DM8 | PG 兼容 | `dm.jdbc.driver.DmDriver` | 信创开源迁移 | [文档](file:///d:/projects/jimuoffice/doc/多数据库支持/6.antflow%20达梦postgresql模式支持.md) |
| 8 | 达梦 DM8 | MSSQL 兼容 | `dm.jdbc.driver.DmDriver` | 老系统迁移 | [文档](file:///d:/projects/jimuoffice/doc/多数据库支持/7.antflow%20达梦sql%20server模式支持.md) |
| 9 | 人大金仓 Kingbase | Oracle 兼容 | `com.kingbase8.Driver` | 信创,学术单位 | [文档](file:///d:/projects/jimuoffice/doc/多数据库支持/8.antflow%20电科金仓（原人大金仓）kingbase%20oracle模式支持.md) |
| 10 | 人大金仓 Kingbase | MySQL 兼容 | `com.kingbase8.Driver` | 信创轻量级 | [文档](file:///d:/projects/jimuoffice/doc/多数据库支持/9.antflow电金金仓mysql模式支持.md) |
| 11 | 人大金仓 Kingbase | PG 兼容 | `com.kingbase8.Driver` | 信创开源 | [文档](file:///d:/projects/jimuoffice/doc/多数据库支持/10.antflow%20电金金仓pg模式支持.md) |
| 12 | 南大通用 GBase | 原生 | `com.gbase.jdbc.Driver` | 信创,统计分析 | [文档](file:///d:/projects/jimuoffice/doc/多数据库支持/11.antflow%20南大通用gbase支持.md) |
| 13 | OceanBase | Oracle 兼容 | `com.oceanbase.jdbc.Driver` | 阿里云生态,金融 | [文档](file:///d:/projects/jimuoffice/doc/多数据库支持/12.antflow%20oceanbase%20oracle模式支持.md) |
| 14 | 高斯 GaussDB / openGauss | 原生 | `org.opengauss.Driver` | 华为云生态 | [文档](file:///d:/projects/jimuoffice/doc/多数据库支持/13.antflow%20高斯数据库opengauss支持.md) |
| 15 | PolarDB-PG | PG 兼容 | `org.postgresql.Driver` | 阿里云云原生 | [文档](file:///d:/projects/jimuoffice/doc/多数据库支持/14.antflow支持ploardb%20pg版.md) |
| 16 | PolarDB-MySQL | MySQL 兼容 | `com.mysql.cj.jdbc.Driver` | 阿里云云原生 | [文档](file:///d:/projects/jimuoffice/doc/多数据库支持/15.antflow支持polardb%20mysql版.md) |
| 17 | MongoDB 4.0+ | 实验性 | `com.mongodb.client.MongoClient` | NoSQL 场景(需副本集) | [文档](file:///d:/projects/jimuoffice/doc/多数据库支持/16.antflow%20mongodb支持.md) |

## 配置切换方式

切换数据库只需修改 [application.properties](file:///d:/projects/jimuoffice/antflow-web/src/main/resources/application.properties) 中的数据源配置。

### MySQL(默认)

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/antflow?nullCatalogMeansCurrent=true&useUnicode=true&characterEncoding=utf-8&serverTimezone=Asia/Shanghai
spring.datasource.username=antflow
spring.datasource.password=YourStrong@Pass123
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
```

### PostgreSQL

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/antflow?currentSchema=public
spring.datasource.username=antflow
spring.datasource.password=YourStrong@Pass123
spring.datasource.driver-class-name=org.postgresql.Driver
```

### Oracle

```properties
spring.datasource.url=jdbc:oracle:thin:@localhost:1521:XE
spring.datasource.username=C##ANTFLOW
spring.datasource.password=YourStrong@Pass123
spring.datasource.driver-class-name=oracle.jdbc.OracleDriver
```

### SQL Server

```properties
spring.datasource.url=jdbc:sqlserver://localhost:1433;databaseName=antflow;encrypt=false
spring.datasource.username=sa
spring.datasource.password=YourStrong@Pass123
spring.datasource.driver-class-name=com.microsoft.sqlserver.jdbc.SQLServerDriver
```

### 达梦 DM8(Oracle 兼容模式,最常用)

```properties
spring.datasource.url=jdbc:dm://127.0.0.1:5236?compatibleMode=oracle&schema=ANTFLOW
spring.datasource.username=SYSDBA
spring.datasource.password=YourDM@Pass123
spring.datasource.driver-class-name=dm.jdbc.driver.DmDriver
```

### 人大金仓 Kingbase

```properties
spring.datasource.url=jdbc:kingbase8://localhost:54321/TEST?useUnicode=true&characterEncoding=UTF-8&currentSchema=antflow
spring.datasource.username=system
spring.datasource.password=YourKingbase@Pass123
spring.datasource.driver-class-name=com.kingbase8.Driver
```

### OceanBase(Oracle 模式)

```properties
spring.datasource.url=jdbc:oceanbase://host:1521/ADMIN?useUnicode=true&characterEncoding=utf-8&useOraclePrepareExecute=true&serverTimezone=Asia/Shanghai
spring.datasource.username=ADMIN
spring.datasource.password=YourOceanbase@Pass123
spring.datasource.driver-class-name=com.oceanbase.jdbc.Driver
```

### 高斯 / openGauss

```properties
spring.datasource.url=jdbc:opengauss://localhost:5432/antflow?currentSchema=public
spring.datasource.username=antflow
spring.datasource.password=YourStrong@Pass123
spring.datasource.driver-class-name=org.opengauss.Driver
```

### MongoDB(实验性)

```properties
spring.data.mongodb.uri=mongodb://localhost:27018,localhost:27019,localhost:27020/antflow?replicaSet=rs0
```

> MongoDB 必须使用副本集模式以支持事务。

## 国产数据库兼容模式对照

### 达梦 DM8 兼容模式

达梦支持 7 种 `COMPATIBLE_MODE`,只能在初始化时通过 `dminit PATH=/dm8/data MODE=2` 指定:

| MODE | 兼容数据库 | 适用场景 | AntFlow 支持 |
|:---:|---|---|:---:|
| 0 | SQL-92 标准 | 通用 | ❌ |
| 1 | 无(达梦自身) | 默认 | ❌ |
| 2 | **Oracle** | 最常用企业迁移模式 | ✅ |
| 3 | MSSQL | SQL Server 迁移 | ✅ |
| 4 | MySQL | 轻量级 Web 应用迁移 | ✅ |
| 5 | DM6 | 老系统升级 | ❌ |
| 6 | Teradata | 数据仓库 | ❌ |
| 7 | PostgreSQL | 开源项目迁移 | ✅ |

### 人大金仓 Kingbase 兼容模式

| 模式 | 兼容数据库 | 配置 | AntFlow 支持 |
|---|---|---|:---:|
| Oracle 模式 | Oracle 11g+ | `compatibleMode=oracle` | ✅ |
| MySQL 模式 | MySQL 5.7+ | `compatibleMode=mysql` | ✅ |
| PG 模式 | PostgreSQL 12+ | 默认 | ✅ |

## Maven 依赖配置

不同数据库需要添加对应的 JDBC 驱动依赖。在 [antflow-base/pom.xml](file:///d:/projects/jimuoffice/antflow-base/pom.xml) 或业务的 pom.xml 中添加:

```xml
<!-- MySQL(默认,已内置) -->
<dependency>
    <groupId>mysql</groupId>
    <artifactId>mysql-connector-java</artifactId>
    <scope>runtime</scope>
</dependency>

<!-- PostgreSQL -->
<dependency>
    <groupId>org.postgresql</groupId>
    <artifactId>postgresql</artifactId>
    <version>42.6.0</version>
    <scope>runtime</scope>
</dependency>

<!-- Oracle(需手动下载 ojdbc8.jar,Oracle 商业授权) -->
<dependency>
    <groupId>com.oracle.database.jdbc</groupId>
    <artifactId>ojdbc8</artifactId>
    <version>21.5.0.0</version>
    <scope>runtime</scope>
</dependency>

<!-- SQL Server -->
<dependency>
    <groupId>com.microsoft.sqlserver</groupId>
    <artifactId>mssql-jdbc</artifactId>
    <version>8.4.1.jre8</version>
    <scope>runtime</scope>
</dependency>

<!-- 达梦 DM8(需到达梦官网下载 DmJdbcDriver18.jar) -->
<dependency>
    <groupId>com.dameng</groupId>
    <artifactId>DmJdbcDriver18</artifactId>
    <version>8.1.2.141</version>
    <scope>runtime</scope>
</dependency>

<!-- 人大金仓 Kingbase(需到金仓官网下载 kingbase8-8.6.0.jar) -->
<dependency>
    <groupId>cn.com.kingbase</groupId>
    <artifactId>kingbase8</artifactId>
    <version>8.6.0</version>
    <scope>runtime</scope>
</dependency>

<!-- OceanBase -->
<dependency>
    <groupId>com.oceanbase</groupId>
    <artifactId>oceanbase-client</artifactId>
    <version>3.2.3</version>
    <scope>runtime</scope>
</dependency>

<!-- openGauss -->
<dependency>
    <groupId>org.opengauss</groupId>
    <artifactId>opengauss-jdbc</artifactId>
    <version>5.0.0</version>
    <scope>runtime</scope>
</dependency>

<!-- MongoDB(实验性) -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-mongodb</artifactId>
</dependency>
```

## 数据库初始化脚本

不同数据库的建表脚本需对应版本,目前 AntFlow 主要提供 MySQL 脚本,其他数据库需要按文档指引转换:

| 数据库 | 建表脚本来源 | 说明 |
|---|---|---|
| MySQL | [script/act_init_db.sql](file:///d:/projects/jimuoffice/script/act_init_db.sql) + [bpm_init_db.sql](file:///d:/projects/jimuoffice/script/bpm_init_db.sql) | 默认提供 |
| PostgreSQL | 参考 MySQL 脚本手工转换 | 数据类型:`varchar` → `varchar`,`datetime` → `timestamp`,`tinyint` → `smallint` |
| Oracle | 参考多数据库文档 | 数据类型:`varchar` → `varchar2`,`datetime` → `date`,需大写表名 |
| 达梦 Oracle 模式 | 可直接用 Oracle 脚本 | 推荐方式 |
| 人大金仓 Oracle 模式 | 可直接用 Oracle 脚本 | 推荐方式 |

### 国产数据库 SQL 转换关键点

```sql
-- MySQL 原始
CREATE TABLE t_bpmn_conf (
    id bigint NOT NULL AUTO_INCREMENT,
    create_time datetime DEFAULT CURRENT_TIMESTAMP,
    -- ...
    PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Oracle / 达梦 Oracle 模式
CREATE TABLE T_BPMN_CONF (  -- 表名大写
    ID NUMBER(19) NOT NULL,  -- bigint → NUMBER(19)
    CREATE_TIME TIMESTAMP DEFAULT CURRENT_TIMESTAMP,  -- datetime → TIMESTAMP
    -- ...
    PRIMARY KEY (ID)
);
CREATE SEQUENCE T_BPMN_CONF_SEQ START WITH 1 INCREMENT BY 1;  -- 需手动建序列
-- 或使用 MyBatis-Plus 的 SequenceGenerator

-- PostgreSQL / 人大金仓 PG 模式
CREATE TABLE t_bpmn_conf (
    id BIGSERIAL NOT NULL,  -- bigint AUTO_INCREMENT → BIGSERIAL
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    -- ...
    PRIMARY KEY (id)
);
```

## Activiti 引擎表初始化

**重要**:**不要开启 Activiti 自动建表**,因为 AntFlow 对 Activiti 源码进行了魔改。

各数据库的 Activiti 表初始化方式:

| 数据库 | Activiti 表来源 | 说明 |
|---|---|---|
| MySQL | [script/act_init_db.sql](file:///d:/projects/jimuoffice/script/act_init_db.sql) | 默认 |
| 其他数据库 | 在 `application.properties` 中临时设置 `spring.activiti.database-schema-update=true` 启动一次 | 启动后改回 `none` |

> 临时开启自动建表时,务必观察启动日志确认表创建成功,然后立即关闭。

## 多数据源/多租户

AntFlow 支持多数据源,适用于 SaaS 多租户场景:

<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 920 320" font-family="-apple-system, BlinkMacSystemFont, 'Segoe UI', sans-serif">
  <text x="460" y="24" text-anchor="middle" font-size="14" font-weight="700" fill="#0f172a">多租户数据源架构</text>

  <rect x="20" y="50" width="880" height="60" rx="8" fill="#1e293b"/>
  <text x="460" y="86" text-anchor="middle" font-size="13" font-weight="700" fill="#fff">AntFlow 引擎(单实例)</text>

  <line x1="200" y1="110" x2="200" y2="140" stroke="#475569" stroke-width="2"/>
  <line x1="460" y1="110" x2="460" y2="140" stroke="#475569" stroke-width="2"/>
  <line x1="720" y1="110" x2="720" y2="140" stroke="#475569" stroke-width="2"/>

  <rect x="40" y="140" width="320" height="170" rx="8" fill="#dbeafe" stroke="#3b82f6" stroke-width="2"/>
  <text x="200" y="166" text-anchor="middle" font-size="12" font-weight="700" fill="#1e40af">租户 A(高校 A)</text>
  <text x="60" y="194" font-size="10" fill="#1e3a8a">数据源:jdbc:mysql://host-a:3306/antflow</text>
  <text x="60" y="212" font-size="10" fill="#1e3a8a">用户:tenant_a_user</text>
  <text x="60" y="230" font-size="10" fill="#1e3a8a">数据完全隔离</text>
  <text x="60" y="252" font-size="10" fill="#1e3a8a">配置:</text>
  <text x="60" y="268" font-size="10" font-family="monospace" fill="#475569">spring.antflow.tenanta.url=...</text>
  <text x="60" y="284" font-size="10" font-family="monospace" fill="#475569">spring.antflow.tenanta.username=...</text>
  <text x="60" y="300" font-size="10" font-family="monospace" fill="#475569">spring.antflow.tenanta.password=...</text>

  <rect x="380" y="140" width="160" height="170" rx="8" fill="#dcfce7" stroke="#16a34a" stroke-width="2"/>
  <text x="460" y="166" text-anchor="middle" font-size="12" font-weight="700" fill="#155e2f">租户 B(高校 B)</text>
  <text x="400" y="194" font-size="10" fill="#14532d">独立数据源</text>
  <text x="400" y="212" font-size="10" fill="#14532d">独立数据库实例</text>
  <text x="400" y="240" font-size="10" fill="#14532d">可使用不同数据库:</text>
  <text x="400" y="258" font-size="10" fill="#14532d">租户 A: MySQL</text>
  <text x="400" y="276" font-size="10" fill="#14532d">租户 B: 达梦</text>
  <text x="400" y="294" font-size="10" fill="#14532d">租户 C: PostgreSQL</text>

  <rect x="560" y="140" width="320" height="170" rx="8" fill="#fef3c7" stroke="#d97706" stroke-width="2"/>
  <text x="720" y="166" text-anchor="middle" font-size="12" font-weight="700" fill="#92400e">租户 C(企业 C)</text>
  <text x="580" y="194" font-size="10" fill="#78350f">数据源:jdbc:dm://host-c:5236?compatibleMode=oracle</text>
  <text x="580" y="212" font-size="10" fill="#78350f">用户:SYSDBA</text>
  <text x="580" y="230" font-size="10" fill="#78350f">完全独立 schema</text>
  <text x="580" y="252" font-size="10" font-weight="600" fill="#92400e">配置(代码侦测):</text>
  <text x="580" y="268" font-size="10" font-family="monospace" fill="#475569">MBPDynamicDataSourceDetector</text>
  <text x="580" y="284" font-size="10" font-family="monospace" fill="#475569">自动识别 mybatis-plus 多数据源</text>
  <text x="580" y="300" font-size="10" font-family="monospace" fill="#475569">作为流程引擎多租户数据源</text>
</svg>

### 多租户配置示例

在 `application.properties` 中:

```properties
# 开启 SaaS 完整隔离模式
antflow.sass.full-sass-mode=true

# 多租户数据源(配合 MBPDynamicDataSourceDetector)
spring.datasource.dynamic.primary=master
spring.datasource.dynamic.strict=true
spring.datasource.dynamic.datasource.master.url=jdbc:mysql://main-host:3306/antflow
spring.datasource.dynamic.datasource.master.username=antflow
spring.datasource.dynamic.datasource.master.password=Pass
spring.datasource.dynamic.datasource.tenanta.url=jdbc:mysql://host-a:3306/antflow
spring.datasource.dynamic.datasource.tenanta.username=tenant_a
spring.datasource.dynamic.datasource.tenanta.password=PassA
spring.datasource.dynamic.datasource.tenantb.url=jdbc:dm://host-b:5236?compatibleMode=oracle
spring.datasource.dynamic.datasource.tenantb.username=SYSDBA
spring.datasource.dynamic.datasource.tenantb.password=PassB
```

## 关键代码位置

| 类 | 路径 | 用途 |
|---|---|---|
| DefaultDataBaseTypeDetector | [antflow-engine/.../DefaultDataBaseTypeDetector.java](file:///d:/projects/jimuoffice/antflow-engine/src/main/java/org/openoa/engine/conf/engineconfig/DefaultDataBaseTypeDetector.java) | 默认数据库类型侦测 |
| MBPDynamicDataSourceDetector | [antflow-engine/.../MBPDynamicDataSourceDetector.java](file:///d:/projects/jimuoffice/antflow-engine/src/main/java/org/openoa/engine/conf/engineconfig/MBPDynamicDataSourceDetector.java) | MyBatis-Plus 多数据源侦测 |
| GenericDruidDataSourceConfig | [antflow-engine/.../GenericDruidDataSourceConfig.java](file:///d:/projects/jimuoffice/antflow-engine/src/main/java/org/openoa/engine/conf/engineconfig/GenericDruidDataSourceConfig.java) | Druid 多租户数据源配置(注释参考) |
| DruidDataSourceFactory | [antflow-engine/.../DruidDataSourceFactory.java](file:///d:/projects/jimuoffice/antflow-engine/src/main/java/org/openoa/engine/conf/engineconfig/DruidDataSourceFactory.java) | Druid 数据源工厂(注释参考) |
| ActivitiConfig | [antflow-engine/.../ActivitiConfig.java](file:///d:/projects/jimuoffice/antflow-engine/src/main/java/org/openoa/engine/conf/engineconfig/ActivitiConfig.java) | Activiti 中文字体修复 + Schema 配置 |
| ActivitiProperties | [antflow-engine/.../ActivitiProperties.java](file:///d:/projects/jimuoffice/antflow-engine/src/main/java/org/openoa/engine/conf/engineconfig/ActivitiProperties.java) | 引擎可调优参数 |

## 切换注意事项

### 1. 表名大小写

| 数据库 | 默认大小写 | 建议 |
|---|---|---|
| MySQL | 取决于 `lower_case_table_names` | Linux 默认敏感,建议设为 1 |
| PostgreSQL | 敏感(必须小写) | 全部小写 |
| Oracle | 不敏感(默认大写存储) | 推荐大写,或加引号强制小写 |
| 达梦 Oracle 模式 | 不敏感(默认大写) | 同 Oracle |
| SQL Server | 取决于排序规则 | 通常不敏感 |

### 2. 主键策略

AntFlow 默认使用 MyBatis-Plus 的 `IdType.AUTO`(数据库自增),不同数据库支持:

| 数据库 | 自增方式 | MyBatis-Plus 配置 |
|---|---|---|
| MySQL | `AUTO_INCREMENT` | `IdType.AUTO` |
| PostgreSQL | `SERIAL` / `BIGSERIAL` | `IdType.AUTO` |
| Oracle | 序列(SEQUENCE) | `IdType.INPUT` + 手动序列 |
| 达梦 Oracle 模式 | 序列 | `IdType.INPUT` + 手动序列 |
| SQL Server | `IDENTITY` | `IdType.AUTO` |

### 3. SQL 函数差异

部分跨库不兼容的函数,在业务 SQL 中需注意:

| 函数 | MySQL | PostgreSQL | Oracle | 达梦 Oracle |
|---|---|---|---|---|
| 当前时间 | `NOW()` / `CURRENT_TIMESTAMP` | `CURRENT_TIMESTAMP` | `SYSDATE` | `SYSDATE` |
| 字符串拼接 | `CONCAT(a, b)` | `a \|\| b` | `a \|\| b` | `a \|\| b` |
| 日期格式化 | `DATE_FORMAT(d, '%Y-%m-%d')` | `TO_CHAR(d, 'YYYY-MM-DD')` | `TO_CHAR(d, 'YYYY-MM-DD')` | `TO_CHAR(d, 'YYYY-MM-DD')` |
| 限制 1 行 | `LIMIT 1` | `LIMIT 1` | `WHERE ROWNUM = 1` | `WHERE ROWNUM = 1` 或 `LIMIT 1` |
| 空值处理 | `IFNULL(a, b)` | `COALESCE(a, b)` | `NVL(a, b)` | `NVL(a, b)` |
| FIND_IN_SET | ✓ | ✗(需自定义) | ✗(需自定义) | ✗(需自定义) |

> AntFlow 内部业务 SQL 已尽量使用跨库通用函数,自定义 Mapper 时注意规避方言。

## 章节导航

- [生产部署](/ops/deploy) — 完整部署流程
- [常见问题排查](/ops/troubleshooting) — 启动/运行问题
- [性能优化](/ops/performance) — JVM/数据库/Activiti 调优
- [集成现有系统](/dev-guide/integrate-existing) — 接入企业用户/角色系统
- [数据库设计](/dev-guide/db-design) — 完整表结构详解
