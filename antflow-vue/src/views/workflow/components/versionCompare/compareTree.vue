<!--
  版本比较 - 单侧流程图容器(滚动 + 定位到指定节点)
-->
<template>
  <div class="vc-tree-wrap">
    <div class="vc-tree-head">
      <span class="vc-tree-title">{{ title }}</span>
      <span class="vc-tree-ver" v-if="versionText">{{ versionText }}</span>
    </div>
    <div class="vc-tree-scroll" ref="scrollRef">
      <div class="vc-tree-inner">
        <cmpNode :node="tree" @select="$emit('select', $event)" />
        <div class="vc-end">
          <div class="vc-end-circle"></div>
          <div class="vc-end-text">流程结束</div>
        </div>
      </div>
      <el-empty v-if="!tree" description="无流程设计数据" :image-size="60" />
    </div>
  </div>
</template>

<script setup>
import { ref, watch, nextTick } from 'vue';
import cmpNode from './cmpNode.vue';

const props = defineProps({
  tree: { type: Object, default: null },
  title: { type: String, default: '' },
  versionText: { type: String, default: '' },
  activeKey: { type: String, default: '' },
});
defineEmits(['select']);

const scrollRef = ref(null);

/** 定位并高亮指定 pairKey 的节点 */
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
.vc-tree-wrap {
  display: flex;
  flex-direction: column;
  border: 1px solid #e3e7ee;
  border-radius: 6px;
  background: #fafbfd;
  min-width: 0;
  flex: 1;
}

.vc-tree-head {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 6px 12px;
  border-bottom: 1px solid #e3e7ee;
  background: #f3f5f9;

  .vc-tree-title { font-weight: 600; font-size: 13px; color: #333c4d; }
  .vc-tree-ver { font-size: 12px; color: #7c8698; }
}

.vc-tree-scroll {
  flex: 1;
  overflow: auto;
  padding: 14px 10px 30px;
}

.vc-tree-inner {
  display: flex;
  flex-direction: column;
  align-items: center;
  min-height: 100%;
}

.vc-end {
  display: flex;
  flex-direction: column;
  align-items: center;
  margin-top: 8px;

  .vc-end-circle {
    width: 12px;
    height: 12px;
    border-radius: 50%;
    border: 2px solid #b6bfce;
  }
  .vc-end-text { font-size: 12px; color: #7c8698; margin-top: 4px; }
}
</style>
