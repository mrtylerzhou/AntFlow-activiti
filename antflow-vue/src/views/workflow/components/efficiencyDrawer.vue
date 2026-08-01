<template>
  <el-drawer
    v-model="drawerVisible"
    title="流程效能"
    :size="1100"
    :with-header="false"
    :destroy-on-close="true"
  >
    <!-- 顶部:标题 + 当时耗时 + 关闭按钮 -->
    <div class="eff-header">
      <div class="eff-header-left">
        <span class="eff-title">流程效能</span>
        <el-tag v-if="summary" :type="stateTagType" size="small" style="margin-left: 12px">
          {{ summary.processStateName }}
        </el-tag>
        <span v-if="summary" class="eff-meta">
          发起时间:{{ parseTime(summary.createTime, '{y}-{m}-{d} {h}:{i}') }}
        </span>
      </div>
      <div class="eff-header-right">
        <div class="eff-total">
          <span class="eff-total-label">当时耗时</span>
          <span class="eff-total-value">{{ summary ? summary.totalDurationText : '--' }}</span>
        </div>
        <el-button link @click="handleRefresh" title="刷新">
          <el-icon><Refresh /></el-icon>
        </el-button>
        <el-button link @click="closeDrawer" title="关闭">
          <el-icon><Close /></el-icon>
        </el-button>
      </div>
    </div>

    <el-divider style="margin: 8px 0" />

    <!-- 左右分栏 -->
    <el-row :gutter="12" class="eff-body">
      <!-- 左侧:节点列表 -->
      <el-col :span="10">
        <div class="eff-panel-title">节点列表(TOP3 红色标识)</div>
        <el-scrollbar style="height: calc(100vh - 180px)">
          <div v-loading="nodesLoading">
            <div
              v-for="node in nodes"
              :key="node.taskDefKey"
              class="eff-node-card"
              :class="{
                'eff-node-top': node.topRank,
                'eff-node-active': selectedTaskDefKey === node.taskDefKey,
                'eff-node-progress': node.inProgress
              }"
              @click="handleSelectNode(node)"
            >
              <div class="eff-node-header">
                <span class="eff-node-order">{{ node.orderNo }}</span>
                <span class="eff-node-name">{{ node.nodeName }}</span>
                <el-tag v-if="node.topRank" type="danger" size="small" effect="dark">
                  TOP{{ node.topRank }}
                </el-tag>
                <el-tag v-if="node.hasRollback" type="warning" size="small">退回</el-tag>
                <el-tag v-if="node.inProgress" type="success" size="small">审批中</el-tag>
              </div>
              <div class="eff-node-duration">{{ node.durationText }}</div>
              <div v-if="node.nodeTypeName" class="eff-node-type">{{ node.nodeTypeName }}</div>
            </div>
            <el-empty v-if="!nodesLoading && nodes.length === 0" description="无节点数据" />
          </div>
        </el-scrollbar>
      </el-col>

      <!-- 右侧:节点详情 -->
      <el-col :span="14">
        <el-scrollbar style="height: calc(100vh - 180px)">
          <div v-if="!selectedTaskDefKey" class="eff-detail-empty"></div>
          <div v-else v-loading="detailLoading">
            <!-- 签署信息 -->
            <div class="eff-panel-title">{{ detail ? detail.nodeName : '' }} - 节点信息</div>
            <el-descriptions v-if="detail" :column="2" border size="small" style="margin-bottom: 16px">
              <el-descriptions-item label="节点类型">
                {{ detail.nodeTypeName || '--' }}
              </el-descriptions-item>
              <el-descriptions-item label="人员来源">
                {{ detail.nodePropertyName || '--' }}
              </el-descriptions-item>
              <el-descriptions-item label="签署类型">
                {{ detail.signTypeName || '--' }}
              </el-descriptions-item>
              <el-descriptions-item label="退回">
                <el-tag v-if="detail.hasRollback" type="warning" size="small">发生过退回</el-tag>
                <span v-else>无</span>
              </el-descriptions-item>
            </el-descriptions>

            <!-- 人员耗时 -->
            <div class="eff-panel-title">审批人耗时(最后一轮)</div>
            <el-table v-if="detail" :data="detail.assignees" size="small" border>
              <el-table-column prop="assigneeName" label="审批人" min-width="100">
                <template #default="scope">
                  {{ scope.row.assigneeName || scope.row.assignee || '--' }}
                </template>
              </el-table-column>
              <el-table-column prop="startTime" label="开始时间" min-width="140">
                <template #default="scope">
                  {{ scope.row.startTime ? parseTime(scope.row.startTime, '{y}-{m}-{d} {h}:{i}') : '--' }}
                </template>
              </el-table-column>
              <el-table-column prop="endTime" label="结束时间" min-width="140">
                <template #default="scope">
                  <span v-if="scope.row.endTime">{{ parseTime(scope.row.endTime, '{y}-{m}-{d} {h}:{i}') }}</span>
                  <el-tag v-else type="success" size="small">审批中</el-tag>
                </template>
              </el-table-column>
              <el-table-column prop="durationText" label="耗时" min-width="100" align="center">
                <template #default="scope">
                  <span :class="{ 'eff-duration-warn': !scope.row.finished }">
                    {{ scope.row.durationText }}
                  </span>
                </template>
              </el-table-column>
            </el-table>
          </div>
        </el-scrollbar>
      </el-col>
    </el-row>
  </el-drawer>
</template>

<script setup>
import { ref, watch, getCurrentInstance } from 'vue'
import {
  getInstanceEfficiencySummary,
  getInstanceEfficiencyNodes,
  getInstanceEfficiencyNodeDetail
} from '@/api/workflow/instanceEfficiencyApi'

const { proxy } = getCurrentInstance()

const props = defineProps({
  visible: { type: Boolean, default: false },
  processNumber: { type: String, default: '' }
})
const emit = defineEmits(['update:visible'])

const drawerVisible = ref(false)
const summary = ref(null)
const nodes = ref([])
const nodesLoading = ref(false)
const selectedTaskDefKey = ref('')
const detail = ref(null)
const detailLoading = ref(false)

watch(
  () => props.visible,
  (val) => {
    drawerVisible.value = val
    if (val && props.processNumber) {
      loadAll()
    }
    if (!val) {
      // 关闭时重置
      summary.value = null
      nodes.value = []
      selectedTaskDefKey.value = ''
      detail.value = null
    }
  }
)

watch(
  () => props.processNumber,
  (val) => {
    if (val && drawerVisible.value) {
      loadAll()
    }
  }
)

watch(drawerVisible, (val) => {
  if (!val) {
    emit('update:visible', false)
  }
})

const stateTagType = computed(() => {
  if (!summary.value) return ''
  const s = summary.value.processState
  if (s === 2) return 'primary'
  if (s === 6) return 'danger'
  if (s === 3) return 'info'
  return 'success'
})

async function loadAll() {
  selectedTaskDefKey.value = ''
  detail.value = null
  await Promise.all([loadSummary(), loadNodes()])
}

async function loadSummary() {
  try {
    const res = await getInstanceEfficiencySummary(props.processNumber)
    if (res.code === 200) {
      summary.value = res.data
    }
  } catch (e) {
    console.error('加载汇总失败', e)
  }
}

async function loadNodes() {
  nodesLoading.value = true
  try {
    const res = await getInstanceEfficiencyNodes(props.processNumber)
    if (res.code === 200) {
      nodes.value = res.data || []
    }
  } catch (e) {
    console.error('加载节点列表失败', e)
  } finally {
    nodesLoading.value = false
  }
}

async function handleSelectNode(node) {
  selectedTaskDefKey.value = node.taskDefKey
  detail.value = null
  detailLoading.value = true
  try {
    const res = await getInstanceEfficiencyNodeDetail(props.processNumber, node.taskDefKey)
    if (res.code === 200) {
      detail.value = res.data
    }
  } catch (e) {
    console.error('加载节点详情失败', e)
  } finally {
    detailLoading.value = false
  }
}

function handleRefresh() {
  loadAll()
}

function closeDrawer() {
  drawerVisible.value = false
  emit('update:visible', false)
}
</script>

<style scoped>
.eff-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 4px;
}
.eff-header-left {
  display: flex;
  align-items: center;
}
.eff-title {
  font-weight: bold;
  font-size: 16px;
}
.eff-meta {
  margin-left: 16px;
  color: #909399;
  font-size: 13px;
}
.eff-header-right {
  display: flex;
  align-items: center;
  gap: 8px;
}
.eff-total {
  display: flex;
  flex-direction: column;
  align-items: center;
  margin-right: 8px;
}
.eff-total-label {
  font-size: 12px;
  color: #909399;
}
.eff-total-value {
  font-size: 18px;
  font-weight: bold;
  color: #e6a23c;
}
.eff-body {
  padding: 0 4px;
}
.eff-panel-title {
  font-weight: bold;
  font-size: 14px;
  margin-bottom: 8px;
  color: #303133;
}
.eff-node-card {
  padding: 10px 12px;
  margin-bottom: 8px;
  border: 1px solid #e4e7ed;
  border-radius: 6px;
  cursor: pointer;
  transition: all 0.2s;
  background: #fff;
}
.eff-node-card:hover {
  border-color: #409eff;
  box-shadow: 0 2px 8px rgba(64, 158, 255, 0.15);
}
.eff-node-active {
  border-color: #409eff;
  background: #ecf5ff;
}
.eff-node-top {
  background: #fef0f0;
  border-color: #f56c6c;
}
.eff-node-top.eff-node-active {
  background: #fde2e2;
}
.eff-node-progress {
  border-left: 3px solid #67c23a;
}
.eff-node-header {
  display: flex;
  align-items: center;
  gap: 6px;
  margin-bottom: 4px;
}
.eff-node-order {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 22px;
  height: 22px;
  border-radius: 50%;
  background: #f0f2f5;
  font-size: 12px;
  color: #606266;
  flex-shrink: 0;
}
.eff-node-name {
  font-weight: 600;
  flex: 1;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}
.eff-node-duration {
  font-size: 18px;
  font-weight: bold;
  color: #303133;
  margin-left: 28px;
}
.eff-node-type {
  font-size: 12px;
  color: #909399;
  margin-left: 28px;
  margin-top: 2px;
}
.eff-detail-empty {
  height: 100%;
  min-height: 400px;
}
.eff-duration-warn {
  color: #e6a23c;
  font-weight: 600;
}
</style>
