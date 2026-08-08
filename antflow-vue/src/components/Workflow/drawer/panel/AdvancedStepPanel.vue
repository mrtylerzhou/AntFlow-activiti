<template>
    <div v-if="props.approverConfig" class="advanced-setting-content">
        <div class="setting-group">
            <p class="setting-group-title">流程标签</p>
            <el-select v-model="selectedLabelValues" multiple filterable clearable
                placeholder="请选择流程标签" style="width: 100%">
                <el-option v-for="item in labelOptions" :key="item.id" :label="item.name"
                    :value="item.id" />
            </el-select>
            <p class="tip">为当前节点设置流程标签，用于后续分类和统计</p>
        </div>
        <div class="setting-group" v-if="props.approverConfig.nodeType == 4 || props.approverConfig.nodeType == 17">
            <p class="setting-group-title">批量审批</p>
            <el-switch v-model="batchProhibited" active-text="禁止批量审批" />
            <p class="tip">开启后，该节点的待办任务不允许被批量同意</p>
        </div>
    </div>
</template>
<script setup>
import { ref, watch, computed, onMounted } from 'vue';
import { getDictDataByType } from '@/api/workflow/index';

const props = defineProps({
    /** 当前节点数据（直接 mutate，drawer 传副本、Zen 传副本树节点） */
    approverConfig: {
        type: Object,
        default: () => ({})
    }
});
const { proxy } = getCurrentInstance();

const labelOptions = ref([]);
const selectedLabelValues = ref([]);

/** 禁止批量审批开关: batchStatus==0 表示禁止 */
const batchProhibited = computed({
    get() {
        return props.approverConfig?.batchStatus === 0;
    },
    set(val) {
        if (props.approverConfig) {
            props.approverConfig.batchStatus = val ? 0 : 1;
        }
    }
});

/** 节点切换时反显流程标签 */
watch(() => props.approverConfig, (val) => {
    if (!val) return;
    selectedLabelValues.value = (val.labelList || []).map(l => l.labelValue);
}, { immediate: true });

/** 同步流程标签选择到 approverConfig.labelList */
watch(selectedLabelValues, (vals) => {
    if (!props.approverConfig) return;
    const existingList = props.approverConfig.labelList || [];
    props.approverConfig.labelList = (vals || []).map(v => {
        const opt = labelOptions.value.find(item => item.id === v);
        const existing = existingList.find(item => item.labelValue === v);
        return {
            labelValue: v,
            labelName: opt ? opt.name : (existing ? existing.labelName : '')
        };
    });
}, { deep: true });

const initLabelOptions = async () => {
    if (labelOptions.value.length > 0) {
        return;
    }
    try {
        const res = await getDictDataByType('processlabel');
        labelOptions.value = res.data || [];
    } catch (error) {
        proxy.$modal.msgError("获取流程标签失败");
    }
}

onMounted(() => {
    initLabelOptions();
});
</script>
<style scoped lang="scss">
.advanced-setting-content {
    padding: 10px 0;
}

.setting-group {
    padding: 10px 0;
    border-bottom: 1px solid #f2f2f2;
}

.setting-group-title {
    margin: 0 0 10px 0;
    font-size: 14px;
    font-weight: 600;
}

.tip {
    margin: 10px 0 0 0;
    font-size: 12px;
    line-height: 16px;
    color: #f8642d;
}
</style>
