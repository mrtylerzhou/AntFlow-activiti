/**
 * 推进/退回共享状态 composable
 *
 * 在 抽屉(approverDrawer) 与 Zen 节点面板之间共享同一份"推进目标节点/退回目标节点"计算逻辑。
 * 由于 nodeType 12 条件推进节点会同时出现"推进设置"tab 与"按钮权限设置"tab，
 * 这些状态必须由父级持有，通过 v-model/props 传给按钮权限面板。
 */
import { ref, computed, watch } from 'vue';
import { NodeUtils, flattenMapTreeToList } from '@/utils/antflow/nodeUtils';

export function useNodeForwardBack(approverConfigRef, rootNodeRef) {
  const forwardFixedNodeId = ref(null);
  const autoReturnTargetNodeId = ref(null);

  /** 可选推进目标节点: 从当前节点向下遍历流程树, 只保留审批人(4)节点, 跨并行网关继续遍历 */
  const availableForwardNodes = computed(() => {
    const root = rootNodeRef.value;
    if (!root || !root.nodeId) return [];
    const currentNodeId = approverConfigRef.value?.nodeId;
    if (!currentNodeId) return [];
    const cloned = JSON.parse(JSON.stringify(root));
    const flatList = flattenMapTreeToList(cloned);
    const nodeMap = {};
    flatList.forEach(n => { nodeMap[n.nodeId] = n; });
    const currentNode = nodeMap[currentNodeId];
    if (!currentNode) return [];
    const result = [];
    const visited = new Set();
    function traverseDown(node) {
      if (!node || visited.has(node.nodeId)) return;
      visited.add(node.nodeId);
      if (node.nodeType === 4 && node.nodeId !== currentNodeId) {
        result.push({ nodeId: node.nodeId, nodeName: node.nodeName });
      }
      if (node.nodeType === 7) {
        if (node.parallelNodes) {
          for (const branch of node.parallelNodes) {
            traverseDown(branch);
          }
        }
        if (node.childNode) {
          traverseDown(node.childNode);
        }
        return;
      }
      if (node.nodeType === 2 && node.conditionNodes) {
        for (const cond of node.conditionNodes) {
          traverseDown(cond);
        }
      }
      if (node.childNode) {
        traverseDown(node.childNode);
      }
    }
    if (currentNode.childNode) {
      traverseDown(currentNode.childNode);
    }
    return result;
  });

  /** 自动完成节点: 固定目标节点名称(用于只读展示) */
  const forwardFixedNodeName = computed(() => {
    const targetNodeId = forwardFixedNodeId.value;
    if (!targetNodeId || !rootNodeRef.value) return '';
    const findName = (node) => {
      if (!node) return null;
      if (node.nodeId === targetNodeId) return node.nodeName;
      if (node.childNode) {
        const found = findName(node.childNode);
        if (found) return found;
      }
      if (node.conditionNodes) {
        for (const cond of node.conditionNodes) {
          const found = findName(cond);
          if (found) return found;
        }
      }
      if (node.parallelNodes) {
        for (const par of node.parallelNodes) {
          const found = findName(par);
          if (found) return found;
        }
      }
      return null;
    };
    return findName(rootNodeRef.value) || '';
  });

  /** 可选退回目标节点: 从当前节点向上遍历流程树, 只保留发起人(1)和审批人(4)节点 */
  const availableBackNodes = computed(() => {
    const root = rootNodeRef.value;
    if (!root || !root.nodeId) return [];
    const currentNodeId = approverConfigRef.value?.nodeId;
    if (!currentNodeId) return [];
    const cloned = JSON.parse(JSON.stringify(root));
    const flatList = flattenMapTreeToList(cloned);
    const nodeMap = {};
    flatList.forEach(n => { nodeMap[n.nodeId] = n; });
    const result = [];
    const visited = new Set();
    const queue = [currentNodeId];
    while (queue.length > 0) {
      const nid = queue.shift();
      if (visited.has(nid)) continue;
      visited.add(nid);
      const node = nodeMap[nid];
      if (!node) continue;
      if (node.nodeFrom) {
        const fromNode = nodeMap[node.nodeFrom];
        if (fromNode && !visited.has(fromNode.nodeId)) {
          if (fromNode.nodeType === 1 || fromNode.nodeType === 4) {
            result.push({ nodeId: fromNode.nodeId, nodeName: fromNode.nodeName });
          }
          queue.push(fromNode.nodeId);
        }
      }
    }
    return result;
  });

  /** 加载自动退回配置(反显) */
  const loadAutoReturnConfig = (nodeData) => {
    if (approverConfigRef.value?.nodeType !== 19) return;
    autoReturnTargetNodeId.value = (nodeData?.drawBackNodeIds && nodeData.drawBackNodeIds.length > 0)
      ? nodeData.drawBackNodeIds[0] : null;
  };

  /** 加载推进配置(反显): 同步 forwardFixedNodeId(与按钮权限面板共享) */
  const loadForwardConfig = (nodeData) => {
    // 自动完成节点/条件完成节点/完成审批节点: 自动填充最后一个审批人节点(不可编辑)
    if (approverConfigRef.value?.isAutoCompleteNode
      || approverConfigRef.value?.isConditionFinishNode
      || approverConfigRef.value?.isFinishApproveNode) {
      const lastNode = NodeUtils.findLastApproveNode(rootNodeRef.value, approverConfigRef.value?.nodeId);
      forwardFixedNodeId.value = lastNode ? lastNode.nodeId : null;
      return;
    }
    // 条件推进节点/自动推进节点(nodeType=18): 固定节点模式(forwardType=2)
    if (approverConfigRef.value?.isConditionAdvanceNode || approverConfigRef.value?.nodeType === 18) {
      forwardFixedNodeId.value = (nodeData?.forwardNodeIds && nodeData.forwardNodeIds.length > 0)
        ? nodeData.forwardNodeIds[0] : null;
      return;
    }
    const ft = nodeData?.forwardType;
    if (ft === 2) {
      forwardFixedNodeId.value = (nodeData.forwardNodeIds && nodeData.forwardNodeIds.length > 0) ? nodeData.forwardNodeIds[0] : null;
    } else {
      forwardFixedNodeId.value = null;
    }
  };

  return {
    forwardFixedNodeId,
    autoReturnTargetNodeId,
    availableForwardNodes,
    forwardFixedNodeName,
    availableBackNodes,
    loadAutoReturnConfig,
    loadForwardConfig,
  };
}
