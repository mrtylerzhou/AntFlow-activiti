<template>
  <div style="max-width: 720px;left: 0;right: 0;margin: auto;">
    <v-form-render ref="vFormRef" :form-json="formJson" :form-data="formData" :option-data="optionData">
    </v-form-render>
    <el-button v-if="!isPreview" type="primary" @click="submitForm">提交</el-button>
  </div>
</template>

<script setup>
import { ref, reactive, getCurrentInstance } from 'vue'
import { ElMessage } from 'element-plus'
const isEmpty = data => data === null || data === undefined || data == '' || data == '{}' || data == '[]' || data == 'null';
const isEmptyArray = data => Array.isArray(data) ? data.length === 0 : true;

const { proxy } = getCurrentInstance()
let props = defineProps({
  lfFormData: {
    type: String,
    default: "{}",
  },
  lfFieldsData: {
    type: String,
    default: "{}",
  },
  lfFieldPerm: {//表单字段控制权限
    type: String,
    default: "[]",
  },
  isPreview: {
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
    if (props.isPreview) {
      let handlerFn = (fieldOpt) => {
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
        if(fieldOpt.fieldTypeName == 'input' || fieldOpt.fieldTypeName == 'textarea'){
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
  vFormRef.value.getFormData().then(formData => {
    // Form Validation OK
    console.log("Form Validation===", JSON.stringify(formData))
    proxy.$emit("handleBizBtn", JSON.stringify(formData))
  }).catch(error => {
    ElMessage.error(error)
  })
}
const handleValidate = () => {
  return new Promise((resolve, reject) => {
    try {
      vFormRef.value.validateForm((isValid) => {
        resolve(isValid);
      });
    } catch (error) {
      reject(false);
    }
  });
}

const getFromData = () => {
  return new Promise((resolve, reject) => {
    try {
      vFormRef.value.getFormData().then(formData => {
        resolve(JSON.stringify(formData));
      }).catch(err => {
        reject("");
      })
    } catch (error) {
      reject("");
    }
  });
}
defineExpose({
  handleValidate,
  getFromData
})
</script>
