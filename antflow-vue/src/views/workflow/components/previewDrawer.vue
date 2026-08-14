<template>
  <div>
    <el-drawer v-model="visible" v-if="visible" title="流程预览" :size="800" :with-header="false" :destroy-on-close="true">
      <span style="font-weight: bold;">流程详情</span>

      <el-divider />
      <div class="tabs-header-wrap">
        <el-tabs v-model="activeName" class="set-tabs" @tab-click="handleTabClick">
          <el-tab-pane label="表单信息" name="baseTab">
            <div v-if="baseTabShow" aria-hidden="true">
              <previewComponent ref="previewCompRef" :isPreview="true" :ignoreReadonly="ignoreReadonly" />
            </div>
          </el-tab-pane>
          <el-tab-pane label="审批记录" name="flowStep">
            <div v-if="flowStepShow">
              <FlowStepTable />
            </div>
          </el-tab-pane>
          <el-tab-pane label="流程预览" name="flowReview">
            <div v-if="flowReviewShow">
              <ReviewWarp :previewConf="ignoreReadonly ? previewConf : null" :key="reviewKey" />
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

      <!-- 右侧贴边: 悬浮菜单(表单字段变更记录 + 流程沟通) -->
      <ProcessFloatMenu v-if="processNumber" :processNumber="processNumber" :fixed="false" />
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
import ProcessFloatMenu from "@/views/workflow/components/ProcessFloatMenu.vue"
import { getApiWorkFlowData } from "@/api/workflow/index"
import { FormatDisplayUtils } from '@/utils/antflow/formatdisplay_data'

let store = useStore()
let { setPreviewDrawer } = store
const { proxy } = getCurrentInstance();
let previewDrawer = computed(() => store.previewDrawer)
let viewConfig = computed(() => store.instanceViewConfig1)
let processNumber = computed(() => viewConfig.value.processNumber)
let processState = computed(() => viewConfig.value.processState)
let ignoreReadonly = computed(() => viewConfig.value.ignoreReadonly)
const activeName = ref('baseTab')
let baseTabShow = ref(true);
let flowStepShow = ref(false);
let flowReviewShow = ref(false);
let flowTemplateShow = ref(false);
let nodeConfig = ref(null);
let templateLoadFail = ref(false);
let previewConf = ref(null);
let reviewKey = ref(0);
const previewCompRef = ref(null);
let visible = computed({
  get() {
    return previewDrawer.value
  },
  set() {
    closeDrawer()
  }
})
const handleTabClick = async (tab, event) => {
  activeName.value = tab.paneName;
  if (tab.paneName == 'baseTab') {
    baseTabShow.value = true;
  } else if (tab.paneName == 'flowStep') {
    flowStepShow.value = true;
  } else if (tab.paneName == 'flowReview') {
    if (ignoreReadonly.value) {
      // 流程监控查看：用当前可编辑表单 + bpmnCode 做发起页式预览，支持改表单看不同分支
      await buildFlowPreviewConf();
      reviewKey.value++;
    }
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
 * 流程监控查看：基于当前可编辑表单内容 + bpmnCode + 真实发起人构建流程预览参数
 * 不带 processNumber(后端 taskPagePreviewNode 检测到无编号则不查 BpmVariable 存储表单,改用此处提交的表单数据)
 * 保留 isStartPreview=false,沿用真实发起人计算审批人(而非当前登录管理员),仅模仿发起页预览的"按表单算分支"行为
 */
const buildFlowPreviewConf = async () => {
  let conf = {
    isStartPreview: false,
    formCode: viewConfig.value.formCode,
    isLowCodeFlow: viewConfig.value.isLowCodeFlow || false,
    isOutSideAccessProc: viewConfig.value.isOutSideAccess || false,
    bpmnCode: viewConfig.value.bpmnCode,
    startUserId: viewConfig.value.startUserId,
  };
  try {
    const comp = previewCompRef.value;
    const formDataStr = await comp?.getFromData();
    if (comp?.isMultiForm) {
      // 外部表单模式: 数据为 { [formdataId]: fieldMap }
      conf.lfFieldsMulti = JSON.parse(formDataStr);
      conf.lfFields = null;
    } else {
      // 内联表单模式: 数据含 approversList/approversValid
      const lfFormdata = JSON.parse(formDataStr);
      conf.approversList = lfFormdata.approversList;
      conf.approversValid = lfFormdata.approversValid;
      conf.lfFields = lfFormdata;
    }
  } catch (e) {
    // 表单数据获取失败时，仅按 bpmnCode 预览流程设计
  }
  previewConf.value = conf;
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