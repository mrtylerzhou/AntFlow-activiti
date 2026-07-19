<template>
  <div class="card-box" style="padding: 10px;">
    <div class="task-title">
      <span class="task-title-text">流程调试 - {{ title }}</span>
    </div>
    <div style="background-color: #f5f5f7;padding: 15px;">
      <el-row :gutter="16">
        <el-col :span="12">
          <div class="debug-form">
            <div class="initiator-row">
              <span class="label"><i style="color:red">*</i> 发起人</span>
              <el-button type="primary" plain icon="Plus" @click="openUserDialog">选择发起人</el-button>
              <el-tag v-if="startUserName" closable @close="clearInitiator" style="margin-left:8px">
                {{ startUserName }}
              </el-tag>
            </div>
            <div class="component">
              <!-- 低代码(LF)流程: 渲染必填字段 + 条件字段(真实表单控件) -->
              <FormRender v-if="formReady && !diyMode" ref="formRef" :lfFormData="filteredFormJson" :lfFieldsData="'{}'"
                :lfFieldPerm="'[]'" :isPreview="false" :showSubmit="false" />
              <!-- DIY流程: 无表单schema,仅渲染条件字段(含下拉/多选)的通用输入 -->
              <div v-else-if="formReady && diyMode" class="diy-cond-form">
                <el-form label-width="120px">
                  <el-form-item v-for="cf in conditionFields" :key="cf.name" :label="cf.label">
                    <!-- 有选项的字段: 下拉(单选/多选) -->
                    <el-select v-if="cf.options" v-model="diyFormData[cf.name]" :multiple="cf.multiple"
                      :placeholder="'请选择' + cf.label" clearable style="width:100%">
                      <el-option v-for="opt in cf.options" :key="opt.key" :label="opt.value" :value="opt.key" />
                    </el-select>
                    <el-date-picker v-else-if="cf.type === 'date'" v-model="diyFormData[cf.name]" type="date"
                      value-format="yyyy-MM-dd" :placeholder="'请选择' + cf.label" style="width:100%" />
                    <el-input v-else-if="cf.type === 'number'" v-model="diyFormData[cf.name]" type="number"
                      :placeholder="'请输入' + cf.label" clearable />
                    <el-select v-else-if="cf.type === 'switch'" v-model="diyFormData[cf.name]"
                      :placeholder="'请选择' + cf.label" clearable style="width:100%">
                      <el-option label="是" value="true" />
                      <el-option label="否" value="false" />
                    </el-select>
                    <el-input v-else v-model="diyFormData[cf.name]" :placeholder="'请输入' + cf.label" clearable />
                  </el-form-item>
                </el-form>
              </div>
              <p v-if="noFields" style="color:#999;text-align:center;margin-top:12px;">
                该流程没有必填字段或条件字段，选择发起人后可直接预览。
              </p>
            </div>
            <div style="text-align:center;margin-top:12px;">
              <el-button type="primary" @click="doPreview">预览流程</el-button>
            </div>
          </div>
        </el-col>
        <el-col :span="12">
          <div v-if="previewConf" class="component">
            <ReviewWarp :previewConf="previewConf" :key="reviewKey" />
          </div>
          <div v-else class="empty-preview">
            <p style="color:#999;">填写表单并选择发起人后，点击"预览流程"查看执行路径</p>
          </div>
        </el-col>
      </el-row>
    </div>
    <label class="page-close-box" @click="close()"><img src="@/assets/images/antflow/back-close.png"></label>
    <select-user-dialog v-model:visible="userDialogVisible" :data="initiatorData" @change="sureInitiator" />
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted, getCurrentInstance } from 'vue';
import { getApiWorkFlowData } from "@/api/workflow/index";
import { FormatDisplayUtils } from '@/utils/antflow/formatdisplay_data';
import FormRender from "@/components/Workflow/DynamicForm/formRender.vue";
import ReviewWarp from "@/components/Workflow/Preview/reviewWarp.vue";
import selectUserDialog from "@/components/Workflow/dialog/selectUserDialog.vue";

const { proxy } = getCurrentInstance();
const route = useRoute();
const id = route.query?.id;

let title = ref('');
let bpmnCode = ref('');
let formCode = ref('');
let isLowCodeFlow = ref(0);
let filteredFormJson = ref('{}');
let formReady = ref(false);
let noFields = ref(false);
let diyMode = ref(false);
// DIY流程: 条件字段描述 {name,label,type,options,multiple}
let conditionFields = ref([]);
const diyFormData = reactive({});
const formRef = ref(null);

let startUserId = ref('');
let startUserName = ref('');
let userDialogVisible = ref(false);

let previewConf = ref(null);
let reviewKey = ref(0);

const initiatorData = computed(() => {
  return startUserId.value ? [{ targetId: startUserId.value, name: startUserName.value }] : [];
});

const openUserDialog = () => { userDialogVisible.value = true; };
const sureInitiator = (data) => {
  if (data && data.length) {
    startUserId.value = data[0].targetId;
    startUserName.value = data[0].name;
  }
  userDialogVisible.value = false;
};
const clearInitiator = () => { startUserId.value = ''; startUserName.value = ''; };

/**
 * 遍历表单 widgetList 收集所有字段控件(含容器内嵌套)
 */
const collectWidgets = (widgetList, acc) => {
  if (!widgetList) return;
  for (const w of widgetList) {
    if (w.formItemFlag && w.options && w.options.name) {
      acc.push(w);
    } else if (w.type === 'grid') {
      w.cols?.forEach(col => collectWidgets(col.widgetList, acc));
    } else if (w.type === 'table') {
      w.rows?.forEach(row => row.cols?.forEach(cell => collectWidgets(cell.widgetList, acc)));
    } else if (w.type === 'tab') {
      w.tabs?.forEach(tab => collectWidgets(tab.widgetList, acc));
    } else if (w.type === 'sub-form' || w.category === 'container') {
      collectWidgets(w.widgetList, acc);
    }
  }
};

/**
 * 遍历 nodeConfig 树收集条件字段描述(nodeType==3 条件分支节点)
 * type==2 为表单字段条件, columnDbname 即表单字段名(兼容 columnName), showName 为展示名
 * 选项来自 item.fixedDownBoxValue(JSON字符串数组,元素为 {key:值,value:显示文本})
 * fieldTypeName 决定控件: input/number/date/switch 为文本类; select/radio 单选; checkbox/select+multiple 多选
 */
const collectConditionFields = (node, acc) => {
  if (!node) return;
  if (node.nodeType == 3 && Array.isArray(node.conditionList)) {
    for (const group of node.conditionList) {
      if (Array.isArray(group)) {
        for (const item of group) {
          const name = item && (item.columnDbname || item.columnName);
          if (item && item.type == 2 && name) {
            if (!acc.find(c => c.name === name)) {
              // 解析下拉选项
              let options = null;
              try {
                const raw = item.fixedDownBoxValue;
                const arr = typeof raw === 'string' ? (raw ? JSON.parse(raw) : []) : (raw || []);
                if (Array.isArray(arr) && arr.length) options = arr;
              } catch (e) { options = null; }
              const isMulti = item.fieldTypeName === 'checkbox'
                || (item.fieldTypeName === 'select' && item.multiple === true);
              acc.push({
                name,
                label: item.showName || name,
                type: item.fieldTypeName || 'input',
                options,
                multiple: isMulti,
              });
            }
          }
        }
      }
    }
  }
  if (node.conditionNodes) node.conditionNodes.forEach(cn => collectConditionFields(cn, acc));
  if (node.childNode) collectConditionFields(node.childNode, acc);
  if (node.parallelNodes) node.parallelNodes.forEach(pn => collectConditionFields(pn, acc));
};

const init = async () => {
  proxy.$modal.loading();
  try {
    let res = await getApiWorkFlowData({ id });
    if (res.code != 200) { proxy.$modal.msgError(res.errMsg || '加载流程配置失败'); return; }
    let data = FormatDisplayUtils.getToTree(res.data);
    title.value = data?.bpmnName || '';
    bpmnCode.value = data?.bpmnCode;
    formCode.value = data?.formCode;
    isLowCodeFlow.value = data?.isLowCodeFlow;
    // 条件字段(低代码与DIY通用)
    const condList = [];
    collectConditionFields(data?.nodeConfig, condList);
    conditionFields.value = condList;
    // DIY流程: 无低代码表单schema,仅渲染条件字段通用输入
    const isDiy = data?.isLowCodeFlow != 1;
    let formDef = {};
    try { formDef = JSON.parse(data?.lfFormData || '{}'); } catch (e) { }
    const hasWidget = formDef.widgetList && formDef.widgetList.length > 0;
    diyMode.value = isDiy || !hasWidget;
    if (diyMode.value) {
      // 初始化条件字段输入值: 多选为数组, 单选取空串
      condList.forEach(c => { if (!(c.name in diyFormData)) diyFormData[c.name] = c.multiple ? [] : ''; });
      noFields.value = condList.length === 0;
      filteredFormJson.value = '{}';
    } else {
      // 低代码流程: 渲染 必填字段 或 条件字段
      const allWidgets = [];
      collectWidgets(formDef.widgetList, allWidgets);
      const condSet = new Set(condList.map(c => c.name));
      const filtered = allWidgets.filter(w => (w.options.required) || condSet.has(w.options.name));
      noFields.value = filtered.length === 0;
      const newFormDef = { widgetList: filtered, formConfig: formDef.formConfig || {} };
      filteredFormJson.value = JSON.stringify(newFormDef);
    }
    formReady.value = true;
  } finally {
    proxy.$modal.closeLoading();
  }
};

const doPreview = async () => {
  if (!startUserId.value) { proxy.$modal.msgError('请选择发起人'); return; }
  let lfFields = {};
  let lfConditions = {};
  if (diyMode.value) {
    // DIY流程: 条件值来自通用输入,按 columnDbname 组装 lfConditions
    // 多选(checkbox/select+multiple)传数组,单选/文本传字符串(与 LFStringConditionJudge / LFCollectionConditionJudge 匹配)
    conditionFields.value.forEach(c => {
      const v = diyFormData[c.name];
      if (c.multiple) {
        if (Array.isArray(v) && v.length) lfConditions[c.name] = v;
      } else if (v !== '' && v !== null && v !== undefined) {
        lfConditions[c.name] = v;
      }
    });
  } else {
    try {
      const fd = formRef.value?.getFromData ? await formRef.value.getFromData() : '{}';
      lfFields = fd ? JSON.parse(fd) : {};
    } catch (e) { /* 表单数据获取失败时用空数据预览 */ }
  }
  previewConf.value = {
    isStartPreview: false,
    formCode: formCode.value,
    isLowCodeFlow: isLowCodeFlow.value || false,
    isOutSideAccessProc: false,
    bpmnCode: bpmnCode.value,
    startUserId: startUserId.value,
    lfFields,
    lfConditions,
  };
  reviewKey.value++;
};

function close() { proxy.$tab.closePage(); }

onMounted(() => { init(); });
</script>

<style scoped lang="scss">
.task-title {
  display: flex;
  justify-content: space-between;
  padding-bottom: 6px;
  background-color: #f5f5f7;
}

.task-title-text {
  line-height: 28px;
  font-weight: 600;
  font-size: 16px;
  color: #383838;
}

.debug-form {
  background: #fff;
  padding: 16px;
  border-radius: 4px;
}

.initiator-row {
  display: flex;
  align-items: center;
  padding: 8px 0 16px;
  border-bottom: 1px dashed #eee;
  margin-bottom: 12px;

  .label {
    width: 80px;
    font-weight: 600;
  }
}

.component {
  background: white !important;
  padding: 16px !important;
  max-width: 720px !important;
  left: 0 !important;
  right: 0 !important;
  margin: auto !important;
}

.empty-preview {
  background: #fff;
  padding: 60px 0;
  text-align: center;
  border-radius: 4px;
}
</style>
