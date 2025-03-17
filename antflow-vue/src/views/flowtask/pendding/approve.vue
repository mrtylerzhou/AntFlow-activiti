<template>
    <div class="app-container">
        <div class="flex gap-2">
            <el-tag type="primary">{{ formCode }}</el-tag>
            <el-tag type="success" style="margin-left: 5px;">{{ processNumber }}</el-tag>
        </div>
        <el-tabs v-model="activeName" class="set-tabs">
            <el-tab-pane label="表单信息" name="baseTab">
                <div class="approve" v-if="activeName === 'baseTab'">
                    <el-row style="padding-left: -5px;padding-right: -5px;">
                        <el-col :span="24" class="my-col">
                            <div v-for="btn in approvalButtons" style="float: left;">
                                <el-button style="margin: 5px;" v-if="btn.label" :type="approveButtonColor[btn.value]"
                                    @click="clickApproveSubmit(btn.value)">
                                    {{ btn.label }}
                                </el-button>
                            </div>
                        </el-col>
                        <el-col :span="24" class="my-col" v-if="baseTabShow">
                            <div v-if="componentLoaded" class="component">
                                <component ref="componentFormRef" :is="loadedComponent" 
                                    :previewData="componentData"  
                                    :isPreview="isPreview" 
                                    :reSubmit="reSubmit"
                                    :lfFormData="lfFormDataConfig" 
                                    :lfFieldsData="lfFieldsConfig"
                                    :lfFieldPerm="lfFieldControlVOs">
                                </component>
                            </div>
                            <div v-else-if="isOutSideAccess == 'true'">
                                <outsideFormRender v-if="formData" :formData="formData"></outsideFormRender>
                            </div>
                        </el-col>
                    </el-row>
                </div>
            </el-tab-pane>
            <el-tab-pane label="审批记录" name="flowStep">
                <div v-if="activeName === 'flowStep'">
                    <FlowStepTable />
                </div>
            </el-tab-pane>
            <el-tab-pane label="流程预览" name="flowReview">
                <div v-if="activeName === 'flowReview'">
                    <ReviewWarp />
                </div>
            </el-tab-pane>
        </el-tabs>
        <transfer-dialog v-model:visible="dialogVisible" :isMultiple="isMultiple" :title="dialogTitle"
            @change="sureDialogBtn" />
        <repulse-dialog v-model:visible="repulseDialogVisible" @clickConfirm="approveSubmit" />
        <approve-dialog v-model:visible="openApproveDialog" :title="approveDialogTitle" @clickConfirm="approveSubmit" />
        <label class="page-close-box" @click="close()"><img src="@/assets/images/back-close.png"></label>
    </div>
</template>

<script setup>
import { ref, watch } from 'vue'; 
import cache from '@/plugins/cache';
import FlowStepTable from '@/components/Workflow/Preview/flowStepTable.vue';
import ReviewWarp from '@/components/Workflow/Preview/reviewWarp.vue';
import outsideFormRender from "@/views/workflow/components/outsideFormRender.vue";
import transferDialog from './components/transferDialog.vue';
import approveDialog from './components/approveDialog.vue';
import repulseDialog from './components/repulseDialog.vue';
import { approveButtonColor, approvalPageButtons, approvalButtonConf } from '@/utils/flow/const';
import { getViewBusinessProcess, processOperation } from '@/api/workflow';
import { loadDIYComponent, loadLFComponent } from '@/views/workflow/components/componentload.js';
const route = useRoute();
const { proxy } = getCurrentInstance();
import { useStore } from '@/store/modules/workflow';
let store = useStore();
let { setPreviewDrawerConfig } = store;
const formCode = route.query?.formCode;
const processNumber = route.query?.processNumber;
const isOutSideAccess = route.query?.isOutSideAccess || false;
const isLowCodeFlow = route.query?.isLowCodeFlow || false;
const taskId = route.query?.taskId;

const activeName = ref('baseTab');
let baseTabShow = ref(true);

let openApproveDialog = ref(false);
let approveDialogTitle = ref("审批");
let dialogVisible = ref(false);
let dialogTitle = ref('');
let repulseDialogVisible = ref(false);

let componentData = ref(null);
let componentLoaded = ref(false);
let loadedComponent = ref(null);
let isPreview = ref(true);
let reSubmit = ref(false);
let approvalButtons = ref([]);

let formData = ref(null);
const componentFormRef = ref(null);
const handleClickType = ref(null);

let isMultiple = ref(false);//false 转办，true 加批

let lfFormDataConfig = ref(null);
let lfFieldsConfig = ref(null);
let lfFieldControlVOs = ref(null);

let approveSubData = reactive({
    taskId: taskId,
    processNumber: processNumber,
    formCode: formCode,
    isOutSideAccessProc: isOutSideAccess,
    outSideType: 2,
    isLowCodeFlow: isLowCodeFlow,
    lfFields: null, //低代码表单字段
});

watch(handleClickType, (val) => {
    dialogTitle.value = `设置${approvalButtonConf.buttonsObj[val]}人员`;
    isMultiple.value = val == approvalButtonConf.addApproval ? true : false;
});
onMounted(async () => {
    setPreviewDrawerConfig({
        formCode: formCode,
        processNumber: processNumber,
        taskId: taskId,
        isOutSideAccess: isOutSideAccess,
        isLowCodeFlow: isLowCodeFlow,
    });
    approvalButtons.value = approvalPageButtons.filter((c) => {
        return c.type == 'default';
    });
    await preview();
});
/**
 * 点击页面审批操作按钮
 */
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
            openApproveDialog.value = true;
            approveDialogTitle.value = approvalButtonConf.buttonsObj[btnType];
            break;
        case approvalButtonConf.repulse:
            repulseDialogVisible.value = true;
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
    approveSubData.approvalComment = param.remark;
    approveSubData.operationType = handleClickType.value;
    if (handleClickType.value == approvalButtonConf.resubmit) {
        await componentFormRef.value.handleValidate().then(async (isValid) => {
            if (isValid) {
                await componentFormRef.value.getFromData().then((data) => {
                    if (isLowCodeFlow && isLowCodeFlow == 'true') {
                        approveSubData.lfFields = JSON.parse(data); //低代码表单字段
                    } else {
                        let componentFormData = JSON.parse(data);
                        Object.assign(approveSubData, componentFormData);
                    }
                });
            }
        });
    };
    if (handleClickType.value == approvalButtonConf.repulse) {//退回操作
        approveSubData.backToModifyType = Number(param.backToModifyType);
        approveSubData.backToNodeId = Number(param.backToNodeId);
    }
    //console.log('approveSubData==========', JSON.stringify(approveSubData));
    await approveProcess(approveSubData);//业务处理
}
/**
 * 承办操作确定
 * @param param 
 * @param type 
 */
const approveUndertakeSubmit = async () => {
    approveSubData.approvalComment = "承办";
    approveSubData.operationType = handleClickType.value;
    proxy.$modal.confirm('确定完成操作吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
    }).then(async () => {
        await processOperation(approveSubData).then((resData) => {
            if (resData.code == 200) {
                proxy.$modal.msgSuccess("承办成功");

            } else {
                proxy.$modal.msgError("承办失败:" + resData.errMsg);
            }

        }).then(() => {
            console.log('刷新当前页签 ==========');
            handleTabClick({ paneName: "baseTab" });
        });     
    });
}
/**
 * 表单预览
 */
const preview = async () => {
    let queryParams = ref({
        formCode: formCode,
        processNumber: processNumber,
        type: 2,
        isOutSideAccessProc: isOutSideAccess,
        isLowCodeFlow: isLowCodeFlow
    });
    proxy.$modal.loading();
    await getViewBusinessProcess(queryParams.value).then(async (response) => {
        if (response.code == 200) {
            //显示审批按钮
            let auditButtons = response.data.processRecordInfo?.pcButtons?.audit;
            if (Array.isArray(auditButtons) && auditButtons.length > 0) {
                approvalButtons.value = auditButtons.map(c => {
                    return { value: c.buttonType, label: c.name };
                }).sort(function (a, b) {
                    return a.value - b.value
                });
                approvalButtons.value = uniqueByMap(approvalButtons.value);
            }
            reSubmit.value = approvalButtons.value.some(c => c.value == approvalButtonConf.resubmit);
            isPreview.value = !approvalButtons.value.some(c => c.value == approvalButtonConf.resubmit);
            if (isOutSideAccess && isOutSideAccess == 'true') {//外部表单接入
                //formData.value = response.data.formData;
                lfFormDataConfig.value = response.data.lfFormData;
                lfFieldControlVOs.value = JSON.stringify(response.data.processRecordInfo.lfFieldControlVOs);
                lfFieldsConfig.value = JSON.stringify(response.data.lfFields);
                loadedComponent.value = await loadLFComponent();
                componentLoaded.value = true;
            }
            else if (isLowCodeFlow && isLowCodeFlow == 'true') {//低代码表单
                lfFormDataConfig.value = response.data.lfFormData;
                lfFieldControlVOs.value = JSON.stringify(response.data.processRecordInfo.lfFieldControlVOs);
                lfFieldsConfig.value = JSON.stringify(response.data.lfFields);
                loadedComponent.value = await loadLFComponent();
                componentLoaded.value = true;
            } else {//自定义表单
                componentData.value = response.data;
                loadedComponent.value = await loadDIYComponent(formCode); 
                componentLoaded.value = true;
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
            name: decodeURIComponent(cache.session.get('userName')),
        });
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
    proxy.$modal.confirm('确定完成操作吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
    }).then(async () => {
        dialogVisible.value = false;
        proxy.$modal.loading();
        await processOperation(param).then((res) => {
            if (res.code == 200) {
                proxy.$modal.msgSuccess("审批成功");
                close();
            } else {
                proxy.$modal.msgError("审批失败:" + res.errMsg);
            }
        }); 
        proxy.$modal.closeLoading();
    }).catch(() => { });
} 

const handleTabClick = async (tab, event) => {
    activeName.value = tab.paneName;
    if (tab.paneName == 'baseTab') { 
        preview(); 
    }
};
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
    background: #f5f5f7;
    position: relative;
    padding: 10px;
    box-sizing: border-box;
    height: calc(78vh) !important;
    overflow: auto;
}

.my-col {
    border: 1px solid #ebeef5;
    margin: 5px;
    background-color: #fff;
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