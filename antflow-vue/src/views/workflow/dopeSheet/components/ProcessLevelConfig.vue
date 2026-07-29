<template>
  <div class="process-level-config">
    <div class="config-section">
      <div class="section-title">流程级配置</div>
      <div class="config-grid">
        <!-- 只读信息 -->
        <div class="config-item">
          <span class="config-label">版本编号</span>
          <span class="config-value">{{ processConfig.bpmnCode || '—' }}</span>
        </div>
        <div class="config-item">
          <span class="config-label">版本名称</span>
          <span class="config-value">{{ processConfig.bpmnName || '—' }}</span>
        </div>
        <div class="config-item">
          <span class="config-label">流程分类</span>
          <span class="config-value">{{ processConfig.isLowCodeFlow == '1' ? 'LF' : 'DIY' }}</span>
        </div>
        <!-- 可编辑：流程说明 -->
        <div class="config-item config-item-wide">
          <span class="config-label">流程说明</span>
          <el-input v-model="processConfig.remark" type="textarea" :rows="1" placeholder="请输入流程说明"
            @input="emitDirty" style="flex: 1;" />
        </div>
      </div>
    </div>

    <!-- 高级设置 -->
    <div class="config-section">
      <div class="section-title">高级设置</div>
      <div class="advanced-grid">
        <div class="advanced-item">
          <span class="advanced-label">审批人去重</span>
          <el-select v-model="processConfig.deduplicationType" size="small" style="width: 140px;" @change="emitDirty">
            <el-option v-for="item in duplicateOptions" :key="item.value" :label="item.label" :value="item.value" />
          </el-select>
        </div>
        <div class="advanced-item">
          <span class="advanced-label">允许撤回</span>
          <el-switch v-model="allowDrawBack" size="small" @change="onAdvancedChange" />
        </div>
        <div class="advanced-item">
          <span class="advanced-label">允许作废</span>
          <el-switch v-model="allowAbandoned" size="small" @change="onAdvancedChange" />
        </div>
        <div class="advanced-item">
          <span class="advanced-label">允许转发</span>
          <el-switch v-model="allowForward" size="small" @change="onAdvancedChange" />
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue';

const props = defineProps({
  processConfig: {
    type: Object,
    required: true
  }
});

const emit = defineEmits(['dirty']);

const duplicateOptions = [
  { label: "不去重", value: 1 },
  { label: "前去重", value: 2 },
  { label: "后去重", value: 3 },
  { label: "相邻节点去重", value: 4 }
];

const BUTTON_DRAW_BACK = 29;
const BUTTON_ABANDONED = 7;
const BUTTON_FORWARD = 15;

const allowDrawBack = ref(false);
const allowAbandoned = ref(false);
const allowForward = ref(false);

onMounted(() => {
  initAdvancedFromConfig();
});

watch(() => props.processConfig, () => {
  initAdvancedFromConfig();
}, { deep: false });

const initAdvancedFromConfig = () => {
  const vp = props.processConfig.viewPageButtons;
  if (vp) {
    const startBtns = vp.viewPageStart || [];
    allowDrawBack.value = startBtns.includes(BUTTON_DRAW_BACK);
    allowAbandoned.value = startBtns.includes(BUTTON_ABANDONED);
    allowForward.value = startBtns.includes(BUTTON_FORWARD);
  }
};

const onAdvancedChange = () => {
  // 重建 viewPageButtons
  let viewPageStart = [];
  let viewPageOther = [];
  if (allowDrawBack.value) viewPageStart.push(BUTTON_DRAW_BACK);
  if (allowAbandoned.value) viewPageStart.push(BUTTON_ABANDONED);
  if (allowForward.value) {
    viewPageStart.push(BUTTON_FORWARD);
    viewPageOther.push(BUTTON_FORWARD);
  }
  props.processConfig.viewPageButtons = { viewPageStart, viewPageOther };
  emitDirty();
};

const emitDirty = () => {
  emit('dirty');
};
</script>

<style scoped lang="scss">
.process-level-config {
  background: #fff;
  border-radius: 6px;
  padding: 16px 20px;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.06);
}

.config-section {
  margin-bottom: 12px;

  &:last-child {
    margin-bottom: 0;
  }
}

.section-title {
  font-size: 14px;
  font-weight: 600;
  color: #303133;
  margin-bottom: 12px;
  padding-left: 8px;
  border-left: 3px solid #409eff;
}

.config-grid {
  display: flex;
  flex-wrap: wrap;
  gap: 12px 24px;
}

.config-item {
  display: flex;
  align-items: center;
  gap: 8px;
}

.config-item-wide {
  flex: 1;
  min-width: 300px;
}

.config-label {
  font-size: 13px;
  color: #909399;
  white-space: nowrap;
}

.config-value {
  font-size: 13px;
  color: #303133;
}

.advanced-grid {
  display: flex;
  flex-wrap: wrap;
  gap: 16px 32px;
}

.advanced-item {
  display: flex;
  align-items: center;
  gap: 8px;
}

.advanced-label {
  font-size: 13px;
  color: #606266;
  white-space: nowrap;
}
</style>
