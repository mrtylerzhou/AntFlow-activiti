<p align="center" style="margin-bottom: 0 !important">
	<img alt="logo" src="./doc/images/logo.png"  width = 20%;>
</p>

<p align="center">
 <img src="https://img.shields.io/badge/SpringBoot-2.7.17-blue" alt="Downloads">
 <img src="https://img.shields.io/badge/Vue-3.5.15-yellow.svg" alt="Downloads">
 <img src="https://img.shields.io/badge/MyBatisPlus-blue.svg" alt="Downloads">
 <img src="https://img.shields.io/badge/activiti-yellow.svg" alt="Downloads">
 <img src="https://img.shields.io/badge/vform-red.svg" alt="Downloads">
 <img src="https://img.shields.io/badge/license-Apache 2.0-green.svg" alt="Downloads">
 <img src="https://img.shields.io/badge/可商用-免费-purple.svg" alt="Downloads">
</p>

<h2 align="center" style="margin: 10px 0 10px; font-weight: bold;">🚀 开源、现代、零门槛 —— 企业级工作流触手可及 🏢</h2>
<h2 align="center" style="margin: 10px 0 10px; font-weight: bold;">🔓 打破授权壁垒，让企业轻松拥有现代工作流 💼</h2>
<h2 align="center" style="margin: 10px 0 10px; font-weight: bold;">🛑 拒绝伪开源，反对加密、加壳、混淆、联网验证等行为 ✅ 让企业产品合规 📄</h2>

## 🌟 随手点个 Star，就是对开源最大的支持！每一颗 ⭐ 都是我们持续打磨 AntFlow 的动力。你的鼓励，会让我走得更远，也让开源走得更远。

我 🐜 常驻上海，白天在国内知名大厂认真搬砖 💻，
晚上加班后、清晨醒来，仍坚持投入 AntFlow 的开发。
开源不易，但我始终相信：好工具值得被分享 💪

### 企业/个人使用登记[登记入口](https://gitee.com/tylerzhou/Antflow/issues/IC07CJ)

> 开发完成的企业或个人务必登记,登记只做开源案例推广使用,不收取任何费用.
>
> 如果AntFlow帮助到了你,也请积极回馈社区,点赞、向朋友介绍、推广、登记案例都是对我们的支持！

<img src="https://img.shields.io/badge/AntFlow可用于个人或者公司项目,禁止二次开源(联系作者获得授权的除外)-注意-red.svg" alt="Downloads">

# Antflow 简介

AntFlow 是一款基于activiti的、久经生产环境考验的、企业级低代码工作流引擎平台。可独立部署，也可以做为模块嵌入到现有系统中。使用简单，易于维护，前端只需要简单点击和选择即可完成流程配置，后端只需要实现一个接口即可新开发出一条业务流程！

## 想要快速上手使用antflow，请拉到最下面找到[学习资源](#studyresource)

| 项目名               | 地址                                                                                                                                                                                   | 描述                                                                                                                                                                                                                       |
| -------------------- | -------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- | -------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| 前端独立设计器地址   | [⭐Gitee仓库](https://gitee.com/ldhnet/AntFlow-Designer)<br />⭐[GitHub仓库](https://github.com/ldhnet/AntFlow-Designer)<br />⭐[GitCode仓库](https://gitcode.com/ldhnet/FlowDesigner/overview) | 项目独立前端设计器地址,主要用于用户只需要集成设计器,代办,已办等列表都自己设计的自定义程度较高场景,<br />目前项目官网demo上部署的是集成ruoyi的,**和后端项目在一个仓库里,**即后端仓库的**antflow-vue**文件夹下面 |
| 开源Java后端项目地址 | [⭐Gitee仓库](https://gitee.com/tylerzhou/Antflow)<br />[⭐Github仓库](https://github.com/mrtylerzhou/AntFlow)<br />[⭐GitCode](https://gitcode.com/zypqqgc/AntFlow/overview)                   | Antflow**后端代码**,其中antflow-vue为官网展示的demo项目,包含了一套去除了后端系统的ruoyi前端                                                                                                                          |
| 开源.net后端项目地址 | ⭐[github地址](https://github.com/mrtylerzhou/AntFlow.net)                                                                                                                                | .net 版和java版共用同一套前端,用户可以使用独立设计器,如果需要和官网demo一样的效果,可以下载后端java仓库,然后取出antflow-vue                                                                                                |
| 独立设计器预览地址   | [预览入口](http://14.103.207.27/ant-flow/dist/#/)                                                                                                                                         | 仅独立设计器的预览地址,独立设计器主要用于前端ui定制化需求较高的场景                                                                                                                                                        |
| 成品案例预览地址     | [预览入口](http://14.103.207.27/admin/)                                                                                                                                                   | 即java后端项目里的antflow-vue项目的预览,用于快速和ruoyi集成                                                                                                                                                                |
| 系统操作手册地址     | [查看入口](https://gitee.com/tylerzhou/Antflow/wikis/)                                                                                                                                    |                                                                                                                                                                                                                            |

- QQ技术交流群（972107977） 期待您的加入
- 有疑问可以Issues留言，我们会认真对待  [issues地址](https://gitee.com/tylerzhou/Antflow/issues)

## Antflow核心特性

+ **🥇🥇全网首创虚拟节点(VNode)模式!将流程流转业务和引擎(Flowable/camunda/activiti)执行api高度分离,零流程引擎知识也可上手开发工作流系统!(有经验的开发者更是如鱼得水,游刃有余,简介最后面有专业书籍推荐)**
+ **🦍🦍虚拟节点是activiti/flowable引擎api无关的、可迁移的（目前迁移的.net版已进入beta阶段）、更加灵活可控的、安全的流程节点（你是否遇到过因为改activiti/flowable某个属性导致报错无法解决或者即便不报错但是行为不符合预期😭😭）。虚拟节点提供更加灵活的控制和更强大的业务表达能力。从而实现更的特定流程引擎api和流转业务设计解耦.让开发者更加聚焦业务开发**
+ **😄😄超级简单的开发模式。使用适配器模式将流程引擎流转业务和用户表单处理业务完全分离。 DIY流程后端只需要实现一个接口即可快速开发上线！低代码流程更是只需要拖拽即可完成流程设计，简单流程实现零代码！！这一切都如此简单，而且不需要流程引擎知识！不需要流程引擎知识！不需要流程引擎知识！**
+ **👨‍👨‍👦‍👦👨‍👨‍👦‍👦完全接管activiti用户系统(activiti自身用户系统太弱了,而且企业都有自身的用户系统设计),企业可以完全忘掉activiti中的用户表,群组表,成员关系表等等.接入企业现有系统中的用户,角色系统,这一切都非常容易!**
+ **🚩🚩允许运行时定义节点.彻底满足中国式办公特点! 安全实现流程串行、并行、会签、或签，顺序会签、审批人去重、加批、委托、转办、退回任意节点、动态跳过节点、变更处理人，版本迁移，低代码设计等等功能。**
+ **💻💻流程预览图、流程审批路径都是Json数据，而非图片流！一切皆Json！可自定义视觉渲染风格，以适应不同风格的系统。**

> 以上多多次提到不需要流程引擎知识，主要是想强调使用Antflow即便没有专业的流程引擎知识也可以快速上手开发流程。 如果想要二开流程引擎知识仍然是必不可少的。二开指的是更改流程引擎核心源代码从而改变引擎的默认行为以适应企业特殊定制需求。Antflow本身提供了强大的审批人规则、审批条件规则等丰富的定制api，已经能满足95%以上场景，这些叫扩展，不叫二开，也是不需要流程引擎专业知识的

# Antflow核心技术栈

* Java8-21 (master分支为java8版本,如果使用较新java版本,请切换到java17_support分支)
* Activiti（fork了activiti源码，并进行了魔改，⚠⚠用户请使用antflow源码中提供的sql进行建表，不要使用activiti自动创建表功能）
* Spring Boot 2.7.17
* MybatisPlus 3.5.1
* MySql 5.7+

## Antflow架构图

![系统架构图](./doc/images/AntFlow架构.png)

## 项目截图

![1.png](./doc/images/1.png)

![](./doc/images/2.png)

![](./doc/images/3.png)

![](./doc/images/4.png)

![](./doc/images/5.png)

![](./doc/images/6.png)

## 快速开始

## 前端运行

### Nodejs V16.20.0 及以上版本

```
# gitee克隆项目
git clone https://gitee.com/tylerzhou/Antflow.git 

# 进入项目目录
cd Antflow/antflow-vue

# 安装依赖
npm  install  --registry=https://registry.npmmirror.com

# 启动服务
npm run dev

# 构建测试环境 npm run build:stage
# 构建生产环境 npm run build:prod
# 前端访问地址 http://localhost:80
```

## 后端运行

1. 项目下载 git clone https://gitee.com/tylerzhou/Antflow.git
   > master分支对应java8，java17_support分支对应java17
   >
2. 打开 **antflow-web模块**下resources目录下的**application-dev.properties**文件,修改数据库连接信息,新建一个名为**antflow**的数据库
3. 打开scripts文件夹,执行里面的**act_init_db.sql**和**bpm_init_db.sql以及bpm_init_db_data.sql（生产环境不需要执行bpm_init_db_data.sql里面的sql，都是demo数据，方便poc使用）**
4. 启动项目,如果一切顺利,这时候后端就启动起来了

<a id="studyresource"/>

## [学习资源]()

1. [官网首页](http://antflow.top/admin/#/index) 查看**操作手册-低代码表单**和**操作手册-自定义表单**
2. 快速接入SaaS流程（外部API调用方式接入），请查看[官方飞快文档](https://pcn3ojogrp79.feishu.cn/wiki/VO07wArXCibz5Jkvzejcn8CTn8f) 5.3节
3. **快速将antflow集成到已有系统**，并对接已有系统的用户、角色、组织构架请查看[gitee wiki页](https://gitee.com/tylerzhou/Antflow/wikis/pages) 之快速上手篇
4. 如何想要对前端设计器进行修改，改为vue2或者react技术栈，请查看gitee wiki页面里的antflow虚拟节点关键字段说明。
5. **想要全面详细了解antflow**，可以查看项目开源仓库里面docs目录下的20余篇文档（之所以不放在wiki里面是因为gitee wiki mermaid插件太老，无法正常查看，最好下载到本地看）
6. antflow是开源免费的，捐赠自愿不强求。如果你感觉我们做的还可以，**请给一颗星[⭐](https://gitee.com/ldhnet/AntFlow-Designer)支持一下**，这是对我们开源的极大鼓励。
7. 学习使用中有疑问，想要和其它用户交流，加antflow官方qq群（**972107977**)
8. 想要了解和三方系统集成案例？请查看**官方ruoyi集成版**：[若依灵犀](https://gitee.com/ruoyimate/ruoyimate)Ruoyi-Mate(目前尚在持续完善中，后面也会一直迭代，完善流程快速开发以及集成开箱即用的、生产可靠的三方组件)
9. 使用中遇到问题？可能其它用户也遇到了，常见问题请查看,[常见问题列表](https://gitee.com/tylerzhou/Antflow/issues/IC0J4Y)
10. 我如何说服老板使用Antflow？请查看[企业登记](https://gitee.com/tylerzhou/Antflow/issues/IC07CJ)

## 生态项目

| 项目名                    | 项目地址                                       | 介绍                                                                                                                                 |
| ------------------------- | ---------------------------------------------- | ------------------------------------------------------------------------------------------------------------------------------------ |
| 若依灵犀（Ruoyi-Mate)     | [gitee地址](https://gitee.com/ruoyimate)          | 目前正在制作中，已集成Antflow后端，前端集成正在制作中。后期会扩展出更多关于工作流的实现功能以及增强快速开发，让RuoYi成为企业开发利器 |
| 蚂蚁报表（AntReport）     | [gitee地址](https://gitee.com/antswarm/antreport) | 基于Urepotr2的企业级报表工具，目前将Ureport2改造成springboot项目，方便快速上手。后期逐步升级过于老旧的包，以及将前端改造成vue3项目   |
| AntFlow与企业已经系统集成 |                                                | 参考[学习资源](#studyresource) 中第三节，如果想要wiki中的demo文件可以加入qq群从群文件中获取                                             |

## 💬 关于工作流集成服务和的小说明

我们发现，许多优秀的开源项目虽然免费实用，但缺乏易用的工作流平台，或者需要购买商业版才能使用完整的流程引擎功能。

笔者有近十年国内头部物流快递行业开发经验，积累了大量使用Oracle数据、Hbase数据以及Mongodb的使用经验。对Oracle和oracle系的国产数据库也有相当经验。如果您的项目

如果你希望将 AntFlow 集成到你的项目中，但又不想花费大量时间精力自行研究，我们可以提供一对一的集成服务 ——
具体细节欢迎加入 QQ 群联系群主私聊。

---


##  **AntFlow 企业级扩展服务**

### **高性能数据层支持**

AntFlow 基于 Activiti 引擎深度定制开发，通过架构级优化突破传统关系型数据库局限。我们提供：

* **海量数据场景** ：完整支持 MongoDB 及其他主流 NoSQL 数据库，实现横向扩展能力与性能加速
* **信创生态兼容** ：全面适配国产信创数据库体系，包括达梦、人大金仓、南大通用、GaussDB、OceanBase、PolarDB 等全系列产品

### **核心价值优势**

区别于市场常见的兼容层伪装方案（如模拟 Oracle/PG 协议），我们提供 **源码级深度改造** ，确保：

* 100% 技术可控，符合信创合规要求
* 原生性能表现，无兼容层性能损耗
* 数据库特性完全发挥，支持分布式、高可用等企业级特性

### **企业级服务保障**

如果你不想自己折腾,如需快速实现生产环境部署并获得持续技术支持，请联系项目作者获取专业咨询服务。我们提供从架构设计到落地实施的全周期保障，助您无忧构建企业级流程平台。

## ❤️ 我们的承诺

- ✅ 仅针对用户需求提供集成服务，不对外开源，不“套壳”其他项目
- ✅ 我们已就[「若依灵犀 RuoYi-Mate」](https://gitee.com/ruoyimate/ruoyimate)项目的集成，与原作者沟通并获得许可（包括项目名、Logo等）,然后开源出来，我们尊重其它开源作者。
- ✅ 不在其他开源项目中强行推广 AntFlow，尊重原作者的设计与生态
- ✅ 认同并支持开源作者探索合理的商业模式，**我们不会也不愿做“断他人财路”的事**
- ✅ 再次重申：AntFlow将始终保持开源免费！集成服务仅针对想要将antflow集成到其它系统又不想花时间折腾的用户。Antflow本身以及底层修改的activiti引擎源码均是开放的。

---

🔧 AntFlow 是我们用心打造的一款现代化工作流引擎，
我们希望它不仅能帮助你提升效率，更能在开源道路上走得更远。

🙏 如果你觉得我们做得还不错，也请随手点个 ⭐Star⭐，给我们更多动力！

## 捐赠支持

😀 你可以请作者喝杯咖啡表示鼓励

- 有捐赠的小伙伴（金额不限）可以联系作者领取一份 **独家提升开发技能的文档**
- 加QQ574427343或者邮件到 574427343@qq.com邮箱 *注明 领取开发技能提升文档*
- 文档宗旨在于提升初级、中级、高级等一线开发人员的编码意识，讲述**什么是好代码**，好代码的标准，**如何编写高质量代码**等

### 💕 捐赠列表（按时间排序,不区分java版还是.net版,两个仓库一并致谢）

```
琉璃 100元、胡不归 66元、*敏 6.6元、w*g 20元、t*n8.88、*原8.8、*楠20元、U*m20元、渣渣辉6.66元、付*旗88元、幻20元、*成50元、*豪10元、X*u 5元、s*i20元、 *虫20元、 *风6.66元、
小郑 30元、高宇 20元、*门 88元、平安喜乐 1元、GxpCode 100元、*十 50元、
不爱吃的白萝卜 10元、汪总 100元+3元、十三 100元(.net版首捐⭐️)、SZ1806 188元,
ゞ低调℡华丽 100元,Dorian 8.88元,小桥流水 6.6元,杨章隐 88元 首捐⭐️，大虫子88元
```

<table>
    <tr>
        <td><img src="https://gitee.com/ldhnet/AntFlow-Designer/raw/master/public/images/wxpay.jpg"/></td>
        <td><img src="https://gitee.com/ldhnet/AntFlow-Designer/raw/master/public/images/alipay.jpg"/></td>
    </tr>  
</table>

## 好书推荐

大家在使用本项目时，推荐结合贺波老师的书
[《深入Flowable流程引擎：核心原理与高阶实战》](https://item.jd.com/14804836.html)学习。这本书得到了Flowable创始人Tijs Rademakers亲笔作序推荐，对系统学习和深入掌握Flowable的用法非常有帮助。

> flowable源于activiti,很多核心表,核心api和设计模式都是一样的.读flowable的书同样也可以用在activiti上

![图书image](./doc/images/flowablebook.jpg)