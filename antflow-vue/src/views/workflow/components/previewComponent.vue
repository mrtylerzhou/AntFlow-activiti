<template>
    <div v-if="visible" class="drawer-div" :style="{ height: viewHeight }">
        <div v-if="loadFaild">
            <p style="font-size: small;color: red;text-align: center;margin: 0 10%;">
                {{ tips }}
            </p>
        </div>
        <el-scrollbar>
            <el-container>
                <el-header class="el-header">
                    <span v-for="btn in initiatorPermBtns" style="margin-right: 5px;">
                        <el-button v-if="btn.show == 1" :type="buttonColor[btn.buttonType]" @click="clickButten(btn)">
                            {{ btn.name }} </el-button>
                    </span>
                    <span style="margin-right: 5px;">
                        <el-button type="primary" @click="openInNewPage">打印预览</el-button>
                    </span>
                </el-header>
                <el-main>
                    <div v-if="componentLoaded" class="component">
                        <component ref="formCompRef" :is="loadedComponent" :previewData="componentData" :lfFormData="lfFormDataConfig"
                            :lfFieldsData="lfFieldsConfig" :lfFieldPerm="lfFieldControlVOs" :isPreview="isPreview"
                            :lfFormdataList="lfFormdataListConfig" :lfFieldsMulti="lfFieldsMultiConfig"
                            :formHidden="formHiddenConfig" :ignoreReadonly="ignoreReadonly">
                        </component>
                    </div>
                </el-main>
            </el-container>
        </el-scrollbar>
        <selectUserDialog v-model:visible="forwardDialogVisible" :data="[]" @change="handleForwardUserSelected" />
    </div>
</template>
<script setup>
import { ref, computed } from 'vue';
import { useWindowSize } from '@vueuse/core'
import { getViewBusinessProcess, processOperation } from "@/api/workflow/index";
import { useStore } from '@/store/modules/workflow';
import { loadDIYComponent, loadLFComponent, loadLFMultiFormComponent } from '@/views/workflow/components/componentload.js';
import { isTrue } from '@/utils/antflow/ObjectUtils';
import { useRouter } from 'vue-router'; // 新增
import selectUserDialog from "@/components/Workflow/dialog/selectUserDialog.vue";
const { proxy } = getCurrentInstance();
const { width, height } = useWindowSize()
let store = useStore()
let viewConfig = computed(() => store.instanceViewConfig1)
let props = defineProps({
    isPreview: {
        type: Boolean,
        default: false,
    },
    ignoreReadonly: {
        type: Boolean,
        default: false,
    }
});
let viewHeight = computed(() => {
    return height.value - 170 + 'px';
})
const buttonColor = {
    0: "info",
    1: "primary", //
    2: "warning", //承办
    3: "success", //同意
    4: "danger", //拒绝
    5: "danger", //
    6: "danger", //
    7: "danger", //
    8: "primary", //
    15: "warning", //转发
    18: "warning", //退回
    19: "success", //加批
    21: "primary", //转办
    29: "warning", //驳回
    99: "success", //处理中
    100: "info",
};
let loadFaild = ref(false);
let componentData = ref(null);
let componentLoaded = ref(false);
let loadedComponent = ref(null);
let lfFormDataConfig = ref(null);
let lfFieldsConfig = ref(null);
let lfFieldControlVOs = ref(null);
let lfFormdataListConfig = ref([]);
let lfFieldsMultiConfig = ref({});
let formHiddenConfig = ref({});
const formCompRef = ref(null);
let isMultiForm = ref(false);


let initiatorPermBtns = ref([]);//发起人权限按钮

let forwardDialogVisible = ref(false); //转发选人对话框

let tips = "*未获取到外部表单信息，请联系管理员。";
let visible = computed({
    get() {
        return true;
    }
})

/** 撤回/作废/转发 */
async function clickButten(row) {
    // 转发按钮需要先选人
    if (row.buttonType === 15) {
        forwardDialogVisible.value = true;
        return;
    }
    let pramForm = {
        operationType: row.buttonType,
        formCode: viewConfig.value.formCode,
        processNumber: viewConfig.value.processNumber,
        isLowCodeFlow: viewConfig.value.isLowCodeFlow
    };
    const alterMsg = {
        7: row.name + "操作成功，关闭抽屉，刷新【我的发起】列表",
        29: row.name + "操作成功，请到【我的代办】页查看",
        32: "撤销同意操作成功，请重新审批",
    };
    proxy.$confirm('确认"' + row.name + '"编号为"' + viewConfig.value.processNumber + '"的流程吗？', "温馨提示").then(async () => {
        proxy.$modal.loading();
        await processOperation(pramForm).then((res) => {
            if (res.code == 200) {
                proxy.$modal.msgSuccess(alterMsg[row.buttonType]);
            } else {
                proxy.$modal.msgError(row.name + "操作失败:" + res.errMsg);
            }
        });
        proxy.$modal.closeLoading();
    }).catch(() => { })
}

/** 转发选人确认回调 */
const handleForwardUserSelected = async (selectedUsers) => {
    if (!selectedUsers || selectedUsers.length === 0) {
        proxy.$modal.msgWarning("请至少选择一个转发用户");
        return;
    }
    let pramForm = {
        operationType: 15,
        formCode: viewConfig.value.formCode,
        processNumber: viewConfig.value.processNumber,
        isLowCodeFlow: viewConfig.value.isLowCodeFlow,
        userInfos: selectedUsers.map(u => ({
            id: u.targetId,
            name: u.name
        }))
    };
    proxy.$modal.loading();
    await processOperation(pramForm).then((res) => {
        if (res.code == 200) {
            proxy.$modal.msgSuccess("转发操作成功");
        } else {
            proxy.$modal.msgError("转发操作失败:" + res.errMsg);
        }
    });
    proxy.$modal.closeLoading();
}
/**预览 */
const preview = async (param) => {
    let queryParams = ref({
        formCode: param.formCode,
        processNumber: param.processNumber,
        type: 2,
        isOutSideAccessProc: param.isOutSideAccess || false,
        isLowCodeFlow: param.isLowCodeFlow || false
    });
    proxy.$modal.loading();
    await getViewBusinessProcess(queryParams.value).then(async (response) => {
        if (response.code == 200) {
            const responseData = response.data;
            componentLoaded.value = true
            initiatorPermBtns.value = responseData.processRecordInfo?.pcButtons?.toView || [];
            if (isTrue(responseData.isLowCodeFlow)) {//低代码表单 和 三方接入
                const formdataList = responseData.lfFormdataList;
                const isExternal = Array.isArray(formdataList) && formdataList.length > 0;
                if (isExternal) {
                    // 外部表单模式: 多 tab 渲染
                    isMultiForm.value = true;
                    lfFormdataListConfig.value = formdataList;
                    lfFieldsMultiConfig.value = responseData.lfFieldsMulti || {};
                    lfFieldControlVOs.value = JSON.stringify(responseData.processRecordInfo?.lfFieldControlVOs || []);
                    formHiddenConfig.value = responseData.processRecordInfo?.formHidden || {};
                    lfFormDataConfig.value = null;
                    lfFieldsConfig.value = '{}';
                    loadedComponent.value = await loadLFMultiFormComponent();
                } else {
                    // 内联表单模式: 兼容旧逻辑
                    isMultiForm.value = false;
                    lfFormDataConfig.value = responseData.lfFormData;
                    lfFieldsConfig.value = JSON.stringify(responseData.lfFields);
                    lfFieldControlVOs.value = JSON.stringify(responseData.processRecordInfo.lfFieldControlVOs);
                    lfFormdataListConfig.value = [];
                    lfFieldsMultiConfig.value = {};
                    formHiddenConfig.value = {};
                    loadedComponent.value = await loadLFComponent(param.formCode);
                }
            }
            else {//自定义开发表单
                isMultiForm.value = false;
                loadedComponent.value = await loadDIYComponent(param.formCode).catch((err) => { proxy.$modal.msgError(err); });
                componentData.value = responseData;
            }
        } else {
            loadFaild.value = true
        }
        proxy.$modal.closeLoading();
    });
}
preview(viewConfig.value);

const router = useRouter(); // 新增
/** 新窗口打开当前预览 */
const openInNewPage = () => {
    if (!viewConfig.value) return;
    const query = {
        formCode: viewConfig.value.formCode,
        processNumber: viewConfig.value.processNumber,
        isLowCodeFlow: viewConfig.value.isLowCodeFlow
    };
    const routeData = router.resolve({ path: '/flowDevOps/flowPrint', query });
    window.open(routeData.href, '_blank');
};
/** 读取当前表单数据(供流程监控查看页做发起页式流程预览) */
const getFromData = () => {
    if (formCompRef.value && typeof formCompRef.value.getFromData === 'function') {
        return formCompRef.value.getFromData();
    }
    return Promise.reject('');
};
defineExpose({ getFromData, isMultiForm });

</script>
<style lang="scss" scoped>
.component {
    left: 0 !important;
    right: 0 !important;
    max-width: 720px !important;
    min-height: 520px !important;
    margin: auto !important;
    background: white !important;
}

.drawer-div {
    border: 1px solid #eee;
    border-radius: 4px;
    background: #fff;
}

.el-header {
    background-color: #eee;
    display: flex;
    align-items: center;
}
</style>