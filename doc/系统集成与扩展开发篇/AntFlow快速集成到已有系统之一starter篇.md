# AntFlow快速集成到企业现有系统一之starter篇

> AntFlow是一款设计上类似钉钉/企业微信工作流,基于spring+mybatis+activiti+vue3等主流技术的低代码工作流平台。部署非常灵活，即可以做为独立的流程引擎中台部署（各系统通过三方接入形式接入流程中台服务），也可以做为一个工作流模块集成到现有的OA，CRM，CMS等系统中。独立部署非常简单，下载源码编译部署就可以了。本文着重讲解如何通过引用antflow-spring-boot-starter包来快速将AntFlow集成到企业现有系统中。通过源码集成方式将通过第二篇来介绍。

## 1.将antflow-spring-boot-starter模块引入到用户自己的系统中,在pom文件中添加以下依赖

```xml
       <dependency>
            <groupId>io.github.mrtylerzhou</groupId>
            <artifactId>antflow-spring-boot-starter</artifactId>
            <version>0.101.0</version>
        </dependency>
```

## 2.进入到AntFlow仓库，将web模块下的application.properties和application.dev.properties文件里的配置拷贝到自己项目配置文件里

> 有些配置项企业已经系统可能已经有了，复制时候请酌情考虑

## 2.进入到AntFlow仓库,将web模块下的resources目录下的script目录下的sql文件拷贝出来在自己的数据库里执行

以上步骤执行完以后,启动项目,如果一切顺利的话,项目就能跑起来啦

# spring boot版本兼容情况

antflow是基于springboot **2.7.17**开发的,测试能兼容**2.6.x**版本(测试使用的是2.6.13)和**2.4.x**(测试使用的是2.4.2). 2.3.x及以下版本需要根据报错信息对源码进行修改,目前已知的是跨域配置不一样.使用低版的可以下载源码,根据报错提示进行修改.

## AntFlow开源仓库一览

| 项目名                | 项目地址                                                                                        |
| --------------------- | ----------------------------------------------------------------------------------------------- |
| antflow独立流程设计器 | [https://gitee.com/ldhnet/AntFlow-Vue3](https://gitee.com/ldhnet/AntFlow-Vue3)                     |
| ruoyi前端集成         | [https://gitee.com/ldhnet/FlowAdmin-vue](https://gitee.com/ldhnet/FlowAdmin-vue)                   |
| antflow后端地址       | [https://github.com/mrtylerzhou/AntFlow-activiti](https://github.com/mrtylerzhou/AntFlow-activiti) |
