<template>
    <div class="app-container app-version-page">
        <el-row :gutter="12">
            <!-- 左侧: 版本列表 -->
            <el-col :span="6">
                <el-card shadow="never" class="version-card">
                    <template #header>
                        <div class="card-header">
                            <span>App 版本</span>
                            <el-button type="primary" size="small" icon="Plus" @click="handleCreate">新建版本</el-button>
                        </div>
                    </template>
                    <div v-loading="listLoading">
                        <div v-if="versionList.length === 0 && !listLoading" class="empty-tip">暂无版本</div>
                        <div v-for="item in versionList" :key="item.id"
                            class="version-item" :class="{ active: item.id === currentId }" @click="handleSelect(item)">
                            <div class="version-item-main">
                                <span class="version-name">{{ item.version }}</span>
                                <el-tag v-if="isOnline(item)" type="success" size="small" effect="dark">线上</el-tag>
                                <el-tag v-else-if="isDraft(item)" type="warning" size="small">草稿</el-tag>
                                <el-tag v-else type="info" size="small">已发布</el-tag>
                            </div>
                            <div class="version-item-sub">
                                <span>创建: {{ parseTime(item.createTime, '{y}-{m}-{d}') }}</span>
                                <span>生效: {{ item.effectiveTime || '-' }}</span>
                            </div>
                        </div>
                    </div>
                </el-card>
            </el-col>

            <!-- 右侧: 版本详情 -->
            <el-col :span="18">
                <el-card v-if="current" shadow="never" class="detail-card">
                    <template #header>
                        <div class="card-header">
                            <span>版本详情: {{ current.version }}</span>
                            <div>
                                <template v-if="isDraft(current)">
                                    <el-button type="primary" :loading="saveLoading" @click="submitBaseInfo(false)">保存基本信息</el-button>
                                    <el-button type="success" @click="handlePublish">发 布</el-button>
                                    <el-button type="danger" plain @click="handleDelete">删 除</el-button>
                                </template>
                                <template v-else>
                                    <el-button type="primary" :loading="saveLoading" @click="submitBaseInfo(true)">保存运营参数</el-button>
                                </template>
                            </div>
                        </div>
                    </template>

                    <!-- 基本信息 -->
                    <el-form ref="formRef" :model="form" :rules="formRules" :validate-on-rule-change="false"
                        label-width="110px" class="base-form">
                        <el-row :gutter="12">
                            <el-col :span="12">
                                <el-form-item label="版本号" prop="version">
                                    <el-input v-model="form.version" :disabled="!isDraft(current)" placeholder="如 1.2.0" />
                                </el-form-item>
                            </el-col>
                            <el-col :span="12">
                                <el-form-item label="版本描述">
                                    <el-input v-model="form.description" :disabled="!isDraft(current)" placeholder="版本描述" />
                                </el-form-item>
                            </el-col>
                            <el-col :span="12">
                                <el-form-item label="强制更新">
                                    <el-switch v-model="form.isForce" :active-value="1" :inactive-value="0"
                                        active-text="强更" inactive-text="不强更" />
                                </el-form-item>
                            </el-col>
                            <el-col :span="12">
                                <el-form-item label="生效时间">
                                    <el-input v-model="form.effectiveTime" :disabled="!isDraft(current)" placeholder="仅记录,不参与定时发布" />
                                </el-form-item>
                            </el-col>
                            <el-col :span="12">
                                <el-form-item label="安卓下载地址">
                                    <el-input v-model="form.androidUrl" placeholder="https://..." />
                                </el-form-item>
                            </el-col>
                            <el-col :span="12">
                                <el-form-item label="iOS下载地址">
                                    <el-input v-model="form.iosUrl" placeholder="https://..." />
                                </el-form-item>
                            </el-col>
                            <el-col :span="24" v-if="qrCode">
                                <el-form-item label="App下载码">
                                    <div class="qr-box">
                                        <el-input v-model="form.downloadCode" placeholder="下载码" style="width: 320px" />
                                        <el-button icon="CopyDocument" @click="handleCopyCode">复制</el-button>
                                    </div>
                                </el-form-item>
                            </el-col>
                        </el-row>
                    </el-form>

                    <!-- 三个 Tab -->
                    <el-tabs v-model="activeTab" class="data-tabs">
                        <el-tab-pane v-for="tab in tabs" :key="tab.type" :name="String(tab.type)">
                            <template #label>
                                {{ tab.label }}
                                <el-badge v-if="tabDirty[tab.type]" is-dot type="danger" class="tab-badge" />
                            </template>
                            <div v-if="isDraft(current)" class="tab-toolbar">
                                <el-button type="primary" plain icon="Plus" @click="openAddDialog(tab)">添加</el-button>
                                <el-button type="primary" :loading="tabSaving" @click="saveTab(tab.type)">保存</el-button>
                                <span v-if="tabDirty[tab.type]" class="dirty-tip">有未保存的修改</span>
                            </div>
                            <draggable v-model="tabData[tab.type]" item-key="id" tag="div" class="drag-list"
                                handle=".drag-handle"
                                :disabled="!isDraft(current) || tab.type === 3"
                                @end="onDragEnd(tab.type)">
                                <template #item="{ element, index }">
                                    <div class="drag-row">
                                        <el-icon v-if="tab.type !== 3 && isDraft(current)" class="drag-handle">
                                            <Rank />
                                        </el-icon>
                                        <span class="drag-name" :title="element.name">{{ element.name }}</span>
                                        <span class="drag-id">ID: {{ element.id }}</span>
                                        <span class="drag-sort">#{{ index + 1 }}</span>
                                        <el-button v-if="isDraft(current)" link type="danger" icon="Delete"
                                            @click="removeItem(tab.type, index)">移除</el-button>
                                    </div>
                                </template>
                            </draggable>
                            <div v-if="tabData[tab.type].length === 0" class="empty-tip">暂无关联数据</div>
                        </el-tab-pane>
                    </el-tabs>
                </el-card>
                <el-card v-else shadow="never" class="detail-card">
                    <el-empty description="请选择左侧版本查看详情" />
                </el-card>
            </el-col>
        </el-row>

        <!-- 新建版本弹窗 -->
        <el-dialog title="新建版本" v-model="createOpen" width="560px" append-to-body destroy-on-close>
            <el-form :model="createForm" :rules="createRules" :validate-on-rule-change="false" ref="createFormRef"
                label-width="120px">
                <el-form-item label="版本号" prop="version">
                    <el-input v-model="createForm.version" placeholder="如 1.2.0" />
                </el-form-item>
                <el-form-item label="版本描述">
                    <el-input v-model="createForm.description" placeholder="版本描述" />
                </el-form-item>
                <el-form-item label="强制更新">
                    <el-switch v-model="createForm.isForce" :active-value="1" :inactive-value="0"
                        active-text="强更" inactive-text="不强更" />
                </el-form-item>
                <el-form-item label="安卓下载地址">
                    <el-input v-model="createForm.androidUrl" placeholder="https://..." />
                </el-form-item>
                <el-form-item label="iOS下载地址">
                    <el-input v-model="createForm.iosUrl" placeholder="https://..." />
                </el-form-item>
                <el-form-item v-if="versionList.length > 0" label="继承上一版本">
                    <el-switch v-model="createForm.inheritFromLast" />
                    <div class="el-form-item__tip">开启后自动复制上一版本(最大index)的上线流程/图标应用/快捷入口配置</div>
                </el-form-item>
            </el-form>
            <template #footer>
                <el-button type="primary" :loading="createLoading" @click="submitCreate">确 定</el-button>
                <el-button @click="createOpen = false">取 消</el-button>
            </template>
        </el-dialog>

        <!-- 添加关联对象弹窗 -->
        <el-dialog :title="`添加${addTab?.label ?? ''}`" v-model="addOpen" width="640px" append-to-body destroy-on-close>
            <el-input v-model="addSearch" placeholder="输入名称搜索" clearable @change="loadCandidates" style="margin-bottom: 10px">
                <template #append>
                    <el-button icon="Search" @click="loadCandidates" />
                </template>
            </el-input>
            <el-table ref="candTableRef" :data="candidateList" v-loading="candLoading" size="small" max-height="360"
                @selection-change="handleCandSelect">
                <el-table-column type="selection" width="50" align="center"
                    :selectable="(row) => !existingIds(addTab?.type).includes(String(row.id))" />
                <el-table-column label="名称" prop="name" show-overflow-tooltip />
                <el-table-column label="ID" prop="id" width="120" align="center" />
                <el-table-column label="已关联" width="80" align="center">
                    <template #default="scope">
                        <el-tag v-if="existingIds(addTab?.type).includes(String(scope.row.id))" type="info" size="small">已关联</el-tag>
                    </template>
                </el-table-column>
            </el-table>
            <template #footer>
                <el-button type="primary" @click="confirmAdd">添加所选 ({{ candSelected.length }})</el-button>
                <el-button @click="addOpen = false">取 消</el-button>
            </template>
        </el-dialog>
    </div>
</template>

<script setup>
import { ref, reactive, toRefs, computed, onMounted, nextTick } from "vue";
import draggable from "vuedraggable";
import {
    getVersionListPage, saveVersion, updateVersion, publishVersion, deleteVersion,
    getCandidates, getAppDatas, saveAppDatas, getQrCode,
} from "@/api/workflow/appVersionApi";
const { proxy } = getCurrentInstance();
const tabs = [
    { type: 1, label: "图标应用" },
    { type: 2, label: "上线流程" },
    { type: 3, label: "快捷入口" },
];

const versionList = ref([]);
const listLoading = ref(false);
const currentId = ref(null);
const current = computed(() => versionList.value.find(v => v.id === currentId.value) ?? null);
const activeTab = ref("2");
const tabData = reactive({ 1: [], 2: [], 3: [] });
const tabDirty = reactive({ 1: false, 2: false, 3: false });
const tabSaving = ref(false);
const saveLoading = ref(false);
const qrCode = ref("");

const data = reactive({
    form: { version: "", description: "", isForce: 0, androidUrl: "", iosUrl: "", downloadCode: "", effectiveTime: "" },
    formRules: {
        version: [{ required: true, message: "请输入版本号", trigger: ["change", "blur"] }],
    },
    createForm: { version: "", description: "", isForce: 0, androidUrl: "", iosUrl: "", inheritFromLast: true },
    createRules: {
        version: [{ required: true, message: "请输入版本号", trigger: ["change", "blur"] }],
    },
});
const { form, formRules, createForm, createRules } = toRefs(data);

const createOpen = ref(false);
const createLoading = ref(false);
const addOpen = ref(false);
const addTab = ref(null);
const addSearch = ref("");
const candidateList = ref([]);
const candLoading = ref(false);
const candSelected = ref([]);

// 列表按id倒序(与后端一致), 线上版本 = 第一个已发布记录(id最大即index最大)
const onlineId = computed(() => {
    return versionList.value.find(v => Number(v.isHide) === 0)?.id ?? null;
});

function isDraft(row) {
    return Number(row.isHide) === 1;
}
function isOnline(row) {
    return row.id === onlineId.value;
}
function existingIds(type) {
    return (tabData[type] ?? []).map(i => String(i.id));
}

onMounted(() => {
    loadList();
});

/** 版本列表(取一页大pageSize, 版本数量少) */
async function loadList(selectId) {
    listLoading.value = true;
    try {
        const res = await getVersionListPage({ page: 1, pageSize: 200 }, {});
        versionList.value = res.data ?? [];
        const target = selectId ?? currentId.value ?? versionList.value[0]?.id;
        if (target && versionList.value.some(v => v.id === target)) {
            await selectVersion(target, true);
        } else if (versionList.value.length > 0) {
            await selectVersion(versionList.value[0].id, true);
        } else {
            currentId.value = null;
        }
        loadQrCode();
    } finally {
        listLoading.value = false;
    }
}

async function loadQrCode() {
    try {
        if (versionList.value.length === 0) {
            qrCode.value = "";
            return;
        }
        const res = await getQrCode();
        qrCode.value = res.data?.downloadCode ?? "";
        form.value.downloadCode = form.value.downloadCode || qrCode.value;
    } catch (e) {
        qrCode.value = "";
    }
}

/** 选中版本 */
async function selectVersion(id, force = false) {
    if (!force && hasDirty()) {
        try {
            await proxy.$modal.confirm("有未保存的修改, 切换将丢弃, 是否继续?");
        } catch (e) {
            return;
        }
    }
    currentId.value = id;
    const row = versionList.value.find(v => v.id === id);
    form.value = {
        version: row.version,
        description: row.description,
        isForce: Number(row.isForce) === 1 ? 1 : 0,
        androidUrl: row.androidUrl,
        iosUrl: row.iosUrl,
        downloadCode: row.downloadCode || "",
        effectiveTime: row.effectiveTime,
    };
    tabDirty[1] = tabDirty[2] = tabDirty[3] = false;
    await Promise.all(tabs.map(t => loadTabData(t.type)));
    nextTick(() => proxy.$refs["formRef"]?.clearValidate());
}

/** 加载某个Tab已关联数据 */
async function loadTabData(type) {
    if (!currentId.value) return;
    const res = await getAppDatas(currentId.value, type);
    tabData[type] = res.data ?? [];
}

function hasDirty() {
    return tabDirty[1] || tabDirty[2] || tabDirty[3];
}

/** 新建版本 */
function handleCreate() {
    createForm.value = { version: "", description: "", isForce: 0, androidUrl: "", iosUrl: "", inheritFromLast: true };
    createOpen.value = true;
    nextTick(() => proxy.$refs["createFormRef"]?.clearValidate());
}

async function submitCreate() {
    proxy.$refs["createFormRef"].validate(async valid => {
        if (!valid) return;
        createLoading.value = true;
        try {
            const res = await saveVersion({ ...createForm.value });
            if (res && (res.success === true || res.code == 200)) {
                proxy.$modal.msgSuccess("新建成功");
                createOpen.value = false;
                await loadList();
                //选中新建的草稿(列表第一项)
                await selectVersion(versionList.value[0].id, true);
            } else {
                proxy.$modal.msgError(res?.errMsg || res?.msg || "新建失败");
            }
        } finally {
            createLoading.value = false;
        }
    });
}

/** 保存基本信息(草稿全量/已发布运营参数) */
async function submitBaseInfo(published) {
    proxy.$refs["formRef"].validate(async valid => {
        if (!valid) return;
        saveLoading.value = true;
        try {
            const payload = published
                ? { isForce: form.value.isForce, androidUrl: form.value.androidUrl, iosUrl: form.value.iosUrl, downloadCode: form.value.downloadCode }
                : { ...form.value };
            const res = await updateVersion(currentId.value, payload);
            if (res && (res.success === true || res.code == 200)) {
                proxy.$modal.msgSuccess("保存成功");
                await loadList();
                await selectVersion(currentId.value, true);
            } else {
                proxy.$modal.msgError(res?.errMsg || res?.msg || "保存失败");
            }
        } finally {
            saveLoading.value = false;
        }
    });
}

/** 发布 */
async function handlePublish() {
    if (hasDirty()) {
        proxy.$modal.msgWarning("存在未保存的关联数据修改, 请先保存或放弃");
        return;
    }
    proxy.$modal.confirm(`确定发布版本[${current.value.version}]吗? 发布后立即对App端生效, 且关联数据将变为只读`).then(async () => {
        const res = await publishVersion(currentId.value);
        if (res && (res.success === true || res.code == 200)) {
            proxy.$modal.msgSuccess("发布成功");
            await loadList();
            await selectVersion(currentId.value, true);
        } else {
            proxy.$modal.msgError(res?.errMsg || res?.msg || "发布失败");
        }
    }).catch(() => { });
}

/** 删除草稿 */
function handleDelete() {
    proxy.$modal.confirm(`确定删除草稿版本[${current.value.version}]吗?`).then(async () => {
        const res = await deleteVersion(currentId.value);
        if (res && (res.success === true || res.code == 200)) {
            proxy.$modal.msgSuccess("删除成功");
            await loadList();
        } else {
            proxy.$modal.msgError(res?.errMsg || res?.msg || "删除失败");
        }
    }).catch(() => { });
}

/** 打开添加弹窗 */
function openAddDialog(tab) {
    addTab.value = tab;
    addSearch.value = "";
    candSelected.value = [];
    addOpen.value = true;
    loadCandidates();
}

async function loadCandidates() {
    if (!addTab.value) return;
    candLoading.value = true;
    try {
        const res = await getCandidates(addTab.value.type, addSearch.value);
        candidateList.value = (res.data ?? []).map(o => ({ id: String(o.id), name: o.name }));
    } finally {
        candLoading.value = false;
    }
}

function handleCandSelect(rows) {
    candSelected.value = rows;
}

/** 确认添加: 本地合并, 未落库 */
function confirmAdd() {
    if (candSelected.value.length === 0) {
        proxy.$modal.msgWarning("请选择要添加的对象");
        return;
    }
    const type = addTab.value.type;
    const exists = existingIds(type);
    candSelected.value.forEach(row => {
        if (!exists.includes(String(row.id))) {
            tabData[type].push({ id: String(row.id), name: row.name, sort: tabData[type].length + 1 });
            exists.push(String(row.id));
        }
    });
    resort(type);
    tabDirty[type] = true;
    addOpen.value = false;
}

/** 移除一行 */
function removeItem(type, index) {
    tabData[type].splice(index, 1);
    resort(type);
    tabDirty[type] = true;
}

/** 按行序重排sort */
function resort(type) {
    tabData[type].forEach((item, idx) => { item.sort = idx + 1; });
}

/** 拖拽回调(vuedraggable) */
function onDragEnd(type) {
    resort(type);
    tabDirty[type] = true;
}

/** 保存某个Tab(全量替换) */
async function saveTab(type) {
    tabSaving.value = true;
    try {
        const res = await saveAppDatas({
            versionId: currentId.value,
            type,
            items: tabData[type].map((item, idx) => ({ id: String(item.id), name: item.name, sort: idx + 1 })),
        });
        if (res && (res.success === true || res.code == 200)) {
            proxy.$modal.msgSuccess("保存成功");
            tabDirty[type] = false;
            await loadTabData(type);
        } else {
            proxy.$modal.msgError(res?.errMsg || res?.msg || "保存失败");
        }
    } finally {
        tabSaving.value = false;
    }
}

function handleCopyCode() {
    if (!form.value.downloadCode) return;
    navigator.clipboard?.writeText(form.value.downloadCode).then(() => {
        proxy.$modal.msgSuccess("已复制");
    }).catch(() => { });
}
</script>

<style scoped lang="scss">
.app-version-page {
    .card-header {
        display: flex;
        justify-content: space-between;
        align-items: center;
    }

    .version-item {
        padding: 8px 10px;
        border: 1px solid #e4e7ed;
        border-radius: 4px;
        margin-bottom: 8px;
        cursor: pointer;

        &:hover {
            border-color: #a0cfff;
        }

        &.active {
            border-color: #409eff;
            background-color: #ecf5ff;
        }

        .version-item-main {
            display: flex;
            align-items: center;
            gap: 6px;

            .version-name {
                font-weight: 600;
                flex: 1;
            }
        }

        .version-item-sub {
            display: flex;
            justify-content: space-between;
            color: #909399;
            font-size: 12px;
            margin-top: 4px;
        }
    }

    .base-form {
        margin-bottom: 8px;
    }

    .qr-box {
        display: flex;
        gap: 8px;
    }

    .data-tabs {
        .tab-toolbar {
            display: flex;
            align-items: center;
            gap: 8px;
            margin-bottom: 8px;

            .dirty-tip {
                color: #e6a23c;
                font-size: 12px;
            }
        }

        .drag-list {
            .drag-row {
                display: flex;
                align-items: center;
                gap: 10px;
                padding: 7px 10px;
                border: 1px solid #e4e7ed;
                border-radius: 4px;
                margin-bottom: 6px;
                background: #fff;

                .drag-handle {
                    cursor: move;
                    color: #909399;
                }

                .drag-name {
                    flex: 1;
                    overflow: hidden;
                    text-overflow: ellipsis;
                    white-space: nowrap;
                }

                .drag-id {
                    color: #909399;
                    font-size: 12px;
                    width: 110px;
                    text-align: right;
                }

                .drag-sort {
                    color: #909399;
                    font-size: 12px;
                    width: 40px;
                    text-align: right;
                }
            }
        }

        .tab-badge {
            margin-left: 4px;
        }
    }

    .empty-tip {
        color: #909399;
        text-align: center;
        padding: 12px 0;
        font-size: 13px;
    }
}
</style>
