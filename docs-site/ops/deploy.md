# 生产部署

> 本章介绍 AntFlow 在生产环境的完整部署流程:从环境准备、数据库初始化、后端打包、前端构建、Nginx 配置到 JVM 调优、监控接入,提供 L3 级手册式指引。

## 部署架构总览

<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 920 460" font-family="-apple-system, BlinkMacSystemFont, 'Segoe UI', sans-serif">
  <defs>
    <marker id="arrDep" viewBox="0 0 10 10" refX="9" refY="5" markerWidth="6" markerHeight="6" orient="auto"><path d="M0,0 L10,5 L0,10 z" fill="#475569"/></marker>
    <linearGradient id="depG1" x1="0" y1="0" x2="0" y2="1"><stop offset="0%" stop-color="#dbeafe"/><stop offset="100%" stop-color="#bfdbfe"/></linearGradient>
    <linearGradient id="depG2" x1="0" y1="0" x2="0" y2="1"><stop offset="0%" stop-color="#dcfce7"/><stop offset="100%" stop-color="#bbf7d0"/></linearGradient>
    <linearGradient id="depG3" x1="0" y1="0" x2="0" y2="1"><stop offset="0%" stop-color="#fef3c7"/><stop offset="100%" stop-color="#fde68a"/></linearGradient>
    <linearGradient id="depG4" x1="0" y1="0" x2="0" y2="1"><stop offset="0%" stop-color="#fce7f3"/><stop offset="100%" stop-color="#fbcfe8"/></linearGradient>
    <linearGradient id="depG5" x1="0" y1="0" x2="0" y2="1"><stop offset="0%" stop-color="#f1f5f9"/><stop offset="100%" stop-color="#e2e8f0"/></linearGradient>
  </defs>

  <!-- 用户层 -->
  <rect x="20" y="20" width="880" height="50" rx="8" fill="url(#depG1)" stroke="#3b82f6" stroke-width="2"/>
  <text x="460" y="52" text-anchor="middle" font-size="13" font-weight="700" fill="#1e40af">用户浏览器</text>

  <line x1="460" y1="70" x2="460" y2="90" stroke="#475569" stroke-width="2" marker-end="url(#arrDep)"/>

  <!-- 反向代理层 -->
  <rect x="20" y="90" width="880" height="80" rx="8" fill="url(#depG2)" stroke="#16a34a" stroke-width="2"/>
  <text x="460" y="114" text-anchor="middle" font-size="13" font-weight="700" fill="#155e2f">Nginx 反向代理(80/443)</text>
  <rect x="40" y="128" width="410" height="32" rx="6" fill="#fff" stroke="#16a34a"/>
  <text x="245" y="148" text-anchor="middle" font-size="10" fill="#14532d">/ → antflow-vue/dist 静态资源</text>
  <rect x="470" y="128" width="410" height="32" rx="6" fill="#fff" stroke="#16a34a"/>
  <text x="675" y="148" text-anchor="middle" font-size="10" fill="#14532d">/api/ → proxy_pass http://127.0.0.1:7001</text>

  <line x1="245" y1="170" x2="245" y2="190" stroke="#475569" stroke-width="2" marker-end="url(#arrDep)"/>
  <line x1="675" y1="170" x2="675" y2="190" stroke="#475569" stroke-width="2" marker-end="url(#arrDep)"/>

  <!-- 应用层 -->
  <rect x="20" y="190" width="430" height="100" rx="8" fill="url(#depG3)" stroke="#d97706" stroke-width="2"/>
  <text x="235" y="214" text-anchor="middle" font-size="13" font-weight="700" fill="#92400e">前端静态资源(antdflow-vue/dist)</text>
  <text x="40" y="238" font-size="10" fill="#78350f">Vue 3 + Vite 构建产物</text>
  <text x="40" y="254" font-size="10" fill="#78350f">含 index.html + assets/*.js + *.css</text>
  <text x="40" y="270" font-size="10" fill="#78350f">体积约 5~10 MB(gzip 后 2~4 MB)</text>

  <rect x="470" y="190" width="430" height="100" rx="8" fill="url(#depG4)" stroke="#db2777" stroke-width="2"/>
  <text x="685" y="214" text-anchor="middle" font-size="13" font-weight="700" fill="#9d174d">后端 Spring Boot(7001)</text>
  <text x="490" y="238" font-size="10" fill="#831843">java -jar antflow-web.jar --spring.profiles.active=pro</text>
  <text x="490" y="254" font-size="10" fill="#831843">JVM:-Xms512m -Xmx2g -XX:+UseG1GC</text>
  <text x="490" y="270" font-size="10" fill="#831843">日志:~/log/antflow/logs/(error/sql/slowsql)</text>

  <line x1="685" y1="290" x2="685" y2="310" stroke="#475569" stroke-width="2" marker-end="url(#arrDep)"/>

  <!-- 数据层 -->
  <rect x="20" y="310" width="880" height="130" rx="8" fill="url(#depG5)" stroke="#475569" stroke-width="2"/>
  <text x="460" y="334" text-anchor="middle" font-size="13" font-weight="700" fill="#1e293b">数据层</text>

  <rect x="40" y="350" width="270" height="76" rx="6" fill="#fff" stroke="#475569"/>
  <text x="175" y="372" text-anchor="middle" font-size="11" font-weight="600" fill="#1e293b">MySQL 8.0+</text>
  <text x="60" y="394" font-size="10" fill="#475569">• Activiti 引擎表(act_*,34 张)</text>
  <text x="60" y="410" font-size="10" fill="#475569">• AntFlow 业务表(t_bpmn_*,46 张)</text>
  <text x="60" y="424" font-size="10" fill="#475569">• 连接池:Druid(max-active=100)</text>

  <rect x="325" y="350" width="270" height="76" rx="6" fill="#fff" stroke="#475569"/>
  <text x="460" y="372" text-anchor="middle" font-size="11" font-weight="600" fill="#1e293b">Redis(可选,推荐)</text>
  <text x="345" y="394" font-size="10" fill="#475569">• 分布式锁</text>
  <text x="345" y="410" font-size="10" fill="#475569">• Session 缓存</text>
  <text x="345" y="424" font-size="10" fill="#475569">• 字典数据缓存</text>

  <rect x="610" y="350" width="270" height="76" rx="6" fill="#fff" stroke="#475569"/>
  <text x="745" y="372" text-anchor="middle" font-size="11" font-weight="600" fill="#1e293b">邮件/短信服务</text>
  <text x="630" y="394" font-size="10" fill="#475569">• SMTP 邮件(smtp.163.com 等)</text>
  <text x="630" y="410" font-size="10" fill="#475569">• 短信网关(阿里云/腾讯云)</text>
  <text x="630" y="424" font-size="10" fill="#475569">• 钉钉/企业微信(可选)</text>
</svg>

## 环境准备

### 操作系统

| OS | 版本 | 备注 |
|---|---|---|
| CentOS | 7.9+ / 8.x | 推荐生产环境 |
| Ubuntu | 20.04 LTS+ | 同样推荐 |
| Windows Server | 2016+ | 可用,但生产不建议 |
| 麒麟 / 统信 | V10+ | 国产化场景 |

### 软件依赖

| 组件 | 最低版本 | 推荐版本 | 说明 |
|---|---|---|---|
| JDK | 8 (master 分支) / 17 (java17_support 分支) | OpenJDK 8u372+ / OpenJDK 17.0.8+ | `java -version` 验证 |
| Maven | 3.6+ | 3.9.x | 仅编译期需要 |
| Node.js | 16+ | 18 LTS | 仅前端构建期需要 |
| pnpm | 7+ | 8.x | 推荐使用,优于 npm |
| MySQL | 5.7+ | 8.0.27+ | 主数据库 |
| Redis | 6+ | 7.x | 可选,推荐 |
| Nginx | 1.18+ | 1.24+ | 反向代理 |

### 硬件配置建议

| 业务规模 | 并发审批量 | CPU | 内存 | 数据库 | 磁盘 |
|---|---|:---:|:---:|---|---|
| 小型(< 100 人) | < 10 并发 | 2 核 | 4 GB | MySQL 单机 4C8G | 50 GB SSD |
| 中型(100~1000 人) | 10~50 并发 | 4 核 | 8 GB | MySQL 主从 8C16G | 200 GB SSD |
| 大型(1000~10000 人) | 50~200 并发 | 8 核 | 16 GB | MySQL 主从 16C32G + Redis | 500 GB SSD |
| 超大型(> 10000 人) | > 200 并发 | 16 核+ | 32 GB+ | MySQL 集群 + Redis 集群 | 1 TB+ NVMe |

## 数据库初始化

### 1. 创建数据库

```sql
-- MySQL 8.0+ 推荐 utf8mb4 字符集
CREATE DATABASE antflow DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
CREATE USER 'antflow'@'%' IDENTIFIED BY 'YourStrong@Pass123';
GRANT ALL PRIVILEGES ON antflow.* TO 'antflow'@'%';
FLUSH PRIVILEGES;
```

### 2. 执行初始化 SQL(顺序敏感)

> **关键**:必须使用 AntFlow 项目 `script/` 目录下的 SQL,**不要使用 Activiti 自动建表功能**(AntFlow 对 Activiti 源码进行了魔改,自动建表会与魔改版本冲突)。

SQL 脚本位于 [script/](file:///d:/projects/jimuoffice/script/) 目录:

| 顺序 | 文件 | 用途 | 是否必需 |
|:---:|---|---|:---:|
| 1 | [act_init_db.sql](file:///d:/projects/jimuoffice/script/act_init_db.sql) | Activiti 引擎表(34 张) | ✅ 必需 |
| 2 | [bpm_init_db.sql](file:///d:/projects/jimuoffice/script/bpm_init_db.sql) | AntFlow 业务表(46 张) | ✅ 必需 |
| 3 | [bpm_init_db_data.sql](file:///d:/projects/jimuoffice/script/bpm_init_db_data.sql) | Demo 数据(测试账号、示例流程) | ⚠️ 生产可选 |
| 4 | [multi_form_support.sql](file:///d:/projects/jimuoffice/script/multi_form_support.sql) | 多表单扩展(外部表单模式) | ⚠️ 按需 |

执行命令:

```bash
# 登录 MySQL
mysql -u root -p antflow

# 在 MySQL 提示符下依次执行
source /path/to/script/act_init_db.sql;
source /path/to/script/bpm_init_db.sql;
source /path/to/script/bpm_init_db_data.sql;  -- 生产可选
source /path/to/script/multi_form_support.sql; -- 按需
```

### 3. 确认 Activiti 自动建表已关闭

[application.properties](file:///d:/projects/jimuoffice/antflow-web/src/main/resources/application.properties):

```properties
spring.activiti.check-process-definitions=false
spring.activiti.database-schema-update=none
```

## 后端打包与启动

### 1. 修改生产环境配置

复制 `application-dev.properties` 为 `application-pro.properties`,修改:

```properties
# application-pro.properties(生产环境)

# 1. 端口
server.port=7001

# 2. 数据源(生产 MySQL)
spring.datasource.url=jdbc:mysql://PROD_MYSQL_HOST:3306/antflow?nullCatalogMeansCurrent=true&useUnicode=true&characterEncoding=utf-8&serverTimezone=Asia/Shanghai
spring.datasource.username=antflow
spring.datasource.password=YourStrong@Pass123
spring.datasource.driver-class-name=com.mysql.jdbc.Driver

# 3. Druid 连接池(生产调优)
spring.datasource.druid.initial-size=10
spring.datasource.druid.min-idle=10
spring.datasource.druid.max-active=100
spring.datasource.druid.max-wait=60000
spring.datasource.druid.keep-alive=true
spring.datasource.druid.remove-abandoned=true
spring.datasource.druid.remove-abandoned-timeout=1800
spring.datasource.druid.log-abandoned=true
spring.datasource.druid.validation-query=SELECT 1 FROM DUAL
spring.datasource.druid.test-while-idle=true
spring.datasource.druid.time-between-eviction-runs-millis=60000
spring.datasource.druid.min-evictable-idle-time-millis=300000

# 4. 日志(生产降级为 WARN,减少日志量)
logging.level.org.openoa=info
logging.level.org.openoa.mapper=warn
logging.level.org.activiti=warn
logging.level.com.alibaba=error
mybatis-plus.configuration.log-impl=org.apache.ibatis.logging.nologging.NoLoggingImpl

# 5. 邮件(生产 SMTP)
message.email.host=smtp.your-company.com
spring.mail.host=smtp.your-company.com
spring.mail.username=workflow@your-company.com
spring.mail.password=YourEmailPass
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true
spring.mail.properties.mail.smtp.ssl.enable=true

# 6. AntFlow 业务配置
# 用户表对接企业系统(参考 /dev-guide/integrate-existing)
antflow.common.empTable.empTblName=t_user
antflow.common.empTable.idField=id
antflow.common.empTable.nameField=user_name
antflow.common.scan-packages=org.openoa,com.yourcompany

# SaaS 模式(默认关闭)
antflow.sass.full-sass-mode=false
```

### 2. Maven 打包

```bash
# 进入项目根目录
cd /path/to/jimuoffice

# Maven 打包(跳过测试,加速)
./mvnw clean install -DskipTests

# 或使用系统 mvn
mvn clean install -DskipTests

# 产物:antflow-web/target/antflow-web-*.jar
ls -lh antflow-web/target/antflow-web-*.jar
# -rw-r--r-- 1 user group 85M Jul 24 10:00 antflow-web-1.0.jar
```

### 3. 启动 jar(生产推荐方式)

#### 方式 A:直接 java -jar

```bash
# 简单启动
java -jar antflow-web/target/antflow-web-*.jar \
  --spring.profiles.active=pro

# 推荐 JVM 参数
java -server \
  -Xms512m -Xmx2g \
  -XX:+UseG1GC \
  -XX:MaxGCPauseMillis=200 \
  -XX:+HeapDumpOnOutOfMemoryError \
  -XX:HeapDumpPath=/var/log/antflow/heapdump.hprof \
  -Dfile.encoding=UTF-8 \
  -Duser.timezone=Asia/Shanghai \
  -Dspring.profiles.active=pro \
  -jar antflow-web-1.0.jar \
  > /var/log/antflow/stdout.log 2>&1 &
```

#### 方式 B:Systemd 服务(生产推荐)

`/etc/systemd/system/antflow.service`:

```ini
[Unit]
Description=AntFlow Workflow Engine
Documentation=https://github.com/mrtylerzhou/AntFlow
After=network.target mysql.service

[Service]
Type=simple
User=antflow
Group=antflow
WorkingDirectory=/opt/antflow
ExecStart=/usr/bin/java -server \
  -Xms512m -Xmx2g \
  -XX:+UseG1GC \
  -XX:MaxGCPauseMillis=200 \
  -XX:+HeapDumpOnOutOfMemoryError \
  -XX:HeapDumpPath=/var/log/antflow/heapdump.hprof \
  -Dfile.encoding=UTF-8 \
  -Duser.timezone=Asia/Shanghai \
  -jar /opt/antflow/antflow-web.jar \
  --spring.profiles.active=pro
ExecStop=/bin/kill -TERM $MAINPID
Restart=on-failure
RestartSec=10
StandardOutput=append:/var/log/antflow/stdout.log
StandardError=append:/var/log/antflow/stderr.log

[Install]
WantedBy=multi-user.target
```

```bash
# 启用并启动
sudo systemctl daemon-reload
sudo systemctl enable antflow
sudo systemctl start antflow
sudo systemctl status antflow
sudo systemctl restart antflow  # 重启
sudo journalctl -u antflow -f   # 实时查看日志
```

## 前端构建与部署

### 1. 修改 API 地址

编辑 [.env.production](file:///d:/projects/jimuoffice/antflow-vue/.env.production):

```properties
# 生产环境后端 API
VITE_APP_BASE_API = 'http://your-domain.com/api'
# 或使用相对路径(配合 Nginx 反向代理)
# VITE_APP_BASE_API = '/api'
```

### 2. 构建生产包

```bash
cd antflow-vue

# 安装依赖(使用淘宝镜像加速)
pnpm install --registry=https://registry.npmmirror.com
# 或 npm install --registry=https://registry.npmmirror.com

# 构建生产环境
pnpm run build:prod
# 产物:antflow-vue/dist/
ls -lh dist/
# index.html
# assets/
# favicon.ico
```

构建产物约 5~10 MB,gzip 后 2~4 MB。

### 3. 上传到服务器

```bash
# 打包并上传
cd antflow-vue
tar -czf dist.tar.gz dist/
scp dist.tar.gz user@your-server:/tmp/

# 在服务器上解压
ssh user@your-server
sudo mkdir -p /var/www/antflow
sudo tar -xzf /tmp/dist.tar.gz -C /var/www/antflow --strip-components=1
sudo chown -R nginx:nginx /var/www/antflow
```

## Nginx 配置

### 完整生产配置

`/etc/nginx/conf.d/antflow.conf`:

```nginx
# HTTP -> HTTPS 重定向
server {
    listen 80;
    server_name your-domain.com;
    return 301 https://$server_name$request_uri;
}

# HTTPS 主配置
server {
    listen 443 ssl http2;
    server_name your-domain.com;

    # SSL 证书
    ssl_certificate     /etc/nginx/ssl/your-domain.com.crt;
    ssl_certificate_key /etc/nginx/ssl/your-domain.com.key;
    ssl_protocols TLSv1.2 TLSv1.3;
    ssl_ciphers HIGH:!aNULL:!MD5;
    ssl_prefer_server_ciphers on;

    # 安全头
    add_header X-Frame-Options "SAMEORIGIN";
    add_header X-Content-Type-Options "nosniff";
    add_header X-XSS-Protection "1; mode=block";

    # Gzip 压缩
    gzip on;
    gzip_vary on;
    gzip_min_length 1024;
    gzip_proxied any;
    gzip_comp_level 6;
    gzip_types
        text/plain
        text/css
        text/xml
        text/javascript
        application/javascript
        application/json
        application/xml+rss
        application/rss+xml
        image/svg+xml;

    # 前端静态资源
    root /var/www/antflow;
    index index.html;

    # 静态资源缓存(Vite 构建产物含 hash,可长期缓存)
    location /assets/ {
        expires 1y;
        add_header Cache-Control "public, immutable";
        try_files $uri =404;
    }

    # 主页不缓存(每次请求都拿最新 index.html)
    location = /index.html {
        add_header Cache-Control "no-cache, no-store, must-revalidate";
        expires 0;
    }

    # SPA 路由回退
    location / {
        try_files $uri $uri/ /index.html;
    }

    # 后端 API 反向代理
    location /api/ {
        proxy_pass http://127.0.0.1:7001/;
        proxy_http_version 1.1;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;

        # 上传文件大小限制
        client_max_body_size 50m;

        # 超时设置
        proxy_connect_timeout 60s;
        proxy_send_timeout 300s;
        proxy_read_timeout 300s;

        # WebSocket 支持(如需实时通知)
        proxy_set_header Upgrade $http_upgrade;
        proxy_set_header Connection "upgrade";
    }

    # 接口文档(可选,生产建议关闭或加 IP 白名单)
    location /doc.html {
        # allow 10.0.0.0/8;     # 内网白名单
        # deny all;
        proxy_pass http://127.0.0.1:7001/doc.html;
        proxy_set_header Host $host;
    }
}
```

### 启动 Nginx

```bash
# 测试配置
sudo nginx -t

# 启动 / 重载
sudo systemctl start nginx
sudo systemctl reload nginx
sudo systemctl status nginx
```

## Docker 化部署(可选)

AntFlow 仓库未提供 Dockerfile,以下为推荐写法:

### Dockerfile(后端)

```dockerfile
# Dockerfile
FROM eclipse-temurin:8-jre-jammy

LABEL maintainer="your-name <your-email>"

WORKDIR /app

# 复制 jar
COPY antflow-web/target/antflow-web-*.jar /app/antflow-web.jar

# 时区
ENV TZ=Asia/Shanghai
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone

# JVM 参数
ENV JAVA_OPTS="-server -Xms512m -Xmx2g -XX:+UseG1GC -XX:MaxGCPauseMillis=200 -Dfile.encoding=UTF-8"

EXPOSE 7001

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -Dspring.profiles.active=pro -jar /app/antflow-web.jar"]
```

### docker-compose.yml

```yaml
version: '3.8'

services:
  mysql:
    image: mysql:8.0
    container_name: antflow-mysql
    restart: always
    environment:
      MYSQL_ROOT_PASSWORD: RootStrong@Pass123
      MYSQL_DATABASE: antflow
      MYSQL_USER: antflow
      MYSQL_PASSWORD: AntflowStrong@Pass123
    ports:
      - "3306:3306"
    volumes:
      - mysql_data:/var/lib/mysql
      - ./script/act_init_db.sql:/docker-entrypoint-initdb.d/01-act.sql
      - ./script/bpm_init_db.sql:/docker-entrypoint-initdb.d/02-bpm.sql
      - ./script/bpm_init_db_data.sql:/docker-entrypoint-initdb.d/03-bpm-data.sql
    command: --character-set-server=utf8mb4 --collation-server=utf8mb4_general_ci

  redis:
    image: redis:7-alpine
    container_name: antflow-redis
    restart: always
    ports:
      - "6379:6379"
    volumes:
      - redis_data:/data

  backend:
    build:
      context: .
      dockerfile: Dockerfile
    container_name: antflow-backend
    restart: always
    ports:
      - "7001:7001"
    environment:
      - SPRING_DATASOURCE_URL=jdbc:mysql://mysql:3306/antflow?nullCatalogMeansCurrent=true
      - SPRING_DATASOURCE_USERNAME=antflow
      - SPRING_DATASOURCE_PASSWORD=AntflowStrong@Pass123
    depends_on:
      - mysql
      - redis
    volumes:
      - ./logs:/var/log/antflow

  frontend:
    image: nginx:1.24-alpine
    container_name: antflow-frontend
    restart: always
    ports:
      - "80:80"
      - "443:443"
    volumes:
      - ./antflow-vue/dist:/usr/share/nginx/html
      - ./nginx/antflow.conf:/etc/nginx/conf.d/default.conf
    depends_on:
      - backend

volumes:
  mysql_data:
  redis_data:
```

### 启动

```bash
# 构建并启动
docker-compose up -d --build

# 查看状态
docker-compose ps
docker-compose logs -f backend
```

## 监控接入

### 1. Spring Boot Actuator(推荐)

在 `application-pro.properties` 追加:

```properties
# Actuator 监控(生产需配合 Spring Security 鉴权)
management.endpoints.web.exposure.include=health,info,metrics,env,prometheus
management.endpoint.health.show-details=when-authorized
management.endpoints.web.base-path=/actuator

# Prometheus 集成(可选)
management.metrics.export.prometheus.enabled=true
```

> 配合 Spring Security 或 Nginx IP 白名单保护 `/actuator/*` 端点,避免敏感信息泄露。

### 2. Druid 监控面板

```properties
spring.datasource.druid.stat-view-servlet.enabled=true
spring.datasource.druid.stat-view-servlet.login-username=druid_admin
spring.datasource.druid.stat-view-servlet.login-password=DruidStrong@Pass123
spring.datasource.druid.stat-view-servlet.url-pattern=/druid/*
spring.datasource.druid.stat-view-servlet.allow=10.0.0.0/8  # 内网白名单
spring.datasource.druid.stat-view-servlet.deny=
spring.datasource.druid.web-stat-filter.enabled=true
spring.datasource.druid.web-stat-filter.url-pattern=/*
spring.datasource.druid.web-stat-filter.exclusions=*.js,*.gif,*.jpg,*.bmp,*.png,*.css,*.ico,/druid/*
```

访问 `https://your-domain.com/api/druid/`(Nginx 需配置 `/druid/` 反向代理到 7001)。

### 3. 日志收集

#### 日志文件清单

基于 [logback-spring.xml](file:///d:/projects/jimuoffice/antflow-web/src/main/resources/logback-spring.xml):

| 文件 | 路径 | 用途 |
|---|---|---|
| `error.log` | `~/log/antflow/logs/error.log` | ERROR 级别异常 |
| `sql.log` | `~/log/antflow/logs/sql.log` | SQL 执行日志(debug) |
| `slowsql.log` | `~/log/antflow/logs/slowsql.log` | 慢 SQL(MyBatis-Plus 插件) |
| `biz_logs/` | `~/log/antflow/monitor/biz_logs/` | 业务埋点日志 |

#### ELK / Loki 接入

```yaml
# filebeat.yml(采集 AntFlow 日志到 ELK)
filebeat.inputs:
- type: log
  paths:
    - /var/log/antflow/logs/*.log
  fields:
    app: antflow
  fields_under_root: true
  multiline.pattern: '^\d{2}-\d{2} \d{2}:\d{2}:\d{2}'
  multiline.negate: true
  multiline.match: after

output.elasticsearch:
  hosts: ["elasticsearch:9200"]
  index: "antflow-%{+yyyy.MM.dd}"
```

## 健康检查

### 1. 应用健康检查

```bash
# 简单 ping
curl http://localhost:7001/actuator/health
# {"status":"UP"}

# 详细检查(需鉴权)
curl -u admin:admin http://localhost:7001/actuator/health/details
```

### 2. 数据库连通性

```bash
# 通过 Actuator
curl http://localhost:7001/actuator/health/db
```

### 3. Nginx 健康检查

```nginx
# 在 antflow.conf 中添加
location /health {
    access_log off;
    return 200 "healthy\n";
}
```

```bash
curl http://your-domain.com/health
# healthy
```

## 升级与回滚

### 1. 升级流程

```bash
# 1. 备份数据库
mysqldump -u root -p antflow > antflow_backup_$(date +%Y%m%d).sql

# 2. 备份当前 jar
cp /opt/antflow/antflow-web.jar /opt/antflow/antflow-web.backup.jar

# 3. 上传新 jar
scp antflow-web-new.jar user@server:/opt/antflow/antflow-web.jar

# 4. 重启服务
sudo systemctl restart antflow

# 5. 验证
curl http://localhost:7001/actuator/health
```

### 2. 回滚流程

```bash
# 1. 停止服务
sudo systemctl stop antflow

# 2. 恢复 jar
cp /opt/antflow/antflow-web.backup.jar /opt/antflow/antflow-web.jar

# 3. 恢复数据库(如需要)
mysql -u root -p antflow < antflow_backup_20260724.sql

# 4. 重启
sudo systemctl start antflow
```

## 安全加固清单

| 项 | 措施 | 优先级 |
|---|---|:---:|
| 数据库密码 | 强密码(16+ 位,含大小写数字符号) | 🔴 高 |
| Druid 监控面板 | 修改默认密码 + IP 白名单 | 🔴 高 |
| Actuator 端点 | 配合 Spring Security 鉴权 | 🔴 高 |
| HTTPS | 全站强制 HTTPS | 🔴 高 |
| 接口文档 | 生产环境关闭 `/doc.html` 或加白名单 | 🟡 中 |
| 文件上传 | 限制类型 + 大小 + 病毒扫描 | 🟡 中 |
| SQL 注入 | 使用 MyBatis-Plus 参数化查询,禁止拼接 SQL | 🟡 中 |
| XSS 防护 | 前端输入过滤 + 后端 HTML escape | 🟡 中 |
| CSRF | Spring Security 开启 CSRF token | 🟢 低(REST API 通常不需要) |
| 速率限制 | Nginx limit_req 限制 API 频率 | 🟢 低 |

## 章节导航

- [多数据库支持](/ops/db-multi) — 12+ 种数据库的配置切换
- [常见问题排查](/ops/troubleshooting) — 启动、运行、集成问题排查
- [性能优化](/ops/performance) — JVM、数据库、Activiti 调优
- [快速开始](/guide/quick-start) — 开发环境搭建
- [集成现有系统](/dev-guide/integrate-existing) — 接入企业用户/角色系统
