<!--
  FlowTree 公共只读流程树 - 单侧流程图容器(滚动 + 定位到指定节点)
  从 versionCompare/compareTree.vue 抽取泛化(流程诊断/版本比较共用)。
  定位高亮: watch activeKey, 滚动到 [data-pk] 节点(诊断传 node.id 作 __pairKey)。
-->
<template>
  <div class="ft-tree-wrap">
    <div class="ft-tree-head">
      <span class="ft-tree-title">{{ title }}</span>
      <span class="ft-tree-ver" v-if="versionText">{{ versionText }}</span>
      <slot name="legend"></slot>
    </div>
    <div class="ft-tree-scroll" ref="scrollRef">
      <div class="ft-tree-inner">
        <flowNode :node="tree" @select="$emit('select', $event)" />
        <div class="ft-end">
          <div class="ft-end-circle"></div>
          <div class="ft-end-text">流程结束</div>
        </div>
      </div>
      <el-empty v-if="!tree" description="无流程设计数据" :image-size="60" />
    </div>
  </div>
</template>

<script setup>
import { ref, watch, nextTick } from 'vue';
import flowNode from './flowNode.vue';

const props = defineProps({
  tree: { type: Object, default: null },
  title: { type: String, default: '' },
  versionText: { type: String, default: '' },
  activeKey: { type: String, default: '' },
});
defineEmits(['select']);

const scrollRef = ref(null);

/** 定位并高亮指定 data-pk 的节点 */
watch(() => props.activeKey, async (key) => {
  if (!key || !scrollRef.value) return;
  await nextTick();
  const el = scrollRef.value.querySelector(`[data-pk="${key}"]`);
  if (el) {
    scrollRef.value.querySelectorAll('.is-active').forEach(n => n.classList.remove('is-active'));
    el.classList.add('is-active');
    el.scrollIntoView({ block: 'center', behavior: 'smooth' });
  }
});
</script>

<style lang="scss" scoped>
.ft-tree-wrap {
  display: flex;
  flex-direction: column;
  border: 1px solid #e3e7ee;
  border-radius: 6px;
  background: #fafbfd;
  min-width: 0;
  flex: 1;
}

.ft-tree-head {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 6px 12px;
  border-bottom: 1px solid #e3e7ee;
  background: #f3f5f9;

  .ft-tree-title { font-weight: 600; font-size: 13px; color: #333c4d; }
  .ft-tree-ver { font-size: 12px; color: #7c8698; }
}

.ft-tree-scroll {
  flex: 1;
  overflow: auto;
  padding: 14px 10px 30px;
}

.ft-tree-inner {
  display: flex;
  flex-direction: column;
  align-items: center;
  min-height: 100%;
}

.ft-end {
  display: flex;
  flex-direction: column;
  align-items: center;
  margin-top: 8px;

  .ft-end-circle {
    width: 12px;
    height: 12px;
    border-radius: 50%;
    border: 2px solid #b6bfce;
  }

  .ft-end-text { font-size: 12px; color: #7c8698; margin-top: 4px; }
}
</style>
