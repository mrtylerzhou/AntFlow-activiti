<template>
    <div class="app-container">
        <div class="box">
            <el-tabs type="border-card" v-model="activeName" @tab-click="handleClick">
                <el-tab-pane name="createFrom">
                    <template #label>
                        填写表单
                    </template>
                <div style="height: calc(100vh - 200px);overflow-y: auto">
                    <component ref="formRef" v-if="componentLoaded" :is="loadedComponent"
                                :lfFormData="lfFormData" :isPreview="false" @handleBizBtn="handleSubmit">
                    </component>
                </div>
                </el-tab-pane>

                <el-tab-pane name="flowFromReview" label="流程预览">
                    <div v-if="reviewWarpShow" >
                        <ReviewWarp v-model:previewConf="previewConf" />
                    </div>
                </el-tab-pane>
            </el-tabs>
            <label class="page-close-box" @click="close()"><img src="@/assets/images/back-close.png"></label>
        </div>
    </div>
</template>

<script setup>
import { ref, getCurrentInstance, onMounted } from 'vue'
import ReviewWarp from '@/components/Workflow/Preview/reviewWarp.vue'
import { processOperation } from '@/api/workflow'
import { getLowCodeFromCodeData } from '@/api/lowcodeApi'
import { loadDIYComponent, loadLFComponent } from '@/views/workflow/components/componentload.js'

const { proxy } = getCurrentInstance()
const route = useRoute();
const activeName = ref("createFrom")
const flowCode = route.query?.formCode
const formRef = ref(null)
const reviewWarpShow = ref(false)
const previewConf = ref({})
let componentLoaded = ref(false);
let loadedComponent = ref(null);
let lfFormData = ref(null);
const isLFFlow = route.query?.formType == 'LF';

onMounted(async () => {
    await adapFlowType();
})
const adapFlowType = async () => { 
    if (isLFFlow && isLFFlow == true) {
        await getLowCodeFromCodeData(flowCode).then((res) => {
            if (res.code == 200) {
                lfFormData.value = res.data
            }
        });
        loadedComponent.value = await loadLFComponent();
        componentLoaded.value = lfFormData.value ? true : false;
    } else {
        loadedComponent.value = await loadDIYComponent(flowCode)
            .catch((err) => { console.log('err=======', err); proxy.$modal.msgError(err); componentLoaded.value = false; });
        componentLoaded.value = true;
    }
}
const handleSubmit = (param) => {
    if (componentLoaded.value != true) { return; }
    startTest(param);
}
/**
 * 点击tab切换
 * @param tab 
 * @param event 
 */
const handleClick = async (tab, event) => {
    if (componentLoaded.value != true) { return; }
    if (tab.paneName != 'flowFromReview') {
        reviewWarpShow.value = false;
        return;
    }
    await formRef.value.handleValidate().then(async (isValid) => {
        if (!isValid) {
            activeName.value = "createFrom";
        } else {
            const _formData = await formRef.value.getFromData();
            if (isLFFlow && isLFFlow == true) {
                previewConf.value.lfFields = JSON.parse(_formData);
            } else {
                previewConf.value = JSON.parse(_formData);
            }
            previewConf.value.formCode = flowCode || '';
            previewConf.value.isStartPreview = true;
            previewConf.value.isLowCodeFlow = isLFFlow;
            previewConf.value.isOutSideAccess = false;
            reviewWarpShow.value = true;
        }
    });
}
/**
 * 发起流程
 * @param param 
 */
const startTest = (param) => {
    let bizFrom = JSON.parse(param);
    bizFrom.formCode = flowCode|| '';
    bizFrom.operationType = 1;//operationType 1发起 3 审批 
    bizFrom.isLowCodeFlow = false; 
    bizFrom.lfFields = null;
    if (isLFFlow && isLFFlow == true) {
        bizFrom = {};  
        bizFrom.formCode = flowCode|| '';
        bizFrom.operationType = 1;//operationType 1发起 3 审批 
        bizFrom.isLowCodeFlow = true;  
        bizFrom.lfFields = JSON.parse(param);
    }
    proxy.$modal.loading();
    processOperation(bizFrom).then((res) => {
        if (res.code == 200) {
            proxy.$modal.msgSuccess("发起流程成功");
            const obj = { path: "/flowtask/mytask" };
            proxy.$tab.openPage(obj);
        } else {
            proxy.$modal.msgError("发起流程失败" + res.errMsg);
        }
        proxy.$modal.closeLoading();
    });
}
/** 关闭按钮 */
function close() {
    proxy.$tab.closePage();
}
</script>
<style scoped lang="scss">
.task-title-text {
    line-height: 28px;
    font-weight: 600;
    font-size: 16px;
    color: #383838;
}
</style>