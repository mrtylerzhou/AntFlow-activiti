<template>
    <div class="form-container">
        <el-form ref="ruleFormRef" :model="form" :rules="rules"
            style="max-width: 600px;min-height: 100px; margin: auto;">
            <el-row :class="{ disableClss: props.isPreview }">
                <el-col :span="12">
                    <el-form-item label="报销姓名" prop="refundUserName">
                        <el-input v-model="form.refundUserName" style="width: 220px;" placeholder="请输入报销人姓名" />
                    </el-form-item>
                </el-col>
                <el-col :span="12">
                    <el-form-item label="报销日期" prop="refundDate">
                        <el-date-picker v-model="form.refundDate" type="datetime" placeholder="请选择报销日期"
                            format="YYYY/MM/DD HH:mm" />
                    </el-form-item>
                </el-col>
                <el-col :span="24">
                    <el-form-item label="报销金额" prop="refundMoney">
                        <el-input-number v-model="form.refundMoney" :min="1" :max="10000" :style="{ width: '100%' }" />
                    </el-form-item>
                </el-col>
                <el-col :span="24">
                    <el-form-item label="备注说明" prop="remark">
                        <el-input v-model="form.remark" type="textarea" placeholder="请输入备注说明" :maxlength="100"
                            show-word-limit :autosize="{ minRows: 4, maxRows: 4 }"
                            :style="{ width: '100%' }"></el-input>
                    </el-form-item>
                </el-col>
                <el-col :span="24" v-if="!props.isPreview && props.showSubmit">
                    <el-form-item>
                        <el-button type="primary" @click="handleSubmit">提交</el-button>
                    </el-form-item>
                </el-col>
            </el-row>
        </el-form>
        <TagApproveSelect v-if="hasChooseApprove == 'true'" v-model:formCode="formCode"
            @chooseApprove="chooseApprovers" />
    </div>
</template>

<script setup>
import { ref, reactive, getCurrentInstance } from 'vue';
import TagApproveSelect from "@/components/BizSelects/TagApproveSelect/index.vue";
const { proxy } = getCurrentInstance();
const route = useRoute();
const formCode = route.query?.formCode ?? '';
const hasChooseApprove = route.query?.hasChooseApprove ?? 'false';
/**传参不需要修改*/
let props = defineProps({
    previewData: {
        type: Object,
        default: () => ({}),
    },
    showSubmit: {//是否显示提交按钮
        type: Boolean,
        default: false,
    },
    isPreview: {
        type: Boolean,
        default: true,
    }
});

const ruleFormRef = ref(null);

/**定义表单字段和预览，根据实际业务表单修改*/
const { refundUserName, refundDate, refundMoney, remark } = props.previewData;
const form = reactive({ RefundType: 1, refundUserName, refundDate, refundMoney, remark });
/**表单字段验证，根据实际业务表单修改*/
let rules = {
    remark: [{
        required: true,
        message: '请输入备注说明',
        trigger: ['blur', 'change'],
    }],
    refundUserName: [{
        required: true,
        message: '请输入报销人姓名',
        trigger: ['blur', 'change'],
    }],
    refundDate: [{
        required: true,
        message: '请选择报销时间',
        trigger: ['blur', 'change'],
    }],
    refundMoney: [{
        required: true,
        message: '请输入报销金额',
        trigger: ['blur', 'change'],
    }],
};
/**以下是通用方法不需要修改 views/startFlow/index.vue中调用*/

/**自选审批人 */
const chooseApprovers = (data) => {
    form.approversList = data.approvers;
    form.approversValid = data.nodeVaild;
}
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
            return Promise.reject(false);
        }
        else if (hasChooseApprove == 'true') {
            if (!form.approversValid || form.approversValid == false) {
                proxy.$modal.msgError('请选择自选审批人');
                return Promise.reject(false);
            }
        }
        else {
            return Promise.resolve(true);
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

.form-container {
    background: white !important;
    padding: 10px;
    max-width: 750px;
    min-height: 58vh;
    left: 0;
    bottom: 0;
    right: 0;
    margin: auto;
}
</style>