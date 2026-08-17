<!--
  流程诊断 全屏 dialog (流程管理-流程监控-更多-流程诊断)
  设计: .scratch/process-diagnosis-design.md
  三栏: 流程图(设计母版, FlowTree) | 审批路径(真实执行, getBpmVerifyInfoVos 带 nodeId) | 右侧 tabs(节点诊断/调试)
  - 图节点 __mark: hit=审批路径已执行 / current=当前停留; __pairKey=node.id 供定位
  - 审批路径点击 → 图联动高亮; nodeId 为 null(发起人除外)标灰 + console.error
  - 节点诊断: "为什么 有/没有 此节点" → POST /bpmnConf/diagnoseNode 后端归因
  - 调试: debugPanel 发起人只读固定为 create_user, 预填真实表单值
-->
<template>
  <el-dialog v-model="showDlg" title="流程诊断" width="94%" top="3vh" append-to-body
    class="diag-dialog" :close-on-click-modal="false" destroy-on-close>
    <template #header>
      <div class="diag-dlg-title">
        <span>流程诊断 - {{ processNumber }}</span>
        <el-tag v-if="initInfo" size="small" :type="initInfo.processFinished ? 'info' : 'success'">
          {{ initInfo.processFinished ? '流程已结束' : '流程进行中' }}
        </el-tag>
        <el-tag v-if="initInfo" size="small" type="warning">版本 {{ initInfo.bpmnCode }}</el-tag>
      </div>
    </template>

    <div class="diag-body" v-loading="loading" element-loading-text="正在加载诊断数据...">
      <template v-if="loaded">
        <!-- ① 流程图(设计母版) -->
        <div class="diag-graph">
          <flowTree :tree="tree" title="流程图 (设计母版)" :versionText="initInfo?.bpmnCode || ''"
            :activeKey="activeKey" @select="onNodeSelect">
            <template #legend>
              <span class="lg lg-hit">已执行</span>
              <span class="lg lg-cur">当前</span>
            </template>
          </flowTree>
        </div>

        <!-- ② 审批路径(真实执行) -->
        <div class="diag-path">
          <div class="diag-col-head">
            <span>审批路径 (真实执行)</span>
            <span class="diag-col-sub">点击条目定位流程图节点</span>
          </div>
          <div class="diag-path-scroll">
            <div v-for="(it, idx) in pathList" :key="idx"
              class="diag-path-item" :class="{
                'is-current': it.verifyStatus === 99,
                'no-node': it.noNode,
                'is-virtual': it.virtual,
              }" @click="onPathClick(it)">
              <div class="dpi-head">
                <span class="dpi-name">{{ it.taskName || '-' }}</span>
                <el-tag size="small" :type="statusTagType(it.verifyStatus)">{{ it.verifyStatusName }}</el-tag>
              </div>
              <div class="dpi-user">{{ it.verifyUserName || '-' }}</div>
              <div class="dpi-meta">
                <span>{{ it.verifyDate ? formatTime(it.verifyDate) : '' }}</span>
                <span class="dpi-node" v-if="it.nodeId">#{{ shortId(it.nodeId) }}</span>
                <span class="dpi-node-none" v-else-if="!it.virtual">{{ it.noNode ? 'node_id缺失' : '' }}</span>
              </div>
            </div>
          </div>
        </div>

        <!-- ③ 右侧: 节点诊断 / 调试 -->
        <div class="diag-right">
          <el-tabs v-model="activeTab" class="diag-tabs">
            <el-tab-pane label="节点诊断" name="node">
              <div class="diag-tab-scroll">
                <el-empty v-if="!selectedNode" description="点击左侧流程图中的节点开始诊断" :image-size="70" />

                <template v-else>
                  <!-- 节点信息卡 -->
                  <div class="diag-node-card">
                    <div class="dnc-title">
                      <span class="dnc-name">{{ selectedNode.nodeName || typeLabel(selectedNode.nodeType) }}</span>
                      <el-tag size="small" v-if="nodeMark(selectedNode) === 'hit'" type="success">已执行</el-tag>
                      <el-tag size="small" v-else-if="nodeMark(selectedNode) === 'current'" type="primary">当前停留</el-tag>
                    </div>
                    <div class="dnc-meta">类型: {{ typeLabel(selectedNode.nodeType) }} · id: {{ selectedNode.id }}</div>
                  </div>

                  <!-- 为什么 有/没有 此节点 -->
                  <div class="diag-ask">
                    <div class="diag-ask-label">为什么</div>
                    <el-radio-group v-model="expectPresent" @change="doDiagnose">
                      <el-radio-button :value="true">有此节点</el-radio-button>
                      <el-radio-button :value="false">没有此节点</el-radio-button>
                    </el-radio-group>
                  </div>

                  <div v-if="diagnosing" class="diag-loading"><el-icon class="is-loading"><Loading /></el-icon> 正在诊断...</div>

                  <template v-if="diagnosis && !diagnosing">
                    <el-alert :type="conclusionAlertType" show-icon :closable="false"
                      :title="conclusionTitle" style="margin-bottom: 10px" />

                    <el-alert v-if="diagnosis.expectationMismatch" type="warning" show-icon :closable="false"
                      title="你的预期与实际情况相反, 以下为实际情况" style="margin-bottom: 10px" />

                    <!-- 加批归因 (节点存在时) -->
                    <template v-if="diagnosis.present">
                      <div class="diag-section" v-if="diagnosis.prevNodeHasAddApproval || (diagnosis.signupRecords && diagnosis.signupRecords.length)">
                        <div class="diag-sec-head">
                          <span>加批归因 (4.2)</span>
                          <span class="diag-sec-sub" v-if="diagnosis.prevNodeName">上一节点: {{ diagnosis.prevNodeName }}
                            {{ diagnosis.prevNodeHasAddApproval ? '· 配置了加批按钮' : '· 未配置加批按钮' }}</span>
                        </div>
                        <table class="diag-table" v-if="diagnosis.signupRecords && diagnosis.signupRecords.length">
                          <thead><tr><th>加批人</th><th style="width:150px">时间</th><th>说明</th><th style="width:110px">来源</th></tr></thead>
                          <tbody>
                            <tr v-for="(r, i) in diagnosis.signupRecords" :key="i">
                              <td>{{ r.userName || '-' }}</td>
                              <td>{{ r.verifyDate ? formatTime(r.verifyDate) : '-' }}</td>
                              <td>{{ r.verifyDesc || '-' }}</td>
                              <td>{{ r.source === 'verify_info' ? '审批记录' : '加批配置' }}</td>
                            </tr>
                          </tbody>
                        </table>
                        <div v-else class="diag-none">未找到加批审批记录 (节点也可能为模板原有节点)</div>
                      </div>
                    </template>

                    <!-- 条件分支横评 -->
                    <div class="diag-section" v-if="diagnosis.branches && diagnosis.branches.length">
                      <div class="diag-sec-head">
                        <span>条件分支横评</span>
                        <span class="diag-sec-sub">按当前表单值求值; 表单审批中被修改过则与当时判断可能不同</span>
                      </div>
                      <div v-for="(b, bi) in diagnosis.branches" :key="bi"
                        class="diag-branch" :class="{
                          'is-hit': b.hit === true,
                          'is-target': b.containsTarget,
                        }">
                        <div class="db-head">
                          <span>{{ b.branchName || ('分支' + (bi + 1)) }}</span>
                          <span class="db-tags">
                            <el-tag v-if="b.containsTarget" size="small" type="primary">目标节点所在</el-tag>
                            <el-tag v-if="b.isDefault" size="small" type="info">默认</el-tag>
                            <el-tag v-if="b.hit === true" size="small" type="success">命中</el-tag>
                            <el-tag v-else-if="b.hit === false" size="small" type="danger">未命中</el-tag>
                          </span>
                        </div>
                        <table class="diag-table" v-if="b.conditions && b.conditions.length">
                          <thead><tr><th>条件</th><th style="width:70px">比较</th><th>期望值</th><th>实际值</th><th style="width:56px">结果</th></tr></thead>
                          <tbody>
                            <tr v-for="(c, ci) in b.conditions" :key="ci">
                              <td>{{ c.label || c.fieldName }}</td>
                              <td>{{ c.opText }}</td>
                              <td>{{ c.expectText }}</td>
                              <td>{{ c.actualValue }}</td>
                              <td>{{ c.pass ? '✅' : '❌' }}</td>
                            </tr>
                          </tbody>
                        </table>
                        <div v-else class="diag-none">无条件 (默认分支)</div>
                      </div>
                    </div>

                    <!-- 人员维度: 审批人诊断 (4.3) -->
                    <div class="diag-section" v-if="diagnosis.present">
                      <div class="diag-sec-head">
                        <span>审批人诊断 (为什么有/没有此审批人)</span>
                        <span class="diag-sec-sub">配置规则: {{ diagnosis.ruleDesc || '未知' }}</span>
                      </div>

                      <!-- 应审人 vs 实际审批人 对照 -->
                      <table class="diag-table" style="margin-bottom:8px">
                        <thead><tr><th style="width:110px">对照</th><th>人员</th></tr></thead>
                        <tbody>
                          <tr>
                            <td>应审人(引擎评估)</td>
                            <td>
                              <template v-if="diagnosis.expectedApprovers && diagnosis.expectedApprovers.length">
                                <el-tag v-for="(a, ai) in diagnosis.expectedApprovers" :key="ai" size="small"
                                  style="margin-right:4px" :type="markTagType(a.mark)">{{ a.name }}</el-tag>
                              </template>
                              <span v-else class="diag-none">未评估(流程未走到该分支或评估失败)</span>
                            </td>
                          </tr>
                          <tr>
                            <td>实际审批人</td>
                            <td>
                              <template v-if="diagnosis.actualApprovers && diagnosis.actualApprovers.length">
                                <el-tag v-for="(a, ai) in diagnosis.actualApprovers" :key="ai" size="small"
                                  style="margin-right:4px" type="success">{{ a.name || a.userId }}</el-tag>
                              </template>
                              <span v-else class="diag-none">无</span>
                            </td>
                          </tr>
                        </tbody>
                      </table>

                      <!-- 单选一个人 + 预期 -->
                      <div class="person-ask">
                        <span class="person-ask-label">选择一个人, 问"为什么有/没有此审批人":</span>
                        <div class="person-cands">
                          <el-tag v-for="(a, ai) in personCandidates" :key="ai"
                            :class="{ 'cand-active': selectedPersonId === a.userId }"
                            closable @close="removePersonCand(a)"
                            @click="pickPerson(a)" style="cursor:pointer;margin-right:4px">
                            {{ a.name || a.userId }}
                          </el-tag>
                          <el-button size="small" plain icon="Plus" @click="openPersonDialog">选其他人</el-button>
                        </div>
                        <div v-if="selectedPersonId" class="person-opts">
                          <el-radio-group v-model="expectPersonPresent">
                            <el-radio-button :value="true">此审批人存在</el-radio-button>
                            <el-radio-button :value="false">此审批人不存在</el-radio-button>
                          </el-radio-group>
                          <el-button type="primary" size="small" style="margin-left:10px" @click="doPersonDiagnose">诊断审批人</el-button>
                        </div>
                        <div v-if="personDiagnosing" class="diag-loading"><el-icon class="is-loading"><Loading /></el-icon> 正在诊断审批人...</div>
                        <template v-if="personDiagnosis && !personDiagnosing">
                          <el-alert :type="personDiagnosis.presentPerson ? 'success' : 'info'" show-icon :closable="false"
                            :title="personDiagnosis.message" style="margin-top:8px" />
                          <el-alert v-if="personDiagnosis.expectationMismatch" type="warning" show-icon :closable="false"
                            title="你的预期与实际情况相反" style="margin-top:8px" />
                          <el-alert v-if="personDiagnosis.inference" type="info" show-icon :closable="false"
                            title="该结论含推断成分(动态评估差异), 非确证事实" style="margin-top:8px" />
                        </template>
                      </div>
                    </div>

                    <!-- 加减签/委托记录 (4.3) -->
                    <div class="diag-section">
                      <div class="diag-sec-head">
                        <span>人员加减签 / 委托记录 (4.3)</span>
                      </div>
                      <table class="diag-table" v-if="diagnosis.entrustRecords && diagnosis.entrustRecords.length">
                        <thead><tr><th style="width:110px">操作</th><th>原审批人</th><th>调整后</th></tr></thead>
                        <tbody>
                          <tr v-for="(r, i) in diagnosis.entrustRecords" :key="i">
                            <td><el-tag size="small" :type="entrustTagType(r.actionType)">{{ r.actionTypeName }}</el-tag></td>
                            <td>{{ r.originalName || '-' }}</td>
                            <td>{{ r.actualName || '-' }}</td>
                          </tr>
                        </tbody>
                      </table>
                      <div v-else class="diag-none">该节点无加减签/委托记录</div>
                    </div>

                    <!-- 兜底原始记录 -->
                    <div class="diag-section" v-if="!diagnosis.present && diagnosis.rawTasks && diagnosis.rawTasks.length">
                      <div class="diag-sec-head"><span>该节点相关 task 原始记录</span></div>
                      <table class="diag-table">
                        <thead><tr><th>任务</th><th>审批人</th><th style="width:150px">开始</th><th style="width:150px">结束</th><th style="width:50px">来源</th></tr></thead>
                        <tbody>
                          <tr v-for="(t, i) in diagnosis.rawTasks" :key="i">
                            <td>{{ t.taskName }}</td>
                            <td>{{ t.assigneeName || '-' }}</td>
                            <td>{{ t.startTime ? formatTime(t.startTime) : '-' }}</td>
                            <td>{{ t.endTime ? formatTime(t.endTime) : '-' }}</td>
                            <td>{{ t.source }}</td>
                          </tr>
                        </tbody>
                      </table>
                    </div>
                  </template>
                </template>
              </div>
            </el-tab-pane>

            <el-tab-pane label="调试" name="debug" :disabled="!initInfo">
              <div class="diag-tab-scroll" v-if="initInfo">
                <debugPanel :confId="initInfo.confId"
                  :initiator="{ userId: initInfo.initiatorUserId, userName: initInfo.initiatorUserName }"
                  :prefillValues="initInfo.formValues || {}"
                  :isLowCodeFlow="initInfo.isLowCodeFlow" />
              </div>
            </el-tab-pane>
          </el-tabs>
        </div>
      </template>
    </div>
    <select-user-dialog v-model:visible="personDialogVisible" :data="[]" @change="surePerson" />
  </el-dialog>
</template>

<script setup>
import { ref, computed, watch } from 'vue';
import { Loading } from '@element-plus/icons-vue';
import {
  getApiWorkFlowData,
  getBpmVerifyInfoVos,
  getDiagnosisInit,
  diagnoseNode,
} from '@/api/workflow/index';
import { FormatDisplayUtils } from '@/utils/antflow/formatdisplay_data';
import { nodeTypeList } from '@/utils/antflow/const';
import flowTree from '@/components/Workflow/FlowTree/flowTree.vue';
import debugPanel from '@/components/Workflow/FlowDebug/debugPanel.vue';
import selectUserDialog from '@/components/Workflow/dialog/selectUserDialog.vue';

const props = defineProps({
  visible: { type: Boolean, default: false },
  processNumber: { type: String, default: '' },
});
const emit = defineEmits(['update:visible']);

const showDlg = computed({
  get: () => props.visible,
  set: v => emit('update:visible', v),
});

const loading = ref(false);
const loaded = ref(false);
const initInfo = ref(null);
const tree = ref(null);
const pathList = ref([]);
const activeKey = ref('');
const activeTab = ref('node');

// 节点诊断状态
const selectedNode = ref(null);
const expectPresent = ref(null);
const diagnosing = ref(false);
const diagnosis = ref(null);

// 审批人诊断状态 (4.3)
const selectedPersonId = ref('');
const expectPersonPresent = ref(null);
const personDiagnosing = ref(false);
const personDiagnosis = ref(null);
const personDialogVisible = ref(false);

/** 选人候选: 应审人 ∪ 实际审批人 ∪ 加减签/委托涉及人 (按 userId 去重, 名称去标记后缀) */
const personCandidates = computed(() => {
  const map = new Map();
  const add = (id, name) => {
    if (!id) return;
    let n = (name || id).trim();
    if (n.endsWith('+') || n.endsWith('-') || n.endsWith('*')) n = n.slice(0, -1);
    if (!map.has(id)) map.set(id, { userId: id, name: n });
  };
  (diagnosis.value?.expectedApprovers || []).forEach(a => add(a.userId, a.name));
  (diagnosis.value?.actualApprovers || []).forEach(a => add(a.userId, a.name));
  (diagnosis.value?.entrustRecords || []).forEach(r => {
    add(r.originalId, r.originalName);
    add(r.actualId, r.actualName);
  });
  return [...map.values()];
});

const markTagType = (m) => (m === '+' ? 'success' : m === '-' ? 'danger' : m === '*' ? 'warning' : 'info');
const markLabel = (m) => (m === '+' ? '加签' : m === '-' ? '减签' : m === '*' ? '转办' : '');

const pickPerson = (a) => {
  selectedPersonId.value = a.userId;
  personDiagnosis.value = null;
};
const removePersonCand = (a) => {
  if (selectedPersonId.value === a.userId) { selectedPersonId.value = ''; personDiagnosis.value = null; }
};
const openPersonDialog = () => { personDialogVisible.value = true; };
const surePerson = (data) => {
  if (data && data.length) {
    selectedPersonId.value = data[0].targetId;
    personDiagnosis.value = null;
  }
  personDialogVisible.value = false;
};

async function doPersonDiagnose() {
  if (!selectedPersonId.value || expectPersonPresent.value == null || !selectedNode.value) return;
  personDiagnosing.value = true;
  personDiagnosis.value = null;
  try {
    const res = await diagnoseNode({
      processNumber: props.processNumber,
      nodeId: Number(selectedNode.value.id),
      expectedPresent: expectPresent.value,
      personId: selectedPersonId.value,
      expectedPersonPresent: expectPersonPresent.value,
    });
    diagnosis.value = res.data?.data || res.data;
    personDiagnosis.value = diagnosis.value?.personDiagnosis || null;
  } catch (e) {
    console.error('diagnoseNode(person) failed', e);
  } finally {
    personDiagnosing.value = false;
  }
}

const typeLabel = (t) => nodeTypeList?.[t] || `节点(${t})`;
const nodeMark = (n) => n.__mark || null;
const shortId = (id) => (id && id.length > 8 ? id.slice(0, 8) : id);
const formatTime = (d) => {
  const dt = new Date(d);
  const p = (x) => String(x).padStart(2, '0');
  return `${dt.getFullYear()}-${p(dt.getMonth() + 1)}-${p(dt.getDate())} ${p(dt.getHours())}:${p(dt.getMinutes())}`;
};

const statusTagType = (s) => {
  if (s === 99) return 'primary';
  if (s === 3 || s === 6 || s === 7) return 'danger';
  if (s === 1 || s === 2 || s === 9) return 'success';
  return 'info';
};
const entrustTagType = (t) => (t === 2 ? 'success' : t === 3 ? 'danger' : 'warning');

const conclusionAlertType = computed(() => {
  const map = {
    EXISTS: 'success',
    NOT_REACHED: 'info',
    CONDITION_MISS: 'warning',
    SIGN_SKIP: 'warning',
    ADD_APPROVAL: 'success',
    UNKNOWN: 'error',
  };
  return map[diagnosis.value?.conclusionType] || 'info';
});
const conclusionTitle = computed(() => {
  const typeMap = {
    EXISTS: '节点实际存在',
    NOT_REACHED: '尚未到达',
    CONDITION_MISS: '条件分支未命中',
    SIGN_SKIP: '疑似减签跳过',
    ADD_APPROVAL: '加批节点',
    UNKNOWN: '无法自动归因',
  };
  if (!diagnosis.value) return '';
  return `[${typeMap[diagnosis.value.conclusionType] || diagnosis.value.conclusionType}] ${diagnosis.value.message}`;
});

// ---------- 加载 ----------
watch(showDlg, async (v) => {
  if (!v || !props.processNumber) return;
  loading.value = true;
  loaded.value = false;
  diagnosis.value = null;
  selectedNode.value = null;
  expectPresent.value = null;
  selectedPersonId.value = '';
  expectPersonPresent.value = null;
  personDiagnosis.value = null;
  activeTab.value = 'node';
  try {
    // 1) init: confId + 发起人 + 表单值
    const initRes = await getDiagnosisInit(props.processNumber);
    initInfo.value = initRes.data?.data || initRes.data;
    // 2) 模板详情 → 设计树
    const detailRes = await getApiWorkFlowData({ id: initInfo.value.confId });
    const raw = detailRes.data?.data || detailRes.data;
    const clone = (x) => JSON.parse(JSON.stringify(x || {}));
    const treeData = FormatDisplayUtils.getToTree(clone(raw));
    tree.value = treeData?.nodeConfig || null;
    // 3) 审批路径(带 nodeId)
    const pathRes = await getBpmVerifyInfoVos({ processNumber: props.processNumber });
    const vos = (pathRes.data?.data || pathRes.data) || [];
    buildPathList(vos);
    markTree();
    loaded.value = true;
  } catch (e) {
    console.error('diagnosis init failed', e);
  } finally {
    loading.value = false;
  }
});

/** 审批路径条目: 发起/流程结束为 virtual; 有 id 但无 nodeId → 标灰 + console.error */
function buildPathList(vos) {
  pathList.value = vos.map(v => ({
    ...v,
    virtual: !v.id && (v.taskName === '发起' || v.taskName === '流程结束'),
    noNode: false,
  }));
  pathList.value.forEach(it => {
    if (!it.virtual && it.id && !it.nodeId) {
      it.noNode = true;
      console.error(`[流程诊断] 审批记录(id=${it.id}, task=${it.taskName})未查到对应的 node_id`);
    }
  });
}

/** 图节点标 hit/current + __pairKey=node.id 供定位 */
function markTree() {
  const hitIds = new Set(pathList.value.filter(it => it.nodeId).map(it => String(it.nodeId)));
  const currentEntry = pathList.value.find(it => it.verifyStatus === 99 && it.nodeId);
  const currentId = currentEntry ? String(currentEntry.nodeId) : null;
  const walk = (node) => {
    if (!node) return;
    node.__pairKey = node.id != null ? String(node.id) : (node.__pairKey || '');
    if (node.id != null && hitIds.has(String(node.id))) {
      node.__mark = String(node.id) === currentId ? 'current' : 'hit';
    }
    (node.conditionNodes || []).forEach(c => { walk(c); walk(c.childNode); });
    (node.parallelNodes || []).forEach(p => { walk(p); walk(p.childNode); });
    walk(node.childNode);
  };
  walk(tree.value);
}

// ---------- 交互 ----------
function onNodeSelect(node) {
  selectedNode.value = node;
  diagnosis.value = null;
  expectPresent.value = null;
  selectedPersonId.value = '';
  expectPersonPresent.value = null;
  personDiagnosis.value = null;
  activeTab.value = 'node';
}

function onPathClick(it) {
  if (!it.nodeId) return;
  activeKey.value = String(it.nodeId);
}

async function doDiagnose() {
  if (!selectedNode.value || expectPresent.value == null) return;
  diagnosing.value = true;
  diagnosis.value = null;
  try {
    const res = await diagnoseNode({
      processNumber: props.processNumber,
      nodeId: Number(selectedNode.value.id),
      expectedPresent: expectPresent.value,
    });
    diagnosis.value = res.data?.data || res.data;
  } catch (e) {
    console.error('diagnoseNode failed', e);
  } finally {
    diagnosing.value = false;
  }
}
</script>

<style lang="scss" scoped>
.diag-dlg-title {
  display: flex;
  align-items: center;
  gap: 10px;
  font-size: 16px;
  font-weight: 600;
  color: #303133;
}

.diag-body {
  display: flex;
  gap: 10px;
  height: 76vh;
}

/* ① 流程图 */
.diag-graph {
  flex: 1.15;
  min-width: 0;
  display: flex;

  .lg {
    font-size: 11px;
    margin-left: 8px;
    padding: 0 5px;
    border-radius: 3px;

    &.lg-hit { color: #3fa84f; border: 1px solid #3fa84f; }
    &.lg-cur { color: #3a6fd8; border: 1px solid #3a6fd8; }
  }
}

/* ② 审批路径 */
.diag-path {
  width: 300px;
  flex-shrink: 0;
  display: flex;
  flex-direction: column;
  border: 1px solid #e3e7ee;
  border-radius: 6px;
  background: #fafbfd;
}

.diag-col-head {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 6px 12px;
  border-bottom: 1px solid #e3e7ee;
  background: #f3f5f9;
  font-weight: 600;
  font-size: 13px;
  color: #333c4d;

  .diag-col-sub { font-weight: 400; font-size: 11px; color: #7c8698; }
}

.diag-path-scroll {
  flex: 1;
  overflow: auto;
  padding: 10px;
}

.diag-path-item {
  border: 1px solid #e3e7ee;
  border-radius: 6px;
  background: #fff;
  padding: 7px 10px;
  margin-bottom: 8px;
  cursor: pointer;
  transition: box-shadow 0.15s;

  &:hover { box-shadow: 0 2px 8px rgba(58, 111, 216, 0.25); }

  &.is-current { border-color: #3a6fd8; background: #f0f6ff; }
  &.no-node { opacity: 0.55; border-style: dashed; cursor: default; }
  &.is-virtual { background: #f7f8fa; cursor: default; }

  .dpi-head {
    display: flex;
    justify-content: space-between;
    align-items: center;

    .dpi-name { font-size: 13px; font-weight: 600; color: #333c4d; }
  }

  .dpi-user { font-size: 12px; color: #5a6474; margin-top: 2px; word-break: break-all; }

  .dpi-meta {
    display: flex;
    justify-content: space-between;
    font-size: 11px;
    color: #98a2b3;
    margin-top: 3px;

    .dpi-node { color: #3a6fd8; }
    .dpi-node-none { color: #d35454; }
  }
}

/* ③ 右侧 */
.diag-right {
  flex: 1.25;
  min-width: 0;
  display: flex;
  flex-direction: column;
  border: 1px solid #e3e7ee;
  border-radius: 6px;
  background: #fff;
  padding: 0 10px;
}

.diag-tabs {
  flex: 1;
  display: flex;
  flex-direction: column;
  min-height: 0;

  :deep(.el-tabs__content) {
    flex: 1;
    min-height: 0;
  }
}

.diag-tab-scroll {
  height: 100%;
  overflow: auto;
  padding-right: 4px;
}

.diag-node-card {
  border: 1px solid #e3e7ee;
  border-radius: 6px;
  padding: 8px 12px;
  background: #f7f9fc;
  margin-bottom: 10px;

  .dnc-title {
    display: flex;
    align-items: center;
    gap: 8px;
    font-weight: 600;
    font-size: 14px;
    color: #333c4d;
  }

  .dnc-meta { font-size: 12px; color: #7c8698; margin-top: 3px; }
}

.diag-ask {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 12px;

  .diag-ask-label { font-size: 13px; font-weight: 600; color: #333c4d; }
}

.diag-loading {
  color: #7c8698;
  font-size: 13px;
  padding: 8px 0;
  display: flex;
  align-items: center;
  gap: 6px;
}

.diag-section {
  margin-bottom: 12px;

  .diag-sec-head {
    display: flex;
    justify-content: space-between;
    align-items: center;
    font-weight: 600;
    font-size: 13px;
    color: #333c4d;
    margin-bottom: 6px;

    .diag-sec-sub { font-weight: 400; font-size: 11px; color: #98a2b3; }
  }
}

.diag-none { font-size: 12px; color: #98a2b3; padding: 4px 0; }

.diag-table {
  width: 100%;
  border-collapse: collapse;
  font-size: 12px;

  th, td {
    border: 1px solid #e8ecf2;
    padding: 4px 8px;
    text-align: left;
    color: #4a5568;
    word-break: break-all;
  }

  th { background: #f5f7fa; color: #697488; font-weight: 600; }
}

.diag-branch {
  border: 1px solid #e3e7ee;
  border-radius: 6px;
  padding: 6px 8px;
  margin-bottom: 8px;

  &.is-hit { border-color: #3fa84f; background: #f8fdf9; }
  &.is-target { box-shadow: 0 0 0 2px rgba(58, 111, 216, 0.25); }

  .db-head {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 5px;
    font-size: 13px;
    font-weight: 600;
    color: #333c4d;

    .db-tags { display: flex; gap: 4px; }
  }
}

.person-ask {
  .person-ask-label { font-size: 12px; color: #5a6474; display: block; margin-bottom: 6px; }

  .person-cands {
    display: flex;
    flex-wrap: wrap;
    gap: 4px;
    align-items: center;
    margin-bottom: 8px;

    .cand-active { border-color: #3a6fd8; background: #f0f6ff; }
  }

  .person-opts {
    display: flex;
    align-items: center;
    margin-top: 4px;
  }
}
</style>
