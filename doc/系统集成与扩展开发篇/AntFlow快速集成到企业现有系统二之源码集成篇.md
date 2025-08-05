# AntFlow快速集成到企业现有系统二之多模块项目源码集成篇

> AntFlow是一款设计上类似钉钉/企业微信工作流,基于spring+mybatis+activiti+vue3等主流技术的低代码工作流平台。部署非常灵活，即可以做为独立的流程引擎中台部署（各系统通过三方接入形式接入流程中台服务），也可以做为一个工作流模块集成到现有的OA，CRM，CMS等系统中。独立部署非常简单，下载源码编译部署就可以了。本文着重讲解如何通过引用antflow-spring-boot-starter包来快速将AntFlow集成到企业现有系统中。通过源码集成方式将通过第二篇来介绍。

## 1.git克隆AntFlow项目到本地

```bash
git clone https://github.com/mrtylerzhou/AntFlow-activiti.git
```

> 如果访问github有障碍,可以使用码云

```bash
git clone https://gitee.com/tylerzhou/Antflow.git
```

## 2.复制antflow-base模块

idea打开刚刚下载好的antflow源码,找到antflow-base模块,右键ctrl+c,打开自己的项目,右键ctrl+v

> 注意,antflow-base模块包含了activiti的源码,复制过程较慢,需要耐心等一会.

## 3.复制antflow-engine模块

按步骤2里的方法(用户也可以使用其它自己熟悉的方法复制模块,甚至也可以不复制根据相对路径加载,都是可以的,目的是加载antflow-engine模块,过程不重要

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

## 7.自己项目父pom里通过以下配置加载antflow-base和antflow-engine模块

```xml
     <modules>
        <module>antflow-base</module>
        <module>antflow-engine</module>
        <module>你的其它模块...</module>
    </modules>
```

## 8进入到antflow-spring-boot-starter目录下,将里面的pom依赖全部复制到自己的项目里

通过以上配置,如果一切都顺利的话,以上步骤之行完你的项目就能正常启动起来了.

# spring boot版本兼容情况

antflow是基于springboot **2.7.17**开发的,测试能兼容**2.6.x**版本(测试使用的是2.6.13)和**2.4.x**(测试使用的是2.4.2). 2.3.x及以下版本需要根据报错信息对源码进行修改,目前已知的是跨域配置不一样.使用低版的可以下载源码,根据报错提示进行修改.

## AntFlow开源仓库一览

| 项目名                | 项目地址                                                                                        |
| --------------------- | ----------------------------------------------------------------------------------------------- |
| antflow独立流程设计器 | [https://gitee.com/ldhnet/AntFlow-Vue3](https://gitee.com/ldhnet/AntFlow-Vue3)                     |
| ruoyi前端集成         | [https://gitee.com/ldhnet/FlowAdmin-vue](https://gitee.com/ldhnet/FlowAdmin-vue)                   |
| antflow后端地址       | [https://github.com/mrtylerzhou/AntFlow-activiti](https://github.com/mrtylerzhou/AntFlow-activiti) |
