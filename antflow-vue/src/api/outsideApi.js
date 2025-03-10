/*
 * @Date:  2024-10-10 20:00:00
 * @LastEditors: LDH 574427343@qq.com
 * @LastEditTime: 2024-10-10 20:00:00
 * @FilePath: src\api\outsideApi.js
 * 三方接入模块 相关接口
 */
import http from '@/utils/axios';
import cache from '@/plugins/cache';
let baseUrl = import.meta.env.VITE_APP_BASE_API
const headers = {
  "Userid": cache.session.get('userId'),
  "Username": cache.session.get('userName')
} 

/** 三方接入模块 流程设计 * / 
 
/**
 * 获取审批数据
 * @param {*} data 
 * @returns 
 */
export function getApiWorkFlowData(data) {
  return http.get(`${baseUrl}/bpmnConf/detail/${data.id}`, { headers })
}
 
/**
 * 设置审批数据
 * @param {*} data 
 * @returns 
 */
export function setApiWorkFlowData(data) {
  return http.post(`${baseUrl}/bpmnConf/edit`, data, { headers })
}

/**
 * 获取接入业务方列表
 * @param { page } 分页
 * @param { vo } 条件
 * @returns 
 */
export function getBusinessPartyList(page,vo) {
  let data = {
    "page": page,
    "vo": vo
  }
  return http.get(`${baseUrl}/outSideBpm/businessParty/listPage`,data, { headers })
}
/**
 * 获取接入业务方详情
 * @param { Number } id 
 * @returns 
 */
export function getBusinessPartyDetail(id) {
  return http.get(`${baseUrl}/outSideBpm/businessParty/detail/${id}`, { headers })
}

/**
 * 编辑接入业务方
 * @param {*} param 
 * @returns 
 */
export function setBusinessParty(data) { 
  return http.post(`${baseUrl}/outSideBpm/businessParty/edit`, data, { headers })
} 
/**
 * 获取业务方应用列表
 * @param {*} page 
 * @param {*} vo 
 * @returns 
 */
export function getApplicationsPageList(page,vo) {
  let data = {
    "page": page,
    "vo": vo
  }
  return http.get(`${baseUrl}/outSideBpm/businessParty/applicationsPageList`,data, { headers })
}
/** 查询应用列表 根据业务方partyMark */
export function getThirdPartyApplications(partyMark) { 
  return http.get(`${baseUrl}/outSideBpm/businessParty/getThirdPartyApplications/${partyMark}`, { headers })
}
/** 查询应用列表 根据业务方ID */
export function getApplicationsByPartyMarkId(partyMarkId) {  
  return http.get(`${baseUrl}/outSideBpm/businessParty/getApplicationsByPartyMarkId/${partyMarkId}`, { headers })
}
/**
 * 添加业务方应用
 * @param {*} data 
 * @returns 
 */
export function addApplication(data) {
  return http.post(`${baseUrl}/outSideBpm/businessParty/addBpmProcessAppApplication`, data, { headers })
} 
/**
 * 获取注册应用详情
 * @param {*} data 
 * @returns 
 */
export function getApplicationDetail(id) {
  return http.get(`${baseUrl}/outSideBpm/businessParty/applicationDetail/${id}`, { headers })
} 
/**
 * 获取可用接入业务方的标识
 * @param { String } businessPartyMark 
 * @returns 
 */
export function getPartyMarkByIdBpmConf(businessPartyMark) {
  return http.get(`${baseUrl}/outSideBpm/businessParty/getPartyMarkByIdBpmConf/${businessPartyMark}`, { headers })
}
/**
 * 获取业务方k-v 
 * @returns 
 */
export function getPartyMarkKV() {
  return http.get(`${baseUrl}/outSideBpm/businessParty/getPartyMarkKV`, { headers })
}
 


/** 应用关联条件模板 * /

/**
 * 获取条件模板列表
 * @param {*} param 
 * @returns 
 */
export function getConditionTemplatelist(businessPartyId,applicationId) {  
  return http.get(`${baseUrl}/outSideBpm/conditionTemplate/selectListByTemp/${businessPartyId}/${applicationId}`, { headers })
} 
/**
 * 获取条件模板列表
 * @param {*} param 
 * @returns 
 */
export function getTemplateByPartyMarkIdAndFormCode(businessPartyId,formCode) {  
  if(!businessPartyId || !formCode) {
    return Promise.resolve({
      "code": 999,
      "msg": "",
      "data": []
    })
  }
  return http.get(`${baseUrl}/outSideBpm/conditionTemplate/selectListByPartyMarkIdAndFormCode/${businessPartyId}/${formCode}`, { headers })
} 
 
/**
 * 添加条件模板 
 * @param {*} param 
 * @returns 
 */
export function setConditionTemplate(data) { 
  return http.post(`${baseUrl}/outSideBpm/conditionTemplate/edit`, data, { headers })
} 
/**
 * 审批人模板列表
 * @param {*} param 
 * @returns 
 */
export function getApproveTemplatePageList(page,vo) { 
  let data = {
    "page": page,
    "vo": vo
  }
  return http.post(`${baseUrl}/outSideBpm/approveTemplate/listPage`, data, { headers })
} 
/**
 * 获取审批人列表
 * @param {*} param 
 * @returns 
 */
export function getApproveTemplatelist(businessPartyId,applicationId) {  
  return http.get(`${baseUrl}/outSideBpm/approveTemplate/selectListByTemp/${businessPartyId}/${applicationId}`, { headers })
} 
/**
 * 审批人模板详情
 * @param {*} param 
 * @returns 
 */
export function getApproveTemplateDetail(id) {  
  return http.post(`${baseUrl}/outSideBpm/approveTemplate/detail/${id}`,  { headers })
} 
/**
 * 添加审批人模板
 * @param {*} param 
 * @returns 
 */
export function setApproveTemplate(data) { 
  return http.post(`${baseUrl}/outSideBpm/approveTemplate/edit`, data, { headers })
} 


/** 三方业务发起 * /

 /**
 * 三方业务发起
 * @param {*} data 
 * @returns 
 */
 export function processSubmit(data) {
  return http.post(`${baseUrl}/outSide/processSubmit`, data, { headers })
} 

