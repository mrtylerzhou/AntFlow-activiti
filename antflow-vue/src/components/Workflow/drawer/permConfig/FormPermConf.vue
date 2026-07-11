<script setup>
import { onBeforeMount, ref, watch, computed } from "vue";
import { getFormPermFields } from "./FormInterface.js";
import { useStore } from "@/store/modules/workflow";

const props = defineProps({
  formItems: {
    type: Array,
    default: () => []
  },
  showE: {
    default: true
  },
  defaultPerm: { //默认加载的字段权限
    default: 'R'
  },
  //外部表单模式: 节点级整表隐藏标记 { formdataId: true/false }
  formHidden: {
    type: Object,
    default: () => ({})
  }
})
const store = useStore();
const useExternalForm = computed(() => store.useExternalForm);
const lowCodeFormFieldsMulti = computed(() => store.lowCodeFormFieldsMulti);

const permSelect = ref('R')
const updateKey = ref(1)
const formFields = ref([])
//多表单模式: [{ formdataId, formCode, formName, formHidden, fields: [{fieldId, fieldName, perm}] }]
const multiFormFields = ref([])
//多表单模式: 整表隐藏标记 { formdataId: bool }
const formHiddenMap = ref({})
let emits = defineEmits(['changePermVal', 'changeFormHidden'])

//加载的时候判断，赋默认值
onBeforeMount(() => {
  permSelect.value = props.defaultPerm;
  if (useExternalForm.value) {
    initMultiForm();
  } else {
    initInlineForm();
  }
})

/**初始化内联表单模式(原逻辑) */
function initInlineForm() {
  formFields.value = getFormPermFields(props.defaultPerm);
  (props.formItems || []).forEach(v => {
    const i = formFields.value.findIndex(fv => fv.fieldId === v.fieldId)
    if (i > -1 && v.perm) {
      formFields.value[i].perm = v.perm
    }
  })
}

/**初始化多表单模式: 按 lowCodeFormFieldsMulti 构建每张卡片的字段列表,并合并已有权限 */
function initMultiForm() {
  //复制 formHidden prop 到本地可编辑 ref
  formHiddenMap.value = { ...(props.formHidden || {}) };
  const itemsByForm = {};
  (props.formItems || []).forEach(v => {
    const key = v.formdataId;
    if (!itemsByForm[key]) itemsByForm[key] = [];
    itemsByForm[key].push(v);
  });
  multiFormFields.value = (lowCodeFormFieldsMulti.value || []).map(formInfo => {
    const existingItems = itemsByForm[formInfo.formdataId] || [];
    const fieldMap = {};
    existingItems.forEach(v => {
      if (v.fieldId && v.perm) fieldMap[v.fieldId] = v.perm;
    });
    const fields = (formInfo.formFields || []).map(widget => {
      const fieldId = widget.options?.name;
      return {
        fieldId,
        fieldName: widget.options?.label || fieldId,
        perm: fieldMap[fieldId] || props.defaultPerm
      };
    }).filter(f => f.fieldId);
    return {
      formdataId: formInfo.formdataId,
      formCode: formInfo.formCode,
      formName: formInfo.formName || formInfo.formCode,
      fields
    };
  });
}

function allSelect(perm) {
  formFields.value.forEach(v => v.perm = perm)
  updateKey.value++
}

/**多表单模式: 某张表单的全选 */
function allSelectMulti(formIdx, perm) {
  const form = multiFormFields.value[formIdx];
  if (form && form.fields) {
    form.fields.forEach(v => v.perm = perm)
  }
  updateKey.value++
}

/**多表单模式: 整表隐藏切换 */
function onFormHiddenChange(formdataId, val) {
  formHiddenMap.value[formdataId] = val;
  emits('changeFormHidden', { ...formHiddenMap.value });
}

//权限变化后，反馈给绑定的值
watch(formFields, (val) => {
  if (!useExternalForm.value) {
    emits('changePermVal', val)
  }
}, { deep: true })

//多表单模式: 字段权限或整表隐藏变化时发射
watch(multiFormFields, (val) => {
  if (!useExternalForm.value) return;
  const flat = [];
  val.forEach(form => {
    form.fields.forEach(f => {
      flat.push({
        formdataId: form.formdataId,
        fieldId: f.fieldId,
        fieldName: f.fieldName,
        perm: f.perm
      });
    });
  });
  emits('changePermVal', flat);
}, { deep: true })
</script>

<template>
  <!-- 内联表单模式: 原单表格布局 -->
  <el-table v-if="!useExternalForm" :key="updateKey" :header-cell-style="{ background: '#f5f6f6' }"
    :data="formFields" border style="width: 100%">
    <template #empty>
      未解析到表单字段列表
    </template>
    <el-table-column prop="title" show-overflow-tooltip label="表单字段">
      <template v-slot="scope">
        <span v-if="scope.row.props?.required" style="color: var(--el-color-danger)"> * </span>
        <span>{{ scope.row.fieldName }}</span>
      </template>
    </el-table-column>
    <el-table-column align="center" prop="readOnly" label="只读" width="80">
      <template #header="scope">
        <el-radio label="R" v-model="permSelect" @change="allSelect('R')">只读</el-radio>
      </template>
      <template v-slot="scope">
        <el-radio v-model="scope.row.perm" value="R" :name="scope.row.id"></el-radio>
      </template>
    </el-table-column>
    <el-table-column align="center" prop="editable" label="可编辑" width="90" v-if="showE">
      <template #header="scope">
        <el-radio label="E" v-model="permSelect" @change="allSelect('E')">可编辑</el-radio>
      </template>
      <template v-slot="scope">
        <el-radio v-model="scope.row.perm" value="E" :name="scope.row.id"></el-radio>
      </template>
    </el-table-column>
    <el-table-column align="center" prop="hide" label="隐藏" width="80">
      <template #header="scope">
        <el-radio label="H" v-model="permSelect" @change="allSelect('H')">隐藏</el-radio>
      </template>
      <template v-slot="scope">
        <el-radio v-model="scope.row.perm" value="H" :name="scope.row.id"></el-radio>
      </template>
    </el-table-column>
  </el-table>

  <!-- 外部表单模式: 多卡片布局,每张表单一张卡片 -->
  <div v-else class="multi-form-perm">
    <template v-if="multiFormFields.length === 0">
      <el-empty description="未解析到外部表单,请在基础设置中关联表单" />
    </template>
    <el-card v-for="(form, fIdx) in multiFormFields" :key="form.formdataId" class="form-card"
      :class="{ 'form-hidden': formHiddenMap[form.formdataId] }">
      <template #header>
        <div class="form-card-header">
          <span class="form-name">【{{ form.formCode }}】{{ form.formName }}</span>
          <el-checkbox :model-value="!!formHiddenMap[form.formdataId]"
            @change="(val) => onFormHiddenChange(form.formdataId, val)">
            整表隐藏
          </el-checkbox>
        </div>
      </template>
      <el-table :key="updateKey + '-' + form.formdataId" :header-cell-style="{ background: '#f5f6f6' }"
        :data="form.fields" border style="width: 100%"
        :class="{ 'table-disabled': formHiddenMap[form.formdataId] }">
        <template #empty>
          该表单未解析到字段
        </template>
        <el-table-column prop="fieldName" show-overflow-tooltip label="表单字段" />
        <el-table-column align="center" label="只读" width="80">
          <template #header>
            <el-radio label="R" :model-value="permSelect"
              @change="allSelectMulti(fIdx, 'R')">只读</el-radio>
          </template>
          <template v-slot="scope">
            <el-radio v-model="scope.row.perm" value="R" :name="String(form.formdataId) + '-' + scope.row.fieldId"
              :disabled="formHiddenMap[form.formdataId]"></el-radio>
          </template>
        </el-table-column>
        <el-table-column align="center" label="可编辑" width="90" v-if="showE">
          <template #header>
            <el-radio label="E" :model-value="permSelect"
              @change="allSelectMulti(fIdx, 'E')">可编辑</el-radio>
          </template>
          <template v-slot="scope">
            <el-radio v-model="scope.row.perm" value="E" :name="String(form.formdataId) + '-' + scope.row.fieldId"
              :disabled="formHiddenMap[form.formdataId]"></el-radio>
          </template>
        </el-table-column>
        <el-table-column align="center" label="隐藏" width="80">
          <template #header>
            <el-radio label="H" :model-value="permSelect"
              @change="allSelectMulti(fIdx, 'H')">隐藏</el-radio>
          </template>
          <template v-slot="scope">
            <el-radio v-model="scope.row.perm" value="H" :name="String(form.formdataId) + '-' + scope.row.fieldId"
              :disabled="formHiddenMap[form.formdataId]"></el-radio>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<style scoped lang="scss">
.multi-form-perm {
  .form-card {
    margin-bottom: 16px;
  }
  .form-card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    .form-name {
      font-weight: 600;
      color: #303133;
    }
  }
  .form-hidden {
    opacity: 0.6;
  }
  .table-disabled {
    pointer-events: none;
  }
}
</style>
