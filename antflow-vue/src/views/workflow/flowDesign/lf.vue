<template>
    <div class="lf-container">
        <div class="fd-nav">
            <div class="fd-nav-left">
                <div class="fd-nav-title">
                    <el-icon>
                        <HomeFilled />
                    </el-icon>
                    {{ title }}
                </div>
            </div>
            <div class="fd-nav-center">
            <div class="step-tab">
                <div v-for="(item, index) in steps" :key="index" class="step"
                    :class="[activeStep == item.key ? 'active' : '', item.disabled ? 'disabled' : '']"
                    @click="changeSteps(item)">
                    <span class="step-index">{{ index + 1 }}</span>
                    {{ item.label }}
                </div>
            </div>
        </div>
        <div class="fd-nav-right">
            <button type="button" class="fd-btn button-publish" @click="previewJson">
                <span>预览json</span>
            </button>
            <button v-if="!isDopeSheetMode" type="button" class="fd-btn button-publish" @click="publish">
                <span>发 布</span>
            </button>
            <button v-else type="button" class="fd-btn button-publish" @click="handleBackToDopeSheet">
                <span>返 回</span>
            </button>
        </div>
    </div>
    <div v-if="processConfig" v-show="activeStep === 'basicSetting'">
        <BasicSetting ref="basicSetting" :basicData="processConfig" @nextChange="changeSteps"
            @externalFormChange="onExternalFormChange" :flowType="'LF'" />
    </div>
    <div v-show="activeStep === 'formDesign'" aria-hidden="true">
        <DynamicForm v-if="!useExternalForm" ref="formDesign" :lfFormData="lfFormDataConfig" />
        <div v-else class="ext-form-placeholder">
            <el-alert title="外部表单模式" type="info" :closable="false" show-icon>
                <template #default>
                    当前流程启用了外部表单,表单设计步骤已禁用。请到【系统管理 - 表单管理】维护表单定义。
                </template>
            </el-alert>
        </div>
    </div>
        <div v-if="nodeConfig" v-show="activeStep === 'processDesign'">
            <Process ref="processDesign" :processData="nodeConfig" @nextChange="changeSteps" />
        </div>
        <div v-if="processConfig" v-show="activeStep === 'advancedSetting'">
            <AdvancedSetting ref="advancedSetting" :basicData="processConfig" @nextChange="changeSteps" />
        </div>
        <jsonDialog v-model:visible="viewJson" :title="jsonTitle" :modelValue="nodeConfig" />
    </div>
</template>

<script setup>
import { ref, onMounted, computed } from "vue";
import { useRoute, useRouter } from 'vue-router';
import { getApiWorkFlowData, setApiWorkFlowData } from '@/api/workflow/index';
import { FormatCommitUtils } from '@/utils/antflow/formatcommit_data';
import { FormatDisplayUtils } from '@/utils/antflow/formatdisplay_data';
import { NodeUtils } from '@/utils/antflow/nodeUtils';
import { useStore } from '@/store/modules/workflow';
import { useDopeSheetStore } from '@/store/modules/dopeSheet';
import { extractFormFieldsMulti } from '@/utils/antflow/formFieldExtractor';
import BasicSetting from "@/components/Workflow/basicSetting/index.vue";
import AdvancedSetting from "@/components/Workflow/AdvancedSetting/index.vue";
import Process from "@/components/Workflow/Process/index.vue";
import DynamicForm from "@/components/Workflow/DynamicForm/index.vue";
import jsonDialog from "@/components/Workflow/dialog/jsonDialog.vue";
const { proxy } = getCurrentInstance()
const route = useRoute();
const router = useRouter();
const store = useStore();
const dopeSheetStore = useDopeSheetStore();
// Dope Sheet 模式：从 store 读数据，发布替换为返回
const isDopeSheetMode = computed(() => route.query.mode === 'store' && dopeSheetStore.fullEditMode);
const basicSetting = ref(null);
const advancedSetting = ref(null);
const processDesign = ref(null);
const formDesign = ref(null);
let activeStep = ref("basicSetting"); // 激活的步骤面板
// 外部表单模式标记: BpmnConfFlagsEnum.USE_EXTERNAL_FORM = 0b1000000 = 64
const USE_EXTERNAL_FORM_FLAG = 64;
let useExternalForm = ref(false);
let steps = ref([
    { label: "基础设置", key: "basicSetting" },
    { label: "表单设计", key: "formDesign" },
    { label: "流程设计", key: "processDesign" },
    { label: "高级设置", key: "advancedSetting" },
]);
const changeSteps = (item) => {
    //外部表单模式下,表单设计步骤被禁用
    if (item.key === 'formDesign' && useExternalForm.value) {
        return;
    }
    activeStep.value = item.key;
};
let processConfig = ref(null);
let nodeConfig = ref(null);
let lfFormDataConfig = ref(null);
let title = ref('');

let viewJson = ref(false);
let jsonTitle = ref('');

onMounted(async () => {
    let mockjson = {};
    proxy.$modal.loading();
    // Dope Sheet 模式：从 store 读取数据
    if (route.query.mode === 'store' && dopeSheetStore.processConfig) {
        let data = JSON.parse(JSON.stringify(dopeSheetStore.processConfig));
        proxy.$modal.closeLoading();
        processConfig.value = data;
        title.value = data?.bpmnName || dopeSheetStore.formCodeName || '';
        nodeConfig.value = data?.nodeConfig;
        lfFormDataConfig.value = data?.lfFormData;
        const flags = Number(data?.extraFlags || 0);
        useExternalForm.value = (flags & USE_EXTERNAL_FORM_FLAG) === USE_EXTERNAL_FORM_FLAG;
        updateStepsDisabled();
        syncExternalFormToStore();
        return;
    }
    if (route.query.id) {
        mockjson = await getApiWorkFlowData({ id: route.query.id });
        if (!mockjson.code || mockjson.code != 200) {
            proxy.$modal.closeLoading();
            proxy.$modal.msgError("获取API数据失败" + JSON.stringify(mockjson.errMsg));
            return;
        }
    } else {
        mockjson = NodeUtils.createStartNode();
    }
    let data = FormatDisplayUtils.getToTree(mockjson.data);
    proxy.$modal.closeLoading();
    processConfig.value = data;
    title.value = proxy.isEmpty(data?.bpmnName) ? decodeURIComponent(route.query.fcname ?? '') : data?.bpmnName;
    nodeConfig.value = data?.nodeConfig;
    lfFormDataConfig.value = data?.lfFormData;
    //根据 extraFlags 判定是否启用外部表单模式
    const flags = Number(data?.extraFlags || 0);
    useExternalForm.value = (flags & USE_EXTERNAL_FORM_FLAG) === USE_EXTERNAL_FORM_FLAG;
    updateStepsDisabled();
    //同步到 store,供节点配置(条件/取人/表单权限)读取
    syncExternalFormToStore();
});

/**根据 useExternalForm 同步 steps 的 disabled 状态 */
const updateStepsDisabled = () => {
    steps.value.forEach(s => {
        if (s.key === 'formDesign') {
            s.disabled = !!useExternalForm.value;
        }
    });
}

/**BasicSetting 中外部表单勾选/选择变化时调用 */
const onExternalFormChange = (payload) => {
    useExternalForm.value = !!payload?.useExternalForm;
    updateStepsDisabled();
    syncExternalFormToStore(payload?.lfFormdataList);
}

/**
 * 将外部表单模式相关状态同步到 store
 * @param {Array} formdataListOverride - BasicSetting 传来的选中表单列表(新流程场景);
 *        未传则回退到后端返回的 processConfig.lfFormdataList(编辑已有流程场景)
 */
const syncExternalFormToStore = (formdataListOverride) => {
    store.setUseExternalForm(useExternalForm.value);
    if (useExternalForm.value) {
        const formdataList = formdataListOverride ?? (processConfig.value?.lfFormdataList || []);
        store.setLfFormdataList(formdataList);
        store.setLowCodeFormFieldsMulti(extractFormFieldsMulti(formdataList));
    } else {
        store.setLfFormdataList([]);
        store.setLowCodeFormFieldsMulti([]);
    }
}


const publish = () => {
    const step1 = basicSetting.value.getData();
    //外部表单模式下,跳过表单设计步骤
    const step2 = useExternalForm.value
        ? Promise.resolve({ formData: { widgetList: [], formConfig: {} } })
        : formDesign.value.getData();
    const step3 = processDesign.value.getData();
    const step4 = advancedSetting.value.getData();
    proxy.$modal.loading();
    Promise.all([step1, step2, step3, step4])
        .then((res) => {
            let basicData = res[0].formData;
            //同步外部表单模式标记到 lf.vue (用户在 basicSetting 中可能切换了勾选)
            useExternalForm.value = !!basicData.useExternalForm;
            basicData.isLowCodeFlow = 1; // 1代表低代码表单
            let lowcodeformData = res[1].formData;
            Object.assign(basicData, { lfFormData: JSON.stringify(lowcodeformData) });
            var nodes = FormatCommitUtils.formatSettings(res[2].formData);
            Object.assign(basicData, { nodes: nodes });
            // 高级设置覆盖 deduplicationType 和 viewPageButtons
            Object.assign(basicData, res[3].formData);
            // page-added DIY: 路由带 auxForm 时, 在 extraFlags 置 USE_AUXILIARY_FORM 位(0b10000000=128),
            // 标记"LF 后端 + 自定义 Vue 渲染"。运行期前端按 bizFormMaps.has 渲染自定义组件,后端按 is_lowcode_flow=1 走 LowFlowApprovalService。
            if (route.query.auxForm === '1') {
                basicData.extraFlags = Number(basicData.extraFlags || 0) | 128;
            }
            return basicData;
        })
        .then((data) => {
            setApiWorkFlowData(data).then((resLog) => {
                proxy.$modal.closeLoading();
                if (resLog.code == 200) {
                    proxy.$modal.msgSuccess("设置成功,F12控制台查看数据");
                    let obj = { path: "flow-version", query: { formCode: data.formCode } };
                    proxy.$tab.closeOpenPage(obj);
                } else {
                    proxy.$modal.msgError("提交到API返回失败" + JSON.stringify(resLog.errMsg));
                }
            })
        })
        .catch((err) => {
            proxy.$modal.closeLoading();
            if (err) {
                console.log("设置失败" + JSON.stringify(err));
            }
        });
};
const previewJson = () => {
    viewJson.value = true;
    jsonTitle.value = "预览JSON";
}

/** Dope Sheet 模式：返回按钮，将当前数据同步回 store 并跳转回 Dope Sheet */
const handleBackToDopeSheet = () => {
    const step1 = basicSetting.value.getData();
    const step2 = useExternalForm.value
        ? Promise.resolve({ formData: { widgetList: [], formConfig: {} } })
        : formDesign.value.getData();
    const step3 = processDesign.value.getData();
    const step4 = advancedSetting.value.getData();
    Promise.all([step1, step2, step3, step4]).then((res) => {
        let basicData = res[0].formData;
        basicData.isLowCodeFlow = 1;
        let lowcodeformData = res[1].formData;
        Object.assign(basicData, { lfFormData: JSON.stringify(lowcodeformData) });
        // 保留 nodeConfig 树结构（不序列化）供 Dope Sheet 使用
        basicData.nodeConfig = res[2].formData;
        Object.assign(basicData, res[3].formData);
        // 保留额外字段
        basicData.formCode = processConfig.value.formCode;
        basicData.bpmnCode = processConfig.value.bpmnCode;
        basicData.extraFlags = processConfig.value.extraFlags;
        basicData.lfFormDataId = processConfig.value.lfFormDataId;
        basicData.lfFormdataIds = processConfig.value.lfFormdataIds;
        basicData.lfFormdataList = processConfig.value.lfFormdataList;
        basicData.templateVos = processConfig.value.templateVos;
        dopeSheetStore.setProcessConfig(basicData);
        dopeSheetStore.markDirty();
        dopeSheetStore.exitFullEdit();
        // 跳转回 Dope Sheet
        router.push({ path: '/workflow/dopeSheet', query: { formCode: dopeSheetStore.formCode } });
    });
}
</script>
<style scoped lang="scss">
@use "@/assets/styles/antflow/workflow.scss";

.lf-container {
    position: relative;
    background-color: #f5f5f7;
    min-height: calc(100vh - 85px);
    padding-top: 15px;
    overflow: auto;
}

.step-tab {
    display: flex;
    justify-content: center;
    position: relative;
    height: 60px;
    font-size: 14px;
    border-right: 0px solid #1583f2;
    text-align: center;
    cursor: pointer
}

.fd-nav .step {
    width: 140px;
    line-height: 100%;
    padding-left: 30px;
    padding-right: 30px;
    line-height: 60px;
    cursor: pointer;
    position: relative;
}

.fd-nav .step:hover {
    background: #5af
}

.fd-nav .step:active {
    background: #1583f2
}

.fd-nav .step.disabled {
    color: #c0c4cc !important;
    cursor: not-allowed !important;
    opacity: 0.6;
}

.fd-nav .step.disabled:hover {
    background: transparent !important;
}

.fd-nav .active {
    background: #5af;
}

.fd-nav .step-index {
    display: inline-block;
    width: 18px;
    height: 18px;
    border: 1px solid #fff;
    border-radius: 8px;
    line-height: 18px;
    text-align: center;
    box-sizing: border-box;
}

.ext-form-placeholder {
    background: white;
    padding: 40px;
    max-width: 750px;
    margin: 20px auto;
    border-radius: 4px;
}
</style>