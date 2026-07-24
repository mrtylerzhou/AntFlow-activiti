# 低代码流程总览

> AntFlow 的低代码(Low-Code)能力是其核心卖点之一:业务方通过**拖拽式表单设计器 + 流程设计器**即可完成完整的工作流配置,无需编写任何后端代码即可上线业务流程。本章从架构、能力、与 DIY 模式的差异三方面剖析 AntFlow 低代码体系。

## 低代码在 AntFlow 中的定位

AntFlow 同时支持两种流程开发模式:

| 模式 | 入口 | 是否需要编码 | 适用场景 |
|---|---|:---:|---|
| **DIY 模式**(Do It Yourself) | `流程类型` → 自定义表单流程 | 需要 | 表单逻辑复杂、需调用企业内部 API、表单字段需联动计算 |
| **LF 模式**(Low-Code Form) | `流程类型` → 低代码表单流程 | **不需要** | 通用审批场景(请假、报销、用车、领用等)、POC、SaaS 多租户 |

低代码模式的本质是:**用 VForm3 表单 JSON 替代业务表 + 用通用服务 `LowFlowApprovalService` 替代业务 Service**。

<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 920 380" font-family="-apple-system, BlinkMacSystemFont, 'Segoe UI', sans-serif">
  <defs>
    <marker id="arrL1" viewBox="0 0 10 10" refX="9" refY="5" markerWidth="6" markerHeight="6" orient="auto"><path d="M0,0 L10,5 L0,10 z" fill="#475569"/></marker>
    <linearGradient id="lfG1" x1="0" y1="0" x2="0" y2="1"><stop offset="0%" stop-color="#dbeafe"/><stop offset="100%" stop-color="#bfdbfe"/></linearGradient>
    <linearGradient id="lfG2" x1="0" y1="0" x2="0" y2="1"><stop offset="0%" stop-color="#dcfce7"/><stop offset="100%" stop-color="#bbf7d0"/></linearGradient>
    <linearGradient id="lfG3" x1="0" y1="0" x2="0" y2="1"><stop offset="0%" stop-color="#fef3c7"/><stop offset="100%" stop-color="#fde68a"/></linearGradient>
    <linearGradient id="lfG4" x1="0" y1="0" x2="0" y2="1"><stop offset="0%" stop-color="#fce7f3"/><stop offset="100%" stop-color="#fbcfe8"/></linearGradient>
  </defs>

  <!-- 设计期 -->
  <rect x="20" y="20" width="430" height="340" rx="10" fill="url(#lfG1)" stroke="#3b82f6" stroke-width="2"/>
  <text x="235" y="46" text-anchor="middle" font-size="14" font-weight="700" fill="#1e40af">设计期(零代码)</text>

  <rect x="40" y="68" width="390" height="56" rx="6" fill="#fff" stroke="#3b82f6"/>
  <text x="60" y="86" font-size="12" font-weight="600" fill="#1e3a8a">① VForm3 表单设计器</text>
  <text x="60" y="104" font-size="10" fill="#475569">拖拽控件生成表单 JSON</text>
  <text x="60" y="118" font-size="10" fill="#475569">输出: { formdata: "..."(VForm3 JSON) }</text>

  <rect x="40" y="136" width="390" height="56" rx="6" fill="#fff" stroke="#3b82f6"/>
  <text x="60" y="154" font-size="12" font-weight="600" fill="#1e3a8a">② AntFlow 流程设计器</text>
  <text x="60" y="172" font-size="10" fill="#475569">配置审批人/条件/抄送节点</text>
  <text x="60" y="186" font-size="10" fill="#475569">字段权限:R(只读)/E(可编辑)/H(隐藏)</text>

  <rect x="40" y="204" width="390" height="56" rx="6" fill="#fff" stroke="#3b82f6"/>
  <text x="60" y="222" font-size="12" font-weight="600" fill="#1e3a8a">③ 一键发布</text>
  <text x="60" y="240" font-size="10" fill="#475569">生成 formCode(LFFM-00001)+ 流程版本</text>
  <text x="60" y="254" font-size="10" fill="#475569">写入:t_bpmn_conf_lf_formdata + t_bpmn_conf</text>

  <rect x="40" y="272" width="390" height="68" rx="6" fill="#1e3a8a" stroke="#1e40af"/>
  <text x="235" y="296" text-anchor="middle" font-size="12" font-weight="700" fill="#fff">零 SQL · 零 Java · 零 Vue</text>
  <text x="235" y="316" text-anchor="middle" font-size="11" fill="#dbeafe">业务人员/产品经理也能配置流程</text>
  <text x="235" y="332" text-anchor="middle" font-size="10" fill="#bfdbfe">平均配置周期:从 2 周降至 30 分钟</text>

  <!-- 运行期 -->
  <rect x="470" y="20" width="430" height="340" rx="10" fill="url(#lfG2)" stroke="#16a34a" stroke-width="2"/>
  <text x="685" y="46" text-anchor="middle" font-size="14" font-weight="700" fill="#155e2f">运行期(通用引擎)</text>

  <rect x="490" y="68" width="390" height="56" rx="6" fill="#fff" stroke="#16a34a"/>
  <text x="510" y="86" font-size="12" font-weight="600" fill="#155e2f">LowFlowApprovalService</text>
  <text x="510" y="104" font-size="10" fill="#475569">@ActivitiServiceAnno(svcName = LOWFLOW_FORM_CODE)</text>
  <text x="510" y="118" font-size="10" fill="#475569">submitData / queryData / consentData / launchParameters</text>

  <rect x="490" y="136" width="390" height="56" rx="6" fill="#fff" stroke="#16a34a"/>
  <text x="510" y="154" font-size="12" font-weight="600" fill="#155e2f">LFFormDataPreProcessor</text>
  <text x="510" y="172" font-size="10" fill="#475569">preWriteProcess:保存流程配置时拆字段</text>
  <text x="510" y="186" font-size="10" fill="#475569">preReadProcess:读取时合并字段元数据</text>

  <rect x="490" y="204" width="390" height="56" rx="6" fill="#fff" stroke="#16a34a"/>
  <text x="510" y="222" font-size="12" font-weight="600" fill="#155e2f">LFFormDataRuntimeHelper</text>
  <text x="510" y="240" font-size="10" fill="#475569">populateLfConditions:提取条件字段</text>
  <text x="510" y="254" font-size="10" fill="#475569">processFormRelatedUserConf:表单上下文取人</text>

  <rect x="490" y="272" width="390" height="68" rx="6" fill="#155e2f" stroke="#166534"/>
  <text x="685" y="296" text-anchor="middle" font-size="12" font-weight="700" fill="#fff">运行期统一服务</text>
  <text x="685" y="316" text-anchor="middle" font-size="11" fill="#dcfce7">所有 LF 流程共享同一套引擎实现</text>
  <text x="685" y="332" text-anchor="middle" font-size="10" fill="#bbf7d0">通过 formCode 路由到具体业务流程</text>

  <!-- 设计->运行 箭头 -->
  <line x1="450" y1="190" x2="470" y2="190" stroke="#475569" stroke-width="3" marker-end="url(#arrL1)"/>
  <text x="460" y="184" text-anchor="middle" font-size="10" fill="#475569" font-weight="600">发布</text>
</svg>

## 低代码能力全景

<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 920 460" font-family="-apple-system, BlinkMacSystemFont, 'Segoe UI', sans-serif">
  <defs>
    <linearGradient id="cap1" x1="0" y1="0" x2="0" y2="1"><stop offset="0%" stop-color="#fef3c7"/><stop offset="100%" stop-color="#fde68a"/></linearGradient>
    <linearGradient id="cap2" x1="0" y1="0" x2="0" y2="1"><stop offset="0%" stop-color="#dbeafe"/><stop offset="100%" stop-color="#bfdbfe"/></linearGradient>
    <linearGradient id="cap3" x1="0" y1="0" x2="0" y2="1"><stop offset="0%" stop-color="#dcfce7"/><stop offset="100%" stop-color="#bbf7d0"/></linearGradient>
    <linearGradient id="cap4" x1="0" y1="0" x2="0" y2="1"><stop offset="0%" stop-color="#fce7f3"/><stop offset="100%" stop-color="#fbcfe8"/></linearGradient>
  </defs>

  <text x="460" y="24" text-anchor="middle" font-size="16" font-weight="700" fill="#0f172a">AntFlow 低代码能力矩阵</text>

  <!-- 表单能力 -->
  <rect x="20" y="50" width="220" height="180" rx="10" fill="url(#cap1)" stroke="#d97706" stroke-width="2"/>
  <text x="130" y="76" text-anchor="middle" font-size="13" font-weight="700" fill="#92400e">表单设计</text>
  <text x="40" y="100" font-size="11" font-weight="600" fill="#78350f">支持控件</text>
  <text x="40" y="118" font-size="10" fill="#78350f">• 单行文本/多行文本/数字</text>
  <text x="40" y="134" font-size="10" fill="#78350f">• 日期/日期时间/时间范围</text>
  <text x="40" y="150" font-size="10" fill="#78350f">• 单选/多选/下拉/级联</text>
  <text x="40" y="166" font-size="10" fill="#78350f">• 树形选择/开关/滑块/评分</text>
  <text x="40" y="182" font-size="10" fill="#78350f">• 图片上传/文件上传/图标</text>
  <text x="40" y="198" font-size="10" fill="#78350f">• 富文本编辑器/颜色选择器</text>
  <text x="40" y="216" font-size="10" fill="#78350f">• 容器:栅格/标签页/子表单</text>

  <!-- 流程能力 -->
  <rect x="260" y="50" width="220" height="180" rx="10" fill="url(#cap2)" stroke="#3b82f6" stroke-width="2"/>
  <text x="370" y="76" text-anchor="middle" font-size="13" font-weight="700" fill="#1e40af">流程设计</text>
  <text x="280" y="100" font-size="11" font-weight="600" fill="#1e3a8a">支持节点</text>
  <text x="280" y="118" font-size="10" fill="#1e3a8a">• 发起人 + 字段权限配置</text>
  <text x="280" y="134" font-size="10" fill="#1e3a8a">• 审批人(15 种来源规则)</text>
  <text x="280" y="150" font-size="10" fill="#1e3a8a">• 条件节点(规则组 OR/AND)</text>
  <text x="280" y="166" font-size="10" fill="#1e3a8a">• 抄送(普通 V1/V2 + 条件抄送)</text>
  <text x="280" y="182" font-size="10" fill="#1e3a8a">• 并行网关(会签/或签)</text>
  <text x="280" y="198" font-size="10" fill="#1e3a8a">• 自动节点(条件满足自动完成)</text>
  <text x="280" y="216" font-size="10" fill="#1e3a8a">• 条件审批(条件失败转人工)</text>

  <!-- 数据能力 -->
  <rect x="500" y="50" width="220" height="180" rx="10" fill="url(#cap3)" stroke="#16a34a" stroke-width="2"/>
  <text x="610" y="76" text-anchor="middle" font-size="13" font-weight="700" fill="#155e2f">数据存储</text>
  <text x="520" y="100" font-size="11" font-weight="600" fill="#14532d">存储结构</text>
  <text x="520" y="118" font-size="10" fill="#14532d">• 设计期:t_bpmn_conf_lf_formdata</text>
  <text x="520" y="134" font-size="10" fill="#14532d">• 字段元数据:_field 表</text>
  <text x="520" y="150" font-size="10" fill="#14532d">• 节点权限:_field_control 表</text>
  <text x="520" y="166" font-size="10" fill="#14532d">• 运行期:t_lf_main + t_lf_main_field</text>
  <text x="520" y="182" font-size="10" fill="#14532d">• 7 种字段类型(STRING/NUMBER/</text>
  <text x="520" y="198" font-size="10" fill="#14532d">  DATE/DATE_TIME/TEXT/BOOLEAN/BLOB)</text>
  <text x="520" y="216" font-size="10" fill="#14532d">• 支持水平分表(海量数据)</text>

  <!-- 扩展能力 -->
  <rect x="740" y="50" width="160" height="180" rx="10" fill="url(#cap4)" stroke="#db2777" stroke-width="2"/>
  <text x="820" y="76" text-anchor="middle" font-size="13" font-weight="700" fill="#9d174d">扩展点</text>
  <text x="760" y="100" font-size="11" font-weight="600" fill="#831843">SPI 接口</text>
  <text x="760" y="118" font-size="10" fill="#831843">• LFFormOperationAdaptor</text>
  <text x="760" y="134" font-size="10" fill="#831843">  (per-formCode 定制)</text>
  <text x="760" y="150" font-size="10" fill="#831843">• 自定义审批人规则</text>
  <text x="760" y="166" font-size="10" fill="#831843">• 自定义条件规则</text>
  <text x="760" y="182" font-size="10" fill="#831843">• 自定义通知渠道</text>
  <text x="760" y="198" font-size="10" fill="#831843">• Hook:提交前/审批后</text>
  <text x="760" y="216" font-size="10" fill="#831843">• 多表单模式(N 个表单)</text>

  <!-- 运行能力 -->
  <rect x="20" y="250" width="880" height="190" rx="10" fill="#f1f5f9" stroke="#475569" stroke-width="2"/>
  <text x="460" y="276" text-anchor="middle" font-size="13" font-weight="700" fill="#1e293b">运行期中国式办公能力(低代码同样支持)</text>

  <text x="40" y="304" font-size="11" font-weight="600" fill="#1e293b">流转控制</text>
  <text x="40" y="322" font-size="10" fill="#475569">• 串行/并行/会签/或签/顺序会签</text>
  <text x="40" y="338" font-size="10" fill="#475569">• 加批/委托/转办</text>
  <text x="40" y="354" font-size="10" fill="#475569">• 退回到任意历史节点</text>
  <text x="40" y="370" font-size="10" fill="#475569">• 撤回(发起人主动撤销)</text>
  <text x="40" y="386" font-size="10" fill="#475569">• 变更审批人(管理员干预)</text>
  <text x="40" y="402" font-size="10" fill="#475569">• 加签/减签(动态调整)</text>
  <text x="40" y="418" font-size="10" fill="#475569">• 跳过节点(条件满足自动跳)</text>

  <text x="320" y="304" font-size="11" font-weight="600" fill="#1e293b">版本管理</text>
  <text x="320" y="322" font-size="10" fill="#475569">• 流程多版本共存</text>
  <text x="320" y="338" font-size="10" fill="#475569">• 同族仅一个生效版本</text>
  <text x="320" y="354" font-size="10" fill="#475569">• 运行中实例锁定版本</text>
  <text x="320" y="370" font-size="10" fill="#475569">• 版本迁移(管理员)</text>
  <text x="320" y="386" font-size="10" fill="#475569">• 表单版本独立管理</text>
  <text x="320" y="402" font-size="10" fill="#475569">• 软删表单仍可被引用读取</text>
  <text x="320" y="418" font-size="10" fill="#475569">• 引用关系实时统计</text>

  <text x="600" y="304" font-size="11" font-weight="600" fill="#1e293b">集成能力</text>
  <text x="600" y="322" font-size="10" fill="#475569">• REST API 完整覆盖</text>
  <text x="600" y="338" font-size="10" fill="#475569">• Spring Boot Starter 一行集成</text>
  <text x="600" y="354" font-size="10" fill="#475569">• 多数据库支持(12+ 种)</text>
  <text x="600" y="370" font-size="10" fill="#475569">• 多租户(SaaS 模式)</text>
  <text x="600" y="386" font-size="10" fill="#475569">• 消息多通道(邮件/短信/钉钉)</text>
  <text x="600" y="402" font-size="10" fill="#475569">• 第三方 Open API(回调机制)</text>
  <text x="600" y="418" font-size="10" fill="#475569">• 低代码 + DIY 混合部署</text>
</svg>

## 与 DIY 模式的关系

低代码模式并非"阉割版 DIY",而是一种**通用化封装**。两者关系如下:

<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 920 320" font-family="-apple-system, BlinkMacSystemFont, 'Segoe UI', sans-serif">
  <defs>
    <linearGradient id="cmpG1" x1="0" y1="0" x2="0" y2="1"><stop offset="0%" stop-color="#dbeafe"/><stop offset="100%" stop-color="#bfdbfe"/></linearGradient>
    <linearGradient id="cmpG2" x1="0" y1="0" x2="0" y2="1"><stop offset="0%" stop-color="#dcfce7"/><stop offset="100%" stop-color="#bbf7d0"/></linearGradient>
    <linearGradient id="cmpG3" x1="0" y1="0" x2="0" y2="1"><stop offset="0%" stop-color="#fef3c7"/><stop offset="100%" stop-color="#fde68a"/></linearGradient>
  </defs>

  <!-- DIY 模式 -->
  <rect x="20" y="20" width="280" height="280" rx="10" fill="url(#cmpG1)" stroke="#3b82f6" stroke-width="2"/>
  <text x="160" y="48" text-anchor="middle" font-size="14" font-weight="700" fill="#1e40af">DIY 模式(自定义)</text>
  <text x="40" y="76" font-size="11" font-weight="600" fill="#1e3a8a">业务方需自己实现</text>
  <text x="40" y="96" font-size="10" fill="#1e3a8a">1. 业务表 SQL(Domain)</text>
  <text x="40" y="114" font-size="10" fill="#1e3a8a">2. Mapper + Service + Controller</text>
  <text x="40" y="132" font-size="10" fill="#1e3a8a">3. Vue 表单组件 .vue 文件</text>
  <text x="40" y="150" font-size="10" fill="#1e3a8a">4. FormOperationAdaptor 实现</text>
  <text x="40" y="168" font-size="10" fill="#1e3a8a">5. @ActivitiServiceAnno 注册</text>
  <text x="40" y="186" font-size="10" fill="#1e3a8a">6. bizFormMaps 路由配置</text>

  <rect x="40" y="206" width="240" height="76" rx="6" fill="#1e3a8a" stroke="#1e40af"/>
  <text x="160" y="228" text-anchor="middle" font-size="11" font-weight="700" fill="#fff">优势</text>
  <text x="160" y="248" text-anchor="middle" font-size="10" fill="#dbeafe">完全自定义、可调用</text>
  <text x="160" y="262" text-anchor="middle" font-size="10" fill="#dbeafe">企业内部 API、复杂表单逻辑</text>
  <text x="160" y="276" text-anchor="middle" font-size="10" fill="#dbeafe">字段联动/计算/异步加载</text>

  <!-- LF 模式 -->
  <rect x="320" y="20" width="280" height="280" rx="10" fill="url(#cmpG2)" stroke="#16a34a" stroke-width="2"/>
  <text x="460" y="48" text-anchor="middle" font-size="14" font-weight="700" fill="#155e2f">LF 模式(低代码)</text>
  <text x="340" y="76" font-size="11" font-weight="600" fill="#14532d">业务方零编码</text>
  <text x="340" y="96" font-size="10" fill="#14532d">1. VForm3 设计器拖拽表单</text>
  <text x="340" y="114" font-size="10" fill="#14532d">2. 配置审批人/条件节点</text>
  <text x="340" y="132" font-size="10" fill="#14532d">3. 配置字段权限 R/E/H</text>
  <text x="340" y="150" font-size="10" fill="#14532d">4. 一键发布</text>
  <text x="340" y="168" font-size="10" fill="#14532d">5. 完成</text>
  <text x="340" y="186" font-size="10" fill="#94a3b8" font-style="italic">(无需步骤 6)</text>

  <rect x="340" y="206" width="240" height="76" rx="6" fill="#155e2f" stroke="#166534"/>
  <text x="460" y="228" text-anchor="middle" font-size="11" font-weight="700" fill="#fff">优势</text>
  <text x="460" y="248" text-anchor="middle" font-size="10" fill="#dcfce7">零代码上手快</text>
  <text x="460" y="262" text-anchor="middle" font-size="10" fill="#dcfce7">业务人员也能配置</text>
  <text x="460" y="276" text-anchor="middle" font-size="10" fill="#dcfce7">适合 SaaS 多租户、POC</text>

  <!-- 桥接 -->
  <rect x="620" y="20" width="280" height="280" rx="10" fill="url(#cmpG3)" stroke="#d97706" stroke-width="2"/>
  <text x="760" y="48" text-anchor="middle" font-size="14" font-weight="700" fill="#92400e">桥接点</text>
  <text x="640" y="76" font-size="11" font-weight="600" fill="#78350f">LF 可平滑升级为 DIY</text>
  <text x="640" y="96" font-size="10" fill="#78350f">当 LF 表单无法满足时</text>
  <text x="640" y="114" font-size="10" fill="#78350f">实现 LFFormOperationAdaptor</text>
  <text x="640" y="132" font-size="10" fill="#78350f">@Service("对应formCode")</text>
  <text x="640" y="150" font-size="10" fill="#78350f">覆盖默认 LF 通用服务</text>
  <text x="640" y="168" font-size="10" fill="#78350f">保留原表单配置不变</text>
  <text x="640" y="186" font-size="10" fill="#78350f">业务可无缝迁移</text>

  <rect x="640" y="206" width="240" height="76" rx="6" fill="#92400e" stroke="#a16207"/>
  <text x="760" y="228" text-anchor="middle" font-size="11" font-weight="700" fill="#fff">关键 SPI</text>
  <text x="760" y="248" text-anchor="middle" font-size="10" fill="#fef3c7">LFFormOperationAdaptor</text>
  <text x="760" y="262" text-anchor="middle" font-size="10" fill="#fef3c7">extends FormOperationAdaptor</text>
  <text x="760" y="276" text-anchor="middle" font-size="10" fill="#fef3c7">@Service 名称 = formCode</text>
</svg>

详细对比见 [低代码 vs 自定义表单](/lowcode/lowcode-vs-diy) 章节。

## 关键技术决策

### 为什么自研而非用 Flowable Form Engine?

AntFlow 选择自研低代码体系,而非使用 Flowable 自带的 Form Engine,原因如下:

1. **Flowable Form Engine 字段类型有限**:不支持栅格/标签页/子表单等复杂布局
2. **与中国式办公脱节**:Flowable 表单权限粒度粗,无法做到节点级 R/E/H 控制
3. **数据存储模式死板**:Flowable 强依赖其表结构,AntFlow 需要支持水平分表
4. **VForm3 生态更成熟**:国内低代码表单生态,VForm3 社区活跃,控件丰富

### 为什么不直接存 JSON 文档(MongoDB 风格)?

低代码表单数据 AntFlow 采用**字段拆分存储**而非整 JSON 存储:

<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 920 240" font-family="-apple-system, BlinkMacSystemFont, 'Segoe UI', sans-serif">
  <rect x="20" y="20" width="430" height="200" rx="10" fill="#fee2e2" stroke="#dc2626" stroke-width="2"/>
  <text x="235" y="46" text-anchor="middle" font-size="13" font-weight="700" fill="#991b1b">❌ 方案 A:整 JSON 存储</text>
  <text x="40" y="74" font-size="11" font-weight="600" fill="#7f1d1d">表结构</text>
  <text x="40" y="92" font-size="10" fill="#7f1d1d">t_lf_main(id, form_code, form_data_json)</text>
  <text x="40" y="120" font-size="11" font-weight="600" fill="#7f1d1d">问题</text>
  <text x="40" y="138" font-size="10" fill="#7f1d1d">• 条件评估需解析 JSON(LIKE 查询低效)</text>
  <text x="40" y="156" font-size="10" fill="#7f1d1d">• 字段索引困难</text>
  <text x="40" y="174" font-size="10" fill="#7f1d1d">• 跨数据库 JSON 函数兼容差</text>
  <text x="40" y="192" font-size="10" fill="#7f1d1d">• 字段类型转换、数值聚合困难</text>
  <text x="40" y="210" font-size="10" fill="#7f1d1d">• 历史数据查询/统计困难</text>

  <rect x="470" y="20" width="430" height="200" rx="10" fill="#dcfce7" stroke="#16a34a" stroke-width="2"/>
  <text x="685" y="46" text-anchor="middle" font-size="13" font-weight="700" fill="#14532d">✓ 方案 B:AntFlow 字段拆分存储</text>
  <text x="490" y="74" font-size="11" font-weight="600" fill="#14532d">表结构</text>
  <text x="490" y="92" font-size="10" fill="#14532d">t_lf_main(id, form_code, ...)</text>
  <text x="490" y="108" font-size="10" fill="#14532d">t_lf_main_field(main_id, field_id,</text>
  <text x="490" y="122" font-size="10" fill="#14532d">  field_value, field_value_number,</text>
  <text x="490" y="136" font-size="10" fill="#14532d">  field_value_dt, field_value_text)</text>
  <text x="490" y="164" font-size="11" font-weight="600" fill="#14532d">优势</text>
  <text x="490" y="182" font-size="10" fill="#14532d">• 字段值类型化存储,索引友好</text>
  <text x="490" y="200" font-size="10" fill="#14532d">• 条件评估走 SQL,性能高</text>
  <text x="490" y="218" font-size="10" fill="#14532d">• 跨数据库通用,无 JSON 函数依赖</text>
</svg>

### 为什么独立管理表单(外部表单模式)?

AntFlow 0.3+ 引入**外部表单模式**(`USE_EXTERNAL_FORM` flag = 64),允许一个流程关联多个独立表单,而非传统的"一流程一表单"。优势:

1. **表单复用**:同一张"员工信息表单"可被 N 个流程引用
2. **独立版本管理**:表单升级不影响引用它的流程配置
3. **删除保护**:被引用的表单版本不能物理删除,只能软删
4. **运行期安全**:运行中流程引用的表单即使被软删,仍可通过 `listByIdsIgnoreDeleted` 读取

详见 [低代码表单引擎](/lowcode/lowcode-form) 章节。

## 配套截图与文档

### 现有截图(可复用)

低代码流程完整配置流程截图位于 `antflow-vue/public/docs/images/`,文档直接复用:

| 截图 | 内容 | 文档位置 |
|---|---|---|
| `2-1.png` / `2-2.png` | 流程类型添加 | [流程分类管理](/workflow-design/flow-category) |
| `3-1.png` ~ `3-7.png` | 低代码流程设计完整步骤 | [流程设计器](/workflow-design/flow-designer) |
| `4-1.png` | 流程启动 | [版本管理与启动](/workflow-design/version-management) |
| `5-1.png` / `5-2.png` | 发起流程 + 表单填写 | [发起流程](/workflow-run/start-flow) |
| `6-1.png` / `6-2.png` | 待办 + 审批 | [我的待办](/workflow-run/my-tasks) |
| `7-1.png` | 流程预览 | [流程预览](/workflow-run/flow-preview) |

### 章节导航

- [低代码表单引擎](/lowcode/lowcode-form) — 详细介绍 VForm3 集成、字段类型、表单权限、版本管理
- [低代码 vs 自定义表单](/lowcode/lowcode-vs-diy) — 两种模式横向对比与选型建议
- [流程设计器](/workflow-design/flow-designer) — 拖拽式流程设计器操作手册
- [低代码表单设计](/workflow-design/form-design) — 表单设计器基础操作
- [扩展审批人来源](/dev-guide/extend-approver) — 通过 SPI 扩展低代码表单的人员来源
- [集成现有系统](/dev-guide/integrate-existing) — 接入企业用户/角色系统
