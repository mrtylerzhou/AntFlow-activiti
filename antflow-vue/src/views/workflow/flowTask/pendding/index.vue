<template>
   <div class="app-container">
      <div class="query-box">
         <el-form :model="taskMgmtVO" ref="queryRef" :inline="true" v-show="showSearch">
            <el-form-item label="流程编号" prop="processNumber">
               <el-input v-model="taskMgmtVO.processNumber" placeholder="请输入关键字" clearable style="width: 200px"
                  @keyup.enter="handleQuery" />
            </el-form-item>
            <el-form-item label="流程名称" prop="description">
               <el-input v-model="taskMgmtVO.description" placeholder="请输入关键字" clearable style="width: 200px"
                  @keyup.enter="handleQuery" />
            </el-form-item>
            <el-form-item>
               <el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button>
               <el-button icon="Refresh" @click="resetQuery">重置</el-button>
            </el-form-item>
         </el-form>
         <el-row :gutter="10" class="mb8" align="middle">
            <el-col :span="24" style="display: flex; align-items: center; gap: 8px;">
               <el-tooltip :content="batchMode ? '退出批量模式' : '批量模式'" placement="top">
                  <el-button :type="batchMode ? 'warning' : 'info'" :icon="batchMode ? 'Close' : 'Grid'" circle
                     @click="toggleBatchMode" />
               </el-tooltip>
               <el-button v-if="batchMode && selectedRows.length > 0" type="success" icon="Check"
                  @click="openBatchAgreeDialog" style="padding: 8px 15px;">
                  批量同意 ({{ selectedRows.length }})
               </el-button>
               <span v-if="batchMode" style="font-size: 12px; color: #909399;">已选 {{ selectedRows.length }} 项</span>
            </el-col>
         </el-row>
      </div>
      <div class="table-box">
         <el-table v-loading="loading" :data="dataList" ref="batchTableRef" row-key="taskId"
            @selection-change="handleSelectionChange">
            <el-table-column v-if="batchMode" type="selection" width="50" align="center" :selectable="isRowSelectable" />
            <el-table-column label="流程类型" align="center" prop="processKey" :show-overflow-tooltip="true">
               <template #default="item"> {{ substringHidden(item.row.processKey) }}
                  <el-tooltip v-if="item.row.isOutSideProcess" content="外部(第三方)业务方表单接入流程引擎" placement="top">
                     <el-tag type="warning" round>OUT</el-tag>
                  </el-tooltip>
               </template>
            </el-table-column>
            <el-table-column label="流程编号" align="center" prop="processNumber" :show-overflow-tooltip="true">
               <template #default="item">
                  <el-tooltip class="box-item" effect="dark" placement="right">
                     <template #content>
                        <span>{{ item.row.processNumber }}</span>
                     </template>
                     {{ substringHidden(item.row.processNumber) }}
                  </el-tooltip>
               </template>
            </el-table-column>
            <el-table-column label="流程描述" align="center" prop="description" :show-overflow-tooltip="true" />
            <el-table-column label="状态" align="center" prop="effectiveStatus">
               <template #default="item">
                  <el-tag>{{ item.row.taskState }}</el-tag>
               </template>
            </el-table-column>
            <el-table-column label="创建时间" align="center" prop="createTime" width="160">
               <template #default="scope">
                  <span>{{ parseTime(scope.row.createTime, '{y}-{m}-{d} {h}:{i}') }}</span>
               </template>
            </el-table-column>
            <el-table-column label="更新时间" align="center" prop="runTime" width="160">
               <template #default="scope">
                  <span>{{ parseTime(scope.row.runTime, '{y}-{m}-{d} {h}:{i}') }}</span>
               </template>
            </el-table-column>
            <el-table-column label="操作" fixed="right" align="center" class-name="small-padding fixed-width">
               <template #default="scope">
                  <el-button link type="primary" icon="Edit" @click="handleApproveBtn(scope.row)">审批</el-button>
               </template>
            </el-table-column>
         </el-table>
         <pagination v-show="total > 0" :total="total" v-model:page="pageDto.page" v-model:limit="pageDto.pageSize"
            @pagination="getList" />
      </div>

      <!-- 批量同意弹窗 -->
      <el-dialog title="批量同意" v-model="batchDialogVisible" width="500px" append-to-body>
         <el-form label-position="top" style="margin: 0 20px;">
            <el-form-item label="审批意见">
               <el-input v-model="batchAgreeComment" type="textarea" placeholder="请输入审批意见" :maxlength="100"
                  show-word-limit :autosize="{ minRows: 4, maxRows: 4 }" />
            </el-form-item>
            <div>
               <el-button type="primary" plain round v-for="txt in quickAnswers" :key="txt"
                  @click="batchAgreeComment = txt" style="margin: 0 5px 5px 0;">
                  {{ txt }}
               </el-button>
            </div>
         </el-form>
         <template #footer>
            <el-button @click="batchDialogVisible = false" style="padding: 8px 20px;">取 消</el-button>
            <el-button type="primary" :loading="batchSubmitting" @click="submitBatchAgree"
               style="padding: 8px 20px;">确 定</el-button>
         </template>
      </el-dialog>

      <!-- 批量同意结果弹窗 -->
      <el-dialog title="批量同意结果" v-model="batchResultVisible" width="550px" append-to-body>
         <el-result v-if="!batchResult || batchResult.failures.length === 0" icon="success"
            :title="`全部同意成功 (${batchResult?.successCount || 0} 条)`" />
         <div v-else>
            <el-alert :title="`成功 ${batchResult.successCount} 条，失败 ${batchResult.failures.length} 条`"
               type="warning" show-icon :closable="false" style="margin-bottom: 10px;" />
            <el-table :data="batchResult.failures" max-height="300" size="small">
               <el-table-column label="流程编号" prop="processNumber" :show-overflow-tooltip="true" />
               <el-table-column label="流程名称" prop="processName" :show-overflow-tooltip="true" />
               <el-table-column label="失败原因" prop="reason" :show-overflow-tooltip="true" />
            </el-table>
         </div>
         <template #footer>
            <el-button type="primary" @click="batchResultVisible = false">确 定</el-button>
         </template>
      </el-dialog>

      <!-- 单条审批弹窗(批量模式下) -->
      <el-dialog title="审批" v-model="singleApproveVisible" width="80%" top="5vh" append-to-body destroy-on-close>
         <div v-if="singleApproveConfig" style="min-height: 400px;">
            <ApporveForm :approveFormData="singleApproveConfig" @handleRefreshList="onSingleApproveDone" />
         </div>
      </el-dialog>
   </div>
</template>

<script setup>
import { getPenddinglistPage, batchAgree } from "@/api/workflow/index";
import ApporveForm from "./components/approveForm.vue";
import { useStore } from '@/store/modules/workflow';
const { proxy } = getCurrentInstance();
let store = useStore();
let { setPreviewDrawerConfig } = store;
const dataList = ref([]);
const loading = ref(true);
const showSearch = ref(true);
const total = ref(0);

// ========== 批量模式 ==========
const batchMode = ref(false);
const selectedRows = ref([]);
const batchTableRef = ref(null);
const batchDialogVisible = ref(false);
const batchAgreeComment = ref('同意');
const batchSubmitting = ref(false);
const batchResultVisible = ref(false);
const batchResult = ref(null);
const singleApproveVisible = ref(false);
const singleApproveConfig = ref(null);
const quickAnswers = ["同意", "好的", "OK", "通过", "已核实"];

const data = reactive({
   form: {},
   pageDto: {
      page: 1,
      pageSize: 10
   },
   taskMgmtVO: {
      processNumber: undefined,
      processTypeName: undefined
   },
   rules: {
      bpmnCode: [{ required: true, message: "关键字不能为空", trigger: "blur" }],
      bpmnName: [{ required: true, message: "关键字不能为空", trigger: "blur" }],
   }
});
const { pageDto, taskMgmtVO } = toRefs(data);

/** 查询待办列表 */
async function getList() {
   loading.value = true;
   await getPenddinglistPage(pageDto.value, taskMgmtVO.value).then(response => {
      dataList.value = response.data;
      total.value = response.pagination.totalCount;
      loading.value = false;
   }).catch((r) => {
      loading.value = false;
      console.log(r);
      proxy.$modal.msgError("加载列表失败:" + r.message);
   });
}

/** 搜索按钮操作 */
function handleQuery() {
   pageDto.value.page = 1;
   getList();
}

function resetQuery() {
   taskMgmtVO.value = {
      processNumber: undefined,
      processTypeName: undefined
   };
   handleQuery();
}

/** 审批按钮(普通模式跳转,批量模式弹窗) */
function handleApproveBtn(row) {
   if (batchMode.value) {
      openSingleApprove(row);
   } else {
      const params = {
         formCode: row.processKey,
         processNumber: row.processNumber,
         taskId: row.taskId,
         isOutSideAccess: row.isOutSideProcess,
         isLowCodeFlow: row.isLowCodeFlow,
      };
      const obj = { path: "pendding/approve", query: params };
      proxy.$tab.closeOpenPage(obj);
   }
}

// ========== 批量模式方法 ==========
function toggleBatchMode() {
   batchMode.value = !batchMode.value;
   selectedRows.value = [];
}

function isRowSelectable(row) {
   return row.isBatchAgree !== false;
}

function handleSelectionChange(selection) {
   selectedRows.value = selection;
}

function openBatchAgreeDialog() {
   batchAgreeComment.value = '同意';
   batchDialogVisible.value = true;
}

async function submitBatchAgree() {
   if (!batchAgreeComment.value || !batchAgreeComment.value.trim()) {
      proxy.$modal.msgWarning("请输入审批意见");
      return;
   }
   batchSubmitting.value = true;
   try {
      const taskIds = selectedRows.value.map(row => row.taskId);
      const res = await batchAgree({ taskIds, batchApprovalComment: batchAgreeComment.value.trim() });
      batchDialogVisible.value = false;
      const result = res.data;
      batchResult.value = result;
      batchResultVisible.value = true;
      // 刷新列表,留在批量模式
      selectedRows.value = [];
      await getList();
   } catch (e) {
      proxy.$modal.msgError("批量同意失败: " + (e.message || '未知错误'));
   } finally {
      batchSubmitting.value = false;
   }
}

function openSingleApprove(row) {
   singleApproveConfig.value = {
      formCode: row.processKey,
      processNumber: row.processNumber,
      taskId: row.taskId,
      isOutSideAccess: row.isOutSideProcess,
      isLowCodeFlow: row.isLowCodeFlow,
   };
   setPreviewDrawerConfig({ ...singleApproveConfig.value });
   singleApproveVisible.value = true;
}

async function onSingleApproveDone() {
   singleApproveVisible.value = false;
   singleApproveConfig.value = null;
   selectedRows.value = [];
   await getList();
}

getList();
</script>
<template>
   <div class="app-container">
      <div class="query-box">
         <el-form :model="taskMgmtVO" ref="queryRef" :inline="true" v-show="showSearch">
            <el-form-item label="流程编号" prop="processNumber">
               <el-input v-model="taskMgmtVO.processNumber" placeholder="请输入关键字" clearable style="width: 200px"
                  @keyup.enter="handleQuery" />
            </el-form-item>
            <el-form-item label="流程名称" prop="description">
               <el-input v-model="taskMgmtVO.description" placeholder="请输入关键字" clearable style="width: 200px"
                  @keyup.enter="handleQuery" />
            </el-form-item>
            <el-form-item>
               <el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button>
               <el-button icon="Refresh" @click="resetQuery">重置</el-button>
            </el-form-item>
         </el-form>
      </div>
      <div class="table-box">
         <el-table v-loading="loading" :data="dataList">
            <el-table-column label="流程类型" align="center" prop="processKey" :show-overflow-tooltip="true">
               <template #default="item"> {{ substringHidden(item.row.processKey) }}
                  <el-tooltip v-if="item.row.isOutSideProcess" content="外部(第三方)业务方表单接入流程引擎" placement="top">
                     <el-tag type="warning" round>OUT</el-tag>
                  </el-tooltip>
               </template>
            </el-table-column>
            <el-table-column label="流程编号" align="center" prop="processNumber" :show-overflow-tooltip="true">
               <template #default="item">
                  <el-tooltip class="box-item" effect="dark" placement="right">
                     <template #content>
                        <span>{{ item.row.processNumber }}</span>
                     </template>
                     {{ substringHidden(item.row.processNumber) }}
                  </el-tooltip>
               </template>
            </el-table-column>
            <el-table-column label="流程描述" align="center" prop="description" :show-overflow-tooltip="true" />
            <el-table-column label="状态" align="center" prop="effectiveStatus">
               <template #default="item">
                  <el-tag>{{ item.row.taskState }}</el-tag>
               </template>
            </el-table-column>
            <el-table-column label="创建时间" align="center" prop="createTime" width="160">
               <template #default="scope">
                  <span>{{ parseTime(scope.row.createTime, '{y}-{m}-{d} {h}:{i}') }}</span>
               </template>
            </el-table-column>
            <el-table-column label="更新时间" align="center" prop="runTime" width="160">
               <template #default="scope">
                  <span>{{ parseTime(scope.row.runTime, '{y}-{m}-{d} {h}:{i}') }}</span>
               </template>
            </el-table-column>
            <el-table-column label="操作" fixed="right" align="center" class-name="small-padding fixed-width">
               <template #default="scope">
                  <el-button link type="primary" icon="Edit" @click="handleApproveBtn(scope.row)">审批</el-button>
               </template>
            </el-table-column>
         </el-table>
         <pagination v-show="total > 0" :total="total" v-model:page="pageDto.page" v-model:limit="pageDto.pageSize"
            @pagination="getList" />
      </div>
   </div>
</template>

<script setup>
import { getPenddinglistPage } from "@/api/workflow/index";
const { proxy } = getCurrentInstance();
const dataList = ref([]);
const loading = ref(true);
const showSearch = ref(true);
const total = ref(0);

const data = reactive({
   form: {},
   pageDto: {
      page: 1,
      pageSize: 10
   },
   taskMgmtVO: {
      processNumber: undefined,
      processTypeName: undefined
   },
   rules: {
      bpmnCode: [{ required: true, message: "关键字不能为空", trigger: "blur" }],
      bpmnName: [{ required: true, message: "关键字不能为空", trigger: "blur" }],
   }
});
const { pageDto, taskMgmtVO } = toRefs(data);

/** 查询岗位列表 */
async function getList() {
   loading.value = true;
   await getPenddinglistPage(pageDto.value, taskMgmtVO.value).then(response => {
      dataList.value = response.data;
      total.value = response.pagination.totalCount;
      loading.value = false;
   }).catch((r) => {
      loading.value = false;
      console.log(r);
      proxy.$modal.msgError("加载列表失败:" + r.message);
   });
}

/** 搜索按钮操作 */
function handleQuery() {
   pageDto.value.page = 1;
   getList();
}
/** 修改按钮操作 */
function handleApproveBtn(row) {
   const params = {
      formCode: row.processKey,
      processNumber: row.processNumber,
      taskId: row.taskId,
      isOutSideAccess: row.isOutSideProcess,
      isLowCodeFlow: row.isLowCodeFlow,
   };
   // 关闭指定页签
   const obj = { path: "pendding/approve", query: params };
   proxy.$tab.closeOpenPage(obj);
}
function resetQuery() {
   taskMgmtVO.value = {
      processNumber: undefined,
      processTypeName: undefined
   };
   handleQuery();
}
getList();
</script>
