<template>
  <div class="app-container">
    <!-- 搜索区 -->
    <el-card shadow="never" class="mb10">
      <template #header><span style="font-weight:bold">流程千里眼</span>
        <el-tag type="info" size="small" class="ml10">搜索运行中流程的当前/未来审批人</el-tag>
      </template>
      <el-form :inline="true" size="default">
        <el-form-item label="审批人">
          <div class="user-tags">
            <el-tag v-for="u in selectedUsers" :key="u.id" closable @close="removeUser(u)" class="user-tag">
              {{ u.name }}
            </el-tag>
            <el-button size="small" type="primary" plain @click="openUserDialog" :disabled="selectedUsers.length >= 5">
              + 选择人员 ({{ selectedUsers.length }}/5)
            </el-button>
          </div>
        </el-form-item>
        <el-form-item label="时间范围">
          <el-select v-model="timeRange" style="width:140px">
            <el-option label="最近1天" :value="1" />
            <el-option label="最近3天" :value="3" />
            <el-option label="最近5天" :value="5" />
            <el-option label="最近7天" :value="7" />
            <el-option label="最近半个月" :value="15" />
            <el-option label="最近1个月" :value="30" />
            <el-option label="最近半年" :value="180" />
          </el-select>
        </el-form-item>
        <el-form-item label="节点范围">
          <el-select v-model="nodeScope" style="width:160px">
            <el-option label="仅当前节点" value="CURRENT" />
            <el-option label="仅未来节点" value="FUTURE" />
            <el-option label="当前+未来节点" value="CURRENT_FUTURE" />
            <el-option label="全部节点" value="ALL" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" icon="Search" :disabled="!canSearch || searching" @click="startSearch">搜索</el-button>
          <el-button type="danger" icon="VideoPause" v-if="searching" @click="stopSearch">停止</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 进度区 -->
    <div v-if="searching || scannedTotal > 0" class="mb10 progress-info">
      <el-icon class="is-loading" v-if="searching"><Loading /></el-icon>
      <span v-if="searching">正在扫描中... 已扫描 {{ scannedTotal }} 条流程</span>
      <span v-else>扫描完成，共扫描 {{ scannedTotal }} 条流程，命中 {{ resultList.length }} 条</span>
    </div>

    <!-- 结果区 -->
    <el-card shadow="never" v-if="resultList.length > 0">
      <template #header><span style="font-weight:bold">搜索结果</span>
        <el-tag type="success" size="small" class="ml10">{{ resultList.length }} 条命中</el-tag>
      </template>
      <el-table :data="resultList" border stripe>
        <el-table-column label="流程类型" align="center" prop="processKey" :show-overflow-tooltip="true" width="120" />
        <el-table-column label="流程编号" align="center" prop="processNumber" :show-overflow-tooltip="true" min-width="180" />
        <el-table-column label="发起人" align="center" prop="userName" width="100" />
        <el-table-column label="创建时间" align="center" prop="createTime" width="170">
          <template #default="{ row }">{{ formatTime(row.createTime) }}</template>
        </el-table-column>
        <el-table-column label="命中摘要" align="center" width="160">
          <template #default="{ row }">
            <el-link type="primary" @click="showDetail(row)">
              {{ row.matchedNodeCount }} 个节点 / {{ row.matchedPersonCount }} 人
            </el-link>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 空结果 -->
    <el-empty v-if="searchDone && resultList.length === 0" description="未找到匹配的流程" />

    <!-- 人员选择弹窗 -->
    <selectUserDialog v-model:visible="userDialogVisible" :data="[]" @change="onUserSelected" />

    <!-- 详情弹窗 -->
    <el-dialog v-model="detailVisible" title="命中节点详情" width="600px" append-to-body>
      <div v-if="detailRow">
        <p class="detail-title">流程编号：{{ detailRow.processNumber }}</p>
        <el-table :data="detailRow.matchedNodes" border size="small">
          <el-table-column label="节点名称" prop="elementName" min-width="120" />
          <el-table-column label="节点ID" prop="elementId" min-width="160" :show-overflow-tooltip="true" />
          <el-table-column label="命中审批人" min-width="180">
            <template #default="{ row }">
              <el-tag v-for="p in row.matchedPersons" :key="p.assignee" size="small" class="person-tag">
                {{ p.assigneeName }} ({{ p.assignee }})
              </el-tag>
            </template>
          </el-table-column>
        </el-table>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed } from "vue";
import { Loading } from "@element-plus/icons-vue";
import { flowClairvoyanceSearch } from "@/api/workflow/flowClairvoyance";
import selectUserDialog from "@/components/Workflow/dialog/selectUserDialog.vue";

const { proxy } = getCurrentInstance();

// === 搜索条件 ===
const selectedUsers = ref([]);
const timeRange = ref(1);
const nodeScope = ref("CURRENT_FUTURE");
const userDialogVisible = ref(false);

// === 搜索状态 ===
const searching = ref(false);
const searchDone = ref(false);
const currentOffset = ref(0);
const scannedTotal = ref(0);
const resultList = ref([]);
let stopped = false;

// === 详情弹窗 ===
const detailVisible = ref(false);
const detailRow = ref(null);

const canSearch = computed(() => selectedUsers.value.length > 0);

// === 人员选择 ===
function openUserDialog() {
  if (selectedUsers.value.length >= 5) {
    proxy.$modal.msgWarning("最多选择5个审批人");
    return;
  }
  userDialogVisible.value = true;
}

function onUserSelected(data) {
  // data: [{type:1, targetId: "123", name: "张三"}]
  for (const item of data) {
    if (selectedUsers.value.length >= 5) break;
    if (!selectedUsers.value.some(u => u.id === String(item.targetId))) {
      selectedUsers.value.push({ id: String(item.targetId), name: item.name });
    }
  }
  userDialogVisible.value = false;
}

function removeUser(u) {
  selectedUsers.value = selectedUsers.value.filter(item => item.id !== u.id);
}

// === 搜索逻辑 ===
async function startSearch() {
  if (!canSearch.value) return;
  // 重置状态
  resultList.value = [];
  scannedTotal.value = 0;
  currentOffset.value = 0;
  searchDone.value = false;
  stopped = false;
  searching.value = true;

  await doSearch();
}

async function doSearch() {
  if (stopped) {
    searching.value = false;
    return;
  }

  try {
    const res = await flowClairvoyanceSearch({
      userIds: selectedUsers.value.map(u => u.id),
      timeRange: timeRange.value,
      nodeScope: nodeScope.value,
      offset: currentOffset.value
    });

    const data = res.data;
    scannedTotal.value += data.scannedCount || 0;

    // 追加结果
    if (data.results && data.results.length > 0) {
      resultList.value.push(...data.results);
    }

    if (data.hasMore) {
      currentOffset.value = data.nextOffset;
      // 0结果时自动继续
      if (!data.results || data.results.length === 0) {
        await doSearch();
        return;
      }
      // 有结果时停止自动续扫，等用户操作（或可改为也自动继续）
      // 按设计：有结果也自动继续直到完成或用户停止
      await doSearch();
    } else {
      // 搜索完成
      searching.value = false;
      searchDone.value = true;
    }
  } catch (e) {
    searching.value = false;
    searchDone.value = true;
    proxy.$modal.msgError("搜索失败: " + (e.message || e));
  }
}

function stopSearch() {
  stopped = true;
  searching.value = false;
  searchDone.value = true;
}

// === 详情 ===
function showDetail(row) {
  detailRow.value = row;
  detailVisible.value = true;
}

// === 工具 ===
function formatTime(t) {
  if (!t) return "";
  return t.replace("T", " ").substring(0, 19);
}
</script>

<style scoped>
.user-tags {
  display: flex;
  align-items: center;
  flex-wrap: wrap;
  gap: 4px;
}
.user-tag {
  margin-right: 2px;
}
.progress-info {
  display: flex;
  align-items: center;
  gap: 6px;
  color: #409eff;
  font-size: 14px;
}
.detail-title {
  margin-bottom: 12px;
  font-weight: bold;
}
.person-tag {
  margin: 2px 4px 2px 0;
}
</style>
