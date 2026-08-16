<!--
  版本比较 全屏 dialog
  设计: .scratch/版本比较功能设计.md
  - 顶部: 源版本/目标版本下拉 + 互换, 选版即自动比较
  - 三 tab: 设置差异(总结式全量) / 表单差异(核心3字段+增删) / 节点差异(双图并排+右侧固定差异区)
-->
<template>
  <el-dialog v-model="showDlg" :title="dialogTitle" width="94%" top="3vh" append-to-body
    class="vc-dialog" :close-on-click-modal="false" destroy-on-close>
    <template #header>
      <div class="vc-dlg-title">
        <span>版本比较 - {{ formName || formCode }}</span>
        <el-tag v-if="result" :type="result.totalDiff ? 'warning' : 'success'" size="small">
          {{ result.totalDiff ? `共 ${result.totalDiff} 处差异` : '两版本配置完全一致' }}
        </el-tag>
      </div>
    </template>

    <div class="vc-body" v-loading="loading" element-loading-text="正在比较...">
      <!-- 选版区 -->
      <div class="vc-picker">
        <div class="vc-picker-item">
          <span class="vc-picker-label">源版本(基准)</span>
          <el-select v-model="sourceId" placeholder="选择源版本" filterable style="width: 320px" size="default">
            <el-option v-for="v in versions" :key="v.id" :value="v.id"
              :label="optionLabel(v)" :disabled="v.id === targetId" />
          </el-select>
        </div>
        <el-button circle size="small" title="互换源/目标" @click="swap" :disabled="!sourceId || !targetId">
          <el-icon style="transform: rotate(90deg)">
            <Switch />
          </el-icon>
        </el-button>
        <div class="vc-picker-item">
          <span class="vc-picker-label">目标版本(比较)</span>
          <el-select v-model="targetId" placeholder="选择目标版本" filterable style="width: 320px" size="default">
            <el-option v-for="v in versions" :key="v.id" :value="v.id"
              :label="optionLabel(v)" :disabled="v.id === sourceId" />
          </el-select>
        </div>
        <div class="vc-picker-tip">差异方向统一为「源值 → 目标值」</div>
      </div>

      <el-alert v-if="versions.length > 0 && versions.length < 2" type="warning" :closable="false" show-icon
        title="该流程类型仅有一个版本, 无法比较" style="margin-bottom: 10px" />
      <el-alert v-else-if="loaded && result && result.totalDiff === 0" type="success" :closable="false" show-icon
        title="两个版本配置完全一致, 无任何差异" style="margin-bottom: 10px" />

      <template v-if="loaded && result">
        <el-tabs v-model="activeTab" class="vc-tabs">
          <!-- ① 设置差异 -->
          <el-tab-pane name="settings">
            <template #label>设置差异<span class="vc-badge" v-if="result.settingDiffCount">{{ result.settingDiffCount }}</span></template>
            <div class="vc-tab-body vc-tab-scroll">
              <table class="vc-table">
                <thead>
                  <tr><th style="width:110px">分组</th><th style="width:200px">配置项</th><th>源版本</th><th>目标版本</th></tr>
                </thead>
                <tbody>
                  <tr v-for="r in result.settingsRows" :key="r.group + r.label" :class="{ 'vc-changed': r.changed }">
                    <td>{{ r.group }}</td>
                    <td>{{ r.label }}</td>
                    <td>{{ r.source }}</td>
                    <td>{{ r.target }}</td>
                  </tr>
                </tbody>
              </table>
            </div>
          </el-tab-pane>

          <!-- ② 表单差异 -->
          <el-tab-pane name="forms">
            <template #label>表单差异<span class="vc-badge" v-if="result.formDiffCount">{{ result.formDiffCount }}</span></template>
            <div class="vc-tab-body vc-tab-scroll">
              <el-empty v-if="!result.forms.available" :description="result.forms.reason" />
              <el-empty v-else-if="result.formDiffCount === 0" description="表单设计无差异" />
              <template v-else>
                <div v-for="g in result.forms.groups" :key="g.name" class="vc-form-group">
                  <div class="vc-form-head">
                    <span class="vc-form-name">{{ g.name }}</span>
                    <span class="vc-form-counts">
                      <el-tag v-if="g.counts.added" type="success" size="small">新增 {{ g.counts.added }}</el-tag>
                      <el-tag v-if="g.counts.removed" type="danger" size="small">删除 {{ g.counts.removed }}</el-tag>
                      <el-tag v-if="g.counts.modified" type="warning" size="small">修改 {{ g.counts.modified }}</el-tag>
                    </span>
                  </div>
                  <table class="vc-table" v-if="g.fields.length">
                    <thead>
                      <tr><th style="width:180px">字段</th><th style="width:120px">差异项</th><th>源版本</th><th>目标版本</th></tr>
                    </thead>
                    <tbody>
                      <template v-for="f in g.fields" :key="g.name + f.name">
                        <tr v-for="(c, ci) in f.changes" :key="f.name + c.label"
                          :class="'vc-st-' + f.status">
                          <td :title="f.name">{{ ci === 0 ? (f.label || f.name) : '' }}</td>
                          <td>
                            <el-tag size="small" :type="f.status === 'added' ? 'success' : f.status === 'removed' ? 'danger' : 'warning'">
                              {{ f.status === 'added' ? '新增' : f.status === 'removed' ? '删除' : c.label }}
                            </el-tag>
                          </td>
                          <td>{{ c.source }}</td>
                          <td>{{ c.target }}</td>
                        </tr>
                      </template>
                    </tbody>
                  </table>
                </div>
              </template>
            </div>
          </el-tab-pane>

          <!-- ③ 节点差异 -->
          <el-tab-pane name="nodes">
            <template #label>节点差异<span class="vc-badge" v-if="result.nodeDiffCount">{{ result.nodeDiffCount }}</span></template>
            <div class="vc-tab-body vc-nodes">
              <div class="vc-node-toolbar">
                <el-button size="small" type="primary" plain :disabled="!diffKeys.length" @click="nextDiff">
                  下一处差异 ({{ diffKeys.length ? diffIdx + 1 : 0 }}/{{ diffKeys.length }})
                </el-button>
                <div class="vc-legend">
                  <span class="lg lg-add">新增</span>
                  <span class="lg lg-del">删除</span>
                  <span class="lg lg-mod">修改</span>
                  <span class="vc-legend-tip">点击任一侧节点查看属性差异</span>
                </div>
              </div>
              <div class="vc-node-layout">
                <compareTree :tree="result.sourceTree" title="源版本" :versionText="versionText(sourceId)"
                  :activeKey="activeKey" @select="onNodeSelect" />
                <compareTree :tree="result.targetTree" title="目标版本" :versionText="versionText(targetId)"
                  :activeKey="activeKey" @select="onNodeSelect" />
                <div class="vc-detail">
                  <template v-if="activeResult">
                    <div class="vc-detail-head">
                      <div class="vc-detail-name">
                        <span v-if="activeResult.status === 'added'" class="vc-dot add"></span>
                        <span v-else-if="activeResult.status === 'removed'" class="vc-dot del"></span>
                        <span v-else-if="activeResult.status === 'modified'" class="vc-dot mod"></span>
                        <span v-else class="vc-dot same"></span>
                        {{ activeTitle }}
                      </div>
                      <div class="vc-detail-sub" v-if="activeResult.status === 'modified' && activeResult.diffCount">
                        共 {{ activeResult.diffCount }} 处差异
                      </div>
                    </div>
                    <div class="vc-detail-scroll">
                      <div v-for="sec in activeSections" :key="sec.name" class="vc-section"
                        :class="{ 'no-diff': !sec.count }">
                        <div class="vc-section-head">
                          <span>{{ sec.name }}</span>
                          <span class="vc-section-count" v-if="sec.count">{{ sec.count }}</span>
                          <span class="vc-section-none" v-else>无差异</span>
                        </div>
                        <template v-if="sec.rows.length">
                          <table class="vc-table vc-table-sm" v-if="plainRows(sec.rows).length">
                            <tbody>
                              <tr v-for="(r, ri) in plainRows(sec.rows)" :key="sec.name + 'r' + ri">
                                <td style="width:150px">{{ r.label }}</td>
                                <td>
                                  <div class="vc-old">{{ r.source }}</div>
                                  <div class="vc-arrow-inline">↓</div>
                                  <div class="vc-new">{{ r.target }}</div>
                                </td>
                              </tr>
                            </tbody>
                          </table>
                          <!-- 表单权限: 全量字段对照表 -->
                          <div v-for="g in formPermGroups(sec.rows)" :key="sec.name + g.name" class="vc-perm">
                            <div class="vc-perm-head">
                              <span>{{ g.name }}</span>
                              <span v-if="g.hidden && g.hidden.changed" class="vc-perm-hidden">
                                整表隐藏: {{ g.hidden.source ? '隐藏' : '不隐藏' }} → {{ g.hidden.target ? '隐藏' : '不隐藏' }}
                              </span>
                            </div>
                            <table class="vc-table vc-table-sm">
                              <thead>
                                <tr><th>字段</th><th style="width:110px">源版本</th><th style="width:110px">目标版本</th></tr>
                              </thead>
                              <tbody>
                                <tr v-for="f in g.fields" :key="g.name + f.fieldId"
                                  :class="f.status === 'same' ? 'vc-perm-same' : 'vc-perm-changed'">
                                  <td>
                                    {{ f.fieldName }}
                                    <span v-if="f.status === 'added'" class="vc-perm-tag add">新增</span>
                                    <span v-else-if="f.status === 'removed'" class="vc-perm-tag del">删除</span>
                                  </td>
                                  <td>{{ permName(f.sourcePerm) }}</td>
                                  <td>{{ permName(f.targetPerm) }}</td>
                                </tr>
                              </tbody>
                            </table>
                          </div>
                        </template>
                      </div>
                    </div>
                  </template>
                  <el-empty v-else description="点击左侧流程图中的节点, 查看该节点的属性差异" :image-size="70" />
                </div>
              </div>
            </div>
          </el-tab-pane>
        </el-tabs>
      </template>
    </div>
  </el-dialog>
</template>

<script setup>
import { ref, watch, computed } from 'vue';
import { Switch } from '@element-plus/icons-vue';
import { getBpmnConflistPage, getApiWorkFlowData } from '@/api/workflow/index';
import { compareConfs, nodeTitle, NODE_SECTIONS } from '@/utils/antflow/versionCompare';
import compareTree from './compareTree.vue';

const props = defineProps({
  visible: { type: Boolean, default: false },
  formCode: { type: String, default: '' },
  formName: { type: String, default: '' },
});
const emit = defineEmits(['update:visible']);

const showDlg = computed({
  get: () => props.visible,
  set: v => emit('update:visible', v),
});

const loading = ref(false);
const loaded = ref(false);
const versions = ref([]);
const sourceId = ref(null);
const targetId = ref(null);
const activeTab = ref('settings');
const result = ref(null);

const dialogTitle = computed(() => `版本比较 - ${props.formName || props.formCode}`);

const optionLabel = (v) =>
  `${v.bpmnName || v.bpmnCode}${v.effectiveStatus == 1 ? '(活跃)' : ''} · ${v.bpmnCode}`;
const versionText = (id) => {
  const v = versions.value.find(x => x.id === id);
  return v ? v.bpmnCode : '';
};

/** 打开时加载版本列表并给默认选版 */
watch(() => props.visible, async (val) => {
  if (!val || !props.formCode) return;
  loading.value = true;
  loaded.value = false;
  result.value = null;
  activeTab.value = 'settings';
  activeKey.value = '';
  diffIdx.value = 0;
  try {
    const res = await getBpmnConflistPage({ page: 1, pageSize: 1000 }, {
      formCode: props.formCode, isOutSideProcess: 0,
    });
    const list = (res.data?.data || []).slice().sort((a, b) => (b.id || 0) - (a.id || 0));
    versions.value = list;
    if (list.length >= 2) {
      // 默认: 目标=当前活跃版本, 源=活跃版的上一个(更早)版本; 活跃版已是最早版则取更新的一个; 无活跃版退化为 最新 vs 次新
      const activeIdx = list.findIndex(v => v.effectiveStatus == 1);
      if (activeIdx >= 0) {
        targetId.value = list[activeIdx].id;
        const prev = list[activeIdx + 1] || (activeIdx > 0 ? list[0] : null);
        sourceId.value = prev ? prev.id : null;
      } else {
        targetId.value = list[0].id;
        sourceId.value = list[1].id;
      }
    }
  } finally {
    loading.value = false;
  }
});

/** 选版变化 → 自动比较 */
watch([sourceId, targetId], async ([s, t]) => {
  if (!s || !t || s === t) return;
  loading.value = true;
  activeKey.value = '';
  diffIdx.value = 0;
  try {
    const [sr, tr] = await Promise.all([getApiWorkFlowData({ id: s }), getApiWorkFlowData({ id: t })]);
    if (sr.code != 200 || tr.code != 200) {
      throw new Error(sr.errMsg || tr.errMsg || '加载版本详情失败');
    }
    result.value = compareConfs(sr.data, tr.data);
    loaded.value = true;
  } catch (e) {
    proxyMsg(String(e.message || e));
  } finally {
    loading.value = false;
  }
});

const { proxy } = getCurrentInstance();
function proxyMsg(msg) { proxy?.$modal?.msgError?.(msg); }

function swap() {
  const s = sourceId.value;
  sourceId.value = targetId.value;
  targetId.value = s;
}

// ===== 节点差异导航 =====
const activeKey = ref('');
const diffIdx = ref(0);
const diffKeys = computed(() =>
  (result.value?.nodeResults || []).filter(r => r.status !== 'same').map(r => r.key));
const activeResult = computed(() =>
  (result.value?.nodeResults || []).find(r => r.key === activeKey.value) || null);
const activeSections = computed(() => {
  if (!activeResult.value) return [];
  return NODE_SECTIONS.map(name => {
    const rows = activeResult.value.sections.get(name) || [];
    const count = rows.reduce((n, r) => n + (r.kind === 'formPermTable' ? (r.formPermCount || 0) : 1), 0);
    return { name, rows, count };
  }).filter(sec => sec.count || ['基本信息', '审批人设置', '高级设置'].includes(sec.name));
});

/** 普通差异行(排除表单权限表格行) */
function plainRows(rows) {
  return rows.filter(r => r.kind !== 'formPermTable');
}
/** 表单权限表格分组(聚合各 formPermTable 行的 groups) */
function formPermGroups(rows) {
  const out = [];
  for (const r of rows) {
    if (r.kind === 'formPermTable' && Array.isArray(r.groups)) out.push(...r.groups);
  }
  return out;
}
const PERM_NAMES = { R: '只读', E: '可编辑', H: '隐藏' };
function permName(p) { return p === null || p === undefined || p === '' ? '(无配置)' : (PERM_NAMES[p] || String(p)); }
const activeTitle = computed(() => {
  const r = activeResult.value;
  if (!r) return '';
  const s = r.pair.source?.node, t = r.pair.target?.node;
  if (r.status === 'added') return `新增节点 · ${nodeTitle(t)}`;
  if (r.status === 'removed') return `删除节点 · ${nodeTitle(s)}`;
  const nameDiff = s?.nodeName !== t?.nodeName ? `${s?.nodeName || ''} → ${t?.nodeName || ''}` : (t?.nodeName || '');
  return nameDiff;
});

function onNodeSelect(node) {
  if (node?.__pairKey) activeKey.value = node.__pairKey;
}
function nextDiff() {
  if (!diffKeys.value.length) return;
  activeKey.value = diffKeys.value[diffIdx.value % diffKeys.value.length];
  diffIdx.value = (diffIdx.value + 1) % diffKeys.value.length;
}
</script>

<style lang="scss" scoped>
.vc-dlg-title {
  display: flex;
  align-items: center;
  gap: 10px;
  font-size: 16px;
  font-weight: 600;
  color: #303640;
}

.vc-body {
  height: calc(92vh - 130px);
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.vc-picker {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 4px 2px 10px;
  flex-shrink: 0;

  .vc-picker-item {
    display: flex;
    align-items: center;
    gap: 8px;
  }
  .vc-picker-label { font-size: 13px; color: #5a6474; white-space: nowrap; }
  .vc-picker-tip { font-size: 12px; color: #98a1b0; margin-left: auto; }
}

.vc-tabs {
  flex: 1;
  display: flex;
  flex-direction: column;
  min-height: 0;
  :deep(.el-tabs__content) { flex: 1; min-height: 0; }
  :deep(.el-tab-pane) { height: 100%; }

  .vc-badge {
    display: inline-block;
    min-width: 18px;
    height: 18px;
    line-height: 17px;
    margin-left: 4px;
    padding: 0 5px;
    border-radius: 9px;
    background: #e69a2e;
    color: #fff;
    font-size: 11px;
    text-align: center;
  }
}

.vc-tab-body {
  height: 100%;
  min-height: 0;
}
.vc-tab-scroll {
  overflow: auto;
  border: 1px solid #e3e7ee;
  border-radius: 6px;
  background: #fff;
}

.vc-table {
  width: 100%;
  border-collapse: collapse;
  font-size: 13px;

  th, td {
    border-bottom: 1px solid #eef1f6;
    padding: 7px 12px;
    text-align: left;
    color: #3d4655;
    word-break: break-all;
    vertical-align: top;
  }
  th { background: #f7f9fc; font-weight: 600; color: #5a6474; }

  tr.vc-changed td { background: #fdf6ec; }
  tr.vc-st-added td { background: #f0f9eb; }
  tr.vc-st-removed td { background: #fef0f0; }
  tr.vc-st-modified td { background: #fdf6ec; }
}
.vc-table-sm td { font-size: 12px; padding: 5px 10px; }

// 表单组
.vc-form-group { margin-bottom: 14px; }
.vc-form-head {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 6px 2px;
  .vc-form-name { font-weight: 600; font-size: 13px; color: #333c4d; }
}

// 节点 tab
.vc-nodes {
  display: flex;
  flex-direction: column;
}
.vc-node-toolbar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding-bottom: 8px;
  flex-shrink: 0;
}
.vc-legend {
  display: flex;
  align-items: center;
  gap: 12px;
  font-size: 12px;
  color: #5a6474;

  .lg { padding: 0 8px; border-radius: 3px; line-height: 20px; }
  .lg-add { background: #f0f9eb; border: 1px solid #3fa84f; color: #3fa84f; }
  .lg-del { background: #fef0f0; border: 1px dashed #d35454; color: #d35454; }
  .lg-mod { background: #fdf6ec; border: 1px solid #e69a2e; color: #b57d22; }
  .vc-legend-tip { color: #98a1b0; }
}
.vc-node-layout {
  flex: 1;
  min-height: 0;
  display: flex;
  gap: 10px;
}
.vc-detail {
  width: 400px;
  flex-shrink: 0;
  display: flex;
  flex-direction: column;
  border: 1px solid #e3e7ee;
  border-radius: 6px;
  background: #fff;

  .vc-detail-head {
    padding: 8px 12px;
    border-bottom: 1px solid #e3e7ee;
    background: #f3f5f9;
    display: flex;
    justify-content: space-between;
    align-items: center;

    .vc-detail-name { font-weight: 600; font-size: 13px; color: #333c4d; display: flex; align-items: center; gap: 6px; }
    .vc-detail-sub { font-size: 12px; color: #b57d22; }
  }
  .vc-detail-scroll { flex: 1; overflow: auto; padding: 8px; }
}

.vc-dot {
  width: 10px;
  height: 10px;
  border-radius: 50%;
  display: inline-block;
  &.add { background: #3fa84f; }
  &.del { background: #d35454; }
  &.mod { background: #e69a2e; }
  &.same { background: #b6bfce; }
}

.vc-section {
  margin-bottom: 8px;
  border: 1px solid #eef1f6;
  border-radius: 6px;
  overflow: hidden;

  .vc-section-head {
    display: flex;
    align-items: center;
    gap: 8px;
    padding: 6px 10px;
    background: #f7f9fc;
    font-size: 13px;
    font-weight: 600;
    color: #4a5568;
    .vc-section-count {
      min-width: 18px;
      height: 18px;
      line-height: 17px;
      padding: 0 5px;
      border-radius: 9px;
      background: #e69a2e;
      color: #fff;
      font-size: 11px;
      text-align: center;
    }
    .vc-section-none { font-size: 12px; font-weight: 400; color: #98a1b0; }
  }
  &.no-diff .vc-section-head { background: #fafbfd; }
}

.vc-old { color: #a05555; text-decoration: line-through; text-decoration-color: rgba(160, 85, 85, 0.5); }
.vc-arrow-inline { color: #98a1b0; text-align: center; font-size: 11px; line-height: 14px; }
.vc-new { color: #2f7d43; font-weight: 500; }

/* 表单字段权限对照表 */
.vc-perm {
  margin-bottom: 10px;
  .vc-perm-head {
    display: flex; align-items: center; gap: 10px;
    padding: 4px 8px; font-size: 12px; font-weight: 600; color: #4a5568;
    background: #f5f6f6; border-radius: 4px 4px 0 0;
    .vc-perm-hidden { font-weight: 400; color: #b7791f; }
  }
  .vc-table-sm { border-radius: 0 0 4px 4px; }
  tr.vc-perm-same { color: #98a1b0; }
  tr.vc-perm-changed td:first-child { font-weight: 600; color: #b7791f; }
  .vc-perm-tag {
    display: inline-block; margin-left: 6px; padding: 0 5px;
    font-size: 11px; line-height: 16px; border-radius: 3px; color: #fff;
    &.add { background: #48a868; }
    &.del { background: #d46a6a; }
  }
}
</style>
