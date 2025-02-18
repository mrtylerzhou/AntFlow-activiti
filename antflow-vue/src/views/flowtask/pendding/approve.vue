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
                        <el-col :span="24" class="my-col">
                            <div v-for="btn in approvalButtons" style="float: left;">
                                <el-button style="margin: 5px;" v-if="btn.label" :type="pageButtonsColor[btn.value]"
                                    @click="clickApproveSubmit(btn.value)">
                                    {{ btn.label }}
                                </el-button>
                            </div>
                        </el-col>
                        <el-col :span="24" class="my-col" v-if="baseTabShow">
                            <div v-if="componentLoaded" class="component">
                                <component ref="componentFormRef" :is="loadedComponent" :previewData="componentData"
                                    :lfFormData="lfFormDataConfig" :lfFieldsData="lfFieldsConfig"
                                    :lfFieldPerm="lfFieldControlVOs" :isPreview="isPreview" :reSubmit="reSubmit">
                                </component>
                            </div>
                            <div v-else-if="isOutSideAccess == 'true'">
                                <p v-if="formData" v-html="formData"></p>
                            </div>
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
        <!-- 审批对话框 -->
        <el-dialog :title="approveDialogTitle" v-model="openApproveDialog" width="550px" append-to-body>
            <el-form :model="approveForm" :rules="rules" ref="approveFormRef" label-width="130px"
                style="margin: 0 20px;">
                <el-row>
                    <el-col :span="24">
                        <el-form-item label="备注/说明" prop="remark">
                            <el-input v-model="approveForm.remark" type="textarea" placeholder="请输入审批备注"
                                :maxlength="100" show-word-limit :autosize="{ minRows: 4, maxRows: 4 }"
                                :style="{ width: '100%' }"></el-input>
                        </el-form-item>
                    </el-col>
                </el-row>
            </el-form>
            <template #footer>
                <div class="dialog-footer">
                    <el-button type="primary" @click="approveSubmit(approveFormRef)">确 定</el-button>
                    <el-button @click="openApproveDialog = false">取 消</el-button>
                </div>
            </template>
        </el-dialog>
    </div>
</template>

<script setup>
import { ref, watch } from 'vue'
import { ElMessageBox } from 'element-plus'
import cache from '@/plugins/cache';
import FlowStepTable from "@/components/Workflow/Preview/flowStepTable.vue"
import ReviewWarp from "@/components/Workflow/Preview/reviewWarp.vue"
import employeesDialog from '@/components/Workflow/dialog/usersDialog.vue'
import { pageButtonsColor, approvalPageButtons, approvalButtonConf } from "@/utils/flow/const"
import { getViewBusinessProcess, processOperation } from "@/api/workflow"
import { loadDIYComponent, loadLFComponent } from '@/views/workflow/components/componentload.js'
const { proxy } = getCurrentInstance()
const route = useRoute();
const formCode = route.query?.formCode
const processNumber = route.query?.processNumber
const isOutSideAccess = route.query?.isOutSideAccess || false;
const isLowCodeFlow = route.query?.isLowCodeFlow || false;
const taskId = route.query?.taskId
const activeName = ref('baseTab')

let openApproveDialog = ref(false);
let approveDialogTitle = ref("审批");

let baseTabShow = ref(true);
let flowStepShow = ref(false);
let flowReviewShow = ref(false);

let componentData = ref(null);
let componentLoaded = ref(false);
let loadedComponent = ref(null);
let isPreview = ref(true);
let reSubmit = ref(false);
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
    taskId: taskId,
    processNumber: processNumber,
    formCode: formCode,
    isOutSideAccessProc: isOutSideAccess,
    outSideType: 2,
    isLowCodeFlow: isLowCodeFlow,
    lfFields: null, //低代码表单字段
});

onMounted(() => {
    approvalButtons.value = approvalPageButtons.filter((c) => {
        return c.type == 'default';
    });
});
watch(approvalButtons, (val) => {
    reSubmit.value = val.some(c => c.value == approvalButtonConf.resubmit);
    isPreview.value = !val.some(c => c.value == approvalButtonConf.resubmit);
})
watch(handleClickType, (val) => {
    dialogTitle.value = `设置${approvalButtonConf.buttonsObj[val]}人员`;
    isMultiple.value = val == approvalButtonConf.addApproval ? true : false;
})


/**点击页面审批操作按钮 */
const clickApproveSubmit = async (btnType) => {
    //console.log('btnType========',JSON.stringify(btnType))     
    handleClickType.value = btnType;
    switch (btnType) {
        case approvalButtonConf.addApproval:
        case approvalButtonConf.transfer:
            dialogTitle.value = `设置${approvalButtonConf.buttonsObj[btnType]}人员`;
            addUserDialog();
            break;
        case approvalButtonConf.agree:
        case approvalButtonConf.noAgree:
        case approvalButtonConf.resubmit:
        case approvalButtonConf.repulse:
            openApproveDialog.value = true;
            approveDialogTitle.value = approvalButtonConf.buttonsObj[btnType];
            break;
        case approvalButtonConf.undertake:
            approveUndertakeSubmit();
            break;
    }
}

/**
 * 审批操作确定
 * @param param 
 * @param type 
 */
const approveSubmit = async (param) => {
    if (!param) return;
    param.validate(async (valid) => {
        if (valid) {
            approveSubData.approvalComment = approveForm.remark;
            approveSubData.operationType = handleClickType.value; 
            if (handleClickType.value == approvalButtonConf.resubmit) {
                await componentFormRef.value.handleValidate().then(async (isValid) => {
                    if (isValid) {
                        await componentFormRef.value.getFromData().then((data) => { 
                            approveSubData.lfFields = JSON.parse(data); //低代码表单字段
                        })
                      
                    }
                });
            };
            console.log('approveSubData==========', JSON.stringify(approveSubData));
            await approveProcess(approveSubData);//业务处理
        }
    })
}
/**
 * 承办操作确定
 * @param param 
 * @param type 
 */
const approveUndertakeSubmit = async () => {
    approveSubData.approvalComment = "承办";
    approveSubData.operationType = handleClickType.value;
    ElMessageBox.confirm('确定完成操作吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
    }).then(async () => {
        let resData = await processOperation(approveSubData);
        if (resData.code == 200) {
            proxy.$modal.msgSuccess("承办成功");
            handleTabClick({ paneName: "baseTab" });
        } else {
            proxy.$modal.msgError("承办失败:" + resData.errMsg);
        }
    }).catch(() => { });
}
/**
 * 表单预览
 */
const preview = () => {
    let queryParams = ref({
        formCode: formCode,
        processNumber: processNumber,
        type: 2,
        isOutSideAccessProc: isOutSideAccess,
        isLowCodeFlow: isLowCodeFlow
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
            proxy.$modal.msgError("获取表单数据失败:" + response.errMsg);
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
            proxy.$modal.msgSuccess("审批成功");
            close();
        } else {
            proxy.$modal.msgError("审批失败:" + resData.errMsg);
        }
        proxy.$modal.closeLoading();
    }).catch(() => { });
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
    padding: 10px !important;
    max-width: 720px !important;
    left: 0 !important;
    right: 0 !important;
    /* margin: auto !important;*/
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