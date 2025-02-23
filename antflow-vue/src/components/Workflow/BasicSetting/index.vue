<template>
    <div class="form-container">
        <el-form ref="ruleFormRef" :model="form" :rules="rules" label-width="auto"
            style="max-width: 600px;margin: auto;">
            <!-- <el-form-item label="流程分组" prop="bpmnType">
                    <el-select v-model="form.bpmnType" placeholder="请选择分组" :style="{ width: '100%' }">
                        <el-option v-for="(item, index) in bpmnTypeOptions" :key="index" :label="item.label"
                            :value="item.value"></el-option>
                    </el-select>
                </el-form-item> -->

            <el-form-item label="模板类型" prop="formCode">
                <el-select v-model="form.formCode" placeholder="请选择模板类型" :style="{ width: '100%' }">
                    <el-option v-for="(item, index) in formCodeOptions" :key="index" :label="item.value"
                        :value="item.key"></el-option>
                </el-select>
            </el-form-item>

            <el-form-item label="流程名称" prop="bpmnName">
                <template #label>
                    <span>
                        <el-tooltip content="同【模板类型】名称一致，不需手动输入" placement="top">
                        <el-icon><question-filled /></el-icon>
                        </el-tooltip>
                        流程名称
                    </span>
                </template>
                <el-input v-model="form.bpmnName" placeholder="请输入审批名称" :style="{ width: '100%' }" readonly/>
            </el-form-item>

            <el-form-item label="审批人去重" prop="deduplicationType">
                <el-select v-model="form.deduplicationType" placeholder="请选择去重类型" :style="{ width: '100%' }">
                    <el-option v-for="(item, index) in duplicateOptions" :key="index" :label="item.label"
                        :value="item.value"></el-option>
                </el-select>
            </el-form-item>
            <el-form-item label="流程说明" prop="remark">
                <el-input v-model="form.remark" type="textarea" placeholder="请输入流程说明" :maxlength="100" show-word-limit
                    :autosize="{ minRows: 4, maxRows: 4 }" :style="{ width: '100%' }"></el-input>
            </el-form-item>
            <!-- <el-form-item style="float: right;">
                <el-button type="primary" @click="nextSubmit(ruleFormRef)">下一步》》》</el-button>
            </el-form-item> -->
        </el-form>
    </div>
</template>

<script setup>
import { ref, reactive, onMounted, watch,getCurrentInstance } from 'vue'
import { NodeUtils } from '@/utils/flow/nodeUtils'
import { getDIYFromCodeData } from "@/api/workflow";
import { getLowCodeFlowFormCodes } from "@/api/lowcodeApi";
const { proxy } = getCurrentInstance()
const emit = defineEmits(['nextChange'])
let loading = ref(false);
let props = defineProps({
    flowType: {
        type: String,
        default: () => (''),
    },
    basicData: {
        type: Object,
        default: () => (null),
    }
});

const generatorID = "PROJECT_" + NodeUtils.idGenerator();
const ruleFormRef = ref(null);
let duplicateOptions = [{
    "label": "不去重",
    "value": 1
}, {
    "label": "前去重",
    "value": 2
}, {
    "label": "后去重",
    "value": 3
}];

let formCodeOptions = ref([]); 
const form = reactive({
    bpmnName: '',
    bpmnCode: generatorID,
    bpmnType: 1,
    formCode: '',
    remark: '',
    effectiveStatus: false,
    deduplicationType: 1
})
watch(() => form.formCode,(val) => {
    if (val) { 
        formCodeOptions.value.forEach(item => {
            if (item.key == val) {
                form.bpmnName = item.value;
            }
        })
    }
});
 
onMounted(async () => {
    //console.log('basicData=====props=======',JSON.stringify(props.basicData))
    if (props.basicData) {
        form.bpmnName = props.basicData.bpmnName;
        form.bpmnCode = props.basicData.bpmnCode;
        form.formCode = props.basicData.formCode;
        form.remark = props.basicData.remark;
        form.deduplicationType = props.basicData.deduplicationType;
    }  
    if (props.flowType == 'DIY') {
        getDIYFromCodeList();
    } else if (props.flowType == 'LF') {
        getLFFromCodeList();
    } 
});
/**获取全部DIY FromCode */
const getDIYFromCodeList = async()=> {
   loading.value = true;
   await getDIYFromCodeData().then((res) => {
    loading.value = false;
        if (res.code == 200) { 
            formCodeOptions.value = res.data;
        }
   });
}
/**获取全部LF FromCode */
const getLFFromCodeList = async()=> {
   loading.value = true;
   await getLowCodeFlowFormCodes().then((res) => {
    loading.value = false;
        if (res.code == 200) { 
            formCodeOptions.value = res.data;
        }
   });
}
  
let rules = {
    formCode: [{
        required: true,
        message: '请选择分类',
        trigger: 'blur'
    }],
    bpmnName: [{
        required: true,
        message: '请输入流程名称',
        trigger: 'change'
    }],
    bpmnCode: [{
        required: true,
        message: '请输入流程编号',
        trigger: 'blur'
    }],
};

// 给父级页面提供得获取本页数据得方法
const getData = () => {
    return new Promise((resolve, reject) => {
        proxy.$refs['ruleFormRef'].validate((valid, fields) => {
            if (!valid) {
                emit('nextChange', { label: "基础设置", key: "basicSetting" })
                reject({valid:false});
            }
            form.effectiveStatus = form.effectiveStatus ? 1 : 0;
            resolve({ formData: form })  // TODO 提交表单
        })
    })
};
defineExpose({
    getData
})
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