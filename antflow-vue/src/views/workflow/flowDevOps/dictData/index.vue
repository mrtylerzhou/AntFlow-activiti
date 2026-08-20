<template>
    <div class="app-container">
        <div class="query-box">
            <el-form :model="query" ref="queryRef" :inline="true" v-show="showSearch">
                <el-form-item label="字典类型" prop="dictType">
                    <el-select v-model="query.dictType" placeholder="请选择类型" clearable style="width: 180px">
                        <el-option v-for="item in dictTypeOptions" :key="item.value" :label="item.label"
                            :value="item.value" />
                    </el-select>
                </el-form-item>
                <el-form-item label="关键字" prop="keyword">
                    <el-input v-model="query.keyword" placeholder="字典标签/字典键值" clearable style="width: 220px"
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

        <div class="query-box">
            <el-table v-loading="loading" :data="list">
                <el-table-column label="字典标签" align="center" prop="dictLabel" show-overflow-tooltip />
                <el-table-column label="字典键值" align="center" prop="dictValue" show-overflow-tooltip />
                <el-table-column label="字典类型" align="center" width="140">
                    <template #default="scope">
                        <el-tag :type="dictTypeTagType(scope.row.dictType)">
                            {{ scope.row.dictTypeLabel || scope.row.dictType }}
                        </el-tag>
                    </template>
                </el-table-column>
                <el-table-column label="排序" align="center" prop="sort" width="80" />
                <el-table-column label="备注" align="center" prop="remark" show-overflow-tooltip>
                    <template #default="scope">
                        {{ scope.row.remark || "-" }}
                    </template>
                </el-table-column>
                <el-table-column label="创建人" align="center" width="120" show-overflow-tooltip>
                    <template #default="scope">
                        {{ scope.row.createUser || "-" }}
                    </template>
                </el-table-column>
                <el-table-column label="创建时间" align="center" prop="createTime" width="160">
                    <template #default="scope">
                        <span>{{ parseTime(scope.row.createTime, '{y}-{m}-{d} {h}:{i}') }}</span>
                    </template>
                </el-table-column>
                <el-table-column label="操作" width="140" align="center" class-name="small-padding fixed-width">
                    <template #default="scope">
                        <el-button link type="primary" icon="Edit" :disabled="isLowCodeFlow(scope.row)"
                            @click="handleEdit(scope.row)">编辑</el-button>
                        <el-button link type="danger" icon="Delete" @click="handleDelete(scope.row)">删除</el-button>
                    </template>
                </el-table-column>
            </el-table>
            <pagination v-show="total > 0" :total="total" v-model:page="pageDto.page" v-model:limit="pageDto.pageSize"
                @pagination="getList" />
        </div>

        <!-- 新增/编辑对话框 -->
        <el-dialog :title="title" v-model="open" width="560px" append-to-body destroy-on-close>
            <el-form :model="form" :rules="rules" :validate-on-rule-change="false" ref="formRef" label-width="100px">
                <el-form-item label="字典标签" prop="dictLabel">
                    <el-input v-model="form.dictLabel" placeholder="请输入字典标签" maxlength="100" style="width: 380px" />
                </el-form-item>
                <el-form-item label="字典键值" prop="dictValue">
                    <el-input v-model="form.dictValue" placeholder="请输入字典键值" maxlength="100" style="width: 380px" />
                </el-form-item>
                <el-form-item label="字典类型" prop="dictType">
                    <el-select v-model="form.dictType" placeholder="请选择字典类型" style="width: 380px">
                        <el-option v-for="item in addableTypeOptions" :key="item.value" :label="item.label"
                            :value="item.value" />
                    </el-select>
                    <div class="el-form-item__tip">低代码流程类型由系统自动写入,不可手动新增</div>
                </el-form-item>
                <el-form-item label="排序" prop="sort">
                    <el-input-number v-model="form.sort" :min="0" :max="9999" style="width: 380px" />
                </el-form-item>
                <el-form-item label="备注" prop="remark">
                    <el-input v-model="form.remark" type="textarea" placeholder="请输入备注(选填)" maxlength="255"
                        :rows="3" style="width: 380px" />
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
    getDictDataListPage, saveDictData, updateDictData,
} from "@/api/workflow/dictDataApi";
const { proxy } = getCurrentInstance();

/**
 * 字典类型枚举(与后端 AFSpecialDictCategoryEnum 对齐)
 * lowcodeflow 系统自动写入, 新增/编辑下拉均不可选
 */
const dictTypeOptions = [
    { value: "lowcodeflow", label: "低代码流程" },
    { value: "udr", label: "自定义审批规则" },
    { value: "processlabel", label: "流程标签" },
];
/** 新增/编辑可选的类型(排除 lowcodeflow) */
const addableTypeOptions = dictTypeOptions.filter((o) => o.value !== "lowcodeflow");

const dictTypeTagType = (type) => {
    if (type === "lowcodeflow") return "warning";
    if (type === "udr") return "success";
    if (type === "processlabel") return "info";
    return "info";
};

const isLowCodeFlow = (row) => row.dictType === "lowcodeflow";

const list = ref([]);
const loading = ref(false);
const showSearch = ref(true);
const total = ref(0);
const open = ref(false);
const title = ref("");

const data = reactive({
    query: {
        dictType: undefined,
        keyword: undefined,
    },
    pageDto: {
        page: 1,
        pageSize: 10
    },
    form: {
        id: undefined,
        dictLabel: undefined,
        dictValue: undefined,
        dictType: undefined,
        sort: 0,
        remark: undefined,
    },
    rules: {
        dictLabel: [{ required: true, message: "请输入字典标签", trigger: ["blur", "change"] }],
        dictValue: [{ required: true, message: "请输入字典键值", trigger: ["blur", "change"] }],
        dictType: [{ required: true, message: "请选择字典类型", trigger: ["change"] }],
    }
});
const { query, pageDto, form, rules } = toRefs(data);

onMounted(() => {
    getList();
});

/** 查询列表 */
async function getList() {
    loading.value = true;
    await getDictDataListPage(pageDto.value, query.value).then(response => {
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
    query.value = { dictType: undefined, keyword: undefined };
    handleQuery();
}

/** 新增按钮操作 */
async function handleAdd() {
    form.value = {
        id: undefined,
        dictLabel: undefined,
        dictValue: undefined,
        dictType: undefined,
        sort: 0,
        remark: undefined,
    };
    title.value = "新增字典数据";
    open.value = true;
    //防止历史校验错误状态残留, 打开弹窗后清除校验提示
    nextTick(() => proxy.$refs["formRef"]?.clearValidate());
}

/** 编辑按钮操作 */
async function handleEdit(row) {
    form.value = {
        id: row.id,
        dictLabel: row.dictLabel,
        dictValue: row.dictValue,
        dictType: row.dictType,
        sort: row.sort ?? 0,
        remark: row.remark,
    };
    title.value = "编辑字典数据";
    open.value = true;
    nextTick(() => proxy.$refs["formRef"]?.clearValidate());
}

/** 删除按钮操作(演示环境禁止删除, 所有行均限制) */
function handleDelete(row) {
    proxy.$modal.msgWarning("演示环境禁止删除");
}

/** 提交表单 */
function submitForm() {
    proxy.$refs["formRef"].validate(async valid => {
        if (!valid) return;
        const payload = {
            id: form.value.id,
            dictLabel: form.value.dictLabel,
            dictValue: form.value.dictValue,
            dictType: form.value.dictType,
            sort: form.value.sort ?? 0,
            remark: form.value.remark,
        };
        const res = form.value.id
            ? await updateDictData(payload)
            : await saveDictData(payload);
        if (res && (res.success === true || res.code == 200)) {
            proxy.$modal.msgSuccess(form.value.id ? "修改成功" : "新增成功");
            open.value = false;
            getList();
        } else {
            proxy.$modal.msgError(res?.errMsg || res?.msg || "保存失败");
        }
    });
}
</script>

<style scoped lang="scss">
.el-form-item__tip {
    font-size: 12px;
    color: #909399;
    line-height: 1.4;
}
</style>
