<template>
  <el-dialog title="选择条件" v-model="visibleDialog" :width="480" append-to-body class="condition_list">
    <p>请选择用来区分审批流程的条件字段</p>
    <p class="check_box">
      <template v-for="(item, index) in conditions" :key="index">
        <a :class="$func.toggleClass(conditionList, item, 'formId') && 'active'"
          @click="$func.toChecked(conditionList, item, 'formId')">{{ item?.showName }}</a>
        <br v-if="(index + 1) % 3 == 0" />
      </template>
    </p>
    <template #footer>
      <el-button @click="closeDialog">取 消</el-button>
      <el-button type="primary" @click="sureCondition">确 定</el-button>
    </template>
  </el-dialog>
</template>

<script setup name="selectConditionDialog">
import { watch } from "vue"
import { getConditions } from '@/api/workflow/mock'
import { useStore } from '@/store/modules/workflow'
import { NodeUtils } from '@/utils/antflow/nodeUtils'
import $func from '@/utils/antflow/index'
const route = useRoute()
const routePath = route.path || ''
const store = useStore()
let tableId = computed(() => store.tableId)
let lowCodeFormFields = computed(() => store.lowCodeFormField)
let useExternalForm = computed(() => store.useExternalForm)
let useAuxiliaryForm = computed(() => store.useAuxiliaryForm)
let storeConfigData = ref(null);
let configData = computed(() => props.nodeConfig || storeConfigData.value);
let conditionsConfig1 = computed(() => store.conditionsConfig1)

const props = defineProps({
  visible: {
    type: Boolean,
    default: false,
  },
  activeGroupIdx: {
    type: Number,
    default: null,
  },
  nodeConfig: {
    type: Object,
    default: null,
  },
});
let emits = defineEmits(["update:visible"]);
let conditions = ref([])//添加条件弹框显示
let conditionList = ref([])//添加条件弹框显示>是否选中 

let visibleDialog = computed({
  get() {
    return props.visible
  },
  set() {
    closeDialog()
  }
});

watch(conditionsConfig1, (val) => {
  storeConfigData.value = val.value?.conditionNodes[val.priorityLevel - 1];
});
watch(() => props.visible, (val) => {
  if (val) {
    getCondition();
  }
});
/**加载条件 */
const getCondition = async () => {
  conditionList.value = [];
  //DIY流程启用辅助表单时,按低代码表单方式加载条件(辅助表单字段作为契约)
  const isDIY = routePath.indexOf('diy-design') > 0;
  conditions.value = (isDIY && !useAuxiliaryForm.value) ? await loadDIYFormCondition() : await loadLFFormCondition();
  if (configData.value?.conditionList) {
    for (var i = 0; i < configData.value.conditionList[props.activeGroupIdx].length; i++) {
      var { formId, columnId } = configData.value.conditionList[props.activeGroupIdx][i];
      if (columnId == 0) {
        conditionList.value.push({ formId: formId, columnId: 0 })
      } else {
        conditionList.value.push(conditions.value.filter(item => { return item.formId == formId; })[0])
      }
    }
  }
  conditionList.value = conditionList.value.filter(item => { return item; })
}
/**
 * 确认/保存
 */
const sureCondition = () => {
  chooseCondition();
  handleClose();
};
/**
 * 固定表达式条件项
 * columnId 使用 ConditionTypeEnum 中表达式类型 code，默认 SpEL(20001)
 */
/**
 * 默认表达式类型 code（SpEL）
 * 同时作为前端弹窗中"表达式"项的 formId，减少魔法变量
 */
const DEFAULT_EXPRESSION_COLUMN_ID = '20001';

const EXPRESSION_CONDITION = {
  formId: DEFAULT_EXPRESSION_COLUMN_ID,
  columnId: DEFAULT_EXPRESSION_COLUMN_ID,
  showType: '1',
  showName: '表达式',
  columnName: 'expression',
  columnType: 'String',
  fieldTypeName: 'expression',
  multiple: false,
  multipleLimit: 0,
  fixedDownBoxValue: ''
};

/**
 * 退回次数条件项
 * columnId 对应后端 ConditionTypeEnum.CONDITION_TYPE_RETURN_COUNT(20002)
 * fieldTypeName='number' 复用 number 控件的 UI(运算符下拉 + 数值输入 + 区间)
 */
const RETURN_COUNT_CONDITION = {
  formId: '20002',
  columnId: '20002',
  showType: '4',
  showName: '退回次数',
  columnName: 'returnCount',
  columnType: 'String',
  fieldTypeName: 'number',
  multiple: false,
  multipleLimit: 0,
  fixedDownBoxValue: ''
};

/**自定义表单条件加载 */
const loadDIYFormCondition = () => {
  return new Promise(async (resolve, reject) => {
    let { data } = await getConditions({ tableId: tableId.value });
    if (Array.isArray(data)) {
      data.push(EXPRESSION_CONDITION);
      data.push(RETURN_COUNT_CONDITION);
    }
    resolve(data);
    reject([]);
  });
}

/**
 * 1、控件对应后端api的判断类型
 * 2、用于条件节点 对接 流程引擎中 条件判断
 * 3、与后端约定的值
 */
const widgetToColumnTypeCode = new Map([
  ["input", "10000"], //"int/fload/double/string" input
  ["number", "10001"], //"Double"
  ["select", "10000"], //"string" select
  ["checkbox", "10004"], //"string" checkbox
  ["radio", "10001"],
  ["switch", "10001"],
  ["time", "10002"],
  ["time-range", "10003"],
  ["data-range", "10002"],
  ["date", "10002"],
]);

/**
 * 1、控件是在条件节点 选择条件时候否显示
 * 2、对应后端数据解析 与后端约定的值
 * Mapping: 1-string 2-int 3-date 4-time 5-text/长字符串 6-boolean 7-二进制/byte
 */
const widgetToFieldTypeCode = new Map([
  ["input", "1"], //"String"
  ["number", "4"], //"time"
  ["select", "2"], //"int" select
  ["checkbox", "1"], //"String" checkbox
  //['radio', '2'], //  int radio
  ["switch", "6"], // boolean switch
  ["time", "1"],
  // ['time-range', '1'],
  // ['data-range', '1'],
  ["date", "1"],
]);
/**
 * 判断控件的值的类型 Number, String, Array, Date,DateTime
 */
const widgetToValueType = new Map([
  ["input", "String"], //"Double"
  ["number", "String"], //"Double"
  ["select", "Int"], //"Int" select
  ["checkbox", "String"], //checkbox 对应 VForm 是Array
  ["radio", "Int"],
  ["switch", "Boolean"],
  ["time", "String"],
  ["time-range", "String"],
  ["data-range", "String"],
  ["date", "String"],
]);
/**低代码表单条件加载 */
const loadLFFormCondition = () => {
  return new Promise((resolve, reject) => {
    //外部表单模式: 从多表单字段列表加载
    if (useExternalForm.value) {
      resolve(loadLFFormConditionMulti());
      return;
    }
    //内联表单模式: 原逻辑
    let conditionArr = [];
    if (!lowCodeFormFields.value.hasOwnProperty("formFields")) {
      resolve(conditionArr);
    }
    conditionArr = lowCodeFormFields.value.formFields.filter(item => { return item.type; }).map((item, index) => {
      if (widgetToFieldTypeCode.has(item.type)) {
        let optionGroup = [];
        if (item.options.optionItems) {
          optionGroup = item.options.optionItems.map(c => {
            let convertValue = parseInt(c.value);
            if (!isNaN(convertValue)) {
              return { key: convertValue, value: c.label }
            }
          });
          optionGroup = optionGroup.filter(c => c);
        }
        return {
          formId: index + 1,
          columnId: widgetToColumnTypeCode.get(item.type),
          showType: widgetToFieldTypeCode.get(item.type),
          showName: item.options.label,
          columnName: item.options.name,
          columnType: widgetToValueType.get(item.type),
          fieldTypeName: item.type,
          multiple: item.options.multiple,
          multipleLimit: item.options.multipleLimit,
          fixedDownBoxValue: JSON.stringify(optionGroup)
        }
      }
    })
    conditionArr = conditionArr.filter(nullableFilter);
    conditionArr.push(EXPRESSION_CONDITION);
    conditionArr.push(RETURN_COUNT_CONDITION);
    resolve(conditionArr);
    reject([]);
  });
};

/**
 * 外部表单模式: 从 store.lowCodeFormFieldsMulti 加载所有表单字段作为条件候选
 * 跨表单连续编号 formId,showName 前缀表单名以便用户区分
 */
const loadLFFormConditionMulti = () => {
  const forms = store.lowCodeFormFieldsMulti || [];
  const result = [];
  let fieldIdx = 0;
  for (const form of forms) {
    const fields = form.formFields || [];
    const formPrefix = forms.length > 1 ? `【${form.formName || form.formCode}】` : '';
    for (const item of fields) {
      if (!item.type || !widgetToFieldTypeCode.has(item.type)) continue;
      let optionGroup = [];
      if (item.options.optionItems) {
        optionGroup = item.options.optionItems.map(c => {
          let convertValue = parseInt(c.value);
          if (!isNaN(convertValue)) {
            return { key: convertValue, value: c.label }
          }
        }).filter(c => c);
      }
      fieldIdx++;
      result.push({
        formId: fieldIdx,
        formdataId: form.formdataId,
        columnId: widgetToColumnTypeCode.get(item.type),
        showType: widgetToFieldTypeCode.get(item.type),
        showName: formPrefix + (item.options.label || item.options.name),
        columnName: item.options.name,
        columnType: widgetToValueType.get(item.type),
        fieldTypeName: item.type,
        multiple: item.options.multiple,
        multipleLimit: item.options.multipleLimit,
        fixedDownBoxValue: JSON.stringify(optionGroup)
      });
    }
  }
  return result;
};

/**过滤空值 */
const nullableFilter = (elm) => {
  return (elm != null && elm !== false && elm !== "");
}
/**
 * 选择条件
 */
const chooseCondition = () => {
  if (!configData.value?.conditionList) return;
  for (var i = 0; i < conditionList.value.length; i++) {
    var { formId, columnId, showName, columnName, showType, columnType, fieldTypeName, multiple, multipleLimit, fixedDownBoxValue, formdataId } = conditionList.value[i];
    if ($func.toggleClass(configData.value.conditionList[props.activeGroupIdx], conditionList.value[i], "formId")) {
      continue;
    }
    const judgeObj = NodeUtils.createJudgeNode(formId, columnId, 2, showName, showType, columnName, columnType, fieldTypeName, multiple, multipleLimit, fixedDownBoxValue);
    //外部表单模式: 附加 formdataId 便于后端定位字段所属表单
    if (formdataId != null) {
      judgeObj.formdataId = formdataId;
    }
    if (columnId == 0) {
      configData.value.conditionList[props.activeGroupIdx].push({ formId: formId, columnId: columnId, type: 1, showName: '发起人' });
    } else {
      configData.value.conditionList[props.activeGroupIdx].push(judgeObj)
    }
  }
  for (let i = configData.value.conditionList[props.activeGroupIdx].length - 1; i >= 0; i--) {
    if (!$func.toggleClass(conditionList.value, configData.value.conditionList[props.activeGroupIdx][i], "formId")) {
      configData.value.conditionList[props.activeGroupIdx].splice(i, 1);
    }
  }
  configData.value.conditionList[props.activeGroupIdx].sort(function (a, b) { return a.columnId - b.columnId; });
}

/**
 * 关闭弹窗
 */
const closeDialog = () => {
  handleClose();
};
const handleClose = () => {
  emits("update:visible", false);
}; 
</script>
<style scoped lang="scss">
@use "@/assets/styles/antflow/dialog.scss";

.condition_list {
  .el-dialog__body {
    padding: 16px 26px;
  }

  p {
    color: #666666;
    margin-bottom: 10px;

    &>.check_box {
      margin-bottom: 0;
      line-height: 36px;
    }
  }
}
</style>