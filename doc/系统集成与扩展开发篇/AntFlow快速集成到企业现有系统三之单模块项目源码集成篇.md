# AntFlow快速集成到企业现有系统三之单模块项目源码集成篇

> AntFlow是一款设计上类似钉钉/企业微信工作流,基于spring+mybatis+activiti+vue3等主流技术的低代码工作流平台。部署非常灵活，即可以做为独立的流程引擎中台部署（各系统通过三方接入形式接入流程中台服务），也可以做为一个工作流模块集成到现有的OA，CRM，CMS等系统中。独立部署非常简单，下载源码编译部署就可以了。本文着重讲解如何通过引用antflow-spring-boot-starter包来快速将AntFlow集成到企业现有系统中。通过源码集成方式将通过第二篇来介绍。

## 1.git克隆AntFlow项目到本地

```bash
git clone https://github.com/mrtylerzhou/AntFlow-activiti.git
```

> 如果访问github有障碍,可以使用码云

```bash
git clone https://gitee.com/tylerzhou/Antflow.git
```

## 2.复制antflow-base模块下源码

idea打开刚刚下载好的antflow源码,找到antflow-base模块,打开/src/main在磁盘所在位置,可以看到里面有两个文件夹分别是java和resources,将它们全选,然后复制,再进入到自己项目的/src/main磁盘目录,将刚复制的文件拷贝到此目录下.

> 注意,复制的时候要复制到/src/main目录下面,不要复制到里面的目录去了.复制目标文件夹下面也有java和resources目录,并不会覆盖,而是将内容拷贝进去,因此不用担心覆盖(mac上没有验证过)

> 如果你看过了上一篇多模块目录复制,那一篇里面复制的是整个antflow-base模块,这里复制的是/src/main下面的源码,其它的内容不要复制.这是和多模块的一个差别

## 3.复制antflow-engine模块下源码

按步骤2里的方法将antflow-engine源码也复制到自己项目里

## 4.进入到源码项目web模块的resources目录下的script目录,复制出里面的两个sql脚本在自己的数据库里执行.

> act_init_db.sql用于初始化activiti所需要的表,注意不能开启自动创建表,一定要使用antflow提供的初始化脚本来初始化数据库

> bpm_init_db.sql为AntFlow扩展的表,表比较多,不要被吓到,这些都是为支持特定业务,用户不需要关心这些表.

## 5.进入到源码项目web模块的resources目录下将application.properties和application.dev.properties文件里的内容复制到自己项目

> 可以先一下子复制过来,里面的内容像jackson配置,mybatis日志输出可以根据自己需求决定要还是不要.但是不能少了,不然项目可能启动不起来,先让项目启动起来,然后再慢慢调整

## 6.将以下内容复制到自己的项目启动类上

```java
@MapperScans(
        { @MapperScan("org.openoa.base.mapper"),
                @MapperScan("org.openoa.common.mapper"),
                @MapperScan("org.openoa.engine.bpmnconf.mapper"),
                @MapperScan("你的项目的mapper路径,不一定要配,视情况而定")
        }
)
@ComponentScan({"org.openoa","指定你的模块的包路径"})
```


## 7.进入到antflow-spring-boot-starter目录下,将里面的pom依赖全部复制到自己的项目里

## 8.模块移除

antflow-spring-boot-starter模块为方便快速以jar包的形式将antflow集成到自己的项目中,本篇是单模块项目集成源码,第7步执行以后,要删除掉非必须的antflow-base和antflow-engine依赖

## 9. 项目编译不通过解决

antflow目前仍然在快速迭代中,目前(0.11版本)依赖管理上还有不少小问题,单模块项目集成稍微麻烦一些,根据报错找到报错缺少的是哪些依赖,然后在源码项目搜索,一项一项引入进来直到编译通过.后续会解决这些问题

目前已知拷贝了antflow-spring-boot-starter目录下的pom依赖后仍然缺少的依赖

```xml
 <dependency>
            <groupId>org.apache.commons</groupId>
            <artifactId>commons-email</artifactId>
            <version>1.5</version>
        </dependency>
        <dependency>
            <groupId>org.hibernate.javax.persistence</groupId>
            <artifactId>hibernate-jpa-2.0-api</artifactId>
            <version>1.0.1.Final</version>
        </dependency>
        <dependency>
            <groupId>com.alibaba.fastjson2</groupId>
            <artifactId>fastjson2-extension-spring5</artifactId>
            <version>${fastjson2.version}</version>
        </dependency>
        <dependency>
            <groupId>commons-io</groupId>
            <artifactId>commons-io</artifactId>
            <version>2.6</version>
        </dependency>
        <dependency>
            <groupId>com.google.guava</groupId>
            <artifactId>guava</artifactId>
            <version>31.0.1-jre</version>
        </dependency>
        <dependency>
            <groupId>com.github.xiaoymin</groupId>
            <artifactId>knife4j-openapi3-spring-boot-starter</artifactId>
            <version>4.5.0</version>
        </dependency>
```
如果一切顺利,执行完以上步骤以后,你的项目就能正常跑起来了

# spring boot版本兼容情况

antflow是基于springboot **2.7.17**开发的,测试能兼容**2.6.x**版本(测试使用的是2.6.13)和**2.4.x**(测试使用的是2.4.2). 2.3.x及以下版本需要根据报错信息对源码进行修改,目前已知的是跨域配置不一样.使用低版的可以下载源码,根据报错提示进行修改.

## AntFlow开源仓库一览

| 项目名                | 项目地址                                                                                        |
| --------------------- | ----------------------------------------------------------------------------------------------- |
| antflow独立流程设计器 | [https://gitee.com/ldhnet/AntFlow-Vue3](https://gitee.com/ldhnet/AntFlow-Vue3)                     |
| ruoyi前端集成         | [https://gitee.com/ldhnet/FlowAdmin-vue](https://gitee.com/ldhnet/FlowAdmin-vue)                   |
| antflow后端地址       | [https://github.com/mrtylerzhou/AntFlow-activiti](https://github.com/mrtylerzhou/AntFlow-activiti) |
