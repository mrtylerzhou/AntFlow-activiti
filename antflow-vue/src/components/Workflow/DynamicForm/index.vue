<template>
  <div class="my-nav-content">
    <div id="designer-id" class="lf-form-container">
      <v-form-designer ref="formDesign"></v-form-designer>
    </div>
    <!-- <button @click="submitForm">ok</button> -->
  </div>
</template>

<script setup>
import { ref, onUnmounted, onMounted, computed, watch } from 'vue'
import { isObjectChanged } from '@/utils/antflow/ObjectUtils'
import { useStore } from '@/store/modules/workflow'
let store = useStore()
let props = defineProps({
  lfFormData: {
    type: String,
    default: null,
  }
});
let lfFormDataConf = computed(() => props.lfFormData)
const formDesign = ref(null)
let formField = {};
//let formImportObj = "{\"widgetList\":[{\"key\":95565,\"type\":\"input\",\"icon\":\"text-field\",\"formItemFlag\":true,\"options\":{\"name\":\"input57337\",\"label\":\"部门\",\"labelAlign\":\"\",\"type\":\"text\",\"defaultValue\":\"\",\"placeholder\":\"\",\"columnWidth\":\"200px\",\"size\":\"\",\"labelWidth\":null,\"labelHidden\":false,\"readonly\":false,\"disabled\":false,\"hidden\":false,\"clearable\":true,\"showPassword\":false,\"required\":false,\"requiredHint\":\"\",\"validation\":\"\",\"validationHint\":\"\",\"customClass\":[],\"labelIconClass\":null,\"labelIconPosition\":\"rear\",\"labelTooltip\":null,\"minLength\":null,\"maxLength\":null,\"showWordLimit\":false,\"prefixIcon\":\"\",\"suffixIcon\":\"\",\"appendButton\":false,\"appendButtonDisabled\":false,\"buttonIcon\":\"custom-search\",\"onCreated\":\"\",\"onMounted\":\"\",\"onInput\":\"\",\"onChange\":\"\",\"onFocus\":\"\",\"onBlur\":\"\",\"onValidate\":\"\",\"onAppendButtonClick\":\"\",\"fieldTypeName\":\"input\",\"fieldType\":\"1\"},\"id\":\"input57337\"}],\"formConfig\":{\"modelName\":\"formData\",\"refName\":\"vForm\",\"rulesName\":\"rules\",\"labelWidth\":80,\"labelPosition\":\"left\",\"size\":\"\",\"labelAlign\":\"label-left-align\",\"cssCode\":\"\",\"customClass\":[],\"functions\":\"\",\"layoutType\":\"PC\",\"jsonVersion\":3,\"onFormCreated\":\"\",\"onFormMounted\":\"\",\"onFormDataChange\":\"\"}}";
const observer = new MutationObserver(() => {
  const returnFiled = formDesign.value.getFormFieldJson();
  const formatReturnField = autoAddFieldType(returnFiled);
  if (isObjectChanged(formField, formatReturnField)) {
    formField = formatReturnField;
    store.setLowCodeFormField(formField);
  }
});
onMounted(() => {
  const targetNode = document.querySelector('#designer-id');
  const config = { childList: true, subtree: true };
  observer.observe(targetNode, config);
});

watch(lfFormDataConf, (val) => {
  if (val) {
    formDesign.value.clearDesigner();
    formDesign.value.designer.loadFormJson(JSON.parse(val));
  }
}, { deep: true, immediate: true })

onUnmounted(() => {
  observer.disconnect();
});
/**
 * 提交表单
 */
const getData = () => {
  let exportData = formDesign.value.getFormJson();
  //console.log('exportData=========', JSON.stringify(exportData))
  return new Promise((resolve, reject) => {
    resolve({ formData: exportData })
    reject(new Error('获取表单数据失败'));
  })
}

/*
获取字段
*/
const getFieldList = () => {
  let exportField = formDesign.value.getFormFieldJson();
  return new Promise((resolve, reject) => {
    resolve({ formData: exportField.formFields })
    reject(new Error('获取表单获取字段失败'));
  })
}

/**
 * 给json字符串中指定key的widget的options添加字段
 * @param {string} jsonStr - 原始JSON字符串 
 * @returns {string} 处理后的新JSON字符串
 */
function autoAddFieldType(data) {
  try {
    if (Array.isArray(data.formFields)) {
      data.formFields.forEach(widget => {
        // 根据type获取对应fieldType数值
        const fieldType = getFieldTypeByType(widget.type);
        // 确保options对象存在
        if (!widget.options) widget.options = {};
        widget.options.fieldType = fieldType;
      });
    }

    return data;
  } catch (err) {
    return data;
  }
}

/**
 * 根据type映射fieldType数字
 * @param {string} type widget的type字段
 * @returns {number} fieldType值
 */
function getFieldTypeByType(type) {
  switch (type) {
    case "number":
      return 2;//数字
    case "date":
      return 4; //日期时间
    case "switch":
      return 6;//boolean
    case "rich-editor":
      return 5;//text
    case "select":
    case "input":
    case "checkbox":
    case "time":
      return 1;//string
    default:
      return 1;
  }
}
defineExpose({
  getData,
  getFieldList
})
</script>

<style lang="scss" scoped>
body {
  margin: 0;
  /* 如果页面出现垂直滚动条，则加入此行CSS以消除之 */
}

.main-container {
  margin-left: 0px !important;
}

.right-toolbar {
  float: right !important;
}

.el-dialog {
  width: 700px !important;
  border: 1px solid #DDE1E5 !important;
  border-radius: 3px !important;
}

.lf-form-container {
  background: white !important;
  padding: 0px;
  width: 95%;
  left: 0;
  bottom: 0;
  right: 0;
  margin: auto;
}
</style>