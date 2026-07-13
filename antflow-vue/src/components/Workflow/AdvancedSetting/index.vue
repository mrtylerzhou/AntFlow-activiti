<template>
    <div class="form-container">
        <div class="setting-cards">
            <!-- 审批人去重 -->
            <div class="setting-card">
                <div class="setting-card-header">
                    <div class="setting-card-title">
                        <el-icon><DocumentCopy /></el-icon>
                        审批人去重
                    </div>
                </div>
                <div class="setting-card-body">
                    <p class="setting-card-desc">当同一审批人在流程中多次出现时，按照设定的去重策略自动处理，避免同一人重复审批相同流程。</p>
                    <el-select v-model="form.deduplicationType" placeholder="请选择去重策略" style="width: 100%;">
                        <el-option v-for="(item, index) in duplicateOptions" :key="index" :label="item.label"
                            :value="item.value"></el-option>
                    </el-select>
                </div>
            </div>

            <!-- 允许撤回 -->
            <div class="setting-card">
                <div class="setting-card-header">
                    <div class="setting-card-title">
                        <el-icon><RefreshLeft /></el-icon>
                        允许撤回
                    </div>
                    <el-switch v-model="form.allowDrawBack" />
                </div>
                <div class="setting-card-body">
                    <p class="setting-card-desc">开启后，发起人可在【我的发起】查看页撤回已提交的流程。撤回后流程将回到发起人处，可重新编辑并提交。</p>
                </div>
            </div>

            <!-- 允许作废 -->
            <div class="setting-card">
                <div class="setting-card-header">
                    <div class="setting-card-title">
                        <el-icon><Delete /></el-icon>
                        允许作废
                    </div>
                    <el-switch v-model="form.allowAbandoned" />
                </div>
                <div class="setting-card-body">
                    <p class="setting-card-desc">开启后，发起人可在【我的发起】查看页作废已提交的流程。作废后流程将终止，不可恢复。</p>
                </div>
            </div>

            <!-- 允许转发 -->
            <div class="setting-card">
                <div class="setting-card-header">
                    <div class="setting-card-title">
                        <el-icon><Promotion /></el-icon>
                        允许转发
                    </div>
                    <el-switch v-model="form.allowForward" />
                </div>
                <div class="setting-card-body">
                    <p class="setting-card-desc">开启后，所有流程相关人员可在查看页将流程转发（抄送）给其他人。被转发人可在【抄送我的】列表中查看该流程详情。</p>
                </div>
            </div>
        </div>
    </div>
</template>

<script setup>
import { ref, reactive, onMounted, watch, getCurrentInstance } from 'vue'
const { proxy } = getCurrentInstance()
const emit = defineEmits(['nextChange'])
let props = defineProps({
    basicData: {
        type: Object,
        default: () => (null),
    }
});

const duplicateOptions = [{
    "label": "不去重",
    "value": 1
}, {
    "label": "前去重",
    "value": 2
}, {
    "label": "后去重",
    "value": 3
}, {
    "label": "相邻节点去重",
    "value": 4
}];

const BUTTON_DRAW_BACK = 29;
const BUTTON_ABANDONED = 7;
const BUTTON_FORWARD = 15;

const form = reactive({
    deduplicationType: 1,
    allowDrawBack: false,
    allowAbandoned: false,
    allowForward: false,
    // 保留原始数据，提交时需要
    viewPageButtons: {
        viewPageStart: [],
        viewPageOther: [],
    }
})

onMounted(() => {
    if (!proxy.isEmpty(props.basicData)) {
        form.deduplicationType = props.basicData.deduplicationType || 1;
        if (props.basicData.viewPageButtons) {
            form.viewPageButtons = JSON.parse(JSON.stringify(props.basicData.viewPageButtons));
            form.allowDrawBack = (form.viewPageButtons.viewPageStart || []).includes(BUTTON_DRAW_BACK);
            form.allowAbandoned = (form.viewPageButtons.viewPageStart || []).includes(BUTTON_ABANDONED);
            form.allowForward = (form.viewPageButtons.viewPageStart || []).includes(BUTTON_FORWARD);
        }
    }
});

// 构建viewPageButtons
const buildViewPageButtons = () => {
    let viewPageStart = [];
    let viewPageOther = [];
    if (form.allowDrawBack) {
        viewPageStart.push(BUTTON_DRAW_BACK);
    }
    if (form.allowAbandoned) {
        viewPageStart.push(BUTTON_ABANDONED);
    }
    if (form.allowForward) {
        viewPageStart.push(BUTTON_FORWARD);
        viewPageOther.push(BUTTON_FORWARD);
    }
    return { viewPageStart, viewPageOther };
}

const getData = () => {
    return new Promise((resolve, reject) => {
        const viewPageButtons = buildViewPageButtons();
        resolve({
            formData: {
                deduplicationType: form.deduplicationType,
                viewPageButtons: viewPageButtons,
            }
        });
    })
};

defineExpose({
    getData
})
</script>

<style scoped>
.form-container {
    background: white !important;
    padding: 20px;
    max-width: 750px;
    min-height: 80vh;
    left: 0;
    bottom: 0;
    right: 0;
    margin: auto;
}

.setting-cards {
    display: flex;
    flex-direction: column;
    gap: 16px;
}

.setting-card {
    border: 1px solid #e4e7ed;
    border-radius: 8px;
    padding: 20px;
    background: #fff;
    transition: box-shadow 0.3s;
}

.setting-card:hover {
    box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.06);
}

.setting-card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 12px;
}

.setting-card-title {
    font-size: 16px;
    font-weight: 600;
    color: #303133;
    display: flex;
    align-items: center;
    gap: 8px;
}

.setting-card-title .el-icon {
    color: #409eff;
    font-size: 18px;
}

.setting-card-body {
    padding-left: 26px;
}

.setting-card-desc {
    font-size: 13px;
    color: #909399;
    line-height: 1.6;
    margin: 0 0 12px 0;
}
</style>
