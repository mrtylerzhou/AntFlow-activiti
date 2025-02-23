<template>
    <div>
        <el-form ref="ruleFormRef" :model="form" :rules="rules"
            style="max-width: 600px;min-height: 100px; margin: auto;">
            <el-row :class="{ disableClss: props.isPreview }">
                <el-col :span="24">
                    <el-form-item label="申请账户类型" prop="accountType">
                        <el-select v-model="form.accountType" placeholder="请选择账户类型" :style="{ width: '100%' }">
                            <el-option v-for="(item, index) in accountTypeOptions" :key="index" :label="item.label"
                                :value="item.value"></el-option>
                        </el-select>
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
import { ref,reactive, getCurrentInstance } from 'vue' 
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
let accountTypeOptions = [{
    "label": "腾讯云",
    "value": 1
}, {
    "label": "百度云",
    "value": 2
}, {
    "label": "阿里云",
    "value": 3
}];
const form = reactive({
    accountType: props.previewData?.accountType??'',
    remark:props.previewData?.remark??''
})

let rules = {
    remark: [{
        required: true,
        message: '请输入备注说明',
        trigger: 'blur'
    }],
    accountType: [{
        required: true,
        message: '请选择账户类型',
        trigger: 'change'
    }],
};
/**以下是通用方法不需要修改 views/bizentry/index.vue中调用*/
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