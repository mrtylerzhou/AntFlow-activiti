<template>
    <div>
        <el-form ref="ruleFormRef" :model="form" :rules="rules"
            style="max-width: 600px;min-height: 100px; margin: auto;">
            <el-row :class="{ disableClss: props.isPreview }">
                <el-col :span="24">
                    <el-form-item label=" 车 牌 号" prop="licensePlateNumber">
                        <el-input v-model="form.licensePlateNumber"  style="width: 220px;"  placeholder="请输入车牌号" />
                    </el-form-item>
                </el-col>
                <el-col :span="24">
                    <el-form-item label="加油日期" prop="refuelTime">
                        <el-date-picker v-model="form.refuelTime" type="datetime"
                            placeholder="请选择加油日期" format="YYYY/MM/DD HH:mm" />
                    </el-form-item>
                </el-col>
                <el-col :span="24">
                    <el-form-item label="备注说明" prop="remark">
                        <el-input v-model="form.remark" type="textarea" placeholder="请输入备注说明" :maxlength="100"
                            show-word-limit :autosize="{ minRows: 4, maxRows: 4 }"
                            :style="{ width: '100%' }"></el-input>
                    </el-form-item>
                </el-col>
                <el-col :span="24" v-if="!props.isPreview">
                    <el-form-item style="float: right;">
                        <el-button type="primary" @click="handleSubmit">提交</el-button>
                    </el-form-item>
                </el-col>
            </el-row>
        </el-form>
    </div>
</template>

<script setup>
import { ref, watch, reactive, getCurrentInstance } from 'vue' 
const { proxy } = getCurrentInstance()

let props = defineProps({
    previewData: {
        type: Object,
        default: () => ({}),
    },
    isPreview: {
        type: Boolean,
        default: true,
    }
});
  
const ruleFormRef = ref(null)
 
const form = reactive({
    licensePlateNumber: props.previewData?.licensePlateNumber??'',
    refuelTime: props.previewData?.refuelTime??'',
    remark:props.previewData?.remark??''
})

let rules = {
    remark: [{
        required: true,
        message: '请输入备注说明',
        trigger: 'blur'
    }],
    licensePlateNumber: [{
        required: true,
        message: '请输入车牌号',
        trigger: 'blur'
    }],
    refuelTime: [{
        required: true,
        message: '请选择加油时间',
        trigger: 'blur'
    }],
};

watch(form, (val) => {
    //console.log(val)
})
const getFromData = () => {
    return JSON.stringify(form);
}
const handleSubmit = () => {
    handleValidate().then((isValid) => {
        if (isValid) {
            proxy.$emit("handleBizBtn", JSON.stringify(form))
        }
    });
}

const handleValidate = () => {
    return proxy.$refs['ruleFormRef'].validate((valid) => {
        if (!valid) {
            return false;
        }
    });
}
defineExpose({
    handleValidate,
    getFromData
})
</script>
<style scoped lang="scss">
.disableClss {
    pointer-events: none;
}
</style>