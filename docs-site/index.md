---
layout: home

hero:
  name: AntFlow
  text: 企业级低代码工作流引擎
  tagline: 基于 Activiti 的开源工作流平台 · 虚拟节点模式 · 零流程引擎知识即可上手
  image:
    src: /logo.png
    alt: AntFlow
  actions:
    - theme: brand
      text: 快速开始
      link: /guide/quick-start
    - theme: alt
      text: 什么是 AntFlow
      link: /guide/introduction
    - theme: alt
      text: Gitee 仓库
      link: https://gitee.com/tylerzhou/Antflow

features:
  - icon: 🥇
    title: 虚拟节点(VNode)模式
    details: 全网首创,将流程流转业务和引擎执行 API 高度分离。零流程引擎知识也可上手开发工作流系统,有经验的开发者更是如鱼得水。
    link: /dev-guide/vnode-system
    linkText: 了解虚拟节点 →

  - icon: 😄
    title: 超级简单的开发模式
    details: 使用适配器模式将流程引擎流转业务和用户表单处理业务完全分离。DIY 流程后端只需实现一个接口即可快速上线,低代码流程拖拽即可完成。
    link: /dev-guide/adaptor-pattern
    linkText: 了解 Adaptor 模式 →

  - icon: 🚩
    title: 中国式办公全支持
    details: 串行、并行、会签、或签、顺序会签、审批人去重、加批、委托、转办、退回任意节点、动态跳过节点、变更处理人、版本迁移等全部支持。
    link: /dev-guide/flow-control
    linkText: 了解流程控制 →

  - icon: 👨‍👨‍👦‍👦
    title: 完全接管用户系统
    details: 忘掉 Activiti 的用户表、群组表、成员关系表。接入企业现有系统的用户、角色系统,只需实现一个接口即可。
    link: /dev-guide/extend-approver
    linkText: 了解用户接入 →

  - icon: 💻
    title: 12 种审批人规则
    details: 指定人员、角色、直属领导、部门负责人、HRBP、层层审批、指定层级、发起人自选、表单关联、自定义规则等 12 种内置审批人来源。
    link: /workflow-design/approver-rules
    linkText: 查看审批人规则 →

  - icon: 🔧
    title: 条件规则引擎
    details: 基于 ConditionJudge 策略模式的双层 AND/OR 条件评估。支持低代码字段、业务字段、JUEL/SpEL 表达式等多种条件类型。
    link: /workflow-design/condition-rules
    linkText: 查看条件规则 →

  - icon: 📋
    title: 低代码表单引擎
    details: 集成 vform 设计器,拖拽即可完成表单设计。支持内联表单和外部表单两种模式,字段级权限控制(R/E/H)。
    link: /lowcode/lowcode-form
    linkText: 了解低代码表单 →

  - icon: 🔗
    title: 三方系统接入
    details: 提供 Open API 和嵌入式两种接入方式。支持审批人模板、条件模板、回调通知,完全满足 SaaS 多租户场景。
    link: /dev-guide/integrate-existing
    linkText: 了解系统集成 →

  - icon: 🗄️
    title: 多数据库支持
    details: MySQL、PostgreSQL、Oracle、SQL Server、达梦、人大金仓、南大通用、OceanBase、高斯、PolarDB、MongoDB 等 12+ 数据库。
    link: /ops/db-multi
    linkText: 查看数据库支持 →

  - icon: 📦
    title: Spring Boot Starter
    details: 引入一个依赖即可集成全套 AntFlow。自动装配 Mapper、组件扫描、引擎配置,开箱即用。
    link: /dev-guide/integrate-existing
    linkText: 了解 Starter 用法 →

  - icon: 🎨
    title: 现代化前端
    details: Vue 3 + Vite + Element Plus + Pinia。流程预览图、审批路径都是 JSON 数据,可自定义视觉渲染风格。
    link: /guide/overview
    linkText: 查看系统总览 →

  - icon: 🔓
    title: 真开源可商用
    details: Apache 2.0 协议,拒绝伪开源、反对加密加壳混淆。可用于个人或公司项目,免费商用。
    link: https://gitee.com/tylerzhou/Antflow
    linkText: 查看许可证 →
---
