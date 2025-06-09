<template>
    <div v-if="visible">
        <div v-if="loadFaild">
            <p style="font-size: small;color: red;text-align: center;margin: 0 10%;">
                {{ tips }}
            </p>
        </div>
        <div v-if="componentLoaded" class="component">
            <component :is="loadedComponent" :previewData="componentData" :lfFormData="lfFormDataConfig"
                :lfFieldsData="lfFieldsConfig" :lfFieldPerm="lfFieldControlVOs" :isPreview="isPreview"></component>
        </div>
    </div>
</template>
<script setup>
import { ref, computed } from 'vue';
import { getViewBusinessProcess } from "@/api/workflow/index";
import { useStore } from '@/store/modules/workflow';
import { loadDIYComponent, loadLFComponent } from '@/views/workflow/components/componentload.js';
const { proxy } = getCurrentInstance();
let store = useStore()
let viewConfig = computed(() => store.instanceViewConfig1)
let props = defineProps({
    isPreview: {
        type: Boolean,
        default: false,
    }
});

let loadFaild = ref(false);
let componentData = ref(null);
let componentLoaded = ref(false);
let loadedComponent = ref(null);
let lfFormDataConfig = ref(null);
let lfFieldsConfig = ref(null);
let lfFieldControlVOs = ref(null);

let tips = "*未获取到外部表单信息，请联系管理员。";
let visible = computed({
    get() {
        return true;
    }
})

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
            if (responseData.isLowCodeFlow || responseData.isOutSideAccessProc) {//低代码表单 和 三方接入
                lfFormDataConfig.value = responseData.lfFormData;
                lfFieldsConfig.value = JSON.stringify(responseData.lfFields);
                lfFieldControlVOs.value = JSON.stringify(responseData.processRecordInfo.lfFieldControlVOs);
                loadedComponent.value = await loadLFComponent(param.formCode);
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
preview(viewConfig.value);
</script>
<style lang="scss" scoped>
.component {
    background: white !important;
    padding: 30px !important;
    max-width: 720px !important;
    min-height: 520px !important;
    left: 0 !important;
    right: 0 !important;
    margin: auto !important;
}
</style>