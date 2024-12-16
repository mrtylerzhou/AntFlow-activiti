<template>
    <div class="app-container">
        <div class="flex gap-2">
            <el-tag type="primary">{{ formCode }}</el-tag>
            <el-tag type="success" style="margin-left: 5px;">{{ processNumber }}</el-tag>
        </div>
        <el-tabs v-model="activeName" class="set-tabs" @tab-click="handleTabClick">
            <el-tab-pane label="表单信息" name="baseTab">
                <div class="approve">
                    <el-row style="padding-left: -5px;padding-right: -5px;">
                        <el-col :span="24" class="my-col" v-if="baseTabShow">
                            <div v-if="componentLoaded"  class="component">
                                <component :is="loadedComponent" 
                                :previewData="componentData" 
                                :lfFormData="lfFormDataConfig"
                                :lfFieldsData="lfFieldsConfig"
                                :lfFieldPerm="lfFieldControlVOs" 
                                :isPreview="true">
                                </component>
                            </div>
                            <div v-else-if="isOutSideAccess == 'true'">
                                <p v-if="formData" v-html="formData"></p>
                            </div> 
                        </el-col>
                        <el-col :span="24" class="my-col">
                            <el-form ref="approveFormRef" :model="approveForm" :rules="rules" class="my-form">
                                <el-form-item label="备注/说明" prop="remark">
                                    <el-input v-model="approveForm.remark" type="textarea" placeholder="请输入备注"
                                        :maxlength="100" show-word-limit :autosize="{ minRows: 4, maxRows: 4 }"
                                        :style="{ width: '100%' }"></el-input>
                                </el-form-item>
                                <el-form-item style="float: right;">
                                    <!-- <el-button type="primary" @click="approveSubmit(approveFormRef,3)">同意</el-button> -->
                                    <div v-for="btn in approvalButtons">
                                        <el-button style="margin: 5px;" v-if="btn.label"
                                            :type="pageButtonsColor[btn.value]"
                                            @click="approveSubmit(approveFormRef, btn.value)">
                                            {{ btn.label }}
                                        </el-button>
                                    </div>

                                </el-form-item>
                            </el-form>
                        </el-col>
                    </el-row>
                </div>
            </el-tab-pane>
            <el-tab-pane label="审批记录" name="flowStep">
                <div v-if="flowStepShow">
                    <FlowStepTable />
                </div>
            </el-tab-pane>
            <el-tab-pane label="流程预览" name="flowReview">
                <div v-if="flowReviewShow">
                    <ReviewWarp />
                </div>
            </el-tab-pane>
        </el-tabs>
        <label class="page-close-box" @click="close()"><img src="@/assets/images/back-close.png"></label>
        <employees-dialog v-model:visible="dialogVisible" :isMultiple="isMultiple" :title="dialogTitle"
            @change="sureDialogBtn" />
    </div>
</template>

<script setup>
import { ref, watch } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import cache from '@/plugins/cache';
import FlowStepTable from "@/components/Workflow/Preview/flowStepTable.vue"
import ReviewWarp from "@/components/Workflow/Preview/reviewWarp.vue"
import employeesDialog from '@/components/Workflow/dialog/usersDialog.vue'
import { pageButtonsColor, approvalPageButtons, approvalButtonConf } from "@/utils/flow/const"
import { getViewBusinessProcess, processOperation } from "@/api/mockflow"
import { loadDIYComponent, loadLFComponent } from '@/views/workflow/components/componentload.js'
const { proxy } = getCurrentInstance()
const route = useRoute();
const formCode = route.query?.formCode
const processNumber = route.query?.processNumber
const isOutSideAccess = route.query?.isOutSideAccess || false;
const isLowCodeFlow = route.query?.isLowCodeFlow || false;
const taskId = route.query?.taskId
const activeName = ref('baseTab')

let baseTabShow = ref(true);
let flowStepShow = ref(false);
let flowReviewShow = ref(false);

let componentData = ref(null);
let componentLoaded = ref(false);
let loadedComponent = ref(null);
let enableClass = ref(false);
let approvalButtons = ref([]);
const approveFormRef = ref(null);
const approveForm = reactive({
    remark: ''
});
let formData = ref(null);
const componentFormRef = ref(null);
const handleClickType = ref(null);
let dialogVisible = ref(false);
let dialogTitle = ref('');
let isMultiple = ref(false);//false 转办，true 加批

let lfFormDataConfig = ref(null);
let lfFieldsConfig = ref(null);
let lfFieldControlVOs = ref(null);
 
let rules = {
    remark: [{
        required: true,
        message: '请输入备注',
        trigger: 'blur'
    }]
};

let approveSubData = reactive({
    "taskId": taskId,
    "processNumber": processNumber,
    "formCode": formCode,
    "isOutSideAccessProc": isOutSideAccess,
    "outSideType": 2,
    "isLowCodeFlow": isLowCodeFlow
});

onMounted(() => {
    approvalButtons.value = approvalPageButtons.filter((c) => {
        return c.type == 'default';
    });
});
watch(approvalButtons, (val) => {
    enableClass.value = val.some(c => c.value == approvalButtonConf.resubmit);
})
watch(handleClickType, (val) => {
    dialogTitle.value = `设置${approvalButtonConf.buttonsObj[val]}人员`;
    isMultiple.value = val == approvalButtonConf.addApproval ? true : false;
})

/**
 * 点击页面按钮
 * @param param 
 * @param type 
 */
const approveSubmit = async (param, type) => {
    if (!param) return;
    handleClickType.value = type;
    if (type == approvalButtonConf.addApproval || type == approvalButtonConf.transfer) {
        addUserDialog();
        return;
    };
    param.validate(async (valid, fields) => {
        if (valid) {
            approveSubData.approvalComment = approveForm.remark;
            approveSubData.operationType = type;
            if (type == approvalButtonConf.resubmit) {
                await componentFormRef.value.handleValidate().then((isValid) => {
                    if (isValid) {
                        Object.assign(approveSubData, JSON.parse(componentFormRef.value.getFromData()));
                    }
                });
            };
            await approveProcess(approveSubData);//业务处理
        }
    })
}
/**
 * 表单预览
 */
const preview = () => {
    let queryParams = ref({
        "formCode": formCode,
        "processNumber": processNumber,
        "type": 2,
        "isOutSideAccessProc": isOutSideAccess,
        "isLowCodeFlow": isLowCodeFlow
    }); 
    proxy.$modal.loading();
    getViewBusinessProcess(queryParams.value).then(async (response) => {
        if (response.code == 200) {
            if (isOutSideAccess && isOutSideAccess == 'true') {//外部表单接入

                formData.value = response.data.formData;
            }
            else if (isLowCodeFlow && isLowCodeFlow == 'true') {//低代码表单
                lfFormDataConfig.value = response.data.lfFormData;
                lfFieldControlVOs.value = JSON.stringify(response.data.processRecordInfo.lfFieldControlVOs); 
                lfFieldsConfig.value = JSON.stringify(response.data.lfFields);
                loadedComponent.value = await loadLFComponent();
                componentLoaded.value = true; 
            } else {//自定义表单
                loadedComponent.value = await loadDIYComponent(formCode);
                componentData.value = response.data;
                componentLoaded.value = true;
            }
            let auditButtons = response.data.processRecordInfo?.pcButtons?.audit;
            if (Array.isArray(auditButtons) && auditButtons.length > 0) {
                approvalButtons.value = auditButtons.map(c => {
                    return { value: c.buttonType, label: c.name };
                }).sort(function (a, b) {
                    return a.value - b.value
                });
                approvalButtons.value = uniqueByMap(approvalButtons.value);
            }
        } else {
            ElMessage.error("获取表单数据失败:" + response.errMsg);
            close();
        }
        proxy.$modal.closeLoading();
    });
}
/**
 * 数组去重
 * @param arr 
 */
function uniqueByMap(arr) {
    if (!Array.isArray(arr)) {
        return
    }
    const res = new Map();
    return arr.filter((item) => !res.has(item.value) && res.set(item.value, true));
}
/**
 * 审批
 * @param param 
 */
const approveProcess = async (param) => {
    ElMessageBox.confirm('确定完成操作吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
    }).then(async () => {
        dialogVisible.value = false;
        proxy.$modal.loading();
        let resData = await processOperation(param);
        if (resData.code == 200) {
            ElMessage.success("审批成功");
            close();
        } else {
            ElMessage.error("审批失败:" + resData.errMsg);
        }
        proxy.$modal.closeLoading();
    }).catch(() => { });
}
/**
 * 关闭当前审批页
 */
const close = () => { 
    const obj = { path: "/flowtask/pendding" };
    proxy.$tab.closeOpenPage(obj);
}
/**
 * 选人员Dialog 弹框
 */
const addUserDialog = () => {
    dialogVisible.value = true;
}
/**
 * 确定Dialog 弹框
 */
const sureDialogBtn = async (data) => {
    approveSubData.operationType = handleClickType.value;
    approveSubData.approvalComment = data.remark;
    if (!isMultiple.value) {
        data.selectList.unshift({
            id: cache.session.get('userId'),
            name: cache.session.get('userName'),
        })
        approveSubData.userInfos = data.selectList;
    } else {
        approveSubData.signUpUsers = data.selectList;
    }
    //console.log('sureDialogBtn==========approveSubData=============', JSON.stringify(approveSubData));  
    await approveProcess(approveSubData);
}

const handleTabClick = async (tab, event) => {
    activeName.value = tab.paneName;
    if (tab.paneName == 'baseTab') {
        preview();
        baseTabShow.value = true;
        flowStepShow.value = false;
        flowReviewShow.value = false;
    } else if (tab.paneName == 'flowStep') {
        baseTabShow.value = false;
        flowStepShow.value = true;
        flowReviewShow.value = false;
    } else if (tab.paneName == 'flowReview') {
        baseTabShow.value = false;
        flowStepShow.value = false;
        flowReviewShow.value = true;
    }
};
handleTabClick({ paneName: "baseTab" });
</script>
<style lang="scss" scoped>
.component {
    background: white !important;
    padding: 30px !important;
    max-width: 720px !important;
    left: 0 !important;
    right: 0 !important;
    margin: auto !important;
}
.approve {
    width: 100%;
    height: 100%;
    background: #fff;
    position: relative;
    padding: 10px 50px;
    box-sizing: border-box;
}

.my-col {
    border: 1px solid #ebeef5;
    padding: 10px 20px 10px 20px;
    margin: 5px;
}

.my-form {
    max-width: 600px;
    min-height: 100px;
    margin: auto;
}

.el-timeline {
    --el-timeline-node-size-normal: 25px !important;
    --el-timeline-node-size-large: 25px !important;
}

.el-timeline-item__node--normal {
    left: -8px !important;
}

.el-timeline-item__node--large {
    left: -8px !important;
}

.el-timeline-item__wrapper {
    top: 0px !important;
}
</style>