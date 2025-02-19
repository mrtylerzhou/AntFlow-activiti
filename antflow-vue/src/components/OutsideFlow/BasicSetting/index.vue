<template>
  <div class="form-container">
    <el-form
      ref="ruleFormRef"
      :model="form"
      :rules="rules"
      label-width="auto"
      style="max-width: 600px; margin: auto"
    >
      <el-form-item label="项目名称" prop="businessPartyId">
        <el-select
          v-model="form.businessPartyId"
          placeholder="请选择项目"
          :style="{ width: '100%' }"
        >
          <el-option
            v-for="(item, index) in businessPartyOptions"
            :key="index"
            :label="item.label"
            :value="item.value"
          ></el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="业务类型" prop="formCode">
        <template #label>
          <span>
            <el-tooltip content="项目注册的业务表单集合" placement="top">
              <el-icon><question-filled /></el-icon>
            </el-tooltip>
            业务类型
          </span>
        </template>
        <el-select
          @change="selectFormCodeChanged"
          v-model="form.formCode"
          placeholder="请选择业务类型"
          :style="{ width: '100%' }"
        >
          <el-option
            v-for="(item, index) in formCodeOptions"
            :key="index"
            :label="item.label"
            :value="item.value"
          ></el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="流程名称" prop="bpmnName">
        <el-input
          v-model="form.bpmnName"
          placeholder="请输入审批名称"
          :style="{ width: '100%' }"
        />
      </el-form-item>
      <el-form-item label="审批人去重" prop="deduplicationType">
        <el-select
          v-model="form.deduplicationType"
          placeholder="请选择去重类型"
          :style="{ width: '100%' }"
        >
          <el-option
            v-for="(item, index) in duplicateOptions"
            :key="index"
            :label="item.label"
            :value="item.value"
          ></el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="流程说明" prop="remark">
        <el-input
          v-model="form.remark"
          type="textarea"
          placeholder="请输入流程说明"
          :maxlength="100"
          show-word-limit
          :autosize="{ minRows: 4, maxRows: 4 }"
          :style="{ width: '100%' }"
        ></el-input>
      </el-form-item>
      <el-form-item style="float: right">
        <el-button type="primary" @click="nextSubmit(ruleFormRef)"
          >下一步》》》</el-button
        >
      </el-form-item>
    </el-form>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, getCurrentInstance } from "vue";
import { NodeUtils } from "@/utils/flow/nodeUtils";
import { useStore } from "@/store/modules/outsideflow";
import { getApplicationsByPartyMarkId } from "@/api/outsideApi";
const { proxy } = getCurrentInstance();
const emit = defineEmits(["nextChange"]);
let store = useStore();
let props = defineProps({
  basicData: {
    type: Object,
    default: () => null,
  },
});

const generatorID = "PROJECT_" + NodeUtils.idGenerator();
const ruleFormRef = ref(null);
let duplicateOptions = [
  {
    label: "不去重",
    value: 1,
  },
  {
    label: "前去重",
    value: 2,
  },
  {
    label: "后去重",
    value: 3,
  },
];
let businessPartyOptions = [
  {
    label: "克伯格学校",
    value: 1,
  },
];

let formCodeOptions = ref([]);
let form = reactive({
  bpmnCode: generatorID,
  bpmnName: undefined,
  businessPartyId: undefined,
  appId: undefined,
  isOutSideProcess: 1,
  bpmnType: 1,
  formCode: undefined,
  remark: undefined,
  effectiveStatus: false,
  deduplicationType: 1,
});
onMounted(async () => {
  //console.log('props.basicData=====',JSON.stringify(props.basicData))
  if (props.basicData) {
    form.bpmnName = props.basicData.bpmnName;
    form.bpmnCode = props.basicData.bpmnCode;
    form.formCode = props.basicData.formCode;
    form.businessPartyId = props.basicData.businessPartyId;
    form.appId = props.basicData.appId;
    form.remark = props.basicData.remark;
    form.deduplicationType = props.basicData.deduplicationType;
  } 
  if (form.businessPartyId) {
    await getApplicationsList(form.businessPartyId);
  } 
});
watch(
  () => form.businessPartyId,
  (val) => {
    if (val) { 
      getApplicationsList(val);
    }
  }
);
watch(
  () => [formCodeOptions.value, form.formCode],
  (newVal) => {
    if (newVal[1] && newVal[0].length > 0) {
      let _formCode = newVal[1] || props.basicData.formCode;
      const result = formCodeOptions.value.find((c) => c.value == _formCode);
      if (result) {
        store.setBasideFormConfig({
          partyMarkId: result.businessPartyId,
          formCode: result.value,
          userRequestUri: result.userRequestUri,
          roleRequestUri: result.roleRequestUri,
        });
      }
    }
  }
);

let rules = {
  formCode: [{ required: true, message: "请选择项目类型", trigger: "blur" }],
  bpmnName: [{ required: true, message: "请输入流程名称", trigger: "blur" }],
  bpmnCode: [{ required: true, message: "请输入流程编号", trigger: "blur" }],
  businessPartyId: [
    { required: true, message: "请输选择项目", trigger: "change" },
  ],
};
const nextSubmit = (ruleFormRef) => {
  if (!ruleFormRef) return;
  ruleFormRef.validate((valid, fields) => {
    if (valid) {
      emit("nextChange", { label: "流程设计", key: "processDesign" });
    }
  });
};
// 给父级页面提供得获取本页数据得方法
const getData = () => {
  return new Promise((resolve, reject) => {
    proxy.$refs["ruleFormRef"].validate((valid, fields) => {
      if (!valid) {
        emit("nextChange", { label: "基础设置", key: "basicSetting" });
        reject({ valid: false });
      }
      form.effectiveStatus = form.effectiveStatus ? 1 : 0;
      resolve({ formData: form }); // TODO 提交表单
    });
  });
};

const selectFormCodeChanged = (value) => {};

const getApplicationsList = async (partMarkId) => {
  await getApplicationsByPartyMarkId(partMarkId).then((response) => {
    if(response.code != 200) return;
    formCodeOptions.value = response.data.map((item) => {
      return {
        businessPartyId: item.businessPartyId,
        value: item.processKey,
        label: item.title,
        userRequestUri: item.userRequestUri,
        roleRequestUri: item.roleRequestUri,
      };
    });
  });
};

defineExpose({
  getData,
});
</script>
<style scoped>
.form-container {
  background: white !important;
  padding: 30px;
  max-width: 600px;
  min-height: 520px;
  left: 0;
  bottom: 0;
  right: 0;
  margin: auto;
}
</style>
