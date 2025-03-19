//import { NodeUtils } from '@/utils/flow/nodeUtils'
export class NodeUtils {
  /**
   * 根据自增数生成64进制id
   * @returns 64进制id字符串
   */
  static idGenerator() {
    let qutient = new Date() - new Date("2024-05-01");
    qutient += Math.ceil(Math.random() * 1000); // 防止重複
    const chars = "0123456789ABCDEFGHIGKLMNOPQRSTUVWXYZabcdefghigklmnopqrstuvwxyz";
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
  static createApproveNode() {
    let approveNode = {
      nodeId: this.idGenerator(),
      nodeName: "审核人",
      nodeDisplayName: "审核人",
      nodeType: 4,//节点类型 4、审批人
      nodeFrom: "",
      nodeTo: [],
      setType: 5, //审批人类型 5、指定人员
      signType: 1,//审批方式 1:会签-需全部同意，2:或签-一人同意即可，3：顺序会签
      isSignUp: 0,//是否加批 0:否，1:是
      directorLevel: 1, 
      noHeaderAction: 1,
      childNode: null,
      error: true,
      property: {
        afterSignUpWay: 2,
        signUpType: 1,
      },
      lfFieldControlVOs: [],
      buttons: {
        startPage: [1],
        approvalPage: [3, 4,18,21],
        viewPage: [0],
      },
      nodeApproveList: [],
    };
    return approveNode;
  }
  /**
   * 创建抄送人对象
   * @returns object
   */
  static createCopyNode() {
    let copyNode = {
      nodeId: this.idGenerator(),
      nodeName: "抄送人",
      nodeDisplayName: "抄送人",
      nodeType: 6,
      nodeFrom: "",
      nodeTo: [],
      setType: 5,//仅支持选择人员
      error: true,
      ccFlag: 1,
      childNode: {},
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
      error: true,
      property: null,
      conditionNodes: [
        this.createConditionNode("条件1", child,1, 0),
        this.createConditionNode("条件2", null,2, 1),
      ],
    };
    return gatewayNode;
  }
  /**
   * 创建条件对象
   * @returns object
   */
  static createConditionNode(name, childNode,priority, isDefault) {
    let conditionNode = {
      nodeId: this.idGenerator(),
      nodeName: name || "条件1",
      nodeDisplayName: name || "条件1",
      nodeType: 3,
      nodeFrom: "",
      nodeTo: [],
      priorityLevel: priority,
      conditionList: [],
      nodeApproveList: [],
      error: true,
      childNode: childNode,
      isDefault: isDefault || 0,
    };
    return conditionNode;
  }

  /**
   * 初始化流程数据
   * @returns object
   */
  static createStartNode() {
    let startObj = {
      "data":{}
    };
    let startNode = {
      bpmnCode: null,
      bpmnName: "",
      bpmnType: null,
      formCode: "",
      appId: null,
      deduplicationType: 1,
      effectiveStatus: 0,
      isLowCodeFlow: 1,
      remark: "",
      isDel: 0,
      nodes: [
        {
          confId: 35,
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
          isSignUp: 0,
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
  static createJudgeNode(formId, columnId,type ,showName,showType,  columnName, columnType,fieldTypeName,multiple,multipleLimit, fixedDownBoxValue) {
    let judgeNode = {
      formId: formId,                    
      columnId: columnId,
      showType: showType,
      type: type, //1，发起人 2，其他表单条件
      showName: showName,
      optType: "5",
      zdy1:fieldTypeName == 'switch'?"1":"",
      opt1: "<",
      zdy2: "",
      opt2: "<",
      columnDbname: columnName,
      columnType: columnType,
      fieldTypeName: fieldTypeName,
      multiple: multiple,
      multipleLimit:multipleLimit,
      fixedDownBoxValue: fixedDownBoxValue
    }
    return judgeNode;
  }

/**
 * 创建并行网关对象
 * @returns object
 */
  static createParallelWayNode(child){
    let parallelwayNode = {
      nodeId: this.idGenerator(),
      nodeName: "并行审核网关",
      nodeType: 7,
      nodeFrom: "",
      nodeTo: [],
      childNode: this.createParallelNode("并行聚合节点", null,1, 0),
      error: true,
      property: null,
      parallelNodes: [
        this.createParallelNode("并行审核人1", child,1, 0),
        this.createParallelNode("并行审核人2", null,2, 0),
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
      nodeDisplayName: '',
      nodeType: 4,//节点类型 4、审批人
      nodeFrom: "",
      nodeTo: [],
      priorityLevel: priority, 
      nodeApproveList: [],
      setType: 5, //审批人类型 5、指定人员
      signType: 1,//审批方式 1:会签-需全部同意，2:或签-一人同意即可，3：顺序会签
      isSignUp: 0,//是否加批 0:否，1:是
      noHeaderAction: 1,
      lfFieldControlVOs: [],
      property: {
        afterSignUpWay: 2,//是否回到加批人 1:是，2:否
        signUpType: 1,//加批类型 1:顺序会签，2:会签 特别 3指: 回到加批人，afterSignUpWay赋值为1，signUpType赋值为1
      },
      buttons: {
        startPage: [1],
        approvalPage:[3, 4,18,21],
        viewPage: [0]
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
  static createOutsideConditionNode(conditionValue){
      let outsideConditionNode ={
            formId: "9999",//固定值
            columnId: "9999",//固定值
            showType: "2",//固定值
            showName: "条件",//固定值
            columnName: "templateMarks",
            columnType: "String",//固定值
            fieldTypeName: "select", //固定值
            fixedDownBoxValue: conditionValue,
        }
    return outsideConditionNode;
  } 
}

/**
 * 添模拟数据
 */
export function getMockData() {
    let startNode = "";//NodeUtils.createNode("start", ""); 
    return startNode;
  }
  