<template>
    <el-container class="layout-setup" style="height: calc(100vh - 84px);">
        <!-- 左侧列表区域 -->
        <el-aside class="layout-aside" :style="{ width: batchMode ? '100%' : '300px' }">
            <el-container>
                <el-header>
                    <div class="toolbar">
                        <el-input v-model="taskMgmtVO.processNumber" placeholder="请输入关键字" clearable
                            style="width: 225px">
                            <template #append>
                                <el-button icon="Search" @click="handleQuery" />
                            </template>
                        </el-input>
                        <el-badge v-if="total > 0" :value="total" :offset="[-10, 5]">
                            <el-button icon="Refresh" @click="getList"
                                style="width: 30px; height: 30px; margin: 0px 5px;" />
                        </el-badge>
                        <el-button v-else icon="Refresh" @click="getList"
                            style="width: 30px; height: 30px; margin: 0px 5px;" />
                        <el-tooltip :content="batchMode ? '退出批量模式' : '批量模式'" placement="top">
                            <el-button :type="batchMode ? 'warning' : 'info'" :icon="batchMode ? 'Close' : 'Grid'" circle
                                @click="toggleBatchMode" style="margin-left: 10px;" />
                        </el-tooltip>
                        <el-button v-if="batchMode && selectedRows.length > 0" type="success" icon="Check"
                            @click="openBatchAgreeDialog" style="margin-left: 8px; padding: 8px 15px;">
                            批量同意 ({{ selectedRows.length }})
                        </el-button>
                    </div>
                </el-header>
                <el-main>
                    <el-scrollbar>
                        <!-- 普通模式: 卡片列表 -->
                        <div v-if="!batchMode" v-loading="loading" class="list-flex-cards">
                            <span v-if="dataList.length === 0" class="empty-text">暂无待办任务</span>
                            <el-card v-if="dataList.length > 0" v-for="(item, index) in dataList" :key="item.id"
                                @click="toggleFlowActive(item, index)"
                                :class="['item-card', { active: activeIndex === index }]">
                                <div class="card-content pointer">
                                    <div>
                                        <p class="card-title">
                                            [{{ item.isLowCodeFlow ? 'LF' : 'DIY' }}] {{ item.processTypeName }}
                                        </p>
                                        <p class="card-detail">
                                            <span style="width: 40px;">描述：</span>
                                            <span class="card-reason">{{ item.description }}</span>
                                        </p>
                                        <p class="card-detail">
                                            <span>流程编号：</span>
                                            <span class="card-reason">{{ substringHidden(item.processNumber) }}</span>
                                        </p>
                                        <p class="card-time">
                                            <span>审批状态：</span>
                                            <span class="card-time-value">
                                                <el-tag type="primary">{{ item.taskState }}</el-tag>
                                            </span>
                                        </p>
                                        <p class="card-time">
                                            <span>发起时间：</span>
                                            <span class="card-time-value">
                                                {{ parseTime(item.createTime, '{y}-{m}-{d} {h}: {i}') }}</span>
                                        </p>
                                    </div>
                                    <div class="card-user">
                                        <span class="card-username">
                                            <el-avatar v-if="item.userName" :size="18">
                                                {{ item.userName.substring(0, 1) }}
                                            </el-avatar>
                                            {{ item.userName }}
                                        </span>
                                        <span class="card-user-runtime">
                                            {{ getDateDiff(item.runTime) }}
                                        </span>
                                    </div>
                                </div>
                            </el-card>
                            <pagination v-show="total > pageDto.pageSize" :total="total" v-model:page="pageDto.page"
                                :pagerCount="5" :layout="layoutSize" v-model:limit="pageDto.pageSize"
                                @pagination="getList" />
                        </div>
                        <!-- 批量模式: 表格 -->
                        <div v-else v-loading="loading" class="batch-table-wrap">
                            <el-table ref="batchTableRef" :data="dataList" @selection-change="handleSelectionChange"
                                row-key="taskId">
                                <el-table-column type="selection" width="50" align="center"
                                    :selectable="isRowSelectable" />
                                <el-table-column label="流程名称" align="center" prop="processTypeName"
                                    :show-overflow-tooltip="true">
                                    <template #default="{ row }">
                                        [{{ row.isLowCodeFlow ? 'LF' : 'DIY' }}] {{ row.processTypeName }}
                                    </template>
                                </el-table-column>
                                <el-table-column label="流程编号" align="center" prop="processNumber"
                                    :show-overflow-tooltip="true">
                                    <template #default="{ row }">
                                        {{ substringHidden(row.processNumber) }}
                                    </template>
                                </el-table-column>
                                <el-table-column label="描述" align="center" prop="description"
                                    :show-overflow-tooltip="true" />
                                <el-table-column label="发起人" align="center" prop="userName" width="100" />
                                <el-table-column label="状态" align="center" width="100">
                                    <template #default="{ row }">
                                        <el-tag type="primary">{{ row.taskState }}</el-tag>
                                    </template>
                                </el-table-column>
                                <el-table-column label="发起时间" align="center" width="150">
                                    <template #default="{ row }">
                                        {{ parseTime(row.createTime, '{y}-{m}-{d} {h}:{i}') }}
                                    </template>
                                </el-table-column>
                                <el-table-column label="操作" fixed="right" align="center" width="100">
                                    <template #default="{ row }">
                                        <el-button link type="primary" @click="openSingleApprove(row)">审批</el-button>
                                    </template>
                                </el-table-column>
                            </el-table>
                            <pagination v-show="total > pageDto.pageSize" :total="total" v-model:page="pageDto.page"
                                v-model:limit="pageDto.pageSize" @pagination="getList" />
                        </div>
                    </el-scrollbar>
                </el-main>
            </el-container>
        </el-aside>
        <!-- 右侧审批面板 (批量模式隐藏) -->
        <el-container v-if="!batchMode">
            <div class="layout-middle" id="fullscreen">
                <el-scrollbar>
                    <div style="min-width:650px;">
                        <el-empty v-if="!approveFormDataConfig || dataList.length === 0"
                            description="这里空空的,请点击左侧代办列表" />
                        <el-tabs v-else v-model="activeName" @tab-click="handleClick" class="content-tabs">
                            <el-tab-pane label="表单信息" name="baseTab">
                                <div v-if="activeName === 'baseTab'">
                                    <ApporveForm :approveFormData="approveFormDataConfig"
                                        @handleRefreshList="refreshList" />
                                </div>
                            </el-tab-pane>
                            <el-tab-pane label="审批记录" name="flowStep">
                                <div v-if="activeName === 'flowStep'">
                                    <FlowStepTable />
                                </div>
                            </el-tab-pane>
                            <el-tab-pane label="流程预览" name="flowReview">
                                <div v-if="activeName === 'flowReview'">
                                    <ReviewWarp />
                                </div>
                            </el-tab-pane>
                        </el-tabs>
                    </div>
                </el-scrollbar>
            </div>
        </el-container>

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
            <div v-if="singleApproveRow" style="min-height: 400px;">
                <ApporveForm :approveFormData="singleApproveConfig" @handleRefreshList="onSingleApproveDone" />
            </div>
        </el-dialog>
    </el-container>
</template>

<script setup>
import { ref } from 'vue';
import FlowStepTable from '@/components/Workflow/Preview/flowStepTable.vue';
import ReviewWarp from '@/components/Workflow/Preview/reviewWarp.vue';
import ApporveForm from "./components/approveForm.vue";
import { getPenddinglistPage, batchAgree } from "@/api/workflow/index";
import { getDateDiff } from "@/utils/antflow/hsharpUtils";
const { proxy } = getCurrentInstance();
import { useStore } from '@/store/modules/workflow';
let store = useStore();
let { setPreviewDrawerConfig } = store;
const activeIndex = ref(null);
const activeName = ref('baseTab');
const dataList = ref([]);
const loading = ref(true);
const total = ref(0);
const approveFormDataConfig = ref(null);

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
const singleApproveRow = ref(null);
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

const layoutSize = 'pager';
onMounted(async () => {
    await getList();
});
/** 搜索按钮操作 */
async function handleQuery() {
    pageDto.value.page = 1;
    await getList();
}
/** 查询代办列表 */
async function getList() {
    loading.value = true;
    await getPenddinglistPage(pageDto.value, taskMgmtVO.value).then(response => {
        dataList.value = response.data;
        total.value = response.pagination.totalCount;
        setTimeout(() => {
            loading.value = false;
        }, 300);
    }).catch((r) => {
        loading.value = false;
        console.log(r);
        proxy.$modal.msgError("加载列表失败:" + r.message);
    });
}
/**
 * 刷新列表
*/
const refreshList = async () => {
    await getList();
    if (!batchMode.value) {
        toggleFlowActive(dataList.value[0], 0);
    }
}
const toggleFlowActive = (data, index) => {
    if (proxy.isEmpty(data)) {
        return;
    }
    activeIndex.value = index;
    approveFormDataConfig.value = {
        ...approveFormDataConfig.value,
        formCode: data.processKey,
        processNumber: data.processNumber,
        taskId: data.taskId,
        isOutSideAccess: data.isOutSideProcess,
        isLowCodeFlow: data.isLowCodeFlow,
    };
    setPreviewDrawerConfig({ ...approveFormDataConfig.value });
    activeName.value = 'baseTab';
}

const handleClick = (tab, event) => {
    activeName.value = tab.paneName;
}

// ========== 批量模式方法 ==========
function toggleBatchMode() {
    batchMode.value = !batchMode.value;
    selectedRows.value = [];
    if (!batchMode.value) {
        // 退出批量模式,恢复选中第一条
        if (dataList.value.length > 0) {
            toggleFlowActive(dataList.value[0], 0);
        }
    }
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
    singleApproveRow.value = row;
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
    singleApproveRow.value = null;
    selectedRows.value = [];
    await getList();
}

window.onload = function () {
    var fullscreen = document.getElementById("fullscreen");
    if (fullscreen) {
        fullscreen.style.height = (window.innerHeight) + "px";
    }
}
</script>

<style lang="scss" scoped>
* {
    margin: 0;
    padding: 0;
    border: 0;
    outline: 0;
    vertical-align: baseline;
    font-size: 12px;
    line-height: 2.0;
}


.empty-text {
    display: block;
    text-align: center;
    width: 100%;
    color: #888;
    margin: 20px 0;
}

.layout-setup .el-aside {
    color: var(--el-text-color-primary);
    background: #cccccc59;
    transition: width 0.3s;
    overflow: hidden;
}

.layout-aside .el-header {
    position: relative;
    color: var(--el-text-color-primary);
    border-radius: 5px;
}

.layout-aside .el-footer {
    position: relative;
    background-color: #f2f3f4f5;
    color: var(--el-text-color-primary);
    border-radius: 5px;
    margin-top: 10px;
}

.layout-aside .toolbar {
    display: inline-flex;
    align-items: center;
    justify-content: center;
    height: 100%;
    right: 20px;
    margin: 0 5px;
}

.layout-middle {
    margin-right: 10px;
    margin-top: 10px;
    width: 100%;
    height: 90vh;
    background-color: #f2f3f4f5;
}

.layout-middle .content-tabs {
    padding: 10px;
    min-width: 680px;
    overflow: auto;
}

.list-flex-cards {
    display: flex;
    flex-wrap: wrap;
    gap: 5px;
    padding-top: 5px;
    padding-left: 5px;
    padding-right: 5px;
}

.item-card {
    width: 290px;
    box-shadow: var(--el-box-shadow-light);
}

.card-content {
    display: flex;
    flex-direction: column;
    border-radius: 20px;
    box-sizing: border-box;
    transition: box-shadow 0.2s;
}

.item-card:hover {
    border: 1px solid var(--current-color);
}

.active {
    border: 1px solid var(--current-color);
}

.card-title {
    font-size: 14px;
    font-weight: 600;
    color: #222;
    margin-bottom: 2px;
    letter-spacing: 1px;
    border-left: 3px solid var(--current-color);
    padding-left: 8px;
    max-width: 200px;
    white-space: nowrap;
    overflow: hidden;
    text-overflow: ellipsis;
    margin-left: 4px;
    display: block;
}

.card-detail {
    display: flex;
    align-items: center;
    color: #222;
    font-size: 12px;
}

.card-detail .card-reason {
    color: #222;
    font-weight: 500;
    max-width: 200px;
    white-space: nowrap;
    overflow: hidden;
    text-overflow: ellipsis;
    margin-left: 4px;
    display: block;
}

.card-time {
    font-size: 13px;
    color: #222;
    display: flex;
    align-items: center;
    gap: 4px;
    margin-bottom: 0;
}

.card-time-value {
    color: #222;
    font-weight: 500;
    margin-left: 2px;
}

.card-time-value .el-tag {
    margin-right: 8px;
}

.card-user {
    display: flex;
    align-items: center;
    justify-content: space-between;
}

.card-username {
    font-size: 12px;
    font-weight: 500;
    color: #222;
}

.card-user-runtime {
    font-size: 12px;
    font-weight: 500;
    color: #222;
    margin-left: auto;
}

.batch-table-wrap {
    padding: 10px;
}
</style>
