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
                    <component ref="componentFormRef" :is="loadedComponent" :previewData="componentData"
                        :isPreview="false" :lfFormData="lfFormDataConfig" :lfFieldsData="lfFieldsConfig"
                        :lfFieldPerm="lfFieldControlVOs">
                    </component>
                </div>
            </el-scrollbar>
        </el-main>
        <transfer-dialog v-model:visible="dialogVisible" :isMultiple="isMultiple" :title="dialogTitle"
            @change="sureDialogBtn" />
        <repulse-dialog v-model:visible="repulseDialogVisible" @clickConfirm="approveSubmit" />
        <approve-dialog v-model:visible="openApproveDialog" :title="approveDialogTitle" @clickConfirm="approveSubmit" />
        <label class="page-close-box" @click="close()"><img src="@/assets/images/back-close.png"></label>
    </div>
</template>

<script setup>
import { onBeforeMount, ref, watch } from 'vue';
import cache from '@/plugins/cache';
import transferDialog from './transferDialog.vue';
import approveDialog from './approveDialog.vue';
import repulseDialog from './repulseDialog.vue';
import { approveButtonColor, approvalPageButtons, approvalButtonConf } from '@/utils/antflow/const';
import { getViewBusinessProcess, processOperation } from '@/api/workflow/index';
import { loadDIYComponent, loadLFComponent } from '@/views/workflow/components/componentload.js';
const { proxy } = getCurrentInstance();
import { useStore } from '@/store/modules/workflow';
let store = useStore();
let { setFormRenderConfig, instanceViewConfig1 } = store;
let formRenderConfig = computed(() => store.formRenderConfig)
let instanceViewConfig = computed(() => store.instanceViewConfig1)

let props = defineProps({
    formData: {
        required: true,
        type: Object,
        default: null,
    }
});

const formCode = props.formData?.formCode;

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

const approveSubData = reactive({
    taskId: props.formData?.taskId,
    processNumber: props.formData?.processNumber,
    formCode: props.formData?.formCode,
    isOutSideAccessProc: props.formData?.isOutSideAccess,
    outSideType: 2,
    isLowCodeFlow: props.formData?.isLowCodeFlow,
    lfFields: null, //低代码表单字段
});

onBeforeMount(async () => {
    await preview();
})

watch(handleClickType, (val) => {
    dialogTitle.value = `设置${approvalButtonConf.buttonsObj[val]}人员`;
    isMultiple.value = val == approvalButtonConf.addApproval ? true : false;
});
watchEffect(() => props.formData, async (val) => {
    console.log('formData========val=======', JSON.stringify(val));
    // setFormRenderConfig({
    //     formCode: formCode
    // });
    // await preview();
});

onMounted(async () => {
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
    if (handleClickType.value == approvalButtonConf.resubmit || handleClickType.value == approvalButtonConf.agree) {
        await componentFormRef.value.handleValidate().then(async (isValid) => {
            if (isValid) {
                await componentFormRef.value.getFromData().then((data) => {
                    if (approveSubData.isLowCodeFlow == true) {
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
            handleTabClick({ paneName: "baseTab" });
        });
    });
}
/**
 * 表单预览
 */
const preview = async () => {
    let queryParams = {
        formCode: approveSubData.formCode,
        processNumber: approveSubData.processNumber,
        type: 2,
        isOutSideAccessProc: approveSubData.isOutSideAccessProc,
        isLowCodeFlow: approveSubData.isLowCodeFlow
    };

    console.log('instanceViewConfig======watch=========', JSON.stringify(instanceViewConfig.value));

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
            if (approveSubData.isLowCodeFlow == true) {//低代码表单 和 外部表单接入
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
    const obj = { path: "/flowTask/pendding" };
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