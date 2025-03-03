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
                    <el-form-item label="采购金额" prop="PlanProcurementTotalMoney">
                        <el-input v-model="form.PlanProcurementTotalMoney"  style="width: 220px;"  placeholder="请输入采购金额" />
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
                    <el-form-item>
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
/**传参不需要修改*/
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
 
/**定义表单字段和预览，根据实际业务表单修改*/
const form = reactive({
    PurchaseUserName: props.previewData?.purchaseUserName??'',
    PurchaseDate: props.previewData?.purchaseDate??'',
    PlanProcurementTotalMoney: props.previewData?.planProcurementTotalMoney??'',
    remark:props.previewData?.remark??''
})
/**表单字段验证，根据实际业务表单修改*/
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
    PlanProcurementTotalMoney: [{
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