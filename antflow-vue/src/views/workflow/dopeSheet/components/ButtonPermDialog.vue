<template>
  <el-dialog title="按钮权限设置" v-model="dialogVisible" width="560px" append-to-body destroy-on-close>
    <div v-if="node">
      <p class="perm-section-title">【审批页面】按钮权限</p>
      <el-checkbox-group v-model="checkApprovalBtns" @change="onApprovalChange">
        <div class="btn-row" v-for="opt in approvalPageButtons" :key="opt.value">
          <el-checkbox :value="opt.value" :disabled="isArbitrationLocked(opt)">
            【{{ opt.label }}】
          </el-checkbox>
          <el-input class="btn-name-input" v-model="approvalCustomNames[opt.value]" maxlength="8"
            placeholder="自定义名称" size="small" :disabled="!checkApprovalBtns.includes(opt.value)" />
        </div>
      </el-checkbox-group>

      <p class="perm-section-title" style="margin-top: 16px;">【查看页面】按钮权限</p>
      <el-checkbox-group v-model="checkViewBtns">
        <div class="btn-row" v-for="opt in nodeViewPageButtons" :key="opt.value">
          <el-checkbox :value="opt.value">
            【{{ opt.label }}】
          </el-checkbox>
          <el-input class="btn-name-input" v-model="viewCustomNames[opt.value]" maxlength="8"
            placeholder="自定义名称" size="small" :disabled="!checkViewBtns.includes(opt.value)" />
        </div>
      </el-checkbox-group>
    </div>
    <template #footer>
      <el-button @click="dialogVisible = false">取消</el-button>
      <el-button type="primary" @click="handleConfirm">确定</el-button>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref, watch, computed } from 'vue';
import { approvalPageButtons, nodeViewPageButtons, approvalButtonConf } from '@/utils/antflow/const';

const props = defineProps({
  visible: Boolean,
  node: Object
});

const emit = defineEmits(['update:visible', 'confirm']);

const dialogVisible = computed({
  get: () => props.visible,
  set: (val) => emit('update:visible', val)
});

const checkApprovalBtns = ref([]);
const checkViewBtns = ref([]);
const approvalCustomNames = ref({});
const viewCustomNames = ref({});

// 仲裁签锁定：反对按钮必须勾选
const isArbitrationLocked = (opt) => {
  return props.node?.signType === 4 && opt.value === approvalButtonConf.oppose;
};

watch(() => props.visible, (val) => {
  if (val && props.node) {
    loadFromNode();
  }
});

const loadFromNode = () => {
  const node = props.node;
  // 审批页按钮
  const approvalPage = node.buttons?.approvalPage || [];
  checkApprovalBtns.value = approvalPage.map(b => b.buttonType);
  const aNames = {};
  approvalPageButtons.forEach(opt => {
    const item = approvalPage.find(b => b.buttonType === opt.value);
    aNames[opt.value] = (item && item.buttonName && item.buttonName !== opt.label) ? item.buttonName : '';
  });
  approvalCustomNames.value = aNames;

  // 查看页按钮
  const viewPage = node.buttons?.viewPage || [];
  checkViewBtns.value = viewPage.map(b => b.buttonType);
  const vNames = {};
  nodeViewPageButtons.forEach(opt => {
    const item = viewPage.find(b => b.buttonType === opt.value);
    vNames[opt.value] = (item && item.buttonName && item.buttonName !== opt.label) ? item.buttonName : '';
  });
  viewCustomNames.value = vNames;
};

const onApprovalChange = () => {
  // 仲裁签时确保反对按钮始终勾选
  if (props.node?.signType === 4 && !checkApprovalBtns.value.includes(approvalButtonConf.oppose)) {
    checkApprovalBtns.value.push(approvalButtonConf.oppose);
  }
};

const handleConfirm = () => {
  if (!props.node) return;
  if (!props.node.buttons) props.node.buttons = {};
  // 同步审批页
  props.node.buttons.approvalPage = checkApprovalBtns.value.map(bt => ({
    buttonType: bt,
    buttonName: approvalCustomNames.value[bt] || ''
  }));
  // 同步查看页
  props.node.buttons.viewPage = checkViewBtns.value.map(bt => ({
    buttonType: bt,
    buttonName: viewCustomNames.value[bt] || ''
  }));
  emit('confirm');
  dialogVisible.value = false;
};
</script>

<style scoped>
.perm-section-title {
  font-size: 13px;
  font-weight: 600;
  color: #303133;
  margin-bottom: 8px;
}

.btn-row {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 8px;
}

.btn-name-input {
  width: 140px;
}
</style>
