# 低代码表单设计

> AntFlow 集成 VForm3 低代码表单设计器,通过拖拽控件即可完成表单设计,无需编写前端代码。支持内联表单和外部表单两种模式,字段级权限控制(R/E/H)。

## VForm3 集成

### 库位置与注册

vform 库文件:
- `antflow-vue/src/lib/vform/designer.umd.js`
- `antflow-vue/src/lib/vform/designer.style.css`

在 [main.js](https://github.com/mrtylerzhou/AntFlow/blob/master/antflow-vue/src/main.js) 中全局注册:

```javascript
import VForm3 from "@/./lib/vform/designer.umd.js";
import "./lib/vform/designer.style.css";
app.use(VForm3);  // 注册 v-form-designer 和 v-form-render
```

VForm3 是第三方低代码表单库,通过 UMD 打包集成,全局注册了两个核心组件:
- **`<v-form-designer>`**:设计器,拖拽控件设计表单
- **`<v-form-render>`**:运行时渲染器,根据 JSON 渲染表单

## 两个设计入口

### 入口一:流程设计器内嵌表单

在 [流程设计器](/workflow-design/flow-designer) 的"表单设计"步骤,通过 [DynamicForm/index.vue](https://github.com/mrtylerzhou/AntFlow/blob/master/antflow-vue/src/components/Workflow/DynamicForm/index.vue) 集成 vform 设计器。

![表单设计](/images/3-3.png)

该组件使用 MutationObserver 监听 DOM 变化,自动提取字段并同步到 workflow store:

```javascript
const observer = new MutationObserver(() => {
  const returnFiled = formDesign.value.getFormFieldJson();
  const formatReturnField = autoAddFieldType(returnFiled);
  if (isObjectChanged(formField, formatReturnField)) {
    formField = formatReturnField;
    store.setLowCodeFormField(formField);  // 供条件配置和权限配置使用
  }
});
```

`autoAddFieldType()` 根据 widget.type 映射 fieldType 数字:

| vform type | fieldType |
|---|---|
| number | 2 |
| date | 4 |
| switch | 6 |
| rich-editor | 5 |
| 其他 | 1 |

### 入口二:独立表单管理

路径:**流程运维 → 表单管理** (`/flowDevops/lfForm`)

- [index.vue](https://github.com/mrtylerzhou/AntFlow/blob/master/antflow-vue/src/views/system/lfForm/index.vue):表单列表页
- [design.vue](https://github.com/mrtylerzhou/AntFlow/blob/master/antflow-vue/src/views/system/lfForm/design.vue):表单设计器

独立表单支持:
- **版本管理**:同一 formCode 下可有多个版本,同时只有一个生效
- **多表单模式**:一个流程可关联多个独立表单(外部表单模式)
- **引用查询**:查询哪些流程引用了此表单
- **被引用保护**:被流程引用的表单不可删除

## 两种表单模式

通过 `BpmnConfFlagsEnum.USE_EXTERNAL_FORM` flag 切换:

### 内联表单模式(默认)

- 表单 JSON 内嵌在流程配置中
- 存储于 `t_bpmn_conf_lf_formdata` 表,`bpmn_conf_id` 指向所属流程
- 一个流程对应一个内联表单
- 在流程设计器的"表单设计"步骤设计

### 外部表单模式

- 表单通过独立表单管理模块维护
- 流程通过 `lf_formdata_ids`(CSV)引用表单版本 ID
- 一个流程可关联**多个**表单
- 在流程设计器中禁用"表单设计"步骤,显示提示"请到表单管理维护"

<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 880 280" font-family="-apple-system, BlinkMacSystemFont, 'Segoe UI', sans-serif">
  <text x="440" y="24" text-anchor="middle" font-size="16" font-weight="700" fill="#1e293b">内联表单 vs 外部表单</text>

  <!-- 内联表单 -->
  <rect x="20" y="50" width="400" height="210" rx="10" fill="#dbeafe" stroke="#3b82f6" stroke-width="1.5"/>
  <text x="220" y="78" text-anchor="middle" font-size="14" font-weight="700" fill="#1e40af">内联表单模式(默认)</text>
  <rect x="40" y="95" width="360" height="32" rx="5" fill="#fff" stroke="#3b82f6"/>
  <text x="220" y="115" text-anchor="middle" font-size="11" fill="#1e293b">流程配置 t_bpmn_conf</text>
  <rect x="40" y="135" width="360" height="32" rx="5" fill="#fff" stroke="#3b82f6"/>
  <text x="220" y="155" text-anchor="middle" font-size="11" fill="#1e293b">↓ bpmn_conf_id 关联</text>
  <rect x="40" y="175" width="360" height="32" rx="5" fill="#fff" stroke="#3b82f6"/>
  <text x="220" y="195" text-anchor="middle" font-size="11" fill="#1e293b">t_bpmn_conf_lf_formdata (1条)</text>
  <rect x="40" y="215" width="360" height="32" rx="5" fill="#fff" stroke="#3b82f6"/>
  <text x="220" y="235" text-anchor="middle" font-size="11" fill="#1e293b">formdata 字段(JSON 字符串)</text>

  <!-- 外部表单 -->
  <rect x="460" y="50" width="400" height="210" rx="10" fill="#dcfce7" stroke="#16a34a" stroke-width="1.5"/>
  <text x="660" y="78" text-anchor="middle" font-size="14" font-weight="700" fill="#166534">外部表单模式(多表单)</text>
  <rect x="480" y="95" width="360" height="32" rx="5" fill="#fff" stroke="#16a34a"/>
  <text x="660" y="115" text-anchor="middle" font-size="11" fill="#1e293b">流程配置 t_bpmn_conf</text>
  <rect x="480" y="135" width="360" height="32" rx="5" fill="#fff" stroke="#16a34a"/>
  <text x="660" y="155" text-anchor="middle" font-size="11" fill="#1e293b">↓ lf_formdata_ids (CSV)</text>
  <rect x="480" y="175" width="360" height="32" rx="5" fill="#fff" stroke="#16a34a"/>
  <text x="660" y="195" text-anchor="middle" font-size="11" fill="#1e293b">t_bpmn_conf_lf_formdata (多条)</text>
  <rect x="480" y="215" width="360" height="32" rx="5" fill="#fff" stroke="#16a34a"/>
  <text x="660" y="235" text-anchor="middle" font-size="11" fill="#1e293b">独立表单管理模块维护</text>
</svg>

## 表单字段提取

### 后端 LfFormWidgetParser

[LfFormWidgetParser.java](https://github.com/mrtylerzhou/AntFlow/blob/master/antflow-engine/src/main/java/org/openoa/engine/lowflow/service/LfFormWidgetParser.java) 递归解析 vform JSON,提取字段元数据:

```java
public List<BpmnConfLfFormdataField> parseFields(String formdataJson, Long confId, Long formDataId) {
    FormConfigWrapper wrapper = JSON.parseObject(formdataJson, FormConfigWrapper.class);
    List<LfWidget> widgetList = wrapper.getWidgetList();
    return parseWidgetListRecursively(widgetList, confId, formDataId);
}
```

### 容器控件递归处理

对容器控件(category == "container")按类型递归:

| 容器类型 | 处理方式 |
|---|---|
| CARD | 递归 widgetList |
| TAB | 遍历 tabs,每个 tab 递归 widgetList |
| TABLE/grid | 遍历 rows→cols→widgetList 或直接 cols→widgetList |

### 字段类型映射

`getFieldTypeByTypeString()` 将 vform 控件类型映射为 [LFFieldTypeEnum](https://github.com/mrtylerzhou/AntFlow/blob/master/antflow-base/src/main/java/org/openoa/base/constant/enums/LFFieldTypeEnum.java):

| vform type | LFFieldTypeEnum |
|---|---|
| number, slider | NUMBER |
| date | DATE |
| date-range, time, time-range | DATE_TIME |
| switch | BOOLEAN |
| textarea, richtext-editor | TEXT |
| select/radio/checkbox/input/cascader/tree-select/color-picker/rate/input/number-range/picture-upload/file-upload/icon-picker/transfer | STRING |

## 字段权限系统(R/E/H)

### 权限值

[StringConstants.java](https://github.com/mrtylerzhou/AntFlow/blob/master/antflow-base/src/main/java/org/openoa/base/constant/StringConstants.java):

| 权限 | 值 | 说明 |
|---|---|---|
| 只读 | `R` | disabled,不可编辑 |
| 可编辑 | `E` | readonly=false,可编辑 |
| 隐藏 | `H` | 字段类型改为 input+text,值改为 `******`,disabled |

### 前端配置组件

[FormPermConf.vue](https://github.com/mrtylerzhou/AntFlow/blob/master/antflow-vue/src/components/Workflow/drawer/permConfig/FormPermConf.vue) 提供两种模式:

- **内联表单模式**:单表格布局,列:表单字段 / 只读(R) / 可编辑(E) / 隐藏(H),表头支持"全选"
- **外部表单模式**:多卡片布局,每张表单一个 `<el-card>`,卡片头有"整表隐藏"checkbox

### 后端存储

- 实体:[BpmnNodeLfFormdataFieldControl](https://github.com/mrtylerzhou/AntFlow/blob/master/antflow-base/src/main/java/org/openoa/base/entity/BpmnNodeLfFormdataFieldControl.java)
- 表:`t_bpmn_node_lf_formdata_field_control`
- 字段:`node_id`, `formdata_id`, `field_id`, `field_name`, `field_perm`
- VO:`LFFieldControlVO`(nodeId, formdataId, fieldId, fieldName, perm)

## 表单配置存储

### 设计期表

| 表名 | 实体 | 作用 |
|---|---|---|
| `t_bpmn_conf_lf_formdata` | [BpmnConfLfFormdata](https://github.com/mrtylerzhou/AntFlow/blob/master/antflow-base/src/main/java/org/openoa/base/entity/BpmnConfLfFormdata.java) | 表单定义主体,`formdata` 字段存 JSON |
| `t_bpmn_conf_lf_formdata_field` | [BpmnConfLfFormdataField](https://github.com/mrtylerzhou/AntFlow/blob/master/antflow-base/src/main/java/org/openoa/base/entity/BpmnConfLfFormdataField.java) | 字段元数据(fieldId, fieldName, fieldType, isCondition) |
| `t_bpmn_node_lf_formdata_field_control` | [BpmnNodeLfFormdataFieldControl](https://github.com/mrtylerzhou/AntFlow/blob/master/antflow-base/src/main/java/org/openoa/base/entity/BpmnNodeLfFormdataFieldControl.java) | 节点级字段权限 |

::: warning 存储字段名
表单 JSON 存储字段名是 `formdata`(单字段),不是 `form_view_json` 或 `form_design_json`。
:::

### 运行期表

| 表名 | 实体 | 作用 |
|---|---|---|
| `t_lf_main` | [LFMain](https://github.com/mrtylerzhou/AntFlow/blob/master/antflow-engine/src/main/java/org/openoa/engine/lowflow/entity/LFMain.java) | 运行时表单数据主表(conf_id, form_code) |
| `t_lf_main_field` | [LFMainField](https://github.com/mrtylerzhou/AntFlow/blob/master/antflow-engine/src/main/java/org/openoa/engine/lowflow/entity/LFMainField.java) | 运行时表单字段值 |

## 数据流:设计 → 运行

### 保存流程配置

[LFFormDataPreProcessor.preWriteProcess](https://github.com/mrtylerzhou/AntFlow/blob/master/antflow-engine/src/main/java/org/openoa/engine/lowflow/service/LFFormDataPreProcessor.java):

1. 检查 `isLowCodeFlow==1` 或 `USE_AUXILIARY_FORM` flag
2. 外部表单模式跳过(表单由独立模块维护)
3. 内联模式:构造 `BpmnConfLfFormdata`(bpmn_conf_id=confId, formdata=lfForm)保存
4. 调用 `LfFormWidgetParser.parseFields()` 提取字段元数据
5. `lfFormdataFieldService.saveBatch()` 批量保存字段到 `t_bpmn_conf_lf_formdata_field`

### 读取流程配置

`LFFormDataPreProcessor.preReadProcess()`:

- **外部表单模式**:解析 `confVo.lfFormdataIds`(CSV)为 ID 列表,调用 `listByIdsIgnoreDeleted`(含已软删表单,保证运行中流程可读)
- **内联表单模式**:按 `bpmn_conf_id` 查询 `BpmnConfLfFormdata`,取第一条

::: tip 软删兼容
`listByIdsIgnoreDeleted` 方法保证运行中流程即使引用的表单被软删,仍可正常读取表单定义。这是为了保证已发起流程的数据完整性。
:::

### 发起流程时的表单加载

**API**:`GET /lowcode/getStartFormData?formCode=xxx`

**后端**:[LowCodeFlowController.getStartFormData](https://github.com/mrtylerzhou/AntFlow/blob/master/antflow-engine/src/main/java/org/openoa/engine/bpmnconf/controller/LowCodeFlowController.java)

返回 [LfStartFormVo](https://github.com/mrtylerzhou/AntFlow/blob/master/antflow-base/src/main/java/org/openoa/base/vo/LfStartFormVo.java):

```java
public class LfStartFormVo {
    private Boolean useExternalForm;
    private String lfFormData;                       // 内联模式:表单 JSON
    private List<BpmnConfLfFormdata> lfFormdataList; // 外部模式:表单列表
    private List<LFFieldControlVO> lfFieldControlVOs; // 发起人节点字段权限
    private Map<String, Boolean> formHidden;         // 外部模式整表隐藏标记
}
```

### 运行时表单渲染

[formRender.vue](https://github.com/mrtylerzhou/AntFlow/blob/master/antflow-vue/src/components/Workflow/DynamicForm/formRender.vue) 使用 `<v-form-render>` 渲染:

```vue
<v-form-render ref="vFormRef" :form-json="formJson" :form-data="formData" :option-data="optionData">
</v-form-render>
```

**权限应用逻辑** `handlerFn(w)`:
- 递归遍历所有 formItemFlag 字段(支持 grid/table/tab/sub-form/container 容器)
- 发起模式(showSubmit=true):
  - `R` → `w.options.disabled = true`
  - `E` → `w.options.readonly = false`
  - `H` → 类型改为 input+text,值改为 `******`,disabled
  - 未配置 → 默认可编辑
- 管理员预览(ignoreReadonly=true):仅 H 生效,其他都设为可编辑
- 审批/预览模式:同上但默认 disabled/readonly

## API 端点清单

[LowCodeFlowController](https://github.com/mrtylerzhou/AntFlow/blob/master/antflow-engine/src/main/java/org/openoa/engine/bpmnconf/controller/LowCodeFlowController.java) 路由前缀 `/lowcode`:

| 端点 | 方法 | 用途 |
|---|---|---|
| `/getLFFormCodePageList` | POST | LF FormCode 分页列表 |
| `/getLFActiveFormCodePageList` | POST | 已启用的 LF FormCode(发起页) |
| `/getStartFormData` | GET | 发起页表单数据 |
| `/getformDataByFormCode` | GET | 按 formCode 获取表单 JSON |
| `/createLowCodeFormCode` | POST | 新建 LF FormCode |
| `/form/listPage` | POST | 独立表单分页查询 |
| `/form/{id}` | GET | 按 id 查询表单版本 |
| `/form/save` | POST | 保存表单(新建家族/版本) |
| `/form/{id}` | DELETE | 软删除版本(被引用时拒绝) |
| `/form/history` | GET | 查询家族所有版本 |
| `/form/effective/{id}` | PUT | 生效指定版本 |
| `/form/references/{formdataId}` | GET | 查询引用此版本的流程 |

## 下一步

- [审批人规则](/workflow-design/approver-rules) — 15 种审批人来源
- [版本管理与启动](/workflow-design/version-management) — 流程发布与激活
- [低代码表单引擎](/lowcode/lowcode-form) — 深入了解低代码实现
