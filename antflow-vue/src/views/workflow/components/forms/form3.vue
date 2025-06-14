<template>
    <div class="form-container">
        <el-form ref="ruleFormRef" :model="form" :rules="rules"
            style="max-width: 600px;min-height: 100px; margin: auto;">
            <el-row :class="{ disableClss: props.isPreview }">
                <el-col :span="24">
                    <el-form-item label=" 车 牌 号" prop="licensePlateNumber">
                        <el-input v-model="form.licensePlateNumber" style="width: 220px;" placeholder="请输入车牌号" />
                    </el-form-item>
                </el-col>
                <el-col :span="24">
                    <el-form-item label="加油日期" prop="refuelTime">
                        <el-date-picker v-model="form.refuelTime" type="datetime" placeholder="请选择加油日期"
                            format="YYYY/MM/DD HH:mm" />
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
import { useStore } from '@/store/modules/workflow';
const { proxy } = getCurrentInstance();
const store = useStore();
const formRenderConfig = computed(() => store.formRenderConfig)
const { formCode, hasChooseApprove = false } = formRenderConfig.value;
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
const ruleFormRef = ref(null)
/**定义表单字段和预览，根据实际业务表单修改*/
const form = reactive({ ...props.previewData });
/**表单字段验证，根据实际业务表单修改*/
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
    padding: 30px;
    max-width: 750px;
    min-height: 95%;
    left: 0;
    bottom: 0;
    right: 0;
    margin: auto;
}
</style>