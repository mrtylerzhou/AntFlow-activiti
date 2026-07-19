<template>
    <div v-if="componentLoaded" class="component">
        <component :is="loadedComponent" :previewData="componentData" :lfFormData="lfFormDataConfig"
            :lfFieldsData="lfFieldsConfig" :lfFieldPerm="lfFieldControlVOs"
            :lfFormdataList="lfFormdataListConfig" :lfFieldsMulti="lfFieldsMultiConfig"
            :formHidden="formHiddenConfig" :isPreview="true">
        </component>
    </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getViewBusinessProcess } from "@/api/workflow/index";
import { loadDIYComponent, loadLFComponent, loadLFMultiFormComponent } from '@/views/workflow/components/componentload.js';

const { proxy } = getCurrentInstance();
const { query } = useRoute();
const { formCode, processNumber, isLowCodeFlow } = query;
const queryParams = {
    formCode: formCode,
    processNumber: processNumber,
    isLowCodeFlow: isLowCodeFlow || false,
    type: 2,
    isOutSideAccessProc: false
};
let loadFaild = ref(false);
let componentLoaded = ref(false);
let componentData = ref(null);
let loadedComponent = ref(null);
let lfFormDataConfig = ref(null);
let lfFieldsConfig = ref(null);
let lfFieldControlVOs = ref(null);
let lfFormdataListConfig = ref([]);
let lfFieldsMultiConfig = ref({});
let formHiddenConfig = ref({});
onMounted(async () => {
    await loadPreview(queryParams);
})
/**预览 */
const loadPreview = async (param) => {
    proxy.$modal.loading();
    await getViewBusinessProcess(param).then(async (response) => {
        if (response.code == 200) {
            const responseData = response.data;
            componentLoaded.value = true
            if (responseData.isLowCodeFlow == true) {//低代码表单 和 三方接入
                const formdataList = responseData.lfFormdataList;
                const isExternal = Array.isArray(formdataList) && formdataList.length > 0;
                if (isExternal) {
                    // 外部表单模式: 多 tab 渲染
                    lfFormdataListConfig.value = formdataList;
                    lfFieldsMultiConfig.value = responseData.lfFieldsMulti || {};
                    lfFieldControlVOs.value = JSON.stringify(responseData.processRecordInfo?.lfFieldControlVOs || []);
                    formHiddenConfig.value = responseData.processRecordInfo?.formHidden || {};
                    lfFormDataConfig.value = null;
                    lfFieldsConfig.value = '{}';
                    loadedComponent.value = await loadLFMultiFormComponent();
                } else {
                    // 内联表单模式: 兼容旧逻辑
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
                loadedComponent.value = await loadDIYComponent(param.formCode).catch((err) => { proxy.$modal.msgError(err); });
                componentData.value = responseData;
            }
        } else {
            loadFaild.value = true
        }
        proxy.$modal.closeLoading();
    });
}
</script>
<style lang="scss" scoped>
.component {
    left: 0 !important;
    right: 0 !important;
    max-width: 720px !important;
    min-height: auto !important;
    margin: auto !important;
    background: white !important;
}
</style>