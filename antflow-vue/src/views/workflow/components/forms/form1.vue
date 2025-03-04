<template>
    <div class="form-container">
        <el-form ref="ruleFormRef" :model="form" :rules="rules">
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
                <el-col :span="24" v-if="!props.isPreview && !props.reSubmit">
                    <el-form-item>
                        <el-button type="primary" @click="handleSubmit">提交</el-button>
                    </el-form-item>
                </el-col>
            </el-row>
        </el-form>
        <TagUserSelect v-if="hasChooseApprove == 'true'" v-model:formCode="formCode" @chooseApprove="chooseApprovers" />
    </div>
</template>

<script setup>
import { ref, reactive, getCurrentInstance } from 'vue';
import TagUserSelect from "@/components/BizSelects/TagApproveSelect/index.vue";
const { proxy } = getCurrentInstance();
const route = useRoute();
const formCode = route.query?.formCode ?? '';
const hasChooseApprove = route.query?.hasChooseApprove ??'false';
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
/**定义表单字段和预览，根据实际业务表单修改*/
const form = reactive({
    accountType: props.previewData?.accountType ?? '',
    remark: props.previewData?.remark ?? ''
})
/**表单字段验证，根据实际业务表单修改*/
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

/**自选审批人 */
const chooseApprovers = (data) => {
    //console.log('data=========',JSON.stringify(data));
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
        else if(hasChooseApprove == 'true'){    
            if (!form.approversValid || form.approversValid == false) {  
                proxy.$modal.msgError('请选择自选审批人'); 
                return Promise.reject(false);
            }  
        }
        else{
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