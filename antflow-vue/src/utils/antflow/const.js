/*
 * @Date: 2024-09-21 22:05:32
 * @LastEditors: LDH 574427343@qq.com
 * @LastEditTime: 2024-09-21 22:05:32
 * @FilePath: /src/utils/antflow/const.js
 */

export let bgColors = [
  "192,192,192",
  "87, 106, 149",
  "255，97，0",
  "65，105，225",
  "255, 148, 62",
  "50, 150, 250",
  "50, 150, 250",
  "",
  "4, 96, 187",
  "155, 89, 182",
  "",
  "",
  "46, 167, 167",
  "70, 130, 180",
  "",
  "",
  "",
  "92, 107, 192",
  "45, 183, 165",
]; // '灰色, 蓝色, 橙色, 黄色, 黄色, , , , 深蓝, 紫色, , , 青绿色(条件审批), 钢蓝色(条件抄送), , , , 靖蓝色(协助), 青色(自动推进)'
// 选择条件节点专属颜色(覆写审批人节点的橙色,使其一眼可辨)
export const PICK_CONDITION_COLOR = "219, 54, 124"; // 树莓红
export let placeholderList = [
  "",
  "发起人",
  "",
  "条件",
  "审核人",
  "",
  "抄送人",
  "审核人",
  "抄送人v2",
  "自动节点",
  "办理人",
  "自动办理",
  "条件审批",
  "条件抄送",
  "",
  "",
  "",
  "协助",
  "自动推进节点",
];
export let nodeTypeList = [
  "未知",
  "发起人",
  "网关",
  "条件",
  "审核人",
  "未知",
  "抄送人",
  "并行审批",
  "抄送人V2",
  "自动节点",
  "办理节点",
  "自动办理",
  "条件审批",
  "条件抄送",
  "未知",
  "未知",
  "未知",
  "协助",
  "自动推进",
];
export let signTypeObj = {
  1: "会签",
  2: "或签", //
  3: "顺序会签", //拒绝
  4: "仲裁签",
};
export let setTypes = [
  { value: 5, label: "指定人员" },
  { value: 4, label: "指定角色" },
  { value: 6, label: "HRBP" },
  { value: 13, label: "直属领导" },
  { value: 2, label: "层层审批" },
  { value: 3, label: "指定层级审批" },
  // { value: 8, label: '关联业务表' },
  { value: 12, label: "发起人自己" },
  { value: 7, label: "发起人自选审批人" },
  // { value: 14, label: '指定部门' },
  { value: 16, label: "表单中选择" },
  { value: 17, label: "自定义" },
  { value: 18, label: "上一节点审批人的" },
  { value: 19, label: "上一节点指定" },
];
export let setCopyerTypes = [
  { value: 5, label: "指定人员" },
  { value: 4, label: "指定角色" },
  { value: 13, label: "直属领导" },
  // { value: 2, label: '层层审批' },
  { value: 3, label: "指定层级审批" },
  // { value: 8, label: '关联业务表' },
  { value: 12, label: "发起人自己" },
  { value: 7, label: "发起人自选审批人" },
  // { value: 14, label: '指定部门' },
];
export let hrbpOptions = [
  { value: 1, label: "HRBP" },
  { value: 2, label: "HRBP Leader" },
];
export let optTypes = [
  { value: 1, label: "大于等于" },
  { value: 2, label: "大于" },
  { value: 3, label: "小于等于" },
  { value: 4, label: "小于" },
  { value: 5, label: "等于" },
  { value: 6, label: "介于两个数之间" },
];

export let opt1s = [
  { value: "<", label: "<" },
  { value: "≤", label: "≤" },
];

export const formUserOptionSet = [
  { label: "表单中的人员", value: 1 },
  { label: "表单中的角色", value: 2 },
  { label: "表单中人员的HRBP", value: 3 },
  { label: "表单中人员的直属领导", value: 4 },
  { label: "表单中人员所在部门负责人", value: 5 },
  { label: "表单中部门的负责人", value: 6 },
  { label: "表单中人员多级领导", value: 7 },
  { label: "表单中人员全部层级领导", value: 8 },
];

export const formPrevNodeApproverOptionSet = [
  { label: "上一节点人员", value: 1 },
  { label: "上一节点人员的HRBP", value: 3 },
  { label: "上一节点人员的直属领导", value: 4 },
  { label: "上一节点人员所在部门负责人", value: 5 },
  { label: "上一节点部门的负责人", value: 6 },
  { label: "上一节点人员多级领导", value: 7 },
  { label: "上一节点人员全部层级领导", value: 8 },
];

export const NO_USER_FIELD_WIDGETS = new Set([
  "textarea",
  "number",
  "switch",
  "time",
  "date",
  "date-range",
  "time-range",
  "rate",
  "slider",
  "color",
  "picture-upload",
  "file-upload",
  "rich-editor",
]);

/**审批按钮lable-value */
export class approvalButtonConf {
  static preview = 0; //预览
  static submit = 1; //提交
  static resubmit = 2; //重新提交
  static agree = 3; //同意
  static noAgree = 4; //拒绝
  static repulsePrev = 6; //退回上节点修改
  static invalid = 7; //作废
  static print = 8; //打印
  static undertake = 10; //承办
  static changeApprove = 11; //变更处理人
  static terminate = 12; //终止
  static forward = 15; //转发
  static repulse = 18; //退回
  static addApproval = 19; //加批
  static transfer = 21; //转办
  static selectApprove = 22; //自选审批人
  static backToAnyNode = 23; //退回任意节点
  static currentNodeReduceSign = 24; //当前节点减签
  static currentNodeAddSign = 25; //当前节点加签
  static futureNodeChangeApprove = 26; //未来节点变更处理人
  static futureNodeReduceSign = 27; //未来节点减签
  static futureNodeAddSign = 28; //未来节点加签
  static withdraw = 29; //流程撤回
  static drawBackAgree = 32; //撤销同意
  static appointNextNodeApprover = 38; //指定下一节点审批人
  static oppose = 39; //反对(仲裁签)
  static pickCondition = 40; //选择分支(选择条件)
  static assist = 41; //协助(协助节点办理)
  static forwardToNode = 42; //推进(审批人推进到未来节点)
  static inApproval = 99; //处理中
  static completed = 100; //已完成

  static buttonsObj = {
    0: "预览",
    1: "提交", //提交
    2: "重新提交", //
    3: "同意", //拒绝
    4: "不同意", //拒绝
    6: "退回上节点修改", //退回上节点修改
    7: "作废", //
    8: "打印", //打印
    10: "承办", //承办
    11: "当前节点变更处理人", //变更处理人
    12: "终止", //终止
    13: "添加审批人", //添加审批人
    15: "转发", //转发
    18: "退回", //退回修改
    19: "加批", //加批
    21: "转办", //转办
    22: "自选审批人", //自选审批人
    23: "退回任意节点", //退回任意节点
    24: "当前节点减签",
    25: "当前节点加签",
    26: "未来节点变更处理人",
    27: "未来节点减签",
    28: "未来节点加签",
    29: "流程撤回",
    32: "撤销同意",
    38: "指定下一节点审批人",
    39: "反对", //仲裁签场景下反对
    40: "选择分支", //选择条件
    41: "协助", //协助节点办理
    42: "推进", //审批人推进到未来节点
  };
}

/**上一节点指定审批人:前端使用的常量*/
export const PREV_NODE_APPOINTED_SET_TYPE = 19; //radio显示值(映射到nodeProperty=5)
export const PREV_NODE_APPOINTED_VIRTUAL_USER_ID = "-4"; //AFSpecialAssigneeEnum.PREV_NODE_APPOINTED
export const PREV_NODE_APPOINTED_VIRTUAL_USER_NAME = "上一节点指定审批人";
export const LABEL_PREV_NODE_APPOINTED = "af_syslabel_prev_node_appointed";
export const LABEL_APPOINT_NEXT_NODE_APPROVER = "af_syslabel_appoint_next_node_approver";
export const LABEL_PICK_CONDITION = "af_syslabel_pick_condition";
/**
 * 流程设计审批按钮显示
 */
export let approvalPageButtons = [
  {
    value: approvalButtonConf.agree,
    label: "同意",
    description: "审批通过，流转到下一个节点",
    type: "default",
  },
  {
    value: approvalButtonConf.noAgree,
    label: "不同意",
    description: "当不同意任务时，当前任务被终止，并结束整个流程",
    type: "default",
  },
  {
    value: approvalButtonConf.repulse,
    label: "退回",
    description: "退回到(发起人或任意节点)，流程重新开始或者回到当前审批人",
  },

  {
    value: approvalButtonConf.transfer,
    label: "转办",
    description:
      "审批页面显示【转办】按钮，转办后选择审批人，转办后自己将不再进行审批",
  },
  {
    value: approvalButtonConf.addApproval,
    label: "加批",
    description:
      "在当前任务上额外添加新人员，以处理相关事项或提供必要的审批或意见",
  },
  {
    value: approvalButtonConf.oppose,
    label: "反对",
    description: "仲裁签场景下反对，达阈值终止",
    signTypeRestrict: 4,
  },
  {
    value: approvalButtonConf.assist,
    label: "协助",
    description: "协助节点办理任务，流程继续向下流转",
  },
  {
    value: approvalButtonConf.forwardToNode,
    label: "推进",
    description: "先同意当前任务，再跳转到未来节点，中间节点将被跳过",
  },
];
export let startPageButtons = [
  { value: approvalButtonConf.submit, label: "提交", type: "default" },
  { value: approvalButtonConf.resubmit, label: "重新提交", type: "default" },
  { value: approvalButtonConf.terminate, label: "终止" },
];
export let viewPageButtons = [
  { value: approvalButtonConf.preview, label: "预览", type: "default" },
  { value: approvalButtonConf.print, label: "打印" },
  { value: approvalButtonConf.forward, label: "转发" },
];
/**
 * 流程设计节点查看页按钮配置（节点级，区别于流程级 viewPageButtons）
 */
export let nodeViewPageButtons = [
  {
    value: approvalButtonConf.drawBackAgree,
    label: "撤销同意",
    description: "撤销当前节点的同意操作，撤销后需重新审批",
  },
];
/**
 * 自定义表单路径与FormCode映射
 */
export const bizFormMaps = new Map([
  ["DSFZH_WMA", "/forms/form1.vue"],
  ["LEAVE_WMA", "/forms/form2.vue"],
  ["UCARREFUEl_WMA", "/forms/form3.vue"],
  ["PURCHASE_WMA", "/forms/form4.vue"],
  ["BXSP_WMA", "/forms/form5.vue"],
]);

/**审批按钮颜色显示 */
export let approveButtonColor = {
  0: "info",
  1: "primary", //
  2: "primary", //
  3: "success", //同意
  4: "danger", //拒绝
  5: "danger", //
  6: "danger", //
  7: "primary", //
  8: "danger", //
  10: "warning", //承办
  13: "primary", //
  18: "warning", //退回
  19: "success", //加批
  21: "primary", //转办
  23: "warning", //驳回
  32: "danger", //撤销同意
  40: "warning", //选择分支
  41: "success", //协助
  42: "success", //推进
  99: "success", //处理中
  100: "info",
};

export const noticeUserList = [
  {
    value: "1",
    label: "申请人",
  },
  {
    value: "2",
    label: "所有已审批人",
  },
  {
    value: "3",
    label: "当前节点审批人",
  },
  {
    value: "4",
    label: "被转发人",
  },
  {
    value: "5",
    label: "指定人员",
  },
  {
    value: "6",
    label: "指定角色",
  },
];
export const messageSendTypeList = [
  {
    active: false,
    id: 1,
    name: "邮件",
  },
  {
    active: false,
    id: 2,
    name: "短信",
  },
  {
    active: false,
    id: 3,
    name: "app推送",
  },
  {
    active: false,
    id: 5,
    name: "企微",
  },
  {
    active: false,
    id: 6,
    name: "钉钉",
  },
  {
    active: false,
    id: 7,
    name: "飞书",
  },
];
export const eventTypeList = [
  {
    active: false,
    id: 1,
    name: "流程发起",
  },
  {
    active: false,
    id: 2,
    name: "作废操作",
  },
  {
    active: false,
    id: 3,
    name: "流程流转至当前节点",
  },
  {
    active: false,
    id: 4,
    name: "同意操作",
  },
  {
    active: false,
    id: 5,
    name: "不同意操作",
  },
  {
    active: false,
    id: 6,
    name: "加批操作",
  },
  {
    active: false,
    id: 7,
    name: "退回修改操作",
  },
  {
    active: false,
    id: 8,
    name: "转发操作",
  },
  {
    active: false,
    id: 9,
    name: "流程结束",
  },
];
