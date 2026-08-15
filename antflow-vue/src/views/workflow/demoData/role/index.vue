<template>
    <div class="app-container">
        <div class="query-box">
            <el-form :model="query" ref="queryRef" :inline="true" v-show="showSearch">
                <el-form-item label="角色名称" prop="roleName">
                    <el-input v-model="query.roleName" placeholder="请输入角色名称" clearable style="width: 200px"
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
                <el-table-column label="角色名称" align="center" prop="roleName" min-width="200" show-overflow-tooltip />
                <el-table-column label="关联人数" align="center" prop="userCount" width="110" />
                <el-table-column label="操作" width="240" align="center" class-name="small-padding fixed-width">
                    <template #default="scope">
                        <el-button link type="primary" icon="View" @click="handleDetail(scope.row)">详情</el-button>
                        <el-button link type="primary" icon="Edit" @click="handleEdit(scope.row)">编辑</el-button>
                        <el-button link type="danger" icon="Delete" @click="handleDelete(scope.row)">删除</el-button>
                    </template>
                </el-table-column>
            </el-table>
            <pagination v-show="total > 0" :total="total" v-model:page="pageDto.page" v-model:limit="pageDto.pageSize"
                @pagination="getList" />
        </div>

        <!-- 角色详情弹窗: 查看角色下人员 -->
        <el-dialog :title="`角色详情 - ${detailRoleName}`" v-model="detailOpen" width="720px" append-to-body
            destroy-on-close>
            <el-table v-loading="detailLoading" :data="detailList" border height="400px">
                <el-table-column label="ID" align="center" prop="id" width="70" />
                <el-table-column label="姓名" align="center" prop="userName" min-width="110" show-overflow-tooltip />
                <el-table-column label="手机号" align="center" prop="mobile" min-width="120" show-overflow-tooltip />
                <el-table-column label="邮箱" align="center" prop="email" min-width="160" show-overflow-tooltip />
                <el-table-column label="部门名称" align="center" prop="departmentName" min-width="140" show-overflow-tooltip />
            </el-table>
            <pagination v-show="detailTotal > 0" :total="detailTotal" v-model:page="detailPageDto.page"
                v-model:limit="detailPageDto.pageSize" @pagination="getDetailList" />
        </el-dialog>
    </div>
</template>

<script setup>
import { ref, reactive, toRefs, onMounted } from "vue";
import { getRoleListPage, getRoleUsersPage } from "@/api/workflow/demoDataApi";

const { proxy } = getCurrentInstance();

const dataList = ref([]);
const loading = ref(false);
const showSearch = ref(true);
const total = ref(0);

const data = reactive({
    query: {
        roleName: undefined,
    },
    pageDto: {
        page: 1,
        pageSize: 20,
    },
});
const { query, pageDto } = toRefs(data);

// 角色详情弹窗
const detailOpen = ref(false);
const detailLoading = ref(false);
const detailList = ref([]);
const detailTotal = ref(0);
const detailRoleName = ref("");
const detailRoleId = ref(null);
const detailData = reactive({
    pageDto: {
        page: 1,
        pageSize: 10,
    },
});
const { pageDto: detailPageDto } = toRefs(detailData);

onMounted(() => {
    getList();
});

/** 查询列表 */
async function getList() {
    loading.value = true;
    await getRoleListPage(pageDto.value, query.value).then(response => {
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
    query.value = { roleName: undefined };
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

/** 详情按钮操作: 打开弹窗查看角色下人员 */
function handleDetail(row) {
    detailRoleId.value = row.id;
    detailRoleName.value = row.roleName;
    detailPageDto.value.page = 1;
    detailOpen.value = true;
    getDetailList();
}

/** 查询角色下人员 */
async function getDetailList() {
    if (detailRoleId.value == null) {
        return;
    }
    detailLoading.value = true;
    await getRoleUsersPage(detailPageDto.value, detailRoleId.value).then(response => {
        detailList.value = response.data;
        detailTotal.value = response.pagination?.totalCount ?? 0;
        detailLoading.value = false;
    }).catch((r) => {
        detailLoading.value = false;
        proxy.$modal.msgError("加载角色人员失败:" + (r?.message ?? r));
    });
}
</script>

<style scoped>
.table-box {
    margin-top: 8px;
}
</style>
