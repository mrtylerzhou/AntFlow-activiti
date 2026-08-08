<template>
  <div class="app-container">
    <el-row :gutter="20">
      <el-col :span="hasChooseApprove == 'true' ? 16 : 24">
        <div class="form-container" :style="hasChooseApprove == 'true' ? {} : { maxWidth: '80vw', margin: '0 auto' }">
          <div class="el-main" ref="formMainRef">
            <v-form-render ref="vFormRef" :form-json="formJson" :form-data="formData" :option-data="optionData">
            </v-form-render>
          </div>
          <div class="el-footer" v-if="!isPreview && props.showSubmit">
            <el-button type="primary" @click="submitForm">提交</el-button>
            <el-button @click="saveDraft">保存草稿</el-button>
            <el-button @click="loadDraftData">加载草稿</el-button>
          </div>
        </div>
      </el-col>
      <el-col :span="8" v-if="hasChooseApprove == 'true'">
        <TagApproveSelect v-model:formCode="formCode" @chooseApprove="chooseApprovers" />
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, reactive, getCurrentInstance, onBeforeMount, onMounted, nextTick } from 'vue';
import TagApproveSelect from "@/components/BizSelects/TagApproveSelect/index.vue";
import { processOperation, loadDraft } from '@/api/workflow/index';
const isEmpty = data => data === null || data === undefined || data == '' || data == '{}' || data == '[]' || data == 'null';
const { proxy } = getCurrentInstance();
const route = useRoute();
const formCode = route.query?.formCode ?? '';
const hasChooseApprove = route.query?.hasChooseApprove ?? 'false';
// watch(() => formRenderConfig.value, () => {
//   console.log("formCode====", JSON.stringify(formCode));
//   console.log("hasChooseApprove====", JSON.stringify(hasChooseApprove));
//   console.log("formRenderConfig.value====", JSON.stringify(formRenderConfig.value));
// });
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
  showSubmit: {//是否显示提交按钮
    type: Boolean,
    default: false,
  },
  isPreview: {//是否预览
    type: Boolean,
    default: true,
  },
  ignoreReadonly: {//管理员预览：忽略只读权限控制（隐藏仍生效），让只读字段可编辑
    type: Boolean,
    default: false,
  },
  showFieldPermLabel: {//Zen预览：在字段label后追加可编辑/只读/隐藏三态标识（不改变字段行为）
    type: Boolean,
    default: false,
  },
  fieldPermEditable: {//Zen预览：字段label后追加可点击的R/W/H三态徽标，点击循环切换并emit(updateFieldPerm)
    type: Boolean,
    default: false,
  }
});
const emit = defineEmits(['updateFieldPerm']);
/* 注意：formJson是指表单设计器导出的json，此处演示的formJson只是一个空白表单json！！ */
const formJson = reactive(JSON.parse(props.lfFormData || "{}"));//表单字段渲染
const formData = reactive(JSON.parse(props.lfFieldsData || "{}"));//表单字段输入值渲染 
const lfFieldPermData = reactive(JSON.parse(props.lfFieldPerm || "{}"));//表单字段权限控制 
const optionData = reactive({});
const vFormRef = ref(null);
const formMainRef = ref(null);
/**表单渲染预处理 */
const advanceHandleFormData = () => {
  if (!isEmpty(props.lfFieldsData) || props.showSubmit || props.showFieldPermLabel) {
    traverseFieldWidgetsList(formJson.widgetList, handlerFn);
  }
}
/**字段权限三态标识文本映射（区域有限，用缩写） */
const permLabelMap = { 'R': 'R', 'H': 'H', 'E': 'W' };
/**表单字段权限控制 */
const handlerFn = (w) => {
  w.options.hidden = false;//字段都隐藏，隐藏后表单字段不会自动补位
  const numberFields = ['number', 'select', 'radio'];
  if (numberFields.includes(w.type)) {
    if (!w.options.multiple) {
      formData[w.options.name] = Number(formData[w.options.name]);
    }
  }
  // Zen预览：表单整体只读 + 字段label后追加三态标识（R只读/W可编辑/H隐藏）
  if (props.showFieldPermLabel) {
    w.options.disabled = true;
    w.options.readonly = true;
    // fieldPermEditable 模式下由 DOM 注入可点击徽标，label 不追加文本
    if (!props.fieldPermEditable) {
      const info = lfFieldPermData.find(ele => ele.fieldId == w.options.name);
      const perm = info ? info.perm : 'E';
      const labelText = permLabelMap[perm] || 'W';
      w.options.label = `${w.options.label || w.options.name}（${labelText}）`;
    }
    return;
  }
  if (props.showSubmit) {
    // 发起模式：优先检查发起人节点的字段权限配置
    if (!isEmpty(props.lfFieldPerm)) {
      let info = lfFieldPermData.find(function (ele) { return ele.fieldId == w.options.name; });
      if (info) {
        if (info.perm == 'R') {
          w.options.disabled = true;
        } else if (info.perm == 'E') {
          w.options.readonly = false;
        } else if (info.perm == 'H') {
          if (w.type != 'textarea' && w.options.type != 'input') {
            w.type = 'input';
            w.options.type = 'text';
          }
          formData[w.options.name] = '******';
          delete w.options.format;
          delete w.options.valueFormat;
          w.options.disabled = true;
        } else {
          w.options.disabled = false;
          w.options.readonly = false;
        }
      } else {
        // 未配置权限的字段默认可编辑
        w.options.disabled = false;
        w.options.readonly = false;
      }
    } else {
      w.options.disabled = false;
      w.options.readonly = false;
    }
  }
  else if (props.ignoreReadonly) {
    // 管理员预览：忽略只读权限控制，仅隐藏字段仍保持隐藏
    let info = lfFieldPermData.find(function (ele) { return ele.fieldId == w.options.name; });
    if (info && info.perm == 'H') {
      if (w.type != 'textarea' && w.options.type != 'input') {
        w.type = 'input';
        w.options.type = 'text';
      }
      formData[w.options.name] = '******';
      delete w.options.format;
      delete w.options.valueFormat;
      w.options.disabled = true;
    } else {
      w.options.disabled = false;
      w.options.readonly = false;
    }
  }
  else if (!isEmpty(props.lfFieldPerm)) {
    let info = lfFieldPermData.find(function (ele) { return ele.fieldId == w.options.name; });
    if (info) {
      if (info.perm == 'R') {
        w.options.disabled = true;
      } else if (info.perm == 'E') {
        w.options.readonly = false;
      } else if (info.perm == 'H') {//隐藏字段处理：将所以字段类型转化为input格式，value 赋值为 ****** 
        if (w.type != 'textarea' && w.options.type != 'input') {
          w.type = 'input';
          w.options.type = 'text';
        }
        formData[w.options.name] = '******';
        delete w.options.format;
        delete w.options.valueFormat;
        w.options.disabled = true;
      } else {
        w.options.disabled = false;
        w.options.readonly = true;
      }
    }
  } else {
    if (props.isPreview == true) {
      w.options.disabled = true;
      w.options.readonly = true;
    }
  }
}
/**递归处理表单中所有字段 */
const traverseFieldWidgetsList = function (widgetList, handler) {
  if (!widgetList) {
    return
  }
  widgetList.map(w => {
    if (w.formItemFlag) {
      handler(w)
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
onBeforeMount(() => {
  // console.log("isPreview======", JSON.stringify(props.isPreview));
  // console.log("showSubmit======", JSON.stringify(props.showSubmit));
  advanceHandleFormData();
})
onMounted(() => {
  nextTick(() => {
    vFormRef.value.setFormJson(formJson)
  }).then(() => {
    vFormRef.value.setFormData(formData)
    if (props.fieldPermEditable) {
      injectPermBadges();
    }
  })
})

/** 权限徽标颜色映射 */
const permColorMap = { 'R': '#f56c6c', 'W': '#67c23a', 'H': '#909399' };
const permTextMap = { 'R': 'R', 'W': 'W', 'H': 'H' };

/**
 * 在字段 label 后追加可点击的 R/W/H 三态徽标（Zen 右侧编辑字段权限）
 * vform 渲染的 el-form-item 有 prop 属性（字段名）或 label 文本，轮询等待渲染完成后注入
 */
const injectPermBadges = (retry = 0) => {
  const rootEl = formMainRef.value;
  if (!rootEl || !rootEl.querySelectorAll) return;
  const formItems = rootEl.querySelectorAll('.el-form-item');
  // 调试日志
  if (retry === 0) {
    console.log('[injectPermBadges] formItems.length=', formItems.length, 'fieldPermEditable=', props.fieldPermEditable, 'showFieldPermLabel=', props.showFieldPermLabel);
  }
  // 尚未渲染出表单字段则重试
  if (formItems.length === 0) {
    if (retry < 10) {
      setTimeout(() => injectPermBadges(retry + 1), 200);
    }
    return;
  }
  const fieldNameToEl = {};
  const labelToField = {};
  // 先收集字段名 -> label 文本映射（遍历 widget 树）
  traverseFieldWidgetsList(formJson.widgetList, (w) => {
    if (!w.formItemFlag) return;
    const fieldId = w.options?.name;
    if (!fieldId) return;
    labelToField[w.options?.label || fieldId] = fieldId;
  });
  formItems.forEach(itemEl => {
    // 优先 el-form-item 的 prop 属性（含字段名）
    let fieldId = itemEl.getAttribute('prop') || itemEl.getAttribute('data-field') || itemEl.getAttribute('data-name');
    if (fieldId && fieldId.includes('.')) {
      fieldId = fieldId.split('.').pop();
    }
    // 无 prop 时用 label 文本反查字段名
    if (!fieldId) {
      const labelEl = itemEl.querySelector('.el-form-item__label');
      const labelText = labelEl?.textContent?.trim?.() || '';
      fieldId = labelToField[labelText];
    }
    if (fieldId) {
      fieldNameToEl[fieldId] = itemEl;
    }
  });
  // 遍历表单字段，找到对应 el-form-item 注入徽标
  let injected = 0;
  traverseFieldWidgetsList(formJson.widgetList, (w) => {
    if (!w.formItemFlag) return;
    const fieldId = w.options?.name;
    if (!fieldId) return;
    let itemEl = fieldNameToEl[fieldId];
    // 兜底：按顺序匹配未占用的 el-form-item（DOM 渲染顺序与 widgetList 一致）
    if (!itemEl) {
      for (const el of formItems) {
        if (el._zenBadged) continue;
        const labelEl = el.querySelector('.el-form-item__label');
        const labelText = labelEl?.textContent?.trim?.() || '';
        if (labelText === (w.options?.label || '')) {
          itemEl = el;
          break;
        }
      }
    }
    if (!itemEl) return;
    itemEl._zenBadged = true;
    const labelEl = itemEl.querySelector('.el-form-item__label');
    if (!labelEl) return;
    // 已注入则跳过
    if (labelEl.querySelector('.zen-perm-badge')) return;
    injected++;
    const info = lfFieldPermData.find(ele => ele.fieldId == fieldId);
    const perm = info ? info.perm : 'E';
    const badge = document.createElement('span');
    badge.className = 'zen-perm-badge';
    badge.style.color = permColorMap[perm] || permColorMap.W;
    badge.style.cursor = 'pointer';
    badge.style.fontSize = '12px';
    badge.style.fontWeight = '600';
    badge.style.marginLeft = '6px';
    badge.style.border = '1px solid currentColor';
    badge.style.borderRadius = '3px';
    badge.style.padding = '0 3px';
    badge.style.lineHeight = '16px';
    badge.textContent = permTextMap[perm] || 'W';
    badge.title = perm === 'R' ? '只读' : perm === 'H' ? '隐藏' : '可编辑';
    badge.addEventListener('click', (e) => {
      e.stopPropagation();
      cyclePerm(fieldId, badge);
    });
    labelEl.appendChild(badge);
  });
  if (retry === 0) {
    console.log('[injectPermBadges] injected=', injected, 'formItems=', formItems.length);
  }
}

/** 循环切换字段权限 E(可编辑)->R(只读)->H(隐藏)->E */
const cyclePerm = (fieldId, badgeEl) => {
  const order = ['E', 'R', 'H'];
  const info = lfFieldPermData.find(ele => ele.fieldId == fieldId);
  const current = info ? info.perm : 'E';
  const nextIdx = (order.indexOf(current) + 1) % order.length;
  const nextPerm = order[nextIdx];
  if (info) {
    info.perm = nextPerm;
  } else {
    lfFieldPermData.push({ fieldId, perm: nextPerm });
  }
  badgeEl.style.color = permColorMap[nextPerm] || permColorMap.W;
  badgeEl.textContent = permTextMap[nextPerm] || 'W';
  badgeEl.title = nextPerm === 'R' ? '只读' : nextPerm === 'H' ? '隐藏' : '可编辑';
  emit('updateFieldPerm', { fieldId, perm: nextPerm });
}
onBeforeUnmount(() => {
  // 清除数据
  Object.keys(formJson).forEach(key => delete formJson[key]);
  Object.keys(formData).forEach(key => delete formData[key]);
  if (lfFieldPermData && Array.isArray(lfFieldPermData)) {
    lfFieldPermData.splice(0, lfFieldPermData.length);
  }
})
const submitForm = () => {
  vFormRef.value.getFormData().then(res => {
    //replaceEmptyStringWithNull(res);
    //res["select81554"] = null;
    //console.log("Form Validation===", JSON.stringify(res, null, 2))
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
        if (hasChooseApprove == 'true') {
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

/* 替换空字符串为null*/
const replaceEmptyStringWithNull = (obj) => {
  if (obj && typeof obj === 'object') {
    Object.keys(obj).forEach(key => {
      if (obj[key] === "") {
        obj[key] = null;
      } else if (typeof obj[key] === 'object') {
        replaceEmptyStringWithNull(obj[key]);
      }
    });
  }
  return obj;
}
/**保存草稿*/
const saveDraft = async () => {
  try {
    const formDataStr = await getFromData();
    const lfFields = JSON.parse(formDataStr);
    const bizFrom = {
      formCode: formCode,
      operationType: 30,
      isLowCodeFlow: true,
      lfFields: lfFields
    };
    proxy.$modal.loading();
    processOperation(bizFrom).then((res) => {
      if (res.code == 200) {
        proxy.$modal.msgSuccess("草稿保存成功");
      } else {
        proxy.$modal.msgError("草稿保存失败:" + res.errMsg);
      }
      proxy.$modal.closeLoading();
    }).catch((err) => {
      proxy.$modal.msgError("草稿保存失败");
      proxy.$modal.closeLoading();
    });
  } catch (error) {
    proxy.$modal.msgError("草稿保存失败:" + error);
  }
}
/**加载草稿*/
const loadDraftData = () => {
  proxy.$modal.confirm('加载草稿将覆盖当前表单内容，是否继续？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    proxy.$modal.loading();
    try {
      const res = await loadDraft(formCode);
      if (res.code == 200) {
        if (res.data && res.data.lfFields) {
          Object.assign(formData, res.data.lfFields);
          vFormRef.value.setFormData(formData);
          proxy.$modal.msgSuccess("草稿加载成功");
        } else {
          proxy.$modal.msgWarning("无可用草稿");
        }
      } else {
        proxy.$modal.msgError("加载草稿失败:" + res.errMsg);
      }
    } catch (error) {
      proxy.$modal.msgError("加载草稿失败:" + error);
    }
    proxy.$modal.closeLoading();
  }).catch(() => { });
}
defineExpose({
  handleValidate,
  getFromData
})
</script>
<style scoped lang="scss">
.form-container {
  /* 新增父级定位 */
  display: flex;
  flex-direction: column;
  margin: auto;
  background: #eee !important;
}

.form-container .el-main {
  background-color: #fff;
  flex: 1 1 auto;
}

.form-container .el-footer {
  background-color: #fff;
  border-top: 2px solid #f0f0f0;
  box-shadow: 0 1px 4px rgba(0, 21, 41, 0.08);
  display: flex;
  justify-content: flex-end;
  align-items: center;
  padding-right: 24px;
}
</style>