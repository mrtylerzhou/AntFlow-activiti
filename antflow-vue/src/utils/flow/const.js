/*
 * @Date: 2024-09-21 22:05:32
 * @LastEditors: LDH 574427343@qq.com
 * @LastEditTime: 2024-09-21 22:05:32
 * @FilePath: /src/utils/flow/const.js
 */

export let bgColors = ["192,192,192", '87, 106, 149', '255，97，0', '65，105，225', '255, 148, 62', '50, 150, 250', '50, 150, 250'] // '灰色, 蓝色, 橙色, 黄色, 黄色'
export let placeholderList = ["发起人", "审核人", "抄送人"];
export let nodeTypeList = ["未知", "发起人", "网关", "条件", "审核人", "抄送人", "抄送人"];

export let setTypes = [
  //{ value: 4, label: '指定角色' },  
  { value: 5, label: '指定人员' }, 
  //{ value: 6, label: 'HRBP' },
  //{ value: 13, label: '直属领导' },
  // { value: 2, label: '层层审批' },
  // { value: 3, label: '指定层级审批' },
  // { value: 8, label: '关联业务表' },
  // { value: 12, label: '发起人自己' }, 
  // { value: 14, label: '指定部门' },
]
export const nodeConf = {
  nodeType: {
    start : 1,//发起人
    getway : 2,//'网关'
    condition : 3,//'条件'
    approve: 4,//'审核人'
    copy: 6//'抄送人'
  },
  approveType: {
      role: 4,//'指定角色'
      user: 5,//'指定人员' 
      hrbp: 6,//'HRBP'
      leader: 13,//'直属领导'
      // layer: 2,//'层层审批'
      // level: 3,//'指定层级审批'
      // business: 8,//'关联业务表'
      // self: 12,//'发起人自己' 
      // department: 14,//'指定部门'
  }
}
export let typeCodes = [
  { value: 2, type: 1 },
  { value: 3, type: 3 },
  { value: 4, type: 4 },
  { value: 5, type: 5 },
  { value: 12, type: 2 },
  { value: 13, type: 13 },
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

export class approvalButtonConf {
  static preview =0;//预览
  static submit =1;//提交
  static resubmit = 2;//重新提交
  static agree = 3;//同意
  static noAgree = 4;//拒绝
  static print = 8;//打印
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
    4: '拒绝',//拒绝 
    8: '打印',//打印
    12: '终止',//终止
    15: '转发',//转发
    18: '打回',//打回
    19: '加批',//加批 
    21: '转办',//转办
  }
}

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

export const bizFormMaps = new Map([
  ['DSFZH_WMA', '/forms/form1.vue'],
  ['LEAVE_WMA', '/forms/form2.vue'],
  ['UCARREFUEl_WMA', '/forms/form3.vue']
]);

export let statusColor = {
  0: 'info',
  1: 'primary',//提交
  2: 'primary',//同意
  3: 'danger',//拒绝
  4: 'danger',//撤回
  5: 'danger',//作废
  6: 'danger',//终止
  7: 'primary',//
  8: 'danger',//打回修改
  10: 'warning',//承办
  13: 'primary',//添加审批人
  19: 'success',//加批
  99: 'success',//处理中
  21: 'success',//转办
  100: 'info'
};

export let pageButtonsColor = {
  0: 'primary',
  1: 'primary',//提交
  2: 'success',//重新提交
  3: 'primary',//同意
  4: 'danger',//不同意
  8: 'success',//打印
  10: 'warning',//承办
  12: 'danger',//终止
  15: 'primary',//转发
  18: 'warning',//打回修改
  13: 'primary',//添加审批人
  19: 'success',//加批
  21: 'primary',//转办 
};

export let approveList = {
  1: '张三',
  2: '李四',
  3: '王五',
  4: '菜六',
  5: '牛七',
  6: '马八',
  7: '李九',
  8: '周十',
  9: '肖十一',
  10: '令狐冲',
  11: '风清扬',
  12: '刘正风',
  13: '岳不群',
  14: '宁中则',
  15: '桃谷六仙',
  16: '不介和尚',
  17: '丁一师太',
  18: '依林师妹',
  19: '邱灵珊',
  20: '任盈盈'
};
//控件对应后端api的判断类型
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
//控件是否显示
export const condition_filedTypeMap = new Map([
  ['input', '4'],//"String" 
  ['input-number', '1'],//"Double" 
  ['select', '2'],//"String" select
  ['checkbox', '3'],//"String" checkbox
  //['radio', '1'],
  ['switch', '1'],
  ['time', '1'],
  //['time-range', '1'],
  //['data-range', '1'],
  ['date', '1']
]);
//判断控件的值的类型
export const condition_filedValueTypeMap = new Map([
  ['input', 'String'],//"Double" 
  ['input-number', 'Double'],//"Double" 
  ['select', 'String'],//"String" select
  ['checkbox', 'String'],//"String" checkbox
  ['radio', 'Double'],
  ['switch', 'Double'],
  ['time', 'Double'],
  ['time-range', 'Double'],
  ['data-range', 'Double'],
  ['date', 'Double']
]);