/*
 * @Date: 2024-09-21 22:05:32
 * @LastEditors: LDH 574427343@qq.com
 * @LastEditTime: 2024-09-21 22:05:32
 * @FilePath: /src/utils/flow/const.js
 */

export let bgColors = ["192,192,192", '87, 106, 149', '255，97，0', '65，105，225', '255, 148, 62', '50, 150, 250', '50, 150, 250']; // '灰色, 蓝色, 橙色, 黄色, 黄色'
export let placeholderList = ['','发起人', '', '条件','审核人','','抄送人','审核人'];
export let nodeTypeList = ["未知", "发起人", "网关", "条件", "审核人", "未知", "抄送人", "并行审批"];
export let signTypeObj = {
  1: '会签', 
  2: '或签',//
  3: '顺序会签',//拒绝
}; 
export let setTypes = [
  { value: 5, label: '指定人员' }, 
  { value: 4, label: '指定角色' },   
  { value: 6, label: 'HRBP' },
  { value: 13, label: '直属领导' },
  // { value: 2, label: '层层审批' },
  { value: 3, label: '指定层级审批' },
  // { value: 8, label: '关联业务表' },
  { value: 12, label: '发起人自己' }, 
  { value: 7, label: '发起人自选审批人' },
  // { value: 14, label: '指定部门' },
] 
export let hrbpOptions = [
  { value: 1, label: 'HRBP' },
  { value: 2, label: 'HRBP Leader' },
]
export let optTypes = [
  { value: '1', label: '大于等于' },
  { value: '2', label: '大于' },
  { value: '3', label: '小于等于' },
  { value: '4', label: '小于' }, 
  { value: '5', label: '等于' },
  { value: '6', label: '介于两个数之间' },
]

export let opt1s = [
  { value: '<', label: '<' },
  { value: '≤', label: '≤' },
]
  /**审批按钮lable-value */
export class approvalButtonConf {
  static preview =0;//预览
  static submit =1;//提交
  static resubmit = 2;//重新提交
  static agree = 3;//同意
  static noAgree = 4;//拒绝
  static print = 8;//打印
  static undertake = 10;//承办
  static terminate = 12;//终止
  static forward = 15;//转发
  static repulse = 18;//打回
  static addApproval = 19;//加批
  static transfer = 21;//转办  

  static buttonsObj={
    0: '预览',
    1: '提交',//提交
    2: '重新提交',//
    3: '同意',//拒绝
    4: '不同意',//拒绝 
    8: '打印',//打印
    10: '承办',//承办
    12: '终止',//终止
    15: '转发',//转发
    18: '打回修改',//打回
    19: '加批',//加批 
    21: '转办',//转办
  }
}
/**
 * 流程设计审批按钮显示
 */
export let approvalPageButtons = [
  { 
    value: approvalButtonConf.agree, 
    label: '同意', 
    description: '审批通过，流转到下一个节点', 
    type: 'default' 
  },
  { 
    value: approvalButtonConf.noAgree, 
    label: '不同意', 
    description: '当不同意任务时，当前任务被终止，并结束整个流程', 
    type: 'default'
  },
  { 
    value: approvalButtonConf.repulse, 
    label: '打回',
    description: '打回到发起人，发起人重新提交后，流程重新开始'
  },

  { 
    value: approvalButtonConf.transfer, 
    label: '转办', 
    description: '审批页面显示【转办】按钮，转办后选择审批人，转办后自己将不再进行审批' 
  },
  { value: approvalButtonConf.addApproval, 
    label: '加批' , 
    description: '在当前任务上额外添加新人员，以处理相关事项或提供必要的审批或意见'
  }
]
export let startPageButtons = [
  { value: approvalButtonConf.submit, label: '提交', type: 'default' },
  { value: approvalButtonConf.resubmit, label: '重新提交', type: 'default' },
  { value: approvalButtonConf.terminate, label: '终止' }
]
export let viewPageButtons = [
  { value: approvalButtonConf.preview, label: '预览', type: 'default' },
  { value: approvalButtonConf.print, label: '打印' },
  { value: approvalButtonConf.forward, label: '转发' }
]
/**
 * 自定义表单路径与FormCode映射
 */
export const bizFormMaps = new Map([
  ['DSFZH_WMA', '/forms/form1.vue'],
  ['LEAVE_WMA', '/forms/form2.vue'],
  ['UCARREFUEl_WMA', '/forms/form3.vue'],
  ['PURCHASE_WMA', '/forms/form4.vue'] 
]);

/**审批按钮颜色显示 */
export let approveButtonColor = {
  0: 'info',
  1: 'primary',//提交
  2: 'primary',//同意
  3: 'primary',//同意
  4: 'danger',//拒绝
  5: 'danger',//作废
  6: 'danger',//终止
  7: 'primary',//
  8: 'danger',//打回修改
  10: 'warning',//承办
  13: 'primary',//添加审批人
  18: 'warning',//加批
  19: 'success',//加批
  99: 'success',//处理中
  21: 'primary',//转办
  23: 'warning',//驳回
  100: 'info'
};
  
/**
 * 1、控件对应后端api的判断类型
 * 2、用于条件节点 对接 流程引擎中 条件判断
 * 3、与后端约定的值
 */
export const condition_columnTypeMap = new Map([
  ['input', '10000'],//"int/fload/double/string" input
  ['input-number', '10001'],//"Double" 
  ['select', '10000'],//"string" select
  ['checkbox', '10004'],//"string" checkbox
  ['radio', '10001'],
  ['switch', '10001'],
  ['time', '10002'],
  ['time-range', '10003'],
  ['data-range', '10002'],
  ['date', '10002']
]);
 
/**
 * 1、控件是在条件节点 选择条件时候否显示
 * 2、对应后端数据解析 与后端约定的值
 * Mapping: 1-string 2-int 3-date 4-time 5-text/长字符串 6-boolean 7-二进制/byte
 */
export const condition_filedTypeMap = new Map([
  ['input', '1'],//"String" 
  ['input-number', '4'],//"time" 
  ['select', '2'],//"int" select
  ['checkbox', '1'],//"String" checkbox
  //['radio', '2'], //  int radio
  ['switch', '6'], // boolean switch
  ['time', '1'],
  // ['time-range', '1'],
  // ['data-range', '1'],
  ['date', '1']
]);
/**
 * 判断控件的值的类型 
 */
export const condition_filedValueTypeMap = new Map([
  ['input', 'String'],//"Double" 
  ['input-number', 'String'],//"Double" 
  ['select', 'Int'],//"Int" select
  ['checkbox', 'String'],//checkbox 对应 VForm 是Array
  ['radio', 'Int'],
  ['switch', 'Boolean'],
  ['time', 'String'],
  ['time-range', 'String'],
  ['data-range', 'String'],
  ['date', 'String']
]);