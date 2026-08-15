<template>
    <div class="app-container">
        <div class="query-box">
            <el-form :model="query" ref="queryRef" :inline="true" v-show="showSearch">
                <el-form-item label="低代码流程" prop="formCode">
                    <el-select v-model="query.formCode" placeholder="请选择低代码流程" clearable filterable
                        style="width: 280px" @change="handleQuery">
                        <el-option v-for="item in confOptions" :key="item.key"
                            :label="`${item.value} [${item.key}]`" :value="item.key" />
                    </el-select>
                </el-form-item>
                <el-form-item label="流程编号" prop="processNumber">
                    <el-input v-model="query.processNumber" placeholder="请输入流程编号" clearable
                        style="width: 200px" @keyup.enter="handleQuery" />
                </el-form-item>
                <el-form-item>
                    <el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button>
                    <el-button icon="Refresh" @click="resetQuery">重置</el-button>
                </el-form-item>
            </el-form>
        </div>
        <div class="table-box">
            <el-table v-loading="loading" :data="dataList" border height="calc(100vh - 260px)">
                <el-table-column v-for="col in columns" :key="col.key" :prop="col.key" :label="col.label"
                    :fixed="col.fixed ? 'left' : false" :min-width="col.fixed ? 150 : 120"
                    :show-overflow-tooltip="true" align="center">
                    <template #default="scope">
                        <!-- 流程编号:可点击跳转详情(权限校验) -->
                        <el-link v-if="col.key === 'processNumber'" type="primary" @click="handleView(scope.row)">
                            {{ scope.row.processNumber }}
                        </el-link>
                        <span v-else-if="col.key === 'processStateName'">
                            <el-tag :type="stateTagType(scope.row.processState)">{{ scope.row.processStateName }}</el-tag>
                        </span>
                        <span v-else>{{ scope.row[col.key] }}</span>
                    </template>
                </el-table-column>
            </el-table>
            <pagination v-show="total > 0" :total="total" v-model:page="pageDto.page" v-model:limit="pageDto.pageSize"
                @pagination="getList" />
        </div>
        <!-- 流程详情预览抽屉(复用任务中心组件) -->
        <previewDrawer v-if="visible" />
    </div>
</template>

<script setup>
import { ref, reactive, toRefs, computed, onMounted } from "vue";
import previewDrawer from "@/views/workflow/components/previewDrawer.vue";
import { useStore } from '@/store/modules/workflow';
import { getLFActiveFormCodePageList } from "@/api/workflow/lowcodeApi";
import { getBusinessDataListPage, checkBusinessDataPermission } from "@/api/workflow/demoDataApi";

const { proxy } = getCurrentInstance();
const store = useStore();
const { setPreviewDrawer, setPreviewDrawerConfig } = store;
const previewDrawerVisible = computed(() => store.previewDrawer);

const columns = ref([]);
const dataList = ref([]);
const loading = ref(false);
const showSearch = ref(true);
const total = ref(0);
const confOptions = ref([]);

const data = reactive({
    query: {
        formCode: undefined,
        processNumber: undefined,
    },
    pageDto: {
        page: 1,
        pageSize: 20,
    },
});
const { query, pageDto } = toRefs(data);

const visible = computed({
    get() {
        return previewDrawerVisible.value
    },
    set() {
        setPreviewDrawer(false)
    }
});

onMounted(() => {
    getConfOptions();
});

/** 加载已启用低代码流程下拉 */
function getConfOptions() {
    getLFActiveFormCodePageList({ page: 1, pageSize: 1000 }, {}).then(res => {
        confOptions.value = res.data ?? [];
        // 默认选中第一个流程,直接出数据
        if (confOptions.value.length > 0) {
            query.value.formCode = confOptions.value[0].key;
            getList();
        }
    }).catch(() => {
        proxy.$modal.msgError("加载低代码流程列表失败");
    });
}

/** 查询列表 */
async function getList() {
    if (!query.value.formCode) {
        proxy.$modal.msgWarning("请先选择低代码流程");
        return;
    }
    loading.value = true;
    await getBusinessDataListPage(pageDto.value, query.value).then(response => {
        columns.value = response.data?.columns ?? [];
        dataList.value = response.data?.rows ?? [];
        total.value = response.data?.total ?? 0;
        loading.value = false;
    }).catch((r) => {
        loading.value = false;
        proxy.$modal.msgError("加载列表失败:" + (r?.message ?? r));
    });
}

/** 搜索按钮操作 */
function handleQuery() {
    pageDto.value.page = 1;
    getList();
}

/** 重置按钮操作 */
function resetQuery() {
    query.value = { formCode: query.value.formCode, processNumber: undefined };
    handleQuery();
}

/** 点击流程编号:先权限校验,通过后打开详情预览抽屉 */
function handleView(row) {
    checkBusinessDataPermission(row.processNumber).then(res => {
        if (res.code == 200 && res.data === true) {
            setPreviewDrawerConfig({
                formCode: row.processKey || query.value.formCode,
                processNumber: row.processNumber,
                isOutSideAccess: false,
                isLowCodeFlow: true,
                processState: row.processState,
            });
            setPreviewDrawer(true);
        } else {
            proxy.$modal.msgError("无权限查看该流程详情");
        }
    }).catch(() => {
        proxy.$modal.msgError("无权限查看该流程详情");
    });
}

/** 流程状态标签样式 */
function stateTagType(processState) {
    if (processState == 1) return "primary";   //审批中
    if (processState == 2) return "success";   //审批通过
    if (processState == 3) return "info";      //作废
    if (processState == 6) return "danger";    //审批拒绝
    return "info";
}
</script>

<style scoped>
.table-box {
    margin-top: 8px;
}
</style>
