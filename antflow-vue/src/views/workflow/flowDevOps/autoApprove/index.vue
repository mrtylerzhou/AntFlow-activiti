<template>
    <div class="app-container">
        <div class="query-box">
            <el-form :model="query" ref="queryRef" :inline="true" v-show="showSearch">
                <el-form-item label="归属人" prop="ownerUserId">
                    <el-select v-model="query.ownerUserId" placeholder="请选择归属人" clearable filterable remote
                        :remote-method="remoteQueryUsers" :loading="userLoading" style="width: 200px">
                        <el-option v-for="item in userOptions" :key="item.id" :label="item.name" :value="item.id" />
                    </el-select>
                </el-form-item>
                <el-form-item label="流程" prop="formCode">
                    <el-select v-model="query.formCode" placeholder="请选择流程" clearable filterable style="width: 220px">
                        <el-option v-for="item in confOptions" :key="item.formCode"
                            :label="`${item.bpmnName} [${item.formCode}]`" :value="item.formCode" />
                    </el-select>
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
                <el-table-column label="归属人" align="center" prop="ownerUserName" />
                <el-table-column label="流程名称" align="center" prop="bpmnName" show-overflow-tooltip />
                <el-table-column label="formCode" align="center" prop="formCode" show-overflow-tooltip />
                <el-table-column label="版本(bpmnCode)" align="center" prop="bpmnCode" />
                <el-table-column label="节点范围" align="center" min-width="140">
                    <template #default="scope">
                        <span>{{ buildNodeScopeText(scope.row) }}</span>
                    </template>
                </el-table-column>
                <el-table-column label="审批条件" align="center" width="100">
                    <template #default="scope">
                        <span>{{ buildConditionText(scope.row) }}</span>
                    </template>
                </el-table-column>
                <el-table-column label="默认意见" align="center" prop="defaultComment" show-overflow-tooltip />
                <el-table-column label="活跃" align="center" width="90">
                    <template #default="scope">
                        <el-tag :type="scope.row.active ? 'success' : 'info'">{{ scope.row.active ? '活跃' : '不活跃'
                            }}</el-tag>
                    </template>
                </el-table-column>
                <el-table-column label="启用" align="center" width="80">
                    <template #default="scope">
                        <el-switch :model-value="scope.row.enabled == 1" @change="handleToggle(scope.row)" />
                    </template>
                </el-table-column>
                <el-table-column label="创建时间" align="center" prop="createTime" width="160">
                    <template #default="scope">
                        <span>{{ parseTime(scope.row.createTime, '{y}-{m}-{d} {h}:{i}') }}</span>
                    </template>
                </el-table-column>
                <el-table-column label="操作" width="180" align="center" class-name="small-padding fixed-width">
                    <template #default="scope">
                        <el-button link type="primary" icon="Edit" @click="handleEdit(scope.row)">编辑</el-button>
                        <el-button link type="primary" icon="CopyDocument" @click="handleCopy(scope.row)">复制</el-button>
                        <el-button link type="danger" icon="Delete" @click="handleDelete(scope.row)">删除</el-button>
                    </template>
                </el-table-column>
            </el-table>
            <pagination v-show="total > 0" :total="total" v-model:page="pageDto.page" v-model:limit="pageDto.pageSize"
                @pagination="getList" />
        </div>

        <!-- 新增/编辑对话框 -->
        <el-dialog :title="title" v-model="open" width="760px" append-to-body>
            <el-form :model="form" :rules="rules" ref="formRef" label-width="110px">
                <el-form-item label="归属人" prop="ownerUserId">
                    <TagUserSelect v-model:list="userSelectedList" placeholder="请选择归属人(默认自己)" style="width: 280px;" />
                </el-form-item>
                <el-form-item label="流程" prop="confId" v-if="!form.id">
                    <el-select v-model="form.confId" filterable placeholder="请选择要自动审批的流程" style="width: 460px"
                        @change="handleFlowChange">
                        <el-option v-for="item in confOptions" :key="item.id"
                            :label="`${item.bpmnName} [${item.formCode}]`" :value="item.id" />
                    </el-select>
                </el-form-item>
                <el-form-item label="流程" v-else>
                    <span>{{ form.bpmnName }} [{{ form.formCode }}] 版本:{{ form.bpmnCode }}</span>
                </el-form-item>
                <el-form-item label="生效节点" v-if="form.confId || form.id">
                    <el-select v-model="selectedElementIds" multiple filterable clearable
                        placeholder="不选则对整个流程生效" style="width: 460px">
                        <el-option v-for="n in nodeOptions" :key="n.elementId" :label="n.nodeName"
                            :value="n.elementId" />
                    </el-select>
                    <div class="el-form-item__tip">不选节点则自动审批对整个流程生效</div>
                </el-form-item>
                <el-form-item label="审批条件" v-if="currentFlowType == 2 && (form.confId || form.id)">
                    <div style="width: 100%">
                        <ConditionGroupEditor :conditionList="form.conditionList"
                            v-model:groupRelation="form.groupRelation" />
                    </div>
                </el-form-item>
                <el-form-item label="审批条件" v-else-if="currentFlowType != 2 && (form.confId || form.id)">
                    <el-text type="warning">该类型流程不支持条件配置, 保存后为无条件自动审批</el-text>
                </el-form-item>
                <el-form-item label="默认审批意见">
                    <el-input v-model="form.defaultComment" placeholder="选填, 未填则审批记录使用默认文案" maxlength="200"
                        show-word-limit style="width: 460px" />
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
import { ref, reactive, toRefs, watch, onMounted, getCurrentInstance } from "vue";
import { useStore } from '@/store/modules/workflow';
import TagUserSelect from "@/components/BizSelects/TagUserSelect/index.vue";
import ConditionGroupEditor from "@/components/Workflow/drawer/condition/ConditionGroupEditor.vue";
import useUserStore from "@/store/modules/user";
import $func from "@/utils/antflow/index";
import { getApiWorkFlowData } from "@/api/workflow/index";
import { getLowCodeFromCodeData } from "@/api/workflow/lowcodeApi";
import {
    getAutoApproveListPage, getAutoApproveActiveConfList, saveAutoApprove,
    updateAutoApprove, toggleAutoApprove, deleteAutoApprove, copyAutoApprove
} from "@/api/workflow/autoApproveApi";
import { queryUsersByName } from "@/api/workflow/processPermissionsApi";

const { proxy } = getCurrentInstance();
const store = useStore();
const userStore = useUserStore();

const list = ref([]);
const loading = ref(false);
const showSearch = ref(true);
const total = ref(0);
const open = ref(false);
const title = ref("");
const confOptions = ref([]);
const userOptions = ref([]); //归属人下拉(远程搜索)
const userLoading = ref(false);
const nodeOptions = ref([]);
const selectedElementIds = ref([]);
const userSelectedList = ref([]);
const currentFlowType = ref(null);

const data = reactive({
    query: { ownerUserName: undefined, ownerUserId: undefined, formCode: undefined },
    pageDto: { page: 1, pageSize: 10 },
    form: {
        id: undefined,
        confId: undefined,
        ownerUserId: undefined,
        ownerUserName: undefined,
        formCode: undefined,
        bpmnCode: undefined,
        bpmnName: undefined,
        conditionList: [[]],
        groupRelation: false,
        defaultComment: undefined,
    },
    rules: {
        ownerUserId: [{ required: true, message: "请选择归属人", trigger: ["change", "blur"] }],
        confId: [{ required: true, message: "请选择要自动审批的流程", trigger: ["change"] }],
    }
});
const { query, pageDto, form, rules } = toRefs(data);

watch(() => userSelectedList.value, (newVal) => {
    if (!proxy.isEmptyArray(newVal)) {
        form.value.ownerUserId = newVal[0].id;
        form.value.ownerUserName = newVal[0].name;
    } else {
        form.value.ownerUserId = undefined;
        form.value.ownerUserName = undefined;
    }
}, { deep: true });

onMounted(async () => {
    await getList();
    //流程下拉数据(搜索栏+新增弹窗共用)
    getAutoApproveActiveConfList().then(res => {
        confOptions.value = res.data ?? [];
    });
});

/** 查询列表 */
async function getList() {
    loading.value = true;
    await getAutoApproveListPage(pageDto.value, query.value).then(response => {
        list.value = response.data;
        total.value = response.pagination.totalCount;
        loading.value = false;
    });
}

function handleQuery() {
    pageDto.value.page = 1;
    getList();
}

function resetQuery() {
    query.value = { ownerUserName: undefined, ownerUserId: undefined, formCode: undefined };
    userOptions.value = [];
    handleQuery();
}

/** 归属人下拉远程搜索 */
function remoteQueryUsers(name) {
    userLoading.value = true;
    queryUsersByName(name).then(res => {
        userOptions.value = (res.data ?? []).map(u => ({ id: String(u.id), name: u.name }));
        userLoading.value = false;
    }).catch(() => {
        userLoading.value = false;
    });
}

function buildNodeScopeText(row) {
    if (!row.nodeScope || row.nodeScope.length == 0) return "整个流程";
    return row.nodeScope.map(i => i.nodeName).join(",");
}

function buildConditionText(row) {
    if (!row.conditionList || row.conditionList.length == 0) return "无条件";
    return row.conditionList.length + "个条件组";
}

function resetForm() {
    form.value = {
        id: undefined,
        confId: undefined,
        ownerUserId: userStore.id,
        ownerUserName: userStore.nickName,
        formCode: undefined,
        bpmnCode: undefined,
        bpmnName: undefined,
        conditionList: [[]],
        groupRelation: false,
        defaultComment: undefined,
    };
    userSelectedList.value = [{ id: userStore.id, name: userStore.nickName }];
    selectedElementIds.value = [];
    nodeOptions.value = [];
    currentFlowType.value = null;
}

async function handleAdd() {
    resetForm();
    title.value = "新增自动审批设置";
    await getAutoApproveActiveConfList().then(res => {
        confOptions.value = res.data ?? [];
    });
    open.value = true;
}

/** 选择流程后加载节点下拉 + LF表单字段 */
async function handleFlowChange(confId) {
    nodeOptions.value = [];
    selectedElementIds.value = [];
    form.value.conditionList = [[]];
    form.value.groupRelation = false;
    const conf = confOptions.value.find(c => c.id === confId);
    if (!conf) return;
    form.value.formCode = conf.formCode;
    form.value.bpmnCode = conf.bpmnCode;
    form.value.bpmnName = conf.bpmnName;
    currentFlowType.value = conf.flowType;
    await loadNodeOptions(confId, conf);
}

async function loadNodeOptions(confId, conf) {
    await getApiWorkFlowData({ id: confId }).then(res => {
        const nodes = res.data?.nodes ?? [];
        nodeOptions.value = nodes.filter(n => n.nodeType == 4)
            .map(n => ({ elementId: n.nodeId, nodeName: n.nodeName }));
    });
    if (conf.flowType == 2) {
        await getLowCodeFromCodeData(conf.formCode).then(res => {
            if (res.code == 200 && res.data) {
                store.setLowCodeFormField(parseLfFormFields(res.data));
            }
        });
    }
}

/**
 * LF formdata 接口返回 VForm JSON 字符串({widgetList, formConfig}),
 * 按 VForm getFormFieldJson 的遍历规则拍平字段控件(formItemFlag),
 * 转成 store.lowCodeFormField 期望的 {formFields} 结构, 供选择条件弹窗使用.
 */
function parseLfFormFields(data) {
    const formJson = typeof data === "string" ? JSON.parse(data) : data;
    const formFields = [];
    const walk = (list) => {
        (list || []).forEach(w => {
            if (w.formItemFlag) {
                formFields.push(w);
            } else if (w.type === "grid") {
                (w.cols || []).forEach(col => walk(col.widgetList));
            } else if (w.type === "table") {
                (w.rows || []).forEach(row => (row.cols || []).forEach(col => walk(col.widgetList)));
            } else if (w.type === "tab") {
                (w.tabs || []).forEach(tab => walk(tab.widgetList));
            } else if (w.type === "sub-form" || w.category === "container") {
                walk(w.widgetList);
            }
        });
    };
    walk(formJson.widgetList);
    return { formFields };
}

async function handleEdit(row) {
    resetForm();
    title.value = "编辑自动审批设置";
    form.value.id = row.id;
    form.value.confId = row.confId;
    form.value.ownerUserId = row.ownerUserId;
    form.value.ownerUserName = row.ownerUserName;
    form.value.formCode = row.formCode;
    form.value.bpmnCode = row.bpmnCode;
    form.value.bpmnName = row.bpmnName;
    form.value.defaultComment = row.defaultComment;
    form.value.groupRelation = row.groupRelation ?? false;
    form.value.conditionList = row.conditionList && row.conditionList.length > 0 ? row.conditionList : [[]];
    userSelectedList.value = [{ id: row.ownerUserId, name: row.ownerUserName }];
    selectedElementIds.value = (row.nodeScope ?? []).map(i => i.elementId);
    currentFlowType.value = row.flowType;
    if (row.confId) {
        await loadNodeOptions(row.confId, { formCode: row.formCode, flowType: row.flowType });
    }
    open.value = true;
}

function submitForm() {
    proxy.$refs["formRef"].validate(async valid => {
        if (!valid) return;
        //条件转后端存储格式
        let conditionList = null;
        if (currentFlowType.value == 2) {
            conditionList = form.value.conditionList;
            $func.convertConditionNodeValue(conditionList, false);
        }
        const payload = {
            id: form.value.id,
            ownerUserId: form.value.ownerUserId,
            ownerUserName: form.value.ownerUserName,
            formCode: form.value.formCode,
            defaultComment: form.value.defaultComment,
            groupRelation: form.value.groupRelation,
            conditionList: conditionList,
            nodeScope: selectedElementIds.value.map(id => ({
                elementId: id,
                nodeName: nodeOptions.value.find(n => n.elementId === id)?.nodeName
            })),
        };
        if (form.value.id != undefined) {
            const res = await updateAutoApprove(payload);
            if (!checkRes(res, "修改成功")) return;
        } else {
            const res = await saveAutoApprove(payload);
            if (!checkRes(res, "新增成功")) return;
        }
        open.value = false;
    });
}

async function handleToggle(row) {
    const enabled = row.enabled == 1 ? 0 : 1;
    const res = await toggleAutoApprove(row.id, enabled);
    checkRes(res, enabled == 1 ? "已启用" : "已停用");
}

/** 复制: 直接请求后端, 校验结果由后端返回(活跃版本/节点/表单变化则报错) */
function handleCopy(row) {
    copyAutoApprove(row.id).then(res => {
        checkRes(res, "复制成功");
    });
}

async function handleDelete(row) {
    proxy.$modal.confirm('是否删除归属人[' + row.ownerUserName + ']的该自动审批配置?').then(async () => {
        const res = await deleteAutoApprove(row.id);
        checkRes(res, "删除成功");
    }).catch(() => { });
}

/** axios 封装不 reject 业务失败, 统一检查 Result */
function checkRes(res, okText) {
    if (res && (res.success === true || res.code == 200)) {
        proxy.$modal.msgSuccess(okText);
        getList();
        return true;
    }
    proxy.$modal.msgError(res?.errMsg || res?.msg || "操作失败");
    return false;
}
</script>

<style scoped lang="scss">
.el-form-item__tip {
    font-size: 12px;
    color: #909399;
    line-height: 1.4;
}

:deep(.condition_content) {
    p.tip {
        margin-bottom: 12px;
    }

    .el-card {
        margin-bottom: 12px;
    }

    .el-card__header {
        padding: 12px 16px 8px !important;
    }
}
</style>
