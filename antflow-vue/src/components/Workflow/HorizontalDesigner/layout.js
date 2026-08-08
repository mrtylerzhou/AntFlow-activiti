/*
 * 横向传统风格流程设计器 - 布局引擎
 * 输入:树形流程数据(与竖向设计器共用同一棵树, nodeUtils/FormatCommitUtils 兼容)
 * 输出:节点矩形 + SVG 边路径(直角折线+箭头)、满宽换行(上排末→下排首折线)、
 *       网关菱形(排它×/并行+/包容○)、分支水平泳道、右端竖线自动汇合
 * 纯几何计算, 不依赖 DOM / Vue, 便于单测。
 */

export const NODE_W = 200;   // 普通节点宽
export const NODE_H = 56;    // 普通节点高
export const BR_W = 200;     // 分支节点(条件/并行审批人)宽
export const BR_H = 44;      // 分支节点高
export const GW = 46;        // 网关菱形对角线
export const H_GAP = 30;     // 节点间水平线长
export const BRANCH_IN = 26; // 菱形右端到泳道首节点的水平距离
export const MERGE = 26;     // 泳道右端到汇合竖线的距离
export const EXIT = 24;      // 汇合竖线到区段出口的水平距离
export const LANE_GAP = 64;  // 泳道垂直间距(需容纳条件标签与泳道线)
export const ROW_GAP = 64;   // 排间垂直间距
export const PAD = 40;       // 画布内边距
export const FOLDED_W = 210; // 折叠网关单元宽度

export function isGateway(n) {
  return !!n && (n.nodeType === 2 || n.nodeType === 7);
}

/** 网关类型元数据(菱形样式/配色) */
export function gatewayMeta(n) {
  if (!n) return null;
  if (n.nodeType === 7)
    return { kind: "parallel", symbol: "plus", fill: "#E6F1FB", stroke: "#185FA5", title: "并行审批" };
  if (n.isParallel)
    return { kind: "inclusive", symbol: "circle", fill: "#EAF3DE", stroke: "#3B6D11", title: "条件并行" };
  if (n.isDynamicCondition)
    return { kind: "exclusive-dynamic", symbol: "cross", fill: "#EEEDFE", stroke: "#534AB7", title: "动态条件" };
  return { kind: "exclusive", symbol: "cross", fill: "#FAEEDA", stroke: "#854F0B", title: "条件分支" };
}

/** 网关的分支数组: 条件网关→conditionNodes, 并行网关→parallelNodes */
export function gatewayBranches(gw) {
  if (!gw) return [];
  return gw.nodeType === 2 ? gw.conditionNodes || [] : gw.parallelNodes || [];
}

/** 条件标签文本(条件节点 nodeDisplayName 由 resetConditionNodesErr 刷新为表达式) */
/** 条件是否已配置表达式 */
export function condConfigured(node) {
  return !!node && Array.isArray(node.conditionList) &&
    node.conditionList.some((g) => (g || []).some((it) => it && it.columnId && it.columnId !== 0));
}
/** 条件标签文本: 仅已配置时显示(nodeDisplayName 由面板/refreshCondDisplays 刷新), 未配置留空避免与节点标题重影 */
export function branchLabelText(node) {
  if (!node || !condConfigured(node)) return "";
  return node.nodeDisplayName || "";
}

/**
 * 布局整棵流程树
 * @param {Object} root 发起人节点(树根, nodeType=1)
 * @param {Number} width 画布可用宽度(触发满宽换行)
 * @param {Set|Array} [foldedIds] 已折叠网关的 nodeId 集合(折叠态: 分支泳道隐藏, 网关渲染为特殊节点)
 * @returns {{nodes:Array, edges:Array, size:{width,height}, rows:Array, units:Array}}
 */
export function layoutFlowTree(root, width, foldedIds) {
  const folded = foldedIds instanceof Set ? foldedIds : new Set(foldedIds || []);
  const nodes = [];   // {key,node,kind:'node'|'gateway',x,y,w,h,meta?}
  const edges = [];   // {id,kind,d,arrow,noArrow?,label?,labelPos?}
  let ec = 0;
  const eid = () => "e" + (++ec);

  // ---------- 主链单元序列 ----------
  const units = [];
  let cur = root;
  while (cur) {
    if (isGateway(cur)) {
      units.push({ kind: "gateway", node: cur });
      cur = cur.childNode; // 汇合后继续主链
    } else {
      units.push({ kind: "node", node: cur, w: NODE_W, h: NODE_H });
      cur = cur.childNode;
    }
  }

  // ---------- 分支链宽度 ----------
  function chainWidth(startNode) {
    let w = 0, cnt = 0, c = startNode;
    while (c) { w += BR_W + (cnt ? H_GAP : 0); cnt++; c = c.childNode; }
    return w;
  }

  // ---------- 网关区段测量 ----------
  function measureGateway(u) {
    const gw = u.node;
    if (folded.has(gw.nodeId)) {
      u.folded = true;
      u.w = FOLDED_W;
      u.h = NODE_H;
      return u;
    }
    const lanes = gatewayBranches(gw).map((b) => ({ branch: b, w: chainWidth(b) }));
    const laneW = lanes.length ? Math.max(...lanes.map((l) => l.w)) : 0;
    u.lanes = lanes;
    u.laneW = laneW;
    u.gwHalf = GW / 2;
    u.inW = u.gwHalf + BRANCH_IN;
    u.outW = MERGE + EXIT;
    u.w = u.inW + laneW + u.outW;
    const n = lanes.length;
    u.laneH = BR_H;
    u.h = n ? n * BR_H + (n - 1) * LANE_GAP : BR_H;
    return u;
  }
  units.forEach((u) => u.kind === "gateway" && measureGateway(u));

  // ---------- 分排(满宽换行) ----------
  const W = Math.max(width || 900, 360);
  const rows = [];
  let r = { units: [], w: 0, h: 0 };
  for (const u of units) {
    if (r.units.length && r.w + u.w + H_GAP > W - PAD * 2) {
      rows.push(r);
      r = { units: [], w: 0, h: 0 };
    }
    r.units.push(u);
    r.w += (r.units.length > 1 ? H_GAP : 0) + u.w;
    r.h = Math.max(r.h, u.h);
  }
  if (r.units.length) rows.push(r);

  // ---------- 摆放 ----------
  function placeBranchChain(L, startX, cy, unit) {
    const { branch } = L;
    let x = startX;
    nodes.push({ key: branch.nodeId, node: branch, kind: "node", x, y: cy - BR_H / 2, w: BR_W, h: BR_H });
    // 分支 → 链内
    let prev = branch;
    let c = branch.childNode;
    while (c) {
      x += BR_W + H_GAP;
      nodes.push({ key: c.nodeId, node: c, kind: "node", x, y: cy - BR_H / 2, w: BR_W, h: BR_H });
      edges.push({ id: eid(), kind: "chain", d: `M ${x - H_GAP} ${cy} H ${x}`, arrow: true });
      prev = c;
      c = c.childNode;
    }
    // 泳道末端 → 汇合竖线(箭头)
    // 画在泳道节点底边下方 4px(cy+BR_H/2+4), 避开与该泳道 childNode 链节点(中心 y=cy)同 y 水平重叠
    const lastRight = x + BR_W;
    const mergeX = unit.mergeX;
    if (unit.lanes.length > 1 || unit.node.childNode) {
      edges.push({ id: eid(), kind: "lane-out", d: `M ${lastRight} ${cy + BR_H / 2 + 4} H ${mergeX}`, arrow: true });
    }
  }

  function placeGateway(u, x, y, rowH) {
    const gw = u.node;
    const meta = gatewayMeta(gw);
    if (u.folded) {
      // 折叠态: 渲染为特殊节点(矩形+网关色描边), 主链直接穿过(出口=右缘中点 → childNode)
      u.x = x; u.y = y + (rowH - NODE_H) / 2; u.w = FOLDED_W; u.h = NODE_H;
      nodes.push({
        key: gw.nodeId, node: gw, kind: "gateway", x, y: u.y, w: FOLDED_W, h: NODE_H, meta,
        folded: true, branchCount: gatewayBranches(gw).length,
      });
      u.entryTop = { x: x + FOLDED_W / 2, y: u.y };
      u.entry = { x, y: u.y + NODE_H / 2 };
      u.exit = { x: x + FOLDED_W, y: u.y + NODE_H / 2 };
      return;
    }
    const sectH = u.h;
    const sy = y + (rowH - sectH) / 2;
    u.x = x; u.y = sy; u.h = sectH;
    const gcx = x + u.gwHalf;
    const gcy = sy + sectH / 2;
    u.gx = gcx; u.gy = gcy;
    u.entryTop = { x: gcx, y: sy };           // 跨排折线进入点(菱形顶)
    u.entry = { x: gcx - GW / 2, y: gcy };    // 同排进入点(菱形左顶点)
    const gwEntry = { key: gw.nodeId, node: gw, kind: "gateway", x: gcx, y: gcy, w: GW, h: GW, meta };
    nodes.push(gwEntry);
    const n = u.lanes.length;
    const laneStartX = gcx + BRANCH_IN;
    const mergeX = laneStartX + u.laneW + MERGE;
    u.mergeX = mergeX;
    // 聚合加号: 分支汇合后继续添加节点(点击弹菜单, 新节点插入为汇合后第一节点 = 网关.childNode)
    if (n > 0) {
      gwEntry.addBtn = { x: mergeX + EXIT, y: sy + sectH / 2 - 24 };
    }
    // 网关 → 泳道线(条件网关带条件标签, 并行网关不带)
    // 改用斜线: M (sx) (gcy) L (laneStartX) (lcy+BR_H/2+4), 跳过泳道节点 y 范围(cy), 视觉上不穿过泳道 1-5 区域
    const isCondGw = gw.nodeType === 2;
    u.lanes.forEach((L, i) => {
      const lcy = sy + i * (BR_H + LANE_GAP) + BR_H / 2;
      L.cy = lcy;
      const sx = gcx + GW / 2;
      const laneEdge = {
        id: eid(), kind: "lane-in", arrow: true,
        d: `M ${sx} ${gcy} L ${laneStartX} ${lcy + BR_H / 2 + 4}`,
      };
      if (isCondGw) {
        const branches = gatewayBranches(gw);
        laneEdge.label = branchLabelText(branches[i]);
        laneEdge.labelPos = { x: (sx + laneStartX) / 2, y: (gcy + lcy) / 2 };
        laneEdge.condNode = branches[i];
        laneEdge.condIndex = i;
      }
      edges.push(laneEdge);
    });
    // 泳道摆放
    u.lanes.forEach((L) => placeBranchChain(L, laneStartX, L.cy, u));
    // 汇合竖线 + 出口(汇合竖线底 = 最低泳道 lane-out 终点, 避免断点)
    if (n > 0) {
      const topY = sy + BR_H / 2;
      const maxLcy = Math.max(...u.lanes.map((L) => L.cy));
      const botY = maxLcy + BR_H / 2 + 4;
      edges.push({ id: eid(), kind: "merge", d: `M ${mergeX} ${topY} V ${botY}`, noArrow: true });
      u.exit = { x: mergeX + EXIT, y: sy + sectH / 2 };
      if (gw.childNode) {
        edges.push({ id: eid(), kind: "merge-out", d: `M ${mergeX} ${sy + sectH / 2} H ${mergeX + EXIT}`, arrow: true });
      }
    } else {
      u.exit = { x: gcx + GW / 2, y: gcy };
    }
  }

  let y = PAD;
  rows.forEach((row, ri) => {
    let x = PAD;
    row.units.forEach((u) => {
      u.rowIndex = ri;
      if (u.kind === "node") {
        u.x = x; u.y = y + (row.h - NODE_H) / 2; u.w = NODE_W; u.h = NODE_H;
        nodes.push({ key: u.node.nodeId, node: u.node, kind: "node", x: u.x, y: u.y, w: u.w, h: u.h });
        u.entryTop = { x: x + NODE_W / 2, y: u.y };
        u.entry = { x: u.x, y: u.y + NODE_H / 2 };
        u.exit = { x: u.x + u.w, y: u.y + NODE_H / 2 };
      } else {
        placeGateway(u, x, y, row.h);
      }
      x += u.w + H_GAP;
    });
    row.y = y;
    y += row.h + ROW_GAP;
  });

  // ---------- 主链边(含跨排折线) ----------
  for (let i = 0; i < units.length - 1; i++) {
    const a = units[i], b = units[i + 1];
    if (b.rowIndex !== a.rowIndex) {
      // 跨排: 上排末出口 → 右出 → 下到两排间空隙 → 左到本排首上方 → 垂直进入
      const midY = (a.y + a.h + b.y) / 2;
      const ex = a.exit.x, ey = a.exit.y;
      const t = b.entryTop;
      edges.push({
        id: eid(), kind: "wrap",
        d: `M ${ex} ${ey} H ${ex + 32} V ${midY} H ${t.x} V ${t.y}`,
        arrow: true,
      });
    } else {
      edges.push({ id: eid(), kind: "chain", d: `M ${a.exit.x} ${a.exit.y} H ${b.entry.x}`, arrow: true });
    }
  }

  // 最后一个单元出口 → 结束圆点
  if (units.length) {
    const last = units[units.length - 1];
    last.exitCircle = { x: last.exit.x + 34, y: last.exit.y };
    edges.push({ id: eid(), kind: "end", fromKey: last.node.nodeId, d: `M ${last.exit.x} ${last.exit.y} H ${last.exitCircle.x - 8}`, arrow: true });
  }

  const maxRowW = Math.max(...rows.map((row) => row.w));
  return {
    nodes, edges, units, rows,
    // width 额外预留结束圆点(距出口 34 + 半径 10 + 边距 12)空间, 避免圆被画布切掉
    size: { width: maxRowW + PAD * 2 + 56, height: y - ROW_GAP + PAD },
  };
}
