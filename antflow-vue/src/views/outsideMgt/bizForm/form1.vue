<template>
    <div class="form-container">
        <div>
            <el-row :gutter="10">
                <el-col :span="24" class="mb20">
                    <el-alert title="*发起测试：模拟外部系统表单，接入本流程引擎" type="warning" center effect="dark" :closable="false" />
                </el-col>
                <el-col :span="12" class="mb20">
                    <el-tag type="success" effect="dark" class="mr10">{{ formCodeTitle }}</el-tag>
                    <el-tag type="success" effect="dark">{{ formCode }}</el-tag>
                </el-col>
            </el-row>
        </div>
        <el-form ref="ruleFormRef" :model="form" :rules="rules" label-position="top">
            <el-row>
                <el-col :span="24" class="mb20" v-if="templateVisible">
                    <el-text size="large" type="danger">*需要测试条件请勾选,勾选条件后流转将会走满足条件的分支</el-text>
                    <el-checkbox-group v-model="templateMarks" class="mt10">
                        <el-checkbox v-for="item in templateConditionList" :value="item.templateMark" :label="item.name"
                            border />
                    </el-checkbox-group>
                </el-col>

                <el-col :span="24">
                    <el-form-item label="人员名称" prop="userName">
                        <el-input v-model="form.userName" placeholder="请输入人员名称" :style="{ width: '100%' }" />
                    </el-form-item>
                </el-col>
                <el-col :span="24">
                    <el-form-item label="年龄" prop="userName">
                        <el-input v-model="form.age" placeholder="请输入年龄" :style="{ width: '100%' }" />
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
import { ref, reactive, getCurrentInstance, watch } from 'vue'
import { processSubmit } from "@/api/outsideApi";
import { getConditionTemplatelist } from "@/api/outsideApi"; 
const { proxy } = getCurrentInstance()
const { query } = useRoute();
import cache from '@/plugins/cache';
const ruleFormRef = ref(null)
let templateMarks = ref([]);
let templateConditionList = ref(null);
let templateVisible = ref(false);
const form = reactive({
    userName: '张三',
    age: 18,
    remark: '外部系统业务表单接入测试'
})

let rules = {
    userName: [
        { required: true, message: '请输入人员名称', trigger: 'blur' },
        { pattern: /^.{2,10}$/, message: '长度必须在2到10位之间', trigger: 'blur' }
    ],
    age: [{
        required: true,
        message: '请输入年龄',
        trigger: 'blur'
    }],
};

let formDataDemo = ref([]);

watch(() => form, (val) => {
    formDataDemo.value = [{
        label: '人员姓名',
        value: val.userName
    }, {
        label: '年龄',
        value: val.age
    }, {
        label: '备注',
        value: val.remark
    }];
}, { deep: true });

const formCode = query.fc;
const formCodeTitle = decodeURIComponent(query.fcname);
const applicationId = query.appid;

onMounted(() => {
    formDataDemo.value = [{
        label: '人员姓名',
        value: form.userName
    }, {
        label: '年龄',
        value: form.age
    }, {
        label: '备注',
        value: form.remark
    }];
    getTempList();
});
function getTempList() {
    if (applicationId <= 0) {
        return;
    }
    else {
        getConditionTemplatelist(applicationId).then(response => {
            templateConditionList.value = response.data;
        });
    }
}
watch(templateConditionList,val => {
    templateVisible.value = val && val.length > 0;
})
const getFromData = () => {
    return JSON.stringify(form);
}
const handleSubmit = () => { 
    let param = {
        formCode: query.fc,
        templateMarks: templateMarks.value,
        userId: cache.session.get('userId'),
        formDataPc: JSON.stringify(formDataDemo.value)
    }
    //console.log('param=====', JSON.stringify(param));
    proxy.$refs['ruleFormRef'].validate((valid) => {
        if (valid) {
            proxy.$modal.loading();
            processSubmit(param).then(res => {
                proxy.$modal.closeLoading();
                if (res.code == 200) {
                    proxy.$message.success('提交成功');
                    const obj = { path: "/flowtask/mytask" };
                    proxy.$tab.openPage(obj);
                } else {
                    console.log('res======', JSON.stringify(res))
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