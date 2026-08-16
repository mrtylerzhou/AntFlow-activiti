/**
 * 版本比较 diff 引擎
 *
 * 设计文档: .scratch/版本比较功能设计.md
 * 数据源: GET /bpmnConf/detail/{id} → BpmnConfVo, 前端转树(FormatDisplayUtils.getToTree)
 *
 * 节点对齐策略(grill-me 定稿):
 *   1. nodeId 相同 → 同一节点(主匹配; node_id 列在拷贝链路下跨版本稳定)
 *   2. 位置锚定兜底: nodeId 配不上, 但"前驱/父已配对" 且名称相同+类型相同 → 仍算同一节点
 *   3. 仍未配对 → 新增/删除
 */
import { nodeTypeList, signTypeObj, setTypes } from '@/utils/antflow/const';
import { FormatDisplayUtils } from '@/utils/antflow/formatdisplay_data';

// ============ 通用工具 ============

/** 深比较(忽略键顺序的对象稳定序列化) */
function stableKey(v) {
  if (v === null || v === undefined) return '∅';
  if (Array.isArray(v)) return '[' + v.map(stableKey).join(',') + ']';
  if (typeof v === 'object') {
    return '{' + Object.keys(v).sort().filter(k => !VOLATILE_KEYS.has(k)).map(k => k + ':' + stableKey(v[k])).join(',') + '}';
  }
  return JSON.stringify(v);
}
export function deepEqual(a, b) { return stableKey(a) === stableKey(b); }

/** 参与比较时忽略的易变/派生键(任意层级) */
const VOLATILE_KEYS = new Set([
  'id', 'confId', 'createTime', 'createUser', 'updateTime', 'updateUser', 'isDel',
  'elementId', 'error', 'params', 'conditionsUrl', 'formCode', 'isOutSideProcess',
  'isLowCodeFlow', 'orderedNodeType', 'fromNodes', 'aggregationNode',
  'nodeFrom', 'nodeFroms', 'nodeTo', 'childNode', 'conditionNodes', 'parallelNodes',
  'nodeId', 'type', 'conditionList', // conditionList 提到顶层单独按 section 比对
  '__diff', '__pairKey', '__active', // 比较引擎自身注入的标记字段
]);

// ============ 值格式化 ============

/** 审批人类型字典: 与设计器 const.js setTypes 一致(数字含义全量映射) */
const NODE_PROPERTY_DICT = Object.fromEntries(setTypes.map(x => [x.value, x.label]));

const ENUM_DICTS = {
  'nodeName': null,
  'deduplicationType': { 1: '不去重', 2: '前序去重', 3: '后序去重', 4: '相邻去重' },
  'signType': signTypeObj,
  'forwardType': { 0: '不允许推进', 1: '允许推进', 2: '推进至固定节点' },
  'drawBackType': { 0: '不允许撤回', 1: '允许撤回', 2: '撤回至指定节点', 3: '上一步', 4: '撤回至发起人', 5: '指定节点' },
  'disagreeBackType': { 1: '退回发起人', 2: '退回上一步', 3: '流程结束', 4: '退回指定节点', 5: '退回发起人重提' },
  'afterSignUpWay': { 1: '回到审批人', 2: '不回到审批人' },
  'nodeProperty': NODE_PROPERTY_DICT,
  'setType': NODE_PROPERTY_DICT, // 旧字段名, 与 nodeProperty 同语义(formatdisplay 顶层镜像)
  'batchStatus': { 0: '禁止', 1: '允许' },
  'approvalStandard': { 1: '发起人', 2: '审批人' },
  'satisfiedAction': { 0: '无动作', 1: '推进至固定节点', 2: '加批', 3: '转办', 4: '抄送' },
  'unsatisfiedAction': { 0: '无动作', 1: '推进', 2: '退回' },
};

/** 值 → 人类可读文本 */
export function fmtValue(v, keyPath) {
  const leafKey = keyPath ? keyPath.split('.').pop() : '';
  if (v === null || v === undefined || v === '') return '(空)';
  if (typeof v === 'boolean') return v ? '开' : '关';
  if (typeof v === 'number' || typeof v === 'string') {
    const dict = ENUM_DICTS[leafKey];
    if (dict && dict[v] !== undefined) return `${dict[v]}(${v})`;
    return String(v);
  }
  if (Array.isArray(v)) {
    if (v.length === 0) return '(空)';
    // 人员/角色列表 → 名称集合
    if (v.every(x => x && typeof x === 'object')) {
      const names = v.map(x => x.name ?? x.label ?? x.nodeName ?? stableKey(x));
      return names.join('、');
    }
    return v.map(x => fmtValue(x, keyPath)).join('、');
  }
  if (typeof v === 'object') {
    const names = (v.name !== undefined) ? [v.name] : Object.keys(v).filter(k => !VOLATILE_KEYS.has(k)).slice(0, 6).map(k => `${k}=${fmtValue(v[k], keyPath + '.' + k)}`);
    if (names.length) return names.join(' ');
    return JSON.stringify(v);
  }
  return String(v);
}

// ============ 节点树遍历与对齐 ============

/**
 * DFS 展开节点树为有序列表
 * 每项: { node, container: 'main'|'cond'|'parallel', parentIdx, idx, prevIdx }
 *   container: 该节点所处的兄弟序列类型; parentIdx: 父锚(网关节点)在列表中的序号
 *   prevIdx: 同序列中前一个兄弟的序号(无则 -1, 锚退化为 parentIdx)
 */
export function flattenTree(root) {
  const out = [];
  const push = (node, container, parentIdx, prevIdx) => {
    const idx = out.length;
    out.push({ node, container, parentIdx, idx, prevIdx, pairKey: null, side: null });
    return idx;
  };
  /** 处理一个【已入栈】节点的子孙(只入栈一次, 避免重复) */
  function process(node, selfIdx) {
    let seqPrev;
    seqPrev = -1;
    for (const c of (Array.isArray(node.conditionNodes) ? node.conditionNodes : [])) {
      const ci = push(c, 'cond', selfIdx, seqPrev);
      seqPrev = ci;
      process(c, ci);
    }
    seqPrev = -1;
    for (const p of (Array.isArray(node.parallelNodes) ? node.parallelNodes : [])) {
      const pi = push(p, 'parallel', selfIdx, seqPrev);
      seqPrev = pi;
      process(p, pi);
    }
    if (node.childNode) {
      // 主干链: 前驱 = 指向它的上一个节点
      const ci = push(node.childNode, 'main', selfIdx, selfIdx);
      process(node.childNode, ci);
    }
  }
  if (root && typeof root === 'object') {
    push(root, 'main', -1, -1);
    process(root, 0);
  }
  return out;
}

/**
 * 两棵树节点对齐
 * @returns { items, pairs } items 带 pairKey/状态, pairs: Map<pairKey, {source, target, status}>
 */
export function alignTrees(sourceRoot, targetRoot) {
  const sList = flattenTree(sourceRoot);
  const tList = flattenTree(targetRoot);
  sList.forEach((it, i) => { it.side = 's'; it.pairKey = 'n' + i; });
  tList.forEach((it, i) => { it.side = 't'; it.pairKey = 'n' + i; });

  const pairs = new Map();
  const sById = new Map(sList.filter(it => it.node.nodeId).map(it => [String(it.node.nodeId), it]));
  const paired = new Set(); // source 已配对 idx

  // pass1: nodeId 主匹配
  for (const t of tList) {
    const nid = t.node.nodeId ? String(t.node.nodeId) : null;
    if (!nid) continue;
    const s = sById.get(nid);
    if (s && !paired.has(s.idx)) {
      paired.add(s.idx);
      t.__pairWith = s.idx;
    }
  }

  // pass2: 位置锚定兜底(前驱/父已配对 + 容器相同 + 类型相同 + 名称相同)
  // __pairWith 仅记录在 target 项上(值为 source idx); 源侧用 paired.has(idx) 判断
  const anchorOk = (s, t) => {
    if (s.node.nodeType !== t.node.nodeType) return false;
    if ((s.node.nodeName || '') !== (t.node.nodeName || '')) return false;
    if (s.container !== t.container) return false;
    if (s.prevIdx >= 0 || t.prevIdx >= 0) {
      // 前驱锚: 双方都有前驱兄弟, 且两个前驱互为配对
      if (s.prevIdx < 0 || t.prevIdx < 0) return false;
      const sPrev = sList[s.prevIdx], tPrev = tList[t.prevIdx];
      if (!paired.has(sPrev.idx)) return false;             // 源前驱必须已配对
      if (tPrev.__pairWith === undefined) return false;     // 目标前驱必须已配对
      return tPrev.__pairWith === sPrev.idx;
    }
    // 首子节点: 父锚(父必须互为配对)
    if (s.parentIdx < 0 || t.parentIdx < 0) return s.parentIdx === t.parentIdx; // 根
    const tParent = tList[t.parentIdx];
    if (!tParent || tParent.__pairWith === undefined) return false;
    return tParent.__pairWith === s.parentIdx;
  };
  for (const t of tList) {
    if (t.__pairWith !== undefined) continue;
    for (const s of sList) {
      if (paired.has(s.idx)) continue;
      if (anchorOk(s, t)) {
        paired.add(s.idx);
        t.__pairWith = s.idx;
        break;
      }
    }
  }

  // 建立配对结果
  const keyOf = (it) => it.side + it.pairKey;
  for (const t of tList) {
    if (t.__pairWith !== undefined) {
      const s = sList[t.__pairWith];
      const key = 'pair_' + s.idx;
      s.pairKey = key; t.pairKey = key;
      pairs.set(key, { source: s, target: t, status: 'modified' /* 待 diff 后定 */ });
    }
  }
  for (const s of sList) {
    if (!pairs.has('pair_' + s.idx)) {
      const key = 'rem_' + s.idx;
      s.pairKey = key;
      pairs.set(key, { source: s, target: null, status: 'removed' });
    }
  }
  for (const t of tList) {
    if (t.pairKey.startsWith('n') || t.pairKey === null) { /* 未配对目标 */ }
    if (!t.pairKey || t.pairKey.startsWith('n')) {
      // 重新赋 key
      const key = 'add_' + t.idx;
      t.pairKey = key;
      pairs.set(key, { source: null, target: t, status: 'added' });
    }
  }
  return { sList, tList, pairs };
}

// ============ 节点属性 diff(按 drawer 逻辑 section) ============

/** 10 个逻辑 section(对应 approverDrawer 的 10 个 tab, 单页并列展示) */
export const NODE_SECTIONS = [
  '基本信息', '审批人设置', '推进设置', '退回设置', '条件设置',
  '加批设置', '转办设置', '按钮权限', '表单权限', '通知设置', '高级设置',
];

const SECTION_RULES = [
  [/^(nodeName|nodeDisplayName|nodeType|labelList)$/, '基本信息'],
  [/^property\./, '审批人设置'],
  [/^(setType|nodeApproveList|signType|directorLevel|nodeProperty|formAssigneeProperty|formInfos|approvalStandard)$/, '审批人设置'],
  [/^(forwardType|forwardNodeIds)$/, '推进设置'],
  [/property\.forward/, '推进设置'],
  [/^(drawBackType|drawBackNodeIds|disagreeBackType|disagreeBackToNodeId|backType|backToNodeId)$/, '退回设置'],
  [/^(conditionList|groupRelation|priorityLevel|isDefault)$/, '条件设置'],
  [/autoNodeConf\.(conditionList|groupRelation)/, '条件设置'],
  [/autoNodeConf\.(satisfiedAction|forwardNodeIds)/, '推进设置'],
  [/autoNodeConf\.(unsatisfiedAction|backToNodeId)/, '退回设置'],
  [/autoNodeConf\.autoSignUpConf|^autoSignUpConf|^isSignUp|property\.afterSignUpWay|property\.sign/, '加批设置'],
  [/autoNodeConf\.(transferToUser|autoTransferConf)|^autoTransferConf/, '转办设置'],
  [/^buttons/, '按钮权限'],
  [/^lfFieldControlVOs|^formHidden/, '表单权限'],
  [/^(templateVos|approveRemindVo|noticeChannelTypes)/, '通知设置'],
  [/./, '高级设置'], // 兜底
];

function sectionOf(path) {
  for (const [re, sec] of SECTION_RULES) if (re.test(path)) return sec;
  return '高级设置';
}

const FIELD_LABELS = {
  nodeName: '节点名称', nodeDisplayName: '节点描述', nodeType: '节点类型', labelList: '节点标签',
  'property.nodeProperty': '审批人类型', 'property.signType': '多人审批方式', 'property.arbitrationRatio': '仲裁签通过比例',
  'property.assignLevelGrade': '主管层级', 'property.hrbpConfType': 'HRBP 类型',
  'property.emplList': '指定成员', 'property.roleList': '指定角色',
  'property.loopEndType': '层层审批终止方式', 'property.loopNumberPlies': '层层审批层数', 'property.loopEndGrade': '终止层级',
  'property.loopEndPersonList': '终止人员', 'property.noparticipatingStaffs': '不参与审批人员',
  'property.afterSignUpWay': '加批后处理方式', 'property.noticeConfig': '通知配置',
  setType: '审批人类型', nodeApproveList: '审批人列表', signType: '多人审批方式', directorLevel: '主管层级',
  formAssigneeProperty: '表单选人配置', formInfos: '关联表单字段',
  forwardType: '推进方式', forwardNodeIds: '推进目标节点',
  drawBackType: '撤回/退回方式', drawBackNodeIds: '退回目标节点',
  disagreeBackType: '不同意退回方式', disagreeBackToNodeId: '不同意退回目标',
  conditionList: '条件表达式', groupRelation: '条件组合关系(且/或)', priorityLevel: '优先级', isDefault: '默认分支',
  isSignUp: '允许加批', autoSignUpConf: '加批配置', autoTransferConf: '转办配置',
  buttons: '按钮权限', lfFieldControlVOs: '表单字段权限', formHidden: '表单字段隐藏',
  templateVos: '通知模板', approveRemindVo: '审批提醒',
  remark: '备注', annotation: '批注', batchStatus: '批量审批', approvalStandard: '审批基准',
  noHeaderAction: '隐藏头部操作', isDeduplication: '节点去重', extraFlags: '节点扩展标志',
  deduplicationExclude: '抗去重（该节点不参与审批人去重）',
  __position: '节点位置',
};

/**
 * 顶层镜像字段 → 标准 label(formatdisplay 把 property.* 镜像到节点顶层,
 * 与 property.* 双报同一变更; 归一到相同 label 后, rows.add 的
 * "label+源值+目标值" 去重自动只留一行)
 */
const MIRROR_LABELS = {
  nodeProperty: '审批人类型', setType: '审批人类型', signType: '多人审批方式',
  directorLevel: '主管层级', nodeApproveList: '审批人列表',
  formAssigneeProperty: '表单选人配置', formInfos: '关联表单字段',
};

function labelOf(path) {
  if (MIRROR_LABELS[path]) return MIRROR_LABELS[path];
  if (FIELD_LABELS[path]) return FIELD_LABELS[path];
  const leaf = path.split('.').pop();
  if (FIELD_LABELS[leaf]) return FIELD_LABELS[leaf];
  return path;
}

/** 递归 diff 一个节点的属性, 产出按 section 分组的差异行 */
export function diffNodePair(pair, sList, tList, formNameCtx) {
  const sections = new Map(NODE_SECTIONS.map(s => [s, []]));
  const rows = {
    add(sec, path, a, b, kind) {
      const label = labelOf(path), source = a, target = b;
      // 去重: 顶层冗余字段(如 signType)与 property.* 同值双报
      const list = sections.get(sec);
      if (list.some(x => x.label === label && x.source === source && x.target === target)) return;
      list.push({ path, label, source, target, kind: kind || 'modify' });
    },
  };

  const s = pair.source?.node, t = pair.target?.node;

  if (pair.status === 'added') {
    rows.add('基本信息', 'nodeName', '(不存在)', nodeTitle(t), 'add');
    return sections;
  }
  if (pair.status === 'removed') {
    rows.add('基本信息', 'nodeName', nodeTitle(s), '(不存在)', 'remove');
    return sections;
  }

  // 基本信息
  if ((s.nodeName || '') !== (t.nodeName || '')) rows.add('基本信息', 'nodeName', s.nodeName, t.nodeName);
  if ((s.nodeDisplayName || '') !== (t.nodeDisplayName || '')) rows.add('基本信息', 'nodeDisplayName', s.nodeDisplayName, t.nodeDisplayName);
  if (s.nodeType !== t.nodeType) rows.add('基本信息', 'nodeType', nodeTypeName(s.nodeType), nodeTypeName(t.nodeType));
  if (!deepEqual(s.labelList, t.labelList)) rows.add('基本信息', 'labelList', fmtValue(s.labelList, 'labelList'), fmtValue(t.labelList, 'labelList'));
  // conditionList(条件表达式)在 VOLATILE_KEYS 中被通用 diff 跳过, 手工比对到"条件设置"
  if (!deepEqual(s.conditionList, t.conditionList)) {
    rows.add('条件设置', 'conditionList', fmtCondition(s.conditionList), fmtCondition(t.conditionList));
  }

  // 位置变更: 结构签名 = 沿链回溯到最近已配对锚点(前驱或父), 免疫"前驱改名/中途插入未配对节点"
  const sAnchor = anchorSignature(pair.source, sList), tAnchor = anchorSignature(pair.target, tList);
  if (sAnchor.key !== tAnchor.key) rows.add('基本信息', '__position', sAnchor.desc, tAnchor.desc, 'position');

  // 属性字段 diff(递归); 显式处理的字段跳过避免重复
  const explicitHandled = new Set(['nodeName', 'nodeDisplayName', 'nodeType', 'labelList', 'lfFieldControlVOs', 'formHidden']);
  const keys = new Set([...Object.keys(s || {}), ...Object.keys(t || {})]);
  for (const k of keys) {
    if (VOLATILE_KEYS.has(k) || explicitHandled.has(k)) continue;
    const a = s ? s[k] : undefined, b = t ? t[k] : undefined;
    if (deepEqual(a, b)) continue;
    diffValueToRows(a, b, k, rows);
  }

  // 表单字段权限: 结构化表格(不走通用 JSON diff)
  const permTable = diffFormPerms(s, t, formNameCtx);
  if (permTable) {
    rows.add('表单权限', 'lfFieldControlVOs', null, null, 'formPermTable');
    const list = sections.get('表单权限');
    const r = list[list.length - 1];
    r.groups = permTable.groups;
    r.hiddenDiff = permTable.hiddenDiff;
    r.formPermCount = permTable.groups.reduce((n, g) => n + g.fields.filter(f => f.status !== 'same').length, 0) + (permTable.hiddenDiff ? 1 : 0);
  }

  return sections;
}

const CONTAINER_NAMES = { main: '主干', cond: '条件分支', parallel: '并行分支' };

/**
 * 结构锚签名: 沿前驱链(或父链)回溯, 找到最近的**已配对**节点作为锚
 * - 中途插入的新增/删除节点会被跳过(不算位置变化)
 * - key 用于比较, desc 用于展示
 */
function anchorSignature(item, list) {
  let cur = item;
  let guard = 0;
  while (cur && guard++ < 1000) {
    const cont = CONTAINER_NAMES[cur.container] || cur.container;
    if (cur.prevIdx >= 0) {
      const p = list[cur.prevIdx];
      if (p.pairKey && p.pairKey.startsWith('pair_')) {
        return { key: `prev:${p.pairKey}`, desc: `${cont} · 「${p.node.nodeName || ''}」之后` };
      }
      cur = p; continue;
    }
    if (cur.parentIdx >= 0) {
      const par = list[cur.parentIdx];
      if (par.pairKey && par.pairKey.startsWith('pair_')) {
        return { key: `child-of:${par.pairKey}`, desc: `${cont} · 「${par.node.nodeName || ''}」的首个子节点` };
      }
      cur = par; continue;
    }
    return { key: 'root', desc: '流程起点' };
  }
  return { key: 'unknown', desc: '(未知位置)' };
}

/** 条件表达式 → 可读文本: 外层"或", 内层"且" */
function fmtCondition(cond) {
  if (!Array.isArray(cond) || cond.length === 0) return '(无条件)';
  return cond.map(group => {
    const items = Array.isArray(group) ? group : [group];
    if (items.length === 0) return '(无条件)';
    return '(' + items.map(c => {
      if (c === null || typeof c !== 'object') return String(c);
      const name = c.showName || c.fieldName || c.label || c.keyName || '字段';
      const op = c.operator ?? c.opt ?? '';
      const val = c.fixedValue ?? c.value ?? c.val ?? '';
      return [name, op, val].filter(x => x !== '' && x !== undefined && x !== null).join(' ');
    }).join(' 且 ') + ')';
  }).join(' 或 ');
}

/** 递归把值差异展开为行(深挖对象/数组, 原始值直接一行) */
function diffValueToRows(a, b, path, rows) {
  const sec = sectionOf(path);
  const bothObj = isPlainObj(a) && isPlainObj(b);
  const bothArr = Array.isArray(a) && Array.isArray(b);
  if (bothArr) {
    // 数组: 对象数组按稳定序列化对比(避免逐 index 误报), 整体一行
    if (a.length !== b.length || !deepEqual(a, b)) {
      rows.add(sec, path, fmtValue(a, path), fmtValue(b, path));
    }
    return;
  }
  if (bothObj) {
    const keys = new Set([...Object.keys(a), ...Object.keys(b)]);
    let any = false;
    for (const k of keys) {
      if (VOLATILE_KEYS.has(k)) continue;
      if (!deepEqual(a[k], b[k])) { diffValueToRows(a[k], b[k], path + '.' + k, rows); any = true; }
    }
    if (!any) return;
    return;
  }
  rows.add(sec, path, fmtValue(a, path), fmtValue(b, path));
}
function isPlainObj(v) { return v !== null && typeof v === 'object' && !Array.isArray(v); }

/**
 * 表单字段权限 diff → 结构化表格数据
 * - lfFieldControlVOs: 内联模式 [{fieldId, fieldName, perm}]; 外部多表单模式 [{formdataId, fieldId, fieldName, perm}]
 * - formHidden: 外部多表单整表隐藏 { formdataId: bool }
 * - 字段对齐键 fieldId, 全量对照(含未变); perm 汉字化由渲染层做(R/E/H → 只读/可编辑/隐藏)
 * @returns null(两版均无权限配置) | { groups: [{name, hidden|null, fields:[{fieldId, fieldName, sourcePerm, targetPerm, status}]}], hiddenDiff }
 */
function diffFormPerms(sNode, tNode, formNameCtx) {
  const sPerms = Array.isArray(sNode?.lfFieldControlVOs) ? sNode.lfFieldControlVOs : [];
  const tPerms = Array.isArray(tNode?.lfFieldControlVOs) ? tNode.lfFieldControlVOs : [];
  const sHidden = (sNode?.formHidden && typeof sNode.formHidden === 'object') ? sNode.formHidden : {};
  const tHidden = (tNode?.formHidden && typeof tNode.formHidden === 'object') ? tNode.formHidden : {};
  if (!sPerms.length && !tPerms.length && !Object.keys(sHidden).length && !Object.keys(tHidden).length) return null;

  const INLINE = '__inline__';
  const groupKey = (p) => (p.formdataId !== undefined && p.formdataId !== null) ? String(p.formdataId) : INLINE;
  const groupName = (key, idx) => key === INLINE ? '主表单' : (formNameCtx?.formNames?.[key] || `表单${idx + 1}`);

  const sGroups = new Map(), tGroups = new Map();
  for (const p of sPerms) { const k = groupKey(p); if (!sGroups.has(k)) sGroups.set(k, []); sGroups.get(k).push(p); }
  for (const p of tPerms) { const k = groupKey(p); if (!tGroups.has(k)) tGroups.set(k, []); tGroups.get(k).push(p); }

  const allKeys = [...new Set([...sGroups.keys(), ...tGroups.keys()])];
  const groups = [];
  let hiddenDiff = false;
  allKeys.forEach((key, idx) => {
    const sList = sGroups.get(key) || [], tList = tGroups.get(key) || [];
    const sMap = new Map(sList.map(p => [String(p.fieldId), p]));
    const tMap = new Map(tList.map(p => [String(p.fieldId), p]));
    const fieldIds = [...new Set([...sMap.keys(), ...tMap.keys()])];
    const fields = fieldIds.map(fid => {
      const sw = sMap.get(fid), tw = tMap.get(fid);
      if (sw && !tw) return { fieldId: fid, fieldName: sw.fieldName || fid, sourcePerm: sw.perm, targetPerm: null, status: 'removed' };
      if (!sw && tw) return { fieldId: fid, fieldName: tw.fieldName || fid, sourcePerm: null, targetPerm: tw.perm, status: 'added' };
      return {
        fieldId: fid, fieldName: tw.fieldName || sw.fieldName || fid,
        sourcePerm: sw.perm, targetPerm: tw.perm,
        status: sw.perm === tw.perm ? 'same' : 'changed',
      };
    });
    // 整表隐藏: 仅外部多表单有(formdataId → formHidden)
    const hidKey = key === INLINE ? null : key;
    if (hidKey !== null) {
      const sH = !!sHidden[hidKey], tH = !!tHidden[hidKey];
      const changed = sH !== tH;
      if (changed) hiddenDiff = true;
      groups.push({ name: groupName(key, idx), hidden: { source: sH, target: tH, changed }, fields });
    } else {
      groups.push({ name: groupName(key, idx), hidden: null, fields });
    }
  });
  return { groups, hiddenDiff };
}

/** formdataId → formdataName 映射(多表单分组名, 与表单差异 tab 对齐) */
function buildFormNameMap(...confs) {
  const m = {};
  for (const c of confs) {
    if (!c || !Array.isArray(c.lfFormdataList)) continue;
    for (const f of c.lfFormdataList) {
      if (f.formdataId === undefined || f.formdataId === null) continue;
      const k = String(f.formdataId);
      m[k] = f.formdataName || f.name || m[k];
    }
  }
  return m;
}

export function nodeTypeName(t) { return nodeTypeList?.[t] || `类型${t}`; }
export function nodeTitle(n) {
  if (!n) return '(不存在)';
  return `${nodeTypeName(n.nodeType)}「${n.nodeName || ''}」`;
}

// ============ 表单 diff ============

/** 递归展平 VForm widgetList */
function flattenWidgets(widgetList, out = []) {
  if (!Array.isArray(widgetList)) return out;
  for (const w of widgetList) {
    if (!w) continue;
    out.push(w);
    if (Array.isArray(w.widgets)) flattenWidgets(w.widgets, out); // 容器子控件(旧版)
    if (w.options && Array.isArray(w.options.widgetList)) flattenWidgets(w.options.widgetList, out); // grid/cell
    if (w.cols) for (const c of Object.values(w.cols)) flattenWidgets(c.widgetList || [], out); // 旧栅格
  }
  return out;
}

const WIDGET_TYPE_NAMES = {
  input: '输入框', textarea: '多行文本', number: '数字', radio: '单选', checkbox: '复选',
  select: '下拉', date: '日期', datetime: '日期时间', time: '时间', switch: '开关',
  slider: '滑块', rate: '评分', upload: '上传', richeditor: '富文本', cascader: '级联',
};

/**
 * 表单 diff: 以 widget options.name(唯一名称)为键, 只比 label/type/required + 增删
 * @param sourceConf/targetConf: BpmnConfVo(原始 detail 返回)
 */
export function diffForms(sourceConf, targetConf) {
  const sForms = extractForms(sourceConf);
  const tForms = extractForms(targetConf);

  if (!sForms.length && !tForms.length) {
    return { available: false, reason: '两个版本均无低代码表单设计(或为 DIY 流程)', groups: [] };
  }

  // 表单对齐: 按表单名(多表单模式); 单表单模式只有一项
  const tByName = new Map(tForms.map(f => [f.name, f]));
  const groups = [];
  const usedT = new Set();
  for (const sf of sForms) {
    const tf = tByName.get(sf.name) || null;
    if (tf) usedT.add(tf.name);
    groups.push(buildFormGroup(sf, tf));
  }
  for (const tf of tForms) {
    if (!usedT.has(tf.name)) groups.push(buildFormGroup(null, tf));
  }
  const available = true;
  return { available, reason: '', groups };
}

function extractForms(conf) {
  if (!conf) return [];
  const out = [];
  const isExternal = Number(conf.extraFlags || 0) & 0b1000000;
  if (isExternal && Array.isArray(conf.lfFormdataList) && conf.lfFormdataList.length) {
    conf.lfFormdataList.forEach((f, i) => {
      const data = parseFormdata(f.formdata);
      out.push({ name: f.formdataName || f.name || `表单${i + 1}`, widgets: flattenWidgets(data?.widgetList || []) });
    });
  } else if (conf.lfFormData) {
    const data = parseFormdata(conf.lfFormData);
    out.push({ name: '主表单', widgets: flattenWidgets(data?.widgetList || []) });
  }
  return out;
}
function parseFormdata(s) {
  if (!s) return null;
  if (typeof s === 'object') return s;
  try { return JSON.parse(s); } catch (e) { return null; }
}

function buildFormGroup(sf, tf) {
  const sMap = new Map(sf ? sf.widgets.filter(w => w.options?.name).map(w => [w.options.name, w]) : []);
  const tMap = new Map(tf ? tf.widgets.filter(w => w.options?.name).map(w => [w.options.name, w]) : []);
  const fields = [];
  const allKeys = [...new Set([...sMap.keys(), ...tMap.keys()])];
  for (const key of allKeys) {
    const sw = sMap.get(key), tw = tMap.get(key);
    if (sw && !tw) { fields.push({ name: key, label: sw.options?.label || '', status: 'removed', changes: [{ label: '字段', source: widgetDesc(sw), target: '(已删除)' }] }); continue; }
    if (!sw && tw) { fields.push({ name: key, label: tw.options?.label || '', status: 'added', changes: [{ label: '字段', source: '(不存在)', target: widgetDesc(tw) }] }); continue; }
    const changes = [];
    const sl = sw.options.label ?? '', tl = tw.options.label ?? '';
    if (sl !== tl) changes.push({ label: '标签(label)', source: sl, target: tl });
    const st = sw.type ?? '', tt = tw.type ?? '';
    if (st !== tt) changes.push({ label: '类型', source: WIDGET_TYPE_NAMES[st] || st, target: WIDGET_TYPE_NAMES[tt] || tt });
    const sr = !!sw.options.required, tr = !!tw.options.required;
    if (sr !== tr) changes.push({ label: '是否必填', source: sr ? '必填' : '非必填', target: tr ? '必填' : '非必填' });
    if (changes.length) fields.push({ name: key, label: tl || sl || '', status: 'modified', changes });
  }
  const cnt = { added: 0, removed: 0, modified: 0 };
  fields.forEach(f => cnt[f.status]++);
  return { name: sf?.name || tf?.name, fields, counts: cnt };
}
function widgetDesc(w) {
  const t = w.type ? (WIDGET_TYPE_NAMES[w.type] || w.type) : '未知';
  return `${w.options?.label || '(无标签)'} / ${t} / ${w.options?.required ? '必填' : '非必填'}`;
}

// ============ 设置 diff(基础+高级, 总结式全量罗列) ============

const CONF_FLAGS = [
  [0b1000000, '使用外部表单模式'],
  [0b10000000, '使用辅助表单(DIY)'],
  [0b100000000, '传统风格横向设计器'],
  [0b1000000000, '审批人非办公状态自动转办'],
];

/**
 * 设置 diff: 全量罗列 + 差异标记
 */
export function diffSettings(sourceConf, targetConf) {
  const rows = [];
  const add = (group, label, a, b, key) => rows.push({
    group, label, key,
    source: fmtValue(a, key), target: fmtValue(b, key),
    changed: !deepEqual(a, b),
  });

  add('基础设置', '版本名称', sourceConf?.bpmnName, targetConf?.bpmnName, 'bpmnName');
  add('基础设置', '版本编号', sourceConf?.bpmnCode, targetConf?.bpmnCode, 'bpmnCode');
  add('基础设置', '描述说明', sourceConf?.remark, targetConf?.remark, 'remark');
  add('基础设置', '关联表单(外部模式)', sourceConf?.lfFormdataIds, targetConf?.lfFormdataIds, 'lfFormdataIds');
  add('高级设置', '审批人去重', sourceConf?.deduplicationType, targetConf?.deduplicationType, 'deduplicationType');
  for (const [bit, label] of CONF_FLAGS) {
    const sv = (Number(sourceConf?.extraFlags || 0) & bit) === bit;
    const tv = (Number(targetConf?.extraFlags || 0) & bit) === bit;
    add('高级设置', label, sv, tv, 'flag_' + bit);
  }
  add('高级设置', '查看页按钮', sourceConf?.viewPageButtons, targetConf?.viewPageButtons, 'viewPageButtons');
  return rows;
}

// ============ 总入口 ============

/**
 * 比较两个版本的完整配置
 * @param sourceRaw/targetRaw: detail 接口原始返回(data)
 * @returns 完整比较结果
 */
export function compareConfs(sourceRaw, targetRaw) {
  const clone = (x) => JSON.parse(JSON.stringify(x || {}));
  const sTreeData = FormatDisplayUtils.getToTree(clone(sourceRaw));
  const tTreeData = FormatDisplayUtils.getToTree(clone(targetRaw));

  // 节点对齐 + 每对 diff
  const { sList, tList, pairs } = alignTrees(sTreeData?.nodeConfig, tTreeData?.nodeConfig);
  const pairResults = []; // 保持遍历顺序: source 序优先
  const orderedKeys = [];
  const sOrder = new Map(sList.map(it => [it.pairKey, it.idx]));
  const tOrder = new Map(tList.map(it => [it.pairKey, it.idx]));
  for (const [key, pair] of pairs) {
    const so = sOrder.has(key) ? sOrder.get(key) : 100000 + (tOrder.get(key) ?? 0);
    orderedKeys.push([key, pair, so]);
  }
  orderedKeys.sort((a, b) => a[2] - b[2]);

  let nodeDiffCount = 0;
  const formNameCtx = { formNames: buildFormNameMap(sourceRaw, targetRaw) };
  for (const [key, pair] of orderedKeys) {
    const sections = diffNodePair(pair, sList, tList, formNameCtx);
    let cnt = 0;
    for (const rows of sections.values()) {
      for (const r of rows) cnt += (r.kind === 'formPermTable' ? (r.formPermCount || 0) : 1);
    }
    if (pair.status === 'modified' && cnt === 0) pair.status = 'same';
    else nodeDiffCount++;
    // 回写状态到树节点供渲染高亮
    pair.source && (pair.source.node.__diff = pair.status);
    pair.target && (pair.target.node.__diff = pair.status);
    pair.source && (pair.source.node.__pairKey = key);
    pair.target && (pair.target.node.__pairKey = key);
    pairResults.push({ key, pair, sections, diffCount: cnt, status: pair.status });
  }

  const forms = diffForms(sourceRaw, targetRaw);
  const formDiffCount = forms.groups.reduce((n, g) => n + g.fields.length, 0);
  const settingsRows = diffSettings(sourceRaw, targetRaw);
  const settingDiffCount = settingsRows.filter(r => r.changed).length;

  return {
    sourceTree: sTreeData?.nodeConfig || null,
    targetTree: tTreeData?.nodeConfig || null,
    nodeResults: pairResults,
    forms,
    formDiffCount,
    settingsRows,
    settingDiffCount,
    nodeDiffCount,
    totalDiff: nodeDiffCount + formDiffCount + settingDiffCount,
  };
}
