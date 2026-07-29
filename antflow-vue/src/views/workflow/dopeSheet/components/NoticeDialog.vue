<template>
  <el-dialog title="通知设置" v-model="dialogVisible" width="650px" append-to-body destroy-on-close>
    <notice-conf v-if="dialogVisible" :formData="templateVos" @changeFlowMsgSet="handleFlowMsgSet" />
    <template #footer>
      <el-button @click="dialogVisible = false">取消</el-button>
      <el-button type="primary" @click="handleConfirm">确定</el-button>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref, watch, computed } from 'vue';
import noticeConf from '@/components/Workflow/drawer/noticeConfig/index.vue';

const props = defineProps({
  visible: Boolean,
  node: Object
});

const emit = defineEmits(['update:visible', 'confirm']);

const dialogVisible = computed({
  get: () => props.visible,
  set: (val) => emit('update:visible', val)
});

const templateVos = ref([]);
const currentNoticeData = ref(null);

watch(() => props.visible, (val) => {
  if (val && props.node) {
    templateVos.value = props.node.templateVos || [];
    currentNoticeData.value = null;
  }
});

const handleFlowMsgSet = (data) => {
  currentNoticeData.value = data || null;
};

const handleConfirm = () => {
  emit('confirm', currentNoticeData.value);
  dialogVisible.value = false;
};
</script>
