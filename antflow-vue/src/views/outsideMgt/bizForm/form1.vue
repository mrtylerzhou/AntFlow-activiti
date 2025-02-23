<template>
    <div>
        <el-form ref="ruleFormRef" :model="form" :rules="rules" label-position="top" style="max-width: 600px;min-height: 100px; margin: 50px auto;"> 
            <div style="margin: 30px;">
                <el-text class="mx-1" style="margin-bottom: 30px;" size="large" type="danger">*发起测试</el-text>
                <br/>
                <el-text class="mx-1"  size="large" type="primary">*业务方（第三方）系统的表单，需要审批流程，接入本流程引擎</el-text>
            </div>
            <el-row>
                <el-col :span="24">
                    <el-form-item label="人员名称" prop="userName">
                        <el-input v-model="form.userName" placeholder="请输入人员名称" :style="{ width: '100%' }" />
                    </el-form-item>
                </el-col>
        
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
                <el-col :span="24">
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
import { processSubmit} from "@/api/outsideApi";
import { formToHTMLString } from '@/utils/index'
const { proxy } = getCurrentInstance() 
  
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
    userName: '',
    accountType: 1,
    remark:'测试'
})

let rules = {
    userName: [{
        required: true,
        message: '请输入人员名称',
        trigger: 'blur'
    }],
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

watch(form, (val) => {
    //console.log(val)
})
const getFromData = () => {
    return JSON.stringify(form);
}
const handleSubmit = () => {
  const form = document.querySelector('form'); 
  const htmlString = formToHTMLString(form);
  let param =  {
        "formCode":"adbgxx",
        "businessPartyId":1, 
        "businessPartyMark":"kbgschool",
        "templateMark":"",
        "outSideType":2,
        "userId":"1",
        "formDataPc":htmlString
        }
     proxy.$refs['ruleFormRef'].validate((valid) => {
        if (valid) {
            proxy.$modal.loading(); 
            processSubmit(param).then(res => {
                proxy.$modal.closeLoading();
                if (res.code == 200) {
                    proxy.$message.success('提交成功');
                } else {
                    console.log('res======',JSON.stringify(res))
                    proxy.$message.error(res.errMsg);
                }
            }).catch((err) => {  
                    proxy.$modal.closeLoading(); 
            });
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
.el-form-item__label-wrap {
    margin-right: 0px !important;
}
</style>