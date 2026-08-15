<template>
    <div class="app-container">
        <div class="query-box">
            <el-form :model="query" ref="queryRef" :inline="true" v-show="showSearch">
                <el-form-item label="流程formCode" prop="formCode">
                    <el-input v-model="query.formCode" placeholder="请输入流程formCode关键字" clearable style="width: 200px"
                        @keyup.enter="handleQuery" />
                </el-form-item>
                <el-form-item label="权限类型" prop="permissionsType">
                    <el-select v-model="query.permissionsType" placeholder="请选择权限类型" clearable style="width: 140px">
                        <el-option v-for="item in permissionsTypeOptions" :key="item.value" :label="item.label"
                            :value="item.value" />
                    </el-select>
                </el-form-item>
                <el-form-item label="授权对象" prop="objectName">
                    <el-input v-model="query.objectName" placeholder="请输入人员/部门/角色名称关键字" clearable style="width: 200px"
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
                <el-table-column label="流程名称" align="center" prop="bpmnName" show-overflow-tooltip />
                <el-table-column label="formCode" align="center" prop="processKey" show-overflow-tooltip />
                <el-table-column label="权限类型" align="center" width="100">
                    <template #default="scope">
                        <el-tag :type="permissionsTypeTagType(scope.row.permissionsType)">
                            {{ permissionsTypeLabel(scope.row.permissionsType) }}
                        </el-tag>
                    </template>
                </el-table-column>
                <el-table-column label="授权类型" align="center" width="90">
                    <template #default="scope">
                        {{ objectTypeLabel(scope.row.objectType) }}
                    </template>
                </el-table-column>
                <el-table-column label="授权对象" align="center" prop="objectName" show-overflow-tooltip />
                <el-table-column label="创建人" align="center" width="120" show-overflow-tooltip>
                    <template #default="scope">
                        {{ scope.row.createUserName || scope.row.createUser || "-" }}
                    </template>
                </el-table-column>
                <el-table-column label="创建时间" align="center" prop="createTime" width="160">
                    <template #default="scope">
                        <span>{{ parseTime(scope.row.createTime, '{y}-{m}-{d} {h}:{i}') }}</span>
                    </template>
                </el-table-column>
                <el-table-column label="操作" width="120" align="center" class-name="small-padding fixed-width">
                    <template #default="scope">
                        <el-button link type="danger" icon="Delete" @click="handleDelete(scope.row)">删除</el-button>
                    </template>
                </el-table-column>
            </el-table>
            <pagination v-show="total > 0" :total="total" v-model:page="pageDto.page" v-model:limit="pageDto.pageSize"
                @pagination="getList" />
        </div>

        <!-- 新增权限对话框 -->
        <el-dialog :title="title" v-model="open" width="640px" append-to-body destroy-on-close>
            <el-form :model="form" :rules="rules" :validate-on-rule-change="false" ref="formRef" label-width="110px">
                <el-form-item label="流程" prop="processKeys">
                    <el-select v-model="form.processKeys" multiple filterable collapse-tags collapse-tags-tooltip
                        placeholder="请选择要授权的流程(可多选)" style="width: 460px">
                        <el-option v-for="item in confOptions" :key="item.formCode" :label="`${item.bpmnName} [${item.formCode}]`"
                            :value="item.formCode" />
                    </el-select>
                </el-form-item>
                <el-form-item label="授权对象类型" prop="objectType">
                    <el-radio-group v-model="form.objectType" @change="handleObjectTypeChange">
                        <el-radio :value="1">人员</el-radio>
                        <el-radio :value="2">部门</el-radio>
                        <el-radio :value="3">角色</el-radio>
                    </el-radio-group>
                </el-form-item>
                <el-form-item label="授权人员" v-if="form.objectType === 1">
                    <TagUserSelect v-model:list="userSelectedList" placeholder="请选择人员(可多选)" multiple
                        style="width: 460px" />
                    <div class="el-form-item__tip">授权人员可多选,与所选流程、权限类型组合批量授权</div>
                </el-form-item>
                <el-form-item label="授权部门" v-else-if="form.objectType === 2">
                    <div class="dept-select-box">
                        <div class="dept-select-tags" v-if="deptSelectedList.length > 0">
                            <el-tag v-for="(item, idx) in deptSelectedList" :key="item.id" closable
                                @close="removeDept(idx)" style="margin-right: 5px; margin-bottom: 4px">
                                {{ item.name }}
                            </el-tag>
                        </div>
                        <span v-else class="dept-select-placeholder">未选择部门</span>
                        <el-button type="default" icon="Plus" @click="deptDialogVisible = true" />
                    </div>
                    <div class="el-form-item__tip">部门可多选(支持任意层级部门),与所选流程、权限类型组合批量授权</div>
                </el-form-item>
                <el-form-item label="授权角色" v-else>
                    <div class="dept-select-box">
                        <div class="dept-select-tags" v-if="roleSelectedList.length > 0">
                            <el-tag v-for="(item, idx) in roleSelectedList" :key="item.id" closable
                                @close="removeRole(idx)" style="margin-right: 5px; margin-bottom: 4px">
                                {{ item.name }}
                            </el-tag>
                        </div>
                        <span v-else class="dept-select-placeholder">未选择角色</span>
                        <el-button type="default" icon="Plus" @click="roleDialogVisible = true" />
                    </div>
                    <div class="el-form-item__tip">角色可多选,与所选流程、权限类型组合批量授权</div>
                </el-form-item>
                <el-form-item label="权限类型" prop="permissionsTypes">
                    <el-checkbox-group v-model="form.permissionsTypes">
                        <el-checkbox v-for="item in permissionsTypeOptions" :key="item.value" :value="item.value"
                            :disabled="form.objectType === 2 && item.value === 3">
                            {{ item.label }}
                        </el-checkbox>
                    </el-checkbox-group>
                    <div class="el-form-item__tip" v-if="form.objectType === 2">部门权限不支持选择监控权限</div>
                </el-form-item>
            </el-form>
            <template #footer>
                <div class="dialog-footer">
                    <el-button type="primary" @click="submitForm">确 定</el-button>
                    <el-button @click="open = false">取 消</el-button>
                </div>
            </template>
        </el-dialog>

        <!-- 部门树选择弹窗 -->
        <DepartmentTreeSelect v-model:visible="deptDialogVisible" v-model:checkedData="deptSelectedList"
            @change="handleDeptChange" />
        <!-- 角色选择弹窗(参照流程设计-审批人设置-指定角色) -->
        <SelectRoleDialog v-model:visible="roleDialogVisible" :data="roleDialogData" @change="handleRoleChange" />
    </div>
</template>

<script setup>
import { ref, reactive, toRefs, computed, onMounted, nextTick } from "vue";
import TagUserSelect from "@/components/BizSelects/TagUserSelect/index.vue";
import DepartmentTreeSelect from "@/components/BizSelects/DepartmentTreeSelect/index.vue";
import SelectRoleDialog from "@/components/Workflow/dialog/selectRoleDialog.vue";
import { getAutoApproveActiveConfList } from "@/api/workflow/autoApproveApi";
import {
    getProcessPermissionsListPage, saveProcessPermissions, deleteProcessPermission,
} from "@/api/workflow/processPermissionsApi";
const { proxy } = getCurrentInstance();

const permissionsTypeOptions = [
    { value: 1, label: "查看权限" },
    { value: 2, label: "创建权限" },
    { value: 3, label: "监控权限" },
];

const permissionsTypeLabel = (type) =>
    permissionsTypeOptions.find((o) => o.value === type)?.label ?? type;
const permissionsTypeTagType = (type) => {
    if (type === 1) return "info";
    if (type === 2) return "success";
    if (type === 3) return "warning";
    return "info";
};

const objectTypeOptions = [
    { value: 1, label: "人员" },
    { value: 2, label: "部门" },
    { value: 3, label: "角色" },
];
const objectTypeLabel = (type) =>
    objectTypeOptions.find((o) => o.value === type)?.label ?? "-";

const list = ref([]);
const loading = ref(false);
const showSearch = ref(true);
const total = ref(0);
const open = ref(false);
const title = ref("");
const confOptions = ref([]);
const userSelectedList = ref([]); // {id,name}
const deptSelectedList = ref([]); // {id,name}
const roleSelectedList = ref([]); // {id,name}
const deptDialogVisible = ref(false);
const roleDialogVisible = ref(false);
//角色弹窗入参(selectRoleDialog 需要 [{targetId,name}] 结构)
const roleDialogData = computed(() => roleSelectedList.value.map(r => ({ targetId: r.id, name: r.name })));

const data = reactive({
    query: {
        formCode: undefined,
        permissionsType: undefined,
        objectName: undefined,
    },
    pageDto: {
        page: 1,
        pageSize: 10
    },
    form: {
        processKeys: [],
        objectType: 1, // 1=人员 2=部门 3=角色
        permissionsTypes: [],
    },
    // 注意: 人员/部门/角色为自定义组件选择,不参与 el-form 校验,提交时在 submitForm 中手动校验
    rules: {
        processKeys: [{ required: true, message: "请选择流程", trigger: ["change", "blur"] }],
        permissionsTypes: [{ required: true, message: "请选择权限类型", trigger: ["change", "blur"] }],
    }
});
const { query, pageDto, form, rules } = toRefs(data);

onMounted(() => {
    getList();
});

/** 查询列表 */
async function getList() {
    loading.value = true;
    await getProcessPermissionsListPage(pageDto.value, query.value).then(response => {
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
    query.value = { formCode: undefined, permissionsType: undefined, objectName: undefined };
    handleQuery();
}

/** 新增按钮操作 */
async function handleAdd() {
    form.value = {
        processKeys: [],
        objectType: 1,
        permissionsTypes: [],
    };
    userSelectedList.value = [];
    deptSelectedList.value = [];
    roleSelectedList.value = [];
    title.value = "新增流程权限";
    await getAutoApproveActiveConfList().then(res => {
        confOptions.value = res.data ?? [];
    });
    open.value = true;
    //防止历史校验错误状态残留, 打开弹窗后清除校验提示
    nextTick(() => proxy.$refs["formRef"]?.clearValidate());
}

/** 授权对象类型切换 */
function handleObjectTypeChange() {
    //切换时清空对象选择, 部门模式下自动去掉监控权限
    userSelectedList.value = [];
    deptSelectedList.value = [];
    roleSelectedList.value = [];
    if (form.value.objectType === 2) {
        form.value.permissionsTypes = form.value.permissionsTypes.filter(t => t !== 3);
    }
}

/** 部门选择完成 */
function handleDeptChange(depts) {
    deptSelectedList.value = depts;
}

function removeDept(idx) {
    deptSelectedList.value.splice(idx, 1);
}

/** 角色选择确认 */
function handleRoleChange(data) {
    roleSelectedList.value = (data ?? []).map(item => ({ id: item.targetId, name: item.name }));
}

function removeRole(idx) {
    roleSelectedList.value.splice(idx, 1);
}

/** 提交表单 */
function submitForm() {
    proxy.$refs["formRef"].validate(async valid => {
        if (!valid) return;
        //手动校验授权对象(自定义组件选择,不参与 el-form 校验)
        if (form.value.objectType === 1 && userSelectedList.value.length === 0) {
            proxy.$modal.msgWarning("请选择授权人员");
            return;
        }
        if (form.value.objectType === 2 && deptSelectedList.value.length === 0) {
            proxy.$modal.msgWarning("请选择授权部门");
            return;
        }
        if (form.value.objectType === 3 && roleSelectedList.value.length === 0) {
            proxy.$modal.msgWarning("请选择授权角色");
            return;
        }
        const objectIds = form.value.objectType === 1
            ? userSelectedList.value.map(u => String(u.id))
            : (form.value.objectType === 2
                ? deptSelectedList.value.map(d => String(d.id))
                : roleSelectedList.value.map(r => String(r.id)));
        const payload = {
            processKeys: form.value.processKeys,
            permissionsTypes: form.value.permissionsTypes,
            objectType: form.value.objectType,
            objectIds,
        };
        const res = await saveProcessPermissions(payload);
        if (res && (res.success === true || res.code == 200)) {
            const result = res.data ?? {};
            proxy.$modal.msgSuccess(`新增 ${result.insertCount ?? 0} 条, 跳过 ${result.skipCount ?? 0} 条重复`);
            open.value = false;
            getList();
        } else {
            proxy.$modal.msgError(res?.errMsg || res?.msg || "保存失败");
        }
    });
}

/** 删除按钮操作 */
function handleDelete(row) {
    proxy.$modal.confirm(`确定删除流程[${row.bpmnName ?? row.processKey}]对[${row.objectName}]的[${permissionsTypeLabel(row.permissionsType)}]吗?`).then(async () => {
        const res = await deleteProcessPermission(row.id);
        if (res && (res.success === true || res.code == 200)) {
            proxy.$modal.msgSuccess("删除成功");
            getList();
        } else {
            proxy.$modal.msgError(res?.errMsg || res?.msg || "删除失败");
        }
    }).catch(() => { });
}
</script>

<style scoped lang="scss">
.el-form-item__tip {
    font-size: 12px;
    color: #909399;
    line-height: 1.4;
}

.dept-select-box {
    display: flex;
    align-items: flex-start;
    width: 460px;
    min-height: 32px;
    border: 1px solid #dcdfe6;
    border-radius: 4px;
    padding: 4px 8px;

    .dept-select-tags {
        flex-grow: 1;
    }

    .dept-select-placeholder {
        flex-grow: 1;
        color: #a8abb2;
        font-size: 14px;
        line-height: 24px;
    }
}
</style>
