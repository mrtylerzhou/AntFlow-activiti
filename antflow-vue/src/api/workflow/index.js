/*
 * @Date:  2024-05-25 14:06:59
 * @LastEditors: LDH 574427343@qq.com
 * @LastEditTime: 2023-03-29 15:52:57
 * @FilePath: src\api\workflow.js
 * 工作流 相关接口
 */
import http from "@/utils/axios";
import Cookies from "js-cookie";
let baseUrl = import.meta.env.VITE_APP_BASE_API;
const headers = {
  Userid: Cookies.get("userId"),
  Username: Cookies.get("userName"),
};
/**
 * 获取流程配置详情
 * @param { * } data
 * @returns
 */
export function getApiWorkFlowData(data) {
  return http.get(`${baseUrl}/bpmnConf/detail/${data.id}`, { headers });
}

/**
 * 获取DIY FromCode
 * @returns
 */
export function getDIYFromCodeData() {
  return http.get(`${baseUrl}/bpmnBusiness/getDIYFormCodeList`, { headers });
}

/**
 * 设置/添加审批流程配置详情
 * @param {*} data
 * @returns
 */
export function setApiWorkFlowData(data) {
  return http.post(`${baseUrl}/bpmnConf/edit`, data, { headers });
}
/**
 * 获取代办事项
 * @returns
 */
export function getTodoList() {
  return http.get(`${baseUrl}/bpmnConf/todoList`, { headers });
}

/**
 * 获取抄送给我流程列表
 * @param {*} pageDto
 * @param {*} taskMgmtVO
 * @returns
 */
export function getCopyToMelistPage(pageDto, taskMgmtVO) {
  let data = {
    pageDto: pageDto,
    taskMgmtVO: taskMgmtVO,
  };
  return http.post(`${baseUrl}/bpmnConf/process/listPage/9`, data, { headers });
}

/**
 * 获取所有实例列表
 * @param {*} pageDto
 * @param {*} taskMgmtVO
 * @returns
 */
export function getAllProcesslistPage(pageDto, taskMgmtVO) {
  let data = {
    pageDto: pageDto,
    taskMgmtVO: taskMgmtVO,
  };
  return http.post(`${baseUrl}/bpmnConf/process/listPage/6`, data, { headers });
}

/**
 * 获取用户代办数据列表
 * @param {*} pageDto
 * @param {*} taskMgmtVO
 * @returns
 */
export function getPenddinglistPage(pageDto, taskMgmtVO) {
  let data = {
    pageDto: pageDto,
    taskMgmtVO: taskMgmtVO,
  };
  return http.post(`${baseUrl}/bpmnConf/process/listPage/5`, data, { headers });
}

/**
 * 获取用户撤销/退回数据列表
 * @param {*} pageDto
 * @param {*} taskMgmtVO
 * @returns
 */
export function getResubmitlistPage(pageDto, taskMgmtVO) {
  let data = {
    pageDto: pageDto,
    taskMgmtVO: taskMgmtVO,
  };
  return http.post(`${baseUrl}/bpmnConf/process/listPage/7`, data, { headers });
}

/**
 * 获取用户已审批数据列表
 * @param {*} pageDto
 * @param {*} taskMgmtVO
 * @returns
 */
export function getApprovedlistPage(pageDto, taskMgmtVO) {
  let data = {
    pageDto: pageDto,
    taskMgmtVO: taskMgmtVO,
  };
  return http.post(`${baseUrl}/bpmnConf/process/listPage/4`, data, { headers });
}

/**
 * 获取我发起的流程列表
 * @param {*} pageDto
 * @param {*} taskMgmtVO
 * @returns
 */
export function getMyRequestlistPage(pageDto, taskMgmtVO) {
  let data = {
    pageDto: pageDto,
    taskMgmtVO: taskMgmtVO,
  };
  return http.post(`${baseUrl}/bpmnConf/process/listPage/3`, data, { headers });
}

/**
 * 获取流程配置数据列表
 * @param {*} data
 * @returns
 */
export function getBpmnConflistPage(pageDto, taskMgmtVO) {
  let data = {
    pageDto: pageDto,
    entity: taskMgmtVO,
  };
  return http.post(`${baseUrl}/bpmnConf/listPage`, data, { headers });
}

/**
 * 审批,发起审批
 * @param {object} data
 * @param operationType 1 发起 2 重新提交 3 审批, 7 作废, 11 当前节点变更处理人,15 抄送, 24 当前节点减签,25 当前节点加签
 *  26 未来节点变更处理人, 27 未来节点减签,28 未来节点加签 29 撤回
 * @returns
 */
export function processOperation(data) {
  return http.post(
    `${baseUrl}/bpmnConf/process/buttonsOperation?formCode=${data.formCode}`,
    data,
    { headers },
  );
}

/**
 * 批量同意
 * @param {object} data - { taskIds: [], batchApprovalComment: '审批意见' }
 * @returns
 */
export function batchAgree(data) {
  return http.post(`${baseUrl}/bpmnBusiness/batchAgree`, data, { headers });
}

/**
 * 获取审批进度数据
 * @param { object } param
 * @returns
 */
export function getBpmVerifyInfoVos(param) {
  return http.get(
    `${baseUrl}/bpmnConf/getBpmVerifyInfoVos?processNumber=${param.processNumber}`,
    { headers },
  );
}

/**
 * 流程诊断: 初始化 (processNumber → confId/bpmnCode/发起人/当前表单值)
 * @param {*} processNumber
 * @returns
 */
export function getDiagnosisInit(processNumber) {
  return http.get(
    `${baseUrl}/bpmnConf/diagnosisInit?processNumber=${processNumber}`,
    { headers },
  );
}

/**
 * 流程诊断: 节点归因诊断
 * @param {*} data { processNumber, nodeId, expectedPresent }
 * @returns
 */
export function diagnoseNode(data) {
  return http.post(`${baseUrl}/bpmnConf/diagnoseNode`, data, { headers });
}

/**
 * 流程预览
 * @param {*} data
 * @returns
 */
export function getFlowPreview(data) {
  // let paramA = {
  //   "formCode": "DSFZH_WMA",
  //    "accountType":1
  // }
  return http.post(`${baseUrl}/bpmnConf/startPagePreviewNode`, data, {
    headers,
  });
}

/**
 * 流程节点当前操作人
 * @param {*} data
 * @returns
 */
export function loadNodeOperationUser(data) {
  // let paramA = {
  //   "formCode": "DSFZH_WMA",
  //    "accountType":1
  // }
  return http.post(`${baseUrl}/bpmnConf/loadNodeOperationUser`, data, {
    headers,
  });
}

/**
 * 流程生效
 * @param {*} data
 * @returns
 */
export function getEffectiveBpmn(data) {
  return http.get(`${baseUrl}/bpmnConf/effectiveBpmn/${data.id}`, { headers });
}

/**
 * 获取审批页面按钮权限
 * @param {*} data
 * @returns
 */
export function getViewBusinessProcess(data) {
  return http.post(
    `${baseUrl}/bpmnConf/process/viewBusinessProcess?formCode=${data.formCode}`,
    data,
    { headers },
  );
}

/**
 * 加载流程草稿
 * @param {string} formCode
 * @returns
 */
export function loadDraft(formCode) {
  return http.get(`${baseUrl}/processDraft/loadDraft?formCode=${formCode}`, { headers });
}

/**
 * 获取审批页面 审批人配置类型
 * @returns
 */
export function getApproveNodeProperties() {
  return http.get(`${baseUrl}/bpmnBusiness/listNodeProperties`, { headers });
}

/**
 * 按 processNumber 查询流程表单字段变更审计记录 (后端 t_bpm_process_audit 表).
 * 返回按 taskDefKey + createTime 升序的列表, 字段: id / processNumber / formCode /
 * fieldName / oldValue / newValue / taskName / taskDefKey / createUser / createTime.
 * @param {string} processNumber
 * @returns
 */
export function getProcessAudits(processNumber) {
  return http.get(`${baseUrl}/bpmnAudit/list?processNumber=${processNumber}`, { headers });
}

/**
 * 按 processNumber 查询流程沟通消息 (后端 t_bpm_process_comment 表).
 * 返回按 createTime + id 升序的未删除消息, 字段: id / processNumber / parentId / rootId /
 * content / attachment / mentions / replyToUser / replyToUserName / createUser / createUserName /
 * createTime / isDeleted.
 * @param {string} processNumber
 * @returns
 */
export function getProcessComments(processNumber) {
  return http.get(`${baseUrl}/bpmnComment/list?processNumber=${processNumber}`, { headers });
}

/**
 * 发送流程沟通消息(根消息或回复).
 * @param {{processNumber:string, parentId:number|null, content:string, attachment:string|null, mentions:Array<{userId:string,userName:string}>}} data
 * @returns
 */
export function saveProcessComment(data) {
  return http.post(`${baseUrl}/bpmnComment/save`, data, { headers });
}

/**
 * 撤回自己发送的流程沟通消息(软删除).
 * @param {number} id
 * @returns
 */
export function withdrawProcessComment(id) {
  return http.post(`${baseUrl}/bpmnComment/withdraw?id=${id}`, {}, { headers });
}

/**
 * 获取委托列表
 * @param {*} pageDto
 * @param {*} taskMgmtVO
 * @returns
 */
export function getUserEntrustListPage(pageDto, taskMgmtVO) {
  let data = {
    pageDto: pageDto,
    taskMgmtVO: taskMgmtVO,
  };
  return http.post(`${baseUrl}/bpmnBusiness/entrustlist/1`, data, { headers });
}
/**
 * 获取委托列表
 * @param {*} pageDto
 * @param {*} taskMgmtVO
 * @returns
 */
export function getEntrustListPage(pageDto, taskMgmtVO) {
  let data = {
    pageDto: pageDto,
    taskMgmtVO: taskMgmtVO,
  };
  return http.post(`${baseUrl}/bpmnBusiness/entrustlist/2`, data, { headers });
}
/**
 *  委托详情
 * @param {Number} id
 * @returns
 */
export function entrustDetail(id) {
  return http.get(`${baseUrl}/bpmnBusiness/entrustDetail/${id}`, { headers });
}
/**
 * 设置委托
 * @param {*} data
 * @returns
 */
export function setEntrust(data) {
  return http.post(`${baseUrl}/bpmnBusiness/editEntrust`, data, { headers });
}
/**
 * 获取流程自选审批人节点
 * @param {*} formCode
 * @returns
 */
export function getStartUserChooseModules(formCode) {
  return http.get(
    `${baseUrl}/bpmnBusiness/getStartUserChooseModules?formCode=${formCode}`,
    { headers },
  );
}

/**
 * 获取自定义审批规则选项
 * @returns
 */
export function getUDROptions() {
  return http.get(`${baseUrl}/taskMgmt/getUDROptions`, { headers });
}

/**
 * 通用字典数据查询
 * @param {string} dictType 字典类型
 * @returns
 */
export function getDictDataByType(dictType) {
  return http.get(`${baseUrl}/taskMgmt/getDictDataByType?dictType=${dictType}`, { headers });
}
