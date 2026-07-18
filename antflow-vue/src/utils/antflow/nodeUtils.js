//import { NodeUtils } from '@/utils/antflow/nodeUtils'
import { isEmpty, isEmptyArray } from "@/utils/antflow/ObjectUtils";
/** 将按钮类型码转为带 buttonType 字段的对象数组(与后端 BpmnConfCommonButtonPropertyVo 对应) */
const btns = (...types) => types.map(t => ({ buttonType: t }));
export class NodeUtils {
  /**
   * 根据自增数生成64进制id
   * @returns 64进制id字符串
   */
  static idGenerator() {
    let qutient = new Date() - new Date("2024-05-01");
    qutient += Math.ceil(Math.random() * 1000); // 防止重複
    const chars =
      "0123456789ABCDEFGHIGKLMNOPQRSTUVWXYZabcdefghigklmnopqrstuvwxyz";
    const charArr = chars.split("");
    const radix = chars.length;
    const res = [];
    do {
      let mod = qutient % radix;
      qutient = (qutient - mod) / radix;
      res.push(charArr[mod]);
    } while (qutient);
    return res.join("").toUpperCase();
  }
  /**
   * 创建审批人对象
   */
  static createApproveNode(child) {
    let approveNode = {
      nodeId: this.idGenerator(),
      nodeName: "审核人",
      nodeDisplayName: "审核人",
      nodeType: 4, //节点类型 4、审批人
      nodeFrom: "",
      nodeTo: [],
      setType: 5, //审批人类型 5、指定人员
      signType: 1, //审批方式 1:会签-需全部同意，2:或签-一人同意即可，3：顺序会签
      isSignUp: 1, //是否加批 0:否，1:是
      directorLevel: 1,
      noHeaderAction: 0,
      childNode: child,
      error: true,
      property: {
        afterSignUpWay: 1,
        signUpType: 1,
        additionalSignInfoList: [],
      },
      lfFieldControlVOs: [],
      buttons: {
        startPage: btns(1),
        approvalPage: btns(3, 4, 18, 19, 21),
        viewPage: btns(0),
      },
      nodeApproveList: [],
      templateVos: [],
    };
    return approveNode;
  }
  /**
   * 创建抄送人对象
   * @returns object
   */
  static createCopyNode(child) {
    let copyNode = {
      nodeId: this.idGenerator(),
      nodeName: "抄送人",
      nodeDisplayName: "抄送人",
      nodeType: 6,
      nodeFrom: "",
      nodeTo: [],
      setType: 5, //仅支持选择人员
      error: true,
      ccFlag: 1,
      childNode: child,
      property: {},
      lfFieldControlVOs: [],
      buttons: {
        startPage: [],
        approvalPage: [],
        viewPage: [],
      },
      nodeApproveList: [],
    };
    return copyNode;
  }

  /**
   * 创建抄送人节点的V2版本
   * @param {Object} child - 子节点信息
   * @returns {Object}
   */
  /**
   * 创建审批节点的副本（版本2）
   * @param {Object} child - 子节点信息
   * @returns {Object} 返回一个包含审批节点所有属性的对象
   */
  static createCopyNodeV2(child) {
    let copyNodeV2 = {
      nodeId: this.idGenerator(), // 生成唯一节点ID
      nodeName: "抄送人v2", // 节点名称
      nodeDisplayName: "抄送人v2",
      nodeType: 8, //节点类型 8、抄送人v2
      nodeFrom: "",
      nodeTo: [],
      setType: 5, //审批人类型 5、指定人员
      signType: 1, //审批方式 1:会签-需全部同意，2:或签-一人同意即可，3：顺序会签
      isSignUp: 1, //是否加批 0:否，1:是
      directorLevel: 1, // 审批级别
      noHeaderAction: 0,
      childNode: child, // 子节点引用
      error: true,
      property: {
        afterSignUpWay: 1, // 加签后处理方式
        signUpType: 1, // 加签类型
      },
      lfFieldControlVOs: [], // 字段控制对象数组（初始为空）
      buttons: {
        startPage: btns(1), // 开始页面可用的按钮ID数组
        approvalPage: btns(3, 4, 18, 19, 21), // 审批页面可用的按钮ID数组
        viewPage: btns(0),
      },
      nodeApproveList: [], // 节点审批人列表（初始为空）
      templateVos: [], // 模板对象数组（初始为空）
    };
    return copyNodeV2; // 返回创建的审批节点对象
  }
  /**
   * 创建办理节点（纯前端组合：一次性生成两个审批人节点）
   * 第一个：办理人，审批页面仅保留"同意"按钮
   * 第二个：发起人确认，审批人类型为"发起人自己"(setType=12)
   * 两个节点串联，第二个节点的 childNode 指向传入的 child
   * @param {Object} child - 原后续节点
   * @returns {Object} 第一个审批人节点（其 childNode 指向第二个节点）
   */
  static createProcessNode(child) {
    // 第二个节点：发起人确认
    let confirmNode = this.createApproveNode(child);
    confirmNode.nodeName = "发起人确认";
    confirmNode.nodeDisplayName = "发起人确认";
    confirmNode.setType = 12; // 发起人自己
    confirmNode.error = false;

    // 第一个节点：办理人，审批页面仅"同意"按钮
    let processNode = this.createApproveNode(confirmNode);
    processNode.nodeName = "办理人";
    processNode.nodeDisplayName = "办理人";
    processNode.buttons = {
      startPage: btns(1),
      approvalPage: btns(3), // 仅同意
      viewPage: btns(0),
    };

    return processNode;
  }
  /**
   * 创建自动办理节点（纯前端组合：自动节点 + 发起人确认审批人节点）
   * 与办理节点不同，第一个节点为自动节点(nodeType=9)
   * @param {Object} child - 原后续节点
   * @returns {Object} 自动节点（其 childNode 指向发起人确认节点）
   */
  static createAutoProcessNode(child) {
    // 第二个节点：发起人确认
    let confirmNode = this.createApproveNode(child);
    confirmNode.nodeName = "发起人确认";
    confirmNode.nodeDisplayName = "发起人确认";
    confirmNode.setType = 12; // 发起人自己
    confirmNode.error = false;

    // 第一个节点：自动节点
    let autoProcessNode = this.createAutoNode(confirmNode);
    return autoProcessNode;
  }
  /**
   * 创建自动节点对象
   * @param {Object} child - 子节点信息
   * @returns {Object} 自动节点
   */
  static createAutoNode(child) {
    let autoNode = {
      nodeId: this.idGenerator(),
      nodeName: "自动节点",
      nodeDisplayName: "自动节点",
      nodeType: 9, //节点类型 9、自动节点
      nodeFrom: "",
      nodeTo: [],
      setType: 5, //审批人类型 5、指定人员 (虚拟人员 AUTO_NODE_SKIP)
      signType: 1, //审批方式 1:会签
      isSignUp: 1,
      directorLevel: 1,
      noHeaderAction: 0,
      childNode: child,
      error: false,
      property: {
        afterSignUpWay: 1,
        signUpType: 1,
        additionalSignInfoList: [],
      },
      lfFieldControlVOs: [],
      buttons: {
        startPage: btns(1),
        approvalPage: btns(3, 4, 18, 19, 21),
        viewPage: btns(0),
      },
      nodeApproveList: [
        { targetId: "-3", name: "自动节点自动跳过", type: 5 },
      ],
      templateVos: [],
      labelList: [
        { labelValue: "auto_node", labelName: "自动节点" },
      ],
      conditionList: [[]], //条件关系,与条件节点相同的多条件组结构
      groupRelation: false, //条件组关系 false:且 true:或
    };
    return autoNode;
  }
  /**
   * 创建条件审批节点对象
   * 本质是审批人节点(nodeType=4) + 条件配置 + condition_approve_node 标签
   * 运行期由后端 NodeUtil.nodeSpecialProcess 转为 nodeType=4, 保留真实审批人
   * 与 auto node 的差异: 保留真实审批人(非虚拟人), 仅条件满足时自动 complete
   * @param {Object} child - 子节点信息
   * @returns {Object} 条件审批节点
   */
  static createConditionApproveNode(child) {
    let conditionApproveNode = {
      nodeId: this.idGenerator(),
      nodeName: "条件审批",
      nodeDisplayName: "条件审批",
      nodeType: 12, //节点类型 12、条件审批节点
      nodeFrom: "",
      nodeTo: [],
      setType: 5, //审批人类型 5、指定人员 (可由用户改为其他类型)
      signType: 1, //审批方式 1:会签
      isSignUp: 1,
      directorLevel: 1,
      noHeaderAction: 0,
      childNode: child,
      error: true, //必须选审批人(与 approver 一致, auto node 是 false)
      property: {
        afterSignUpWay: 1,
        signUpType: 1,
        additionalSignInfoList: [],
      },
      lfFieldControlVOs: [],
      buttons: {
        startPage: btns(1),
        approvalPage: btns(3, 4, 18, 19, 21),
        viewPage: btns(0),
      },
      nodeApproveList: [], //真实审批人,由用户配置(不塞虚拟人)
      templateVos: [],
      labelList: [
        { labelValue: "condition_approve_node", labelName: "条件审批节点" },
      ],
      conditionList: [[]], //条件关系,与条件节点/自动节点相同结构
      groupRelation: false, //条件组关系 false:且 true:或
    };
    return conditionApproveNode;
  }
  /**
   * 创建网关对象
   * @returns object
   */
  static createGatewayNode(child) {
    let gatewayNode = {
      nodeId: this.idGenerator(),
      nodeName: "网关",
      nodeType: 2,
      nodeFrom: "",
      nodeTo: [],
      childNode: null,
      isDynamicCondition: false, //true 动态条件 false 非动态条件
      isParallel: false, //true 是并行条件 false 非并行条件
      error: false,
      property: null,
      conditionNodes: [
        this.createConditionNode("条件1", child, 1, 0),
        this.createConditionNode("条件2", null, 2, 1),
      ],
    };
    return gatewayNode;
  }
  /**
   * 创建动态网关对象
   * @returns object
   */
  static createDynamicConditionWayNode(child) {
    let gatewayNode = {
      nodeId: this.idGenerator(),
      nodeName: "动态网关",
      nodeType: 2,
      nodeFrom: "",
      nodeTo: [],
      childNode: null,
      isDynamicCondition: true, //true 动态条件 false 非动态条件
      isParallel: false, //true 是并行条件 false 非并行条件
      error: false,
      property: null,
      conditionNodes: [
        this.createConditionNode("动态条件1", child, 1, 0),
        this.createConditionNode("动态条件2", null, 2, 1),
      ],
    };
    return gatewayNode;
  }
  /**
   * 创建条件并行网关对象
   * @returns object
   */
  static createParallelConditionWayNode(child) {
    let gatewayNode = {
      nodeId: this.idGenerator(),
      nodeName: "条件并行网关",
      nodeType: 2,
      nodeFrom: "",
      nodeTo: [],
      childNode: this.createParallelNode("条件并行聚合审批人", null, 1, 0),
      isDynamicCondition: false, //true 动态条件 false 非动态条件
      isParallel: true, //true 是并行条件 false 非并行条件
      error: false,
      property: null,
      conditionNodes: [
        this.createConditionNode("并行条件1", child, 1, 0),
        this.createConditionNode("并行条件2", null, 2, 0),
      ],
    };
    return gatewayNode;
  }
  /**
   * 创建条件对象
   * @returns object
   */
  static createConditionNode(name, childNode, priority, isDefault) {
    let conditionNode = {
      nodeId: this.idGenerator(),
      nodeName: name || "条件1",
      nodeDisplayName: name || "条件1",
      nodeType: 3,
      nodeFrom: "",
      nodeTo: [],
      priorityLevel: priority,
      conditionList: [[]], //条件关系 0：且 1：或
      nodeApproveList: [],
      error: true,
      childNode: childNode,
      isDefault: isDefault || 0,
      groupRelation: false, //条件组关系 0：且 1：或
    };
    return conditionNode;
  }

  /**
   * 克隆节点：深拷贝源节点属性，重置结构字段为新节点
   * @param {Object} sourceNode - 要克隆的源节点
   * @param {Object} childNode - 新节点的后续节点（即当前插入位置的 childNodeP）
   * @returns {Object} 克隆后的新节点
   */
  static cloneNode(sourceNode, childNode) {
    const cloned = JSON.parse(JSON.stringify(sourceNode));
    cloned.nodeId = this.idGenerator();
    cloned.nodeFrom = "";
    cloned.nodeTo = [];
    cloned.childNode = childNode;
    // 使用默认名称，用户可自行修改
    if (cloned.nodeType === 4) {
      cloned.nodeName = "审核人";
      cloned.nodeDisplayName = "审核人";
    } else if (cloned.nodeType === 8) {
      cloned.nodeName = "抄送人v2";
      cloned.nodeDisplayName = "抄送人v2";
    }
    return cloned;
  }

  /**
   * 遍历整棵节点树，收集所有指定 nodeType 的节点
   * @param {Object} rootNode - 根节点
   * @param {Array} targetTypes - 目标 nodeType 列表，如 [4, 8]
   * @returns {Array} 符合条件的节点列表
   */
  static collectNodesByType(rootNode, targetTypes) {
    const result = [];
    function traverse(node) {
      if (!node) return;
      if (targetTypes.includes(node.nodeType)) {
        result.push(node);
      }
      // 条件网关/动态网关/条件并行的 conditionNodes
      if (node.conditionNodes && node.conditionNodes.length) {
        for (const cond of node.conditionNodes) {
          traverse(cond);
        }
      }
      // 并行审批的 parallelNodes
      if (node.parallelNodes && node.parallelNodes.length) {
        for (const par of node.parallelNodes) {
          traverse(par);
        }
      }
      // 链式子节点
      if (node.childNode) {
        traverse(node.childNode);
      }
    }
    traverse(rootNode);
    return result;
  }

  /**
   * 初始化流程数据
   * @returns object
   */
  static createStartNode() {
    let startObj = {
      data: {},
    };
    let startNode = {
      bpmnCode: null,
      bpmnName: "",
      bpmnType: null,
      formCode: "",
      appId: null,
      deduplicationType: 1,
      effectiveStatus: 0,
      isLowCodeFlow: 1, //是否低代码流程 0:否，1:是
      isOutSideProcess: 0, //是否外部流程 0:否，1:是
      viewPageButtons: {
        viewPageStart: [],
        viewPageOther: [],
      },
      remark: "",
      isDel: 0,
      nodes: [
        {
          confId: 1,
          nodeId: "Gb2",
          nodeType: 1,
          nodeProperty: 1,
          nodePropertyName: null,
          nodeFrom: "",
          nodeFroms: null,
          prevId: [],
          batchStatus: 0,
          approvalStandard: 2,
          nodeName: "发起人",
          nodeDisplayName: "发起人",
          annotation: null,
          isDeduplication: 0,
          orderedNodeType: null,
          remark: "",
          isDel: 0,
          nodeTo: [],
          property: null,
          params: null,
          buttons: {
            startPage: [],
            approvalPage: [],
            viewPage: null,
          },
          templateVos: null,
          approveRemindVo: null,
          conditionNodes: [],
        },
      ],
    };
    startObj.data = startNode;
    return startObj;
  }

  /**
   * 条件判断对象
   * @param {*} formId  条件表单Id
   * @param {*} columnId 条件判断id
   * @param {*} type 类型 1，发起人 2，其他表单条件
   * @param {*} showName 显示名称.
   * @param {*} showType //1,值类型（>,>=,<,<=,=）,2单选下拉, 3多选(checkbox) 其他
   * @param {*} columnName  DB字段名称
   * @param {*} columnType  DB字段类型
   * @param {*} fixedDownBoxValue 条件选项
   * @returns
   */
  static createJudgeNode(
    formId,
    columnId,
    type,
    showName,
    showType,
    columnName,
    columnType,
    fieldTypeName,
    multiple,
    multipleLimit,
    fixedDownBoxValue,
  ) {
    let judgeNode = {
      formId: formId,
      columnId: columnId,
      showType: showType,
      type: type, //1，发起人 2，其他表单条件
      showName: showName,
      optType: 5,
      zdy1: fieldTypeName == "switch" ? "1" : "",
      opt1: "<",
      zdy2: "",
      opt2: "<",
      columnDbname: columnName,
      columnType: columnType,
      fieldTypeName: fieldTypeName,
      multiple: multiple,
      multipleLimit: multipleLimit,
      fixedDownBoxValue: fixedDownBoxValue,
    };
    return judgeNode;
  }

  /**
   * 创建并行网关对象
   * @returns object
   */
  static createParallelWayNode(child) {
    let parallelwayNode = {
      nodeId: this.idGenerator(),
      nodeName: "并行审核网关",
      nodeType: 7,
      nodeFrom: "",
      nodeTo: [],
      childNode: this.createParallelNode("并行聚合节点", null, 1, 0),
      error: false,
      property: null,
      parallelNodes: [
        this.createParallelNode("并行审核人1", child, 1, 0),
        this.createParallelNode("并行审核人2", null, 2, 0),
      ],
    };
    return parallelwayNode;
  }
  /**
   * 创建并行审批人对象
   * @returns object
   */
  static createParallelNode(name, childNode, priority, isDefault) {
    let parallelNode = {
      nodeId: this.idGenerator(),
      nodeName: name || "并行审核人1",
      nodeDisplayName: "",
      nodeType: 4, //节点类型 4、审批人
      nodeFrom: "",
      nodeTo: [],
      priorityLevel: priority,
      nodeApproveList: [],
      setType: 5, //审批人类型 5、指定人员
      signType: 1, //审批方式 1:会签-需全部同意，2:或签-一人同意即可，3：顺序会签
      isSignUp: 1, //是否加批 0:否，1:是
      noHeaderAction: 0,
      lfFieldControlVOs: [],
      templateVos: [], //消息通知设置
      property: {
        afterSignUpWay: 1, //是否回到加批人 1:是，2:否
        signUpType: 1, //加批类型 1:顺序会签，2:会签 特别 3指: 回到加批人，afterSignUpWay赋值为1，signUpType赋值为1
        additionalSignInfoList: [],
      },
      buttons: {
        startPage: btns(1),
        approvalPage: btns(3, 4, 18, 19, 21),
        viewPage: btns(0),
      },
      error: true,
      childNode: childNode,
      isDefault: isDefault || 0,
    };
    return parallelNode;
  }

  /**
   * 三方接入条件对象
   * @param {*} conditionValue
   * @returns
   */
  static createOutsideConditionNode(conditionValue) {
    let outsideConditionNode = {
      formId: "9999", //固定值
      columnId: "9999", //固定值
      showType: "2", //固定值
      showName: "条件", //固定值
      columnName: "templateMarks",
      columnType: "String", //固定值
      fieldTypeName: "select", //固定值
      fixedDownBoxValue: conditionValue,
    };
    return outsideConditionNode;
  }
}

/**
 * 添模拟数据
 */
export function getMockData() {
  let startNode = ""; //NodeUtils.createNode("start", "");
  return startNode;
}

/**
 * 展平树结构
 * @param {Object} treeData  - 节点数据
 * @returns Array - 节点数组
 */
export function flattenMapTreeToList(treeData) {
  let nodeData = [];
  function traverse(node) {
    if (!node && !node.hasOwnProperty("nodeType")) {
      return nodeData;
    }
    if (node.nodeType == 2) {
      if (node.childNode) {
        node.childNode.nodeFrom = node.nodeId;
        traverse(node.childNode);
      }
      if (!isEmptyArray(node.conditionNodes)) {
        for (let child of node.conditionNodes) {
          child.nodeFrom = node.nodeId;
          traverse(child);
        }
        node.nodeTo = node.conditionNodes.map((item) => item.nodeId);
      }
    } else if (node.nodeType == 7) {
      if (node.childNode) {
        node.childNode.nodeFrom = node.nodeId;
        traverse(node.childNode);
      }
      if (!isEmptyArray(node.parallelNodes)) {
        for (let child of node.parallelNodes) {
          child.nodeFrom = node.nodeId;
          traverse(child);
        }
        node.nodeTo = node.parallelNodes.map((item) => item.nodeId);
      }
    } else if (node.childNode) {
      node.nodeTo = [node.childNode.nodeId];
      node.childNode.nodeFrom = node.nodeId;
      traverse(node.childNode);
    }
    nodeData.push(node);
  }
  traverse(treeData);
  return nodeData;
}
