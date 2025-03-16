<template>
  <div class="form-container">
    <v-form-render ref="vFormRef" :form-json="formJson" :form-data="formData" :option-data="optionData">
    </v-form-render>
    <el-button v-if="!isPreview && !props.reSubmit" type="primary" @click="submitForm">提交</el-button>
    <div style="margin-top: 20px;">
      <TagApproveSelect v-if="hasChooseApprove == 'true'" v-model:formCode="formCode" @chooseApprove="chooseApprovers" />
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, getCurrentInstance } from 'vue'; 
import TagApproveSelect from "@/components/BizSelects/TagApproveSelect/index.vue";
const isEmpty = data => data === null || data === undefined || data == '' || data == '{}' || data == '[]' || data == 'null';
const isEmptyArray = data => Array.isArray(data) ? data.length === 0 : true;
const { proxy } = getCurrentInstance();
const route = useRoute();
const formCode = route.query?.formCode ?? '';
const hasChooseApprove = route.query?.hasChooseApprove ?? 'false';
let props = defineProps({
  lfFormData: {//业务表单字段
    type: String,
    default: "{}",
  },
  lfFieldsData: {//字段权限控制
    type: String,
    default: "{}",
  },
  lfFieldPerm: {//表单字段控制权限
    type: String,
    default: "[]",
  },
  reSubmit: {//是否重新提交
    type: Boolean,
    default: false,
  },
  isPreview: {//是否预览
    type: Boolean,
    default: true,
  }
});
/* 注意：formJson是指表单设计器导出的json，此处演示的formJson只是一个空白表单json！！ */
const formJson = reactive(JSON.parse(props.lfFormData));//表单字段渲染
const formData = reactive(JSON.parse(props.lfFieldsData));//表单字段输入值渲染 
const lfFieldPermData = reactive(JSON.parse(props.lfFieldPerm));//表单字段权限控制 
const optionData = reactive({});
const vFormRef = ref(null);
/**表单渲染预处理 */
const advanceHandleFormData = () => {
  if (!isEmpty(props.lfFieldsData)) {
    let handlerFieldType = (fieldOpt) => {
      if (fieldOpt.fieldTypeName == 'input-number') {
        formData[fieldOpt.name] = Number(formData[fieldOpt.name]);
      }
    }
    traverseFieldWidgetsList(formJson.widgetList, handlerFieldType);
  }
  if (!isEmpty(props.lfFieldPerm)) {
    handleFormPerm();
  } else {
    if (props.isPreview && !props.reSubmit) {
      let handlerFn = (fieldOpt) => { //控制元素是否可编辑
        fieldOpt.readonly = true;
        fieldOpt.hidden = false;
      }
      traverseFieldWidgetsList(formJson.widgetList, handlerFn);
    }
  }
}
/**表单字段权限控制 */
const handleFormPerm = () => {
  if (isEmpty(props.lfFieldPerm)) return;
  if (isEmptyArray(lfFieldPermData)) return;
  let handlerFn = (fieldOpt) => {
    let info = lfFieldPermData.find(function (ele) { return ele.fieldId == fieldOpt.name; });
    if (info) {
      if (info.perm == 'R') {
        fieldOpt.readonly = true;
        fieldOpt.hidden = false;
      } else if (info.perm == 'E') {
        fieldOpt.readonly = false;
        fieldOpt.hidden = false;
      } else if (info.perm == 'H') {
        if (fieldOpt.fieldTypeName == 'input' || fieldOpt.fieldTypeName == 'textarea') {
          formData[fieldOpt.name] = '******';
        }
        fieldOpt.readonly = true;
        fieldOpt.hidden = false;
      } else {
        fieldOpt.readonly = true;
        fieldOpt.hidden = false;
      }
    }
  };
  traverseFieldWidgetsList(formJson.widgetList, handlerFn);
}
/**递归处理表单中所有字段 */
const traverseFieldWidgetsList = function (widgetList, handler) {
  if (!widgetList) {
    return
  }
  widgetList.map(w => {
    if (w.formItemFlag) {
      handler(w.options)
    } else if (w.type === 'grid') {
      w.cols.map(col => {
        traverseFieldWidgetsList(col.widgetList, handler, w)
      })
    } else if (w.type === 'table') {
      w.rows.map(row => {
        row.cols.map(cell => {
          traverseFieldWidgetsList(cell.widgetList, handler, w)
        })
      })
    } else if (w.type === 'tab') {
      w.tabs.map(tab => {
        traverseFieldWidgetsList(tab.widgetList, handler, w)
      })
    } else if (w.type === 'sub-form') {
      traverseFieldWidgetsList(w.widgetList, handler, w)
    } else if (w.category === 'container') {  //自定义容器
      traverseFieldWidgetsList(w.widgetList, handler, w)
    }
  })
}
advanceHandleFormData();
const submitForm = () => {
  vFormRef.value.getFormData().then(res => { 
    //console.log("Form Validation===", JSON.stringify(res))
    proxy.$emit("handleBizBtn", JSON.stringify(res))
  }).catch(error => {
    proxy.$modal.msgError(error);
  })
}
const handleValidate = () => {
  return new Promise((resolve, reject) => {
    try {
      vFormRef.value.validateForm((isValid) => {
        if (!isValid) {
          reject(false);
        }
        else {
          if (hasChooseApprove == 'true' && (!formData.approversValid || formData.approversValid == false)) {
            proxy.$modal.msgError('请选择自选审批人');
            reject(false);
          }
          else {
            resolve(isValid);
          }
        }
      });
    } catch (error) {
      reject(false);
    }
  });
}

const getFromData = () => {
  return new Promise((resolve, reject) => {
    try {
      vFormRef.value.getFormData().then(res => {
        if(hasChooseApprove == 'true'){
          Object.assign(res, {
            approversList: formData.approversList,
            approversValid: formData.approversValid
          });
        }
        resolve(JSON.stringify(res));
      }).catch(err => {
        reject("");
      })
    } catch (error) {
      reject("");
    }
  });
}
/**自选审批人 */
const chooseApprovers = (data) => {
  formData.approversList = data.approvers;
  formData.approversValid = data.nodeVaild;
}

defineExpose({
  handleValidate,
  getFromData
})
</script>
<style scoped lang="scss">
.form-container {
  background: white !important;
  padding: 10px;
  max-width: 750px;
  min-height: 95%;
  left: 0;
  bottom: 0;
  right: 0;
  margin: auto;
}
</style>