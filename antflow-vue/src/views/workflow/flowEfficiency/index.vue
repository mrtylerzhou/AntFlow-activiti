<template>
  <div class="app-container">
    <!-- 筛选区域 -->
    <div class="query-box">
      <el-form :model="queryParams" ref="queryRef" :inline="true">
        <el-form-item label="流程类型" prop="formCode">
          <el-input v-model="queryParams.formCode" placeholder="流程类型编码" clearable style="width: 160px"
            @keyup.enter="handleQuery" />
        </el-form-item>
        <el-form-item label="流程编号" prop="processNumber">
          <el-input v-model="queryParams.processNumber" placeholder="流程编号" clearable style="width: 160px"
            @keyup.enter="handleQuery" />
        </el-form-item>
        <el-form-item label="审批人" prop="assignee">
          <el-input v-model="queryParams.assignee" placeholder="审批人" clearable style="width: 120px"
            @keyup.enter="handleQuery" />
        </el-form-item>
        <el-form-item label="流程状态" prop="processState">
          <el-select v-model="queryParams.processState" placeholder="全部" clearable style="width: 120px">
            <el-option label="审批中" :value="1" />
            <el-option label="审批通过" :value="2" />
            <el-option label="作废" :value="3" />
            <el-option label="审批拒绝" :value="6" />
          </el-select>
        </el-form-item>
        <el-form-item label="创建时间" prop="timeRange">
          <el-date-picker v-model="timeRange" type="daterange" range-separator="-" start-placeholder="开始"
            end-placeholder="结束" value-format="YYYY-MM-DD" style="width: 240px" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button>
          <el-button icon="Refresh" @click="resetQuery">重置</el-button>
        </el-form-item>
      </el-form>
    </div>

    <!-- 数据表格 -->
    <div class="table-box">
      <el-table v-loading="loading" :data="tableData" row-key="id" border default-expand-all=false
        :tree-props="{ children: 'children', hasChildren: 'hasChildren' }" @expand-change="handleExpand"
        height="65vh">
        <el-table-column type="expand">
          <template #default="props">
            <div style="padding: 0 48px;">
              <el-table v-loading="props.row._nodeLoading" :data="props.row._nodes || []" row-key="id" border
                @expand-change="(row, expanded) => handleNodeExpand(row, expanded, props.row)">
                <el-table-column type="expand">
                  <template #default="nodeProps">
                    <div style="padding: 0 48px;">
                      <el-table v-loading="nodeProps.row._taskLoading" :data="nodeProps.row._tasks || []" border>
                        <el-table-column label="审批人" prop="assigneeName" min-width="100" />
                        <el-table-column label="开始时间" prop="startTime" min-width="160">
                          <template #default="scope">
                            {{ parseTime(scope.row.startTime) }}
                          </template>
                        </el-table-column>
                        <el-table-column label="结束时间" prop="endTime" min-width="160">
                          <template #default="scope">
                            {{ scope.row.endTime ? parseTime(scope.row.endTime) : '进行中' }}
                          </template>
                        </el-table-column>
                        <el-table-column label="耗时" min-width="140">
                          <template #default="scope">
                            <el-tag type="info" size="small">{{ formatDuration(scope.row.duration) }}</el-tag>
                          </template>
                        </el-table-column>
                      </el-table>
                    </div>
                  </template>
                </el-table-column>
                <el-table-column label="节点名称" prop="nodeName" min-width="140" />
                <el-table-column label="审批人" prop="assigneeName" min-width="140" :show-overflow-tooltip="true" />
                <el-table-column label="开始时间" prop="startTime" min-width="160">
                  <template #default="scope">
                    {{ parseTime(scope.row.startTime) }}
                  </template>
                </el-table-column>
                <el-table-column label="结束时间" prop="endTime" min-width="160">
                  <template #default="scope">
                    {{ scope.row.endTime ? parseTime(scope.row.endTime) : '进行中' }}
                  </template>
                </el-table-column>
                <el-table-column label="耗时" min-width="140">
                  <template #default="scope">
                    <el-tag type="warning" size="small">{{ formatDuration(scope.row.duration) }}</el-tag>
                  </template>
                </el-table-column>
              </el-table>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="流程编号" prop="processNumber" min-width="180" :show-overflow-tooltip="true" />
        <el-table-column label="流程状态" min-width="100" align="center">
          <template #default="scope">
            <el-tag :type="stateTagType(scope.row.processState)">{{ stateText(scope.row.processState) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="创建时间" prop="startTime" min-width="160">
          <template #default="scope">
            {{ parseTime(scope.row.startTime) }}
          </template>
        </el-table-column>
        <el-table-column label="结束时间" prop="endTime" min-width="160">
          <template #default="scope">
            {{ scope.row.endTime ? parseTime(scope.row.endTime) : '进行中' }}
          </template>
        </el-table-column>
        <el-table-column label="总耗时" min-width="160">
          <template #default="scope">
            <el-tag type="danger">{{ formatDuration(scope.row.duration) }}</el-tag>
          </template>
        </el-table-column>
      </el-table>
      <pagination v-show="total > 0" :total="total" v-model:page="pageDto.page" v-model:limit="pageDto.pageSize"
        @pagination="getList" />
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from "vue";
import { useRoute } from "vue-router";
import { getEfficiencyPage, getEfficiencyNodes, getEfficiencyTasks } from "@/api/workflow/efficiencyApi";

const { proxy } = getCurrentInstance();
const route = useRoute();

const loading = ref(false);
const tableData = ref([]);
const total = ref(0);
const timeRange = ref(null);

const pageDto = reactive({ page: 1, pageSize: 10 });
const queryParams = reactive({
  formCode: undefined,
  processNumber: undefined,
  assignee: undefined,
  processState: undefined,
});

onMounted(() => {
  if (route.query.formCode) {
    queryParams.formCode = route.query.formCode;
  }
  getList();
});

function getList() {
  loading.value = true;
  const params = {
    pageDto: { page: pageDto.page, pageSize: pageDto.pageSize },
    formCode: queryParams.formCode || undefined,
    processNumber: queryParams.processNumber || undefined,
    assignee: queryParams.assignee || undefined,
    processState: queryParams.processState,
  };
  if (timeRange.value && timeRange.value.length === 2) {
    params.startTimeBegin = timeRange.value[0] + " 00:00:00";
    params.startTimeEnd = timeRange.value[1] + " 23:59:59";
  }
  getEfficiencyPage(params).then((res) => {
    const data = res.data;
    tableData.value = (data.data || []).map((item) => ({
      ...item,
      _nodes: null,
      _nodeLoading: false,
      hasChildren: true,
    }));
    total.value = data.pagination?.totalCount || 0;
    loading.value = false;
  }).catch(() => {
    loading.value = false;
  });
}

/** 展开流程行 -> 加载节点级 */
function handleExpand(row, expandedRows) {
  const isExpanded = expandedRows.some((r) => r.id === row.id);
  if (!isExpanded || row._nodes) return;
  row._nodeLoading = true;
  getEfficiencyNodes(row.procInstId).then((res) => {
    row._nodes = (res.data || []).map((item) => ({
      ...item,
      _tasks: null,
      _taskLoading: false,
    }));
    row._nodeLoading = false;
  }).catch(() => {
    row._nodeLoading = false;
  });
}

/** 展开节点行 -> 加载任务级 */
function handleNodeExpand(nodeRow, expandedRows, parentRow) {
  const isExpanded = (expandedRows || []).some((r) => r.id === nodeRow.id);
  if (!isExpanded || nodeRow._tasks) return;
  nodeRow._taskLoading = true;
  getEfficiencyTasks(nodeRow.procInstId, nodeRow.taskDefKey).then((res) => {
    nodeRow._tasks = res.data || [];
    nodeRow._taskLoading = false;
  }).catch(() => {
    nodeRow._taskLoading = false;
  });
}

/** 格式化耗时 */
function formatDuration(ms) {
  if (ms == null) return "-";
  if (ms < 60000) return "小于1min";
  const minutes = Math.floor(ms / 60000);
  const days = Math.floor(minutes / 1440);
  const hours = Math.floor((minutes % 1440) / 60);
  const mins = minutes % 60;
  let result = "";
  if (days > 0) result += days + "天";
  if (hours > 0) result += hours + "小时";
  if (mins > 0) result += mins + "分";
  return result || "小于1min";
}

function stateText(state) {
  const map = { 1: "审批中", 2: "审批通过", 3: "作废", 6: "审批拒绝" };
  return map[state] || "未知";
}

function stateTagType(state) {
  const map = { 1: "warning", 2: "success", 3: "info", 6: "danger" };
  return map[state] || "info";
}

function handleQuery() {
  pageDto.page = 1;
  getList();
}

function resetQuery() {
  queryParams.formCode = undefined;
  queryParams.processNumber = undefined;
  queryParams.assignee = undefined;
  queryParams.processState = undefined;
  timeRange.value = null;
  handleQuery();
}
</script>
