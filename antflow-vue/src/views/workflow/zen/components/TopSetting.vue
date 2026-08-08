<template>
    <div class="top-setting" v-if="config">
        <div class="top-setting-group">
            <span class="group-label">基础设置</span>
            <div class="group-items">
                <div class="group-item">
                    <span class="item-label">类型标识</span>
                    <span class="item-value readonly">{{ config.formCode }}</span>
                </div>
                <div class="group-item">
                    <span class="item-label">流程名称</span>
                    <span class="item-value readonly">{{ config.bpmnName }}</span>
                </div>
                <div class="group-item" v-if="flowType === 'LF'">
                    <span class="item-label">外部表单</span>
                    <el-tag size="small" :type="isExternalForm ? 'success' : 'info'" effect="plain">
                        {{ isExternalForm ? '使用外部表单' : '内联表单' }}
                    </el-tag>
                </div>
                <div class="group-item" v-else>
                    <span class="item-label">辅助表单</span>
                    <el-tag size="small" :type="isAuxiliaryForm ? 'success' : 'info'" effect="plain">
                        {{ isAuxiliaryForm ? '使用辅助表单' : '未使用' }}
                    </el-tag>
                </div>
                <div class="group-item grow">
                    <span class="item-label">流程说明</span>
                    <el-input v-model="config.remark" type="textarea" placeholder="请输入流程说明"
                        :maxlength="100" show-word-limit :autosize="{ minRows: 1, maxRows: 2 }"
                        style="flex:1;" />
                </div>
            </div>
        </div>
        <div class="top-setting-group">
            <span class="group-label">高级设置</span>
            <div class="group-items">
                <div class="group-item">
                    <span class="item-label">审批人去重</span>
                    <el-select v-model="config.deduplicationType" placeholder="请选择去重策略" size="small"
                        style="width: 140px;">
                        <el-option v-for="(item, index) in duplicateOptions" :key="index" :label="item.label"
                            :value="item.value" />
                    </el-select>
                </div>
                <div class="group-item switch-item">
                    <span class="item-label">允许撤回</span>
                    <el-switch v-model="allowDrawBack" />
                </div>
                <div class="group-item switch-item">
                    <span class="item-label">允许作废</span>
                    <el-switch v-model="allowAbandoned" />
                </div>
                <div class="group-item switch-item">
                    <span class="item-label">允许转发</span>
                    <el-switch v-model="allowForward" />
                </div>
            </div>
        </div>
    </div>
</template>
<script setup>
import { ref, computed, watch } from 'vue';

const props = defineProps({
    config: {
        type: Object,
        default: () => null
    },
    flowType: {
        type: String,
        default: 'LF'
    }
});

const duplicateOptions = [{
    label: '不去重',
    value: 1
}, {
    label: '前去重',
    value: 2
}, {
    label: '后去重',
    value: 3
}, {
    label: '相邻节点去重',
    value: 4
}];

const USE_EXTERNAL_FORM_FLAG = 64;
const USE_AUXILIARY_FORM_FLAG = 128;

const isExternalForm = computed(() => {
    const flags = Number(props.config?.extraFlags || 0);
    return (flags & USE_EXTERNAL_FORM_FLAG) === USE_EXTERNAL_FORM_FLAG;
});
const isAuxiliaryForm = computed(() => {
    const flags = Number(props.config?.extraFlags || 0);
    return (flags & USE_AUXILIARY_FORM_FLAG) === USE_AUXILIARY_FORM_FLAG;
});

// 高级设置开关（直接写回 config.viewPageButtons，与完整编辑一致）
const BUTTON_DRAW_BACK = 29;
const BUTTON_ABANDONED = 7;
const BUTTON_FORWARD = 15;

const allowDrawBack = computed({
    get() {
        return (props.config?.viewPageButtons?.viewPageStart || []).includes(BUTTON_DRAW_BACK);
    },
    set(val) {
        updateViewPageStart(BUTTON_DRAW_BACK, val);
    }
});
const allowAbandoned = computed({
    get() {
        return (props.config?.viewPageButtons?.viewPageStart || []).includes(BUTTON_ABANDONED);
    },
    set(val) {
        updateViewPageStart(BUTTON_ABANDONED, val);
    }
});
const allowForward = computed({
    get() {
        return (props.config?.viewPageButtons?.viewPageStart || []).includes(BUTTON_FORWARD);
    },
    set(val) {
        updateViewPageStart(BUTTON_FORWARD, val);
        const other = props.config.viewPageButtons?.viewPageOther || [];
        if (val && !other.includes(BUTTON_FORWARD)) {
            other.push(BUTTON_FORWARD);
        }
        if (!val) {
            props.config.viewPageButtons.viewPageOther = other.filter(b => b !== BUTTON_FORWARD);
        }
    }
});

const updateViewPageStart = (button, add) => {
    if (!props.config.viewPageButtons) {
        props.config.viewPageButtons = { viewPageStart: [], viewPageOther: [] };
    }
    const list = props.config.viewPageButtons.viewPageStart;
    if (add && !list.includes(button)) {
        list.push(button);
    }
    if (!add) {
        props.config.viewPageButtons.viewPageStart = list.filter(b => b !== button);
    }
};
</script>
<style scoped lang="scss">
.top-setting {
    display: flex;
    gap: 16px;
    background: #fff;
    padding: 10px 16px;
    border-radius: 6px;
    box-shadow: 0 1px 4px rgba(0, 0, 0, 0.08);
    margin-bottom: 10px;
}

.top-setting-group {
    display: flex;
    align-items: flex-start;
    gap: 12px;
    flex: 1;
}

.group-label {
    font-size: 14px;
    font-weight: 600;
    color: #303133;
    white-space: nowrap;
    padding-top: 4px;
    width: 64px;
}

.group-items {
    display: flex;
    flex-wrap: wrap;
    gap: 8px 16px;
    flex: 1;
    align-items: center;
}

.group-item {
    display: flex;
    align-items: center;
    gap: 6px;
    font-size: 13px;

    &.grow {
        flex: 1;
        min-width: 200px;
    }
}

.item-label {
    color: #909399;
    white-space: nowrap;
}

.item-value.readonly {
    color: #303133;
    font-weight: 500;
}

.switch-item {
    gap: 4px;
}
</style>
