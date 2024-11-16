
<p align="center" style="margin-bottom: 0 !important">
	<img alt="logo" src="./doc/images/logo.png"  width = 25%; height=25%;>
</p>

# 简介
AntFlow 是一款基于activiti的、✈久经生产环境考验的、企业级低代码工作流引擎平台。可独立部署，也可以做为模块嵌入到现有系统中。使用简单，易于维护，前端只需要简单点击和选择即可完成流程配置，后端只需要实现一个接口即可新开发出一条业务流程！

# 系统架构图
![系统架构图](./doc/images/AntFlow架构.png)

# 界面一览
-  🚦**本项目是后端，前端完整功能请移步** [gitee地址](https://gitee.com/ldhnet/AntFlow-Vue3)
- 🚦集成ruoyi演示项目 [http://117.72.70.166/admin/](http://117.72.70.166/admin/)
> 前端使用了ruoyi进行功能展示,后端暂无集成ruoyi(目前是纯粹的、独立的、可嵌入已有系统的工作流引擎，后续会做一个基于ruoyi的admin系统）
![1.png](./doc/images/1.png)

添加节点

![](./doc/images/4.png)

属性配置面板

![](./doc/images/2.png)

审批节点属性面板

![](./doc/images/搜狗截图20240818082058.png)

参数校验
![](./doc/images/3.png)

管理界面

![](./doc/images/QQ20240818-082212.png)

# 核心特性：
1. **开源,完全免费**,没有任何收费功能,无付费引导。(如果您的企业使用了,麻烦让作者知道,帮助作者推广开源项目,作者也会帮您进行技术支持,帮您在企业快速落地)

2. 引擎遵守高内聚低耦合设计理念,将流程引擎和审批业务完全分离,流程引擎业务不会入侵到业务里,只需要关注业务流转逻辑编写,极大降低入门门槛,**开发超级简单,只需要实现一个接口即可完成一个流程开发**

3. 流程设计简单,流程设计器用户友好，人人可用：AntFlow提供了一个简洁的流程设计器，摒弃了传统设计工具的复杂性，使得用户能够直观、轻松地设计和管理工作流程。

4. 流程引擎和用户角色系统解耦,可以较方便的接入用户系统现有的用户角色系统,**快速集成到现有业务系统中**

5. 流程可调试:AntFlow 提供了一个管理流程调试界面，同样不需要程序员介入，流程管理员便可通过流程调试界面解决大部分日常常见流程问题。把程序员从繁琐的重复的日常小问题排查中解放出来专注于解决更有价值的问题。

6. 引擎完全透明化，流程所有节点、任意属性都是可追溯的，出了问题有据可依,心里有底.

7. 久经生产检验的：AntFlow经历了多个版本的迭代更新.在某大中型客服公司、某中型互联网公司、某大型快递公司落地使用，经受住了复杂业务场景海量数据压力的考验。

# 核心技术栈

* Java8-21 (main分支为java8版本,如果使用较新java版本,请切换到java17_support分支)
* Activiti（fork了activiti5源码，并进行了魔改，⚠⚠用户请使用antflow源码中提供的sql进行建表，不要使用activiti自动创建表功能）
* Spring Boot 2.7.17
* MybatisPlus 3.5.1
* MySql 5.7+


# 快速开始

1. git clone 本项目
2. 打开 **web模块**下resources目录下的**resources**目录,修改数据库连接信息,新建一个名为**antflow**的数据库
3. 打开里面**web**模块resources目录下的scripts文件夹,执行里面的**act_init_db.sql**和**bpm_init_db.sql**
4. 启动项目,如果一切顺利,这时候后端就启动起来了
5. [前端设计器地址](https://gitee.com/ldhnet/AntFlow-Vue3)
6. 前端ruoyi演示项目 [http://117.72.70.166/admin/](http://117.72.70.166/admin/)

##  捐赠支持
😀 你可以请作者喝杯咖啡表示鼓励
<table>
    <tr>
        <td><img src="https://gitee.com/ldhnet/AntFlow-Vue3/raw/master/public/images/wxpay.jpg"/></td>
        <td><img src="https://gitee.com/ldhnet/AntFlow-Vue3/raw/master/public/images/alipay.jpg"/></td>
    </tr>  
</table>
## 好书推荐
大家在使用本项目时，推荐结合贺波老师的书
[《深入Flowable流程引擎：核心原理与高阶实战》](https://item.jd.com/14804836.html)学习。这本书得到了Flowable创始人Tijs Rademakers亲笔作序推荐，对系统学习和深入掌握Flowable的用法非常有帮助。

> flowable源于activiti,很多核心表,核心api和设计模式都是一样的.读flowable的书同样也可以用在activiti上

![图书image](./doc/images/flowablebook.jpg)
