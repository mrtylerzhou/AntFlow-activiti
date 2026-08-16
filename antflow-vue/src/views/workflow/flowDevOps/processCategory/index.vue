<template>
    <div class="app-container">
        <div class="query-box">
            <el-form :model="query" ref="queryRef" :inline="true" v-show="showSearch">
                <el-form-item label="分类名称" prop="processTypeName">
                    <el-input v-model="query.processTypeName" placeholder="请输入分类名称" clearable style="width: 200px"
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
                <right-toolbar v-model:showSearch="showSearch" @queryTable="getList" :columns="columns"></right-toolbar>
            </el-row>
        </div>

        <div class="query-box">
            <el-table v-loading="loading" :data="list">
                <el-table-column label="分类名称" align="center" prop="processTypeName" v-if="columns[0].visible"
                    show-overflow-tooltip />
                <el-table-column label="入口" align="center" prop="entrance" v-if="columns[1].visible" width="100">
                    <template #default="scope">
                        <el-tag :type="scope.row.entrance === 'PC' ? 'success' : 'warning'">
                            {{ scope.row.entrance || '-' }}
                        </el-tag>
                    </template>
                </el-table-column>
                <el-table-column label="排序" align="center" prop="sort" v-if="columns[2].visible" width="80" />
                <el-table-column label="操作" width="230" align="center" class-name="small-padding fixed-width">
                    <template #default="scope">
                        <el-button link type="primary" icon="Edit" @click="handleEdit(scope.row)">编辑</el-button>
                        <el-button link type="primary" icon="Top" @click="handleMove(2, scope.row)">上移</el-button>
                        <el-button link type="primary" icon="Bottom" @click="handleMove(3, scope.row)">下移</el-button>
                        <el-button link type="danger" icon="Delete" @click="handleDelete(scope.row)">删除</el-button>
                    </template>
                </el-table-column>
            </el-table>
            <pagination v-show="total > 0" :total="total" v-model:page="pageDto.page" v-model:limit="pageDto.pageSize"
                @pagination="getList" />
        </div>

        <!-- 新增/编辑分类对话框 -->
        <el-dialog :title="title" v-model="open" width="480px" append-to-body destroy-on-close>
            <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
                <el-form-item label="分类名称" prop="processTypeName">
                    <el-input v-model="form.processTypeName" placeholder="请输入分类名称" maxlength="50" show-word-limit />
                </el-form-item>
            </el-form>
            <template #footer>
                <div class="dialog-footer">
                    <el-button type="primary" @click="submitForm">确 定</el-button>
                    <el-button @click="open = false">取 消</el-button>
                </div>
            </template>
        </el-dialog>
    </div>
</template>

<script setup>
import { ref, reactive, toRefs, onMounted, nextTick } from "vue";
import {
    getProcessCategoryListPage, saveProcessCategory, operateProcessCategory,
} from "@/api/workflow/processCategoryApi";
const { proxy } = getCurrentInstance();

const list = ref([]);
const loading = ref(false);
const showSearch = ref(true);
const total = ref(0);
const open = ref(false);
const title = ref("");

const data = reactive({
    query: {
        processTypeName: undefined,
    },
    pageDto: {
        page: 1,
        pageSize: 10
    },
    form: {
        id: undefined,
        processTypeName: undefined,
    },
    rules: {
        processTypeName: [{ required: true, message: "请输入分类名称", trigger: ["change", "blur"] }],
    }
});
const { query, pageDto, form, rules } = toRefs(data);

// 列显隐信息
const columns = ref([
    { key: 0, label: `分类名称`, visible: true },
    { key: 1, label: `入口`, visible: true },
    { key: 2, label: `排序`, visible: true }
]);

onMounted(() => {
    getList();
});

/** 查询列表 */
async function getList() {
    loading.value = true;
    await getProcessCategoryListPage(pageDto.value, query.value).then(response => {
        list.value = response.data;
        total.value = response.pagination.totalCount;
        loading.value = false;
    }).catch(() => {
        loading.value = false;
    });
}

/** 搜索按钮操作 */
function handleQuery() {
    pageDto.value.page = 1;
    getList();
}

/** 重置按钮操作 */
function resetQuery() {
    query.value = { processTypeName: undefined };
    handleQuery();
}

/** 新增按钮操作 */
function handleAdd() {
    form.value = {
        id: undefined,
        processTypeName: undefined,
    };
    title.value = "新增分类";
    open.value = true;
    nextTick(() => proxy.$refs["formRef"]?.clearValidate());
}

/** 编辑按钮操作 */
function handleEdit(row) {
    form.value = {
        id: row.id,
        processTypeName: row.processTypeName,
    };
    title.value = "编辑分类";
    open.value = true;
    nextTick(() => proxy.$refs["formRef"]?.clearValidate());
}

/** 提交表单 */
function submitForm() {
    proxy.$refs["formRef"].validate(async valid => {
        if (!valid) return;
        const res = await saveProcessCategory(form.value);
        if (res && (res.success === true || res.code == 200)) {
            proxy.$modal.msgSuccess("保存成功");
            open.value = false;
            getList();
        } else {
            proxy.$modal.msgError(res?.errMsg || res?.msg || "保存失败");
        }
    });
}

/** 上移/下移 */
function handleMove(type, row) {
    operateProcessCategory(type, row.id).then(res => {
        if (res && (res.success === true || res.code == 200)) {
            proxy.$modal.msgSuccess(type === 2 ? "上移成功" : "下移成功");
            getList();
        } else {
            proxy.$modal.msgError(res?.errMsg || res?.msg || "操作失败");
        }
    });
}

/** 删除按钮操作(演示环境禁止删除) */
function handleDelete(row) {
    proxy.$modal.msgWarning("演示环境,禁止删除数据");
}
</script>

<style scoped lang="scss">
.el-form-item__tip {
    font-size: 12px;
    color: #909399;
    line-height: 1.4;
}
</style>
