<template>
    <div class="app-container">
        <el-header>
            <div class="approval-btns" v-for="btn in approvalButtons">
                <el-button v-if="btn.label" :type="approveButtonColor[btn.value]"
                    @click="clickApproveSubmit(btn.value)"> {{ btn.label }}
                </el-button>
            </div>
        </el-header>
        <el-main>
            <el-scrollbar>
                <div v-if="componentLoaded" class="component">
                    <component ref="componentFormRef" :key="approveSubData.taskId" :is="loadedComponent"
                        :previewData="componentData" :isPreview="false" :lfFormData="lfFormDataConfig"
                        :lfFieldsData="lfFieldsConfig" :lfFieldPerm="lfFieldControlVOs">
                    </component>
                </div>
            </el-scrollbar>
        </el-main>
        <transfer-dialog v-model:visible="dialogVisible" :isMultiple="isMultiple" :title="dialogTitle"
            @change="sureDialogBtn" />
        <repulse-dialog v-model:visible="repulseDialogVisible" @clickConfirm="approveSubmit" />
        <approve-dialog v-model:visible="openApproveDialog" :title="approveDialogTitle" @clickConfirm="approveSubmit" />
        <label class="page-close-box" @click="close()"><img src="@/assets/images/antflow/back-close.png"></label>
    </div>
</template>

<script setup>
import { ref, watch } from 'vue';
import Cookies from "js-cookie";
import transferDialog from './transferDialog.vue';
import approveDialog from './approveDialog.vue';
import repulseDialog from './repulseDialog.vue';
import { approveButtonColor, approvalButtonConf } from '@/utils/antflow/const';
import { getViewBusinessProcess, processOperation } from '@/api/workflow/index';
import { loadDIYComponent, loadLFComponent } from '@/views/workflow/components/componentload.js';
import { boolToString } from '@/utils/antflow/ObjectUtils'
const { proxy } = getCurrentInstance();
import { useStore } from '@/store/modules/workflow';
let store = useStore();
let instanceViewConfig = computed(() => store.instanceViewConfig1)
const activeName = ref('baseTab');
let openApproveDialog = ref(false);
let repulseDialogVisible = ref(false);
let approveDialogTitle = ref("审批");
let dialogVisible = ref(false);
let dialogTitle = ref('');
let isMultiple = ref(false);//false 转办，true 加批
let approvalButtons = ref([]);
let loadedComponent = ref(null);
let componentData = ref(null);
let componentLoaded = ref(false);
let lfFormDataConfig = ref(null);
let lfFieldsConfig = ref(null);
let lfFieldControlVOs = ref(null);
const componentFormRef = ref(null);
const handleClickType = ref(null);
let approveSubData = ref(null);

let props = defineProps({
    approveFormData: {
        type: Object,
        required: true,
        default: () => { }
    }
});
const emits = defineEmits(["handleRefreshList"]);
watch(handleClickType, (val) => {
    dialogTitle.value = `设置${approvalButtonConf.buttonsObj[val]}人员`;
    isMultiple.value = val == approvalButtonConf.addApproval ? true : false;
}, { deep: true });

watch(() => instanceViewConfig.value, async (newVal) => {
    approveSubData.value = { ...instanceViewConfig.value };
    await preview(newVal);
}, { deep: true });

onMounted(async () => {
    approveSubData.value = { ...props.approveFormData };
    await preview(instanceViewConfig.value);
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
    approveSubData.value.approvalComment = param.remark;
    approveSubData.value.operationType = handleClickType.value;
    if (handleClickType.value == approvalButtonConf.resubmit || handleClickType.value == approvalButtonConf.agree) {
        await componentFormRef.value.handleValidate().then(async (isValid) => {
            if (isValid) {
                await componentFormRef.value.getFromData().then((data) => {
                    if (approveSubData.value.isLowCodeFlow == true) {
                        approveSubData.value.lfFields = JSON.parse(data); //低代码表单字段
                    } else {
                        let componentFormData = JSON.parse(data);
                        approveSubData.value = { ...approveSubData.value, ...componentFormData };
                    }
                });
            }
        });
    };
    if (handleClickType.value == approvalButtonConf.repulse) {//退回操作
        approveSubData.value.backToModifyType = Number(param.backToModifyType);
        approveSubData.value.backToNodeId = Number(param.backToNodeId);
    }
    await approveProcess(approveSubData.value);//业务处理
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
/**
 * 承办操作确定
 * @param param 
 * @param type 
 */
const approveUndertakeSubmit = async () => {
    approveSubData.value.approvalComment = "承办";
    approveSubData.value.operationType = handleClickType.value;
    proxy.$modal.confirm('确定完成操作吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
    }).then(async () => {
        await processOperation(approveSubData.value).then((resData) => {
            if (resData.code == 200) {
                proxy.$modal.msgSuccess("承办成功");

            } else {
                proxy.$modal.msgError("承办失败:" + resData.errMsg);
            }
        }).then(async () => {
            await preview(approveSubData.value);
            activeName.value = "baseTab";
        });
    });
}
/**
 * 表单预览
 */
const preview = async (viewData) => {
    let queryParams = {
        ...viewData,
        type: 2
    };
    proxy.$modal.loading();
    await getViewBusinessProcess(queryParams).then(async (response) => {
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
            try {
                if (viewData.isLowCodeFlow == true) {//低代码表单 和 外部表单接入
                    lfFormDataConfig.value = response.data.lfFormData;
                    lfFieldControlVOs.value = JSON.stringify(response.data.processRecordInfo.lfFieldControlVOs);
                    lfFieldsConfig.value = JSON.stringify(response.data.lfFields);
                    loadedComponent.value = await loadLFComponent();
                    componentLoaded.value = true;
                } else {//自定义表单
                    componentData.value = response.data;
                    loadedComponent.value = await loadDIYComponent(viewData.formCode);
                    componentLoaded.value = true;
                }
            } catch (error) {
                close();
            }
        } else {
            proxy.$modal.msgError("获取表单数据失败:" + response.errMsg);
            close();
        }
        proxy.$modal.closeLoading();
    });
}

/**
 * 确定Dialog 弹框
 */
const sureDialogBtn = async (data) => {
    approveSubData.value.operationType = handleClickType.value;
    approveSubData.value.approvalComment = data.remark;
    if (!isMultiple.value) {
        data.selectList.unshift({
            id: Cookies.get('userId'),
            name: decodeURIComponent(Cookies.get('userName')),
        });
        approveSubData.value.userInfos = data.selectList.filter(item => item.id);
    } else {
        approveSubData.value.signUpUsers = data.selectList.filter(item => item.id);
    }
    //console.log('sureDialogBtn==========approveSubData=============', JSON.stringify(approveSubData));  
    await approveProcess(approveSubData.value);
}
/**
 * 选人员Dialog 弹框
 */
const addUserDialog = () => {
    dialogVisible.value = true;
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
const close = async () => {
    activeName.value = "baseTab";
    emits("handleRefreshList");
    // const obj = { path: "/flowTask/pendding" };
    // proxy.$tab.closeOpenPage(obj);
}

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

.approval-btns {
    float: left;
    margin: 16px 5px;
}

.app-container .el-header {
    box-shadow: var(--el-box-shadow-light);
    background-color: #f2f3f4f5;
}

.app-container .el-main {
    background-color: #fff;
    color: var(--el-text-color-primary);
    border-radius: 5px;
    height: 75vh;
}

.app-container .toolbar {
    display: inline-flex;
    align-items: center;
    justify-content: center;
    height: 100%;
    right: 20px;
}

.approve {
    width: 100%;
    position: relative;
    padding: 0px 10px 10px 10px;
    box-sizing: border-box;
    height: calc(78vh) !important;
    overflow: auto;
}

.my-col {
    border: 1px solid #ebeef5;
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