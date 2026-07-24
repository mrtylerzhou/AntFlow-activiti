# 低代码表单引擎

> 本章是 AntFlow 低代码能力的核心技术剖析:从 VForm3 集成、字段类型系统、表单权限模型、版本管理、数据存储到 SPI 扩展点全面解读,帮助你理解低代码流程的运行机制,并为二次开发提供参考。

## 整体架构

<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 920 540" font-family="-apple-system, BlinkMacSystemFont, 'Segoe UI', sans-serif">
  <defs>
    <marker id="arrLf1" viewBox="0 0 10 10" refX="9" refY="5" markerWidth="6" markerHeight="6" orient="auto"><path d="M0,0 L10,5 L0,10 z" fill="#475569"/></marker>
    <linearGradient id="lfA1" x1="0" y1="0" x2="0" y2="1"><stop offset="0%" stop-color="#dbeafe"/><stop offset="100%" stop-color="#bfdbfe"/></linearGradient>
    <linearGradient id="lfA2" x1="0" y1="0" x2="0" y2="1"><stop offset="0%" stop-color="#dcfce7"/><stop offset="100%" stop-color="#bbf7d0"/></linearGradient>
    <linearGradient id="lfA3" x1="0" y1="0" x2="0" y2="1"><stop offset="0%" stop-color="#fef3c7"/><stop offset="100%" stop-color="#fde68a"/></linearGradient>
    <linearGradient id="lfA4" x1="0" y1="0" x2="0" y2="1"><stop offset="0%" stop-color="#fce7f3"/><stop offset="100%" stop-color="#fbcfe8"/></linearGradient>
    <linearGradient id="lfA5" x1="0" y1="0" x2="0" y2="1"><stop offset="0%" stop-color="#f1f5f9"/><stop offset="100%" stop-color="#e2e8f0"/></linearGradient>
  </defs>

  <!-- 前端层 -->
  <rect x="20" y="20" width="880" height="100" rx="10" fill="url(#lfA1)" stroke="#3b82f6" stroke-width="2"/>
  <text x="460" y="44" text-anchor="middle" font-size="14" font-weight="700" fill="#1e40af">前端层(antflow-vue)</text>

  <rect x="40" y="60" width="200" height="48" rx="6" fill="#fff" stroke="#3b82f6"/>
  <text x="140" y="80" text-anchor="middle" font-size="11" font-weight="600" fill="#1e3a8a">v-form-designer</text>
  <text x="140" y="96" text-anchor="middle" font-size="10" fill="#475569">VForm3 设计器(拖拽)</text>

  <rect x="252" y="60" width="200" height="48" rx="6" fill="#fff" stroke="#3b82f6"/>
  <text x="352" y="80" text-anchor="middle" font-size="11" font-weight="600" fill="#1e3a8a">v-form-render</text>
  <text x="352" y="96" text-anchor="middle" font-size="10" fill="#475569">VForm3 渲染器(运行时)</text>

  <rect x="464" y="60" width="200" height="48" rx="6" fill="#fff" stroke="#3b82f6"/>
  <text x="564" y="80" text-anchor="middle" font-size="11" font-weight="600" fill="#1e3a8a">FormPermConf.vue</text>
  <text x="564" y="96" text-anchor="middle" font-size="10" fill="#475569">字段权限配置 R/E/H</text>

  <rect x="676" y="60" width="200" height="48" rx="6" fill="#fff" stroke="#3b82f6"/>
  <text x="776" y="80" text-anchor="middle" font-size="11" font-weight="600" fill="#1e3a8a">lowcodeApi.js</text>
  <text x="776" y="96" text-anchor="middle" font-size="10" fill="#475569">REST API 封装</text>

  <!-- Controller -->
  <rect x="20" y="140" width="880" height="56" rx="10" fill="url(#lfA4)" stroke="#db2777" stroke-width="2"/>
  <text x="460" y="162" text-anchor="middle" font-size="13" font-weight="700" fill="#9d174d">LowCodeFlowController (@RequestMapping("/lowcode"))</text>
  <text x="460" y="180" text-anchor="middle" font-size="10" fill="#831843">12 个 REST 端点 · 表单 CRUD + 流程关联 + 引用统计</text>

  <!-- Service 层 -->
  <rect x="20" y="216" width="880" height="160" rx="10" fill="url(#lfA2)" stroke="#16a34a" stroke-width="2"/>
  <text x="460" y="240" text-anchor="middle" font-size="14" font-weight="700" fill="#155e2f">业务服务层(antflow-engine)</text>

  <rect x="40" y="256" width="200" height="56" rx="6" fill="#fff" stroke="#16a34a"/>
  <text x="140" y="276" text-anchor="middle" font-size="11" font-weight="600" fill="#155e2f">LfFormManageBizServiceImpl</text>
  <text x="140" y="294" text-anchor="middle" font-size="10" fill="#475569">独立表单版本管理</text>
  <text x="140" y="308" text-anchor="middle" font-size="10" fill="#475569">formCode 家族生成(LFFM-XXXXX)</text>

  <rect x="252" y="256" width="200" height="56" rx="6" fill="#fff" stroke="#16a34a"/>
  <text x="352" y="276" text-anchor="middle" font-size="11" font-weight="600" fill="#155e2f">LFFormDataPreProcessor</text>
  <text x="352" y="294" text-anchor="middle" font-size="10" fill="#475569">设计期字段拆分/合并</text>
  <text x="352" y="308" text-anchor="middle" font-size="10" fill="#475569">preWriteProcess / preReadProcess</text>

  <rect x="464" y="256" width="200" height="56" rx="6" fill="#fff" stroke="#16a34a"/>
  <text x="564" y="276" text-anchor="middle" font-size="11" font-weight="600" fill="#155e2f">LowFlowApprovalService</text>
  <text x="564" y="294" text-anchor="middle" font-size="10" fill="#475569">运行期通用服务</text>
  <text x="564" y="308" text-anchor="middle" font-size="10" fill="#475569">@ActivitiServiceAnno(LOWFLOW_FORM_CODE)</text>

  <rect x="676" y="256" width="200" height="56" rx="6" fill="#fff" stroke="#16a34a"/>
  <text x="776" y="276" text-anchor="middle" font-size="11" font-weight="600" fill="#155e2f">LFFormDataRuntimeHelper</text>
  <text x="776" y="294" text-anchor="middle" font-size="10" fill="#475569">运行期助手</text>
  <text x="776" y="308" text-anchor="middle" font-size="10" fill="#475569">条件提取/表单取人/字段反射</text>

  <rect x="40" y="328" width="836" height="40" rx="6" fill="#fef3c7" stroke="#d97706"/>
  <text x="460" y="354" text-anchor="middle" font-size="11" font-weight="600" fill="#92400e">SPI 扩展点:LFFormOperationAdaptor(实现 @Service 名 = formCode,覆盖默认通用服务)</text>

  <!-- 数据层 -->
  <rect x="20" y="396" width="430" height="124" rx="10" fill="url(#lfA3)" stroke="#d97706" stroke-width="2"/>
  <text x="235" y="420" text-anchor="middle" font-size="13" font-weight="700" fill="#92400e">设计期表(配置态)</text>
  <text x="40" y="444" font-size="10" font-weight="600" fill="#78350f">t_bpmn_conf_lf_formdata</text>
  <text x="40" y="458" font-size="10" fill="#78350f">  表单 JSON 主体(formdata 字段)</text>
  <text x="40" y="476" font-size="10" font-weight="600" fill="#78350f">t_bpmn_conf_lf_formdata_field</text>
  <text x="40" y="490" font-size="10" fill="#78350f">  字段元数据(field_id/type/condition 标记)</text>
  <text x="40" y="508" font-size="10" font-weight="600" fill="#78350f">t_bpmn_node_lf_formdata_field_control</text>
  <text x="40" y="520" font-size="10" fill="#78350f">  节点级字段权限(R/E/H)</text>

  <rect x="470" y="396" width="430" height="124" rx="10" fill="url(#lfA5)" stroke="#475569" stroke-width="2"/>
  <text x="685" y="420" text-anchor="middle" font-size="13" font-weight="700" fill="#1e293b">运行期表(实例态)</text>
  <text x="490" y="444" font-size="10" font-weight="600" fill="#1e293b">t_lf_main</text>
  <text x="490" y="458" font-size="10" fill="#475569">  每次表单提交一行(id + form_code + conf_id)</text>
  <text x="490" y="476" font-size="10" font-weight="600" fill="#1e293b">t_lf_main_field</text>
  <text x="490" y="490" font-size="10" fill="#475569">  每个字段一行,按类型存 4 列之一:</text>
  <text x="490" y="504" font-size="10" fill="#475569">  field_value / _number / _dt / _text</text>
  <text x="490" y="518" font-size="10" fill="#475569">  支持水平分表(lf.main.table.count 等)</text>

  <!-- 箭头 -->
  <line x1="460" y1="120" x2="460" y2="140" stroke="#475569" stroke-width="2" marker-end="url(#arrLf1)"/>
  <line x1="460" y1="196" x2="460" y2="216" stroke="#475569" stroke-width="2" marker-end="url(#arrLf1)"/>
  <line x1="235" y1="376" x2="235" y2="396" stroke="#475569" stroke-width="2" marker-end="url(#arrLf1)"/>
  <line x1="685" y1="376" x2="685" y2="396" stroke="#475569" stroke-width="2" marker-end="url(#arrLf1)"/>
</svg>

## VForm3 集成机制

### 库文件与注册

AntFlow 没有用 npm 安装 VForm3,而是以 **UMD 包**形式集成,便于离线部署与版本锁定:

- 库文件:[antflow-vue/src/lib/vform/designer.umd.js](file:///d:/projects/jimuoffice/antflow-vue/src/lib/vform/designer.umd.js)
- 样式文件:[antflow-vue/src/lib/vform/designer.style.css](file:///d:/projects/jimuoffice/antflow-vue/src/lib/vform/designer.style.css)

在 [antflow-vue/src/main.js](file:///d:/projects/jimuoffice/antflow-vue/src/main.js) 中全局注册:

```javascript
import VForm3 from "@/./lib/vform/designer.umd.js";
import "./lib/vform/designer.style.css";
app.use(VForm3);
```

注册后即可在任意 Vue 组件中使用两个核心组件:

| 组件 | 用途 | 关键 API |
|---|---|---|
| `<v-form-designer>` | 表单设计器(拖拽编辑) | `clearDesigner()` / `loadFormJson(json)` / `getFormJson()` |
| `<v-form-render>` | 表单渲染器(运行时) | `setFormJson(json)` / `getFormData()` / `setFormData(obj)` / `disableForm()` |

### 设计器入口

独立表单设计器入口 [antflow-vue/src/views/system/lfForm/design.vue](file:///d:/projects/jimuoffice/antflow-vue/src/views/system/lfForm/design.vue):

```vue
<template>
  <div id="form-designer-container" class="lf-form-container">
    <v-form-designer ref="formDesign"></v-form-designer>
  </div>
</template>

<script setup>
import { ref, onMounted } from "vue";
import { useRoute } from "vue-router";
import { getFormById, saveForm } from "@/api/workflow/lowcodeApi";

const route = useRoute();
const formDesign = ref(null);
const formCode = ref(route.query.formCode);
const formName = ref(route.query.formName || "");
const existingId = ref(route.query.id);

// 回显已有表单
onMounted(async () => {
  if (existingId.value) {
    const res = await getFormById(existingId.value);
    if (res.code === 200 && res.data?.formdata) {
      formDesign.value.clearDesigner();
      formDesign.value.designer.loadFormJson(JSON.parse(res.data.formdata));
    }
  }
});

// 保存表单
const handleSave = async () => {
  const formJson = formDesign.value.getFormJson();
  const data = {
    formCode: formCode.value,    // 有则新版本,无则新建家族
    formName: formName.value.trim(),
    formdata: JSON.stringify(formJson),
  };
  await saveForm(data);
};
</script>
```

注意:**表单内容字段名为 `formdata`**(单字段,JSON 字符串),不要写成 `form_view_json` 之类的别名。

## REST API:LowCodeFlowController

[LowCodeFlowController.java](file:///d:/projects/jimuoffice/antflow-engine/src/main/java/org/openoa/engine/bpmnconf/controller/LowCodeFlowController.java) 是低代码模块的统一入口,共 12 个端点:

| 方法 | 路径 | 用途 |
|---|---|---|
| GET | `/lowcode/getLowCodeFlowFormCodes` | 获取所有 LF FormCode(流程设计选择) |
| POST | `/lowcode/getLFFormCodePageList` | LF FormCode 分页列表(模板管理) |
| POST | `/lowcode/getLFActiveFormCodePageList` | 已启用流程的 LF FormCode(发起页) |
| GET | `/lowcode/getformDataByFormCode` | 按 formCode 获取表单 JSON |
| GET | `/lowcode/getStartFormData` | **发起页表单数据**(兼容内联/外部) |
| POST | `/lowcode/createLowCodeFormCode` | 新建 LF FormCode |
| POST | `/lowcode/form/listPage` | 独立表单分页查询(家族分组) |
| GET | `/lowcode/form/{id}` | 按 id 查询表单版本(回显) |
| POST | `/lowcode/form/save` | 保存表单(新建家族/版本) |
| DELETE | `/lowcode/form/{id}` | 软删除版本(被引用时拒绝) |
| GET | `/lowcode/form/history` | 查询某家族所有版本 |
| PUT | `/lowcode/form/effective/{id}` | 生效指定版本(同族互斥) |
| GET | `/lowcode/form/references/{formdataId}` | 查询引用此版本的流程列表 |

### 关键端点:getStartFormData

发起流程时,前端调用此端点获取表单数据,后端根据 `USE_EXTERNAL_FORM` flag 返回不同结构:

```java
@GetMapping("/getStartFormData")
public Result getStartFormData(String processNumber, String formCode) {
    BpmnConfVo confVo = bpmnConfBizService.getConfVoByFormCode(formCode);
    StartFormDataResultVo result = new StartFormDataResultVo();
    
    boolean useExternal = BpmnConfFlagsEnum.USE_EXTERNAL_FORM
            .flagsContainsCurrent(confVo.getExtraFlags());
    result.setUseExternalForm(useExternal);
    
    if (useExternal) {
        // 外部模式:多表单列表
        result.setLfFormdataList(confVo.getLfFormdataList());
    } else {
        // 内联模式:单表单 JSON
        result.setLfFormData(confVo.getLfFormData());
    }
    
    // 提取发起人节点(nodeType=1)的字段权限
    if (confVo.getNodes() != null) {
        for (BpmnNodeVo node : confVo.getNodes()) {
            if (NodeTypeEnum.NODE_TYPE_START.getCode().equals(node.getNodeType())) {
                result.setLfFieldControlVOs(node.getLfFieldControlVOs());
                result.setFormHidden(node.getFormHidden());
                break;
            }
        }
    }
    return Result.success(result);
}
```

## 字段类型系统

### LFFieldTypeEnum

[LFFieldTypeEnum.java](file:///d:/projects/jimuoffice/antflow-base/src/main/java/org/openoa/base/constant/enums/LFFieldTypeEnum.java) 定义了 7 种字段存储类型:

| Code | 枚举 | 中文名 | 存储字段 | 适用场景 |
|:---:|---|---|---|---|
| 1 | STRING | 字符串 | `field_value` (varchar 2000) | 单行文本/下拉/单选/多选/级联 |
| 2 | NUMBER | 数字 | `field_value_number` (double 14,2) | 数字/滑块/评分 |
| 3 | DATE | 日期 | `field_value_dt` (timestamp) | 日期控件 |
| 4 | DATE_TIME | 日期时间 | `field_value_dt` (timestamp) | 日期时间/时间范围 |
| 5 | TEXT | 长字符串 | `field_value_text` (longtext) | 多行文本/富文本 |
| 6 | BOOLEAN | 布尔 | `field_value` (字符串 "true"/"false") | 开关 |
| 7 | BLOB | 二进制 | (未实现) | 文件附件(预留) |

### VForm3 控件类型映射

VForm3 控件 type 到 LFFieldTypeEnum 的映射在 `LfFormWidgetParser.getFieldTypeByTypeString` 中实现:

| VForm3 type | LFFieldTypeEnum |
|---|---|
| `input` | STRING |
| `textarea` | TEXT |
| `number`, `slider` | NUMBER |
| `date` | DATE |
| `date-range`, `time`, `time-range` | DATE_TIME |
| `switch` | BOOLEAN |
| `select`, `radio`, `checkbox`, `cascader`, `tree-select` | STRING |
| `color-picker`, `rate`, `picture-upload`, `file-upload`, `icon-picker`, `transfer` | STRING |
| `richtext-editor` | TEXT |
| `grid`, `table`, `tab`, `sub-form`, `container` | (容器,递归处理子字段) |

### 字段解析器:LfFormWidgetParser

`LfFormWidgetParser.parseFields(formdataJson, ...)` 递归解析 VForm3 JSON,提取字段元数据并写入 `t_bpmn_conf_lf_formdata_field` 表:

- 处理容器控件(grid/table/tab/sub-form):递归遍历子字段
- 维护 `parent_field_id` / `parent_field_name`:支持嵌套
- 标记 `is_condition`:从流程设计的条件配置中识别哪些字段是流程条件字段
- 生成 `field_id`:VForm3 控件的 id(全局唯一)

## 表单权限模型(R/E/H)

权限值定义在 [StringConstants.java](file:///d:/projects/jimuoffice/antflow-base/src/main/java/org/openoa/base/constant/StringConstants.java):

| 权限 | 值 | 前端行为 | 适用节点 |
|:---:|:---:|---|---|
| 只读 | `R` | `w.options.disabled = true` | 审批节点查看字段 |
| 可编辑 | `E` | `w.options.readonly = false` | 重新提交节点修改字段 |
| 隐藏 | `H` | 字段类型改为 input+text,值改为 `******`,disabled | 财务字段对非财务审批人隐藏 |

### 权限配置组件

[FormPermConf.vue](file:///d:/projects/jimuoffice/antflow-vue/src/components/Workflow/drawer/permConfig/FormPermConf.vue) 提供两种布局:

<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 920 280" font-family="-apple-system, BlinkMacSystemFont, 'Segoe UI', sans-serif">
  <rect x="20" y="20" width="430" height="240" rx="10" fill="#dbeafe" stroke="#3b82f6" stroke-width="2"/>
  <text x="235" y="46" text-anchor="middle" font-size="13" font-weight="700" fill="#1e40af">内联表单模式(单表单)</text>
  <text x="40" y="74" font-size="11" font-weight="600" fill="#1e3a8a">单表格布局</text>
  <rect x="40" y="84" width="390" height="30" fill="#1e3a8a"/>
  <text x="60" y="104" font-size="10" font-weight="600" fill="#fff">表单字段</text>
  <text x="240" y="104" font-size="10" font-weight="600" text-anchor="middle" fill="#fff">R</text>
  <text x="290" y="104" font-size="10" font-weight="600" text-anchor="middle" fill="#fff">E</text>
  <text x="340" y="104" font-size="10" font-weight="600" text-anchor="middle" fill="#fff">H</text>
  <text x="390" y="104" font-size="10" font-weight="600" text-anchor="middle" fill="#fff">全选</text>
  
  <rect x="40" y="114" width="390" height="24" fill="#fff" stroke="#bfdbfe"/>
  <text x="60" y="130" font-size="10" fill="#1e3a8a">申请人姓名</text>
  <text x="240" y="130" font-size="10" text-anchor="middle" fill="#16a34a">●</text>
  <text x="290" y="130" font-size="10" text-anchor="middle" fill="#94a3b8">○</text>
  <text x="340" y="130" font-size="10" text-anchor="middle" fill="#94a3b8">○</text>
  
  <rect x="40" y="138" width="390" height="24" fill="#f8fafc" stroke="#bfdbfe"/>
  <text x="60" y="154" font-size="10" fill="#1e3a8a">请假天数</text>
  <text x="240" y="154" font-size="10" text-anchor="middle" fill="#94a3b8">○</text>
  <text x="290" y="154" font-size="10" text-anchor="middle" fill="#16a34a">●</text>
  <text x="340" y="154" font-size="10" text-anchor="middle" fill="#94a3b8">○</text>
  
  <rect x="40" y="162" width="390" height="24" fill="#fff" stroke="#bfdbfe"/>
  <text x="60" y="178" font-size="10" fill="#1e3a8a">薪资级别</text>
  <text x="240" y="178" font-size="10" text-anchor="middle" fill="#94a3b8">○</text>
  <text x="290" y="178" font-size="10" text-anchor="middle" fill="#94a3b8">○</text>
  <text x="340" y="178" font-size="10" text-anchor="middle" fill="#16a34a">●</text>
  
  <text x="40" y="208" font-size="10" fill="#475569">表头支持"全选"批量设置</text>
  <text x="40" y="226" font-size="10" fill="#475569">默认值:发起人节点全 E,其他节点全 R</text>
  <text x="40" y="244" font-size="10" fill="#475569">权限粒度:字段级(含容器内字段)</text>

  <rect x="470" y="20" width="430" height="240" rx="10" fill="#dcfce7" stroke="#16a34a" stroke-width="2"/>
  <text x="685" y="46" text-anchor="middle" font-size="13" font-weight="700" fill="#155e2f">外部表单模式(多表单)</text>
  <text x="490" y="74" font-size="11" font-weight="600" fill="#14532d">多卡片布局(每个表单一个 Card)</text>
  
  <rect x="490" y="84" width="390" height="76" rx="6" fill="#fff" stroke="#16a34a"/>
  <rect x="500" y="92" width="370" height="22" fill="#dcfce7"/>
  <text x="510" y="108" font-size="11" font-weight="600" fill="#14532d">员工信息表</text>
  <text x="730" y="108" font-size="10" fill="#78350f" text-anchor="start">☐ 整表隐藏</text>
  <text x="510" y="128" font-size="10" fill="#475569">姓名(R) · 工号(R) · 部门(R) · 职级(E)</text>
  <text x="510" y="146" font-size="10" fill="#475569">入职日期(R) · 联系方式(H)</text>
  
  <rect x="490" y="170" width="390" height="76" rx="6" fill="#fff" stroke="#16a34a"/>
  <rect x="500" y="178" width="370" height="22" fill="#dcfce7"/>
  <text x="510" y="194" font-size="11" font-weight="600" fill="#14532d">用车申请表</text>
  <text x="730" y="194" font-size="10" fill="#78350f" text-anchor="start">☐ 整表隐藏</text>
  <text x="510" y="214" font-size="10" fill="#475569">出发地(E) · 目的地(E) · 用车时间(E)</text>
  <text x="510" y="232" font-size="10" fill="#475569">车牌号(R) · 司机(R)</text>
</svg>

### 运行时权限应用

[formRender.vue](file:///d:/projects/jimuoffice/antflow-vue/src/components/Workflow/DynamicForm/formRender.vue) 中的 `handlerFn(w)` 递归遍历所有 `formItemFlag` 字段(支持 grid/table/tab/sub-form/container 容器):

```javascript
const handlerFn = (w) => {
  if (!w.formItemFlag && !w.columns && !w.tabs && !w.rows) return;
  
  // 容器控件递归处理
  if (w.type === 'grid') {
    w.cols.forEach(col => col.widgetList.forEach(child => handlerFn(child)));
    return;
  }
  if (w.type === 'tab') {
    w.tabs.forEach(tab => tab.widgetList.forEach(child => handlerFn(child)));
    return;
  }
  // ... table, sub-form, container 类似
  
  // 字段级权限应用
  const perm = fieldPermMap[w.id];  // 从节点 lfFieldControlVOs 取
  if (perm === 'R') {
    w.options.disabled = true;
  } else if (perm === 'E') {
    w.options.readonly = false;
    w.options.disabled = false;
  } else if (perm === 'H') {
    w.type = 'input';
    w.options.type = 'text';
    w.options.defaultValue = '******';
    w.options.disabled = true;
  }
};
```

## 版本管理

### formCode 家族命名规则

[LfFormManageBizServiceImpl.java](file:///d:/projects/jimuoffice/antflow-engine/src/main/java/org/openoa/engine/bpmnconf/service/biz/LfFormManageBizServiceImpl.java) 中定义:

```java
private static final String FORM_CODE_PREFIX = "LFFM";
private static final int FORM_CODE_SEQ_LEN = 5;
private static final String FORM_CODE_FORMAT = "%0" + FORM_CODE_SEQ_LEN + "d";
private static final Pattern FORM_CODE_PATTERN = 
    Pattern.compile(".*-([0-9]{" + FORM_CODE_SEQ_LEN + "})");
// 生成结果:LFFM-00001, LFFM-00002, ...
```

### 版本管理逻辑

<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 920 380" font-family="-apple-system, BlinkMacSystemFont, 'Segoe UI', sans-serif">
  <defs>
    <marker id="arrVer" viewBox="0 0 10 10" refX="9" refY="5" markerWidth="6" markerHeight="6" orient="auto"><path d="M0,0 L10,5 L0,10 z" fill="#475569"/></marker>
  </defs>

  <text x="460" y="24" text-anchor="middle" font-size="14" font-weight="700" fill="#0f172a">独立表单版本管理流程</text>

  <!-- 新建家族 -->
  <rect x="20" y="50" width="280" height="100" rx="8" fill="#dbeafe" stroke="#3b82f6" stroke-width="2"/>
  <text x="160" y="74" text-anchor="middle" font-size="12" font-weight="700" fill="#1e40af">① 新建家族</text>
  <text x="40" y="96" font-size="10" fill="#1e3a8a">输入:formName + formdata(JSON)</text>
  <text x="40" y="114" font-size="10" fill="#1e3a8a">调用:POST /lowcode/form/save</text>
  <text x="40" y="132" font-size="10" fill="#1e3a8a">formCode = null → 自动生成</text>
  <text x="40" y="146" font-size="10" font-weight="600" fill="#16a34a">结果:LFFM-00001 v1(默认生效)</text>

  <!-- 新建版本 -->
  <rect x="320" y="50" width="280" height="100" rx="8" fill="#fef3c7" stroke="#d97706" stroke-width="2"/>
  <text x="460" y="74" text-anchor="middle" font-size="12" font-weight="700" fill="#92400e">② 编辑产生新版本</text>
  <text x="340" y="96" font-size="10" fill="#78350f">输入:formCode + 新 formdata</text>
  <text x="340" y="114" font-size="10" fill="#78350f">调用:POST /lowcode/form/save</text>
  <text x="340" y="132" font-size="10" fill="#78350f">formCode 已存在 → 新版本</text>
  <text x="340" y="146" font-size="10" font-weight="600" fill="#dc2626">结果:LFFM-00001 v2(默认不生效)</text>

  <!-- 生效版本 -->
  <rect x="620" y="50" width="280" height="100" rx="8" fill="#dcfce7" stroke="#16a34a" stroke-width="2"/>
  <text x="760" y="74" text-anchor="middle" font-size="12" font-weight="700" fill="#155e2f">③ 生效指定版本</text>
  <text x="640" y="96" font-size="10" fill="#14532d">输入:id(版本主键)</text>
  <text x="640" y="114" font-size="10" fill="#14532d">调用:PUT /lowcode/form/effective/{id}</text>
  <text x="640" y="132" font-size="10" fill="#14532d">同族其他生效版本自动置为 0</text>
  <text x="640" y="146" font-size="10" font-weight="600" fill="#16a34a">结果:同族同时只有一个生效</text>

  <line x1="300" y1="100" x2="320" y2="100" stroke="#475569" stroke-width="2" marker-end="url(#arrVer)"/>
  <line x1="600" y1="100" x2="620" y2="100" stroke="#475569" stroke-width="2" marker-end="url(#arrVer)"/>

  <!-- 数据表视图 -->
  <text x="460" y="180" text-anchor="middle" font-size="12" font-weight="700" fill="#0f172a">t_bpmn_conf_lf_formdata 数据示例</text>

  <rect x="20" y="200" width="880" height="160" fill="#f8fafc" stroke="#475569" stroke-width="2"/>
  <rect x="20" y="200" width="880" height="28" fill="#1e293b"/>
  <text x="40" y="218" font-size="10" font-weight="600" fill="#fff">id</text>
  <text x="120" y="218" font-size="10" font-weight="600" fill="#fff">bpmn_conf_id</text>
  <text x="260" y="218" font-size="10" font-weight="600" fill="#fff">form_code</text>
  <text x="380" y="218" font-size="10" font-weight="600" fill="#fff">form_name</text>
  <text x="540" y="218" font-size="10" font-weight="600" fill="#fff">effective_status</text>
  <text x="700" y="218" font-size="10" font-weight="600" fill="#fff">formdata</text>
  <text x="820" y="218" font-size="10" font-weight="600" fill="#fff">is_del</text>
  
  <rect x="20" y="228" width="880" height="24" fill="#fff"/>
  <text x="40" y="244" font-size="10" fill="#1e293b">1001</text>
  <text x="120" y="244" font-size="10" fill="#94a3b8">NULL</text>
  <text x="260" y="244" font-size="10" fill="#1e40af" font-weight="600">LFFM-00001</text>
  <text x="380" y="244" font-size="10" fill="#1e293b">请假申请表</text>
  <text x="540" y="244" font-size="10" fill="#94a3b8">0(未生效)</text>
  <text x="700" y="244" font-size="10" fill="#475569">{...v1 JSON...}</text>
  <text x="820" y="244" font-size="10" fill="#1e293b">0</text>
  
  <rect x="20" y="252" width="880" height="24" fill="#dcfce7"/>
  <text x="40" y="268" font-size="10" fill="#1e293b">1002</text>
  <text x="120" y="268" font-size="10" fill="#94a3b8">NULL</text>
  <text x="260" y="268" font-size="10" fill="#1e40af" font-weight="600">LFFM-00001</text>
  <text x="380" y="268" font-size="10" fill="#1e293b">请假申请表</text>
  <text x="540" y="268" font-size="10" fill="#16a34a" font-weight="600">1(生效)★</text>
  <text x="700" y="268" font-size="10" fill="#475569">{...v2 JSON...}</text>
  <text x="820" y="268" font-size="10" fill="#1e293b">0</text>
  
  <rect x="20" y="276" width="880" height="24" fill="#fff"/>
  <text x="40" y="292" font-size="10" fill="#1e293b">1003</text>
  <text x="120" y="292" font-size="10" fill="#94a3b8">NULL</text>
  <text x="260" y="292" font-size="10" fill="#1e40af" font-weight="600">LFFM-00001</text>
  <text x="380" y="292" font-size="10" fill="#1e293b">请假申请表</text>
  <text x="540" y="292" font-size="10" fill="#94a3b8">0(未生效)</text>
  <text x="700" y="292" font-size="10" fill="#475569">{...v3 JSON...}</text>
  <text x="820" y="292" font-size="10" fill="#1e293b">0</text>
  
  <rect x="20" y="300" width="880" height="24" fill="#fef3c7"/>
  <text x="40" y="316" font-size="10" fill="#94a3b8">1004</text>
  <text x="120" y="316" font-size="10" fill="#94a3b8">NULL</text>
  <text x="260" y="316" font-size="10" fill="#94a3b8">LFFM-00001</text>
  <text x="380" y="316" font-size="10" fill="#94a3b8">请假申请表(旧)</text>
  <text x="540" y="316" font-size="10" fill="#94a3b8">0(未生效)</text>
  <text x="700" y="316" font-size="10" fill="#475569">{...v0 JSON...}</text>
  <text x="820" y="316" font-size="10" fill="#dc2626">1(软删)</text>
  
  <text x="40" y="340" font-size="10" fill="#475569">说明:同一 formCode 的多个版本同时存在,但仅一个 effective_status=1</text>
  <text x="40" y="354" font-size="10" fill="#475569">软删的版本仍可被运行中流程实例读取(通过 listByIdsIgnoreDeleted)</text>
</svg>

### 生效互斥逻辑

`effective` 方法核心 SQL:

```java
// 同 formCode 其他生效版本置为非生效
lfFormdataService.update(Wrappers.<BpmnConfLfFormdata>lambdaUpdate()
        .eq(BpmnConfLfFormdata::getFormCode, formCode)
        .eq(BpmnConfLfFormdata::getEffectiveStatus, 1)
        .set(BpmnConfLfFormdata::getEffectiveStatus, 0));
// 当前版本置为生效
lfFormdataService.update(Wrappers.<BpmnConfLfFormdata>lambdaUpdate()
        .eq(BpmnConfLfFormdata::getId, id)
        .set(BpmnConfLfFormdata::getEffectiveStatus, 1));
```

### 删除保护

`delete` 方法在软删前检查引用关系:

```xml
<!-- BpmnConfMapper.xml:统计有多少生效流程引用了指定表单版本id -->
<select id="countEffectiveConfReferencingFormdata" resultType="int">
    SELECT COUNT(*) FROM t_bpmn_conf
    WHERE is_del = 0 AND effective_status = 1
      AND lf_formdata_ids IS NOT NULL
      AND FIND_IN_SET(#{formdataId}, lf_formdata_ids) > 0
</select>
```

被生效流程引用的表单版本**拒绝删除**,前端会收到错误提示。

## 内联 vs 外部表单模式

通过 [BpmnConfFlagsEnum.USE_EXTERNAL_FORM](file:///d:/projects/jimuoffice/antflow-base/src/main/java/org/openoa/base/constant/enums/BpmnConfFlagsEnum.java)(位掩码 `0b1000000` = 64)切换:

| 维度 | 内联表单模式(默认) | 外部表单模式 |
|---|---|---|
| 标志位 | `USE_EXTERNAL_FORM` 未设置 | `USE_EXTERNAL_FORM` 已设置(64) |
| 存储 | `t_bpmn_conf_lf_formdata.bpmn_conf_id` 指向流程 | `t_bpmn_conf.lf_formdata_ids`(CSV)引用表单版本 |
| 表单数量 | 1 个 | N 个(逗号分隔) |
| 表单管理 | 随流程一起管理 | 独立管理(表单管理页面) |
| 版本管理 | 跟随流程版本 | 表单独立版本,与流程解耦 |
| 适用场景 | 简单流程 | 复杂流程、表单复用、SaaS |

### 切换方式

在流程设计器"基础表单设置"步骤中勾选"使用外部表单"选项,保存时设置 `extraFlags` 位:

```java
// BpmnConfFlagsEnum.USE_EXTERNAL_FORM.flagsContainsCurrent(confVo.getExtraFlags())
// 等价于:(confVo.getExtraFlags() & 64) != 0
```

## 数据存储结构

### 设计期表

#### t_bpmn_conf_lf_formdata — 表单定义主体表

实体:[BpmnConfLfFormdata.java](file:///d:/projects/jimuoffice/antflow-base/src/main/java/org/openoa/base/entity/BpmnConfLfFormdata.java)

| 字段 | 类型 | 含义 |
|---|---|---|
| `id` | bigint | 主键 |
| `bpmn_conf_id` | bigint | 流程配置 ID(独立表单为 NULL;内联表单指向 `t_bpmn_conf.id`) |
| `form_code` | varchar(100) | 独立表单家族标识(同族各版本共享;内联表单为 NULL) |
| `form_name` | varchar(255) | 独立表单显示名(内联表单为 NULL) |
| `effective_status` | tinyint | 是否当前生效版本(0否 1是,仅独立表单使用;内联表单恒为 0) |
| `formdata` | longtext | 表单数据(JSON 格式,VForm3 配置 JSON) |
| `is_del` | tinyint | 逻辑删除(0未删 1已删) |
| `tenant_id` | varchar(255) | 多租户 ID |
| `create_user`/`create_time`/`update_user`/`update_time` | - | 审计字段 |

#### t_bpmn_conf_lf_formdata_field — 字段元数据表

实体:[BpmnConfLfFormdataField.java](file:///d:/projects/jimuoffice/antflow-base/src/main/java/org/openoa/base/entity/BpmnConfLfFormdataField.java)

| 字段 | 类型 | 含义 |
|---|---|---|
| `id` | bigint | 主键 |
| `bpmn_conf_id` | bigint | BPMN 配置 ID |
| `formdata_id` | bigint | 表单版本 ID(外部模式使用) |
| `field_id` | varchar(255) | 字段 ID(VForm3 控件 id) |
| `field_name` | varchar(255) | 字段名 |
| `field_type` | tinyint | 字段类型(LFFieldTypeEnum) |
| `is_condition` | tinyint | 是否是流程条件字段(0否 1是) |

#### t_bpmn_node_lf_formdata_field_control — 节点级字段权限表

| 字段 | 类型 | 含义 |
|---|---|---|
| `node_id` | - | 节点 ID |
| `formdata_id` | - | 表单版本 ID |
| `field_id` | - | 字段 ID |
| `field_name` | - | 字段名 |
| `field_perm` | - | 权限值(R/E/H) |

### 运行期表

#### t_lf_main — 低代码表单主表

```sql
create table t_lf_main (
    id bigint auto_increment,
    conf_id bigint null,           -- 流程配置 ID
    form_code varchar(255) null,   -- 表单代码
    is_del tinyint default 0 not null,
    tenant_id varchar(255) NOT NULL DEFAULT '',
    create_user varchar(255) null,
    create_time timestamp default current_timestamp,
    update_user varchar(255) null,
    update_time timestamp default current_timestamp ON UPDATE CURRENT_TIMESTAMP,
    primary key (id),
    KEY `t_lf_main_dx2` (`form_code`),
    KEY `t_lf_main_idx1` (`conf_id`)
) ENGINE=InnoDB comment '低代码表单主表';
```

#### t_lf_main_field — 低代码表单字段值表

```sql
create table t_lf_main_field (
    id bigint auto_increment,
    main_id bigint not null,       -- 关联 t_lf_main.id
    form_code varchar(255) null,
    field_id varchar(255) null,
    field_name varchar(255) null,
    parent_field_id varchar(255) null,    -- 父字段 ID(支持容器嵌套)
    parent_field_name varchar(255) null,
    field_value varchar(2000) null,       -- 字符串值
    field_value_number double(14,2) null, -- 数值
    field_value_dt timestamp null,        -- 日期时间
    field_value_text longtext null,       -- 长文本
    sort int default 0 not null,
    formdata_id bigint null,              -- 表单版本 ID(多表单模式)
    is_del tinyint default 0 not null,
    tenant_id varchar(255) NOT NULL DEFAULT '',
    primary key (id),
    KEY `t_lf_main_field_dx1` (`main_id`),
    KEY `t_lf_main_field_idx2` (`form_code`),
    KEY `t_lf_main_field_idx3` (`field_id`),
    KEY `idx_lf_main_field_formdata_id` (`formdata_id`)
) ENGINE=InnoDB comment '低代码表单字段值表';
```

### 水平分表支持

`t_lf_main` 与 `t_lf_main_field` 支持水平分表,适合海量数据场景:

| 配置项 | 默认值 | 说明 |
|---|:---:|---|
| `lf.main.table.count` | 2 | 主表分表数量(索引从 0 开始,如 `t_lf_main_0`, `t_lf_main_1`) |
| `lf.field.table.count` | 10 | 字段表分表数量 |

**注意**:分表需手动创建对应表名,引擎不会自动建表。分表路由基于 `main_id` hash。

## SPI 扩展点:LFFormOperationAdaptor

### 接口定义

[LFFormOperationAdaptor.java](file:///d:/projects/jimuoffice/antflow-base/src/main/java/org/openoa/base/interf/LFFormOperationAdaptor.java):

```java
/**
 * 此类主要用于用特定低代码表流程的一些自定义行为
 * 如果你熟悉FormOperationAdaptor和FormOperationAdaptor不同的是,
 * 这里实现类使用普通的@Service而不是使用定制的@ActivitiServiceAnno
 * 实现类的@Service需要指定名称,需要和低代码表单的formCode一样
 */
public interface LFFormOperationAdaptor<T extends UDLFApplyVo> extends FormOperationAdaptor<T> {
}
```

### 关键差异

| 维度 | DIY 模式 FormOperationAdaptor | LF 模式 LFFormOperationAdaptor |
|---|---|---|
| 注册方式 | `@ActivitiServiceAnno(svcName = "xxx")` | `@Service("xxx")`(普通 Spring) |
| 路由依据 | `t_bpmn_conf.form_code` | 表单家族 `formCode` |
| 默认实现 | 无,业务方必须实现 | 有(LowFlowApprovalService) |
| 覆盖方式 | 直接实现接口 | `@Service` 名 = formCode 即覆盖 |

### 自定义示例

假设你的低代码流程 `formCode = LEAVE_LF_WMA`,需要审批前调用企业 HR 接口校验年假余额:

```java
package com.yourcompany.workflow.lf;

import org.openoa.base.interf.LFFormOperationAdaptor;
import org.openoa.base.vo.UDLFApplyVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("LEAVE_LF_WMA")  // bean 名必须等于低代码表单的 formCode
public class LeaveLFFormAdaptor implements LFFormOperationAdaptor<UDLFApplyVo> {

    @Autowired
    private YourHRService hrService;  // 企业 HR 接口

    @Override
    public void onBeforeSubmit(UDLFApplyVo vo) {
        // 1. 从 vo.lfFields 取请假天数
        double days = extractDaysFromLfFields(vo.getLfFields());
        String userId = vo.getStartUserMo().getCreateUserId();
        
        // 2. 调 HR 接口校验
        if (!hrService.checkLeaveBalance(userId, days)) {
            throw new RuntimeException("年假余额不足");
        }
    }

    @Override
    public void onAfterApprove(UDLFApplyVo vo) {
        // 审批通过后扣减年假
        double days = extractDaysFromLfFields(vo.getLfFields());
        String userId = vo.getStartUserMo().getCreateUserId();
        hrService.deductLeave(userId, days);
    }

    private double extractDaysFromLfFields(List<LfFieldVo> fields) {
        return fields.stream()
            .filter(f -> "leave_days".equals(f.getFieldId()))
            .mapToDouble(f -> Double.parseDouble(f.getFieldValue()))
            .findFirst()
            .orElse(0);
    }
}
```

注册后,AntFlow 引擎在处理 `formCode = LEAVE_LF_WMA` 的低代码流程时,会优先调用你的 `LeaveLFFormAdaptor`,而非默认的 `LowFlowApprovalService`。

### 参考实现

- 默认实现:[LowFlowApprovalService](file:///d:/projects/jimuoffice/antflow-engine/src/main/java/org/openoa/engine/lowflow/service/LowFlowApprovalService.java)
- 示例实现:[TestLfFormOperationAdaptor](file:///d:/projects/jimuoffice/antflow-engine/src/main/java/org/openoa/engine/lowflow/service/TestLfFormOperationAdaptor.java)
- 运行期助手:[LFFormDataRuntimeHelper](file:///d:/projects/jimuoffice/antflow-engine/src/main/java/org/openoa/engine/lowflow/service/LFFormDataRuntimeHelper.java)

## 设计期字段处理:LFFormDataPreProcessor

[LFFormDataPreProcessor.java](file:///d:/projects/jimuoffice/antflow-engine/src/main/java/org/openoa/engine/lowflow/service/LFFormDataPreProcessor.java) 在保存/读取流程配置时做字段拆分/合并:

### preWriteProcess(保存流程配置时)

```java
public void preWriteProcess(BpmnConfVo confVo) {
    // 1. 检查 isLowCodeFlow==1 或 USE_AUXILIARY_FORM flag
    if (!confVo.isLowCodeFlow() && !USE_AUXILIARY_FORM.flagsContainsCurrent(confVo.getExtraFlags())) {
        return;
    }
    
    // 2. 外部表单模式跳过(表单由独立模块维护)
    if (USE_EXTERNAL_FORM.flagsContainsCurrent(confVo.getExtraFlags())) {
        return;
    }
    
    // 3. 内联模式:构造 BpmnConfLfFormdata 保存
    BpmnConfLfFormdata formdata = new BpmnConfLfFormdata();
    formdata.setBpmnConfId(confVo.getId());
    formdata.setFormdata(confVo.getLfForm());  // VForm3 JSON
    lfFormdataService.save(formdata);
    
    // 4. 调用 LfFormWidgetParser.parseFields() 提取字段元数据
    List<BpmnConfLfFormdataField> fields = LfFormWidgetParser.parseFields(
        confVo.getLfForm(), confVo.getId(), formdata.getId());
    lfFormdataFieldService.saveBatch(fields);
}
```

### preReadProcess(读取流程配置时)

```java
public void preReadProcess(BpmnConfVo confVo) {
    if (USE_EXTERNAL_FORM.flagsContainsCurrent(confVo.getExtraFlags())) {
        // 外部模式:解析 lf_formdata_ids(CSV)为 ID 列表
        List<Long> ids = Arrays.stream(confVo.getLfFormdataIds().split(","))
            .map(Long::parseLong)
            .collect(Collectors.toList());
        // 调用 listByIdsIgnoreDeleted(含已软删表单,保证运行中流程可读)
        List<BpmnConfLfFormdata> forms = lfFormdataService.listByIdsIgnoreDeleted(ids);
        confVo.setLfFormdataList(forms);
    } else {
        // 内联模式:按 bpmn_conf_id 查询
        BpmnConfLfFormdata formdata = lfFormdataService.getOne(
            Wrappers.<BpmnConfLfFormdata>lambdaQuery()
                .eq(BpmnConfLfFormdata::getBpmnConfId, confVo.getId())
                .last("LIMIT 1"));
        confVo.setLfFormData(formdata);
    }
}
```

**软删兼容**:`listByIdsIgnoreDeleted` 保证运行中流程即使引用的表单被软删,仍可正常读取表单定义。

## 运行期助手:LFFormDataRuntimeHelper

[LFFormDataRuntimeHelper.java](file:///d:/projects/jimuoffice/antflow-engine/src/main/java/org/openoa/engine/lowflow/service/LFFormDataRuntimeHelper.java) 提供 LF 与 DIY 辅助表单流程共用的运行期方法:

| 方法 | 用途 |
|---|---|
| `populateLfConditions()` | 将 `vo.lfFields` 拷贝到 `startConditionsVo.lfConditions`(条件评估用) |
| `processFormRelatedUserConf()` | 解析"表单上下文人员"节点,按 `HAS_FORM_RELATED_ASSIGNEES` 标志查找 `NODE_PROPERTY_FORM_RELATED` 节点 |
| `collectReferencedFieldNames()` | 解析已引用的辅助表单字段名集合 = 条件字段名 ∪ 表单取人字段名 |
| `resolveBpmnConf()` | 按 processNumber/formCode 解析当前生效的 BpmnConf |
| `extractFieldsByLookup()` | 用 `MethodHandles.lookup` 按字段名从 businessDataVo 反射取值 |

## 章节导航

- [低代码流程总览](/lowcode/lowcode-overview) — 低代码在 AntFlow 中的定位与能力全景
- [低代码 vs 自定义表单](/lowcode/lowcode-vs-diy) — 两种模式横向对比与选型建议
- [低代码表单设计](/workflow-design/form-design) — 表单设计器操作手册
- [数据库设计](/dev-guide/db-design) — 完整表结构详解
- [扩展审批人来源](/dev-guide/extend-approver) — 通过 SPI 扩展审批人来源
- [集成现有系统](/dev-guide/integrate-existing) — 接入企业用户/角色系统
