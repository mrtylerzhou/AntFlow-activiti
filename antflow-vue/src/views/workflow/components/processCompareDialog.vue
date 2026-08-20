<!--
  流程对比(实例级) 全屏 dialog (流程管理-流程监控-更多-流程对比)
  设计: .scratch/process-instance-compare-design.md
  - A 固定(监控列表当前行), B 远程搜索下拉(同 formCode), 支持一键互换
  - 顶部元信息两列对比表(差异行高亮)
  - 双图并排(FlowTree) + 右侧节点差异区: 实际审批人对比 / 加减签转办记录 / 设计期审批人规则差异
  - 颜色: 绿=B 新增 红=B 减少 橙=配对有差异; 差异方向「以 A 为基准看 B」, 互换翻转
-->
<template>
  <el-dialog v-model="showDlg" :title="dialogTitle" width="94%" top="3vh" append-to-body
    class="pc-dialog" :close-on-click-modal="false" destroy-on-close>
    <template #header>
      <div class="pc-dlg-title">
        <span>流程对比 - {{ row?.processTypeName || row?.processKey || '' }}</span>
        <el-tag v-if="loaded" :type="diffCount ? 'warning' : 'success'" size="small">
          {{ diffCount ? `共 ${diffCount} 处差异` : '两个实例无差异' }}
        </el-tag>
      </div>
    </template>

    <div class="pc-body" v-loading="loading" element-loading-text="正在对比...">
      <!-- 选实例区 -->
      <div class="pc-picker">
        <div class="pc-picker-item">
          <span class="pc-picker-label">A(基准)</span>
          <span class="pc-picker-fixed">{{ aInfo?.processNumber || row?.processNumber }}</span>
          <el-tag size="small" type="info" v-if="aInfo">v{{ aInfo.version }}</el-tag>
        </div>
        <el-button circle size="small" title="互换 A/B" @click="swap" :disabled="!bInfo">
          <el-icon style="transform: rotate(90deg)"><Switch /></el-icon>
        </el-button>
        <div class="pc-picker-item">
          <span class="pc-picker-label">B(对比)</span>
          <el-select v-model="bKey" placeholder="搜索流程编号/发起人选择对比实例" filterable remote reserve-keyword
            :remote-method="remoteSearch" :loading="searching" style="width: 360px"
            @change="onBChange">
            <el-option v-for="c in bCandidates" :key="c.processNumber" :value="c.processNumber"
              :label="candidateLabel(c)" :disabled="c.processNumber === aInfo?.processNumber" />
          </el-select>
        </div>
        <div class="pc-picker-tip">仅可对比同 formCode 的流程实例; 差异方向「以 A 为基准看 B」</div>
      </div>

      <!-- 元信息对比表 -->
      <table class="pc-meta" v-if="aInfo && bInfo">
        <thead>
          <tr><th style="width:90px">字段</th><th>A ({{ aInfo.processNumber }})</th><th>B ({{ bInfo.processNumber }})</th></tr>
        </thead>
        <tbody>
          <tr v-for="m in metaRows" :key="m.label" :class="{ 'pc-changed': m.changed }">
            <td>{{ m.label }}</td>
            <td>{{ m.a }}</td>
            <td>{{ m.b }}</td>
          </tr>
        </tbody>
      </table>

      <template v-if="loaded && nodeResults.length">
        <!-- 工具栏 -->
        <div class="pc-toolbar">
          <el-button size="small" type="primary" plain :disabled="!diffKeys.length" @click="nextDiff">
            下一处差异 ({{ diffKeys.length ? diffIdx + 1 : 0 }}/{{ diffKeys.length }})
          </el-button>
          <div class="pc-legend">
            <span class="lg lg-add">B 新增</span>
            <span class="lg lg-del">B 减少</span>
            <span class="lg lg-mod">有差异</span>
            <span class="pc-legend-tip">点击任一侧节点查看审批人差异</span>
          </div>
        </div>

        <!-- 双图 + 差异区 -->
        <div class="pc-node-layout">
          <flowTree :tree="treeA" title="A 流程图" :versionText="aInfo?.version || ''"
            :activeKey="activeKey" @select="onNodeSelect" />
          <flowTree :tree="treeB" title="B 流程图" :versionText="bInfo?.version || ''"
            :activeKey="activeKey" @select="onNodeSelect" />
          <div class="pc-detail">
            <template v-if="activeResult">
              <div class="pc-detail-head">
                <span v-if="activeResult.status === 'added'" class="pc-dot add"></span>
                <span v-else-if="activeResult.status === 'removed'" class="pc-dot del"></span>
                <span v-else-if="activeResult.status === 'modified'" class="pc-dot mod"></span>
                <span v-else class="pc-dot same"></span>
                {{ activeTitle }}
              </div>
              <div class="pc-detail-scroll">
                <!-- 单边节点 -->
                <template v-if="activeResult.status === 'added' || activeResult.status === 'removed'">
                  <el-alert :type="activeResult.status === 'added' ? 'success' : 'danger'" show-icon :closable="false"
                    :title="activeResult.status === 'added' ? '该节点仅 B 侧流程图中存在' : '该节点仅 A 侧流程图中存在'"
                    style="margin-bottom: 10px" />
                  <personList :title="activeResult.status === 'added' ? 'B 侧实际审批人' : 'A 侧实际审批人'"
                    :persons="activeResult.oneSidePersons" :entrusts="activeResult.oneSideEntrusts" />
                </template>
                <!-- 配对节点 -->
                <template v-else>
                  <div class="pc-section">
                    <div class="pc-sec-head">实际审批人对比</div>
                    <template v-if="activeResult.personDiff">
                      <table class="pc-table">
                        <thead><tr><th style="width:90px">归属</th><th>审批人</th></tr></thead>
                        <tbody>
                          <tr v-if="activeResult.personDiff.both.length" class="pc-row-same">
                            <td>双方都有</td>
                            <td>{{ names(activeResult.personDiff.both) }}</td>
                          </tr>
                          <tr v-if="activeResult.personDiff.onlyA.length" class="pc-row-a">
                            <td>仅 A 有</td>
                            <td>{{ names(activeResult.personDiff.onlyA) }}</td>
                          </tr>
                          <tr v-if="activeResult.personDiff.onlyB.length" class="pc-row-b">
                            <td>仅 B 有</td>
                            <td>{{ names(activeResult.personDiff.onlyB) }}</td>
                          </tr>
                        </tbody>
                      </table>
                    </template>
                    <div v-else class="pc-none">{{ activeResult.personTip }}</div>
                  </div>

                  <div class="pc-section" v-if="activeResult.entrustsA.length || activeResult.entrustsB.length">
                    <div class="pc-sec-head">加签 / 减签 / 转办记录</div>
                    <entrustTable side="A" :records="activeResult.entrustsA" />
                    <entrustTable side="B" :records="activeResult.entrustsB" />
                  </div>

                  <div class="pc-section" v-if="activeResult.ruleRows.length">
                    <div class="pc-sec-head">
                      设计期审批人规则差异
                      <span class="pc-sec-sub">(解释实际审批人不同的可能原因; 其余属性差异请用版本比较)</span>
                    </div>
                    <table class="pc-table">
                      <thead><tr><th style="width:130px">配置项</th><th>A</th><th>B</th></tr></thead>
                      <tbody>
                        <tr v-for="(r, ri) in activeResult.ruleRows" :key="ri">
                          <td>{{ r.label }}</td>
                          <td>{{ r.source }}</td>
                          <td>{{ r.target }}</td>
                        </tr>
                      </tbody>
                    </table>
                  </div>

                  <div class="pc-section" v-if="activeResult.status === 'same'">
                    <div class="pc-none">该节点两侧无差异</div>
                  </div>
                </template>
              </div>
            </template>
            <el-empty v-else description="点击流程图中的节点, 查看该节点的审批人差异" :image-size="70" />
          </div>
        </div>
      </template>

      <el-empty v-else-if="loaded" description="两个流程实例无结构差异或数据加载异常" :image-size="80" />
      <el-empty v-else-if="aInfo && !bInfo && !loading" description="请选择 B 侧对比实例" :image-size="80" />
    </div>
  </el-dialog>
</template>

<script setup>
import { ref, computed, watch, defineComponent, h } from 'vue';
import { Switch } from '@element-plus/icons-vue';
import {
  getApiWorkFlowData,
  getBpmVerifyInfoVos,
  getCompareCandidates,
  getCompareEntrusts,
} from '@/api/workflow/index';
import { FormatDisplayUtils } from '@/utils/antflow/formatdisplay_data';
import { alignTrees, diffNodePair } from '@/utils/antflow/versionCompare';
import flowTree from '@/components/Workflow/FlowTree/flowTree.vue';

const props = defineProps({
  visible: { type: Boolean, default: false },
  row: { type: Object, default: null },
});
const emit = defineEmits(['update:visible']);

const showDlg = computed({
  get: () => props.visible,
  set: v => emit('update:visible', v),
});

const STATE_NAMES = { 1: '审批中', 2: '审批通过', 3: '作废', 6: '审批拒绝' };
const stateName = (s) => STATE_NAMES[s] || (s != null ? `状态${s}` : '-');
const formatTime = (d) => {
  if (!d) return '-';
  const dt = new Date(d);
  const p = (x) => String(x).padStart(2, '0');
  return `${dt.getFullYear()}-${p(dt.getMonth() + 1)}-${p(dt.getDate())} ${p(dt.getHours())}:${p(dt.getMinutes())}`;
};
const stripMark = (n) => {
  let s = (n || '').trim();
  if (s.endsWith('+') || s.endsWith('-') || s.endsWith('*')) s = s.slice(0, -1);
  return s;
};

// ---------- 状态 ----------
const loading = ref(false);
const loaded = ref(false);
const searching = ref(false);
const aInfo = ref(null);
const bInfo = ref(null);
const bKey = ref('');
const bCandidates = ref([]);
const treeA = ref(null);
const treeB = ref(null);
const nodeResults = ref([]);
const activeKey = ref('');
const diffIdx = ref(0);

const dialogTitle = computed(() => '流程对比');
const candidateLabel = (c) =>
  `${c.processNumber} · ${c.userName || c.createUser || '?'} · v${c.version} · ${stateName(c.processState)}`;

// ---------- 元信息对比 ----------
const metaRows = computed(() => {
  if (!aInfo.value || !bInfo.value) return [];
  const rows = [
    { label: '流程编号', a: aInfo.value.processNumber, b: bInfo.value.processNumber },
    { label: 'formCode', a: props.row?.processKey, b: props.row?.processKey },
    { label: '流程版本', a: aInfo.value.version, b: bInfo.value.version },
    { label: '发起人', a: aInfo.value.userName || aInfo.value.createUser || '-', b: bInfo.value.userName || bInfo.value.createUser || '-' },
    { label: '发起时间', a: formatTime(aInfo.value.createTime), b: formatTime(bInfo.value.createTime) },
    { label: '状态', a: stateName(aInfo.value.processState), b: stateName(bInfo.value.processState) },
  ];
  rows.forEach(r => { r.changed = String(r.a) !== String(r.b); });
  return rows;
});

// ---------- 打开 ----------
watch(showDlg, async (v) => {
  if (!v || !props.row?.processNumber) return;
  resetState();
  loading.value = true;
  try {
    const res = await getCompareCandidates(props.row.processKey);
    const list = (res.data?.data || res.data) || [];
    bCandidates.value = list;
    aInfo.value = list.find(c => c.processNumber === props.row.processNumber) || fallbackA();
    if (!aInfo.value.confId) {
      aInfo.value.confId = props.row.confId;
    }
  } catch (e) {
    console.error('compareCandidates failed', e);
  } finally {
    loading.value = false;
  }
});

function resetState() {
  loaded.value = false;
  aInfo.value = null;
  bInfo.value = null;
  bKey.value = '';
  bCandidates.value = [];
  treeA.value = null;
  treeB.value = null;
  nodeResults.value = [];
  activeKey.value = '';
  diffIdx.value = 0;
}

/** 候选列表里找不到自己(如已被逻辑删除)时用监控行数据兜底 */
function fallbackA() {
  return {
    processNumber: props.row.processNumber,
    version: props.row.version,
    createUser: props.row.userId,
    userName: props.row.userName || '',
    createTime: props.row.createTime,
    processState: props.row.processState,
    confId: props.row.confId,
  };
}

// ---------- B 侧选择 ----------
async function remoteSearch(q) {
  searching.value = true;
  try {
    const res = await getCompareCandidates(props.row.processKey, q || '');
    bCandidates.value = (res.data?.data || res.data) || [];
  } catch (e) {
    console.error('remoteSearch failed', e);
  } finally {
    searching.value = false;
  }
}

async function onBChange(val) {
  const c = bCandidates.value.find(x => x.processNumber === val);
  if (!c) return;
  bInfo.value = c;
  await doCompare();
}

function swap() {
  if (!bInfo.value) return;
  const oldA = aInfo.value;
  aInfo.value = bInfo.value;
  bInfo.value = oldA;
  bKey.value = oldA.processNumber;
  doCompare();
}

// ---------- 核心对比 ----------
async function doCompare() {
  if (!aInfo.value || !bInfo.value) return;
  loading.value = true;
  loaded.value = false;
  activeKey.value = '';
  diffIdx.value = 0;
  try {
    const [detA, detB, verA, verB, entA, entB] = await Promise.all([
      getApiWorkFlowData({ id: aInfo.value.confId }),
      getApiWorkFlowData({ id: bInfo.value.confId }),
      getBpmVerifyInfoVos({ processNumber: aInfo.value.processNumber }),
      getBpmVerifyInfoVos({ processNumber: bInfo.value.processNumber }),
      getCompareEntrusts(aInfo.value.processNumber),
      getCompareEntrusts(bInfo.value.processNumber),
    ]);
    const clone = (x) => JSON.parse(JSON.stringify(x || {}));
    const rawA = detA.data?.data || detA.data;
    const rawB = detB.data?.data || detB.data;
    const treeDataA = FormatDisplayUtils.getToTree(clone(rawA));
    const treeDataB = FormatDisplayUtils.getToTree(clone(rawB));

    const personsA = groupPersons(verA.data?.data || verA.data || []);
    const personsB = groupPersons(verB.data?.data || verB.data || []);
    const entrustsA = groupEntrusts(entA.data?.data || entA.data || []);
    const entrustsB = groupEntrusts(entB.data?.data || entB.data || []);

    const { sList, tList, pairs } = alignTrees(treeDataA?.nodeConfig, treeDataB?.nodeConfig);

    const results = [];
    for (const [key, pair] of pairs) {
      const r = buildResult(key, pair, sList, tList, personsA, personsB, entrustsA, entrustsB);
      results.push(r);
      // 回写渲染标记
      if (pair.source) {
        pair.source.node.__diff = r.status;
        pair.source.node.__pairKey = key;
      }
      if (pair.target) {
        pair.target.node.__diff = r.status;
        pair.target.node.__pairKey = key;
      }
    }
    nodeResults.value = results;
    treeA.value = treeDataA?.nodeConfig || null;
    treeB.value = treeDataB?.nodeConfig || null;
    loaded.value = true;
  } catch (e) {
    console.error('doCompare failed', e);
    proxyMsg(String(e?.message || e));
  } finally {
    loading.value = false;
  }
}

/** 审批路径 → nodeId → [{id,name}] (排除发起/结束虚拟条目) */
function groupPersons(vos) {
  const map = new Map();
  for (const v of vos) {
    if (!v.nodeId || !v.verifyUserId) continue;
    const key = String(v.nodeId);
    if (!map.has(key)) map.set(key, []);
    const list = map.get(key);
    if (!list.some(p => p.id === v.verifyUserId)) {
      list.push({ id: v.verifyUserId, name: stripMark(v.verifyUserName) || v.verifyUserId });
    }
  }
  return map;
}

/** 加减签记录 → nodeId 分组 */
function groupEntrusts(list) {
  const map = new Map();
  for (const r of list) {
    if (!r.nodeId) continue;
    const key = String(r.nodeId);
    if (!map.has(key)) map.set(key, []);
    map.get(key).push(r);
  }
  return map;
}

function buildResult(key, pair, sList, tList, personsA, personsB, entrustsA, entrustsB) {
  const sNode = pair.source?.node, tNode = pair.target?.node;
  const sId = sNode?.id != null ? String(sNode.id) : null;
  const tId = tNode?.id != null ? String(tNode.id) : null;
  const pa = sId ? (personsA.get(sId) || []) : [];
  const pb = tId ? (personsB.get(tId) || []) : [];
  const ea = sId ? (entrustsA.get(sId) || []) : [];
  const eb = tId ? (entrustsB.get(tId) || []) : [];

  const r = { key, pair, status: pair.status, entrustsA: ea, entrustsB: eb,
    personDiff: null, personTip: '', ruleRows: [], oneSidePersons: [], oneSideEntrusts: [] };

  if (pair.status === 'added') {
    r.oneSidePersons = pb;
    r.oneSideEntrusts = eb;
    return r;
  }
  if (pair.status === 'removed') {
    r.oneSidePersons = pa;
    r.oneSideEntrusts = ea;
    return r;
  }

  // 配对节点: 人员集合比较
  const bothExecuted = pa.length > 0 && pb.length > 0;
  if (bothExecuted) {
    const idsA = new Set(pa.map(p => p.id));
    const idsB = new Set(pb.map(p => p.id));
    r.personDiff = {
      both: pa.filter(p => idsB.has(p.id)),
      onlyA: pa.filter(p => !idsB.has(p.id)),
      onlyB: pb.filter(p => !idsA.has(p.id)),
    };
  } else {
    r.personTip = !pa.length && !pb.length
      ? '两侧该节点均未执行, 不参与人员对比'
      : `仅 ${pa.length ? 'A' : 'B'} 侧该节点已执行, 不参与人员对比`;
  }

  // 设计期审批人规则差异(复用版本比较引擎, 只取「审批人设置」section)
  try {
    const sections = diffNodePair(pair, sList, tList, {});
    r.ruleRows = sections.get('审批人设置') || [];
  } catch (e) {
    console.warn('rule diff failed', e);
  }

  const personDiffers = r.personDiff && (r.personDiff.onlyA.length || r.personDiff.onlyB.length);
  const nameDiffers = (sNode?.nodeName || '') !== (tNode?.nodeName || '');
  if (personDiffers || ea.length || eb.length || nameDiffers || r.ruleRows.length) {
    r.status = 'modified';
    pair.status = 'modified';
  } else {
    r.status = 'same';
    pair.status = 'same';
  }
  return r;
}

// ---------- 差异导航 ----------
const diffKeys = computed(() => nodeResults.value.filter(r => r.status !== 'same').map(r => r.key));
const diffCount = computed(() => diffKeys.value.length);
const activeResult = computed(() => nodeResults.value.find(r => r.key === activeKey.value) || null);
const activeTitle = computed(() => {
  const r = activeResult.value;
  if (!r) return '';
  const s = r.pair.source?.node, t = r.pair.target?.node;
  if (r.status === 'added') return `B 新增节点 · ${t?.nodeName || ''}`;
  if (r.status === 'removed') return `B 减少节点 · ${s?.nodeName || ''}`;
  return t?.nodeName || s?.nodeName || '';
});

function onNodeSelect(node) {
  if (node?.__pairKey) activeKey.value = node.__pairKey;
}
function nextDiff() {
  if (!diffKeys.value.length) return;
  activeKey.value = diffKeys.value[diffIdx.value % diffKeys.value.length];
  diffIdx.value = (diffIdx.value + 1) % diffKeys.value.length;
}

const names = (list) => list.map(p => p.name).join('、') || '-';

const { proxy } = getCurrentInstance();
function proxyMsg(msg) { proxy?.$modal?.msgError?.(msg); }

// ---------- 局部渲染小组件 ----------
const personList = defineComponent({
  name: 'personList',
  props: {
    title: { type: String, default: '' },
    persons: { type: Array, default: () => [] },
    entrusts: { type: Array, default: () => [] },
  },
  setup(p) {
    return () => h('div', { class: 'pc-section' }, [
      h('div', { class: 'pc-sec-head' }, p.title),
      h('div', { class: 'pc-none' }, p.persons.length
        ? p.persons.map(x => x.name).join('、')
        : '该侧无实际审批人记录'),
      p.entrusts.length
        ? h('div', { class: 'pc-none', style: 'margin-top:6px' },
          `加减签/转办 ${p.entrusts.length} 条: ` + p.entrusts.map(e => `[${e.actionTypeName}] ${e.originalName || ''} → ${e.actualName || ''}`).join('; '))
        : null,
    ]);
  },
});
const entrustTable = defineComponent({
  name: 'entrustTable',
  props: {
    side: { type: String, default: '' },
    records: { type: Array, default: () => [] },
  },
  setup(p) {
    if (!p.records.length) return () => null;
    return () => h('div', { class: 'pc-entrust' }, [
      h('div', { class: 'pc-entrust-side' }, `${p.side} 侧 (${p.records.length})`),
      h('table', { class: 'pc-table' }, [
        h('thead', null, h('tr', [
          h('th', { style: 'width:60px' }, '类型'),
          h('th', null, '原审批人'),
          h('th', null, '实际/被操作人'),
        ])),
        h('tbody', null, p.records.map((r, i) => h('tr', { key: i }, [
          h('td', null, r.actionTypeName),
          h('td', null, r.originalName || '-'),
          h('td', null, r.actualName || '-'),
        ]))),
      ]),
    ]);
  },
});
</script>

<style lang="scss" scoped>
.pc-dlg-title {
  display: flex;
  align-items: center;
  gap: 10px;
  font-size: 16px;
  font-weight: 600;
  color: #303640;
}

.pc-body {
  height: calc(92vh - 120px);
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.pc-picker {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 2px 2px 8px;
  flex-shrink: 0;

  .pc-picker-item {
    display: flex;
    align-items: center;
    gap: 8px;
  }
  .pc-picker-label { font-size: 13px; color: #5a6474; white-space: nowrap; }
  .pc-picker-fixed {
    font-size: 13px;
    color: #303640;
    font-weight: 600;
    padding: 4px 8px;
    background: #f3f5f9;
    border-radius: 4px;
  }
  .pc-picker-tip { font-size: 12px; color: #98a1b0; margin-left: auto; }
}

.pc-meta {
  flex-shrink: 0;
  width: 100%;
  border-collapse: collapse;
  font-size: 12px;
  margin-bottom: 8px;

  th, td {
    border: 1px solid #e3e7ee;
    padding: 4px 10px;
    text-align: left;
  }
  th { background: #f3f5f9; color: #5a6474; font-weight: 600; }
  td { color: #303640; }
  tr.pc-changed td { background: #fff7ec; color: #b9741a; font-weight: 600; }
}

.pc-toolbar {
  display: flex;
  align-items: center;
  gap: 12px;
  padding-bottom: 8px;
  flex-shrink: 0;
}

.pc-legend {
  display: flex;
  align-items: center;
  gap: 8px;

  .lg {
    font-size: 11px;
    padding: 0 6px;
    border-radius: 3px;

    &.lg-add { color: #3fa84f; border: 1px solid #3fa84f; }
    &.lg-del { color: #d35454; border: 1px dashed #d35454; }
    &.lg-mod { color: #e69a2e; border: 1px solid #e69a2e; }
  }
  .pc-legend-tip { font-size: 12px; color: #98a1b0; margin-left: 8px; }
}

.pc-node-layout {
  flex: 1;
  min-height: 0;
  display: flex;
  gap: 10px;

  > :nth-child(1), > :nth-child(2) { flex: 1; min-width: 0; }
}

.pc-detail {
  flex: 0.9;
  min-width: 280px;
  display: flex;
  flex-direction: column;
  border: 1px solid #e3e7ee;
  border-radius: 6px;
  background: #fff;
}

.pc-detail-head {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 8px 12px;
  border-bottom: 1px solid #e3e7ee;
  font-weight: 600;
  font-size: 13px;
  color: #333c4d;
}

.pc-dot {
  width: 10px;
  height: 10px;
  border-radius: 50%;
  flex-shrink: 0;

  &.add { background: #3fa84f; }
  &.del { background: #d35454; }
  &.mod { background: #e69a2e; }
  &.same { background: #c3cad6; }
}

.pc-detail-scroll {
  flex: 1;
  overflow: auto;
  padding: 10px 12px;
}

.pc-section { margin-bottom: 14px; }

.pc-sec-head {
  font-size: 13px;
  font-weight: 600;
  color: #333c4d;
  margin-bottom: 6px;

  .pc-sec-sub { font-size: 11px; color: #98a1b0; font-weight: 400; }
}

.pc-table {
  width: 100%;
  border-collapse: collapse;
  font-size: 12px;

  th, td {
    border: 1px solid #e3e7ee;
    padding: 4px 8px;
    text-align: left;
    vertical-align: top;
    word-break: break-all;
  }
  th { background: #f3f5f9; color: #5a6474; font-weight: 600; }

  tr.pc-row-same td { background: #f4fcf5; }
  tr.pc-row-a td { background: #f0f6ff; }
  tr.pc-row-b td { background: #fff7ec; }
}

.pc-none {
  font-size: 12px;
  color: #98a1b0;
  padding: 4px 0;
}

.pc-entrust {
  margin-bottom: 8px;

  .pc-entrust-side {
    font-size: 12px;
    color: #5a6474;
    margin-bottom: 4px;
    font-weight: 600;
  }
}
</style>
