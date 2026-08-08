<template>
  <div class="multi-form-render">
    <el-tabs v-if="visibleForms.length > 1" v-model="activeTab" type="card" class="multi-form-tabs">
      <el-tab-pane v-for="form in visibleForms" :key="form.id" :name="String(form.id)">
        <template #label>
          <span>【{{ form.formCode || '' }}】{{ form.formName || '' }}</span>
        </template>
        <formRender
          :ref="(el) => setFormRef(form.id, el)"
          :lfFormData="form.formdata"
          :lfFieldsData="getFieldsData(form.id)"
          :lfFieldPerm="getFieldPerm(form.id)"
          :isPreview="isPreview"
          :showSubmit="false"
          :ignoreReadonly="ignoreReadonly"
          :showFieldPermLabel="showFieldPermLabel"
          :fieldPermEditable="fieldPermEditable"
          @updateFieldPerm="handleFieldPermChange"
        />
      </el-tab-pane>
    </el-tabs>
    <template v-else>
      <formRender
        v-for="form in visibleForms" :key="form.id"
        :ref="(el) => setFormRef(form.id, el)"
        :lfFormData="form.formdata"
        :lfFieldsData="getFieldsData(form.id)"
        :lfFieldPerm="getFieldPerm(form.id)"
        :isPreview="isPreview"
        :showSubmit="false"
        :ignoreReadonly="ignoreReadonly"
        :showFieldPermLabel="showFieldPermLabel"
        :fieldPermEditable="fieldPermEditable"
        @updateFieldPerm="handleFieldPermChange"
      />
    </template>
    <div class="multi-form-footer" v-if="!isPreview && showSubmit">
      <el-button type="primary" @click="submitForm">提交</el-button>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, nextTick, onBeforeUnmount, getCurrentInstance } from 'vue';
import formRender from './formRender.vue';

const { proxy } = getCurrentInstance();

const props = defineProps({
  /** 表单版本列表(含 formdata JSON 定义) */
  lfFormdataList: {
    type: Array,
    default: () => []
  },
  /** 按表单版本id分组的字段值 Map<formdataId, Map<fieldId, value>> */
  lfFieldsMulti: {
    type: Object,
    default: () => ({})
  },
  /** 字段权限控制列表(含 formdataId 维度) */
  lfFieldControlVOs: {
    type: Array,
    default: () => []
  },
  /** 节点级整表隐藏标记 Map<formdataId, boolean> */
  formHidden: {
    type: Object,
    default: () => ({})
  },
  isPreview: {
    type: Boolean,
    default: true
  },
  showSubmit: {
    type: Boolean,
    default: false
  },
  ignoreReadonly: {//管理员预览：忽略只读权限控制（隐藏仍生效），让只读字段可编辑
    type: Boolean,
    default: false
  },
  showFieldPermLabel: {//Zen预览：在字段label后追加三态标识
    type: Boolean,
    default: false
  },
  fieldPermEditable: {//Zen预览：字段label后追加可点击R/W/H徽标
    type: Boolean,
    default: false
  }
});

const emit = defineEmits(['handleBizBtn', 'updateFieldPerm']);

const activeTab = ref('');
const formRefs = {};

const visibleForms = computed(() => {
  const list = props.lfFormdataList || [];
  const hiddenMap = props.formHidden || {};
  return list.filter(f => !hiddenMap[f.id]);
});

const setFormRef = (formdataId, el) => {
  if (el) {
    formRefs[formdataId] = el;
  } else {
    delete formRefs[formdataId];
  }
};

const getFieldsData = (formdataId) => {
  const fields = props.lfFieldsMulti?.[formdataId];
  return fields ? JSON.stringify(fields) : '{}';
};

const getFieldPerm = (formdataId) => {
  const all = props.lfFieldControlVOs || [];
  const filtered = all.filter(fc => fc.formdataId === formdataId);
  return JSON.stringify(filtered);
};

const ensureActiveTab = () => {
  if (visibleForms.value.length > 0 && !activeTab.value) {
    activeTab.value = String(visibleForms.value[0].id);
  }
};

/** 字段权限徽标点击：转发给父级（携带 formdataId 便于写回对应表单版本） */
const handleFieldPermChange = (payload) => {
  emit('updateFieldPerm', payload);
};

const handleValidate = () => {
  return new Promise(async (resolve, reject) => {
    try {
      ensureActiveTab();
      for (const form of visibleForms.value) {
        const ref = formRefs[form.id];
        if (ref && typeof ref.handleValidate === 'function') {
          const valid = await ref.handleValidate();
          if (!valid) {
            activeTab.value = String(form.id);
            resolve(false);
            return;
          }
        }
      }
      resolve(true);
    } catch (e) {
      reject(false);
    }
  });
};

const getFromData = () => {
  return new Promise(async (resolve, reject) => {
    try {
      const result = {};
      for (const form of visibleForms.value) {
        const ref = formRefs[form.id];
        if (ref && typeof ref.getFromData === 'function') {
          const data = await ref.getFromData();
          result[form.id] = JSON.parse(data);
        } else {
          result[form.id] = {};
        }
      }
      resolve(JSON.stringify(result));
    } catch (e) {
      reject('');
    }
  });
};

/** 提交按钮: 触发 handleBizBtn 事件, 由父组件处理提交逻辑 */
const submitForm = () => {
  handleValidate().then(valid => {
    if (valid) {
      getFromData().then(data => {
        emit('handleBizBtn', data);
      }).catch(() => {
        proxy.$modal.msgError('表单数据获取失败');
      });
    }
  }).catch(() => {
    proxy.$modal.msgError('表单校验未通过');
  });
};

onBeforeUnmount(() => {
  Object.keys(formRefs).forEach(k => delete formRefs[k]);
});

defineExpose({
  handleValidate,
  getFromData
});
</script>

<style scoped lang="scss">
.multi-form-render {
  .multi-form-tabs {
    :deep(.el-tabs__header) {
      margin-bottom: 12px;
    }
    :deep(.el-tabs__item) {
      font-weight: 500;
    }
  }
  .multi-form-footer {
    background-color: #fff;
    border-top: 2px solid #f0f0f0;
    box-shadow: 0 1px 4px rgba(0, 21, 41, 0.08);
    display: flex;
    justify-content: flex-end;
    align-items: center;
    padding: 12px 24px;
    margin-top: 12px;
  }
}
</style>
