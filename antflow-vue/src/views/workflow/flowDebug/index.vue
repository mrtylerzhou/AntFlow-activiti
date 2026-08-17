<!--
  流程调试页(薄壳): 主体逻辑已抽取到 components/Workflow/FlowDebug/debugPanel.vue
  (流程诊断 dialog 复用同一面板; 本页发起人仍由用户手选)
-->
<template>
  <div class="card-box" style="padding: 10px;">
    <div class="task-title">
      <span class="task-title-text">流程调试</span>
    </div>
    <debugPanel :confId="id" />
    <label class="page-close-box" @click="close()"><img src="@/assets/images/antflow/back-close.png"></label>
  </div>
</template>

<script setup>
import { getCurrentInstance } from 'vue';
import debugPanel from '@/components/Workflow/FlowDebug/debugPanel.vue';

const { proxy } = getCurrentInstance();
const route = useRoute();
const id = route.query?.id;

function close() { proxy.$tab.closePage(); }
</script>

<style scoped lang="scss">
.task-title {
  display: flex;
  justify-content: space-between;
  padding-bottom: 6px;
  background-color: #f5f5f7;
}

.task-title-text {
  line-height: 28px;
  font-weight: 600;
  font-size: 16px;
  color: #383838;
}
</style>
