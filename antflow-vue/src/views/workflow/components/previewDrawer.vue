<template>
  <div>
    <el-drawer v-model="visible" v-if="visible" title="流程预览" :size="800" :with-header="false" :destroy-on-close="true">
      <span style="font-weight: bold;">流程详情</span>
      
      <el-divider />
      <div class="tabs-header-wrap">
        <el-tabs v-model="activeName" class="set-tabs" @tab-click="handleTabClick">
          <el-tab-pane label="表单信息" name="baseTab">
            <div v-if="baseTabShow" aria-hidden="true">
              <previewComponent :isPreview="true" :ignoreReadonly="ignoreReadonly" />
            </div>
          </el-tab-pane>
          <el-tab-pane label="审批记录" name="flowStep">
            <div v-if="flowStepShow">
              <FlowStepTable />
            </div>
          </el-tab-pane>
          <el-tab-pane label="流程预览" name="flowReview">
            <div v-if="flowReviewShow">
              <ReviewWarp />
            </div>
          </el-tab-pane>
          <el-tab-pane label="流程模板" name="flowTemplate">
            <div v-if="flowTemplateShow">
              <Process v-if="nodeConfig" :processData="nodeConfig" />
              <div v-else-if="templateLoadFail" style="text-align:center;color:#999;padding:20px;">未获取到流程模板</div>
            </div>
          </el-tab-pane>
        </el-tabs>
        <ProcessStateImg :process-state="processState" />
      </div>
      <label class="page-close-box" @click="closeDrawer()"><img src="@/assets/images/antflow/back-close.png"></label>
    </el-drawer>
     
  </div>

</template>

<script setup>
import { ref, computed, getCurrentInstance } from 'vue'
import { useStore } from '@/store/modules/workflow'
import FlowStepTable from "@/components/Workflow/Preview/flowStepTable.vue"
import ReviewWarp from "@/components/Workflow/Preview/reviewWarp.vue"
import previewComponent from "@/views/workflow/components/previewComponent.vue"
import ProcessStateImg from './ProcessStateImg.vue'
import Process from "@/components/Workflow/Process/index.vue"
import { getApiWorkFlowData } from "@/api/workflow/index"
import { FormatDisplayUtils } from '@/utils/antflow/formatdisplay_data'

let store = useStore()
let { setPreviewDrawer } = store
const { proxy } = getCurrentInstance();
let previewDrawer = computed(() => store.previewDrawer)
let viewConfig = computed(() => store.instanceViewConfig1)
let processState = computed(() => viewConfig.value.processState)
let ignoreReadonly = computed(() => viewConfig.value.ignoreReadonly)
const activeName = ref('baseTab')
let baseTabShow = ref(true);
let flowStepShow = ref(false);
let flowReviewShow = ref(false);
let flowTemplateShow = ref(false);
let nodeConfig = ref(null);
let templateLoadFail = ref(false);
let visible = computed({
  get() {
    return previewDrawer.value
  },
  set() {
    closeDrawer()
  }
})
const handleTabClick = (tab, event) => {
  activeName.value = tab.paneName;
  if (tab.paneName == 'baseTab') {
    baseTabShow.value = true;
  } else if (tab.paneName == 'flowStep') {
    flowStepShow.value = true;
  } else if (tab.paneName == 'flowReview') {
    flowReviewShow.value = true;
  } else if (tab.paneName == 'flowTemplate') {
    flowTemplateShow.value = true;
    loadFlowTemplate();
  }
}
/** 加载流程模板（流程设计）预览 */
const loadFlowTemplate = async () => {
  if (nodeConfig.value || templateLoadFail.value) {
    return;
  }
  const confId = viewConfig.value.confId;
  if (!confId) {
    templateLoadFail.value = true;
    return;
  }
  proxy.$modal.loading();
  try {
    let mockjson = await getApiWorkFlowData({ id: confId });
    if (mockjson.code != 200) {
      templateLoadFail.value = true;
      proxy.$modal.msgError(mockjson.errMsg);
      return;
    }
    let data = FormatDisplayUtils.getToTree(mockjson.data);
    nodeConfig.value = data?.nodeConfig;
    if (!nodeConfig.value) {
      templateLoadFail.value = true;
    }
  } catch (e) {
    templateLoadFail.value = true;
  } finally {
    proxy.$modal.closeLoading();
  }
}
/**
 * 关闭抽屉
 */
const closeDrawer = () => {
  setPreviewDrawer(false)
}
handleTabClick({ paneName: "baseTab" }) 
</script>

<style lang="scss" scoped>
.tabs-header-wrap {
  position: relative;
}
</style>