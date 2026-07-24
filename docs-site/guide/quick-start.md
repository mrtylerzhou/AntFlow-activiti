# 快速开始

> 10 分钟,从 0 到 1,跑通你的第一个工作流!

## 环境准备

### 必需软件

| 软件 | 版本要求 | 说明 |
|---|---|---|
| JDK | 8(推荐)或 17 | master 分支为 Java 8,新版本切换 `java17_support` 分支 |
| MySQL | 5.7+ | 主数据库 |
| Node.js | 16.20.0+ | 前端构建 |
| Maven | 3.6+ | 或使用项目自带的 `mvnw` |

### 拉取代码

```bash
# Gitee(国内推荐)
git clone https://gitee.com/tylerzhou/Antflow.git

# GitHub
git clone https://github.com/mrtylerzhou/AntFlow.git
```

## 后端启动

### 第 1 步:数据库初始化

新建数据库 `antflow`,然后依次执行 `script/` 目录下的 SQL 脚本:

```bash
# 1. 引擎表 + 业务表(必须)
mysql -u root -p antflow < script/bpm_init_db.sql

# 2. 演示数据(可选,含 23 个测试用户、8 个角色、9 个部门)
mysql -u root -p antflow < script/t_demo_init.db.sql

# 3. 多表单支持(增量,低代码流程用到)
mysql -u root -p antflow < script/multi_form_support.sql
```

::: warning 重要
**必须使用 AntFlow 源码提供的 SQL 建表**,不要使用 Activiti 自动创建表功能。AntFlow fork 了 Activiti 5.23 源码并进行了魔改,表名前缀为 `AF_`(而非 `ACT_`),字段也有扩展。
:::

### 第 2 步:修改数据库配置

编辑 `antflow-web/src/main/resources/application-dev.properties`:

```properties
# 数据库连接
spring.datasource.url=jdbc:mysql://localhost:3306/antflow?useUnicode=true&characterEncoding=utf8&serverTimezone=Asia/Shanghai
spring.datasource.username=root
spring.datasource.password=你的密码

# 服务端口(默认 7001)
server.port=7001
```

### 第 3 步:启动后端

```bash
cd antflow
mvn spring-boot:run -pl antflow-web
```

或在 IDE 中直接运行 `antflow-web/src/main/java/org/openoa/AntFlowApplication.java`。

启动成功标志:控制台输出 `AntFlow Engine is started`,服务监听 `http://localhost:7001`。

## 前端启动

```bash
cd antflow-vue

# 安装依赖
pnpm install
# 或 npm install

# 启动开发服务器
pnpm run dev
# 或 npm run dev
```

::: tip 端口说明
`vite.config.js` 中默认配置端口为 `80`。如果 80 端口被占用或需要权限,可修改 `vite.config.js` 的 `server.port`,或通过命令行参数覆盖:

```bash
pnpm run dev -- --port 8080
```

注意:使用 `pnpm` 时,`--` 后的参数传递可能有问题,可直接修改 `vite.config.js`。
:::

启动成功后,浏览器访问 `http://localhost`(或你配置的端口),即可看到登录页。

## 登录系统

AntFlow 演示环境使用 mock 登录(读静态 JSON 文件),无需真实账号密码:

1. 打开 `http://localhost`
2. 在登录页选择或输入任意用户名(如"张三")
3. 密码随意输入(如 `123456`)
4. 点击登录

::: tip 测试账号
执行 `t_demo_init.db.sql` 后,系统内置 10 个测试用户(id 1-10):
- 张三、李四、王五、菜六、赵七、孙八、周九、吴十、郑十一、王十二
:::

详见 [登录与用户切换](/guide/login)。

## 创建第一个流程

### 方式一:低代码流程(零代码,推荐新手)

1. 登录系统后,进入 **流程管理 → 流程类型**
2. 点击"新增",创建一个低代码流程分类
3. 点击"流程设计"按钮,进入流程设计器
4. 在"表单设计"步骤,拖拽控件设计表单(如请假单:请假天数、请假原因)
5. 在"流程设计"步骤,添加审批节点和条件节点
6. 保存并发布
7. 进入"版本管理",点击"启动"
8. 在"发起请求"页面发起流程

详见 [流程设计](/workflow-design/flow-designer)。

### 方式二:DIY 自定义表单流程(需后端开发)

1. 后端实现 `FormOperationAdaptor` 接口,标注 `@ActivitiServiceAnno`
2. 前端在 `views/workflow/components/forms/` 下创建表单组件
3. 在 `bizFormMaps`(const.js)中注册 FormCode → 组件路径映射
4. 其余步骤同低代码流程

详见 [集成现有系统](/dev-guide/integrate-existing)。

## 常见启动问题

### 后端启动失败

| 问题 | 原因 | 解决 |
|---|---|---|
| 数据库连接失败 | 密码错误/数据库未创建 | 检查 `application-dev.properties` 配置 |
| 表不存在 | 未执行 SQL 脚本 | 依次执行 `script/` 下 3 个 SQL |
| Activiti 表结构错误 | 使用了 Activiti 自动建表 | 删库重建,用 AntFlow 的 SQL |
| 端口 7001 被占用 | 其他程序占用 | 修改 `server.port` |

### 前端启动失败

| 问题 | 原因 | 解决 |
|---|---|---|
| `npm install` 失败 | 网络问题 | 使用淘宝镜像 `npm config set registry https://registry.npmmirror.com` |
| 端口 80 被占用 | 系统服务占用 | 修改 `vite.config.js` 的 `server.port` |
| `vform` 加载失败 | 依赖未安装 | 重新 `npm install` |
| 后端接口 404 | 后端未启动或端口不对 | 检查 `vite.config.js` 的 `baseUrl` 指向后端(默认 `http://localhost:7001`) |

## 下一步

- [系统总览](/guide/overview) — 了解 AntFlow 的整体功能和界面
- [流程设计](/workflow-design/flow-category) — 学习如何设计工作流
- [架构总览](/dev-guide/architecture) — 理解 AntFlow 的技术架构
