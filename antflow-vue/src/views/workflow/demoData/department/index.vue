<template>
    <div class="app-container">
        <div class="query-box">
            <el-form :model="query" ref="queryRef" :inline="true" v-show="showSearch">
                <el-form-item label="部门名称" prop="deptName">
                    <el-input v-model="query.deptName" placeholder="请输入部门名称" clearable style="width: 200px"
                        @keyup.enter="handleQuery" />
                </el-form-item>
                <el-form-item>
                    <el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button>
                    <el-button icon="Refresh" @click="resetQuery">重置</el-button>
                </el-form-item>
            </el-form>
            <el-row :gutter="10" class="mb8">
                <el-col :span="1.5">
                    <el-button type="primary" plain icon="Plus" @click="handleAdd">新增</el-button>
                </el-col>
            </el-row>
        </div>
        <div class="table-box">
            <el-table v-loading="loading" :data="dataList" border height="calc(100vh - 260px)">
                <el-table-column label="ID" align="center" prop="id" width="70" />
                <el-table-column label="部门名称" align="center" prop="name" min-width="140" show-overflow-tooltip />
                <el-table-column label="简称" align="center" prop="shortName" min-width="110" show-overflow-tooltip />
                <el-table-column label="上级部门" align="center" prop="parentName" min-width="140" show-overflow-tooltip />
                <el-table-column label="负责人" align="center" prop="leaderName" min-width="110" show-overflow-tooltip />
                <el-table-column label="层级" align="center" prop="level" width="80" />
                <el-table-column label="排序" align="center" prop="sort" width="80" />
                <el-table-column label="是否删除" align="center" width="100">
                    <template #default="scope">
                        <el-tag :type="scope.row.isDel == 1 ? 'danger' : 'success'">
                            {{ scope.row.isDel == 1 ? '删除' : '正常' }}
                        </el-tag>
                    </template>
                </el-table-column>
                <el-table-column label="操作" width="180" align="center" class-name="small-padding fixed-width">
                    <template #default="scope">
                        <el-button link type="primary" icon="Edit" @click="handleEdit(scope.row)">编辑</el-button>
                        <el-button link type="danger" icon="Delete" @click="handleDelete(scope.row)">删除</el-button>
                    </template>
                </el-table-column>
            </el-table>
            <pagination v-show="total > 0" :total="total" v-model:page="pageDto.page" v-model:limit="pageDto.pageSize"
                @pagination="getList" />
        </div>
    </div>
</template>

<script setup>
import { ref, reactive, toRefs, onMounted } from "vue";
import { getDepartmentListPage } from "@/api/workflow/demoDataApi";

const { proxy } = getCurrentInstance();

const dataList = ref([]);
const loading = ref(false);
const showSearch = ref(true);
const total = ref(0);

const data = reactive({
    query: {
        deptName: undefined,
    },
    pageDto: {
        page: 1,
        pageSize: 20,
    },
});
const { query, pageDto } = toRefs(data);

onMounted(() => {
    getList();
});

/** 查询列表 */
async function getList() {
    loading.value = true;
    await getDepartmentListPage(pageDto.value, query.value).then(response => {
        dataList.value = response.data;
        total.value = response.pagination?.totalCount ?? 0;
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
    query.value = { deptName: undefined };
    handleQuery();
}

/** 新增按钮操作(演示环境禁止操作) */
function handleAdd() {
    proxy.$modal.msgError("演示环境禁止操作！");
}

/** 编辑按钮操作(演示环境禁止操作) */
function handleEdit() {
    proxy.$modal.msgError("演示环境禁止操作！");
}

/** 删除按钮操作(演示环境禁止操作) */
function handleDelete() {
    proxy.$modal.msgError("演示环境禁止操作！");
}
</script>

<style scoped>
.table-box {
    margin-top: 8px;
}
</style>
