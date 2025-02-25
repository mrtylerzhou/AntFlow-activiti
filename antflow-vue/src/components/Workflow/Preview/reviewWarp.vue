<template>
    <div style="text-align:center;background: #f5f5f7;width: 100%;height: 100%">
        <section class="antflow-design">
            <div class="box-scale">
                <LineWarp v-if="nodeConfig" v-model:nodeConfig="nodeConfig" />
                <div class="end-node">
                    <div class="end-node-circle"></div>
                    <div class="end-node-text">流程结束</div>
                </div>
            </div>
        </section>
    </div>
</template>
<script setup>
import LineWarp from "@/components/Workflow/Preview/lineWarp.vue"
import { getFlowPreview } from '@/api/workflow'
import { FormatUtils } from '@/utils/flow/formatFlowPreview'
import { useStore } from '@/store/modules/workflow' 
const { proxy } = getCurrentInstance()
let store = useStore()
let viewConfig = computed(() => store.instanceViewConfig1) 
let props = defineProps({
    previewConf: {
        type: Object,
        default: () => (null)
    }
});
const nodeConfig = ref(null) 
const getFlowPreviewList = async (objData) => {  
    let param = {
        processNumber: objData.processNumber,
        isStartPreview: false,
        isOutSideAccessProc: objData.isOutSideAccess || false,
        isLowCodeFlow: objData.isLowCodeFlow || false
    };
    if(props.previewConf) {
        param = props.previewConf;
    }
    // console.log("param=========8888=====", JSON.stringify(param))
    proxy.$modal.loading();
    let resData = await getFlowPreview(param);
    proxy.$modal.closeLoading();
    let formatData = FormatUtils.formatSettings(resData.data);
    nodeConfig.value = formatData;
}
getFlowPreviewList(viewConfig.value);
</script>
<style lang="scss" scoped> 
.end-node-circle {
    width: 20px;
    height: 20px;
    margin: auto;
    border-radius: 50%;
    background: #dbdcdc
} 
</style>