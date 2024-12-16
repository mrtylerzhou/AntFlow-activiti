<template>
  <div class="my-nav-content">
    <div id="designer-id" class="form-container">
      <v-form-designer ref="formDesign"></v-form-designer> 
    </div>
    <!-- <button @click="submitForm">ok</button> -->
  </div>
</template>

<script setup>
import { ref,onUnmounted, onMounted,computed, watch } from 'vue'
import { ObjectUtils } from '@/utils/ObjectUtils'
import { useStore } from '@/store/modules/workflow'
let store = useStore()
let props = defineProps({
  lfFormData: {
        type: String,
        default: null,
    }
}); 
let lfFormDataConf = computed(()=> props.lfFormData) 
const formDesign = ref(null)
let formField = {};
//let formImportObj = "{\"widgetList\":[{\"key\":95565,\"type\":\"input\",\"icon\":\"text-field\",\"formItemFlag\":true,\"options\":{\"name\":\"input57337\",\"label\":\"部门\",\"labelAlign\":\"\",\"type\":\"text\",\"defaultValue\":\"\",\"placeholder\":\"\",\"columnWidth\":\"200px\",\"size\":\"\",\"labelWidth\":null,\"labelHidden\":false,\"readonly\":false,\"disabled\":false,\"hidden\":false,\"clearable\":true,\"showPassword\":false,\"required\":false,\"requiredHint\":\"\",\"validation\":\"\",\"validationHint\":\"\",\"customClass\":[],\"labelIconClass\":null,\"labelIconPosition\":\"rear\",\"labelTooltip\":null,\"minLength\":null,\"maxLength\":null,\"showWordLimit\":false,\"prefixIcon\":\"\",\"suffixIcon\":\"\",\"appendButton\":false,\"appendButtonDisabled\":false,\"buttonIcon\":\"custom-search\",\"onCreated\":\"\",\"onMounted\":\"\",\"onInput\":\"\",\"onChange\":\"\",\"onFocus\":\"\",\"onBlur\":\"\",\"onValidate\":\"\",\"onAppendButtonClick\":\"\",\"fieldTypeName\":\"input\",\"fieldType\":\"1\"},\"id\":\"input57337\"}],\"formConfig\":{\"modelName\":\"formData\",\"refName\":\"vForm\",\"rulesName\":\"rules\",\"labelWidth\":80,\"labelPosition\":\"left\",\"size\":\"\",\"labelAlign\":\"label-left-align\",\"cssCode\":\"\",\"customClass\":[],\"functions\":\"\",\"layoutType\":\"PC\",\"jsonVersion\":3,\"onFormCreated\":\"\",\"onFormMounted\":\"\",\"onFormDataChange\":\"\"}}";
const observer = new MutationObserver(() => {
    const returnFiled = formDesign.value.getFormFieldJson(); 
    if (ObjectUtils.isObjectChanged(formField, returnFiled)) {
      formField = returnFiled; 
      store.setLowCodeFormField(formField);
    }
}); 

onMounted(() => { 
  const targetNode = document.querySelector('#designer-id');
  const config = { childList: true, subtree: true };
  observer.observe(targetNode, config); 
}); 

watch(lfFormDataConf, (val) => {  
  if(val){ 
      formDesign.value.clearDesigner();
      formDesign.value.designer.loadFormJson(JSON.parse(val));
   } 
},{deep:true,immediate:true})
 
onUnmounted(() => {
  observer.disconnect();
});
const getData = () => {
  let exportData = formDesign.value.getFormJson();
  //console.log('exportData=========',JSON.stringify(exportData)) 
  return new Promise((resolve, reject) => {
    resolve({ formData: exportData })
  })
}

/*
获取字段
*/
const getFieldList = () => {
  let exportField = formDesign.value.getFormFieldJson();
  return new Promise((resolve, reject) => {
    resolve({ formData: exportField.formFields })
  })
}

// const fieldListApi = {
//   URL: `${baseUrl}/vform/listField?bpmnCode=aa`,
//   labelKey: 'fieldLabel',
//   nameKey: 'fieldName',
//   headers: {
//     "Userid": 1,
//     "Username": encodeURIComponent('张三'),
//   }
// }

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
.el-dialog {
  width: 700px !important;
  border: 1px solid #DDE1E5 !important;
  border-radius: 3px !important;
} 
.form-container {
  background: white !important;
  padding: 0px;
  width: 95%;
  left: 0;
  bottom: 0;
  right: 0;
  margin: auto;
} 
.main-container {
  margin-left: 0px !important;
}
</style>