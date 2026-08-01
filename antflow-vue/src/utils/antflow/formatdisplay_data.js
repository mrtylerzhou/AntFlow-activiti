// import { FormatDisplayUtils } from '@/utils/antflowformatdisplay_data.js'
import { hrbpOptions } from "@/utils/antflow/const";
import { isEmpty, isEmptyArray } from "@/utils/antflow/ObjectUtils";
export class FormatDisplayUtils {
  /**
   * 格式化显示数据
   * @param {Array} parmData
   * @returns Object
   */
  static getToTree(parmData) {
    if (isEmptyArray(parmData)) return;
    let node = this.createNodeDisplay(parmData);
    if (!node) return;
    let formatList = this.formatDisplayStructNodeList(parmData?.nodes);
    node.nodeConfig = this.depthConverterNodes(formatList); //parmData.nodes
    return node;
  }

  /**
   * 创建Node Data 数据
   * @param { Object } nodeData - 源节点数据
   * @returns Object
   */
  static createNodeDisplay(nodeData) {
    if (!nodeData) return;
    if (isEmptyArray(nodeData)) return;
    let displayObj = {
      tableId: nodeData?.id,
      bpmnCode: nodeData.bpmnCode,
      bpmnName: nodeData.bpmnName, //name 改成 bpmnName 其他的都是添加的
      bpmnType: nodeData.bpmnType,
      formCode: nodeData.formCode,
      appId: nodeData.appId,
      isOutSideProcess: nodeData.isOutSideProcess,
      businessPartyId: nodeData.businessPartyId,
      businessPartyName: nodeData.businessPartyName,
      businessPartyMark: nodeData.businessPartyMark,
      deduplicationType: nodeData.deduplicationType, //2去重,1不去重
      effectiveStatus: nodeData.effectiveStatus == 1 ? true : false,
      isLowCodeFlow: nodeData.isLowCodeFlow,
      lfFormData: nodeData.lfFormData,
      lfFormDataId: nodeData.lfFormDataId,
      //外部表单模式相关字段
      extraFlags: nodeData.extraFlags || 0,
      lfFormdataIds: nodeData.lfFormdataIds || '',
      lfFormdataList: nodeData.lfFormdataList || null,
      viewPageButtons: nodeData.viewPageButtons,
      property: nodeData.property,
      remark: nodeData.remark,
      isDel: 0,
      directorMaxLevel: 3,
      nodeConfig: {},
      templateVos: nodeData.templateVos || [],
    };
    return displayObj;
  }
  /**
   * List 转成tree结构
   * @param {Array} parmData
   * @returns
   */
  static depthConverterNodes(parmData) {
    if (!parmData) return;
    if (isEmptyArray(parmData)) return;
    let nodesGroup = {};
    for (let t of parmData) {
      if (isEmpty(t.nodeFrom)) continue;
      if (nodesGroup.hasOwnProperty(t.nodeFrom) && !isEmpty(t.nodeFrom)) {
        nodesGroup[t.nodeFrom].push(t);
      } else {
        nodesGroup[t.nodeFrom] = [t];
      }
    }
    if (!parmData.some((c) => c.nodeType == 7)) {
      //判断是否包含并行网关
      return this.depthConverterToTree(nodesGroup, parmData);
    } else {
      return this.depthConverterToTreeForParallelway(nodesGroup, parmData);
    }
  }
  /**
   * List 转成tree结构（不包含并行网关）
   */
  static depthConverterToTree(nodesGroup, parmData) {
    let startNode = {};
    for (let node of parmData) {
      if (1 == node.nodeType) {
        startNode = node;
      }
      Object.assign(node, { conditionNodes: [] });
      let currNodeId = node.nodeId;
      if (nodesGroup.hasOwnProperty(currNodeId)) {
        let itemNodes = nodesGroup[currNodeId];
        for (let itemNode of itemNodes) {
          if (3 == itemNode.nodeType) {
            node.conditionNodes.push(itemNode);
          } else {
            node.childNode = itemNode;
          }
        }
      }
    }
    return startNode;
  }
  /**
   * List 转成tree结构（包含并行网关）
   */
  static depthConverterToTreeForParallelway(nodesGroup, parmData) {
    let startNode = {};
    for (let node of parmData) {
      if (1 == node.nodeType) {
        startNode = node;
      }
      Object.assign(node, { conditionNodes: [] });
      let currNodeId = node.nodeId;
      if (nodesGroup.hasOwnProperty(currNodeId)) {
        let itemNodes = nodesGroup[currNodeId];
        for (let itemNode of itemNodes) {
          if (3 == itemNode.nodeType) {
            node.conditionNodes.push(itemNode);
          } else if (4 == itemNode.nodeType) {
            let isTrueParallelNode = this.isParallelChildNode(
              itemNode,
              parmData,
            );
            if (isTrueParallelNode == false) {
              node.childNode = itemNode;
            } else {
              if (!node.hasOwnProperty("parallelNodes")) {
                Object.assign(node, { parallelNodes: [] });
              }
              node.parallelNodes.push(itemNode);
            }
          } else {
            node.childNode = itemNode;
          }
        }
      }
    }
    return startNode;
  }
  /**
   * 判断是否是并行节点下的 分支子节点
   *
   * 1、当前节点的父节点 nodeType=7
   * 2、当前节点的nodeId,存在于父节点的nodeTo中
   * 以上两个条件同时满足，则该节点为并行节点下的分支子节点
   *
   * 只满足条件 1 ，则 并行路由的下一个普通审批人节点
   *
   * @param {*} currentNode
   * @param {*} parmData
   * @returns
   */
  static isParallelChildNode(currentNode, parmData) {
    for (let node of parmData) {
      if (currentNode.nodeFrom == node.nodeId) {
        if (node.nodeType != 7) {
          return false;
        } else {
          return node.nodeTo.includes(currentNode.nodeId) ? true : false;
        }
      }
    }
    return false;
  }
  /**
   * 格式api返回的数组
   * @param {*} nodeList
   * @returns
   */
  static formatDisplayStructNodeList(nodeList) {
    if (!nodeList) return;
    if (isEmptyArray(nodeList)) return nodeList;
    for (let node of nodeList) {
      if (node.nodeType == 3) {
        node.priorityLevel = node.property.sort;
        node.isDefault = node.property.isDefault;
        node.groupRelation = node.property.groupRelation;
        Object.assign(node, { conditionList: [] });
        node.conditionList = node.property.conditionList
          ? node.property.conditionList
          : [];
        delete node.property;
      }
      node.formAssigneeProperty = node?.property?.formAssigneeProperty;
      node.formInfos = node?.property?.formInfos ?? [];
      if (node.nodeType == 4 || node.nodeType == 6 || node.nodeType == 8 || node.nodeType == 12 || node.nodeType == 13) {
        let empList = [];
        if (node.nodeProperty == 6) {
          let approveObj = {
            type: 5,
            targetId: node.property.hrbpConfType || 0,
            name: hrbpOptions.find(
              (item) => item.value == node.property.hrbpConfType,
            )?.label,
          };
          empList.push(approveObj);
        } else if (
          node.nodeProperty == 4 &&
          !isEmptyArray(node.property.roleList)
        ) {
          for (let role of node.property.roleList) {
            let r = {
              type: 3,
              targetId: role.id,
              name: role.name,
            };
            empList.push(r);
          }
        } else if (
          node.nodeProperty == 5 &&
          !isEmptyArray(node.property.emplList)
        ) {
          for (let emp of node.property.emplList) {
            let approveObj = {
              type: 5,
              targetId: emp.id,
              name: emp.name,
            };
            empList.push(approveObj);
          }
        } else if (node.nodeProperty == 3) {
          node.directorLevel = node.property.assignLevelGrade;
        } else if (node.nodeProperty == 2) {
          // 层层审批: 回填 loop 字段和人员对象到 property 供 drawer 使用
          const prop = node.property || {};
          node.property = {
            ...prop,
            loopEndType: prop.loopEndType || 1,
            loopNumberPlies: prop.loopNumberPlies || 10,
            loopEndGrade: prop.loopEndGrade,
            loopEndPersonList: prop.loopEndPersonList || [],
            loopEndPersonObjList: (prop.loopEndPersonObjList || []).map(
              item => ({ id: item.id, name: item.name })),
            noparticipatingStaffIds: prop.noparticipatingStaffIds || [],
            noparticipatingStaffs: (prop.noparticipatingStaffs || []).map(
              item => ({ id: item.id, name: item.name })),
          };
        }
        Object.assign(node, { signType: node.property?.signType });
        // 仲裁签通过比例回填 (保留在 property, 不提顶层)
        if (node.property) {
          node.property.arbitrationRatio = node.property.arbitrationRatio ?? null;
        }
        node.setType = node.nodeProperty;
        Object.assign(node, { nodeApproveList: [] });

        node.nodeApproveList = empList;
        //delete node.property;
      }

      // 自动节点反显: nodeType=4 + automaticNode标签 → nodeType=9
      if (node.nodeType == 4 && node.labelList && node.labelList.some(l => l.labelValue === "auto_node")) {
        node.nodeType = 9;
        node.nodeName = node.nodeName || "自动节点";
        node.nodeDisplayName = node.nodeDisplayName || "自动节点";
        // 从 autoNodeConf 中恢复条件数据
        node.conditionList = [[]];
        node.groupRelation = false;
        if (node.autoNodeConf) {
          node.conditionList = node.autoNodeConf.conditionList || [[]];
          node.groupRelation = node.autoNodeConf.groupRelation || false;
        }
      }

      // 条件完成节点反显: nodeType=4 或 12 (后端可能已转) + condition_finish_node 标签
      // 条件推进(nodeType=12)子类型, 目标自动为最后一个审批人节点, 不可编辑, 运行时复用条件推进处理器
      if ((node.nodeType == 4 || node.nodeType == 12) && node.labelList && node.labelList.some(l => l.labelValue === "condition_finish_node")) {
        node.nodeType = 12;
        node.nodeName = node.nodeName || "条件完成";
        node.nodeDisplayName = node.nodeDisplayName || "条件完成";
        node.isConditionFinishNode = true;
        // 从 autoNodeConf 中恢复条件数据
        node.conditionList = [[]];
        node.groupRelation = false;
        if (node.autoNodeConf) {
          node.conditionList = node.autoNodeConf.conditionList || [[]];
          node.groupRelation = node.autoNodeConf.groupRelation || false;
        }
      }

      // 条件推进节点反显: nodeType=4 或 12 (后端可能已转) + condition_advance_node 标签
      // 条件审批(nodeType=12)子类型, 自动勾选推进按钮(42,别名同意), 强制 forwardType=2
      if ((node.nodeType == 4 || node.nodeType == 12) && node.labelList && node.labelList.some(l => l.labelValue === "condition_advance_node")) {
        node.nodeType = 12;
        node.nodeName = node.nodeName || "条件推进";
        node.nodeDisplayName = node.nodeDisplayName || "条件推进";
        node.isConditionAdvanceNode = true;
        // 从 autoNodeConf 中恢复条件数据
        node.conditionList = [[]];
        node.groupRelation = false;
        if (node.autoNodeConf) {
          node.conditionList = node.autoNodeConf.conditionList || [[]];
          node.groupRelation = node.autoNodeConf.groupRelation || false;
        }
      }

      // 条件审批节点反显: nodeType=4 或 12 (后端可能已转) + condition_approve_node标签
      // 与 auto node 类似, 但保留真实审批人 (不替换为虚拟人)
      if ((node.nodeType == 4 || node.nodeType == 12) && node.labelList && node.labelList.some(l => l.labelValue === "condition_approve_node")) {
        node.nodeType = 12;
        node.nodeName = node.nodeName || "条件审批";
        node.nodeDisplayName = node.nodeDisplayName || "条件审批";
        // 从 autoNodeConf 中恢复条件数据
        node.conditionList = [[]];
        node.groupRelation = false;
        if (node.autoNodeConf) {
          node.conditionList = node.autoNodeConf.conditionList || [[]];
          node.groupRelation = node.autoNodeConf.groupRelation || false;
        }
      }

      // 条件抄送节点反显: nodeType=4 或 8 或 13 (后端可能已转) + condition_copy_node标签
      // 与抄送V2 类似, 但带条件; 保留真实抄送人 (运行期由后端设 CC_NODE)
      if ((node.nodeType == 4 || node.nodeType == 8 || node.nodeType == 13) && node.labelList && node.labelList.some(l => l.labelValue === "condition_copy_node")) {
        node.nodeType = 13;
        node.nodeName = node.nodeName || "条件抄送";
        node.nodeDisplayName = node.nodeDisplayName || "条件抄送";
        // 从 autoNodeConf 中恢复条件数据
        node.conditionList = [[]];
        node.groupRelation = false;
        if (node.autoNodeConf) {
          node.conditionList = node.autoNodeConf.conditionList || [[]];
          node.groupRelation = node.autoNodeConf.groupRelation || false;
        }
      }

      // 协助节点反显: nodeType=4 或 17 (后端可能已转) + assist_node标签
      // 与审批人节点一致, 但语义为"办理"而非"审批"
      if ((node.nodeType == 4 || node.nodeType == 17) && node.labelList && node.labelList.some(l => l.labelValue === "assist_node")) {
        node.nodeType = 17;
        node.nodeName = node.nodeName || "协助";
        node.nodeDisplayName = node.nodeDisplayName || "协助";
      }

      // 完成审批节点反显: nodeType=4 + finish_approve_node 标签
      // 不转 nodeType(本质是审批人节点), 只设标志位供抽屉识别(自动填充+只读)
      if (node.nodeType == 4 && node.labelList && node.labelList.some(l => l.labelValue === "finish_approve_node")) {
        node.isFinishApproveNode = true;
      }

      // 自动完成节点反显: nodeType=4 或 18 + auto_complete_node 标签 (优先于自动推进判据)
      // 自动完成本质是自动推进(18)子类型, 目标自动为最后一个审批人, 仅前端反显区分 + 颜色区分
      if ((node.nodeType == 4 || node.nodeType == 18) && node.labelList && node.labelList.some(l => l.labelValue === "auto_complete_node")) {
        node.nodeType = 18;
        node.nodeName = node.nodeName || "自动完成";
        node.nodeDisplayName = node.nodeDisplayName || "自动完成";
        node.isAutoCompleteNode = true;
        node.conditionList = [[]];
        node.groupRelation = false;
        if (node.autoNodeConf) {
          node.conditionList = node.autoNodeConf.conditionList || [[]];
          node.groupRelation = node.autoNodeConf.groupRelation || false;
        }
      }
      // 自动推进节点反显: nodeType=4 或 18 (后端可能已转) + auto_advance_node标签
      // 与自动节点(9)同构(虚拟人 -3), 差异: 满足条件时推进到指定目标节点
      // 从 autoNodeConf 恢复 conditionList/groupRelation; forwardType/forwardNodeIds 由公共块处理
      else if ((node.nodeType == 4 || node.nodeType == 18) && node.labelList && node.labelList.some(l => l.labelValue === "auto_advance_node")) {
        node.nodeType = 18;
        node.nodeName = node.nodeName || "自动推进";
        node.nodeDisplayName = node.nodeDisplayName || "自动推进";
        node.conditionList = [[]];
        node.groupRelation = false;
        if (node.autoNodeConf) {
          node.conditionList = node.autoNodeConf.conditionList || [[]];
          node.groupRelation = node.autoNodeConf.groupRelation || false;
        }
      }
      // 自动退回节点反显: nodeType=4 或 19 (后端可能已转) + auto_return_node标签
      // 与自动推进(18)对称, 差异: 满足条件时退回到指定目标节点(FOUR_DISAGREE)
      // 从 autoNodeConf 恢复 conditionList/groupRelation; drawBackType/drawBackNodeIds 由公共块处理
      else if ((node.nodeType == 4 || node.nodeType == 19) && node.labelList && node.labelList.some(l => l.labelValue === "auto_return_node")) {
        node.nodeType = 19;
        node.nodeName = node.nodeName || "自动退回";
        node.nodeDisplayName = node.nodeDisplayName || "自动退回";
        node.conditionList = [[]];
        node.groupRelation = false;
        if (node.autoNodeConf) {
          node.conditionList = node.autoNodeConf.conditionList || [[]];
          node.groupRelation = node.autoNodeConf.groupRelation || false;
        }
      }
    }
    return nodeList;
  }
}
