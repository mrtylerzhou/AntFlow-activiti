<!--
 * 横向传统风格流程设计器(入口组件)
 * 布局: 树形数据 → layoutFlowTree → SVG 渲染
 * 交互: 点击选中(右侧常驻面板联动 store)、拖操作点新建节点(弹菜单)、网关悬停+加分支、
 *        hover 删除、条件线上标签点击编辑条件
 * 数据结构与竖向设计器共用同一棵树, 保存/反显走现有 FormatCommitUtils
-->
<template>
  <div class="hd-root">
    <div class="hd-main">
      <div class="hd-toolbar">
        <span class="hd-toolbar-title">传统风格设计器</span>
        <el-button size="small" @click="$emit('toggleView')">切换到竖向设计器</el-button>
        <el-button size="small" @click="toggleFoldAll" :title="hasFolded ? '展开全部网关分支' : '折叠全部网关分支'">
          <svg v-if="!hasFolded" width="14" height="14" viewBox="0 0 16 16" style="vertical-align: -2px; margin-right: 4px;">
            <path d="M4 5 L8 9 L12 5 M4 9 L8 13 L12 9" fill="none" stroke="currentColor" stroke-width="1.8"
              stroke-linecap="round" stroke-linejoin="round" />
          </svg>
          <svg v-else width="14" height="14" viewBox="0 0 16 16" style="vertical-align: -2px; margin-right: 4px;">
            <path d="M5 4 L9 8 L5 12 M9 4 L13 8 L9 12" fill="none" stroke="currentColor" stroke-width="1.8"
              stroke-linecap="round" stroke-linejoin="round" />
          </svg>
          {{ hasFolded ? '展开全部' : '折叠全部' }}
        </el-button>
        <span class="hd-spacer"></span>
        <span class="hd-zoom">
          <el-button size="small" @click="zoomOut">缩小</el-button>
          <span class="hd-zoom-val">{{ zoom }}%</span>
          <el-button size="small" @click="zoomIn">放大</el-button>
          <el-button size="small" @click="zoomReset">适应画布</el-button>
        </span>
      </div>
      <div class="hd-canvas" ref="canvasRef" @click.self="clearSelect">
        <div class="hd-scroll">
          <div class="hd-scale" :style="{ transform: `scale(${zoom / 100})`, transformOrigin: 'top left' }">
            <svg :width="size.width" :height="size.height" class="hd-svg"
              @mousemove="onSvgMove" @mouseleave="onSvgLeave" @wheel.prevent="onWheel">
              <defs>
                <marker id="hd-arrow" viewBox="0 0 10 10" refX="8" refY="5" markerWidth="6" markerHeight="6"
                  orient="auto-start-reverse">
                  <path d="M2 1L8 5L2 9" fill="none" stroke="context-stroke" stroke-width="1.5"
                    stroke-linecap="round" stroke-linejoin="round" />
                </marker>
              </defs>
              <rect x="0" y="0" :width="size.width" :height="size.height" fill="transparent" @click="clearSelect" />

              <!-- 连线 -->
              <g v-for="e in edges" :key="e.id">
                <path :d="e.d" class="hd-edge" :class="e.noArrow ? '' : 'hd-arrow'"
                  :marker-end="e.noArrow ? undefined : 'url(#hd-arrow)'" />
                <g v-if="e.label && e.labelPos" class="hd-label"
                  :transform="`translate(${e.labelPos.x}, ${e.labelPos.y})`" @click.stop="openCondition(e)">
                  <rect x="-2" y="-10" :width="labelW(e.label) + 4" height="16" rx="3" class="hd-label-bg" />
                  <text x="0" y="0" dominant-baseline="central" class="hd-label-text">{{ e.label }}</text>
                </g>
              </g>

              <!-- 网关 -->
              <g v-for="gw in gateways" :key="'gw-' + gw.key" class="hd-gateway"
                :class="selected && selected.key === gw.key ? 'hd-selected' : ''" @click.stop="selectGateway(gw)">
                <!-- 折叠态: 特殊节点(矩形 + 网关色), 分支泳道隐藏, 主链直连 -->
                <template v-if="gw.folded">
                  <rect :x="gw.x" :y="gw.y" :width="gw.w" :height="gw.h" rx="8" class="hd-folded-body"
                    :fill="gw.meta.fill" :stroke="gw.meta.stroke" />
                  <use :href="'#icon-' + gwIcon(gw)" :x="gw.x + 12" :y="gw.y + 8" width="14" height="14"
                    :fill="gw.meta.stroke" />
                  <text :x="gw.x + 32" :y="gw.y + 15" text-anchor="start" class="hd-node-title"
                    dominant-baseline="central" :fill="gw.meta.stroke">{{ gw.meta.title }}</text>
                  <text :x="gw.x + 14" :y="gw.y + 38" text-anchor="start" class="hd-node-desc"
                    dominant-baseline="central">已折叠 · {{ gw.branchCount }} 个分支</text>
                  <!-- 展开按钮 -->
                  <g class="hd-fold-btn" :transform="`translate(${gw.x + gw.w - 20}, ${gw.y + 28})`"
                    @click.stop="toggleFold(gw)">
                    <circle r="10" :fill="gw.meta.stroke" />
                    <path d="M -3 -4 L 3 0 L -3 4" fill="#fff" />
                  </g>
                </template>
                <!-- 展开态: 菱形 + 分支泳道 -->
                <template v-else>
                  <polygon :points="gwPoints(gw)" :fill="gw.meta.fill" :stroke="gw.meta.stroke" stroke-width="1.5" />
                  <path v-if="gw.meta.symbol === 'cross'" :d="crossPath(gw)" :stroke="gw.meta.stroke" stroke-width="1.4" fill="none" />
                  <path v-else-if="gw.meta.symbol === 'plus'" :d="plusPath(gw)" :stroke="gw.meta.stroke" stroke-width="1.4" fill="none" />
                  <circle v-else :cx="gw.x" :cy="gw.y" r="9" fill="none" :stroke="gw.meta.stroke" stroke-width="1.4" />
                  <text :x="gw.x" :y="gw.y + gw.h / 2 + 14" text-anchor="middle" class="hd-gw-title">{{ gw.meta.title }}</text>
                  <!-- 折叠按钮(菱形下方常显): 分支过多时可折叠节省上下空间 -->
                  <g class="hd-fold-btn" :transform="`translate(${gw.x}, ${gw.y + gw.h / 2 + 24})`"
                    @click.stop="toggleFold(gw)">
                    <circle r="10" :fill="gw.meta.stroke" />
                    <path d="M -3 -4 L 3 0 L -3 4 M 3 -4 L -3 0 L 3 4" fill="none" stroke="#fff" stroke-width="1.5" />
                  </g>
                  <!-- 选中网关: 下方加分支按钮 -->
                  <g v-if="selected && selected.key === gw.key" class="hd-add-branch"
                    :transform="`translate(${gw.x}, ${gw.y + gw.h / 2 + 44})`" @click.stop="addBranch(gw)">
                    <circle r="10" fill="#3296fa" />
                    <path d="M -4 0 H 4 M 0 -4 V 4" stroke="#fff" stroke-width="2" fill="none" />
                  </g>
                  <!-- 聚合加号: 分支汇合后继续添加节点(点击弹节点菜单) -->
                  <g v-if="gw.addBtn" class="hd-gw-add" :transform="`translate(${gw.addBtn.x}, ${gw.addBtn.y})`"
                    @click.stop="openGwAddMenu(gw)">
                    <circle r="10" fill="#3296fa" />
                    <path d="M -4 0 H 4 M 0 -4 V 4" stroke="#fff" stroke-width="2" fill="none" />
                  </g>
                </template>
              </g>

              <!-- 普通节点 -->
              <g v-for="n in nodeRects" :key="'n-' + n.key" class="hd-node"
                :class="selected && selected.key === n.key ? 'hd-selected' : ''"
                @mouseover="hoverKey = n.key" @mouseleave="hoverKey = null" @click.stop="selectNode(n)">
                <rect :x="n.x" :y="n.y" :width="n.w" :height="n.h" rx="8" class="hd-node-body"
                  :class="isTried && nodeHasError(n.node) ? 'hd-node-error' : ''" />
                <path :d="titlePath(n)" :fill="titleColor(n.node)" class="hd-node-title-bar" />
                <!-- 标题条左侧类型图标(与竖向 nodeWrap 一致, 顶端对齐) -->
                <use v-if="nodeIcon(n.node)" :href="'#icon-' + nodeIcon(n.node)" :x="n.x + 12" :y="n.y + 7"
                  width="14" height="14" fill="#fff" />
                <text :x="n.x + (nodeIcon(n.node) ? 32 : 14)" :y="n.y + 15" text-anchor="start" class="hd-node-title"
                  dominant-baseline="central" @dblclick.stop="startRename(n)">{{ clip(n.node.nodeName, n.w - 24) }}</text>
                <text :x="n.x + 14" :y="n.y + 38" text-anchor="start" class="hd-node-desc"
                  dominant-baseline="central">{{ clip(desc(n.node), n.w - 36) }}</text>
                <!-- 内容区右侧箭头(与竖向设计器一致) -->
                <text :x="n.x + n.w - 16" :y="n.y + 38" text-anchor="middle" class="hd-node-arrow"
                  dominant-baseline="central">❯</text>
                <!-- 操作点(拖线新建) -->
                <g v-if="hoverKey === n.key || (selected && selected.key === n.key)"
                  :transform="`translate(${n.x + n.w}, ${n.y + n.h / 2})`">
                  <circle r="7" fill="#3296fa" stroke="#fff" stroke-width="1.5" class="hd-connector"
                    @mousedown.stop="startDrag($event, n)" />
                </g>
                <!-- 删除按钮(发起人不可删) -->
                <g v-if="(hoverKey === n.key) && n.node.nodeType !== 1" class="hd-del"
                  :transform="`translate(${n.x + n.w - 12}, ${n.y + 12})`" @click.stop="delNode(n)">
                  <circle r="9" fill="#e53935" />
                  <path d="M -3.5 -3.5 L 3.5 3.5 M 3.5 -3.5 L -3.5 3.5" stroke="#fff" stroke-width="1.6" fill="none" />
                </g>
                <!-- 分支删除按钮(条件/并行分支首节点) -->
                <g v-if="hoverKey === n.key && branchIndex(n) !== -1" class="hd-del"
                  :transform="`translate(${n.x + n.w - 12}, ${n.y + 12})`" @click.stop="delBranch(n)">
                  <circle r="9" fill="#e53935" />
                  <path d="M -3.5 -3.5 L 3.5 3.5 M 3.5 -3.5 L -3.5 3.5" stroke="#fff" stroke-width="1.6" fill="none" />
                </g>
              </g>

              <!-- 结束圆 -->
              <g v-for="ec in endCircles" :key="ec.id">
                <path :d="ec.d" class="hd-edge hd-arrow" marker-end="url(#hd-arrow)" />
                <circle :cx="ec.x" :cy="ec.y" r="10" fill="#5F5E5A" />
                <text :x="ec.x" :y="ec.y + 20" text-anchor="middle" class="hd-end-text">流程结束</text>
              </g>

              <!-- 拖线临时线 -->
              <path v-if="dragging" :d="tempD" class="hd-temp" />
            </svg>
          </div>
        </div>
        <!-- 节点重命名输入框(双击节点标题出现) -->
        <input v-if="editing" ref="renameInputRef" v-model="renameText" class="hd-rename-input"
          :style="renameStyle" @blur="commitRename" @keydown.enter.prevent="commitRename"
          @keydown.esc.prevent="editing = null" />
      </div>
    </div>

    <!-- 右侧固定面板(可折叠) -->
    <div class="hd-panel" :class="panelCollapsed ? 'collapsed' : ''">
      <div class="hd-panel-toggle" @click="panelCollapsed = !panelCollapsed">
        <span v-if="panelCollapsed" class="hd-panel-arrow">◀</span>
        <span v-else class="hd-panel-arrow">▶</span>
      </div>
      <div v-show="!panelCollapsed" class="hd-panel-body">
        <div class="hd-panel-hint">点击画布节点/网关/条件线进行配置</div>
        <promoterDrawer embed />
        <approverDrawer embed :directorMaxLevel="3" />
        <copyerDrawer embed />
        <copyerV2Drawer embed />
        <conditionDrawer embed />
        <autoNodeDrawer embed />
      </div>
    </div>

    <nodeMenu :visible="menuVisible" :x="menuX" :y="menuY" :from-node="dragFrom" @close="menuVisible = false"
      @created="onNodeCreated" />
    <errorDialog v-model:visible="tipVisible" :list="tipList" />
  </div>
</template>
<script setup>
import { ref, computed, onMounted, onBeforeUnmount, watch, provide, nextTick, getCurrentInstance } from 'vue'
import { useStore } from '@/store/modules/workflow'
import $func from '@/utils/antflow/index'
import { layoutFlowTree, isGateway } from './layout.js'
import { bgColors, PICK_CONDITION_COLOR, FINISH_APPROVE_COLOR, FORWARD_APPROVE_COLOR,
  AUTO_COMPLETE_COLOR, CONDITION_ADVANCE_COLOR, CONDITION_FINISH_COLOR, BACK_APPROVE_COLOR } from '@/utils/antflow/const'
import { NodeUtils } from '@/utils/antflow/nodeUtils'
import nodeMenu from './nodeMenu.vue'
import errorDialog from "@/components/Workflow/dialog/errorDialog.vue";
import promoterDrawer from "@/components/Workflow/drawer/promoterDrawer.vue";
import approverDrawer from "@/components/Workflow/drawer/approverDrawer.vue";
import copyerDrawer from "@/components/Workflow/drawer/copyerDrawer.vue";
import copyerV2Drawer from "@/components/Workflow/drawer/copyerDrawerV2.vue";
import conditionDrawer from "@/components/Workflow/drawer/conditionDrawer.vue";
import autoNodeDrawer from "@/components/Workflow/drawer/autoNodeDrawer.vue";

const { proxy } = getCurrentInstance()
const emit = defineEmits(['toggleView'])
const props = defineProps({
  processData: { type: Object, default: null },
})

const store = useStore()
const {
  setPromoter, setPromoterConfig, setApprover, setCopyer, setCondition, setFlowPermission,
  setApproverConfig, setCopyerConfig, setConditionsConfig, setCopyerV2, setCopyerConfigV2,
  setAutoNode, setAutoNodeConfig, setIsTried,
} = store

// ---------- 数据与布局 ----------
const rootNode = ref(null)
provide('rootNode', rootNode)
const layoutRef = ref({ nodes: [], edges: [], size: { width: 800, height: 600 }, rows: [], units: [] })
const canvasRef = ref(null)
const canvasWidth = ref(1000)
const panelCollapsed = ref(false)
// 折叠的网关 nodeId 集合(视图状态, 不修改数据)
const collapsedGwIds = ref(new Set())

function relayout() {
  const w = canvasRef.value ? canvasRef.value.clientWidth - 8 : 1000
  canvasWidth.value = Math.max(w, 360)
  layoutRef.value = layoutFlowTree(rootNode.value, canvasWidth.value, collapsedGwIds.value)
  rebuildNodeMap()
}
/** 折叠/展开网关(折叠=隐藏分支泳道, 渲染为特殊节点; 视图状态, 数据零改动) */
function toggleFold(gw) {
  const s = new Set(collapsedGwIds.value)
  if (s.has(gw.node.nodeId)) s.delete(gw.node.nodeId)
  else s.add(gw.node.nodeId)
  collapsedGwIds.value = s
  relayout()
}
/** 遍历树收集所有网关节点 */
function collectGateways(node) {
  const list = []
  const walk = (n) => {
    if (!n) return
    if (isGateway(n)) list.push(n)
    if (n.childNode) walk(n.childNode)
    ;(n.conditionNodes || []).forEach((c) => walk(c))
    ;(n.parallelNodes || []).forEach((p) => walk(p))
  }
  walk(node)
  return list
}
/** 节点所属的祖先网关 id(分支内节点在 autoFold 时保护所在网关) */
function ancestorGatewayId(nodeId) {
  let p = nodeMap.get(nodeId)
  let cur = p && p.parent
  const seen = new Set()
  while (cur && !seen.has(cur.nodeId)) {
    seen.add(cur.nodeId)
    if (isGateway(cur)) return cur.nodeId
    cur = (nodeMap.get(cur.nodeId) || {}).parent
  }
  return null
}
const FOLD_THRESHOLD = 5
/** 分支数超过阈值的网关在移开焦点时自动折叠(exceptId 所在网关除外, 保留正在编辑的网关) */
function autoFoldBigGateways(exceptId) {
  const s = new Set(collapsedGwIds.value)
  let changed = false
  collectGateways(rootNode.value).forEach((gw) => {
    const cnt = (gw.nodeType === 2 ? (gw.conditionNodes || []) : (gw.parallelNodes || [])).length
    if (cnt > FOLD_THRESHOLD && gw.nodeId !== exceptId && !s.has(gw.nodeId)) {
      s.add(gw.nodeId)
      changed = true
    }
  })
  if (changed) {
    collapsedGwIds.value = s
    relayout()
  }
}
/** 工具栏: 一键折叠/展开全部可折叠网关(有分支的网关) */
const hasFolded = computed(() => collapsedGwIds.value.size > 0)
function toggleFoldAll() {
  const foldable = collectGateways(rootNode.value)
    .filter((gw) => (gw.nodeType === 2 ? (gw.conditionNodes || []).length : (gw.parallelNodes || []).length) > 0)
    .map((gw) => gw.nodeId)
  const s = new Set(collapsedGwIds.value)
  if (s.size > 0) s.clear()
  else foldable.forEach((id) => s.add(id))
  collapsedGwIds.value = s
  relayout()
}

const nodeRects = computed(() => layoutRef.value.nodes.filter((n) => n.kind === 'node'))
const gateways = computed(() => layoutRef.value.nodes.filter((n) => n.kind === 'gateway'))
const edges = computed(() => layoutRef.value.edges)
const endCircles = computed(() => layoutRef.value.units.filter((u) => u.exitCircle).map((u) => ({
  id: u.node.nodeId, x: u.exitCircle.x, y: u.exitCircle.y,
  d: layoutRef.value.edges.find((e) => e.kind === 'end' && e.fromKey === u.node.nodeId)?.d || '',
})))
const size = computed(() => layoutRef.value.size)

// ---------- 节点定位 map(删除/加分支用) ----------
let nodeMap = new Map() // nodeId -> {node, parent, slot}
function rebuildNodeMap() {
  nodeMap = new Map()
  function walk(node, parent, slot) {
    if (!node) return
    nodeMap.set(node.nodeId, { node, parent, slot })
    if (node.childNode) walk(node.childNode, node, 'childNode')
    ;(node.conditionNodes || []).forEach((c, i) => walk(c, node, 'conditionNodes:' + i))
    ;(node.parallelNodes || []).forEach((p, i) => walk(p, node, 'parallelNodes:' + i))
  }
  walk(rootNode.value, null, 'root')
}

function branchIndex(n) {
  const info = nodeMap.get(n.node.nodeId)
  if (!info) return -1
  if (info.slot.startsWith('conditionNodes:')) return Number(info.slot.split(':')[1])
  if (info.slot.startsWith('parallelNodes:')) return Number(info.slot.split(':')[1])
  return -1
}

// ---------- 选中与右侧面板联动 ----------
let uidCounter = 0
const selected = ref(null) // {key, node, uid, kind, storeKey}

/** 各节点类型对应的 store 配置(面板实时编辑对象, value 与抽屉内编辑引用一致) */
const CFG_MAP = {
  approver: () => store.approverConfig1,
  promoter: () => store.promoterConfig,
  copyer: () => store.copyerConfig1,
  copyerV2: () => store.copyerConfigV2,
  condition: () => store.conditionsConfig1,
  autoNode: () => store.autoNodeConfig1,
}

/**
 * 切换选中前, 把上一节点正在面板中编辑的数据(实时变更, 无需点"确定")合并回树
 * 排除结构字段(childNode/conditionNodes/parallelNodes), 保持树引用
 */
function flushCurrent() {
  const sel = selected.value
  if (!sel || !sel.storeKey) return
  const cfg = CFG_MAP[sel.storeKey]()
  if (!cfg || !cfg.value || cfg.id !== sel.uid) return
  const { childNode, conditionNodes, parallelNodes, ...rest } = cfg.value
  Object.assign(sel.node, rest)
  if (sel.storeKey === 'condition') refreshCondDisplays(sel.node)
  refreshNodeError(sel.node)
  relayout()
}

function selectNode(n) {
  flushCurrent()
  autoFoldBigGateways(ancestorGatewayId(n.node.nodeId))
  selected.value = { key: n.key, node: n.node, uid: ++uidCounter, kind: 'node' }
  openConfig(n.node)
}
function selectGateway(gw) {
  flushCurrent()
  autoFoldBigGateways(gw.node.nodeId)
  selected.value = { key: gw.key, node: gw.node, uid: ++uidCounter, kind: 'gateway', storeKey: 'condition' }
  closeAllDrawers()
  if (gw.node.nodeType === 2) {
    setCondition(true)
    setConditionsConfig({ value: JSON.parse(JSON.stringify(gw.node)), priorityLevel: undefined, flag: false, id: selected.value.uid })
  }
}
function clearSelect() {
  flushCurrent()
  autoFoldBigGateways(null)
  selected.value = null
  closeAllDrawers()
}
function closeAllDrawers() {
  setPromoter(false); setApprover(false); setCopyer(false); setCopyerV2(false)
  setCondition(false); setAutoNode(false)
}

/** 打开节点对应配置面板(与 nodeWrap.setNodeInfo 一致); 先关闭其它面板, 保证只显示当前节点类型的配置 */
function openConfig(node) {
  closeAllDrawers()
  const { nodeType } = node
  const uid = selected.value.uid
  if (nodeType === 1) {
    selected.value.storeKey = 'promoter'
    setPromoter(true)
    setPromoterConfig({ value: { ...JSON.parse(JSON.stringify(node)) }, flag: false, id: uid })
    setFlowPermission({ value: [], flag: false, id: uid })
  } else if ([4, 12, 17, 18, 19, 20, 21].includes(nodeType)) {
    selected.value.storeKey = 'approver'
    setApprover(true)
    setApproverConfig({ value: { ...JSON.parse(JSON.stringify(node)) }, flag: false, id: uid })
  } else if (nodeType === 6) {
    selected.value.storeKey = 'copyer'
    setCopyer(true)
    setCopyerConfig({ value: JSON.parse(JSON.stringify(node)), flag: false, id: uid })
  } else if (nodeType === 7) {
    selected.value.storeKey = 'approver'
    setApprover(true)
    setApproverConfig({ value: { ...JSON.parse(JSON.stringify(node)), index: branchIndex({ node }) }, flag: false, id: uid })
  } else if ([8, 13].includes(nodeType)) {
    selected.value.storeKey = 'copyerV2'
    setCopyerV2(true)
    setCopyerConfigV2({ value: JSON.parse(JSON.stringify(node)), flag: false, id: uid })
  } else if (nodeType === 9) {
    selected.value.storeKey = 'autoNode'
    setAutoNode(true)
    setAutoNodeConfig({ value: JSON.parse(JSON.stringify(node)), flag: false, id: uid })
  } else if (nodeType === 2) {
    selected.value.storeKey = 'condition'
    setCondition(true)
    setConditionsConfig({ value: JSON.parse(JSON.stringify(node)), priorityLevel: undefined, flag: false, id: uid })
  } else {
    // 条件分支节点: 面板数据源必须是网关(含 conditionNodes), priorityLevel 定位具体条件 —— 与 conditionDrawer 约定一致
    selected.value.storeKey = 'condition'
    const info = nodeMap.get(node.nodeId)
    const gw = info && info.parent ? info.parent : node
    selected.value.node = gw
    setCondition(true)
    setConditionsConfig({ value: JSON.parse(JSON.stringify(gw)), priorityLevel: branchIndex({ node }) + 1, flag: false, id: uid })
  }
}

/** 条件线上标签点击 → 打开条件编辑面板
 * 注意: 面板数据源必须是网关(含 conditionNodes), priorityLevel 指定第几个条件 —— 与竖向设计器一致 */
function openCondition(e) {
  const cond = e.condNode
  if (!cond) return
  flushCurrent()
  closeAllDrawers()
  const info = nodeMap.get(cond.nodeId)
  const gw = info && info.parent ? info.parent : cond
  const uid = ++uidCounter
  selected.value = { key: 'cond-gw-' + gw.nodeId, node: gw, uid, kind: 'condition', storeKey: 'condition' }
  setCondition(true)
  setConditionsConfig({ value: JSON.parse(JSON.stringify(gw)), priorityLevel: (e.condIndex || 0) + 1, flag: false, id: uid })
}

/**
 * store 配置回写: 面板编辑实时合并回树(无需点"确定"), 使节点内容即时反馈
 * 排除结构字段(childNode/conditionNodes/parallelNodes), 保持树引用;
 * 条件编辑场景额外把 conditionNodes 按字段合并(保持条件节点引用, 让条件文字实时刷新)
 */
function syncConfig(cfg) {
  const sel = selected.value
  if (!cfg || !cfg.value || !sel || sel.uid !== cfg.id) return
  const { childNode, conditionNodes, parallelNodes, ...rest } = cfg.value
  Object.assign(sel.node, rest)
  if (sel.storeKey === 'condition' && Array.isArray(conditionNodes)) {
    const treeConds = sel.node.conditionNodes || []
    conditionNodes.forEach((c, i) => {
      if (treeConds[i]) Object.assign(treeConds[i], c)
    })
    refreshCondDisplays(sel.node)
  }
  refreshNodeError(sel.node)
  relayout()
}
/** 同步节点的 error 字段(与实时校验一致, 保持数据健康, 后端/序列化不依赖过期 error) */
function refreshNodeError(node) {
  if (!node) return
  node.error = nodeHasError(node)
}
/** 刷新网关下各条件的展示名(与竖版 resetConditionNodesErr 一致): 未配置="请设置条件", 已配置=条件表达式 */
function refreshCondDisplays(gw) {
  if (!gw || !Array.isArray(gw.conditionNodes)) return
  gw.conditionNodes.forEach((c, i) => {
    c.nodeDisplayName = $func.conditionStr(gw, i)
  })
}
watch(() => store.approverConfig1, (cfg) => syncConfig(cfg), { deep: true })
watch(() => store.promoterConfig, (cfg) => syncConfig(cfg), { deep: true })
watch(() => store.copyerConfig1, (cfg) => syncConfig(cfg), { deep: true })
watch(() => store.copyerConfigV2, (cfg) => syncConfig(cfg), { deep: true })
watch(() => store.conditionsConfig1, (cfg) => syncConfig(cfg), { deep: true })
watch(() => store.autoNodeConfig1, (cfg) => syncConfig(cfg), { deep: true })

// ---------- 拖线新建节点 ----------
const dragging = ref(null) // {x, y, from}
const tempD = ref('')
const dragFrom = ref(null)
const menuVisible = ref(false)
const menuX = ref(0)
const menuY = ref(0)
let dragCleanup = null

function svgPoint(clientX, clientY) {
  const rect = canvasRef.value.getBoundingClientRect()
  const scale = zoom.value / 100
  return { x: (clientX - rect.left) / scale, y: (clientY - rect.top) / scale }
}
function startDrag(ev, n) {
  const p = svgPoint(ev.clientX, ev.clientY)
  const from = { x: n.x + n.w, y: n.y + n.h / 2 }
  dragging.value = { x: p.x, y: p.y, from }
  dragFrom.value = n.node
  tempD.value = `M ${from.x} ${from.y} H ${p.x} V ${p.y}`
  const onMove = (e) => {
    const q = svgPoint(e.clientX, e.clientY)
    dragging.value = { ...dragging.value, x: q.x, y: q.y }
    tempD.value = `M ${from.x} ${from.y} H ${q.x} V ${q.y}`
  }
  const onUp = (e) => {
    const q = svgPoint(e.clientX, e.clientY)
    const dist = Math.hypot(q.x - from.x, q.y - from.y)
    dragging.value = null
    tempD.value = ''
    window.removeEventListener('mousemove', onMove)
    window.removeEventListener('mouseup', onUp)
    dragCleanup = null
    if (dist > 24) {
      menuX.value = e.clientX
      menuY.value = e.clientY
      menuVisible.value = true
    } else {
      dragFrom.value = null
    }
  }
  window.addEventListener('mousemove', onMove)
  window.addEventListener('mouseup', onUp)
  dragCleanup = onUp
}
function onNodeCreated() {
  dragFrom.value = null
  menuVisible.value = false
  relayout()
}

// ---------- 删除 ----------
function delNode(n) {
  const info = nodeMap.get(n.node.nodeId)
  if (!info || !info.parent) return
  info.parent[info.slot] = n.node.childNode
  if (selected.value && selected.value.key === n.key) clearSelect()
  relayout()
}
/** 删除网关分支(条件/并行审批人); 剩 1 个时整个网关收敛为唯一分支的后续链(与竖向 delConditionNodeTerm/delParallelNodeTerm 一致) */
function delBranch(n) {
  const info = nodeMap.get(n.node.nodeId)
  if (!info) return
  const gw = info.parent
  const idx = branchIndex(n)
  if (gw.nodeType === 2) {
    gw.conditionNodes.splice(idx, 1)
    gw.conditionNodes.forEach((c, i) => { c.priorityLevel = i + 1; c.nodeName = condTitle(gw, i) })
    if (gw.conditionNodes.length === 1) {
      const only = gw.conditionNodes[0]
      if (gw.childNode) {
        if (only.childNode) tailNode(only.childNode).childNode = gw.childNode
        else only.childNode = gw.childNode
      }
      const gInfo = nodeMap.get(gw.nodeId)
      // 网关替换为唯一分支的后续链(条件分支整体消失, 含剩余的条件), 与竖版一致
      if (gInfo && gInfo.parent) gInfo.parent[gInfo.slot] = only.childNode
    }
  } else {
    gw.parallelNodes.splice(idx, 1)
    gw.parallelNodes.forEach((p, i) => { p.priorityLevel = i + 1; p.nodeName = `审批人${i + 1}` })
    if (gw.parallelNodes.length === 1) {
      const only = gw.parallelNodes[0]
      if (gw.childNode) {
        if (only.childNode) tailNode(only.childNode).childNode = gw.childNode
        else only.childNode = gw.childNode
      }
      const gInfo = nodeMap.get(gw.nodeId)
      // 并行网关替换为唯一分支的后续链(并行审批整体消失)
      if (gInfo && gInfo.parent) gInfo.parent[gInfo.slot] = only.childNode
    }
  }
  if (selected.value) clearSelect()
  relayout()
}
function tailNode(n) { let c = n; while (c.childNode) c = c.childNode; return c }
function condTitle(gw, i) {
  if (gw.isDynamicCondition) return `动态条件${i + 1}`
  if (gw.isParallel) return `并行条件${i + 1}`
  return `条件${i + 1}`
}

// ---------- 网关加分支 ----------
function addBranch(gw) {
  const g = gw.node
  if (g.nodeType === 2) {
    const len = g.conditionNodes.length
    const nc = NodeUtils.createConditionNode(condTitle(g, len), null, len, 0)
    g.conditionNodes.push(nc)
  } else if (g.nodeType === 7) {
    const len = g.parallelNodes.length + 1
    g.parallelNodes.push(NodeUtils.createParallelNode(`审批人${len}`, null, len, 0))
  }
  relayout()
}
/** 聚合加号点击: 弹出节点菜单, 新节点插入为汇合后第一节点(网关.childNode), 与竖向分支盒底部加号一致 */
function openGwAddMenu(gw) {
  const c = canvasRef.value
  const scale = zoom.value / 100
  const rect = c.getBoundingClientRect()
  menuX.value = rect.left + (gw.addBtn.x - c.scrollLeft) * scale
  menuY.value = rect.top + (gw.addBtn.y - c.scrollTop) * scale
  dragFrom.value = gw.node
  menuVisible.value = true
}

// ---------- 校验(getData 与 Process 一致) ----------
const isTried = computed(() => store.isTried)
const tipList = ref([])
const tipVisible = ref(false)
/** 条件是否未配置(与 resetConditionNodesErr 的 defaultCond 判断一致) */
function condUnconfigured(c) {
  return !Array.isArray(c.conditionList) || c.conditionList.length === 0 ||
    !c.conditionList.some((g) => (g || []).some((it) => it && it.columnId && it.columnId !== 0))
}
/** 节点是否缺失必填配置(实时判断, 与发布校验一致; 横向自动保存不刷新 error 字段) */
function nodeHasError(node) {
  if (!node) return false
  if ([4, 12, 17, 20, 21].includes(node.nodeType)) return !$func.setApproverStr(node)
  if (node.nodeType === 6) return !$func.copyerStr(node)
  if ([8, 13].includes(node.nodeType)) return !$func.setCopyStrV2(node)
  if (node.nodeType === 3) return condUnconfigured(node)
  return !!node.error
}
function reErr(node) {
  if (!node) return
  const { nodeType, nodeName, conditionNodes, parallelNodes } = node
  if (nodeType === 2) {
    reErr(node.childNode)
    ;(conditionNodes || []).forEach((c) => {
      if (condUnconfigured(c)) tipList.value.push({ name: c.nodeName, nodeType: '条件' })
      reErr(c)
    })
  } else if (nodeType === 7) {
    reErr(node.childNode)
    ;(parallelNodes || []).forEach((p) => {
      if (!$func.setApproverStr(p)) tipList.value.push({ name: p.nodeName, nodeType: '审批人' })
      reErr(p)
    })
  } else if ([4, 12, 17, 20, 21].includes(nodeType)) {
    // 只依赖实时判断: 横向自动保存不刷新 error 字段, error 不可信
    if (!$func.setApproverStr(node)) tipList.value.push({ name: nodeName, nodeType: '审批人' })
    reErr(node.childNode)
  } else if (nodeType === 6) {
    if (!$func.copyerStr(node)) tipList.value.push({ name: nodeName, nodeType: '抄送人' })
    reErr(node.childNode)
  } else if ([8, 13].includes(nodeType)) {
    if (!$func.setCopyStrV2(node)) tipList.value.push({ name: nodeName, nodeType: '抄送人' })
    reErr(node.childNode)
  } else {
    reErr(node.childNode)
  }
}
const getJson = () => {
  setIsTried(true)
  tipList.value = []
  reErr(rootNode.value)
  if (tipList.value.length) {
    tipVisible.value = true
    return null
  }
  // 并行审批/条件并行必须存在聚合节点(与 Process 校验一致)
  let errorMsg = ''
  const checkAggregate = (node) => {
    if (!node || errorMsg) return
    if (node.nodeType === 7 || (node.nodeType === 2 && node.isParallel)) {
      if (!node.childNode || node.childNode.nodeType !== 4) {
        errorMsg = node.nodeType === 7 ? '并行审批下必须有一个审批人节点作为聚合节点' : '条件并行节点下必须有一个审批人节点作为聚合节点'
        return
      }
    }
    if (node.childNode) checkAggregate(node.childNode)
    ;(node.conditionNodes || []).forEach((c) => checkAggregate(c))
    ;(node.parallelNodes || []).forEach((p) => checkAggregate(p))
  }
  checkAggregate(rootNode.value)
  if (errorMsg) { proxy.$modal.msgError(errorMsg); return null }
  return JSON.parse(JSON.stringify(rootNode.value))
}
const getData = () => new Promise((resolve, reject) => {
  const res = getJson()
  if (!res) { reject({ formData: null }); return }
  resolve({ formData: res })
})
defineExpose({ getData })

// ---------- 缩放 ----------
const zoom = ref(100)
function zoomIn() { zoom.value = Math.min(zoom.value + 10, 200) }
function zoomOut() { zoom.value = Math.max(zoom.value - 10, 40) }
function zoomReset() {
  const w = canvasWidth.value
  const scale = Math.min(1, (w - 40) / Math.max(size.value.width, 1))
  zoom.value = Math.round(scale * 100)
}
function onWheel(ev) {
  if (!ev.ctrlKey) return
  const d = ev.deltaY > 0 ? -10 : 10
  zoom.value = Math.min(Math.max(zoom.value + d, 40), 200)
}
function onSvgMove() { }
function onSvgLeave() { hoverKey.value = null }

// ---------- 尺寸与挂载 ----------
let resizeObserver = null
onMounted(() => {
  if (props.processData) rootNode.value = props.processData
  relayout()
  resizeObserver = new ResizeObserver(() => relayout())
  if (canvasRef.value) resizeObserver.observe(canvasRef.value)
})
onBeforeUnmount(() => { resizeObserver?.disconnect(); if (dragCleanup) window.removeEventListener('mouseup', dragCleanup) })

// ---------- SVG 辅助 ----------
const hoverKey = ref(null)
function gwPoints(gw) {
  const r = gw.w / 2
  return `${gw.x + r},${gw.y} ${gw.x},${gw.y + r} ${gw.x - r},${gw.y} ${gw.x},${gw.y - r}`
}
function crossPath(gw) {
  const r = gw.w / 2 - 6
  return `M ${gw.x - r} ${gw.y - r} L ${gw.x + r} ${gw.y + r} M ${gw.x + r} ${gw.y - r} L ${gw.x - r} ${gw.y + r}`
}
function plusPath(gw) {
  const r = gw.w / 2 - 6
  return `M ${gw.x - r} ${gw.y} H ${gw.x + r} M ${gw.x} ${gw.y - r} V ${gw.y + r}`
}
function titlePath(n) {
  return `M ${n.x + 8} ${n.y} H ${n.x + n.w - 8} V ${n.y + 30} H ${n.x + 8} Z`
}
function clip(text, maxW) {
  const s = text == null ? '' : String(text)
  const chars = Math.max(4, Math.floor(maxW / 13))
  return s.length > chars ? s.slice(0, chars - 1) + '…' : s
}
function desc(node) {
  if (!node) return ''
  // 与竖向 nodeWrap 的 showText 一致: 内容实时计算, 面板编辑后无需点"确定"即可反馈到节点
  let text = ''
  if (node.nodeType === 1) text = '流程发起'
  else if ([4, 12, 17, 20, 21].includes(node.nodeType)) text = $func.setApproverStr(node)
  else if (node.nodeType === 6) text = $func.copyerStr(node)
  else if ([8, 13].includes(node.nodeType)) text = $func.setCopyStrV2(node)
  else if (node.nodeType === 9) text = $func.autoNodeConditionStr(node)
  else if (node.nodeType === 3) {
    // 条件节点: 严格"已配"= 有 columnId 且 zdy1 已填
    // 拼接各组时去掉"条件组N:"前缀(标题已有"条件N"避免重影),保留【】用 且/或 连多组
    const groups = node.conditionList || []
    const grpStrs = groups.map((g, i) => {
      if (!Array.isArray(g) || !g.length) return null
      if (!g.some((it) => it && it.columnId && it.columnId !== 0 && it.zdy1)) return null
      return '【' + $func.getConditionStr(g).trim() + '】'
    }).filter(Boolean)
    if (grpStrs.length === 0) text = '请设置条件'
    else text = grpStrs.join(node.groupRelation ? ' 或 ' : ' 且 ')
  }
  else text = node.nodeDisplayName || ''
  return text || '请配置'
}
/** 节点标题条左侧类型图标(与竖向 nodeWrap 的 svg-icon 映射一致) */
function nodeIcon(node) {
  if (!node) return ''
  const has = (lv) => Array.isArray(node.labelList) && node.labelList.some((l) => l.labelValue === lv)
  if (node.nodeType === 6) return 'copy-user'
  if (node.nodeType === 8 || node.nodeType === 13) return 'copy-user'
  if (node.nodeType === 17) return 'assist'
  if (node.nodeType === 18 && has('auto_complete_node')) return 'auto-finish'
  if (node.nodeType === 18) return 'auto-drive-ahead'
  if (node.nodeType === 19 && node.drawBackType === 2) return 'auto-drive-to-starter'
  if (node.nodeType === 19) return 'auto-drive-back'
  if (node.nodeType === 20) return 'conditional-drive-back'
  if (node.nodeType === 21) return 'conditional-drive-to-starter'
  if (node.nodeType === 4 && has('finish_approve_node')) return 'finish-process'
  if (node.nodeType === 4 && has('approve_forward_node')) return 'approver-drive-ahead'
  if (node.nodeType === 4 && Array.isArray(node.buttons?.approvalPage) && node.buttons.approvalPage.some((b) => b.buttonType === 42)) return 'approver-drive-ahead'
  if (node.nodeType === 4 && has('af_syslabel_disagree_back')) return 'drive-back'
  if (node.nodeType === 12 && has('condition_finish_node')) return 'condition-finish-process'
  if (node.nodeType === 12 && has('condition_advance_node')) return 'conditional-drive-ahead'
  if (node.nodeType === 3) {
    // 条件分支节点: 按所属网关类型显示图标(与竖版条件分支一致)
    const info = nodeMap.get(node.nodeId)
    const gw = info && info.parent
    if (gw && gw.isDynamicCondition) return 'dynamic-condition'
    if (gw && gw.isParallel) return 'parallel-condition'
    return 'condition'
  }
  if (node.nodeType === 1) return ''
  return 'approve'
}
/** 网关折叠态图标(按网关类型) */
function gwIcon(gw) {
  const k = gw.meta && gw.meta.kind
  if (k === 'parallel') return 'parallel-approve'
  if (k === 'inclusive') return 'parallel-condition'
  if (k === 'exclusive-dynamic') return 'dynamic-condition'
  return 'condition'
}

// ---------- 双击节点标题重命名 ----------
const editing = ref(null) // {node, x, y, w}
const renameText = ref('')
const renameInputRef = ref(null)
watch(editing, (v) => {
  if (v) {
    renameText.value = v.node.nodeName || ''
    nextTick(() => renameInputRef.value?.focus())
  }
})
const renameStyle = computed(() => {
  if (!editing.value) return {}
  const s = zoom.value / 100
  const c = canvasRef.value
  return {
    left: (editing.value.x * s - (c ? c.scrollLeft : 0)) + 'px',
    top: (editing.value.y * s - (c ? c.scrollTop : 0)) + 2 + 'px',
    width: (editing.value.w * s - 12) + 'px',
  }
})
function startRename(n) {
  editing.value = { node: n.node, x: n.x, y: n.y, w: n.w }
}
function commitRename() {
  const ed = editing.value
  if (ed && ed.node) {
    const name = (renameText.value || '').trim()
    if (name) {
      ed.node.nodeName = name
      // 同步到右侧面板的编辑副本, 避免切换节点时被打开面板时的旧名字覆盖
      const sel = selected.value
      if (sel && sel.storeKey) {
        const cfg = CFG_MAP[sel.storeKey]()
        if (cfg && cfg.value && cfg.id === sel.uid && sel.node === ed.node) {
          cfg.value.nodeName = name
        }
      }
    }
    relayout()
  }
  editing.value = null
}
function labelW(label) { return String(label).length * 12 }
function titleColor(n) {
  if (!n) return 'rgb(192,192,192)'
  const norm = (c) => String(c).replace(/，/g, ',')
  const has = (lv) => Array.isArray(n.labelList) && n.labelList.some((l) => l.labelValue === lv)
  if (n.isPickCondition) return `rgb(${norm(PICK_CONDITION_COLOR)})`
  if (n.nodeType === 4 && has('finish_approve_node')) return `rgb(${norm(FINISH_APPROVE_COLOR)})`
  if (n.nodeType === 4 && has('approve_forward_node')) return `rgb(${norm(FORWARD_APPROVE_COLOR)})`
  if (n.nodeType === 4 && Array.isArray(n.buttons?.approvalPage) && n.buttons.approvalPage.some((b) => b.buttonType === 42)) return `rgb(${norm(FORWARD_APPROVE_COLOR)})`
  if (n.nodeType === 18 && has('auto_complete_node')) return `rgb(${norm(AUTO_COMPLETE_COLOR)})`
  if (n.nodeType === 12 && has('condition_finish_node')) return `rgb(${norm(CONDITION_FINISH_COLOR)})`
  if (n.nodeType === 12 && has('condition_advance_node')) return `rgb(${norm(CONDITION_ADVANCE_COLOR)})`
  if (n.nodeType === 4 && has('af_syslabel_disagree_back')) return `rgb(${norm(BACK_APPROVE_COLOR)})`
  if ([19, 20, 21].includes(n.nodeType)) return `rgb(${norm(BACK_APPROVE_COLOR)})`
  return `rgb(${norm(bgColors[n.nodeType] || '192,192,192')})`
}
</script>
<style scoped>
.hd-root {
  display: flex;
  height: calc(100vh - 85px);
  min-height: 480px;
  background: #f5f5f7;
}
.hd-main {
  flex: 1;
  display: flex;
  flex-direction: column;
  min-width: 0;
}
.hd-toolbar {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 8px 14px;
  background: #fff;
  border-bottom: 1px solid #e2e2e2;
  flex-shrink: 0;
}
.hd-toolbar-title { font-size: 14px; font-weight: 500; color: #191f25; }
.hd-spacer { flex: 1; }
.hd-zoom { display: flex; align-items: center; gap: 6px; }
.hd-zoom-val { font-size: 13px; color: #606266; min-width: 42px; text-align: center; }
.hd-canvas {
  flex: 1;
  overflow: auto;
  position: relative;
}
.hd-scroll { min-width: 100%; min-height: 100%; width: max-content; }
.hd-svg { display: block; }
.hd-edge { fill: none; stroke: #8a8a8a; stroke-width: 1.2; }
.hd-arrow { }
.hd-temp { fill: none; stroke: #3296fa; stroke-width: 1.5; stroke-dasharray: 6 4; }
.hd-node { cursor: pointer; }
.hd-node-body { fill: #fff; stroke: #cacaca; stroke-width: 1; }
.hd-node-error { stroke: #e53935; stroke-width: 2; }
.hd-node-title-bar { stroke: none; }
.hd-node-title { fill: #fff; font-size: 13px; font-weight: 500; }
.hd-node-desc { fill: #8c8c8c; font-size: 12px; }
.hd-node-arrow { fill: #c0c4cc; font-size: 12px; }
.hd-selected .hd-node-body,
.hd-selected polygon { stroke: #3296fa; stroke-width: 2.5; }
.hd-gateway { cursor: pointer; }
.hd-gw-add { cursor: pointer; }
.hd-gw-add:hover circle { fill: #1e83e9; }
.hd-folded-body { fill: #fff; stroke-width: 1.5; }
.hd-selected .hd-folded-body { stroke: #3296fa; stroke-width: 2.5; }
.hd-fold-btn { cursor: pointer; }
.hd-fold-btn:hover circle { opacity: 0.85; }
.hd-gw-title { font-size: 11px; fill: #8c8c8c; }
.hd-connector { cursor: crosshair; }
.hd-connector:hover { r: 9; }
.hd-del { cursor: pointer; }
.hd-label { cursor: pointer; }
.hd-label-bg { fill: #fff; stroke: #d9d9d9; stroke-width: 0.8; }
.hd-label-text { fill: #606266; font-size: 12px; }
.hd-end-text { font-size: 12px; fill: #8c8c8c; }
.hd-rename-input {
  position: absolute;
  height: 26px;
  z-index: 20;
  border: 1px solid #3296fa;
  border-radius: 4px;
  padding: 0 6px;
  font-size: 13px;
  outline: none;
  box-shadow: 0 0 6px rgba(50, 150, 250, 0.3);
  background: #fff;
}
.hd-panel {
  width: 520px;
  flex-shrink: 0;
  background: #fff;
  border-left: 1px solid #e2e2e2;
  display: flex;
  transition: width 0.2s;
  position: relative;
}
.hd-panel.collapsed { width: 40px; }
.hd-panel-toggle {
  position: absolute;
  left: -1px;
  top: 10px;
  width: 22px;
  height: 34px;
  background: #fff;
  border: 1px solid #e2e2e2;
  border-left: none;
  border-radius: 0 6px 6px 0;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  z-index: 5;
}
.hd-panel-arrow { font-size: 11px; color: #606266; }
.hd-panel-body {
  flex: 1;
  overflow: auto;
  padding: 12px 12px 24px;
}
.hd-panel-hint {
  font-size: 12px;
  color: #909399;
  padding: 6px 0 12px;
  border-bottom: 1px dashed #e2e2e2;
  margin-bottom: 10px;
}
</style>
