/**
 * Zen 模式返回校验工具
 *
 * 合并三套校验（脱离组件依赖）：
 * 1. 节点必填校验（reErr 等价）—— 审批人/抄送/条件/并行子节点 error 标记
 * 2. 结构校验（nodeVerifyMap 等价）—— 至少一个有效节点 / 并行聚合 / 条件并行聚合
 * 3. 按钮权限校验（validateButtonPerms 等价）—— 审批/办理/条件审批节点必须有按钮
 *
 * 只校验用户能在 Zen 模式里改的东西（bpmnName/bpmnCode/formCode 只读，不校验）。
 */
import { nodeTypeList } from './const';
import { flattenMapTreeToList } from './nodeUtils';

/**
 * 校验节点配置
 * @param {Object} nodeConfig 流程节点树
 * @returns {{ isSuccess: boolean, tipList: Array<{name,nodeType}>, msg: string, emptyBtnNodes: Array<string> }}
 */
export const validateBeforeReturn = (nodeConfig) => {
  const result = {
    isSuccess: true,
    tipList: [],
    msg: '',
    emptyBtnNodes: []
  };

  // 1. 节点必填校验
  const tipList = [];
  const reErr = (treeNode) => {
    if (!treeNode) return;
    const { nodeType, error, nodeName, conditionNodes, parallelNodes } = treeNode;
    if (nodeType == 2) {
      for (const cond of conditionNodes || []) {
        if (cond.error == true) {
          tipList.push({ name: cond.nodeName, nodeType: '条件' });
        }
        reErr(cond);
      }
    } else if (nodeType == 4 || nodeType == 6 || nodeType == 17) {
      if (error) {
        tipList.push({ name: nodeName, nodeType: nodeTypeList[nodeType] });
      }
    } else if (nodeType == 7) {
      for (const pn of parallelNodes || []) {
        if (pn.error) {
          tipList.push({ name: pn.nodeName, nodeType: '审批人' });
        }
        reErr(pn);
      }
    }
    reErr(treeNode.childNode);
  };
  reErr(nodeConfig);
  if (tipList.length !== 0) {
    result.isSuccess = false;
    result.tipList = tipList;
    return result;
  }

  // 2. 结构校验
  const validateIsExistApproveNode = (treeNode) => {
    if (!treeNode) return { isSuccess: false, msg: '至少配置一个有效节点，实际项目中不需要可以去掉' };
    const nodeArray = flattenMapTreeToList(treeNode);
    const isExist = nodeArray.some(node =>
      node.nodeType == 4 || node.nodeType == 6 || node.nodeType == 7 || node.nodeType == 17
    );
    if (!isExist) return { isSuccess: false, msg: '至少配置一个有效节点，实际项目中不需要可以去掉' };
    return { isSuccess: true, msg: '' };
  };
  const validateParallelApproveNode = (treeNode) => {
    if (!treeNode) return { isSuccess: true, msg: '' };
    if (treeNode.nodeType == 7) {
      if (!treeNode.childNode || treeNode.childNode.nodeType != 4) {
        return { isSuccess: false, msg: '并行审批下必须有一个审批人节点作为聚合节点' };
      }
      return validateParallelApproveNode(treeNode.childNode);
    }
    return validateParallelApproveNode(treeNode.childNode);
  };
  const validateParallelConditionNode = (treeNode) => {
    if (!treeNode) return { isSuccess: true, msg: '' };
    if (treeNode.nodeType == 2 && treeNode.isParallel == true) {
      if (!treeNode.childNode || treeNode.childNode.nodeType != 4) {
        return { isSuccess: false, msg: '条件并行节点下必须有一个审批人节点作为聚合节点' };
      }
      return validateParallelConditionNode(treeNode.childNode);
    }
    return validateParallelConditionNode(treeNode.childNode);
  };

  const nodeVerifyMap = [validateIsExistApproveNode, validateParallelApproveNode, validateParallelConditionNode];
  for (const handleVerifyFunc of nodeVerifyMap) {
    const { isSuccess, msg } = handleVerifyFunc(nodeConfig);
    if (!isSuccess) {
      result.isSuccess = false;
      result.msg = msg;
      return result;
    }
  }

  // 3. 按钮权限校验
  const emptyBtnNodes = [];
  const traverse = (node) => {
    if (!node) return;
    // 审批人节点(4)、办理节点(10)、条件审批节点(12)需要按钮
    if ([4, 10, 12].includes(node.nodeType)) {
      const btns = node.buttons?.approvalPage || [];
      if (btns.length === 0) {
        emptyBtnNodes.push(node.nodeName || '未命名节点');
      }
    }
    // 并行审批的子节点
    if (node.nodeType === 7 && node.parallelNodes) {
      node.parallelNodes.forEach(pn => {
        const btns = pn.buttons?.approvalPage || [];
        if (btns.length === 0) {
          emptyBtnNodes.push(pn.nodeName || '未命名节点');
        }
      });
    }
    if (node.childNode) traverse(node.childNode);
    if (node.conditionNodes) {
      node.conditionNodes.forEach(cn => {
        if (cn.childNode) traverse(cn.childNode);
      });
    }
  };
  traverse(nodeConfig);
  if (emptyBtnNodes.length > 0) {
    result.isSuccess = false;
    result.emptyBtnNodes = emptyBtnNodes;
    return result;
  }

  return result;
};
