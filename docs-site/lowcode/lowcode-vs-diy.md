# 低代码 vs 自定义表单

> AntFlow 同时支持低代码(LF)模式与自定义(DIY)模式。本章横向对比两种模式的实现成本、能力边界、性能差异、适用场景,并提供选型决策树与平滑迁移方案。

## 模式速览

| 维度 | LF 模式(低代码) | DIY 模式(自定义) |
|---|---|---|
| **开发方式** | 拖拽设计 + 零编码 | 业务表 + Java + Vue 实现 |
| **入口** | 流程类型 → 低代码表单流程 | 流程类型 → 自定义表单流程 |
| **后端代码** | 不需要 | 需要(Mapper/Service/Controller) |
| **前端代码** | 不需要 | 需要(.vue 表单组件) |
| **数据库表** | 不需要(用 t_lf_main + t_lf_main_field) | 需要(自定义业务表) |
| **配置周期** | 30 分钟 ~ 2 小时 | 2 天 ~ 2 周 |
| **维护成本** | 低(业务人员可改) | 高(需开发介入) |
| **能力上限** | 受 VForm3 + LF 通用服务限制 | 无限制 |
| **典型场景** | 通用审批、POC、SaaS | 复杂业务、需调用内部 API |

## 能力对比矩阵

<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 920 720" font-family="-apple-system, BlinkMacSystemFont, 'Segoe UI', sans-serif">
  <defs>
    <linearGradient id="vsG1" x1="0" y1="0" x2="0" y2="1"><stop offset="0%" stop-color="#dbeafe"/><stop offset="100%" stop-color="#bfdbfe"/></linearGradient>
    <linearGradient id="vsG2" x1="0" y1="0" x2="0" y2="1"><stop offset="0%" stop-color="#dcfce7"/><stop offset="100%" stop-color="#bbf7d0"/></linearGradient>
  </defs>

  <text x="460" y="24" text-anchor="middle" font-size="16" font-weight="700" fill="#0f172a">能力对比矩阵</text>

  <!-- 表头 -->
  <rect x="20" y="40" width="220" height="40" fill="#1e293b"/>
  <text x="130" y="64" text-anchor="middle" font-size="12" font-weight="700" fill="#fff">能力维度</text>
  <rect x="240" y="40" width="340" height="40" fill="url(#vsG1)" stroke="#3b82f6" stroke-width="2"/>
  <text x="410" y="64" text-anchor="middle" font-size="12" font-weight="700" fill="#1e40af">LF 模式(低代码)</text>
  <rect x="580" y="40" width="320" height="40" fill="url(#vsG2)" stroke="#16a34a" stroke-width="2"/>
  <text x="740" y="64" text-anchor="middle" font-size="12" font-weight="700" fill="#155e2f">DIY 模式(自定义)</text>

  <!-- 表单设计 -->
  <rect x="20" y="80" width="220" height="48" fill="#f1f5f9" stroke="#cbd5e1"/>
  <text x="40" y="108" font-size="11" font-weight="600" fill="#1e293b">表单设计</text>
  <rect x="240" y="80" width="340" height="48" fill="#fff" stroke="#bfdbfe"/>
  <text x="260" y="100" font-size="10" fill="#1e3a8a">VForm3 拖拽设计</text>
  <text x="260" y="116" font-size="10" fill="#1e3a8a">20+ 控件 + 容器组件</text>
  <rect x="580" y="80" width="320" height="48" fill="#fff" stroke="#bbf7d0"/>
  <text x="600" y="100" font-size="10" fill="#14532d">Vue 自定义组件</text>
  <text x="600" y="116" font-size="10" fill="#14532d">无限制(可调用任意 API)</text>

  <!-- 字段类型 -->
  <rect x="20" y="128" width="220" height="48" fill="#f1f5f9" stroke="#cbd5e1"/>
  <text x="40" y="156" font-size="11" font-weight="600" fill="#1e293b">字段类型</text>
  <rect x="240" y="128" width="340" height="48" fill="#fff" stroke="#bfdbfe"/>
  <text x="260" y="148" font-size="10" fill="#1e3a8a">7 种存储类型(STRING/NUMBER/</text>
  <text x="260" y="164" font-size="10" fill="#1e3a8a">DATE/DATE_TIME/TEXT/BOOLEAN/BLOB)</text>
  <rect x="580" y="128" width="320" height="48" fill="#fff" stroke="#bbf7d0"/>
  <text x="600" y="148" font-size="10" fill="#14532d">完全自定义</text>
  <text x="600" y="164" font-size="10" fill="#14532d">支持任意 Java 类型 / 自定义 DTO</text>

  <!-- 字段权限 -->
  <rect x="20" y="176" width="220" height="48" fill="#f1f5f9" stroke="#cbd5e1"/>
  <text x="40" y="204" font-size="11" font-weight="600" fill="#1e293b">字段权限(R/E/H)</text>
  <rect x="240" y="176" width="340" height="48" fill="#dcfce7" stroke="#bfdbfe"/>
  <text x="260" y="196" font-size="10" font-weight="600" fill="#14532d">✓ 原生支持(节点级)</text>
  <text x="260" y="212" font-size="10" fill="#1e3a8a">设计器内可视化配置</text>
  <rect x="580" y="176" width="320" height="48" fill="#fff" stroke="#bbf7d0"/>
  <text x="600" y="196" font-size="10" fill="#14532d">需在 Vue 组件中手动实现</text>
  <text x="600" y="212" font-size="10" fill="#94a3b8">(根据节点 ID 判断 disabled/hidden)</text>

  <!-- 流程节点 -->
  <rect x="20" y="224" width="220" height="48" fill="#f1f5f9" stroke="#cbd5e1"/>
  <text x="40" y="252" font-size="11" font-weight="600" fill="#1e293b">流程节点支持</text>
  <rect x="240" y="224" width="340" height="48" fill="#dcfce7" stroke="#bfdbfe"/>
  <text x="260" y="244" font-size="10" font-weight="600" fill="#14532d">✓ 全部 12 种节点类型</text>
  <text x="260" y="260" font-size="10" fill="#1e3a8a">与 DIY 完全一致</text>
  <rect x="580" y="224" width="320" height="48" fill="#dcfce7" stroke="#bbf7d0"/>
  <text x="600" y="244" font-size="10" font-weight="600" fill="#14532d">✓ 全部 12 种节点类型</text>
  <text x="600" y="260" font-size="10" fill="#14532d">引擎能力与模式无关</text>

  <!-- 条件评估 -->
  <rect x="20" y="272" width="220" height="48" fill="#f1f5f9" stroke="#cbd5e1"/>
  <text x="40" y="300" font-size="11" font-weight="600" fill="#1e293b">条件评估</text>
  <rect x="240" y="272" width="340" height="48" fill="#dcfce7" stroke="#bfdbfe"/>
  <text x="260" y="292" font-size="10" font-weight="600" fill="#14532d">✓ 支持(从表单字段提取)</text>
  <text x="260" y="308" font-size="10" fill="#1e3a8a">条件字段自动识别(is_condition)</text>
  <rect x="580" y="272" width="320" height="48" fill="#dcfce7" stroke="#bbf7d0"/>
  <text x="600" y="292" font-size="10" font-weight="600" fill="#14532d">✓ 支持</text>
  <text x="600" y="308" font-size="10" fill="#14532d">从业务表字段取值</text>

  <!-- 审批人来源 -->
  <rect x="20" y="320" width="220" height="48" fill="#f1f5f9" stroke="#cbd5e1"/>
  <text x="40" y="348" font-size="11" font-weight="600" fill="#1e293b">审批人来源(15 种)</text>
  <rect x="240" y="320" width="340" height="48" fill="#dcfce7" stroke="#bfdbfe"/>
  <text x="260" y="340" font-size="10" font-weight="600" fill="#14532d">✓ 全部支持</text>
  <text x="260" y="356" font-size="10" fill="#1e3a8a">含"表单上下文人员"(FORM_RELATED)</text>
  <rect x="580" y="320" width="320" height="48" fill="#dcfce7" stroke="#bbf7d0"/>
  <text x="600" y="340" font-size="10" font-weight="600" fill="#14532d">✓ 全部支持</text>
  <text x="600" y="356" font-size="10" fill="#14532d">可通过 Service 直接调用</text>

  <!-- 业务逻辑 -->
  <rect x="20" y="368" width="220" height="48" fill="#f1f5f9" stroke="#cbd5e1"/>
  <text x="40" y="396" font-size="11" font-weight="600" fill="#1e293b">业务逻辑嵌入</text>
  <rect x="240" y="368" width="340" height="48" fill="#fef3c7" stroke="#bfdbfe"/>
  <text x="260" y="388" font-size="10" font-weight="600" fill="#92400e">△ 有限(通过 SPI Hook)</text>
  <text x="260" y="404" font-size="10" fill="#1e3a8a">onBeforeSubmit / onAfterApprove</text>
  <rect x="580" y="368" width="320" height="48" fill="#dcfce7" stroke="#bbf7d0"/>
  <text x="600" y="388" font-size="10" font-weight="600" fill="#14532d">✓ 完全自定义</text>
  <text x="600" y="404" font-size="10" fill="#14532d">可在 Service 任意位置嵌入业务逻辑</text>

  <!-- 字段联动 -->
  <rect x="20" y="416" width="220" height="48" fill="#f1f5f9" stroke="#cbd5e1"/>
  <text x="40" y="444" font-size="11" font-weight="600" fill="#1e293b">字段联动/异步加载</text>
  <rect x="240" y="416" width="340" height="48" fill="#fef3c7" stroke="#bfdbfe"/>
  <text x="260" y="436" font-size="10" font-weight="600" fill="#92400e">△ 受限</text>
  <text x="260" y="452" font-size="10" fill="#1e3a8a">VForm3 内置 onChange 事件可配置</text>
  <rect x="580" y="416" width="320" height="48" fill="#dcfce7" stroke="#bbf7d0"/>
  <text x="600" y="436" font-size="10" font-weight="600" fill="#14532d">✓ 完全自定义</text>
  <text x="600" y="452" font-size="10" fill="#14532d">可调用任意接口,实现复杂联动</text>

  <!-- 数据存储 -->
  <rect x="20" y="464" width="220" height="48" fill="#f1f5f9" stroke="#cbd5e1"/>
  <text x="40" y="492" font-size="11" font-weight="600" fill="#1e293b">数据存储</text>
  <rect x="240" y="464" width="340" height="48" fill="#fff" stroke="#bfdbfe"/>
  <text x="260" y="484" font-size="10" fill="#1e3a8a">t_lf_main + t_lf_main_field(字段拆分)</text>
  <text x="260" y="500" font-size="10" fill="#1e3a8a">支持水平分表</text>
  <rect x="580" y="464" width="320" height="48" fill="#fff" stroke="#bbf7d0"/>
  <text x="600" y="484" font-size="10" fill="#14532d">自定义业务表</text>
  <text x="600" y="500" font-size="10" fill="#14532d">结构由业务方设计</text>

  <!-- 数据查询 -->
  <rect x="20" y="512" width="220" height="48" fill="#f1f5f9" stroke="#cbd5e1"/>
  <text x="40" y="540" font-size="11" font-weight="600" fill="#1e293b">数据查询/统计</text>
  <rect x="240" y="512" width="340" height="48" fill="#fef3c7" stroke="#bfdbfe"/>
  <text x="260" y="532" font-size="10" font-weight="600" fill="#92400e">△ 需 JOIN t_lf_main_field</text>
  <text x="260" y="548" font-size="10" fill="#1e3a8a">复杂统计需特殊 SQL</text>
  <rect x="580" y="512" width="320" height="48" fill="#dcfce7" stroke="#bbf7d0"/>
  <text x="600" y="532" font-size="10" font-weight="600" fill="#14532d">✓ 直接 SQL 查询</text>
  <text x="600" y="548" font-size="10" fill="#14532d">字段即列,索引友好</text>

  <!-- 版本管理 -->
  <rect x="20" y="560" width="220" height="48" fill="#f1f5f9" stroke="#cbd5e1"/>
  <text x="40" y="588" font-size="11" font-weight="600" fill="#1e293b">表单版本管理</text>
  <rect x="240" y="560" width="340" height="48" fill="#dcfce7" stroke="#bfdbfe"/>
  <text x="260" y="580" font-size="10" font-weight="600" fill="#14532d">✓ 原生支持(独立/内联)</text>
  <text x="260" y="596" font-size="10" fill="#1e3a8a">同族互斥、引用保护</text>
  <rect x="580" y="560" width="320" height="48" fill="#fef3c7" stroke="#bbf7d0"/>
  <text x="600" y="580" font-size="10" font-weight="600" fill="#92400e">△ 需业务方自行实现</text>
  <text x="600" y="596" font-size="10" fill="#14532d">表单字段变更需表结构 ALTER</text>

  <!-- 多租户 -->
  <rect x="20" y="608" width="220" height="48" fill="#f1f5f9" stroke="#cbd5e1"/>
  <text x="40" y="636" font-size="11" font-weight="600" fill="#1e293b">多租户(SaaS)</text>
  <rect x="240" y="608" width="340" height="48" fill="#dcfce7" stroke="#bfdbfe"/>
  <text x="260" y="628" font-size="10" font-weight="600" fill="#14532d">✓ 原生支持</text>
  <text x="260" y="644" font-size="10" fill="#1e3a8a">tenant_id 字段 + 字段隔离</text>
  <rect x="580" y="608" width="320" height="48" fill="#fef3c7" stroke="#bbf7d0"/>
  <text x="600" y="628" font-size="10" font-weight="600" fill="#92400e">△ 需业务方实现租户字段</text>
  <text x="600" y="644" font-size="10" fill="#14532d">每张业务表加 tenant_id</text>

  <!-- 性能 -->
  <rect x="20" y="656" width="220" height="48" fill="#f1f5f9" stroke="#cbd5e1"/>
  <text x="40" y="684" font-size="11" font-weight="600" fill="#1e293b">性能(同等数据量)</text>
  <rect x="240" y="656" width="340" height="48" fill="#fef3c7" stroke="#bfdbfe"/>
  <text x="260" y="676" font-size="10" font-weight="600" fill="#92400e">△ 略低(字段表 JOIN)</text>
  <text x="260" y="692" font-size="10" fill="#1e3a8a">但分表后差距缩小</text>
  <rect x="580" y="656" width="320" height="48" fill="#dcfce7" stroke="#bbf7d0"/>
  <text x="600" y="676" font-size="10" font-weight="600" fill="#14532d">✓ 最优</text>
  <text x="600" y="692" font-size="10" fill="#14532d">字段即列,无 JOIN</text>
</svg>

## 选型决策树

<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 920 580" font-family="-apple-system, BlinkMacSystemFont, 'Segoe UI', sans-serif">
  <defs>
    <marker id="arrDec" viewBox="0 0 10 10" refX="9" refY="5" markerWidth="6" markerHeight="6" orient="auto"><path d="M0,0 L10,5 L0,10 z" fill="#475569"/></marker>
  </defs>

  <!-- 起点 -->
  <rect x="380" y="20" width="160" height="50" rx="25" fill="#1e293b"/>
  <text x="460" y="44" text-anchor="middle" font-size="13" font-weight="700" fill="#fff">需要配置流程</text>
  <text x="460" y="60" text-anchor="middle" font-size="10" fill="#cbd5e1">开始决策</text>

  <!-- 问题1:表单复杂度 -->
  <line x1="460" y1="70" x2="460" y2="100" stroke="#475569" stroke-width="2" marker-end="url(#arrDec)"/>
  <polygon points="460,100 580,150 460,200 340,150" fill="#fef3c7" stroke="#d97706" stroke-width="2"/>
  <text x="460" y="146" text-anchor="middle" font-size="11" font-weight="600" fill="#92400e">表单逻辑复杂?</text>
  <text x="460" y="162" text-anchor="middle" font-size="10" fill="#78350f">字段联动/计算/异步加载</text>
  <text x="460" y="180" text-anchor="middle" font-size="10" fill="#78350f">需调用企业 API</text>

  <!-- 是 -> DIY -->
  <line x1="340" y1="150" x2="220" y2="150" stroke="#475569" stroke-width="2" marker-end="url(#arrDec)"/>
  <text x="280" y="142" text-anchor="middle" font-size="10" font-weight="600" fill="#16a34a">是</text>
  <rect x="40" y="120" width="180" height="60" rx="8" fill="#dcfce7" stroke="#16a34a" stroke-width="2"/>
  <text x="130" y="146" text-anchor="middle" font-size="12" font-weight="700" fill="#14532d">DIY 模式</text>
  <text x="130" y="164" text-anchor="middle" font-size="10" fill="#14532d">完全自定义</text>

  <!-- 否 -> 问题2 -->
  <line x1="580" y1="150" x2="700" y2="150" stroke="#475569" stroke-width="2" marker-end="url(#arrDec)"/>
  <text x="640" y="142" text-anchor="middle" font-size="10" font-weight="600" fill="#16a34a">否</text>
  <line x1="700" y1="150" x2="700" y2="220" stroke="#475569" stroke-width="2" marker-end="url(#arrDec)"/>
  <polygon points="700,220 820,270 700,320 580,270" fill="#fef3c7" stroke="#d97706" stroke-width="2"/>
  <text x="700" y="266" text-anchor="middle" font-size="11" font-weight="600" fill="#92400e">需要独立表单库?</text>
  <text x="700" y="282" text-anchor="middle" font-size="10" fill="#78350f">多个流程复用同一表单</text>
  <text x="700" y="300" text-anchor="middle" font-size="10" fill="#78350f">或表单独立升级</text>

  <!-- 是 -> LF 外部模式 -->
  <line x1="820" y1="270" x2="880" y2="270" stroke="#475569" stroke-width="2" marker-end="url(#arrDec)"/>
  <text x="850" y="262" text-anchor="middle" font-size="10" font-weight="600" fill="#16a34a">是</text>
  <rect x="700" y="350" width="200" height="60" rx="8" fill="#dbeafe" stroke="#3b82f6" stroke-width="2"/>
  <text x="800" y="376" text-anchor="middle" font-size="12" font-weight="700" fill="#1e40af">LF 模式</text>
  <text x="800" y="394" text-anchor="middle" font-size="10" fill="#1e3a8a">外部表单模式</text>
  <line x1="800" y1="270" x2="800" y2="350" stroke="#475569" stroke-width="2" marker-end="url(#arrDec)"/>

  <!-- 否 -> 问题3 -->
  <line x1="580" y1="270" x2="460" y2="270" stroke="#475569" stroke-width="2" marker-end="url(#arrDec)"/>
  <text x="520" y="262" text-anchor="middle" font-size="10" font-weight="600" fill="#16a34a">否</text>
  <line x1="460" y1="270" x2="460" y2="340" stroke="#475569" stroke-width="2" marker-end="url(#arrDec)"/>
  <polygon points="460,340 580,390 460,440 340,390" fill="#fef3c7" stroke="#d97706" stroke-width="2"/>
  <text x="460" y="386" text-anchor="middle" font-size="11" font-weight="600" fill="#92400e">SaaS 多租户?</text>
  <text x="460" y="402" text-anchor="middle" font-size="10" fill="#78350f">需要租户隔离</text>
  <text x="460" y="420" text-anchor="middle" font-size="10" fill="#78350f">业务方零代码配置</text>

  <!-- 是 -> LF 内联 -->
  <line x1="580" y1="390" x2="660" y2="390" stroke="#475569" stroke-width="2" marker-end="url(#arrDec)"/>
  <text x="620" y="382" text-anchor="middle" font-size="10" font-weight="600" fill="#16a34a">是</text>
  <rect x="460" y="460" width="200" height="60" rx="8" fill="#dbeafe" stroke="#3b82f6" stroke-width="2"/>
  <text x="560" y="486" text-anchor="middle" font-size="12" font-weight="700" fill="#1e40af">LF 模式</text>
  <text x="560" y="504" text-anchor="middle" font-size="10" fill="#1e3a8a">内联表单模式</text>
  <line x1="560" y1="390" x2="560" y2="460" stroke="#475569" stroke-width="2" marker-end="url(#arrDec)"/>

  <!-- 否 -> 问题4 -->
  <line x1="340" y1="390" x2="240" y2="390" stroke="#475569" stroke-width="2" marker-end="url(#arrDec)"/>
  <text x="290" y="382" text-anchor="middle" font-size="10" font-weight="600" fill="#16a34a">否</text>
  <line x1="240" y1="390" x2="240" y2="460" stroke="#475569" stroke-width="2" marker-end="url(#arrDec)"/>
  <polygon points="240,460 360,510 240,560 120,510" fill="#fef3c7" stroke="#d97706" stroke-width="2"/>
  <text x="240" y="506" text-anchor="middle" font-size="11" font-weight="600" fill="#92400e">需要自定义业务逻辑?</text>
  <text x="240" y="522" text-anchor="middle" font-size="10" fill="#78350f">提交前校验/审批后回调</text>
  <text x="240" y="540" text-anchor="middle" font-size="10" fill="#78350f">业务表已有数据</text>

  <!-- 是 -> DIY -->
  <line x1="120" y1="510" x2="60" y2="510" stroke="#475569" stroke-width="2" marker-end="url(#arrDec)"/>
  <text x="90" y="502" text-anchor="middle" font-size="10" font-weight="600" fill="#16a34a">是</text>
  <rect x="40" y="490" width="80" height="40" rx="6" fill="#dcfce7" stroke="#16a34a" stroke-width="2"/>
  <text x="80" y="516" text-anchor="middle" font-size="11" font-weight="700" fill="#14532d">DIY</text>

  <!-- 否 -> LF -->
  <line x1="360" y1="510" x2="400" y2="510" stroke="#475569" stroke-width="2" marker-end="url(#arrDec)"/>
  <text x="380" y="502" text-anchor="middle" font-size="10" font-weight="600" fill="#16a34a">否</text>
  <rect x="400" y="490" width="80" height="40" rx="6" fill="#dbeafe" stroke="#3b82f6" stroke-width="2"/>
  <text x="440" y="516" text-anchor="middle" font-size="11" font-weight="700" fill="#1e40af">LF</text>
</svg>

## 开发工作量对比

### LF 模式:零编码

以"请假申请"为例,LF 模式完整配置流程:

<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 920 360" font-family="-apple-system, BlinkMacSystemFont, 'Segoe UI', sans-serif">
  <defs>
    <marker id="arrW" viewBox="0 0 10 10" refX="9" refY="5" markerWidth="6" markerHeight="6" orient="auto"><path d="M0,0 L10,5 L0,10 z" fill="#475569"/></marker>
  </defs>

  <text x="460" y="24" text-anchor="middle" font-size="14" font-weight="700" fill="#0f172a">LF 模式:请假申请流程配置(零编码)</text>

  <rect x="20" y="50" width="160" height="80" rx="8" fill="#dbeafe" stroke="#3b82f6" stroke-width="2"/>
  <text x="100" y="74" text-anchor="middle" font-size="11" font-weight="700" fill="#1e40af">步骤 1</text>
  <text x="100" y="92" text-anchor="middle" font-size="10" fill="#1e3a8a">新建流程类型</text>
  <text x="100" y="108" text-anchor="middle" font-size="10" fill="#1e3a8a">类型:低代码</text>
  <text x="100" y="124" text-anchor="middle" font-size="10" fill="#94a3b8">5 分钟</text>

  <line x1="180" y1="90" x2="200" y2="90" stroke="#475569" stroke-width="2" marker-end="url(#arrW)"/>

  <rect x="200" y="50" width="160" height="80" rx="8" fill="#dbeafe" stroke="#3b82f6" stroke-width="2"/>
  <text x="280" y="74" text-anchor="middle" font-size="11" font-weight="700" fill="#1e40af">步骤 2</text>
  <text x="280" y="92" text-anchor="middle" font-size="10" fill="#1e3a8a">VForm3 设计表单</text>
  <text x="280" y="108" text-anchor="middle" font-size="10" fill="#1e3a8a">拖拽 6 个字段</text>
  <text x="280" y="124" text-anchor="middle" font-size="10" fill="#94a3b8">10 分钟</text>

  <line x1="360" y1="90" x2="380" y2="90" stroke="#475569" stroke-width="2" marker-end="url(#arrW)"/>

  <rect x="380" y="50" width="160" height="80" rx="8" fill="#dbeafe" stroke="#3b82f6" stroke-width="2"/>
  <text x="460" y="74" text-anchor="middle" font-size="11" font-weight="700" fill="#1e40af">步骤 3</text>
  <text x="460" y="92" text-anchor="middle" font-size="10" fill="#1e3a8a">设计流程节点</text>
  <text x="460" y="108" text-anchor="middle" font-size="10" fill="#1e3a8a">审批人 + 条件</text>
  <text x="460" y="124" text-anchor="middle" font-size="10" fill="#94a3b8">10 分钟</text>

  <line x1="540" y1="90" x2="560" y2="90" stroke="#475569" stroke-width="2" marker-end="url(#arrW)"/>

  <rect x="560" y="50" width="160" height="80" rx="8" fill="#dbeafe" stroke="#3b82f6" stroke-width="2"/>
  <text x="640" y="74" text-anchor="middle" font-size="11" font-weight="700" fill="#1e40af">步骤 4</text>
  <text x="640" y="92" text-anchor="middle" font-size="10" fill="#1e3a8a">配置字段权限</text>
  <text x="640" y="108" text-anchor="middle" font-size="10" fill="#1e3a8a">R/E/H 三选一</text>
  <text x="640" y="124" text-anchor="middle" font-size="10" fill="#94a3b8">5 分钟</text>

  <line x1="720" y1="90" x2="740" y2="90" stroke="#475569" stroke-width="2" marker-end="url(#arrW)"/>

  <rect x="740" y="50" width="160" height="80" rx="8" fill="#16a34a" stroke="#166534" stroke-width="2"/>
  <text x="820" y="74" text-anchor="middle" font-size="11" font-weight="700" fill="#fff">步骤 5</text>
  <text x="820" y="92" text-anchor="middle" font-size="10" fill="#dcfce7">点击发布</text>
  <text x="820" y="108" text-anchor="middle" font-size="10" fill="#dcfce7">立即生效</text>
  <text x="820" y="124" text-anchor="middle" font-size="10" fill="#bbf7d0">1 分钟</text>

  <!-- 总览 -->
  <rect x="20" y="160" width="880" height="180" rx="10" fill="#f1f5f9" stroke="#475569" stroke-width="2"/>
  <text x="460" y="186" text-anchor="middle" font-size="13" font-weight="700" fill="#0f172a">总投入</text>

  <text x="40" y="216" font-size="11" font-weight="600" fill="#1e293b">开发人员</text>
  <text x="40" y="234" font-size="10" fill="#475569">无(业务人员/产品经理即可)</text>

  <text x="320" y="216" font-size="11" font-weight="600" fill="#1e293b">编码量</text>
  <text x="320" y="234" font-size="10" fill="#475569">0 行 Java / 0 行 Vue / 0 行 SQL</text>

  <text x="600" y="216" font-size="11" font-weight="600" fill="#1e293b">配置时长</text>
  <text x="600" y="234" font-size="10" fill="#475569">约 30 分钟</text>

  <text x="40" y="270" font-size="11" font-weight="600" fill="#1e293b">文件改动</text>
  <text x="40" y="288" font-size="10" fill="#475569">无(完全基于 UI 配置)</text>

  <text x="320" y="270" font-size="11" font-weight="600" fill="#1e293b">数据库改动</text>
  <text x="320" y="288" font-size="10" fill="#475569">无(使用现有 t_lf_main 表)</text>

  <text x="600" y="270" font-size="11" font-weight="600" fill="#1e293b">维护成本</text>
  <text x="600" y="288" font-size="10" fill="#475569">低(改 UI 即生效)</text>

  <rect x="40" y="304" width="840" height="28" rx="6" fill="#dcfce7" stroke="#16a34a"/>
  <text x="460" y="322" text-anchor="middle" font-size="11" font-weight="600" fill="#14532d">★ 业务人员可独立完成,无需 IT 部门介入</text>
</svg>

### DIY 模式:完整开发

同样"请假申请"流程,DIY 模式完整开发流程:

| 阶段 | 工作内容 | 产出 | 时长 |
|---|---|---|---|
| 1. 数据库设计 | 设计业务表 `t_biz_leave` | DDL SQL | 30 分钟 |
| 2. 后端实体/Mapper | 生成 Entity + Mapper | Java 类 4 个 | 30 分钟 |
| 3. Service + Controller | 实现业务逻辑 + REST | Java 类 2 个 | 2 小时 |
| 4. FormOperationAdaptor | 实现 `LeaveFormAdaptor` | Java 类 1 个 | 1 小时 |
| 5. Vue 表单组件 | 编写 `.vue` 文件 | Vue 文件 1 个 | 2 小时 |
| 6. 路由注册 | `bizFormMaps` 添加 | 改 const.js | 5 分钟 |
| 7. 流程配置 | UI 配置流程 | 数据库记录 | 30 分钟 |
| 8. 联调测试 | 启动测试 | - | 2 小时 |
| **合计** | - | **8+ 文件改动** | **~2 天** |

### DIY 模式示例代码骨架

```java
// 1. 业务表实体
@Data
@TableName("t_biz_leave")
public class Leave {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String applicant;       // 申请人
    private String leaveType;       // 请假类型
    private Date startDate;         // 开始日期
    private Date endDate;           // 结束日期
    private Double days;            // 天数
    private String reason;          // 原因
    // 审计字段...
}

// 2. Mapper
@Mapper
public interface LeaveMapper extends BaseMapper<Leave> {}

// 3. FormOperationAdaptor 实现
@ActivitiServiceAnno(svcName = "LEAVE_WMA")  // 与 formCode 一致
public class LeaveFormAdaptor implements FormOperationAdaptor<LeaveVo> {

    @Autowired
    private LeaveMapper leaveMapper;

    @Override
    public void submitData(LeaveVo vo) {
        Leave leave = new Leave();
        BeanUtils.copyProperties(vo, leave);
        leaveMapper.insert(leave);
        vo.setProcessInstanceId(leave.getId().toString());
    }

    @Override
    public LeaveVo queryData(String processNumber) {
        Leave leave = leaveMapper.selectById(Long.parseLong(processNumber));
        LeaveVo vo = new LeaveVo();
        BeanUtils.copyProperties(leave, vo);
        return vo;
    }

    @Override
    public void consentData(LeaveVo vo) {
        // 审批通过后的业务逻辑
    }
    
    // ... 其他方法
}
```

```javascript
// 4. Vue 表单组件 antflow-vue/src/views/workflow/components/forms/form2.vue
<template>
  <el-form :model="formData" :rules="rules" ref="formRef">
    <el-form-item label="请假类型" prop="leaveType">
      <el-select v-model="formData.leaveType">
        <el-option label="事假" value="personal" />
        <el-option label="病假" value="sick" />
        <el-option label="年假" value="annual" />
      </el-select>
    </el-form-item>
    <el-form-item label="开始日期" prop="startDate">
      <el-date-picker v-model="formData.startDate" type="date" />
    </el-form-item>
    <!-- 其他字段... -->
  </el-form>
</template>

<script setup>
import { ref, reactive } from "vue";
const formData = reactive({
  applicant: "", leaveType: "", startDate: "", endDate: "", days: 0, reason: ""
});
const rules = {
  leaveType: [{ required: true, message: "请选择请假类型", trigger: "change" }],
  // ...
};
</script>
```

```javascript
// 5. 路由注册 antflow-vue/src/utils/flow/const.js
export const bizFormMaps = new Map([
  ['DSFZH_WMA', '/forms/form1.vue'],
  ['LEAVE_WMA', '/forms/form2.vue'],     // ← 新增
  ['UCARREFUEl_WMA', '/forms/form3.vue']
]);
```

## 性能对比

### 数据读取性能(10 万条实例)

| 场景 | LF 模式 | DIY 模式 | 差距 |
|---|---|---|---|
| 单实例查询 | ~5 ms | ~2 ms | LF 多 1 次 JOIN |
| 列表查询(分页 20 条) | ~30 ms | ~15 ms | LF 多 1 次 JOIN + 字段聚合 |
| 字段值过滤查询 | ~80 ms(走 t_lf_main_field) | ~10 ms(走业务表索引) | LF 索引粒度粗 |
| 统计聚合(SUM/COUNT) | ~500 ms | ~50 ms | LF 需 JOIN 后聚合 |

> 实测数据基于 MySQL 8.0 + 16G 内存 + SSD,数据量 10 万实例 / 100 万字段值

### 性能优化建议

LF 模式性能不够时的优化路径:

1. **开启水平分表**:配置 `lf.main.table.count=8`、`lf.field.table.count=20`,需手动建表
2. **添加索引**:为高频查询字段在 `t_lf_main_field` 上添加联合索引 `(field_id, field_value)`
3. **数据归档**:历史数据迁移到归档表
4. **降级为 DIY**:如果性能瓶颈严重,可平滑迁移到 DIY 模式(见下文)

## 平滑迁移:LF → DIY

当 LF 模式无法满足业务需求时,可平滑迁移到 DIY 模式,**无需废弃已有流程实例**:

<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 920 340" font-family="-apple-system, BlinkMacSystemFont, 'Segoe UI', sans-serif">
  <defs>
    <marker id="arrMig" viewBox="0 0 10 10" refX="9" refY="5" markerWidth="6" markerHeight="6" orient="auto"><path d="M0,0 L10,5 L0,10 z" fill="#475569"/></marker>
  </defs>

  <text x="460" y="24" text-anchor="middle" font-size="14" font-weight="700" fill="#0f172a">LF → DIY 平滑迁移方案</text>

  <!-- 阶段1 -->
  <rect x="20" y="50" width="220" height="120" rx="8" fill="#dbeafe" stroke="#3b82f6" stroke-width="2"/>
  <text x="130" y="76" text-anchor="middle" font-size="12" font-weight="700" fill="#1e40af">阶段 1:LF 运行中</text>
  <text x="40" y="100" font-size="10" fill="#1e3a8a">• 表单:VForm3 JSON</text>
  <text x="40" y="118" font-size="10" fill="#1e3a8a">• 数据:t_lf_main + _field</text>
  <text x="40" y="136" font-size="10" fill="#1e3a8a">• 服务:LowFlowApprovalService</text>
  <text x="40" y="154" font-size="10" fill="#1e3a8a">• 实例:N 个运行中</text>

  <line x1="240" y1="110" x2="280" y2="110" stroke="#475569" stroke-width="2" marker-end="url(#arrMig)"/>

  <!-- 阶段2 -->
  <rect x="280" y="50" width="220" height="120" rx="8" fill="#fef3c7" stroke="#d97706" stroke-width="2"/>
  <text x="390" y="76" text-anchor="middle" font-size="12" font-weight="700" fill="#92400e">阶段 2:实现 Adaptor</text>
  <text x="300" y="100" font-size="10" fill="#78350f">• 新建业务表 t_biz_leave</text>
  <text x="300" y="118" font-size="10" fill="#78350f">• 实现 LeaveLFFormAdaptor</text>
  <text x="300" y="136" font-size="10" fill="#78350f">  @Service("LEAVE_LF_WMA")</text>
  <text x="300" y="154" font-size="10" fill="#78350f">• 数据双写:LF 表 + 业务表</text>

  <line x1="500" y1="110" x2="540" y2="110" stroke="#475569" stroke-width="2" marker-end="url(#arrMig)"/>

  <!-- 阶段3 -->
  <rect x="540" y="50" width="220" height="120" rx="8" fill="#fef3c7" stroke="#d97706" stroke-width="2"/>
  <text x="650" y="76" text-anchor="middle" font-size="12" font-weight="700" fill="#92400e">阶段 3:数据迁移</text>
  <text x="560" y="100" font-size="10" fill="#78350f">• 编写迁移脚本</text>
  <text x="560" y="118" font-size="10" fill="#78350f">  t_lf_main_field → t_biz_leave</text>
  <text x="560" y="136" font-size="10" fill="#78350f">• 历史实例数据迁移</text>
  <text x="560" y="154" font-size="10" fill="#78350f">• 验证数据完整性</text>

  <line x1="760" y1="110" x2="800" y2="110" stroke="#475569" stroke-width="2" marker-end="url(#arrMig)"/>

  <!-- 阶段4 -->
  <rect x="800" y="50" width="100" height="120" rx="8" fill="#dcfce7" stroke="#16a34a" stroke-width="2"/>
  <text x="850" y="76" text-anchor="middle" font-size="12" font-weight="700" fill="#14532d">阶段 4</text>
  <text x="850" y="100" text-anchor="middle" font-size="10" fill="#14532d">DIY 模式</text>
  <text x="850" y="118" text-anchor="middle" font-size="10" fill="#14532d">完全运行</text>
  <text x="850" y="148" text-anchor="middle" font-size="10" fill="#16a34a" font-weight="600">★ 切换</text>
  <text x="850" y="162" text-anchor="middle" font-size="10" fill="#16a34a" font-weight="600">完成</text>

  <!-- 关键点 -->
  <rect x="20" y="190" width="880" height="140" rx="10" fill="#f1f5f9" stroke="#475569" stroke-width="2"/>
  <text x="460" y="216" text-anchor="middle" font-size="12" font-weight="700" fill="#0f172a">关键点</text>

  <text x="40" y="244" font-size="11" font-weight="600" fill="#1e293b">1. formCode 保持不变</text>
  <text x="40" y="262" font-size="10" fill="#475569">DIY 模式的 formCode 与原 LF formCode 一致,引擎路由无感知</text>

  <text x="40" y="290" font-size="11" font-weight="600" fill="#1e293b">2. SPI 接口选择 LFFormOperationAdaptor 而非 FormOperationAdaptor</text>
  <text x="40" y="308" font-size="10" fill="#475569">仍用 @Service("formCode") 注册,引擎会优先调用此实现而非默认 LowFlowApprovalService</text>
</svg>

### 迁移示例代码

```java
// 阶段 2:实现 LeaveLFFormAdaptor(数据双写期)
@Service("LEAVE_LF_WMA")  // formCode 与原 LF 一致
public class LeaveLFFormAdaptor implements LFFormOperationAdaptor<UDLFApplyVo> {

    @Autowired
    private LeaveMapper leaveMapper;          // 新业务表 Mapper
    @Autowired
    private LowFlowApprovalService lfService; // 原 LF 通用服务

    @Override
    public void submitData(UDLFApplyVo vo) {
        // 1. 调原 LF 服务,数据写入 t_lf_main(兼容运行中实例)
        lfService.submitData(vo);
        
        // 2. 同步写入新业务表(双写)
        Leave leave = extractLeaveFromLfFields(vo.getLfFields());
        leaveMapper.insert(leave);
    }

    @Override
    public UDLFApplyVo queryData(String processNumber) {
        // 优先从业务表查询(若已迁移)
        Leave leave = leaveMapper.selectByProcessNumber(processNumber);
        if (leave != null) {
            return convertToVo(leave);
        }
        // 兜底:从 LF 表查询(历史数据未迁移)
        return lfService.queryData(processNumber);
    }
}
```

## 选型建议(按场景)

| 场景 | 推荐模式 | 理由 |
|---|---|---|
| **POC / Demo** | LF | 快速验证,无需开发 |
| **请假/报销/用车等通用审批** | LF | 表单简单,LF 完全够用 |
| **SaaS 多租户** | LF(外部表单模式) | 业务方零代码配置,表单复用 |
| **HR 系统(调薪/入职)** | DIY | 需调用 HR 接口校验薪资 |
| **财务系统(预算审批)** | DIY | 需调用预算系统 + 复杂计算 |
| **CRM 系统(合同审批)** | DIY | 需调用 CRM API + 字段联动 |
| **工单系统(派单)** | DIY | 需根据工单类型动态路由 + SLA 计算 |
| **混合场景** | LF + DIY 并存 | AntFlow 支持两种模式同时运行 |

## 混合部署最佳实践

AntFlow 支持同一应用中 LF 与 DIY 流程并存:

<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 920 280" font-family="-apple-system, BlinkMacSystemFont, 'Segoe UI', sans-serif">
  <rect x="20" y="20" width="880" height="240" rx="10" fill="#f1f5f9" stroke="#475569" stroke-width="2"/>
  <text x="460" y="46" text-anchor="middle" font-size="14" font-weight="700" fill="#0f172a">混合部署架构</text>

  <!-- 一个 AntFlow 实例 -->
  <rect x="40" y="70" width="840" height="60" rx="8" fill="#1e293b"/>
  <text x="460" y="108" text-anchor="middle" font-size="13" font-weight="700" fill="#fff">一个 AntFlow 实例(jar 包)</text>

  <!-- 流程分类 -->
  <rect x="40" y="150" width="200" height="100" rx="8" fill="#dbeafe" stroke="#3b82f6" stroke-width="2"/>
  <text x="140" y="174" text-anchor="middle" font-size="11" font-weight="700" fill="#1e40af">LF 流程分类</text>
  <text x="60" y="196" font-size="10" fill="#1e3a8a">• 请假申请</text>
  <text x="60" y="212" font-size="10" fill="#1e3a8a">• 用车申请</text>
  <text x="60" y="228" font-size="10" fill="#1e3a8a">• 领用申请</text>
  <text x="60" y="244" font-size="10" fill="#1e3a8a">• POC 临时流程</text>

  <rect x="260" y="150" width="200" height="100" rx="8" fill="#dcfce7" stroke="#16a34a" stroke-width="2"/>
  <text x="360" y="174" text-anchor="middle" font-size="11" font-weight="700" fill="#14532d">DIY 流程分类</text>
  <text x="280" y="196" font-size="10" fill="#14532d">• 调薪审批</text>
  <text x="280" y="212" font-size="10" fill="#14532d">• 合同审批</text>
  <text x="280" y="228" font-size="10" fill="#14532d">• 预算审批</text>
  <text x="280" y="244" font-size="10" fill="#14532d">• 派单流程</text>

  <rect x="480" y="150" width="200" height="100" rx="8" fill="#fef3c7" stroke="#d97706" stroke-width="2"/>
  <text x="580" y="174" text-anchor="middle" font-size="11" font-weight="700" fill="#92400e">混合表单(高级)</text>
  <text x="500" y="196" font-size="10" fill="#78350f">• LF + 自定义 Hook</text>
  <text x="500" y="212" font-size="10" fill="#78350f">• 实现 LFFormOperation</text>
  <text x="500" y="228" font-size="10" fill="#78350f">  Adaptor 覆盖特定 formCode</text>
  <text x="500" y="244" font-size="10" fill="#78350f">• 保留 LF 表单 + 业务逻辑</text>

  <rect x="700" y="150" width="180" height="100" rx="8" fill="#fce7f3" stroke="#db2777" stroke-width="2"/>
  <text x="790" y="174" text-anchor="middle" font-size="11" font-weight="700" fill="#9d174d">外部流程</text>
  <text x="720" y="196" font-size="10" fill="#831843">• 三方系统接入</text>
  <text x="720" y="212" font-size="10" fill="#831843">• SaaS 流程</text>
  <text x="720" y="228" font-size="10" fill="#831843">• 业务方独立配置</text>
</svg>

## 章节导航

- [低代码流程总览](/lowcode/lowcode-overview) — 低代码在 AntFlow 中的定位
- [低代码表单引擎](/lowcode/lowcode-form) — VForm3 集成与表单权限详解
- [低代码表单设计](/workflow-design/form-design) — 表单设计器操作手册
- [节点类型详解](/workflow-design/node-types) — 12 种节点类型
- [扩展审批人来源](/dev-guide/extend-approver) — 通过 SPI 扩展审批人来源
- [集成现有系统](/dev-guide/integrate-existing) — 接入企业用户/角色系统
