<!--
  版本比较 - 只读节点渲染器(递归)
  复用设计器 nodeWrap 的视觉语言(竖向主干 + 分支列), 去掉全部编辑控件;
  按 node.__diff 着差异高亮, 点击节点向父级冒泡 select 事件.
-->
<template>
  <!-- 条件网关 -->
  <div v-if="node && node.nodeType == 2" class="vc-branch">
    <div class="vc-node" :class="cls(node)" :data-pk="node.__pairKey" @click.stop="$emit('select', node)">
      <div class="vc-node-title" :style="{ background: typeBg(node.nodeType) }">
        <span>{{ node.nodeName || '条件分支' }}</span>
        <span class="vc-tag">{{ parallelText(node) }}</span>
      </div>
    </div>
    <div class="vc-cols">
      <div class="vc-col" v-for="(c, i) in node.conditionNodes || []" :key="'c' + i">
        <cmpNode :node="c" @select="$emit('select', $event)" />
        <cmpNode v-if="c.childNode" :node="c.childNode" @select="$emit('select', $event)" />
      </div>
    </div>
    <cmpNode v-if="node.childNode" :node="node.childNode" @select="$emit('select', $event)" />
  </div>

  <!-- 并行审批网关 -->
  <div v-else-if="node && node.nodeType == 7" class="vc-branch">
    <div class="vc-node" :class="cls(node)" :data-pk="node.__pairKey" @click.stop="$emit('select', node)">
      <div class="vc-node-title" :style="{ background: typeBg(node.nodeType) }">
        <span>{{ node.nodeName || '并行审批' }}</span>
      </div>
    </div>
    <div class="vc-cols vc-cols-parallel">
      <div class="vc-col" v-for="(p, i) in node.parallelNodes || []" :key="'p' + i">
        <cmpNode :node="p" @select="$emit('select', $event)" />
        <cmpNode v-if="p.childNode" :node="p.childNode" @select="$emit('select', $event)" />
      </div>
    </div>
    <cmpNode v-if="node.childNode" :node="node.childNode" @select="$emit('select', $event)" />
  </div>

  <!-- 普通节点 -->
  <div v-else-if="node" class="vc-chain">
    <div class="vc-node" :class="cls(node)" :data-pk="node.__pairKey" @click.stop="$emit('select', node)">
      <div class="vc-node-title" :style="{ background: typeBg(node.nodeType) }">
        <span>{{ node.nodeName || typeName(node.nodeType) }}</span>
        <span class="vc-status" v-if="node.__diff === 'added'">新增</span>
        <span class="vc-status" v-else-if="node.__diff === 'removed'">删除</span>
        <span class="vc-status" v-else-if="node.__diff === 'modified'">有差异</span>
      </div>
      <div class="vc-node-body">
        <span class="vc-node-text">{{ summary(node) }}</span>
      </div>
    </div>
    <div class="vc-arrow" v-if="node.childNode"></div>
    <cmpNode v-if="node.childNode" :node="node.childNode" @select="$emit('select', $event)" />
  </div>
</template>

<script setup>
import { nodeTypeList } from '@/utils/antflow/const';

defineProps({ node: { type: Object, default: null } });
defineEmits(['select']);

const typeName = (t) => nodeTypeList?.[t] || `节点(${t})`;
const typeBg = (t) => {
  const map = {
    1: '#8f9bb3', 2: '#7a6eab', 3: '#9a7bab', 4: '#4a7cc7', 6: '#6b7f96',
    7: '#4a7cc7', 8: '#6b7f96', 9: '#c7844a', 12: '#c7844a', 13: '#6b7f96',
    17: '#4a9c7c', 18: '#c7844a', 19: '#c26a6a', 20: '#c26a6a', 21: '#c26a6a',
  };
  return map[t] || '#7c8a9c';
};
const cls = (n) => [
  'st-' + (n.__diff || 'same'),
  { 'is-active': n.__active },
];
const parallelText = (n) => (n.isParallel ? '并行' : n.isDynamicCondition ? '动态' : '互斥');
const summary = (n) => n.nodeDisplayName || typeName(n.nodeType);
</script>

<style lang="scss" scoped>
$blue: #3a6fd8;

.vc-chain {
  display: flex;
  flex-direction: column;
  align-items: center;
}

.vc-node {
  width: 216px;
  border-radius: 6px;
  background: #fff;
  border: 1px solid #d9dee8;
  box-shadow: 0 1px 2px rgba(0, 0, 0, 0.06);
  cursor: pointer;
  overflow: hidden;
  transition: box-shadow 0.15s;

  &:hover { box-shadow: 0 2px 8px rgba(58, 111, 216, 0.28); }

  &.st-modified { border: 2px solid #e69a2e; }
  &.st-added { border: 2px solid #3fa84f; }
  &.st-removed { border: 2px dashed #d35454; opacity: 0.85; }
  &.is-active { box-shadow: 0 0 0 3px rgba(58, 111, 216, 0.35); }
}

.vc-node-title {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 5px 10px;
  color: #fff;
  font-size: 13px;
  line-height: 18px;

  .vc-status {
    font-size: 11px;
    padding: 0 5px;
    border-radius: 8px;
    background: rgba(255, 255, 255, 0.28);
  }
  .vc-tag { font-size: 11px; opacity: 0.9; }
}

.vc-node-body {
  padding: 6px 10px;
  font-size: 12px;
  color: #5a6474;
  min-height: 28px;
  display: flex;
  align-items: center;

  .vc-node-text {
    display: -webkit-box;
    -webkit-line-clamp: 2;
    -webkit-box-orient: vertical;
    overflow: hidden;
    word-break: break-all;
  }
}

.vc-arrow {
  width: 2px;
  height: 18px;
  background: #b6bfce;
}

.vc-branch {
  display: flex;
  flex-direction: column;
  align-items: center;

  > .vc-node { margin-bottom: 4px; }
}

.vc-cols {
  display: flex;
  align-items: stretch;

  .vc-col {
    display: flex;
    flex-direction: column;
    align-items: center;
    padding: 0 6px;
    position: relative;
    min-width: 228px;

    &::before,
    &::after {
      content: '';
      position: absolute;
      left: 0;
      right: 0;
      height: 2px;
      background: #b6bfce;
    }
    &::before { top: 0; }
    &::after { bottom: 0; }

    > .vc-chain:first-child > .vc-node { margin-top: 14px; }
  }
}

.vc-cols-parallel .vc-col > :deep(.vc-chain:first-child) > .vc-node {
  border-left: 3px solid #4a7cc7;
}
</style>
