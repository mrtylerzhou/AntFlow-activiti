<template>
  <div class="app-container">
    <!-- 一级筛选: 流程选择 -->
    <el-card shadow="never" class="mb10">
      <template #header><span style="font-weight:bold">流程选择</span></template>
      <el-select v-model="selectedFormCodes" multiple filterable clearable placeholder="搜索并选择流程"
        style="width: 100%" :loading="flowListLoading" @visible-change="onFlowSelectVisible">
        <el-option-group v-if="diyOptions.length" label="DIY 流程">
          <el-option v-for="item in diyOptions" :key="item.key" :label="item.value" :value="item.key" />
        </el-option-group>
        <el-option-group v-if="lfOptions.length" label="LF 低代码流程">
          <el-option v-for="item in lfOptions" :key="item.key" :label="item.value" :value="item.key" />
        </el-option-group>
        <el-option-group v-if="outsideOptions.length" label="SaaS 第三方流程">
          <el-option v-for="item in outsideOptions" :key="item.key" :label="item.value" :value="item.key" />
        </el-option-group>
      </el-select>

      <!-- 版本范围 -->
      <el-row class="mt10" :gutter="20">
        <el-col :span="12">
          <el-radio-group v-model="versionMode" :disabled="!filtersEnabled">
            <el-radio value="RECENT">最近</el-radio>
            <el-input-number v-model="recentN" :min="1" :max="50" size="small" style="width:80px"
              :disabled="versionMode !== 'RECENT' || !filtersEnabled" />
            <el-radio value="EFFECTIVE" class="ml10">仅生效版本</el-radio>
          </el-radio-group>
        </el-col>
      </el-row>
    </el-card>

    <!-- 二级筛选 -->
    <el-card shadow="never" class="mb10">
      <template #header><span style="font-weight:bold">筛选条件</span>
        <el-tag v-if="!filtersEnabled" type="info" size="small" class="ml10">请先选择流程</el-tag>
      </template>
      <el-form :disabled="!filtersEnabled" label-width="130px" size="default">
        <el-row :gutter="16">
          <el-col :span="8">
            <el-form-item label="流程名称包含">
              <el-input v-model="filters.bpmnNameLike" placeholder="模糊匹配" clearable />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="使用外部表单">
              <el-switch v-model="filters.useExternalForm" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="表单字段包含">
              <el-input v-model="filters.formFieldKeyword" placeholder="字段名/ID" clearable />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="16">
          <el-col :span="8">
            <el-form-item label="含可编辑字段权限">
              <el-switch v-model="filters.hasEditableFieldPerm" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="审批人规则">
              <el-select v-model="filters.approverRules" multiple clearable placeholder="选择规则" style="width:100%">
                <el-option v-for="item in approverRuleOptions" :key="item.value" :label="item.label" :value="item.value" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="审批人为空规则">
              <el-select v-model="filters.noHeaderActions" multiple clearable placeholder="选择规则" style="width:100%">
                <el-option label="不允许发起" :value="0" />
                <el-option label="跳过" :value="1" />
                <el-option label="转交管理员" :value="2" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="16">
          <el-col :span="8">
            <el-form-item label="额外增加审批">
              <el-switch v-model="filters.hasAdditionalSign" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="额外排除审批">
              <el-switch v-model="filters.hasExcludeSign" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="包含通知">
              <el-switch v-model="filters.hasNotice" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="16">
          <el-col :span="8">
            <el-form-item label="按钮权限包含">
              <el-select v-model="filters.buttonTypes" multiple clearable placeholder="选择按钮" style="width:100%">
                <el-option v-for="item in buttonTypeOptions" :key="item.value" :label="item.label" :value="item.value" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="节点类型包含">
              <el-select v-model="filters.nodeTypes" multiple clearable placeholder="选择类型" style="width:100%">
                <el-option v-for="item in nodeTypeOptions" :key="item.value" :label="item.label" :value="item.value" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="审批人去重">
              <el-switch v-model="filters.deduplication" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="16">
          <el-col :span="8">
            <el-form-item label="允许撤回">
              <el-switch v-model="filters.allowRevoke" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="允许作废">
              <el-switch v-model="filters.allowCancel" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="允许转发">
              <el-switch v-model="filters.allowForward" />
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
    </el-card>

    <!-- 操作按钮 -->
    <div class="mb10">
      <el-button type="primary" icon="Search" :disabled="!filtersEnabled || searching || hasMore" @click="startSearch">搜索</el-button>
      <el-button type="danger" icon="VideoPause" :disabled="!searching" @click="stopSearch">停止</el-button>
      <el-button type="success" icon="Download" :disabled="!hasMore || searching" @click="loadMore">继续加载</el-button>
      <el-tag v-if="searchDone" type="success" class="ml10">搜索完成</el-tag>
      <el-tag v-if="searching" type="warning" class="ml10">搜索中...</el-tag>
    </div>
    <!-- 当前批次正在搜索的流程 -->
    <div v-if="currentBatchNames.length" class="mb10 current-batch-info">
      <el-icon class="is-loading" v-if="searching"><Loading /></el-icon>
      <span class="batch-label">当前搜索：</span>
      <el-tag v-for="name in currentBatchNames" :key="name" size="small" class="batch-tag">{{ name }}</el-tag>
    </div>

    <!-- 结果表格 -->
    <el-table v-loading="searching" :data="results" row-key="formCode" border default-expand-all
      :tree-props="{ children: 'children' }">
      <el-table-column label="流程名称" prop="displayName" min-width="160" :show-overflow-tooltip="true" />
      <el-table-column label="formCode" prop="formCode" width="140" :show-overflow-tooltip="true" />
      <el-table-column label="分类" prop="flowType" width="80" align="center">
        <template #default="{ row }">
          <el-tag :type="row.flowType === 'LF' ? 'success' : row.flowType === 'OUTSIDE' ? 'warning' : ''" size="small">
            {{ row.flowType }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="版本名称" prop="bpmnName" min-width="150" :show-overflow-tooltip="true" />
      <el-table-column label="版本号" prop="bpmnCode" width="140" :show-overflow-tooltip="true" />
      <el-table-column label="状态" width="80" align="center">
        <template #default="{ row }">
          <el-tag :type="row.effectiveStatus === 1 ? 'success' : 'info'" size="small">
            {{ row.effectiveStatus === 1 ? '生效中' : '未生效' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="创建时间" prop="createTime" width="150" align="center">
        <template #default="{ row }">
          <span>{{ parseTime(row.createTime, '{y}-{m}-{d} {h}:{i}') }}</span>
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>

<script setup>
import { ref, reactive, computed } from "vue";
import { Loading } from "@element-plus/icons-vue";
import { getAllFormCodeList, perspectiveSearch } from "@/api/workflow/perspectiveApi";

const { proxy } = getCurrentInstance();

// ========== 流程列表 ==========
const flowListLoading = ref(false);
const flowList = ref([]);
const selectedFormCodes = ref([]);

const diyOptions = computed(() => flowList.value.filter(f => !f.type || f.type === 'DIY'));
const lfOptions = computed(() => flowList.value.filter(f => f.type === 'LF'));
const outsideOptions = computed(() => flowList.value.filter(f => f.type === 'OUTSIDE'));

const onFlowSelectVisible = async (visible) => {
  if (visible && flowList.value.length === 0) {
    flowListLoading.value = true;
    try {
      const res = await getAllFormCodeList('');
      flowList.value = res.data || [];
    } catch (e) {
      proxy.$modal.msgError("加载流程列表失败");
    } finally {
      flowListLoading.value = false;
    }
  }
};

// ========== 版本模式 ==========
const versionMode = ref("RECENT");
const recentN = ref(1);

// ========== 二级筛选 ==========
const filtersEnabled = computed(() => selectedFormCodes.value.length > 0);
const filters = reactive({
  bpmnNameLike: '',
  useExternalForm: false,
  formFieldKeyword: '',
  hasEditableFieldPerm: false,
  approverRules: [],
  hasAdditionalSign: false,
  hasExcludeSign: false,
  noHeaderActions: [],
  buttonTypes: [],
  hasNotice: false,
  nodeTypes: [],
  deduplication: false,
  allowRevoke: false,
  allowCancel: false,
  allowForward: false,
});

// 下拉选项
const approverRuleOptions = [
  { value: 2, label: '层层审批' },
  { value: 3, label: '指定层级审批' },
  { value: 4, label: '指定角色' },
  { value: 5, label: '指定人员' },
  { value: 6, label: 'HRBP' },
  { value: 7, label: '自选模块' },
  { value: 8, label: '关联业务表' },
  { value: 11, label: '外部传入人员' },
  { value: 12, label: '发起人' },
  { value: 13, label: '直属领导' },
  { value: 14, label: '部门负责人' },
  { value: 15, label: '被审批人自己' },
  { value: 16, label: '表单中相关人员' },
  { value: 17, label: '自定义规则' },
  { value: 18, label: '上一节点相关人员' },
];
const buttonTypeOptions = [
  { value: 3, label: '同意' },
  { value: 4, label: '拒绝' },
  { value: 18, label: '退回' },
  { value: 19, label: '撤回' },
  { value: 21, label: '加签' },
  { value: 15, label: '转发' },
  { value: 7, label: '作废' },
  { value: 29, label: '撤回(发起)' },
];
const nodeTypeOptions = [
  { value: 4, label: '审批人节点' },
  { value: 6, label: '抄送节点' },
  { value: 8, label: '抄送节点v2' },
  { value: 9, label: '自动节点' },
  { value: 12, label: '条件审批节点' },
  { value: 13, label: '条件抄送节点' },
  { value: 7, label: '并行网关' },
];

// ========== 搜索逻辑 ==========
const searching = ref(false);
const searchDone = ref(false);
const hasMore = ref(false);
const results = ref([]);
const currentBatchNames = ref([]);
let currentOffset = 0;
let stopped = false;

const startSearch = () => {
  results.value = [];
  currentOffset = 0;
  stopped = false;
  searchDone.value = false;
  doSearch();
};

const stopSearch = () => {
  stopped = true;
};

const loadMore = () => {
  doSearch();
};

const doSearch = async () => {
  searching.value = true;
  // 计算当前批次正在搜索的流程名称
  const batchCodes = selectedFormCodes.value.slice(currentOffset, currentOffset + 5);
  currentBatchNames.value = batchCodes.map(code => {
    const found = flowList.value.find(f => f.key === code);
    return found ? found.value : code;
  });
  try {
    const data = {
      formCodes: selectedFormCodes.value,
      versionMode: versionMode.value,
      recentN: recentN.value,
      offset: currentOffset,
      batchSize: 5,
      filters: buildFilters(),
    };
    const res = await perspectiveSearch(data);
    const d = res.data;
    if (d && d.results) {
      for (const item of d.results) {
        // 将allMatches展开为子行
        const row = {
          formCode: item.formCode,
          displayName: item.displayName,
          flowType: item.flowType,
          bpmnName: item.latestMatch.bpmnName,
          bpmnCode: item.latestMatch.bpmnCode,
          effectiveStatus: item.latestMatch.effectiveStatus,
          createTime: item.latestMatch.createTime,
          children: item.allMatches.length > 1 ? item.allMatches.slice(1).map(m => ({
            formCode: item.formCode,
            displayName: '',
            flowType: item.flowType,
            bpmnName: m.bpmnName,
            bpmnCode: m.bpmnCode,
            effectiveStatus: m.effectiveStatus,
            createTime: m.createTime,
          })) : undefined,
        };
        results.value.push(row);
      }
    }
    hasMore.value = d ? d.hasMore : false;
    currentOffset += (d ? d.processedCount : 5);
    if (!hasMore.value) {
      searchDone.value = true;
      currentBatchNames.value = [];
    }
  } catch (e) {
    proxy.$modal.msgError("搜索失败: " + (e.message || e));
  } finally {
    searching.value = false;
    if (stopped) {
      searchDone.value = true;
      hasMore.value = false;
      currentBatchNames.value = [];
    }
  }
};

const buildFilters = () => {
  const f = {};
  if (filters.bpmnNameLike) f.bpmnNameLike = filters.bpmnNameLike;
  if (filters.useExternalForm) f.useExternalForm = true;
  if (filters.formFieldKeyword) f.formFieldKeyword = filters.formFieldKeyword;
  if (filters.hasEditableFieldPerm) f.hasEditableFieldPerm = true;
  if (filters.approverRules.length) f.approverRules = filters.approverRules;
  if (filters.hasAdditionalSign) f.hasAdditionalSign = true;
  if (filters.hasExcludeSign) f.hasExcludeSign = true;
  if (filters.noHeaderActions.length) f.noHeaderActions = filters.noHeaderActions;
  if (filters.buttonTypes.length) f.buttonTypes = filters.buttonTypes;
  if (filters.hasNotice) f.hasNotice = true;
  if (filters.nodeTypes.length) f.nodeTypes = filters.nodeTypes;
  if (filters.deduplication) f.deduplication = true;
  if (filters.allowRevoke) f.allowRevoke = true;
  if (filters.allowCancel) f.allowCancel = true;
  if (filters.allowForward) f.allowForward = true;
  return f;
};
</script>

<style scoped>
.mb10 { margin-bottom: 10px; }
.mt10 { margin-top: 10px; }
.ml10 { margin-left: 10px; }
.current-batch-info {
  display: flex;
  align-items: center;
  flex-wrap: wrap;
  gap: 4px;
  font-size: 13px;
  color: #606266;
}
.current-batch-info .batch-label { margin-left: 4px; }
.current-batch-info .batch-tag { margin-right: 2px; }
</style>
