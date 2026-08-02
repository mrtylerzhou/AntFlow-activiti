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
   * 创建退回审批节点对象
   * 本质是审批人节点(nodeType=4), 与普通审批人的差异:
   *   - 按钮默认只选"同意(3)"和"不同意(4)"(不含退回18/加批19/转办21)
   *   - 不同意按钮行为默认为"退回指定节点·重新开始"(disagreeBackType=4), 用户可改为5(回到当前节点)
   * 颜色/图标由 af_syslabel_disagree_back 标签驱动(disagreeBackType=4/5 时后端自动贴),
   * 因此"用户自配的退回指定节点审批人"与"退回审批节点"显示相同颜色, 无需独立标签.
   * @param {Object} child - 子节点信息
   * @returns {Object} 退回审批节点
   */
  static createBackApproveNode(child) {
    let backApproveNode = {
      nodeId: this.idGenerator(),
      nodeName: "退回审批",
      nodeDisplayName: "退回审批",
      nodeType: 4, //节点类型 4、审批人
      nodeFrom: "",
      nodeTo: [],
      setType: 5, //审批人类型 5、指定人员
      signType: 1, //审批方式 1:会签
      isSignUp: 1,
      directorLevel: 1,
      noHeaderAction: 0,
      childNode: child,
      error: true, //必须选审批人
      property: {
        afterSignUpWay: 1,
        signUpType: 1,
        additionalSignInfoList: [],
      },
      lfFieldControlVOs: [],
      buttons: {
        startPage: btns(1),
        approvalPage: btns(3, 4), //只同意+不同意(不含退回/加批/转办)
        viewPage: btns(0),
      },
      nodeApproveList: [], //真实审批人,由用户配置
      templateVos: [],
      labelList: [
        { labelValue: "af_syslabel_disagree_back", labelName: "不同意退回指定节点" },
      ],
      disagreeBackType: 4, //不同意按钮行为: 4=退回指定节点·重新开始 (用户可改为5=回到当前节点)
      disagreeBackToNodeId: null, //退回目标节点(设计时在抽屉里配置, 必填, 后端校验)
    };
    return backApproveNode;
  }
  /**
   * 创建退回发起人节点对象
   * 本质是审批人节点(nodeType=4), 与退回审批(createBackApproveNode)的差异:
   *   - 不同意按钮行为默认为"退回发起人"(disagreeBackType=2), 运行时由 BackToModifyImpl
   *     硬编码定位发起人节点, 无需目标节点(disagreeBackToNodeId 恒为空)
   *   - 用户可在抽屉里把"不同意行为"切换为"退回指定节点·重新开始(4)/回到当前节点(5)"
   * 颜色/图标由 af_syslabel_disagree_back 标签驱动(disagreeBackType=2/4/5 时后端自动贴)
   * @param {Object} child - 子节点信息
   * @returns {Object} 退回发起人节点
   */
  static createBackStarterNode(child) {
    let backStarterNode = {
      nodeId: this.idGenerator(),
      nodeName: "退回发起人",
      nodeDisplayName: "退回发起人",
      nodeType: 4, //节点类型 4、审批人
      nodeFrom: "",
      nodeTo: [],
      setType: 5, //审批人类型 5、指定人员
      signType: 1, //审批方式 1:会签
      isSignUp: 1,
      directorLevel: 1,
      noHeaderAction: 0,
      childNode: child,
      error: true, //必须选审批人
      property: {
        afterSignUpWay: 1,
        signUpType: 1,
        additionalSignInfoList: [],
      },
      lfFieldControlVOs: [],
      buttons: {
        startPage: btns(1),
        approvalPage: btns(3, 4), //只同意+不同意(不含退回/加批/转办)
        viewPage: btns(0),
      },
      nodeApproveList: [], //真实审批人,由用户配置
      templateVos: [],
      labelList: [
        { labelValue: "af_syslabel_disagree_back", labelName: "不同意退回" },
      ],
      disagreeBackType: 2, //不同意按钮行为: 2=退回发起人(默认, 用户可改为4/5=退回指定节点)
      disagreeBackToNodeId: null, //退回目标节点: 仅选4/5时需配置, 选2时恒为空(运行时自动退回发起人)
    };
    return backStarterNode;
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
   * 创建推进审批节点对象
   * 本质是审批人节点(nodeType=4) + 推进按钮(固定节点)的预置模板
   * 不引入新 nodeType, 不贴标签; 着色判据为 nodeType==4 && forwardType!=null
   * 默认按钮: 不同意(4) + 推进(42, 自定义名"同意"); 不含同意(3)
   * 用户点"同意"(实为推进)即自动同意当前任务并推进到目标节点(复用 ForwardToNodeImpl)
   * @param {Object} child - 子节点信息
   * @returns {Object} 推进审批节点(nodeType=4, 带 forwardType=2)
   */
  static createForwardApproveNode(child) {
    let node = this.createApproveNode(child);
    node.nodeName = "推进审批";
    node.nodeDisplayName = "推进审批";
    // 默认按钮: 不同意 + 推进(改名"同意"); 不含同意(3)
    node.buttons = {
      startPage: btns(1),
      approvalPage: [
        { buttonType: 4 },
        { buttonType: 42, buttonName: '同意' },
      ],
      viewPage: btns(0),
    };
    // 推进配置: 默认固定节点模式, 必须选择目标节点(后端发布校验保证)
    node.forwardType = 2;
    node.forwardNodeIds = []; // 空, 未选目标节点时后端发布校验抛异常
    return node;
  }
  /**
   * 创建完成审批节点对象
   * 本质是审批人节点(nodeType=4) + 推进按钮(固定节点) + finish_approve_node 标签
   * 目标节点自动填充为流程树中最后一个 nodeType=4 节点(跨并行网关遍历)
   * 用户不可改目标节点(前端隐藏选择框, 只读展示)
   * 着色判据: labelList 含 finish_approve_node
   * @param {Object} child - 子节点信息
   * @returns {Object} 完成审批节点(nodeType=4, 带 forwardType=2 + finish_approve_node 标签)
   */
  static createFinishApproveNode(child) {
    let node = this.createApproveNode(child);
    node.nodeName = "完成审批";
    node.nodeDisplayName = "完成审批";
    // 默认按钮: 不同意 + 推进(改名"同意"); 不含同意(3)
    node.buttons = {
      startPage: btns(1),
      approvalPage: [
        { buttonType: 4 },
        { buttonType: 42, buttonName: '同意' },
      ],
      viewPage: btns(0),
    };
    // 推进配置: 固定节点模式, 目标由 findLastApproveNode 自动计算填充
    node.forwardType = 2;
    node.forwardNodeIds = []; // 空, 提交时/打开抽屉时自动填充
    // 完成审批标志位(前端用, 提交时贴 finish_approve_node 标签)
    node.isFinishApproveNode = true;
    // 预贴标签(提交时 labelList 已带此标签, 后端原样存储+反显)
    node.labelList = [
      { labelValue: 'finish_approve_node', labelName: '完成审批节点' },
    ];
    return node;
  }
  /**
   * 创建自动完成节点对象
   * 本质是自动推进节点(nodeType=18)的一个子类型, 结构完全参照 createAutoAdvanceNode
   * 与自动推进的差异:
   *   - 目标节点不可编辑, 自动选择为流程树中最后一个 nodeType=4 节点(findLastApproveNode)
   *   - 贴 auto_complete_node 标签(仅前端反显区分 + 颜色区分用)
   * 运行时复用 auto_advance_node 处理器(processAutoAdvanceNode), 双标签不会重复触发
   * @param {Object} child - 子节点信息
   * @returns {Object} 自动完成节点(nodeType=18, 带 auto_advance_node + auto_complete_node 标签)
   */
  static createAutoCompleteNode(child) {
    let autoCompleteNode = {
      nodeId: this.idGenerator(),
      nodeName: "自动完成",
      nodeDisplayName: "自动完成",
      nodeType: 18, //节点类型 18、自动推进家族(子类型:自动完成)
      nodeFrom: "",
      nodeTo: [],
      setType: 5, //审批人类型 5、指定人员 (虚拟人员 AUTO_NODE_SKIP)
      signType: 1, //审批方式 1:会签
      isSignUp: 1,
      directorLevel: 1,
      noHeaderAction: 0,
      childNode: child,
      error: true, //必须选推进目标节点(自动完成不能是最后一个审批人), 阻止发布
      property: {
        afterSignUpWay: 1,
        signUpType: 1,
        additionalSignInfoList: [],
      },
      lfFieldControlVOs: [],
      // 审批页无任何操作按钮(自动执行, 无人工交互)
      buttons: {
        startPage: btns(1),
        approvalPage: btns(0),
        viewPage: btns(0),
      },
      nodeApproveList: [
        { targetId: "-3", name: "自动节点自动跳过", type: 5 },
      ],
      templateVos: [],
      // 双标签: auto_advance_node 触发运行时处理器, auto_complete_node 仅前端反显/颜色区分
      labelList: [
        { labelValue: "auto_advance_node", labelName: "自动推进节点" },
        { labelValue: "auto_complete_node", labelName: "自动完成节点" },
      ],
      conditionList: [[]], //条件可空, 空时等价无条件推进(每次必推进)
      groupRelation: false,
      // 自动完成标志位(前端用, 提交时后端据此贴 auto_complete_node 标签)
      isAutoCompleteNode: true,
      // 推进配置: 强制固定节点模式(forwardType=2), 目标由 findLastApproveNode 自动填充
      forwardType: 2,
      forwardNodeIds: [], //打开抽屉时/提交时自动填充为最后一个审批人
    };
    return autoCompleteNode;
  }
  /**
   * 遍历流程树, 找到最后一个 nodeType=4(审批人) 节点
   * 跨并行网关: 遍历 parallelNodes 分支 + childNode(聚合节点)
   * 跨条件网关: 遍历 conditionNodes 分支
   * 排除当前节点(完成审批节点自己不能是目标)
   * @param {Object} rootNode - 流程树根节点
   * @param {String} excludeNodeId - 排除的节点ID(当前完成审批节点)
   * @returns {Object|null} 最后一个审批人节点, 或 null(没有后续审批人)
   */
  static findLastApproveNode(rootNode, excludeNodeId) {
    if (!rootNode) return null;
    let lastApprove = null;
    const visited = new Set();
    function traverse(node) {
      if (!node || visited.has(node.nodeId)) return;
      visited.add(node.nodeId);
      // 收集审批人节点(nodeType=4), 排除当前节点
      if (node.nodeType === 4 && node.nodeId !== excludeNodeId) {
        lastApprove = node;
      }
      // 并行网关(nodeType=7): 遍历所有分支(parallelNodes) + 聚合节点(childNode)
      if (node.nodeType === 7) {
        if (node.parallelNodes) {
          for (const branch of node.parallelNodes) {
            traverse(branch);
          }
        }
        if (node.childNode) {
          traverse(node.childNode);
        }
        return;
      }
      // 条件网关(nodeType=2): 遍历所有分支(conditionNodes)
      if (node.nodeType === 2 && node.conditionNodes) {
        for (const cond of node.conditionNodes) {
          traverse(cond);
        }
      }
      // 继续向下
      if (node.childNode) {
        traverse(node.childNode);
      }
    }
    traverse(rootNode);
    return lastApprove;
  }
  /**
   * 创建自动推进节点对象
   * 本质是自动节点(nodeType=9) + 推进按钮(固定节点)的组合
   * 运行期由后端 NodeUtil.nodeSpecialProcess 转为 nodeType=4, 塞虚拟审批人 -3
   * 满足条件时推进到指定目标节点, 不满足时和自动节点一样 complete
   * @param {Object} child - 子节点信息
   * @returns {Object} 自动推进节点
   */
  static createAutoAdvanceNode(child) {
    let autoAdvanceNode = {
      nodeId: this.idGenerator(),
      nodeName: "自动推进",
      nodeDisplayName: "自动推进",
      nodeType: 18, //节点类型 18、自动推进节点
      nodeFrom: "",
      nodeTo: [],
      setType: 5, //审批人类型 5、指定人员 (虚拟人员 AUTO_NODE_SKIP)
      signType: 1, //审批方式 1:会签
      isSignUp: 1,
      directorLevel: 1,
      noHeaderAction: 0,
      childNode: child,
      error: true, //必须选推进目标节点, 阻止发布
      property: {
        afterSignUpWay: 1,
        signUpType: 1,
        additionalSignInfoList: [],
      },
      lfFieldControlVOs: [],
      // 审批页无任何操作按钮(自动执行, 无人工交互)
      buttons: {
        startPage: btns(1),
        approvalPage: btns(0),
        viewPage: btns(0),
      },
      nodeApproveList: [
        { targetId: "-3", name: "自动节点自动跳过", type: 5 },
      ],
      templateVos: [],
      labelList: [
        { labelValue: "auto_advance_node", labelName: "自动推进节点" },
      ],
      conditionList: [[]], //条件可空, 空时等价无条件推进(每次必推进)
      groupRelation: false,
      // 推进配置: 强制固定节点模式(forwardType=2), 用户选择目标节点
      forwardType: 2,
      forwardNodeIds: [], //待用户选择, 空时 error=true 阻止发布
    };
    return autoAdvanceNode;
  }
  /**
   * 创建自动退回节点对象
   * 本质是自动节点 + 退回按钮(固定节点)的组合
   * 运行期由后端 NodeUtil.nodeSpecialProcess 转为 nodeType=4, 塞虚拟审批人 -3
   * 满足条件时退回到指定目标节点(FOUR_DISAGREE), 不满足时自动 complete
   * @param {Object} child - 子节点信息
   * @returns {Object} 自动退回节点
   */
  static createAutoReturnNode(child) {
    let autoReturnNode = {
      nodeId: this.idGenerator(),
      nodeName: "自动退回",
      nodeDisplayName: "自动退回",
      nodeType: 19, //节点类型 19、自动退回节点
      nodeFrom: "",
      nodeTo: [],
      setType: 5, //审批人类型 5、指定人员 (虚拟人员 AUTO_NODE_SKIP)
      signType: 1, //审批方式 1:会签
      isSignUp: 1,
      directorLevel: 1,
      noHeaderAction: 0,
      childNode: child,
      error: true, //必须选退回目标节点, 阻止发布
      property: {
        afterSignUpWay: 1,
        signUpType: 1,
        additionalSignInfoList: [],
      },
      lfFieldControlVOs: [],
      // 审批页无任何操作按钮(自动执行, 无人工交互)
      buttons: {
        startPage: btns(1),
        approvalPage: btns(0),
        viewPage: btns(0),
      },
      nodeApproveList: [
        { targetId: "-3", name: "自动退回节点自动退回", type: 5 },
      ],
      templateVos: [],
      labelList: [
        { labelValue: "auto_return_node", labelName: "自动退回节点" },
      ],
      conditionList: [[]], //条件可空, 空时等价无条件退回(每次必退回)
      groupRelation: false,
      // 退回配置: 强制 drawBackType=4(指定节点不回到退回人), 用户选择目标节点
      drawBackType: 4,
      drawBackNodeIds: [], //待用户选择, 空时 error=true 阻止发布
    };
    return autoReturnNode;
  }
  /**
   * 创建自动退回发起人节点对象
   * 与 createAutoReturnNode 完全一致, 区别: drawBackType=2(退回发起人), drawBackNodeIds 在创建时即填充
   * @param {Object} child - 子节点信息
   * @param {string} starterNodeId - 发起人节点的 nodeId(UUID)
   */
  static createAutoReturnStarterNode(child, starterNodeId) {
    let node = this.createAutoReturnNode(child);
    node.nodeName = "自动退回发起人";
    node.nodeDisplayName = "自动退回发起人";
    node.drawBackType = 2; // TWO_DISAGREE: 退回发起人, 不回到退回人
    node.drawBackNodeIds = starterNodeId ? [starterNodeId] : [];
    node.error = !starterNodeId; // 有发起人ID则不阻止发布
    return node;
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
   * 创建条件退回节点对象
   * 本质是审批人节点(nodeType=20) + 条件配置 + 不同意按钮退回行为
   * 满足条件时自动退回到不同意按钮配置的目标节点; 不满足时留给真实审批人人工处理
   * @param {Object} child - 子节点信息
   * @returns {Object} 条件退回节点
   */
  static createConditionReturnNode(child) {
    let conditionReturnNode = {
      nodeId: this.idGenerator(),
      nodeName: "条件退回",
      nodeDisplayName: "条件退回",
      nodeType: 20, //节点类型 20、条件退回节点
      nodeFrom: "",
      nodeTo: [],
      setType: 5, //审批人类型 5、指定人员
      signType: 1, //审批方式 1:会签
      isSignUp: 1,
      directorLevel: 1,
      noHeaderAction: 0,
      childNode: child,
      error: true, //必须选审批人
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
      nodeApproveList: [], //真实审批人
      templateVos: [],
      labelList: [
        { labelValue: "condition_return_node", labelName: "条件退回节点" },
      ],
      conditionList: [[]], //条件关系
      groupRelation: false, //条件组关系 false:且 true:或
      disagreeBackType: 5, //默认退回指定节点(回到当前节点)
      disagreeBackToNodeId: null, //用户需选择目标节点
    };
    return conditionReturnNode;
  }
  /**
   * 创建条件推进节点对象
   * 本质是条件审批节点(nodeType=12) + 自动勾选推进按钮(42,别名"同意"), 不含"同意"按钮
   * 与条件审批的差异:
   *   - 按钮: 只勾选不同意(4) + 推进(42, 显示"同意"), 不含同意(3) (与推进审批相同)
   *   - 强制 forwardType=2(固定目标节点), 满足条件时自动推进到该目标
   * 运行期: 满足条件 → 后端自动推进到固定目标(虚拟人-3); 不满足 → 留给真实审批人(点"同意"=推进按钮)
   * @param {Object} child - 子节点信息
   * @returns {Object} 条件推进节点
   */
  static createConditionAdvanceNode(child) {
    let conditionAdvanceNode = {
      nodeId: this.idGenerator(),
      nodeName: "条件推进",
      nodeDisplayName: "条件推进",
      nodeType: 12, //节点类型 12、条件审批家族(条件推进为其子类型)
      nodeFrom: "",
      nodeTo: [],
      setType: 5, //审批人类型 5、指定人员 (可由用户改为其他类型)
      signType: 1, //审批方式 1:会签
      isSignUp: 1,
      directorLevel: 1,
      noHeaderAction: 0,
      childNode: child,
      error: true, //必须选审批人(同条件审批)
      property: {
        afterSignUpWay: 1,
        signUpType: 1,
        additionalSignInfoList: [],
      },
      lfFieldControlVOs: [],
      buttons: {
        startPage: btns(1),
        approvalPage: [
          { buttonType: 4 }, //不同意
          { buttonType: 42, buttonName: "同意" }, //推进按钮(显示为"同意")
        ],
        viewPage: btns(0),
      },
      nodeApproveList: [], //真实审批人,由用户配置(不塞虚拟人)
      templateVos: [],
      labelList: [
        { labelValue: "condition_advance_node", labelName: "条件推进节点" },
      ],
      conditionList: [[]], //条件关系,与条件审批相同结构
      groupRelation: false, //条件组关系 false:且 true:或
      isConditionAdvanceNode: true, //条件推进标记(前端用, 提交时后端据此贴 condition_advance_node 标签)
      forwardType: 2, //强制固定节点(满足条件时自动推进到固定目标)
      forwardNodeIds: [], //推进目标节点(设计时在抽屉里配置, 恰好1个)
    };
    return conditionAdvanceNode;
  }
  /**
   * 创建条件完成节点对象
   * 本质是条件推进节点(nodeType=12)的子类型, 与条件推进的惟一差别: 目标节点来源
   *   - 条件推进: 目标由用户在设计时手动选择(抽屉里选)
   *   - 条件完成: 目标自动为流程最后一个 nodeType=4 审批人节点(设计时 findLastApproveNode 计算), 不可编辑
   * 结构/按钮/条件/运行时与条件推进完全一致(运行时复用条件推进处理器 processConditionAdvanceNode)
   * @param {Object} child - 子节点信息
   * @returns {Object} 条件完成节点
   */
  static createConditionFinishNode(child) {
    let conditionFinishNode = {
      nodeId: this.idGenerator(),
      nodeName: "条件完成",
      nodeDisplayName: "条件完成",
      nodeType: 12, //节点类型 12、条件审批家族(条件完成为其子类型)
      nodeFrom: "",
      nodeTo: [],
      setType: 5, //审批人类型 5、指定人员
      signType: 1,
      isSignUp: 1,
      directorLevel: 1,
      noHeaderAction: 0,
      childNode: child,
      error: true, //必须选审批人
      property: {
        afterSignUpWay: 1,
        signUpType: 1,
        additionalSignInfoList: [],
      },
      lfFieldControlVOs: [],
      buttons: {
        startPage: btns(1),
        approvalPage: [
          { buttonType: 4 }, //不同意
          { buttonType: 42, buttonName: "同意" }, //推进按钮(显示为"同意")
        ],
        viewPage: btns(0),
      },
      nodeApproveList: [], //真实审批人,由用户配置
      templateVos: [],
      labelList: [
        { labelValue: "condition_finish_node", labelName: "条件完成节点" },
      ],
      conditionList: [[]],
      groupRelation: false,
      isConditionFinishNode: true, //条件完成标记(前端用, 提交时后端据此贴 condition_finish_node 标签)
      forwardType: 2, //强制固定节点
      forwardNodeIds: [], //目标自动填充(设计时开抽屉/提交时 refill 计算为最后一个审批人)
    };
    return conditionFinishNode;
  }
  /**
   * 创建条件抄送节点对象
   * 本质是抄送V2节点(nodeType=8) + 条件配置 + condition_copy_node 标签
   * 运行期由后端 NodeUtil.nodeSpecialProcess 转为 nodeType=4, 由 processConditionCopyNode 处理
   * 与抄送V2 的差异: 仅条件满足时才写 BpmProcessForward 抄送记录; 无论条件如何都 complete
   * @param {Object} child - 子节点信息
   * @returns {Object} 条件抄送节点
   */
  static createConditionCopyNode(child) {
    let conditionCopyNode = {
      nodeId: this.idGenerator(),
      nodeName: "条件抄送",
      nodeDisplayName: "条件抄送",
      nodeType: 13, //节点类型 13、条件抄送节点
      nodeFrom: "",
      nodeTo: [],
      setType: 5, //抄送人类型 5、指定人员 (可由用户改为其他类型)
      signType: 1, //审批方式 1:会签
      isSignUp: 1,
      directorLevel: 1,
      noHeaderAction: 0,
      childNode: child,
      error: true, //必须选抄送人(与抄送V2 一致)
      property: {
        afterSignUpWay: 1,
        signUpType: 1,
      },
      lfFieldControlVOs: [],
      buttons: {
        startPage: btns(1),
        approvalPage: btns(3, 4, 18, 19, 21),
        viewPage: btns(0),
      },
      nodeApproveList: [], //真实抄送人,由用户配置(运行期由 processConditionCopyNode 设 CC_NODE)
      templateVos: [],
      labelList: [
        { labelValue: "condition_copy_node", labelName: "条件抄送节点" },
      ],
      conditionList: [[]], //条件关系,与条件节点/自动节点/条件审批节点相同结构
      groupRelation: false, //条件组关系 false:且 true:或
    };
    return conditionCopyNode;
  }
  /**
   * 创建协助节点对象
   * 本质是审批人节点(nodeType=4) + assist_node 标签
   * 运行期由后端 NodeUtil.nodeSpecialProcess 转为 nodeType=4, 复用审批人任务链路
   * 语义为"办理"而非"审批", 按钮权限默认仅包含协助按钮(41)
   * @param {Object} child - 子节点信息
   * @returns {Object} 协助节点
   */
  static createAssistNode(child) {
    let assistNode = {
      nodeId: this.idGenerator(),
      nodeName: "协助",
      nodeDisplayName: "协助",
      nodeType: 17, //节点类型 17、协助节点
      nodeFrom: "",
      nodeTo: [],
      setType: 5, //审批人类型 5、指定人员
      signType: 1, //审批方式 1:会签
      isSignUp: 0, //协助节点默认不开启加批
      directorLevel: 1,
      noHeaderAction: 0,
      childNode: child,
      error: true, //必须配置办理人
      property: {
        afterSignUpWay: 2,
        signUpType: 1,
        additionalSignInfoList: [],
      },
      lfFieldControlVOs: [],
      buttons: {
        startPage: btns(1),
        approvalPage: btns(41), //仅协助按钮
        viewPage: btns(0),
      },
      nodeApproveList: [],
      templateVos: [],
      labelList: [
        { labelValue: "assist_node", labelName: "协助节点" },
      ],
    };
    return assistNode;
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
   * 创建选择条件组合节点（审批人节点 + 动态条件网关）
   * 上方是审批人节点(isPickCondition=true)，下方是动态条件网关作为其childNode
   * @param {Object} child - 原后续节点
   * @returns {Object} 审批人节点（其childNode指向动态条件网关）
   */
  static createPickConditionNode(child) {
    // 先创建动态条件网关（下方）
    let gatewayNode = this.createDynamicConditionWayNode(child);
    // 再创建审批人节点（上方），childNode指向网关
    let approveNode = this.createApproveNode(gatewayNode);
    approveNode.nodeName = "选择条件审批人";
    approveNode.nodeDisplayName = "选择条件审批人";
    approveNode.isPickCondition = true;
    return approveNode;
  }
  /**
   * 创建动态条件并行网关对象（isDynamicCondition=true + isParallel=true）
   * @returns object
   */
  static createDynamicConditionParallelNode(child) {
    let gatewayNode = {
      nodeId: this.idGenerator(),
      nodeName: "动态条件并行网关",
      nodeType: 2,
      nodeFrom: "",
      nodeTo: [],
      childNode: this.createParallelNode("动态条件并行聚合审批人", null, 1, 0),
      isDynamicCondition: true, //true 动态条件
      isParallel: true, //true 并行条件
      error: false,
      property: null,
      conditionNodes: [
        this.createConditionNode("动态并行条件1", child, 1, 0),
        this.createConditionNode("动态并行条件2", null, 2, 0),
      ],
    };
    return gatewayNode;
  }
  /**
   * 创建选择动态条件并行组合节点（审批人节点 + 动态条件并行网关）
   * 上方是审批人节点(isPickCondition=true)，下方是动态条件并行网关作为其childNode
   * @param {Object} child - 原后续节点
   * @returns {Object} 审批人节点（其childNode指向动态条件并行网关）
   */
  static createSelectDynamicParallelNode(child) {
    // 先创建动态条件并行网关（下方）
    let gatewayNode = this.createDynamicConditionParallelNode(child);
    // 再创建审批人节点（上方），childNode指向网关
    let approveNode = this.createApproveNode(gatewayNode);
    approveNode.nodeName = "选择动态条件并行审批人";
    approveNode.nodeDisplayName = "选择动态条件并行审批人";
    approveNode.isPickCondition = true;
    return approveNode;
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
