<template>
    <el-drawer v-model="visible" title="表单字段变更记录" direction="rtl" :size="680"
        :destroy-on-close="true" :with-header="true">
        <div v-loading="loading" class="audit-drawer">
            <div v-if="!loading && (!groups || groups.length === 0)" class="empty">
                <el-empty description="该流程暂无字段变更记录" />
            </div>
            <template v-else>
                <!-- 展开/折叠控制条 -->
                <div class="audit-toolbar">
                    <el-button link type="primary" size="small" @click="toggleAll">
                        <el-icon>
                            <Fold v-if="allExpanded" />
                            <Expand v-else />
                        </el-icon>&nbsp;{{ allExpanded ? '全部折叠' : '全部展开' }}
                    </el-button>
                    <span class="audit-total">共 {{ groups.length }} 个节点</span>
                </div>

                <el-timeline>
                    <el-timeline-item v-for="(group, idx) in groups" :key="group.taskDefKey + '_' + idx"
                        :timestamp="group.createTime" placement="top" type="primary">
                        <div class="audit-node-card">
                            <!-- 节点头部: 点击折叠/展开 -->
                            <div class="audit-node-header" @click="toggleGroup(group)">
                                <el-icon class="audit-toggle-icon">
                                    <ArrowDown v-if="isExpanded(group)" />
                                    <ArrowRight v-else />
                                </el-icon>
                                <span class="audit-node-name">
                                    <el-tag size="small" type="primary">{{ group.taskName || group.taskDefKey || '未知节点' }}</el-tag>
                                </span>
                                <span class="audit-node-user">{{ displayUser(group) }}</span>
                                <span class="audit-node-count">{{ group.fields.length }} 个字段</span>
                            </div>
                            <!-- 折叠内容: 字段明细表 -->
                            <el-collapse-transition>
                                <div v-show="isExpanded(group)">
                                    <el-table :data="group.fields" size="small" border :show-header="true" class="audit-field-table">
                                        <el-table-column label="字段" min-width="140">
                                            <template #default="scope">
                                                <el-tooltip :content="scope.row.fieldName" placement="top" :show-after="200">
                                                    <span class="audit-field-label">{{ displayLabel(scope.row) }}</span>
                                                </el-tooltip>
                                            </template>
                                        </el-table-column>
                                        <el-table-column label="旧值" min-width="150">
                                            <template #default="scope">
                                                <span :class="{ 'audit-changed': !isSame(scope.row) }">{{ formatValue(scope.row.oldValue) }}</span>
                                            </template>
                                        </el-table-column>
                                        <el-table-column label="新值" min-width="150">
                                            <template #default="scope">
                                                <span :class="{ 'audit-changed': !isSame(scope.row) }">{{ formatValue(scope.row.newValue) }}</span>
                                            </template>
                                        </el-table-column>
                                        <el-table-column label="变更人" min-width="90">
                                            <template #default="scope">
                                                {{ displayName(scope.row) }}
                                            </template>
                                        </el-table-column>
                                    </el-table>
                                </div>
                            </el-collapse-transition>
                        </div>
                    </el-timeline-item>
                </el-timeline>
            </template>
        </div>
    </el-drawer>
</template>

<script setup>
import { computed, ref, watch } from 'vue';
import { ArrowDown, ArrowRight, Expand, Fold } from '@element-plus/icons-vue';
import { getProcessAudits } from '@/api/workflow/index';
import { parseTime } from '@/utils/ruoyi';

const props = defineProps({
    modelValue: { type: Boolean, default: false },
    processNumber: { type: String, default: '' },
});
const emits = defineEmits(['update:modelValue']);

const visible = computed({
    get() {
        return props.modelValue;
    },
    set(v) {
        emits('update:modelValue', v);
    },
});

const loading = ref(false);
const records = ref([]);

// 用独立 Set 记录展开的 taskDefKey.
// 原因: groups 是 computed, computed 内新建的普通对象非 reactive,
//        直接 group.expanded = !group.expanded 不会触发渲染.
//        把展开状态抽到顶层 ref(Set), 通过 v-show 读取.
const expandedKeys = ref(new Set());

// 按 taskDefKey 分组
const groups = computed(() => {
    if (!records.value || records.value.length === 0) {
        return [];
    }
    const map = new Map();
    for (const r of records.value) {
        const key = r.taskDefKey || 'unknown';
        if (!map.has(key)) {
            map.set(key, {
                taskDefKey: r.taskDefKey,
                taskName: r.taskName,
                createUser: r.createUser,
                createUserName: r.createUserName,
                createTime: formatTime(r.createTime),
                fields: [],
            });
        }
        const g = map.get(key);
        g.fields.push({
            fieldName: r.fieldName,
            fieldLabel: r.fieldLabel,
            oldValue: r.oldValue,
            newValue: r.newValue,
            createUser: r.createUser,
            createUserName: r.createUserName,
        });
        if (!g.createUser && r.createUser) g.createUser = r.createUser;
        if (!g.createUserName && r.createUserName) g.createUserName = r.createUserName;
    }
    return Array.from(map.values());
});

// 默认仅第一个节点展开
watch(groups, (arr) => {
    if (arr && arr.length > 0 && expandedKeys.value.size === 0) {
        const firstKey = arr[0].taskDefKey || 'unknown';
        expandedKeys.value = new Set([firstKey]);
    }
}, { immediate: true });

function groupKey(g) {
    return g.taskDefKey || 'unknown';
}

function isExpanded(g) {
    return expandedKeys.value.has(groupKey(g));
}

function toggleGroup(g) {
    const key = groupKey(g);
    // 重新 new Set 触发响应式 (Vue 3 ref 对 Set 的 add/delete 不会自动触发 ref 更新)
    const next = new Set(expandedKeys.value);
    if (next.has(key)) {
        next.delete(key);
    } else {
        next.add(key);
    }
    expandedKeys.value = next;
}

function expandAll() {
    expandedKeys.value = new Set(groups.value.map(groupKey));
}

function collapseAll() {
    expandedKeys.value = new Set();
}

// 当前是否全部展开 (用于切换按钮文案)
const allExpanded = computed(() => {
    const arr = groups.value;
    if (!arr || arr.length === 0) {
        return false;
    }
    return arr.every(g => isExpanded(g));
});

// 全部展开/全部折叠 切换
function toggleAll() {
    if (allExpanded.value) {
        collapseAll();
    } else {
        expandAll();
    }
}

function formatTime(t) {
    if (!t) return '';
    return parseTime(t, '{y}-{m}-{d} {h}:{i}:{s}');
}

function formatValue(v) {
    if (v === null || v === undefined || v === '') {
        return '(空)';
    }
    return String(v);
}

function isSame(row) {
    return (row.oldValue || '') === (row.newValue || '');
}

/**
 * 默认显示 label, 没有 label 时 fallback 到 fieldName.
 */
function displayLabel(row) {
    if (row.fieldLabel && String(row.fieldLabel).trim()) {
        return row.fieldLabel;
    }
    return row.fieldName || '(未知字段)';
}

/**
 * 变更人姓名, 没有姓名时 fallback 到 empId.
 */
function displayName(row) {
    if (row.createUserName && String(row.createUserName).trim()) {
        return row.createUserName;
    }
    return row.createUser || '-';
}

/**
 * 节点头部变更人: 优先 group 级姓名, fallback empId.
 */
function displayUser(group) {
    if (group.createUserName && String(group.createUserName).trim()) {
        return group.createUserName;
    }
    return group.createUser || '';
}

async function load() {
    if (!props.processNumber) {
        records.value = [];
        return;
    }
    loading.value = true;
    try {
        const res = await getProcessAudits(props.processNumber);
        if (res && res.code === 200) {
            records.value = Array.isArray(res.data) ? res.data : [];
        } else {
            records.value = [];
        }
    } catch (e) {
        console.error('getProcessAudits failed', e);
        records.value = [];
    } finally {
        loading.value = false;
    }
}

watch(() => props.modelValue, (v) => {
    if (v) {
        load();
    }
});
watch(() => props.processNumber, () => {
    if (props.modelValue) {
        load();
    }
});
</script>

<style lang="scss" scoped>
.audit-drawer {
    padding: 0 8px;
}

.empty {
    display: flex;
    align-items: center;
    justify-content: center;
    min-height: 300px;
}

.audit-toolbar {
    display: flex;
    align-items: center;
    gap: 4px;
    margin-bottom: 10px;
}

.audit-total {
    margin-left: auto;
    font-size: 12px;
    color: #909399;
}

.audit-node-card {
    border: 1px solid #e4e7ed;
    border-radius: 6px;
    background-color: #fafbfc;
    margin-bottom: 8px;
}

.audit-node-header {
    display: flex;
    align-items: center;
    gap: 8px;
    padding: 8px 12px;
    font-size: 13px;
    color: #606266;
    cursor: pointer;
    user-select: none;

    &:hover {
        background-color: #f0f2f5;
        border-radius: 6px 6px 0 0;
    }
}

.audit-toggle-icon {
    font-size: 14px;
    color: #909399;
}

.audit-node-name {
    font-weight: 600;
}

.audit-node-user {
    color: #409eff;
}

.audit-node-count {
    margin-left: auto;
    font-size: 12px;
    color: #909399;
}

.audit-field-table {
    background-color: #fff;
    border-top: 1px solid #e4e7ed;
}

.audit-changed {
    color: #f56c6c;
    font-weight: 600;
}

.audit-field-label {
    cursor: help;
    border-bottom: 1px dotted #c0c4cc;
}

:deep(.el-timeline-item__node--primary) {
    background-color: var(--el-color-primary);
}
</style>