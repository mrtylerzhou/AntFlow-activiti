<template>
    <div class="diy-container">
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
                        :class="[activeStep == item.key ? 'active' : '', item.disabled ? 'disabled' : '']" @click="changeSteps(item)">
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
            <BasicSetting ref="basicSetting" :basicData="processConfig" @nextChange="changeSteps" :flowType="'DIY'" @auxiliaryFormChange="onAuxiliaryFormChange" />
        </div>
        <div v-if="processConfig && useAuxiliaryForm" v-show="activeStep === 'formDesign'">
            <DynamicForm ref="formDesign" :lfFormData="lfFormDataConfig" />
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
import BasicSetting from "@/components/Workflow/basicSetting/index.vue";
import AdvancedSetting from "@/components/Workflow/AdvancedSetting/index.vue";
import Process from "@/components/Workflow/Process/index.vue";
import DynamicForm from "@/components/Workflow/DynamicForm/index.vue";
import jsonDialog from "@/components/Workflow/dialog/jsonDialog.vue";
import { useStore } from '@/store/modules/workflow';
import { useDopeSheetStore } from '@/store/modules/dopeSheet';
//import { getWorkFlowData } from '@/api/workflow/mock.js';
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
// 辅助表单模式标记(DIY专用): BpmnConfFlagsEnum.USE_AUXILIARY_FORM = 0b10000000 = 128
const USE_AUXILIARY_FORM_FLAG = 128;
let useAuxiliaryForm = ref(false);
let lfFormDataConfig = ref(null);

let steps = ref([
    { label: "基础设置", key: "basicSetting" },
    { label: "表单设计", key: "formDesign", disabled: true },
    { label: "流程设计", key: "processDesign" },
    { label: "高级设置", key: "advancedSetting" },
]);

const changeSteps = (item) => {
    //辅助表单未启用时,表单设计步骤禁用,点击给出引导提示
    if (item.key === 'formDesign' && !useAuxiliaryForm.value) {
        proxy.$modal.msgWarning('请先在【基础设置】中勾选"使用辅助表单"后再进行表单设计。');
        return;
    }
    activeStep.value = item.key;
};

/**根据 useAuxiliaryForm 同步 steps 的 disabled 状态 */
const updateStepsDisabled = () => {
    steps.value.forEach(s => {
        if (s.key === 'formDesign') {
            s.disabled = !useAuxiliaryForm.value;
        }
    });
}

/**BasicSetting 中辅助表单勾选变化时调用 */
const onAuxiliaryFormChange = (payload) => {
    useAuxiliaryForm.value = !!payload?.useAuxiliaryForm;
    updateStepsDisabled();
    store.setUseAuxiliaryForm(useAuxiliaryForm.value);
    //取消勾选时清空 store 中的辅助表单字段,避免条件/取人残留旧字段
    if (!useAuxiliaryForm.value) {
        store.setLowCodeFormField({});
    }
}

let processConfig = ref(null);
let nodeConfig = ref(null);
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
        useAuxiliaryForm.value = (flags & USE_AUXILIARY_FORM_FLAG) === USE_AUXILIARY_FORM_FLAG;
        updateStepsDisabled();
        store.setUseAuxiliaryForm(useAuxiliaryForm.value);
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
    //回显辅助表单模式
    lfFormDataConfig.value = data?.lfFormData;
    const flags = Number(data?.extraFlags || 0);
    useAuxiliaryForm.value = (flags & USE_AUXILIARY_FORM_FLAG) === USE_AUXILIARY_FORM_FLAG;
    updateStepsDisabled();
    store.setUseAuxiliaryForm(useAuxiliaryForm.value);
});


const publish = () => {
    const step1 = basicSetting.value.getData();
    //辅助表单未启用时,跳过表单设计步骤
    const step2 = useAuxiliaryForm.value
        ? formDesign.value.getData()
        : Promise.resolve({ formData: { widgetList: [], formConfig: {} } });
    const step3 = processDesign.value.getData();
    const step4 = advancedSetting.value.getData();
    proxy.$modal.loading();
    Promise.all([step1, step2, step3, step4])
        .then((res) => {
            let basicData = res[0].formData;
            //同步辅助表单模式标记(用户在 basicSetting 中可能切换了勾选)
            useAuxiliaryForm.value = !!basicData.useAuxiliaryForm;
            //辅助表单设计数据;未启用时提交空串,后端据此清空旧辅助表单
            const lfFormData = useAuxiliaryForm.value ? JSON.stringify(res[1].formData) : '';
            Object.assign(basicData, { lfFormData: lfFormData });
            var nodes;
            try {
                // formatSettings 内部已并入 validateDrawBackPrev 校验, 失败时 throw
                nodes = FormatCommitUtils.formatSettings(res[2].formData);
            } catch (e) {
                proxy.$modal.closeLoading();
                proxy.$modal.msgError(e.message);
                return Promise.reject();
            }
            Object.assign(basicData, { nodes: nodes });
            // 高级设置覆盖 deduplicationType 和 viewPageButtons;
            // extraFlags 与 basicData 已设位做 OR 合并(高级设置只控制自己的位),避免覆盖 BasicSetting 的位
            const advancedFormData = res[3].formData;
            if (advancedFormData.extraFlags !== undefined && advancedFormData.extraFlags !== null) {
                basicData.extraFlags = Number(basicData.extraFlags || 0) | Number(advancedFormData.extraFlags || 0);
                delete advancedFormData.extraFlags;
            }
            Object.assign(basicData, advancedFormData);
            console.log("New===Json==========", JSON.stringify(basicData));
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
    const step2 = useAuxiliaryForm.value
        ? formDesign.value.getData()
        : Promise.resolve({ formData: { widgetList: [], formConfig: {} } });
    const step3 = processDesign.value.getData();
    const step4 = advancedSetting.value.getData();
    Promise.all([step1, step2, step3, step4]).then((res) => {
        let basicData = res[0].formData;
        const lfFormData = useAuxiliaryForm.value ? JSON.stringify(res[1].formData) : '';
        Object.assign(basicData, { lfFormData: lfFormData });
        // 保留 nodeConfig 树结构（不序列化）供 Dope Sheet 使用
        basicData.nodeConfig = res[2].formData;
        // 高级设置的 extraFlags 与 basicData 已设位做 OR 合并,避免覆盖 BasicSetting 的位
        const advancedFormData = res[3].formData;
        if (advancedFormData.extraFlags !== undefined && advancedFormData.extraFlags !== null) {
            basicData.extraFlags = Number(basicData.extraFlags || 0) | Number(advancedFormData.extraFlags || 0);
            delete advancedFormData.extraFlags;
        }
        Object.assign(basicData, advancedFormData);
        // 保留额外字段
        basicData.formCode = processConfig.value.formCode;
        basicData.bpmnCode = processConfig.value.bpmnCode;
        basicData.extraFlags = Number(basicData.extraFlags || 0) | Number(processConfig.value.extraFlags || 0);
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

.diy-container {
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

.aux-form-placeholder {
    background: white;
    padding: 40px;
    max-width: 750px;
    margin: 20px auto;
    border-radius: 4px;
}
</style>