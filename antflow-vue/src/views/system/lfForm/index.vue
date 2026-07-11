<template>
  <div class="app-container">
    <div class="query-box">
      <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch">
        <el-form-item label="表单名称" prop="search">
          <el-input v-model="queryParams.search" placeholder="请输入表单名称" clearable style="width: 200px"
            @keyup.enter="handleQuery" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button>
          <el-button icon="Refresh" @click="resetQuery">重置</el-button>
        </el-form-item>
      </el-form>
    </div>
    <div class="table-box">
      <el-row :gutter="10" class="mb8">
        <el-col :span="1.5">
          <el-button type="primary" plain icon="Plus" @click="handleAdd">新建表单</el-button>
        </el-col>
      </el-row>
      <el-table v-loading="loading" :data="formList">
        <el-table-column label="表单编码" align="center" prop="formCode" width="150" />
        <el-table-column label="表单名称" align="center" prop="formName" :show-overflow-tooltip="true" />
        <el-table-column label="状态" align="center" prop="effectiveStatus" width="80">
          <template #default="scope">
            <el-tag :type="scope.row.effectiveStatus === 1 ? 'success' : 'info'">
              {{ scope.row.effectiveStatus === 1 ? '生效' : '未生效' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="版本数" align="center" prop="versionCount" width="80" />
        <el-table-column label="创建人" align="center" prop="createUser" width="100" />
        <el-table-column label="创建时间" align="center" prop="createTime" width="160">
          <template #default="scope">
            <span>{{ parseTime(scope.row.createTime, '{y}-{m}-{d} {h}:{i}') }}</span>
          </template>
        </el-table-column>
        <el-table-column label="操作" fixed="right" width="160" align="center">
          <template #default="scope">
            <el-button link type="primary" @click="handleHistory(scope.row)">历史版本</el-button>
            <el-button link type="danger" @click="handleDelete(scope.row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      <pagination v-show="total > 0" :total="total" v-model:page="pageDto.page" v-model:limit="pageDto.pageSize"
        @pagination="getList" />
    </div>

    <!-- 历史版本对话框 -->
    <el-dialog v-model="historyVisible" title="历史版本" width="800px" append-to-body>
      <el-table :data="historyList" v-loading="historyLoading">
        <el-table-column label="版本ID" align="center" prop="id" width="80" />
        <el-table-column label="表单名称" align="center" prop="formName" :show-overflow-tooltip="true" />
        <el-table-column label="状态" align="center" prop="effectiveStatus" width="80">
          <template #default="scope">
            <el-tag :type="scope.row.effectiveStatus === 1 ? 'success' : 'info'">
              {{ scope.row.effectiveStatus === 1 ? '生效' : '未生效' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="创建人" align="center" prop="createUser" width="100" />
        <el-table-column label="创建时间" align="center" prop="createTime" width="160">
          <template #default="scope">
            <span>{{ parseTime(scope.row.createTime, '{y}-{m}-{d} {h}:{i}') }}</span>
          </template>
        </el-table-column>
        <el-table-column label="操作" fixed="right" width="180" align="center">
          <template #default="scope">
            <el-button v-if="scope.row.effectiveStatus !== 1" link type="success" @click="handleEffective(scope.row)">生效</el-button>
            <el-button link type="primary" @click="handleEdit(scope.row)">编辑</el-button>
            <el-button link type="primary" @click="handleViewVersion(scope.row)">查看</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, toRefs, onMounted, getCurrentInstance } from "vue";
import { useRouter } from "vue-router";
import { listFormPage, deleteForm, listFormHistory, effectiveForm } from "@/api/workflow/lowcodeApi";

const { proxy } = getCurrentInstance();
const router = useRouter();

const formList = ref([]);
const loading = ref(false);
const showSearch = ref(true);
const total = ref(0);

const data = reactive({
  pageDto: { page: 1, pageSize: 10 },
  queryParams: { search: undefined },
});
const { pageDto, queryParams } = toRefs(data);

// 历史版本对话框
const historyVisible = ref(false);
const historyLoading = ref(false);
const historyList = ref([]);

onMounted(async () => {
  await getList();
});

const getList = async () => {
  loading.value = true;
  try {
    const res = await listFormPage(pageDto.value, queryParams.value);
    formList.value = res.data || [];
    total.value = res.pagination?.totalCount || 0;
  } catch (e) {
    proxy.$modal.msgError("获取表单列表失败");
  } finally {
    loading.value = false;
  }
};

const handleQuery = () => {
  pageDto.value.page = 1;
  getList();
};

const resetQuery = () => {
  queryParams.value.search = undefined;
  handleQuery();
};

const handleAdd = () => {
  router.push({ path: "/system/lfForm-design" });
};

const handleEdit = (row) => {
  historyVisible.value = false;
  router.push({
    path: "/system/lfForm-design",
    query: { formCode: row.formCode, formName: row.formName, id: row.id },
  });
};

const handleHistory = async (row) => {
  historyVisible.value = true;
  historyLoading.value = true;
  try {
    const res = await listFormHistory(row.formCode);
    historyList.value = res.data || [];
  } catch (e) {
    proxy.$modal.msgError("获取历史版本失败");
  } finally {
    historyLoading.value = false;
  }
};

const handleEffective = (row) => {
  proxy.$modal.confirm("确认生效此版本吗？同族其他生效版本将自动置为非生效。").then(async () => {
    try {
      await effectiveForm(row.id);
      proxy.$modal.msgSuccess("生效成功");
      const res = await listFormHistory(row.formCode);
      historyList.value = res.data || [];
      getList();
    } catch (e) {
      proxy.$modal.msgError(e?.data?.msg || "生效失败");
    }
  }).catch(() => {});
};

const handleViewVersion = (row) => {
  historyVisible.value = false;
  router.push({
    path: "/system/lfForm-design",
    query: { id: row.id, formCode: row.formCode, formName: row.formName, readonly: "1" },
  });
};

const handleDelete = (row) => {
  proxy.$modal.confirm(`确认删除表单"${row.formName}"(版本${row.formCode})吗？`).then(async () => {
    try {
      await deleteForm(row.id);
      proxy.$modal.msgSuccess("删除成功");
      getList();
    } catch (e) {
      proxy.$modal.msgError(e?.data?.msg || "删除失败");
    }
  }).catch(() => {});
};
</script>
