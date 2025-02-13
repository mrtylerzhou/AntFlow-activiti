<template>
    <div>
        <el-form ref="ruleFormRef" :model="form" :rules="rules"
            style="max-width: 600px;min-height: 100px; margin: auto;">
            <el-row :class="{ disableClss: props.isPreview }">
                <el-col :span="24">
                    <el-form-item label="采购人姓名" prop="PurchaseUserName">
                        <el-input v-model="form.PurchaseUserName"  style="width: 220px;"  placeholder="请输入采购人姓名" />
                    </el-form-item>
                </el-col>
                <el-col :span="24">
                    <el-form-item label="采购日期" prop="PurchaseDate">
                        <el-date-picker v-model="form.PurchaseDate" type="datetime"
                            placeholder="请选择采购日期" format="YYYY/MM/DD HH:mm" />
                    </el-form-item>
                </el-col>
                <el-col :span="24">
                    <el-form-item label="采购金额" prop="PurchaseMoney">
                        <el-input v-model="form.PurchaseMoney"  style="width: 220px;"  placeholder="请输入采购金额" />
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
import { ref, reactive, getCurrentInstance } from 'vue' 
const { proxy } = getCurrentInstance()

let props = defineProps({
    previewData: {
        type: Object,
        default: () => ({}),
    },
    reSubmit: {//是否重新提交
        type: Boolean,
        default: false,
    },
    isPreview: {
        type: Boolean,
        default: true,
    }
});
  
const ruleFormRef = ref(null)
 
const form = reactive({
    PurchaseUserName: props.previewData?.PurchaseUserName??'',
    PurchaseDate: props.previewData?.PurchaseDate??'',
    PurchaseMoney: props.previewData?.PurchaseMoney??'',
    remark:props.previewData?.remark??''
})

let rules = {
    remark: [{
        required: true,
        message: '请输入备注说明',
        trigger: ['blur', 'change'],
    }],
    PurchaseUserName: [{
        required: true,
        message: '请输入采购人姓名',
        trigger: ['blur', 'change'],
    }],
    PurchaseDate: [{
        required: true,
        message: '请选择采购时间',
        trigger: ['blur', 'change'],
    }],
    PurchaseMoney: [{
        required: true,
        message: '请输入采购金额',
        trigger: ['blur', 'change'],
    }],
}; 
/**以下是通用方法不需要修改 views/bizentry/index.vue中调用*/
const getFromData = () => {
    return new Promise((resolve, reject) => {
        try {
            resolve(JSON.stringify(form));
        } catch (error) {
            reject(error);
        }
    });
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