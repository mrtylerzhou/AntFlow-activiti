<template>
  <div class="card-box" style="padding: 10px;">
    <div class="task-title">
      <span class="task-title-text">流程详情预览</span>
    </div>
    <div style="background-color: #f5f5f7;padding: 15px;">
      <el-tabs v-model="activeTab" class="demo-tabs">
        <el-tab-pane label="流程基本信息" name="flowForm"></el-tab-pane>
        <el-tab-pane label="业务表单预览" name="formRender"></el-tab-pane>
        <el-tab-pane label="流程模板预览" name="flow"></el-tab-pane>
      </el-tabs>
      <el-row>
        <el-col :span="24" v-if="activeTab === 'flowForm'" class="item">
          <div v-if="processConfig">
            <BasicSetting ref="basicSetting" :basicData="processConfig" :flowType="flowType" :readonly="true" />
          </div>
        </el-col>
        <el-col :span="24" v-if="activeTab === 'formRender'" class="item">
          <div v-if="processConfig" class="component">
            <component v-if="componentLoaded" :is="loadedComponent" :lfFormData="lfFormDataConfig"
              :lfFieldPerm="lfFieldControlVOs" :isPreview="true"
              :lfFormdataList="lfFormdataListConfig" :lfFieldsMulti="lfFieldsMultiConfig"
              :formHidden="formHiddenConfig">
            </component>
          </div>
        </el-col>
        <el-col :span="24" v-if="activeTab === 'flow'" class="item">
          <div v-if="nodeConfig" class="flow">
            <Process ref="processDesign" :processData="nodeConfig" />
          </div>
        </el-col>
      </el-row>
    </div>
    <label class="page-close-box" @click="close()"><img src="@/assets/images/antflow/back-close.png"></label>
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue';
import { getApiWorkFlowData } from "@/api/workflow/index";
import BasicSetting from "@/components/Workflow/basicSetting/index.vue";
import Process from "@/components/Workflow/Process/index.vue";
import { FormatDisplayUtils } from '@/utils/antflow/formatdisplay_data';
import { loadDIYComponent, loadLFComponent, loadLFMultiFormComponent } from '@/views/workflow/components/componentload.js';
import { useDopeSheetStore } from '@/store/modules/dopeSheet';

const { proxy } = getCurrentInstance();
const route = useRoute();
const dopeSheetStore = useDopeSheetStore();
const activeTab = ref('flowForm')
let processConfig = ref(null)
let lfFormDataConfig = ref(null)
let lfFieldControlVOs = ref(null)
let lfFormdataListConfig = ref([])
let lfFieldsMultiConfig = ref({})
let formHiddenConfig = ref({})
let nodeConfig = ref(null)
let title = ref('')
let id = route.query?.id
let flowType = ref('DIY')
let loadedComponent = ref(null)
let componentLoaded = ref(null)

/** 关闭按钮 */
function close() {
  proxy.$tab.closePage();
};
onMounted(async () => {
  proxy.$modal.loading();
  // Dope Sheet 模式：从 store 读取数据
  if (route.query.mode === 'store' && dopeSheetStore.processConfig) {
    await initFromStore();
  } else {
    await init();
  }
  proxy.$modal.closeLoading();
});

/** 从 Dope Sheet store 初始化 */
const initFromStore = async () => {
  let data = dopeSheetStore.processConfig;
  processConfig.value = data;
  flowType.value = data?.isLowCodeFlow == '1' ? 'LF' : 'DIY';
  title.value = data?.bpmnName;
  nodeConfig.value = data?.nodeConfig;
  if (data.isLowCodeFlow == '1') {
    const USE_EXTERNAL_FORM_FLAG = 64;
    const flags = Number(data?.extraFlags || 0);
    const isExternal = (flags & USE_EXTERNAL_FORM_FLAG) === USE_EXTERNAL_FORM_FLAG;
    if (isExternal) {
      lfFormdataListConfig.value = data?.lfFormdataList || [];
      lfFieldsMultiConfig.value = {};
      formHiddenConfig.value = {};
      lfFormDataConfig.value = null;
      lfFieldControlVOs.value = '[]';
      loadedComponent.value = await loadLFMultiFormComponent();
    } else {
      lfFormDataConfig.value = data?.lfFormData;
      lfFieldControlVOs.value = '[]';
      lfFormdataListConfig.value = [];
      lfFieldsMultiConfig.value = {};
      formHiddenConfig.value = {};
      loadedComponent.value = await loadLFComponent();
    }
    componentLoaded.value = true;
  } else {
    loadedComponent.value = await loadDIYComponent(data.formCode).catch((err) => {
      proxy.$modal.msgError(err);
    });
    componentLoaded.value = true;
  }
}
const init = async () => {
  let mockjson = await getApiWorkFlowData({ id });
  if (mockjson.code != 200) {
    proxy.$modal.msgError(mockjson.errMsg);
    return;
  }
  let data = FormatDisplayUtils.getToTree(mockjson.data);
  processConfig.value = data;
  flowType.value = data?.isLowCodeFlow == '1' ? 'LF' : 'DIY';
  title.value = data?.bpmnName;
  nodeConfig.value = data?.nodeConfig;
  if (data.isLowCodeFlow == '1') {//低代码表单
    const USE_EXTERNAL_FORM_FLAG = 64;
    const flags = Number(data?.extraFlags || 0);
    const isExternal = (flags & USE_EXTERNAL_FORM_FLAG) === USE_EXTERNAL_FORM_FLAG;
    if (isExternal) {
      // 外部表单模式: 多 tab 渲染
      lfFormdataListConfig.value = data?.lfFormdataList || [];
      lfFieldsMultiConfig.value = {};
      formHiddenConfig.value = {};
      lfFormDataConfig.value = null;
      lfFieldControlVOs.value = '[]';
      loadedComponent.value = await loadLFMultiFormComponent();
    } else {
      // 内联表单模式: 兼容旧逻辑
      lfFormDataConfig.value = data?.lfFormData
      lfFieldControlVOs.value = JSON.stringify(data.processRecordInfo?.lfFieldControlVOs);
      lfFormdataListConfig.value = [];
      lfFieldsMultiConfig.value = {};
      formHiddenConfig.value = {};
      loadedComponent.value = await loadLFComponent();
    }
    componentLoaded.value = true;
  } else {//自定义表单
    loadedComponent.value = await loadDIYComponent(data.formCode).catch((err) => {
      proxy.$modal.msgError(err);
    });
    componentLoaded.value = true;
  }
}

</script>

<style scoped lang="scss">
.task-title {
  display: flex;
  justify-content: space-between;
  padding-bottom: 6px;
  //border-bottom: 2px solid #e8e8e8;
  background-color: #f5f5f7;
}

.task-title-text {
  line-height: 28px;
  font-weight: 600;
  font-size: 16px;
  color: #383838;
}

.component {
  background: white !important;
  padding: 30px !important;
  max-width: 720px !important;
  left: 0 !important;
  right: 0 !important;
  margin: auto !important;
}
</style>
